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


package org.jbpm.process.workitem.handler;

import org.jbpm.test.util.AbstractBaseTest;
import org.drools.compiler.compiler.BPMN2ProcessFactory;
import org.drools.core.impl.EnvironmentFactory;
import java.util.HashMap;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import java.util.Map;
import org.drools.compiler.compiler.ProcessBuilderFactory;
import org.drools.core.marshalling.impl.ProcessMarshallerFactory;
import org.drools.core.runtime.process.ProcessRuntimeFactory;
import java.util.Properties;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.junit.Test;

/**
 * This is a sample file to launch a process.
 */
public class HandlerTest extends AbstractBaseTest {
    @Test
    public void testHandler() throws Exception {
        KnowledgeBase kbase = HandlerTest.readKnowledgeBase();
        StatefulKnowledgeSession ksession = HandlerTest.createSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Handler", new JavaHandlerWorkItemHandler(ksession));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("employeeId", "12345-ABC");
        ksession.startProcess("com.sample.bpmn.java", params);
    }

    private static KnowledgeBase readKnowledgeBase() throws Exception {
        ProcessBuilderFactory.setProcessBuilderFactoryService(new org.jbpm.process.builder.ProcessBuilderFactoryServiceImpl());
        ProcessMarshallerFactory.setProcessMarshallerFactoryService(new org.jbpm.marshalling.impl.ProcessMarshallerFactoryServiceImpl());
        ProcessRuntimeFactory.setProcessRuntimeFactoryService(new org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl());
        BPMN2ProcessFactory.setBPMN2ProcessProvider(new org.jbpm.bpmn2.BPMN2ProcessProviderImpl());
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("JavaHandler.bpmn"), ResourceType.BPMN2);
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

