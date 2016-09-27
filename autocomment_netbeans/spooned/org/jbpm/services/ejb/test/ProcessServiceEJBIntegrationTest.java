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
import org.kie.internal.process.CorrelationKey;
import org.jbpm.services.ejb.api.DefinitionServiceEJBLocal;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jbpm.services.ejb.api.DeploymentServiceEJBLocal;
import org.jbpm.services.api.model.DeploymentUnit;
import javax.ejb.EJB;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.kie.api.KieServices;
import java.util.List;
import java.util.Map;
import org.kie.scanner.MavenRepository;
import org.jbpm.services.api.model.NodeInstanceDesc;
import org.kie.api.runtime.process.ProcessInstance;
import org.jbpm.services.ejb.api.ProcessServiceEJBLocal;
import org.kie.api.builder.ReleaseId;
import org.junit.runner.RunWith;
import org.jbpm.services.ejb.api.RuntimeDataServiceEJBLocal;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Test;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.kie.api.runtime.process.WorkItem;

@RunWith(value = Arquillian.class)
public class ProcessServiceEJBIntegrationTest extends AbstractTestSupport {
    @Deployment
    public static WebArchive createDeployment() {
        File archive = new File("target/sample-war-ejb-app.war");
        if (!(archive.exists())) {
            throw new IllegalStateException("There is no archive yet generated, run maven build or mvn assembly:assembly");
        } 
        WebArchive war = ShrinkWrap.createFromZipFile(WebArchive.class, archive);
        war.addPackage("org.jbpm.services.ejb.test");// test cases
        
        // deploy test kjar
        ProcessServiceEJBIntegrationTest.deployKjar();
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
    }

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

    @EJB
    private DefinitionServiceEJBLocal bpmn2Service;

    @EJB
    private DeploymentServiceEJBLocal deploymentService;

    @EJB
    private ProcessServiceEJBLocal processService;

    @EJB
    private RuntimeDataServiceEJBLocal runtimeDataService;

