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

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.jbpm.test.listener.IterableProcessEventListener.CachedProcessStartedEvent;
import java.util.HashMap;
import org.junit.Ignore;
import org.jbpm.test.listener.IterableProcessEventListener;
import org.jbpm.test.JbpmTestCase;
import org.kie.api.runtime.KieSession;
import java.util.Map;
import org.kie.api.runtime.process.ProcessInstance;
import org.junit.Test;
import org.jbpm.test.listener.TrackingProcessEventListener;

public class SubprocessesTest extends JbpmTestCase {
    private static final String P2_START = "Start";

    private static final String P2_GATEWAY_START = "Gateway";

    private static final String P2_GATEWAY_END = "Gateway";

    private static final String P2_SIGNAL_END = "Signal";

    private static final String P2_END = "End";

    private static final String P3_START = "Start";

    private static final String P3_SIGNAL = "Signal";

    private static final String P3_SCRIPT = "Set variable";

    private static final String P3_END = "End";

    private static final String PROCESS_PATH_1 = "org/jbpm/test/functional/subprocess/Subprocesses-first.bpmn";

    private static final String PROCESS_ID_1 = "org.jbpm.test.functional.subprocess.Subprocesses-first";

    private static final String PROCESS_PATH_2 = "org/jbpm/test/functional/subprocess/Subprocesses-second.bpmn2";

    private static final String PROCESS_ID_2 = "org.jbpm.test.functional.subprocess.Subprocesses-second";

    private static final String PROCESS_PATH_3 = "org/jbpm/test/functional/subprocess/Subprocesses-third.bpmn2";

    private static final String PROCESS_ID_3 = "org.jbpm.test.functional.subprocess.Subprocesses-third";

    private static final String HELLO_WORLD_PROCESS = "org/jbpm/test/functional/common/HelloWorldProcess1.bpmn";

    private static final String HELLO_WORLD_PROCESS_ID = "org.jbpm.test.functional.common.HelloWorldProcess1";

    private static final boolean JAVA8 = System.getProperty("java.version").contains("1.8");

    private KieSession ksession;

    public SubprocessesTest() {
        super(false);
    }

    @Before
    public void init() throws Exception {
        ksession = createKSession(SubprocessesTest.PROCESS_PATH_1, SubprocessesTest.PROCESS_PATH_2, SubprocessesTest.PROCESS_PATH_3, SubprocessesTest.HELLO_WORLD_PROCESS);
    }

    @Test(timeout = 30000)
    public void testEmbedded() {
        TrackingProcessEventListener process = runProcess(ksession, "embedded");
        Assertions.assertThat(process.wasProcessCompleted(SubprocessesTest.PROCESS_ID_1)).isTrue();
        Assertions.assertThat(process.wasNodeTriggered("embedded")).isTrue();
        Assertions.assertThat(process.wasNodeLeft("embedded")).isTrue();
        Assertions.assertThat(process.wasNodeLeft("print info")).isTrue();
    }

    @Test(timeout = 30000)
    public void testReusable() {
        TrackingProcessEventListener process = runProcess(ksession, "external");
        Assertions.assertThat(process.wasProcessCompleted(SubprocessesTest.PROCESS_ID_1)).isTrue();
        Assertions.assertThat(process.wasProcessCompleted(SubprocessesTest.HELLO_WORLD_PROCESS_ID)).isTrue();
    }

    @Test(timeout = 30000)
    public void testMissingProcess() {
        try {
            runProcess(ksession, "missing");
        } catch (RuntimeException ex) {
            Assertions.assertThat(ex.getCause()).isNotNull();
            Assertions.assertThat(ex.getCause().getMessage()).contains("Could not find process missingProcess");
        }
    }

    @Test(timeout = 30000)
    public void testProcessStartFromScript() {
        TrackingProcessEventListener process = runProcess(ksession, "script");
        Assertions.assertThat(process.wasProcessCompleted(SubprocessesTest.PROCESS_ID_1)).isTrue();
        Assertions.assertThat(process.wasProcessCompleted(SubprocessesTest.HELLO_WORLD_PROCESS_ID)).isTrue();
    }

