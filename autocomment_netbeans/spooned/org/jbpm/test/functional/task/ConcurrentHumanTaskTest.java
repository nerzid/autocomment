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


package org.jbpm.test.functional.task;

import org.jbpm.process.audit.AuditLogService;
import org.junit.Before;
import java.util.concurrent.CountDownLatch;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.kie.internal.task.api.InternalTaskService;
import org.jbpm.test.JbpmTestCase;
import java.util.List;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.api.task.TaskService;
import org.junit.Test;

public class ConcurrentHumanTaskTest extends JbpmTestCase {
    public ConcurrentHumanTaskTest() {
        super(true, true);
    }

    private static final int THREADS = 2;

    @Before
    public void populateOrgEntity() {
        TaskService taskService = HumanTaskServiceFactory.newTaskServiceConfigurator().entityManagerFactory(getEmf()).getTaskService();
        ((InternalTaskService) (taskService)).addUser(TaskModelProvider.getFactory().newUser("krisv"));
        ((InternalTaskService) (taskService)).addUser(TaskModelProvider.getFactory().newUser("sales-rep"));
        ((InternalTaskService) (taskService)).addUser(TaskModelProvider.getFactory().newUser("john"));
        ((InternalTaskService) (taskService)).addUser(TaskModelProvider.getFactory().newUser("Administrator"));
        ((InternalTaskService) (taskService)).addGroup(TaskModelProvider.getFactory().newGroup("sales"));
        ((InternalTaskService) (taskService)).addGroup(TaskModelProvider.getFactory().newGroup("PM"));
        ((InternalTaskService) (taskService)).addGroup(TaskModelProvider.getFactory().newGroup("Administrators"));
    }

    @Test(timeout = 10000)
    public void testConcurrentInvocationsIncludingUserTasks() throws Exception {
        CountDownLatch latch = new CountDownLatch(ConcurrentHumanTaskTest.THREADS);
        for (int i = 0; i < (ConcurrentHumanTaskTest.THREADS); i++) {
            ProcessRunner pr = new ProcessRunner(i, getEmf(), latch);
            Thread t = new Thread(pr, (i + "-process-runner"));
            t.start();
        }
        latch.await();
        AuditLogService logService = new org.jbpm.process.audit.JPAAuditLogService(getEmf());
        List<? extends ProcessInstanceLog> logs = logService.findProcessInstances("com.sample.humantask.concurrent");
        assertEquals(2, logs.size());
        for (ProcessInstanceLog log : logs) {
            assertEquals(ProcessInstance.STATE_COMPLETED, log.getStatus().intValue());
        }
        logService.dispose();
    }
}

