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


package org.jbpm.test.functional.service;

import org.junit.After;
import org.junit.Before;
import java.util.Date;
import java.util.HashMap;
import org.jbpm.test.JbpmTestCase;
import org.kie.api.runtime.KieSession;
import java.util.Map;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.jbpm.bpmn2.handler.ServiceTaskHandler;
import org.junit.Test;
import org.jbpm.workflow.instance.WorkflowProcessInstance;

public class ServiceTaskHandlerTest extends JbpmTestCase {
    private KieSession ksession;

    private ServiceTaskHandler bpmn2Handler;

    private org.jbpm.process.workitem.bpmn2.ServiceTaskHandler workitemsHandler;

    private static final int BPMN2 = 0;

    private static final int WORKITEMS = 1;

    public ServiceTaskHandlerTest() {
        super(true, true);
    }

    @Before
    public void setup() {
        RuntimeManager manager = createRuntimeManager("org/jbpm/test/functional/service/ServiceTaskShortenInterfaceName.bpmn2", "org/jbpm/test/functional/service/ServiceTaskFullInterfaceName.bpmn2");
        RuntimeEngine engine = manager.getRuntimeEngine(null);
        ksession = engine.getKieSession();
        bpmn2Handler = new ServiceTaskHandler();
        workitemsHandler = new org.jbpm.process.workitem.bpmn2.ServiceTaskHandler();
    }

    @After
    public void dispose() {
        if ((ksession) != null) {
            ksession.dispose();
            ksession = null;
        } 
    }

    @Test
    public void testShortenInterfaceNameBPMN2() throws Exception {
        assertServiceTaskCompleted("BPMN2-ServiceTaskShortenInterfaceName", ServiceTaskHandlerTest.BPMN2);
    }

    @Test
    public void testFullInterfaceNameBPMN2() throws Exception {
        assertServiceTaskCompleted("BPMN2-ServiceTaskFullInterfaceName", ServiceTaskHandlerTest.BPMN2);
    }

    @Test
    public void testShortenInterfaceNameWorkitems() throws Exception {
        assertServiceTaskCompleted("BPMN2-ServiceTaskShortenInterfaceName", ServiceTaskHandlerTest.WORKITEMS);
    }

    @Test
    public void testFullInterfaceNameWorkitems() throws Exception {
        assertServiceTaskCompleted("BPMN2-ServiceTaskFullInterfaceName", ServiceTaskHandlerTest.WORKITEMS);
    }

    private void assertServiceTaskCompleted(String processName, int jbpmmodule) throws Exception {
        if (jbpmmodule == (ServiceTaskHandlerTest.BPMN2)) {
            ksession.getWorkItemManager().registerWorkItemHandler("Service Task", bpmn2Handler);
        } else if (jbpmmodule == (ServiceTaskHandlerTest.WORKITEMS)) {
            ksession.getWorkItemManager().registerWorkItemHandler("Service Task", workitemsHandler);
        } 
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("IntegerVar", new Integer(12345));
        params.put("DateVar", new Date());
        WorkflowProcessInstance processInstance = ((WorkflowProcessInstance) (ksession.startProcess(processName.replace("-", ""), params)));
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals(Integer.valueOf(1), processInstance.getVariable("IntegerVar"));
    }
}

