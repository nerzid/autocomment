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


package org.jbpm.runtime.manager.impl.deploy;

import org.junit.After;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Before;
import org.jbpm.process.core.timer.BusinessCalendar;
import java.util.Date;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.jbpm.process.instance.event.DefaultSignalManagerFactory;
import org.jbpm.services.task.events.DefaultTaskEventListener;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import java.util.HashMap;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.kie.internal.runtime.manager.InternalRegisterableItemsFactory;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieSession;
import java.util.List;
import java.util.Map;
import java.io.ObjectInputStream;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import java.io.ObjectOutputStream;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import java.util.Properties;
import org.kie.api.runtime.manager.RegisterableItemsFactory;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.drools.core.SessionConfiguration;
import org.kie.api.task.TaskEvent;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.junit.Test;
import org.jbpm.runtime.manager.util.TestUtil;
import org.kie.internal.task.api.UserGroupCallback;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.jbpm.workflow.instance.WorkflowProcessInstance;

public class RuntimeManagerWithDescriptorTest extends AbstractDeploymentDescriptorTest {
    private PoolingDataSource pds;

    private UserGroupCallback userGroupCallback;

    private RuntimeManager manager;

    private static List<String> taskEvents;

    private static List<String> processEvents;

    @Before
    public void setup() {
        TestUtil.cleanupSingletonSessionId();
        pds = TestUtil.setupPoolingDataSource();
        Properties properties = new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        userGroupCallback = new org.jbpm.services.task.identity.JBossUserGroupCallbackImpl(properties);
        RuntimeManagerWithDescriptorTest.taskEvents = new ArrayList<String>();
        RuntimeManagerWithDescriptorTest.processEvents = new ArrayList<String>();
    }

    @After
    public void teardown() {
        if ((manager) != null) {
            manager.close();
        } 
        EntityManagerFactoryManager.get().clear();
        pds.close();
        RuntimeManagerWithDescriptorTest.taskEvents = null;
        RuntimeManagerWithDescriptorTest.processEvents = null;
    }

