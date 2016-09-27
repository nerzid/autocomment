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


package org.jbpm.process.workitem;

import org.junit.Assert;
import org.drools.compiler.compiler.BPMN2ProcessFactory;
import org.drools.core.impl.EnvironmentFactory;
import java.util.HashMap;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import java.util.Map;
import org.kie.api.runtime.process.NodeInstance;
import org.drools.compiler.compiler.ProcessBuilderFactory;
import org.drools.core.marshalling.impl.ProcessMarshallerFactory;
import org.drools.core.runtime.process.ProcessRuntimeFactory;
import java.util.Properties;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.junit.Test;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.process.WorkflowProcessInstance;

public class AbstractWorkItemHandlerTest {
    @Test
    public void testServiceInvocationWithMultipleIntParams() throws Exception {
        KnowledgeBaseFactory.setKnowledgeBaseServiceFactory(new org.drools.core.impl.KnowledgeBaseFactoryServiceImpl());
        KnowledgeBase kbase = AbstractWorkItemHandlerTest.readKnowledgeBase();
        StatefulKnowledgeSession ksession = AbstractWorkItemHandlerTest.createSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new AbstractWorkItemHandlerTest.CustomWorkItemHandler(ksession));
        Map<String, Object> params = new HashMap<String, Object>();
        WorkflowProcessInstance processInstance = ((WorkflowProcessInstance) (ksession.startProcess("HR.test", params)));
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    private static KnowledgeBase readKnowledgeBase() throws Exception {
        ProcessBuilderFactory.setProcessBuilderFactoryService(new org.jbpm.process.builder.ProcessBuilderFactoryServiceImpl());
        ProcessMarshallerFactory.setProcessMarshallerFactoryService(new org.jbpm.marshalling.impl.ProcessMarshallerFactoryServiceImpl());
        ProcessRuntimeFactory.setProcessRuntimeFactoryService(new org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl());
        BPMN2ProcessFactory.setBPMN2ProcessProvider(new org.jbpm.bpmn2.BPMN2ProcessProviderImpl());
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-TwoUserTasks.bpmn2"), ResourceType.BPMN2);
        return kbuilder.newKnowledgeBase();
    }

    private static StatefulKnowledgeSession createSession(KnowledgeBase kbase) {
        Properties properties = new Properties();
        properties.put("drools.processInstanceManagerFactory", "org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory");
        properties.put("drools.processSignalManagerFactory", "org.jbpm.process.instance.event.DefaultSignalManagerFactory");
        KieSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);
        return kbase.newStatefulKnowledgeSession(config, EnvironmentFactory.newEnvironment());
    }

    private class CustomWorkItemHandler extends AbstractWorkItemHandler {
        public CustomWorkItemHandler(StatefulKnowledgeSession ksession) {
            super(ksession);
        }

        @Override
        public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
            NodeInstance nodeInstance = getNodeInstance(workItem);
            System.out.println(((("Node instance " + nodeInstance) + " for work item ") + workItem));
            manager.completeWorkItem(workItem.getId(), null);
        }

        @Override
        public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        }
    }
}