    @Test(timeout = 30000)
    public void testIndependent() {
        IterableProcessEventListener process = new IterableProcessEventListener();
        ksession.addEventListener(process);
        // start the process and track the progress
        ProcessInstance pi = ksession.startProcess(SubprocessesTest.PROCESS_ID_2, createBranchDefiningMap("parameters"));
        assertChangedVariable(process, "node", null, "parameters");
        assertProcessStarted(process, SubprocessesTest.PROCESS_ID_2);
        assertNextNode(process, SubprocessesTest.P2_START);
        assertNextNode(process, SubprocessesTest.P2_GATEWAY_START);
        assertTriggered(process, "parameter mapping");
        // subprocess is started
        if (SubprocessesTest.JAVA8) {
            assertChangedVariable(process, "variable", null, "parameters");
            assertChangedVariable(process, "undefined", null, "parameters");
        } else {
            assertChangedVariable(process, "undefined", null, "parameters");
            assertChangedVariable(process, "variable", null, "parameters");
        }
        assertProcessStarted(process, SubprocessesTest.PROCESS_ID_3);
        long id = process.current().<IterableProcessEventListener>getEvent().getProcessInstanceId();
        assertNextNode(process, SubprocessesTest.P3_START);
        assertTriggered(process, SubprocessesTest.P3_SIGNAL);
        // signal the subprocess to continue
        ksession.signalEvent("continue", null, id);
        assertLeft(process, SubprocessesTest.P3_SIGNAL);
        assertTriggered(process, SubprocessesTest.P3_SCRIPT);
        assertChangedVariable(process, "variable", "parameters", "new value");
        assertLeft(process, SubprocessesTest.P3_SCRIPT);
        assertNextNode(process, SubprocessesTest.P3_END);
        assertProcessCompleted(process, SubprocessesTest.PROCESS_ID_3);
        // track the progress after subprocess completion
        assertChangedVariable(process, "node", "parameters", "new value");
        assertLeft(process, "parameter mapping");
        assertNextNode(process, "Gateway");
        assertNextNode(process, SubprocessesTest.P2_GATEWAY_END);
        assertTriggered(process, SubprocessesTest.P2_SIGNAL_END);
        // signal the parent process to finish
        ksession.signalEvent("finish", null, pi.getId());
        assertLeft(process, SubprocessesTest.P2_SIGNAL_END);
        assertNextNode(process, SubprocessesTest.P2_END);
        assertProcessCompleted(process, SubprocessesTest.PROCESS_ID_2);
    }

    @Ignore
    @Test(timeout = 30000)
    public void testIndependentAbort() {
        IterableProcessEventListener process = new IterableProcessEventListener();
        ksession.addEventListener(process);
        // start the process and track the progress
        ProcessInstance pi = ksession.startProcess(SubprocessesTest.PROCESS_ID_2, createBranchDefiningMap("parameters"));
        assertChangedVariable(process, "node", null, "parameters");
        assertProcessStarted(process, SubprocessesTest.PROCESS_ID_2);
        assertNextNode(process, SubprocessesTest.P2_START);
        assertNextNode(process, SubprocessesTest.P2_GATEWAY_START);
        assertTriggered(process, "parameter mapping");
        // subprocess is started
        if (SubprocessesTest.JAVA8) {
            assertChangedVariable(process, "variable", null, "parameters");
            assertChangedVariable(process, "undefined", null, "parameters");
        } else {
            assertChangedVariable(process, "undefined", null, "parameters");
            assertChangedVariable(process, "variable", null, "parameters");
        }
        assertProcessStarted(process, SubprocessesTest.PROCESS_ID_3);
        long id = process.current().<CachedProcessStartedEvent>getEvent().getProcessInstanceId();
        assertNextNode(process, SubprocessesTest.P3_START);
        assertTriggered(process, SubprocessesTest.P3_SIGNAL);
        ksession.abortProcessInstance(id);
        assertProcessCompleted(process, SubprocessesTest.PROCESS_ID_3);
        process.printRemainingEvents();
        // track the progress after subprocess completion
        assertLeft(process, "parameter mapping");
        assertNextNode(process, "Gateway");
        // variable value was not changed -> error
        assertTriggered(process, "Error");
        assertProcessCompleted(process, SubprocessesTest.PROCESS_ID_2);
    }

