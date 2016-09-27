/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.test.functional.timer;

import org.junit.Before;
import java.util.HashMap;
import org.jbpm.test.JBPMHelper;
import org.jbpm.test.JbpmTestCase;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.junit.Test;
import org.kie.api.runtime.process.WorkItem;
import org.drools.core.process.instance.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

/**
 * See JBPM-3170/JBPM-3391
 */
public class InMemoryTimerPersistenceTest extends JbpmTestCase {
    // General setup
    private static final Logger logger = LoggerFactory.getLogger(InMemoryTimerPersistenceTest.class);

    // Test processses
    private static final String PROCESS_FILE_NAME = "org/jbpm/test/functional/timer/boundaryTimerProcess.bpmn";

    private static final String PROCESS_NAME = "BoundaryTimerEventProcess";

    private static final String WORK_ITEM_HANLDER_TASK = "Human Task";

    private static final String TIMER_FIRED_PROP = "timerFired";

    private static final String TIMER_FIRED_TIME_PROP = "afterTimerTime";

    public InMemoryTimerPersistenceTest() {
        super(true, false);
    }

    @Before
    public void setup() {
        System.clearProperty(InMemoryTimerPersistenceTest.TIMER_FIRED_PROP);
        System.clearProperty(InMemoryTimerPersistenceTest.TIMER_FIRED_TIME_PROP);
    }

    @Test
    public void boundaryEventTimerAndCompleteHumanTaskWithoutPersistence() throws InterruptedException {
        createRuntimeManager(InMemoryTimerPersistenceTest.PROCESS_FILE_NAME);
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        KieSession ksession = runtimeEngine.getKieSession();
        // Do stuff
        InMemoryTimerPersistenceTest.HumanTaskMockHandler humanTaskMockHandler = new InMemoryTimerPersistenceTest.HumanTaskMockHandler();
        ProcessInstance process = registerHTHandlerAndStartProcess(ksession, humanTaskMockHandler);
        sleepAndVerifyTimerRuns(process.getState());
        completeWork(ksession, humanTaskMockHandler);
        // The process reaches the end node
        int processState = process.getState();
        assertEquals(("Expected process state to be " + (JBPMHelper.processStateName[ProcessInstance.STATE_COMPLETED])), ProcessInstance.STATE_COMPLETED, processState);
    }

    private ProcessInstance registerHTHandlerAndStartProcess(KieSession ksession, InMemoryTimerPersistenceTest.HumanTaskMockHandler humanTaskMockHandler) {
        // Register Human Task Handler
        ksession.getWorkItemManager().registerWorkItemHandler(InMemoryTimerPersistenceTest.WORK_ITEM_HANLDER_TASK, humanTaskMockHandler);
        // Start the process
        ProcessInstance process = ksession.startProcess(InMemoryTimerPersistenceTest.PROCESS_NAME);
        long processId = process.getId();
        assertTrue("process id not saved", (processId > 0));
        // The process is in the Human Task waiting for its completion
        int processState = process.getState();
        assertEquals(((("Expected process state to be " + (JBPMHelper.processStateName[ProcessInstance.STATE_ACTIVE])) + " not ") + (JBPMHelper.processStateName[processState])), ProcessInstance.STATE_ACTIVE, processState);
        return process;
    }

    private void completeWork(KieSession ksession, InMemoryTimerPersistenceTest.HumanTaskMockHandler humanTaskMockHandler) {
        assertTrue("The work item task handler does not have a work item!", ((humanTaskMockHandler.workItem) != null));
        long workItemId = humanTaskMockHandler.workItem.getId();
        assertTrue("work item id not saved", (workItemId > 0));
        // The Human Task is completed
        Map<String, Object> results = new HashMap<String, Object>();
        try {
            ksession.getWorkItemManager().completeWorkItem(workItemId, results);
        } catch (Exception e) {
            InMemoryTimerPersistenceTest.logger.warn("Work item could not be completed!");
            e.printStackTrace();
            fail((((e.getClass().getSimpleName()) + " thrown when completing work item: ") + (e.getMessage())));
        }
    }

    private void sleepAndVerifyTimerRuns(int processState) throws InterruptedException {
        // wait 3 seconds to see if the boss is notified
        if (processState == (ProcessInstance.STATE_ACTIVE)) {
            int sleep = 2000;
            InMemoryTimerPersistenceTest.logger.debug("Sleeping {} seconds", (sleep / 1000));
            Thread.sleep(sleep);
            InMemoryTimerPersistenceTest.logger.debug("Awake!");
        } 
        long afterSleepTime = System.currentTimeMillis();
        assertTrue("The timer has not fired!", timerHasFired());
        assertTrue("The timer did not fire on time!", (afterSleepTime > (timerFiredTime())));
        int timerFiredCount = timerFiredCount();
        assertTrue((("The timer only fired " + timerFiredCount) + " times."), (timerFiredCount >= 1));
    }

    private boolean timerHasFired() {
        String hasFired = System.getProperty(InMemoryTimerPersistenceTest.TIMER_FIRED_PROP);
        if (hasFired != null) {
            return true;
        } 
        return false;
    }

    private int timerFiredCount() {
        String timerFiredCount = System.getProperty(InMemoryTimerPersistenceTest.TIMER_FIRED_PROP);
        if (timerFiredCount == null) {
            return 0;
        } 
        return Integer.parseInt(timerFiredCount);
    }

    private long timerFiredTime() {
        String timerFiredCount = System.getProperty(InMemoryTimerPersistenceTest.TIMER_FIRED_TIME_PROP);
        if (timerFiredCount == null) {
            return 0;
        } 
        return Long.parseLong(timerFiredCount);
    }

    private static class HumanTaskMockHandler implements WorkItemHandler {
        private WorkItemManager workItemManager;

        private WorkItem workItem;

        public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
            InMemoryTimerPersistenceTest.HumanTaskMockHandler.this.workItem = workItem;
            InMemoryTimerPersistenceTest.HumanTaskMockHandler.this.workItemManager = manager;
            InMemoryTimerPersistenceTest.logger.debug("Work completed!");
        }

        public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
            InMemoryTimerPersistenceTest.HumanTaskMockHandler.this.workItemManager.abortWorkItem(workItem.getId());
            InMemoryTimerPersistenceTest.logger.debug("Work aborted.");
        }
    }
}