    @Test
    public void testDeployWithDefaultDeploymentDescriptor() throws Exception {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.jbpm.test.dd", "kjar-with-dd", "1.0.0");
        String processString = IOUtils.toString(RuntimeManagerWithDescriptorTest.this.getClass().getResourceAsStream("/BPMN2-ScriptTask.bpmn2"), "UTF-8");
        Map<String, String> resources = new HashMap<String, String>();
        resources.put("src/main/resources/BPMN2-ScriptTask.bpmn2", processString);
        InternalKieModule kJar1 = createKieJar(ks, releaseId, resources);
        installKjar(releaseId, kJar1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder(releaseId).userGroupCallback(userGroupCallback).get();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        Assert.assertNotNull(manager);
        InternalRuntimeManager internalManager = ((InternalRuntimeManager) (manager));
        DeploymentDescriptor descriptor = internalManager.getDeploymentDescriptor();
        Assert.assertNotNull(descriptor);
        RegisterableItemsFactory factory = internalManager.getEnvironment().getRegisterableItemsFactory();
        Assert.assertNotNull(factory);
        Assert.assertTrue((factory instanceof InternalRegisterableItemsFactory));
        Assert.assertNotNull(((InternalRegisterableItemsFactory) (factory)).getRuntimeManager());
        String descriptorFromKjar = descriptor.toXml();
        DeploymentDescriptorManager ddManager = new DeploymentDescriptorManager();
        String defaultDescriptor = ddManager.getDefaultDescriptor().toXml();
        Assert.assertEquals(defaultDescriptor, descriptorFromKjar);
    }

    @Test
    public void testDeployWithCustomDeploymentDescriptor() throws Exception {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.jbpm.test.dd", "-kjar-with-dd", "1.0.0");
        DeploymentDescriptor customDescriptor = new DeploymentDescriptorImpl("org.jbpm.persistence.jpa");
        customDescriptor.getBuilder().runtimeStrategy(RuntimeStrategy.PER_REQUEST).addGlobal(new org.kie.internal.runtime.conf.NamedObjectModel("service", "java.util.ArrayList"));
        String processString = IOUtils.toString(RuntimeManagerWithDescriptorTest.this.getClass().getResourceAsStream("/BPMN2-ScriptTask.bpmn2"), "UTF-8");
        Map<String, String> resources = new HashMap<String, String>();
        resources.put("src/main/resources/BPMN2-ScriptTask.bpmn2", processString);
        resources.put(("src/main/resources/" + (DeploymentDescriptor.META_INF_LOCATION)), customDescriptor.toXml());
        String drl = "package org.jbpm; global java.util.List service; " + ("\trule \"Start Hello1\"" + ("	  when" + ("	  then" + ("\t    System.out.println(\"Hello\");" + "	end"))));
        resources.put("src/main/resources/simple.drl", drl);
        InternalKieModule kJar1 = createKieJar(ks, releaseId, resources);
        installKjar(releaseId, kJar1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder(releaseId).userGroupCallback(userGroupCallback).get();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        Assert.assertNotNull(manager);
        InternalRuntimeManager internalManager = ((InternalRuntimeManager) (manager));
        RegisterableItemsFactory factory = internalManager.getEnvironment().getRegisterableItemsFactory();
        Assert.assertNotNull(factory);
        Assert.assertTrue((factory instanceof InternalRegisterableItemsFactory));
        Assert.assertNotNull(((InternalRegisterableItemsFactory) (factory)).getRuntimeManager());
        DeploymentDescriptor descriptor = internalManager.getDeploymentDescriptor();
        Assert.assertNotNull(descriptor);
        Assert.assertEquals("org.jbpm.persistence.jpa", descriptor.getPersistenceUnit());
        Assert.assertEquals("org.jbpm.persistence.jpa", descriptor.getAuditPersistenceUnit());
        Assert.assertEquals(AuditMode.JPA, descriptor.getAuditMode());
        Assert.assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
        Assert.assertEquals(RuntimeStrategy.PER_REQUEST, descriptor.getRuntimeStrategy());
        Assert.assertEquals(0, descriptor.getMarshallingStrategies().size());
        Assert.assertEquals(0, descriptor.getConfiguration().size());
        Assert.assertEquals(0, descriptor.getEnvironmentEntries().size());
        Assert.assertEquals(0, descriptor.getEventListeners().size());
        Assert.assertEquals(1, descriptor.getGlobals().size());
        Assert.assertEquals(0, descriptor.getTaskEventListeners().size());
        Assert.assertEquals(0, descriptor.getWorkItemHandlers().size());
        Assert.assertEquals(0, descriptor.getRequiredRoles().size());
        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        Assert.assertNotNull(engine);
        Object service = engine.getKieSession().getGlobal("service");
        Assert.assertNotNull(service);
        Assert.assertTrue((service instanceof ArrayList));
    }

    @Test
    public void testDeployWithFullCustomDeploymentDescriptor() throws Exception {
        Map<String, String> resources = new HashMap<String, String>();
        String scriptString = IOUtils.toString(RuntimeManagerWithDescriptorTest.this.getClass().getResourceAsStream("/BPMN2-ScriptTask.bpmn2"), "UTF-8");
        resources.put("src/main/resources/BPMN2-ScriptTask.bpmn2", scriptString);
        String manualString = IOUtils.toString(RuntimeManagerWithDescriptorTest.this.getClass().getResourceAsStream("/BPMN2-ManualTask.bpmn2"), "UTF-8");
        resources.put("src/main/resources/BPMN2-ManualTask.bpmn2", manualString);
        String userString = IOUtils.toString(RuntimeManagerWithDescriptorTest.this.getClass().getResourceAsStream("/BPMN2-UserTask.bpmn2"), "UTF-8");
        resources.put("src/main/resources/BPMN2-UserTask.bpmn2", userString);
        String callString = IOUtils.toString(RuntimeManagerWithDescriptorTest.this.getClass().getResourceAsStream("/BPMN2-CallActivity.bpmn2"), "UTF-8");
        resources.put("src/main/resources/BPMN2-CallActivity.bpmn2", callString);
        String subProcessString = IOUtils.toString(RuntimeManagerWithDescriptorTest.this.getClass().getResourceAsStream("/BPMN2-CallActivitySubProcess.bpmn2"), "UTF-8");
        resources.put("src/main/resources/BPMN2-CallActivitySubProcess.bpmn2", subProcessString);
        DeploymentDescriptor customDescriptor = new DeploymentDescriptorImpl("org.jbpm.persistence.jpa");
        customDescriptor.getBuilder().runtimeStrategy(RuntimeStrategy.PER_PROCESS_INSTANCE).addMarshalingStrategy(new org.kie.internal.runtime.conf.ObjectModel(("org.jbpm.runtime.manager.impl.deploy" + ".RuntimeManagerWithDescriptorTest$TestMarshallingStrategy"))).addConfiguration(new org.kie.internal.runtime.conf.NamedObjectModel("drools.processSignalManagerFactory", "java.lang.String", new Object[]{ DefaultSignalManagerFactory.class.getName() })).addEnvironmentEntry(new org.kie.internal.runtime.conf.NamedObjectModel("jbpm.business.calendar", "org.jbpm.runtime.manager.impl.deploy.RuntimeManagerWithDescriptorTest$TestBusinessCalendar")).addEventListener(new org.kie.internal.runtime.conf.ObjectModel(("org.jbpm.runtime.manager.impl.deploy" + ".RuntimeManagerWithDescriptorTest$TestProcessEventListener"))).addGlobal(new org.kie.internal.runtime.conf.NamedObjectModel("service", "java.util.ArrayList")).addTaskEventListener(new org.kie.internal.runtime.conf.ObjectModel(("org.jbpm.runtime.manager.impl.deploy" + ".RuntimeManagerWithDescriptorTest$TestTaskEventListener"))).addWorkItemHandler(new org.kie.internal.runtime.conf.NamedObjectModel("Manual Task", ("org.jbpm.runtime.manager.impl.deploy" + ".RuntimeManagerWithDescriptorTest$TestWorkItemHandler")));
        resources.put(("src/main/resources/" + (DeploymentDescriptor.META_INF_LOCATION)), customDescriptor.toXml());
        String drl = "package org.jbpm; global java.util.List service; " + ("\trule \"Start Hello1\"" + ("	  when" + ("	  then" + ("\t    System.out.println(\"Hello\");" + "	end"))));
        resources.put("src/main/resources/simple.drl", drl);
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.jbpm.test.dd", "-kjar-with-dd", "1.0.0");
        InternalKieModule kJar1 = createKieJar(ks, releaseId, resources);
        installKjar(releaseId, kJar1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder(releaseId).userGroupCallback(userGroupCallback).get();
        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        Assert.assertNotNull(manager);
        InternalRuntimeManager internalManager = ((InternalRuntimeManager) (manager));
        RegisterableItemsFactory factory = internalManager.getEnvironment().getRegisterableItemsFactory();
        Assert.assertNotNull(factory);
        Assert.assertTrue((factory instanceof InternalRegisterableItemsFactory));
        Assert.assertNotNull(((InternalRegisterableItemsFactory) (factory)).getRuntimeManager());
        DeploymentDescriptor descriptor = internalManager.getDeploymentDescriptor();
        Assert.assertNotNull(descriptor);
        Assert.assertEquals("org.jbpm.persistence.jpa", descriptor.getPersistenceUnit());
        Assert.assertEquals("org.jbpm.persistence.jpa", descriptor.getAuditPersistenceUnit());
        Assert.assertEquals(AuditMode.JPA, descriptor.getAuditMode());
        Assert.assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
        Assert.assertEquals(RuntimeStrategy.PER_PROCESS_INSTANCE, descriptor.getRuntimeStrategy());
        Assert.assertEquals(1, descriptor.getMarshallingStrategies().size());
        // assertEquals(1, descriptor.getConfiguration().size());
        Assert.assertEquals(1, descriptor.getEnvironmentEntries().size());
        Assert.assertEquals(1, descriptor.getEventListeners().size());
        Assert.assertEquals(1, descriptor.getGlobals().size());
        Assert.assertEquals(1, descriptor.getTaskEventListeners().size());
        Assert.assertEquals(1, descriptor.getWorkItemHandlers().size());
        Assert.assertEquals(0, descriptor.getRequiredRoles().size());
        RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        Assert.assertNotNull(engine);
        KieSession kieSession = engine.getKieSession();
        // configuration
        Assert.assertEquals(((SessionConfiguration) (kieSession.getSessionConfiguration())).getSignalManagerFactory(), DefaultSignalManagerFactory.class.getName());
        BusinessCalendar bc = ((BusinessCalendar) (kieSession.getEnvironment().get("jbpm.business.calendar")));
        Assert.assertNotNull(bc);
        Assert.assertTrue((bc instanceof RuntimeManagerWithDescriptorTest.TestBusinessCalendar));
        // globals
        Object service = kieSession.getGlobal("service");
        Assert.assertNotNull(service);
        Assert.assertTrue((service instanceof ArrayList));
        // work item handler
        ProcessInstance processInstance = kieSession.startProcess("ManualTask");
        long processInstanceId = processInstance.getId();
        Assert.assertNotNull(kieSession.getProcessInstance(processInstanceId));
        kieSession.getWorkItemManager().completeWorkItem(RuntimeManagerWithDescriptorTest.TestWorkItemHandler.getWorkItem().getId(), null);
        Assert.assertNull(kieSession.getProcessInstance(processInstanceId));
        manager.disposeRuntimeEngine(engine);
        engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        kieSession = engine.getKieSession();
        // process event listener
        Assert.assertArrayEquals(new String[]{ "beforeProcessStarted" , "afterProcessStarted" , "beforeProcessCompleted" , "afterProcessCompleted" }, RuntimeManagerWithDescriptorTest.processEvents.toArray());
        RuntimeManagerWithDescriptorTest.processEvents.clear();
        manager.disposeRuntimeEngine(engine);
        engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        kieSession = engine.getKieSession();
        // task event listener
        processInstance = kieSession.startProcess("UserTask");
        processInstanceId = processInstance.getId();
        Assert.assertNotNull(kieSession.getProcessInstance(processInstanceId));
        manager.disposeRuntimeEngine(engine);
        engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
        TaskService taskService = engine.getTaskService();
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        long taskId = tasks.get(0).getId();
        taskService.start(taskId, "john");
        taskService.complete(taskId, "john", null);
        Assert.assertArrayEquals(new String[]{ "beforeTaskAddedEvent" , "afterTaskAddedEvent" , "beforeTaskStartedEvent" , "afterTaskStartedEvent" , "beforeTaskCompletedEvent" , "afterTaskCompletedEvent" }, RuntimeManagerWithDescriptorTest.taskEvents.toArray());
        manager.disposeRuntimeEngine(engine);
        engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        kieSession = engine.getKieSession();
        // marshalling strategy
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "marshal");
        processInstance = kieSession.startProcess("ParentProcess", params);
        processInstanceId = processInstance.getId();
        ProcessInstance pi = kieSession.getProcessInstance(processInstanceId);
        Assert.assertNotNull(pi);
        String varX = ((String) (((WorkflowProcessInstance) (pi)).getVariable("x")));
        Assert.assertEquals(RuntimeManagerWithDescriptorTest.TestMarshallingStrategy.ALWAYS_RESPOND_WITH, varX);
        manager.disposeRuntimeEngine(engine);
    }

    public static class TestWorkItemHandler implements WorkItemHandler {
        private static List<WorkItem> workItems = new ArrayList<WorkItem>();

        public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
            RuntimeManagerWithDescriptorTest.TestWorkItemHandler.workItems.add(workItem);
        }

        public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        }

        public static WorkItem getWorkItem() {
            if ((RuntimeManagerWithDescriptorTest.TestWorkItemHandler.workItems.size()) == 0) {
                return null;
            } 
            if ((RuntimeManagerWithDescriptorTest.TestWorkItemHandler.workItems.size()) == 1) {
                WorkItem result = RuntimeManagerWithDescriptorTest.TestWorkItemHandler.workItems.get(0);
                RuntimeManagerWithDescriptorTest.TestWorkItemHandler.workItems.clear();
                return result;
            } else {
                throw new IllegalArgumentException("More than one work item active");
            }
        }

        public static List<WorkItem> getWorkItems() {
            List<WorkItem> result = new ArrayList<WorkItem>(RuntimeManagerWithDescriptorTest.TestWorkItemHandler.workItems);
            RuntimeManagerWithDescriptorTest.TestWorkItemHandler.workItems.clear();
            return result;
        }
    }

