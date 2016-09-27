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
import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.core.command.CommandService;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.internal.runtime.manager.context.EmptyContext;
import java.util.HashMap;
import org.junit.Ignore;
import org.kie.api.runtime.KieSession;
import java.util.List;
import java.util.Map;
import org.kie.api.runtime.manager.audit.NodeInstanceLog;
import org.drools.persistence.jpa.OptimisticLockRetryInterceptor;
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
import org.drools.persistence.SingleSessionCommandService;
import org.kie.api.task.TaskService;
import org.junit.Test;
import org.jbpm.runtime.manager.util.TestUtil;
import org.drools.persistence.jta.TransactionLockInterceptor;
import org.kie.internal.task.api.UserGroupCallback;
import org.jbpm.workflow.instance.WorkflowRuntimeException;

public class SingletonRuntimeManagerTest extends AbstractBaseTest {
    private PoolingDataSource pds;

    private UserGroupCallback userGroupCallback;

    private RuntimeManager manager;

    @Before
    public void setup() {
        TestUtil.cleanupSingletonSessionId();
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
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        long sessionId = ksession.getIdentifier();
        Assert.assertTrue((sessionId == 0));
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();
        Assert.assertEquals(sessionId, ksession.getIdentifier());
        // dispose session that should not have affect on the session at all
        manager.disposeRuntimeEngine(runtime);
        ksession = manager.getRuntimeEngine(EmptyContext.get()).getKieSession();
        Assert.assertEquals(sessionId, ksession.getIdentifier());
        // close manager which will close session maintained by the manager
        manager.close();
    }

