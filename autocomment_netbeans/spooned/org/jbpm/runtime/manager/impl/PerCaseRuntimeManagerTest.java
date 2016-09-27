/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.runtime.manager.impl;

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.After;
import java.util.ArrayList;
import org.junit.Assert;
import org.kie.api.runtime.manager.audit.AuditService;
import org.junit.Before;
import org.kie.internal.runtime.manager.context.CaseContext;
import org.jbpm.test.util.CountDownProcessEventListener;
import org.kie.api.event.process.DefaultProcessEventListener;
import javax.persistence.EntityManagerFactory;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import java.util.HashSet;
import org.kie.api.runtime.KieSession;
import java.util.List;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import java.util.Properties;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import java.util.Set;
import org.junit.Test;
import org.jbpm.runtime.manager.util.TestUtil;
import org.kie.api.task.UserGroupCallback;

public class PerCaseRuntimeManagerTest extends AbstractBaseTest {
    private PoolingDataSource pds;

    private UserGroupCallback userGroupCallback;

    private RuntimeManager manager;

    private EntityManagerFactory emf;

    @Before
    public void setup() {
        Properties properties = new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        userGroupCallback = new org.jbpm.services.task.identity.JBossUserGroupCallbackImpl(properties);
        pds = TestUtil.setupPoolingDataSource();
        emf = EntityManagerFactoryManager.get().getOrCreate("org.jbpm.persistence.jpa");
    }

    @After
    public void teardown() {
        manager.close();
        EntityManagerFactoryManager.get().clear();
        pds.close();
    }

