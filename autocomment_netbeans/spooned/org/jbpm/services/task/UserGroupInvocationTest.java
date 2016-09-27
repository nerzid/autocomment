/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Before;
import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import org.kie.internal.task.api.InternalTaskService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.persistence.Persistence;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import java.io.StringReader;
import org.kie.api.task.model.Task;
import org.jbpm.services.task.impl.factories.TaskFactory;
import org.junit.Test;
import org.kie.api.task.UserGroupCallback;

public class UserGroupInvocationTest extends HumanTaskServicesBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(UserGroupInvocationTest.class);

    private PoolingDataSource pds;

    private EntityManagerFactory emf;

    private UserGroupInvocationTest.CountInvokeUserGroupCallback callback;

    @Before
    public void setup() {
        pds = setupPoolingDataSource();
        emf = Persistence.createEntityManagerFactory("org.jbpm.services.task");
        callback = new UserGroupInvocationTest.CountInvokeUserGroupCallback();
        UserGroupInvocationTest.this.taskService = ((InternalTaskService) (HumanTaskServiceFactory.newTaskServiceConfigurator().entityManagerFactory(emf).userGroupCallback(callback).getTaskService()));
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
    public void testAddStartCompleteUserAssignment() {
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";
        Task task = ((Task) (TaskFactory.evalTask(new StringReader(str))));
        taskService.addTask(task, new HashMap<String, java.lang.Object>());
        UserGroupInvocationTest.logger.debug("Callback invokation {}", callback.getExistsUserCounter());
        UserGroupInvocationTest.logger.debug("Callback invokation {}", callback.getExistsGroupCounter());
        UserGroupInvocationTest.logger.debug("Callback invokation {}", callback.getGetGroupCounter());
        UserGroupInvocationTest.logger.debug("-------------------------");
        Assert.assertEquals(3, callback.getExistsUserCounter());
        Assert.assertEquals(0, callback.getExistsGroupCounter());
        Assert.assertEquals(0, callback.getGetGroupCounter());
        callback.reset();
        long taskId = task.getId();
        taskService.start(taskId, "Darth Vader");
        UserGroupInvocationTest.logger.debug("Callback invokation {}", callback.getExistsUserCounter());
        UserGroupInvocationTest.logger.debug("Callback invokation {}", callback.getExistsGroupCounter());
        UserGroupInvocationTest.logger.debug("Callback invokation {}", callback.getGetGroupCounter());
        UserGroupInvocationTest.logger.debug("-------------------------");
        Assert.assertEquals(1, callback.getExistsUserCounter());
        Assert.assertEquals(0, callback.getExistsGroupCounter());
        Assert.assertEquals(1, callback.getGetGroupCounter());
        callback.reset();
        Task task1 = taskService.getTaskById(taskId);
        Assert.assertEquals(Status.InProgress, task1.getTaskData().getStatus());
        Assert.assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());
        taskService.complete(taskId, "Darth Vader", null);
        UserGroupInvocationTest.logger.debug("Callback invokation {}", callback.getExistsUserCounter());
        UserGroupInvocationTest.logger.debug("Callback invokation {}", callback.getExistsGroupCounter());
        UserGroupInvocationTest.logger.debug("Callback invokation {}", callback.getGetGroupCounter());
        UserGroupInvocationTest.logger.debug("-------------------------");
        Assert.assertEquals(1, callback.getExistsUserCounter());
        Assert.assertEquals(0, callback.getExistsGroupCounter());
        Assert.assertEquals(1, callback.getGetGroupCounter());
        Task task2 = taskService.getTaskById(taskId);
        Assert.assertEquals(Status.Completed, task2.getTaskData().getStatus());
        Assert.assertEquals("Darth Vader", task2.getTaskData().getActualOwner().getId());
    }

    @Test
    public void testAddStartCompleteGroupAssignment() {
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Knights Templer'), new Group('Crusaders') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";
        Task task = ((Task) (TaskFactory.evalTask(new StringReader(str))));
        taskService.addTask(task, new HashMap<String, java.lang.Object>());
        UserGroupInvocationTest.logger.debug("Callback invokation {}", callback.getExistsUserCounter());
        UserGroupInvocationTest.logger.debug("Callback invokation {}", callback.getExistsGroupCounter());
        UserGroupInvocationTest.logger.debug("Callback invokation {}", callback.getGetGroupCounter());
        UserGroupInvocationTest.logger.debug("-------------------------");
        Assert.assertEquals(1, callback.getExistsUserCounter());
        Assert.assertEquals(2, callback.getExistsGroupCounter());
        Assert.assertEquals(0, callback.getGetGroupCounter());
        callback.reset();
        long taskId = task.getId();
        taskService.claim(taskId, "Darth Vader");
        UserGroupInvocationTest.logger.debug("Callback invokation {}", callback.getExistsUserCounter());
        UserGroupInvocationTest.logger.debug("Callback invokation {}", callback.getExistsGroupCounter());
        UserGroupInvocationTest.logger.debug("Callback invokation {}", callback.getGetGroupCounter());
        UserGroupInvocationTest.logger.debug("-------------------------");
        Assert.assertEquals(1, callback.getExistsUserCounter());
        Assert.assertEquals(0, callback.getExistsGroupCounter());
        Assert.assertEquals(1, callback.getGetGroupCounter());
        callback.reset();
        taskService.start(taskId, "Darth Vader");
        UserGroupInvocationTest.logger.debug("Callback invokation {}", callback.getExistsUserCounter());
        UserGroupInvocationTest.logger.debug("Callback invokation {}", callback.getExistsGroupCounter());
        UserGroupInvocationTest.logger.debug("Callback invokation {}", callback.getGetGroupCounter());
        UserGroupInvocationTest.logger.debug("-------------------------");
        Assert.assertEquals(1, callback.getExistsUserCounter());
        Assert.assertEquals(0, callback.getExistsGroupCounter());
        Assert.assertEquals(1, callback.getGetGroupCounter());
        callback.reset();
        Task task1 = taskService.getTaskById(taskId);
        Assert.assertEquals(Status.InProgress, task1.getTaskData().getStatus());
        Assert.assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());
        taskService.complete(taskId, "Darth Vader", null);
        UserGroupInvocationTest.logger.debug("Callback invokation {}", callback.getExistsUserCounter());
        UserGroupInvocationTest.logger.debug("Callback invokation {}", callback.getExistsGroupCounter());
        UserGroupInvocationTest.logger.debug("Callback invokation {}", callback.getGetGroupCounter());
        UserGroupInvocationTest.logger.debug("-------------------------");
        Assert.assertEquals(1, callback.getExistsUserCounter());
        Assert.assertEquals(0, callback.getExistsGroupCounter());
        Assert.assertEquals(1, callback.getGetGroupCounter());
        Task task2 = taskService.getTaskById(taskId);
        Assert.assertEquals(Status.Completed, task2.getTaskData().getStatus());
        Assert.assertEquals("Darth Vader", task2.getTaskData().getActualOwner().getId());
    }

    private class CountInvokeUserGroupCallback implements UserGroupCallback {
        private int existsUserCounter = 0;

        private int existsGroupCounter = 0;

        private int getGroupCounter = 0;

        @Override
        public boolean existsUser(String userId) {
            (existsUserCounter)++;
            return true;
        }

        @Override
        public boolean existsGroup(String groupId) {
            (existsGroupCounter)++;
            return true;
        }

        @Override
        public List<String> getGroupsForUser(String userId, List<String> groupIds, List<String> allExistingGroupIds) {
            (getGroupCounter)++;
            List<String> groups = new ArrayList<String>();
            groups.add("Knights Templer");
            groups.add("Crusaders");
            return groups;
        }

        public int getExistsUserCounter() {
            return existsUserCounter;
        }

        public int getExistsGroupCounter() {
            return existsGroupCounter;
        }

        public int getGetGroupCounter() {
            return getGroupCounter;
        }

        public void reset() {
            UserGroupInvocationTest.CountInvokeUserGroupCallback.this.existsUserCounter = 0;
            UserGroupInvocationTest.CountInvokeUserGroupCallback.this.existsGroupCounter = 0;
            UserGroupInvocationTest.CountInvokeUserGroupCallback.this.getGroupCounter = 0;
        }
    }
}

