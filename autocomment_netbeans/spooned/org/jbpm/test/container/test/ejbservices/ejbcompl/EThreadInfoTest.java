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


package org.jbpm.test.container.test.ejbservices.ejbcompl;

import org.jbpm.test.container.AbstractRuntimeEJBServicesTest;
import org.junit.AfterClass;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;
import java.util.Collection;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.jbpm.test.container.groups.EAP;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.internal.query.QueryContext;
import org.jbpm.test.container.mock.RestService;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.task.model.TaskSummary;
import org.junit.Test;
import org.jbpm.services.api.model.VariableDesc;

@Category(value = { EAP.class })
public class EThreadInfoTest extends AbstractRuntimeEJBServicesTest {
    private static final Object LOCK = new Object();

    public static final int TIMEOUT = 4000;

    @BeforeClass
    public static void startRestService() {
        RestService.start();
    }

    @Before
    @Override
    public void deployKieJar() {
        if ((kieJar) == null) {
            kieJar = archive.deployEJBComplianceKieJar().getIdentifier();
        } 
        RuntimeManager manager = deploymentService.getRuntimeManager(kieJar);
        RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        engine.getKieSession().addEventListener(new DefaultProcessEventListener() {
            @Override
            public void afterProcessCompleted(ProcessCompletedEvent event) {
                if (event.getProcessInstance().getProcessId().equals("org.jboss.qa.bpms.ThreadInfo")) {
                    synchronized(EThreadInfoTest.LOCK) {
                        EThreadInfoTest.LOCK.notifyAll();
                    }
                } 
            }

            @Override
            public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
                if (event.getNodeInstance().getNodeName().equals("User Task")) {
                    synchronized(EThreadInfoTest.LOCK) {
                        EThreadInfoTest.LOCK.notifyAll();
                    }
                } 
            }
        });
    }

    @AfterClass
    public static void stopRestService() {
        RestService.stop();
    }

    @Test
    public void testTimerThreadInfo() throws Exception {
        Long pid = startProcessInstance(THREAD_INFO_PROCESS_ID);
        processService.signalProcessInstance(pid, "start", "timer");
        waitTillPrepared();
        // to make sure the timer is completed
        Thread.sleep(EThreadInfoTest.TIMEOUT);
        System.out.println("After LOCK");
        Assertions.assertThat(hasNodeLeft(pid, "timer")).isTrue();
        Assertions.assertThat(hasNodeLeft(pid, "Timer")).isTrue();
        Collection<VariableDesc> stackTraceHistory = getStackTrace(pid);
        Collection<VariableDesc> threadNameHistory = getThreadName(pid);
        System.out.println("====stackTraceHistory====");
        System.out.println(stackTraceHistory);
        System.out.println("====stackTraceHistoryLast====");
        System.out.println(stackTraceHistory.iterator().next().getNewValue());
        System.out.println("====stackTraceHistorySize====");
        System.out.println(stackTraceHistory.size());
        System.out.println("====threadNameHistory====");
        System.out.println(threadNameHistory);
        System.out.println("====threadNameHistoryLast====");
        System.out.println(threadNameHistory.iterator().next().getNewValue());
        Assertions.assertThat(threadNameHistory.iterator().next().getNewValue()).startsWith("EJB");
    }

    @Test
    public void testLogThreadInfo() throws Exception {
        Long pid = startProcessInstance(THREAD_INFO_PROCESS_ID);
        processService.signalProcessInstance(pid, "start", "log");
        waitTillPrepared();
        // to make sure the async task is completed
        Thread.sleep(EThreadInfoTest.TIMEOUT);
        Assertions.assertThat(hasNodeLeft(pid, "log")).isTrue();
        Assertions.assertThat(hasNodeLeft(pid, "Log")).isTrue();
        Collection<VariableDesc> stackTraceHistory = getStackTrace(pid);
        Collection<VariableDesc> threadNameHistory = getThreadName(pid);
        System.out.println("====stackTraceHistory====");
        System.out.println(stackTraceHistory);
        System.out.println("====stackTraceHistoryLast====");
        System.out.println(stackTraceHistory.iterator().next().getNewValue());
        System.out.println("====stackTraceHistorySize====");
        System.out.println(stackTraceHistory.size());
        System.out.println("====threadNameHistory====");
        System.out.println(threadNameHistory);
        System.out.println("====threadNameHistoryLast====");
        System.out.println(threadNameHistory.iterator().next().getNewValue());
        Assertions.assertThat(threadNameHistory.iterator().next().getNewValue()).startsWith("EJB");
    }

    @Test
    public void testRESTThreadInfo() throws Exception {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("url", RestService.PING_URL);
        parameters.put("method", "GET");
        Long pid = startProcessInstance(THREAD_INFO_PROCESS_ID, parameters);
        processService.signalProcessInstance(pid, "start", "rest");
        waitTillPrepared();
        // to make sure the rest request is completed
        Thread.sleep(EThreadInfoTest.TIMEOUT);
        Assertions.assertThat(hasNodeLeft(pid, "rest")).isTrue();
        Assertions.assertThat(hasNodeLeft(pid, "REST")).isTrue();
        Collection<VariableDesc> result = runtimeDataService.getVariableHistory(pid, "result", new QueryContext());
        Assertions.assertThat(result).hasSize(1);
        Assertions.assertThat(result.iterator().next().getNewValue()).isEqualTo("pong");
        Collection<VariableDesc> status = runtimeDataService.getVariableHistory(pid, "status", new QueryContext());
        Assertions.assertThat(status).hasSize(1);
        Assertions.assertThat(status.iterator().next().getNewValue()).isEqualTo("200");
        Collection<VariableDesc> statusMsg = runtimeDataService.getVariableHistory(pid, "statusMsg", new QueryContext());
        Assertions.assertThat(statusMsg).hasSize(1);
        Assertions.assertThat(statusMsg.iterator().next().getNewValue()).contains("successfully completed Ok");
        Collection<VariableDesc> stackTraceHistory = getStackTrace(pid);
        Collection<VariableDesc> threadNameHistory = getThreadName(pid);
        System.out.println("====stackTraceHistory====");
        System.out.println(stackTraceHistory);
        System.out.println("====stackTraceHistoryLast====");
        System.out.println(stackTraceHistory.iterator().next().getNewValue());
        System.out.println("====stackTraceHistorySize====");
        System.out.println(stackTraceHistory.size());
        System.out.println("====threadNameHistory====");
        System.out.println(threadNameHistory);
        System.out.println("====threadNameHistoryLast====");
        System.out.println(threadNameHistory.iterator().next().getNewValue());
        Assertions.assertThat(threadNameHistory.iterator().next().getNewValue()).startsWith("EJB");
    }

    @Test
    public void testScriptThreadInfo() throws Exception {
        Long pid = startProcessInstance(THREAD_INFO_PROCESS_ID);
        processService.signalProcessInstance(pid, "start", "script");
        waitTillPrepared();
        // to make sure the script task is completed
        Thread.sleep(EThreadInfoTest.TIMEOUT);
        Assertions.assertThat(hasNodeLeft(pid, "script")).isTrue();
        Assertions.assertThat(hasNodeLeft(pid, "Script")).isTrue();
        Collection<VariableDesc> stackTraceHistory = getStackTrace(pid);
        Collection<VariableDesc> threadNameHistory = getThreadName(pid);
        System.out.println("====stackTraceHistory====");
        System.out.println(stackTraceHistory);
        System.out.println("====stackTraceHistoryLast====");
        System.out.println(stackTraceHistory.iterator().next().getNewValue());
        System.out.println("====stackTraceHistorySize====");
        System.out.println(stackTraceHistory.size());
        System.out.println("====threadNameHistory====");
        System.out.println(threadNameHistory);
        System.out.println("====threadNameHistoryLast====");
        System.out.println(threadNameHistory.iterator().next().getNewValue());
        Assertions.assertThat(threadNameHistory.iterator().next().getNewValue()).startsWith("EJB");
    }

    @Test
    public void testUserTaskThreadInfo() throws Exception {
        Long pid = startProcessInstance(THREAD_INFO_PROCESS_ID);
        processService.signalProcessInstance(pid, "start", "usertask");
        waitTillPrepared();
        // to make sure the task is created and available to start
        Thread.sleep(EThreadInfoTest.TIMEOUT);
        List<TaskSummary> taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new org.kie.internal.query.QueryFilter());
        Assertions.assertThat(taskSummaries).isNotNull().hasSize(1);
        userTaskService.start(taskSummaries.get(0).getId(), "john");
        userTaskService.complete(taskSummaries.get(0).getId(), "john", new HashMap<String, Object>());
        Assertions.assertThat(hasNodeLeft(pid, "usertask")).isTrue();
        Assertions.assertThat(hasNodeLeft(pid, "User Task")).isTrue();
        Collection<VariableDesc> stackTraceHistory = getStackTrace(pid);
        Collection<VariableDesc> threadNameHistory = getThreadName(pid);
        System.out.println("====stackTraceHistory====");
        System.out.println(stackTraceHistory);
        System.out.println("====stackTraceHistoryLast====");
        System.out.println(stackTraceHistory.iterator().next().getNewValue());
        System.out.println("====stackTraceHistorySize====");
        System.out.println(stackTraceHistory.size());
        System.out.println("====threadNameHistory====");
        System.out.println(threadNameHistory);
        System.out.println("====threadNameHistoryLast====");
        System.out.println(threadNameHistory.iterator().next().getNewValue());
        // in this scenario we have to check the middle value, so it is the old one in the last history entry
        Assertions.assertThat(threadNameHistory.iterator().next().getOldValue()).startsWith("EJB");
    }

    @Test
    public void testRuleThreadInfo() throws Exception {
        Long pid = startProcessInstance(THREAD_INFO_PROCESS_ID);
        processService.signalProcessInstance(pid, "start", "rule");
        waitTillPrepared();
        // to make sure the rule is fired
        Thread.sleep(EThreadInfoTest.TIMEOUT);
        System.out.println("After LOCK");
        Assertions.assertThat(hasNodeLeft(pid, "rule")).isTrue();
        Assertions.assertThat(hasNodeLeft(pid, "Rule")).isTrue();
        Collection<VariableDesc> stackTraceHistory = getStackTrace(pid);
        Collection<VariableDesc> threadNameHistory = getThreadName(pid);
        System.out.println("====stackTraceHistory====");
        System.out.println(stackTraceHistory);
        System.out.println("====stackTraceHistoryLast====");
        System.out.println(stackTraceHistory.iterator().next().getNewValue());
        System.out.println("====stackTraceHistorySize====");
        System.out.println(stackTraceHistory.size());
        System.out.println("====threadNameHistory====");
        System.out.println(threadNameHistory);
        System.out.println("====threadNameHistoryLast====");
        System.out.println(threadNameHistory.iterator().next().getNewValue());
        Assertions.assertThat(threadNameHistory.iterator().next().getNewValue()).startsWith("EJB");
    }

    @Test
    public void testEmbeddedThreadInfo() throws Exception {
        Long pid = startProcessInstance(THREAD_INFO_PROCESS_ID);
        processService.signalProcessInstance(pid, "start", "embedded");
        waitTillPrepared();
        // to make sure the embedded process is completed
        Thread.sleep(EThreadInfoTest.TIMEOUT);
        System.out.println("After LOCK");
        Assertions.assertThat(hasNodeLeft(pid, "embedded")).isTrue();
        Assertions.assertThat(hasNodeLeft(pid, "Embedded")).isTrue();
        Collection<VariableDesc> stackTraceHistory = getStackTrace(pid);
        Collection<VariableDesc> threadNameHistory = getThreadName(pid);
        System.out.println("====stackTraceHistory====");
        System.out.println(stackTraceHistory);
        System.out.println("====stackTraceHistoryLast====");
        System.out.println(stackTraceHistory.iterator().next().getNewValue());
        System.out.println("====stackTraceHistorySize====");
        System.out.println(stackTraceHistory.size());
        System.out.println("====threadNameHistory====");
        System.out.println(threadNameHistory);
        System.out.println("====threadNameHistoryLast====");
        System.out.println(threadNameHistory.iterator().next().getNewValue());
        Assertions.assertThat(threadNameHistory.iterator().next().getNewValue()).startsWith("EJB");
    }

    @Test
    public void testSubProcessThreadInfo() throws Exception {
        Long pid = startProcessInstance(THREAD_INFO_PROCESS_ID);
        processService.signalProcessInstance(pid, "start", "subprocess");
        waitTillPrepared();
        // to make sure the subprocess is completed
        Thread.sleep(EThreadInfoTest.TIMEOUT);
        System.out.println("After LOCK");
        Assertions.assertThat(hasNodeLeft(pid, "subprocess")).isTrue();
        Assertions.assertThat(hasNodeLeft(pid, "HelloWorld_1.0")).isTrue();
        Collection<VariableDesc> stackTraceHistory = getStackTrace(pid);
        Collection<VariableDesc> threadNameHistory = getThreadName(pid);
        System.out.println("====stackTraceHistory====");
        System.out.println(stackTraceHistory);
        System.out.println("====stackTraceHistoryLast====");
        System.out.println(stackTraceHistory.iterator().next().getNewValue());
        System.out.println("====stackTraceHistorySize====");
        System.out.println(stackTraceHistory.size());
        System.out.println("====threadNameHistory====");
        System.out.println(threadNameHistory);
        System.out.println("====threadNameHistoryLast====");
        System.out.println(threadNameHistory.iterator().next().getNewValue());
        Assertions.assertThat(threadNameHistory.iterator().next().getNewValue()).startsWith("EJB");
    }

    // ========== PRIVATE METHODS ==========
    private Collection<VariableDesc> getStackTrace(Long pid) {
        QueryContext query = new QueryContext();
        query.setOrderBy("id");
        query.setAscending(false);
        return runtimeDataService.getVariableHistory(pid, "stackTrace", query);
    }

    private Collection<VariableDesc> getThreadName(Long pid) {
        QueryContext query = new QueryContext();
        query.setOrderBy("id");
        query.setAscending(false);
        return runtimeDataService.getVariableHistory(pid, "threadName", query);
    }

    private void waitTillPrepared() throws InterruptedException {
        synchronized(EThreadInfoTest.LOCK) {
            EThreadInfoTest.LOCK.wait();
        }
    }
}

