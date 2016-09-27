/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.jbpm.test.container;

import org.junit.After;
import java.util.ArrayList;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.model.DeploymentUnit;
import javax.ejb.EJB;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jbpm.services.api.model.NodeInstanceDesc;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.jbpm.services.ejb.api.ProcessServiceEJBLocal;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.jbpm.services.ejb.api.UserTaskServiceEJBLocal;

public abstract class AbstractRuntimeEJBServicesTest extends AbstractEJBServicesTest {
    private static DeploymentService staticDeploymentService;

    @EJB
    protected ProcessServiceEJBLocal processService;

    @EJB
    protected UserTaskServiceEJBLocal userTaskService;

    protected static String kieJar;

    @Before
    public void testRuntimeEJBs() {
        Assertions.assertThat(processService).isNotNull();
        Assertions.assertThat(userTaskService).isNotNull();
        archive.setProcessService(processService);
    }

    @Before
    public void saveDeploymentService() {
        AbstractRuntimeEJBServicesTest.staticDeploymentService = deploymentService;
    }

    @Before
    public void deployKieJar() {
        AbstractRuntimeEJBServicesTest.kieJar = archive.deployBasicKieJar().getIdentifier();
    }

    @After
    @Override
    public void cleanup() {
        try {
            List<Long> pids = archive.getPids();
            List<Long> all = ((List<Long>) (((ArrayList<Long>) (pids)).clone()));
            for (Long pid : all) {
                ProcessInstance pi = processService.getProcessInstance(pid);
                if (pi == null) {
                    pids.remove(pid);
                } 
            }
            if (!(pids.isEmpty())) {
                processService.abortProcessInstances(pids);
            } 
            pids.clear();
        } catch (Exception ex) {
            // ignore
        }
        cleanupSingletonSessionId();
        List<DeploymentUnit> units = archive.getUnits();
        if ((units != null) && (!(units.isEmpty()))) {
            for (DeploymentUnit unit : units) {
                // clear audit logs
                RuntimeManager manager = deploymentService.getRuntimeManager(unit.getIdentifier());
                RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
                engine.getAuditService().clear();
                deploymentService.undeploy(unit);
            }
            units.clear();
        } 
        AbstractRuntimeEJBServicesTest.kieJar = null;
    }

    public Long startProcessInstance(String processId) {
        return startProcessInstance(processId, new HashMap<String, Object>());
    }

    public Long startProcessInstance(String processId, Map<String, Object> params) {
        return archive.startProcess(AbstractRuntimeEJBServicesTest.kieJar, processId, params);
    }

    public void abortProcessInstance(Long processInstanceId) {
        processService.abortProcessInstance(processInstanceId);
    }

    public boolean hasNodeLeft(Long processInstanceId, String nodeName) {
        List<NodeInstanceDesc> processInstanceHistory = getProcessInstanceHistory(processInstanceId);
        for (NodeInstanceDesc node : processInstanceHistory) {
            if (((node.getName()) != null) && (node.getName().equals(nodeName))) {
                // The history contains also records of a node when it was not
                // yet
                // completed.
                if (node.isCompleted()) {
                    return true;
                } 
            } 
        }
        return false;
    }

    public boolean hasProcessInstanceCompleted(Long processInstanceId) {
        ProcessInstance processInstance = processService.getProcessInstance(processInstanceId);
        if (processInstance != null) {
            return (processInstance.getState()) == (ProcessInstance.STATE_COMPLETED);
        } 
        return true;
    }

    public boolean hasTaskCompleted(Long taskId) {
        return runtimeDataService.getTaskById(taskId).getStatus().equals(org.kie.api.task.model.Status.Completed);
    }

    public List<NodeInstanceDesc> getProcessInstanceHistory(Long processInstanceId) {
        return ((List<NodeInstanceDesc>) (runtimeDataService.getProcessInstanceFullHistory(processInstanceId, new org.kie.internal.query.QueryContext(0, 40))));
    }
}