    @Test(timeout = 30000)
    public void testIndependentNoWaitForCompletionParentFirst() {
        IterableProcessEventListener process = new IterableProcessEventListener();
        ksession.addEventListener(process);
        ProcessInstance pi = ksession.startProcess(SubprocessesTest.PROCESS_ID_2, createBranchDefiningMap("nowait"));
        assertChangedVariable(process, "node", null, "nowait");
        assertProcessStarted(process, SubprocessesTest.PROCESS_ID_2);
        assertNextNode(process, SubprocessesTest.P2_START);
        assertNextNode(process, SubprocessesTest.P2_GATEWAY_START);
        assertTriggered(process, "don't wait for completion");
        // subprocess
        assertProcessStarted(process, SubprocessesTest.PROCESS_ID_3);
        long id = process.current().<CachedProcessStartedEvent>getEvent().getProcessInstanceId();
        assertNextNode(process, SubprocessesTest.P3_START);
        assertTriggered(process, SubprocessesTest.P3_SIGNAL);
        assertLeft(process, "don't wait for completion");
        assertNextNode(process, SubprocessesTest.P2_GATEWAY_END);
        assertTriggered(process, SubprocessesTest.P2_SIGNAL_END);
        ksession.signalEvent("finish", null, pi.getId());
        assertLeft(process, SubprocessesTest.P2_SIGNAL_END);
        assertNextNode(process, SubprocessesTest.P2_END);
        assertProcessCompleted(process, SubprocessesTest.PROCESS_ID_2);
        ksession.signalEvent("continue", null, id);
        assertLeft(process, SubprocessesTest.P3_SIGNAL);
        assertTriggered(process, SubprocessesTest.P3_SCRIPT);
        assertChangedVariable(process, "variable", null, "new value");
        assertLeft(process, SubprocessesTest.P3_SCRIPT);
        assertNextNode(process, SubprocessesTest.P3_END);
        assertProcessCompleted(process, SubprocessesTest.PROCESS_ID_3);
    }

    @Test(timeout = 30000)
    public void testIndependentNoWaitForCompletionSubprocessFirst() {
        IterableProcessEventListener process = new IterableProcessEventListener();
        ksession.addEventListener(process);
        ProcessInstance pi = ksession.startProcess(SubprocessesTest.PROCESS_ID_2, createBranchDefiningMap("nowait"));
        assertChangedVariable(process, "node", null, "nowait");
        assertProcessStarted(process, SubprocessesTest.PROCESS_ID_2);
        assertNextNode(process, SubprocessesTest.P2_START);
        assertNextNode(process, SubprocessesTest.P2_GATEWAY_START);
        assertTriggered(process, "don't wait for completion");
        // subprocess
        assertProcessStarted(process, SubprocessesTest.PROCESS_ID_3);
        long id = process.current().<CachedProcessStartedEvent>getEvent().getProcessInstanceId();
        assertNextNode(process, SubprocessesTest.P3_START);
        assertTriggered(process, SubprocessesTest.P3_SIGNAL);
        assertLeft(process, "don't wait for completion");
        assertNextNode(process, SubprocessesTest.P2_GATEWAY_END);
        assertTriggered(process, SubprocessesTest.P2_SIGNAL_END);
        // signal the subprocess to continue
        ksession.signalEvent("continue", null, id);
        assertLeft(process, SubprocessesTest.P3_SIGNAL);
        assertTriggered(process, SubprocessesTest.P3_SCRIPT);
        assertChangedVariable(process, "variable", null, "new value");
        assertLeft(process, SubprocessesTest.P3_SCRIPT);
        assertNextNode(process, SubprocessesTest.P3_END);
        assertProcessCompleted(process, SubprocessesTest.PROCESS_ID_3);
        ksession.signalEvent("finish", null, pi.getId());
        assertLeft(process, SubprocessesTest.P2_SIGNAL_END);
        assertNextNode(process, SubprocessesTest.P2_END);
        assertProcessCompleted(process, SubprocessesTest.PROCESS_ID_2);
    }

