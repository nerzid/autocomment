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


package org.jbpm.persistence.map.impl;

import java.util.ArrayList;
import org.jbpm.process.core.event.EventTypeFilter;
import java.util.List;
import org.jbpm.workflow.core.Node;
import org.drools.core.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.persistence.session.objects.Person;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.process.core.context.variable.Variable;
import org.drools.core.process.core.Work;

public class ProcessCreatorForHelp {
    public static RuleFlowProcess newSimpleEventProcess(String processId, String eventType) {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId(processId);
        StartNode startNode = new StartNode();
        startNode.setName("Start");
        startNode.setId(1);
        EventNode eventNode = new EventNode();
        eventNode.setName("EventNode");
        eventNode.setId(2);
        eventNode.setScope("external");
        EventTypeFilter eventFilter = new EventTypeFilter();
        eventFilter.setType(eventType);
        eventNode.addEventFilter(eventFilter);
        EndNode endNode = new EndNode();
        endNode.setName("End");
        endNode.setId(3);
        ProcessCreatorForHelp.connect(startNode, eventNode);
        ProcessCreatorForHelp.connect(eventNode, endNode);
        process.addNode(startNode);
        process.addNode(eventNode);
        process.addNode(endNode);
        return process;
    }

    public static RuleFlowProcess newProcessWithOneVariableAndOneWork(String processId, String variableName, String workName) {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId(processId);
        List<Variable> variables = new ArrayList<Variable>();
        Variable variable = new Variable();
        variable.setName(variableName);
        ObjectDataType extendingSerializableDataType = new ObjectDataType();
        extendingSerializableDataType.setClassName(Person.class.getName());
        variable.setType(extendingSerializableDataType);
        variables.add(variable);
        process.getVariableScope().setVariables(variables);
        StartNode startNode = new StartNode();
        startNode.setName("Start");
        startNode.setId(1);
        WorkItemNode workItemNode = new WorkItemNode();
        workItemNode.setName("workItemNode");
        workItemNode.setId(2);
        Work work = new org.drools.core.process.core.impl.WorkImpl();
        work.setName(workName);
        workItemNode.setWork(work);
        EndNode endNode = new EndNode();
        endNode.setName("EndNode");
        endNode.setId(4);
        ProcessCreatorForHelp.connect(startNode, workItemNode);
        ProcessCreatorForHelp.connect(workItemNode, endNode);
        process.addNode(startNode);
        process.addNode(workItemNode);
        process.addNode(endNode);
        return process;
    }

    public static RuleFlowProcess newProcessWithOneWork(String processId, String workName) {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId(processId);
        StartNode startNode = new StartNode();
        startNode.setName("Start");
        startNode.setId(1);
        WorkItemNode workItemNode = new WorkItemNode();
        workItemNode.setName("workItemNode");
        workItemNode.setId(2);
        Work work = new org.drools.core.process.core.impl.WorkImpl();
        work.setName(workName);
        workItemNode.setWork(work);
        EndNode endNode = new EndNode();
        endNode.setName("EndNode");
        endNode.setId(4);
        ProcessCreatorForHelp.connect(startNode, workItemNode);
        ProcessCreatorForHelp.connect(workItemNode, endNode);
        process.addNode(startNode);
        process.addNode(workItemNode);
        process.addNode(endNode);
        return process;
    }

    public static RuleFlowProcess newProcessWithOneSubProcess(String processId, String subProcessId) {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId(processId);
        StartNode startNode = new StartNode();
        startNode.setName("Start");
        startNode.setId(1);
        SubProcessNode subProcessNode = new SubProcessNode();
        subProcessNode.setId(2);
        subProcessNode.setProcessId(subProcessId);
        subProcessNode.setName("subProcess");
        EndNode endNode = new EndNode();
        endNode.setName("EndNode");
        endNode.setId(4);
        ProcessCreatorForHelp.connect(startNode, subProcessNode);
        ProcessCreatorForHelp.connect(subProcessNode, endNode);
        process.addNode(startNode);
        process.addNode(subProcessNode);
        process.addNode(endNode);
        return process;
    }

    public static RuleFlowProcess newShortestProcess(String processId) {
        StartNode startNode = new StartNode();
        startNode.setName("Start");
        startNode.setId(1);
        EndNode endNode = new EndNode();
        endNode.setName("EndNode");
        endNode.setId(2);
        ProcessCreatorForHelp.connect(startNode, endNode);
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId(processId);
        process.addNode(startNode);
        process.addNode(endNode);
        return process;
    }

    private static void connect(Node sourceNode, Node targetNode) {
        new org.jbpm.workflow.core.impl.ConnectionImpl(sourceNode, Node.CONNECTION_DEFAULT_TYPE, targetNode, Node.CONNECTION_DEFAULT_TYPE);
    }
}

