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


package org.jbpm.services.ejb.test;

import org.junit.After;
import org.jboss.arquillian.junit.Arquillian;
import java.util.ArrayList;
import org.junit.Assert;
import java.util.Collection;
import org.jbpm.services.api.model.DeployedUnit;
import org.jbpm.kie.services.impl.DeployedUnitImpl;
import org.jboss.arquillian.container.test.api.Deployment;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.jbpm.services.ejb.api.DeploymentServiceEJBLocal;
import org.jbpm.kie.services.impl.store.DeploymentStore;
import org.jbpm.services.api.model.DeploymentUnit;
import javax.ejb.EJB;
import org.kie.internal.runtime.manager.context.EmptyContext;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.kie.api.KieServices;
import java.util.List;
import java.util.Map;
import org.kie.scanner.MavenRepository;
import org.jbpm.services.api.model.ProcessDefinition;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.builder.ReleaseId;
import org.junit.runner.RunWith;
import org.jbpm.services.ejb.api.RuntimeDataServiceEJBLocal;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.kie.api.task.model.TaskSummary;
import org.junit.Test;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.jbpm.services.ejb.impl.tx.TransactionalCommandServiceEJBImpl;
import org.jboss.shrinkwrap.api.spec.WebArchive;

@RunWith(value = Arquillian.class)
public class DeploymentServiceEJBIntegrationTest extends AbstractTestSupport {
    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();

    @After
    public void cleanup() {
        cleanupSingletonSessionId();
        if (((units) != null) && (!(units.isEmpty()))) {
            for (DeploymentUnit unit : units) {
                deploymentService.undeploy(unit);
            }
            units.clear();
        } 
    }

    @Deployment
    public static WebArchive createDeployment() {
        File archive = new File("target/sample-war-ejb-app.war");
        if (!(archive.exists())) {
            throw new IllegalStateException("There is no archive yet generated, run maven build or mvn assembly:assembly");
        } 
        WebArchive war = ShrinkWrap.createFromZipFile(WebArchive.class, archive);
        war.addPackage("org.jbpm.services.ejb.test");// test cases
        
        // deploy test kjar
        DeploymentServiceEJBIntegrationTest.deployKjar();
        return war;
    }

