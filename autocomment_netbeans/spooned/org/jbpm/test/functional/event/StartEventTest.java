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


package org.jbpm.test.functional.event;

import java.util.ArrayList;
import org.assertj.core.api.Assertions;
import org.kie.api.command.Command;
import org.jbpm.test.listener.IterableProcessEventListener;
import org.jbpm.test.JbpmTestCase;
import org.kie.api.runtime.KieSession;
import java.util.List;
import org.junit.Test;
import org.jbpm.test.listener.TrackingProcessEventListener;

public class StartEventTest extends JbpmTestCase {
    private static final String CONDITIONAL = "org/jbpm/test/functional/event/StartEvent-conditional.bpmn2";

    private static final String CONDITIONAL_ID = "org.jbpm.test.functional.event.StartEvent-conditional";

    private static final String MESSAGE = "org/jbpm/test/functional/event/StartEvent-message.bpmn2";

    private static final String MESSAGE_ID = "org.jbpm.test.functional.event.StartEvent-message";

    private static final String NONE = "org/jbpm/test/functional/event/StartEvent-none.bpmn2";

    private static final String NONE_ID = "org.jbpm.test.functional.event.StartEvent-none";

    private static final String SIGNAL = "org/jbpm/test/functional/event/StartEvent-signal.bpmn2";

    private static final String SIGNAL_ID = "org.jbpm.test.functional.event.StartEvent-signal";

    private static final String TIMER_CYCLE = "org/jbpm/test/functional/event/StartEvent-timer-cycle.bpmn2";

    private static final String TIMER_CYCLE_ID = "org.jbpm.test.functional.event.StartEvent-timer-cycle";

    private static final String TIMER_DURATION = "org/jbpm/test/functional/event/StartEvent-timer-duration.bpmn2";

    private static final String TIMER_DURATION_ID = "org.jbpm.test.functional.event.StartEvent-timer-duration";

    public StartEventTest() {
        super(false);
    }

    @Test(timeout = 30000)
    public void testConditionalStartEvent() {
        KieSession ksession = createKSession(StartEventTest.CONDITIONAL);
        IterableProcessEventListener events = new IterableProcessEventListener();
        TrackingProcessEventListener process = new TrackingProcessEventListener();
        ksession.addEventListener(events);
        ksession.addEventListener(process);
        List<Command<?>> cmds = new ArrayList<Command<?>>();
        cmds.add(getCommands().newInsert("condition"));
        cmds.add(getCommands().newFireAllRules());
        ksession.execute(getCommands().newBatchExecution(cmds, null));
        Assertions.assertThat(process.wasProcessStarted(StartEventTest.CONDITIONAL_ID)).isTrue();
        Assertions.assertThat(process.wasProcessCompleted(StartEventTest.CONDITIONAL_ID)).isTrue();
        assertProcessStarted(events, StartEventTest.CONDITIONAL_ID);
        assertNextNode(events, "start");
        assertNextNode(events, "script");
        assertNextNode(events, "end");
        assertProcessCompleted(events, StartEventTest.CONDITIONAL_ID);
        Assertions.assertThat(events.hasNext()).isFalse();
    }

    @Test(timeout = 30000)
    public void testMessageStartEvent() {
        KieSession ksession = createKSession(StartEventTest.MESSAGE);
        IterableProcessEventListener events = new IterableProcessEventListener();
        TrackingProcessEventListener process = new TrackingProcessEventListener();
        ksession.addEventListener(events);
        ksession.addEventListener(process);
        Command<?> cmd = getCommands().newSignalEvent("Message-type", null);
        ksession.execute(cmd);
        Assertions.assertThat(process.wasProcessStarted(StartEventTest.MESSAGE_ID)).isTrue();
        Assertions.assertThat(process.wasProcessCompleted(StartEventTest.MESSAGE_ID)).isTrue();
        assertProcessStarted(events, StartEventTest.MESSAGE_ID);
        assertNextNode(events, "start");
        assertNextNode(events, "script");
        assertNextNode(events, "end");
        assertProcessCompleted(events, StartEventTest.MESSAGE_ID);
        Assertions.assertThat(events.hasNext()).isFalse();
    }

