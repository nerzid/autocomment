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
import org.jbpm.process.core.async.AsyncSignalEventCommand;
import org.junit.Before;
import org.kie.api.executor.CommandContext;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.executor.ExecutorService;
import org.jbpm.executor.ExecutorServiceFactory;
import org.jbpm.test.JbpmTestCase;
import org.kie.api.runtime.KieSession;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.manager.RuntimeManager;
import org.junit.Test;
import org.jbpm.workflow.instance.WorkflowRuntimeException;

/**
 * process1: start -> catch signal -> first time exception -> end
 * AsyncSignalEventCommand should be repeated when fails
 */
public class AsyncIntermediateCatchSignalTest extends JbpmTestCase {
    private static Object LOCK = new Object();

    private static final String PROCESS_AICS = "org.jbpm.test.functional.async.AsyncIntermediateCatchSignal";

    private static final String BPMN_AICS = "org/jbpm/test/functional/async/AsyncIntermediateCatchSignal.bpmn2";

    private ExecutorService executorService;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        executorService = ExecutorServiceFactory.newExecutorService(getEmf());
        executorService.setInterval(1);
        executorService.setThreadPoolSize(3);
        executorService.init();
        addEnvironmentEntry("ExecutorService", executorService);
        addWorkItemHandler("SyncError", new org.jbpm.test.wih.FirstErrorWorkItemHandler());
        addProcessEventListener(new DefaultProcessEventListener() {
            @Override
            public void afterProcessCompleted(ProcessCompletedEvent event) {
                synchronized(AsyncIntermediateCatchSignalTest.LOCK) {
                    AsyncIntermediateCatchSignalTest.LOCK.notifyAll();
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
    public void testCorrectProcessStateAfterExceptionSignalCommand() {
        RuntimeManager runtimeManager = createRuntimeManager(AsyncIntermediateCatchSignalTest.BPMN_AICS);
        KieSession ksession = getRuntimeEngine().getKieSession();
        ProcessInstance pi = ksession.startProcess(AsyncIntermediateCatchSignalTest.PROCESS_AICS, null);
        long pid = pi.getId();
        CommandContext ctx = new CommandContext();
        ctx.setData("DeploymentId", runtimeManager.getIdentifier());
        ctx.setData("ProcessInstanceId", pid);
        ctx.setData("Signal", "MySignal");
        ctx.setData("Event", null);
        executorService.scheduleRequest(AsyncSignalEventCommand.class.getName(), ctx);
        synchronized(AsyncIntermediateCatchSignalTest.LOCK) {
            try {
                AsyncIntermediateCatchSignalTest.LOCK.wait();
            } catch (InterruptedException e) {
            }
        }
    }

    @Test
    public void testCorrectProcessStateAfterExceptionSignalCommandMulti() {
        RuntimeManager runtimeManager = createRuntimeManager(AsyncIntermediateCatchSignalTest.BPMN_AICS);
        KieSession ksession = getRuntimeEngine().getKieSession();
        long[] pid = new long[5];
        for (int i = 0; i < 5; ++i) {
            ProcessInstance pi = ksession.startProcess(AsyncIntermediateCatchSignalTest.PROCESS_AICS, null);
            pid[i] = pi.getId();
            CommandContext ctx = new CommandContext();
            ctx.setData("DeploymentId", runtimeManager.getIdentifier());
            ctx.setData("ProcessInstanceId", pid[i]);
            ctx.setData("Signal", "MySignal");
            ctx.setData("Event", null);
            executorService.scheduleRequest(AsyncSignalEventCommand.class.getName(), ctx);
        }
        for (int i = 0; i < 5; ++i) {
            synchronized(AsyncIntermediateCatchSignalTest.LOCK) {
                try {
                    AsyncIntermediateCatchSignalTest.LOCK.wait();
                } catch (InterruptedException e) {
                }
            }
        }
        for (long p : pid) {
            ProcessInstance pi = ksession.getProcessInstance(p);
            Assertions.assertThat(pi).isNull();
        }
    }

    @Test(expected = WorkflowRuntimeException.class)
    public void testSyncGlobalSignal() {
        KieSession ksession = createKSession(AsyncIntermediateCatchSignalTest.BPMN_AICS);
        ksession.startProcess(AsyncIntermediateCatchSignalTest.PROCESS_AICS, null);
        ksession.signalEvent("MySignal", null);
    }
}