    public static class TestTaskEventListener extends DefaultTaskEventListener {
        @Override
        public void beforeTaskStartedEvent(TaskEvent taskEvent) {
            RuntimeManagerWithDescriptorTest.taskEvents.add("beforeTaskStartedEvent");
        }

        @Override
        public void beforeTaskCompletedEvent(TaskEvent taskEvent) {
            RuntimeManagerWithDescriptorTest.taskEvents.add("beforeTaskCompletedEvent");
        }

        @Override
        public void beforeTaskAddedEvent(TaskEvent taskEvent) {
            RuntimeManagerWithDescriptorTest.taskEvents.add("beforeTaskAddedEvent");
        }

        @Override
        public void beforeTaskSuspendedEvent(TaskEvent taskEvent) {
            RuntimeManagerWithDescriptorTest.taskEvents.add("beforeTaskSuspendedEvent");
        }

        @Override
        public void afterTaskStartedEvent(TaskEvent taskEvent) {
            RuntimeManagerWithDescriptorTest.taskEvents.add("afterTaskStartedEvent");
        }

        @Override
        public void afterTaskCompletedEvent(TaskEvent taskEvent) {
            RuntimeManagerWithDescriptorTest.taskEvents.add("afterTaskCompletedEvent");
        }

        @Override
        public void afterTaskAddedEvent(TaskEvent taskEvent) {
            RuntimeManagerWithDescriptorTest.taskEvents.add("afterTaskAddedEvent");
        }
    }

