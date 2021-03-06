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


package org.jbpm.services.task;

import java.util.ArrayList;
import org.junit.Assert;
import org.kie.internal.task.api.model.ContentData;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.jbpm.services.task.util.CountDownTaskEventListener;
import java.util.Date;
import java.util.HashMap;
import java.io.InputStreamReader;
import org.kie.internal.task.api.model.InternalContent;
import org.kie.internal.task.api.model.InternalOrganizationalEntity;
import org.kie.internal.task.api.model.InternalPeopleAssignments;
import org.kie.internal.task.api.model.InternalTask;
import org.kie.internal.task.api.model.InternalTaskData;
import java.util.List;
import java.util.Map;
import org.jbpm.services.task.deadlines.notifications.impl.MockNotificationListener;
import org.jbpm.services.task.deadlines.NotificationListener;
import org.kie.api.task.model.OrganizationalEntity;
import java.io.Reader;
import org.kie.api.task.model.Task;
import org.jbpm.services.task.impl.factories.TaskFactory;
import org.kie.internal.task.api.TaskModelProvider;
import org.junit.Test;
import org.kie.api.task.model.User;

public abstract class DeadlinesBaseTest extends HumanTaskServicesBaseTest {
    protected NotificationListener notificationListener;

    public void tearDown() {
        super.tearDown();
    }

