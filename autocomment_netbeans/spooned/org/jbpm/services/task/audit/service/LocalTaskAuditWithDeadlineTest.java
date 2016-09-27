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


package org.jbpm.services.task.audit.service;

import org.junit.After;
import java.util.ArrayList;
import org.junit.Assert;
import org.kie.internal.task.api.AuditTask;
import org.junit.Before;
import org.jbpm.services.task.util.CountDownTaskEventListener;
import java.util.Date;
import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.HumanTaskServicesBaseTest;
import java.io.InputStreamReader;
import org.kie.internal.task.api.model.InternalTask;
import org.kie.internal.task.api.InternalTaskService;
import java.util.List;
import java.util.Map;
import org.kie.api.task.model.OrganizationalEntity;
import javax.persistence.Persistence;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import java.io.Reader;
import org.kie.api.task.model.Task;
import org.jbpm.services.task.audit.TaskAuditServiceFactory;
import org.jbpm.services.task.impl.TaskDeadlinesServiceImpl;
import org.jbpm.services.task.impl.factories.TaskFactory;
import org.junit.Test;

public class LocalTaskAuditWithDeadlineTest extends HumanTaskServicesBaseTest {
    private PoolingDataSource pds;

    private EntityManagerFactory emf;

    protected TaskAuditService taskAuditService;

    @Before
    public void setup() {
        TaskDeadlinesServiceImpl.reset();
        pds = setupPoolingDataSource();
        emf = Persistence.createEntityManagerFactory("org.jbpm.services.task");
        LocalTaskAuditWithDeadlineTest.this.taskService = ((InternalTaskService) (HumanTaskServiceFactory.newTaskServiceConfigurator().entityManagerFactory(emf).listener(new org.jbpm.services.task.audit.JPATaskLifeCycleEventListener(true)).listener(new org.jbpm.services.task.lifecycle.listeners.BAMTaskEventListener(true)).getTaskService()));
        LocalTaskAuditWithDeadlineTest.this.taskAuditService = TaskAuditServiceFactory.newTaskAuditServiceConfigurator().setTaskService(taskService).getTaskAuditService();
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
        taskService.claim(taskId, "Tony Stark");
        task = taskService.getTaskById(taskId);
        List<OrganizationalEntity> potentialOwners = ((List<OrganizationalEntity>) (task.getPeopleAssignments().getPotentialOwners()));
        List<String> ids = new ArrayList<String>(potentialOwners.size());
        for (OrganizationalEntity entity : potentialOwners) {
            ids.add(entity.getId());
        }
        Assert.assertTrue(ids.contains("Tony Stark"));
        Assert.assertTrue(ids.contains("Luke Cage"));
        List<AuditTask> tasks = taskAuditService.getAllAuditTasks(new org.kie.internal.query.QueryFilter());
        Assert.assertEquals(1, tasks.size());
        AuditTask auditTask = tasks.get(0);
        Assert.assertEquals(Status.Reserved.toString(), auditTask.getStatus());
        Assert.assertEquals("Tony Stark", auditTask.getActualOwner());
        // should have re-assigned by now
        countDownListener.waitTillCompleted();
        task = taskService.getTaskById(taskId);
        Assert.assertNull(task.getTaskData().getActualOwner());
        Assert.assertEquals(Status.Ready, task.getTaskData().getStatus());
        potentialOwners = ((List<OrganizationalEntity>) (task.getPeopleAssignments().getPotentialOwners()));
        ids = new ArrayList<String>(potentialOwners.size());
        for (OrganizationalEntity entity : potentialOwners) {
            ids.add(entity.getId());
        }
        Assert.assertTrue(ids.contains("Bobba Fet"));
        Assert.assertTrue(ids.contains("Jabba Hutt"));
        tasks = taskAuditService.getAllAuditTasks(new org.kie.internal.query.QueryFilter());
        Assert.assertEquals(1, tasks.size());
        auditTask = tasks.get(0);
        Assert.assertEquals(Status.Ready.toString(), auditTask.getStatus());
        Assert.assertEquals("", auditTask.getActualOwner());
    }
}

