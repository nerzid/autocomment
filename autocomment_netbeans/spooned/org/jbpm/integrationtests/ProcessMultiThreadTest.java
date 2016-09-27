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


package org.jbpm.integrationtests;

import org.jbpm.test.util.AbstractBaseTest;
import java.util.ArrayList;
import org.drools.compiler.compiler.DroolsError;
import java.io.InputStreamReader;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.junit.Test;

public class ProcessMultiThreadTest extends AbstractBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(ProcessMultiThreadTest.class);

    @Test
    public void testMultiThreadProcessInstanceSignalling() {
        final int THREAD_COUNT = 2;
        try {
            boolean success = true;
            final Thread[] t = new Thread[THREAD_COUNT];
            builder.addProcessFromXml(new InputStreamReader(getClass().getResourceAsStream("test_ProcessMultithreadEvent.rf")));
            if ((builder.getErrors().getErrors().length) > 0) {
                for (DroolsError error : builder.getErrors().getErrors()) {
                    ProcessMultiThreadTest.logger.error(error.toString());
                }
                fail("Could not parse process");
            } 
            StatefulKnowledgeSession session = createKieSession(true, builder.getPackage());
            session = JbpmSerializationHelper.getSerialisedStatefulKnowledgeSession(session);
            List<String> list = new ArrayList<String>();
            session.setGlobal("list", list);
            ProcessInstance processInstance = session.startProcess("org.drools.integrationtests.multithread");
            final ProcessMultiThreadTest.ProcessInstanceSignalRunner[] r = new ProcessMultiThreadTest.ProcessInstanceSignalRunner[THREAD_COUNT];
            for (int i = 0; i < (t.length); i++) {
                r[i] = new ProcessMultiThreadTest.ProcessInstanceSignalRunner(i, processInstance, ("event" + (i + 1)));
                t[i] = new Thread(r[i], ("thread-" + i));
                t[i].start();
            }
            for (int i = 0; i < (t.length); i++) {
                t[i].join();
                if ((r[i].getStatus()) == (ProcessMultiThreadTest.ProcessInstanceSignalRunner.Status.FAIL)) {
                    success = false;
                } 
            }
            if (!success) {
                fail("Multithread test failed. Look at the stack traces for details. ");
            } 
            assertEquals(2, list.size());
            assertFalse(list.get(0).equals(list.get(1)));
            assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        } catch (Exception e) {
            e.printStackTrace();
            fail(("Should not raise any exception: " + (e.getMessage())));
        }
    }

    public static class ProcessInstanceSignalRunner implements Runnable {
        private ProcessInstance processInstance;

        private String type;

        private ProcessMultiThreadTest.ProcessInstanceSignalRunner.Status status;

        private int id;

        public ProcessInstanceSignalRunner(int id, ProcessInstance processInstance, String type) {
            ProcessMultiThreadTest.ProcessInstanceSignalRunner.this.id = id;
            ProcessMultiThreadTest.ProcessInstanceSignalRunner.this.processInstance = processInstance;
            ProcessMultiThreadTest.ProcessInstanceSignalRunner.this.type = type;
            ProcessMultiThreadTest.ProcessInstanceSignalRunner.this.status = ProcessMultiThreadTest.ProcessInstanceSignalRunner.Status.SUCCESS;
        }

        public void run() {
            try {
                processInstance.signalEvent(type, null);
            } catch (Exception e) {
                ProcessMultiThreadTest.ProcessInstanceSignalRunner.this.status = ProcessMultiThreadTest.ProcessInstanceSignalRunner.Status.FAIL;
                ProcessMultiThreadTest.logger.warn("{} failed: {}", Thread.currentThread().getName(), e.getMessage());
            }
        }

        public static enum Status {
SUCCESS, FAIL;        }

        public int getId() {
            return id;
        }

        public ProcessMultiThreadTest.ProcessInstanceSignalRunner.Status getStatus() {
            return status;
        }
    }
}

