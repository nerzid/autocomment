/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
import org.jbpm.services.api.model.DeploymentUnit;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.io.IOException;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.kie.api.KieServices;
import java.util.List;
import java.util.Map;
import org.kie.scanner.MavenRepository;
import org.jbpm.kie.services.impl.model.ProcessAssetDesc;
import org.jbpm.services.api.model.ProcessDefinition;
import org.kie.api.builder.ReleaseId;
import org.junit.Test;
import org.jbpm.services.api.model.UserTaskDefinition;

public class BPMN2DataServicesTest extends AbstractKieServicesBaseTest {
    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();

    @Before
    public void prepare() {
        configureServices();
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/hr/hiring.bpmn2");
        processes.add("repo/processes/general/customtask.bpmn");
        processes.add("repo/processes/general/humanTask.bpmn");
        processes.add("repo/processes/general/import.bpmn");
        processes.add("repo/processes/general/callactivity.bpmn");
        processes.add("repo/processes/general/BPMN2-UserTask.bpmn2");
        processes.add("repo/processes/itemrefissue/itemrefissue.bpmn");
        processes.add("repo/processes/general/ObjectVariableProcess.bpmn2");
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
    public void testHumanTaskProcess() throws IOException {
        Assert.assertNotNull(deploymentService);
        DeploymentUnit deploymentUnit = new org.jbpm.kie.services.impl.KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        String processId = "org.jbpm.writedocument";
        ProcessDefinition procDef = bpmn2Service.getProcessDefinition(deploymentUnit.getIdentifier(), processId);
        Assert.assertNotNull(procDef);
        Assert.assertEquals(procDef.getId(), "org.jbpm.writedocument");
        Assert.assertEquals(procDef.getName(), "humanTaskSample");
        Assert.assertEquals(procDef.getKnowledgeType(), "PROCESS");
        Assert.assertEquals(procDef.getPackageName(), "defaultPackage");
        Assert.assertEquals(procDef.getType(), "RuleFlow");
        Assert.assertEquals(procDef.getVersion(), "3");
        Assert.assertNotNull(((ProcessAssetDesc) (procDef)).getEncodedProcessSource());
        Collection<UserTaskDefinition> processTasks = bpmn2Service.getTasksDefinitions(deploymentUnit.getIdentifier(), processId);
        Assert.assertEquals(3, processTasks.size());
        Map<String, String> processData = bpmn2Service.getProcessVariables(deploymentUnit.getIdentifier(), processId);
        Assert.assertEquals("String", processData.get("approval_document"));
        Assert.assertEquals("String", processData.get("approval_translatedDocument"));
        Assert.assertEquals("String", processData.get("approval_reviewComment"));
        Assert.assertEquals(3, processData.keySet().size());
        Collection<UserTaskDefinition> userTasks = bpmn2Service.getTasksDefinitions(deploymentUnit.getIdentifier(), processId);
        Assert.assertNotNull(userTasks);
        Assert.assertEquals(3, userTasks.size());
        Map<String, UserTaskDefinition> tasksByName = new HashMap<String, UserTaskDefinition>();
        for (UserTaskDefinition userTask : userTasks) {
            tasksByName.put(userTask.getName(), userTask);
        }
        Assert.assertTrue(tasksByName.containsKey("Write a Document"));
        Assert.assertTrue(tasksByName.containsKey("Translate Document"));
        Assert.assertTrue(tasksByName.containsKey("Review Document"));
        UserTaskDefinition task = tasksByName.get("Write a Document");
        Assert.assertEquals(true, task.isSkippable());
        Assert.assertEquals("Write a Document", task.getName());
        Assert.assertEquals(9, task.getPriority().intValue());
        Assert.assertEquals("Write a Document", task.getComment());
        task = tasksByName.get("Translate Document");
        Assert.assertEquals(true, task.isSkippable());
        Assert.assertEquals("Translate Document", task.getName());
        Assert.assertEquals(0, task.getPriority().intValue());
        Assert.assertEquals("", task.getComment());
        task = tasksByName.get("Review Document");
        Assert.assertEquals(false, task.isSkippable());
        Assert.assertEquals("Review Document", task.getName());
        Assert.assertEquals(0, task.getPriority().intValue());
        Assert.assertEquals("", task.getComment());
        Map<String, String> taskInputMappings = bpmn2Service.getTaskInputMappings(deploymentUnit.getIdentifier(), processId, "Write a Document");
        Assert.assertEquals(4, taskInputMappings.keySet().size());
        Map<String, String> taskOutputMappings = bpmn2Service.getTaskOutputMappings(deploymentUnit.getIdentifier(), processId, "Write a Document");
        Assert.assertEquals(1, taskOutputMappings.keySet().size());
        Map<String, Collection<String>> associatedEntities = bpmn2Service.getAssociatedEntities(deploymentUnit.getIdentifier(), processId);
        Assert.assertEquals(3, associatedEntities.keySet().size());
    }

    @Test
    public void testHiringProcessData() throws IOException {
        Assert.assertNotNull(deploymentService);
        DeploymentUnit deploymentUnit = new org.jbpm.kie.services.impl.KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        String processId = "hiring";
        Collection<UserTaskDefinition> processTasks = bpmn2Service.getTasksDefinitions(deploymentUnit.getIdentifier(), processId);
        Assert.assertEquals(4, processTasks.size());
        Map<String, String> processData = bpmn2Service.getProcessVariables(deploymentUnit.getIdentifier(), processId);
        Assert.assertEquals(9, processData.keySet().size());
        Map<String, String> taskInputMappings = bpmn2Service.getTaskInputMappings(deploymentUnit.getIdentifier(), processId, "HR Interview");
        Assert.assertEquals(4, taskInputMappings.keySet().size());
        Assert.assertEquals("java.lang.String", taskInputMappings.get("TaskName"));
        Assert.assertEquals("Object", taskInputMappings.get("GroupId"));
        Assert.assertEquals("Object", taskInputMappings.get("Comment"));
        Assert.assertEquals("String", taskInputMappings.get("in_name"));
        Map<String, String> taskOutputMappings = bpmn2Service.getTaskOutputMappings(deploymentUnit.getIdentifier(), processId, "HR Interview");
        Assert.assertEquals(4, taskOutputMappings.keySet().size());
        Assert.assertEquals("String", taskOutputMappings.get("out_name"));
        Assert.assertEquals("Integer", taskOutputMappings.get("out_age"));
        Assert.assertEquals("String", taskOutputMappings.get("out_mail"));
        Assert.assertEquals("Integer", taskOutputMappings.get("out_score"));
        Map<String, Collection<String>> associatedEntities = bpmn2Service.getAssociatedEntities(deploymentUnit.getIdentifier(), processId);
        Assert.assertEquals(4, associatedEntities.keySet().size());
        Map<String, String> allServiceTasks = bpmn2Service.getServiceTasks(deploymentUnit.getIdentifier(), processId);
        Assert.assertEquals(2, allServiceTasks.keySet().size());
    }

    @Test
    public void testFindReusableSubProcesses() {
        Assert.assertNotNull(deploymentService);
        DeploymentUnit deploymentUnit = new org.jbpm.kie.services.impl.KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        String theString = "ParentProcess";
        Assert.assertNotNull(theString);
        Collection<String> reusableProcesses = bpmn2Service.getReusableSubProcesses(deploymentUnit.getIdentifier(), theString);
        Assert.assertNotNull(reusableProcesses);
        Assert.assertEquals(1, reusableProcesses.size());
        Assert.assertEquals("signal", reusableProcesses.iterator().next());
    }

    @Test
    public void itemRefIssue() {
        Assert.assertNotNull(deploymentService);
        DeploymentUnit deploymentUnit = new org.jbpm.kie.services.impl.KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        String processId = "itemrefissue";
        Map<String, String> processData = bpmn2Service.getProcessVariables(deploymentUnit.getIdentifier(), processId);
        Assert.assertNotNull(processData);
    }

    @Test
    public void testHumanTaskProcessNoIO() throws IOException {
        Assert.assertNotNull(deploymentService);
        DeploymentUnit deploymentUnit = new org.jbpm.kie.services.impl.KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        String processId = "UserTask";
        Collection<UserTaskDefinition> processTasks = bpmn2Service.getTasksDefinitions(deploymentUnit.getIdentifier(), processId);
        Assert.assertEquals(1, processTasks.size());
        Map<String, String> processData = bpmn2Service.getProcessVariables(deploymentUnit.getIdentifier(), processId);
        Assert.assertEquals(0, processData.keySet().size());
        Map<String, String> taskInputMappings = bpmn2Service.getTaskInputMappings(deploymentUnit.getIdentifier(), processId, "Hello");
        Assert.assertEquals(0, taskInputMappings.keySet().size());
        Map<String, String> taskOutputMappings = bpmn2Service.getTaskOutputMappings(deploymentUnit.getIdentifier(), processId, "Hello");
        Assert.assertEquals(0, taskOutputMappings.keySet().size());
        Map<String, Collection<String>> associatedEntities = bpmn2Service.getAssociatedEntities(deploymentUnit.getIdentifier(), processId);
        Assert.assertEquals(1, associatedEntities.keySet().size());
    }

    @Test
    public void testHumanTaskProcessBeforeAndAfterUndeploy() throws IOException {
        Assert.assertNotNull(deploymentService);
        DeploymentUnit deploymentUnit = new org.jbpm.kie.services.impl.KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        String processId = "org.jbpm.writedocument";
        ProcessDefinition procDef = bpmn2Service.getProcessDefinition(deploymentUnit.getIdentifier(), processId);
        Assert.assertNotNull(procDef);
        Assert.assertEquals(procDef.getId(), "org.jbpm.writedocument");
        Assert.assertEquals(procDef.getName(), "humanTaskSample");
        Assert.assertEquals(procDef.getKnowledgeType(), "PROCESS");
        Assert.assertEquals(procDef.getPackageName(), "defaultPackage");
        Assert.assertEquals(procDef.getType(), "RuleFlow");
        Assert.assertEquals(procDef.getVersion(), "3");
        // now let's undeploy the unit
        deploymentService.undeploy(deploymentUnit);
        procDef = bpmn2Service.getProcessDefinition(deploymentUnit.getIdentifier(), processId);
        Assert.assertNull(procDef);
    }

    @Test
    public void testObjectVariable() throws IOException {
        Assert.assertNotNull(deploymentService);
        DeploymentUnit deploymentUnit = new org.jbpm.kie.services.impl.KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        String processId = "ObjectVariableProcess";
        ProcessDefinition procDef = bpmn2Service.getProcessDefinition(deploymentUnit.getIdentifier(), processId);
        Assert.assertNotNull(procDef);
        Assert.assertEquals(procDef.getId(), "ObjectVariableProcess");
        Assert.assertEquals(procDef.getName(), "ObjectVariableProcess");
        Assert.assertEquals(procDef.getKnowledgeType(), "PROCESS");
        Assert.assertEquals(procDef.getPackageName(), "defaultPackage");
        Assert.assertEquals(procDef.getType(), "RuleFlow");
        Assert.assertEquals(procDef.getVersion(), "1");
        Map<String, String> processData = bpmn2Service.getProcessVariables(deploymentUnit.getIdentifier(), processId);
        Assert.assertEquals("String", processData.get("type"));
        Assert.assertEquals("Object", processData.get("myobject"));
        Assert.assertEquals(2, processData.keySet().size());
    }
}

