/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */


package org.jbpm.services.task.query;

import org.junit.After;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Before;
import javax.persistence.EntityManagerFactory;
import org.jbpm.services.task.commands.GetTasksByVariousFieldsCommand;
import java.util.HashMap;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.HumanTaskServicesBaseTest;
import org.kie.internal.task.api.model.InternalTaskData;
import org.kie.internal.task.api.InternalTaskService;
import java.util.List;
import java.util.Map;
import javax.persistence.Persistence;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import org.kie.api.task.model.Status;
import java.io.StringReader;
import org.kie.api.task.model.Task;
import org.jbpm.services.task.impl.factories.TaskFactory;
import org.jbpm.services.task.impl.model.TaskImpl;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.task.query.TaskSummaryQueryBuilder;
import org.junit.Test;

@SuppressWarnings(value = "deprecation")
public class TaskQueryBuilderLocalTest extends HumanTaskServicesBaseTest {
    private PoolingDataSource pds;

    private EntityManagerFactory emf;

    @Before
    public void setup() {
        pds = setupPoolingDataSource();
        emf = Persistence.createEntityManagerFactory("org.jbpm.services.task");
        TaskQueryBuilderLocalTest.this.taskService = ((InternalTaskService) (HumanTaskServiceFactory.newTaskServiceConfigurator().entityManagerFactory(emf).getTaskService()));
    }

    @After
    public void clean() {
        super.tearDown();
        if ((emf) != null) {
            emf.close();
        } 
        if ((pds) != null) {
            pds.close();
        } 
    }

    private static final String stakeHolder = "vampire";

    private TaskImpl addTask(long workItemId, long procInstId, String busAdmin, String potOwner, String name, String deploymentId) {
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += ((((((((("peopleAssignments = (with ( new PeopleAssignments() ) { " + "taskStakeholders = [new User('") + (TaskQueryBuilderLocalTest.stakeHolder)) + "')],") + "businessAdministrators = [new User('") + busAdmin) + "')],") + "potentialOwners = [new User('") + potOwner) + "')]") + " }),";
        str += ("name =  '" + name) + "' })";
        Task taskImpl = TaskFactory.evalTask(new StringReader(str));
        ((InternalTaskData) (taskImpl.getTaskData())).setWorkItemId(workItemId);
        ((InternalTaskData) (taskImpl.getTaskData())).setProcessInstanceId(procInstId);
        ((InternalTaskData) (taskImpl.getTaskData())).setDeploymentId(deploymentId);
        taskService.addTask(taskImpl, new HashMap<String, java.lang.Object>());
        Assert.assertNotNull("Null task id", taskImpl.getId());
        return ((TaskImpl) (taskImpl));
    }

    @Test
    public void testTaskQueryBuilderSimply() {
        TaskSummaryQueryBuilder queryBuilder = taskService.taskSummaryQuery(TaskQueryBuilderLocalTest.stakeHolder);
        queryBuilder.build().getResultList();
    }

