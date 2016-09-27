/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.services.task.wih;

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Assert;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import java.util.Date;
import org.jbpm.process.core.timer.DateTimeUtils;
import org.jbpm.services.task.events.DefaultTaskEventListener;
import org.kie.internal.task.api.EventService;
import java.util.HashMap;
import org.kie.internal.task.api.model.InternalTaskData;
import java.util.List;
import java.util.Map;
import org.jbpm.services.task.test.MyObject;
import org.jbpm.services.task.exception.PermissionDeniedException;
import org.kie.api.task.model.Task;
import org.kie.api.task.TaskEvent;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.junit.Test;
import org.jbpm.services.task.test.TestStatefulKnowledgeSession;
import org.kie.api.runtime.process.WorkItemHandler;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.kie.api.runtime.process.WorkItemManager;

public abstract class HTWorkItemHandlerBaseTest extends AbstractBaseTest {
    private static final int DEFAULT_WAIT_TIME = 5000;

    private static final int MANAGER_COMPLETION_WAIT_TIME = HTWorkItemHandlerBaseTest.DEFAULT_WAIT_TIME;

    private static final int MANAGER_ABORT_WAIT_TIME = HTWorkItemHandlerBaseTest.DEFAULT_WAIT_TIME;

    private WorkItemHandler handler;

    protected TestStatefulKnowledgeSession ksession;

    protected TaskService taskService;

