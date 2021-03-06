/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.jbpm.test.functional.async;

import org.junit.After;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.executor.ExecutorService;
import org.jbpm.executor.ExecutorServiceFactory;
import org.jbpm.test.JbpmTestCase;
import org.kie.api.runtime.KieSession;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.runtime.process.ProcessInstance;
import org.junit.Test;

/**
 * process1: start -> catch signal -> first time exception -> end process2:
 * start -> async end signal --- should repeat when fails
 */
public class AsyncEndSignalTest extends JbpmTestCase {
    private static Object LOCK = new Object();

    private static final String PROCESS_AES = "org.jbpm.test.functional.async.AsyncEndSignal";

    private static final String PROCESS_AICS = "org.jbpm.test.functional.async.AsyncIntermediateCatchSignal";

    private static final String BPMN_AES = "org/jbpm/test/functional/async/AsyncEndSignal.bpmn2";

    private static final String BPMN_AICS = "org/jbpm/test/functional/async/AsyncIntermediateCatchSignal.bpmn2";

    private ExecutorService executorService;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        executorService = ExecutorServiceFactory.newExecutorService(getEmf());
        executorService.setInterval(1);
        executorService.init();
        addEnvironmentEntry("ExecutorService", executorService);
        addWorkItemHandler("SyncError", new org.jbpm.test.wih.FirstErrorWorkItemHandler());
        addProcessEventListener(new DefaultProcessEventListener() {
            @Override
            public void afterProcessCompleted(ProcessCompletedEvent event) {
                synchronized(AsyncEndSignalTest.LOCK) {
                    AsyncEndSignalTest.LOCK.notifyAll();
                }
            }
        });
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        executorService.destroy();
    }

    @Test(timeout = 10000)
    public void testCorrectProcessStateAfterExceptionEndSignal() {
        KieSession ksession = createKSession(AsyncEndSignalTest.BPMN_AICS, AsyncEndSignalTest.BPMN_AES);
        ProcessInstance pi1 = ksession.startProcess(AsyncEndSignalTest.PROCESS_AICS, null);
        long pid1 = pi1.getId();
        ProcessInstance pi2 = ksession.startProcess(AsyncEndSignalTest.PROCESS_AES, null);
        long pid2 = pi2.getId();
        synchronized(AsyncEndSignalTest.LOCK) {
            try {
                AsyncEndSignalTest.LOCK.wait();
            } catch (InterruptedException e) {
            }
        }
        pi1 = ksession.getProcessInstance(pid1);
        Assertions.assertThat(pi1).isNull();
        pi2 = ksession.getProcessInstance(pid2);
        Assertions.assertThat(pi2).isNull();
    }
}

