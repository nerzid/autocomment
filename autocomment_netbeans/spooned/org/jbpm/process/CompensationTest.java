/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
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
import org.junit.After;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Assert;
import org.jbpm.workflow.core.node.BoundaryEventNode;
import org.jbpm.process.core.context.exception.CompensationHandler;
import org.jbpm.process.core.context.exception.CompensationScope;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.jbpm.workflow.core.node.CompositeNode;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.process.core.event.EventFilter;
import org.jbpm.workflow.core.node.EventSubProcessNode;
import org.jbpm.process.core.event.EventTypeFilter;
import org.kie.api.runtime.KieSession;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.jbpm.workflow.core.Node;
import org.kie.api.definition.process.NodeContainer;
import org.jbpm.process.test.NodeCreator;
import org.drools.core.process.core.datatype.impl.type.ObjectDataType;
import org.kie.api.runtime.process.ProcessContext;
import org.kie.api.runtime.process.ProcessInstance;
import java.util.Queue;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.node.StartNode;
import org.junit.Test;
import org.jbpm.process.test.TestWorkItemHandler;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.workflow.core.node.WorkItemNode;

public class CompensationTest extends AbstractBaseTest {
    public void addLogger() {
        logger = LoggerFactory.getLogger(CompensationTest.this.getClass());
    }

    private KieSession ksession;

    @After
    public void cleanUp() {
        if ((ksession) != null) {
            ksession.dispose();
            ksession = null;
        } 
    }

    /* General HELPER methods */
    private void addCompensationScope(final Node node, final NodeContainer parentContainer, final String compensationHandlerId) {
        ContextContainer contextContainer = ((ContextContainer) (parentContainer));
        CompensationScope scope = null;
        boolean addScope = false;
        if ((contextContainer.getContexts(CompensationScope.COMPENSATION_SCOPE)) == null) {
            addScope = true;
        } else {
            scope = ((CompensationScope) (contextContainer.getContexts(CompensationScope.COMPENSATION_SCOPE).get(0)));
            if (scope == null) {
                addScope = true;
            } 
        }
        if (addScope) {
            scope = new CompensationScope();
            contextContainer.addContext(scope);
            contextContainer.setDefaultContext(scope);
            scope.setContextContainer(contextContainer);
        } 
        CompensationHandler handler = new CompensationHandler();
        handler.setNode(node);
        scope.setExceptionHandler(compensationHandlerId, handler);
        node.setMetaData("isForCompensation", Boolean.TRUE);
    }

    private Node findNode(RuleFlowProcess process, String nodeName) {
        Node found = null;
        Queue<org.kie.api.definition.process.Node> nodes = new LinkedList<org.kie.api.definition.process.Node>();
        nodes.addAll(Arrays.asList(process.getNodes()));
        while (!(nodes.isEmpty())) {
            org.kie.api.definition.process.Node node = nodes.poll();
            if (node.getName().equals(nodeName)) {
                found = ((Node) (node));
            } 
            if (node instanceof NodeContainer) {
                nodes.addAll(Arrays.asList(((NodeContainer) (node)).getNodes()));
            } 
        }
        Assert.assertNotNull((("Could not find node (" + nodeName) + ")."), found);
        return found;
    }

    /* TESTS */
    @Test
    public void testCompensationBoundaryEventSpecific() throws Exception {
        String processId = "org.jbpm.process.compensation.boundary";
        String[] workItemNames = new String[]{ "Don-Quixote" , "Sancho" , "Ricote" };
        List<String> eventList = new ArrayList<String>();
        RuleFlowProcess process = createCompensationBoundaryEventProcess(processId, workItemNames, eventList);
        // run process
        ksession = createKieSession(process);
        Node compensatedNode = findNode(process, "work1");
        String compensationEvent = ((String) (compensatedNode.getMetaData().get("UniqueId")));
        CompensationTest.runCompensationBoundaryEventSpecificTest(ksession, process, processId, workItemNames, eventList, compensationEvent);
    }

