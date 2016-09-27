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
import org.kie.api.task.model.Attachment;
import org.junit.Before;
import org.kie.api.task.model.Comment;
import java.util.Date;
import org.jbpm.services.api.model.DeploymentUnit;
import java.io.File;
import java.io.FileOutputStream;
import org.kie.api.task.model.Group;
import java.util.HashMap;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.kie.internal.task.api.model.InternalTask;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.kie.api.KieServices;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.kie.scanner.MavenRepository;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.runtime.process.ProcessInstance;
import org.jbpm.services.api.ProcessInstanceNotFoundException;
import org.kie.api.builder.ReleaseId;
import java.text.SimpleDateFormat;
import org.drools.core.util.StringUtils;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.TaskModelProvider;
import org.junit.Test;
import org.kie.api.task.model.User;
import org.jbpm.services.api.model.UserTaskInstanceDesc;

public class UserTaskServiceImplTest extends AbstractKieServicesBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(KModuleDeploymentServiceTest.class);

    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();

    private Long processInstanceId = null;

    private KModuleDeploymentUnit deploymentUnit = null;

    @Before
    public void prepare() {
        configureServices();
        UserTaskServiceImplTest.logger.debug("Preparing kjar");
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/general/EmptyHumanTask.bpmn");
        processes.add("repo/processes/general/humanTask.bpmn");
        processes.add("repo/processes/general/NoFormNameHumanTask.bpmn");
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
        Assert.assertNotNull(deploymentService);
        deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        Assert.assertNotNull(processService);
    }

    @After
    public void cleanup() {
        if ((processInstanceId) != null) {
            try {
                // let's abort process instance to leave the system in clear state
                processService.abortProcessInstance(processInstanceId);
                ProcessInstance pi = processService.getProcessInstance(processInstanceId);
                Assert.assertNull(pi);
            } catch (ProcessInstanceNotFoundException e) {
                // ignore it as it might already be completed/aborted
            }
        } 
        cleanupSingletonSessionId();
        if (((units) != null) && (!(units.isEmpty()))) {
            for (DeploymentUnit unit : units) {
                try {
                    deploymentService.undeploy(unit);
                } catch (Exception e) {
                    // do nothing in case of some failed tests to avoid next test to fail as well
                }
            }
            units.clear();
        } 
        close();
    }

    @Test
    public void testActivate() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument.empty");
        Assert.assertNotNull(processInstanceId);
        List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
        Assert.assertNotNull(taskIds);
        Assert.assertEquals(1, taskIds.size());
        Long taskId = taskIds.get(0);
        userTaskService.activate(taskId, "Administrator");
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        Assert.assertNotNull(task);
        Assert.assertEquals(Status.Ready.toString(), task.getStatus());
    }

    @Test
    public void testReleaseAndClaim() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
        Assert.assertNotNull(taskIds);
        Assert.assertEquals(1, taskIds.size());
        Long taskId = taskIds.get(0);
        userTaskService.release(taskId, "salaboy");
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        Assert.assertNotNull(task);
        Assert.assertEquals(Status.Ready.toString(), task.getStatus());
        userTaskService.claim(taskId, "salaboy");
        task = runtimeDataService.getTaskById(taskId);
        Assert.assertNotNull(task);
        Assert.assertEquals(Status.Reserved.toString(), task.getStatus());
    }

    @Test
    public void testStartAndComplete() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
        Assert.assertNotNull(taskIds);
        Assert.assertEquals(1, taskIds.size());
        Long taskId = taskIds.get(0);
        userTaskService.start(taskId, "salaboy");
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        Assert.assertNotNull(task);
        Assert.assertEquals(Status.InProgress.toString(), task.getStatus());
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("Result", "some document data");
        userTaskService.complete(taskId, "salaboy", results);
        task = runtimeDataService.getTaskById(taskId);
        Assert.assertNotNull(task);
        Assert.assertEquals(Status.Completed.toString(), task.getStatus());
    }

    @Test
    public void testCompleteAutoProgress() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
        Assert.assertNotNull(taskIds);
        Assert.assertEquals(1, taskIds.size());
        Long taskId = taskIds.get(0);
        userTaskService.release(taskId, "salaboy");
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("Result", "some document data");
        // claim, start, and complete the task
        userTaskService.completeAutoProgress(taskId, "salaboy", results);
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        Assert.assertNotNull(task);
        Assert.assertEquals(Status.Completed.toString(), task.getStatus());
    }

    @Test
    public void testDelegate() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
        Assert.assertNotNull(taskIds);
        Assert.assertEquals(1, taskIds.size());
        Long taskId = taskIds.get(0);
        userTaskService.delegate(taskId, "Administrator", "john");
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        Assert.assertNotNull(task);
        Assert.assertEquals(Status.Reserved.toString(), task.getStatus());
        Assert.assertEquals("john", task.getActualOwner());
    }

    @Test
    public void testExit() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
        Assert.assertNotNull(taskIds);
        Assert.assertEquals(1, taskIds.size());
        Long taskId = taskIds.get(0);
        userTaskService.exit(taskId, "Administrator");
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        Assert.assertNotNull(task);
        Assert.assertEquals(Status.Exited.toString(), task.getStatus());
    }

    @Test
    public void testStartAndFail() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
        Assert.assertNotNull(taskIds);
        Assert.assertEquals(1, taskIds.size());
        Long taskId = taskIds.get(0);
        userTaskService.start(taskId, "salaboy");
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        Assert.assertNotNull(task);
        Assert.assertEquals(Status.InProgress.toString(), task.getStatus());
        userTaskService.fail(taskId, "Administrator", new HashMap<String, Object>());
        task = runtimeDataService.getTaskById(taskId);
        Assert.assertNotNull(task);
        Assert.assertEquals(Status.Failed.toString(), task.getStatus());
    }

    @Test
    public void testStartAndForward() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
        Assert.assertNotNull(taskIds);
        Assert.assertEquals(1, taskIds.size());
        Long taskId = taskIds.get(0);
        userTaskService.start(taskId, "salaboy");
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        Assert.assertNotNull(task);
        Assert.assertEquals(Status.InProgress.toString(), task.getStatus());
        userTaskService.forward(taskId, "salaboy", "john");
        task = runtimeDataService.getTaskById(taskId);
        Assert.assertNotNull(task);
        Assert.assertEquals(Status.Ready.toString(), task.getStatus());
        Assert.assertEquals("", task.getActualOwner());
    }

    @Test
    public void testSuspendAndResume() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
        Assert.assertNotNull(taskIds);
        Assert.assertEquals(1, taskIds.size());
        Long taskId = taskIds.get(0);
        userTaskService.suspend(taskId, "salaboy");
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        Assert.assertNotNull(task);
        Assert.assertEquals(Status.Suspended.toString(), task.getStatus());
        userTaskService.resume(taskId, "salaboy");
        task = runtimeDataService.getTaskById(taskId);
        Assert.assertNotNull(task);
        Assert.assertEquals(Status.Reserved.toString(), task.getStatus());
    }

    @Test
    public void testStartAndStop() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
        Assert.assertNotNull(taskIds);
        Assert.assertEquals(1, taskIds.size());
        Long taskId = taskIds.get(0);
        userTaskService.start(taskId, "salaboy");
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        Assert.assertNotNull(task);
        Assert.assertEquals(Status.InProgress.toString(), task.getStatus());
        userTaskService.stop(taskId, "salaboy");
        task = runtimeDataService.getTaskById(taskId);
        Assert.assertNotNull(task);
        Assert.assertEquals(Status.Reserved.toString(), task.getStatus());
    }

    @Test
    public void testSkip() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
        Assert.assertNotNull(taskIds);
        Assert.assertEquals(1, taskIds.size());
        Long taskId = taskIds.get(0);
        userTaskService.skip(taskId, "Administrator");
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        Assert.assertNotNull(task);
        Assert.assertEquals(Status.Obsolete.toString(), task.getStatus());
    }

    @Test
    public void testNominate() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument.empty");
        Assert.assertNotNull(processInstanceId);
        List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
        Assert.assertNotNull(taskIds);
        Assert.assertEquals(1, taskIds.size());
        Long taskId = taskIds.get(0);
        List<OrganizationalEntity> owners = new ArrayList<OrganizationalEntity>();
        User user = TaskModelProvider.getFactory().newUser("john");
        owners.add(user);
        user = TaskModelProvider.getFactory().newUser("salaboy");
        owners.add(user);
        Group group = TaskModelProvider.getFactory().newGroup("HR");
        owners.add(group);
        userTaskService.nominate(taskId, "Administrator", owners);
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        Assert.assertNotNull(task);
        Assert.assertEquals(Status.Ready.toString(), task.getStatus());
    }

    @Test
    public void testSetPriority() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
        Assert.assertNotNull(taskIds);
        Assert.assertEquals(1, taskIds.size());
        Long taskId = taskIds.get(0);
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        Assert.assertNotNull(task);
        Assert.assertEquals(Status.Reserved.toString(), task.getStatus());
        Assert.assertEquals(9, ((int) (task.getPriority())));
        userTaskService.setPriority(taskId, 8);
        task = runtimeDataService.getTaskById(taskId);
        Assert.assertNotNull(task);
        Assert.assertEquals(Status.Reserved.toString(), task.getStatus());
        Assert.assertEquals(8, ((int) (task.getPriority())));
    }

    @Test
    public void testSetExpirationDate() throws Exception {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
        Assert.assertNotNull(taskIds);
        Assert.assertEquals(1, taskIds.size());
        Long taskId = taskIds.get(0);
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        Assert.assertNotNull(task);
        Assert.assertEquals(Status.Reserved.toString(), task.getStatus());
        Date origDueDate = task.getDueDate();
        Assert.assertNull(origDueDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        userTaskService.setExpirationDate(taskId, sdf.parse("2013-12-31"));
        task = runtimeDataService.getTaskById(taskId);
        Assert.assertNotNull(task);
        Assert.assertEquals(Status.Reserved.toString(), task.getStatus());
        Assert.assertEquals("2013-12-31", sdf.format(task.getDueDate()));
    }

    @Test
    public void testSetSkippable() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
        Assert.assertNotNull(taskIds);
        Assert.assertEquals(1, taskIds.size());
        Long taskId = taskIds.get(0);
        Task taskInstance = userTaskService.getTask(taskId);
        Assert.assertNotNull(taskInstance);
        Assert.assertEquals(Status.Reserved, taskInstance.getTaskData().getStatus());
        Assert.assertTrue(taskInstance.getTaskData().isSkipable());
        userTaskService.setSkipable(taskId, false);
        taskInstance = userTaskService.getTask(taskId);
        Assert.assertNotNull(taskInstance);
        Assert.assertEquals(Status.Reserved, taskInstance.getTaskData().getStatus());
        Assert.assertFalse(taskInstance.getTaskData().isSkipable());
    }

    @Test
    public void testSetName() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
        Assert.assertNotNull(taskIds);
        Assert.assertEquals(1, taskIds.size());
        Long taskId = taskIds.get(0);
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        Assert.assertNotNull(task);
        Assert.assertEquals(Status.Reserved.toString(), task.getStatus());
        Assert.assertEquals("Write a Document", task.getName());
        userTaskService.setName(taskId, "updated");
        task = runtimeDataService.getTaskById(taskId);
        Assert.assertNotNull(task);
        Assert.assertEquals(Status.Reserved.toString(), task.getStatus());
        Assert.assertEquals("updated", task.getName());
    }

    @Test
    public void testSetDescription() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
        Assert.assertNotNull(taskIds);
        Assert.assertEquals(1, taskIds.size());
        Long taskId = taskIds.get(0);
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        Assert.assertNotNull(task);
        Assert.assertEquals(Status.Reserved.toString(), task.getStatus());
        Assert.assertEquals("Write a Document", task.getDescription());
        userTaskService.setDescription(taskId, "updated");
        task = runtimeDataService.getTaskById(taskId);
        Assert.assertNotNull(task);
        Assert.assertEquals(Status.Reserved.toString(), task.getStatus());
        Assert.assertEquals("updated", task.getDescription());
    }

    @Test
    public void testContentRelatedOperations() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
        Assert.assertNotNull(taskIds);
        Assert.assertEquals(1, taskIds.size());
        Long taskId = taskIds.get(0);
        Map<String, Object> input = userTaskService.getTaskInputContentByTaskId(taskId);
        Assert.assertNotNull(input);
        Assert.assertEquals(6, input.size());
        Assert.assertTrue(input.containsKey("ActorId"));
        Assert.assertTrue(input.containsKey("Comment"));
        Assert.assertTrue(input.containsKey("TaskName"));
        Assert.assertTrue(input.containsKey("NodeName"));
        Assert.assertTrue(input.containsKey("Priority"));
        Assert.assertTrue(input.containsKey("Skippable"));
        // now let's add some output data
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("Content", "testing save");
        values.put("Author", "john");
        Long contentId = userTaskService.saveContent(taskId, values);
        Assert.assertNotNull(contentId);
        // let's now validate it
        Map<String, Object> output = userTaskService.getTaskOutputContentByTaskId(taskId);
        Assert.assertNotNull(output);
        Assert.assertEquals(2, output.size());
        Assert.assertTrue(output.containsKey("Content"));
        Assert.assertTrue(output.containsKey("Author"));
        // now we delete it
        userTaskService.deleteContent(taskId, contentId);
        // and confirm it was deleted
        output = userTaskService.getTaskOutputContentByTaskId(taskId);
        Assert.assertNotNull(output);
        Assert.assertEquals(0, output.size());
    }

    @Test
    public void testCommentOperations() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
        Assert.assertNotNull(taskIds);
        Assert.assertEquals(1, taskIds.size());
        Long taskId = taskIds.get(0);
        List<Comment> comments = userTaskService.getCommentsByTaskId(taskId);
        Assert.assertNotNull(comments);
        Assert.assertEquals(0, comments.size());
        Long commentId = userTaskService.addComment(taskId, "Simple comment", "john", new Date());
        Assert.assertNotNull(commentId);
        Long commentId2 = userTaskService.addComment(taskId, "Another comment", "john", new Date());
        Assert.assertNotNull(commentId2);
        comments = userTaskService.getCommentsByTaskId(taskId);
        Assert.assertNotNull(comments);
        Assert.assertEquals(2, comments.size());
        Comment cm = userTaskService.getCommentById(taskId, commentId2);
        Assert.assertNotNull(cm);
        Assert.assertEquals("john", cm.getAddedBy().getId());
        Assert.assertEquals("Another comment", cm.getText());
        userTaskService.deleteComment(taskId, commentId2);
        comments = userTaskService.getCommentsByTaskId(taskId);
        Assert.assertNotNull(comments);
        Assert.assertEquals(1, comments.size());
    }

    @Test
    public void testAttachmentOperations() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
        Assert.assertNotNull(taskIds);
        Assert.assertEquals(1, taskIds.size());
        Long taskId = taskIds.get(0);
        List<Attachment> attachments = userTaskService.getAttachmentsByTaskId(taskId);
        Assert.assertNotNull(attachments);
        Assert.assertEquals(0, attachments.size());
        Long attId = userTaskService.addAttachment(taskId, "john", "my attachment", "String attachment");
        Assert.assertNotNull(attId);
        attachments = userTaskService.getAttachmentsByTaskId(taskId);
        Assert.assertNotNull(attachments);
        Assert.assertEquals(1, attachments.size());
        String content = ((String) (userTaskService.getAttachmentContentById(taskId, attId)));
        Assert.assertNotNull(content);
        Assert.assertEquals("String attachment", content);
        Attachment attachment = userTaskService.getAttachmentById(taskId, attId);
        Assert.assertNotNull(attachment);
        Assert.assertEquals("john", attachment.getAttachedBy().getId());
        Assert.assertEquals("my attachment", attachment.getName());
        Assert.assertNotNull(attachment.getAttachmentContentId());
        Assert.assertEquals("java.lang.String", attachment.getContentType());
        userTaskService.deleteAttachment(taskId, attId);
        attachments = userTaskService.getAttachmentsByTaskId(taskId);
        Assert.assertNotNull(attachments);
        Assert.assertEquals(0, attachments.size());
    }

    @Test
    public void testExecute() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
        Assert.assertNotNull(taskIds);
        Assert.assertEquals(1, taskIds.size());
        Long taskId = taskIds.get(0);
        Task task = userTaskService.execute(deploymentUnit.getIdentifier(), new org.jbpm.services.task.commands.GetTaskCommand(taskId));
        Assert.assertNotNull(task);
        Assert.assertEquals(taskId, task.getId());
        Assert.assertEquals("Write a Document", task.getName());
    }

    @Test
    public void testGetTask() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument.noform");
        Assert.assertNotNull(processInstanceId);
        List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
        Assert.assertNotNull(taskIds);
        Assert.assertEquals(1, taskIds.size());
        Long taskId = taskIds.get(0);
        Task taskInstance = userTaskService.getTask(taskId);
        Assert.assertNotNull(taskInstance);
        Assert.assertEquals(Status.Reserved, taskInstance.getTaskData().getStatus());
        Assert.assertEquals("Write a Document", taskInstance.getName());
        Assert.assertTrue(StringUtils.isEmpty(((InternalTask) (taskInstance)).getFormName()));
    }
}

