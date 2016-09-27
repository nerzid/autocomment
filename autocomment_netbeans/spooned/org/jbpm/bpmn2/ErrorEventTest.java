/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.jbpm.bpmn2;

import org.junit.After;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.BeforeClass;
import java.util.Collection;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.jbpm.bpmn2.objects.ExceptionOnPurposeHandler;
import java.util.HashMap;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.jbpm.workflow.instance.WorkflowRuntimeException;

@RunWith(value = Parameterized.class)
public class ErrorEventTest extends JbpmBpmn2TestCase {
    @Parameters
    public static Collection<Object[]> persistence() {
        Object[][] data = new Object[][]{ new Object[]{ false } , new Object[]{ true } };
        return Arrays.asList(data);
    }

    private Logger logger = LoggerFactory.getLogger(ErrorEventTest.class);

    private KieSession ksession;

    public ErrorEventTest(boolean persistence) {
        super(persistence);
    }

    @BeforeClass
    public static void setup() throws Exception {
        setUpDataSource();
    }

    @After
    public void dispose() {
        if ((ksession) != null) {
            ksession.dispose();
            ksession = null;
        } 
    }

    private ProcessEventListener LOGGING_EVENT_LISTENER = new DefaultProcessEventListener() {
        @Override
        public void afterNodeLeft(ProcessNodeLeftEvent event) {
            logger.info("After node left {}", event.getNodeInstance().getNodeName());
        }

        @Override
        public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
            logger.info("After node triggered {}", event.getNodeInstance().getNodeName());
        }

        @Override
        public void beforeNodeLeft(ProcessNodeLeftEvent event) {
            logger.info("Before node left {}", event.getNodeInstance().getNodeName());
        }