    @Test
    public void testTask() throws Exception {
        HTWorkItemHandlerBaseTest.TestWorkItemManager manager = new HTWorkItemHandlerBaseTest.TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("NodeName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setProcessInstanceId(10);
        handler.executeWorkItem(workItem, manager);
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        Assert.assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        Assert.assertEquals("TaskName", task.getName());
        Assert.assertEquals(10, task.getPriority().intValue());
        Assert.assertEquals("Comment", task.getDescription());
        Assert.assertEquals(Status.Reserved, task.getStatus());
        Assert.assertEquals("Darth Vader", task.getActualOwner().getId());
        Assert.assertEquals(10, task.getProcessInstanceId().intValue());
        taskService.start(task.getId(), "Darth Vader");
        taskService.complete(task.getId(), "Darth Vader", null);
        Assert.assertTrue(manager.waitTillCompleted(HTWorkItemHandlerBaseTest.MANAGER_COMPLETION_WAIT_TIME));
        String actualOwner = ((String) (manager.getResults().get("ActorId")));
        Assert.assertNotNull(actualOwner);
        Assert.assertEquals("Darth Vader", actualOwner);
    }

    @Test
    public void testTaskMultipleActors() throws Exception {
        HTWorkItemHandlerBaseTest.TestWorkItemManager manager = new HTWorkItemHandlerBaseTest.TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("NodeName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader, Dalai Lama");
        workItem.setProcessInstanceId(10);
        getHandler().executeWorkItem(workItem, manager);
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        Assert.assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        Assert.assertEquals("TaskName", task.getName());
        Assert.assertEquals(10, task.getPriority().intValue());
        Assert.assertEquals("Comment", task.getDescription());
        Assert.assertEquals(Status.Ready, task.getStatus());
        taskService.claim(task.getId(), "Darth Vader");
        taskService.start(task.getId(), "Darth Vader");
        taskService.complete(task.getId(), "Darth Vader", null);
        Assert.assertTrue(manager.waitTillCompleted(HTWorkItemHandlerBaseTest.MANAGER_COMPLETION_WAIT_TIME));
    }

    @Test
    public void testTaskGroupActors() throws Exception {
        HTWorkItemHandlerBaseTest.TestWorkItemManager manager = new HTWorkItemHandlerBaseTest.TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("NodeName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("GroupId", "Crusaders");
        workItem.setProcessInstanceId(10);
        getHandler().executeWorkItem(workItem, manager);
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Luke Cage", "en-UK");
        Assert.assertEquals(1, tasks.size());
        TaskSummary taskSummary = tasks.get(0);
        Assert.assertEquals("TaskName", taskSummary.getName());
        Assert.assertEquals(10, taskSummary.getPriority().intValue());
        Assert.assertEquals("Comment", taskSummary.getDescription());
        Assert.assertEquals(Status.Ready, taskSummary.getStatus());
        PermissionDeniedException denied = null;
        try {
            taskService.claim(taskSummary.getId(), "nocrusadaer");
        } catch (PermissionDeniedException e) {
            denied = e;
        }
        Assert.assertNotNull("Should get permissed denied exception", denied);
        // Check if the parent task is InProgress
        Task task = taskService.getTaskById(taskSummary.getId());
        Assert.assertEquals(Status.Ready, task.getTaskData().getStatus());
    }

    @Test
    public void testTaskSingleAndGroupActors() throws Exception {
        // Properties userGroups = new Properties();
        // userGroups.setProperty("Darth Vader", "Crusaders");
        // 
        // UserGroupCallbackManager.getInstance().setCallback(new DefaultUserGroupCallbackImpl(userGroups));
        HTWorkItemHandlerBaseTest.TestWorkItemManager manager = new HTWorkItemHandlerBaseTest.TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task One");
        workItem.setParameter("NodeName", "TaskNameOne");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("GroupId", "Crusaders");
        workItem.setProcessInstanceId(10);
        getHandler().executeWorkItem(workItem, manager);
        workItem = new WorkItemImpl();
        workItem.setName("Human Task Two");
        workItem.setParameter("NodeName", "TaskNameTwo");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        getHandler().executeWorkItem(workItem, manager);
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        Assert.assertEquals(2, tasks.size());
    }

    @Test
    public void testTaskFail() throws Exception {
        HTWorkItemHandlerBaseTest.TestWorkItemManager manager = new HTWorkItemHandlerBaseTest.TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("NodeName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setProcessInstanceId(10);
        getHandler().executeWorkItem(workItem, manager);
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        Assert.assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        Assert.assertEquals("TaskName", task.getName());
        Assert.assertEquals(10, task.getPriority().intValue());
        Assert.assertEquals("Comment", task.getDescription());
        Assert.assertEquals(Status.Reserved, task.getStatus());
        Assert.assertEquals("Darth Vader", task.getActualOwner().getId());
        taskService.start(task.getId(), "Darth Vader");
        taskService.fail(task.getId(), "Darth Vader", null);
        Assert.assertTrue(manager.waitTillAborted(HTWorkItemHandlerBaseTest.MANAGER_ABORT_WAIT_TIME));
    }

    @Test
    public void testTaskSkip() throws Exception {
        HTWorkItemHandlerBaseTest.TestWorkItemManager manager = new HTWorkItemHandlerBaseTest.TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("NodeName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setProcessInstanceId(10);
        getHandler().executeWorkItem(workItem, manager);
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        Assert.assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        Assert.assertEquals("TaskName", task.getName());
        Assert.assertEquals(10, task.getPriority().intValue());
        Assert.assertEquals("Comment", task.getDescription());
        Assert.assertEquals(Status.Reserved, task.getStatus());
        Assert.assertEquals("Darth Vader", task.getActualOwner().getId());
        taskService.skip(task.getId(), "Darth Vader");
        Assert.assertTrue(manager.waitTillAborted(HTWorkItemHandlerBaseTest.MANAGER_ABORT_WAIT_TIME));
    }

    @Test
    public void testTaskExit() throws Exception {
        HTWorkItemHandlerBaseTest.TestWorkItemManager manager = new HTWorkItemHandlerBaseTest.TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("NodeName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setProcessInstanceId(10);
        getHandler().executeWorkItem(workItem, manager);
        Task task = taskService.getTaskByWorkItemId(workItem.getId());
        taskService.exit(task.getId(), "Administrator");
        task = taskService.getTaskByWorkItemId(workItem.getId());
        Assert.assertEquals("TaskName", task.getNames().get(0).getText());
        Assert.assertEquals(10, task.getPriority());
        Assert.assertEquals("Comment", task.getDescriptions().get(0).getText());
        Assert.assertEquals(Status.Exited, task.getTaskData().getStatus());
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        Assert.assertEquals(0, tasks.size());
    }

    @Test
    public void testTaskExitNonAdministrator() throws Exception {
        HTWorkItemHandlerBaseTest.TestWorkItemManager manager = new HTWorkItemHandlerBaseTest.TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("NodeName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setProcessInstanceId(10);
        getHandler().executeWorkItem(workItem, manager);
        Task task = taskService.getTaskByWorkItemId(workItem.getId());
        try {
            taskService.exit(task.getId(), "Darth Vader");
            Assert.fail("Should not allow to exit task for non administrators");
        } catch (PermissionDeniedException e) {
        }
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        Assert.assertEquals(1, tasks.size());
        TaskSummary taskSummary = tasks.get(0);
        Assert.assertEquals("TaskName", taskSummary.getName());
        Assert.assertEquals(10, taskSummary.getPriority().intValue());
        Assert.assertEquals("Comment", taskSummary.getDescription());
        Assert.assertEquals(Status.Reserved, taskSummary.getStatus());
        Assert.assertEquals("Darth Vader", taskSummary.getActualOwner().getId());
    }

    @Test
    public void testTaskAbortSkippable() throws Exception {
        HTWorkItemHandlerBaseTest.TestWorkItemManager manager = new HTWorkItemHandlerBaseTest.TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("NodeName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setProcessInstanceId(10);
        getHandler().executeWorkItem(workItem, manager);
        getHandler().abortWorkItem(workItem, manager);
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        Assert.assertEquals(0, tasks.size());
    }

    @Test
    public void testTaskAbortNotSkippable() throws Exception {
        HTWorkItemHandlerBaseTest.TestWorkItemManager manager = new HTWorkItemHandlerBaseTest.TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("NodeName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setParameter("Skippable", "false");
        workItem.setProcessInstanceId(10);
        getHandler().executeWorkItem(workItem, manager);
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        Assert.assertEquals(1, tasks.size());
        getHandler().abortWorkItem(workItem, manager);
        // aborting work item will exit task and not skip it
        tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        Assert.assertEquals(0, tasks.size());
    }

    @Test
    public void testTaskData() throws Exception {
        HTWorkItemHandlerBaseTest.TestWorkItemManager manager = new HTWorkItemHandlerBaseTest.TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("NodeName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setParameter("Content", "This is the content");
        workItem.setProcessInstanceId(10);
        getHandler().executeWorkItem(workItem, manager);
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        Assert.assertEquals(1, tasks.size());
        TaskSummary taskSummary = tasks.get(0);
        Assert.assertEquals("TaskName", taskSummary.getName());
        Assert.assertEquals(10, taskSummary.getPriority().intValue());
        Assert.assertEquals("Comment", taskSummary.getDescription());
        Assert.assertEquals(Status.Reserved, taskSummary.getStatus());
        Assert.assertEquals("Darth Vader", taskSummary.getActualOwner().getId());
        Task task = taskService.getTaskById(taskSummary.getId());
        Assert.assertEquals(AccessType.Inline, ((InternalTaskData) (task.getTaskData())).getDocumentAccessType());
        Assert.assertEquals(task.getTaskData().getProcessSessionId(), TestStatefulKnowledgeSession.testSessionId.intValue());
        long contentId = task.getTaskData().getDocumentContentId();
        Assert.assertTrue((contentId != (-1)));
        Object data = ContentMarshallerHelper.unmarshall(taskService.getContentById(contentId).getContent(), ksession.getEnvironment());
        Assert.assertEquals("This is the content", data);
        taskService.start(task.getId(), "Darth Vader");
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("Result", "This is the result");
        // ContentData result = ContentMarshallerHelper.marshal(,
        // ksession.getEnvironment());
        taskService.complete(task.getId(), "Darth Vader", results);
        Assert.assertTrue(manager.waitTillCompleted(HTWorkItemHandlerBaseTest.MANAGER_COMPLETION_WAIT_TIME));
        results = manager.getResults();
        Assert.assertNotNull(results);
        Assert.assertEquals("Darth Vader", results.get("ActorId"));
        Assert.assertEquals("This is the result", results.get("Result"));
    }

    @Test
    public void testTaskDataAutomaticMapping() throws Exception {
        HTWorkItemHandlerBaseTest.TestWorkItemManager manager = new HTWorkItemHandlerBaseTest.TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("NodeName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        MyObject myObject = new MyObject("MyObjectValue");
        workItem.setParameter("MyObject", myObject);
        Map<String, Object> mapParameter = new HashMap<String, Object>();
        mapParameter.put("MyObjectInsideTheMap", myObject);
        workItem.setParameter("MyMap", mapParameter);
        workItem.setParameter("MyObject", myObject);
        workItem.setProcessInstanceId(10);
        getHandler().executeWorkItem(workItem, manager);
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        Assert.assertEquals(1, tasks.size());
        TaskSummary taskSummary = tasks.get(0);
        Assert.assertEquals("TaskName", taskSummary.getName());
        Assert.assertEquals(10, taskSummary.getPriority().intValue());
        Assert.assertEquals("Comment", taskSummary.getDescription());
        Assert.assertEquals(Status.Reserved, taskSummary.getStatus());
        Assert.assertEquals("Darth Vader", taskSummary.getActualOwner().getId());
        Task task = taskService.getTaskById(taskSummary.getId());
        Assert.assertEquals(AccessType.Inline, ((InternalTaskData) (task.getTaskData())).getDocumentAccessType());
        long contentId = task.getTaskData().getDocumentContentId();
        Assert.assertTrue((contentId != (-1)));
        Map<String, Object> data = ((Map<String, Object>) (ContentMarshallerHelper.unmarshall(taskService.getContentById(contentId).getContent(), ksession.getEnvironment())));
        // Checking that the input parameters are being copied automatically if the Content Element doesn't exist
        Assert.assertEquals("MyObjectValue", ((MyObject) (data.get("MyObject"))).getValue());
        Assert.assertEquals("10", data.get("Priority"));
        Assert.assertEquals("MyObjectValue", ((MyObject) (((Map<String, Object>) (data.get("MyMap"))).get("MyObjectInsideTheMap"))).getValue());
        taskService.start(task.getId(), "Darth Vader");
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("Result", "This is the result");
        taskService.complete(task.getId(), "Darth Vader", results);
        Assert.assertTrue(manager.waitTillCompleted(HTWorkItemHandlerBaseTest.MANAGER_COMPLETION_WAIT_TIME));
        results = manager.getResults();
        Assert.assertNotNull(results);
        Assert.assertEquals("Darth Vader", results.get("ActorId"));
        Assert.assertEquals("This is the result", results.get("Result"));
    }

    @SuppressWarnings(value = "unchecked")
    @Test
    public void testTaskCreateFailedWithLog() throws Exception {
        HTWorkItemHandlerBaseTest.TestWorkItemManager manager = new HTWorkItemHandlerBaseTest.TestWorkItemManager();
        ((AbstractHTWorkItemHandler) (handler)).setAction(OnErrorAction.LOG);
        TaskLifeCycleEventListener listener = new HTWorkItemHandlerBaseTest.AddedTaskListener(true);
        ((EventService<TaskLifeCycleEventListener>) (taskService)).registerTaskEventListener(listener);
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("NodeName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "DoesNotExist");
        workItem.setProcessInstanceId(10);
        handler.executeWorkItem(workItem, manager);
        Assert.assertFalse(manager.isAborted());
        ((EventService<TaskLifeCycleEventListener>) (taskService)).removeTaskEventListener(listener);
    }

    @SuppressWarnings(value = "unchecked")
    @Test
    public void testTaskCreateFailedWithAbort() throws Exception {
        HTWorkItemHandlerBaseTest.TestWorkItemManager manager = new HTWorkItemHandlerBaseTest.TestWorkItemManager();
        TaskLifeCycleEventListener listener = new HTWorkItemHandlerBaseTest.AddedTaskListener(true);
        ((EventService<TaskLifeCycleEventListener>) (taskService)).registerTaskEventListener(listener);
        ((AbstractHTWorkItemHandler) (handler)).setAction(OnErrorAction.ABORT);
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("NodeName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "DoesNotExist");
        workItem.setProcessInstanceId(10);
        handler.executeWorkItem(workItem, manager);
        Assert.assertTrue(manager.isAborted());
        ((EventService<TaskLifeCycleEventListener>) (taskService)).removeTaskEventListener(listener);
    }

    @SuppressWarnings(value = "unchecked")
    @Test
    public void testTaskCreateFailedWithRethrow() throws Exception {
        HTWorkItemHandlerBaseTest.TestWorkItemManager manager = new HTWorkItemHandlerBaseTest.TestWorkItemManager();
        TaskLifeCycleEventListener listener = new HTWorkItemHandlerBaseTest.AddedTaskListener(true);
        ((EventService<TaskLifeCycleEventListener>) (taskService)).registerTaskEventListener(listener);
        ((AbstractHTWorkItemHandler) (handler)).setAction(OnErrorAction.RETHROW);
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("NodeName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "DoesNotExist");
        workItem.setProcessInstanceId(10);
        try {
            handler.executeWorkItem(workItem, manager);
            Assert.fail("Should fail due to OnErroAction set to rethrow");
        } catch (Exception e) {
            // do nothing
        }
        ((EventService<TaskLifeCycleEventListener>) (taskService)).removeTaskEventListener(listener);
    }

    @Test
    public void testTaskWithCreatedBy() throws Exception {
        HTWorkItemHandlerBaseTest.TestWorkItemManager manager = new HTWorkItemHandlerBaseTest.TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("NodeName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setParameter("CreatedBy", "john");
        workItem.setProcessInstanceId(10);
        handler.executeWorkItem(workItem, manager);
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        Assert.assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        Assert.assertEquals("TaskName", task.getName());
        Assert.assertEquals(10, task.getPriority().intValue());
        Assert.assertEquals("Comment", task.getDescription());
        Assert.assertEquals(Status.Reserved, task.getStatus());
        Assert.assertEquals("Darth Vader", task.getActualOwner().getId());
        Assert.assertEquals("john", task.getCreatedBy().getId());
        Assert.assertEquals(10, task.getProcessInstanceId().intValue());
        taskService.start(task.getId(), "Darth Vader");
        taskService.complete(task.getId(), "Darth Vader", null);
        Assert.assertTrue(manager.waitTillCompleted(HTWorkItemHandlerBaseTest.MANAGER_COMPLETION_WAIT_TIME));
    }

    @Test
    public void testTaskWithoutCreatedBy() throws Exception {
        HTWorkItemHandlerBaseTest.TestWorkItemManager manager = new HTWorkItemHandlerBaseTest.TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("NodeName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setProcessInstanceId(10);
        handler.executeWorkItem(workItem, manager);
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        Assert.assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        Assert.assertEquals("TaskName", task.getName());
        Assert.assertEquals(10, task.getPriority().intValue());
        Assert.assertEquals("Comment", task.getDescription());
        Assert.assertEquals(Status.Reserved, task.getStatus());
        Assert.assertEquals("Darth Vader", task.getActualOwner().getId());
        Assert.assertEquals("Darth Vader", task.getCreatedBy().getId());
        Assert.assertEquals(10, task.getProcessInstanceId().intValue());
        taskService.start(task.getId(), "Darth Vader");
        taskService.complete(task.getId(), "Darth Vader", null);
        Assert.assertTrue(manager.waitTillCompleted(HTWorkItemHandlerBaseTest.MANAGER_COMPLETION_WAIT_TIME));
    }

    @Test
    public void testTaskWitAutoClaimTaskWithActorAndGroup() throws Exception {
        HTWorkItemHandlerBaseTest.TestWorkItemManager manager = new HTWorkItemHandlerBaseTest.TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("NodeName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("GroupId", "Crusaders");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setParameter("SwimlaneActorId", "Darth Vader");
        workItem.setProcessInstanceId(10);
        handler.executeWorkItem(workItem, manager);
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        Assert.assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        Assert.assertEquals("TaskName", task.getName());
        Assert.assertEquals(10, task.getPriority().intValue());
        Assert.assertEquals("Comment", task.getDescription());
        Assert.assertEquals(Status.Reserved, task.getStatus());
        Assert.assertEquals("Darth Vader", task.getActualOwner().getId());
        Assert.assertEquals(10, task.getProcessInstanceId().intValue());
        taskService.start(task.getId(), "Darth Vader");
        taskService.complete(task.getId(), "Darth Vader", null);
        Assert.assertTrue(manager.waitTillCompleted(HTWorkItemHandlerBaseTest.MANAGER_COMPLETION_WAIT_TIME));
    }

    @Test
    public void testTaskWithAutoClaimTaskWithGroupOnly() throws Exception {
        HTWorkItemHandlerBaseTest.TestWorkItemManager manager = new HTWorkItemHandlerBaseTest.TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("NodeName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("GroupId", "Crusaders");
        workItem.setParameter("SwimlaneActorId", "Darth Vader");
        workItem.setProcessInstanceId(10);
        handler.executeWorkItem(workItem, manager);
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        Assert.assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        Assert.assertEquals("TaskName", task.getName());
        Assert.assertEquals(10, task.getPriority().intValue());
        Assert.assertEquals("Comment", task.getDescription());
        Assert.assertEquals(Status.Reserved, task.getStatus());
        Assert.assertEquals("Darth Vader", task.getActualOwner().getId());
        Assert.assertEquals(10, task.getProcessInstanceId().intValue());
        taskService.start(task.getId(), "Darth Vader");
        taskService.complete(task.getId(), "Darth Vader", null);
        Assert.assertTrue(manager.waitTillCompleted(HTWorkItemHandlerBaseTest.MANAGER_COMPLETION_WAIT_TIME));
    }

    @Test
    public void testTaskWithDueDate() throws Exception {
        HTWorkItemHandlerBaseTest.TestWorkItemManager manager = new HTWorkItemHandlerBaseTest.TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("NodeName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setParameter("DueDate", "2013-11-25T10:35:00Z");
        workItem.setProcessInstanceId(10);
        handler.executeWorkItem(workItem, manager);
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        Assert.assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        Assert.assertEquals("TaskName", task.getName());
        Assert.assertEquals(10, task.getPriority().intValue());
        Assert.assertEquals("Comment", task.getDescription());
        Assert.assertEquals(Status.Reserved, task.getStatus());
        Assert.assertEquals("Darth Vader", task.getActualOwner().getId());
        Assert.assertEquals(10, task.getProcessInstanceId().intValue());
        Assert.assertEquals(DateTimeUtils.parseDateTime("2013-11-25T10:35:00Z"), task.getExpirationTime().getTime());
        taskService.start(task.getId(), "Darth Vader");
        taskService.complete(task.getId(), "Darth Vader", null);
        Assert.assertTrue(manager.waitTillCompleted(HTWorkItemHandlerBaseTest.MANAGER_COMPLETION_WAIT_TIME));
    }

    @Test
    public void testTaskWithDelay() throws Exception {
        HTWorkItemHandlerBaseTest.TestWorkItemManager manager = new HTWorkItemHandlerBaseTest.TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("NodeName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setParameter("DueDate", "P2d");// Period 2 days
        
        workItem.setProcessInstanceId(10);
        handler.executeWorkItem(workItem, manager);
        long currentTime = new Date().getTime();
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        Assert.assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        Assert.assertEquals("TaskName", task.getName());
        Assert.assertEquals(10, task.getPriority().intValue());
        Assert.assertEquals("Comment", task.getDescription());
        Assert.assertEquals(Status.Reserved, task.getStatus());
        Assert.assertEquals("Darth Vader", task.getActualOwner().getId());
        Assert.assertEquals(10, task.getProcessInstanceId().intValue());
        Assert.assertTrue(((currentTime + (DateTimeUtils.parseDuration("2d"))) > (task.getExpirationTime().getTime())));
        taskService.start(task.getId(), "Darth Vader");
        taskService.complete(task.getId(), "Darth Vader", null);
        Assert.assertTrue(manager.waitTillCompleted(HTWorkItemHandlerBaseTest.MANAGER_COMPLETION_WAIT_TIME));
    }

    @Test
    public void testTaskCompleteGroupActors() throws Exception {
        HTWorkItemHandlerBaseTest.TestWorkItemManager manager = new HTWorkItemHandlerBaseTest.TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("NodeName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("GroupId", "Crusaders");
        workItem.setProcessInstanceId(10);
        getHandler().executeWorkItem(workItem, manager);
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Luke Cage", "en-UK");
        Assert.assertEquals(1, tasks.size());
        TaskSummary taskSummary = tasks.get(0);
        Assert.assertEquals("TaskName", taskSummary.getName());
        Assert.assertEquals(10, taskSummary.getPriority().intValue());
        Assert.assertEquals("Comment", taskSummary.getDescription());
        Assert.assertEquals(Status.Ready, taskSummary.getStatus());
        taskService.claim(taskSummary.getId(), "Luke Cage");
        taskService.start(taskSummary.getId(), "Luke Cage");
        taskService.complete(taskSummary.getId(), "Luke Cage", null);
        Assert.assertTrue(manager.waitTillCompleted(HTWorkItemHandlerBaseTest.MANAGER_COMPLETION_WAIT_TIME));
        String actualOwner = ((String) (manager.getResults().get("ActorId")));
        Assert.assertNotNull(actualOwner);
        Assert.assertEquals("Luke Cage", actualOwner);
    }

    @Test
    public void testTaskWithVariables() throws Exception {
        final HTWorkItemHandlerBaseTest.TestWorkItemManager manager = new HTWorkItemHandlerBaseTest.TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        final String taskDescriptionParam = "Comment for task " + ("${task.id} " + ("${task.taskData.processSessionId} " + ("${task.taskData.actualOwner.id} " + "${task.taskData.parentId}")));
        handler.executeWorkItem(prepareWorkItemWithTaskVariables(taskDescriptionParam), manager);
        final List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        Assert.assertEquals(1, tasks.size());
        final Task task = taskService.getTaskById(tasks.get(0).getId());
        testTaskWithExpectedDescription(task, ((((((("Comment for task " + (task.getId())) + " ") + (task.getTaskData().getProcessSessionId())) + " ") + (task.getTaskData().getActualOwner().getId())) + " ") + (task.getTaskData().getParentId())));
        taskService.start(task.getId(), "Darth Vader");
        taskService.complete(task.getId(), "Darth Vader", null);
        Assert.assertTrue(manager.waitTillCompleted(HTWorkItemHandlerBaseTest.MANAGER_COMPLETION_WAIT_TIME));
        final String actualOwner = ((String) (manager.getResults().get("ActorId")));
        Assert.assertNotNull(actualOwner);
        Assert.assertEquals("Darth Vader", actualOwner);
    }

    @Test(timeout = 10000)
    public void testTaskWithVariablesRecurse() {
        final HTWorkItemHandlerBaseTest.TestWorkItemManager manager = new HTWorkItemHandlerBaseTest.TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        handler.executeWorkItem(prepareWorkItemWithTaskVariables("Comment for task ${task.description}"), manager);
        final List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        Assert.assertEquals(1, tasks.size());
        final Task task = taskService.getTaskById(tasks.get(0).getId());
        testTaskWithExpectedDescription(task, task.getDescription());
        taskService.start(task.getId(), "Darth Vader");
        taskService.complete(task.getId(), "Darth Vader", null);
        Assert.assertTrue(manager.waitTillCompleted(HTWorkItemHandlerBaseTest.MANAGER_COMPLETION_WAIT_TIME));
        final String actualOwner = ((String) (manager.getResults().get("ActorId")));
        Assert.assertNotNull(actualOwner);
        Assert.assertEquals("Darth Vader", actualOwner);
    }

    private WorkItemImpl prepareWorkItemWithTaskVariables(final String taskDescriptionParam) {
        final WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("NodeName", "TaskName ${task.taskData.processInstanceId}");
        workItem.setParameter("Comment", taskDescriptionParam);
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setProcessInstanceId(10);
        return workItem;
    }

    private void testTaskWithExpectedDescription(final Task task, final String expectedDescription) {
        Assert.assertEquals(("TaskName " + (task.getTaskData().getProcessInstanceId())), task.getName());
        Assert.assertEquals(10, task.getPriority());
        Assert.assertEquals(expectedDescription, task.getDescription());
        Assert.assertEquals(Status.Reserved, task.getTaskData().getStatus());
        Assert.assertEquals("Darth Vader", task.getTaskData().getActualOwner().getId());
        Assert.assertEquals(10L, task.getTaskData().getProcessInstanceId());
    }

    public void setHandler(WorkItemHandler handler) {
        HTWorkItemHandlerBaseTest.this.handler = handler;
    }

    public WorkItemHandler getHandler() {
        return handler;
    }

    private class TestWorkItemManager implements WorkItemManager {
        private volatile boolean completed;

        private volatile boolean aborted;

        private volatile Map<String, Object> results;

        public synchronized boolean waitTillCompleted(long time) {
            if (!(isCompleted())) {
                try {
                    wait(time);
                } catch (InterruptedException e) {
                    // swallow and return state of completed
                }
            } 
            return isCompleted();
        }

        public synchronized boolean waitTillAborted(long time) {
            if (!(isAborted())) {
                try {
                    wait(time);
                } catch (InterruptedException e) {
                    // swallow and return state of aborted
                }
            } 
            return isAborted();
        }

        public void abortWorkItem(long id) {
            setAborted(true);
        }

        public synchronized boolean isAborted() {
            return aborted;
        }

        private synchronized void setAborted(boolean aborted) {
            HTWorkItemHandlerBaseTest.TestWorkItemManager.this.aborted = aborted;
            notifyAll();
        }

        public void completeWorkItem(long id, Map<String, Object> results) {
            HTWorkItemHandlerBaseTest.TestWorkItemManager.this.results = results;
            setCompleted(true);
        }

        private synchronized void setCompleted(boolean completed) {
            HTWorkItemHandlerBaseTest.TestWorkItemManager.this.completed = completed;
            notifyAll();
        }

        public synchronized boolean isCompleted() {
            return completed;
        }

        public Map<String, Object> getResults() {
            return results;
        }

        public void registerWorkItemHandler(String workItemName, WorkItemHandler handler) {
        }
    }

    public static class AddedTaskListener extends DefaultTaskEventListener {
        public AddedTaskListener() {
        }

        public AddedTaskListener(boolean throwException) {
            HTWorkItemHandlerBaseTest.AddedTaskListener.this.throwException = throwException;
        }

        private boolean throwException = false;

        public boolean isThrowException() {
            return throwException;
        }

        public void setThrowException(boolean throwException) {
            HTWorkItemHandlerBaseTest.AddedTaskListener.this.throwException = throwException;
        }

        @Override
        public void afterTaskAddedEvent(TaskEvent event) {
            if (isThrowException()) {
                throw new RuntimeException("test exception");
            } 
        }
    }
}
