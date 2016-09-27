/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
import org.junit.Before;
import org.kie.internal.runtime.manager.context.CaseContext;
import org.jbpm.test.util.CountDownProcessEventListener;
import javax.persistence.EntityManagerFactory;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import java.util.HashMap;
import org.jbpm.process.audit.JPAAuditLogService;
import org.kie.api.runtime.KieSession;
import java.util.List;
import java.util.Map;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import java.util.Properties;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.junit.Test;
import org.jbpm.runtime.manager.util.TestUtil;
import org.kie.internal.task.api.UserGroupCallback;

public class MultiInstanceCallActivityRuntimeManagerTest extends AbstractBaseTest {
    private PoolingDataSource pds;

    private UserGroupCallback userGroupCallback;

    private EntityManagerFactory emf;

    private RuntimeManager manager;

    private CountDownProcessEventListener countDownListener;

    private int numberOfChildProcesses = 15;

    @Before
    public void setup() {
        TestUtil.cleanupSingletonSessionId();
        pds = TestUtil.setupPoolingDataSource();
        emf = EntityManagerFactoryManager.get().getOrCreate("org.jbpm.persistence.jpa");
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
        EntityManagerFactoryManager.get().clear();
        pds.close();
    }

    @Test(timeout = 10000)
    public void testSingletonMultiInstanceCallactivityCompleteAtTheSameTime() {
        RuntimeEnvironment environment = createEnvironment();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment, "first");
        Assert.assertNotNull(manager);
        testMultiInstanceCallactivityCompleteAtTheSameTime(ProcessInstanceIdContext.get());
    }

    @Test(timeout = 10000)
    public void testPerProcessInstanceMultiInstanceCallactivityCompleteAtTheSameTime() {
        RuntimeEnvironment environment = createEnvironment();
        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment, "first");
        Assert.assertNotNull(manager);
        testMultiInstanceCallactivityCompleteAtTheSameTime(ProcessInstanceIdContext.get());
    }

    @Test(timeout = 10000)
    public void testPerCaseMultiInstanceCallactivityCompleteAtTheSameTime() {
        RuntimeEnvironment environment = createEnvironment();
        manager = RuntimeManagerFactory.Factory.get().newPerCaseRuntimeManager(environment, "first");
        Assert.assertNotNull(manager);
        testMultiInstanceCallactivityCompleteAtTheSameTime(CaseContext.get("CASE-00000001"));
    }

    public void testMultiInstanceCallactivityCompleteAtTheSameTime(Context<?> startContext) {
        // start first process instance with first manager
        RuntimeEngine runtime1 = manager.getRuntimeEngine(startContext);
        KieSession ksession1 = runtime1.getKieSession();
        Assert.assertNotNull(ksession1);
        List<String> items = new ArrayList<String>();
        for (int i = 0; i < (numberOfChildProcesses); i++) {
            items.add((i + ""));
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("items", items);
        ProcessInstance processInstance = ksession1.startProcess("test.Parent", params);
        manager.disposeRuntimeEngine(runtime1);
        countDownListener.waitTillCompleted();
        JPAAuditLogService auditService = new JPAAuditLogService(emf);
        // process instance 1 should be completed by signal
        ProcessInstanceLog pi1Log = auditService.findProcessInstance(processInstance.getId());
        Assert.assertNotNull(pi1Log);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, pi1Log.getStatus().intValue());
        auditService.dispose();
        // close manager which will close session maintained by the manager
        manager.close();
    }

    private RuntimeEnvironment createEnvironment() {
        countDownListener = new CountDownProcessEventListener("timer", numberOfChildProcesses);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().entityManagerFactory(emf).userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("reusable-subprocess/Parent.bpmn2"), ResourceType.BPMN2).addAsset(ResourceFactory.newClassPathResource("reusable-subprocess/Child.bpmn2"), ResourceType.BPMN2).registerableItemsFactory(new DefaultRegisterableItemsFactory() {
            @Override
            public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
                List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                listeners.add(countDownListener);
                return listeners;
            }
        }).get();
        return environment;
    }
}

