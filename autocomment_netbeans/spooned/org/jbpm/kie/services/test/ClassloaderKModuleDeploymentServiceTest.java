/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.jbpm.kie.services.test;

import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.junit.After;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Before;
import java.util.Collection;
import org.jbpm.services.api.model.DeployedUnit;
import org.jbpm.services.api.model.DeploymentUnit;
import org.kie.internal.runtime.manager.context.EmptyContext;
import java.io.File;
import java.util.HashMap;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.internal.task.api.InternalTaskService;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.kie.api.KieServices;
import java.util.List;
import java.util.Map;
import org.kie.scanner.MavenRepository;
import org.jbpm.services.api.model.ProcessDefinition;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.task.model.TaskSummary;
import org.junit.Test;

public class ClassloaderKModuleDeploymentServiceTest extends AbstractKieServicesBaseTest {
    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();

    private static final String ARTIFACT_ID = "jbpm-module";

    private static final String GROUP_ID = "org.jbpm.test";

    private static final String VERSION = "1.0";

    @Before
    public void prepare() {
        configureServices();
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(ClassloaderKModuleDeploymentServiceTest.GROUP_ID, ClassloaderKModuleDeploymentServiceTest.ARTIFACT_ID, ClassloaderKModuleDeploymentServiceTest.VERSION);
        File kjar = new File("src/test/resources/kjar/jbpm-module.jar");
        File pom = new File("src/test/resources/kjar/pom.xml");
        MavenRepository repository = MavenRepository.getMavenRepository();
        repository.installArtifact(releaseId, kjar, pom);
    }

    @After
    public void cleanup() {
        cleanupSingletonSessionId();
        if (((units) != null) && (!(units.isEmpty()))) {
            for (DeploymentUnit unit : units) {
                deploymentService.undeploy(unit);
            }
            units.clear();
        } 
        close();
    }

    @Test
    public void testDeploymentOfProcesses() throws Exception {
        Assert.assertNotNull(deploymentService);
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(ClassloaderKModuleDeploymentServiceTest.GROUP_ID, ClassloaderKModuleDeploymentServiceTest.ARTIFACT_ID, ClassloaderKModuleDeploymentServiceTest.VERSION, "defaultKieBase", "defaultKieSession");
        deploymentUnit.setStrategy(RuntimeStrategy.PER_REQUEST);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        DeployedUnit deployed = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        Assert.assertNotNull(deployed);
        Assert.assertNotNull(deployed.getDeploymentUnit());
        Assert.assertNotNull(deployed.getRuntimeManager());
        Assert.assertEquals("org.jbpm.test:jbpm-module:1.0:defaultKieBase:defaultKieSession", deployed.getDeploymentUnit().getIdentifier());
        Assert.assertNotNull(runtimeDataService);
        Collection<ProcessDefinition> processes = runtimeDataService.getProcesses(new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(processes);
        Assert.assertEquals(1, processes.size());
        RuntimeManager manager = deploymentService.getRuntimeManager(deploymentUnit.getIdentifier());
        Assert.assertNotNull(manager);
        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        Assert.assertNotNull(engine);
        Class<?> clazz = Class.forName("org.jbpm.test.Person", true, ((InternalRuntimeManager) (manager)).getEnvironment().getClassLoader());
        Object instance = clazz.newInstance();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("person", instance);
        ProcessInstance processInstance = engine.getKieSession().startProcess("testkjar.src.main.resources.process", params);
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        List<TaskSummary> tasks = engine.getTaskService().getTasksOwned("salaboy", "en-UK");
        Assert.assertEquals(1, tasks.size());
        long taskId = tasks.get(0).getId();
        Map<String, Object> content = ((InternalTaskService) (engine.getTaskService())).getTaskContent(taskId);
        Assert.assertTrue(content.containsKey("personIn"));
        Object person = content.get("personIn");
        Assert.assertEquals(clazz.getName(), person.getClass().getName());
        engine.getTaskService().start(taskId, "salaboy");
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("personOut", instance);
        engine.getTaskService().complete(taskId, "salaboy", data);
        processInstance = engine.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNull(processInstance);
    }
}

