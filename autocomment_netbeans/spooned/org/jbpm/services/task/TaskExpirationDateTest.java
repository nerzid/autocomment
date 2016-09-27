/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
import org.junit.Assert;
import org.junit.Before;
import java.util.Date;
import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import org.kie.internal.task.api.InternalTaskService;
import java.util.List;
import javax.persistence.Persistence;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import java.io.StringReader;
import org.kie.api.task.model.Task;
import org.jbpm.services.task.impl.factories.TaskFactory;
import org.kie.api.task.model.TaskSummary;
import org.junit.Test;

public class TaskExpirationDateTest extends HumanTaskServicesBaseTest {
    private PoolingDataSource pds;

    private EntityManagerFactory emf;

    @Before
    public void setup() {
        pds = setupPoolingDataSource();
        emf = Persistence.createEntityManagerFactory("org.jbpm.services.task");
        TaskExpirationDateTest.this.taskService = ((InternalTaskService) (HumanTaskServiceFactory.newTaskServiceConfigurator().entityManagerFactory(emf).getTaskService()));
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

    @Test
    public void testExpirationDate() {
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { businessAdministrators = [new User('Bobba Fet')], }),";
        str += "name =  'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, java.lang.Object>());
        List<TaskSummary> tasks = taskService.getTasksAssignedAsBusinessAdministrator("Bobba Fet", "en-UK");
        Assert.assertEquals(1, tasks.size());
        TaskSummary taskSum = tasks.get(0);
        Date exDate = new Date();
        Assert.assertNull(taskService.getExpirationDate(taskSum.getId()));
        taskService.setExpirationDate(taskSum.getId(), exDate);
        Date date = taskService.getExpirationDate(taskSum.getId());
        Assert.assertNotNull(date);
        Assert.assertEquals(exDate, date);
    }
}