    @Test(timeout = 10000)
    public void testDelayedEmailNotificationOnDeadline() throws Exception {
        CountDownTaskEventListener countDownListener = new CountDownTaskEventListener(1, false, true);
        addCountDownListner(countDownListener);
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());
        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotification));
        Task task = ((Task) (TaskFactory.evalTask(reader, vars)));
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();
        InternalContent content = ((InternalContent) (TaskModelProvider.getFactory().newContent()));
        Map<String, String> params = fillMarshalSubjectAndBodyParams();
        ContentData marshalledObject = ContentMarshallerHelper.marshal(task, params, null);
        content.setContent(marshalledObject.getContent());
        taskService.addContent(taskId, content);
        long contentId = content.getId();
        content = ((InternalContent) (taskService.getContentById(contentId)));
        Object unmarshallObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        checkContentSubjectAndBody(unmarshallObject);
        // emails should not be set yet
        // assertEquals(0, getWiser().getMessages().size());
        Assert.assertEquals(0, ((MockNotificationListener) (notificationListener)).getEventsRecieved().size());
        countDownListener.waitTillCompleted();
        // 1 email with two recipients should now exist
        Assert.assertEquals(1, ((MockNotificationListener) (notificationListener)).getEventsRecieved().size());
    }

    @Test(timeout = 10000)
    public void testDelayedEmailNotificationOnDeadlineContentSingleObject() throws Exception {
        CountDownTaskEventListener countDownListener = new CountDownTaskEventListener(1, false, true);
        addCountDownListner(countDownListener);
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());
        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotificationContentSingleObject));
        Task task = ((Task) (TaskFactory.evalTask(reader, vars)));
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();
        InternalContent content = ((InternalContent) (TaskModelProvider.getFactory().newContent()));
        ContentData marshalledObject = ContentMarshallerHelper.marshal(task, "'singleobject'", null);
        content.setContent(marshalledObject.getContent());
        taskService.addContent(taskId, content);
        long contentId = content.getId();
        content = ((InternalContent) (taskService.getContentById(contentId)));
        Object unmarshallObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        Assert.assertEquals("'singleobject'", unmarshallObject.toString());
        // emails should not be set yet
        Assert.assertEquals(0, ((MockNotificationListener) (notificationListener)).getEventsRecieved().size());
        countDownListener.waitTillCompleted();
        // 1 email with two recipients should now exist
        Assert.assertEquals(1, ((MockNotificationListener) (notificationListener)).getEventsRecieved().size());
    }

    @Test(timeout = 10000)
    public void testDelayedEmailNotificationOnDeadlineTaskCompleted() throws Exception {
        CountDownTaskEventListener countDownListener = new CountDownTaskEventListener(1, false, true);
        addCountDownListner(countDownListener);
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());
        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotification));
        InternalTask task = ((InternalTask) (TaskFactory.evalTask(reader, vars)));
        ((InternalTaskData) (task.getTaskData())).setSkipable(true);
        InternalPeopleAssignments assignments = ((InternalPeopleAssignments) (TaskModelProvider.getFactory().newPeopleAssignments()));
        List<OrganizationalEntity> ba = new ArrayList<OrganizationalEntity>();
        User user = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) (user)).setId("Administrator");
        ba.add(user);
        assignments.setBusinessAdministrators(ba);
        List<OrganizationalEntity> po = new ArrayList<OrganizationalEntity>();
        User user2 = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) (user2)).setId("Administrator");
        po.add(user2);
        assignments.setPotentialOwners(po);
        task.setPeopleAssignments(assignments);
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();
        InternalContent content = ((InternalContent) (TaskModelProvider.getFactory().newContent()));
        Map<String, String> params = fillMarshalSubjectAndBodyParams();
        ContentData marshalledObject = ContentMarshallerHelper.marshal(task, params, null);
        content.setContent(marshalledObject.getContent());
        taskService.addContent(taskId, content);
        long contentId = content.getId();
        content = ((InternalContent) (taskService.getContentById(contentId)));
        Object unmarshallObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        checkContentSubjectAndBody(unmarshallObject);
        taskService.start(taskId, "Administrator");
        taskService.complete(taskId, "Administrator", null);
        // emails should not be set yet
        Assert.assertEquals(0, ((MockNotificationListener) (notificationListener)).getEventsRecieved().size());
        countDownListener.waitTillCompleted();
        // no email should ne sent as task was completed before deadline was triggered
        Assert.assertEquals(0, ((MockNotificationListener) (notificationListener)).getEventsRecieved().size());
        task = ((InternalTask) (taskService.getTaskById(taskId)));
        Assert.assertEquals(Status.Completed, task.getTaskData().getStatus());
        Assert.assertEquals(0, ((InternalTask) (task)).getDeadlines().getStartDeadlines().size());
        Assert.assertEquals(0, ((InternalTask) (task)).getDeadlines().getEndDeadlines().size());
    }

    @Test(timeout = 10000)
    public void testDelayedEmailNotificationOnDeadlineTaskFailed() throws Exception {
        CountDownTaskEventListener countDownListener = new CountDownTaskEventListener(1, false, true);
        addCountDownListner(countDownListener);
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());
        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotification));
        InternalTask task = ((InternalTask) (TaskFactory.evalTask(reader, vars)));
        ((InternalTaskData) (task.getTaskData())).setSkipable(true);
        InternalPeopleAssignments assignments = ((InternalPeopleAssignments) (TaskModelProvider.getFactory().newPeopleAssignments()));
        List<OrganizationalEntity> ba = new ArrayList<OrganizationalEntity>();
        User user = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) (user)).setId("Administrator");
        ba.add(user);
        assignments.setBusinessAdministrators(ba);
        List<OrganizationalEntity> po = new ArrayList<OrganizationalEntity>();
        User user2 = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) (user2)).setId("Administrator");
        po.add(user2);
        assignments.setPotentialOwners(po);
        task.setPeopleAssignments(assignments);
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();
        InternalContent content = ((InternalContent) (TaskModelProvider.getFactory().newContent()));
        Map<String, String> params = fillMarshalSubjectAndBodyParams();
        ContentData marshalledObject = ContentMarshallerHelper.marshal(task, params, null);
        content.setContent(marshalledObject.getContent());
        taskService.addContent(taskId, content);
        long contentId = content.getId();
        content = ((InternalContent) (taskService.getContentById(contentId)));
        Object unmarshallObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        checkContentSubjectAndBody(unmarshallObject);
        taskService.start(taskId, "Administrator");
        taskService.fail(taskId, "Administrator", null);
        // emails should not be set yet
        Assert.assertEquals(0, ((MockNotificationListener) (notificationListener)).getEventsRecieved().size());
        countDownListener.waitTillCompleted();
        // no email should ne sent as task was completed before deadline was triggered
        Assert.assertEquals(0, ((MockNotificationListener) (notificationListener)).getEventsRecieved().size());
        task = ((InternalTask) (taskService.getTaskById(taskId)));
        Assert.assertEquals(Status.Failed, task.getTaskData().getStatus());
        Assert.assertEquals(0, task.getDeadlines().getStartDeadlines().size());
        Assert.assertEquals(0, task.getDeadlines().getEndDeadlines().size());
    }

    @Test(timeout = 10000)
    public void testDelayedEmailNotificationOnDeadlineTaskSkipped() throws Exception {
        CountDownTaskEventListener countDownListener = new CountDownTaskEventListener(1, false, true);
        addCountDownListner(countDownListener);
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());
        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotification));
        InternalTask task = ((InternalTask) (TaskFactory.evalTask(reader, vars)));
        ((InternalTaskData) (task.getTaskData())).setSkipable(true);
        InternalPeopleAssignments assignments = ((InternalPeopleAssignments) (TaskModelProvider.getFactory().newPeopleAssignments()));
        List<OrganizationalEntity> ba = new ArrayList<OrganizationalEntity>();
        User user = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) (user)).setId("Administrator");
        ba.add(user);
        assignments.setBusinessAdministrators(ba);
        List<OrganizationalEntity> po = new ArrayList<OrganizationalEntity>();
        User user2 = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) (user2)).setId("Administrator");
        po.add(user2);
        assignments.setPotentialOwners(po);
        task.setPeopleAssignments(assignments);
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();
        InternalContent content = ((InternalContent) (TaskModelProvider.getFactory().newContent()));
        Map<String, String> params = fillMarshalSubjectAndBodyParams();
        ContentData marshalledObject = ContentMarshallerHelper.marshal(task, params, null);
        content.setContent(marshalledObject.getContent());
        taskService.addContent(taskId, content);
        long contentId = content.getId();
        content = ((InternalContent) (taskService.getContentById(contentId)));
        Object unmarshallObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        checkContentSubjectAndBody(unmarshallObject);
        taskService.skip(taskId, "Administrator");
        // emails should not be set yet
        Assert.assertEquals(0, ((MockNotificationListener) (notificationListener)).getEventsRecieved().size());
        countDownListener.waitTillCompleted();
        // no email should ne sent as task was completed before deadline was triggered
        Assert.assertEquals(0, ((MockNotificationListener) (notificationListener)).getEventsRecieved().size());
        task = ((InternalTask) (taskService.getTaskById(taskId)));
        Assert.assertEquals(Status.Obsolete, task.getTaskData().getStatus());
        Assert.assertEquals(0, task.getDeadlines().getStartDeadlines().size());
        Assert.assertEquals(0, task.getDeadlines().getEndDeadlines().size());
    }

    @Test(timeout = 10000)
    public void testDelayedEmailNotificationOnDeadlineTaskExited() throws Exception {
        CountDownTaskEventListener countDownListener = new CountDownTaskEventListener(1, false, true);
        addCountDownListner(countDownListener);
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());
        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotification));
        InternalTask task = ((InternalTask) (TaskFactory.evalTask(reader, vars)));
        ((InternalTaskData) (task.getTaskData())).setSkipable(true);
        InternalPeopleAssignments assignments = ((InternalPeopleAssignments) (TaskModelProvider.getFactory().newPeopleAssignments()));
        List<OrganizationalEntity> ba = new ArrayList<OrganizationalEntity>();
        User user = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) (user)).setId("Administrator");
        ba.add(user);
        assignments.setBusinessAdministrators(ba);
        List<OrganizationalEntity> po = new ArrayList<OrganizationalEntity>();
        User user2 = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) (user2)).setId("Administrator");
        po.add(user2);
        assignments.setPotentialOwners(po);
        task.setPeopleAssignments(assignments);
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();
        InternalContent content = ((InternalContent) (TaskModelProvider.getFactory().newContent()));
        Map<String, String> params = fillMarshalSubjectAndBodyParams();
        ContentData marshalledObject = ContentMarshallerHelper.marshal(task, params, null);
        content.setContent(marshalledObject.getContent());
        taskService.addContent(taskId, content);
        long contentId = content.getId();
        content = ((InternalContent) (taskService.getContentById(contentId)));
        Object unmarshallObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        checkContentSubjectAndBody(unmarshallObject);
        taskService.exit(taskId, "Administrator");
        // emails should not be set yet
        Assert.assertEquals(0, ((MockNotificationListener) (notificationListener)).getEventsRecieved().size());
        countDownListener.waitTillCompleted();
        // no email should ne sent as task was completed before deadline was triggered
        Assert.assertEquals(0, ((MockNotificationListener) (notificationListener)).getEventsRecieved().size());
        task = ((InternalTask) (taskService.getTaskById(taskId)));
        Assert.assertEquals(Status.Exited, task.getTaskData().getStatus());
        Assert.assertEquals(0, task.getDeadlines().getStartDeadlines().size());
        Assert.assertEquals(0, task.getDeadlines().getEndDeadlines().size());
    }

    @Test(timeout = 10000)
    public void testDelayedReassignmentOnDeadline() throws Exception {
        CountDownTaskEventListener countDownListener = new CountDownTaskEventListener(1, true, false);
        addCountDownListner(countDownListener);
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());
        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithReassignment));
        Task task = ((InternalTask) (TaskFactory.evalTask(reader, vars)));
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();
        task = taskService.getTaskById(taskId);
        List<OrganizationalEntity> potentialOwners = ((List<OrganizationalEntity>) (task.getPeopleAssignments().getPotentialOwners()));
        List<String> ids = new ArrayList<String>(potentialOwners.size());
        for (OrganizationalEntity entity : potentialOwners) {
            ids.add(entity.getId());
        }
        Assert.assertTrue(ids.contains("Tony Stark"));
        Assert.assertTrue(ids.contains("Luke Cage"));
        // should have re-assigned by now
        countDownListener.waitTillCompleted();
        task = taskService.getTaskById(taskId);
        Assert.assertEquals(Status.Ready, task.getTaskData().getStatus());
        potentialOwners = ((List<OrganizationalEntity>) (task.getPeopleAssignments().getPotentialOwners()));
        ids = new ArrayList<String>(potentialOwners.size());
        for (OrganizationalEntity entity : potentialOwners) {
            ids.add(entity.getId());
        }
        Assert.assertTrue(ids.contains("Bobba Fet"));
        Assert.assertTrue(ids.contains("Jabba Hutt"));
    }

    @Test(timeout = 10000)
    public void testDelayedEmailNotificationOnDeadlineTaskCompletedMultipleTasks() throws Exception {
        CountDownTaskEventListener countDownListener = new CountDownTaskEventListener(2, false, true);
        addCountDownListner(countDownListener);
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());
        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotification));
        // create task 1
        InternalTask task = ((InternalTask) (TaskFactory.evalTask(reader, vars)));
        ((InternalTaskData) (task.getTaskData())).setSkipable(true);
        InternalPeopleAssignments assignments = ((InternalPeopleAssignments) (TaskModelProvider.getFactory().newPeopleAssignments()));
        List<OrganizationalEntity> ba = new ArrayList<OrganizationalEntity>();
        User user = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) (user)).setId("Administrator");
        ba.add(user);
        assignments.setBusinessAdministrators(ba);
        List<OrganizationalEntity> po = new ArrayList<OrganizationalEntity>();
        User user2 = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) (user2)).setId("Administrator");
        po.add(user2);
        assignments.setPotentialOwners(po);
        task.setPeopleAssignments(assignments);
        // create task 2
        reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotification));
        InternalTask task2 = ((InternalTask) (TaskFactory.evalTask(reader, vars)));
        ((InternalTaskData) (task2.getTaskData())).setSkipable(true);
        task2.setPeopleAssignments(assignments);
        taskService.addTask(task, new HashMap<String, Object>());
        taskService.addTask(task2, new HashMap<String, Object>());
        long taskId = task.getId();
        InternalContent content = ((InternalContent) (TaskModelProvider.getFactory().newContent()));
        Map<String, String> params = fillMarshalSubjectAndBodyParams();
        ContentData marshalledObject = ContentMarshallerHelper.marshal(task, params, null);
        content.setContent(marshalledObject.getContent());
        taskService.addContent(taskId, content);
        long contentId = content.getId();
        content = ((InternalContent) (taskService.getContentById(contentId)));
        Object unmarshallObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        checkContentSubjectAndBody(unmarshallObject);
        taskService.start(taskId, "Administrator");
        taskService.complete(taskId, "Administrator", null);
        // emails should not be set yet
        Assert.assertEquals(0, ((MockNotificationListener) (notificationListener)).getEventsRecieved().size());
        countDownListener.waitTillCompleted();
        // no email should be sent as task was completed before deadline was triggered
        Assert.assertEquals(1, ((MockNotificationListener) (notificationListener)).getEventsRecieved().size());
        task = ((InternalTask) (taskService.getTaskById(taskId)));
        Assert.assertEquals(Status.Completed, task.getTaskData().getStatus());
        Assert.assertEquals(0, ((InternalTask) (task)).getDeadlines().getStartDeadlines().size());
        Assert.assertEquals(0, ((InternalTask) (task)).getDeadlines().getEndDeadlines().size());
        taskService.start(task2.getId(), "Administrator");
        taskService.complete(task2.getId(), "Administrator", null);
        task = ((InternalTask) (taskService.getTaskById(task2.getId())));
        Assert.assertEquals(Status.Completed, task.getTaskData().getStatus());
        Assert.assertEquals(0, ((InternalTask) (task)).getDeadlines().getStartDeadlines().size());
        Assert.assertEquals(0, ((InternalTask) (task)).getDeadlines().getEndDeadlines().size());
    }
}

