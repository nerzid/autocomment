/**
 * Copyright 2015 JBoss by Red Hat.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import java.util.ArrayList;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.jbpm.test.listener.CountDownProcessEventListener;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.jbpm.test.JbpmTestCase;
import org.kie.api.runtime.KieSession;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.junit.Test;

/**
 * Test for UpdateTimmerCommand
 */
public class TimerUpdateTest extends JbpmTestCase {
    private static final Logger logger = LoggerFactory.getLogger(TimerPersistenceTest.class);

    private static final String TIMER_FILE = "org/jbpm/test/functional/timer/UpdateTimer.bpmn2";

    private static final String PROCESS_NAME = "UpdateTimer";

    private static final String TIMER_NAME = "Timer";

    private static final String START_TIMER_FILE = "org/jbpm/test/functional/timer/UpdateStartTimer.bpmn2";

    private static final String START_PROCESS_NAME = "UpdateStartTimer";

    private static final String START_TIMER_NAME = "StartTimer";

    private static final String BOUNDARY_TIMER_FILE = "org/jbpm/test/functional/timer/UpdateBoundaryTimer.bpmn2";

    private static final String BOUNDARY_PROCESS_NAME = "UpdateBoundaryTimer";

    private static final String BOUNDARY_TIMER_NAME = "BoundaryTimer";

    private static final String BOUNDARY_TIMER_ATTACHED_TO_NAME = "User";

    private RuntimeEngine runtimeEngine;

    private KieSession kieSession;

    private static final String TIMER_FIRED_TEXT = "Hello Adele";

    private static final String TIMER_FIRED_TIME_PROP = "Time Adele";

    @Before
    public void setup() {
        runtimeEngine = null;
        kieSession = null;
        System.clearProperty(TimerUpdateTest.TIMER_FIRED_TEXT);
        System.clearProperty(TimerUpdateTest.TIMER_FIRED_TIME_PROP);
    }

    @Test(timeout = 30000)
    public void updateTimerLongerDelayTest() {
        CountDownProcessEventListener countDownListener = new CountDownProcessEventListener(TimerUpdateTest.TIMER_NAME, 1);
        // delay is set for 5s
        setProcessScenario(TimerUpdateTest.TIMER_FILE);
        kieSession.addEventListener(countDownListener);
        final List<Long> list = new ArrayList<Long>();
        kieSession.addEventListener(new DefaultProcessEventListener() {
            @Override
            public void beforeProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });
        Assertions.assertThat(list).isEmpty();
        long id = kieSession.startProcess(TimerUpdateTest.PROCESS_NAME).getId();
        long startTime = System.currentTimeMillis();
        Assertions.assertThat(list).isNotEmpty();
        // set delay to 8s
        kieSession.execute(new org.jbpm.process.instance.command.UpdateTimerCommand(id, TimerUpdateTest.TIMER_NAME, 8));
        countDownListener.waitTillCompleted();
        Assertions.assertThat(timerHasFired()).isTrue();
        long firedTime = timerFiredTime();
        long timeDifference = Math.abs(((firedTime - startTime) - 8000));
        TimerUpdateTest.logger.info(((((("Start time: " + startTime) + ", fired time: ") + firedTime) + ", difference: ") + (firedTime - startTime)));
        Assertions.assertThat(timeDifference).isLessThan(500);
        Assertions.assertThat(kieSession.getProcessInstance(id)).isNull();
    }

    @Test(timeout = 30000)
    public void updateTimerShortherDelayTest() {
        CountDownProcessEventListener countDownListener = new CountDownProcessEventListener(TimerUpdateTest.TIMER_NAME, 1);
        // delay is set for 5s
        setProcessScenario(TimerUpdateTest.TIMER_FILE);
        kieSession.addEventListener(countDownListener);
        final List<Long> list = new ArrayList<Long>();
        kieSession.addEventListener(new DefaultProcessEventListener() {
            @Override
            public void beforeProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });
        Assertions.assertThat(list).isEmpty();
        long id = kieSession.startProcess(TimerUpdateTest.PROCESS_NAME).getId();
        long startTime = System.currentTimeMillis();
        Assertions.assertThat(list).isNotEmpty();
        // set delay to 3s
        kieSession.execute(new org.jbpm.process.instance.command.UpdateTimerCommand(id, TimerUpdateTest.TIMER_NAME, 3));
        countDownListener.waitTillCompleted();
        Assertions.assertThat(timerHasFired()).isTrue();
        long firedTime = timerFiredTime();
        long timeDifference = Math.abs(((firedTime - startTime) - 3000));
        TimerUpdateTest.logger.info(((((("Start time: " + startTime) + ", fired time: ") + firedTime) + ", difference: ") + (firedTime - startTime)));
        Assertions.assertThat(timeDifference).isLessThan(500);
        Assertions.assertThat(kieSession.getProcessInstance(id)).isNull();
    }

