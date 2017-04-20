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


package org.jbpm.event.process;

import org.slf4j.LoggerFactory;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.test.util.AbstractBaseTest;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.jbpm.process.instance.impl.Action;
import java.util.ArrayList;
import org.junit.Assert;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.jbpm.workflow.core.DroolsAction;
import org.kie.api.event.process.ProcessEvent;
import org.jbpm.workflow.core.node.StartNode;
import org.kie.api.runtime.process.ProcessInstance;
import org.jbpm.workflow.core.node.EndNode;
import org.kie.api.event.process.ProcessStartedEvent;
import org.jbpm.process.core.event.EventFilter;
import org.jbpm.workflow.core.node.EventNode;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.jbpm.workflow.core.node.EventTrigger;
import org.kie.internal.KnowledgeBase;
import org.junit.Test;
import org.jbpm.workflow.core.Node;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.api.runtime.process.ProcessContext;
import org.jbpm.process.core.event.EventTypeFilter;
import java.util.List;

public class ProcessEventSupportTest extends AbstractBaseTest {
    public void addLogger() {
        logger = LoggerFactory.getLogger(this.getClass());
    }

    @Test
    public void testProcessEventListener() throws Exception {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        // create a simple package with one process to test the events
        final InternalKnowledgePackage pkg = new KnowledgePackageImpl("org.drools.test");
        RuleFlowProcess process = new RuleFlowProcess();
        // set id String{"org.drools.core.process.event"} to RuleFlowProcess{process}
        process.setId("org.drools.core.process.event");
        // set name String{"Event Process"} to RuleFlowProcess{process}
        process.setName("Event Process");
        StartNode startNode = new StartNode();
        // set name String{"Start"} to StartNode{startNode}
        startNode.setName("Start");
        // set id int{1} to StartNode{startNode}
        startNode.setId(1);
        // add node StartNode{startNode} to RuleFlowProcess{process}
        process.addNode(startNode);
        ActionNode actionNode = new ActionNode();
        // set name String{"Print"} to ActionNode{actionNode}
        actionNode.setName("Print");
        DroolsAction action = new DroolsConsequenceAction("java", null);
        // set meta String{"Action"} to DroolsAction{action}
        action.setMetaData("Action", new Action() {
            public void execute(ProcessContext context) throws Exception {
                // info String{"Executed action"} to 1{logger}
                logger.info("Executed action");
            }
        });
        // set action DroolsAction{action} to ActionNode{actionNode}
        actionNode.setAction(action);
        // set id int{2} to ActionNode{actionNode}
        actionNode.setId(2);
        // add node ActionNode{actionNode} to RuleFlowProcess{process}
        process.addNode(actionNode);
        new ConnectionImpl(startNode, Node.CONNECTION_DEFAULT_TYPE, actionNode, Node.CONNECTION_DEFAULT_TYPE);
        EndNode endNode = new EndNode();
        // set name String{"End"} to EndNode{endNode}
        endNode.setName("End");
        // set id int{3} to EndNode{endNode}
        endNode.setId(3);
        // add node EndNode{endNode} to RuleFlowProcess{process}
        process.addNode(endNode);
        new ConnectionImpl(actionNode, Node.CONNECTION_DEFAULT_TYPE, endNode, Node.CONNECTION_DEFAULT_TYPE);
        // add process RuleFlowProcess{process} to InternalKnowledgePackage{pkg}
        pkg.addProcess(process);
        List<KnowledgePackage> pkgs = new ArrayList<KnowledgePackage>();
        // add InternalKnowledgePackage{pkg} to List{pkgs}
        pkgs.add(pkg);
        // add knowledge List{pkgs} to KnowledgeBase{kbase}
        kbase.addKnowledgePackages(pkgs);
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
        final List<ProcessEvent> processEventList = new ArrayList<ProcessEvent>();
        final ProcessEventListener processEventListener = new ProcessEventListener() {
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                // add ProcessNodeLeftEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
                // add ProcessNodeTriggeredEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void afterProcessCompleted(ProcessCompletedEvent event) {
                // add ProcessCompletedEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void afterProcessStarted(ProcessStartedEvent event) {
                // add ProcessStartedEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void beforeNodeLeft(ProcessNodeLeftEvent event) {
                // add ProcessNodeLeftEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
                // add ProcessNodeTriggeredEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void beforeProcessCompleted(ProcessCompletedEvent event) {
                // add ProcessCompletedEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void beforeProcessStarted(ProcessStartedEvent event) {
                // add ProcessStartedEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void beforeVariableChanged(ProcessVariableChangedEvent event) {
                // add ProcessVariableChangedEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void afterVariableChanged(ProcessVariableChangedEvent event) {
                // add ProcessVariableChangedEvent{event} to List{processEventList}
                processEventList.add(event);
            }
        };
        // add event ProcessEventListener{processEventListener} to StatefulKnowledgeSession{session}
        session.addEventListener(processEventListener);
        // execute the process
        // start process String{"org.drools.core.process.event"} to StatefulKnowledgeSession{session}
        session.startProcess("org.drools.core.process.event");
        // assert equals int{16} to void{Assert}
        Assert.assertEquals(16, processEventList.size());
        // assert equals String{"org.drools.core.process.event"} to void{Assert}
        Assert.assertEquals("org.drools.core.process.event", ((ProcessStartedEvent) (processEventList.get(0))).getProcessInstance().getProcessId());
        // assert equals String{"Start"} to void{Assert}
        Assert.assertEquals("Start", ((ProcessNodeTriggeredEvent) (processEventList.get(1))).getNodeInstance().getNodeName());
        // assert equals String{"Start"} to void{Assert}
        Assert.assertEquals("Start", ((ProcessNodeLeftEvent) (processEventList.get(2))).getNodeInstance().getNodeName());
        // assert equals String{"Print"} to void{Assert}
        Assert.assertEquals("Print", ((ProcessNodeTriggeredEvent) (processEventList.get(3))).getNodeInstance().getNodeName());
        // assert equals String{"Print"} to void{Assert}
        Assert.assertEquals("Print", ((ProcessNodeLeftEvent) (processEventList.get(4))).getNodeInstance().getNodeName());
        // assert equals String{"End"} to void{Assert}
        Assert.assertEquals("End", ((ProcessNodeTriggeredEvent) (processEventList.get(5))).getNodeInstance().getNodeName());
        // assert equals String{"End"} to void{Assert}
        Assert.assertEquals("End", ((ProcessNodeLeftEvent) (processEventList.get(6))).getNodeInstance().getNodeName());
        // assert equals String{"org.drools.core.process.event"} to void{Assert}
        Assert.assertEquals("org.drools.core.process.event", ((ProcessCompletedEvent) (processEventList.get(7))).getProcessInstance().getProcessId());
        // assert equals String{"org.drools.core.process.event"} to void{Assert}
        Assert.assertEquals("org.drools.core.process.event", ((ProcessCompletedEvent) (processEventList.get(8))).getProcessInstance().getProcessId());
        // assert equals String{"End"} to void{Assert}
        Assert.assertEquals("End", ((ProcessNodeLeftEvent) (processEventList.get(9))).getNodeInstance().getNodeName());
        // assert equals String{"End"} to void{Assert}
        Assert.assertEquals("End", ((ProcessNodeTriggeredEvent) (processEventList.get(10))).getNodeInstance().getNodeName());
        // assert equals String{"Print"} to void{Assert}
        Assert.assertEquals("Print", ((ProcessNodeLeftEvent) (processEventList.get(11))).getNodeInstance().getNodeName());
        // assert equals String{"Print"} to void{Assert}
        Assert.assertEquals("Print", ((ProcessNodeTriggeredEvent) (processEventList.get(12))).getNodeInstance().getNodeName());
        // assert equals String{"Start"} to void{Assert}
        Assert.assertEquals("Start", ((ProcessNodeLeftEvent) (processEventList.get(13))).getNodeInstance().getNodeName());
        // assert equals String{"Start"} to void{Assert}
        Assert.assertEquals("Start", ((ProcessNodeTriggeredEvent) (processEventList.get(14))).getNodeInstance().getNodeName());
        // assert equals String{"org.drools.core.process.event"} to void{Assert}
        Assert.assertEquals("org.drools.core.process.event", ((ProcessStartedEvent) (processEventList.get(15))).getProcessInstance().getProcessId());
    }

    @Test
    public void testProcessEventListenerWithEvent() throws Exception {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        // create a simple package with one process to test the events
        final InternalKnowledgePackage pkg = new KnowledgePackageImpl("org.drools.test");
        RuleFlowProcess process = new RuleFlowProcess();
        // set id String{"org.drools.core.process.event"} to RuleFlowProcess{process}
        process.setId("org.drools.core.process.event");
        // set name String{"Event Process"} to RuleFlowProcess{process}
        process.setName("Event Process");
        StartNode startNode = new StartNode();
        // set name String{"Start"} to StartNode{startNode}
        startNode.setName("Start");
        // set id int{1} to StartNode{startNode}
        startNode.setId(1);
        // add node StartNode{startNode} to RuleFlowProcess{process}
        process.addNode(startNode);
        ActionNode actionNode = new ActionNode();
        // set name String{"Print"} to ActionNode{actionNode}
        actionNode.setName("Print");
        DroolsAction action = new DroolsConsequenceAction("java", null);
        // set meta String{"Action"} to DroolsAction{action}
        action.setMetaData("Action", new Action() {
            public void execute(ProcessContext context) throws Exception {
                // info String{"Executed action"} to 3{logger}
                logger.info("Executed action");
            }
        });
        // set action DroolsAction{action} to ActionNode{actionNode}
        actionNode.setAction(action);
        // set id int{2} to ActionNode{actionNode}
        actionNode.setId(2);
        // add node ActionNode{actionNode} to RuleFlowProcess{process}
        process.addNode(actionNode);
        new ConnectionImpl(startNode, Node.CONNECTION_DEFAULT_TYPE, actionNode, Node.CONNECTION_DEFAULT_TYPE);
        EventNode eventNode = new EventNode();
        // set name String{"Event"} to EventNode{eventNode}
        eventNode.setName("Event");
        // set id int{3} to EventNode{eventNode}
        eventNode.setId(3);
        List<EventFilter> filters = new ArrayList<EventFilter>();
        EventTypeFilter filter = new EventTypeFilter();
        // set type String{"signal"} to EventTypeFilter{filter}
        filter.setType("signal");
        // add EventTypeFilter{filter} to List{filters}
        filters.add(filter);
        // set event List{filters} to EventNode{eventNode}
        eventNode.setEventFilters(filters);
        // add node EventNode{eventNode} to RuleFlowProcess{process}
        process.addNode(eventNode);
        new ConnectionImpl(actionNode, Node.CONNECTION_DEFAULT_TYPE, eventNode, Node.CONNECTION_DEFAULT_TYPE);
        EndNode endNode = new EndNode();
        // set name String{"End"} to EndNode{endNode}
        endNode.setName("End");
        // set id int{4} to EndNode{endNode}
        endNode.setId(4);
        // add node EndNode{endNode} to RuleFlowProcess{process}
        process.addNode(endNode);
        new ConnectionImpl(eventNode, Node.CONNECTION_DEFAULT_TYPE, endNode, Node.CONNECTION_DEFAULT_TYPE);
        // add process RuleFlowProcess{process} to InternalKnowledgePackage{pkg}
        pkg.addProcess(process);
        List<KnowledgePackage> pkgs = new ArrayList<KnowledgePackage>();
        // add InternalKnowledgePackage{pkg} to List{pkgs}
        pkgs.add(pkg);
        // add knowledge List{pkgs} to KnowledgeBase{kbase}
        kbase.addKnowledgePackages(pkgs);
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
        final List<ProcessEvent> processEventList = new ArrayList<ProcessEvent>();
        final ProcessEventListener processEventListener = new ProcessEventListener() {
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                // add ProcessNodeLeftEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
                // add ProcessNodeTriggeredEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void afterProcessCompleted(ProcessCompletedEvent event) {
                // add ProcessCompletedEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void afterProcessStarted(ProcessStartedEvent event) {
                // add ProcessStartedEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void beforeNodeLeft(ProcessNodeLeftEvent event) {
                // add ProcessNodeLeftEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
                // add ProcessNodeTriggeredEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void beforeProcessCompleted(ProcessCompletedEvent event) {
                // add ProcessCompletedEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void beforeProcessStarted(ProcessStartedEvent event) {
                // add ProcessStartedEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void beforeVariableChanged(ProcessVariableChangedEvent event) {
                // add ProcessVariableChangedEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void afterVariableChanged(ProcessVariableChangedEvent event) {
                // add ProcessVariableChangedEvent{event} to List{processEventList}
                processEventList.add(event);
            }
        };
        // add event ProcessEventListener{processEventListener} to StatefulKnowledgeSession{session}
        session.addEventListener(processEventListener);
        // execute the process
        ProcessInstance pi = session.startProcess("org.drools.core.process.event");
        // signal event String{"signal"} to ProcessInstance{pi}
        pi.signalEvent("signal", null);
        // assert equals int{20} to void{Assert}
        Assert.assertEquals(20, processEventList.size());
        // assert equals String{"org.drools.core.process.event"} to void{Assert}
        Assert.assertEquals("org.drools.core.process.event", ((ProcessStartedEvent) (processEventList.get(0))).getProcessInstance().getProcessId());
        // assert equals String{"Start"} to void{Assert}
        Assert.assertEquals("Start", ((ProcessNodeTriggeredEvent) (processEventList.get(1))).getNodeInstance().getNodeName());
        // assert equals String{"Start"} to void{Assert}
        Assert.assertEquals("Start", ((ProcessNodeLeftEvent) (processEventList.get(2))).getNodeInstance().getNodeName());
        // assert equals String{"Print"} to void{Assert}
        Assert.assertEquals("Print", ((ProcessNodeTriggeredEvent) (processEventList.get(3))).getNodeInstance().getNodeName());
        // assert equals String{"Print"} to void{Assert}
        Assert.assertEquals("Print", ((ProcessNodeLeftEvent) (processEventList.get(4))).getNodeInstance().getNodeName());
        // assert equals String{"Event"} to void{Assert}
        Assert.assertEquals("Event", ((ProcessNodeTriggeredEvent) (processEventList.get(5))).getNodeInstance().getNodeName());
        // assert equals String{"Event"} to void{Assert}
        Assert.assertEquals("Event", ((ProcessNodeTriggeredEvent) (processEventList.get(6))).getNodeInstance().getNodeName());
        // assert equals String{"Print"} to void{Assert}
        Assert.assertEquals("Print", ((ProcessNodeLeftEvent) (processEventList.get(7))).getNodeInstance().getNodeName());
        // assert equals String{"Print"} to void{Assert}
        Assert.assertEquals("Print", ((ProcessNodeTriggeredEvent) (processEventList.get(8))).getNodeInstance().getNodeName());
        // assert equals String{"Start"} to void{Assert}
        Assert.assertEquals("Start", ((ProcessNodeLeftEvent) (processEventList.get(9))).getNodeInstance().getNodeName());
        // assert equals String{"Start"} to void{Assert}
        Assert.assertEquals("Start", ((ProcessNodeTriggeredEvent) (processEventList.get(10))).getNodeInstance().getNodeName());
        // assert equals String{"org.drools.core.process.event"} to void{Assert}
        Assert.assertEquals("org.drools.core.process.event", ((ProcessStartedEvent) (processEventList.get(11))).getProcessInstance().getProcessId());
        // assert equals String{"Event"} to void{Assert}
        Assert.assertEquals("Event", ((ProcessNodeLeftEvent) (processEventList.get(12))).getNodeInstance().getNodeName());
        // assert equals String{"End"} to void{Assert}
        Assert.assertEquals("End", ((ProcessNodeTriggeredEvent) (processEventList.get(13))).getNodeInstance().getNodeName());
        // assert equals String{"End"} to void{Assert}
        Assert.assertEquals("End", ((ProcessNodeLeftEvent) (processEventList.get(14))).getNodeInstance().getNodeName());
        // assert equals String{"org.drools.core.process.event"} to void{Assert}
        Assert.assertEquals("org.drools.core.process.event", ((ProcessCompletedEvent) (processEventList.get(15))).getProcessInstance().getProcessId());
        // assert equals String{"org.drools.core.process.event"} to void{Assert}
        Assert.assertEquals("org.drools.core.process.event", ((ProcessCompletedEvent) (processEventList.get(16))).getProcessInstance().getProcessId());
        // assert equals String{"End"} to void{Assert}
        Assert.assertEquals("End", ((ProcessNodeLeftEvent) (processEventList.get(17))).getNodeInstance().getNodeName());
        // assert equals String{"Event"} to void{Assert}
        Assert.assertEquals("Event", ((ProcessNodeLeftEvent) (processEventList.get(19))).getNodeInstance().getNodeName());
        // assert equals String{"End"} to void{Assert}
        Assert.assertEquals("End", ((ProcessNodeTriggeredEvent) (processEventList.get(18))).getNodeInstance().getNodeName());
    }

    @Test
    public void testProcessEventListenerWithEndEvent() throws Exception {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        // create a simple package with one process to test the events
        final InternalKnowledgePackage pkg = new KnowledgePackageImpl("org.drools.test");
        RuleFlowProcess process = new RuleFlowProcess();
        // set id String{"org.drools.core.process.event"} to RuleFlowProcess{process}
        process.setId("org.drools.core.process.event");
        // set name String{"Event Process"} to RuleFlowProcess{process}
        process.setName("Event Process");
        StartNode startNode = new StartNode();
        // set name String{"Start"} to StartNode{startNode}
        startNode.setName("Start");
        // set id int{1} to StartNode{startNode}
        startNode.setId(1);
        // add node StartNode{startNode} to RuleFlowProcess{process}
        process.addNode(startNode);
        ActionNode actionNode = new ActionNode();
        // set name String{"Print"} to ActionNode{actionNode}
        actionNode.setName("Print");
        DroolsAction action = new DroolsConsequenceAction("java", null);
        // set meta String{"Action"} to DroolsAction{action}
        action.setMetaData("Action", new Action() {
            public void execute(ProcessContext context) throws Exception {
                // info String{"Executed action"} to 5{logger}
                logger.info("Executed action");
            }
        });
        // set action DroolsAction{action} to ActionNode{actionNode}
        actionNode.setAction(action);
        // set id int{2} to ActionNode{actionNode}
        actionNode.setId(2);
        // add node ActionNode{actionNode} to RuleFlowProcess{process}
        process.addNode(actionNode);
        new ConnectionImpl(startNode, Node.CONNECTION_DEFAULT_TYPE, actionNode, Node.CONNECTION_DEFAULT_TYPE);
        EndNode endNode = new EndNode();
        // set name String{"End"} to EndNode{endNode}
        endNode.setName("End");
        // set id int{3} to EndNode{endNode}
        endNode.setId(3);
        // set terminate boolean{false} to EndNode{endNode}
        endNode.setTerminate(false);
        // add node EndNode{endNode} to RuleFlowProcess{process}
        process.addNode(endNode);
        new ConnectionImpl(actionNode, Node.CONNECTION_DEFAULT_TYPE, endNode, Node.CONNECTION_DEFAULT_TYPE);
        // add process RuleFlowProcess{process} to InternalKnowledgePackage{pkg}
        pkg.addProcess(process);
        List<KnowledgePackage> pkgs = new ArrayList<KnowledgePackage>();
        // add InternalKnowledgePackage{pkg} to List{pkgs}
        pkgs.add(pkg);
        // add knowledge List{pkgs} to KnowledgeBase{kbase}
        kbase.addKnowledgePackages(pkgs);
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
        final List<ProcessEvent> processEventList = new ArrayList<ProcessEvent>();
        final ProcessEventListener processEventListener = new ProcessEventListener() {
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                // add ProcessNodeLeftEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
                // add ProcessNodeTriggeredEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void afterProcessCompleted(ProcessCompletedEvent event) {
                // add ProcessCompletedEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void afterProcessStarted(ProcessStartedEvent event) {
                // add ProcessStartedEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void beforeNodeLeft(ProcessNodeLeftEvent event) {
                // add ProcessNodeLeftEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
                // add ProcessNodeTriggeredEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void beforeProcessCompleted(ProcessCompletedEvent event) {
                // add ProcessCompletedEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void beforeProcessStarted(ProcessStartedEvent event) {
                // add ProcessStartedEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void beforeVariableChanged(ProcessVariableChangedEvent event) {
                // add ProcessVariableChangedEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void afterVariableChanged(ProcessVariableChangedEvent event) {
                // add ProcessVariableChangedEvent{event} to List{processEventList}
                processEventList.add(event);
            }
        };
        // add event ProcessEventListener{processEventListener} to StatefulKnowledgeSession{session}
        session.addEventListener(processEventListener);
        // execute the process
        // start process String{"org.drools.core.process.event"} to StatefulKnowledgeSession{session}
        session.startProcess("org.drools.core.process.event");
        // assert equals int{14} to void{Assert}
        Assert.assertEquals(14, processEventList.size());
        // assert equals String{"org.drools.core.process.event"} to void{Assert}
        Assert.assertEquals("org.drools.core.process.event", ((ProcessStartedEvent) (processEventList.get(0))).getProcessInstance().getProcessId());
        // assert equals String{"Start"} to void{Assert}
        Assert.assertEquals("Start", ((ProcessNodeTriggeredEvent) (processEventList.get(1))).getNodeInstance().getNodeName());
        // assert equals String{"Start"} to void{Assert}
        Assert.assertEquals("Start", ((ProcessNodeLeftEvent) (processEventList.get(2))).getNodeInstance().getNodeName());
        // assert equals String{"Print"} to void{Assert}
        Assert.assertEquals("Print", ((ProcessNodeTriggeredEvent) (processEventList.get(3))).getNodeInstance().getNodeName());
        // assert equals String{"Print"} to void{Assert}
        Assert.assertEquals("Print", ((ProcessNodeLeftEvent) (processEventList.get(4))).getNodeInstance().getNodeName());
        // assert equals String{"End"} to void{Assert}
        Assert.assertEquals("End", ((ProcessNodeTriggeredEvent) (processEventList.get(5))).getNodeInstance().getNodeName());
        // assert equals String{"End"} to void{Assert}
        Assert.assertEquals("End", ((ProcessNodeLeftEvent) (processEventList.get(6))).getNodeInstance().getNodeName());
        // assert equals String{"End"} to void{Assert}
        Assert.assertEquals("End", ((ProcessNodeLeftEvent) (processEventList.get(7))).getNodeInstance().getNodeName());
        // assert equals String{"End"} to void{Assert}
        Assert.assertEquals("End", ((ProcessNodeTriggeredEvent) (processEventList.get(8))).getNodeInstance().getNodeName());
        // assert equals String{"Print"} to void{Assert}
        Assert.assertEquals("Print", ((ProcessNodeLeftEvent) (processEventList.get(9))).getNodeInstance().getNodeName());
        // assert equals String{"Print"} to void{Assert}
        Assert.assertEquals("Print", ((ProcessNodeTriggeredEvent) (processEventList.get(10))).getNodeInstance().getNodeName());
        // assert equals String{"Start"} to void{Assert}
        Assert.assertEquals("Start", ((ProcessNodeLeftEvent) (processEventList.get(11))).getNodeInstance().getNodeName());
        // assert equals String{"Start"} to void{Assert}
        Assert.assertEquals("Start", ((ProcessNodeTriggeredEvent) (processEventList.get(12))).getNodeInstance().getNodeName());
        // assert equals String{"org.drools.core.process.event"} to void{Assert}
        Assert.assertEquals("org.drools.core.process.event", ((ProcessStartedEvent) (processEventList.get(13))).getProcessInstance().getProcessId());
    }

    @Test
    public void testProcessEventListenerWithStartEvent() throws Exception {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        // create a simple package with one process to test the events
        final InternalKnowledgePackage pkg = new KnowledgePackageImpl("org.drools.test");
        RuleFlowProcess process = new RuleFlowProcess();
        // set id String{"org.drools.core.process.event"} to RuleFlowProcess{process}
        process.setId("org.drools.core.process.event");
        // set name String{"Event Process"} to RuleFlowProcess{process}
        process.setName("Event Process");
        StartNode startNode = new StartNode();
        // set name String{"Start"} to StartNode{startNode}
        startNode.setName("Start");
        // set id int{1} to StartNode{startNode}
        startNode.setId(1);
        EventTrigger trigger = new EventTrigger();
        EventTypeFilter eventFilter = new EventTypeFilter();
        // set type String{"signal"} to EventTypeFilter{eventFilter}
        eventFilter.setType("signal");
        // add event EventTypeFilter{eventFilter} to EventTrigger{trigger}
        trigger.addEventFilter(eventFilter);
        // add trigger EventTrigger{trigger} to StartNode{startNode}
        startNode.addTrigger(trigger);
        // add node StartNode{startNode} to RuleFlowProcess{process}
        process.addNode(startNode);
        ActionNode actionNode = new ActionNode();
        // set name String{"Print"} to ActionNode{actionNode}
        actionNode.setName("Print");
        DroolsAction action = new DroolsConsequenceAction("java", null);
        // set meta String{"Action"} to DroolsAction{action}
        action.setMetaData("Action", new Action() {
            public void execute(ProcessContext context) throws Exception {
                // info String{"Executed action"} to 7{logger}
                logger.info("Executed action");
            }
        });
        // set action DroolsAction{action} to ActionNode{actionNode}
        actionNode.setAction(action);
        // set id int{2} to ActionNode{actionNode}
        actionNode.setId(2);
        // add node ActionNode{actionNode} to RuleFlowProcess{process}
        process.addNode(actionNode);
        new ConnectionImpl(startNode, Node.CONNECTION_DEFAULT_TYPE, actionNode, Node.CONNECTION_DEFAULT_TYPE);
        EndNode endNode = new EndNode();
        // set name String{"End"} to EndNode{endNode}
        endNode.setName("End");
        // set id int{3} to EndNode{endNode}
        endNode.setId(3);
        // add node EndNode{endNode} to RuleFlowProcess{process}
        process.addNode(endNode);
        new ConnectionImpl(actionNode, Node.CONNECTION_DEFAULT_TYPE, endNode, Node.CONNECTION_DEFAULT_TYPE);
        // add process RuleFlowProcess{process} to InternalKnowledgePackage{pkg}
        pkg.addProcess(process);
        List<KnowledgePackage> pkgs = new ArrayList<KnowledgePackage>();
        // add InternalKnowledgePackage{pkg} to List{pkgs}
        pkgs.add(pkg);
        // add knowledge List{pkgs} to KnowledgeBase{kbase}
        kbase.addKnowledgePackages(pkgs);
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
        final List<ProcessEvent> processEventList = new ArrayList<ProcessEvent>();
        final ProcessEventListener processEventListener = new ProcessEventListener() {
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                // add ProcessNodeLeftEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
                // add ProcessNodeTriggeredEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void afterProcessCompleted(ProcessCompletedEvent event) {
                // add ProcessCompletedEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void afterProcessStarted(ProcessStartedEvent event) {
                // add ProcessStartedEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void beforeNodeLeft(ProcessNodeLeftEvent event) {
                // add ProcessNodeLeftEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
                // add ProcessNodeTriggeredEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void beforeProcessCompleted(ProcessCompletedEvent event) {
                // add ProcessCompletedEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void beforeProcessStarted(ProcessStartedEvent event) {
                // add ProcessStartedEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void beforeVariableChanged(ProcessVariableChangedEvent event) {
                // add ProcessVariableChangedEvent{event} to List{processEventList}
                processEventList.add(event);
            }

            public void afterVariableChanged(ProcessVariableChangedEvent event) {
                // add ProcessVariableChangedEvent{event} to List{processEventList}
                processEventList.add(event);
            }
        };
        // add event ProcessEventListener{processEventListener} to StatefulKnowledgeSession{session}
        session.addEventListener(processEventListener);
        // execute the process
        // session.startProcess("org.drools.core.process.event");
        // signal event String{"signal"} to StatefulKnowledgeSession{session}
        session.signalEvent("signal", null);
        // assert equals int{16} to void{Assert}
        Assert.assertEquals(16, processEventList.size());
        // assert equals String{"org.drools.core.process.event"} to void{Assert}
        Assert.assertEquals("org.drools.core.process.event", ((ProcessStartedEvent) (processEventList.get(0))).getProcessInstance().getProcessId());
        // assert equals String{"Start"} to void{Assert}
        Assert.assertEquals("Start", ((ProcessNodeTriggeredEvent) (processEventList.get(1))).getNodeInstance().getNodeName());
        // assert equals String{"Start"} to void{Assert}
        Assert.assertEquals("Start", ((ProcessNodeLeftEvent) (processEventList.get(2))).getNodeInstance().getNodeName());
        // assert equals String{"Print"} to void{Assert}
        Assert.assertEquals("Print", ((ProcessNodeTriggeredEvent) (processEventList.get(3))).getNodeInstance().getNodeName());
        // assert equals String{"Print"} to void{Assert}
        Assert.assertEquals("Print", ((ProcessNodeLeftEvent) (processEventList.get(4))).getNodeInstance().getNodeName());
        // assert equals String{"End"} to void{Assert}
        Assert.assertEquals("End", ((ProcessNodeTriggeredEvent) (processEventList.get(5))).getNodeInstance().getNodeName());
        // assert equals String{"End"} to void{Assert}
        Assert.assertEquals("End", ((ProcessNodeLeftEvent) (processEventList.get(6))).getNodeInstance().getNodeName());
        // assert equals String{"org.drools.core.process.event"} to void{Assert}
        Assert.assertEquals("org.drools.core.process.event", ((ProcessCompletedEvent) (processEventList.get(7))).getProcessInstance().getProcessId());
        // assert equals String{"org.drools.core.process.event"} to void{Assert}
        Assert.assertEquals("org.drools.core.process.event", ((ProcessCompletedEvent) (processEventList.get(8))).getProcessInstance().getProcessId());
        // assert equals String{"End"} to void{Assert}
        Assert.assertEquals("End", ((ProcessNodeLeftEvent) (processEventList.get(9))).getNodeInstance().getNodeName());
        // assert equals String{"End"} to void{Assert}
        Assert.assertEquals("End", ((ProcessNodeTriggeredEvent) (processEventList.get(10))).getNodeInstance().getNodeName());
        // assert equals String{"Print"} to void{Assert}
        Assert.assertEquals("Print", ((ProcessNodeLeftEvent) (processEventList.get(11))).getNodeInstance().getNodeName());
        // assert equals String{"Print"} to void{Assert}
        Assert.assertEquals("Print", ((ProcessNodeTriggeredEvent) (processEventList.get(12))).getNodeInstance().getNodeName());
        // assert equals String{"Start"} to void{Assert}
        Assert.assertEquals("Start", ((ProcessNodeLeftEvent) (processEventList.get(13))).getNodeInstance().getNodeName());
        // assert equals String{"Start"} to void{Assert}
        Assert.assertEquals("Start", ((ProcessNodeTriggeredEvent) (processEventList.get(14))).getNodeInstance().getNodeName());
        // assert equals String{"org.drools.core.process.event"} to void{Assert}
        Assert.assertEquals("org.drools.core.process.event", ((ProcessStartedEvent) (processEventList.get(15))).getProcessInstance().getProcessId());
    }
}

