/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.executor.impl.wih;

import org.jbpm.test.util.AbstractExecutorBaseTest;
import org.junit.After;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Before;
import org.jbpm.test.util.CountDownProcessEventListener;
import org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;
import javax.persistence.EntityManagerFactory;
import org.kie.api.executor.ExecutorService;
import org.jbpm.executor.ExecutorServiceFactory;
import org.jbpm.test.util.ExecutorTestUtil;
import java.util.HashMap;
import org.kie.api.runtime.KieSession;
import java.util.List;
import java.util.Map;
import org.kie.api.runtime.manager.audit.NodeInstanceLog;
import javax.persistence.Persistence;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.runtime.process.ProcessInstance;
import java.util.Properties;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.api.task.model.TaskSummary;
import org.junit.Test;
import java.util.concurrent.TimeUnit;
import org.kie.api.task.UserGroupCallback;
import org.kie.api.runtime.process.WorkItemHandler;

public class AsyncContinuationSupportTest extends AbstractExecutorBaseTest {
    private PoolingDataSource pds;

    private UserGroupCallback userGroupCallback;

    private RuntimeManager manager;

    private ExecutorService executorService;

    private EntityManagerFactory emf = null;

    private long delay = 1000;

    @Before
    public void setup() {
        ExecutorTestUtil.cleanupSingletonSessionId();
        pds = ExecutorTestUtil.setupPoolingDataSource();
        Properties properties = new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        userGroupCallback = new org.jbpm.services.task.identity.JBossUserGroupCallbackImpl(properties);
        executorService = buildExecutorService();
    }

    @After
    public void teardown() {
        executorService.destroy();
        if ((manager) != null) {
            RuntimeManagerRegistry.get().remove(manager.getIdentifier());
            manager.close();
        } 
        if ((emf) != null) {
            emf.close();
        } 
        pds.close();
    }

