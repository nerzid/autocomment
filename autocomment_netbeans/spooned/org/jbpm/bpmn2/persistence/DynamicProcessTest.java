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


package org.jbpm.bpmn2.persistence;

import org.junit.BeforeClass;
import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.kie.api.definition.process.Connection;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.kie.internal.command.Context;
import org.drools.core.command.impl.GenericCommand;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.bpmn2.JbpmBpmn2TestCase;
import org.kie.api.KieBase;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.io.Resource;
import org.kie.internal.io.ResourceFactory;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.junit.Test;
import org.jbpm.persistence.session.objects.TestWorkItemHandler;

/**
 * This is a sample file to launch a process.
 */
public class DynamicProcessTest extends JbpmBpmn2TestCase {
    private static final Logger logger = LoggerFactory.getLogger(DynamicProcessTest.class);

    @BeforeClass
    public static void setup() throws Exception {
        if (PERSISTENCE) {
            setUpDataSource();
        } 
    }

    @Test
    public void testDynamicProcess() throws Exception {
        RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("org.jbpm.HelloWorld");
        // Connections
        // Nodes
        // Header
        factory.name("HelloWorldProcess").version("1.0").packageName("org.jbpm").startNode(1).name("Start").done().humanTaskNode(2).name("Task1").actorId("krisv").taskName("MyTask").done().endNode(3).name("End").done().connection(1, 2).connection(2, 3);
        final RuleFlowProcess process = factory.validate().getProcess();
        Resource resource = ResourceFactory.newByteArrayResource(XmlRuleFlowProcessDumper.INSTANCE.dump(process).getBytes());
        resource.setSourcePath("/tmp/dynamicProcess.bpmn2");// source path or target path must be set to be added into kbase
        
        KieBase kbase = createKnowledgeBaseFromResources(resource);
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler testHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", testHandler);
        ksession.addEventListener(new ProcessEventListener() {
            public void beforeVariableChanged(ProcessVariableChangedEvent arg0) {
            }

            public void beforeProcessStarted(ProcessStartedEvent arg0) {
                DynamicProcessTest.logger.info("{}", arg0);
            }

            public void beforeProcessCompleted(ProcessCompletedEvent arg0) {
                DynamicProcessTest.logger.info("{}", arg0);
            }

            public void beforeNodeTriggered(ProcessNodeTriggeredEvent arg0) {
                DynamicProcessTest.logger.info("{}", arg0);
            }

            public void beforeNodeLeft(ProcessNodeLeftEvent arg0) {
                DynamicProcessTest.logger.info("{}", arg0);
            }

            public void afterVariableChanged(ProcessVariableChangedEvent arg0) {
            }

            public void afterProcessStarted(ProcessStartedEvent arg0) {
            }

            public void afterProcessCompleted(ProcessCompletedEvent arg0) {
            }

            public void afterNodeTriggered(ProcessNodeTriggeredEvent arg0) {
            }

            public void afterNodeLeft(ProcessNodeLeftEvent arg0) {
            }
        });
        final ProcessInstanceImpl processInstance = ((ProcessInstanceImpl) (ksession.startProcess("org.jbpm.HelloWorld")));
        HumanTaskNode node = new HumanTaskNode();
        node.setName("Task2");
        node.setId(4);
        DynamicProcessTest.insertNodeInBetween(process, 2, 3, node);
        ((CommandBasedStatefulKnowledgeSession) (ksession)).getCommandService().execute(new GenericCommand<Void>() {
            public Void execute(Context context) {
                StatefulKnowledgeSession ksession = ((StatefulKnowledgeSession) (((KnowledgeCommandContext) (context)).getKieSession()));
                ((ProcessInstanceImpl) (ksession.getProcessInstance(processInstance.getId()))).updateProcess(process);
                return null;
            }
        });
        assertProcessInstanceActive(processInstance);
        ksession.getWorkItemManager().completeWorkItem(testHandler.getWorkItem().getId(), null);
        assertProcessInstanceActive(processInstance);
        ksession.getWorkItemManager().completeWorkItem(testHandler.getWorkItem().getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        ksession.dispose();
    }

    private static void insertNodeInBetween(RuleFlowProcess process, long startNodeId, long endNodeId, NodeImpl node) {
        if (process == null) {
            throw new IllegalArgumentException("Process may not be null");
        } 
        NodeImpl selectedNode = ((NodeImpl) (process.getNode(startNodeId)));
        if (selectedNode == null) {
            throw new IllegalArgumentException(((("Node " + startNodeId) + " not found in process ") + (process.getId())));
        } 
        for (Connection connection : selectedNode.getDefaultOutgoingConnections()) {
            if ((connection.getTo().getId()) == endNodeId) {
                process.addNode(node);
                NodeImpl endNode = ((NodeImpl) (connection.getTo()));
                ((ConnectionImpl) (connection)).terminate();
                new ConnectionImpl(selectedNode, NodeImpl.CONNECTION_DEFAULT_TYPE, node, NodeImpl.CONNECTION_DEFAULT_TYPE);
                new ConnectionImpl(node, NodeImpl.CONNECTION_DEFAULT_TYPE, endNode, NodeImpl.CONNECTION_DEFAULT_TYPE);
                return ;
            } 
        }
        throw new IllegalArgumentException(((("Connection to node " + endNodeId) + " not found in process ") + (process.getId())));
    }
}

