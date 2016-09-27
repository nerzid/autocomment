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


package org.jbpm.test.functional.subprocess;

import java.util.ArrayList;
import qa.tools.ikeeper.annotation.BZ;
import org.kie.api.command.Command;
import org.kie.internal.command.CommandFactory;
import org.drools.core.command.runtime.process.CompleteWorkItemCommand;
import org.jbpm.workflow.instance.node.DynamicNodeInstance;
import org.jbpm.workflow.instance.node.DynamicUtils;
import java.util.HashMap;
import org.jbpm.test.JbpmTestCase;
import org.kie.api.runtime.KieSession;
import java.util.List;
import org.drools.core.command.runtime.process.RegisterWorkItemHandlerCommand;
import org.junit.Test;
import JbpmJUnitBaseTestCase.TestWorkItemHandler;
import org.jbpm.test.listener.TrackingProcessEventListener;

public class AdHocSubProcessTest extends JbpmTestCase {
    private static final String ADHOC = "org/jbpm/test/functional/subprocess/AdHocSubProcess.bpmn";

    private static final String ADHOC_ID = "org.jbpm.test.functional.subprocess.AdHocSubProcess";

    private static final String ADHOC_AUTOCOMPLETE = "org/jbpm/test/functional/subprocess/AdHocSubProcess-autocomplete.bpmn";

    private static final String ADHOC_AUTOCOMPLETE_ID = "org.jbpm.test.functional.subprocess.AdHocSubProcess-autocomplete";

    private static final String ADHOC_AUTOCOMPLETE2 = "org/jbpm/test/functional/subprocess/AdHocSubProcess-autocomplete2.bpmn";

    private static final String ADHOC_AUTOCOMPLETE2_ID = "org.jbpm.test.functional.subprocess.AdHocSubProcess-autocomplete2";

    public AdHocSubProcessTest() {
        super(false);
    }

    /**
     * Tests dynamic insertion of new work item node into the adhoc subprocess.
     * Uses DynamicUtils.
     */
    @Test(timeout = 30000)
    public void testAdHocSubprocessDynamicWorkItem() {
        KieSession kieSession = createKSession(AdHocSubProcessTest.ADHOC);
        TrackingProcessEventListener eventListener = new TrackingProcessEventListener();
        kieSession.addEventListener(eventListener);
        WorkflowProcessInstance pi = ((WorkflowProcessInstance) (kieSession.execute(((Command<?>) (CommandFactory.newStartProcess(AdHocSubProcessTest.ADHOC_ID))))));
        assertProcessStarted(eventListener, AdHocSubProcessTest.ADHOC_ID);
        assertTriggeredAndLeft(eventListener, "start");
        assertTriggered(eventListener, "adhoc");
        // dynamic node insert
        JbpmJUnitBaseTestCase.TestWorkItemHandler handler = getTestWorkItemHandler();
        RegisterWorkItemHandlerCommand registerCommand = new RegisterWorkItemHandlerCommand("addedWorkItem", handler);
        kieSession.execute(registerCommand);
        DynamicNodeInstance dynamicNodeInstance = ((DynamicNodeInstance) (pi.getNodeInstances().iterator().next()));
        DynamicUtils.addDynamicWorkItem(dynamicNodeInstance, kieSession, "addedWorkItem", new HashMap<String, Object>());
        WorkItem wi = handler.getWorkItem();
        CompleteWorkItemCommand completeCommand = new CompleteWorkItemCommand(wi.getId(), null);
        kieSession.execute(completeCommand);
        // end of the dynamic stuff
        assertEquals("addedWorkItem", wi.getName());
    }

    @BZ(value = "807187")
    @Test(timeout = 30000)
    public void testAdHocSubprocess() {
        KieSession kieSession = createKSession(AdHocSubProcessTest.ADHOC);
        TrackingProcessEventListener eventListener = new TrackingProcessEventListener();
        kieSession.addEventListener(eventListener);
        ProcessInstance pi = ((ProcessInstance) (kieSession.execute(((Command<?>) (CommandFactory.newStartProcess(AdHocSubProcessTest.ADHOC_ID))))));
        assertProcessStarted(eventListener, AdHocSubProcessTest.ADHOC_ID);
        assertTriggeredAndLeft(eventListener, "start");
        assertTriggered(eventListener, "adhoc");
        kieSession.execute(((Command<?>) (CommandFactory.newSignalEvent(pi.getId(), "script1", null))));
        assertTriggeredAndLeft(eventListener, "script1");
        kieSession.execute(((Command<?>) (CommandFactory.newSignalEvent(pi.getId(), "script2", null))));
        assertTriggeredAndLeft(eventListener, "script2");
        kieSession.execute(((Command<?>) (CommandFactory.newSignalEvent(pi.getId(), "script3", null))));
        assertTriggeredAndLeft(eventListener, "script3");
        assertTriggered(eventListener, "innerEnd");
        assertLeft(eventListener, "adhoc");
        assertProcessCompleted(eventListener, AdHocSubProcessTest.ADHOC_ID);
    }

