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


package org.jbpm.workflow.instance.node;

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Assert;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.slf4j.LoggerFactory;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.instance.impl.NodeInstanceFactoryRegistry;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.junit.Test;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;

public class EndNodeInstanceTest extends AbstractBaseTest {
    public void addLogger() {
        logger = LoggerFactory.getLogger(EndNodeInstanceTest.this.getClass());
    }

    @Test
    public void testEndNode() {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        MockNode mockNode = new MockNode();
        MockNodeInstanceFactory factory = new MockNodeInstanceFactory(new MockNodeInstance(mockNode));
        NodeInstanceFactoryRegistry.getInstance(ksession.getEnvironment()).register(mockNode.getClass(), factory);
        WorkflowProcessImpl process = new WorkflowProcessImpl();
        Node endNode = new org.jbpm.workflow.core.node.EndNode();
        endNode.setId(1);
        endNode.setName("end node");
        mockNode.setId(2);
        new org.jbpm.workflow.core.impl.ConnectionImpl(mockNode, Node.CONNECTION_DEFAULT_TYPE, endNode, Node.CONNECTION_DEFAULT_TYPE);
        process.addNode(mockNode);
        process.addNode(endNode);
        RuleFlowProcessInstance processInstance = new RuleFlowProcessInstance();
        processInstance.setState(ProcessInstance.STATE_ACTIVE);
        processInstance.setProcess(process);
        processInstance.setKnowledgeRuntime(((InternalKnowledgeRuntime) (ksession)));
        MockNodeInstance mockNodeInstance = ((MockNodeInstance) (processInstance.getNodeInstance(mockNode)));
        mockNodeInstance.triggerCompleted();
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }
}

