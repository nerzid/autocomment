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


package org.jbpm.runtime.manager.concurrent;

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.After;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Assert;
import org.kie.api.runtime.manager.audit.AuditService;
import org.junit.Before;
import java.util.Collection;
import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.jbpm.workflow.instance.node.HumanTaskNodeInstance;
import org.junit.Ignore;
import javax.naming.InitialContext;
import org.kie.api.runtime.KieSession;
import java.util.List;
import javax.persistence.OptimisticLockException;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.jbpm.services.task.exception.PermissionDeniedException;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import java.util.Properties;
import org.kie.internal.io.ResourceFactory;
import org.junit.runner.RunWith;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.drools.persistence.SingleSessionCommandService;
import org.hibernate.StaleObjectStateException;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.junit.Test;
import org.jbpm.runtime.manager.util.TestUtil;
import org.kie.internal.task.api.UserGroupCallback;
import javax.transaction.UserTransaction;
import org.kie.api.runtime.process.WorkflowProcessInstance;

@RunWith(value = Parameterized.class)
public class SessionTest extends AbstractBaseTest {
    private long maxWaitTime = 60 * 1000;

    // max wait to complete operation is set to 60 seconds to avoid build hangs
    private int nbThreadsProcess = 10;

    private int nbThreadsTask = 10;

    private int nbInvocations = 10;

    private transient int completedStart = 0;

    private transient int completedTask = 0;

    private PoolingDataSource pds;

    private UserGroupCallback userGroupCallback;

    private RuntimeManager manager;

    private boolean useLocking;

    public SessionTest(boolean locking) {
        SessionTest.this.useLocking = locking;
    }

    @Parameters
    public static Collection<Object[]> persistence() {
        Object[][] data = new Object[][]{ new Object[]{ false } };
        return Arrays.asList(data);
    }

    @Before
    public void setup() {
        TestUtil.cleanupSingletonSessionId();
        Properties properties = new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        userGroupCallback = new org.jbpm.services.task.identity.JBossUserGroupCallbackImpl(properties);
        pds = TestUtil.setupPoolingDataSource();
    }

    @After
    public void teardown() {
        pds.close();
        if ((manager) != null) {
            manager.close();
        } 
    }

