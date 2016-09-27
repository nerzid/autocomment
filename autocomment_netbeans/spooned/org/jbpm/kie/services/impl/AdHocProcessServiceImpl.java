/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.kie.services.impl;

import org.jbpm.services.api.AdHocProcessService;
import org.kie.internal.process.CorrelationAwareProcessRuntime;
import org.kie.internal.process.CorrelationKey;
import org.jbpm.services.api.model.DeployedUnit;
import org.jbpm.services.api.DeploymentService;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.jbpm.services.api.RuntimeDataService;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;

public class AdHocProcessServiceImpl implements VariablesAware , AdHocProcessService {
    private static final Logger logger = LoggerFactory.getLogger(AdHocProcessServiceImpl.class);

    protected DeploymentService deploymentService;

    protected RuntimeDataService dataService;

    public void setDeploymentService(DeploymentService deploymentService) {
        AdHocProcessServiceImpl.this.deploymentService = deploymentService;
    }

    public void setDataService(RuntimeDataService dataService) {
        AdHocProcessServiceImpl.this.dataService = dataService;
    }

    @Override
    public Long startProcess(String deploymentId, String processId, CorrelationKey correlationKey, Map<String, Object> params, Long parentProcessInstanceId) {
        DeployedUnit deployedUnit = deploymentService.getDeployedUnit(deploymentId);
        if (deployedUnit == null) {
            throw new org.jbpm.services.api.DeploymentNotFoundException(("No deployments available for " + deploymentId));
        } 
        if (!(deployedUnit.isActive())) {
            throw new org.jbpm.services.api.DeploymentNotFoundException((("Deployments " + deploymentId) + " is not active"));
        } 
        RuntimeManager manager = deployedUnit.getRuntimeManager();
        params = process(params, ((InternalRuntimeManager) (manager)).getEnvironment().getClassLoader());
        RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = engine.getKieSession();
        ProcessInstance pi = null;
        try {
            pi = ((CorrelationAwareProcessRuntime) (ksession)).createProcessInstance(processId, correlationKey, params);
            pi = ksession.execute(new org.jbpm.kie.services.impl.cmd.StartProcessInstanceWithParentCommand(pi.getId(), parentProcessInstanceId));
            return pi.getId();
        } finally {
            disposeRuntimeEngine(manager, engine);
        }
    }

    @Override
    public <T> T process(T variables, ClassLoader cl) {
        // do nothing here as there is no need to process variables
        return variables;
    }

    protected void disposeRuntimeEngine(RuntimeManager manager, RuntimeEngine engine) {
        manager.disposeRuntimeEngine(engine);
    }
}

