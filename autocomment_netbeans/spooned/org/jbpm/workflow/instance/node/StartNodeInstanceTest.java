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
import java.util.List;
import org.slf4j.LoggerFactory;
import org.jbpm.workflow.core.Node;
import org.kie.api.runtime.process.NodeInstance;
import org.jbpm.workflow.instance.impl.NodeInstanceFactoryRegistry;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.workflow.core.node.StartNode;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.junit.Test;

public class StartNodeInstanceTest extends AbstractBaseTest {
    public void addLogger() {
        logger = LoggerFactory.getLogger(StartNodeInstanceTest.this.getClass());
    }

    @Test
    public void testStartNode() {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        MockNode mockNode = new MockNode();
        MockNodeInstanceFactory mockNodeFactory = new MockNodeInstanceFactory(new MockNodeInstance(mockNode));
        NodeInstanceFactoryRegistry.getInstance(ksession.getEnvironment()).register(mockNode.getClass(), mockNodeFactory);
        RuleFlowProcess process = new RuleFlowProcess();
        StartNode startNode = new StartNode();
        startNode.setId(1);
        startNode.setName("start node");
        mockNode.setId(2);
        new org.jbpm.workflow.core.impl.ConnectionImpl(startNode, Node.CONNECTION_DEFAULT_TYPE, mockNode, Node.CONNECTION_DEFAULT_TYPE);
        process.addNode(startNode);
        process.addNode(mockNode);
        RuleFlowProcessInstance processInstance = new RuleFlowProcessInstance();
        processInstance.setProcess(process);
        processInstance.setKnowledgeRuntime(((InternalKnowledgeRuntime) (ksession)));
        Assert.assertEquals(ProcessInstance.STATE_PENDING, processInstance.getState());
        processInstance.start();
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        MockNodeInstance mockNodeInstance = mockNodeFactory.getMockNodeInstance();
        List<NodeInstance> triggeredBy = mockNodeInstance.getTriggers().get(org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE);
        Assert.assertNotNull(triggeredBy);
        Assert.assertEquals(1, triggeredBy.size());
        Assert.assertSame(startNode.getId(), triggeredBy.get(0).getNodeId());
    }
}

