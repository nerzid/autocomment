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


package org.jbpm.integrationtests;

import org.jbpm.test.util.AbstractBaseTest;
import java.util.ArrayList;
import org.junit.Assert;
import java.util.Collection;
import org.junit.Ignore;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import java.util.List;
import org.kie.api.runtime.process.NodeInstance;
import org.jbpm.integrationtests.test.Person;
import org.kie.api.runtime.process.ProcessInstance;
import java.io.Reader;
import org.kie.internal.io.ResourceFactory;
import org.jbpm.workflow.instance.node.StateNodeInstance;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import java.io.StringReader;
import org.junit.Test;
import org.kie.api.runtime.process.WorkflowProcessInstance;

public class ProcessStateTest extends AbstractBaseTest {
    @Test
    public void testManualSignalState() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Reader source = new StringReader(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ("<process xmlns=\"http://drools.org/drools-5.0/process\"\n" + ("         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + ("         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" + ("         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.state\" package-name=\"org.drools\" version=\"1\" >\n" + ("\n" + ("  <header>\n" + ("  </header>\n" + ("\n" + ("  <nodes>\n" + ("    <start id=\"1\" name=\"Start\" />\n" + ("    <state id=\"2\" name=\"StateA\" >\n" + ("      <constraints>\n" + ("        <constraint toNodeId=\"3\" name=\"toB\" />\n" + ("       <constraint toNodeId=\"4\" name=\"toC\" />\n" + ("      </constraints>\n" + ("    </state>\n" + ("    <state id=\"3\" name=\"StateB\" />\n" + ("    <state id=\"4\" name=\"StateC\" />\n" + ("    <end id=\"5\" name=\"End\" />\n" + ("  </nodes>\n" + ("\n" + ("  <connections>\n" + ("    <connection from=\"1\" to=\"2\" />\n" + ("    <connection from=\"2\" to=\"3\" />\n" + ("    <connection from=\"2\" to=\"4\" />\n" + ("    <connection from=\"3\" to=\"2\" />\n" + ("    <connection from=\"4\" to=\"5\" />\n" + ("  </connections>\n" + ("\n" + "</process>")))))))))))))))))))))))))))))));
        kbuilder.add(ResourceFactory.newReaderResource(source), ResourceType.DRF);
        KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        // start process
        WorkflowProcessInstance processInstance = ((WorkflowProcessInstance) (ksession.startProcess("org.drools.state")));
        // should be in state A
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        Collection<NodeInstance> nodeInstances = processInstance.getNodeInstances();
        Assert.assertEquals(1, nodeInstances.size());
        StateNodeInstance stateInstance = ((StateNodeInstance) (nodeInstances.iterator().next()));
        Assert.assertEquals("StateA", stateInstance.getNodeName());
        // signal "toB" so we move to state B
        processInstance.signalEvent("signal", "toB");
        nodeInstances = processInstance.getNodeInstances();
        Assert.assertEquals(1, nodeInstances.size());
        stateInstance = ((StateNodeInstance) (nodeInstances.iterator().next()));
        Assert.assertEquals("StateB", stateInstance.getNodeName());
        // if no constraint specified for a connection,
        // we default to the name of the target node
        // signal "StateA", so we move back to state A
        processInstance.signalEvent("signal", "StateA");
        nodeInstances = processInstance.getNodeInstances();
        Assert.assertEquals(1, nodeInstances.size());
        stateInstance = ((StateNodeInstance) (nodeInstances.iterator().next()));
        Assert.assertEquals("StateA", stateInstance.getNodeName());
        // signal "toC" so we move to state C
        processInstance.signalEvent("signal", "toC");
        nodeInstances = processInstance.getNodeInstances();
        Assert.assertEquals(1, nodeInstances.size());
        stateInstance = ((StateNodeInstance) (nodeInstances.iterator().next()));
        Assert.assertEquals("StateC", stateInstance.getNodeName());
        // signal something completely wrong, this should simply be ignored
        processInstance.signalEvent("signal", "Invalid");
        nodeInstances = processInstance.getNodeInstances();
        Assert.assertEquals(1, nodeInstances.size());
        stateInstance = ((StateNodeInstance) (nodeInstances.iterator().next()));
        Assert.assertEquals("StateC", stateInstance.getNodeName());
        // signal "End", so we move to the end
        processInstance.signalEvent("signal", "End");
        nodeInstances = processInstance.getNodeInstances();
        Assert.assertEquals(0, nodeInstances.size());
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    @Test
    public void testImmediateStateConstraint1() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Reader source = new StringReader(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ("<process xmlns=\"http://drools.org/drools-5.0/process\"\n" + ("         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + ("         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" + ("         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.state\" package-name=\"org.drools\" version=\"1\" >\n" + ("\n" + ("  <header>\n" + ("    <globals>\n" + ("      <global identifier=\"list\" type=\"java.util.List\" />\n" + ("    </globals>\n" + ("  </header>\n" + ("\n" + ("  <nodes>\n" + ("    <start id=\"1\" name=\"Start\" />\n" + ("    <state id=\"2\" >\n" + ("      <constraints>\n" + ("        <constraint toNodeId=\"3\" name=\"one\" >\n" + ("            eval(true)" + ("        </constraint>" + ("       <constraint toNodeId=\"4\" name=\"two\" >\n" + ("           eval(false)" + ("        </constraint>" + ("      </constraints>\n" + ("    </state>\n" + ("    <actionNode id=\"3\" name=\"ActionNode1\" >\n" + ("      <action type=\"expression\" dialect=\"java\" >list.add(\"1\");</action>\n" + ("    </actionNode>\n" + ("    <end id=\"4\" name=\"End\" />\n" + ("    <actionNode id=\"5\" name=\"ActionNode2\" >\n" + ("      <action type=\"expression\" dialect=\"java\" >list.add(\"2\");</action>\n" + ("    </actionNode>\n" + ("    <end id=\"6\" name=\"End\" />\n" + ("  </nodes>\n" + ("\n" + ("  <connections>\n" + ("    <connection from=\"1\" to=\"2\" />\n" + ("    <connection from=\"2\" to=\"3\" />\n" + ("    <connection from=\"3\" to=\"4\" />\n" + ("    <connection from=\"2\" to=\"5\" />\n" + ("    <connection from=\"5\" to=\"6\" />\n" + ("  </connections>\n" + ("\n" + "</process>")))))))))))))))))))))))))))))))))))))))))));
        kbuilder.add(ResourceFactory.newReaderResource(source), ResourceType.DRF);
        KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ProcessInstance processInstance = ksession.startProcess("org.drools.state");
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("1", list.get(0));
    }

    @Test
    public void testImmediateStateConstraintPriorities1() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Reader source = new StringReader(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ("<process xmlns=\"http://drools.org/drools-5.0/process\"\n" + ("         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + ("         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" + ("         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.state\" package-name=\"org.drools\" version=\"1\" >\n" + ("\n" + ("  <header>\n" + ("    <globals>\n" + ("      <global identifier=\"list\" type=\"java.util.List\" />\n" + ("    </globals>\n" + ("  </header>\n" + ("\n" + ("  <nodes>\n" + ("    <start id=\"1\" name=\"Start\" />\n" + ("    <state id=\"2\" >\n" + ("      <constraints>\n" + ("        <constraint toNodeId=\"3\" name=\"one\" priority=\"1\" >\n" + ("            eval(true)" + ("        </constraint>" + ("       <constraint toNodeId=\"5\" name=\"two\" priority=\"2\" >\n" + ("           eval(true)" + ("        </constraint>" + ("      </constraints>\n" + ("    </state>\n" + ("    <actionNode id=\"3\" name=\"ActionNode1\" >\n" + ("      <action type=\"expression\" dialect=\"java\" >list.add(\"1\");</action>\n" + ("    </actionNode>\n" + ("    <end id=\"4\" name=\"End\" />\n" + ("    <actionNode id=\"5\" name=\"ActionNode2\" >\n" + ("      <action type=\"expression\" dialect=\"java\" >list.add(\"2\");</action>\n" + ("    </actionNode>\n" + ("    <end id=\"6\" name=\"End\" />\n" + ("  </nodes>\n" + ("\n" + ("  <connections>\n" + ("    <connection from=\"1\" to=\"2\" />\n" + ("    <connection from=\"2\" to=\"3\" />\n" + ("    <connection from=\"3\" to=\"4\" />\n" + ("    <connection from=\"2\" to=\"5\" />\n" + ("    <connection from=\"5\" to=\"6\" />\n" + ("  </connections>\n" + ("\n" + "</process>")))))))))))))))))))))))))))))))))))))))))));
        kbuilder.add(ResourceFactory.newReaderResource(source), ResourceType.DRF);
        KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ProcessInstance processInstance = ksession.startProcess("org.drools.state");
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("1", list.get(0));
    }

    @Test
    public void testImmediateStateConstraintPriorities2() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Reader source = new StringReader(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ("<process xmlns=\"http://drools.org/drools-5.0/process\"\n" + ("         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + ("         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" + ("         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.state\" package-name=\"org.drools\" version=\"1\" >\n" + ("\n" + ("  <header>\n" + ("    <globals>\n" + ("      <global identifier=\"list\" type=\"java.util.List\" />\n" + ("    </globals>\n" + ("  </header>\n" + ("\n" + ("  <nodes>\n" + ("    <start id=\"1\" name=\"Start\" />\n" + ("    <state id=\"2\" >\n" + ("      <constraints>\n" + ("        <constraint toNodeId=\"3\" name=\"one\" priority=\"2\" >\n" + ("            eval(true)" + ("        </constraint>" + ("       <constraint toNodeId=\"5\" name=\"two\" priority=\"1\" >\n" + ("           eval(true)" + ("        </constraint>" + ("      </constraints>\n" + ("    </state>\n" + ("    <actionNode id=\"3\" name=\"ActionNode1\" >\n" + ("      <action type=\"expression\" dialect=\"java\" >list.add(\"1\");</action>\n" + ("    </actionNode>\n" + ("    <end id=\"4\" name=\"End\" />\n" + ("    <actionNode id=\"5\" name=\"ActionNode2\" >\n" + ("      <action type=\"expression\" dialect=\"java\" >list.add(\"2\");</action>\n" + ("    </actionNode>\n" + ("    <end id=\"6\" name=\"End\" />\n" + ("  </nodes>\n" + ("\n" + ("  <connections>\n" + ("    <connection from=\"1\" to=\"2\" />\n" + ("    <connection from=\"2\" to=\"3\" />\n" + ("    <connection from=\"3\" to=\"4\" />\n" + ("    <connection from=\"2\" to=\"5\" />\n" + ("    <connection from=\"5\" to=\"6\" />\n" + ("  </connections>\n" + ("\n" + "</process>")))))))))))))))))))))))))))))))))))))))))));
        kbuilder.add(ResourceFactory.newReaderResource(source), ResourceType.DRF);
        KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ProcessInstance processInstance = ksession.startProcess("org.drools.state");
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("2", list.get(0));
    }

    @Test
    public void testDelayedStateConstraint() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Reader source = new StringReader(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ("<process xmlns=\"http://drools.org/drools-5.0/process\"\n" + ("         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + ("         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" + ("         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.state\" package-name=\"org.jbpm\" version=\"1\" >\n" + ("\n" + ("  <header>\n" + ("    <imports>\n" + ("      <import name=\"org.jbpm.integrationtests.test.Person\" />\n" + ("    </imports>\n" + ("    <globals>\n" + ("      <global identifier=\"list\" type=\"java.util.List\" />\n" + ("    </globals>\n" + ("  </header>\n" + ("\n" + ("  <nodes>\n" + ("    <start id=\"1\" name=\"Start\" />\n" + ("    <state id=\"2\" >\n" + ("      <constraints>\n" + ("        <constraint toNodeId=\"3\" name=\"one\" >\n" + ("            Person( age &gt; 21 )" + ("        </constraint>" + ("       <constraint toNodeId=\"4\" name=\"two\" >\n" + ("           Person( age &lt;= 21 )" + ("        </constraint>" + ("      </constraints>\n" + ("    </state>\n" + ("    <actionNode id=\"3\" name=\"ActionNode1\" >\n" + ("      <action type=\"expression\" dialect=\"java\" >list.add(\"1\");</action>\n" + ("    </actionNode>\n" + ("    <end id=\"4\" name=\"End\" />\n" + ("    <actionNode id=\"5\" name=\"ActionNode2\" >\n" + ("      <action type=\"expression\" dialect=\"java\" >list.add(\"2\");</action>\n" + ("    </actionNode>\n" + ("    <end id=\"6\" name=\"End\" />\n" + ("  </nodes>\n" + ("\n" + ("  <connections>\n" + ("    <connection from=\"1\" to=\"2\" />\n" + ("    <connection from=\"2\" to=\"3\" />\n" + ("    <connection from=\"3\" to=\"4\" />\n" + ("    <connection from=\"2\" to=\"5\" />\n" + ("    <connection from=\"5\" to=\"6\" />\n" + ("  </connections>\n" + ("\n" + "</process>"))))))))))))))))))))))))))))))))))))))))))))));
        kbuilder.add(ResourceFactory.newReaderResource(source), ResourceType.DRF);
        KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ProcessInstance processInstance = ksession.startProcess("org.drools.state");
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        Assert.assertTrue(list.isEmpty());
        Person person = new Person("John Doe", 30);
        ksession.insert(person);
        ksession.fireAllRules();
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("1", list.get(0));
    }

    @Test
    public void testDelayedStateConstraint2() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Reader source = new StringReader(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ("<process xmlns=\"http://drools.org/drools-5.0/process\"\n" + ("         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + ("         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" + ("         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.state\" package-name=\"org.jbpm\" version=\"1\" >\n" + ("\n" + ("  <header>\n" + ("    <imports>\n" + ("      <import name=\"org.jbpm.integrationtests.test.Person\" />\n" + ("    </imports>\n" + ("    <globals>\n" + ("      <global identifier=\"list\" type=\"java.util.List\" />\n" + ("    </globals>\n" + ("  </header>\n" + ("\n" + ("  <nodes>\n" + ("    <start id=\"1\" name=\"Start\" />\n" + ("    <state id=\"2\" >\n" + ("      <constraints>\n" + ("        <constraint toNodeId=\"3\" name=\"age &gt; 21\" >\n" + ("            Person( age &gt; 21 )" + ("        </constraint>" + ("       <constraint toNodeId=\"5\" name=\"age &lt;=21 \" >\n" + ("           Person( age &lt;= 21 )" + ("        </constraint>" + ("      </constraints>\n" + ("    </state>\n" + ("    <actionNode id=\"3\" name=\"ActionNode1\" >\n" + ("      <action type=\"expression\" dialect=\"java\" >list.add(\"1\");</action>\n" + ("    </actionNode>\n" + ("    <end id=\"4\" name=\"End\" />\n" + ("    <actionNode id=\"5\" name=\"ActionNode2\" >\n" + ("      <action type=\"expression\" dialect=\"java\" >list.add(\"2\");</action>\n" + ("    </actionNode>\n" + ("    <end id=\"6\" name=\"End\" />\n" + ("  </nodes>\n" + ("\n" + ("  <connections>\n" + ("    <connection from=\"1\" to=\"2\" />\n" + ("    <connection from=\"2\" to=\"3\" />\n" + ("    <connection from=\"3\" to=\"4\" />\n" + ("    <connection from=\"2\" to=\"5\" />\n" + ("    <connection from=\"5\" to=\"6\" />\n" + ("  </connections>\n" + ("\n" + "</process>"))))))))))))))))))))))))))))))))))))))))))))));
        kbuilder.add(ResourceFactory.newReaderResource(source), ResourceType.DRF);
        KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ProcessInstance processInstance = ksession.startProcess("org.drools.state");
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        Assert.assertTrue(list.isEmpty());
        Person person = new Person("John Doe", 20);
        ksession.insert(person);
        ksession.fireAllRules();
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("2", list.get(0));
    }

    @Test
    @Ignore
    public void FIXMEtestDelayedStateConstraintPriorities1() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Reader source = new StringReader(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ("<process xmlns=\"http://drools.org/drools-5.0/process\"\n" + ("         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + ("         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" + ("         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.state\" package-name=\"org.drools\" version=\"1\" >\n" + ("\n" + ("  <header>\n" + ("    <imports>\n" + ("      <import name=\"org.jbpm.integrationtests.test.Person\" />\n" + ("    </imports>\n" + ("    <globals>\n" + ("      <global identifier=\"list\" type=\"java.util.List\" />\n" + ("    </globals>\n" + ("  </header>\n" + ("\n" + ("  <nodes>\n" + ("    <start id=\"1\" name=\"Start\" />\n" + ("    <state id=\"2\" >\n" + ("      <constraints>\n" + ("        <constraint toNodeId=\"3\" name=\"one\" priority=\"1\" >\n" + ("            Person( )" + ("        </constraint>" + ("       <constraint toNodeId=\"5\" name=\"two\" priority=\"2\" >\n" + ("           Person( )" + ("        </constraint>" + ("      </constraints>\n" + ("    </state>\n" + ("    <actionNode id=\"3\" name=\"ActionNode1\" >\n" + ("      <action type=\"expression\" dialect=\"java\" >list.add(\"1\");</action>\n" + ("    </actionNode>\n" + ("    <end id=\"4\" name=\"End\" />\n" + ("    <actionNode id=\"5\" name=\"ActionNode2\" >\n" + ("      <action type=\"expression\" dialect=\"java\" >list.add(\"2\");</action>\n" + ("    </actionNode>\n" + ("    <end id=\"6\" name=\"End\" />\n" + ("  </nodes>\n" + ("\n" + ("  <connections>\n" + ("    <connection from=\"1\" to=\"2\" />\n" + ("    <connection from=\"2\" to=\"3\" />\n" + ("    <connection from=\"3\" to=\"4\" />\n" + ("    <connection from=\"2\" to=\"5\" />\n" + ("    <connection from=\"5\" to=\"6\" />\n" + ("  </connections>\n" + ("\n" + "</process>"))))))))))))))))))))))))))))))))))))))))))))));
        kbuilder.add(ResourceFactory.newReaderResource(source), ResourceType.DRF);
        KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ProcessInstance processInstance = ksession.startProcess("org.drools.state");
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        Assert.assertTrue(list.isEmpty());
        Person person = new Person("John Doe", 30);
        ksession.insert(person);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("1", list.get(0));
    }

    @Test
    @Ignore
    public void FIXMEtestDelayedStateConstraintPriorities2() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Reader source = new StringReader(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ("<process xmlns=\"http://drools.org/drools-5.0/process\"\n" + ("         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + ("         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" + ("         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.state\" package-name=\"org.drools\" version=\"1\" >\n" + ("\n" + ("  <header>\n" + ("    <imports>\n" + ("      <import name=\"org.jbpm.integrationtests.test.Person\" />\n" + ("    </imports>\n" + ("    <globals>\n" + ("      <global identifier=\"list\" type=\"java.util.List\" />\n" + ("    </globals>\n" + ("  </header>\n" + ("\n" + ("  <nodes>\n" + ("    <start id=\"1\" name=\"Start\" />\n" + ("    <state id=\"2\" >\n" + ("      <constraints>\n" + ("        <constraint toNodeId=\"3\" name=\"one\" priority=\"2\" >\n" + ("            Person( )" + ("        </constraint>" + ("       <constraint toNodeId=\"5\" name=\"two\" priority=\"1\" >\n" + ("           Person( )" + ("        </constraint>" + ("      </constraints>\n" + ("    </state>\n" + ("    <actionNode id=\"3\" name=\"ActionNode1\" >\n" + ("      <action type=\"expression\" dialect=\"java\" >list.add(\"1\");</action>\n" + ("    </actionNode>\n" + ("    <end id=\"4\" name=\"End\" />\n" + ("    <actionNode id=\"5\" name=\"ActionNode2\" >\n" + ("      <action type=\"expression\" dialect=\"java\" >list.add(\"2\");</action>\n" + ("    </actionNode>\n" + ("    <end id=\"6\" name=\"End\" />\n" + ("  </nodes>\n" + ("\n" + ("  <connections>\n" + ("    <connection from=\"1\" to=\"2\" />\n" + ("    <connection from=\"2\" to=\"3\" />\n" + ("    <connection from=\"3\" to=\"4\" />\n" + ("    <connection from=\"2\" to=\"5\" />\n" + ("    <connection from=\"5\" to=\"6\" />\n" + ("  </connections>\n" + ("\n" + "</process>"))))))))))))))))))))))))))))))))))))))))))))));
        kbuilder.add(ResourceFactory.newReaderResource(source), ResourceType.DRF);
        KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ProcessInstance processInstance = ksession.startProcess("org.drools.state");
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        Assert.assertTrue(list.isEmpty());
        Person person = new Person("John Doe", 30);
        ksession.insert(person);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("2", list.get(0));
    }

    @Test
    public void testActionState() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Reader source = new StringReader(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ("<process xmlns=\"http://drools.org/drools-5.0/process\"\n" + ("         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + ("         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" + ("         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.state\" package-name=\"org.drools\" version=\"1\" >\n" + ("\n" + ("  <header>\n" + ("    <globals>\n" + ("      <global identifier=\"list\" type=\"java.util.List\" />\n" + ("    </globals>\n" + ("    <variables>\n" + ("      <variable name=\"s\" >\n" + ("        <type name=\"org.drools.core.process.core.datatype.impl.type.StringDataType\" />\n" + ("        <value>a</value>\n" + ("      </variable>\n" + ("    </variables>\n" + ("  </header>\n" + ("\n" + ("  <nodes>\n" + ("    <start id=\"1\" name=\"Start\" />\n" + ("    <state id=\"2\" name=\"State\" >\n" + ("      <onEntry>" + ("        <action type=\"expression\" dialect=\"mvel\" >list.add(\"Action1\" + s);</action>\n" + ("        <action type=\"expression\" dialect=\"java\" >list.add(\"Action2\" + s);</action>\n" + ("      </onEntry>\n" + ("      <onExit>\n" + ("        <action type=\"expression\" dialect=\"mvel\" >list.add(\"Action3\" + s);</action>\n" + ("        <action type=\"expression\" dialect=\"java\" >list.add(\"Action4\" + s);</action>\n" + ("      </onExit>\n" + ("    </state>\n" + ("    <end id=\"3\" name=\"End\" />\n" + ("  </nodes>\n" + ("\n" + ("  <connections>\n" + ("    <connection from=\"1\" to=\"2\" />\n" + ("    <connection from=\"2\" to=\"3\" />\n" + ("  </connections>\n" + ("\n" + "</process>")))))))))))))))))))))))))))))))))))))));
        kbuilder.add(ResourceFactory.newReaderResource(source), ResourceType.DRF);
        KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        // start process
        WorkflowProcessInstance processInstance = ((WorkflowProcessInstance) (ksession.startProcess("org.drools.state")));
        // should be in state A
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        Collection<NodeInstance> nodeInstances = processInstance.getNodeInstances();
        Assert.assertEquals(1, nodeInstances.size());
        StateNodeInstance stateInstance = ((StateNodeInstance) (nodeInstances.iterator().next()));
        Assert.assertEquals("State", stateInstance.getNodeName());
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.contains("Action1a"));
        Assert.assertTrue(list.contains("Action2a"));
        processInstance.signalEvent("signal", "End");
        nodeInstances = processInstance.getNodeInstances();
        Assert.assertEquals(0, nodeInstances.size());
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        Assert.assertEquals(4, list.size());
        Assert.assertTrue(list.contains("Action3a"));
        Assert.assertTrue(list.contains("Action4a"));
    }

    @Test
    public void testTimerState() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Reader source = new StringReader(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ("<process xmlns=\"http://drools.org/drools-5.0/process\"\n" + ("         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + ("         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" + ("         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.state\" package-name=\"org.drools\" version=\"1\" >\n" + ("\n" + ("  <header>\n" + ("    <globals>\n" + ("      <global identifier=\"list\" type=\"java.util.List\" />\n" + ("    </globals>\n" + ("    <variables>\n" + ("      <variable name=\"s\" >\n" + ("        <type name=\"org.drools.core.process.core.datatype.impl.type.StringDataType\" />\n" + ("        <value>a</value>\n" + ("      </variable>\n" + ("    </variables>\n" + ("  </header>\n" + ("\n" + ("  <nodes>\n" + ("    <start id=\"1\" name=\"Start\" />\n" + ("    <state id=\"2\" name=\"State\" >\n" + ("      <timers>\n" + ("        <timer id=\"1\" delay=\"1s\" period=\"2s\" >\n" + ("          <action type=\"expression\" dialect=\"mvel\" >list.add(\"Timer1\" + s);</action>\n" + ("        </timer>\n" + ("        <timer id=\"2\" delay=\"1s\" period=\"2s\" >\n" + ("          <action type=\"expression\" dialect=\"mvel\" >list.add(\"Timer2\" + s);</action>\n" + ("        </timer>\n" + ("      </timers>\n" + ("    </state>\n" + ("    <end id=\"3\" name=\"End\" />\n" + ("  </nodes>\n" + ("\n" + ("  <connections>\n" + ("    <connection from=\"1\" to=\"2\" />\n" + ("    <connection from=\"2\" to=\"3\" />\n" + ("  </connections>\n" + ("\n" + "</process>")))))))))))))))))))))))))))))))))))))));
        kbuilder.add(ResourceFactory.newReaderResource(source), ResourceType.DRF);
        KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        new Thread(new Runnable() {
            public void run() {
                ksession.fireUntilHalt();
            }
        }).start();
        // start process
        WorkflowProcessInstance processInstance = ((WorkflowProcessInstance) (ksession.startProcess("org.drools.state")));
        // should be in state A
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        Collection<NodeInstance> nodeInstances = processInstance.getNodeInstances();
        Assert.assertEquals(1, nodeInstances.size());
        StateNodeInstance stateInstance = ((StateNodeInstance) (nodeInstances.iterator().next()));
        Assert.assertEquals("State", stateInstance.getNodeName());
        Assert.assertEquals(0, list.size());
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
        }
        Assert.assertEquals(4, list.size());
        Assert.assertTrue(list.contains("Timer1a"));
        Assert.assertTrue(list.contains("Timer2a"));
        processInstance.signalEvent("signal", "End");
        nodeInstances = processInstance.getNodeInstances();
        Assert.assertEquals(0, nodeInstances.size());
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        Assert.assertEquals(4, list.size());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        Assert.assertEquals(4, list.size());
        ksession.halt();
    }
}

