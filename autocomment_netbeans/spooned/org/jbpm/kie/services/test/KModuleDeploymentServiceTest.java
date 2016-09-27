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
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.jbpm.services.api.model.DeploymentUnit;
import org.kie.internal.runtime.manager.context.EmptyContext;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.kie.api.KieServices;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.kie.scanner.MavenRepository;
import org.jbpm.services.api.model.ProcessDefinition;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.junit.Test;

public class KModuleDeploymentServiceTest extends AbstractKieServicesBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(KModuleDeploymentServiceTest.class);

    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();

    @Before
    public void prepare() {
        configureServices();
        KModuleDeploymentServiceTest.logger.debug("Preparing kjar");
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/general/customtask.bpmn");
        processes.add("repo/processes/general/humanTask.bpmn");
        processes.add("repo/processes/general/import.bpmn");
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
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION, "KBase-test", "ksession-test");
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        Assert.assertNotNull(deploymentUnit.getDeploymentDescriptor());
        DeployedUnit deployed = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        Assert.assertNotNull(deployed);
        Assert.assertNotNull(deployed.getDeploymentUnit());
        Assert.assertNotNull(deployed.getRuntimeManager());
        Assert.assertNull(deployed.getDeployedAssetLocation("customtask"));
        Assert.assertEquals((((((((((GROUP_ID) + ":") + (ARTIFACT_ID)) + ":") + (VERSION)) + ":") + "KBase-test") + ":") + "ksession-test"), deployed.getDeploymentUnit().getIdentifier());
        Assert.assertNotNull(runtimeDataService);
        Collection<ProcessDefinition> processes = runtimeDataService.getProcesses(new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(processes);
        Assert.assertEquals(3, processes.size());
        processes = runtimeDataService.getProcessesByFilter("custom", new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(processes);
        Assert.assertEquals(1, processes.size());
        processes = runtimeDataService.getProcessesByDeploymentId(deploymentUnit.getIdentifier(), new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(processes);
        Assert.assertEquals(3, processes.size());
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
    public void testDeploymentOfProcessesOnDefaultKbaseAndKsession() {
        Assert.assertNotNull(deploymentService);
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        DeployedUnit deployed = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        Assert.assertNotNull(deployed);
        Assert.assertNotNull(deployed.getDeploymentUnit());
        Assert.assertNotNull(deployed.getRuntimeManager());
        Assert.assertNull(deployed.getDeployedAssetLocation("customtask"));
        Assert.assertEquals((((((GROUP_ID) + ":") + (ARTIFACT_ID)) + ":") + (VERSION)), deployed.getDeploymentUnit().getIdentifier());
        Assert.assertNotNull(runtimeDataService);
        Collection<ProcessDefinition> processes = runtimeDataService.getProcesses(new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(processes);
        Assert.assertEquals(3, processes.size());
        processes = runtimeDataService.getProcessesByFilter("custom", new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(processes);
        Assert.assertEquals(1, processes.size());
        processes = runtimeDataService.getProcessesByDeploymentId(deploymentUnit.getIdentifier(), new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(processes);
        Assert.assertEquals(3, processes.size());
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

    @Test(expected = RuntimeException.class)
    public void testDuplicatedDeployment() {
        Assert.assertNotNull(deploymentService);
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        DeployedUnit deployedGeneral = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        Assert.assertNotNull(deployedGeneral);
        Assert.assertNotNull(deployedGeneral.getDeploymentUnit());
        Assert.assertNotNull(deployedGeneral.getRuntimeManager());
        // duplicated deployment of the same deployment unit should fail
        deploymentService.deploy(deploymentUnit);
    }

    @Test
    public void testUnDeploymentWithActiveProcesses() {
        Assert.assertNotNull(deploymentService);
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        DeployedUnit deployedGeneral = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        Assert.assertNotNull(deployedGeneral);
        Assert.assertNotNull(deployedGeneral.getDeploymentUnit());
        Assert.assertNotNull(deployedGeneral.getRuntimeManager());
        RuntimeManager manager = deploymentService.getRuntimeManager(deploymentUnit.getIdentifier());
        Assert.assertNotNull(manager);
        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        Assert.assertNotNull(engine);
        Map<String, Object> params = new HashMap<String, Object>();
        ProcessInstance processInstance = engine.getKieSession().startProcess("org.jbpm.writedocument", params);
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        try {
            // undeploy should fail due to active process instances
            deploymentService.undeploy(deploymentUnit);
            Assert.fail("Should fail due to active process instance");
        } catch (IllegalStateException e) {
        }
        engine.getKieSession().abortProcessInstance(processInstance.getId());
    }

    @Test
    public void testDeploymentAndExecutionOfProcessWithImports() {
        Assert.assertNotNull(deploymentService);
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        DeployedUnit deployedGeneral = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        Assert.assertNotNull(deployedGeneral);
        Assert.assertNotNull(deployedGeneral.getDeploymentUnit());
        Assert.assertNotNull(deployedGeneral.getRuntimeManager());
        RuntimeManager manager = deploymentService.getRuntimeManager(deploymentUnit.getIdentifier());
        Assert.assertNotNull(manager);
        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        Assert.assertNotNull(engine);
        Map<String, Object> params = new HashMap<String, Object>();
        ProcessInstance processInstance = engine.getKieSession().startProcess("Import", params);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    @Test
    public void testDeploymentOfProcessWithDescriptor() {
        Assert.assertNotNull(deploymentService);
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, "kjar-with-dd", VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/general/customtask.bpmn");
        processes.add("repo/processes/general/humanTask.bpmn");
        processes.add("repo/processes/general/import.bpmn");
        DeploymentDescriptor customDescriptor = new org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorImpl("org.jbpm.domain");
        customDescriptor.getBuilder().runtimeStrategy(RuntimeStrategy.PER_REQUEST).addWorkItemHandler(new org.kie.internal.runtime.conf.NamedObjectModel("Log", "org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler"));
        Map<String, String> resources = new HashMap<String, String>();
        resources.put(("src/main/resources/" + (DeploymentDescriptor.META_INF_LOCATION)), customDescriptor.toXml());
        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes, resources);
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
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, "kjar-with-dd", VERSION, "KBase-test", "ksession-test2");
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        DeployedUnit deployedGeneral = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        Assert.assertNotNull(deployedGeneral);
        Assert.assertNotNull(deployedGeneral.getDeploymentUnit());
        Assert.assertNotNull(deployedGeneral.getRuntimeManager());
        DeploymentDescriptor descriptor = ((InternalRuntimeManager) (deployedGeneral.getRuntimeManager())).getDeploymentDescriptor();
        Assert.assertNotNull(descriptor);
        Assert.assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
        Assert.assertEquals("org.jbpm.domain", descriptor.getAuditPersistenceUnit());
        Assert.assertEquals(AuditMode.JPA, descriptor.getAuditMode());
        Assert.assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
        Assert.assertEquals(RuntimeStrategy.PER_REQUEST, descriptor.getRuntimeStrategy());
        Assert.assertEquals(0, descriptor.getMarshallingStrategies().size());
        Assert.assertEquals(0, descriptor.getConfiguration().size());
        Assert.assertEquals(0, descriptor.getEnvironmentEntries().size());
        Assert.assertEquals(0, descriptor.getEventListeners().size());
        Assert.assertEquals(0, descriptor.getGlobals().size());
        Assert.assertEquals(0, descriptor.getTaskEventListeners().size());
        Assert.assertEquals(1, descriptor.getWorkItemHandlers().size());
        Assert.assertEquals(0, descriptor.getRequiredRoles().size());
        RuntimeManager manager = deploymentService.getRuntimeManager(deploymentUnit.getIdentifier());
        Assert.assertNotNull(manager);
        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        Assert.assertNotNull(engine);
        Map<String, Object> params = new HashMap<String, Object>();
        ProcessInstance processInstance = engine.getKieSession().startProcess("customtask", params);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        manager.disposeRuntimeEngine(engine);
    }

    @Test(expected = SecurityException.class)
    public void testDeploymentOfProcessWithDescriptorWitSecurityManager() {
        Assert.assertNotNull(deploymentService);
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, "kjar-with-dd", VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/general/customtask.bpmn");
        processes.add("repo/processes/general/humanTask.bpmn");
        processes.add("repo/processes/general/import.bpmn");
        DeploymentDescriptor customDescriptor = new org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorImpl("org.jbpm.domain");
        customDescriptor.getBuilder().runtimeStrategy(RuntimeStrategy.PER_PROCESS_INSTANCE).addWorkItemHandler(new org.kie.internal.runtime.conf.NamedObjectModel("Log", "org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler")).addRequiredRole("experts");
        Map<String, String> resources = new HashMap<String, String>();
        resources.put(("src/main/resources/" + (DeploymentDescriptor.META_INF_LOCATION)), customDescriptor.toXml());
        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes, resources);
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
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, "kjar-with-dd", VERSION, "KBase-test", "ksession-test2");
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        DeployedUnit deployedGeneral = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        Assert.assertNotNull(deployedGeneral);
        Assert.assertNotNull(deployedGeneral.getDeploymentUnit());
        Assert.assertNotNull(deployedGeneral.getRuntimeManager());
        DeploymentDescriptor descriptor = ((InternalRuntimeManager) (deployedGeneral.getRuntimeManager())).getDeploymentDescriptor();
        Assert.assertNotNull(descriptor);
        Assert.assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
        Assert.assertEquals("org.jbpm.domain", descriptor.getAuditPersistenceUnit());
        Assert.assertEquals(AuditMode.JPA, descriptor.getAuditMode());
        Assert.assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
        Assert.assertEquals(RuntimeStrategy.PER_PROCESS_INSTANCE, descriptor.getRuntimeStrategy());
        Assert.assertEquals(0, descriptor.getMarshallingStrategies().size());
        Assert.assertEquals(0, descriptor.getConfiguration().size());
        Assert.assertEquals(0, descriptor.getEnvironmentEntries().size());
        Assert.assertEquals(0, descriptor.getEventListeners().size());
        Assert.assertEquals(0, descriptor.getGlobals().size());
        Assert.assertEquals(0, descriptor.getTaskEventListeners().size());
        Assert.assertEquals(1, descriptor.getWorkItemHandlers().size());
        Assert.assertEquals(1, descriptor.getRequiredRoles().size());
        RuntimeManager manager = deploymentService.getRuntimeManager(deploymentUnit.getIdentifier());
        Assert.assertNotNull(manager);
        manager.getRuntimeEngine(EmptyContext.get());
    }

    @Test
    public void testDeploymentOfProcessWithDescriptorKieConteinerInjection() {
        Assert.assertNotNull(deploymentService);
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, "kjar-with-dd", VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/general/customtask.bpmn");
        processes.add("repo/processes/general/humanTask.bpmn");
        processes.add("repo/processes/general/import.bpmn");
        DeploymentDescriptor customDescriptor = new org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorImpl("org.jbpm.domain");
        customDescriptor.getBuilder().runtimeStrategy(RuntimeStrategy.PER_REQUEST).addWorkItemHandler(new org.kie.internal.runtime.conf.NamedObjectModel("mvel", "Log", "new org.jbpm.kie.services.test.objects.KieConteinerSystemOutWorkItemHandler(kieContainer)"));
        Map<String, String> resources = new HashMap<String, String>();
        resources.put(("src/main/resources/" + (DeploymentDescriptor.META_INF_LOCATION)), customDescriptor.toXml());
        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes, resources);
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
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, "kjar-with-dd", VERSION, "KBase-test", "ksession-test2");
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        DeployedUnit deployedGeneral = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        Assert.assertNotNull(deployedGeneral);
        Assert.assertNotNull(deployedGeneral.getDeploymentUnit());
        Assert.assertNotNull(deployedGeneral.getRuntimeManager());
        DeploymentDescriptor descriptor = ((InternalRuntimeManager) (deployedGeneral.getRuntimeManager())).getDeploymentDescriptor();
        Assert.assertNotNull(descriptor);
        Assert.assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
        Assert.assertEquals("org.jbpm.domain", descriptor.getAuditPersistenceUnit());
        Assert.assertEquals(AuditMode.JPA, descriptor.getAuditMode());
        Assert.assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
        Assert.assertEquals(RuntimeStrategy.PER_REQUEST, descriptor.getRuntimeStrategy());
        Assert.assertEquals(0, descriptor.getMarshallingStrategies().size());
        Assert.assertEquals(0, descriptor.getConfiguration().size());
        Assert.assertEquals(0, descriptor.getEnvironmentEntries().size());
        Assert.assertEquals(0, descriptor.getEventListeners().size());
        Assert.assertEquals(0, descriptor.getGlobals().size());
        Assert.assertEquals(0, descriptor.getTaskEventListeners().size());
        Assert.assertEquals(1, descriptor.getWorkItemHandlers().size());
        Assert.assertEquals(0, descriptor.getRequiredRoles().size());
        RuntimeManager manager = deploymentService.getRuntimeManager(deploymentUnit.getIdentifier());
        Assert.assertNotNull(manager);
        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        Assert.assertNotNull(engine);
        Map<String, Object> params = new HashMap<String, Object>();
        ProcessInstance processInstance = engine.getKieSession().startProcess("customtask", params);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        manager.disposeRuntimeEngine(engine);
    }

    @Test
    public void testDeploymentOfProcessesKieConteinerInjection() {
        Assert.assertNotNull(deploymentService);
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION, "KBase-test", "ksession-test-2");
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        Assert.assertNotNull(deploymentUnit.getDeploymentDescriptor());
        DeployedUnit deployed = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        Assert.assertNotNull(deployed);
        Assert.assertNotNull(deployed.getDeploymentUnit());
        Assert.assertNotNull(deployed.getRuntimeManager());
        Assert.assertNull(deployed.getDeployedAssetLocation("customtask"));
        Assert.assertEquals((((((((((GROUP_ID) + ":") + (ARTIFACT_ID)) + ":") + (VERSION)) + ":") + "KBase-test") + ":") + "ksession-test-2"), deployed.getDeploymentUnit().getIdentifier());
        RuntimeManager manager = deploymentService.getRuntimeManager(deploymentUnit.getIdentifier());
        Assert.assertNotNull(manager);
        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        Assert.assertNotNull(engine);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", "test");
        ProcessInstance processInstance = engine.getKieSession().startProcess("customtask", params);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        manager.disposeRuntimeEngine(engine);
    }

    @Test
    public void testDeploymentAvoidEmptyDescriptorOverride() {
        Assert.assertNotNull(deploymentService);
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, "kjar-with-dd", VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/general/customtask.bpmn");
        processes.add("repo/processes/general/humanTask.bpmn");
        processes.add("repo/processes/general/import.bpmn");
        DeploymentDescriptor customDescriptor = new org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorImpl("org.jbpm.domain");
        customDescriptor.getBuilder().runtimeStrategy(RuntimeStrategy.PER_REQUEST).addWorkItemHandler(new org.kie.internal.runtime.conf.NamedObjectModel("Log", "org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler"));
        Map<String, String> resources = new HashMap<String, String>();
        resources.put(("src/main/resources/" + (DeploymentDescriptor.META_INF_LOCATION)), customDescriptor.toXml());
        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes, resources);
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
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, "kjar-with-dd", VERSION, "KBase-test", "ksession-test2");
        // let's simulate change of deployment descriptor on deploy time
        deploymentUnit.setDeploymentDescriptor(new org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorImpl());// set empty one...
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        DeployedUnit deployedGeneral = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        Assert.assertNotNull(deployedGeneral);
        Assert.assertNotNull(deployedGeneral.getDeploymentUnit());
        Assert.assertNotNull(deployedGeneral.getRuntimeManager());
        DeploymentDescriptor descriptor = ((InternalRuntimeManager) (deployedGeneral.getRuntimeManager())).getDeploymentDescriptor();
        Assert.assertNotNull(descriptor);
        Assert.assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
        Assert.assertEquals("org.jbpm.domain", descriptor.getAuditPersistenceUnit());
        Assert.assertEquals(AuditMode.JPA, descriptor.getAuditMode());
        Assert.assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
        Assert.assertEquals(RuntimeStrategy.PER_REQUEST, descriptor.getRuntimeStrategy());
        Assert.assertEquals(0, descriptor.getMarshallingStrategies().size());
        Assert.assertEquals(0, descriptor.getConfiguration().size());
        Assert.assertEquals(0, descriptor.getEnvironmentEntries().size());
        Assert.assertEquals(0, descriptor.getEventListeners().size());
        Assert.assertEquals(0, descriptor.getGlobals().size());
        Assert.assertEquals(0, descriptor.getTaskEventListeners().size());
        Assert.assertEquals(1, descriptor.getWorkItemHandlers().size());
        Assert.assertEquals(0, descriptor.getRequiredRoles().size());
        RuntimeManager manager = deploymentService.getRuntimeManager(deploymentUnit.getIdentifier());
        Assert.assertNotNull(manager);
        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        Assert.assertNotNull(engine);
        Map<String, Object> params = new HashMap<String, Object>();
        ProcessInstance processInstance = engine.getKieSession().startProcess("customtask", params);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        manager.disposeRuntimeEngine(engine);
    }
}

