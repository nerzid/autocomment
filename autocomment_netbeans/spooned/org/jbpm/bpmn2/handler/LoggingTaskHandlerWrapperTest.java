/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.bpmn2.handler;

import org.junit.After;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.BeforeClass;
import java.util.Collection;
import java.util.HashMap;
import org.jbpm.bpmn2.handler.LoggingTaskHandlerDecorator.InputParameter;
import org.jbpm.bpmn2.JbpmBpmn2TestCase;
import org.kie.api.KieBase;
import java.util.List;
import java.util.Map;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runner.RunWith;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.junit.Test;

@RunWith(value = Parameterized.class)
public class LoggingTaskHandlerWrapperTest extends JbpmBpmn2TestCase {
    private StatefulKnowledgeSession ksession;

    @Parameters
    public static Collection<Object[]> persistence() {
        Object[][] data = new Object[][]{ new Object[]{ false } , new Object[]{ true } };
        return Arrays.asList(data);
    }

    public LoggingTaskHandlerWrapperTest(boolean persistence) {
        super(persistence);
    }

    @BeforeClass
    public static void setup() throws Exception {
        setUpDataSource();
    }

    @After
    public void dispose() {
        if ((ksession) != null) {
            ksession.dispose();
            ksession = null;
        } 
    }

    @Test
    public void testLimitExceptionInfoList() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ExceptionThrowingServiceProcess.bpmn2");
        ksession = createKnowledgeSession(kbase);
        LoggingTaskHandlerDecorator loggingTaskHandlerWrapper = new LoggingTaskHandlerDecorator(ServiceTaskHandler.class, 2);
        loggingTaskHandlerWrapper.setPrintStackTrace(false);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", loggingTaskHandlerWrapper);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("serviceInputItem", "exception message");
        ksession.startProcess("ServiceProcess", params);
        ksession.startProcess("ServiceProcess", params);
        ksession.startProcess("ServiceProcess", params);
        int size = loggingTaskHandlerWrapper.getWorkItemExceptionInfoList().size();
        assertTrue(("WorkItemExceptionInfoList is too large: " + size), (size == 2));
    }

    @Test
    public void testFormatLoggingError() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ExceptionThrowingServiceProcess.bpmn2");
        ksession = createKnowledgeSession(kbase);
        LoggingTaskHandlerDecorator loggingTaskHandlerWrapper = new LoggingTaskHandlerDecorator(ServiceTaskHandler.class, 2);
        loggingTaskHandlerWrapper.setLoggedMessageFormat("{0} - {1} - {2} - {3}");
        List<InputParameter> inputParameters = new ArrayList<LoggingTaskHandlerDecorator.InputParameter>();
        inputParameters.add(InputParameter.EXCEPTION_CLASS);
        inputParameters.add(InputParameter.WORK_ITEM_ID);
        inputParameters.add(InputParameter.WORK_ITEM_NAME);
        inputParameters.add(InputParameter.PROCESS_INSTANCE_ID);
        loggingTaskHandlerWrapper.setLoggedMessageInput(inputParameters);
        loggingTaskHandlerWrapper.setPrintStackTrace(false);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", loggingTaskHandlerWrapper);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("serviceInputItem", "exception message");
        ksession.startProcess("ServiceProcess", params);
        ksession.startProcess("ServiceProcess", params);
        ksession.startProcess("ServiceProcess", params);
    }
}

