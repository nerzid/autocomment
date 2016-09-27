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


package org.jbpm.test.functional.async;

import org.junit.After;
import org.junit.Before;
import java.util.HashMap;
import org.jbpm.test.JbpmTestCase;
import org.kie.api.runtime.KieSession;
import java.util.Map;
import org.junit.Test;
import org.jbpm.workflow.instance.WorkflowRuntimeException;

public class AsyncNodesWithoutExecutionServiceTest extends JbpmTestCase {
    private static final String PROCESS_ATC = "org.jbpm.test.functional.async.AsyncThreadContinuation";

    private static final String BPMN_ATC = "org/jbpm/test/functional/async/AsyncThreadContinuation.bpmn2";

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        addWorkItemHandler("SyncError", new org.jbpm.test.wih.FirstErrorWorkItemHandler());
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test(expected = WorkflowRuntimeException.class)
    public void testExceptionInMainThread() {
        KieSession ksession = createKSession(AsyncNodesWithoutExecutionServiceTest.BPMN_ATC);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("message", "Ivo");
        ksession.startProcess(AsyncNodesWithoutExecutionServiceTest.PROCESS_ATC, params);
    }
}