    public static void runCompensationBoundaryEventSpecificTest(KieSession ksession, RuleFlowProcess process, String processId, String[] workItemNames, List<String> eventList, String compensationEvent) {
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        for (String workItem : workItemNames) {
            ksession.getWorkItemManager().registerWorkItemHandler(workItem, workItemHandler);
        }
        ProcessInstance processInstance = ksession.startProcess(processId);
        // call compensation on the uncompleted work 1 (which should not fire)
        ksession.signalEvent("Compensation", compensationEvent, processInstance.getId());
        Assert.assertEquals("Compensation should not have fired yet.", 0, eventList.size());
        // complete work 1
        ksession.getWorkItemManager().completeWorkItem(workItemHandler.getWorkItems().removeLast().getId(), null);
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        // call compensation on work 1, which should now fire
        ksession.signalEvent("Compensation", compensationEvent, processInstance.getId());
        Assert.assertEquals("Compensation should have fired.", 1, eventList.size());
        // complete work 2 & 3
        ksession.getWorkItemManager().completeWorkItem(workItemHandler.getWorkItems().removeLast().getId(), null);
        ksession.getWorkItemManager().completeWorkItem(workItemHandler.getWorkItems().removeLast().getId(), null);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    @Test
    public void testCompensationBoundaryEventGeneral() throws Exception {
        String processId = "org.jbpm.process.compensation.boundary";
        String[] workItemNames = new String[]{ "Don-Quixote" , "Sancho" , "Ricote" };
        List<String> eventList = new ArrayList<String>();
        RuleFlowProcess process = createCompensationBoundaryEventProcess(processId, workItemNames, eventList);
        // run process
        ksession = createKieSession(process);
        String compensationEvent = (CompensationScope.IMPLICIT_COMPENSATION_PREFIX) + processId;
        CompensationTest.runCompensationBoundaryEventGeneralTest(ksession, process, processId, workItemNames, eventList, compensationEvent);
    }

    public static void runCompensationBoundaryEventGeneralTest(KieSession ksession, RuleFlowProcess process, String processId, String[] workItemNames, List<String> eventList, String compensationEvent) {
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        for (String workItem : workItemNames) {
            ksession.getWorkItemManager().registerWorkItemHandler(workItem, workItemHandler);
        }
        ProcessInstance processInstance = ksession.startProcess(processId);
        // general compensation should not cause anything to happen
        ksession.signalEvent("Compensation", compensationEvent, processInstance.getId());
        Assert.assertEquals("Compensation should not have fired yet.", 0, eventList.size());
        // complete work 1 & 2
        ksession.getWorkItemManager().completeWorkItem(workItemHandler.getWorkItems().removeLast().getId(), null);
        ksession.getWorkItemManager().completeWorkItem(workItemHandler.getWorkItems().removeLast().getId(), null);
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        Assert.assertEquals("Compensation should not have fired yet.", 0, eventList.size());
        // general compensation should now cause the compensation handlers to fire in reverse order
        ksession.signalEvent("Compensation", compensationEvent, processInstance.getId());
        Assert.assertEquals("Compensation should have fired.", 2, eventList.size());
        // complete work 3 and finish
        ksession.getWorkItemManager().completeWorkItem(workItemHandler.getWorkItems().removeLast().getId(), null);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    private RuleFlowProcess createCompensationBoundaryEventProcess(String processId, String[] workItemNames, final List<String> eventList) throws Exception {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setAutoComplete(true);
        process.setId(processId);
        process.setName("CESP Process");
        process.setMetaData("Compensation", true);
        List<Variable> variables = new ArrayList<Variable>();
        Variable variable = new Variable();
        variable.setName("event");
        ObjectDataType personDataType = new ObjectDataType();
        personDataType.setClassName("java.lang.String");
        variable.setType(personDataType);
        variables.add(variable);
        process.getVariableScope().setVariables(variables);
        NodeCreator<StartNode> startNodeCreator = new NodeCreator<StartNode>(process, StartNode.class);
        NodeCreator<EndNode> endNodeCreator = new NodeCreator<EndNode>(process, EndNode.class);
        NodeCreator<WorkItemNode> workItemNodeCreator = new NodeCreator<WorkItemNode>(process, WorkItemNode.class);
        NodeCreator<BoundaryEventNode> boundaryNodeCreator = new NodeCreator<BoundaryEventNode>(process, BoundaryEventNode.class);
        NodeCreator<ActionNode> actionNodeCreator = new NodeCreator<ActionNode>(process, ActionNode.class);
        // Create process
        StartNode startNode = startNodeCreator.createNode("start");
        Node lastNode = startNode;
        WorkItemNode[] workItemNodes = new WorkItemNode[3];
        for (int i = 0; i < 3; ++i) {
            workItemNodes[i] = workItemNodeCreator.createNode(("work" + (i + 1)));
            workItemNodes[i].getWork().setName(workItemNames[i]);
            NodeCreator.connect(lastNode, workItemNodes[i]);
            lastNode = workItemNodes[i];
        }
        EndNode endNode = endNodeCreator.createNode("end");
        NodeCreator.connect(workItemNodes[2], endNode);
        // Compensation (boundary event) handlers
        for (int i = 0; i < 3; ++i) {
            createBoundaryEventCompensationHandler(process, workItemNodes[i], eventList, (("" + i) + 1));
        }
        return process;
    }

    @Test
    public void testCompensationEventSubProcessSpecific() throws Exception {
        String processId = "org.jbpm.process.compensation.event.subprocess";
        String[] workItemNames = new String[]{ "kwik" , "kwek" , "kwak" };
        List<String> eventList = new ArrayList<String>();
        RuleFlowProcess process = createCompensationEventSubProcessProcess(processId, workItemNames, eventList);
        Node toCompensateNode = findNode(process, "sub0");
        String compensationEvent = ((String) (toCompensateNode.getMetaData().get("UniqueId")));
        // run process
        ksession = createKieSession(process);
        CompensationTest.runCompensationEventSubProcessSpecificTest(ksession, process, processId, workItemNames, eventList, compensationEvent);
    }

    public static void runCompensationEventSubProcessSpecificTest(KieSession ksession, RuleFlowProcess process, String processId, String[] workItemNames, List<String> eventList, String compensationEvent) {
        // run process
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        for (String workItem : workItemNames) {
            ksession.getWorkItemManager().registerWorkItemHandler(workItem, workItemHandler);
        }
        ProcessInstance processInstance = ksession.startProcess(processId);
        // call compensation on the uncompleted work 1 (which should not fire)
        ksession.signalEvent("Compensation", compensationEvent, processInstance.getId());
        Assert.assertEquals("Compensation should not have fired yet.", 0, eventList.size());
        // pre work item
        ksession.getWorkItemManager().completeWorkItem(workItemHandler.getWorkItems().removeLast().getId(), null);
        // sub-process is active, but not complete
        ksession.signalEvent("Compensation", compensationEvent, processInstance.getId());
        Assert.assertEquals("Compensation should not have fired yet.", 0, eventList.size());
        // sub process work item
        ksession.getWorkItemManager().completeWorkItem(workItemHandler.getWorkItems().removeLast().getId(), null);
        // sub-process has completed
        ksession.signalEvent("Compensation", compensationEvent, processInstance.getId());
        Assert.assertEquals("Compensation should have fired once.", 1, eventList.size());
        // post work item
        ksession.getWorkItemManager().completeWorkItem(workItemHandler.getWorkItems().removeLast().getId(), null);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    @Test
    public void testCompensationEventSubProcessGeneral() throws Exception {
        String processId = "org.jbpm.process.compensation.event.subprocess.general";
        String[] workItemNames = new String[]{ "kwik" , "kwek" , "kwak" };
        List<String> eventList = new ArrayList<String>();
        RuleFlowProcess process = createCompensationEventSubProcessProcess(processId, workItemNames, eventList);
        String compensationEvent = (CompensationScope.IMPLICIT_COMPENSATION_PREFIX) + (process.getId());
        // run process
        ksession = createKieSession(process);
        CompensationTest.runCompensationEventSubProcessGeneralTest(ksession, process, processId, workItemNames, eventList, compensationEvent);
    }

    public static void runCompensationEventSubProcessGeneralTest(KieSession ksession, RuleFlowProcess process, String processId, String[] workItemNames, List<String> eventList, String compensationEvent) {
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        for (String workItem : workItemNames) {
            ksession.getWorkItemManager().registerWorkItemHandler(workItem, workItemHandler);
        }
        ProcessInstance processInstance = ksession.startProcess(processId);
        // pre and sub process work item
        ksession.getWorkItemManager().completeWorkItem(workItemHandler.getWorkItems().removeLast().getId(), null);
        ksession.getWorkItemManager().completeWorkItem(workItemHandler.getWorkItems().removeLast().getId(), null);
        // Call general compensation
        ksession.signalEvent("Compensation", compensationEvent, processInstance.getId());
        Assert.assertEquals("Compensation should have fired once.", 1, eventList.size());
        // post work item
        ksession.getWorkItemManager().completeWorkItem(workItemHandler.getWorkItems().removeLast().getId(), null);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    private RuleFlowProcess createCompensationEventSubProcessProcess(String processId, String[] workItemNames, final List<String> eventList) throws Exception {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setAutoComplete(true);
        process.setId(processId);
        process.setName("CESP Process");
        process.setMetaData("Compensation", true);
        NodeCreator<StartNode> startNodeCreator = new NodeCreator<StartNode>(process, StartNode.class);
        NodeCreator<WorkItemNode> workItemNodeCreator = new NodeCreator<WorkItemNode>(process, WorkItemNode.class);
        NodeCreator<CompositeContextNode> compNodeCreator = new NodeCreator<CompositeContextNode>(process, CompositeContextNode.class);
        NodeCreator<EndNode> endNodeCreator = new NodeCreator<EndNode>(process, EndNode.class);
        // outer process
        StartNode startNode = startNodeCreator.createNode("start0");
        WorkItemNode workItemNode = workItemNodeCreator.createNode("work0-pre");
        workItemNode.getWork().setName(workItemNames[0]);
        NodeCreator.connect(startNode, workItemNode);
        CompositeNode compositeNode = compNodeCreator.createNode("sub0");
        NodeCreator.connect(workItemNode, compositeNode);
        workItemNode = workItemNodeCreator.createNode("work0-post");
        workItemNode.getWork().setName(workItemNames[2]);
        NodeCreator.connect(compositeNode, workItemNode);
        EndNode endNode = endNodeCreator.createNode("end0");
        NodeCreator.connect(workItemNode, endNode);
        // 1rst level nested subprocess
        startNodeCreator.setNodeContainer(compositeNode);
        workItemNodeCreator.setNodeContainer(compositeNode);
        endNodeCreator.setNodeContainer(compositeNode);
        startNode = startNodeCreator.createNode("start1");
        workItemNode = workItemNodeCreator.createNode("work1");
        workItemNode.getWork().setName(workItemNames[1]);
        NodeCreator.connect(startNode, workItemNode);
        endNode = endNodeCreator.createNode("end1");
        NodeCreator.connect(workItemNode, endNode);
        // 2nd level nested event subprocess in 1rst level subprocess
        NodeCreator<EventSubProcessNode> espNodeCreator = new NodeCreator<EventSubProcessNode>(compositeNode, EventSubProcessNode.class);
        EventSubProcessNode espNode = espNodeCreator.createNode("eventSub1");
        EventTypeFilter eventFilter = new org.jbpm.process.core.event.NonAcceptingEventTypeFilter();
        eventFilter.setType("Compensation");
        espNode.addEvent(eventFilter);
        addCompensationScope(espNode, process, ((String) (compositeNode.getMetaData("UniqueId"))));
        startNodeCreator.setNodeContainer(espNode);
        endNodeCreator.setNodeContainer(espNode);
        NodeCreator<ActionNode> actionNodeCreator = new NodeCreator<ActionNode>(espNode, ActionNode.class);
        startNode = startNodeCreator.createNode("start1*");
        ActionNode actionNode = actionNodeCreator.createNode("action1*");
        actionNode.setName("Execute");
        DroolsAction action = new org.jbpm.workflow.core.impl.DroolsConsequenceAction("java", null);
        action.setMetaData("Action", new Action() {
            public void execute(ProcessContext context) throws Exception {
                eventList.add("Executed action");
            }
        });
        actionNode.setAction(action);
        NodeCreator.connect(startNode, actionNode);
        endNode = endNodeCreator.createNode("end1*");
        NodeCreator.connect(actionNode, endNode);
        return process;
    }

    @Test
    public void testNestedCompensationEventSubProcessSpecific() throws Exception {
        String processId = "org.jbpm.process.compensation.event.nested.subprocess";
        String[] workItemNames = new String[]{ "kwik" , "kwek" , "kwak" };
        List<String> eventList = new ArrayList<String>();
        RuleFlowProcess process = createNestedCompensationEventSubProcessProcess(processId, workItemNames, eventList);
        Node toCompensateNode = findNode(process, "sub1");
        String compensationEvent = ((String) (toCompensateNode.getMetaData().get("UniqueId")));
        ksession = createKieSession(process);
        CompensationTest.runCompensationEventSubProcessSpecificTest(ksession, process, processId, workItemNames, eventList, compensationEvent);
    }

    @Test
    public void testNestedCompensationEventSubProcessGeneral() throws Exception {
        String processId = "org.jbpm.process.compensation.event.subprocess.general";
        String[] workItemNames = new String[]{ "apple" , "banana" , "orange" };
        List<String> eventList = new ArrayList<String>();
        RuleFlowProcess process = createNestedCompensationEventSubProcessProcess(processId, workItemNames, eventList);
        Node toCompensateNode = findNode(process, "sub0");
        String compensationEvent = (CompensationScope.IMPLICIT_COMPENSATION_PREFIX) + (toCompensateNode.getMetaData().get("UniqueId"));
        ksession = createKieSession(process);
        CompensationTest.runCompensationEventSubProcessGeneralTest(ksession, process, processId, workItemNames, eventList, compensationEvent);
    }

    private RuleFlowProcess createNestedCompensationEventSubProcessProcess(String processId, String[] workItemNames, final List<String> eventList) throws Exception {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setAutoComplete(true);
        process.setId(processId);
        process.setName("CESP Process");
        process.setMetaData("Compensation", true);
        NodeCreator<StartNode> startNodeCreator = new NodeCreator<StartNode>(process, StartNode.class);
        NodeCreator<WorkItemNode> workItemNodeCreator = new NodeCreator<WorkItemNode>(process, WorkItemNode.class);
        NodeCreator<CompositeContextNode> compNodeCreator = new NodeCreator<CompositeContextNode>(process, CompositeContextNode.class);
        NodeCreator<EndNode> endNodeCreator = new NodeCreator<EndNode>(process, EndNode.class);
        // outer process
        CompositeContextNode compositeNode = compNodeCreator.createNode("sub0");
        {
            StartNode startNode = startNodeCreator.createNode("start0");
            WorkItemNode workItemNode = workItemNodeCreator.createNode("work0-pre");
            workItemNode.getWork().setName(workItemNames[0]);
            NodeCreator.connect(startNode, workItemNode);
            NodeCreator.connect(workItemNode, compositeNode);
            EndNode endNode = endNodeCreator.createNode("end0");
            NodeCreator.connect(compositeNode, endNode);
        }
        // 1rst level nested subprocess (contains compensation visibility scope)
        CompositeContextNode compensationScopeContainerNode = compositeNode;
        {
            startNodeCreator.setNodeContainer(compositeNode);
            workItemNodeCreator.setNodeContainer(compositeNode);
            compNodeCreator.setNodeContainer(compositeNode);
            endNodeCreator.setNodeContainer(compositeNode);
            StartNode startNode = startNodeCreator.createNode("start1");
            CompositeContextNode subCompNode = compNodeCreator.createNode("sub1");
            NodeCreator.connect(startNode, subCompNode);
            WorkItemNode workItemNode = workItemNodeCreator.createNode("work1-post");
            workItemNode.getWork().setName(workItemNames[2]);
            NodeCreator.connect(subCompNode, workItemNode);
            EndNode endNode = endNodeCreator.createNode("end1");
            NodeCreator.connect(workItemNode, endNode);
            compositeNode = subCompNode;
        }
        // 2nd level nested subprocess
        {
            startNodeCreator.setNodeContainer(compositeNode);
            workItemNodeCreator.setNodeContainer(compositeNode);
            endNodeCreator.setNodeContainer(compositeNode);
            StartNode startNode = startNodeCreator.createNode("start2");
            WorkItemNode workItemNode = workItemNodeCreator.createNode("work2");
            workItemNode.getWork().setName(workItemNames[1]);
            NodeCreator.connect(startNode, workItemNode);
            EndNode endNode = endNodeCreator.createNode("end2");
            NodeCreator.connect(workItemNode, endNode);
        }
        // 3nd level nested event subprocess in 2nd level subprocess
        {
            NodeCreator<EventSubProcessNode> espNodeCreator = new NodeCreator<EventSubProcessNode>(compositeNode, EventSubProcessNode.class);
            EventSubProcessNode espNode = espNodeCreator.createNode("eventSub2");
            startNodeCreator.setNodeContainer(espNode);
            endNodeCreator.setNodeContainer(espNode);
            NodeCreator<ActionNode> actionNodeCreator = new NodeCreator<ActionNode>(espNode, ActionNode.class);
            EventTypeFilter eventFilter = new org.jbpm.process.core.event.NonAcceptingEventTypeFilter();
            eventFilter.setType("Compensation");
            espNode.addEvent(eventFilter);
            addCompensationScope(espNode, compensationScopeContainerNode, ((String) (compositeNode.getMetaData("UniqueId"))));
            StartNode startNode = startNodeCreator.createNode("start3*");
            ActionNode actionNode = actionNodeCreator.createNode("action3*");
            actionNode.setName("Execute");
            DroolsAction action = new org.jbpm.workflow.core.impl.DroolsConsequenceAction("java", null);
            action.setMetaData("Action", new Action() {
                public void execute(ProcessContext context) throws Exception {
                    eventList.add("Executed action");
                }
            });
            actionNode.setAction(action);
            NodeCreator.connect(startNode, actionNode);
            EndNode endNode = endNodeCreator.createNode("end3*");
            NodeCreator.connect(actionNode, endNode);
        }
        return process;
    }

    @Test
    public void testNestedCompensationBoundaryEventSpecific() throws Exception {
        String processId = "org.jbpm.process.compensation.boundary.nested";
        String[] workItemNames = new String[]{ "Don-Quixote" , "Sancho" , "Ricote" };
        List<String> eventList = new ArrayList<String>();
        RuleFlowProcess process = createNestedCompensationBoundaryEventProcess(processId, workItemNames, eventList);
        // run process
        ksession = createKieSession(process);
        Node compensatedNode = findNode(process, "work-comp-1");
        String compensationEvent = ((String) (compensatedNode.getMetaData().get("UniqueId")));
        CompensationTest.runCompensationBoundaryEventSpecificTest(ksession, process, processId, workItemNames, eventList, compensationEvent);
    }

    @Test
    public void testNestedCompensationBoundaryEventGeneral() throws Exception {
        String processId = "org.jbpm.process.compensation.boundary.general.nested";
        String[] workItemNames = new String[]{ "Jip" , "Janneke" , "Takkie" };
        List<String> eventList = new ArrayList<String>();
        RuleFlowProcess process = createNestedCompensationBoundaryEventProcess(processId, workItemNames, eventList);
        // run process
        ksession = createKieSession(process);
        Node toCompensateNode = findNode(process, "sub2");
        String compensationEvent = (CompensationScope.IMPLICIT_COMPENSATION_PREFIX) + ((String) (toCompensateNode.getMetaData().get("UniqueId")));
        CompensationTest.runCompensationBoundaryEventGeneralTest(ksession, process, processId, workItemNames, eventList, compensationEvent);
    }

    private RuleFlowProcess createNestedCompensationBoundaryEventProcess(String processId, String[] workItemNames, final List<String> eventList) throws Exception {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setAutoComplete(true);
        process.setId(processId);
        process.setName("CESP Process");
        process.setMetaData("Compensation", true);
        List<Variable> variables = new ArrayList<Variable>();
        Variable variable = new Variable();
        variable.setName("event");
        ObjectDataType personDataType = new ObjectDataType();
        personDataType.setClassName("java.lang.String");
        variable.setType(personDataType);
        variables.add(variable);
        process.getVariableScope().setVariables(variables);
        NodeCreator<StartNode> startNodeCreator = new NodeCreator<StartNode>(process, StartNode.class);
        NodeCreator<EndNode> endNodeCreator = new NodeCreator<EndNode>(process, EndNode.class);
        NodeCreator<CompositeContextNode> compNodeCreator = new NodeCreator<CompositeContextNode>(process, CompositeContextNode.class);
        // process level
        CompositeContextNode compositeNode = compNodeCreator.createNode("sub0");
        {
            StartNode startNode = startNodeCreator.createNode("start0");
            NodeCreator.connect(startNode, compositeNode);
            EndNode endNode = endNodeCreator.createNode("end0");
            NodeCreator.connect(compositeNode, endNode);
        }
        // 1rst level nested subprocess (contains compensation visibility scope)
        {
            startNodeCreator.setNodeContainer(compositeNode);
            compNodeCreator.setNodeContainer(compositeNode);
            endNodeCreator.setNodeContainer(compositeNode);
            StartNode startNode = startNodeCreator.createNode("start1");
            CompositeContextNode subCompNode = compNodeCreator.createNode("sub1");
            NodeCreator.connect(startNode, subCompNode);
            EndNode endNode = endNodeCreator.createNode("end1");
            NodeCreator.connect(subCompNode, endNode);
            compositeNode = subCompNode;
        }
        // 2nd level nested subprocess (contains compensation visibility scope)
        NodeCreator<WorkItemNode> workItemNodeCreator = new NodeCreator<WorkItemNode>(compositeNode, WorkItemNode.class);
        {
            startNodeCreator.setNodeContainer(compositeNode);
            compNodeCreator.setNodeContainer(compositeNode);
            endNodeCreator.setNodeContainer(compositeNode);
            StartNode startNode = startNodeCreator.createNode("start2");
            CompositeContextNode subCompNode = compNodeCreator.createNode("sub2");
            NodeCreator.connect(startNode, subCompNode);
            WorkItemNode workItemNode = workItemNodeCreator.createNode("work2");
            workItemNode.getWork().setName(workItemNames[2]);
            NodeCreator.connect(subCompNode, workItemNode);
            EndNode endNode = endNodeCreator.createNode("end2");
            NodeCreator.connect(workItemNode, endNode);
            createBoundaryEventCompensationHandler(compositeNode, workItemNode, eventList, "2");
            compositeNode = subCompNode;
        }
        // Fill 3rd level with process with compensation
        {
            startNodeCreator.setNodeContainer(compositeNode);
            workItemNodeCreator.setNodeContainer(compositeNode);
            endNodeCreator.setNodeContainer(compositeNode);
            StartNode startNode = startNodeCreator.createNode("start");
            Node lastNode = startNode;
            WorkItemNode[] workItemNodes = new WorkItemNode[3];
            for (int i = 0; i < 2; ++i) {
                workItemNodes[i] = workItemNodeCreator.createNode(("work-comp-" + (i + 1)));
                workItemNodes[i].getWork().setName(workItemNames[i]);
                NodeCreator.connect(lastNode, workItemNodes[i]);
                lastNode = workItemNodes[i];
            }
            EndNode endNode = endNodeCreator.createNode("end");
            NodeCreator.connect(workItemNodes[1], endNode);
            // Compensation (boundary event) handlers
            for (int i = 0; i < 2; ++i) {
                createBoundaryEventCompensationHandler(compositeNode, workItemNodes[i], eventList, (("" + i) + 1));
            }
        }
        return process;
    }

    private void createBoundaryEventCompensationHandler(NodeContainer nodeContainer, Node attachedToNode, final List<String> eventList, final String id) throws Exception {
        NodeCreator<BoundaryEventNode> boundaryNodeCreator = new NodeCreator<BoundaryEventNode>(nodeContainer, BoundaryEventNode.class);
        BoundaryEventNode boundaryNode = boundaryNodeCreator.createNode(("boundary" + id));
        String attachedTo = ((String) (attachedToNode.getMetaData().get("UniqueId")));
        boundaryNode.setMetaData("AttachedTo", attachedTo);
        boundaryNode.setAttachedToNodeId(attachedTo);
        EventTypeFilter eventFilter = new org.jbpm.process.core.event.NonAcceptingEventTypeFilter();
        eventFilter.setType("Compensation");
        List<EventFilter> eventFilters = new ArrayList<EventFilter>();
        boundaryNode.setEventFilters(eventFilters);
        eventFilters.add(eventFilter);
        addCompensationScope(boundaryNode, nodeContainer, attachedTo);
        NodeCreator<ActionNode> actionNodeCreator = new NodeCreator<ActionNode>(nodeContainer, ActionNode.class);
        ActionNode actionNode = actionNodeCreator.createNode(("handlerAction" + id));
        actionNode.setMetaData("isForCompensation", true);
        actionNode.setName("Execute");
        DroolsAction action = new org.jbpm.workflow.core.impl.DroolsConsequenceAction("java", null);
        action.setMetaData("Action", new Action() {
            public void execute(ProcessContext context) throws Exception {
                eventList.add(("action" + id));
            }
        });
        actionNode.setAction(action);
        NodeCreator.connect(boundaryNode, actionNode);
    }
}

