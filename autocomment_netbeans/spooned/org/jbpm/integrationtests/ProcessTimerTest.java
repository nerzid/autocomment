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
import java.util.Arrays;
import org.junit.Assert;
import org.kie.api.runtime.conf.ClockTypeOption;
import java.util.Collection;
import org.drools.compiler.compiler.DroolsError;
import java.util.HashMap;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.drools.core.common.InternalWorkingMemory;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.jbpm.integrationtests.test.Message;
import org.jbpm.process.instance.ProcessInstance;
import java.io.Reader;
import org.drools.core.SessionConfiguration;
import org.drools.core.time.SessionPseudoClock;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import java.io.StringReader;
import org.junit.Test;
import java.util.concurrent.TimeUnit;

public class ProcessTimerTest extends AbstractBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(ProcessTimerTest.class);

    @Test
    public void testSimpleProcess() throws Exception {
        Reader source = new StringReader(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ("<process xmlns=\"http://drools.org/drools-5.0/process\"\n" + ("         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + ("         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" + ("         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.timer\" package-name=\"org.jbpm\" version=\"1\" >\n" + ("\n" + ("  <header>\n" + ("    <imports>\n" + ("      <import name=\"org.jbpm.integrationtests.test.Message\" />\n" + ("    </imports>\n" + ("    <globals>\n" + ("      <global identifier=\"myList\" type=\"java.util.List\" />\n" + ("    </globals>\n" + ("  </header>\n" + ("\n" + ("  <nodes>\n" + ("    <start id=\"1\" name=\"Start\" />\n" + ("    <end id=\"2\" name=\"End\" />\n" + ("    <timerNode id=\"3\" name=\"Timer\" delay=\"800ms\" period=\"200ms\" />\n" + ("    <actionNode id=\"4\" name=\"Action\" >\n" + ("      <action type=\"expression\" dialect=\"java\" >System.out.println(\"Triggered\");\n" + ("myList.add( new Message() );\n" + ("insert( new Message() );\n" + ("</action>\n" + ("    </actionNode>\n" + ("    <milestone id=\"5\" name=\"Wait\" >\n" + ("      <constraint type=\"rule\" dialect=\"mvel\" >Number( intValue &gt;= 5 ) from accumulate ( m: Message( ), count( m ) )</constraint>\n" + ("    </milestone>\n" + ("  </nodes>\n" + ("\n" + ("  <connections>\n" + ("    <connection from=\"5\" to=\"2\" />\n" + ("    <connection from=\"1\" to=\"3\" />\n" + ("    <connection from=\"3\" to=\"4\" />\n" + ("    <connection from=\"4\" to=\"5\" />\n" + ("  </connections>\n" + ("\n" + "</process>"))))))))))))))))))))))))))))))))))))));
        builder.addRuleFlow(source);
        if (!(builder.getErrors().isEmpty())) {
            for (DroolsError error : builder.getErrors().getErrors()) {
                ProcessTimerTest.logger.error(error.toString());
            }
            Assert.fail("Could not build process");
        } 
        StatefulKnowledgeSession session = createKieSession(builder.getPackage());
        List<Message> myList = new ArrayList<Message>();
        session.setGlobal("myList", myList);
        ProcessInstance processInstance = ((ProcessInstance) (session.startProcess("org.drools.timer")));
        Assert.assertEquals(0, myList.size());
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        Assert.assertEquals(1, ((InternalProcessRuntime) (((InternalWorkingMemory) (session)).getProcessRuntime())).getTimerManager().getTimers().size());
        // test that the delay works
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            // do nothing
        }
        Assert.assertEquals(0, myList.size());
        // test that the period works
        try {
            Thread.sleep(1300);
        } catch (InterruptedException e) {
            // do nothing
        }
        Assert.assertEquals(5, myList.size());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            // do nothing
        }
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        session.dispose();
    }

    @Test
    public void testVariableSimpleProcess() throws Exception {
        Reader source = new StringReader(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ("<process xmlns=\"http://drools.org/drools-5.0/process\"\n" + ("         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + ("         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" + ("         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.timer\" package-name=\"org.jbpm\" version=\"1\" >\n" + ("\n" + ("  <header>\n" + ("    <imports>\n" + ("      <import name=\"org.jbpm.integrationtests.test.Message\" />\n" + ("    </imports>\n" + ("    <globals>\n" + ("      <global identifier=\"myList\" type=\"java.util.List\" />\n" + ("    </globals>\n" + ("    <variables>\n" + ("      <variable name=\"x\" >\n" + ("        <type name=\"org.drools.core.process.core.datatype.impl.type.IntegerDataType\" />\n" + ("      </variable>\n" + ("      <variable name=\"y\" >\n" + ("        <type name=\"org.drools.core.process.core.datatype.impl.type.IntegerDataType\" />\n" + ("      </variable>\n" + ("    </variables>\n" + ("  </header>\n" + ("\n" + ("  <nodes>\n" + ("    <start id=\"1\" name=\"Start\" />\n" + ("    <end id=\"2\" name=\"End\" />\n" + ("    <timerNode id=\"3\" name=\"Timer\" delay=\"#{x}ms\" period=\"#{y}ms\" />\n" + ("    <actionNode id=\"4\" name=\"Action\" >\n" + ("      <action type=\"expression\" dialect=\"java\" >System.out.println(\"Triggered\");\n" + ("myList.add( new Message() );\n" + ("insert( new Message() );\n" + ("</action>\n" + ("    </actionNode>\n" + ("    <milestone id=\"5\" name=\"Wait\" >\n" + ("      <constraint type=\"rule\" dialect=\"mvel\" >Number( intValue &gt;= 5 ) from accumulate ( m: Message( ), count( m ) )</constraint>\n" + ("    </milestone>\n" + ("  </nodes>\n" + ("\n" + ("  <connections>\n" + ("    <connection from=\"5\" to=\"2\" />\n" + ("    <connection from=\"1\" to=\"3\" />\n" + ("    <connection from=\"3\" to=\"4\" />\n" + ("    <connection from=\"4\" to=\"5\" />\n" + ("  </connections>\n" + ("\n" + "</process>"))))))))))))))))))))))))))))))))))))))))))))));
        builder.addRuleFlow(source);
        if (!(builder.getErrors().isEmpty())) {
            for (DroolsError error : builder.getErrors().getErrors()) {
                ProcessTimerTest.logger.error(error.toString());
            }
            Assert.fail("Could not build process");
        } 
        StatefulKnowledgeSession session = createKieSession(builder.getPackage());
        List<Message> myList = new ArrayList<Message>();
        session.setGlobal("myList", myList);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", 800);
        params.put("y", 200);
        ProcessInstance processInstance = ((ProcessInstance) (session.startProcess("org.drools.timer", params)));
        Assert.assertEquals(0, myList.size());
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        Assert.assertEquals(1, ((InternalProcessRuntime) (((InternalWorkingMemory) (session)).getProcessRuntime())).getTimerManager().getTimers().size());
        // test that the delay works
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            // do nothing
        }
        Assert.assertEquals(0, myList.size());
        // test that the period works
        try {
            Thread.sleep(1300);
        } catch (InterruptedException e) {
            // do nothing
        }
        Assert.assertEquals(5, myList.size());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            // do nothing
        }
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        session.dispose();
    }

    @Test
    public void testIncorrectTimerNode() throws Exception {
        Reader source = new StringReader(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ("<process xmlns=\"http://drools.org/drools-5.0/process\"\n" + ("         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + ("         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" + ("         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.timer\" package-name=\"org.drools\" version=\"1\" >\n" + ("\n" + ("  <header>\n" + ("  </header>\n" + ("\n" + ("  <nodes>\n" + ("    <start id=\"1\" name=\"Start\" />\n" + ("    <end id=\"2\" name=\"End\" />\n" + ("    <timerNode id=\"3\" name=\"Timer\" delay=\"800msdss\" period=\"200mssds\" />\n" + ("  </nodes>\n" + ("\n" + ("  <connections>\n" + ("    <connection from=\"1\" to=\"3\" />\n" + ("    <connection from=\"3\" to=\"2\" />\n" + ("  </connections>\n" + ("\n" + "</process>")))))))))))))))))))));
        builder.addRuleFlow(source);
        Assert.assertEquals(2, builder.getErrors().size());
        for (DroolsError error : builder.getErrors().getErrors()) {
            ProcessTimerTest.logger.error(error.toString());
        }
    }

    @Test
    public void testOnEntryTimerExecuted() throws Exception {
        Reader source = new StringReader(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ("<process xmlns=\"http://drools.org/drools-5.0/process\"\n" + ("         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + ("         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" + ("         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.timer\" package-name=\"org.drools\" version=\"1\" >\n" + ("\n" + ("  <header>\n" + ("    <globals>\n" + ("      <global identifier=\"myList\" type=\"java.util.List\" />\n" + ("    </globals>\n" + ("  </header>\n" + ("\n" + ("  <nodes>\n" + ("    <start id=\"1\" name=\"Start\" />\n" + ("    <milestone id=\"2\" name=\"Wait\" >\n" + ("      <timers>\n" + ("        <timer id=\"1\" delay=\"300\" >\n" + ("          <action type=\"expression\" dialect=\"java\" >myList.add(\"Executing timer\");</action>\n" + ("        </timer>\n" + ("      </timers>\n" + ("      <constraint type=\"rule\" dialect=\"mvel\" >eval(false)</constraint>\n" + ("    </milestone>\n" + ("    <end id=\"3\" name=\"End\" />\n" + ("  </nodes>\n" + ("\n" + ("  <connections>\n" + ("    <connection from=\"1\" to=\"2\" />\n" + ("    <connection from=\"2\" to=\"3\" />\n" + ("  </connections>\n" + ("\n" + "</process>")))))))))))))))))))))))))))))));
        builder.addRuleFlow(source);
        StatefulKnowledgeSession session = createKieSession(builder.getPackage());
        List<String> myList = new ArrayList<String>();
        session.setGlobal("myList", myList);
        ProcessInstance processInstance = ((ProcessInstance) (session.startProcess("org.drools.timer")));
        Assert.assertEquals(0, myList.size());
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        Assert.assertEquals(1, ((InternalProcessRuntime) (((InternalWorkingMemory) (session)).getProcessRuntime())).getTimerManager().getTimers().size());
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            // do nothing
        }
        Assert.assertEquals(1, myList.size());
        session.dispose();
    }

    @Test
    public void testOnEntryTimerVariableExecuted() throws Exception {
        Reader source = new StringReader(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ("<process xmlns=\"http://drools.org/drools-5.0/process\"\n" + ("         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + ("         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" + ("         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.timer\" package-name=\"org.drools\" version=\"1\" >\n" + ("\n" + ("  <header>\n" + ("    <globals>\n" + ("      <global identifier=\"myList\" type=\"java.util.List\" />\n" + ("    </globals>\n" + ("    <variables>\n" + ("      <variable name=\"x\" >\n" + ("        <type name=\"org.drools.core.process.core.datatype.impl.type.IntegerDataType\" />\n" + ("      </variable>\n" + ("    </variables>\n" + ("  </header>\n" + ("\n" + ("  <nodes>\n" + ("    <start id=\"1\" name=\"Start\" />\n" + ("    <milestone id=\"2\" name=\"Wait\" >\n" + ("      <timers>\n" + ("        <timer id=\"1\" delay=\"#{x}\" >\n" + ("          <action type=\"expression\" dialect=\"java\" >myList.add(\"Executing timer\");</action>\n" + ("        </timer>\n" + ("      </timers>\n" + ("      <constraint type=\"rule\" dialect=\"mvel\" >eval(false)</constraint>\n" + ("    </milestone>\n" + ("    <end id=\"3\" name=\"End\" />\n" + ("  </nodes>\n" + ("\n" + ("  <connections>\n" + ("    <connection from=\"1\" to=\"2\" />\n" + ("    <connection from=\"2\" to=\"3\" />\n" + ("  </connections>\n" + ("\n" + "</process>"))))))))))))))))))))))))))))))))))));
        builder.addRuleFlow(source);
        final StatefulKnowledgeSession session = createKieSession(builder.getPackage());
        List<String> myList = new ArrayList<String>();
        session.setGlobal("myList", myList);
        new Thread(new Runnable() {
            public void run() {
                session.fireUntilHalt();
            }
        }).start();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", 300);
        ProcessInstance processInstance = ((ProcessInstance) (session.startProcess("org.drools.timer", params)));
        Assert.assertEquals(0, myList.size());
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        Assert.assertEquals(1, ((InternalProcessRuntime) (((InternalWorkingMemory) (session)).getProcessRuntime())).getTimerManager().getTimers().size());
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            // do nothing
        }
        Assert.assertEquals(1, myList.size());
        session.dispose();
    }

    @Test
    public void testOnEntryTimerWorkItemExecuted() throws Exception {
        Reader source = new StringReader(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ("<process xmlns=\"http://drools.org/drools-5.0/process\"\n" + ("         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + ("         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" + ("         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.timer\" package-name=\"org.drools\" version=\"1\" >\n" + ("\n" + ("  <header>\n" + ("    <globals>\n" + ("      <global identifier=\"myList\" type=\"java.util.List\" />\n" + ("    </globals>\n" + ("  </header>\n" + ("\n" + ("  <nodes>\n" + ("    <start id=\"1\" name=\"Start\" />\n" + ("    <workItem id=\"2\" name=\"Work\" >\n" + ("      <timers>\n" + ("        <timer id=\"1\" delay=\"300\" >\n" + ("          <action type=\"expression\" dialect=\"java\" >myList.add(\"Executing timer\");</action>\n" + ("        </timer>\n" + ("      </timers>\n" + ("      <work name=\"Human Task\" >\n" + ("      </work>\n" + ("    </workItem>\n" + ("    <end id=\"3\" name=\"End\" />\n" + ("  </nodes>\n" + ("\n" + ("  <connections>\n" + ("    <connection from=\"1\" to=\"2\" />\n" + ("    <connection from=\"2\" to=\"3\" />\n" + ("  </connections>\n" + ("\n" + "</process>"))))))))))))))))))))))))))))))));
        builder.addRuleFlow(source);
        StatefulKnowledgeSession session = createKieSession(builder.getPackage());
        List<String> myList = new ArrayList<String>();
        session.setGlobal("myList", myList);
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler());
        ProcessInstance processInstance = ((ProcessInstance) (session.startProcess("org.drools.timer")));
        Assert.assertEquals(0, myList.size());
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        Assert.assertEquals(1, ((InternalProcessRuntime) (((InternalWorkingMemory) (session)).getProcessRuntime())).getTimerManager().getTimers().size());
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            // do nothing
        }
        Assert.assertEquals(1, myList.size());
        session.dispose();
    }

    @Test
    public void testIncorrectOnEntryTimer() throws Exception {
        Reader source = new StringReader(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ("<process xmlns=\"http://drools.org/drools-5.0/process\"\n" + ("         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + ("         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" + ("         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.timer\" package-name=\"org.drools\" version=\"1\" >\n" + ("\n" + ("  <header>\n" + ("    <globals>\n" + ("      <global identifier=\"myList\" type=\"java.util.List\" />\n" + ("    </globals>\n" + ("  </header>\n" + ("\n" + ("  <nodes>\n" + ("    <start id=\"1\" name=\"Start\" />\n" + ("    <milestone id=\"2\" name=\"Wait\" >\n" + ("      <timers>\n" + ("        <timer id=\"1\" delay=\"300asdf\" period=\"asfd\" >\n" + ("          <action type=\"expression\" dialect=\"java\" >myList.add(\"Executing timer\");</action>\n" + ("        </timer>\n" + ("      </timers>\n" + ("      <constraint type=\"rule\" dialect=\"mvel\" >eval(false)</constraint>\n" + ("    </milestone>\n" + ("    <end id=\"3\" name=\"End\" />\n" + ("  </nodes>\n" + ("\n" + ("  <connections>\n" + ("    <connection from=\"1\" to=\"2\" />\n" + ("    <connection from=\"2\" to=\"3\" />\n" + ("  </connections>\n" + ("\n" + "</process>")))))))))))))))))))))))))))))));
        builder.addRuleFlow(source);
        Assert.assertEquals(2, builder.getErrors().size());
        for (DroolsError error : builder.getErrors().getErrors()) {
            ProcessTimerTest.logger.error(error.toString());
        }
    }

    @Test
    public void testOnEntryTimerExecutedMultipleTimes() throws Exception {
        Reader source = new StringReader(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ("<process xmlns=\"http://drools.org/drools-5.0/process\"\n" + ("         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + ("         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" + ("         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.timer\" package-name=\"org.drools\" version=\"1\" >\n" + ("\n" + ("  <header>\n" + ("    <globals>\n" + ("      <global identifier=\"myList\" type=\"java.util.List\" />\n" + ("    </globals>\n" + ("  </header>\n" + ("\n" + ("  <nodes>\n" + ("    <start id=\"1\" name=\"Start\" />\n" + ("    <milestone id=\"2\" name=\"Wait\" >\n" + ("      <timers>\n" + ("        <timer id=\"1\" delay=\"300\" period =\"200\" >\n" + ("          <action type=\"expression\" dialect=\"java\" >myList.add(\"Executing timer\");</action>\n" + ("        </timer>\n" + ("      </timers>\n" + ("      <constraint type=\"rule\" dialect=\"mvel\" >eval(false)</constraint>\n" + ("    </milestone>\n" + ("    <end id=\"3\" name=\"End\" />\n" + ("  </nodes>\n" + ("\n" + ("  <connections>\n" + ("    <connection from=\"1\" to=\"2\" />\n" + ("    <connection from=\"2\" to=\"3\" />\n" + ("  </connections>\n" + ("\n" + "</process>")))))))))))))))))))))))))))))));
        builder.addRuleFlow(source);
        StatefulKnowledgeSession session = createKieSession(builder.getPackage());
        List<String> myList = new ArrayList<String>();
        session.setGlobal("myList", myList);
        ProcessInstance processInstance = ((ProcessInstance) (session.startProcess("org.drools.timer")));
        Assert.assertEquals(0, myList.size());
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        Assert.assertEquals(1, ((InternalProcessRuntime) (((InternalWorkingMemory) (session)).getProcessRuntime())).getTimerManager().getTimers().size());
        session.halt();
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            // do nothing
        }
        Assert.assertEquals(2, myList.size());
        session.dispose();
    }

    @Test
    public void testMultipleTimers() throws Exception {
        Reader source = new StringReader(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ("<process xmlns=\"http://drools.org/drools-5.0/process\"\n" + ("         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + ("         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" + ("         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.timer\" package-name=\"org.drools\" version=\"1\" >\n" + ("\n" + ("  <header>\n" + ("    <globals>\n" + ("      <global identifier=\"myList\" type=\"java.util.List\" />\n" + ("    </globals>\n" + ("  </header>\n" + ("\n" + ("  <nodes>\n" + ("    <start id=\"1\" name=\"Start\" />\n" + ("    <milestone id=\"2\" name=\"Wait\" >\n" + ("      <timers>\n" + ("        <timer id=\"1\" delay=\"600\" >\n" + ("          <action type=\"expression\" dialect=\"java\" >myList.add(\"Executing timer1\");</action>\n" + ("        </timer>\n" + ("        <timer id=\"2\" delay=\"200\" >\n" + ("          <action type=\"expression\" dialect=\"java\" >myList.add(\"Executing timer2\");</action>\n" + ("        </timer>\n" + ("      </timers>\n" + ("      <constraint type=\"rule\" dialect=\"mvel\" >eval(false)</constraint>\n" + ("    </milestone>\n" + ("    <end id=\"3\" name=\"End\" />\n" + ("  </nodes>\n" + ("\n" + ("  <connections>\n" + ("    <connection from=\"1\" to=\"2\" />\n" + ("    <connection from=\"2\" to=\"3\" />\n" + ("  </connections>\n" + ("\n" + "</process>"))))))))))))))))))))))))))))))))));
        builder.addRuleFlow(source);
        final StatefulKnowledgeSession session;
        {
            InternalKnowledgePackage pkg = builder.getPackage();
            KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
            kbase.addKnowledgePackages(((Collection) (Arrays.asList(builder.getPackages()))));
            SessionConfiguration conf = SessionConfiguration.newInstance();
            conf.setOption(ClockTypeOption.get(ClockType.PSEUDO_CLOCK.getId()));
            session = kbase.newStatefulKnowledgeSession(conf, null);
        }
        SessionPseudoClock clock = ((SessionPseudoClock) (session.getSessionClock()));
        clock.advanceTime(300, TimeUnit.MILLISECONDS);
        List<String> myList = new ArrayList<String>();
        session.setGlobal("myList", myList);
        ProcessInstance processInstance = ((ProcessInstance) (session.startProcess("org.drools.timer")));
        Assert.assertEquals(0, myList.size());
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        Assert.assertEquals(2, ((InternalProcessRuntime) (((InternalWorkingMemory) (session)).getProcessRuntime())).getTimerManager().getTimers().size());
        clock = ((SessionPseudoClock) (session.getSessionClock()));
        clock.advanceTime(500, TimeUnit.MILLISECONDS);
        Assert.assertEquals(1, myList.size());
        Assert.assertEquals("Executing timer2", myList.get(0));
        clock.advanceTime(500, TimeUnit.MILLISECONDS);
        Assert.assertEquals(2, myList.size());
        session.dispose();
    }

    @Test
    public void testOnEntryTimerCancelled() throws Exception {
        Reader source = new StringReader(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ("<process xmlns=\"http://drools.org/drools-5.0/process\"\n" + ("         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + ("         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" + ("         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.timer\" package-name=\"org.drools\" version=\"1\" >\n" + ("\n" + ("  <header>\n" + ("    <globals>\n" + ("      <global identifier=\"myList\" type=\"java.util.List\" />\n" + ("    </globals>\n" + ("  </header>\n" + ("\n" + ("  <nodes>\n" + ("    <start id=\"1\" name=\"Start\" />\n" + ("    <milestone id=\"2\" name=\"Wait\" >\n" + ("      <timers>\n" + ("        <timer id=\"1\" delay=\"2000\" >\n" + ("          <action type=\"expression\" dialect=\"java\" >myList.add(\"Executing timer\");</action>\n" + ("        </timer>\n" + ("      </timers>\n" + ("      <constraint type=\"rule\" dialect=\"mvel\" >org.jbpm.integrationtests.test.Message( )</constraint>\n" + ("    </milestone>\n" + ("    <end id=\"3\" name=\"End\" />\n" + ("  </nodes>\n" + ("\n" + ("  <connections>\n" + ("    <connection from=\"1\" to=\"2\" />\n" + ("    <connection from=\"2\" to=\"3\" />\n" + ("  </connections>\n" + ("\n" + "</process>")))))))))))))))))))))))))))))));
        builder.addRuleFlow(source);
        StatefulKnowledgeSession session = createKieSession(builder.getPackage());
        List<String> myList = new ArrayList<String>();
        session.setGlobal("myList", myList);
        ProcessInstance processInstance = ((ProcessInstance) (session.startProcess("org.drools.timer")));
        Assert.assertEquals(0, myList.size());
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        Assert.assertEquals(1, ((InternalProcessRuntime) (((InternalWorkingMemory) (session)).getProcessRuntime())).getTimerManager().getTimers().size());
        session.insert(new Message());
        session.fireAllRules();
        Assert.assertEquals(0, myList.size());
        Assert.assertEquals(0, ((InternalProcessRuntime) (((InternalWorkingMemory) (session)).getProcessRuntime())).getTimerManager().getTimers().size());
        session.dispose();
    }
}

