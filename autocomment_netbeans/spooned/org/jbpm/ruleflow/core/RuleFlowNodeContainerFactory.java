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


package org.jbpm.ruleflow.core;

import org.jbpm.ruleflow.core.factory.ActionNodeFactory;
import org.jbpm.ruleflow.core.factory.BoundaryEventNodeFactory;
import org.jbpm.ruleflow.core.factory.CompositeNodeFactory;
import org.jbpm.ruleflow.core.factory.DynamicNodeFactory;
import org.jbpm.ruleflow.core.factory.EndNodeFactory;
import org.jbpm.ruleflow.core.factory.EventNodeFactory;
import org.jbpm.ruleflow.core.factory.FaultNodeFactory;
import org.jbpm.ruleflow.core.factory.ForEachNodeFactory;
import org.jbpm.ruleflow.core.factory.HumanTaskNodeFactory;
import org.jbpm.ruleflow.core.factory.JoinFactory;
import org.jbpm.ruleflow.core.factory.MilestoneNodeFactory;
import org.kie.api.definition.process.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.ruleflow.core.factory.RuleSetNodeFactory;
import org.jbpm.ruleflow.core.factory.SplitFactory;
import org.jbpm.ruleflow.core.factory.StartNodeFactory;
import org.jbpm.ruleflow.core.factory.SubProcessNodeFactory;
import org.jbpm.ruleflow.core.factory.TimerNodeFactory;
import org.jbpm.ruleflow.core.factory.WorkItemNodeFactory;

public abstract class RuleFlowNodeContainerFactory {
    private NodeContainer nodeContainer;

    protected void setNodeContainer(NodeContainer nodeContainer) {
        RuleFlowNodeContainerFactory.this.nodeContainer = nodeContainer;
    }

    protected NodeContainer getNodeContainer() {
        return nodeContainer;
    }

    public StartNodeFactory startNode(long id) {
        return new StartNodeFactory(RuleFlowNodeContainerFactory.this, nodeContainer, id);
    }

    public EndNodeFactory endNode(long id) {
        return new EndNodeFactory(RuleFlowNodeContainerFactory.this, nodeContainer, id);
    }

    public ActionNodeFactory actionNode(long id) {
        return new ActionNodeFactory(RuleFlowNodeContainerFactory.this, nodeContainer, id);
    }

    public MilestoneNodeFactory milestoneNode(long id) {
        return new MilestoneNodeFactory(RuleFlowNodeContainerFactory.this, nodeContainer, id);
    }

    public TimerNodeFactory timerNode(long id) {
        return new TimerNodeFactory(RuleFlowNodeContainerFactory.this, nodeContainer, id);
    }

    public HumanTaskNodeFactory humanTaskNode(long id) {
        return new HumanTaskNodeFactory(RuleFlowNodeContainerFactory.this, nodeContainer, id);
    }

    public SubProcessNodeFactory subProcessNode(long id) {
        return new SubProcessNodeFactory(RuleFlowNodeContainerFactory.this, nodeContainer, id);
    }

    public SplitFactory splitNode(long id) {
        return new SplitFactory(RuleFlowNodeContainerFactory.this, nodeContainer, id);
    }

    public JoinFactory joinNode(long id) {
        return new JoinFactory(RuleFlowNodeContainerFactory.this, nodeContainer, id);
    }

    public RuleSetNodeFactory ruleSetNode(long id) {
        return new RuleSetNodeFactory(RuleFlowNodeContainerFactory.this, nodeContainer, id);
    }

    public FaultNodeFactory faultNode(long id) {
        return new FaultNodeFactory(RuleFlowNodeContainerFactory.this, nodeContainer, id);
    }

    public EventNodeFactory eventNode(long id) {
        return new EventNodeFactory(RuleFlowNodeContainerFactory.this, nodeContainer, id);
    }

    public BoundaryEventNodeFactory boundaryEventNode(long id) {
        return new BoundaryEventNodeFactory(RuleFlowNodeContainerFactory.this, nodeContainer, id);
    }

    public CompositeNodeFactory compositeNode(long id) {
        return new CompositeNodeFactory(RuleFlowNodeContainerFactory.this, nodeContainer, id);
    }

    public ForEachNodeFactory forEachNode(long id) {
        return new ForEachNodeFactory(RuleFlowNodeContainerFactory.this, nodeContainer, id);
    }

    public DynamicNodeFactory dynamicNode(long id) {
        return new DynamicNodeFactory(RuleFlowNodeContainerFactory.this, nodeContainer, id);
    }

    public WorkItemNodeFactory workItemNode(long id) {
        return new WorkItemNodeFactory(RuleFlowNodeContainerFactory.this, nodeContainer, id);
    }

    public RuleFlowNodeContainerFactory connection(long fromId, long toId) {
        Node from = nodeContainer.getNode(fromId);
        Node to = nodeContainer.getNode(toId);
        new org.jbpm.workflow.core.impl.ConnectionImpl(from, Node.CONNECTION_DEFAULT_TYPE, to, Node.CONNECTION_DEFAULT_TYPE);
        return RuleFlowNodeContainerFactory.this;
    }

    public abstract RuleFlowNodeContainerFactory done();
}

