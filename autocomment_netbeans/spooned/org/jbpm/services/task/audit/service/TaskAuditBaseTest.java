/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.services.task.audit.service;

import java.util.ArrayList;
import org.junit.Assert;
import org.assertj.core.api.Assertions;
import org.kie.internal.task.api.AuditTask;
import org.jbpm.services.task.audit.impl.model.BAMTaskSummaryImpl;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import org.jbpm.services.task.HumanTaskServicesBaseTest;
import org.kie.api.task.model.I18NText;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import org.kie.internal.query.QueryFilter;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.model.TaskEvent;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.task.api.TaskVariable;
import org.junit.Test;
import java.sql.Timestamp;

public abstract class TaskAuditBaseTest extends HumanTaskServicesBaseTest {
    @Inject
    protected TaskAuditService taskAuditService;

    @Test
    public void testComplete() {
        Task task = new org.jbpm.services.task.utils.TaskFluent().setName("This is my task name").addPotentialGroup("Knights Templer").setAdminUser("Administrator").getTask();
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();
        List<TaskSummary> allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", null, null, null);
        Assert.assertEquals(1, allGroupAuditTasks.size());
        Assert.assertTrue(allGroupAuditTasks.get(0).getStatusId().equals("Ready"));
        taskService.claim(taskId, "Darth Vader");
        allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", null, null, null);
        Assert.assertEquals(0, allGroupAuditTasks.size());
        allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", null, null, null);
        Assert.assertEquals(1, allGroupAuditTasks.size());
        Assert.assertTrue(allGroupAuditTasks.get(0).getStatusId().equals("Reserved"));
        taskService.release(taskId, "Darth Vader");
        allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", null, null, null);
        Assert.assertEquals(1, allGroupAuditTasks.size());
        Assert.assertTrue(allGroupAuditTasks.get(0).getStatusId().equals("Ready"));
        taskService.claim(taskId, "Darth Vader");
        allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", null, null, null);
        Assert.assertEquals(0, allGroupAuditTasks.size());
        allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", null, null, null);
        Assert.assertEquals(1, allGroupAuditTasks.size());
        Assert.assertTrue(allGroupAuditTasks.get(0).getStatusId().equals("Reserved"));
        // Go straight from Ready to Inprogress
        taskService.start(taskId, "Darth Vader");
        Task task1 = taskService.getTaskById(taskId);
        Assert.assertEquals(Status.InProgress, task1.getTaskData().getStatus());
        Assert.assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());
        // Check is Complete
        taskService.complete(taskId, "Darth Vader", null);
        Task task2 = taskService.getTaskById(taskId);
        Assert.assertEquals(Status.Completed, task2.getTaskData().getStatus());
        Assert.assertEquals("Darth Vader", task2.getTaskData().getActualOwner().getId());
        List<TaskEvent> allTaskEvents = taskService.execute(new org.jbpm.services.task.audit.commands.GetAuditEventsCommand(taskId, new QueryFilter(0, 0)));
        Assert.assertEquals(6, allTaskEvents.size());
        // test DeleteAuditEventsCommand
        int numFirstTaskEvents = allTaskEvents.size();
        task = new org.jbpm.services.task.utils.TaskFluent().setName("This is my task name 2").addPotentialGroup("Knights Templer").setAdminUser("Administrator").getTask();
        taskService.addTask(task, new HashMap<String, Object>());
        long secondTaskId = task.getId();
        taskService.claim(secondTaskId, "Darth Vader");
        taskService.start(secondTaskId, "Darth Vader");
        taskService.complete(secondTaskId, "Darth Vader", null);
        allTaskEvents = taskService.execute(new org.jbpm.services.task.audit.commands.GetAuditEventsCommand());
        int numTaskEvents = allTaskEvents.size();
        Assert.assertTrue(((("Expected more than " + numFirstTaskEvents) + " events: ") + numTaskEvents), (numTaskEvents > numFirstTaskEvents));
        taskService.execute(new org.jbpm.services.task.audit.commands.DeleteAuditEventsCommand(taskId));
        allTaskEvents = taskService.execute(new org.jbpm.services.task.audit.commands.GetAuditEventsCommand());
        Assert.assertEquals((numTaskEvents - numFirstTaskEvents), allTaskEvents.size());
        taskService.execute(new org.jbpm.services.task.audit.commands.DeleteAuditEventsCommand());
        allTaskEvents = taskService.execute(new org.jbpm.services.task.audit.commands.GetAuditEventsCommand());
        Assert.assertEquals(0, allTaskEvents.size());
        // test get/delete BAM Task summaries commands
        List<BAMTaskSummaryImpl> bamTaskList = taskService.execute(new org.jbpm.services.task.audit.commands.GetBAMTaskSummariesCommand());
        Assert.assertEquals("BAM Task Summary list size: ", 2, bamTaskList.size());
        taskService.execute(new org.jbpm.services.task.audit.commands.DeleteBAMTaskSummariesCommand(taskId));
        bamTaskList = taskService.execute(new org.jbpm.services.task.audit.commands.GetBAMTaskSummariesCommand());
        Assert.assertEquals((("BAM Task Summary list size after delete (task id: " + taskId) + ") : "), 1, bamTaskList.size());
        bamTaskList = taskService.execute(new org.jbpm.services.task.audit.commands.GetBAMTaskSummariesCommand(secondTaskId));
        Assert.assertEquals((("BAM Task Summary list size after delete (task id: " + taskId) + ") : "), 1, bamTaskList.size());
        taskService.execute(new org.jbpm.services.task.audit.commands.DeleteBAMTaskSummariesCommand());
        bamTaskList = taskService.execute(new org.jbpm.services.task.audit.commands.GetBAMTaskSummariesCommand());
        Assert.assertEquals((("BAM Task Summary list size after delete (task id: " + taskId) + ") : "), 0, bamTaskList.size());
        List<AuditTask> allHistoryAuditTasks = taskAuditService.getAllAuditTasks(new QueryFilter(0, 0));
        Assert.assertEquals(2, allHistoryAuditTasks.size());
    }

    @Test
    public void testOnlyActiveTasks() {
        Task task = new org.jbpm.services.task.utils.TaskFluent().setName("This is my task name").addPotentialUser("salaboy").setAdminUser("Administrator").getTask();
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> allActiveTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", null, null, null);
        Assert.assertEquals(1, allActiveTasks.size());
        Assert.assertTrue(allActiveTasks.get(0).getStatusId().equals("Reserved"));
        QueryFilter queryFilter = new QueryFilter(0, 0);
        Map<String, Object> params = new HashMap<String, Object>();
        List<String> statuses = new ArrayList<String>();
        statuses.add(Status.Reserved.toString());
        params.put("statuses", statuses);
        queryFilter.setParams(params);
        List<AuditTask> allActiveAuditTasksByUser = taskAuditService.getAllAuditTasksByStatus("salaboy", queryFilter);
        Assert.assertEquals(1, allActiveAuditTasksByUser.size());
        Assert.assertTrue(allActiveAuditTasksByUser.get(0).getStatus().equals("Reserved"));
        statuses = new ArrayList<String>();
        statuses.add(Status.Completed.toString());
        params.put("statuses", statuses);
        queryFilter.setParams(params);
        allActiveAuditTasksByUser = taskAuditService.getAllAuditTasksByStatus("salaboy", queryFilter);
        Assert.assertEquals(0, allActiveAuditTasksByUser.size());
    }

    @Test
    public void testGroupTasks() {
        Task task = new org.jbpm.services.task.utils.TaskFluent().setName("This is my task name").addPotentialUser("salaboy").addPotentialUser("krisv").addPotentialGroup("Knights Templer").setAdminUser("Administrator").getTask();
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> allGroupTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", null, null, null);
        Assert.assertEquals(1, allGroupTasks.size());
        Assert.assertTrue(allGroupTasks.get(0).getStatusId().equals("Ready"));
        List<AuditTask> allGroupAuditTasksByUser = taskAuditService.getAllGroupAuditTasksByUser("salaboy", new QueryFilter(0, 0));
        Assert.assertEquals(1, allGroupAuditTasksByUser.size());
        Assert.assertTrue(allGroupAuditTasksByUser.get(0).getStatus().equals("Ready"));
    }

    @Test
    public void testAdminTasks() {
        Task task = new org.jbpm.services.task.utils.TaskFluent().setName("This is my task name").setAdminUser("salaboy").getTask();
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> allAdminTasks = taskService.getTasksAssignedAsBusinessAdministrator("salaboy", null);
        Assert.assertEquals(1, allAdminTasks.size());
        List<AuditTask> allAdminAuditTasksByUser = taskAuditService.getAllAdminAuditTasksByUser("salaboy", new QueryFilter(0, 0));
        Assert.assertEquals(1, allAdminAuditTasksByUser.size());
    }

    @Test
    public void testExitAfterClaim() {
        // One potential owner, should go straight to state Reserved
        Task task = new org.jbpm.services.task.utils.TaskFluent().setName("This is my task name 2").addPotentialGroup("Knights Templer").setAdminUser("Administrator").getTask();
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();
        List<TaskSummary> allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", null, null, null);
        Assert.assertEquals(1, allGroupAuditTasks.size());
        Assert.assertTrue(allGroupAuditTasks.get(0).getStatusId().equals("Ready"));
        taskService.claim(taskId, "Darth Vader");
        allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", null, null, null);
        Assert.assertEquals(0, allGroupAuditTasks.size());
        allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", null, null, null);
        Assert.assertEquals(1, allGroupAuditTasks.size());
        Assert.assertTrue(allGroupAuditTasks.get(0).getStatusId().equals("Reserved"));
        taskService.exit(taskId, "Administrator");
        List<AuditTask> allHistoryAuditTasks = taskAuditService.getAllAuditTasks(new QueryFilter(0, 0));
        Assert.assertEquals(1, allHistoryAuditTasks.size());
        allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", null, null, null);
        Assert.assertEquals(0, allGroupAuditTasks.size());
    }

    @Test
    public void testExitBeforeClaim() {
        Task task = new org.jbpm.services.task.utils.TaskFluent().setName("This is my task name 2").addPotentialGroup("Knights Templer").setAdminUser("Administrator").getTask();
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();
        List<TaskSummary> allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", null, null, null);
        Assert.assertEquals(1, allGroupAuditTasks.size());
        Assert.assertTrue(allGroupAuditTasks.get(0).getStatusId().equals("Ready"));
        taskService.exit(taskId, "Administrator");
        List<AuditTask> allHistoryAuditTasks = taskAuditService.getAllAuditTasks(new QueryFilter(0, 0));
        Assert.assertEquals(1, allHistoryAuditTasks.size());
        allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", null, null, null);
        Assert.assertEquals(0, allGroupAuditTasks.size());
    }

    private void testDescriptionUpdate(String oldDescription, String newDescription, boolean changeExpected) {
        Task task = new org.jbpm.services.task.utils.TaskFluent().setDescription(oldDescription).setAdminUser("Administrator").getTask();
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();
        List<I18NText> descriptions = new ArrayList<I18NText>();
        descriptions.add(new org.jbpm.services.task.impl.model.I18NTextImpl("", newDescription));
        taskService.setDescriptions(taskId, descriptions);
        task = taskService.getTaskById(taskId);
        Assertions.assertThat(task.getDescription()).isEqualTo(newDescription);
        List<AuditTask> auditTasks = taskAuditService.getAllAuditTasks(new QueryFilter());
        Assertions.assertThat(auditTasks).hasSize(1);
        Assertions.assertThat(auditTasks.get(0).getDescription()).isEqualTo(newDescription);
        List<TaskEvent> taskEvents = taskAuditService.getAllTaskEvents(taskId, new QueryFilter());
        if (changeExpected) {
            Assertions.assertThat(taskEvents).hasSize(2);
            Assertions.assertThat(taskEvents.get(1).getMessage()).contains(String.valueOf(oldDescription), String.valueOf(newDescription));
        } else {
            Assertions.assertThat(taskEvents).hasSize(1);
        }
    }

    @Test
    public void testDescriptionUpdateSame() {
        testDescriptionUpdate("description", "description", false);
    }

    @Test
    public void testDescriptionUpdateDifferent() {
        testDescriptionUpdate("old description", "new description", true);
    }

    @Test
    public void testDescriptionUpdateToNull() {
        testDescriptionUpdate("old description", null, true);
    }

    @Test
    public void testDescriptionUpdateToEmpty() {
        testDescriptionUpdate("old description", "", true);
    }

    @Test
    public void testDescriptionUpdateFromNull() {
        testDescriptionUpdate(null, "new description", true);
    }

    @Test
    public void testDescriptionUpdateFromEmpty() {
        testDescriptionUpdate("", "new description", true);
    }

    private void testNameUpdate(String oldName, String newName, boolean changeExpected) {
        Task task = new org.jbpm.services.task.utils.TaskFluent().setName(oldName).setAdminUser("Administrator").getTask();
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();
        List<I18NText> taskNames = new ArrayList<I18NText>();
        taskNames.add(new org.jbpm.services.task.impl.model.I18NTextImpl("", newName));
        taskService.setTaskNames(taskId, taskNames);
        task = taskService.getTaskById(taskId);
        Assertions.assertThat(task.getName()).isEqualTo(newName);
        List<AuditTask> auditTasks = taskAuditService.getAllAuditTasks(new QueryFilter());
        Assertions.assertThat(auditTasks).hasSize(1);
        Assertions.assertThat(auditTasks.get(0).getName()).isEqualTo(newName);
        List<TaskEvent> taskEvents = taskAuditService.getAllTaskEvents(taskId, new QueryFilter());
        if (changeExpected) {
            Assertions.assertThat(taskEvents).hasSize(2);
            Assertions.assertThat(taskEvents.get(1).getMessage()).contains(String.valueOf(oldName), String.valueOf(newName));
        } else {
            Assertions.assertThat(taskEvents).hasSize(1);
        }
    }

    @Test
    public void testNameUpdateSame() {
        testNameUpdate("name", "name", false);
    }

    @Test
    public void testNameUpdateDifferent() {
        testNameUpdate("old name", "new name", true);
    }

    @Test
    public void testNameUpdateToNull() {
        testNameUpdate("old name", null, true);
    }

    @Test
    public void testNameUpdateToEmpty() {
        testNameUpdate("old name", "", true);
    }

    @Test
    public void testNameUpdateFromNull() {
        testNameUpdate(null, "new name", true);
    }

    @Test
    public void testNameUpdateFromEmpty() {
        testNameUpdate("", "new name", true);
    }

    private void testPriorityUpdate(int oldPriority, int newPriority, boolean changeExpected) {
        Task task = new org.jbpm.services.task.utils.TaskFluent().setPriority(oldPriority).setAdminUser("Administrator").getTask();
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();
        taskService.setPriority(taskId, newPriority);
        task = taskService.getTaskById(taskId);
        Assertions.assertThat(task.getPriority()).isEqualTo(newPriority);
        List<AuditTask> auditTasks = taskAuditService.getAllAuditTasks(new QueryFilter());
        Assertions.assertThat(auditTasks).hasSize(1);
        Assertions.assertThat(auditTasks.get(0).getPriority()).isEqualTo(newPriority);
        List<TaskEvent> taskEvents = taskAuditService.getAllTaskEvents(taskId, new QueryFilter());
        if (changeExpected) {
            Assertions.assertThat(taskEvents).hasSize(2);
            Assertions.assertThat(taskEvents.get(1).getMessage()).contains(String.valueOf(oldPriority), String.valueOf(newPriority));
        } else {
            Assertions.assertThat(taskEvents).hasSize(1);
        }
    }

    @Test
    public void testPriorityUpdateSame() {
        testPriorityUpdate(0, 0, false);
    }

    @Test
    public void testPriorityUpdateDifferent() {
        testPriorityUpdate(0, 10, true);
    }

    private void testDueDateUpdate(Date oldDate, Date newDate, boolean changeExpected) {
        Task task = new org.jbpm.services.task.utils.TaskFluent().setDueDate(oldDate).setAdminUser("Administrator").getTask();
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();
        taskService.setExpirationDate(taskId, newDate);
        task = taskService.getTaskById(taskId);
        Assertions.assertThat(task.getTaskData().getExpirationTime()).isEqualTo(newDate);
        List<AuditTask> auditTasks = taskAuditService.getAllAuditTasks(new QueryFilter());
        Assertions.assertThat(auditTasks).hasSize(1);
        Assertions.assertThat(auditTasks.get(0).getDueDate()).isEqualTo(newDate);
        List<TaskEvent> taskEvents = taskAuditService.getAllTaskEvents(taskId, new QueryFilter());
        if (changeExpected) {
            Assertions.assertThat(taskEvents).hasSize(2);
            Assertions.assertThat(taskEvents.get(1).getMessage()).contains(String.valueOf(oldDate), String.valueOf(newDate));
        } else {
            Assertions.assertThat(taskEvents).hasSize(1);
        }
    }

    private Timestamp getToday() {
        return new Timestamp(new Date().getTime());
    }

    private Timestamp getTomorrow() {
        Calendar c = Calendar.getInstance();
        c.setTime(getToday());
        c.add(Calendar.DATE, 1);
        return new Timestamp(c.getTimeInMillis());
    }

    @Test
    public void testDueDateUpdateSame() {
        final Timestamp today = getToday();
        testDueDateUpdate(today, today, false);
    }

    @Test
    public void testDueDateUpdateDifferent() {
        testDueDateUpdate(getToday(), getTomorrow(), true);
    }

    @Test
    public void testDueDateUpdateFromNull() {
        testDueDateUpdate(null, getTomorrow(), true);
    }

    @Test
    public void testDueDateUpdateToNull() {
        testDueDateUpdate(getToday(), null, true);
    }

    @Test
    public void testVariableIndexInputAndOutput() {
        Task task = new org.jbpm.services.task.utils.TaskFluent().setName("This is my task name").addPotentialGroup("Knights Templer").setAdminUser("Administrator").getTask();
        Map<String, Object> inputVariables = new HashMap<String, Object>();
        inputVariables.put("firstVariable", "string content");
        inputVariables.put("number", 1234);
        taskService.addTask(task, inputVariables);
        long taskId = task.getId();
        List<TaskSummary> allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", null, null, null);
        Assert.assertEquals(1, allGroupAuditTasks.size());
        Assert.assertTrue(allGroupAuditTasks.get(0).getStatusId().equals("Ready"));
        List<TaskVariable> inputVars = taskAuditService.taskVariableQuery().taskId(taskId).intersect().type(VariableType.INPUT).build().getResultList();
        Assert.assertNotNull(inputVars);
        Assert.assertEquals(2, inputVars.size());
        Map<String, String> vars = collectVariableNameAndValue(inputVars);
        Assert.assertTrue(vars.containsKey("firstVariable"));
        Assert.assertTrue(vars.containsKey("number"));
        Assert.assertEquals("string content", vars.get("firstVariable"));
        Assert.assertEquals("1234", vars.get("number"));
        taskService.claim(taskId, "Darth Vader");
        allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", null, null, null);
        Assert.assertEquals(0, allGroupAuditTasks.size());
        allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", null, null, null);
        Assert.assertEquals(1, allGroupAuditTasks.size());
        Assert.assertTrue(allGroupAuditTasks.get(0).getStatusId().equals("Reserved"));
        taskService.start(taskId, "Darth Vader");
        Task task1 = taskService.getTaskById(taskId);
        Assert.assertEquals(Status.InProgress, task1.getTaskData().getStatus());
        Assert.assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());
        Map<String, Object> outputVariables = new HashMap<String, Object>();
        outputVariables.put("reply", "updated content");
        outputVariables.put("age", 25);
        // Check is Complete
        taskService.complete(taskId, "Darth Vader", outputVariables);
        List<TaskVariable> outputVars = taskAuditService.taskVariableQuery().taskId(taskId).intersect().type(VariableType.OUTPUT).build().getResultList();
        Assert.assertNotNull(outputVars);
        Assert.assertEquals(2, outputVars.size());
        Map<String, String> outvars = collectVariableNameAndValue(outputVars);
        Assert.assertTrue(outvars.containsKey("reply"));
        Assert.assertTrue(outvars.containsKey("age"));
        Assert.assertEquals("updated content", outvars.get("reply"));
        Assert.assertEquals("25", outvars.get("age"));
    }

    @Test
    public void testVariableIndexInputAndUpdateOutput() {
        Task task = new org.jbpm.services.task.utils.TaskFluent().setName("This is my task name").addPotentialGroup("Knights Templer").setAdminUser("Administrator").getTask();
        Map<String, Object> inputVariables = new HashMap<String, Object>();
        inputVariables.put("firstVariable", "string content");
        inputVariables.put("number", 1234);
        taskService.addTask(task, inputVariables);
        long taskId = task.getId();
        List<TaskSummary> allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", null, null, null);
        Assert.assertEquals(1, allGroupAuditTasks.size());
        Assert.assertTrue(allGroupAuditTasks.get(0).getStatusId().equals("Ready"));
        List<TaskVariable> inputVars = taskAuditService.taskVariableQuery().taskId(taskId).intersect().type(VariableType.INPUT).build().getResultList();
        Assert.assertNotNull(inputVars);
        Assert.assertEquals(2, inputVars.size());
        Map<String, String> vars = collectVariableNameAndValue(inputVars);
        Assert.assertTrue(vars.containsKey("firstVariable"));
        Assert.assertTrue(vars.containsKey("number"));
        Assert.assertEquals("string content", vars.get("firstVariable"));
        Assert.assertEquals("1234", vars.get("number"));
        taskService.claim(taskId, "Darth Vader");
        allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", null, null, null);
        Assert.assertEquals(0, allGroupAuditTasks.size());
        allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", null, null, null);
        Assert.assertEquals(1, allGroupAuditTasks.size());
        Assert.assertTrue(allGroupAuditTasks.get(0).getStatusId().equals("Reserved"));
        taskService.start(taskId, "Darth Vader");
        Task task1 = taskService.getTaskById(taskId);
        Assert.assertEquals(Status.InProgress, task1.getTaskData().getStatus());
        Assert.assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());
        // update task output
        Map<String, Object> outputVariables = new HashMap<String, Object>();
        outputVariables.put("reply", "updated content");
        outputVariables.put("age", 25);
        taskService.addOutputContentFromUser(taskId, "Darth Vader", outputVariables);
        List<TaskVariable> outputVars = taskAuditService.taskVariableQuery().taskId(taskId).intersect().type(VariableType.OUTPUT).build().getResultList();
        Assert.assertNotNull(outputVars);
        Assert.assertEquals(2, outputVars.size());
        Map<String, String> outvars = collectVariableNameAndValue(outputVars);
        Assert.assertTrue(outvars.containsKey("reply"));
        Assert.assertTrue(outvars.containsKey("age"));
        Assert.assertEquals("updated content", outvars.get("reply"));
        Assert.assertEquals("25", outvars.get("age"));
        // Check is Complete
        outputVariables = new HashMap<String, Object>();
        outputVariables.put("reply", "completed content");
        outputVariables.put("age", 44);
        outputVariables.put("reason", "rework, please");
        taskService.complete(taskId, "Darth Vader", outputVariables);
        outputVars = taskAuditService.taskVariableQuery().taskId(taskId).intersect().type(VariableType.OUTPUT).build().getResultList();
        Assert.assertNotNull(outputVars);
        Assert.assertEquals(3, outputVars.size());
        outvars = collectVariableNameAndValue(outputVars);
        Assert.assertTrue(outvars.containsKey("reply"));
        Assert.assertTrue(outvars.containsKey("age"));
        Assert.assertTrue(outvars.containsKey("reason"));
        Assert.assertEquals("completed content", outvars.get("reply"));
        Assert.assertEquals("44", outvars.get("age"));
        Assert.assertEquals("rework, please", outvars.get("reason"));
    }

    @Test
    public void testVariableIndexInputAndOutputWithCustomIdexer() {
        Task task = new org.jbpm.services.task.utils.TaskFluent().setName("This is my task name").addPotentialGroup("Knights Templer").setAdminUser("Administrator").getTask();
        Map<String, Object> inputVariables = new HashMap<String, Object>();
        inputVariables.put("firstVariable", "string content");
        inputVariables.put("person", new org.jbpm.services.task.audit.service.objects.Person("john", 25));
        taskService.addTask(task, inputVariables);
        long taskId = task.getId();
        List<TaskSummary> allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", null, null, null);
        Assert.assertEquals(1, allGroupAuditTasks.size());
        Assert.assertTrue(allGroupAuditTasks.get(0).getStatusId().equals("Ready"));
        List<TaskVariable> inputVars = taskAuditService.taskVariableQuery().taskId(taskId).intersect().type(VariableType.INPUT).build().getResultList();
        Assert.assertNotNull(inputVars);
        Assert.assertEquals(3, inputVars.size());
        Map<String, String> vars = collectVariableNameAndValue(inputVars);
        Assert.assertTrue(vars.containsKey("firstVariable"));
        Assert.assertTrue(vars.containsKey("person.name"));
        Assert.assertTrue(vars.containsKey("person.age"));
        Assert.assertEquals("string content", vars.get("firstVariable"));
        Assert.assertEquals("john", vars.get("person.name"));
        Assert.assertEquals("25", vars.get("person.age"));
        taskService.claim(taskId, "Darth Vader");
        allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", null, null, null);
        Assert.assertEquals(0, allGroupAuditTasks.size());
        allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", null, null, null);
        Assert.assertEquals(1, allGroupAuditTasks.size());
        Assert.assertTrue(allGroupAuditTasks.get(0).getStatusId().equals("Reserved"));
        taskService.start(taskId, "Darth Vader");
        Task task1 = taskService.getTaskById(taskId);
        Assert.assertEquals(Status.InProgress, task1.getTaskData().getStatus());
        Assert.assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());
        Map<String, Object> outputVariables = new HashMap<String, Object>();
        outputVariables.put("reply", "updated content");
        outputVariables.put("person", new org.jbpm.services.task.audit.service.objects.Person("mary", 28));
        // Check is Complete
        taskService.complete(taskId, "Darth Vader", outputVariables);
        List<TaskVariable> outputVars = taskAuditService.taskVariableQuery().taskId(taskId).intersect().type(VariableType.OUTPUT).build().getResultList();
        Assert.assertNotNull(outputVars);
        Assert.assertEquals(3, outputVars.size());
        Map<String, String> outvars = collectVariableNameAndValue(outputVars);
        Assert.assertTrue(outvars.containsKey("reply"));
        Assert.assertTrue(vars.containsKey("person.name"));
        Assert.assertTrue(vars.containsKey("person.age"));
        Assert.assertEquals("updated content", outvars.get("reply"));
        Assert.assertEquals("mary", outvars.get("person.name"));
        Assert.assertEquals("28", outvars.get("person.age"));
    }

    @Test
    public void testSearchTasksByVariable() {
        Task task = new org.jbpm.services.task.utils.TaskFluent().setName("This is my task name").addPotentialGroup("Knights Templer").setAdminUser("Administrator").getTask();
        Map<String, Object> inputVariables = new HashMap<String, Object>();
        inputVariables.put("firstVariable", "string content");
        inputVariables.put("number", 1234);
        taskService.addTask(task, inputVariables);
        long taskId = task.getId();
        List<TaskSummary> allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", null, null, null);
        Assert.assertEquals(1, allGroupAuditTasks.size());
        Assert.assertTrue(allGroupAuditTasks.get(0).getStatusId().equals("Ready"));
        List<TaskVariable> inputVars = taskAuditService.taskVariableQuery().taskId(taskId).intersect().type(VariableType.INPUT).build().getResultList();
        Assert.assertNotNull(inputVars);
        Assert.assertEquals(2, inputVars.size());
        Map<String, String> vars = collectVariableNameAndValue(inputVars);
        Assert.assertTrue(vars.containsKey("firstVariable"));
        Assert.assertTrue(vars.containsKey("number"));
        Assert.assertEquals("string content", vars.get("firstVariable"));
        Assert.assertEquals("1234", vars.get("number"));
        List<TaskSummary> tasksByVariable = taskService.taskSummaryQuery("salaboy").variableName("firstVariable").build().getResultList();
        Assert.assertNotNull(tasksByVariable);
        Assert.assertEquals(1, tasksByVariable.size());
        // search by unauthorized user
        tasksByVariable = taskService.taskSummaryQuery("WinterMute").variableName("fistVariable").build().getResultList();
        Assert.assertNotNull(tasksByVariable);
        Assert.assertEquals(0, tasksByVariable.size());
        // search by not existing variable
        tasksByVariable = taskService.taskSummaryQuery("salaboy").variableName("notexistingVariable").build().getResultList();
        Assert.assertNotNull(tasksByVariable);
        Assert.assertEquals(0, tasksByVariable.size());
        // search by variable name with wildcard
        tasksByVariable = taskService.taskSummaryQuery("salaboy").regex().variableName("first*").build().getResultList();
        Assert.assertNotNull(tasksByVariable);
        Assert.assertNotNull(tasksByVariable);
        Assert.assertEquals(1, tasksByVariable.size());
    }

    @Test
    public void testSearchTasksByVariableNameAndValue() {
        Task task = new org.jbpm.services.task.utils.TaskFluent().setName("This is my task name").addPotentialGroup("Knights Templer").setAdminUser("Administrator").getTask();
        Map<String, Object> inputVariables = new HashMap<String, Object>();
        String userId = "salaboy";
        String varName = "firstVariable";
        String varValue = "string content";
        inputVariables.put(varName, varValue);
        inputVariables.put("number", 1234);
        taskService.addTask(task, inputVariables);
        long taskId = task.getId();
        List<TaskSummary> allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner(userId, null, null, null);
        Assert.assertEquals(1, allGroupAuditTasks.size());
        Assert.assertTrue(allGroupAuditTasks.get(0).getStatusId().equals("Ready"));
        List<TaskVariable> inputVars = taskAuditService.taskVariableQuery().taskId(taskId).build().getResultList();
        Assert.assertNotNull(inputVars);
        Assert.assertEquals(2, inputVars.size());
        Map<String, String> vars = collectVariableNameAndValue(inputVars);
        Assert.assertTrue(vars.containsKey(varName));
        Assert.assertTrue(vars.containsKey("number"));
        Assert.assertEquals(varValue, vars.get(varName));
        Assert.assertEquals("1234", vars.get("number"));
        List<TaskSummary> tasksByVariable = taskService.taskSummaryQuery(userId).variableName(varName).and().variableValue(varValue).build().getResultList();
        Assert.assertNotNull(tasksByVariable);
        Assert.assertEquals(1, tasksByVariable.size());
        // search with value wild card
        tasksByVariable = taskService.taskSummaryQuery(userId).variableName(varName).and().regex().variableValue("string*").build().getResultList();
        Assert.assertNotNull(tasksByVariable);
        Assert.assertEquals(1, tasksByVariable.size());
        // search with name and value wild card
        tasksByVariable = taskService.taskSummaryQuery(userId).regex().variableName("first*").and().variableValue("string*").build().getResultList();
        Assert.assertNotNull(tasksByVariable);
        Assert.assertEquals(1, tasksByVariable.size());
        // search with unauthorized user
        tasksByVariable = taskService.taskSummaryQuery("WinterMute").regex().variableName(varName).and().variableValue(varValue).build().getResultList();
        Assert.assertNotNull(tasksByVariable);
        Assert.assertEquals(0, tasksByVariable.size());
        // search with non existing variable
        tasksByVariable = taskService.taskSummaryQuery(userId).regex().variableName("nonexistingvariable").and().variableValue(varValue).build().getResultList();
        Assert.assertNotNull(tasksByVariable);
        Assert.assertEquals(0, tasksByVariable.size());
        // search with not matching value
        tasksByVariable = taskService.taskSummaryQuery(userId).regex().variableName(varName).and().variableValue("updated content").build().getResultList();
        Assert.assertNotNull(tasksByVariable);
        Assert.assertEquals(0, tasksByVariable.size());
    }

    @Test
    public void testVariableIndexInputAndOutputWitlLongText() {
        Task task = new org.jbpm.services.task.utils.TaskFluent().setName("This is my task name").addPotentialGroup("Knights Templer").setAdminUser("Administrator").getTask();
        Map<String, Object> inputVariables = new HashMap<String, Object>();
        inputVariables.put("firstVariable", "string content");
        inputVariables.put("number", 1234);
        taskService.addTask(task, inputVariables);
        long taskId = task.getId();
        List<TaskSummary> allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", null, null, null);
        Assert.assertEquals(1, allGroupAuditTasks.size());
        Assert.assertTrue(allGroupAuditTasks.get(0).getStatusId().equals("Ready"));
        List<TaskVariable> inputVars = taskAuditService.taskVariableQuery().taskId(taskId).intersect().type(VariableType.INPUT).build().getResultList();
        Assert.assertNotNull(inputVars);
        Assert.assertEquals(2, inputVars.size());
        Map<String, String> vars = collectVariableNameAndValue(inputVars);
        Assert.assertTrue(vars.containsKey("firstVariable"));
        Assert.assertTrue(vars.containsKey("number"));
        Assert.assertEquals("string content", vars.get("firstVariable"));
        Assert.assertEquals("1234", vars.get("number"));
        taskService.claim(taskId, "Darth Vader");
        allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", null, null, null);
        Assert.assertEquals(0, allGroupAuditTasks.size());
        allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", null, null, null);
        Assert.assertEquals(1, allGroupAuditTasks.size());
        Assert.assertTrue(allGroupAuditTasks.get(0).getStatusId().equals("Reserved"));
        taskService.start(taskId, "Darth Vader");
        Task task1 = taskService.getTaskById(taskId);
        Assert.assertEquals(Status.InProgress, task1.getTaskData().getStatus());
        Assert.assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());
        String reply = "Just a short part of the reply";
        String veryLongReply = reply;
        for (int i = 0; i < 15; i++) {
            veryLongReply += reply;
        }
        Map<String, Object> outputVariables = new HashMap<String, Object>();
        outputVariables.put("reply", veryLongReply);
        outputVariables.put("age", 25);
        // Check is Complete
        taskService.complete(taskId, "Darth Vader", outputVariables);
        List<TaskVariable> outputVars = taskAuditService.taskVariableQuery().taskId(taskId).intersect().type(VariableType.OUTPUT).build().getResultList();
        Assert.assertNotNull(outputVars);
        Assert.assertEquals(2, outputVars.size());
        Map<String, String> outvars = collectVariableNameAndValue(outputVars);
        Assert.assertTrue(outvars.containsKey("reply"));
        Assert.assertTrue(outvars.containsKey("age"));
        Assert.assertEquals(veryLongReply, outvars.get("reply"));
        Assert.assertEquals("25", outvars.get("age"));
    }

    @Test
    public void testVariableIndexInputAndOutputWitlLongTextTrimmed() {
        System.setProperty("org.jbpm.task.var.log.length", "10");
        try {
            Task task = new org.jbpm.services.task.utils.TaskFluent().setName("This is my task name").addPotentialGroup("Knights Templer").setAdminUser("Administrator").getTask();
            Map<String, Object> inputVariables = new HashMap<String, Object>();
            inputVariables.put("firstVariable", "string content");
            inputVariables.put("number", 1234);
            taskService.addTask(task, inputVariables);
            long taskId = task.getId();
            List<TaskSummary> allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", null, null, null);
            Assert.assertEquals(1, allGroupAuditTasks.size());
            Assert.assertTrue(allGroupAuditTasks.get(0).getStatusId().equals("Ready"));
            List<TaskVariable> inputVars = taskAuditService.taskVariableQuery().taskId(taskId).intersect().type(VariableType.INPUT).build().getResultList();
            Assert.assertNotNull(inputVars);
            Assert.assertEquals(2, inputVars.size());
            Map<String, String> vars = collectVariableNameAndValue(inputVars);
            Assert.assertTrue(vars.containsKey("firstVariable"));
            Assert.assertTrue(vars.containsKey("number"));
            // the variable was longer that 10 so it had to be trimmed
            Assert.assertEquals("string con", vars.get("firstVariable"));
            Assert.assertEquals("1234", vars.get("number"));
            taskService.claim(taskId, "Darth Vader");
            allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", null, null, null);
            Assert.assertEquals(0, allGroupAuditTasks.size());
            allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", null, null, null);
            Assert.assertEquals(1, allGroupAuditTasks.size());
            Assert.assertTrue(allGroupAuditTasks.get(0).getStatusId().equals("Reserved"));
            taskService.start(taskId, "Darth Vader");
            Task task1 = taskService.getTaskById(taskId);
            Assert.assertEquals(Status.InProgress, task1.getTaskData().getStatus());
            Assert.assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());
            String reply = "Just a short part of the reply";
            String veryLongReply = reply;
            for (int i = 0; i < 15; i++) {
                veryLongReply += reply;
            }
            Map<String, Object> outputVariables = new HashMap<String, Object>();
            outputVariables.put("reply", veryLongReply);
            outputVariables.put("age", 25);
            // Check is Complete
            taskService.complete(taskId, "Darth Vader", outputVariables);
            List<TaskVariable> outputVars = taskAuditService.taskVariableQuery().taskId(taskId).intersect().type(VariableType.OUTPUT).build().getResultList();
            Assert.assertNotNull(outputVars);
            Assert.assertEquals(2, outputVars.size());
            Map<String, String> outvars = collectVariableNameAndValue(outputVars);
            Assert.assertTrue(outvars.containsKey("reply"));
            Assert.assertTrue(outvars.containsKey("age"));
            Assert.assertEquals("Just a sho", outvars.get("reply"));
            Assert.assertEquals("25", outvars.get("age"));
        } finally {
            System.clearProperty("org.jbpm.task.var.log.length");
        }
    }

    protected Map<String, String> collectVariableNameAndValue(List<TaskVariable> variables) {
        Map<String, String> nameValue = new HashMap<String, String>();
        for (TaskVariable taskVar : variables) {
            nameValue.put(taskVar.getName(), taskVar.getValue());
        }
        return nameValue;
    }
}