    @Test
    public void testCreationOfSessionWithPersistence() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2).get();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        long sessionId = ksession.getIdentifier();
        Assert.assertTrue((sessionId == 1));
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();
        Assert.assertEquals(sessionId, ksession.getIdentifier());
        // dispose session that should not have affect on the session at all
        manager.disposeRuntimeEngine(runtime);
        ksession = manager.getRuntimeEngine(EmptyContext.get()).getKieSession();
        Assert.assertEquals(sessionId, ksession.getIdentifier());
        // close manager which will close session maintained by the manager
        manager.close();
    }

    @Test
    public void testReCreationOfSessionWithPersistence() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2).get();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        long sessionId = ksession.getIdentifier();
        Assert.assertTrue((sessionId == 1));
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();
        Assert.assertEquals(sessionId, ksession.getIdentifier());
        // dispose session that should not have affect on the session at all
        manager.disposeRuntimeEngine(runtime);
        ksession = manager.getRuntimeEngine(EmptyContext.get()).getKieSession();
        Assert.assertEquals(sessionId, ksession.getIdentifier());
        // close manager which will close session maintained by the manager
        manager.close();
        // recreate it once again to ensure it has right id
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        Assert.assertNotNull(manager);
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        sessionId = ksession.getIdentifier();
        Assert.assertTrue((sessionId == 1));
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();
        Assert.assertEquals(sessionId, ksession.getIdentifier());
        // dispose session that should not have affect on the session at all
        manager.disposeRuntimeEngine(runtime);
        ksession = manager.getRuntimeEngine(EmptyContext.get()).getKieSession();
        Assert.assertEquals(sessionId, ksession.getIdentifier());
        // close manager which will close session maintained by the manager
        manager.close();
    }

    @Test
    public void testCreationOfMultipleSingletonManagerWithPersistence() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2).get();
        // create first manager
        // -----------------------------------------
        RuntimeManager manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment, "manager1");
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        long sessionId = ksession.getIdentifier();
        Assert.assertTrue((sessionId == 1));
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();
        Assert.assertEquals(sessionId, ksession.getIdentifier());
        // dispose session that should not have affect on the session at all
        manager.disposeRuntimeEngine(runtime);
        ksession = manager.getRuntimeEngine(EmptyContext.get()).getKieSession();
        Assert.assertEquals(sessionId, ksession.getIdentifier());
        // close manager which will close session maintained by the manager
        manager.close();
        // create another manager
        // -----------------------------------------
        RuntimeManager manager2 = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment, "manager2");
        Assert.assertNotNull(manager2);
        runtime = manager2.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        sessionId = ksession.getIdentifier();
        Assert.assertTrue((sessionId == 2));
        runtime = manager2.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();
        Assert.assertEquals(sessionId, ksession.getIdentifier());
        // dispose session that should not have affect on the session at all
        manager2.disposeRuntimeEngine(runtime);
        ksession = manager2.getRuntimeEngine(EmptyContext.get()).getKieSession();
        Assert.assertEquals(sessionId, ksession.getIdentifier());
        // close manager which will close session maintained by the manager
        manager2.close();
        // recreate first manager
        // -----------------------------------------
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment, "manager1");
        Assert.assertNotNull(manager);
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        sessionId = ksession.getIdentifier();
        Assert.assertTrue((sessionId == 1));
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();
        Assert.assertEquals(sessionId, ksession.getIdentifier());
        // dispose session that should not have affect on the session at all
        manager.disposeRuntimeEngine(runtime);
        ksession = manager.getRuntimeEngine(EmptyContext.get()).getKieSession();
        Assert.assertEquals(sessionId, ksession.getIdentifier());
        // close manager which will close session maintained by the manager
        manager.close();
        // create another manager
        // -----------------------------------------
        manager2 = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment, "manager2");
        Assert.assertNotNull(manager2);
        runtime = manager2.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        sessionId = ksession.getIdentifier();
        Assert.assertTrue((sessionId == 2));
        runtime = manager2.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();
        Assert.assertEquals(sessionId, ksession.getIdentifier());
        // dispose session that should not have affect on the session at all
        manager2.disposeRuntimeEngine(runtime);
        ksession = manager2.getRuntimeEngine(EmptyContext.get()).getKieSession();
        Assert.assertEquals(sessionId, ksession.getIdentifier());
        // close manager which will close session maintained by the manager
        manager2.close();
    }

    @Test
    public void testCreationOfDuplicatedManagers() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2).get();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        Assert.assertNotNull(manager);
        try {
            RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
            Assert.fail("Should fail as it's not allowed to have singleton manager with same identifier");
        } catch (IllegalStateException e) {
        }
        manager.close();
        // now it is possible to load the manager again
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        Assert.assertNotNull(manager);
    }

    @Test
    public void testExecuteReusableSubprocess() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-CallActivity.bpmn2"), ResourceType.BPMN2).addAsset(ResourceFactory.newClassPathResource("BPMN2-CallActivitySubProcess.bpmn2"), ResourceType.BPMN2).get();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
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
    public void testBusinessRuleTask() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().addAsset(ResourceFactory.newClassPathResource("BPMN2-BusinessRuleTask.bpmn2"), ResourceType.BPMN2).addAsset(ResourceFactory.newClassPathResource("BPMN2-BusinessRuleTask.drl"), ResourceType.DRL).get();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        long sessionId = ksession.getIdentifier();
        Assert.assertTrue((sessionId == 1));
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();
        Assert.assertEquals(sessionId, ksession.getIdentifier());
        // start process
        ProcessInstance pi = ksession.createProcessInstance("BPMN2-BusinessRuleTask", null);
        ksession.insert(pi);
        ksession.startProcessInstance(pi.getId());
        Assert.assertNull(ksession.getProcessInstance(pi.getId()));
        AuditService logService = runtime.getAuditService();
        List<? extends ProcessInstanceLog> logs = logService.findActiveProcessInstances("BPMN2-BusinessRuleTask");
        Assert.assertNotNull(logs);
        Assert.assertEquals(0, logs.size());
        logs = logService.findProcessInstances("BPMN2-BusinessRuleTask");
        Assert.assertNotNull(logs);
        Assert.assertEquals(1, logs.size());
        // dispose session that should not have affect on the session at all
        manager.disposeRuntimeEngine(runtime);
        // close manager which will close session maintained by the manager
        manager.close();
    }

    @Test
    public void testBusinessRuleTaskWithRuleAwareListener() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().addAsset(ResourceFactory.newClassPathResource("BPMN2-BusinessRuleTask.bpmn2"), ResourceType.BPMN2).addAsset(ResourceFactory.newClassPathResource("BPMN2-BusinessRuleTask.drl"), ResourceType.DRL).get();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        long sessionId = ksession.getIdentifier();
        Assert.assertTrue((sessionId == 1));
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();
        Assert.assertEquals(sessionId, ksession.getIdentifier());
        // start process
        ksession.addEventListener(new org.jbpm.process.instance.event.listeners.RuleAwareProcessEventLister());
        ProcessInstance pi = ksession.startProcess("BPMN2-BusinessRuleTask");
        Assert.assertNull(ksession.getProcessInstance(pi.getId()));
        AuditService logService = runtime.getAuditService();
        List<? extends ProcessInstanceLog> logs = logService.findActiveProcessInstances("BPMN2-BusinessRuleTask");
        Assert.assertNotNull(logs);
        Assert.assertEquals(0, logs.size());
        logs = logService.findProcessInstances("BPMN2-BusinessRuleTask");
        Assert.assertNotNull(logs);
        Assert.assertEquals(1, logs.size());
        // dispose session that should not have affect on the session at all
        manager.disposeRuntimeEngine(runtime);
        // close manager which will close session maintained by the manager
        manager.close();
    }

    // until KieSession is protected so it won't be possible to dispose it manually
    @Test
    @Ignore
    public void testCreationOfSessionProtectedDispose() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newEmptyBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2).get();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        long sessionId = ksession.getIdentifier();
        Assert.assertTrue((sessionId == 0));
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();
        Assert.assertEquals(sessionId, ksession.getIdentifier());
        // dispose session that should not have affect on the session at all
        manager.disposeRuntimeEngine(runtime);
        ksession = manager.getRuntimeEngine(EmptyContext.get()).getKieSession();
        Assert.assertEquals(sessionId, ksession.getIdentifier());
        ksession.dispose();
        Assert.assertEquals(sessionId, ksession.getIdentifier());
        // close manager which will close session maintained by the manager
        manager.close();
    }

    @Test
    public void testCreationOfSessionTaskServiceNotConfigured() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newEmptyBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2).get();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
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
        // dispose session that should not have affect on the session at all
        manager.disposeRuntimeEngine(runtime);
        // close manager which will close session maintained by the manager
        manager.close();
    }

    @Test
    public void testBusinessRuleTaskWithGlobal() {
        final List<String> list = new ArrayList<String>();
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().addAsset(ResourceFactory.newClassPathResource("BPMN2-BusinessRuleTask.bpmn2"), ResourceType.BPMN2).addAsset(ResourceFactory.newClassPathResource("BPMN2-BusinessRuleTaskWithGlobal.drl"), ResourceType.DRL).registerableItemsFactory(new DefaultRegisterableItemsFactory() {
            @Override
            public Map<String, Object> getGlobals(RuntimeEngine runtime) {
                Map<String, Object> globals = super.getGlobals(runtime);
                globals.put("list", list);
                return globals;
            }
        }).get();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        long sessionId = ksession.getIdentifier();
        Assert.assertTrue((sessionId == 1));
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();
        Assert.assertEquals(sessionId, ksession.getIdentifier());
        // start process
        ProcessInstance pi = ksession.createProcessInstance("BPMN2-BusinessRuleTask", null);
        ksession.insert(pi);
        ksession.startProcessInstance(pi.getId());
        Assert.assertNull(ksession.getProcessInstance(pi.getId()));
        Assert.assertEquals(1, list.size());
        AuditService logService = runtime.getAuditService();
        List<? extends ProcessInstanceLog> logs = logService.findActiveProcessInstances("BPMN2-BusinessRuleTask");
        Assert.assertNotNull(logs);
        Assert.assertEquals(0, logs.size());
        logs = logService.findProcessInstances("BPMN2-BusinessRuleTask");
        Assert.assertNotNull(logs);
        Assert.assertEquals(1, logs.size());
        // dispose session that should not have affect on the session at all
        manager.disposeRuntimeEngine(runtime);
        // close manager which will close session maintained by the manager
        manager.close();
    }

    @Test
    public void testEventSignalingBetweenProcessesWithPeristence() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("events/throw-an-event.bpmn"), ResourceType.BPMN2).addAsset(ResourceFactory.newClassPathResource("events/start-on-event.bpmn"), ResourceType.BPMN2).get();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
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
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
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
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime1 = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession1 = runtime1.getKieSession();
        Assert.assertNotNull(ksession1);
        ProcessInstance processInstance = ksession1.startProcess("intermediate-event-scope");
        RuntimeEngine runtime2 = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession2 = runtime2.getKieSession();
        Assert.assertNotNull(ksession2);
        ProcessInstance processInstance2 = ksession2.startProcess("intermediate-event-scope");
        List<Long> tasks1 = runtime1.getTaskService().getTasksByProcessInstanceId(processInstance.getId());
        Assert.assertNotNull(tasks1);
        Assert.assertEquals(1, tasks1.size());
        List<Long> tasks2 = runtime1.getTaskService().getTasksByProcessInstanceId(processInstance2.getId());
        Assert.assertNotNull(tasks2);
        Assert.assertEquals(1, tasks2.size());
        Object data = "some data";
        runtime1.getTaskService().claim(tasks1.get(0), "john");
        runtime1.getTaskService().start(tasks1.get(0), "john");
        runtime1.getTaskService().complete(tasks1.get(0), "john", Collections.singletonMap("_output", data));
        AuditService auditService = runtime1.getAuditService();
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
    public void testInterceptorAfterRollback() throws Exception {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTaskWithRollback.bpmn2"), ResourceType.BPMN2).get();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        ProcessInstance processInstance = ksession.startProcess("UserTaskWithRollback");
        CommandService commandService = ((CommandBasedStatefulKnowledgeSession) (ksession)).getCommandService();
        Assert.assertEquals(SingleSessionCommandService.class, commandService.getClass());
        CommandService internalCommandService = ((SingleSessionCommandService) (commandService)).getCommandService();
        Assert.assertEquals(TransactionLockInterceptor.class, internalCommandService.getClass());
        TaskService taskService = runtime.getTaskService();
        List<Long> taskIds = taskService.getTasksByProcessInstanceId(processInstance.getId());
        taskService.start(taskIds.get(0), "john");
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("output1", "rollback");
        try {
            taskService.complete(taskIds.get(0), "john", result);// rollback transaction
            
        } catch (WorkflowRuntimeException e) {
            // ignore
        }
        result = new HashMap<String, Object>();
        result.put("output1", "ok");
        taskService.complete(taskIds.get(0), "john", result);// this time, execute normally
        
        internalCommandService = ((SingleSessionCommandService) (commandService)).getCommandService();
        Assert.assertEquals(TransactionLockInterceptor.class, internalCommandService.getClass());
        internalCommandService = ((TransactionLockInterceptor) (internalCommandService)).getNext();
        Assert.assertEquals(OptimisticLockRetryInterceptor.class, internalCommandService.getClass());
        internalCommandService = ((OptimisticLockRetryInterceptor) (internalCommandService)).getNext();
        Assert.assertEquals("org.drools.persistence.SingleSessionCommandService$TransactionInterceptor", internalCommandService.getClass().getName());
        // close manager which will close session maintained by the manager
        manager.close();
    }
}

