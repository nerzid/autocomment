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


package org.jbpm.services.ejb.timer.test;

import org.junit.After;
import org.jboss.arquillian.junit.Arquillian;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Before;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory;
import org.jboss.arquillian.container.test.api.Deployment;
import org.kie.internal.runtime.manager.context.EmptyContext;
import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import org.kie.api.runtime.KieSession;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import javax.persistence.PersistenceUnit;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import java.util.Properties;
import org.kie.internal.io.ResourceFactory;
import org.junit.runner.RunWith;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.kie.api.task.model.TaskSummary;
import org.junit.Test;
import org.kie.internal.task.api.UserGroupCallback;
import org.jboss.shrinkwrap.api.spec.WebArchive;

@RunWith(value = Arquillian.class)
public class EjbTimerServiceIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(EjbTimerServiceIntegrationTest.class);

    private UserGroupCallback userGroupCallback;

    private RuntimeManager manager;

    @Deployment
    public static WebArchive createDeployment() {
        File archive = new File("target/timer-war-ejb-app.war");
        if (!(archive.exists())) {
            throw new IllegalStateException("There is no archive yet generated, run maven build or mvn assembly:assembly");
        } 
        WebArchive war = ShrinkWrap.createFromZipFile(WebArchive.class, archive);
        war.addPackage("org.jbpm.services.ejb.timer.test");// test cases
        
        war.addAsResource("BPMN2-TimerTask.bpmn2");
        war.addAsResource("HumanTaskWithDeadlines.bpmn");
        return war;
    }

    @Before
    public void setup() {
        Properties properties = new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        properties.setProperty("krisv", "HR");
        userGroupCallback = new org.jbpm.services.task.identity.JBossUserGroupCallbackImpl(properties);
    }

    @After
    public void cleanup() {
        cleanupSingletonSessionId();
    }

    @PersistenceUnit(unitName = "org.jbpm.domain")
    private EntityManagerFactory emf;

    @Test
    public void testProcessWithTimerOverEJBTimerService2SecDelay() throws InterruptedException {
        testProcessWithTimerOverEJBTimerService("2s");
    }

    @Test
    public void testProcessWithTimerOverEJBTimerService0SecDelay() throws InterruptedException {
        testProcessWithTimerOverEJBTimerService("0s");
    }

    public void testProcessWithTimerOverEJBTimerService(String delay) throws InterruptedException {
        cleanupSingletonSessionId();
        final List<String> timerExecution = new ArrayList<String>();
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().entityManagerFactory(emf).userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-TimerTask.bpmn2"), ResourceType.BPMN2).schedulerService(new org.jbpm.services.ejb.timer.EjbSchedulerService()).registerableItemsFactory(new DefaultRegisterableItemsFactory() {
            @Override
            public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
                List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                listeners.add(new DefaultProcessEventListener() {
                    @Override
                    public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
                        if (event.getNodeInstance().getNodeName().equals("Event")) {
                            timerExecution.add(event.getNodeInstance().getNodeName());
                        } 
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
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("x", delay);
        ProcessInstance instance = ksession.startProcess("IntermediateCatchEvent", parameters);
        Assert.assertNotNull(instance);
        Thread.sleep(3000);
        instance = ksession.getProcessInstance(instance.getId());
        Assert.assertNull(instance);
        Assert.assertEquals(1, timerExecution.size());
        manager.disposeRuntimeEngine(runtime);
        // close manager which will close session maintained by the manager
        manager.close();
    }

    @Test
    public void testProcessWithTimerOverEJBTimerServiceCancelTimer() throws InterruptedException {
        cleanupSingletonSessionId();
        final List<String> timerExecution = new ArrayList<String>();
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().entityManagerFactory(emf).userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-TimerTask.bpmn2"), ResourceType.BPMN2).schedulerService(new org.jbpm.services.ejb.timer.EjbSchedulerService()).registerableItemsFactory(new DefaultRegisterableItemsFactory() {
            @Override
            public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
                List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                listeners.add(new DefaultProcessEventListener() {
                    @Override
                    public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
                        if (event.getNodeInstance().getNodeName().equals("Event")) {
                            timerExecution.add(event.getNodeInstance().getNodeName());
                        } 
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
        ProcessInstance instance = ksession.startProcess("IntermediateCatchEvent");
        Assert.assertNotNull(instance);
        ksession.abortProcessInstance(instance.getId());
        instance = ksession.getProcessInstance(instance.getId());
        Assert.assertNull(instance);
        Thread.sleep(3000);
        Assert.assertEquals(0, timerExecution.size());
        manager.disposeRuntimeEngine(runtime);
        // close manager which will close session maintained by the manager
        manager.close();
    }

    @Test
    public void testProcessWithHTDeadlineTimerOverEJBTimerService() throws InterruptedException {
        cleanupSingletonSessionId();
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().entityManagerFactory(emf).userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("HumanTaskWithDeadlines.bpmn"), ResourceType.BPMN2).schedulerService(new org.jbpm.services.ejb.timer.EjbSchedulerService()).get();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        ProcessInstance processInstance = ksession.startProcess("htdeadlinetest");
        Assert.assertTrue(((processInstance.getState()) == (ProcessInstance.STATE_ACTIVE)));
        List<TaskSummary> krisTasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("krisv", "en-UK");
        Assert.assertEquals(1, krisTasks.size());
        List<TaskSummary> johnTasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("john", "en-UK");
        Assert.assertEquals(0, johnTasks.size());
        List<TaskSummary> maryTasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("mary", "en-UK");
        Assert.assertEquals(0, maryTasks.size());
        // now wait for 2 seconds for first reassignment
        Thread.sleep(3000);
        krisTasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("krisv", "en-UK");
        Assert.assertEquals(0, krisTasks.size());
        johnTasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("john", "en-UK");
        Assert.assertEquals(1, johnTasks.size());
        maryTasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("mary", "en-UK");
        Assert.assertEquals(0, maryTasks.size());
        runtime.getTaskService().start(johnTasks.get(0).getId(), "john");
        // now wait for 2 more seconds for second reassignment
        Thread.sleep(2000);
        krisTasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("krisv", "en-UK");
        Assert.assertEquals(0, krisTasks.size());
        johnTasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("john", "en-UK");
        Assert.assertEquals(1, johnTasks.size());
        maryTasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("mary", "en-UK");
        Assert.assertEquals(0, maryTasks.size());
        // now wait for 1 seconds to make sure that reassignment did not happen any more since task was already started
        Thread.sleep(3000);
        krisTasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("krisv", "en-UK");
        Assert.assertEquals(0, krisTasks.size());
        johnTasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("john", "en-UK");
        Assert.assertEquals(0, johnTasks.size());
        maryTasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("mary", "en-UK");
        Assert.assertEquals(1, maryTasks.size());
        runtime.getTaskService().start(maryTasks.get(0).getId(), "mary");
        runtime.getTaskService().complete(maryTasks.get(0).getId(), "mary", null);
        // now wait for 2 seconds to make sure that reassignment did not happen any more since task was completed
        Thread.sleep(2000);
        processInstance = ksession.getProcessInstance(processInstance.getId());
        Assert.assertNull(processInstance);
        // close manager which will close session maintained by the manager
        manager.close();
    }

    protected void cleanupSingletonSessionId() {
        File tempDir = new File(getLocation());
        if (tempDir.exists()) {
            String[] jbpmSerFiles = tempDir.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith("-jbpmSessionId.ser");
                }
            });
            for (String file : jbpmSerFiles) {
                EjbTimerServiceIntegrationTest.logger.debug("Temp dir to be removed {} file {}", tempDir, file);
                new File(tempDir, file).delete();
            }
        } 
    }

    protected String getLocation() {
        String location = System.getProperty("jbpm.data.dir", System.getProperty("jboss.server.data.dir"));
        if (location == null) {
            location = System.getProperty("java.io.tmpdir");
        } 
        return location;
    }
}

