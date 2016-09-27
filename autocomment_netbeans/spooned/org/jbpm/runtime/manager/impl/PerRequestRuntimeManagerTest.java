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


package org.jbpm.runtime.manager.impl;

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.After;
import java.util.ArrayList;
import org.junit.Assert;
import org.kie.api.runtime.manager.audit.AuditService;
import org.junit.Before;
import java.util.Collections;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.internal.runtime.manager.context.EmptyContext;
import java.util.HashMap;
import javax.naming.InitialContext;
import org.kie.api.runtime.KieSession;
import java.util.List;
import java.util.Map;
import org.kie.api.runtime.manager.audit.NodeInstanceLog;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.event.process.ProcessStartedEvent;
import java.util.Properties;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.task.model.Task;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.junit.Test;
import org.jbpm.runtime.manager.util.TestUtil;
import java.lang.reflect.UndeclaredThrowableException;
import org.kie.api.task.UserGroupCallback;
import javax.transaction.UserTransaction;

public class PerRequestRuntimeManagerTest extends AbstractBaseTest {
    private PoolingDataSource pds;

    private UserGroupCallback userGroupCallback;

    private RuntimeManager manager;

    @Before
    public void setup() {
        pds = TestUtil.setupPoolingDataSource();
        Properties properties = new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        userGroupCallback = new org.jbpm.services.task.identity.JBossUserGroupCallbackImpl(properties);
    }

    @After
    public void teardown() {
        if ((manager) != null) {
            manager.close();
        } 
        pds.close();
    }

