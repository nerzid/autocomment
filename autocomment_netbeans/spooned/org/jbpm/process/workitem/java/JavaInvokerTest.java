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


package org.jbpm.process.workitem.java;

import org.jbpm.test.util.AbstractBaseTest;
import java.util.ArrayList;
import org.drools.compiler.compiler.BPMN2ProcessFactory;
import org.drools.core.impl.EnvironmentFactory;
import java.util.HashMap;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import java.util.List;
import java.util.Map;
import org.drools.compiler.compiler.ProcessBuilderFactory;
import org.drools.core.marshalling.impl.ProcessMarshallerFactory;
import org.drools.core.runtime.process.ProcessRuntimeFactory;
import java.util.Properties;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.junit.Test;

public class JavaInvokerTest extends AbstractBaseTest {
    @Test
    public void testStaticMethod1() throws Exception {
        KnowledgeBase kbase = JavaInvokerTest.readKnowledgeBase();
        StatefulKnowledgeSession ksession = JavaInvokerTest.createSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Java", new JavaInvocationWorkItemHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("Class", "org.jbpm.process.workitem.java.MyJavaClass");
        params.put("Method", "staticMethod1");
        params.put("Object", new MyJavaClass());
        ksession.startProcess("com.sample.bpmn.java", params);
    }

    @Test
    public void testStaticMethod2() throws Exception {
        KnowledgeBase kbase = JavaInvokerTest.readKnowledgeBase();
        StatefulKnowledgeSession ksession = JavaInvokerTest.createSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Java", new JavaInvocationWorkItemHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("Class", "org.jbpm.process.workitem.java.MyJavaClass");
        params.put("Method", "staticMethod2");
        params.put("Object", new MyJavaClass());
        List<Object> parameters = new ArrayList<Object>();
        parameters.add("krisv");
        params.put("Parameters", parameters);
        ksession.startProcess("com.sample.bpmn.java", params);
    }

    @Test
    public void testMyFirstMethod1() throws Exception {
        KnowledgeBase kbase = JavaInvokerTest.readKnowledgeBase();
        StatefulKnowledgeSession ksession = JavaInvokerTest.createSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Java", new JavaInvocationWorkItemHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("Class", "org.jbpm.process.workitem.java.MyJavaClass");
        params.put("Method", "myFirstMethod");
        params.put("Object", new MyJavaClass());
        List<Object> parameters = new ArrayList<Object>();
        parameters.add("krisv");
        parameters.add(32);
        params.put("Parameters", parameters);
        List<String> parameterTypes = new ArrayList<String>();
        parameterTypes.add("java.lang.String");
        parameterTypes.add("java.lang.Integer");
        params.put("ParameterTypes", parameterTypes);
        ksession.startProcess("com.sample.bpmn.java", params);
    }

    @Test
    public void testMyFirstMethod2() throws Exception {
        KnowledgeBase kbase = JavaInvokerTest.readKnowledgeBase();
        StatefulKnowledgeSession ksession = JavaInvokerTest.createSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Java", new JavaInvocationWorkItemHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("Class", "org.jbpm.process.workitem.java.MyJavaClass");
        params.put("Method", "myFirstMethod");
        params.put("Object", new MyJavaClass());
        List<Object> parameters = new ArrayList<Object>();
        parameters.add("krisv");
        parameters.add("32");
        params.put("Parameters", parameters);
        List<String> parameterTypes = new ArrayList<String>();
        parameterTypes.add("java.lang.String");
        parameterTypes.add("java.lang.String");
        params.put("ParameterTypes", parameterTypes);
        ksession.startProcess("com.sample.bpmn.java", params);
    }

    @Test
    public void testMyFirstMethod3() throws Exception {
        KnowledgeBase kbase = JavaInvokerTest.readKnowledgeBase();
        StatefulKnowledgeSession ksession = JavaInvokerTest.createSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Java", new JavaInvocationWorkItemHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("Class", "org.jbpm.process.workitem.java.MyJavaClass");
        params.put("Method", "myFirstMethod");
        params.put("Object", new MyJavaClass());
        List<Object> parameters = new ArrayList<Object>();
        parameters.add("krisv");
        parameters.add(32);
        parameters.add("male");
        params.put("Parameters", parameters);
        ksession.startProcess("com.sample.bpmn.java", params);
    }

    @Test
    public void testMySecondMethod() throws Exception {
        KnowledgeBase kbase = JavaInvokerTest.readKnowledgeBase();
        StatefulKnowledgeSession ksession = JavaInvokerTest.createSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Java", new JavaInvocationWorkItemHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("Class", "org.jbpm.process.workitem.java.MyJavaClass");
        params.put("Method", "mySecondMethod");
        params.put("Object", new MyJavaClass());
        List<Object> parameters = new ArrayList<Object>();
        parameters.add("krisv");
        List<String> children = new ArrayList<String>();
        children.add("Arne");
        parameters.add(children);
        params.put("Parameters", parameters);
        ksession.startProcess("com.sample.bpmn.java.list", params);
    }

    @Test
    public void failingtestHello() throws Exception {
        KnowledgeBase kbase = JavaInvokerTest.readKnowledgeBase();
        StatefulKnowledgeSession ksession = JavaInvokerTest.createSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Java", new JavaInvocationWorkItemHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("Class", "org.jbpm.process.workitem.java.MyJavaClass");
        params.put("Method", "writeHello");
        List<Object> parameters = new ArrayList<Object>();
        parameters.add("krisv");
        params.put("Parameters", parameters);
        ksession.startProcess("com.sample.bpmn.java", params);
    }

    private static KnowledgeBase readKnowledgeBase() throws Exception {
        ProcessBuilderFactory.setProcessBuilderFactoryService(new org.jbpm.process.builder.ProcessBuilderFactoryServiceImpl());
        ProcessMarshallerFactory.setProcessMarshallerFactoryService(new org.jbpm.marshalling.impl.ProcessMarshallerFactoryServiceImpl());
        ProcessRuntimeFactory.setProcessRuntimeFactoryService(new org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl());
        BPMN2ProcessFactory.setBPMN2ProcessProvider(new org.jbpm.bpmn2.BPMN2ProcessProviderImpl());
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("JavaInvoker.bpmn"), ResourceType.BPMN2);
        kbuilder.add(ResourceFactory.newClassPathResource("JavaInvokerListResult.bpmn"), ResourceType.BPMN2);
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