    @Test
    public void testCreationOfSession() {
        final Set<Long> ksessionUsed = new HashSet<Long>();
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).entityManagerFactory(emf).addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2).addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTask.bpmn2"), ResourceType.BPMN2).registerableItemsFactory(new DefaultRegisterableItemsFactory() {
            @Override
            public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
                List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                listeners.add(new DefaultProcessEventListener() {
                    @Override
                    public void beforeProcessStarted(ProcessStartedEvent event) {
                        ksessionUsed.add(((KieSession) (event.getKieRuntime())).getIdentifier());
                    }
                });
                return listeners;
            }
        }).get();
        manager = RuntimeManagerFactory.Factory.get().newPerCaseRuntimeManager(environment);
        Assert.assertNotNull(manager);
        // ksession for process instance #1
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime = manager.getRuntimeEngine(CaseContext.get("Case-1"));
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        long ksession1Id = ksession.getIdentifier();
        Assert.assertTrue((ksession1Id > 0));
        // ksession for process instance #2
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime2 = manager.getRuntimeEngine(CaseContext.get("Case-2"));
        KieSession ksession2 = runtime2.getKieSession();
        Assert.assertNotNull(ksession2);
        long ksession2Id = ksession2.getIdentifier();
        Assert.assertTrue((ksession2Id > ksession1Id));
        ProcessInstance pi1 = ksession.startProcess("UserTask");
        ProcessInstance pi2 = ksession2.startProcess("UserTask");
        // both processes started
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, pi2.getState());
        manager.disposeRuntimeEngine(runtime);
        manager.disposeRuntimeEngine(runtime2);
        runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi1.getId()));
        ksession = runtime.getKieSession();
        Assert.assertEquals(ksession1Id, ksession.getIdentifier());
        runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi2.getId()));
        ksession2 = runtime2.getKieSession();
        Assert.assertEquals(ksession2Id, ksession2.getIdentifier());
        manager.disposeRuntimeEngine(runtime);
        manager.disposeRuntimeEngine(runtime2);
        // now let's check by case context
        runtime = manager.getRuntimeEngine(CaseContext.get("Case-1"));
        ksession = runtime.getKieSession();
        Assert.assertEquals(ksession1Id, ksession.getIdentifier());
        runtime2 = manager.getRuntimeEngine(CaseContext.get("Case-2"));
        ksession2 = runtime2.getKieSession();
        Assert.assertEquals(ksession2Id, ksession2.getIdentifier());
        Assert.assertEquals(2, ksessionUsed.size());
        Assert.assertTrue(ksessionUsed.contains(ksession1Id));
        Assert.assertTrue(ksessionUsed.contains(ksession2Id));
        manager.disposeRuntimeEngine(runtime);
        manager.disposeRuntimeEngine(runtime2);
        manager.close();
    }

    @Test
    public void testEventSignalingBetweenProcessesWithPeristence() {
        final Set<Long> ksessionUsed = new HashSet<Long>();
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("events/throw-an-event.bpmn"), ResourceType.BPMN2).addAsset(ResourceFactory.newClassPathResource("events/start-on-event.bpmn"), ResourceType.BPMN2).registerableItemsFactory(new DefaultRegisterableItemsFactory() {
            @Override
            public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
                List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                listeners.add(new DefaultProcessEventListener() {
                    @Override
                    public void beforeProcessStarted(ProcessStartedEvent event) {
                        ksessionUsed.add(((KieSession) (event.getKieRuntime())).getIdentifier());
                    }
                });
                return listeners;
            }
        }).get();
        manager = RuntimeManagerFactory.Factory.get().newPerCaseRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(CaseContext.get("Case-1"));
        KieSession ksession = runtime.getKieSession();
        long ksession1Id = ksession.getIdentifier();
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
        Assert.assertEquals(1, ksessionUsed.size());
        Assert.assertEquals(ksession1Id, ksessionUsed.iterator().next().longValue());
        manager.disposeRuntimeEngine(runtime);
        manager.close();
    }

    @Test
    public void testExecuteReusableSubprocess() {
        final Set<Long> ksessionUsed = new HashSet<Long>();
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-CallActivity.bpmn2"), ResourceType.BPMN2).addAsset(ResourceFactory.newClassPathResource("BPMN2-CallActivitySubProcess.bpmn2"), ResourceType.BPMN2).registerableItemsFactory(new DefaultRegisterableItemsFactory() {
            @Override
            public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
                List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                listeners.add(new DefaultProcessEventListener() {
                    @Override
                    public void beforeProcessStarted(ProcessStartedEvent event) {
                        ksessionUsed.add(((KieSession) (event.getKieRuntime())).getIdentifier());
                    }
                });
                return listeners;
            }
        }).get();
        manager = RuntimeManagerFactory.Factory.get().newPerCaseRuntimeManager(environment);
        Assert.assertNotNull(manager);
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime = manager.getRuntimeEngine(CaseContext.get("Case-1"));
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        long ksession1Id = ksession.getIdentifier();
        Assert.assertTrue((ksession1Id == 2));
        ProcessInstance pi1 = ksession.startProcess("ParentProcess");
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        manager.disposeRuntimeEngine(runtime);
        runtime = manager.getRuntimeEngine(CaseContext.get("Case-1"));
        ksession = runtime.getKieSession();
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
        String externalId = logs.get(0).getExternalId();
        Assert.assertEquals(manager.getIdentifier(), externalId);
        logs = logService.findProcessInstances("SubProcess");
        Assert.assertNotNull(logs);
        Assert.assertEquals(1, logs.size());
        externalId = logs.get(0).getExternalId();
        Assert.assertEquals(manager.getIdentifier(), externalId);
        Assert.assertEquals(1, ksessionUsed.size());
        Assert.assertEquals(ksession1Id, ksessionUsed.iterator().next().longValue());
        manager.disposeRuntimeEngine(runtime);
        manager.close();
    }

    @Test
    public void testMultipleProcessesInSingleCaseCompletedInSequence() {
        final Set<Long> ksessionUsed = new HashSet<Long>();
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).entityManagerFactory(emf).addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2).registerableItemsFactory(new DefaultRegisterableItemsFactory() {
            @Override
            public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
                List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                listeners.add(new DefaultProcessEventListener() {
                    @Override
                    public void beforeProcessStarted(ProcessStartedEvent event) {
                        ksessionUsed.add(((KieSession) (event.getKieRuntime())).getIdentifier());
                    }
                });
                return listeners;
            }
        }).get();
        manager = RuntimeManagerFactory.Factory.get().newPerCaseRuntimeManager(environment);
        Assert.assertNotNull(manager);
        // ksession for process instance #1
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime = manager.getRuntimeEngine(CaseContext.get("Case-1"));
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        long ksession1Id = ksession.getIdentifier();
        Assert.assertTrue((ksession1Id > 0));
        ProcessInstance pi1 = ksession.startProcess("ScriptTask");
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, pi1.getState());
        manager.disposeRuntimeEngine(runtime);
        runtime = manager.getRuntimeEngine(CaseContext.get("Case-1"));
        ksession = runtime.getKieSession();
        ProcessInstance pi2 = ksession.startProcess("ScriptTask");
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, pi2.getState());
        // there should be only one ksession used
        Assert.assertEquals(1, ksessionUsed.size());
        Assert.assertEquals(ksession1Id, ksessionUsed.iterator().next().longValue());
        manager.close();
    }

    @Test(timeout = 10000)
    public void testTimerOnPerCaseManager() throws Exception {
        final Set<Long> ksessionUsed = new HashSet<Long>();
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("timer", 3);
        final List<Long> timerExpirations = new ArrayList<Long>();
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).registerableItemsFactory(new DefaultRegisterableItemsFactory() {
            @Override
            public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
                List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                listeners.add(new DefaultProcessEventListener() {
                    @Override
                    public void afterNodeLeft(ProcessNodeLeftEvent event) {
                        if (event.getNodeInstance().getNodeName().equals("timer")) {
                            timerExpirations.add(event.getProcessInstance().getId());
                            ksessionUsed.add(((KieSession) (event.getKieRuntime())).getIdentifier());
                        } 
                    }
                });
                listeners.add(new DefaultProcessEventListener() {
                    @Override
                    public void beforeProcessStarted(ProcessStartedEvent event) {
                        ksessionUsed.add(((KieSession) (event.getKieRuntime())).getIdentifier());
                    }
                });
                listeners.add(countDownListener);
                return listeners;
            }
        }).addAsset(ResourceFactory.newClassPathResource("BPMN2-IntermediateCatchEventTimerCycle3.bpmn2"), ResourceType.BPMN2).get();
        manager = RuntimeManagerFactory.Factory.get().newPerCaseRuntimeManager(environment);
        Assert.assertNotNull(manager);
        // ksession for process instance #1
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime = manager.getRuntimeEngine(CaseContext.get("Case-1"));
        KieSession ksession = runtime.getKieSession();
        long ksession1Id = ksession.getIdentifier();
        ProcessInstance pi1 = ksession.startProcess("IntermediateCatchEvent");
        // both processes started
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        // wait a bit for some timers to fire
        countDownListener.waitTillCompleted();
        ksession.abortProcessInstance(pi1.getId());
        manager.disposeRuntimeEngine(runtime);
        manager.close();
        // there should be only one ksession used
        Assert.assertEquals(1, ksessionUsed.size());
        Assert.assertEquals(ksession1Id, ksessionUsed.iterator().next().longValue());
    }
}

