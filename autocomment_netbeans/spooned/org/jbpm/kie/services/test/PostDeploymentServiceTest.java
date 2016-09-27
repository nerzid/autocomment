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
import org.jbpm.services.api.DeploymentEvent;
import org.jbpm.services.api.DeploymentEventListener;
import org.jbpm.services.api.model.DeploymentUnit;
import org.kie.internal.runtime.manager.context.EmptyContext;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.kie.services.impl.KModuleDeploymentService;
import org.kie.api.KieServices;
import java.util.List;
import java.util.Map;
import org.kie.scanner.MavenRepository;
import org.jbpm.services.api.model.ProcessDefinition;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.junit.Test;

public class PostDeploymentServiceTest extends AbstractKieServicesBaseTest {
    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();

    @Before
    public void prepare() {
        configureServices();
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/general/customtask.bpmn");
        processes.add("repo/processes/general/humanTask.bpmn");
        processes.add("repo/processes/general/signal.bpmn");
        processes.add("repo/processes/general/import.bpmn");
        processes.add("repo/processes/general/callactivity.bpmn");
        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes);
        File pom = new File("target/kmodule", "pom.xml");
        pom.getParentFile().mkdir();
        try {
            FileOutputStream fs = new FileOutputStream(pom);
            fs.write(getPom(releaseId).getBytes());
            fs.close();
        } catch (Exception e) {
        }
        MavenRepository repository = MavenRepository.getMavenRepository();
        repository.deployArtifact(releaseId, kJar1, pom);
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
    public void testDeploymentOfProcesses() {
        Assert.assertNotNull(deploymentService);
        DeploymentUnit deploymentUnit = new org.jbpm.kie.services.impl.KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        DeployedUnit deployed = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        Assert.assertNotNull(deployed);
        Assert.assertNotNull(deployed.getDeploymentUnit());
        Assert.assertNotNull(deployed.getRuntimeManager());
        Assert.assertNotNull(runtimeDataService);
        Collection<ProcessDefinition> processes = runtimeDataService.getProcesses(new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(processes);
        Assert.assertEquals(5, processes.size());
        processes = runtimeDataService.getProcessesByFilter("custom", new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(processes);
        Assert.assertEquals(1, processes.size());
        processes = runtimeDataService.getProcessesByDeploymentId(deploymentUnit.getIdentifier(), new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(processes);
        Assert.assertEquals(5, processes.size());
        ProcessDefinition process = runtimeDataService.getProcessesByDeploymentIdProcessId(deploymentUnit.getIdentifier(), "customtask");
        Assert.assertNotNull(process);
        RuntimeManager manager = deploymentService.getRuntimeManager(deploymentUnit.getIdentifier());
        Assert.assertNotNull(manager);
        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        Assert.assertNotNull(engine);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", "test");
        ProcessInstance processInstance = engine.getKieSession().startProcess("customtask", params);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    @Test
    public void testDuplicatedDeployment() {
        Assert.assertNotNull(deploymentService);
        ((KModuleDeploymentService) (deploymentService)).addListener(new DeploymentEventListener() {
            @Override
            public void onUnDeploy(DeploymentEvent event) {
            }

            @Override
            public void onDeploy(DeploymentEvent event) {
                throw new IllegalArgumentException("On purpose");
            }

            @Override
            public void onActivate(DeploymentEvent event) {
            }

            @Override
            public void onDeactivate(DeploymentEvent event) {
            }
        });
        DeploymentUnit deploymentUnit = new org.jbpm.kie.services.impl.KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION, "KBase-test", "ksession-test");
        try {
            deploymentService.deploy(deploymentUnit);
            units.add(deploymentUnit);
            Assert.fail("Deployment should fail due to post process failuer - see ThrowExceptionOnDeploymentEvent");
        } catch (RuntimeException e) {
        }
        DeployedUnit deployedGeneral = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        Assert.assertNull(deployedGeneral);
        Assert.assertFalse(RuntimeManagerRegistry.get().isRegistered(deploymentUnit.getIdentifier()));
    }
}

