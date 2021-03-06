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
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.ForEachNode;
import java.util.HashMap;
import org.kie.api.runtime.KieSession;
import java.util.List;
import org.drools.core.process.core.datatype.impl.type.ListDataType;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.jbpm.workflow.core.Node;
import org.drools.core.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.process.test.Person;
import org.kie.api.runtime.process.ProcessContext;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.node.StartNode;
import org.junit.Test;
import org.jbpm.process.test.TestProcessEventListener;
import org.jbpm.process.core.context.variable.Variable;

public class ForEachTest extends AbstractBaseTest {
    public void addLogger() {
        logger = LoggerFactory.getLogger(ForEachTest.this.getClass());
    }

    private String[] eventOrder = new String[]{ "bvc-persons" , "avc-persons" , "bps" , "bnt-0" , "bnl-0" , "bnt-1" , "bvc-3:2:child" , "avc-3:2:child" , "bvc-3:2:child" , "avc-3:2:child" , "bvc-3:2:child" , "avc-3:2:child" , "bnt-1:5:9" , "bnl-1:5:9" , "anl-1:5:9" , "ant-1:5:9" , "bnt-1:6:13" , "bnl-1:6:13" , "anl-1:6:13" , "ant-1:6:13" , "bnt-1:7:16" , "bnl-1:7:16" , "bnl-1" , "bnt-18" , "bnl-18" , "bpc" , "apc" , "anl-18" , "ant-18" , "anl-1" , "anl-1:7:16" , "ant-1:7:16" , "ant-1" , "anl-0" , "ant-0" , "aps" };

    @Test
    public void testForEach() {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId("org.drools.core.process.foreach");
        process.setName("ForEach Process");
        List<Variable> variables = new ArrayList<Variable>();
        Variable variable = new Variable();
        variable.setName("persons");
        ListDataType listDataType = new ListDataType();
        ObjectDataType personDataType = new ObjectDataType();
        personDataType.setClassName("org.jbpm.process.test.Person");
        listDataType.setType(personDataType);
        variable.setType(listDataType);
        variables.add(variable);
        process.getVariableScope().setVariables(variables);
        StartNode startNode = new StartNode();
        startNode.setName("Start");
        startNode.setId(1);
        process.addNode(startNode);
        EndNode endNode = new EndNode();
        endNode.setName("EndNode");
        endNode.setId(2);
        process.addNode(endNode);
        ForEachNode forEachNode = new ForEachNode();
        forEachNode.setName("ForEach");
        forEachNode.setId(3);
        forEachNode.setCollectionExpression("persons");
        personDataType = new ObjectDataType();
        personDataType.setClassName("org.drools.Person");
        process.addNode(forEachNode);
        new org.jbpm.workflow.core.impl.ConnectionImpl(startNode, Node.CONNECTION_DEFAULT_TYPE, forEachNode, Node.CONNECTION_DEFAULT_TYPE);
        new org.jbpm.workflow.core.impl.ConnectionImpl(forEachNode, Node.CONNECTION_DEFAULT_TYPE, endNode, Node.CONNECTION_DEFAULT_TYPE);
        final List<String> myList = new ArrayList<String>();
        ActionNode actionNode = new ActionNode();
        actionNode.setName("Print child");
        DroolsAction action = new org.jbpm.workflow.core.impl.DroolsConsequenceAction("java", null);
        action.setMetaData("Action", new Action() {
            public void execute(ProcessContext context) throws Exception {
                logger.info("Executed action for child {}", ((Person) (context.getVariable("child"))).getName());
                myList.add("Executed action");
            }
        });
        actionNode.setAction(action);
        forEachNode.addNode(actionNode);
        forEachNode.linkIncomingConnections(Node.CONNECTION_DEFAULT_TYPE, actionNode.getId(), Node.CONNECTION_DEFAULT_TYPE);
        forEachNode.linkOutgoingConnections(actionNode.getId(), Node.CONNECTION_DEFAULT_TYPE, Node.CONNECTION_DEFAULT_TYPE);
        forEachNode.setVariable("child", personDataType);
        KieSession ksession = createKieSession(process);
        Map<String, Object> parameters = new HashMap<String, Object>();
        List<Person> persons = new ArrayList<Person>();
        persons.add(new Person("John Doe"));
        persons.add(new Person("Jane Doe"));
        persons.add(new Person("Jack"));
        parameters.put("persons", persons);
        TestProcessEventListener procEventListener = new TestProcessEventListener();
        ksession.addEventListener(procEventListener);
        ksession.startProcess("org.drools.core.process.foreach", parameters);
        Assert.assertEquals(3, myList.size());
        verifyEventHistory(eventOrder, procEventListener.getEventHistory());
    }
}

