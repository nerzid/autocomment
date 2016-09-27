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
import org.kie.internal.runtime.manager.context.EmptyContext;
import javax.persistence.EntityManagerFactory;
import org.kie.api.executor.ExecutorService;
import org.jbpm.executor.ExecutorServiceFactory;
import org.jbpm.executor.impl.ExecutorServiceImpl;
import org.jbpm.test.util.ExecutorTestUtil;
import org.kie.api.runtime.KieSession;
import javax.persistence.Persistence;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import org.kie.api.runtime.process.ProcessInstance;
import java.util.Properties;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.junit.Test;
import org.kie.api.task.UserGroupCallback;

public class AsyncThrowSignalEventTest extends AbstractExecutorBaseTest {
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

    protected CountDownAsyncJobListener configureListener(int threads) {
        CountDownAsyncJobListener countDownListener = new CountDownAsyncJobListener(threads);
        ((ExecutorServiceImpl) (executorService)).addAsyncJobListener(countDownListener);
        return countDownListener;
    }

    @Test(timeout = 10000)
    public void testAsyncThrowEndEvent() throws Exception {
        CountDownAsyncJobListener countDownListener = configureListener(1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-WaitForEvent.bpmn2"), ResourceType.BPMN2).addAsset(ResourceFactory.newClassPathResource("BPMN2-ThrowEventEnd.bpmn2"), ResourceType.BPMN2).addEnvironmentEntry("ExecutorService", executorService).get();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        ProcessInstance processInstance = ksession.startProcess("WaitForEvent");
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        ProcessInstance processInstanceThrow = ksession.startProcess("SendEvent");
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstanceThrow.getState());
        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNotNull(processInstance);
        countDownListener.waitTillCompleted();
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNull(processInstance);
    }

    @Test(timeout = 10000)
    public void testAsyncThrowIntermediateEvent() throws Exception {
        CountDownAsyncJobListener countDownListener = configureListener(1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-WaitForEvent.bpmn2"), ResourceType.BPMN2).addAsset(ResourceFactory.newClassPathResource("BPMN2-ThrowEventIntermediate.bpmn2"), ResourceType.BPMN2).addEnvironmentEntry("ExecutorService", executorService).get();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        ProcessInstance processInstance = ksession.startProcess("WaitForEvent");
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        ProcessInstance processInstanceThrow = ksession.startProcess("SendIntermediateEvent");
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstanceThrow.getState());
        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNotNull(processInstance);
        countDownListener.waitTillCompleted();
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNull(processInstance);
    }

    @Test(timeout = 10000)
    public void testAsyncThrowManualEvent() throws Exception {
        CountDownAsyncJobListener countDownListener = configureListener(1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-WaitForEvent.bpmn2"), ResourceType.BPMN2).addEnvironmentEntry("ExecutorService", executorService).get();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        ProcessInstance processInstance = ksession.startProcess("WaitForEvent");
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        ksession.signalEvent("ASYNC-MySignal", null);
        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNotNull(processInstance);
        countDownListener.waitTillCompleted();
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

