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


package org.jbpm.test.functional.timer;

import org.junit.After;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import java.util.Collection;
import org.jbpm.test.listener.CountDownProcessEventListener;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.runtime.KieSession;
import java.util.List;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import javax.persistence.Persistence;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.internal.io.ResourceFactory;
import org.junit.runner.RunWith;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.SessionNotFoundException;
import org.junit.Test;

@RunWith(value = Parameterized.class)
public class GlobalThreadPoolTimerServiceTest extends GlobalTimerServiceBaseTest {
    private int managerType;

    @Parameters
    public static Collection<Object[]> persistence() {
        Object[][] data = new Object[][]{ new Object[]{ 1 } , new Object[]{ 2 } , new Object[]{ 3 } };
        return Arrays.asList(data);
    }

    public GlobalThreadPoolTimerServiceTest(int managerType) {
        GlobalThreadPoolTimerServiceTest.this.managerType = managerType;
    }

    @Before
    public void setUp() {
        cleanupSingletonSessionId();
        emf = Persistence.createEntityManagerFactory("org.jbpm.test.persistence");
        globalScheduler = new org.jbpm.process.core.timer.impl.ThreadPoolSchedulerService(1);
    }

    @After
    public void tearDown() {
        try {
            globalScheduler.shutdown();
        } catch (Exception e) {
        }
        cleanup();
    }

    @Override
    protected RuntimeManager getManager(RuntimeEnvironment environment, boolean waitOnStart) {
        if ((managerType) == 1) {
            return RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        } else if ((managerType) == 2) {
            return RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);
        } else if ((managerType) == 3) {
            return RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        } else {
            throw new IllegalArgumentException("Invalid runtime maanger type");
        }
    }

    @Test(timeout = 20000)
    public void testInterediateTimerWithGlobalTestServiceWithinTransaction() throws Exception {
        CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("timer", 3);
        globalScheduler = new org.jbpm.test.functional.timer.addon.TransactionalThreadPoolSchedulerService(3);
        // prepare listener to assert results
        final List<Long> timerExporations = new ArrayList<Long>();
        ProcessEventListener listener = new DefaultProcessEventListener() {
            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName().equals("timer")) {
                    timerExporations.add(event.getProcessInstance().getId());
                } 
            }
        };
        environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().entityManagerFactory(emf).addAsset(ResourceFactory.newClassPathResource("org/jbpm/test/functional/timer/IntermediateCatchEventTimerCycle3.bpmn2"), ResourceType.BPMN2).schedulerService(globalScheduler).registerableItemsFactory(new TestRegisterableItemsFactory(listener, countDownListener)).get();
        manager = getManager(environment, true);
        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();
        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent");
        assertTrue(((processInstance.getState()) == (ProcessInstance.STATE_ACTIVE)));
        // now wait for 1 second for first timer to trigger
        countDownListener.waitTillCompleted(2000);
        // dispose session to force session to be reloaded on timer expiration
        manager.disposeRuntimeEngine(runtime);
        countDownListener.waitTillCompleted();
        countDownListener.reset(1);
        try {
            runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstance.getId()));
            ksession = runtime.getKieSession();
            processInstance = ksession.getProcessInstance(processInstance.getId());
            assertNull(processInstance);
        } catch (SessionNotFoundException e) {
            // expected for PerProcessInstanceManagers since process instance is completed
        }
        // let's wait to ensure no more timers are expired and triggered
        countDownListener.waitTillCompleted(3000);
        assertEquals(3, timerExporations.size());
        manager.disposeRuntimeEngine(runtime);
    }
}

