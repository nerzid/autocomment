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


package org.jbpm.process;

import org.jbpm.test.util.AbstractBaseTest;
import java.util.ArrayList;
import org.junit.Assert;
import org.jbpm.workflow.core.node.EndNode;
import java.util.HashMap;
import java.util.HashSet;
import org.kie.api.runtime.KieSession;
import java.util.List;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.jbpm.workflow.core.Node;
import org.drools.core.process.core.ParameterDefinition;
import org.jbpm.process.test.Person;
import org.kie.api.runtime.process.ProcessInstance;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import java.util.Set;
import org.jbpm.workflow.core.node.StartNode;
import org.junit.Test;
import org.jbpm.process.core.context.variable.Variable;
import org.drools.core.process.core.Work;
import org.drools.core.WorkItemHandlerNotFoundException;
import org.jbpm.workflow.core.node.WorkItemNode;

public class WorkItemTest extends AbstractBaseTest {
    public void addLogger() {
        logger = LoggerFactory.getLogger(WorkItemTest.this.getClass());
    }

    @Test
    public void testReachNonRegisteredWorkItemHandler() {
        String processId = "org.drools.actions";
        String workName = "Unnexistent Task";
        RuleFlowProcess process = getWorkItemProcess(processId, workName);
        KieSession ksession = createKieSession(process);
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("UserName", "John Doe");
        parameters.put("Person", new Person("John Doe"));
        ProcessInstance processInstance = null;
        try {
            processInstance = ksession.startProcess("org.drools.actions", parameters);
            Assert.fail((("should fail if WorkItemHandler for" + workName) + "is not registered"));
        } catch (Throwable e) {
        }
        Assert.assertNull(processInstance);
    }

    @Test
    public void testCancelNonRegisteredWorkItemHandler() {
        String processId = "org.drools.actions";
        String workName = "Unnexistent Task";
        RuleFlowProcess process = getWorkItemProcess(processId, workName);
        KieSession ksession = createKieSession(process);
        ksession.getWorkItemManager().registerWorkItemHandler(workName, new org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler());
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("UserName", "John Doe");
        parameters.put("Person", new Person("John Doe"));
        ProcessInstance processInstance = ksession.startProcess("org.drools.actions", parameters);
        long processInstanceId = processInstance.getId();
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        ksession.getWorkItemManager().registerWorkItemHandler(workName, null);
        try {
            ksession.abortProcessInstance(processInstanceId);
            Assert.fail((("should fail if WorkItemHandler for" + workName) + "is not registered"));
        } catch (WorkItemHandlerNotFoundException wihnfe) {
        }
        Assert.assertEquals(ProcessInstance.STATE_ABORTED, processInstance.getState());
    }

    private RuleFlowProcess getWorkItemProcess(String processId, String workName) {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId(processId);
        List<Variable> variables = new ArrayList<Variable>();
        Variable variable = new Variable();
        variable.setName("UserName");
        variable.setType(new org.drools.core.process.core.datatype.impl.type.StringDataType());
        variables.add(variable);
        variable = new Variable();
        variable.setName("Person");
        variable.setType(new org.drools.core.process.core.datatype.impl.type.ObjectDataType(Person.class.getName()));
        variables.add(variable);
        variable = new Variable();
        variable.setName("MyObject");
        variable.setType(new org.drools.core.process.core.datatype.impl.type.ObjectDataType());
        variables.add(variable);
        variable = new Variable();
        variable.setName("Number");
        variable.setType(new org.drools.core.process.core.datatype.impl.type.IntegerDataType());
        variables.add(variable);
        process.getVariableScope().setVariables(variables);
        StartNode startNode = new StartNode();
        startNode.setName("Start");
        startNode.setId(1);
        WorkItemNode workItemNode = new WorkItemNode();
        workItemNode.setName("workItemNode");
        workItemNode.setId(2);
        workItemNode.addInMapping("Comment", "Person.name");
        workItemNode.addInMapping("Attachment", "MyObject");
        workItemNode.addOutMapping("Result", "MyObject");
        workItemNode.addOutMapping("Result.length()", "Number");
        Work work = new org.drools.core.process.core.impl.WorkImpl();
        work.setName(workName);
        Set<ParameterDefinition> parameterDefinitions = new HashSet<ParameterDefinition>();
        ParameterDefinition parameterDefinition = new org.drools.core.process.core.impl.ParameterDefinitionImpl("ActorId", new org.drools.core.process.core.datatype.impl.type.StringDataType());
        parameterDefinitions.add(parameterDefinition);
        parameterDefinition = new org.drools.core.process.core.impl.ParameterDefinitionImpl("Content", new org.drools.core.process.core.datatype.impl.type.StringDataType());
        parameterDefinitions.add(parameterDefinition);
        parameterDefinition = new org.drools.core.process.core.impl.ParameterDefinitionImpl("Comment", new org.drools.core.process.core.datatype.impl.type.StringDataType());
        parameterDefinitions.add(parameterDefinition);
        work.setParameterDefinitions(parameterDefinitions);
        work.setParameter("ActorId", "#{UserName}");
        work.setParameter("Content", "#{Person.name}");
        workItemNode.setWork(work);
        EndNode endNode = new EndNode();
        endNode.setName("End");
        endNode.setId(3);
        connect(startNode, workItemNode);
        connect(workItemNode, endNode);
        process.addNode(startNode);
        process.addNode(workItemNode);
        process.addNode(endNode);
        return process;
    }

    private void connect(Node sourceNode, Node targetNode) {
        new org.jbpm.workflow.core.impl.ConnectionImpl(sourceNode, Node.CONNECTION_DEFAULT_TYPE, targetNode, Node.CONNECTION_DEFAULT_TYPE);
    }
}

