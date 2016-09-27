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
import org.jbpm.workflow.core.node.HumanTaskNode;
import java.util.List;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.process.core.timer.Timer;
import org.drools.core.process.core.Work;

/**
 */
public class HumanTaskNodeFactory extends NodeFactory {
    public HumanTaskNodeFactory(RuleFlowNodeContainerFactory nodeContainerFactory, NodeContainer nodeContainer, long id) {
        super(nodeContainerFactory, nodeContainer, id);
    }

    protected Node createNode() {
        return new HumanTaskNode();
    }

    protected HumanTaskNode getHumanTaskNode() {
        return ((HumanTaskNode) (getNode()));
    }

    public HumanTaskNodeFactory name(String name) {
        getNode().setName(name);
        return HumanTaskNodeFactory.this;
    }

    public HumanTaskNodeFactory taskName(String taskName) {
        Work work = getHumanTaskNode().getWork();
        if (work == null) {
            work = new org.drools.core.process.core.impl.WorkImpl();
            getHumanTaskNode().setWork(work);
        } 
        work.setParameter("TaskName", taskName);
        return HumanTaskNodeFactory.this;
    }

    public HumanTaskNodeFactory actorId(String actorId) {
        Work work = getHumanTaskNode().getWork();
        if (work == null) {
            work = new org.drools.core.process.core.impl.WorkImpl();
            getHumanTaskNode().setWork(work);
        } 
        work.setParameter("ActorId", actorId);
        return HumanTaskNodeFactory.this;
    }

    public HumanTaskNodeFactory priority(String priority) {
        Work work = getHumanTaskNode().getWork();
        if (work == null) {
            work = new org.drools.core.process.core.impl.WorkImpl();
            getHumanTaskNode().setWork(work);
        } 
        work.setParameter("Priority", priority);
        return HumanTaskNodeFactory.this;
    }

    public HumanTaskNodeFactory comment(String comment) {
        Work work = getHumanTaskNode().getWork();
        if (work == null) {
            work = new org.drools.core.process.core.impl.WorkImpl();
            getHumanTaskNode().setWork(work);
        } 
        work.setParameter("Comment", comment);
        return HumanTaskNodeFactory.this;
    }

    public HumanTaskNodeFactory skippable(boolean skippable) {
        Work work = getHumanTaskNode().getWork();
        if (work == null) {
            work = new org.drools.core.process.core.impl.WorkImpl();
            getHumanTaskNode().setWork(work);
        } 
        work.setParameter("Skippable", Boolean.toString(skippable));
        return HumanTaskNodeFactory.this;
    }

    public HumanTaskNodeFactory content(String content) {
        Work work = getHumanTaskNode().getWork();
        if (work == null) {
            work = new org.drools.core.process.core.impl.WorkImpl();
            getHumanTaskNode().setWork(work);
        } 
        work.setParameter("Content", content);
        return HumanTaskNodeFactory.this;
    }

    public HumanTaskNodeFactory inMapping(String parameterName, String variableName) {
        getHumanTaskNode().addInMapping(parameterName, variableName);
        return HumanTaskNodeFactory.this;
    }

    public HumanTaskNodeFactory outMapping(String parameterName, String variableName) {
        getHumanTaskNode().addOutMapping(parameterName, variableName);
        return HumanTaskNodeFactory.this;
    }

    public HumanTaskNodeFactory waitForCompletion(boolean waitForCompletion) {
        getHumanTaskNode().setWaitForCompletion(waitForCompletion);
        return HumanTaskNodeFactory.this;
    }

    public HumanTaskNodeFactory swimlane(String swimlane) {
        getHumanTaskNode().setSwimlane(swimlane);
        return HumanTaskNodeFactory.this;
    }

    public HumanTaskNodeFactory onEntryAction(String dialect, String action) {
        if ((getHumanTaskNode().getActions(dialect)) != null) {
            getHumanTaskNode().getActions(dialect).add(new org.jbpm.workflow.core.impl.DroolsConsequenceAction(dialect, action));
        } else {
            List<DroolsAction> actions = new ArrayList<DroolsAction>();
            actions.add(new org.jbpm.workflow.core.impl.DroolsConsequenceAction(dialect, action));
            getHumanTaskNode().setActions(MilestoneNode.EVENT_NODE_ENTER, actions);
        }
        return HumanTaskNodeFactory.this;
    }

    public HumanTaskNodeFactory onExitAction(String dialect, String action) {
        if ((getHumanTaskNode().getActions(dialect)) != null) {
            getHumanTaskNode().getActions(dialect).add(new org.jbpm.workflow.core.impl.DroolsConsequenceAction(dialect, action));
        } else {
            List<DroolsAction> actions = new ArrayList<DroolsAction>();
            actions.add(new org.jbpm.workflow.core.impl.DroolsConsequenceAction(dialect, action));
            getHumanTaskNode().setActions(MilestoneNode.EVENT_NODE_EXIT, actions);
        }
        return HumanTaskNodeFactory.this;
    }

    public HumanTaskNodeFactory timer(String delay, String period, String dialect, String action) {
        Timer timer = new Timer();
        timer.setDelay(delay);
        timer.setPeriod(period);
        getHumanTaskNode().addTimer(timer, new org.jbpm.workflow.core.impl.DroolsConsequenceAction(dialect, action));
        return HumanTaskNodeFactory.this;
    }

    public HumanTaskNodeFactory workParameter(String name, Object value) {
        Work work = getHumanTaskNode().getWork();
        if (work == null) {
            work = new org.drools.core.process.core.impl.WorkImpl();
            getHumanTaskNode().setWork(work);
        } 
        work.setParameter(name, value);
        return HumanTaskNodeFactory.this;
    }
}

