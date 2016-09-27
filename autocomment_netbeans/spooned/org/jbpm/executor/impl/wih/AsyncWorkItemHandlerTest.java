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
import org.junit.Assert;
import org.junit.Before;
import org.jbpm.executor.test.CountDownAsyncJobListener;
import org.jbpm.test.util.CountDownProcessEventListener;
import org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;
import javax.persistence.EntityManagerFactory;
import org.kie.api.executor.ExecutorService;
import org.jbpm.executor.ExecutorServiceFactory;
import org.jbpm.executor.impl.ExecutorServiceImpl;
import org.jbpm.test.util.ExecutorTestUtil;
import java.util.HashMap;
import org.kie.api.runtime.KieSession;
import java.util.List;
import java.util.Map;
import javax.persistence.Persistence;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.runtime.process.ProcessInstance;
import java.util.Properties;
import org.kie.api.executor.RequestInfo;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.junit.Test;
import java.util.UUID;
import org.kie.api.task.UserGroupCallback;
import org.kie.api.runtime.process.WorkItemHandler;

public class AsyncWorkItemHandlerTest extends AbstractExecutorBaseTest {
    private PoolingDataSource pds;

    private UserGroupCallback userGroupCallback;

    private RuntimeManager manager;

    private ExecutorService executorService;

    private EntityManagerFactory emf = null;

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

