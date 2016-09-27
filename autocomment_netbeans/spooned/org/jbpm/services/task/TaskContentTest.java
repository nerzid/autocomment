/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
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
import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import org.kie.internal.task.api.InternalTaskService;
import java.util.Map;
import javax.persistence.Persistence;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import java.io.StringReader;
import org.kie.api.task.model.Task;
import org.jbpm.services.task.impl.factories.TaskFactory;
import org.junit.Test;

public class TaskContentTest extends HumanTaskServicesBaseTest {
    private PoolingDataSource pds;

    private EntityManagerFactory emf;

    @Before
    public void setup() {
        pds = setupPoolingDataSource();
        emf = Persistence.createEntityManagerFactory("org.jbpm.services.task");
        TaskContentTest.this.taskService = ((InternalTaskService) (HumanTaskServiceFactory.newTaskServiceConfigurator().entityManagerFactory(emf).getTaskService()));
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
    public void testTaskContent() throws Exception {
        String userId = "Bobba Fet";
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += ("peopleAssignments = (with ( new PeopleAssignments() ) { businessAdministrators = [new User('" + userId) + "')], }),";
        str += "name =  'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();
        Map<String, Object> outputParams = new HashMap<String, Object>();
        outputParams.put("str", "str");
        outputParams.put("int", new Integer(23));
        try {
            taskService.addOutputContentFromUser(task.getId(), "Jabba Hutt", outputParams);
            Assert.fail("This should not have succeeded (Jabba doesn't have permissions)");
        } catch (Exception e) {
            // do nothing
        }
        long contentId = taskService.addOutputContentFromUser(task.getId(), userId, outputParams);
        Map<String, Object> gotOutputParams = taskService.getOutputContentMapForUser(taskId, userId);
        for (Map.Entry<String, Object> origEntry : outputParams.entrySet()) {
            String key = origEntry.getKey();
            Assert.assertEquals(("Entry: " + key), origEntry.getValue(), gotOutputParams.get(key));
        }
        try {
            taskService.getOutputContentMapForUser(taskId, "Jabba Hutt");
            Assert.fail("This should not have succeeded (Jabba doesn't have permissions)");
        } catch (Exception e) {
            // do nothing
        }
    }
}

