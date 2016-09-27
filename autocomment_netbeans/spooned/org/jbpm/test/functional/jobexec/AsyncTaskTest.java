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


package org.jbpm.test.functional.jobexec;

import org.assertj.core.api.Assertions;
import qa.tools.ikeeper.annotation.BZ;
import org.jbpm.test.listener.CountDownAsyncJobListener;
import org.jbpm.test.listener.CountDownProcessEventListener;
import org.jbpm.executor.impl.ExecutorServiceImpl;
import java.util.HashMap;
import org.jbpm.test.JbpmAsyncJobTestCase;
import org.kie.api.runtime.KieSession;
import java.util.Map;
import org.kie.api.runtime.process.ProcessInstance;
import org.junit.Test;
import org.kie.api.runtime.process.WorkItemManager;

// 
// TODO: Add asserts job results
// 
public class AsyncTaskTest extends JbpmAsyncJobTestCase {
    public static final String ASYNC_EXECUTOR = "org/jbpm/test/functional/jobexec/AsyncExecutor.bpmn2";

    public static final String ASYNC_EXECUTOR_ID = "org.jbpm.test.functional.jobexec.AsyncExecutor";

    public static final String ASYNC_DATA_EXECUTOR = "org/jbpm/test/functional/jobexec/AsyncDataExecutor.bpmn2";

    public static final String ASYNC_DATA_EXECUTOR_ID = "org.jbpm.test.functional.jobexec.AsyncDataExecutor";

    public static final String USER_COMMAND = "org.jbpm.test.jobexec.UserCommand";

    public static final String USER_FAILING_COMMAND = "org.jbpm.test.jobexec.UserFailingCommand";

    @Test(timeout = 10000)
    public void testTaskErrorHandling() throws Exception {
        CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("Task 1", 1);
        addProcessEventListener(countDownListener);
        KieSession ksession = createKSession(AsyncTaskTest.ASYNC_EXECUTOR);
        WorkItemManager wim = ksession.getWorkItemManager();
        wim.registerWorkItemHandler("async", new org.jbpm.executor.impl.wih.AsyncWorkItemHandler(getExecutorService()));
        Map<String, Object> pm = new HashMap<String, Object>();
        pm.put("command", AsyncTaskTest.USER_FAILING_COMMAND);
        ProcessInstance pi = ksession.startProcess(AsyncTaskTest.ASYNC_EXECUTOR_ID, pm);
        assertNodeTriggered(pi.getId(), "Start", "Hello", "Task 1");
        assertNodeNotTriggered(pi.getId(), "Output");
        assertNodeNotTriggered(pi.getId(), "Runtime Error Handling");
        assertNodeNotTriggered(pi.getId(), "Illegal Argument Error Handling");
        // Wait for the 4 retries to fail
        countDownListener.waitTillCompleted();
        ProcessInstance processInstance = ksession.getProcessInstance(pi.getId());
        assertNull(processInstance);
        assertNodeTriggered(pi.getId(), "Runtime Error Handling", "RuntimeErrorEnd");
        assertNodeNotTriggered(pi.getId(), "Output");
        assertNodeNotTriggered(pi.getId(), "Illegal Argument Error Handling");
        Assertions.assertThat(getExecutorService().getInErrorRequests(new org.kie.api.runtime.query.QueryContext())).hasSize(1);
        Assertions.assertThat(getExecutorService().getInErrorRequests(new org.kie.api.runtime.query.QueryContext()).get(0).getErrorInfo().get(0).getMessage()).isEqualTo("Internal Error");
        assertProcessInstanceCompleted(pi.getId());
    }

    @Test(timeout = 10000)
    @BZ(value = "1121027")
    public void testTaskComplete() throws Exception {
        CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("Process async", 1);
        CountDownAsyncJobListener countDownJobListener = new CountDownAsyncJobListener(1);
        try {
            ((ExecutorServiceImpl) (getExecutorService())).addAsyncJobListener(countDownJobListener);
            addProcessEventListener(countDownListener);
            KieSession ksession = createKSession(AsyncTaskTest.ASYNC_DATA_EXECUTOR);
            WorkItemManager wim = ksession.getWorkItemManager();
            wim.registerWorkItemHandler("async", new org.jbpm.executor.impl.wih.AsyncWorkItemHandler(getExecutorService()));
            Map<String, Object> pm = new HashMap<String, Object>();
            pm.put("command", AsyncTaskTest.USER_COMMAND);
            ProcessInstance pi = ksession.startProcess(AsyncTaskTest.ASYNC_DATA_EXECUTOR_ID, pm);
            assertNodeTriggered(pi.getId(), "StartProcess", "Set user info", "Process async");
            assertNodeNotTriggered(pi.getId(), "Output");
            // Wait for the job to complete
            countDownListener.waitTillCompleted();
            ProcessInstance processInstance = ksession.getProcessInstance(pi.getId());
            assertNull(processInstance);
            assertNodeTriggered(pi.getId(), "Output", "EndProcess");
            countDownJobListener.waitTillCompleted();
            Assertions.assertThat(getExecutorService().getCompletedRequests(new org.kie.api.runtime.query.QueryContext())).hasSize(1);
            assertProcessInstanceCompleted(pi.getId());
        } finally {
            ((ExecutorServiceImpl) (getExecutorService())).removeAsyncJobListener(countDownJobListener);
        }
    }

    @Test(timeout = 10000)
    public void testTaskFail() throws Exception {
        CountDownAsyncJobListener countDownListener = new CountDownAsyncJobListener(4);
        ((ExecutorServiceImpl) (getExecutorService())).addAsyncJobListener(countDownListener);
        KieSession ksession = createKSession(AsyncTaskTest.ASYNC_DATA_EXECUTOR);
        WorkItemManager wim = ksession.getWorkItemManager();
        wim.registerWorkItemHandler("async", new org.jbpm.executor.impl.wih.AsyncWorkItemHandler(getExecutorService()));
        Map<String, Object> pm = new HashMap<String, Object>();
        pm.put("command", AsyncTaskTest.USER_FAILING_COMMAND);
        ProcessInstance pi = ksession.startProcess(AsyncTaskTest.ASYNC_DATA_EXECUTOR_ID, pm);
        assertNodeTriggered(pi.getId(), "StartProcess", "Set user info", "Process async");
        assertNodeNotTriggered(pi.getId(), "Output");
        // Wait for the 4 retries to fail
        countDownListener.waitTillCompleted();
        ProcessInstance processInstance = ksession.getProcessInstance(pi.getId());
        assertNotNull(processInstance);
        assertNodeNotTriggered(pi.getId(), "Output");
        Assertions.assertThat(getExecutorService().getInErrorRequests(new org.kie.api.runtime.query.QueryContext())).hasSize(1);
        Assertions.assertThat(getExecutorService().getInErrorRequests(new org.kie.api.runtime.query.QueryContext()).get(0).getErrorInfo()).hasSize(4);
        Assertions.assertThat(getExecutorService().getInErrorRequests(new org.kie.api.runtime.query.QueryContext()).get(0).getErrorInfo().get(0).getMessage()).isEqualTo("Internal Error");
        assertProcessInstanceActive(pi.getId());
        ksession.abortProcessInstance(pi.getId());
        assertProcessInstanceAborted(pi.getId());
    }
}

