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


package org.jbpm.bpmn2.concurrency;

import org.jbpm.test.util.AbstractBaseTest;
import junit.framework.Assert;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import org.junit.Ignore;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.jbpm.bpmn2.objects.Status;
import org.junit.Test;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

/**
 * This test takes time and resources, please only run it locally
 */
@Ignore
public class OneProcessPerThreadTest extends AbstractBaseTest {
    private static final int THREAD_COUNT = 1000;

    private static volatile AtomicInteger started = new AtomicInteger(0);

    private static volatile AtomicInteger done = new AtomicInteger(0);

    private static final Logger logger = LoggerFactory.getLogger(OneProcessPerThreadTest.class);

    protected StatefulKnowledgeSession createStatefulKnowledgeSession(KnowledgeBase kbase) {
        return kbase.newStatefulKnowledgeSession();
    }

    @Test
    public void testMultiThreadProcessInstanceWorkItem() throws Exception {
        final ConcurrentHashMap<Long, Long> workItems = new ConcurrentHashMap<Long, Long>();
        try {
            final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-MultiThreadServiceProcess.bpmn"), ResourceType.BPMN2);
            KnowledgeBase kbase = kbuilder.newKnowledgeBase();
            StatefulKnowledgeSession ksession = createStatefulKnowledgeSession(kbase);
            ksession.getWorkItemManager().registerWorkItemHandler("Log", new WorkItemHandler() {
                public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
                    Long threadId = ((Long) (workItem.getParameter("id")));
                    workItems.put(workItem.getProcessInstanceId(), threadId);
                }

                public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
                }
            });
            OneProcessPerThreadTest.startThreads(ksession);
            Assert.assertEquals(OneProcessPerThreadTest.THREAD_COUNT, workItems.size());
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail(("Should not raise any exception: " + (t.getMessage())));
        }
        int i = 0;
        while ((OneProcessPerThreadTest.started.get()) > (OneProcessPerThreadTest.done.get())) {
            OneProcessPerThreadTest.logger.info("{} > {}", OneProcessPerThreadTest.started, OneProcessPerThreadTest.done);
            Thread.sleep((10 * 1000));
            if ((++i) > 10) {
                Assert.fail("Not all threads completed.");
            } 
        }
    }

    private static void startThreads(StatefulKnowledgeSession ksession) throws Throwable {
        boolean success = true;
        final Thread[] t = new Thread[OneProcessPerThreadTest.THREAD_COUNT];
        final OneProcessPerThreadTest.ProcessInstanceStartRunner[] r = new OneProcessPerThreadTest.ProcessInstanceStartRunner[OneProcessPerThreadTest.THREAD_COUNT];
        for (int i = 0; i < (t.length); i++) {
            r[i] = new OneProcessPerThreadTest.ProcessInstanceStartRunner(ksession, i, "org.drools.integrationtests.multithread");
            t[i] = new Thread(r[i], ("thread-" + i));
            try {
                t[i].start();
            } catch (Throwable fault) {
                Assert.fail(("Unable to complete test: " + (fault.getMessage())));
            }
        }
        for (int i = 0; i < (t.length); i++) {
            t[i].join();
            if ((r[i].getStatus()) == (Status.FAIL)) {
                success = false;
            } 
        }
        if (!success) {
            Assert.fail("Multithread test failed. Look at the stack traces for details. ");
        } 
    }

    public static class ProcessInstanceStartRunner implements Runnable {
        private StatefulKnowledgeSession ksession;

        private String processId;

        private long id;

        private Status status;

        public ProcessInstanceStartRunner(StatefulKnowledgeSession ksession, int id, String processId) {
            OneProcessPerThreadTest.ProcessInstanceStartRunner.this.ksession = ksession;
            OneProcessPerThreadTest.ProcessInstanceStartRunner.this.id = id;
            OneProcessPerThreadTest.ProcessInstanceStartRunner.this.processId = processId;
        }

        public void run() {
            OneProcessPerThreadTest.started.incrementAndGet();
            try {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("id", id);
                ksession.startProcess(processId, params);
            } catch (Throwable t) {
                OneProcessPerThreadTest.ProcessInstanceStartRunner.this.status = Status.FAIL;
                OneProcessPerThreadTest.logger.error("{} failed: {}", Thread.currentThread().getName(), t.getMessage());
                t.printStackTrace();
            }
            OneProcessPerThreadTest.done.incrementAndGet();
        }

        public long getId() {
            return id;
        }

        public Status getStatus() {
            return status;
        }
    }
}