    @Test(timeout = 30000)
    public void testIndependentNoWaitForCompletionAbortParent() {
        TrackingProcessEventListener listener = new TrackingProcessEventListener();
        ksession.addEventListener(listener);
        IterableProcessEventListener process = new IterableProcessEventListener();
        ksession.addEventListener(process);
        ProcessInstance pi = ksession.startProcess(SubprocessesTest.PROCESS_ID_2, createBranchDefiningMap("nowait"));
        assertChangedVariable(process, "node", null, "nowait");
        assertProcessStarted(process, SubprocessesTest.PROCESS_ID_2);
        assertNextNode(process, SubprocessesTest.P2_START);
        assertNextNode(process, SubprocessesTest.P2_GATEWAY_START);
        assertTriggered(process, "don't wait for completion");
        // subprocess
        assertProcessStarted(process, SubprocessesTest.PROCESS_ID_3);
        long id = process.current().<CachedProcessStartedEvent>getEvent().getProcessInstanceId();
        assertNextNode(process, SubprocessesTest.P3_START);
        assertTriggered(process, SubprocessesTest.P3_SIGNAL);
        assertLeft(process, "don't wait for completion");
        assertNextNode(process, SubprocessesTest.P2_GATEWAY_END);
        assertTriggered(process, SubprocessesTest.P2_SIGNAL_END);
        Assertions.assertThat(listener.wasProcessStarted(SubprocessesTest.PROCESS_ID_2)).isTrue();
        Assertions.assertThat(listener.wasProcessStarted(SubprocessesTest.PROCESS_ID_3)).isTrue();
        ksession.abortProcessInstance(pi.getId());
        assertProcessCompleted(process, SubprocessesTest.PROCESS_ID_2);
        Assertions.assertThat(listener.wasProcessAborted(SubprocessesTest.PROCESS_ID_2)).isTrue();
        Assertions.assertThat(listener.wasProcessCompleted(SubprocessesTest.PROCESS_ID_3)).isFalse();
        // signal the subprocess to continue
        ksession.signalEvent("continue", null, id);
        assertLeft(process, SubprocessesTest.P3_SIGNAL);
        assertTriggered(process, SubprocessesTest.P3_SCRIPT);
        assertChangedVariable(process, "variable", null, "new value");
        assertLeft(process, SubprocessesTest.P3_SCRIPT);
        assertNextNode(process, SubprocessesTest.P3_END);
        assertProcessCompleted(process, SubprocessesTest.PROCESS_ID_3);
        Assertions.assertThat(listener.wasProcessAborted(SubprocessesTest.PROCESS_ID_2)).isTrue();
        Assertions.assertThat(listener.wasProcessCompleted(SubprocessesTest.PROCESS_ID_3)).isTrue();
    }

    @Test(timeout = 30000)
    public void testIndependentNoWaitForCompletionAbortSubprocess() {
        TrackingProcessEventListener listener = new TrackingProcessEventListener();
        ksession.addEventListener(listener);
        IterableProcessEventListener process = new IterableProcessEventListener();
        ksession.addEventListener(process);
        ProcessInstance pi = ksession.startProcess(SubprocessesTest.PROCESS_ID_2, createBranchDefiningMap("nowait"));
        assertChangedVariable(process, "node", null, "nowait");
        assertProcessStarted(process, SubprocessesTest.PROCESS_ID_2);
        assertNextNode(process, SubprocessesTest.P2_START);
        assertNextNode(process, SubprocessesTest.P2_GATEWAY_START);
        assertTriggered(process, "don't wait for completion");
        // subprocess
        assertProcessStarted(process, SubprocessesTest.PROCESS_ID_3);
        long id = process.current().<CachedProcessStartedEvent>getEvent().getProcessInstanceId();
        assertNextNode(process, SubprocessesTest.P3_START);
        assertTriggered(process, SubprocessesTest.P3_SIGNAL);
        assertLeft(process, "don't wait for completion");
        assertNextNode(process, SubprocessesTest.P2_GATEWAY_END);
        assertTriggered(process, SubprocessesTest.P2_SIGNAL_END);
        Assertions.assertThat(listener.wasProcessStarted(SubprocessesTest.PROCESS_ID_2)).isTrue();
        Assertions.assertThat(listener.wasProcessStarted(SubprocessesTest.PROCESS_ID_3)).isTrue();
        ksession.abortProcessInstance(id);
        assertProcessCompleted(process, SubprocessesTest.PROCESS_ID_3);
        Assertions.assertThat(listener.wasProcessCompleted(SubprocessesTest.PROCESS_ID_2)).isFalse();
        Assertions.assertThat(listener.wasProcessAborted(SubprocessesTest.PROCESS_ID_3)).isTrue();
        ksession.signalEvent("finish", null, pi.getId());
        assertLeft(process, SubprocessesTest.P2_SIGNAL_END);
        assertNextNode(process, SubprocessesTest.P2_END);
        assertProcessCompleted(process, SubprocessesTest.PROCESS_ID_2);
        Assertions.assertThat(listener.wasProcessCompleted(SubprocessesTest.PROCESS_ID_2)).isTrue();
        Assertions.assertThat(listener.wasProcessAborted(SubprocessesTest.PROCESS_ID_3)).isTrue();
    }