    @Test(timeout = 10000)
    public void testRunProcessWithAsyncHandler() throws Exception {
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("Hello", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2).registerableItemsFactory(new DefaultRegisterableItemsFactory() {
            @Override
            public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
                Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                handlers.put("async", new AsyncWorkItemHandler(executorService, "org.jbpm.executor.commands.PrintOutCommand"));
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
        ProcessInstance processInstance = ksession.startProcess("ScriptTask");
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        Thread.sleep(3000);
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNull(processInstance);
    }

    @Test(timeout = 10000)
    public void testRunProcessWithAsyncHandlerWithAbort() throws Exception {
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("Task 1", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2).registerableItemsFactory(new DefaultRegisterableItemsFactory() {
            @Override
            public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
                Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                handlers.put("async", new AsyncWorkItemHandler(executorService, "org.jbpm.executor.commands.PrintOutCommand"));
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
        ProcessInstance processInstance = ksession.startProcess("ScriptTask");
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        runtime.getKieSession().abortProcessInstance(processInstance.getId());
        countDownListener.waitTillCompleted();
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNull(processInstance);
    }

    @Test(timeout = 10000)
    public void testRunProcessWithAsyncHandlerDuplicatedRegister() throws Exception {
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("Task 1", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2).registerableItemsFactory(new DefaultRegisterableItemsFactory() {
            @Override
            public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
                Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                handlers.put("async", new AsyncWorkItemHandler(executorService, "org.jbpm.executor.commands.PrintOutCommand"));
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
        ProcessInstance processInstance = ksession.startProcess("ScriptTask");
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        countDownListener.waitTillCompleted();
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNull(processInstance);
        manager.close();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
    }

    @Test(timeout = 10000)
    public void testRunProcessWithAsyncHandlerDelayed() throws Exception {
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("Task 1", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTaskWithParams.bpmn2"), ResourceType.BPMN2).registerableItemsFactory(new DefaultRegisterableItemsFactory() {
            @Override
            public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
                Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                handlers.put("async", new AsyncWorkItemHandler(executorService, "org.jbpm.executor.commands.PrintOutCommand"));
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
        params.put("delayAsync", "4s");
        ProcessInstance processInstance = ksession.startProcess("ScriptTask", params);
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        countDownListener.waitTillCompleted();
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNull(processInstance);
    }

    @Test(timeout = 10000)
    public void testRunProcessWithAsyncHandlerAndReturnNullCommand() throws Exception {
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("Task 1", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2).registerableItemsFactory(new DefaultRegisterableItemsFactory() {
            @Override
            public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
                Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                handlers.put("async", new AsyncWorkItemHandler(executorService, "org.jbpm.executor.test.ReturnNullCommand"));
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
        ProcessInstance processInstance = ksession.startProcess("ScriptTask");
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        countDownListener.waitTillCompleted();
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNull(processInstance);
    }

    @Test
    public void testRunProcessWithAsyncHandlerWithBusinessKey() throws Exception {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTaskWithBusinessKey.bpmn2"), ResourceType.BPMN2).registerableItemsFactory(new DefaultRegisterableItemsFactory() {
            @Override
            public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
                Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                handlers.put("async", new AsyncWorkItemHandler(executorService, "org.jbpm.executor.commands.PrintOutCommand"));
                return handlers;
            }
        }).get();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        String businessKey = UUID.randomUUID().toString();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("businessKey", businessKey);
        ProcessInstance processInstance = ksession.startProcess("ScriptTask", params);
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        Thread.sleep(3000);
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNull(processInstance);
        List<RequestInfo> jobRequest = executorService.getRequestsByBusinessKey(businessKey, new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(jobRequest);
        Assert.assertEquals(1, jobRequest.size());
        Assert.assertEquals(businessKey, jobRequest.get(0).getKey());
        Assert.assertEquals(STATUS.DONE, jobRequest.get(0).getStatus());
    }

    @Test
    public void testRunProcessWithAsyncHandlerWithBusinessKeyAbort() throws Exception {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTaskWithBusinessKey.bpmn2"), ResourceType.BPMN2).registerableItemsFactory(new DefaultRegisterableItemsFactory() {
            @Override
            public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
                Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                handlers.put("async", new AsyncWorkItemHandler(executorService, "org.jbpm.executor.commands.PrintOutCommand"));
                return handlers;
            }
        }).get();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        String businessKey = UUID.randomUUID().toString();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("businessKey", businessKey);
        ProcessInstance processInstance = ksession.startProcess("ScriptTask", params);
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        runtime.getKieSession().abortProcessInstance(processInstance.getId());
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNull(processInstance);
        List<RequestInfo> jobRequest = executorService.getRequestsByBusinessKey(businessKey, new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(jobRequest);
        Assert.assertEquals(1, jobRequest.size());
        Assert.assertEquals(businessKey, jobRequest.get(0).getKey());
        Assert.assertEquals(STATUS.CANCELLED, jobRequest.get(0).getStatus());
    }

    @Test(timeout = 10000)
    public void testRunProcessWithAsyncHandlerProritizedJobs() throws Exception {
        CountDownAsyncJobListener countDownListener = new CountDownAsyncJobListener(1);
        ((ExecutorServiceImpl) (executorService)).addAsyncJobListener(countDownListener);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-PrioritizedAsyncTasks.bpmn2"), ResourceType.BPMN2).registerableItemsFactory(new DefaultRegisterableItemsFactory() {
            @Override
            public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
                Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                handlers.put("async", new AsyncWorkItemHandler(executorService, "org.jbpm.executor.commands.PrintOutCommand"));
                return handlers;
            }
        }).get();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        ProcessInstance processInstance = ksession.startProcess("async-examples.priority-jobs");
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        List<RequestInfo> delayedPrintOuts = executorService.getRequestsByCommand("org.jbpm.executor.test.CustomCommand", new org.kie.api.runtime.query.QueryContext());
        List<RequestInfo> printOuts = executorService.getRequestsByCommand("org.jbpm.executor.commands.PrintOutCommand", new org.kie.api.runtime.query.QueryContext());
        Assert.assertEquals(1, delayedPrintOuts.size());
        Assert.assertEquals(1, printOuts.size());
        Assert.assertEquals(STATUS.QUEUED, delayedPrintOuts.get(0).getStatus());
        Assert.assertEquals(STATUS.QUEUED, printOuts.get(0).getStatus());
        countDownListener.waitTillCompleted();
        delayedPrintOuts = executorService.getRequestsByCommand("org.jbpm.executor.test.CustomCommand", new org.kie.api.runtime.query.QueryContext());
        printOuts = executorService.getRequestsByCommand("org.jbpm.executor.commands.PrintOutCommand", new org.kie.api.runtime.query.QueryContext());
        Assert.assertEquals(1, delayedPrintOuts.size());
        Assert.assertEquals(1, printOuts.size());
        Assert.assertEquals(STATUS.DONE, delayedPrintOuts.get(0).getStatus());
        Assert.assertEquals(STATUS.QUEUED, printOuts.get(0).getStatus());
        countDownListener.reset(1);
        countDownListener.waitTillCompleted();
        delayedPrintOuts = executorService.getRequestsByCommand("org.jbpm.executor.test.CustomCommand", new org.kie.api.runtime.query.QueryContext());
        printOuts = executorService.getRequestsByCommand("org.jbpm.executor.commands.PrintOutCommand", new org.kie.api.runtime.query.QueryContext());
        Assert.assertEquals(1, delayedPrintOuts.size());
        Assert.assertEquals(1, printOuts.size());
        Assert.assertEquals(STATUS.DONE, delayedPrintOuts.get(0).getStatus());
        Assert.assertEquals(STATUS.DONE, printOuts.get(0).getStatus());
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNull(processInstance);
    }

    private ExecutorService buildExecutorService() {
        emf = Persistence.createEntityManagerFactory("org.jbpm.executor");
        executorService = ExecutorServiceFactory.newExecutorService(emf);
        executorService.init();
        return executorService;
    }
}

