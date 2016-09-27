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
import org.junit.Assert;
import org.junit.Before;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.api.runtime.KieSession;
import java.util.List;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import java.util.Properties;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.task.model.TaskSummary;
import org.junit.Test;
import org.jbpm.runtime.manager.util.TestUtil;
import org.kie.api.task.UserGroupCallback;

public class EventHnadlingTest extends AbstractBaseTest {
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
    public void testRunMultiEventProcessPerRequestRuntimeManager() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPM2-MultiEventProcess.bpmn2"), ResourceType.BPMN2).get();
        manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        ProcessInstance processInstance = ksession.startProcess("signalbroadcast");
        manager.disposeRuntimeEngine(runtime);
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();
        List<TaskSummary> tasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("john", "en-UK");
        Assert.assertNotNull(tasks);
        Assert.assertEquals(0, tasks.size());
        ksession.signalEvent("Message-Msg", null);
        manager.disposeRuntimeEngine(runtime);
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();
        tasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("john", "en-UK");
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());
        ksession.signalEvent("signal", null);
        manager.disposeRuntimeEngine(runtime);
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();
        tasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("john", "en-UK");
        Assert.assertNotNull(tasks);
        Assert.assertEquals(3, tasks.size());
        for (TaskSummary task : tasks) {
            runtime.getTaskService().start(task.getId(), "john");
            runtime.getTaskService().complete(task.getId(), "john", null);
        }
        processInstance = ksession.getProcessInstance(processInstance.getId());
        Assert.assertNull(processInstance);
        // close manager which will close session maintained by the manager
        manager.close();
    }

    @Test
    public void testRunMultiEventProcessPerProcessInstanceRuntimeManager() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPM2-MultiEventProcess.bpmn2"), ResourceType.BPMN2).get();
        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        ProcessInstance processInstance = ksession.startProcess("signalbroadcast");
        manager.disposeRuntimeEngine(runtime);
        runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstance.getId()));
        ksession = runtime.getKieSession();
        List<TaskSummary> tasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("john", "en-UK");
        Assert.assertNotNull(tasks);
        Assert.assertEquals(0, tasks.size());
        ksession.signalEvent("Message-Msg", null);
        manager.disposeRuntimeEngine(runtime);
        runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstance.getId()));
        ksession = runtime.getKieSession();
        tasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("john", "en-UK");
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());
        ksession.signalEvent("signal", null);
        manager.disposeRuntimeEngine(runtime);
        runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstance.getId()));
        ksession = runtime.getKieSession();
        tasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("john", "en-UK");
        Assert.assertNotNull(tasks);
        Assert.assertEquals(3, tasks.size());
        for (TaskSummary task : tasks) {
            runtime.getTaskService().start(task.getId(), "john");
            runtime.getTaskService().complete(task.getId(), "john", null);
        }
        processInstance = ksession.getProcessInstance(processInstance.getId());
        Assert.assertNull(processInstance);
        // close manager which will close session maintained by the manager
        manager.close();
    }

    @Test
    public void testRunMultiEventProcessSingletonRuntimeManager() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPM2-MultiEventProcess.bpmn2"), ResourceType.BPMN2).get();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        ProcessInstance processInstance = ksession.startProcess("signalbroadcast");
        List<TaskSummary> tasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("john", "en-UK");
        Assert.assertNotNull(tasks);
        Assert.assertEquals(0, tasks.size());
        ksession.signalEvent("Message-Msg", null);
        tasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("john", "en-UK");
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());
        ksession.signalEvent("signal", null);
        tasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("john", "en-UK");
        Assert.assertNotNull(tasks);
        Assert.assertEquals(3, tasks.size());
        for (TaskSummary task : tasks) {
            runtime.getTaskService().start(task.getId(), "john");
            runtime.getTaskService().complete(task.getId(), "john", null);
        }
        processInstance = ksession.getProcessInstance(processInstance.getId());
        Assert.assertNull(processInstance);
        // close manager which will close session maintained by the manager
        manager.close();
    }
}

