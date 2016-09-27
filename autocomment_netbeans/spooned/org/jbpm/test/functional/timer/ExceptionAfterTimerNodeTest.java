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

import org.junit.After;
import java.util.Arrays;
import org.junit.Before;
import java.util.Collection;
import org.jbpm.test.listener.CountDownProcessEventListener;
import org.jbpm.test.JbpmTestCase;
import org.kie.api.runtime.KieSession;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.runtime.process.ProcessInstance;
import org.junit.runner.RunWith;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.junit.Test;

@RunWith(value = Parameterized.class)
public class ExceptionAfterTimerNodeTest extends JbpmTestCase {
    private final boolean useQuartz;

    @Parameters(name = "Use quartz scheduler={0}")
    public static Collection<Object[]> parameters() {
        Object[][] locking = new Object[][]{ new Object[]{ true } , new Object[]{ false } };
        return Arrays.asList(locking);
    }

    public ExceptionAfterTimerNodeTest(boolean useQuartz) {
        super(true, true);
        this.useQuartz = useQuartz;
    }

    @Before
    public void setup() {
        if (useQuartz) {
            System.setProperty("org.quartz.properties", "quartz-ram.properties");
        } 
    }

    @After
    public void cleanup() {
        System.clearProperty("org.quartz.properties");
    }

    @Test(timeout = 10000)
    public void testExceptionAfterTimer() {
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("TimerEvent", 1, true);
        createRuntimeManager("org/jbpm/test/functional/timer/ExceptionAfterTimer.bpmn2");
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        KieSession ksession = runtimeEngine.getKieSession();
        ksession.addEventListener(countDownListener);
        ProcessInstance pi = ksession.startProcess("com.bpms.customer.RuntimeExceptionAfterTimer");
        countDownListener.waitTillCompleted();
        pi = ksession.getProcessInstance(pi.getId());
        assertNotNull(pi);
        ksession.abortProcessInstance(pi.getId());
        pi = ksession.getProcessInstance(pi.getId());
        assertNull(pi);
    }
}

