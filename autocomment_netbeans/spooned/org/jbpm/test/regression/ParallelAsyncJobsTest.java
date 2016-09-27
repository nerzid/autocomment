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

import java.util.ArrayList;
import org.assertj.core.api.Assertions;
import qa.tools.ikeeper.annotation.BZ;
import java.util.HashMap;
import org.jbpm.test.JbpmAsyncJobTestCase;
import org.kie.api.runtime.KieSession;
import java.util.List;
import java.util.Map;
import org.kie.api.runtime.process.ProcessInstance;
import org.junit.Test;

public class ParallelAsyncJobsTest extends JbpmAsyncJobTestCase {
    private static final String PARENT = "org/jbpm/test/regression/ParallelAsyncJobs-parent.bpmn2";

    private static final String PARENT_ID = "org.jbpm.test.regression.ParallelAsyncJobs-parent";

    private static final String SUBPROCESS = "org/jbpm/test/regression/ParallelAsyncJobs-subprocess.bpmn2";

    /**
     * The tests verifies that async. jobs will be executed in parallel.
     * 
     * JobExecutor configuration:
     * - Thread pool size = 4
     * - Pending task scan interval = 1 second
     * 
     * The process in this test will create 4 long running tasks (8 seconds) one after another. Then
     * the test will sleep for 4 seconds giving the JobExecutor time to pick up at least 2 tasks.
     * 
     * The JobExecutor should be able to pick up all the tasks because one task takes 8 seconds to
     * complete and the scan interval is 1 second. On the other hand a task should not complete in
     * the 4 seconds so pending task count should not be lower than 3 if parallelism does not work.
     */
    @Test
    @BZ(value = "1146829")
    public void testRunBasicAsync() throws Exception {
        KieSession ks = createKSession(ParallelAsyncJobsTest.PARENT, ParallelAsyncJobsTest.SUBPROCESS);
        ks.getWorkItemManager().registerWorkItemHandler("async", new org.jbpm.executor.impl.wih.AsyncWorkItemHandler(getExecutorService(), "org.jbpm.test.command.LongRunningCommand"));
        List<String> exceptions = new ArrayList<String>();
        exceptions.add("ADRESS_EXCEPTION");
        exceptions.add("ID_EXCEPTION");
        exceptions.add("PHONE_EXCEPTION");
        Map<String, Object> pm = new HashMap<String, Object>();
        pm.put("exceptions", exceptions);
        ProcessInstance pi = ks.startProcess(ParallelAsyncJobsTest.PARENT_ID, pm);
        // assert that the main process was completed as tasks are executed asynchronously
        assertProcessInstanceCompleted(pi.getId());
        // wait for the JobExecutor to pick up at least 2 jobs
        Thread.sleep(4000);
        // assert that more than 1 task was picked up by the executor - if parallel execution
        // does work then more than 1 task got picked up because the scan interval is 1 second!
        Assertions.assertThat(getExecutorService().getPendingRequests(new org.kie.api.runtime.query.QueryContext()).size()).as("More than 2 async jobs should have been executed").isLessThanOrEqualTo(2);
        // wait for the process
        Thread.sleep(8000);
        // assert that all jobs have where completed.
        Assertions.assertThat(getExecutorService().getCompletedRequests(new org.kie.api.runtime.query.QueryContext())).as("All async jobs should have been executed").hasSize(4);
    }
}