    @Test(timeout = 30000)
    public void testNoneStartEvent() {
        KieSession ksession = createKSession(StartEventTest.NONE);
        IterableProcessEventListener events = new IterableProcessEventListener();
        TrackingProcessEventListener process = new TrackingProcessEventListener();
        ksession.addEventListener(events);
        ksession.addEventListener(process);
        Command<?> cmd = getCommands().newStartProcess(StartEventTest.NONE_ID);
        ksession.execute(cmd);
        Assertions.assertThat(process.wasProcessStarted(StartEventTest.NONE_ID)).isTrue();
        Assertions.assertThat(process.wasProcessCompleted(StartEventTest.NONE_ID)).isTrue();
        assertProcessStarted(events, StartEventTest.NONE_ID);
        assertNextNode(events, "start");
        assertNextNode(events, "script");
        assertNextNode(events, "end");
        assertProcessCompleted(events, StartEventTest.NONE_ID);
        Assertions.assertThat(events.hasNext()).isFalse();
    }

    @Test(timeout = 30000)
    public void testSignalStartEvent() {
        KieSession ksession = createKSession(StartEventTest.SIGNAL);
        IterableProcessEventListener events = new IterableProcessEventListener();
        TrackingProcessEventListener process = new TrackingProcessEventListener();
        ksession.addEventListener(events);
        ksession.addEventListener(process);
        Command<?> cmd = getCommands().newSignalEvent("start", null);
        ksession.execute(cmd);
        Assertions.assertThat(process.wasProcessStarted(StartEventTest.SIGNAL_ID)).isTrue();
        Assertions.assertThat(process.wasProcessCompleted(StartEventTest.SIGNAL_ID)).isTrue();
        assertProcessStarted(events, StartEventTest.SIGNAL_ID);
        assertNextNode(events, "start");
        assertNextNode(events, "script");
        assertNextNode(events, "end");
        assertProcessCompleted(events, StartEventTest.SIGNAL_ID);
        Assertions.assertThat(events.hasNext()).isFalse();
    }

    @Test(timeout = 30000)
    public void testRecurringTimerStartEvent() throws Exception {
        KieSession ksession = createKSession(StartEventTest.TIMER_CYCLE);
        TrackingProcessEventListener process = new TrackingProcessEventListener();
        ksession.addEventListener(process);
        ksession.fireAllRules();
        assertTrue("The process did not start on time!", process.waitForProcessToStart(1000));
        assertTrue("The process did not complete on time!", process.waitForProcessToComplete(1000));
        Assertions.assertThat(process.wasProcessStarted(StartEventTest.TIMER_CYCLE_ID)).isTrue();
        Assertions.assertThat(process.wasProcessCompleted(StartEventTest.TIMER_CYCLE_ID)).isTrue();
        process.clear();
        for (int i = 0; i < 10; i++) {
            assertTrue("The process was not triggered on time!", process.waitForProcessToStart(1000));
            assertTrue("The process did not complete on time!", process.waitForProcessToComplete(1000));
            Assertions.assertThat(process.wasProcessStarted(StartEventTest.TIMER_CYCLE_ID)).isTrue();
            Assertions.assertThat(process.wasProcessCompleted(StartEventTest.TIMER_CYCLE_ID)).isTrue();
            process.clear();
        }
    }

    @Test(timeout = 30000)
    public void testDelayingTimerStartEvent() throws Exception {
        KieSession ksession = createKSession(StartEventTest.TIMER_DURATION);
        TrackingProcessEventListener process = new TrackingProcessEventListener();
        ksession.addEventListener(process);
        ksession.fireAllRules();
        assertTrue("The process did not start on time!", process.waitForProcessToStart(1000));
        assertTrue("The process did not complete on time!", process.waitForProcessToComplete(1000));
        ksession.fireAllRules();
        Assertions.assertThat(process.wasProcessStarted(StartEventTest.TIMER_DURATION_ID)).isTrue();
        Assertions.assertThat(process.wasProcessCompleted(StartEventTest.TIMER_DURATION_ID)).isTrue();
        process.clear();
    }
}