    @Test
    public void testAsyncScriptTask() throws Exception {
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("Hello", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-AsyncScriptTask.bpmn2"), ResourceType.BPMN2).addEnvironmentEntry("ExecutorService", executorService).registerableItemsFactory(new DefaultRegisterableItemsFactory() {
            @Override
            public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
                Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                handlers.put("async", new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler());
                return handlers;
            }

            @Override
            public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
                List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                listeners.add(countDownListener);
                return listeners;
            }
        }).get();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        ProcessInstance processInstance = ksession.startProcess("AsyncScriptTask");
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();
        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstanceId);
        Assert.assertNotNull(processInstance);
        countDownListener.waitTillCompleted();
        processInstance = runtime.getKieSession().getProcessInstance(processInstanceId);
        Assert.assertNull(processInstance);
        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        Assert.assertNotNull(logs);
        Assert.assertEquals(8, logs.size());
    }

    @Test
    public void testNoAsyncServiceAvilableScriptTask() throws Exception {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-AsyncScriptTask.bpmn2"), ResourceType.BPMN2).registerableItemsFactory(new DefaultRegisterableItemsFactory() {
            @Override
            public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
                Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                handlers.put("async", new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler());
                return handlers;
            }
        }).get();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        ProcessInstance processInstance = ksession.startProcess("AsyncScriptTask");
        long processInstanceId = processInstance.getId();
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNull(processInstance);
        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        Assert.assertNotNull(logs);
        Assert.assertEquals(8, logs.size());
    }

    @Test(timeout = 10000)
    public void testAsyncServiceTask() throws Exception {
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("Hello", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-ServiceProcess.bpmn2"), ResourceType.BPMN2).addEnvironmentEntry("ExecutorService", executorService).registerableItemsFactory(new DefaultRegisterableItemsFactory() {
            @Override
            public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
                Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                handlers.put("async", new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler());
                handlers.put("Service Task", new org.jbpm.bpmn2.handler.ServiceTaskHandler());
                return handlers;
            }

            @Override
            public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
                List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                listeners.add(countDownListener);
                return listeners;
            }
        }).get();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s", "john");
        ProcessInstance processInstance = ksession.startProcess("ServiceProcess", params);
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();
        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNotNull(processInstance);
        countDownListener.waitTillCompleted();
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNull(processInstance);
        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        Assert.assertNotNull(logs);
        Assert.assertEquals(6, logs.size());
    }

    @Test(timeout = 10000)
    public void testAsyncMIUserTask() throws Exception {
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("Hello", 1, true);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-MultiInstanceLoopCharacteristicsTask.bpmn2"), ResourceType.BPMN2).addEnvironmentEntry("ExecutorService", executorService).registerableItemsFactory(new DefaultRegisterableItemsFactory() {
            @Override
            public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
                Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                handlers.put("async", new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler());
                return handlers;
            }

            @Override
            public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
                List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                listeners.add(countDownListener);
                return listeners;
            }
        }).get();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        ArrayList<String> items = new ArrayList<String>();
        items.add("one");
        items.add("two");
        items.add("three");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("list", items);
        ProcessInstance processInstance = ksession.startProcess("MultiInstanceLoopCharacteristicsTask", params);
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();
        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNotNull(processInstance);
        countDownListener.waitTillCompleted();
        List<TaskSummary> tasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("john", "en-UK");
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());
        countDownListener.reset(1);
        countDownListener.waitTillCompleted();
        tasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("john", "en-UK");
        Assert.assertNotNull(tasks);
        Assert.assertEquals(2, tasks.size());
        countDownListener.reset(1);
        countDownListener.waitTillCompleted();
        tasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("john", "en-UK");
        Assert.assertNotNull(tasks);
        Assert.assertEquals(3, tasks.size());
        for (TaskSummary task : tasks) {
            runtime.getTaskService().start(task.getId(), "john");
            runtime.getTaskService().complete(task.getId(), "john", null);
        }
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNull(processInstance);
        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        Assert.assertNotNull(logs);
        Assert.assertEquals(12, logs.size());
    }

    @Test(timeout = 10000)
    public void testAsyncMISubProcess() throws Exception {
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("Hello", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-MultiInstanceLoopCharacteristicsProcess.bpmn2"), ResourceType.BPMN2).addEnvironmentEntry("ExecutorService", executorService).registerableItemsFactory(new DefaultRegisterableItemsFactory() {
            @Override
            public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
                Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                handlers.put("async", new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler());
                return handlers;
            }

            @Override
            public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
                List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                listeners.add(countDownListener);
                return listeners;
            }
        }).get();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        ArrayList<String> items = new ArrayList<String>();
        items.add("one");
        items.add("two");
        items.add("three");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("list", items);
        ProcessInstance processInstance = ksession.startProcess("MultiInstanceLoopCharacteristicsProcess", params);
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();
        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNotNull(processInstance);
        countDownListener.waitTillCompleted();
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNotNull(processInstance);
        countDownListener.reset(1);
        countDownListener.waitTillCompleted();
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNotNull(processInstance);
        countDownListener.reset(1);
        countDownListener.waitTillCompleted();
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNull(processInstance);
        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        Assert.assertNotNull(logs);
        Assert.assertEquals(26, logs.size());
    }

    @Test(timeout = 10000)
    public void testAsyncSubProcess() throws Exception {
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("Hello", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-SubProcess.bpmn2"), ResourceType.BPMN2).addEnvironmentEntry("ExecutorService", executorService).registerableItemsFactory(new DefaultRegisterableItemsFactory() {
            @Override
            public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
                Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                handlers.put("async", new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler());
                return handlers;
            }

            @Override
            public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
                List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                listeners.add(countDownListener);
                return listeners;
            }
        }).get();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        Map<String, Object> params = new HashMap<String, Object>();
        ProcessInstance processInstance = ksession.startProcess("SubProcess", params);
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();
        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNotNull(processInstance);
        countDownListener.waitTillCompleted();
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNull(processInstance);
        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        Assert.assertNotNull(logs);
        Assert.assertEquals(18, logs.size());
    }

    @Test(timeout = 10000)
    public void testSubProcessWithAsyncNodes() throws Exception {
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("Hello1", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-SubProcessAsyncNodes.bpmn2"), ResourceType.BPMN2).addEnvironmentEntry("ExecutorService", executorService).registerableItemsFactory(new DefaultRegisterableItemsFactory() {
            @Override
            public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
                Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                handlers.put("async", new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler());
                return handlers;
            }

            @Override
            public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
                List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                listeners.add(countDownListener);
                return listeners;
            }
        }).get();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        Map<String, Object> params = new HashMap<String, Object>();
        ProcessInstance processInstance = ksession.startProcess("SubProcess", params);
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();
        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNotNull(processInstance);
        countDownListener.waitTillCompleted();
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNotNull(processInstance);
        countDownListener.reset("Hello2", 1);
        countDownListener.waitTillCompleted();
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNotNull(processInstance);
        countDownListener.reset("Hello3", 1);
        countDownListener.waitTillCompleted();
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNotNull(processInstance);
        Thread.sleep(delay);
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNull(processInstance);
        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        Assert.assertNotNull(logs);
        Assert.assertEquals(18, logs.size());
    }

    @Test(timeout = 10000)
    public void testSubProcessWithSomeAsyncNodes() throws Exception {
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("Hello2", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-SubProcessSomeAsyncNodes.bpmn2"), ResourceType.BPMN2).addEnvironmentEntry("ExecutorService", executorService).registerableItemsFactory(new DefaultRegisterableItemsFactory() {
            @Override
            public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
                Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                handlers.put("async", new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler());
                return handlers;
            }

            @Override
            public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
                List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                listeners.add(countDownListener);
                return listeners;
            }
        }).get();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        Map<String, Object> params = new HashMap<String, Object>();
        ProcessInstance processInstance = ksession.startProcess("SubProcess", params);
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();
        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNotNull(processInstance);
        countDownListener.waitTillCompleted();
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNotNull(processInstance);
        countDownListener.reset("Goodbye", 1);
        countDownListener.waitTillCompleted();
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNull(processInstance);
        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        Assert.assertNotNull(logs);
        Assert.assertEquals(18, logs.size());
    }

    @Test(timeout = 10000)
    public void testAsyncCallActivityTask() throws Exception {
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("CallActivity", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2).addAsset(ResourceFactory.newClassPathResource("BPMN2-CallActivity.bpmn2"), ResourceType.BPMN2).addEnvironmentEntry("ExecutorService", executorService).registerableItemsFactory(new DefaultRegisterableItemsFactory() {
            @Override
            public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
                Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                handlers.put("async", new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler());
                return handlers;
            }

            @Override
            public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
                List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                listeners.add(countDownListener);
                return listeners;
            }
        }).get();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        ProcessInstance processInstance = ksession.startProcess("ParentProcess");
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();
        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNotNull(processInstance);
        countDownListener.waitTillCompleted();
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNull(processInstance);
        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        Assert.assertNotNull(logs);
        Assert.assertEquals(6, logs.size());
    }

    @Test(timeout = 10000)
    public void testAsyncAndSyncServiceTasks() throws Exception {
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("Async Service", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-AsyncServiceTask.bpmn2"), ResourceType.BPMN2).addEnvironmentEntry("ExecutorService", executorService).registerableItemsFactory(new DefaultRegisterableItemsFactory() {
            @Override
            public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
                Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                handlers.put("async", new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler());
                handlers.put("Service Task", new org.jbpm.bpmn2.handler.ServiceTaskHandler());
                return handlers;
            }

            @Override
            public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
                List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                listeners.add(countDownListener);
                return listeners;
            }
        }).get();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "john");
        ProcessInstance processInstance = ksession.startProcess("async-cont.async-service-task", params);
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();
        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNotNull(processInstance);
        countDownListener.waitTillCompleted();
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNotNull(processInstance);
        countDownListener.reset(1);
        countDownListener.waitTillCompleted();
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNotNull(processInstance);
        countDownListener.reset(1);
        countDownListener.waitTillCompleted();
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNull(processInstance);
        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        Assert.assertNotNull(logs);
        Assert.assertEquals(14, logs.size());
    }

    private ExecutorService buildExecutorService() {
        emf = Persistence.createEntityManagerFactory("org.jbpm.executor");
        executorService = ExecutorServiceFactory.newExecutorService(emf);
        executorService.setInterval(((int) (delay)));
        executorService.setTimeunit(TimeUnit.MILLISECONDS);
        executorService.init();
        // let the executor start worker threads
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
        }
        return executorService;
    }
}