    @Test
    public void testStartProcess() {
        Assert.assertNotNull(deploymentService);
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
        Assert.assertTrue(isDeployed);
        Assert.assertNotNull(processService);
        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "customtask");
        Assert.assertNotNull(processInstanceId);
        ProcessInstance pi = processService.getProcessInstance(processInstanceId);
        Assert.assertNull(pi);
    }

    @Test
    public void testStartProcessWithParms() {
        Assert.assertNotNull(deploymentService);
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
        Assert.assertTrue(isDeployed);
        Assert.assertNotNull(processService);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", "test");
        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "customtask", params);
        Assert.assertNotNull(processInstanceId);
        ProcessInstance pi = processService.getProcessInstance(processInstanceId);
        Assert.assertNull(pi);
    }

    @Test
    public void testStartProcessWithCorrelationKey() {
        Assert.assertNotNull(deploymentService);
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        Assert.assertNotNull(processService);
        CorrelationKey key = KieInternalServices.Factory.get().newCorrelationKeyFactory().newCorrelationKey("my business key");
        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "customtask", key);
        Assert.assertNotNull(processInstanceId);
        ProcessInstance pi = processService.getProcessInstance(key);
        Assert.assertNull(pi);
    }

    @Test
    public void testStartProcessWithParmsWithCorrelationKey() {
        Assert.assertNotNull(deploymentService);
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        Assert.assertNotNull(processService);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", "test");
        CorrelationKey key = KieInternalServices.Factory.get().newCorrelationKeyFactory().newCorrelationKey("my business key");
        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "customtask", key, params);
        Assert.assertNotNull(processInstanceId);
        ProcessInstance pi = processService.getProcessInstance(key);
        Assert.assertNull(pi);
    }

    @Test
    public void testStartAndAbortProcess() {
        Assert.assertNotNull(deploymentService);
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
        Assert.assertTrue(isDeployed);
        Assert.assertNotNull(processService);
        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        ProcessInstance pi = processService.getProcessInstance(processInstanceId);
        Assert.assertNotNull(pi);
        processService.abortProcessInstance(processInstanceId);
        pi = processService.getProcessInstance(processInstanceId);
        Assert.assertNull(pi);
    }

    @Test
    public void testStartAndAbortProcesses() {
        Assert.assertNotNull(deploymentService);
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
        Assert.assertTrue(isDeployed);
        Assert.assertNotNull(processService);
        // first start first instance
        long processInstanceId1 = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId1);
        ProcessInstance pi = processService.getProcessInstance(processInstanceId1);
        Assert.assertNotNull(pi);
        // then start second instance
        long processInstanceId2 = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId2);
        ProcessInstance pi2 = processService.getProcessInstance(processInstanceId2);
        Assert.assertNotNull(pi2);
        List<Long> instances = new ArrayList<Long>();
        instances.add(processInstanceId1);
        instances.add(processInstanceId2);
        // and lastly cancel both
        processService.abortProcessInstances(instances);
        pi = processService.getProcessInstance(processInstanceId1);
        Assert.assertNull(pi);
        pi2 = processService.getProcessInstance(processInstanceId2);
        Assert.assertNull(pi2);
    }

    @Test
    public void testStartAndSignalProcess() {
        Assert.assertNotNull(deploymentService);
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
        Assert.assertTrue(isDeployed);
        Assert.assertNotNull(processService);
        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "signal");
        Assert.assertNotNull(processInstanceId);
        ProcessInstance pi = processService.getProcessInstance(processInstanceId);
        Assert.assertNotNull(pi);
        Collection<String> signals = processService.getAvailableSignals(processInstanceId);
        Assert.assertNotNull(signals);
        Assert.assertEquals(1, signals.size());
        Assert.assertTrue(signals.contains("MySignal"));
        processService.signalProcessInstance(processInstanceId, "MySignal", null);
        pi = processService.getProcessInstance(processInstanceId);
        Assert.assertNull(pi);
    }

    @Test
    public void testStartAndSignalProcesses() {
        Assert.assertNotNull(deploymentService);
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
        Assert.assertTrue(isDeployed);
        Assert.assertNotNull(processService);
        // first start first instance
        long processInstanceId1 = processService.startProcess(deploymentUnit.getIdentifier(), "signal");
        Assert.assertNotNull(processInstanceId1);
        ProcessInstance pi = processService.getProcessInstance(processInstanceId1);
        Assert.assertNotNull(pi);
        // then start second instance
        long processInstanceId2 = processService.startProcess(deploymentUnit.getIdentifier(), "signal");
        Assert.assertNotNull(processInstanceId2);
        ProcessInstance pi2 = processService.getProcessInstance(processInstanceId2);
        Assert.assertNotNull(pi2);
        List<Long> instances = new ArrayList<Long>();
        instances.add(processInstanceId1);
        instances.add(processInstanceId2);
        // and lastly cancel both
        processService.signalProcessInstances(instances, "MySignal", null);
        pi = processService.getProcessInstance(processInstanceId1);
        Assert.assertNull(pi);
        pi2 = processService.getProcessInstance(processInstanceId2);
        Assert.assertNull(pi2);
    }

    @Test
    public void testStartAndSignal() {
        Assert.assertNotNull(deploymentService);
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
        Assert.assertTrue(isDeployed);
        Assert.assertNotNull(processService);
        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "signal");
        Assert.assertNotNull(processInstanceId);
        long processInstanceId2 = processService.startProcess(deploymentUnit.getIdentifier(), "signal");
        Assert.assertNotNull(processInstanceId2);
        ProcessInstance pi = processService.getProcessInstance(processInstanceId);
        Assert.assertNotNull(pi);
        pi = processService.getProcessInstance(processInstanceId2);
        Assert.assertNotNull(pi);
        processService.signalEvent(deploymentUnit.getIdentifier(), "MySignal", null);
        pi = processService.getProcessInstance(processInstanceId);
        Assert.assertNull(pi);
        pi = processService.getProcessInstance(processInstanceId2);
        Assert.assertNull(pi);
    }

    @Test
    public void testStartProcessAndChangeVariables() {
        Assert.assertNotNull(deploymentService);
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
        Assert.assertTrue(isDeployed);
        Assert.assertNotNull(processService);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("approval_document", "test");
        params.put("approval_reviewComment", "need review");
        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument", params);
        Assert.assertNotNull(processInstanceId);
        ProcessInstance pi = processService.getProcessInstance(processInstanceId);
        Assert.assertNotNull(pi);
        // get variable by name
        Object variableValue = processService.getProcessInstanceVariable(processInstanceId, "approval_document");
        Assert.assertNotNull(variableValue);
        Assert.assertTrue((variableValue instanceof String));
        Assert.assertEquals("test", variableValue);
        // get all variables
        Map<String, Object> variables = processService.getProcessInstanceVariables(processInstanceId);
        Assert.assertNotNull(variables);
        Assert.assertEquals(2, variables.size());
        Assert.assertTrue(variables.containsKey("approval_document"));
        Assert.assertTrue(variables.containsKey("approval_reviewComment"));
        Assert.assertEquals("test", variables.get("approval_document"));
        Assert.assertEquals("need review", variables.get("approval_reviewComment"));
        // now change single variable
        processService.setProcessVariable(processInstanceId, "approval_reviewComment", "updated review comment");
        // let's verify it
        variableValue = processService.getProcessInstanceVariable(processInstanceId, "approval_reviewComment");
        Assert.assertNotNull(variableValue);
        Assert.assertTrue((variableValue instanceof String));
        Assert.assertEquals("updated review comment", variableValue);
        // and lastly let's update both variables
        params = new HashMap<String, Object>();
        params.put("approval_document", "updated document");
        params.put("approval_reviewComment", "final review");
        processService.setProcessVariables(processInstanceId, params);
        variables = processService.getProcessInstanceVariables(processInstanceId);
        Assert.assertNotNull(variables);
        Assert.assertEquals(2, variables.size());
        Assert.assertTrue(variables.containsKey("approval_document"));
        Assert.assertTrue(variables.containsKey("approval_reviewComment"));
        Assert.assertEquals("updated document", variables.get("approval_document"));
        Assert.assertEquals("final review", variables.get("approval_reviewComment"));
        processService.abortProcessInstance(processInstanceId);
        pi = processService.getProcessInstance(processInstanceId);
        Assert.assertNull(pi);
    }

    @Test
    public void testStartProcessAndCompleteWorkItem() {
        Assert.assertNotNull(deploymentService);
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
        Assert.assertTrue(isDeployed);
        Assert.assertNotNull(processService);
        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        ProcessInstance pi = processService.getProcessInstance(processInstanceId);
        Assert.assertNotNull(pi);
        Collection<NodeInstanceDesc> activeNodes = runtimeDataService.getProcessInstanceHistoryActive(processInstanceId, new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(activeNodes);
        Assert.assertEquals(1, activeNodes.size());
        Assert.assertEquals("Write a Document", activeNodes.iterator().next().getName());
        Map<String, Object> outcome = new HashMap<String, Object>();
        outcome.put("Result", "here is my first document");
        processService.completeWorkItem(activeNodes.iterator().next().getWorkItemId(), outcome);
        activeNodes = runtimeDataService.getProcessInstanceHistoryActive(processInstanceId, new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(activeNodes);
        Assert.assertEquals(2, activeNodes.size());
        Object variableValue = processService.getProcessInstanceVariable(processInstanceId, "approval_document");
        Assert.assertNotNull(variableValue);
        Assert.assertTrue((variableValue instanceof String));
        Assert.assertEquals("here is my first document", variableValue);
        processService.abortProcessInstance(processInstanceId);
        pi = processService.getProcessInstance(processInstanceId);
        Assert.assertNull(pi);
    }

    @Test
    public void testStartProcessAndAbortWorkItem() {
        Assert.assertNotNull(deploymentService);
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
        Assert.assertTrue(isDeployed);
        Assert.assertNotNull(processService);
        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        ProcessInstance pi = processService.getProcessInstance(processInstanceId);
        Assert.assertNotNull(pi);
        Collection<NodeInstanceDesc> activeNodes = runtimeDataService.getProcessInstanceHistoryActive(processInstanceId, new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(activeNodes);
        Assert.assertEquals(1, activeNodes.size());
        Assert.assertEquals("Write a Document", activeNodes.iterator().next().getName());
        processService.abortWorkItem(activeNodes.iterator().next().getWorkItemId());
        activeNodes = runtimeDataService.getProcessInstanceHistoryActive(processInstanceId, new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(activeNodes);
        Assert.assertEquals(2, activeNodes.size());
        Object variableValue = processService.getProcessInstanceVariable(processInstanceId, "approval_document");
        Assert.assertNull(variableValue);
        processService.abortProcessInstance(processInstanceId);
        pi = processService.getProcessInstance(processInstanceId);
        Assert.assertNull(pi);
    }

    @Test
    public void testStartProcessAndGetWorkItem() {
        Assert.assertNotNull(deploymentService);
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
        Assert.assertTrue(isDeployed);
        Assert.assertNotNull(processService);
        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        ProcessInstance pi = processService.getProcessInstance(processInstanceId);
        Assert.assertNotNull(pi);
        Collection<NodeInstanceDesc> activeNodes = runtimeDataService.getProcessInstanceHistoryActive(processInstanceId, new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(activeNodes);
        Assert.assertEquals(1, activeNodes.size());
        Assert.assertEquals("Write a Document", activeNodes.iterator().next().getName());
        WorkItem wi = processService.getWorkItem(activeNodes.iterator().next().getWorkItemId());
        Assert.assertNotNull(wi);
        Assert.assertEquals("Human Task", wi.getName());
        Assert.assertEquals("Write a Document", wi.getParameter("NodeName"));
        processService.abortProcessInstance(processInstanceId);
        pi = processService.getProcessInstance(processInstanceId);
        Assert.assertNull(pi);
    }

    @Test
    public void testStartProcessAndGetWorkItems() {
        Assert.assertNotNull(deploymentService);
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
        Assert.assertTrue(isDeployed);
        Assert.assertNotNull(processService);
        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        ProcessInstance pi = processService.getProcessInstance(processInstanceId);
        Assert.assertNotNull(pi);
        Collection<NodeInstanceDesc> activeNodes = runtimeDataService.getProcessInstanceHistoryActive(processInstanceId, new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(activeNodes);
        Assert.assertEquals(1, activeNodes.size());
        Assert.assertEquals("Write a Document", activeNodes.iterator().next().getName());
        List<WorkItem> wis = processService.getWorkItemByProcessInstance(processInstanceId);
        Assert.assertNotNull(wis);
        Assert.assertEquals(1, wis.size());
        Assert.assertEquals("Human Task", wis.get(0).getName());
        Assert.assertEquals("Write a Document", wis.get(0).getParameter("NodeName"));
        processService.abortProcessInstance(processInstanceId);
        pi = processService.getProcessInstance(processInstanceId);
        Assert.assertNull(pi);
    }

    @Test
    public void testStartProcessAndExecuteCmd() {
        Assert.assertNotNull(deploymentService);
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
        Assert.assertTrue(isDeployed);
        Assert.assertNotNull(processService);
        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        ProcessInstance pi = processService.execute(deploymentUnit.getIdentifier(), new org.drools.core.command.runtime.process.GetProcessInstanceCommand(processInstanceId));
        Assert.assertNotNull(pi);
        processService.abortProcessInstance(processInstanceId);
        pi = processService.getProcessInstance(processInstanceId);
        Assert.assertNull(pi);
    }

    @Test
    public void testStartProcessAfterDeactivation() {
        Assert.assertNotNull(deploymentService);
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
        Assert.assertTrue(isDeployed);
        Assert.assertNotNull(processService);
        deploymentService.deactivate(deploymentUnit.getIdentifier());
        try {
            processService.startProcess(deploymentUnit.getIdentifier(), "customtask");
            Assert.fail("Deployment is deactivated so cannot start new process instances");
        } catch (Exception e) {
            Assert.assertEquals("org.jbpm.services.api.DeploymentNotFoundException: Deployments org.jbpm.test:test-module:1.0.0-SNAPSHOT is not active", e.getMessage());
        }
    }

    @Test
    public void testStartProcessAndCompleteWorkItemAfterDeactivation() {
        Assert.assertNotNull(deploymentService);
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
        Assert.assertTrue(isDeployed);
        Assert.assertNotNull(processService);
        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        ProcessInstance pi = processService.getProcessInstance(processInstanceId);
        Assert.assertNotNull(pi);
        deploymentService.deactivate(deploymentUnit.getIdentifier());
        Collection<NodeInstanceDesc> activeNodes = runtimeDataService.getProcessInstanceHistoryActive(processInstanceId, new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(activeNodes);
        Assert.assertEquals(1, activeNodes.size());
        Assert.assertEquals("Write a Document", activeNodes.iterator().next().getName());
        Map<String, Object> outcome = new HashMap<String, Object>();
        outcome.put("Result", "here is my first document");
        processService.completeWorkItem(activeNodes.iterator().next().getWorkItemId(), outcome);
        activeNodes = runtimeDataService.getProcessInstanceHistoryActive(processInstanceId, new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(activeNodes);
        Assert.assertEquals(2, activeNodes.size());
        Object variableValue = processService.getProcessInstanceVariable(processInstanceId, "approval_document");
        Assert.assertNotNull(variableValue);
        Assert.assertTrue((variableValue instanceof String));
        Assert.assertEquals("here is my first document", variableValue);
        processService.abortProcessInstance(processInstanceId);
        pi = processService.getProcessInstance(processInstanceId);
        Assert.assertNull(pi);
    }
}

