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


package org.jbpm.test.functional.task;

import java.util.ArrayList;
import qa.tools.ikeeper.annotation.BZ;
import org.kie.api.command.Command;
import org.kie.internal.command.CommandFactory;
import java.util.HashMap;
import org.jbpm.test.listener.IterableProcessEventListener;
import org.jbpm.test.JbpmTestCase;
import org.kie.api.runtime.KieSession;
import java.util.List;
import java.util.Map;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.io.ResourceType;
import org.junit.Test;
import org.kie.api.runtime.process.WorkflowProcessInstance;

/**
 * Business rules task test. testing execution of rules with specified rule-flow group.
 */
public class RuleTaskTest extends JbpmTestCase {
    private static final String RULE_TASK = "org/jbpm/test/functional/task/RuleTask.bpmn";

    private static final String RULE_TASK_ID = "org.jbpm.test.functional.task.RuleTask";

    private static final String RULE_TASK_DRL = "org/jbpm/test/functional/task/RuleTask.drl";

    private static final String RULE_TASK_2 = "org/jbpm/test/functional/task/RuleTask2.bpmn2";

    private static final String RULE_TASK_2_ID = "org.jbpm.test.functional.task.RuleTask2";

    private static final String RULE_TASK_2_DRL = "org/jbpm/test/functional/task/RuleTask2.drl";

    public RuleTaskTest() {
        super(false);
    }

    @Test(timeout = 30000)
    public void testRuleTask() {
        Map<String, ResourceType> res = new HashMap<String, ResourceType>();
        res.put(RuleTaskTest.RULE_TASK, ResourceType.BPMN2);
        res.put(RuleTaskTest.RULE_TASK_DRL, ResourceType.DRL);
        KieSession kieSession = createKSession(res);
        List<String> executedRules = new ArrayList<String>();
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newSetGlobal("executed", executedRules));
        commands.add(CommandFactory.newStartProcess(RuleTaskTest.RULE_TASK_ID));
        commands.add(CommandFactory.newFireAllRules());
        IterableProcessEventListener listener = new IterableProcessEventListener();
        kieSession.addEventListener(listener);
        kieSession.execute(CommandFactory.newBatchExecution(commands));
        assertProcessStarted(listener, RuleTaskTest.RULE_TASK_ID);
        assertNextNode(listener, "start");
        assertNextNode(listener, "rules");
        assertNextNode(listener, "end");
        assertProcessCompleted(listener, RuleTaskTest.RULE_TASK_ID);
        assertEquals(3, executedRules.size());
        String[] expected = new String[]{ "firstRule" , "secondRule" , "thirdRule" };
        for (String expectedRuleName : expected) {
            assertTrue(executedRules.contains(expectedRuleName));
        }
    }

    @Test(timeout = 30000)
    public void testRuleTaskInsertFact() {
        Map<String, ResourceType> res = new HashMap<String, ResourceType>();
        res.put(RuleTaskTest.RULE_TASK, ResourceType.BPMN2);
        res.put(RuleTaskTest.RULE_TASK_DRL, ResourceType.DRL);
        KieSession kieSession = createKSession(res);
        List<String> executedRules = new ArrayList<String>();
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newSetGlobal("executed", executedRules));
        commands.add(CommandFactory.newStartProcess(RuleTaskTest.RULE_TASK_ID));
        commands.add(CommandFactory.newInsert(6));
        commands.add(CommandFactory.newFireAllRules());
        IterableProcessEventListener listener = new IterableProcessEventListener();
        kieSession.addEventListener(listener);
        kieSession.execute(CommandFactory.newBatchExecution(commands));
        assertProcessStarted(listener, RuleTaskTest.RULE_TASK_ID);
        assertNextNode(listener, "start");
        assertNextNode(listener, "rules");
        assertNextNode(listener, "end");
        assertProcessCompleted(listener, RuleTaskTest.RULE_TASK_ID);
        assertEquals(4, executedRules.size());
        String[] expected = new String[]{ "firstRule" , "secondRule" , "thirdRule" , "fifthRule" };
        for (String expectedRuleName : expected) {
            assertTrue(executedRules.contains(expectedRuleName));
        }
    }

    @BZ(value = "1044504")
    @Test(timeout = 30000)
    public void testRuleTask2() {
        Map<String, ResourceType> res = new HashMap<String, ResourceType>();
        res.put(RuleTaskTest.RULE_TASK_2, ResourceType.BPMN2);
        res.put(RuleTaskTest.RULE_TASK_2_DRL, ResourceType.DRL);
        KieSession ksession = createKSession(res);
        ProcessInstance pi = ksession.startProcess(RuleTaskTest.RULE_TASK_2_ID, null);
        assertNotNull(pi);
        assertEquals(ProcessInstance.STATE_ACTIVE, pi.getState());
        ksession.fireAllRules();
        WorkflowProcessInstance wpi = ((WorkflowProcessInstance) (pi));
        List<String> executeRuleList = ((List<String>) (wpi.getVariable("results")));
        assertNotNull(executeRuleList);
        for (String s : executeRuleList) {
            System.out.println(s);
        }
        assertEquals(2, executeRuleList.size());
        pi = ksession.startProcess(RuleTaskTest.RULE_TASK_2_ID, null);
        assertNotNull(pi);
        assertEquals(ProcessInstance.STATE_ACTIVE, pi.getState());
        ksession.fireAllRules();
        wpi = ((WorkflowProcessInstance) (pi));
        executeRuleList = ((List<String>) (wpi.getVariable("results")));
        assertNotNull(executeRuleList);
        for (String s : executeRuleList) {
            System.out.println(s);
        }
        assertEquals(2, executeRuleList.size());
    }

    public KieSession createKSession(Map<String, ResourceType> res) {
        createRuntimeManager(res);
        return getRuntimeEngine().getKieSession();
    }
}

