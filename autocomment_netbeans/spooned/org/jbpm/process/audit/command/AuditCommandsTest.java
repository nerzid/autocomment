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


package org.jbpm.process.audit.command;

import org.jbpm.process.audit.AuditLogService;
import org.junit.BeforeClass;
import org.kie.api.command.Command;
import org.kie.api.runtime.Environment;
import org.drools.core.impl.EnvironmentFactory;
import org.jbpm.bpmn2.JbpmBpmn2TestCase;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import java.util.List;
import org.jbpm.process.audit.NodeInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.junit.Test;
import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.jbpm.process.audit.VariableInstanceLog;
import org.kie.api.runtime.process.WorkItem;

public class AuditCommandsTest extends JbpmBpmn2TestCase {
    public AuditCommandsTest() {
        super(true);
    }

    private static AuditLogService logService;

    @BeforeClass
    public static void setup() throws Exception {
        setUpDataSource();
        // clear logs
        Environment env = EnvironmentFactory.newEnvironment();
        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
        AuditCommandsTest.logService = new org.jbpm.process.audit.JPAAuditLogService(env);
        AuditCommandsTest.logService.clear();
    }

    @Test
    public void testFindProcessInstanceCommands() throws Exception {
        String processId = "IntermediateCatchEvent";
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventSignal.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler());
        ProcessInstance processInstance = ksession.startProcess(processId);
        assertTrue(((processInstance.getState()) == (ProcessInstance.STATE_ACTIVE)));
        Command<?> cmd = new FindProcessInstancesCommand();
        Object result = ksession.execute(cmd);
        assertNotNull("Command result is empty!", result);
        assertTrue((result instanceof List));
        List<ProcessInstanceLog> logList = ((List<ProcessInstanceLog>) (result));
        assertEquals("Log list size is incorrect.", 1, logList.size());
        ProcessInstanceLog log = logList.get(0);
        assertEquals(log.getProcessInstanceId().longValue(), processInstance.getId());
        assertEquals(log.getProcessId(), processInstance.getProcessId());
        cmd = new FindActiveProcessInstancesCommand(processId);
        result = ksession.execute(cmd);
        assertNotNull("Command result is empty!", result);
        assertTrue((result instanceof List));
        logList = ((List<ProcessInstanceLog>) (result));
        assertEquals("Log list size is incorrect.", 1, logList.size());
        log = logList.get(0);
        assertEquals("Process instance id", log.getProcessInstanceId().longValue(), processInstance.getId());
        assertEquals("Process id", log.getProcessId(), processInstance.getProcessId());
        assertEquals("Status", log.getStatus().intValue(), ProcessInstance.STATE_ACTIVE);
        cmd = new FindProcessInstanceCommand(processInstance.getId());
        result = ksession.execute(cmd);
        assertNotNull("Command result is empty!", result);
        assertTrue((result instanceof ProcessInstanceLog));
        log = ((ProcessInstanceLog) (result));
        assertEquals(log.getProcessInstanceId().longValue(), processInstance.getId());
        assertEquals(log.getProcessId(), processInstance.getProcessId());
        cmd = new ClearHistoryLogsCommand();
        result = ksession.execute(cmd);
        assertEquals("There should be no more logs", 0, AuditCommandsTest.logService.findProcessInstances().size());
        // now signal process instance
        ksession = restoreSession(ksession, true);
        ksession.signalEvent("MyMessage", "SomeValue", processInstance.getId());
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    @Test
    public void testVarAndNodeInstanceCommands() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-SubProcessUserTask.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ProcessInstance processInstance = ksession.startProcess("SubProcess");
        assertProcessInstanceActive(processInstance);
        Command<?> cmd = new FindNodeInstancesCommand(processInstance.getId());
        Object result = ksession.execute(cmd);
        assertNotNull("Command result is empty!", result);
        assertTrue((result instanceof List));
        List<NodeInstanceLog> nodeLogList = ((List<NodeInstanceLog>) (result));
        assertEquals("Log list size is incorrect.", 8, nodeLogList.size());
        cmd = new FindNodeInstancesCommand(processInstance.getId(), "UserTask_1");
        result = ksession.execute(cmd);
        assertNotNull("Command result is empty!", result);
        assertTrue((result instanceof List));
        nodeLogList = ((List<NodeInstanceLog>) (result));
        assertEquals("Log list size is incorrect.", 1, nodeLogList.size());
        cmd = new FindVariableInstancesCommand(processInstance.getId(), "2:x");
        result = ksession.execute(cmd);
        assertNotNull("Command result is empty!", result);
        assertTrue((result instanceof List));
        List<VariableInstanceLog> varLogList = ((List<VariableInstanceLog>) (result));
        assertEquals("Log list size is incorrect.", 1, varLogList.size());
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
    }
}