    @Test(timeout = 30000)
    public void testDependentNoWaitForCompletion() {
        TrackingProcessEventListener listener = new TrackingProcessEventListener();
        ksession.addEventListener(listener);
        IterableProcessEventListener process = new IterableProcessEventListener();
        ksession.addEventListener(process);
        ProcessInstance pi = ksession.startProcess(SubprocessesTest.PROCESS_ID_2, createBranchDefiningMap("dependent-nowait"));
        assertChangedVariable(process, "node", null, "dependent-nowait");
        assertProcessStarted(process, SubprocessesTest.PROCESS_ID_2);
        assertNextNode(process, SubprocessesTest.P2_START);
        assertNextNode(process, SubprocessesTest.P2_GATEWAY_START);
        assertTriggered(process, "dependent process that doesn't have to be completed");
        // subprocess
        assertProcessStarted(process, SubprocessesTest.PROCESS_ID_3);
        long id = process.current().<CachedProcessStartedEvent>getEvent().getProcessInstanceId();
        assertNextNode(process, SubprocessesTest.P3_START);
        assertTriggered(process, SubprocessesTest.P3_SIGNAL);
        assertLeft(process, "dependent process that doesn't have to be completed");
        assertNextNode(process, SubprocessesTest.P2_GATEWAY_END);
        assertTriggered(process, SubprocessesTest.P2_SIGNAL_END);
        Assertions.assertThat(listener.wasProcessStarted(SubprocessesTest.PROCESS_ID_2)).isTrue();
        Assertions.assertThat(listener.wasProcessStarted(SubprocessesTest.PROCESS_ID_3)).isTrue();
        // signal the parent process to finish
        ksession.signalEvent("finish", null, pi.getId());
        assertLeft(process, SubprocessesTest.P2_SIGNAL_END);
        assertNextNode(process, SubprocessesTest.P2_END);
        assertProcessCompleted(process, SubprocessesTest.PROCESS_ID_2);
        Assertions.assertThat(listener.wasProcessCompleted(SubprocessesTest.PROCESS_ID_2)).isTrue();
        Assertions.assertThat(listener.wasProcessCompleted(SubprocessesTest.PROCESS_ID_3)).isFalse();
        // signal the subprocess to continue
        ksession.signalEvent("continue", null, id);
        assertLeft(process, SubprocessesTest.P3_SIGNAL);
        assertTriggered(process, SubprocessesTest.P3_SCRIPT);
        assertChangedVariable(process, "variable", null, "new value");
        assertLeft(process, SubprocessesTest.P3_SCRIPT);
        assertNextNode(process, SubprocessesTest.P3_END);
        assertProcessCompleted(process, SubprocessesTest.PROCESS_ID_3);
        Assertions.assertThat(listener.wasProcessCompleted(SubprocessesTest.PROCESS_ID_2)).isTrue();
        Assertions.assertThat(listener.wasProcessCompleted(SubprocessesTest.PROCESS_ID_3)).isTrue();
    }

    @Test(timeout = 30000)
    public void testDependentNoWaitForCompletionAbortSubprocess() {
        TrackingProcessEventListener listener = new TrackingProcessEventListener();
        ksession.addEventListener(listener);
        IterableProcessEventListener process = new IterableProcessEventListener();
        ksession.addEventListener(process);
        ProcessInstance pi = ksession.startProcess(SubprocessesTest.PROCESS_ID_2, createBranchDefiningMap("dependent-nowait"));
        assertChangedVariable(process, "node", null, "dependent-nowait");
        assertProcessStarted(process, SubprocessesTest.PROCESS_ID_2);
        assertNextNode(process, SubprocessesTest.P2_START);
        assertNextNode(process, SubprocessesTest.P2_GATEWAY_START);
        assertTriggered(process, "dependent process that doesn't have to be completed");
        // subprocess
        assertProcessStarted(process, SubprocessesTest.PROCESS_ID_3);
        long id = process.current().<CachedProcessStartedEvent>getEvent().getProcessInstanceId();
        assertNextNode(process, SubprocessesTest.P3_START);
        assertTriggered(process, SubprocessesTest.P3_SIGNAL);
        assertLeft(process, "dependent process that doesn't have to be completed");
        assertNextNode(process, SubprocessesTest.P2_GATEWAY_END);
        assertTriggered(process, SubprocessesTest.P2_SIGNAL_END);
        ksession.abortProcessInstance(id);
        assertProcessCompleted(process, SubprocessesTest.PROCESS_ID_3);
        Assertions.assertThat(listener.wasProcessCompleted(SubprocessesTest.PROCESS_ID_2)).isFalse();
        Assertions.assertThat(listener.wasProcessAborted(SubprocessesTest.PROCESS_ID_3)).isTrue();
        // signal the parent process to finish
        ksession.signalEvent("finish", null, pi.getId());
        assertLeft(process, SubprocessesTest.P2_SIGNAL_END);
        assertNextNode(process, SubprocessesTest.P2_END);
        assertProcessCompleted(process, SubprocessesTest.PROCESS_ID_2);
        Assertions.assertThat(listener.wasProcessCompleted(SubprocessesTest.PROCESS_ID_2)).isTrue();
        Assertions.assertThat(listener.wasProcessAborted(SubprocessesTest.PROCESS_ID_3)).isTrue();
    }

