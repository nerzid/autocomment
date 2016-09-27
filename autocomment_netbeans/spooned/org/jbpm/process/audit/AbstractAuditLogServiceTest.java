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


package org.jbpm.process.audit;

import org.jbpm.test.util.AbstractBaseTest;
import java.util.ArrayList;
import org.junit.Assert;
import org.kie.api.runtime.Environment;
import java.util.HashMap;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.Properties;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

/**
 * This class tests the following classes:
 * <ul>
 * <li>JPAWorkingMemoryDbLogger</li>
 * <li>AuditLogService</li>
 * </ul>
 */
public abstract class AbstractAuditLogServiceTest extends AbstractBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(AbstractAuditLogServiceTest.class);

    public static KnowledgeBase createKnowledgeBase() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(new org.drools.core.io.impl.ClassPathResource("ruleflow.rf"), ResourceType.DRF);
        kbuilder.add(new org.drools.core.io.impl.ClassPathResource("ruleflow2.rf"), ResourceType.DRF);
        kbuilder.add(new org.drools.core.io.impl.ClassPathResource("ruleflow3.rf"), ResourceType.DRF);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        return kbase;
    }

    public static StatefulKnowledgeSession createKieSession(KieBase kbase, Environment env) {
        // create a new session
        Properties properties = new Properties();
        properties.put("drools.processInstanceManagerFactory", "org.jbpm.persistence.processinstance.JPAProcessInstanceManagerFactory");
        properties.put("drools.processSignalManagerFactory", "org.jbpm.persistence.processinstance.JPASignalManagerFactory");
        KieSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);
        StatefulKnowledgeSession session = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, config, env);
        return session;
    }

    public static void runTestLogger1(KieSession session, AuditLogService auditLogService) throws Exception {
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler());
        // record the initial count to compare to later
        List<ProcessInstanceLog> processInstances = auditLogService.findProcessInstances("com.sample.ruleflow");
        int initialProcessInstanceSize = processInstances.size();
        // start process instance
        long processInstanceId = session.startProcess("com.sample.ruleflow").getId();
        AbstractAuditLogServiceTest.logger.debug("Checking process instances for process 'com.sample.ruleflow'");
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow");
        Assert.assertEquals((initialProcessInstanceSize + 1), processInstances.size());
        ProcessInstanceLog processInstance = processInstances.get(initialProcessInstanceSize);
        AbstractAuditLogServiceTest.logger.debug("{} -> {} - {}", processInstance.toString(), processInstance.getStart(), processInstance.getEnd());
        Assert.assertNotNull(processInstance.getStart());
        Assert.assertNotNull("ProcessInstanceLog does not contain end date.", processInstance.getEnd());
        Assert.assertEquals(processInstanceId, processInstance.getProcessInstanceId().longValue());
        Assert.assertEquals("com.sample.ruleflow", processInstance.getProcessId());
        List<NodeInstanceLog> nodeInstances = auditLogService.findNodeInstances(processInstanceId);
        Assert.assertEquals(6, nodeInstances.size());
        for (NodeInstanceLog nodeInstance : nodeInstances) {
            AbstractAuditLogServiceTest.logger.debug(nodeInstance.toString());
            Assert.assertEquals(processInstanceId, processInstance.getProcessInstanceId().longValue());
            Assert.assertEquals("com.sample.ruleflow", processInstance.getProcessId());
            Assert.assertNotNull(nodeInstance.getDate());
        }
        auditLogService.clear();
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow");
        Assert.assertTrue(processInstances.isEmpty());
    }

    public static void runTestLogger2(KieSession session, AuditLogService auditLogService) {
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler());
        // record the initial count to compare to later
        List<ProcessInstanceLog> processInstances = auditLogService.findProcessInstances("com.sample.ruleflow");
        int initialProcessInstanceSize = processInstances.size();
        // start process instance
        session.startProcess("com.sample.ruleflow");
        session.startProcess("com.sample.ruleflow");
        AbstractAuditLogServiceTest.logger.debug("Checking process instances for process 'com.sample.ruleflow'");
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow");
        Assert.assertEquals((initialProcessInstanceSize + 2), processInstances.size());
        for (ProcessInstanceLog processInstance : processInstances) {
            AbstractAuditLogServiceTest.logger.debug("{} -> {} - {}", processInstance.toString(), processInstance.getStart(), processInstance.getEnd());
            List<NodeInstanceLog> nodeInstances = auditLogService.findNodeInstances(processInstance.getProcessInstanceId());
            for (NodeInstanceLog nodeInstance : nodeInstances) {
                AbstractAuditLogServiceTest.logger.debug("{} -> {}", nodeInstance.toString(), nodeInstance.getDate());
            }
            Assert.assertEquals(6, nodeInstances.size());
        }
        auditLogService.clear();
    }

    public static void runTestLogger3(KieSession session, AuditLogService auditLogService) {
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler());
        // record the initial count to compare to later
        List<ProcessInstanceLog> processInstances = auditLogService.findProcessInstances("com.sample.ruleflow");
        int initialProcessInstanceSize = processInstances.size();
        // start process instance
        long processInstanceId = session.startProcess("com.sample.ruleflow2").getId();
        AbstractAuditLogServiceTest.logger.debug("Checking process instances for process 'com.sample.ruleflow2'");
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow2");
        Assert.assertEquals((initialProcessInstanceSize + 1), processInstances.size());
        ProcessInstanceLog processInstance = processInstances.get(initialProcessInstanceSize);
        AbstractAuditLogServiceTest.logger.debug("{} -> {} - {}", processInstance.toString(), processInstance.getStart(), processInstance.getEnd());
        Assert.assertNotNull(processInstance.getStart());
        Assert.assertNotNull("ProcessInstanceLog does not contain end date.", processInstance.getEnd());
        Assert.assertEquals(processInstanceId, processInstance.getProcessInstanceId().longValue());
        Assert.assertEquals("com.sample.ruleflow2", processInstance.getProcessId());
        List<NodeInstanceLog> nodeInstances = auditLogService.findNodeInstances(processInstanceId);
        for (NodeInstanceLog nodeInstance : nodeInstances) {
            AbstractAuditLogServiceTest.logger.debug("{} -> {}", nodeInstance.toString(), nodeInstance.getDate());
            Assert.assertEquals(processInstanceId, processInstance.getProcessInstanceId().longValue());
            Assert.assertEquals("com.sample.ruleflow2", processInstance.getProcessId());
            Assert.assertNotNull(nodeInstance.getDate());
        }
        Assert.assertEquals(14, nodeInstances.size());
        auditLogService.clear();
    }

    public static void runTestLogger4(KieSession session, AuditLogService auditLogService) throws Exception {
        final List<Long> workItemIds = new ArrayList<Long>();
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new WorkItemHandler() {
            public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
                workItemIds.add(workItem.getId());
            }

            public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
            }
        });
        // record the initial count to compare to later
        List<ProcessInstanceLog> processInstances = auditLogService.findProcessInstances("com.sample.ruleflow");
        int initialProcessInstanceSize = processInstances.size();
        // start process instance
        Map<String, Object> params = new HashMap<String, Object>();
        List<String> list = new ArrayList<String>();
        list.add("One");
        list.add("Two");
        list.add("Three");
        params.put("list", list);
        long processInstanceId = session.startProcess("com.sample.ruleflow3", params).getId();
        // Test findVariableInstancesByName* methods: check for variables (only) in active processes
        List<VariableInstanceLog> varLogs = auditLogService.findVariableInstancesByName("s", true);
        Assert.assertFalse(varLogs.isEmpty());
        Assert.assertEquals(1, varLogs.size());
        for (Long workItemId : workItemIds) {
            Map<String, Object> results = new HashMap<String, Object>();
            results.put("Result", "ResultValue");
            session.getWorkItemManager().completeWorkItem(workItemId, results);
        }
        AbstractAuditLogServiceTest.logger.debug("Checking process instances for process 'com.sample.ruleflow3'");
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow3");
        Assert.assertEquals((initialProcessInstanceSize + 1), processInstances.size());
        ProcessInstanceLog processInstance = processInstances.get(initialProcessInstanceSize);
        AbstractAuditLogServiceTest.logger.debug("{} -> {} - {}", processInstance.toString(), processInstance.getStart(), processInstance.getEnd());
        Assert.assertNotNull(processInstance.getStart());
        Assert.assertNotNull("ProcessInstanceLog does not contain end date.", processInstance.getEnd());
        Assert.assertEquals(processInstanceId, processInstance.getProcessInstanceId().longValue());
        Assert.assertEquals("com.sample.ruleflow3", processInstance.getProcessId());
        List<VariableInstanceLog> variableInstances = auditLogService.findVariableInstances(processInstanceId);
        Assert.assertEquals(11, variableInstances.size());
        for (VariableInstanceLog variableInstance : variableInstances) {
            AbstractAuditLogServiceTest.logger.debug(variableInstance.toString());
            Assert.assertEquals(processInstanceId, processInstance.getProcessInstanceId().longValue());
            Assert.assertEquals("com.sample.ruleflow3", processInstance.getProcessId());
            Assert.assertNotNull(variableInstance.getDate());
        }
        // Test findVariableInstancesByName* methods
        List<VariableInstanceLog> emptyVarLogs = auditLogService.findVariableInstancesByName("s", true);
        Assert.assertTrue(emptyVarLogs.isEmpty());
        for (VariableInstanceLog origVarLog : variableInstances) {
            varLogs = auditLogService.findVariableInstancesByName(origVarLog.getVariableId(), false);
            for (VariableInstanceLog varLog : varLogs) {
                Assert.assertEquals(origVarLog.getVariableId(), varLog.getVariableId());
            }
        }
        emptyVarLogs = auditLogService.findVariableInstancesByNameAndValue("s", "InitialValue", true);
        Assert.assertTrue(emptyVarLogs.isEmpty());
        String varId = "s";
        String varValue = "ResultValue";
        variableInstances = auditLogService.findVariableInstancesByNameAndValue(varId, varValue, false);
        Assert.assertEquals(3, variableInstances.size());
        VariableInstanceLog varLog = variableInstances.get(0);
        Assert.assertEquals(varId, varLog.getVariableId());
        Assert.assertEquals(varValue, varLog.getValue());
        auditLogService.clear();
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow3");
        Assert.assertTrue(processInstances.isEmpty());
    }

    public static void runTestLogger4LargeVariable(KieSession session, AuditLogService auditLogService) throws Exception {
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new WorkItemHandler() {
            public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
                Map<String, Object> results = new HashMap<String, Object>();
                results.put("Result", "ResultValue");
                manager.completeWorkItem(workItem.getId(), results);
            }

            public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
            }
        });
        // record the initial count to compare to later
        List<ProcessInstanceLog> processInstances = auditLogService.findProcessInstances("com.sample.ruleflow3");
        int initialProcessInstanceSize = processInstances.size();
        // start process instance
        Map<String, Object> params = new HashMap<String, Object>();
        List<String> list = new ArrayList<String>();
        list.add("One");
        list.add("Two");
        String three = "";
        for (int i = 0; i < 1024; i++) {
            three += "*";
        }
        list.add(three);
        params.put("list", list);
        long processInstanceId = session.startProcess("com.sample.ruleflow3", params).getId();
        AbstractAuditLogServiceTest.logger.debug("Checking process instances for process 'com.sample.ruleflow3'");
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow3");
        int expected = initialProcessInstanceSize + 1;
        Assert.assertEquals((((("[Expected " + expected) + " ProcessInstanceLog instances, not ") + (processInstances.size())) + "]"), expected, processInstances.size());
        ProcessInstanceLog processInstance = processInstances.get(initialProcessInstanceSize);
        AbstractAuditLogServiceTest.logger.debug("{} -> {} - {}", processInstance.toString(), processInstance.getStart(), processInstance.getEnd());
        Assert.assertNotNull(processInstance.getStart());
        Assert.assertNotNull("ProcessInstanceLog does not contain end date.", processInstance.getEnd());
        Assert.assertEquals(processInstanceId, processInstance.getProcessInstanceId().longValue());
        Assert.assertEquals("com.sample.ruleflow3", processInstance.getProcessId());
        List<VariableInstanceLog> variableInstances = auditLogService.findVariableInstances(processInstanceId);
        Assert.assertEquals(8, variableInstances.size());
        for (VariableInstanceLog variableInstance : variableInstances) {
            AbstractAuditLogServiceTest.logger.debug(variableInstance.toString());
            Assert.assertEquals(processInstanceId, processInstance.getProcessInstanceId().longValue());
            Assert.assertEquals("com.sample.ruleflow3", processInstance.getProcessId());
            Assert.assertNotNull(variableInstance.getDate());
        }
        auditLogService.clear();
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow3");
        Assert.assertTrue(processInstances.isEmpty());
    }

    public static void runTestLogger5(KieSession session, AuditLogService auditLogService) throws Exception {
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler());
        // record the initial count to compare to later
        List<ProcessInstanceLog> processInstances = auditLogService.findProcessInstances("com.sample.ruleflow");
        int initialProcessInstanceSize = processInstances.size();
        // start process instance
        long processInstanceId = session.startProcess("com.sample.ruleflow").getId();
        AbstractAuditLogServiceTest.logger.debug("Checking process instances for process 'com.sample.ruleflow'");
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow");
        Assert.assertEquals((initialProcessInstanceSize + 1), processInstances.size());
        ProcessInstanceLog processInstance = processInstances.get(initialProcessInstanceSize);
        AbstractAuditLogServiceTest.logger.debug("{} -> {} - {}", processInstance.toString(), processInstance.getStart(), processInstance.getEnd());
        Assert.assertNotNull(processInstance.getStart());
        Assert.assertNotNull(processInstance.getEnd());
        Assert.assertEquals(processInstanceId, processInstance.getProcessInstanceId().longValue());
        Assert.assertEquals("com.sample.ruleflow", processInstance.getProcessId());
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getStatus().intValue());
        List<NodeInstanceLog> nodeInstances = auditLogService.findNodeInstances(processInstanceId);
        Assert.assertEquals(6, nodeInstances.size());
        for (NodeInstanceLog nodeInstance : nodeInstances) {
            AbstractAuditLogServiceTest.logger.debug(nodeInstance.toString());
            Assert.assertEquals(processInstanceId, processInstance.getProcessInstanceId().longValue());
            Assert.assertEquals("com.sample.ruleflow", processInstance.getProcessId());
            Assert.assertNotNull(nodeInstance.getDate());
        }
        auditLogService.clear();
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow");
        Assert.assertTrue(processInstances.isEmpty());
    }

    public static void runTestLoggerWithCustomVariableLogLength(KieSession session, AuditLogService auditLogService) throws Exception {
        System.setProperty("org.jbpm.var.log.length", "15");
        final List<Long> workItemIds = new ArrayList<Long>();
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new WorkItemHandler() {
            public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
                workItemIds.add(workItem.getId());
            }

            public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
            }
        });
        // record the initial count to compare to later
        List<ProcessInstanceLog> processInstances = auditLogService.findProcessInstances("com.sample.ruleflow");
        int initialProcessInstanceSize = processInstances.size();
        processInstances = auditLogService.findActiveProcessInstances();
        int initialActiveProcessInstanceSize = processInstances.size();
        // prepare variable value
        String variableValue = "very short value that should be trimmed by custom variable log length";
        // start process instance
        Map<String, Object> params = new HashMap<String, Object>();
        List<String> list = new ArrayList<String>();
        list.add("One");
        list.add("Two");
        list.add("Three");
        params.put("list", list);
        params.put("s", variableValue);
        long processInstanceId = session.startProcess("com.sample.ruleflow3", params).getId();
        int numActiveProcesses = auditLogService.findActiveProcessInstances().size();
        Assert.assertEquals("find active processes did not work", (initialActiveProcessInstanceSize + 1), numActiveProcesses);
        // Test findVariableInstancesByName* methods: check for variables (only) in active processes
        List<VariableInstanceLog> varLogs = auditLogService.findVariableInstancesByName("s", true);
        Assert.assertFalse(varLogs.isEmpty());
        Assert.assertEquals(2, varLogs.size());
        VariableInstanceLog varLogS = varLogs.get(1);
        Assert.assertEquals(variableValue.substring(0, 15), varLogS.getValue());
        for (Long workItemId : workItemIds) {
            Map<String, Object> results = new HashMap<String, Object>();
            results.put("Result", "ResultValue");
            session.getWorkItemManager().completeWorkItem(workItemId, results);
        }
        AbstractAuditLogServiceTest.logger.debug("Checking process instances for process 'com.sample.ruleflow3'");
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow3");
        Assert.assertEquals((initialProcessInstanceSize + 1), processInstances.size());
        ProcessInstanceLog processInstance = processInstances.get(initialProcessInstanceSize);
        AbstractAuditLogServiceTest.logger.debug("{} -> {} - {}", processInstance.toString(), processInstance.getStart(), processInstance.getEnd());
        Assert.assertNotNull(processInstance.getStart());
        Assert.assertNotNull("ProcessInstanceLog does not contain end date.", processInstance.getEnd());
        Assert.assertEquals(processInstanceId, processInstance.getProcessInstanceId().longValue());
        Assert.assertEquals("com.sample.ruleflow3", processInstance.getProcessId());
        List<VariableInstanceLog> variableInstances = auditLogService.findVariableInstances(processInstanceId);
        Assert.assertEquals(12, variableInstances.size());
        for (VariableInstanceLog variableInstance : variableInstances) {
            AbstractAuditLogServiceTest.logger.debug(variableInstance.toString());
            Assert.assertEquals(processInstanceId, processInstance.getProcessInstanceId().longValue());
            Assert.assertEquals("com.sample.ruleflow3", processInstance.getProcessId());
            Assert.assertNotNull(variableInstance.getDate());
        }
        // Test findVariableInstancesByName* methods
        List<VariableInstanceLog> emptyVarLogs = auditLogService.findVariableInstancesByName("s", true);
        Assert.assertTrue(emptyVarLogs.isEmpty());
        for (VariableInstanceLog origVarLog : variableInstances) {
            varLogs = auditLogService.findVariableInstancesByName(origVarLog.getVariableId(), false);
            for (VariableInstanceLog varLog : varLogs) {
                Assert.assertEquals(origVarLog.getVariableId(), varLog.getVariableId());
            }
        }
        emptyVarLogs = auditLogService.findVariableInstancesByNameAndValue("s", "InitialValue", true);
        Assert.assertTrue(emptyVarLogs.isEmpty());
        String varId = "s";
        String varValue = "ResultValue";
        variableInstances = auditLogService.findVariableInstancesByNameAndValue(varId, varValue, false);
        Assert.assertEquals(3, variableInstances.size());
        VariableInstanceLog varLog = variableInstances.get(0);
        Assert.assertEquals(varId, varLog.getVariableId());
        Assert.assertEquals(varValue, varLog.getValue());
        auditLogService.clear();
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow3");
        Assert.assertTrue(processInstances.isEmpty());
    }

    public static void runTestLogger4WithCustomVariableIndexer(KieSession session, AuditLogService auditLogService) throws Exception {
        final List<Long> workItemIds = new ArrayList<Long>();
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new WorkItemHandler() {
            public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
                workItemIds.add(workItem.getId());
            }

            public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
            }
        });
        // record the initial count to compare to later
        List<ProcessInstanceLog> processInstances = auditLogService.findProcessInstances("com.sample.ruleflow");
        int initialProcessInstanceSize = processInstances.size();
        // start process instance
        Map<String, Object> params = new HashMap<String, Object>();
        List<String> list = new LinkedList<String>();
        list.add("One");
        list.add("Two");
        list.add("Three");
        params.put("list", list);
        long processInstanceId = session.startProcess("com.sample.ruleflow3", params).getId();
        // Test findVariableInstancesByName* methods: check for variables (only) in active processes
        List<VariableInstanceLog> varLogs = auditLogService.findVariableInstancesByName("s", true);
        Assert.assertFalse(varLogs.isEmpty());
        Assert.assertEquals(1, varLogs.size());
        for (Long workItemId : workItemIds) {
            Map<String, Object> results = new HashMap<String, Object>();
            results.put("Result", "ResultValue");
            session.getWorkItemManager().completeWorkItem(workItemId, results);
        }
        AbstractAuditLogServiceTest.logger.debug("Checking process instances for process 'com.sample.ruleflow3'");
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow3");
        Assert.assertEquals((initialProcessInstanceSize + 1), processInstances.size());
        ProcessInstanceLog processInstance = processInstances.get(initialProcessInstanceSize);
        AbstractAuditLogServiceTest.logger.debug("{} -> {} - {}", processInstance.toString(), processInstance.getStart(), processInstance.getEnd());
        Assert.assertNotNull(processInstance.getStart());
        Assert.assertNotNull("ProcessInstanceLog does not contain end date.", processInstance.getEnd());
        Assert.assertEquals(processInstanceId, processInstance.getProcessInstanceId().longValue());
        Assert.assertEquals("com.sample.ruleflow3", processInstance.getProcessId());
        List<VariableInstanceLog> variableInstances = auditLogService.findVariableInstances(processInstanceId);
        Assert.assertEquals(13, variableInstances.size());
        for (VariableInstanceLog variableInstance : variableInstances) {
            AbstractAuditLogServiceTest.logger.debug(variableInstance.toString());
            Assert.assertEquals(processInstanceId, processInstance.getProcessInstanceId().longValue());
            Assert.assertEquals("com.sample.ruleflow3", processInstance.getProcessId());
            Assert.assertNotNull(variableInstance.getDate());
        }
        List<VariableInstanceLog> listVariables = new ArrayList<VariableInstanceLog>();
        // collect only those that are related to list process variable
        for (VariableInstanceLog v : variableInstances) {
            if (v.getVariableInstanceId().equals("list")) {
                listVariables.add(v);
            } 
        }
        Assert.assertEquals(3, listVariables.size());
        VariableInstanceLog var = listVariables.get(0);
        Assert.assertEquals("One", var.getValue());
        Assert.assertEquals("", var.getOldValue());
        Assert.assertEquals(processInstance.getId(), var.getProcessInstanceId().longValue());
        Assert.assertEquals(processInstance.getProcessId(), var.getProcessId());
        Assert.assertEquals("list[0]", var.getVariableId());
        Assert.assertEquals("list", var.getVariableInstanceId());
        var = listVariables.get(1);
        Assert.assertEquals("Two", var.getValue());
        Assert.assertEquals("", var.getOldValue());
        Assert.assertEquals(processInstance.getId(), var.getProcessInstanceId().longValue());
        Assert.assertEquals(processInstance.getProcessId(), var.getProcessId());
        Assert.assertEquals("list[1]", var.getVariableId());
        Assert.assertEquals("list", var.getVariableInstanceId());
        var = listVariables.get(2);
        Assert.assertEquals("Three", var.getValue());
        Assert.assertEquals("", var.getOldValue());
        Assert.assertEquals(processInstance.getId(), var.getProcessInstanceId().longValue());
        Assert.assertEquals(processInstance.getProcessId(), var.getProcessId());
        Assert.assertEquals("list[2]", var.getVariableId());
        Assert.assertEquals("list", var.getVariableInstanceId());
        // Test findVariableInstancesByName* methods
        List<VariableInstanceLog> emptyVarLogs = auditLogService.findVariableInstancesByName("s", true);
        Assert.assertTrue(emptyVarLogs.isEmpty());
        for (VariableInstanceLog origVarLog : variableInstances) {
            varLogs = auditLogService.findVariableInstancesByName(origVarLog.getVariableId(), false);
            for (VariableInstanceLog varLog : varLogs) {
                Assert.assertEquals(origVarLog.getVariableId(), varLog.getVariableId());
            }
        }
        emptyVarLogs = auditLogService.findVariableInstancesByNameAndValue("s", "InitialValue", true);
        Assert.assertTrue(emptyVarLogs.isEmpty());
        String varId = "s";
        String varValue = "ResultValue";
        variableInstances = auditLogService.findVariableInstancesByNameAndValue(varId, varValue, false);
        Assert.assertEquals(3, variableInstances.size());
        VariableInstanceLog varLog = variableInstances.get(0);
        Assert.assertEquals(varId, varLog.getVariableId());
        Assert.assertEquals(varValue, varLog.getValue());
        auditLogService.clear();
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow3");
        Assert.assertTrue(processInstances.isEmpty());
    }
}