    public static class TestProcessEventListener extends DefaultProcessEventListener {
        @Override
        public void beforeProcessStarted(ProcessStartedEvent processStartedEvent) {
            RuntimeManagerWithDescriptorTest.processEvents.add("beforeProcessStarted");
        }

        @Override
        public void afterProcessStarted(ProcessStartedEvent processStartedEvent) {
            RuntimeManagerWithDescriptorTest.processEvents.add("afterProcessStarted");
        }

        @Override
        public void beforeProcessCompleted(ProcessCompletedEvent processCompletedEvent) {
            RuntimeManagerWithDescriptorTest.processEvents.add("beforeProcessCompleted");
        }

        @Override
        public void afterProcessCompleted(ProcessCompletedEvent processCompletedEvent) {
            RuntimeManagerWithDescriptorTest.processEvents.add("afterProcessCompleted");
        }

        @Override
        public void beforeVariableChanged(ProcessVariableChangedEvent processVariableChangedEvent) {
            RuntimeManagerWithDescriptorTest.processEvents.add("beforeVariableChanged");
        }

        @Override
        public void afterVariableChanged(ProcessVariableChangedEvent processVariableChangedEvent) {
            RuntimeManagerWithDescriptorTest.processEvents.add("afterVariableChanged");
        }
    }

