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


package org.jbpm.persistence.session;

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.After;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import java.util.Collection;
import org.kie.api.runtime.Environment;
import java.util.HashMap;
import javax.naming.InitialContext;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.api.KieBaseConfiguration;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.internal.io.ResourceFactory;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.junit.Test;
import org.junit.rules.TestName;
import org.jbpm.persistence.session.objects.TestWorkItemHandler;
import javax.transaction.UserTransaction;
import org.kie.api.runtime.process.WorkItem;

@RunWith(value = Parameterized.class)
public class PersistentStatefulSessionTest extends AbstractBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(PersistentStatefulSessionTest.class);

    private HashMap<String, Object> context;

    private Environment env;

    public PersistentStatefulSessionTest(boolean locking) {
        this.useLocking = locking;
    }

    @Parameters
    public static Collection<Object[]> persistence() {
        Object[][] data = new Object[][]{ new Object[]{ false } , new Object[]{ true } };
        return Arrays.asList(data);
    }

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setUp() throws Exception {
        String methodName = testName.getMethodName();
        context = setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME);
        env = createEnvironment(context);
        if (useLocking) {
            env.set(EnvironmentName.USE_PESSIMISTIC_LOCKING, true);
        } 
    }

    @After
    public void tearDown() throws Exception {
        cleanUp(context);
    }

    private static String ruleString = "" + ("package org.drools.test\n" + ("global java.util.List list\n" + ("rule rule1\n" + ("when\n" + ("  Integer($i : intValue > 0)\n" + ("then\n" + ("  list.add( $i );\n" + ("end\n" + "\n"))))))));

    @Test
    public void testLocalTransactionPerStatement() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(PersistentStatefulSessionTest.ruleString.getBytes()), ResourceType.DRL);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        } 
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        List<?> list = new ArrayList<Object>();
        ksession.setGlobal("list", list);
        ksession.insert(1);
        ksession.insert(2);
        ksession.insert(3);
        ksession.fireAllRules();
        assertEquals(3, list.size());
    }

    @Test
    public void testUserTransactions() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(PersistentStatefulSessionTest.ruleString.getBytes()), ResourceType.DRL);
        KieBaseConfiguration kBaseConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        // ((RuleBaseConfiguration)kBaseConf).setPhreakEnabled(false);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kBaseConf);
        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        } 
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        UserTransaction ut = ((UserTransaction) (new InitialContext().lookup("java:comp/UserTransaction")));
        ut.begin();
        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        ut.commit();
        List<?> list = new ArrayList<Object>();
        // insert and commit
        ut = ((UserTransaction) (new InitialContext().lookup("java:comp/UserTransaction")));
        ut.begin();
        ksession.setGlobal("list", list);
        ksession.insert(1);
        ksession.insert(2);
        ut.commit();
        // insert and rollback
        ut = ((UserTransaction) (new InitialContext().lookup("java:comp/UserTransaction")));
        ut.begin();
        ksession.insert(3);
        ut.rollback();
        // check we rolled back the state changes from the 3rd insert
        ut = ((UserTransaction) (new InitialContext().lookup("java:comp/UserTransaction")));
        ut.begin();
        ksession.fireAllRules();
        ut.commit();
        assertEquals(2, list.size());
        // insert and commit
        ut = ((UserTransaction) (new InitialContext().lookup("java:comp/UserTransaction")));
        ut.begin();
        ksession.insert(3);
        ksession.insert(4);
        ut.commit();
        // rollback again, this is testing that we can do consecutive rollbacks and commits without issue
        ut = ((UserTransaction) (new InitialContext().lookup("java:comp/UserTransaction")));
        ut.begin();
        ksession.insert(5);
        ksession.insert(6);
        ut.rollback();
        ksession.fireAllRules();
        assertEquals(4, list.size());
        // now load the ksession
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(ksession.getIdentifier(), kbase, null, env);
        ut = ((UserTransaction) (new InitialContext().lookup("java:comp/UserTransaction")));
        ut.begin();
        ksession.insert(7);
        ksession.insert(8);
        ut.commit();
        ksession.fireAllRules();
        assertEquals(6, list.size());
    }

    @Test
    public void testPersistenceWorkItems() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(new org.drools.core.io.impl.ClassPathResource("WorkItemsProcess.rf"), ResourceType.DRF);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        int origNumObjects = ksession.getObjects().size();
        long id = ksession.getIdentifier();
        ProcessInstance processInstance = ksession.startProcess("org.drools.test.TestProcess");
        ksession.insert("TestString");
        PersistentStatefulSessionTest.logger.debug("Started process instance {}", processInstance.getId());
        TestWorkItemHandler handler = TestWorkItemHandler.getInstance();
        WorkItem workItem = handler.getWorkItem();
        assertNotNull(workItem);
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        workItem = handler.getWorkItem();
        assertNotNull(workItem);
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        workItem = handler.getWorkItem();
        assertNotNull(workItem);
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        workItem = handler.getWorkItem();
        assertNull(workItem);
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertEquals((origNumObjects + 1), ksession.getObjects().size());
        for (Object o : ksession.getObjects()) {
            PersistentStatefulSessionTest.logger.debug(o.toString());
        }
        assertNull(processInstance);
    }

    @Test
    public void testPersistenceWorkItems2() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(new org.drools.core.io.impl.ClassPathResource("WorkItemsProcess.rf"), ResourceType.DRF);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        long id = ksession.getIdentifier();
        UserTransaction ut = ((UserTransaction) (new InitialContext().lookup("java:comp/UserTransaction")));
        ut.begin();
        ProcessInstance processInstance = ksession.startProcess("org.drools.test.TestProcess");
        ksession.insert("TestString");
        PersistentStatefulSessionTest.logger.debug("Started process instance {}", processInstance.getId());
        TestWorkItemHandler handler = TestWorkItemHandler.getInstance();
        WorkItem workItem = handler.getWorkItem();
        assertNotNull(workItem);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        workItem = handler.getWorkItem();
        assertNotNull(workItem);
        ut.commit();
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        workItem = handler.getWorkItem();
        assertNotNull(workItem);
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        workItem = handler.getWorkItem();
        assertNull(workItem);
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertEquals(1, ksession.getObjects().size());
        for (Object o : ksession.getObjects()) {
            PersistentStatefulSessionTest.logger.debug(o.toString());
        }
        assertNull(processInstance);
    }

    @Test
    public void testPersistenceWorkItems3() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(new org.drools.core.io.impl.ClassPathResource("WorkItemsProcess.rf"), ResourceType.DRF);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        ksession.getWorkItemManager().registerWorkItemHandler("MyWork", new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler());
        ProcessInstance processInstance = ksession.startProcess("org.drools.test.TestProcess");
        ksession.insert("TestString");
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    @Test
    public void testPersistenceState() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(new org.drools.core.io.impl.ClassPathResource("StateProcess.rf"), ResourceType.DRF);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        long id = ksession.getIdentifier();
        ProcessInstance processInstance = ksession.startProcess("org.drools.test.TestProcess");
        PersistentStatefulSessionTest.logger.debug("Started process instance {}", processInstance.getId());
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        ksession.insert(new ArrayList<Object>());
        ksession.fireAllRules();
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNull(processInstance);
    }

    @Test
    public void testPersistenceRuleSet() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(new org.drools.core.io.impl.ClassPathResource("RuleSetProcess.rf"), ResourceType.DRF);
        kbuilder.add(new org.drools.core.io.impl.ClassPathResource("RuleSetRules.drl"), ResourceType.DRL);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        long id = ksession.getIdentifier();
        ksession.insert(new ArrayList<Object>());
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        ProcessInstance processInstance = ksession.startProcess("org.drools.test.TestProcess");
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        ksession.fireAllRules();
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNull(processInstance);
    }

    @Test
    public void testPersistenceEvents() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(new org.drools.core.io.impl.ClassPathResource("EventsProcess.rf"), ResourceType.DRF);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        long id = ksession.getIdentifier();
        ProcessInstance processInstance = ksession.startProcess("org.drools.test.TestProcess");
        PersistentStatefulSessionTest.logger.debug("Started process instance {}", processInstance.getId());
        TestWorkItemHandler handler = TestWorkItemHandler.getInstance();
        WorkItem workItem = handler.getWorkItem();
        assertNotNull(workItem);
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        ksession.signalEvent("MyEvent1", null, processInstance.getId());
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        ksession.signalEvent("MyEvent2", null, processInstance.getId());
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNull(processInstance);
    }

    @Test
    public void testProcessListener() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(new org.drools.core.io.impl.ClassPathResource("WorkItemsProcess.rf"), ResourceType.DRF);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        final List<ProcessEvent> events = new ArrayList<ProcessEvent>();
        ProcessEventListener listener = new ProcessEventListener() {
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                PersistentStatefulSessionTest.logger.debug("After node left: {}", event.getNodeInstance().getNodeName());
                events.add(event);
            }

            public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
                PersistentStatefulSessionTest.logger.debug("After node triggered: {}", event.getNodeInstance().getNodeName());
                events.add(event);
            }

            public void afterProcessCompleted(ProcessCompletedEvent event) {
                PersistentStatefulSessionTest.logger.debug("After process completed");
                events.add(event);
            }

            public void afterProcessStarted(ProcessStartedEvent event) {
                PersistentStatefulSessionTest.logger.debug("After process started");
                events.add(event);
            }

            public void beforeNodeLeft(ProcessNodeLeftEvent event) {
                PersistentStatefulSessionTest.logger.debug("Before node left: {}", event.getNodeInstance().getNodeName());
                events.add(event);
            }

            public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
                PersistentStatefulSessionTest.logger.debug("Before node triggered: {}", event.getNodeInstance().getNodeName());
                events.add(event);
            }

            public void beforeProcessCompleted(ProcessCompletedEvent event) {
                PersistentStatefulSessionTest.logger.debug("Before process completed");
                events.add(event);
            }

            public void beforeProcessStarted(ProcessStartedEvent event) {
                PersistentStatefulSessionTest.logger.debug("Before process started");
                events.add(event);
            }

            public void afterVariableChanged(ProcessVariableChangedEvent event) {
                PersistentStatefulSessionTest.logger.debug("After Variable Changed");
                events.add(event);
            }

            public void beforeVariableChanged(ProcessVariableChangedEvent event) {
                PersistentStatefulSessionTest.logger.debug("Before Variable Changed");
                events.add(event);
            }
        };
        ksession.addEventListener(listener);
        ProcessInstance processInstance = ksession.startProcess("org.drools.test.TestProcess");
        PersistentStatefulSessionTest.logger.debug("Started process instance {}", processInstance.getId());
        assertEquals(12, events.size());
        assertTrue(((events.get(0)) instanceof ProcessStartedEvent));
        assertTrue(((events.get(1)) instanceof ProcessNodeTriggeredEvent));
        assertTrue(((events.get(2)) instanceof ProcessNodeLeftEvent));
        assertTrue(((events.get(3)) instanceof ProcessNodeTriggeredEvent));
        assertTrue(((events.get(4)) instanceof ProcessNodeLeftEvent));
        assertTrue(((events.get(5)) instanceof ProcessNodeTriggeredEvent));
        assertTrue(((events.get(6)) instanceof ProcessNodeTriggeredEvent));
        assertTrue(((events.get(7)) instanceof ProcessNodeLeftEvent));
        assertTrue(((events.get(8)) instanceof ProcessNodeTriggeredEvent));
        assertTrue(((events.get(9)) instanceof ProcessNodeLeftEvent));
        assertTrue(((events.get(10)) instanceof ProcessNodeTriggeredEvent));
        assertTrue(((events.get(11)) instanceof ProcessStartedEvent));
        ksession.removeEventListener(listener);
        events.clear();
        processInstance = ksession.startProcess("org.drools.test.TestProcess");
        PersistentStatefulSessionTest.logger.debug("Started process instance {}", processInstance.getId());
        assertTrue(events.isEmpty());
    }

    @Test
    public void testPersistenceSubProcess() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(new org.drools.core.io.impl.ClassPathResource("SuperProcess.rf"), ResourceType.DRF);
        kbuilder.add(new org.drools.core.io.impl.ClassPathResource("SubProcess.rf"), ResourceType.DRF);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        long id = ksession.getIdentifier();
        ProcessInstance processInstance = ksession.startProcess("com.sample.SuperProcess");
        PersistentStatefulSessionTest.logger.debug("Started process instance {}", processInstance.getId());
        TestWorkItemHandler handler = TestWorkItemHandler.getInstance();
        WorkItem workItem = handler.getWorkItem();
        assertNotNull(workItem);
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        workItem = handler.getWorkItem();
        assertNotNull(workItem);
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        workItem = handler.getWorkItem();
        assertNull(workItem);
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNull("Process did not complete.", processInstance);
    }

    @Test
    public void testPersistenceVariables() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(new org.drools.core.io.impl.ClassPathResource("VariablesProcess.rf"), ResourceType.DRF);
        for (KnowledgeBuilderError error : kbuilder.getErrors()) {
            PersistentStatefulSessionTest.logger.debug(error.toString());
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        long id = ksession.getIdentifier();
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("name", "John Doe");
        ProcessInstance processInstance = ksession.startProcess("org.drools.test.TestProcess", parameters);
        TestWorkItemHandler handler = TestWorkItemHandler.getInstance();
        WorkItem workItem = handler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("John Doe", workItem.getParameter("name"));
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        workItem = handler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("John Doe", workItem.getParameter("text"));
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        workItem = handler.getWorkItem();
        assertNull(workItem);
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, null, env);
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNull(processInstance);
    }

    @Test
    public void testSetFocus() {
        String str = "";
        str += "package org.drools.test\n";
        str += "global java.util.List list\n";
        str += "rule rule1\n";
        str += "agenda-group \"badfocus\"";
        str += "when\n";
        str += "  Integer(intValue > 0)\n";
        str += "then\n";
        str += "  list.add( 1 );\n";
        str += "end\n";
        str += "\n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        } 
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        List<?> list = new ArrayList<Object>();
        ksession.setGlobal("list", list);
        ksession.insert(1);
        ksession.insert(2);
        ksession.insert(3);
        ksession.getAgenda().getAgendaGroup("badfocus").setFocus();
        ksession.fireAllRules();
        assertEquals(3, list.size());
    }
}

