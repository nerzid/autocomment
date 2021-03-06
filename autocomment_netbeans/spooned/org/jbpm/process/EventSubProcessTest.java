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


package org.jbpm.process;

import org.jbpm.test.util.AbstractBaseTest;
import org.jbpm.process.instance.impl.Action;
import org.jbpm.workflow.core.node.ActionNode;
import java.util.ArrayList;
import org.junit.Assert;
import org.jbpm.workflow.core.node.CompositeNode;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.EventSubProcessNode;
import org.jbpm.process.core.event.EventTypeFilter;
import org.kie.api.runtime.KieSession;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.jbpm.process.test.NodeCreator;
import org.drools.core.process.core.datatype.impl.type.ObjectDataType;
import org.kie.api.runtime.process.ProcessContext;
import org.kie.api.runtime.process.ProcessInstance;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.node.StartNode;
import org.junit.Test;
import org.jbpm.process.test.TestProcessEventListener;
import org.jbpm.process.test.TestWorkItemHandler;
import org.jbpm.process.core.context.variable.Variable;
import org.drools.core.process.core.Work;
import org.jbpm.workflow.core.node.WorkItemNode;

public class EventSubProcessTest extends AbstractBaseTest {
    public void addLogger() {
        logger = LoggerFactory.getLogger(EventSubProcessTest.this.getClass());
    }

    String[] nestedEventOrder = new String[]{ "bps" , "bnt-0" , "bnl-0" , "bnt-1" , "bnt-1:3" , "bnl-1:3" , "bnt-1:4" , "bnt-1:4:5" , "bnl-1:4:5" , "bnt-1:4:6" , "ant-1:4:6" , "anl-1:4:5" , "ant-1:4:5" , "ant-1:4" , "anl-1:3" , "ant-1:3" , "ant-1" , "anl-0" , "ant-0" , "aps" , "bnl-1:4:7:8" , "bnt-1:4:7:9" , "bnl-1:4:7:9" , "bnt-1:4:7:10" , "bnl-1:4:7:10" , "bnl-1:4:7" , "anl-1:4:7" , "anl-1:4:7:10" , "ant-1:4:7:10" , "anl-1:4:7:9" , "ant-1:4:7:9" , "anl-1:4:7:8" , "bnl-1:4:6" , "bnt-1:4:11" , "bnl-1:4:11" , "bnl-1:4" , "bnt-1:12" , "bnl-1:12" , "bnl-1" , "bnt-12" , "bnl-12" , "bpc" , "apc" , "anl-12" , "ant-12" , "anl-1" , "anl-1:12" , "ant-1:12" , "anl-1:4" , "anl-1:4:11" , "ant-1:4:11" , "anl-1:4:6" };

    @Test
    public void testNestedEventSubProcess() throws Exception {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setAutoComplete(true);
        String processId = "org.jbpm.process.event.subprocess";
        process.setId(processId);
        process.setName("Event SubProcess Process");
        List<Variable> variables = new ArrayList<Variable>();
        Variable variable = new Variable();
        variable.setName("event");
        ObjectDataType personDataType = new ObjectDataType();
        personDataType.setClassName("org.drools.Person");
        variable.setType(personDataType);
        variables.add(variable);
        process.getVariableScope().setVariables(variables);
        NodeCreator<StartNode> startNodeCreator = new NodeCreator<StartNode>(process, StartNode.class);
        NodeCreator<EndNode> endNodeCreator = new NodeCreator<EndNode>(process, EndNode.class);
        NodeCreator<CompositeNode> compNodeCreator = new NodeCreator<CompositeNode>(process, CompositeNode.class);
        // outer process
        StartNode startNode = startNodeCreator.createNode("start0");
        CompositeNode compositeNode = compNodeCreator.createNode("comp0");
        NodeCreator.connect(startNode, compositeNode);
        EndNode endNode = endNodeCreator.createNode("end0");
        NodeCreator.connect(compositeNode, endNode);
        // 1rst level nested subprocess
        startNodeCreator.setNodeContainer(compositeNode);
        endNodeCreator.setNodeContainer(compositeNode);
        compNodeCreator.setNodeContainer(compositeNode);
        startNode = startNodeCreator.createNode("start1");
        compositeNode = compNodeCreator.createNode("comp1");
        NodeCreator.connect(startNode, compositeNode);
        endNode = endNodeCreator.createNode("end1");
        NodeCreator.connect(compositeNode, endNode);
        // 2nd level subprocess
        startNodeCreator.setNodeContainer(compositeNode);
        endNodeCreator.setNodeContainer(compositeNode);
        NodeCreator<WorkItemNode> workItemNodeCreator = new NodeCreator<WorkItemNode>(compositeNode, WorkItemNode.class);
        startNode = startNodeCreator.createNode("start2");
        WorkItemNode workItemNode = workItemNodeCreator.createNode("workItem2");
        Work work = new org.drools.core.process.core.impl.WorkImpl();
        String workItemName = "play";
        work.setName(workItemName);
        workItemNode.setWork(work);
        NodeCreator.connect(startNode, workItemNode);
        endNode = endNodeCreator.createNode("end2");
        NodeCreator.connect(workItemNode, endNode);
        // (3rd level) event sub process in 2nd level subprocess
        NodeCreator<EventSubProcessNode> espNodeCreator = new NodeCreator<EventSubProcessNode>(compositeNode, EventSubProcessNode.class);
        EventSubProcessNode espNode = espNodeCreator.createNode("eventSub2");
        EventTypeFilter eventFilter = new EventTypeFilter();
        String EVENT_NAME = "subEvent";
        eventFilter.setType(EVENT_NAME);
        espNode.addEvent(eventFilter);
        startNodeCreator.setNodeContainer(espNode);
        endNodeCreator.setNodeContainer(espNode);
        NodeCreator<ActionNode> actionNodeCreator = new NodeCreator<ActionNode>(espNode, ActionNode.class);
        startNode = startNodeCreator.createNode("start3*");
        ActionNode actionNode = actionNodeCreator.createNode("print3*");
        actionNode.setName("Print");
        final List<String> eventList = new ArrayList<String>();
        DroolsAction action = new org.jbpm.workflow.core.impl.DroolsConsequenceAction("java", null);
        action.setMetaData("Action", new Action() {
            public void execute(ProcessContext context) throws Exception {
                eventList.add("Executed action");
            }
        });
        actionNode.setAction(action);
        NodeCreator.connect(startNode, actionNode);
        endNode = endNodeCreator.createNode("end3*");
        NodeCreator.connect(actionNode, endNode);
        // run process
        KieSession ksession = createKieSession(process);
        TestProcessEventListener procEventListener = new TestProcessEventListener();
        ksession.addEventListener(procEventListener);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler(workItemName, workItemHandler);
        ProcessInstance processInstance = ksession.startProcess(processId);
        processInstance.signalEvent(EVENT_NAME, null);
        Assert.assertEquals((("Event " + EVENT_NAME) + " did not fire!"), 1, eventList.size());
        ksession.getWorkItemManager().completeWorkItem(workItemHandler.getWorkItems().removeLast().getId(), null);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        verifyEventHistory(nestedEventOrder, procEventListener.getEventHistory());
    }
}

