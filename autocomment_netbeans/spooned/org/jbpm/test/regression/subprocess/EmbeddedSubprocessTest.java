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


package org.jbpm.test.regression.subprocess;

import java.util.ArrayList;
import org.assertj.core.api.Assertions;
import qa.tools.ikeeper.annotation.BZ;
import org.kie.api.command.Command;
import java.util.HashMap;
import org.jbpm.test.JbpmTestCase;
import org.kie.api.runtime.KieSession;
import java.util.List;
import java.util.Map;
import org.kie.api.runtime.process.ProcessInstance;
import org.junit.Test;
import org.jbpm.test.listener.TrackingProcessEventListener;
import org.kie.api.runtime.manager.audit.VariableInstanceLog;

public class EmbeddedSubprocessTest extends JbpmTestCase {
    private static final String INVALID_SUBPROCESS = "org/jbpm/test/regression/subprocess/EmbeddedSubprocess-invalidSubprocess.bpmn2";

    private static final String INVALID_SUBPROCESS2 = "org/jbpm/test/regression/subprocess/EmbeddedSubprocess-invalidSubprocess2.bpmn2";

    public static final String TERMINATING_END_EVENT = "org/jbpm/test/regression/subprocess/EmbeddedSubprocess-terminatingEndEvent.bpmn2";

    public static final String TERMINATING_END_EVENT_ID = "org.jbpm.test.regression.subprocess.EmbeddedSubprocess-terminatingEndEvent";

    public static final String TASK_COMPENSATION = "org/jbpm/test/regression/subprocess/EmbeddedSubprocess-taskCompensation.bpmn2";

    public static final String TASK_COMPENSATION_ID = "org.jbpm.test.regression.subprocess.EmbeddedSubprocess-taskCompensation";

    @Test
    @BZ(value = "1139591")
    public void testInvalidSubprocess() {
        try {
            createKSession(EmbeddedSubprocessTest.INVALID_SUBPROCESS);
            Assertions.fail("Process definition is invalid. KieSession should not have been created.");
        } catch (IllegalArgumentException ex) {
            // expected behaviour
        }
    }

    @Test
    @BZ(value = "1150226")
    public void testInvalidSubprocess2() {
        try {
            createKSession(EmbeddedSubprocessTest.INVALID_SUBPROCESS2);
            Assertions.fail("Process definition is invalid. KieSession should not have been created.");
        } catch (IllegalArgumentException ex) {
            // expected behaviour
            ex.printStackTrace();
        }
    }

    @Test
    @BZ(value = "851286")
    public void testTerminatingEndEvent() {
        KieSession ksession = createKSession(EmbeddedSubprocessTest.TERMINATING_END_EVENT);
        TrackingProcessEventListener processEvents = new TrackingProcessEventListener();
        ksession.addEventListener(processEvents);
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(getCommands().newStartProcess(EmbeddedSubprocessTest.TERMINATING_END_EVENT_ID));
        ksession.execute(getCommands().newBatchExecution(commands, null));
        Assertions.assertThat(processEvents.wasNodeTriggered("main-script")).isTrue();
        Assertions.assertThat(processEvents.wasNodeTriggered("main-end")).isTrue();
    }

    @Test
    @BZ(value = "1191768")
    public void testTaskCompensation() throws Exception {
        KieSession kieSession = createKSession(EmbeddedSubprocessTest.TASK_COMPENSATION);
        kieSession.getWorkItemManager().registerWorkItemHandler("Human Task", new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("compensation", "True");
        ProcessInstance processInstance = kieSession.startProcess(EmbeddedSubprocessTest.TASK_COMPENSATION_ID, params);
        long pid = processInstance.getId();
        assertProcessInstanceCompleted(pid);
        List<? extends VariableInstanceLog> log = getLogService().findVariableInstances(processInstance.getId(), "compensation");
        Assertions.assertThat(log.get(((log.size()) - 1)).getValue()).isEqualTo("compensation");
    }
}