    @Test
    public void testCreationOfSession() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newEmptyBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2).get();
        manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        long sessionId = ksession.getIdentifier();
        Assert.assertTrue((sessionId == 0));
        manager.disposeRuntimeEngine(runtime);
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();
        // session id should be 1+ previous session id
        Assert.assertEquals((sessionId + 1), ksession.getIdentifier());
        sessionId = ksession.getIdentifier();
        manager.disposeRuntimeEngine(runtime);
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();
        // session id should be 1+ previous session id
        Assert.assertEquals((sessionId + 1), ksession.getIdentifier());
        manager.disposeRuntimeEngine(runtime);
        // when trying to access session after dispose
        try {
            ksession.getIdentifier();
            Assert.fail("Should fail as session manager was closed and with that it's session");
        } catch (IllegalStateException e) {
        } catch (UndeclaredThrowableException e) {
            TestUtil.checkDisposedSessionException(e);
        }
    }

    @Test
    public void testCreationOfSessionWithPeristence() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2).get();
        manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        long sessionId = ksession.getIdentifier();
        Assert.assertTrue((sessionId == 1));
        manager.disposeRuntimeEngine(runtime);
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();
        // session id should be 1+ previous session id
        Assert.assertEquals((sessionId + 1), ksession.getIdentifier());
        sessionId = ksession.getIdentifier();
        manager.disposeRuntimeEngine(runtime);
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();
        // session id should be 1+ previous session id
        Assert.assertEquals((sessionId + 1), ksession.getIdentifier());
        manager.disposeRuntimeEngine(runtime);
        // when trying to access session after dispose
        try {
            ksession.getIdentifier();
            Assert.fail("Should fail as session manager was closed and with that it's session");
        } catch (IllegalStateException e) {
        } catch (UndeclaredThrowableException e) {
            TestUtil.checkDisposedSessionException(e);
        }
    }

    @Test
    public void testCreationOfSessionWithinTransaction() throws Exception {
        System.setProperty("jbpm.tm.jndi.lookup", "java:comp/UserTransaction");
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2).get();
        manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);
        Assert.assertNotNull(manager);
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        long sessionId = ksession.getIdentifier();
        Assert.assertTrue((sessionId == 1));
        ut.commit();
        // since session was created with transaction tx sync is registered to dispose session
        // so now session should already be disposed
        try {
            ksession.getIdentifier();
            Assert.fail("Should fail as session manager was closed and with that it's session");
        } catch (IllegalStateException e) {
        } catch (UndeclaredThrowableException e) {
            TestUtil.checkDisposedSessionException(e);
        }
        System.clearProperty("jbpm.tm.jndi.lookup");
    }

    @Test
    public void testExecuteReusableSubprocess() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addEnvironmentEntry("RuntimeEngineEagerInit", "true").addAsset(ResourceFactory.newClassPathResource("BPMN2-CallActivity.bpmn2"), ResourceType.BPMN2).addAsset(ResourceFactory.newClassPathResource("BPMN2-CallActivitySubProcess.bpmn2"), ResourceType.BPMN2).get();
        manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);
        Assert.assertNotNull(manager);
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        long ksession1Id = ksession.getIdentifier();
        Assert.assertTrue((ksession1Id == 1));
        ProcessInstance pi1 = ksession.startProcess("ParentProcess");
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        ksession.getWorkItemManager().completeWorkItem(1, null);
        AuditService logService = runtime.getAuditService();
        List<? extends ProcessInstanceLog> logs = logService.findActiveProcessInstances("ParentProcess");
        Assert.assertNotNull(logs);
        Assert.assertEquals(0, logs.size());
        logs = logService.findActiveProcessInstances("SubProcess");
        Assert.assertNotNull(logs);
        Assert.assertEquals(0, logs.size());
        logs = logService.findProcessInstances("ParentProcess");
        Assert.assertNotNull(logs);
        Assert.assertEquals(1, logs.size());
        logs = logService.findProcessInstances("SubProcess");
        Assert.assertNotNull(logs);
        Assert.assertEquals(1, logs.size());
        manager.disposeRuntimeEngine(runtime);
        manager.close();
    }

    @Test
    public void testCreationOfRuntimeManagerWithinTransaction() throws Exception {
        System.setProperty("jbpm.tm.jndi.lookup", "java:comp/UserTransaction");
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2).get();
        manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        ksession.startProcess("ScriptTask");
        ut.commit();
        System.clearProperty("jbpm.tm.jndi.lookup");
    }

    @Test
    public void testCreationOfSessionTaskServiceNotConfigured() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newEmptyBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2).get();
        manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        try {
            runtime.getTaskService();
            Assert.fail("Should fail as task service is not configured");
        } catch (UnsupportedOperationException e) {
            Assert.assertEquals("TaskService was not configured", e.getMessage());
        }
        manager.disposeRuntimeEngine(runtime);
        manager.close();
    }

    @Test
    public void testEventSignalingBetweenProcessesWithPeristence() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("events/throw-an-event.bpmn"), ResourceType.BPMN2).addAsset(ResourceFactory.newClassPathResource("events/start-on-event.bpmn"), ResourceType.BPMN2).get();
        manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        ksession.startProcess("com.sample.bpmn.hello");
        AuditService auditService = runtime.getAuditService();
        List<? extends ProcessInstanceLog> throwProcessLogs = auditService.findProcessInstances("com.sample.bpmn.hello");
        List<? extends ProcessInstanceLog> catchProcessLogs = auditService.findProcessInstances("com.sample.bpmn.Second");
        Assert.assertNotNull(throwProcessLogs);
        Assert.assertEquals(1, throwProcessLogs.size());
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, throwProcessLogs.get(0).getStatus().intValue());
        Assert.assertNotNull(catchProcessLogs);
        Assert.assertEquals(1, catchProcessLogs.size());
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, catchProcessLogs.get(0).getStatus().intValue());
        manager.disposeRuntimeEngine(runtime);
        manager.close();
    }

    @Test
    public void testEventSignalingBetweenProcesses() {
        final Map<String, Integer> processStates = new HashMap<String, Integer>();
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultInMemoryBuilder().persistence(false).userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("events/throw-an-event.bpmn"), ResourceType.BPMN2).addAsset(ResourceFactory.newClassPathResource("events/start-on-event.bpmn"), ResourceType.BPMN2).registerableItemsFactory(new DefaultRegisterableItemsFactory() {
            @Override
            public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
                List<ProcessEventListener> listeners = new ArrayList<ProcessEventListener>();
                listeners.add(new DefaultProcessEventListener() {
                    @Override
                    public void afterProcessCompleted(ProcessCompletedEvent event) {
                        processStates.put(event.getProcessInstance().getProcessId(), event.getProcessInstance().getState());
                    }

                    @Override
                    public void beforeProcessStarted(ProcessStartedEvent event) {
                        processStates.put(event.getProcessInstance().getProcessId(), event.getProcessInstance().getState());
                    }
                });
                return listeners;
            }
        }).get();
        manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        ksession.startProcess("com.sample.bpmn.hello");
        Assert.assertEquals(2, processStates.size());
        Assert.assertTrue(processStates.containsKey("com.sample.bpmn.hello"));
        Assert.assertTrue(processStates.containsKey("com.sample.bpmn.Second"));
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processStates.get("com.sample.bpmn.hello").intValue());
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processStates.get("com.sample.bpmn.Second").intValue());
        manager.disposeRuntimeEngine(runtime);
        manager.close();
    }

    @Test
    public void testSignalEventViaRuntimeManager() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2IntermediateThrowEventScope.bpmn2"), ResourceType.BPMN2).get();
        manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime1 = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession1 = runtime1.getKieSession();
        Assert.assertNotNull(ksession1);
        ProcessInstance processInstance = ksession1.startProcess("intermediate-event-scope");
        manager.disposeRuntimeEngine(runtime1);
        RuntimeEngine runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession2 = runtime2.getKieSession();
        Assert.assertNotNull(ksession2);
        ProcessInstance processInstance2 = ksession2.startProcess("intermediate-event-scope");
        manager.disposeRuntimeEngine(runtime2);
        runtime1 = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstance.getId()));
        List<Long> tasks1 = runtime1.getTaskService().getTasksByProcessInstanceId(processInstance.getId());
        Assert.assertNotNull(tasks1);
        Assert.assertEquals(1, tasks1.size());
        runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstance2.getId()));
        List<Long> tasks2 = runtime1.getTaskService().getTasksByProcessInstanceId(processInstance2.getId());
        Assert.assertNotNull(tasks2);
        Assert.assertEquals(1, tasks2.size());
        Object data = "some data";
        runtime1.getTaskService().claim(tasks1.get(0), "john");
        runtime1.getTaskService().start(tasks1.get(0), "john");
        runtime1.getTaskService().complete(tasks1.get(0), "john", Collections.singletonMap("_output", data));
        manager.disposeRuntimeEngine(runtime1);
        manager.disposeRuntimeEngine(runtime2);
        runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstance2.getId()));
        AuditService auditService = runtime2.getAuditService();
        ProcessInstanceLog pi1Log = auditService.findProcessInstance(processInstance.getId());
        Assert.assertNotNull(pi1Log);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, pi1Log.getStatus().intValue());
        ProcessInstanceLog pi2Log = auditService.findProcessInstance(processInstance2.getId());
        Assert.assertNotNull(pi2Log);
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, pi2Log.getStatus().intValue());
        List<? extends NodeInstanceLog> nLogs = auditService.findNodeInstances(processInstance2.getId(), "_527AF0A7-D741-4062-9953-A05E51479C80");
        Assert.assertNotNull(nLogs);
        Assert.assertEquals(2, nLogs.size());
        auditService.dispose();
        // dispose session that should not have affect on the session at all
        manager.disposeRuntimeEngine(runtime1);
        manager.disposeRuntimeEngine(runtime2);
        // close manager which will close session maintained by the manager
        manager.close();
    }

    @Test
    public void testNonexistentDeploymentId() {
        // JBPM-4852
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTask.bpmn2"), ResourceType.BPMN2).get();
        RuntimeManager manager1 = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment, "id-1");
        RuntimeEngine runtime1 = manager1.getRuntimeEngine(EmptyContext.get());
        KieSession ksession1 = runtime1.getKieSession();
        ProcessInstance processInstance = ksession1.startProcess("UserTask");
        manager1.disposeRuntimeEngine(runtime1);
        manager1.close();// simulating server reboot
        
        RuntimeManager manager2 = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment, "id-2");
        RuntimeEngine runtime2 = manager2.getRuntimeEngine(EmptyContext.get());
        TaskService taskService2 = runtime2.getTaskService();
        List<TaskSummary> taskList = taskService2.getTasksAssignedAsPotentialOwner("john", "en-UK");
        Assert.assertEquals(1, taskList.size());
        Long taskId = taskList.get(0).getId();
        Task task = taskService2.getTaskById(taskId);
        Assert.assertEquals("id-1", task.getTaskData().getDeploymentId());
        taskService2.start(taskId, "john");
        try {
            taskService2.complete(taskId, "john", null);
        } catch (NullPointerException npe) {
            Assert.fail("NullPointerException is thrown");
        } catch (RuntimeException re) {
            // RuntimeException with a better message
            Assert.assertEquals("No RuntimeManager registered with identifier: id-1", re.getMessage());
        }
        manager2.disposeRuntimeEngine(runtime2);
        manager2.close();
    }

    @Test
    public void testMultiplePerRequestManagerFromSingleThread() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-IntermediateCatchEventSignalWithRef.bpmn2"), ResourceType.BPMN2).get();
        manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment, "first");
        Assert.assertNotNull(manager);
        RuntimeEnvironment environment2 = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTask.bpmn2"), ResourceType.BPMN2).get();
        RuntimeManager manager2 = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment2, "second");
        Assert.assertNotNull(manager2);
        // start first process instance with first manager
        RuntimeEngine runtime1 = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession1 = runtime1.getKieSession();
        Assert.assertNotNull(ksession1);
        ProcessInstance processInstance = ksession1.startProcess("IntermediateCatchEventWithRef");
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        // start another process instance of the same process just owned by another manager
        RuntimeEngine runtime2 = manager2.getRuntimeEngine(EmptyContext.get());
        KieSession ksession2 = runtime2.getKieSession();
        Assert.assertNotNull(ksession2);
        ProcessInstance processInstance2 = ksession2.startProcess("UserTask");
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance2.getState());
        manager.disposeRuntimeEngine(runtime1);
        manager.disposeRuntimeEngine(runtime2);
        // close manager which will close session maintained by the manager
        manager.close();
        manager2.close();
    }
}