    @Test(timeout = 30000)
    public void testDependentNoWaitForCompletionAbortParent() {
        TrackingProcessEventListener listener = new TrackingProcessEventListener();
        ksession.addEventListener(listener);
        IterableProcessEventListener process = new IterableProcessEventListener();
        ksession.addEventListener(process);
        ProcessInstance pi = ksession.startProcess(SubprocessesTest.PROCESS_ID_2, createBranchDefiningMap("dependent-nowait"));
        assertChangedVariable(process, "node", null, "dependent-nowait");
        assertProcessStarted(process, SubprocessesTest.PROCESS_ID_2);
        assertNextNode(process, SubprocessesTest.P2_START);
        assertNextNode(process, SubprocessesTest.P2_GATEWAY_START);
        assertTriggered(process, "dependent process that doesn't have to be completed");
        // subprocess
        assertProcessStarted(process, SubprocessesTest.PROCESS_ID_3);
        long id = process.current().<CachedProcessStartedEvent>getEvent().getProcessInstanceId();
        assertNextNode(process, SubprocessesTest.P3_START);
        assertTriggered(process, SubprocessesTest.P3_SIGNAL);
        assertLeft(process, "dependent process that doesn't have to be completed");
        assertNextNode(process, SubprocessesTest.P2_GATEWAY_END);
        assertTriggered(process, SubprocessesTest.P2_SIGNAL_END);
        ksession.abortProcessInstance(pi.getId());
        assertProcessCompleted(process, SubprocessesTest.PROCESS_ID_2);
        Assertions.assertThat(listener.wasProcessAborted(SubprocessesTest.PROCESS_ID_2)).isTrue();
        Assertions.assertThat(listener.wasProcessCompleted(SubprocessesTest.PROCESS_ID_3)).isFalse();
        // signal the subprocess to continue
        ksession.signalEvent("continue", null, id);
        assertLeft(process, SubprocessesTest.P3_SIGNAL);
        assertTriggered(process, SubprocessesTest.P3_SCRIPT);
        assertChangedVariable(process, "variable", null, "new value");
        assertLeft(process, SubprocessesTest.P3_SCRIPT);
        assertNextNode(process, SubprocessesTest.P3_END);
        assertProcessCompleted(process, SubprocessesTest.PROCESS_ID_3);
        Assertions.assertThat(listener.wasProcessAborted(SubprocessesTest.PROCESS_ID_2)).isTrue();
        Assertions.assertThat(listener.wasProcessCompleted(SubprocessesTest.PROCESS_ID_3)).isTrue();
    }

    @Test(timeout = 30000)
    public void testDependent() {
        IterableProcessEventListener process = new IterableProcessEventListener();
        ksession.addEventListener(process);
        ProcessInstance pi = ksession.startProcess(SubprocessesTest.PROCESS_ID_2, createBranchDefiningMap("dependent"));
        assertChangedVariable(process, "node", null, "dependent");
        assertProcessStarted(process, SubprocessesTest.PROCESS_ID_2);
        assertNextNode(process, SubprocessesTest.P2_START);
        assertNextNode(process, SubprocessesTest.P2_GATEWAY_START);
        assertTriggered(process, "dependent process");
        assertProcessStarted(process, SubprocessesTest.PROCESS_ID_3);
        long id = process.current().<CachedProcessStartedEvent>getEvent().getProcessInstanceId();
        assertNextNode(process, SubprocessesTest.P3_START);
        assertTriggered(process, SubprocessesTest.P3_SIGNAL);
        // signal the subprocess to continue
        ksession.signalEvent("continue", null, id);
        assertLeft(process, SubprocessesTest.P3_SIGNAL);
        assertTriggered(process, SubprocessesTest.P3_SCRIPT);
        assertChangedVariable(process, "variable", null, "new value");
        assertLeft(process, SubprocessesTest.P3_SCRIPT);
        assertNextNode(process, SubprocessesTest.P3_END);
        assertProcessCompleted(process, SubprocessesTest.PROCESS_ID_3);
        // track the progress after subprocess completion
        assertLeft(process, "dependent process");
        assertNextNode(process, SubprocessesTest.P2_GATEWAY_END);
        assertTriggered(process, SubprocessesTest.P2_SIGNAL_END);
        // signal the parent process to finish
        ksession.signalEvent("finish", null, pi.getId());
        assertLeft(process, SubprocessesTest.P2_SIGNAL_END);
        assertNextNode(process, SubprocessesTest.P2_END);
        assertProcessCompleted(process, SubprocessesTest.PROCESS_ID_2);
    }

