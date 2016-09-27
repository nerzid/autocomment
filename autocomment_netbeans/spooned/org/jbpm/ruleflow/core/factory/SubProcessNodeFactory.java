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
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.jbpm.process.core.timer.Timer;

/**
 */
public class SubProcessNodeFactory extends NodeFactory {
    public SubProcessNodeFactory(RuleFlowNodeContainerFactory nodeContainerFactory, NodeContainer nodeContainer, long id) {
        super(nodeContainerFactory, nodeContainer, id);
    }

    protected Node createNode() {
        return new SubProcessNode();
    }

    protected SubProcessNode getSubProcessNode() {
        return ((SubProcessNode) (getNode()));
    }

    public SubProcessNodeFactory name(String name) {
        getNode().setName(name);
        return SubProcessNodeFactory.this;
    }

    public SubProcessNodeFactory processId(final String processId) {
        getSubProcessNode().setProcessId(processId);
        return SubProcessNodeFactory.this;
    }

    public SubProcessNodeFactory waitForCompletion(boolean waitForCompletion) {
        getSubProcessNode().setWaitForCompletion(waitForCompletion);
        return SubProcessNodeFactory.this;
    }

    public SubProcessNodeFactory inMapping(String parameterName, String variableName) {
        getSubProcessNode().addInMapping(parameterName, variableName);
        return SubProcessNodeFactory.this;
    }

    public SubProcessNodeFactory outMapping(String parameterName, String variableName) {
        getSubProcessNode().addOutMapping(parameterName, variableName);
        return SubProcessNodeFactory.this;
    }

    public SubProcessNodeFactory independent(boolean independent) {
        getSubProcessNode().setIndependent(independent);
        return SubProcessNodeFactory.this;
    }

    public SubProcessNodeFactory onEntryAction(String dialect, String action) {
        if ((getSubProcessNode().getActions(dialect)) != null) {
            getSubProcessNode().getActions(dialect).add(new org.jbpm.workflow.core.impl.DroolsConsequenceAction(dialect, action));
        } else {
            List<DroolsAction> actions = new ArrayList<DroolsAction>();
            actions.add(new org.jbpm.workflow.core.impl.DroolsConsequenceAction(dialect, action));
            getSubProcessNode().setActions(MilestoneNode.EVENT_NODE_ENTER, actions);
        }
        return SubProcessNodeFactory.this;
    }

    public SubProcessNodeFactory onExitAction(String dialect, String action) {
        if ((getSubProcessNode().getActions(dialect)) != null) {
            getSubProcessNode().getActions(dialect).add(new org.jbpm.workflow.core.impl.DroolsConsequenceAction(dialect, action));
        } else {
            List<DroolsAction> actions = new ArrayList<DroolsAction>();
            actions.add(new org.jbpm.workflow.core.impl.DroolsConsequenceAction(dialect, action));
            getSubProcessNode().setActions(MilestoneNode.EVENT_NODE_EXIT, actions);
        }
        return SubProcessNodeFactory.this;
    }

    public SubProcessNodeFactory timer(String delay, String period, String dialect, String action) {
        Timer timer = new Timer();
        timer.setDelay(delay);
        timer.setPeriod(period);
        getSubProcessNode().addTimer(timer, new org.jbpm.workflow.core.impl.DroolsConsequenceAction(dialect, action));
        return SubProcessNodeFactory.this;
    }
}

