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


package org.jbpm.test.regression.task;

import qa.tools.ikeeper.annotation.BZ;
import org.jbpm.test.JbpmTestCase;
import org.kie.api.runtime.KieSession;
import java.util.List;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.manager.RuntimeEngine;
import JbpmJUnitBaseTestCase.Strategy;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.junit.Test;

public class HumanTaskCleanUpEarlyFlushTest extends JbpmTestCase {
    private static final String SUBPROCESS_PARENT = "org/jbpm/test/regression/task/HumanTaskCleanUpEarlyFlush-subprocess-parent.bpmn2";

    private static final String SUBPROCESS_PARENT_ID = "org.jbpm.test.regression.task.HumanTaskCleanUpEarlyFlush-subprocess-parent";

    private static final String SUBPROCESS_CHILD1 = "org/jbpm/test/regression/task/HumanTaskCleanUpEarlyFlush-subprocess-child1.bpmn2";

    private static final String SUBPROCESS_CHILD2 = "org/jbpm/test/regression/task/HumanTaskCleanUpEarlyFlush-subprocess-child2.bpmn2";

    private static final String SIGNAL_SENDER = "org/jbpm/test/regression/task/HumanTaskCleanUpEarlyFlush-signal-sender.bpmn2";

    private static final String SIGNAL_SENDER_ID = "org.jbpm.test.regression.task.HumanTaskCleanUpEarlyFlush-signal-sender";

    private static final String SIGNAL_RECEIVER = "org/jbpm/test/regression/task/HumanTaskCleanUpEarlyFlush-signal-receiver.bpmn2";

    @Test
    @BZ(value = { "1128377" , "1177736" })
    public void testSubprocess() {
        createRuntimeManager(HumanTaskCleanUpEarlyFlushTest.SUBPROCESS_PARENT, HumanTaskCleanUpEarlyFlushTest.SUBPROCESS_CHILD1, HumanTaskCleanUpEarlyFlushTest.SUBPROCESS_CHILD2);
        RuntimeEngine engine = getRuntimeEngine();
        KieSession ksession = engine.getKieSession();
        TaskService taskService = engine.getTaskService();
        ksession.addEventListener(new org.jbpm.services.task.admin.listener.TaskCleanUpProcessEventListener(taskService));
        ProcessInstance processInstance = ksession.startProcess(HumanTaskCleanUpEarlyFlushTest.SUBPROCESS_PARENT_ID);
        for (int i = 0; i < 2; ++i) {
            List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
            assertEquals(1, tasks.size());
            long taskId = tasks.get(0).getId();
            taskService.start(taskId, "john");
            taskService.complete(taskId, "john", null);
        }
        assertProcessInstanceCompleted(processInstance.getId());
    }

    private void testSignal(Strategy strategy) {
        createRuntimeManager(strategy, null, HumanTaskCleanUpEarlyFlushTest.SIGNAL_SENDER, HumanTaskCleanUpEarlyFlushTest.SIGNAL_RECEIVER);
        RuntimeEngine engine = getRuntimeEngine();
        KieSession ksession = engine.getKieSession();
        TaskService taskService = engine.getTaskService();
        ksession.addEventListener(new org.jbpm.services.task.admin.listener.TaskCleanUpProcessEventListener(taskService));
        ksession.startProcess(HumanTaskCleanUpEarlyFlushTest.SIGNAL_SENDER_ID);
    }

    @Test
    @BZ(value = "1165466")
    public void testSignalSingleton() {
        testSignal(JbpmJUnitBaseTestCase.Strategy.SINGLETON);
    }

    @Test
    @BZ(value = "1165466")
    public void testSignalPerRequest() {
        testSignal(JbpmJUnitBaseTestCase.Strategy.REQUEST);
    }

    @Test
    @BZ(value = "1165466")
    public void testSignalPerProcessInstance() {
        testSignal(JbpmJUnitBaseTestCase.Strategy.PROCESS_INSTANCE);
    }
}