    public static class TestMarshallingStrategy implements ObjectMarshallingStrategy {
        private static final String ALWAYS_RESPOND_WITH = "custom marshaller invoked";

        @Override
        public boolean accept(Object o) {
            return o instanceof String;
        }

        @Override
        public void write(ObjectOutputStream objectOutputStream, Object o) throws IOException {
        }

        @Override
        public Object read(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            return RuntimeManagerWithDescriptorTest.TestMarshallingStrategy.ALWAYS_RESPOND_WITH;
        }

        @Override
        public byte[] marshal(Context context, ObjectOutputStream objectOutputStream, Object o) throws IOException {
            return ((String) (o)).getBytes();
        }

        @Override
        public Object unmarshal(Context context, ObjectInputStream objectInputStream, byte[] bytes, ClassLoader classLoader) throws IOException, ClassNotFoundException {
            return RuntimeManagerWithDescriptorTest.TestMarshallingStrategy.ALWAYS_RESPOND_WITH;
        }

        @Override
        public Context createContext() {
            return null;
        }
    }

    public static class TestBusinessCalendar implements BusinessCalendar {
        @Override
        public long calculateBusinessTimeAsDuration(String timeExpression) {
            return 0;
        }

        @Override
        public Date calculateBusinessTimeAsDate(String timeExpression) {
            return null;
        }
    }
}

