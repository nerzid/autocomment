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


package org.jbpm.test.regression;

import org.assertj.core.api.Assertions;
import qa.tools.ikeeper.annotation.BZ;
import java.util.HashMap;
import org.jbpm.test.JbpmTestCase;
import org.kie.api.runtime.KieSession;
import java.util.Map;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.junit.Test;

@BZ(value = "852738")
public class SessionIsolationTest extends JbpmTestCase {
    private static final String SIGNAL = "org/jbpm/test/regression/SessionIsolation-signal.bpmn";

    private static final String SIGNAL_ID = "org.jbpm.test.regression.SessionIsolation-signal";

    private static final String RULE_TASK = "org/jbpm/test/regression/SessionIsolation-ruleTask.bpmn";

    private static final String RULE_TASK_ID = "org.jbpm.test.regression.SessionIsolation-ruleTask";

    private static final String RULE_TASK_DRL = "org/jbpm/test/regression/SessionIsolation-ruleTask.drl";

    public SessionIsolationTest() {
        super(false);
    }

    @Test
    public void testSignal() throws Exception {
        createRuntimeManager(Strategy.PROCESS_INSTANCE, SessionIsolationTest.SIGNAL_ID, SessionIsolationTest.SIGNAL);
        RuntimeEngine runtime1 = getRuntimeEngine(ProcessInstanceIdContext.get());
        RuntimeEngine runtime2 = getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession1 = runtime1.getKieSession();
        KieSession ksession2 = runtime2.getKieSession();
        Assertions.assertThat(ksession1).isNotEqualTo(ksession2);
        ProcessInstance pi1 = ksession1.startProcess(SessionIsolationTest.SIGNAL_ID);
        ProcessInstance pi2 = ksession2.startProcess(SessionIsolationTest.SIGNAL_ID);
        assertProcessInstanceActive(pi1.getId(), ksession1);
        assertProcessInstanceActive(pi2.getId(), ksession2);
        ksession1.signalEvent("event", null);
        assertProcessInstanceNotActive(pi1.getId(), ksession1);
        assertProcessInstanceActive(pi2.getId(), ksession2);
        ksession2.signalEvent("event", null);
        assertProcessInstanceNotActive(pi1.getId(), ksession1);
        assertProcessInstanceNotActive(pi2.getId(), ksession2);
    }

    @Test
    public void testRuleTask() throws Exception {
        Map<String, ResourceType> res = new HashMap<String, ResourceType>();
        res.put(SessionIsolationTest.RULE_TASK, ResourceType.BPMN2);
        res.put(SessionIsolationTest.RULE_TASK_DRL, ResourceType.DRL);
        createRuntimeManager(Strategy.PROCESS_INSTANCE, res, SessionIsolationTest.RULE_TASK_ID);
        RuntimeEngine runtime1 = getRuntimeEngine(ProcessInstanceIdContext.get());
        RuntimeEngine runtime2 = getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession1 = runtime1.getKieSession();
        KieSession ksession2 = runtime2.getKieSession();
        Assertions.assertThat(ksession1).isNotEqualTo(ksession2);
        ProcessInstance pi1 = ksession1.startProcess(SessionIsolationTest.RULE_TASK_ID);
        ProcessInstance pi2 = ksession2.startProcess(SessionIsolationTest.RULE_TASK_ID);
        assertProcessInstanceActive(pi1.getId(), ksession1);
        assertProcessInstanceActive(pi2.getId(), ksession2);
        ksession1.fireAllRules();
        assertProcessInstanceNotActive(pi1.getId(), ksession1);
        assertProcessInstanceActive(pi2.getId(), ksession2);
        ksession2.fireAllRules();
        assertProcessInstanceNotActive(pi1.getId(), ksession1);
        assertProcessInstanceNotActive(pi2.getId(), ksession2);
    }
}