    @Test
    public void testGetTasksByVariousFields() {
        Task[] tasks = new Task[12];
        List<Long> workItemIds = new ArrayList<Long>();
        List<Long> procInstIds = new ArrayList<Long>();
        List<Long> taskIds = new ArrayList<Long>();
        List<String> busAdmins = new ArrayList<String>();
        List<String> potOwners = new ArrayList<String>();
        List<Status> statuses = new ArrayList<Status>();
        statuses.add(Status.Reserved);
        TaskImpl taskImpl;
        Long firstTaskId;
        {
            long workItemId = 23;
            long procInstId = 101;
            String busAdmin = "Wintermute";
            String potOwner = "Maelcum";
            taskImpl = addTask(workItemId, procInstId, busAdmin, potOwner, "This is my task name", null);
            firstTaskId = taskImpl.getId();
            taskIds.add(firstTaskId);
            Assert.assertEquals(potOwner, taskImpl.getTaskData().getActualOwner().getId());
            tasks[0] = taskImpl;
            procInstIds.add(procInstId);
            workItemIds.add(workItemId);
            busAdmins.add(busAdmin);
            potOwners.add(potOwner);
        }
        List<TaskSummary> results = taskService.getTasksByVariousFields(TaskQueryBuilderLocalTest.stakeHolder, null, null, null, null, null, null, null, null, false);
        Assert.assertFalse("No tasks retrieved!", results.isEmpty());
        results = taskService.getTasksByVariousFields(TaskQueryBuilderLocalTest.stakeHolder, workItemIds, null, null, null, null, null, null, null, false);
        Assert.assertEquals("List of tasks: work item id", 1, results.size());
        {
            long workItemId = 25;
            long procInstId = 103;
            String busAdmin = "Neuromancer";
            String potOwner = "Hideo";
            taskImpl = addTask(workItemId, procInstId, busAdmin, potOwner, "This is my task name", null);
            taskIds.add(taskImpl.getId());
            Assert.assertEquals(potOwner, taskImpl.getTaskData().getActualOwner().getId());
            // start task
            taskService.start(taskImpl.getId(), potOwner);
            tasks[1] = ((Task) (taskService.getTaskById(taskImpl.getId())));
            statuses.add(tasks[1].getTaskData().getStatus());
            procInstIds.add(procInstId);
            workItemIds.add(workItemId);
            busAdmins.add(busAdmin);
            potOwners.add(potOwner);
        }
        {
            // Add one more task, just to make sure things are working wel
            long workItemId = 57;
            long procInstId = 111;
            String busAdmin = "reviewer";
            String potOwner = "translator";
            String deploymentId = "armitage";
            String name = "Koude Bevel";
            taskImpl = addTask(workItemId, procInstId, busAdmin, potOwner, name, deploymentId);
            Assert.assertEquals(potOwner, taskImpl.getTaskData().getActualOwner().getId());
            taskService.start(taskImpl.getId(), potOwner);
            taskService.fail(taskImpl.getId(), busAdmin, null);
        }
        // everything
        results = taskService.getTasksByVariousFields(TaskQueryBuilderLocalTest.stakeHolder, null, null, null, null, null, null, null, null, false);
        Assert.assertEquals("List of tasks: everything", 3, results.size());
        testOrderByTaskIdAscending(results);
        // max results
        GetTasksByVariousFieldsCommand queryCmd = new GetTasksByVariousFieldsCommand();
        queryCmd.setUserId(TaskQueryBuilderLocalTest.stakeHolder);
        queryCmd.setUnion(false);
        queryCmd.setMaxResults(2);
        results = ((InternalTaskService) (taskService)).execute(queryCmd);
        Assert.assertEquals("List of tasks: max results", 2, results.size());
        testOrderByTaskIdAscending(results);
        Assert.assertEquals(("Did not order when returning tasks (first task id: " + (results.get(0).getId())), firstTaskId.longValue(), results.get(0).getId().longValue());
        // single param tests
        results = taskService.getTasksByVariousFields(TaskQueryBuilderLocalTest.stakeHolder, workItemIds, null, null, null, null, null, null, null, false);
        Assert.assertEquals("List of tasks: work item ids", 2, results.size());
        testOrderByTaskIdAscending(results);
        results = taskService.getTasksByVariousFields(TaskQueryBuilderLocalTest.stakeHolder, null, taskIds, null, null, null, null, null, null, false);
        Assert.assertEquals("List of tasks: task ids", 2, results.size());
        testOrderByTaskIdAscending(results);
        results = taskService.getTasksByVariousFields(TaskQueryBuilderLocalTest.stakeHolder, null, null, procInstIds, null, null, null, null, null, false);
        Assert.assertEquals("List of tasks: process instance ids", 2, results.size());
        testOrderByTaskIdAscending(results);
        results = taskService.getTasksByVariousFields(TaskQueryBuilderLocalTest.stakeHolder, null, null, null, busAdmins, null, null, null, null, false);
        Assert.assertEquals("List of tasks: bus admins", 2, results.size());
        testOrderByTaskIdAscending(results);
        results = taskService.getTasksByVariousFields(TaskQueryBuilderLocalTest.stakeHolder, null, null, null, null, potOwners, null, null, null, false);
        Assert.assertEquals("List of tasks: pot owners", 2, results.size());
        testOrderByTaskIdAscending(results);
        results = taskService.getTasksByVariousFields(TaskQueryBuilderLocalTest.stakeHolder, null, null, null, null, null, potOwners, null, null, false);
        Assert.assertEquals("List of tasks: task owners", 2, results.size());
        testOrderByTaskIdAscending(results);
        results = taskService.getTasksByVariousFields(TaskQueryBuilderLocalTest.stakeHolder, null, null, null, null, null, null, statuses, null, false);
        Assert.assertEquals("List of tasks: status", 2, results.size());
        testOrderByTaskIdAscending(results);
        // work item id and/or task id
        List<Long> testLongList = new ArrayList<Long>();
        testLongList.add(workItemIds.get(0));
        List<Long> testLongListTwo = new ArrayList<Long>();
        testLongListTwo.add(taskIds.get(1));
        results = taskService.getTasksByVariousFields(TaskQueryBuilderLocalTest.stakeHolder, testLongList, testLongListTwo, null, null, null, null, null, null, false);
        Assert.assertEquals("List of tasks", 0, results.size());
        results = taskService.getTasksByVariousFields(TaskQueryBuilderLocalTest.stakeHolder, testLongList, testLongListTwo, null, null, null, null, null, null, true);
        Assert.assertEquals("List of tasks", 2, results.size());
        testOrderByTaskIdAscending(results);
        // task id and/or process instance id
        testLongList.clear();
        testLongList.add(procInstIds.get(1));
        results = taskService.getTasksByVariousFields(TaskQueryBuilderLocalTest.stakeHolder, null, testLongListTwo, testLongList, null, null, null, null, null, true);
        Assert.assertEquals("List of tasks", 1, results.size());
        testOrderByTaskIdAscending(results);
        results = taskService.getTasksByVariousFields(TaskQueryBuilderLocalTest.stakeHolder, null, testLongListTwo, testLongList, null, null, null, null, null, false);
        Assert.assertEquals("List of tasks", 1, results.size());
        testOrderByTaskIdAscending(results);
        // process instance id and/or bus admin
        List<String> testStringList = new ArrayList<String>();
        testStringList.add(busAdmins.get(0));
        results = taskService.getTasksByVariousFields(TaskQueryBuilderLocalTest.stakeHolder, null, testLongListTwo, null, testStringList, null, null, null, null, false);
        Assert.assertEquals("List of tasks", 0, results.size());
        results = taskService.getTasksByVariousFields(TaskQueryBuilderLocalTest.stakeHolder, null, testLongListTwo, null, testStringList, null, null, null, null, true);
        Assert.assertEquals("List of tasks", 2, results.size());
        testOrderByTaskIdAscending(results);
        // bus admin and/or pot owner
        testStringList.clear();
        testStringList.add(busAdmins.get(1));
        List<String> testStringListTwo = new ArrayList<String>();
        testStringListTwo.add(potOwners.get(0));
        results = taskService.getTasksByVariousFields(TaskQueryBuilderLocalTest.stakeHolder, null, null, null, testStringList, testStringListTwo, null, null, null, false);
        Assert.assertEquals("List of tasks", 0, results.size());
        results = taskService.getTasksByVariousFields(TaskQueryBuilderLocalTest.stakeHolder, null, null, null, testStringList, testStringListTwo, null, null, null, true);
        Assert.assertEquals("List of tasks", 2, results.size());
        testOrderByTaskIdAscending(results);
        // pot owner and/or task owner
        testStringList.clear();
        testStringList.add(tasks[1].getTaskData().getActualOwner().getId());
        results = taskService.getTasksByVariousFields(TaskQueryBuilderLocalTest.stakeHolder, null, null, null, null, testStringListTwo, testStringList, null, null, false);
        Assert.assertEquals("List of tasks", 0, results.size());
        results = taskService.getTasksByVariousFields(TaskQueryBuilderLocalTest.stakeHolder, null, null, null, null, testStringListTwo, testStringList, null, null, true);
        Assert.assertEquals("List of tasks", 2, results.size());
        testOrderByTaskIdAscending(results);
        // task owner and/or status
        List<Status> testStatusList = new ArrayList<Status>();
        testStatusList.add(statuses.get(0));
        results = taskService.getTasksByVariousFields(TaskQueryBuilderLocalTest.stakeHolder, null, null, null, null, null, testStringList, testStatusList, null, false);
        Assert.assertEquals("List of tasks", 0, results.size());
        results = taskService.getTasksByVariousFields(TaskQueryBuilderLocalTest.stakeHolder, null, null, null, null, null, testStringList, testStatusList, null, true);
        Assert.assertEquals("List of tasks", 2, results.size());
        testOrderByTaskIdAscending(results);
        // deploymentId
        testStatusList.add(statuses.get(0));
        results = taskService.getTasksByVariousFields(TaskQueryBuilderLocalTest.stakeHolder, null, null, null, null, null, testStringList, testStatusList, null, false);
    }