    @Test
    @Ignore
    public void testSingletonSessionMemory() throws Exception {
        for (int i = 0; i < 1000; i++) {
            RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("sample.bpmn"), ResourceType.BPMN2).get();
            RuntimeManager manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
            RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
            manager.disposeRuntimeEngine(runtime);
            manager.close();
            System.gc();
            Thread.sleep(100);
            System.gc();
            logger.info("Used memory {}", ((Runtime.getRuntime().totalMemory()) - (Runtime.getRuntime().freeMemory())));
        }
    }

    @Test
    public void testSingletonSession() throws Exception {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("sample.bpmn"), ResourceType.BPMN2).get();
        if (useLocking) {
            environment.getEnvironment().set(EnvironmentName.USE_PESSIMISTIC_LOCKING, true);
        } 
        long startTimeStamp = System.currentTimeMillis();
        long maxEndTime = startTimeStamp + (maxWaitTime);
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        completedStart = 0;
        for (int i = 0; i < (nbThreadsProcess); i++) {
            new Thread(new SessionTest.StartProcessRunnable(manager, i)).start();
        }
        completedTask = 0;
        for (int i = 0; i < (nbThreadsTask); i++) {
            new Thread(new SessionTest.CompleteTaskRunnable(manager, i)).start();
        }
        while (((completedStart) < (nbThreadsProcess)) || ((completedTask) < (nbThreadsTask))) {
            Thread.sleep(100);
            if ((System.currentTimeMillis()) > maxEndTime) {
                Assert.fail("Failure, did not finish in time most likely hanging");
            } 
        }
        Thread.sleep(1000);
        // make sure all process instance were completed
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        AuditService logService = runtime.getAuditService();
        // active
        List<? extends ProcessInstanceLog> logs = logService.findActiveProcessInstances("com.sample.bpmn.hello");
        Assert.assertNotNull(logs);
        Assert.assertEquals(0, logs.size());
        // completed
        logs = logService.findProcessInstances("com.sample.bpmn.hello");
        Assert.assertNotNull(logs);
        Assert.assertEquals(((nbThreadsProcess) * (nbInvocations)), logs.size());
        logger.debug("Done");
        manager.disposeRuntimeEngine(runtime);
    }

    @Test
    public void testNewSession() throws Exception {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("sample.bpmn"), ResourceType.BPMN2).get();
        if (useLocking) {
            environment.getEnvironment().set(EnvironmentName.USE_PESSIMISTIC_LOCKING, true);
        } 
        long startTimeStamp = System.currentTimeMillis();
        long maxEndTime = startTimeStamp + (maxWaitTime);
        manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);
        completedStart = 0;
        for (int i = 0; i < (nbThreadsProcess); i++) {
            new SessionTest.StartProcessRunnable(manager, i).run();
        }
        completedTask = 0;
        for (int i = 0; i < (nbThreadsTask); i++) {
            new Thread(new SessionTest.CompleteTaskRunnable(manager, i)).start();
        }
        while (((completedStart) < (nbThreadsProcess)) || ((completedTask) < (nbThreadsTask))) {
            Thread.sleep(100);
            if ((System.currentTimeMillis()) > maxEndTime) {
                Assert.fail("Failure, did not finish in time most likely hanging");
            } 
        }
        // make sure all process instance were completed
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        AuditService logService = runtime.getAuditService();
        // active
        List<? extends ProcessInstanceLog> logs = logService.findActiveProcessInstances("com.sample.bpmn.hello");
        Assert.assertNotNull(logs);
        Assert.assertEquals(0, logs.size());
        // completed
        logs = logService.findProcessInstances("com.sample.bpmn.hello");
        Assert.assertNotNull(logs);
        Assert.assertEquals(((nbThreadsProcess) * (nbInvocations)), logs.size());
        logger.debug("Done");
        manager.disposeRuntimeEngine(runtime);
    }

    @Test
    public void testSessionPerProcessInstance() throws Exception {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("sample.bpmn"), ResourceType.BPMN2).get();
        if (useLocking) {
            environment.getEnvironment().set(EnvironmentName.USE_PESSIMISTIC_LOCKING, true);
        } 
        long startTimeStamp = System.currentTimeMillis();
        long maxEndTime = startTimeStamp + (maxWaitTime);
        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        completedStart = 0;
        for (int i = 0; i < (nbThreadsProcess); i++) {
            new SessionTest.StartProcessPerProcessInstanceRunnable(manager, i).run();
        }
        completedTask = 0;
        for (int i = 0; i < (nbThreadsTask); i++) {
            new Thread(new SessionTest.CompleteTaskPerProcessInstanceRunnable(manager, i)).start();
        }
        while (((completedStart) < (nbThreadsProcess)) || ((completedTask) < (nbThreadsTask))) {
            Thread.sleep(100);
            if ((System.currentTimeMillis()) > maxEndTime) {
                Assert.fail("Failure, did not finish in time most likely hanging");
            } 
        }
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        // make sure all process instance were completed
        AuditService logService = runtime.getAuditService();
        // active
        List<? extends ProcessInstanceLog> logs = logService.findActiveProcessInstances("com.sample.bpmn.hello");
        Assert.assertNotNull(logs);
        Assert.assertEquals(0, logs.size());
        // completed
        logs = logService.findProcessInstances("com.sample.bpmn.hello");
        Assert.assertNotNull(logs);
        Assert.assertEquals(((nbThreadsProcess) * (nbInvocations)), logs.size());
        logger.debug("Done");
        manager.disposeRuntimeEngine(runtime);
    }

    @Test
    public void testNewSessionSuccess() throws Exception {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("sample.bpmn"), ResourceType.BPMN2).get();
        if (useLocking) {
            environment.getEnvironment().set(EnvironmentName.USE_PESSIMISTIC_LOCKING, true);
        } 
        manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        UserTransaction ut = ((UserTransaction) (new InitialContext().lookup("java:comp/UserTransaction")));
        ut.begin();
        ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello", null);
        logger.debug("Started process instance {}", processInstance.getId());
        long workItemId = ((HumanTaskNodeInstance) (((WorkflowProcessInstance) (processInstance)).getNodeInstances().iterator().next())).getWorkItemId();
        long taskId = runtime.getTaskService().getTaskByWorkItemId(workItemId).getId();
        runtime.getTaskService().claim(taskId, "mary");
        ut.commit();
        List<Status> statusses = new ArrayList<Status>();
        statusses.add(Status.Reserved);
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        Assert.assertNotNull(runtime.getKieSession().getProcessInstance(processInstance.getId()));
        List<TaskSummary> tasks = runtime.getTaskService().getTasksOwnedByStatus("mary", statusses, "en-UK");
        Assert.assertEquals(1, tasks.size());
        taskId = tasks.get(0).getId();
        ut = ((UserTransaction) (new InitialContext().lookup("java:comp/UserTransaction")));
        ut.begin();
        runtime.getTaskService().start(taskId, "mary");
        runtime.getTaskService().complete(taskId, "mary", null);
        ut.commit();
        Assert.assertNull(runtime.getKieSession().getProcessInstance(processInstance.getId()));
        tasks = runtime.getTaskService().getTasksOwnedByStatus("mary", statusses, "en-UK");
        Assert.assertEquals(0, tasks.size());
        manager.disposeRuntimeEngine(runtime);
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();
        ut = ((UserTransaction) (new InitialContext().lookup("java:comp/UserTransaction")));
        ut.begin();
        processInstance = ksession.startProcess("com.sample.bpmn.hello", null);
        workItemId = ((HumanTaskNodeInstance) (((WorkflowProcessInstance) (processInstance)).getNodeInstances().iterator().next())).getWorkItemId();
        taskId = runtime.getTaskService().getTaskByWorkItemId(workItemId).getId();
        runtime.getTaskService().claim(taskId, "mary");
        logger.debug("Started process instance {}", processInstance.getId());
        ut.commit();
        Assert.assertNotNull(ksession.getProcessInstance(processInstance.getId()));
        tasks = runtime.getTaskService().getTasksOwnedByStatus("mary", statusses, "en-UK");
        Assert.assertEquals(1, tasks.size());
        taskId = tasks.get(0).getId();
        ut = ((UserTransaction) (new InitialContext().lookup("java:comp/UserTransaction")));
        ut.begin();
        runtime.getTaskService().start(taskId, "mary");
        runtime.getTaskService().complete(taskId, "mary", null);
        ut.commit();
        Assert.assertNull(ksession.getProcessInstance(processInstance.getId()));
        tasks = runtime.getTaskService().getTasksOwnedByStatus("mary", statusses, "en-UK");
        Assert.assertEquals(0, tasks.size());
        manager.disposeRuntimeEngine(runtime);
    }

    @Test
    public void testNewSessionFail() throws Exception {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("sample.bpmn"), ResourceType.BPMN2).get();
        if (useLocking) {
            environment.getEnvironment().set(EnvironmentName.USE_PESSIMISTIC_LOCKING, true);
        } 
        manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        UserTransaction ut = ((UserTransaction) (new InitialContext().lookup("java:comp/UserTransaction")));
        ut.begin();
        ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello", null);
        logger.debug("Started process instance {}", processInstance.getId());
        long workItemId = ((HumanTaskNodeInstance) (((WorkflowProcessInstance) (processInstance)).getNodeInstances().iterator().next())).getWorkItemId();
        long taskId = runtime.getTaskService().getTaskByWorkItemId(workItemId).getId();
        runtime.getTaskService().claim(taskId, "mary");
        ut.rollback();
        logger.debug("Rolled back");
        // TODO: whenever transaction fails, do we need to dispose? can we?
        // sessionManager.dispose();
        List<Status> statusses = new ArrayList<Status>();
        statusses.add(Status.Reserved);
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        Assert.assertNull(runtime.getKieSession().getProcessInstance(processInstance.getId()));
        List<TaskSummary> tasks = runtime.getTaskService().getTasksOwnedByStatus("mary", statusses, "en-UK");
        Assert.assertEquals(0, tasks.size());
        ut = ((UserTransaction) (new InitialContext().lookup("java:comp/UserTransaction")));
        ut.begin();
        processInstance = runtime.getKieSession().startProcess("com.sample.bpmn.hello", null);
        workItemId = ((HumanTaskNodeInstance) (((WorkflowProcessInstance) (processInstance)).getNodeInstances().iterator().next())).getWorkItemId();
        taskId = runtime.getTaskService().getTaskByWorkItemId(workItemId).getId();
        runtime.getTaskService().claim(taskId, "mary");
        logger.debug("Started process instance {}", processInstance.getId());
        ut.commit();
        Assert.assertNotNull(runtime.getKieSession().getProcessInstance(processInstance.getId()));
        tasks = runtime.getTaskService().getTasksOwnedByStatus("mary", statusses, "en-UK");
        Assert.assertEquals(1, tasks.size());
        taskId = tasks.get(0).getId();
        ut = ((UserTransaction) (new InitialContext().lookup("java:comp/UserTransaction")));
        ut.begin();
        runtime.getTaskService().start(taskId, "mary");
        runtime.getTaskService().complete(taskId, "mary", null);
        ut.rollback();
        manager.disposeRuntimeEngine(runtime);
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        Assert.assertNotNull(runtime.getKieSession().getProcessInstance(processInstance.getId()));
        tasks = runtime.getTaskService().getTasksOwnedByStatus("mary", statusses, "en-UK");
        Assert.assertEquals(1, tasks.size());
        taskId = tasks.get(0).getId();
        ut = ((UserTransaction) (new InitialContext().lookup("java:comp/UserTransaction")));
        ut.begin();
        runtime.getTaskService().start(taskId, "mary");
        runtime.getTaskService().complete(taskId, "mary", null);
        ut.commit();
        Assert.assertNull(runtime.getKieSession().getProcessInstance(processInstance.getId()));
        tasks = runtime.getTaskService().getTasksOwnedByStatus("mary", statusses, "en-UK");
        Assert.assertEquals(0, tasks.size());
        manager.disposeRuntimeEngine(runtime);
    }

    @Test
    public void testNewSessionFailBefore() throws Exception {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("sampleFailBefore.bpmn"), ResourceType.BPMN2).get();
        if (useLocking) {
            environment.getEnvironment().set(EnvironmentName.USE_PESSIMISTIC_LOCKING, true);
        } 
        manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        try {
            ProcessInstance processInstance = runtime.getKieSession().startProcess("com.sample.bpmn.hello", null);
            Assert.fail(("Started process instance " + (processInstance.getId())));
        } catch (RuntimeException e) {
            // do nothing
        }
        // TODO: whenever transaction fails, do we need to dispose? can we?
        // sessionManager.dispose();
        manager.disposeRuntimeEngine(runtime);
        List<Status> statusses = new ArrayList<Status>();
        statusses.add(Status.Reserved);
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        List<TaskSummary> tasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("mary", "en-UK");
        Assert.assertEquals(0, tasks.size());
        manager.disposeRuntimeEngine(runtime);
    }

    @Test
    public void testNewSessionFailAfter() throws Exception {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("sampleFailAfter.bpmn"), ResourceType.BPMN2).get();
        if (useLocking) {
            environment.getEnvironment().set(EnvironmentName.USE_PESSIMISTIC_LOCKING, true);
        } 
        manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        ProcessInstance processInstance = runtime.getKieSession().startProcess("com.sample.bpmn.hello.fa", null);
        long workItemId = ((HumanTaskNodeInstance) (((WorkflowProcessInstance) (processInstance)).getNodeInstances().iterator().next())).getWorkItemId();
        long taskId = runtime.getTaskService().getTaskByWorkItemId(workItemId).getId();
        runtime.getTaskService().claim(taskId, "mary");
        List<Status> statusses = new ArrayList<Status>();
        statusses.add(Status.Reserved);
        List<TaskSummary> tasks = runtime.getTaskService().getTasksOwnedByStatus("mary", statusses, "en-UK");
        Assert.assertEquals(1, tasks.size());
        taskId = tasks.get(0).getId();
        UserTransaction ut = ((UserTransaction) (new InitialContext().lookup("java:comp/UserTransaction")));
        try {
            ut.begin();
            runtime.getTaskService().start(taskId, "mary");
            runtime.getTaskService().complete(taskId, "mary", null);
            Assert.fail("Task completed");
        } catch (RuntimeException e) {
            // do nothing
            e.printStackTrace();
        }
        try {
            ut.rollback();
        } catch (Exception e) {
        }
        // TODO: whenever transaction fails, do we need to dispose? can we?
        // sessionManager.dispose();
        manager.disposeRuntimeEngine(runtime);
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        tasks = runtime.getTaskService().getTasksOwnedByStatus("mary", statusses, "en-UK");
        Assert.assertEquals(1, tasks.size());
        manager.disposeRuntimeEngine(runtime);
    }

    @Test
    public void testNewSessionFailAfter2() throws Exception {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("sampleFailAfter.bpmn"), ResourceType.BPMN2).get();
        if (useLocking) {
            environment.getEnvironment().set(EnvironmentName.USE_PESSIMISTIC_LOCKING, true);
        } 
        manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        ProcessInstance processInstance = runtime.getKieSession().startProcess("com.sample.bpmn.hello.fa", null);
        long workItemId = ((HumanTaskNodeInstance) (((WorkflowProcessInstance) (processInstance)).getNodeInstances().iterator().next())).getWorkItemId();
        long taskId = runtime.getTaskService().getTaskByWorkItemId(workItemId).getId();
        runtime.getTaskService().claim(taskId, "mary");
        runtime.getTaskService().start(taskId, "mary");
        List<Status> statusses = new ArrayList<Status>();
        statusses.add(Status.InProgress);
        List<TaskSummary> tasks = runtime.getTaskService().getTasksOwnedByStatus("mary", statusses, "en-UK");
        Assert.assertEquals(1, tasks.size());
        taskId = tasks.get(0).getId();
        try {
            runtime.getTaskService().complete(taskId, "mary", null);
            Assert.fail("Task completed");
        } catch (RuntimeException e) {
            // do nothing
        }
        // TODO: whenever transaction fails, do we need to dispose? can we?
        // sessionManager.dispose();
        manager.disposeRuntimeEngine(runtime);
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        tasks = runtime.getTaskService().getTasksOwnedByStatus("mary", statusses, "en-UK");
        Assert.assertEquals(1, tasks.size());
        manager.disposeRuntimeEngine(runtime);
    }

    private void testStartProcess(RuntimeEngine runtime) throws Exception {
        long taskId;
        synchronized(((SingleSessionCommandService) (((CommandBasedStatefulKnowledgeSession) (runtime.getKieSession())).getCommandService()))) {
            UserTransaction ut = ((UserTransaction) (new InitialContext().lookup("java:comp/UserTransaction")));
            ut.begin();
            logger.debug("Starting process on ksession {}", runtime.getKieSession().getIdentifier());
            ProcessInstance processInstance = runtime.getKieSession().startProcess("com.sample.bpmn.hello", null);
            logger.debug("Started process instance {} on ksession {}", processInstance.getId(), runtime.getKieSession().getIdentifier());
            long workItemId = ((HumanTaskNodeInstance) (((WorkflowProcessInstance) (processInstance)).getNodeInstances().iterator().next())).getWorkItemId();
            taskId = runtime.getTaskService().getTaskByWorkItemId(workItemId).getId();
            logger.debug("Created task {}", taskId);
            runtime.getTaskService().claim(taskId, "mary");
            ut.commit();
        }
    }

    public class StartProcessRunnable implements Runnable {
        private RuntimeManager manager;

        @SuppressWarnings(value = "unused")
        private int counter;

        public StartProcessRunnable(RuntimeManager manager, int counter) {
            SessionTest.StartProcessRunnable.this.manager = manager;
            SessionTest.StartProcessRunnable.this.counter = counter;
        }

        public void run() {
            try {
                for (int i = 0; i < (nbInvocations); i++) {
                    RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
                    logger.trace("Thread {} doing call {}", counter, i);
                    testStartProcess(runtime);
                    manager.disposeRuntimeEngine(runtime);
                }
                logger.trace("Process thread {} completed", counter);
                (completedStart)++;
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    private boolean testCompleteTask(RuntimeEngine runtime) throws Exception, InterruptedException {
        boolean result = false;
        List<Status> statusses = new ArrayList<Status>();
        statusses.add(Status.Reserved);
        List<TaskSummary> tasks = null;
        tasks = runtime.getTaskService().getTasksOwnedByStatus("mary", statusses, "en-UK");
        if (tasks.isEmpty()) {
            logger.debug("Task thread found no tasks");
            Thread.sleep(1000);
        } else {
            long taskId = tasks.get(0).getId();
            logger.debug("Completing task {}", taskId);
            boolean success = false;
            try {
                runtime.getTaskService().start(taskId, "mary");
                success = true;
            } catch (PermissionDeniedException e) {
                // TODO can we avoid these by doing it all in one transaction?
                logger.debug("Task thread was too late for starting task {}", taskId);
            } catch (RuntimeException e) {
                if (isCausedByOptimisticLockingFailure(e)) {
                    logger.debug("Task thread got in conflict when starting task {}", taskId);
                } else {
                    throw e;
                }
            }
            if (success) {
                try {
                    runtime.getTaskService().complete(taskId, "mary", null);
                    logger.debug("Completed task {}", taskId);
                    result = true;
                } catch (RuntimeException e) {
                    if (isCausedByOptimisticLockingFailure(e)) {
                        logger.debug("Task thread got in conflict when completing task {}", taskId);
                    } else {
                        throw e;
                    }
                }
            } 
        }
        return result;
    }

    private boolean testCompleteTaskByProcessInstance(RuntimeEngine runtime, long piId) throws Exception, InterruptedException {
        boolean result = false;
        List<Status> statusses = new ArrayList<Status>();
        statusses.add(Status.Reserved);
        List<TaskSummary> tasks = null;
        tasks = runtime.getTaskService().getTasksByStatusByProcessInstanceId(piId, statusses, "en-UK");
        if (tasks.isEmpty()) {
            logger.debug("Task thread found no tasks");
            Thread.sleep(1000);
        } else {
            long taskId = tasks.get(0).getId();
            logger.debug("Completing task {}", taskId);
            boolean success = false;
            try {
                runtime.getTaskService().start(taskId, "mary");
                success = true;
            } catch (PermissionDeniedException e) {
                // TODO can we avoid these by doing it all in one transaction?
                logger.debug("Task thread was too late for starting task {}", taskId);
            } catch (RuntimeException e) {
                if (isCausedByOptimisticLockingFailure(e)) {
                    logger.debug("Task thread got in conflict when starting task {}", taskId);
                } else {
                    throw e;
                }
            }
            if (success) {
                runtime.getTaskService().complete(taskId, "mary", null);
                logger.debug("Completed task {}", taskId);
                result = true;
            } 
        }
        return result;
    }

    public class CompleteTaskRunnable implements Runnable {
        private RuntimeManager manager;

        @SuppressWarnings(value = "unused")
        private int counter;

        public CompleteTaskRunnable(RuntimeManager manager, int counter) {
            SessionTest.CompleteTaskRunnable.this.manager = manager;
            SessionTest.CompleteTaskRunnable.this.counter = counter;
        }

        public void run() {
            try {
                int i = 0;
                while (i < (nbInvocations)) {
                    RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
                    boolean success = testCompleteTask(runtime);
                    manager.disposeRuntimeEngine(runtime);
                    if (success) {
                        i++;
                    } 
                }
                (completedTask)++;
                logger.trace("Task thread {} completed", counter);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public class StartProcessPerProcessInstanceRunnable implements Runnable {
        private RuntimeManager manager;

        @SuppressWarnings(value = "unused")
        private int counter;

        public StartProcessPerProcessInstanceRunnable(RuntimeManager manager, int counter) {
            SessionTest.StartProcessPerProcessInstanceRunnable.this.manager = manager;
            SessionTest.StartProcessPerProcessInstanceRunnable.this.counter = counter;
        }

        public void run() {
            try {
                for (int i = 0; i < (nbInvocations); i++) {
                    RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
                    logger.trace("Thread {} doing call {}", counter, i);
                    testStartProcess(runtime);
                    manager.disposeRuntimeEngine(runtime);
                }
                logger.trace("Process thread {} completed", counter);
                (completedStart)++;
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public class CompleteTaskPerProcessInstanceRunnable implements Runnable {
        private RuntimeManager manager;

        private int counter;

        public CompleteTaskPerProcessInstanceRunnable(RuntimeManager manager, int counter) {
            SessionTest.CompleteTaskPerProcessInstanceRunnable.this.manager = manager;
            SessionTest.CompleteTaskPerProcessInstanceRunnable.this.counter = counter;
        }

        public void run() {
            try {
                int i = 0;
                while (i < (nbInvocations)) {
                    long processInstanceId = (((nbInvocations) * (counter)) + 1) + i;
                    logger.trace("pi id {} counter {}", processInstanceId, counter);
                    RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
                    boolean success = false;
                    success = testCompleteTaskByProcessInstance(runtime, processInstanceId);
                    manager.disposeRuntimeEngine(runtime);
                    if (success) {
                        i++;
                    } 
                }
                (completedTask)++;
                logger.trace("Task thread {} completed", counter);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    protected boolean isCausedByOptimisticLockingFailure(Throwable throwable) {
        while (throwable != null) {
            if ((OptimisticLockException.class.isAssignableFrom(throwable.getClass())) || (StaleObjectStateException.class.isAssignableFrom(throwable.getClass()))) {
                return true;
            } else {
                throwable = throwable.getCause();
            }
        }
        return false;
    }
}

