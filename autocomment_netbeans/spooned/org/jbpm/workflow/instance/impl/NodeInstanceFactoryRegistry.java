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


package org.jbpm.workflow.instance.impl;

import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.instance.node.ActionNodeInstance;
import org.jbpm.workflow.core.node.AsyncEventNode;
import org.jbpm.workflow.instance.node.AsyncEventNodeInstance;
import org.jbpm.workflow.core.node.BoundaryEventNode;
import org.jbpm.workflow.instance.node.BoundaryEventNodeInstance;
import org.jbpm.workflow.core.node.CatchLinkNode;
import org.jbpm.workflow.instance.node.CatchLinkNodeInstance;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.jbpm.workflow.instance.node.CompositeContextNodeInstance;
import org.jbpm.workflow.core.node.CompositeNode;
import org.jbpm.workflow.instance.node.CompositeNodeInstance;
import org.jbpm.workflow.core.node.DynamicNode;
import org.jbpm.workflow.instance.node.DynamicNodeInstance;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.instance.node.EndNodeInstance;
import org.kie.api.runtime.Environment;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.instance.node.EventNodeInstance;
import org.jbpm.workflow.core.node.EventSubProcessNode;
import org.jbpm.workflow.instance.node.EventSubProcessNodeInstance;
import org.jbpm.workflow.core.node.FaultNode;
import org.jbpm.workflow.instance.node.FaultNodeInstance;
import org.jbpm.workflow.core.node.ForEachNode;
import org.jbpm.workflow.instance.node.ForEachNodeInstance;
import java.util.HashMap;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.instance.node.HumanTaskNodeInstance;
import org.jbpm.workflow.core.node.Join;
import org.jbpm.workflow.instance.node.JoinInstance;
import java.util.Map;
import org.jbpm.workflow.core.node.MilestoneNode;
import org.jbpm.workflow.instance.node.MilestoneNodeInstance;
import org.kie.api.definition.process.Node;
import org.jbpm.workflow.core.node.RuleSetNode;
import org.jbpm.workflow.instance.node.RuleSetNodeInstance;
import org.jbpm.workflow.core.node.Split;
import org.jbpm.workflow.instance.node.SplitInstance;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.instance.node.StartNodeInstance;
import org.jbpm.workflow.core.node.StateNode;
import org.jbpm.workflow.instance.node.StateNodeInstance;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.jbpm.workflow.instance.node.SubProcessNodeInstance;
import org.jbpm.workflow.core.node.ThrowLinkNode;
import org.jbpm.workflow.instance.node.ThrowLinkNodeInstance;
import org.jbpm.workflow.core.node.TimerNode;
import org.jbpm.workflow.instance.node.TimerNodeInstance;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.jbpm.workflow.instance.node.WorkItemNodeInstance;

public class NodeInstanceFactoryRegistry {
    private static final NodeInstanceFactoryRegistry INSTANCE = new NodeInstanceFactoryRegistry();

    private Map<Class<? extends Node>, NodeInstanceFactory> registry;

    public static NodeInstanceFactoryRegistry getInstance(Environment environment) {
        // allow custom NodeInstanceFactoryRegistry to be given as part of the environment - e.g simulation
        if ((environment != null) && ((environment.get("NodeInstanceFactoryRegistry")) != null)) {
            return ((NodeInstanceFactoryRegistry) (environment.get("NodeInstanceFactoryRegistry")));
        } 
        return NodeInstanceFactoryRegistry.INSTANCE;
    }

    protected NodeInstanceFactoryRegistry() {
        NodeInstanceFactoryRegistry.this.registry = new HashMap<Class<? extends Node>, NodeInstanceFactory>();
        // hard wired nodes:
        register(RuleSetNode.class, new org.jbpm.workflow.instance.impl.factory.CreateNewNodeFactory(RuleSetNodeInstance.class));
        register(Split.class, new org.jbpm.workflow.instance.impl.factory.CreateNewNodeFactory(SplitInstance.class));
        register(Join.class, new org.jbpm.workflow.instance.impl.factory.ReuseNodeFactory(JoinInstance.class));
        register(StartNode.class, new org.jbpm.workflow.instance.impl.factory.CreateNewNodeFactory(StartNodeInstance.class));
        register(EndNode.class, new org.jbpm.workflow.instance.impl.factory.CreateNewNodeFactory(EndNodeInstance.class));
        register(MilestoneNode.class, new org.jbpm.workflow.instance.impl.factory.CreateNewNodeFactory(MilestoneNodeInstance.class));
        register(SubProcessNode.class, new org.jbpm.workflow.instance.impl.factory.CreateNewNodeFactory(SubProcessNodeInstance.class));
        register(ActionNode.class, new org.jbpm.workflow.instance.impl.factory.CreateNewNodeFactory(ActionNodeInstance.class));
        register(WorkItemNode.class, new org.jbpm.workflow.instance.impl.factory.CreateNewNodeFactory(WorkItemNodeInstance.class));
        register(TimerNode.class, new org.jbpm.workflow.instance.impl.factory.CreateNewNodeFactory(TimerNodeInstance.class));
        register(FaultNode.class, new org.jbpm.workflow.instance.impl.factory.CreateNewNodeFactory(FaultNodeInstance.class));
        register(EventSubProcessNode.class, new org.jbpm.workflow.instance.impl.factory.CreateNewNodeFactory(EventSubProcessNodeInstance.class));
        register(CompositeNode.class, new org.jbpm.workflow.instance.impl.factory.CreateNewNodeFactory(CompositeNodeInstance.class));
        register(CompositeContextNode.class, new org.jbpm.workflow.instance.impl.factory.CreateNewNodeFactory(CompositeContextNodeInstance.class));
        register(HumanTaskNode.class, new org.jbpm.workflow.instance.impl.factory.CreateNewNodeFactory(HumanTaskNodeInstance.class));
        register(ForEachNode.class, new org.jbpm.workflow.instance.impl.factory.CreateNewNodeFactory(ForEachNodeInstance.class));
        register(EventNode.class, new org.jbpm.workflow.instance.impl.factory.CreateNewNodeFactory(EventNodeInstance.class));
        register(StateNode.class, new org.jbpm.workflow.instance.impl.factory.CreateNewNodeFactory(StateNodeInstance.class));
        register(DynamicNode.class, new org.jbpm.workflow.instance.impl.factory.CreateNewNodeFactory(DynamicNodeInstance.class));
        register(BoundaryEventNode.class, new org.jbpm.workflow.instance.impl.factory.CreateNewNodeFactory(BoundaryEventNodeInstance.class));
        register(AsyncEventNode.class, new org.jbpm.workflow.instance.impl.factory.CreateNewNodeFactory(AsyncEventNodeInstance.class));
        register(CatchLinkNode.class, new org.jbpm.workflow.instance.impl.factory.CreateNewNodeFactory(CatchLinkNodeInstance.class));
        register(ThrowLinkNode.class, new org.jbpm.workflow.instance.impl.factory.CreateNewNodeFactory(ThrowLinkNodeInstance.class));
    }

    public void register(Class<? extends Node> cls, NodeInstanceFactory factory) {
        NodeInstanceFactoryRegistry.this.registry.put(cls, factory);
    }

    public NodeInstanceFactory getProcessNodeInstanceFactory(Node node) {
        Class<?> clazz = node.getClass();
        while (clazz != null) {
            NodeInstanceFactory result = NodeInstanceFactoryRegistry.this.registry.get(clazz);
            if (result != null) {
                return result;
            } 
            clazz = clazz.getSuperclass();
        }
        return null;
    }
}