        @Override
        public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
            logger.info("Before node triggered {}", event.getNodeInstance().getNodeName());
        }
    };

    @Test
    public void testEventSubprocessError() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EventSubprocessError.bpmn2");
        final List<Long> executednodes = new ArrayList<Long>();
        ProcessEventListener listener = new DefaultProcessEventListener() {
            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName().equals("Script Task 1")) {
                    executednodes.add(event.getNodeInstance().getId());
                } 
            }
        };
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(listener);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ProcessInstance processInstance = ksession.startProcess("BPMN2-EventSubprocessError");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        ksession.addEventListener(listener);
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getId(), "start", "User Task 1", "end", "Sub Process 1", "start-sub", "Script Task 1", "end-sub");
        assertEquals(1, executednodes.size());
    }

    @Test
    public void testEventSubprocessErrorThrowOnTask() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EventSubprocessError.bpmn2");
        final List<Long> executednodes = new ArrayList<Long>();
        ProcessEventListener listener = new DefaultProcessEventListener() {
            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName().equals("Script Task 1")) {
                    executednodes.add(event.getNodeInstance().getId());
                } 
            }
        };
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(listener);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new TestWorkItemHandler() {
            @Override
            public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
                throw new org.jbpm.bpmn2.objects.MyError();
            }

            @Override
            public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
                manager.abortWorkItem(workItem.getId());
            }
        });
        ProcessInstance processInstance = ksession.startProcess("BPMN2-EventSubprocessError");
        assertProcessInstanceFinished(processInstance, ksession);
        assertProcessInstanceAborted(processInstance);
        assertNodeTriggered(processInstance.getId(), "start", "User Task 1", "Sub Process 1", "start-sub", "Script Task 1", "end-sub");
        assertEquals(1, executednodes.size());
    }

    @Test
    public void testEventSubprocessErrorWithErrorCode() throws Exception {
        KieBase kbase = createKnowledgeBase("subprocess/EventSubprocessErrorHandlingWithErrorCode.bpmn2");
        final List<Long> executednodes = new ArrayList<Long>();
        ProcessEventListener listener = new DefaultProcessEventListener() {
            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName().equals("Script2")) {
                    executednodes.add(event.getNodeInstance().getId());
                } 
            }
        };
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(listener);
        ProcessInstance processInstance = ksession.startProcess("order-fulfillment-bpm.ccc");
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getId(), "start", "Script1", "starterror", "Script2", "end2", "eventsubprocess");
        assertProcessVarValue(processInstance, "CapturedException", "java.lang.RuntimeException: XXX");
        assertEquals(1, executednodes.size());
    }

    @Test
    public void testEventSubprocessErrorWithOutErrorCode() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("subprocess/EventSubprocessErrorHandlingWithOutErrorCode.bpmn2");
        final List<Long> executednodes = new ArrayList<Long>();
        ProcessEventListener listener = new DefaultProcessEventListener() {
            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName().equals("Script2")) {
                    executednodes.add(event.getNodeInstance().getId());
                } 
            }
        };
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(listener);
        ProcessInstance processInstance = ksession.startProcess("order-fulfillment-bpm.ccc");
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getId(), "start", "Script1", "starterror", "Script2", "end2", "eventsubprocess");
        assertProcessVarValue(processInstance, "CapturedException", "java.lang.RuntimeException: XXX");
        assertEquals(1, executednodes.size());
    }

    @Test
    public void testErrorBoundaryEvent() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ErrorBoundaryEventInterrupting.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask", new org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession.startProcess("ErrorBoundaryEvent");
        assertProcessInstanceFinished(processInstance, ksession);
    }

    @Test
    public void testErrorBoundaryEventOnTask() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ErrorBoundaryEventOnTask.bpmn2");
        ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        ProcessInstance processInstance = ksession.startProcess("BPMN2-ErrorBoundaryEventOnTask");
        List<WorkItem> workItems = handler.getWorkItems();
        assertEquals(2, workItems.size());
        WorkItem workItem = workItems.get(0);
        if (!("john".equalsIgnoreCase(((String) (workItem.getParameter("ActorId")))))) {
            workItem = workItems.get(1);
        } 
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        assertProcessInstanceAborted(processInstance);
        assertNodeTriggered(processInstance.getId(), "start", "split", "User Task", "User task error attached", "error end event");
        assertNotNodeTriggered(processInstance.getId(), "Script Task", "error1", "error2");
    }

    @Test
    public void testErrorBoundaryEventOnServiceTask() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ErrorBoundaryEventOnServiceTask.bpmn2");
        ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", new org.jbpm.bpmn2.handler.ServiceTaskHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s", "test");
        ProcessInstance processInstance = ksession.startProcess("BPMN2-ErrorBoundaryEventOnServiceTask", params);
        List<WorkItem> workItems = handler.getWorkItems();
        assertEquals(1, workItems.size());
        ksession.getWorkItemManager().completeWorkItem(workItems.get(0).getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getId(), "start", "split", "User Task", "Service task error attached", "end0", "Script Task", "error2");
    }

    @Test
    public void testCatchErrorBoundaryEventOnTask() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ErrorBoundaryEventOnTask.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new TestWorkItemHandler() {
            @Override
            public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
                if (workItem.getParameter("ActorId").equals("mary")) {
                    throw new org.jbpm.bpmn2.objects.MyError();
                } 
            }

            @Override
            public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
                manager.abortWorkItem(workItem.getId());
            }
        });
        ProcessInstance processInstance = ksession.startProcess("BPMN2-ErrorBoundaryEventOnTask");
        assertProcessInstanceActive(processInstance);
        assertNodeTriggered(processInstance.getId(), "start", "split", "User Task", "User task error attached", "Script Task", "error1", "error2");
    }

    @Test
    public void testErrorSignallingExceptionServiceTask() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ExceptionServiceProcess-ErrorSignalling.bpmn2");
        ksession = createKnowledgeSession(kbase);
        StandaloneBPMNProcessTest.runTestErrorSignallingExceptionServiceTask(ksession);
    }

    @Test
    public void testSignallingExceptionServiceTask() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ExceptionServiceProcess-Signalling.bpmn2");
        ksession = createKnowledgeSession(kbase);
        StandaloneBPMNProcessTest.runTestSignallingExceptionServiceTask(ksession);
    }

    @Test
    public void testEventSubProcessErrorWithScript() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EventSubProcessErrorWithScript.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Request Handler", new org.jbpm.bpmn2.handler.SignallingTaskHandlerDecorator(ExceptionOnPurposeHandler.class, "Error-90277"));
        ksession.getWorkItemManager().registerWorkItemHandler("Error Handler", new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler());
        ProcessInstance processInstance = ksession.startProcess("com.sample.process");
        assertProcessInstanceAborted(processInstance);
        assertEquals("90277", ((WorkflowProcessInstance) (processInstance)).getOutcome());
    }

    @Test
    public void testErrorBoundaryEventOnEntry() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-BoundaryErrorEventCatchingOnEntryException.bpmn2");
        ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        ProcessInstance processInstance = ksession.startProcess("BoundaryErrorEventOnEntry");
        assertProcessInstanceActive(processInstance.getId(), ksession);
        assertEquals(1, handler.getWorkItems().size());
    }

    @Test
    public void testErrorBoundaryEventOnExit() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-BoundaryErrorEventCatchingOnExitException.bpmn2");
        ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        ProcessInstance processInstance = ksession.startProcess("BoundaryErrorEventOnExit");
        assertProcessInstanceActive(processInstance.getId(), ksession);
        WorkItem workItem = handler.getWorkItem();
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertEquals(1, handler.getWorkItems().size());
    }

    @Test
    public void testBoundaryErrorEventDefaultHandlerWithErrorCodeWithStructureRef() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-BoundaryErrorEventDefaultHandlerWithErrorCodeWithStructureRef.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ErrorEventTest.ExceptionWorkItemHandler handler = new ErrorEventTest.ExceptionWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        try {
            ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello");
            fail("This is not a default handler. So WorkflowRuntimeException must be thrown");
        } catch (WorkflowRuntimeException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testBoundaryErrorEventDefaultHandlerWithErrorCodeWithoutStructureRef() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-BoundaryErrorEventDefaultHandlerWithErrorCodeWithoutStructureRef.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ErrorEventTest.ExceptionWorkItemHandler handler = new ErrorEventTest.ExceptionWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        try {
            ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello");
            fail("This is not a default handler. So WorkflowRuntimeException must be thrown");
        } catch (WorkflowRuntimeException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testBoundaryErrorEventDefaultHandlerWithoutErrorCodeWithStructureRef() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-BoundaryErrorEventDefaultHandlerWithoutErrorCodeWithStructureRef.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ErrorEventTest.ExceptionWorkItemHandler handler = new ErrorEventTest.ExceptionWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello");
        assertNodeTriggered(processInstance.getId(), "Start", "User Task", "MyBoundaryErrorEvent");
    }

    @Test
    public void testBoundaryErrorEventDefaultHandlerWithoutErrorCodeWithoutStructureRef() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-BoundaryErrorEventDefaultHandlerWithoutErrorCodeWithoutStructureRef.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ErrorEventTest.ExceptionWorkItemHandler handler = new ErrorEventTest.ExceptionWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello");
        assertNodeTriggered(processInstance.getId(), "Start", "User Task", "MyBoundaryErrorEvent");
    }

    @Test
    public void testBoundaryErrorEventSubProcessExceptionMapping() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-BoundaryErrorEventSubProcessExceptionMapping.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ErrorEventTest.ExceptionWorkItemHandler handler = new ErrorEventTest.ExceptionWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello");
        assertEquals("java.lang.RuntimeException", getProcessVarValue(processInstance, "var1"));
    }

    @Test
    public void testBoundaryErrorEventStructureRef() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-BoundaryErrorEventStructureRef.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ErrorEventTest.ExceptionWorkItemHandler handler = new ErrorEventTest.ExceptionWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello");
        assertNodeTriggered(processInstance.getId(), "Start", "User Task", "MyBoundaryErrorEvent");
    }

    class ExceptionWorkItemHandler implements WorkItemHandler {
        @Override
        public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
            throw new RuntimeException();
        }

        @Override
        public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        }
    }
}