    @Test(timeout = 30000)
    public void updateTimerBeforeDelayTest() {
        CountDownProcessEventListener countDownListener = new CountDownProcessEventListener(TimerUpdateTest.TIMER_NAME, 1);
        // delay is set for 5s
        setProcessScenario(TimerUpdateTest.TIMER_FILE);
        kieSession.addEventListener(countDownListener);
        final List<Long> list = new ArrayList<Long>();
        kieSession.addEventListener(new DefaultProcessEventListener() {
            @Override
            public void beforeProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });
        Assertions.assertThat(list).isEmpty();
        long id = kieSession.startProcess(TimerUpdateTest.PROCESS_NAME).getId();
        long startTime = System.currentTimeMillis();
        Assertions.assertThat(list).isNotEmpty();
        // set delay on time that passed -> expected that timer fired immediately
        kieSession.execute(new org.jbpm.process.instance.command.UpdateTimerCommand(id, TimerUpdateTest.TIMER_NAME, (-5)));
        countDownListener.waitTillCompleted();
        Assertions.assertThat(timerHasFired()).isTrue();
        long firedTime = timerFiredTime();
        long timeDifference = Math.abs((firedTime - startTime));
        TimerUpdateTest.logger.info(((((("Start time: " + startTime) + ", fired time: ") + firedTime) + ", difference: ") + (firedTime - startTime)));
        Assertions.assertThat(timeDifference).isLessThan(500);
        Assertions.assertThat(kieSession.getProcessInstance(id)).isNull();
    }

    @Test(timeout = 30000)
    public void updateBoundaryTimerTest() {
        CountDownProcessEventListener countDownListener = new CountDownProcessEventListener(TimerUpdateTest.BOUNDARY_TIMER_NAME, 1);
        // timer is set for long duration (100s)
        setProcessScenario(TimerUpdateTest.BOUNDARY_TIMER_FILE);
        final List<Long> list = new ArrayList<Long>();
        kieSession.addEventListener(new DefaultProcessEventListener() {
            @Override
            public void beforeProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });
        kieSession.addEventListener(countDownListener);
        Assertions.assertThat(list).isEmpty();
        long id = kieSession.startProcess(TimerUpdateTest.BOUNDARY_PROCESS_NAME).getId();
        long startTime = System.currentTimeMillis();
        Assertions.assertThat(list).isNotEmpty();
        // set timer delay to 3s
        kieSession.execute(new org.jbpm.process.instance.command.UpdateTimerCommand(id, TimerUpdateTest.BOUNDARY_TIMER_ATTACHED_TO_NAME, 3));
        countDownListener.waitTillCompleted();
        Assertions.assertThat(timerHasFired()).isTrue();
        long firedTime = timerFiredTime();
        long timeDifference = Math.abs(((firedTime - startTime) - 3000));
        TimerUpdateTest.logger.info(((((("Start time: " + startTime) + ", fired time: ") + firedTime) + ", difference: ") + (firedTime - startTime)));
        Assertions.assertThat(timeDifference).isLessThan(1000);
        Assertions.assertThat(kieSession.getProcessInstance(id)).isNull();
    }

    private void setProcessScenario(String file) {
        createRuntimeManager(file);
        runtimeEngine = getRuntimeEngine();
        kieSession = runtimeEngine.getKieSession();
        Assertions.assertThat(kieSession).isNotNull();
    }

    private boolean timerHasFired() {
        String hasFired = System.getProperty(TimerUpdateTest.TIMER_FIRED_TEXT);
        return hasFired != null;
    }

    private int timerFiredCount() {
        String timerFiredCount = System.getProperty(TimerUpdateTest.TIMER_FIRED_TEXT);
        if (timerFiredCount == null) {
            return 0;
        } 
        return Integer.parseInt(timerFiredCount);
    }

    private long timerFiredTime() {
        String timerFiredTime = System.getProperty(TimerUpdateTest.TIMER_FIRED_TIME_PROP);
        if (timerFiredTime == null) {
            return 0;
        } 
        return Long.parseLong(timerFiredTime);
    }
}

