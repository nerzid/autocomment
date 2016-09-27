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


package org.jbpm.services.task;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.jbpm.services.task.exception.CannotAddTaskException;
import org.kie.internal.task.api.model.ContentData;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import java.util.List;
import java.util.Map;
import org.jbpm.services.task.exception.PermissionDeniedException;
import javax.persistence.Persistence;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import java.util.Properties;
import org.kie.api.io.Resource;
import org.kie.internal.io.ResourceFactory;
import org.jbpm.services.task.rule.RuleContextProvider;
import org.jbpm.services.task.rule.impl.RuleContextProviderImpl;
import java.io.StringReader;
import org.kie.api.task.model.Task;
import org.jbpm.services.task.impl.factories.TaskFactory;
import org.kie.api.task.model.TaskSummary;
import org.junit.Test;

public class LifeCycleLocalWithRuleServiceTest extends HumanTaskServicesBaseTest {
    private PoolingDataSource pds;

    private EntityManagerFactory emf;

    @BeforeClass
    public static void setupOnce() {
        RuleContextProvider ruleContextProvider = RuleContextProviderImpl.get();
        Map<String, Object> addTaskGlobals = ruleContextProvider.getGlobals(TaskRuleService.ADD_TASK_SCOPE);
        if (addTaskGlobals == null) {
            addTaskGlobals = new HashMap<String, Object>();
            addTaskGlobals.put("assignmentService", new org.jbpm.services.task.internals.rule.AssignmentService());
            ruleContextProvider.addGlobals(TaskRuleService.ADD_TASK_SCOPE, addTaskGlobals);
        } 
        try {
            Resource addTask = ResourceFactory.newClassPathResource("simple-add-task-rules.drl");
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            kbuilder.add(addTask, ResourceType.DRL);
            ruleContextProvider.addKieBase(TaskRuleService.ADD_TASK_SCOPE, kbuilder.newKnowledgeBase());
        } catch (Exception e) {
        }
        try {
            Resource completeTask = ResourceFactory.newClassPathResource("simple-complete-task-rules.drl");
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            kbuilder.add(completeTask, ResourceType.DRL);
            ruleContextProvider.addKieBase(TaskRuleService.COMPLETE_TASK_SCOPE, kbuilder.newKnowledgeBase());
        } catch (Exception e) {
        }
    }

    @AfterClass
    public static void clear() {
        RuleContextProvider ruleContextProvider = RuleContextProviderImpl.get();
        Map<String, Object> addTaskGlobals = ruleContextProvider.getGlobals(TaskRuleService.ADD_TASK_SCOPE);
        addTaskGlobals.clear();
        ruleContextProvider.addKieBase(TaskRuleService.ADD_TASK_SCOPE, null);
        ruleContextProvider.addKieBase(TaskRuleService.COMPLETE_TASK_SCOPE, null);
    }

    @Before
    public void setup() {
        pds = setupPoolingDataSource();
        emf = Persistence.createEntityManagerFactory("org.jbpm.services.task");
        LifeCycleLocalWithRuleServiceTest.this.taskService = ((InternalTaskService) (HumanTaskServiceFactory.newTaskServiceConfigurator().entityManagerFactory(emf).getTaskService()));
    }

    @After
    public void clean() {
        if ((emf) != null) {
            emf.close();
        } 
        if ((pds) != null) {
            pds.close();
        } 
    }