    /**
     * Tests if adhoc subprocess autocompletes when numberOfActiveInstances ==
     * 0. Signals 2 tasks inside subprocess; the first completes, the second
     * stays active, then completes the second one => (activeInstances == 0) =>
     * subprocess is also completed.
     */
    @Test(timeout = 30000)
    public void testAdHocSubprocessAutocomplete() {
        KieSession kieSession = createKSession(AdHocSubProcessTest.ADHOC_AUTOCOMPLETE);
        TrackingProcessEventListener eventListener = new TrackingProcessEventListener();
        kieSession.addEventListener(eventListener);
        AdHocSubProcessTest.TestUserWorkItemHandler handler = new AdHocSubProcessTest.TestUserWorkItemHandler();
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(new RegisterWorkItemHandlerCommand("Human Task", handler));
        kieSession.execute(CommandFactory.newBatchExecution(commands));
        WorkflowProcessInstance pi = ((WorkflowProcessInstance) (kieSession.execute(((Command<?>) (CommandFactory.newStartProcess(AdHocSubProcessTest.ADHOC_AUTOCOMPLETE_ID))))));
        assertProcessStarted(eventListener, AdHocSubProcessTest.ADHOC_AUTOCOMPLETE_ID);
        assertTriggeredAndLeft(eventListener, "start");
        assertTriggered(eventListener, "adhoc");
        kieSession.execute(((Command<?>) (CommandFactory.newSignalEvent(pi.getId(), "task1", null))));
        assertTriggered(eventListener, "task1");
        kieSession.execute(((Command<?>) (CommandFactory.newSignalEvent(pi.getId(), "task2", null))));
        assertTriggered(eventListener, "task2");
        WorkItem wi1 = handler.getWorkItems().get(0);
        WorkItem wi2 = handler.getWorkItems().get(1);
        kieSession.getWorkItemManager().completeWorkItem(wi1.getId(), null);
        assertFalse(eventListener.wasNodeLeft("adhoc"));
        kieSession.getWorkItemManager().completeWorkItem(wi2.getId(), null);
        assertLeft(eventListener, "adhoc");
        assertProcessCompleted(eventListener, AdHocSubProcessTest.ADHOC_AUTOCOMPLETE_ID);
    }

    @BZ(value = "808070")
    @Test(timeout = 30000)
    public void testAdHocSubProcessAutoComplete2() {
        KieSession kieSession = createKSession(AdHocSubProcessTest.ADHOC_AUTOCOMPLETE2);
        TrackingProcessEventListener eventListener = new TrackingProcessEventListener();
        kieSession.addEventListener(eventListener);
        AdHocSubProcessTest.TestUserWorkItemHandler handler = new AdHocSubProcessTest.TestUserWorkItemHandler();
        kieSession.execute(new RegisterWorkItemHandlerCommand("Human Task", handler));
        WorkflowProcessInstance pi = ((WorkflowProcessInstance) (kieSession.execute(((Command<?>) (CommandFactory.newStartProcess(AdHocSubProcessTest.ADHOC_AUTOCOMPLETE2_ID))))));
        assertProcessStarted(eventListener, AdHocSubProcessTest.ADHOC_AUTOCOMPLETE2_ID);
        assertTriggeredAndLeft(eventListener, "start");
        assertTriggered(eventListener, "adhoc");
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newSignalEvent(pi.getId(), "task1", null));
        commands.add(CommandFactory.newSignalEvent(pi.getId(), "task2", null));
        kieSession.execute(((Command<?>) (CommandFactory.newBatchExecution(commands))));
        assertTriggered(eventListener, "task1");
        assertTriggered(eventListener, "task2");
        WorkItem wi = handler.getWorkItems().get(0);
        kieSession.getWorkItemManager().completeWorkItem(wi.getId(), null);
        assertLeft(eventListener, "adhoc");
        assertProcessCompleted(eventListener, AdHocSubProcessTest.ADHOC_AUTOCOMPLETE2_ID);
    }

    class TestUserWorkItemHandler implements WorkItemHandler {
        List<WorkItem> workItems = new ArrayList<WorkItem>();

        @Override
        public void executeWorkItem(WorkItem wi, WorkItemManager wim) {
            if (!(workItems.contains(wi))) {
                workItems.add(wi);
            } 
        }

        @Override
        public void abortWorkItem(WorkItem wi, WorkItemManager wim) {
            // dont want to abort
        }

        public List<WorkItem> getWorkItems() {
            return workItems;
        }
    }
}