    private void testOrderByTaskIdAscending(List<TaskSummary> results) {
        for (int i = 1; i < (results.size()); ++i) {
            Assert.assertTrue(((("Tasks not correctly ordered: " + (results.get(0).getId())) + " ? ") + (results.get(1).getId())), ((results.get((i - 1)).getId()) < (results.get(i).getId())));
        }
    }

    @Test
    public void testGetTasksByVariousFieldsWithUserGroupCallback() {
        String potOwner = "Bobba Fet";
        List<String> potOwners = new ArrayList<String>();
        potOwners.add(potOwner);
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { " + ("businessAdministrators = [new Group('Administrators')]," + ("potentialOwners = [new Group('Crusaders')]" + " }),"));
        str += "name = 'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        ((InternalTaskData) (task.getTaskData())).setWorkItemId(1);
        ((InternalTaskData) (task.getTaskData())).setProcessInstanceId(1);
        taskService.addTask(task, new HashMap<String, java.lang.Object>());
        List<TaskSummary> results = taskService.getTasksByVariousFields("Administrator", null, null, null, null, potOwners, null, null, null, false);
        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        TaskSummary resultTask = results.get(0);
        // "Wintermute" does not have the proper permissions
        results = taskService.getTasksByVariousFields("Wintermute", null, null, null, null, potOwners, null, null, null, false);
        Assert.assertEquals(0, results.size());
    }

    @Test
    public void testGetTasksByVariousFieldsWithUserGroupCallbackAdmin() {
        String potOwner = "Administrator";
        List<String> busAdmins = new ArrayList<String>();
        busAdmins.add(potOwner);
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { " + ("businessAdministrators = [new Group('Administrators')]," + ("potentialOwners = [new Group('Crusaders')]" + " }),"));
        str += "name =  'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        ((InternalTaskData) (task.getTaskData())).setWorkItemId(1);
        ((InternalTaskData) (task.getTaskData())).setProcessInstanceId(1);
        taskService.addTask(task, new HashMap<String, java.lang.Object>());
        List<TaskSummary> results = taskService.getTasksByVariousFields(potOwner, null, null, null, busAdmins, null, null, null, null, false);
        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
    }

    @Test
    public void testGetTasksByVariousFieldsWithUserGroupCallbackByParams() {
        Map<String, List<?>> parameters = new HashMap<String, List<?>>();
        String potOwner = "Bobba Fet";
        List<String> potOwners = new ArrayList<String>();
        potOwners.add(potOwner);
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { " + ("businessAdministrators = [new Group('Administrators')]," + ("potentialOwners = [new Group('Crusaders')]" + " }),"));
        str += "name = 'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        ((InternalTaskData) (task.getTaskData())).setWorkItemId(1);
        ((InternalTaskData) (task.getTaskData())).setProcessInstanceId(1);
        taskService.addTask(task, new HashMap<String, java.lang.Object>());
        parameters.put(QueryParameterIdentifiers.POTENTIAL_OWNER_ID_LIST, potOwners);
        List<TaskSummary> results = taskService.getTasksByVariousFields("Administrator", parameters, false);
        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
    }

    @Test
    public void testGetTasksByVariousFieldsWithUserGroupCallbackAdminByParams() {
        Map<String, List<?>> parameters = new HashMap<String, List<?>>();
        String potOwner = "Administrator";
        List<String> busAdmins = new ArrayList<String>();
        busAdmins.add(potOwner);
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { " + ("businessAdministrators = [new Group('Administrators')]," + ("potentialOwners = [new Group('Crusaders')]" + " }),"));
        str += "name = 'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        ((InternalTaskData) (task.getTaskData())).setWorkItemId(1);
        ((InternalTaskData) (task.getTaskData())).setProcessInstanceId(1);
        taskService.addTask(task, new HashMap<String, java.lang.Object>());
        parameters.put(QueryParameterIdentifiers.BUSINESS_ADMIN_ID_LIST, busAdmins);
        List<TaskSummary> results = taskService.getTasksByVariousFields("Crusaders", parameters, false);
        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
    }

    @Test
    public void testTaskQueryBuilder() {
        Task[] tasks = new Task[12];
        List<Long> workItemIds = new ArrayList<Long>();
        List<Long> procInstIds = new ArrayList<Long>();
        List<Long> taskIds = new ArrayList<Long>();
        List<String> busAdmins = new ArrayList<String>();
        List<String> potOwners = new ArrayList<String>();
        List<Status> statuses = new ArrayList<Status>();
        statuses.add(Status.Reserved);
        TaskImpl taskImpl;
        Long firstTaskId;
        {
            long workItemId = 231;
            long procInstId = 1011;
            String busAdmin = "Parzival";
            String potOwner = "Art3mis";
            String name = "EggHunting";
            taskImpl = addTask(workItemId, procInstId, busAdmin, potOwner, name, null);
            firstTaskId = taskImpl.getId();
            taskIds.add(firstTaskId);
            Assert.assertEquals(potOwner, taskImpl.getTaskData().getActualOwner().getId());
            tasks[0] = taskImpl;
            procInstIds.add(procInstId);
            workItemIds.add(workItemId);
            busAdmins.add(busAdmin);
            potOwners.add(potOwner);
            // as much as possible
            TaskSummaryQueryBuilder queryBuilder = taskService.taskSummaryQuery(TaskQueryBuilderLocalTest.stakeHolder).intersect().workItemId(workItemId).processInstanceId(procInstId).businessAdmin(busAdmin).potentialOwner(potOwner).taskId(taskImpl.getId()).ascending(OrderBy.taskId);
            List<TaskSummary> results = queryBuilder.build().getResultList();
            Assert.assertEquals("List of tasks", 1, results.size());
            queryBuilder.clear();
            results = queryBuilder.build().getResultList();
            Assert.assertEquals("List of tasks", 1, results.size());
        }
        {
            long workItemId = 251;
            long procInstId = 1031;
            String busAdmin = "Neuromancer";
            String potOwner = "Hideo";
            taskImpl = addTask(workItemId, procInstId, busAdmin, potOwner, "This is my task name", null);
            taskIds.add(taskImpl.getId());
            Assert.assertEquals(potOwner, taskImpl.getTaskData().getActualOwner().getId());
            // start task
            taskService.start(taskImpl.getId(), potOwner);
            tasks[1] = ((Task) (taskService.getTaskById(taskImpl.getId())));
            statuses.add(tasks[1].getTaskData().getStatus());
            procInstIds.add(procInstId);
            workItemIds.add(workItemId);
            busAdmins.add(busAdmin);
            potOwners.add(potOwner);
            // as much as possible
            TaskSummaryQueryBuilder queryBuilder = taskService.taskSummaryQuery(TaskQueryBuilderLocalTest.stakeHolder).intersect().workItemId(workItemId).processInstanceId(procInstId).businessAdmin(busAdmin).potentialOwner(potOwner).taskId(taskImpl.getId());
            List<TaskSummary> results = queryBuilder.build().getResultList();
            Assert.assertEquals("List of tasks", 1, results.size());
            queryBuilder.clear();
            results = queryBuilder.build().getResultList();
            Assert.assertEquals("List of tasks", 2, results.size());
            queryBuilder.clear();
            queryBuilder.workItemId(workItemId);
            results = queryBuilder.build().getResultList();
            Assert.assertEquals("List of tasks", 1, results.size());
            Assert.assertEquals("Incorrect task retrieved", tasks[1].getId(), results.get(0).getId());
            queryBuilder.clear();
            queryBuilder.processInstanceId(procInstId);
            results = queryBuilder.build().getResultList();
            Assert.assertEquals("List of tasks", 1, results.size());
            Assert.assertEquals("Incorrect task retrieved", tasks[1].getId(), results.get(0).getId());
            queryBuilder.clear();
            queryBuilder.businessAdmin(busAdmin);
            results = queryBuilder.build().getResultList();
            Assert.assertEquals("List of tasks", 1, results.size());
            Assert.assertEquals("Incorrect task retrieved", tasks[1].getId(), results.get(0).getId());
            queryBuilder.clear();
            queryBuilder.potentialOwner(potOwner);
            results = queryBuilder.build().getResultList();
            Assert.assertEquals("List of tasks", 1, results.size());
            Assert.assertEquals("Incorrect task retrieved", tasks[1].getId(), results.get(0).getId());
            queryBuilder.clear();
            queryBuilder.taskId(taskImpl.getId());
            results = queryBuilder.build().getResultList();
            Assert.assertEquals("List of tasks", 1, results.size());
            Assert.assertEquals("Incorrect task retrieved", tasks[1].getId(), results.get(0).getId());
        }
        {
            // Add one more task, just to make sure things are working wel
            long workItemId = 57;
            long procInstId = 11111;
            String busAdmin = "reviewer";
            String potOwner = "translator";
            String deploymentId = "armitage";
            String name = "Koude Bevel";
            taskImpl = addTask(workItemId, procInstId, busAdmin, potOwner, name, deploymentId);
            procInstIds.add(procInstId);
            workItemIds.add(workItemId);
            busAdmins.add(busAdmin);
            potOwners.add(potOwner);
            Assert.assertEquals(potOwner, taskImpl.getTaskData().getActualOwner().getId());
            taskService.start(taskImpl.getId(), potOwner);
            taskService.fail(taskImpl.getId(), busAdmin, null);
            TaskSummaryQueryBuilder queryBuilder = taskService.taskSummaryQuery(TaskQueryBuilderLocalTest.stakeHolder).maxResults(1);
            List<TaskSummary> results = queryBuilder.build().getResultList();
            Assert.assertEquals("List of tasks", 1, results.size());
            queryBuilder.clear();
            results = queryBuilder.build().getResultList();
            Assert.assertFalse("Empty List of tasks", ((results.isEmpty()) || ((results.size()) == 1)));
            testOrderByTaskIdAscending(results);
            queryBuilder.clear();
            queryBuilder.descending(OrderBy.processInstanceId);
            results = queryBuilder.build().getResultList();
            Assert.assertFalse("List of tasks too small", ((results.isEmpty()) || ((results.size()) == 1)));
            for (int i = 1; i < (results.size()); ++i) {
                Long aVal = results.get((i - 1)).getProcessInstanceId();
                Long bVal = results.get(i).getProcessInstanceId();
                Assert.assertTrue(((("Tasks not correctly ordered: " + aVal) + " ?>? ") + bVal), (aVal > bVal));
            }
            queryBuilder.offset(((results.size()) - 1)).ascending(OrderBy.taskId);
            results = queryBuilder.build().getResultList();
            Assert.assertFalse("Empty List of tasks", results.isEmpty());
            Assert.assertEquals("List of tasks", 1, results.size());
            Assert.assertEquals("Task id", taskImpl.getId(), results.get(0).getId());
        }
        TaskSummaryQueryBuilder queryBuilder = taskService.taskSummaryQuery(TaskQueryBuilderLocalTest.stakeHolder);
        List<TaskSummary> results = queryBuilder.businessAdmin(busAdmins.toArray(new String[busAdmins.size()])).build().getResultList();
        Assert.assertEquals(3, results.size());
        // pagination
        {
            // Add two more tasks, in order to have a quorum
            long workItemId = 59;
            long procInstId = 12111;
            String busAdmin = "Wintermute";
            String potOwner = "molly";
            String deploymentId = "Dixie Flatline";
            String name = "Complete Mission";
            taskImpl = addTask(workItemId, procInstId, busAdmin, potOwner, name, deploymentId);
            // Add two more tasks, in order to have a quorum
            ++workItemId;
            ++procInstId;
            busAdmin = "Neuromancer";
            potOwner = "case";
            deploymentId = "Linda Lee";
            name = "Resurrect";
            taskImpl = addTask(workItemId, procInstId, busAdmin, potOwner, name, deploymentId);
        }
        results = queryBuilder.clear().build().getResultList();
        Assert.assertTrue(("Result list too small to test: " + (results.size())), ((results.size()) == 5));
        results = queryBuilder.clear().offset(1).build().getResultList();
        Assert.assertTrue((("Expected 4, not " + (results.size())) + " results"), ((results.size()) == 4));
        results = queryBuilder.clear().offset(1).maxResults(3).build().getResultList();
        Assert.assertTrue((("Expected 3, not " + (results.size())) + " results"), ((results.size()) == 3));
        results = queryBuilder.clear().offset(3).maxResults(3).build().getResultList();
        Assert.assertTrue((("Expected 2, not " + (results.size())) + " results"), ((results.size()) == 2));
        // pot owner (and no "user-group limiting" clause)
        {
            // Add two more tasks, in order to have a quorum
            long workItemId = 104;
            long procInstId = 1766;
            String busAdmin = TaskQueryBuilderLocalTest.stakeHolder;
            String potOwner = TaskQueryBuilderLocalTest.stakeHolder;
            String deploymentId = "Louis de Ponte du Lac";
            String name = "Interview";
            taskImpl = addTask(workItemId, procInstId, busAdmin, potOwner, name, deploymentId);
        }
        queryBuilder.clear();
        queryBuilder = taskService.taskSummaryQuery(TaskQueryBuilderLocalTest.stakeHolder).intersect().potentialOwner(TaskQueryBuilderLocalTest.stakeHolder);
        results = queryBuilder.build().getResultList();
        Assert.assertEquals("List of tasks", 1, results.size());
    }
}

