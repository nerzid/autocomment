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
/**
 * import org.jbpm.process.instance.impl.util.LoggingPrintStream;
 */


package org.jbpm.services.task.audit.service;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.kie.internal.task.api.AuditTask;
import org.kie.internal.task.query.AuditTaskDeleteBuilder;
import org.kie.internal.task.query.AuditTaskQueryBuilder;
import org.junit.Before;
import org.junit.BeforeClass;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.EntityManagerFactory;
import org.kie.api.runtime.EnvironmentName;
import java.util.GregorianCalendar;
import java.util.HashMap;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.kie.internal.task.api.InternalTaskService;
import java.util.List;
import org.jbpm.process.instance.impl.util.LoggingPrintStream;
import org.jbpm.persistence.util.PersistenceUtil;
import java.util.Random;
import org.kie.api.task.model.Task;
import org.junit.Test;

public class AuditTaskDeleteTest extends TaskJPAAuditService {
    private static HashMap<String, Object> context;

    private static EntityManagerFactory emf;

    private Task[] taskTestData;

    @BeforeClass
    public static void configure() {
        LoggingPrintStream.interceptSysOutSysErr();
    }

    @AfterClass
    public static void reset() {
        LoggingPrintStream.resetInterceptSysOutSysErr();
    }

    @Before
    public void setUp() throws Exception {
        AuditTaskDeleteTest.context = PersistenceUtil.setupWithPoolingDataSource("org.jbpm.services.task", "jdbc/jbpm-ds");
        AuditTaskDeleteTest.emf = ((EntityManagerFactory) (AuditTaskDeleteTest.context.get(EnvironmentName.ENTITY_MANAGER_FACTORY)));
        AuditTaskDeleteTest.this.persistenceStrategy = new org.jbpm.process.audit.strategy.StandaloneJtaStrategy(AuditTaskDeleteTest.emf);
        produceTaskInstances();
    }

    @After
    public void cleanup() {
        PersistenceUtil.cleanUp(AuditTaskDeleteTest.context);
    }

    private static Random random = new Random();

    private Calendar randomCal() {
        Calendar cal = GregorianCalendar.getInstance();
        cal.roll(Calendar.DAY_OF_YEAR, ((-1) * (AuditTaskDeleteTest.random.nextInt((10 * 365)))));
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        return cal;
    }

    private void produceTaskInstances() {
        InternalTaskService taskService = ((InternalTaskService) (HumanTaskServiceFactory.newTaskServiceConfigurator().entityManagerFactory(AuditTaskDeleteTest.emf).listener(new org.jbpm.services.task.audit.JPATaskLifeCycleEventListener(true)).listener(new org.jbpm.services.task.lifecycle.listeners.BAMTaskEventListener(true)).getTaskService()));
        Calendar cal = randomCal();
        String processId = "process";
        taskTestData = new Task[10];
        for (int i = 0; i < 10; i++) {
            cal.add(Calendar.HOUR_OF_DAY, 1);
            Task task = new org.jbpm.services.task.utils.TaskFluent().setName("This is my task name").addPotentialGroup("Knights Templer").setAdminUser("Administrator").setProcessId((processId + i)).setCreatedOn(cal.getTime()).getTask();
            taskService.addTask(task, new HashMap<String, Object>());
            taskTestData[i] = task;
        }
    }

    @Test
    public void testDeleteAuditTaskInfoLogByProcessId() {
        int p = 0;
        String processId = taskTestData[(p++)].getTaskData().getProcessId();
        String processId2 = taskTestData[(p++)].getTaskData().getProcessId();
        AuditTaskDeleteBuilder updateBuilder = AuditTaskDeleteTest.this.auditTaskDelete().processId(processId, processId2);
        int result = updateBuilder.build().execute();
        Assert.assertEquals(2, result);
    }