    protected static void deployKjar() {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("processes/customtask.bpmn");
        processes.add("processes/humanTask.bpmn");
        processes.add("processes/signal.bpmn");
        processes.add("processes/import.bpmn");
        processes.add("processes/callactivity.bpmn");
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
        repository.installArtifact(releaseId, kJar1, pom);
        ReleaseId releaseIdSupport = ks.newReleaseId(GROUP_ID, "support", VERSION);
        List<String> processesSupport = new ArrayList<String>();
        processesSupport.add("processes/support.bpmn");
        InternalKieModule kJar2 = createKieJar(ks, releaseIdSupport, processesSupport);
        File pom2 = new File("target/kmodule2", "pom.xml");
        pom2.getParentFile().mkdir();
        try {
            FileOutputStream fs = new FileOutputStream(pom2);
            fs.write(getPom(releaseIdSupport).getBytes());
            fs.close();
        } catch (Exception e) {
        }
        repository.installArtifact(releaseIdSupport, kJar2, pom2);
        ReleaseId releaseId3 = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, "1.1.0-SNAPSHOT");
        InternalKieModule kJar3 = createKieJar(ks, releaseId3, processes);
        File pom3 = new File("target/kmodule3", "pom.xml");
        pom3.getParentFile().mkdirs();
        try {
            FileOutputStream fs = new FileOutputStream(pom3);
            fs.write(getPom(releaseId3).getBytes());
            fs.close();
        } catch (Exception e) {
        }
        repository = MavenRepository.getMavenRepository();
        repository.installArtifact(releaseId3, kJar3, pom3);
    }

    @EJB
    private RuntimeDataServiceEJBLocal runtimeDataService;

    @EJB
    private DeploymentServiceEJBLocal deploymentService;

    @EJB(beanInterface = TransactionalCommandServiceEJBImpl.class)
    private TransactionalCommandService commandService;

    @Test
    public void testDeploymentOfProcesses() {
        Assert.assertNotNull(deploymentService);
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
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
    public void testDeploymentOfAllProcesses() {
        Assert.assertNotNull(deploymentService);
        // deploy first unit
        DeploymentUnit deploymentUnitGeneral = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnitGeneral);
        units.add(deploymentUnitGeneral);
        RuntimeManager managerGeneral = deploymentService.getRuntimeManager(deploymentUnitGeneral.getIdentifier());
        Assert.assertNotNull(managerGeneral);
        // deploy second unit
        DeploymentUnit deploymentUnitSupport = new KModuleDeploymentUnit(GROUP_ID, "support", VERSION);
        deploymentService.deploy(deploymentUnitSupport);
        units.add(deploymentUnitSupport);
        DeployedUnit deployedGeneral = deploymentService.getDeployedUnit(deploymentUnitGeneral.getIdentifier());
        Assert.assertNotNull(deployedGeneral);
        Assert.assertNotNull(deployedGeneral.getDeploymentUnit());
        Assert.assertNotNull(deployedGeneral.getRuntimeManager());
        RuntimeManager managerSupport = deploymentService.getRuntimeManager(deploymentUnitSupport.getIdentifier());
        Assert.assertNotNull(managerSupport);
        DeployedUnit deployedSupport = deploymentService.getDeployedUnit(deploymentUnitSupport.getIdentifier());
        Assert.assertNotNull(deployedSupport);
        Assert.assertNotNull(deployedSupport.getDeploymentUnit());
        Assert.assertNotNull(deployedSupport.getRuntimeManager());
        // execute process that is bundled in first deployment unit
        RuntimeEngine engine = managerGeneral.getRuntimeEngine(EmptyContext.get());
        Assert.assertNotNull(engine);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", "test");
        ProcessInstance processInstance = engine.getKieSession().startProcess("customtask", params);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        // execute process that is in second deployment unit
        RuntimeEngine engineSupport = managerSupport.getRuntimeEngine(EmptyContext.get());
        Assert.assertNotNull(engineSupport);
        ProcessInstance supportPI = engineSupport.getKieSession().startProcess("support.process");
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, supportPI.getState());
        List<TaskSummary> tasks = engineSupport.getTaskService().getTasksAssignedAsPotentialOwner("salaboy", "en-UK");
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());
        engineSupport.getKieSession().abortProcessInstance(supportPI.getId());
        Assert.assertNull(engineSupport.getKieSession().getProcessInstance(supportPI.getState()));
    }

    @Test
    public void testDuplicatedDeployment() {
        Assert.assertNotNull(deploymentService);
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        DeployedUnit deployedGeneral = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        Assert.assertNotNull(deployedGeneral);
        Assert.assertNotNull(deployedGeneral.getDeploymentUnit());
        Assert.assertNotNull(deployedGeneral.getRuntimeManager());
        try {
            // duplicated deployment of the same deployment unit should fail
            deploymentService.deploy(deploymentUnit);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().endsWith("Unit with id org.jbpm.test:test-module:1.0.0-SNAPSHOT is already deployed"));
        }
    }

    @Test
    public void testDeploymentOfMultipleVersions() {
        Assert.assertNotNull(deploymentService);
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        DeploymentUnit deploymentUnit3 = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, "1.1.0-SNAPSHOT");
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        deploymentService.deploy(deploymentUnit3);
        units.add(deploymentUnit3);
        DeployedUnit deployed = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        Assert.assertNotNull(deployed);
        Assert.assertNotNull(deployed.getDeploymentUnit());
        Assert.assertNotNull(deployed.getRuntimeManager());
        Assert.assertEquals(0, ((DeployedUnitImpl) (deployed)).getDeployedClasses().size());
        DeployedUnit deployed3 = deploymentService.getDeployedUnit(deploymentUnit3.getIdentifier());
        Assert.assertNotNull(deployed3);
        Assert.assertNotNull(deployed3.getDeploymentUnit());
        Assert.assertNotNull(deployed3.getRuntimeManager());
        Assert.assertEquals(0, ((DeployedUnitImpl) (deployed3)).getDeployedClasses().size());
        Assert.assertNotNull(runtimeDataService);
        Collection<ProcessDefinition> processes = runtimeDataService.getProcesses(new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(processes);
        Assert.assertEquals(10, processes.size());
        DeployedUnit deployedLatest = deploymentService.getDeployedUnit(((((GROUP_ID) + ":") + (ARTIFACT_ID)) + ":LATEST"));
        Assert.assertNotNull(deployedLatest);
        Assert.assertNotNull(deployedLatest.getDeploymentUnit());
        Assert.assertNotNull(deployedLatest.getRuntimeManager());
        Assert.assertEquals(deploymentUnit3.getIdentifier(), deployedLatest.getDeploymentUnit().getIdentifier());
    }

    @Test
    public void testDeploymentOfProcessesWithActivation() {
        Assert.assertNotNull(deploymentService);
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        DeployedUnit deployed = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        Assert.assertNotNull(deployed);
        Assert.assertNotNull(deployed.getDeploymentUnit());
        Assert.assertNotNull(deployed.getRuntimeManager());
        Assert.assertTrue(deployed.isActive());
        Assert.assertEquals(0, ((DeployedUnitImpl) (deployed)).getDeployedClasses().size());
        Assert.assertNotNull(runtimeDataService);
        Collection<ProcessDefinition> processes = runtimeDataService.getProcesses(new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(processes);
        Assert.assertEquals(5, processes.size());
        RuntimeManager manager = deploymentService.getRuntimeManager(deploymentUnit.getIdentifier());
        Assert.assertNotNull(manager);
        // then deactivate it
        deploymentService.deactivate(deploymentUnit.getIdentifier());
        deployed = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        Assert.assertNotNull(deployed);
        Assert.assertNotNull(deployed.getDeploymentUnit());
        Assert.assertNotNull(deployed.getRuntimeManager());
        Assert.assertFalse(deployed.isActive());
        processes = runtimeDataService.getProcesses(new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(processes);
        Assert.assertEquals(0, processes.size());
        // and not activate it again
        deploymentService.activate(deploymentUnit.getIdentifier());
        deployed = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        Assert.assertNotNull(deployed);
        Assert.assertNotNull(deployed.getDeploymentUnit());
        Assert.assertNotNull(deployed.getRuntimeManager());
        Assert.assertTrue(deployed.isActive());
        processes = runtimeDataService.getProcesses(new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(processes);
        Assert.assertEquals(5, processes.size());
    }

    @Test
    public void testDeploymentOfProcessesVerifyTransientObjectOmitted() {
        Assert.assertNotNull(deploymentService);
        Assert.assertNotNull(commandService);
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
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
        DeploymentStore store = new DeploymentStore();
        store.setCommandService(commandService);
        Collection<DeploymentUnit> units = store.getEnabledDeploymentUnits();
        Assert.assertNotNull(units);
        Assert.assertEquals(1, units.size());
        DeploymentUnit enabled = units.iterator().next();
        Assert.assertNotNull(enabled);
        Assert.assertTrue((enabled instanceof KModuleDeploymentUnit));
        KModuleDeploymentUnit kmoduleEnabled = ((KModuleDeploymentUnit) (enabled));
        DeploymentDescriptor dd = kmoduleEnabled.getDeploymentDescriptor();
        Assert.assertNotNull(dd);
        // ejb deployment service add transitively Async WorkItem handler that should not be stored as part of deployment store
        Assert.assertEquals(0, dd.getWorkItemHandlers().size());
    }
}