    @Test(timeout = 30000)
    public void testDependentAbort() {
        TrackingProcessEventListener listener = new TrackingProcessEventListener();
        IterableProcessEventListener process = new IterableProcessEventListener();
        ksession.addEventListener(process);
        ksession.addEventListener(listener);
        ProcessInstance pi = ksession.startProcess(SubprocessesTest.PROCESS_ID_2, createBranchDefiningMap("dependent"));
        assertChangedVariable(process, "node", null, "dependent");
        assertProcessStarted(process, SubprocessesTest.PROCESS_ID_2);
        assertNextNode(process, SubprocessesTest.P2_START);
        assertNextNode(process, SubprocessesTest.P2_GATEWAY_START);
        assertTriggered(process, "dependent process");
        assertProcessStarted(process, SubprocessesTest.PROCESS_ID_3);
        assertNextNode(process, SubprocessesTest.P3_START);
        assertTriggered(process, SubprocessesTest.P3_SIGNAL);
        ksession.abortProcessInstance(pi.getId());
        assertProcessCompleted(process, SubprocessesTest.PROCESS_ID_2);
        assertProcessCompleted(process, SubprocessesTest.PROCESS_ID_3);
        Assertions.assertThat(listener.wasProcessCompleted(SubprocessesTest.PROCESS_ID_2)).isFalse();
        Assertions.assertThat(listener.wasProcessCompleted(SubprocessesTest.PROCESS_ID_3)).isFalse();
        Assertions.assertThat(listener.wasProcessAborted(SubprocessesTest.PROCESS_ID_2)).isTrue();
        Assertions.assertThat(listener.wasProcessAborted(SubprocessesTest.PROCESS_ID_3)).isTrue();
    }

    @Test(timeout = 30000)
    public void testDependentAbort2() {
        TrackingProcessEventListener listener = new TrackingProcessEventListener();
        IterableProcessEventListener process = new IterableProcessEventListener();
        ksession.addEventListener(process);
        ksession.addEventListener(listener);
        ProcessInstance pi = ksession.startProcess(SubprocessesTest.PROCESS_ID_2, createBranchDefiningMap("dependent"));
        assertChangedVariable(process, "node", null, "dependent");
        assertProcessStarted(process, SubprocessesTest.PROCESS_ID_2);
        assertNextNode(process, SubprocessesTest.P2_START);
        assertNextNode(process, SubprocessesTest.P2_GATEWAY_START);
        assertTriggered(process, "dependent process");
        // subprocess started
        assertProcessStarted(process, SubprocessesTest.PROCESS_ID_3);
        assertNextNode(process, SubprocessesTest.P3_START);
        assertTriggered(process, SubprocessesTest.P3_SIGNAL);
        long subprocessId = process.current().getProcessInstanceId();
        Assertions.assertThat(pi.getId()).isNotEqualTo(subprocessId);
        // abort subprocess
        ksession.abortProcessInstance(subprocessId);
        // carry on with execution of superprocess
        assertProcessCompleted(process, SubprocessesTest.PROCESS_ID_3);
        assertProcessCompleted(process, SubprocessesTest.PROCESS_ID_2);
        Assertions.assertThat(listener.wasProcessAborted(SubprocessesTest.PROCESS_ID_2)).isTrue();
        Assertions.assertThat(listener.wasProcessAborted(SubprocessesTest.PROCESS_ID_3)).isTrue();
    }

