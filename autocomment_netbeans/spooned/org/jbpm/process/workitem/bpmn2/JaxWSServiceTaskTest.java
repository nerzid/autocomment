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


package org.jbpm.process.workitem.bpmn2;

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.After;
import org.junit.Assert;
import org.drools.compiler.compiler.BPMN2ProcessFactory;
import org.junit.Before;
import org.jbpm.test.util.CountDownProcessEventListener;
import javax.xml.ws.Endpoint;
import org.drools.core.impl.EnvironmentFactory;
import java.util.HashMap;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.drools.compiler.compiler.ProcessBuilderFactory;
import org.drools.core.marshalling.impl.ProcessMarshallerFactory;
import org.drools.core.runtime.process.ProcessRuntimeFactory;
import java.util.Properties;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.junit.Test;
import org.jbpm.bpmn2.handler.WorkItemHandlerRuntimeException;
import org.kie.api.runtime.process.WorkflowProcessInstance;

public class JaxWSServiceTaskTest extends AbstractBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(JaxWSServiceTaskTest.class);

    private Endpoint endpoint;

    private Endpoint endpoint2;

    private SimpleService service;

    @Before
    public void setUp() {
        startWebService();
    }

    @After
    public void tearDown() {
        stopWebService();
    }

    @Test
    public void testServiceInvocation() throws Exception {
        KnowledgeBaseFactory.setKnowledgeBaseServiceFactory(new org.drools.core.impl.KnowledgeBaseFactoryServiceImpl());
        KnowledgeBase kbase = JaxWSServiceTaskTest.readKnowledgeBase();
        StatefulKnowledgeSession ksession = JaxWSServiceTaskTest.createSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", new ServiceTaskHandler(ksession));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s", "john");
        params.put("mode", "sync");
        WorkflowProcessInstance processInstance = ((WorkflowProcessInstance) (ksession.startProcess("WebServiceTask", params)));
        String variable = ((String) (processInstance.getVariable("s")));
        Assert.assertEquals("Hello john", variable);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    @Test(timeout = 10000)
    public void testAsyncServiceInvocation() throws Exception {
        CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("Service Task", 1);
        KnowledgeBaseFactory.setKnowledgeBaseServiceFactory(new org.drools.core.impl.KnowledgeBaseFactoryServiceImpl());
        KnowledgeBase kbase = JaxWSServiceTaskTest.readKnowledgeBase();
        StatefulKnowledgeSession ksession = JaxWSServiceTaskTest.createSession(kbase);
        ksession.addEventListener(countDownListener);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", new ServiceTaskHandler(ksession));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s", "john");
        params.put("mode", "async");
        WorkflowProcessInstance processInstance = ((WorkflowProcessInstance) (ksession.startProcess("WebServiceTask", params)));
        JaxWSServiceTaskTest.logger.info("Service invoked async...waiting to get reponse back");
        countDownListener.waitTillCompleted();
        String variable = ((String) (processInstance.getVariable("s")));
        Assert.assertEquals("Hello john", variable);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    @Test
    public void testOneWayServiceInvocation() throws Exception {
        KnowledgeBaseFactory.setKnowledgeBaseServiceFactory(new org.drools.core.impl.KnowledgeBaseFactoryServiceImpl());
        KnowledgeBase kbase = JaxWSServiceTaskTest.readKnowledgeBase();
        StatefulKnowledgeSession ksession = JaxWSServiceTaskTest.createSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", new ServiceTaskHandler(ksession));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s", "john");
        params.put("mode", "oneway");
        WorkflowProcessInstance processInstance = ((WorkflowProcessInstance) (ksession.startProcess("WebServiceTask", params)));
        JaxWSServiceTaskTest.logger.info("Execution finished");
        String variable = ((String) (processInstance.getVariable("s")));
        Assert.assertNull(variable);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    @Test
    public void testServiceInvocationWithErrorHandled() throws Exception {
        KnowledgeBaseFactory.setKnowledgeBaseServiceFactory(new org.drools.core.impl.KnowledgeBaseFactoryServiceImpl());
        KnowledgeBase kbase = JaxWSServiceTaskTest.readKnowledgeBase();
        StatefulKnowledgeSession ksession = JaxWSServiceTaskTest.createSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", new ServiceTaskHandler(ksession));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s", "john");
        params.put("mode", "sync");
        WorkflowProcessInstance processInstance = ((WorkflowProcessInstance) (ksession.startProcess("WebServiceTaskError", params)));
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        Object error = processInstance.getVariable("exception");
        Assert.assertNotNull(error);
        Assert.assertTrue((error instanceof WorkItemHandlerRuntimeException));
    }

    @Test(timeout = 10000)
    public void testServiceInvocationProcessWith2WSImports() throws Exception {
        KnowledgeBaseFactory.setKnowledgeBaseServiceFactory(new org.drools.core.impl.KnowledgeBaseFactoryServiceImpl());
        KnowledgeBase kbase = JaxWSServiceTaskTest.readKnowledgeBase();
        StatefulKnowledgeSession ksession = JaxWSServiceTaskTest.createSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", new ServiceTaskHandler(ksession));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s", "john");
        params.put("mode", "sync");
        WorkflowProcessInstance processInstance = ((WorkflowProcessInstance) (ksession.startProcess("org.jboss.qa.jbpm.CallWS", params)));
        String variable = ((String) (processInstance.getVariable("s")));
        Assert.assertEquals("Hello john", variable);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    @Test(timeout = 10000)
    public void testServiceInvocationProcessWith2WSImportsWSHandler() throws Exception {
        KnowledgeBaseFactory.setKnowledgeBaseServiceFactory(new org.drools.core.impl.KnowledgeBaseFactoryServiceImpl());
        KnowledgeBase kbase = JaxWSServiceTaskTest.readKnowledgeBase();
        StatefulKnowledgeSession ksession = JaxWSServiceTaskTest.createSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", new org.jbpm.process.workitem.webservice.WebServiceWorkItemHandler(ksession));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s", "john");
        params.put("mode", "sync");
        WorkflowProcessInstance processInstance = ((WorkflowProcessInstance) (ksession.startProcess("org.jboss.qa.jbpm.CallWS", params)));
        String variable = ((String) (processInstance.getVariable("s")));
        Assert.assertEquals("Hello john", variable);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    @Test
    public void testServiceInvocationWithMultipleParams() throws Exception {
        KnowledgeBaseFactory.setKnowledgeBaseServiceFactory(new org.drools.core.impl.KnowledgeBaseFactoryServiceImpl());
        KnowledgeBase kbase = JaxWSServiceTaskTest.readKnowledgeBase();
        StatefulKnowledgeSession ksession = JaxWSServiceTaskTest.createSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", new org.jbpm.process.workitem.webservice.WebServiceWorkItemHandler(ksession));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s", new String[]{ "john" , "doe" });
        params.put("mode", "sync");
        WorkflowProcessInstance processInstance = ((WorkflowProcessInstance) (ksession.startProcess("multiparamws", params)));
        String variable = ((String) (processInstance.getVariable("s2")));
        Assert.assertEquals("Hello doe, john", variable);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    @Test
    public void testServiceInvocationWithMultipleIntParams() throws Exception {
        KnowledgeBaseFactory.setKnowledgeBaseServiceFactory(new org.drools.core.impl.KnowledgeBaseFactoryServiceImpl());
        KnowledgeBase kbase = JaxWSServiceTaskTest.readKnowledgeBase();
        StatefulKnowledgeSession ksession = JaxWSServiceTaskTest.createSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", new org.jbpm.process.workitem.webservice.WebServiceWorkItemHandler(ksession));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s", new int[]{ 2 , 3 });
        params.put("mode", "sync");
        WorkflowProcessInstance processInstance = ((WorkflowProcessInstance) (ksession.startProcess("multiparamws-int", params)));
        String variable = ((String) (processInstance.getVariable("s2")));
        Assert.assertEquals("Hello 2, 3", variable);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    @Test
    public void testOneWayServiceInvocationProcessWSHandler() throws Exception {
        KnowledgeBaseFactory.setKnowledgeBaseServiceFactory(new org.drools.core.impl.KnowledgeBaseFactoryServiceImpl());
        KnowledgeBase kbase = JaxWSServiceTaskTest.readKnowledgeBase();
        StatefulKnowledgeSession ksession = JaxWSServiceTaskTest.createSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", new org.jbpm.process.workitem.webservice.WebServiceWorkItemHandler(ksession));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s", "john");
        params.put("mode", "oneway");
        WorkflowProcessInstance processInstance = ((WorkflowProcessInstance) (ksession.startProcess("org.jboss.qa.jbpm.CallWS", params)));
        JaxWSServiceTaskTest.logger.info("Execution finished");
        String variable = ((String) (processInstance.getVariable("s")));
        Assert.assertNull(variable);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    private void startWebService() {
        JaxWSServiceTaskTest.this.service = new SimpleService();
        JaxWSServiceTaskTest.this.endpoint = Endpoint.publish("http://127.0.0.1:9876/HelloService/greeting", service);
        JaxWSServiceTaskTest.this.endpoint2 = Endpoint.publish("http://127.0.0.1:9877/SecondService/greeting", service);
    }

    private void stopWebService() {
        JaxWSServiceTaskTest.this.endpoint.stop();
        JaxWSServiceTaskTest.this.endpoint2.stop();
    }

    private static KnowledgeBase readKnowledgeBase() throws Exception {
        ProcessBuilderFactory.setProcessBuilderFactoryService(new org.jbpm.process.builder.ProcessBuilderFactoryServiceImpl());
        ProcessMarshallerFactory.setProcessMarshallerFactoryService(new org.jbpm.marshalling.impl.ProcessMarshallerFactoryServiceImpl());
        ProcessRuntimeFactory.setProcessRuntimeFactoryService(new org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl());
        BPMN2ProcessFactory.setBPMN2ProcessProvider(new org.jbpm.bpmn2.BPMN2ProcessProviderImpl());
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-JaxWSServiceTask.bpmn2"), ResourceType.BPMN2);
        kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-JaxWSServiceTaskWithErrorBoundaryEvent.bpmn2"), ResourceType.BPMN2);
        kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-TwoWebServiceImports.bpmn"), ResourceType.BPMN2);
        kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-MultipleParamsWebService.bpmn"), ResourceType.BPMN2);
        kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-MultipleIntParamsWebService.bpmn"), ResourceType.BPMN2);
        return kbuilder.newKnowledgeBase();
    }

    private static StatefulKnowledgeSession createSession(KnowledgeBase kbase) {
        Properties properties = new Properties();
        properties.put("drools.processInstanceManagerFactory", "org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory");
        properties.put("drools.processSignalManagerFactory", "org.jbpm.process.instance.event.DefaultSignalManagerFactory");
        KieSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);
        return kbase.newStatefulKnowledgeSession(config, EnvironmentFactory.newEnvironment());
    }
}

