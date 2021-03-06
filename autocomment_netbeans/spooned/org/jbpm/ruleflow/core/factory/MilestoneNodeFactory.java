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


package org.jbpm.ruleflow.core.factory;

import java.util.ArrayList;
import org.jbpm.workflow.core.DroolsAction;
import java.util.List;
import org.jbpm.workflow.core.node.MilestoneNode;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.process.core.timer.Timer;

/**
 */
public class MilestoneNodeFactory extends NodeFactory {
    public MilestoneNodeFactory(RuleFlowNodeContainerFactory nodeContainerFactory, NodeContainer nodeContainer, long id) {
        super(nodeContainerFactory, nodeContainer, id);
    }

    protected Node createNode() {
        return new MilestoneNode();
    }

    protected MilestoneNode getMilestoneNode() {
        return ((MilestoneNode) (getNode()));
    }

    public MilestoneNodeFactory name(String name) {
        getNode().setName(name);
        return MilestoneNodeFactory.this;
    }

    public MilestoneNodeFactory onEntryAction(String dialect, String action) {
        if ((getMilestoneNode().getActions(dialect)) != null) {
            getMilestoneNode().getActions(dialect).add(new org.jbpm.workflow.core.impl.DroolsConsequenceAction(dialect, action));
        } else {
            List<DroolsAction> actions = new ArrayList<DroolsAction>();
            actions.add(new org.jbpm.workflow.core.impl.DroolsConsequenceAction(dialect, action));
            getMilestoneNode().setActions(MilestoneNode.EVENT_NODE_ENTER, actions);
        }
        return MilestoneNodeFactory.this;
    }

    public MilestoneNodeFactory onExitAction(String dialect, String action) {
        if ((getMilestoneNode().getActions(dialect)) != null) {
            getMilestoneNode().getActions(dialect).add(new org.jbpm.workflow.core.impl.DroolsConsequenceAction(dialect, action));
        } else {
            List<DroolsAction> actions = new ArrayList<DroolsAction>();
            actions.add(new org.jbpm.workflow.core.impl.DroolsConsequenceAction(dialect, action));
            getMilestoneNode().setActions(MilestoneNode.EVENT_NODE_EXIT, actions);
        }
        return MilestoneNodeFactory.this;
    }

    public MilestoneNodeFactory constraint(String constraint) {
        getMilestoneNode().setConstraint(constraint);
        return MilestoneNodeFactory.this;
    }

    public MilestoneNodeFactory timer(String delay, String period, String dialect, String action) {
        Timer timer = new Timer();
        timer.setDelay(delay);
        timer.setPeriod(period);
        getMilestoneNode().addTimer(timer, new org.jbpm.workflow.core.impl.DroolsConsequenceAction(dialect, action));
        return MilestoneNodeFactory.this;
    }
}