    @Test
    public void testDeleteAuditTaskInfoLogByDate() {
        int p = 0;
        Date endDate = taskTestData[(p++)].getTaskData().getCreatedOn();
        AuditTaskDeleteBuilder updateBuilder = AuditTaskDeleteTest.this.auditTaskDelete().date(endDate);
        int result = updateBuilder.build().execute();
        Assert.assertEquals(1, result);
    }

    @Test
    public void testDeleteAuditTaskInfoLogByProcessIdAndDate() {
        int p = 0;
        String processId = taskTestData[p].getTaskData().getProcessId();
        Date endDate = taskTestData[p].getTaskData().getCreatedOn();
        AuditTaskDeleteBuilder updateBuilder = AuditTaskDeleteTest.this.auditTaskDelete().date(endDate).processId(processId);
        int result = updateBuilder.build().execute();
        Assert.assertEquals(1, result);
    }

    @Test
    public void testDeleteAuditTaskInfoLogByProcessIdAndNotMatchingDate() {
        int p = 0;
        String processId = taskTestData[(p++)].getTaskData().getProcessId();
        Date endDate = taskTestData[(p++)].getTaskData().getCreatedOn();
        AuditTaskDeleteBuilder updateBuilder = AuditTaskDeleteTest.this.auditTaskDelete().date(endDate).processId(processId);
        int result = updateBuilder.build().execute();
        Assert.assertEquals(0, result);
    }

    @Test
    public void testDeleteAuditTaskInfoLogByDateRangeEnd() {
        Date endDate = taskTestData[4].getTaskData().getCreatedOn();
        AuditTaskDeleteBuilder updateBuilder = AuditTaskDeleteTest.this.auditTaskDelete().dateRangeEnd(endDate);
        int result = updateBuilder.build().execute();
        Assert.assertEquals(5, result);
    }

    @Test
    public void testDeleteAuditTaskInfoLogByDateRangeStart() {
        Date endDate = taskTestData[8].getTaskData().getCreatedOn();
        AuditTaskDeleteBuilder updateBuilder = AuditTaskDeleteTest.this.auditTaskDelete().dateRangeStart(endDate);
        int result = updateBuilder.build().execute();
        Assert.assertEquals(2, result);
    }

    @Test
    public void testDeleteAuditTaskInfoLogByDateRange() {
        Date startDate = taskTestData[4].getTaskData().getCreatedOn();
        Date endDate = taskTestData[8].getTaskData().getCreatedOn();
        AuditTaskDeleteBuilder updateBuilder = AuditTaskDeleteTest.this.auditTaskDelete().dateRangeStart(startDate).dateRangeEnd(endDate);
        int result = updateBuilder.build().execute();
        Assert.assertEquals(5, result);
    }

    @Test
    public void testTaskAuditServiceClear() {
        AuditTaskQueryBuilder queryBuilder = AuditTaskDeleteTest.this.auditTaskQuery();
        List<AuditTask> tasks = queryBuilder.taskId(taskTestData[4].getId()).build().getResultList();
        Assert.assertEquals(1, tasks.size());
        queryBuilder.clear();
        List<AuditTask> data = AuditTaskDeleteTest.this.auditTaskQuery().build().getResultList();
        Assert.assertEquals(10, data.size());
        AuditTaskDeleteTest.this.clear();
        data = AuditTaskDeleteTest.this.auditTaskQuery().build().getResultList();
        Assert.assertEquals(0, data.size());
    }

    @Test
    public void testDeleteAuditTaskInfoLogByTimestamp() {
        List<AuditTask> tasks = AuditTaskDeleteTest.this.auditTaskQuery().taskId(taskTestData[4].getId()).build().getResultList();
        Assert.assertEquals(1, tasks.size());
        AuditTaskDeleteBuilder updateBuilder = AuditTaskDeleteTest.this.auditTaskDelete().date(tasks.get(0).getCreatedOn());
        int result = updateBuilder.build().execute();
        Assert.assertEquals(1, result);
    }
}