    @Test(timeout = 30000)
    public void testParameterMapping() {
        IterableProcessEventListener process = new IterableProcessEventListener();
        ksession.addEventListener(process);
        // start the process and track the progress
        ProcessInstance pi = ksession.startProcess(SubprocessesTest.PROCESS_ID_2, createBranchDefiningMap("parameters"));
        assertChangedVariable(process, "node", null, "parameters");
        assertProcessStarted(process, SubprocessesTest.PROCESS_ID_2);
        assertNextNode(process, SubprocessesTest.P2_START);
        assertNextNode(process, SubprocessesTest.P2_GATEWAY_START);
        assertTriggered(process, "parameter mapping");
        // subprocess is started
        if (SubprocessesTest.JAVA8) {
            assertChangedVariable(process, "variable", null, "parameters");
            assertChangedVariable(process, "undefined", null, "parameters");
        } else {
            assertChangedVariable(process, "undefined", null, "parameters");
            assertChangedVariable(process, "variable", null, "parameters");
        }
        assertProcessStarted(process, SubprocessesTest.PROCESS_ID_3);
        assertNextNode(process, SubprocessesTest.P3_START);
        assertTriggered(process, SubprocessesTest.P3_SIGNAL);
        // signal the subprocess to continue
        ksession.signalEvent("continue", null);
        assertLeft(process, SubprocessesTest.P3_SIGNAL);
        assertTriggered(process, SubprocessesTest.P3_SCRIPT);
        assertChangedVariable(process, "variable", "parameters", "new value");
        assertLeft(process, SubprocessesTest.P3_SCRIPT);
        assertNextNode(process, SubprocessesTest.P3_END);
        assertProcessCompleted(process, SubprocessesTest.PROCESS_ID_3);
        // track the progress after subprocess completion
        assertChangedVariable(process, "node", "parameters", "new value");
        assertLeft(process, "parameter mapping");
        assertNextNode(process, "Gateway");
        assertNextNode(process, SubprocessesTest.P2_GATEWAY_END);
        assertTriggered(process, SubprocessesTest.P2_SIGNAL_END);
        // signal the parent process to finish
        ksession.signalEvent("finish", null, pi.getId());
        assertLeft(process, SubprocessesTest.P2_SIGNAL_END);
        assertNextNode(process, SubprocessesTest.P2_END);
        assertProcessCompleted(process, SubprocessesTest.PROCESS_ID_2);
    }

    @Test(timeout = 30000)
    public void testVariableScope() {
        IterableProcessEventListener process = new IterableProcessEventListener();
        ksession.addEventListener(process);
        ProcessInstance pi = ksession.startProcess(SubprocessesTest.PROCESS_ID_2, createBranchDefiningMap("variables"));
        assertChangedVariable(process, "node", null, "variables");
        assertProcessStarted(process, SubprocessesTest.PROCESS_ID_2);
        assertNextNode(process, SubprocessesTest.P2_START);
        assertNextNode(process, SubprocessesTest.P2_GATEWAY_START);
        assertTriggered(process, "variables scope");
        // subprocess
        assertNextNode(process, "Start");
        assertNextNode(process, "Script");
        assertTriggered(process, "Script");
        assertChangedVariable(process, "9:variable", null, "variables");
        assertChangedVariable(process, "node", "variables", "new value");
        assertLeft(process, "Script");
        assertNextNode(process, "Script");
        assertNextNode(process, "End");
        assertLeft(process, "variables scope");
        assertNextNode(process, "Gateway");
        assertNextNode(process, SubprocessesTest.P2_GATEWAY_END);
        assertTriggered(process, SubprocessesTest.P2_SIGNAL_END);
        // signal the parent process to finish
        ksession.signalEvent("finish", null, pi.getId());
        assertLeft(process, SubprocessesTest.P2_SIGNAL_END);
        assertNextNode(process, SubprocessesTest.P2_END);
        assertProcessCompleted(process, SubprocessesTest.PROCESS_ID_2);
    }

    private TrackingProcessEventListener runProcess(KieSession session, String processId, String nodeType, Command<?>... additionalCommands) {
        TrackingProcessEventListener process = new TrackingProcessEventListener();
        session.addEventListener(process);
        ProcessInstance pi = session.startProcess(processId);
        session.signalEvent("nodeType", nodeType, pi.getId());
        for (org.kie.api.command.Command<?> additionalCommand : additionalCommands) {
            session.execute(additionalCommand);
        }
        try {
            assertTrue("Process was not started on time!", process.waitForProcessToStart(1000));
        } catch (Exception ex) {
            logger.warn("Interrupted", ex);
        }
        return process;
    }

    private TrackingProcessEventListener runProcess(KieSession session, String nodeType) {
        return runProcess(session, SubprocessesTest.PROCESS_ID_1, nodeType);
    }

    private Map<String, Object> createBranchDefiningMap(String branch) {
        Map<String, Object> processVariables = new HashMap<String, Object>();
        processVariables.put("node", branch);
        return processVariables;
    }
}