    @Test
    public void testCreateTaskWithExcludedActorByRule() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { workItemId = 1 } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('john')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "description =  'This is my description', ";
        str += "subject = 'This is my subject', ";
        str += "name =  'This is my task name' })";
        Task task = ((Task) (TaskFactory.evalTask(new StringReader(str))));
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getTasksOwned("john", "en-UK");
        Assert.assertNotNull(tasks);
        Assert.assertEquals(0, tasks.size());
    }

    @Test
    public void testCreateTaskWithAutoAssignActorByRule() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { workItemId = 1 } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('john'), new User('mary'),new User('krisv')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "description = 'This is my description', ";
        str += "subject =  'This is my subject', ";
        str += "name =  'This is my task name' })";
        Task task = ((Task) (TaskFactory.evalTask(new StringReader(str))));
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getTasksOwned("mary", "en-UK");
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());
        Assert.assertEquals("mary", tasks.get(0).getActualOwnerId());
        Assert.assertEquals(Status.Reserved, tasks.get(0).getStatus());
    }

    @Test
    public void testCreateTaskWithDisallowedCreationByRule() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { workItemId = 1 } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('peter')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "descriptions = [ new I18NText( 'en-UK', 'This is my description')], ";
        str += "subjects = [ new I18NText( 'en-UK', 'This is my subject')], ";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = ((Task) (TaskFactory.evalTask(new StringReader(str))));
        try {
            taskService.addTask(task, new HashMap<String, Object>());
            Assert.fail("Task should not be created due to rule violation");
        } catch (CannotAddTaskException e) {
            Assert.assertTrue(((e.getMessage().indexOf("peter does not work here any more")) != (-1)));
        }
    }

    @Test
    public void testCreateTaskWithDisallowedCreationBasedOnContentByRule() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { workItemId = 1 } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('john')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "descriptions = [ new I18NText( 'en-UK', 'This is my description')], ";
        str += "subjects = [ new I18NText( 'en-UK', 'This is my subject')], ";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = ((Task) (TaskFactory.evalTask(new StringReader(str))));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("manager", "John");
        try {
            taskService.addTask(task, params);
            Assert.fail("Task should not be created due to rule violation");
        } catch (CannotAddTaskException e) {
            Assert.assertTrue(((e.getMessage().indexOf("John (manager) does not work here any more")) != (-1)));
        }
    }

    @Test
    public void testCreateTaskWithDisallowedCreationBasedOnContentByRuleWithContentData() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { workItemId = 1 } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('john')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "descriptions = [ new I18NText( 'en-UK', 'This is my description')], ";
        str += "subjects = [ new I18NText( 'en-UK', 'This is my subject')], ";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = ((Task) (TaskFactory.evalTask(new StringReader(str))));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("manager", "John");
        ContentData data = ContentMarshallerHelper.marshal(task, params, null);
        try {
            taskService.addTask(task, data);
            Assert.fail("Task should not be created due to rule violation");
        } catch (CannotAddTaskException e) {
            Assert.assertTrue(((e.getMessage().indexOf("John (manager) does not work here any more")) != (-1)));
        }
    }

    @Test
    public void testCreateTaskWithAssignByServiceByRule() {
        Properties userGroups = new Properties();
        userGroups.setProperty("john", "Crusaders");
        userGroups.setProperty("Administrator", "BA");
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { workItemId = 1 } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [], businessAdministrators = [ new User('Administrator') ], }),";
        str += "description = 'This is my description', ";
        str += "subject =  'This is my subject', ";
        str += "name =  'This is my task name' })";
        Task task = ((Task) (TaskFactory.evalTask(new StringReader(str))));
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());
        Assert.assertEquals(Status.Ready, tasks.get(0).getStatus());
    }

    @Test
    public void testCompleteTaskWithCheckByRule() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { workItemId = 1 } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('mary')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "description =  'This is my description', ";
        str += "subject = 'This is my subject', ";
        str += "name = 'This is my task name' })";
        Task task = ((Task) (TaskFactory.evalTask(new StringReader(str))));
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getTasksOwned("mary", "en-UK");
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());
        long taskId = tasks.get(0).getId();
        taskService.start(taskId, "mary");
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("approved", "false");
        try {
            taskService.complete(taskId, "mary", data);
            Assert.fail("Task should not be created due to rule violation");
        } catch (PermissionDeniedException e) {
            Assert.assertTrue(((e.getMessage().indexOf("Mary is not allowed to complete task with approved false")) != (-1)));
        }
    }
}

