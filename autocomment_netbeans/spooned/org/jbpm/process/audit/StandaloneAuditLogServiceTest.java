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


package org.jbpm.process.audit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.kie.api.runtime.Environment;
import java.util.HashMap;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jbpm.persistence.util.PersistenceUtil;
import org.junit.Test;

public class StandaloneAuditLogServiceTest extends AbstractAuditLogServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(StandaloneAuditLogServiceTest.class);

    private HashMap<String, Object> context;

    private AuditLogService auditLogService;

    private KieSession ksession;

    @Before
    public void setUp() throws Exception {
        // persistence
        context = PersistenceUtil.setupWithPoolingDataSource(PersistenceUtil.JBPM_PERSISTENCE_UNIT_NAME);
        // create a new session
        Environment env = PersistenceUtil.createEnvironment(context);
        KieBase kbase = createKnowledgeBase();
        ksession = createKieSession(kbase, env);
        new JPAWorkingMemoryDbLogger(ksession);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler());
        // log service
        auditLogService = new JPAAuditLogService(env);
    }

    @After
    public void tearDown() throws Exception {
        PersistenceUtil.cleanUp(context);
    }

    private <T> T setAuditLogServiceAndExecute(AuditCommand<T> cmd) {
        cmd.setAuditLogService(auditLogService);
        return cmd.execute(null);
    }

    // TESTS ----------------------------------------------------------------------------------------------------------------------
    @Test
    public void setAuditLogServiceForCommandTest() {
        String PROCESS_ID = "com.sample.ruleflow";
        // record the initial count to compare to later
        List<ProcessInstanceLog> processInstances = setAuditLogServiceAndExecute(new org.jbpm.process.audit.command.FindProcessInstancesCommand(PROCESS_ID));
        int initialProcessInstanceSize = processInstances.size();
        // start process instance
        long processInstanceId = ksession.startProcess(PROCESS_ID).getId();
        StandaloneAuditLogServiceTest.logger.debug("Checking process instances for process '{}'", PROCESS_ID);
        processInstances = setAuditLogServiceAndExecute(new org.jbpm.process.audit.command.FindProcessInstancesCommand(PROCESS_ID));
        Assert.assertEquals((initialProcessInstanceSize + 1), processInstances.size());
        ProcessInstanceLog processInstance = processInstances.get(initialProcessInstanceSize);
        StandaloneAuditLogServiceTest.logger.debug("{} -> {} - {}", processInstance.toString(), processInstance.getStart(), processInstance.getEnd());
        Assert.assertNotNull(processInstance.getStart());
        Assert.assertNotNull("ProcessInstanceLog does not contain end date.", processInstance.getEnd());
        Assert.assertEquals(processInstanceId, processInstance.getProcessInstanceId().longValue());
        Assert.assertEquals(PROCESS_ID, processInstance.getProcessId());
        List<NodeInstanceLog> nodeInstances = setAuditLogServiceAndExecute(new org.jbpm.process.audit.command.FindNodeInstancesCommand(processInstanceId));
        Assert.assertEquals(6, nodeInstances.size());
        setAuditLogServiceAndExecute(new org.jbpm.process.audit.command.ClearHistoryLogsCommand());
        nodeInstances = setAuditLogServiceAndExecute(new org.jbpm.process.audit.command.FindNodeInstancesCommand(processInstanceId));
        Assert.assertEquals(0, nodeInstances.size());
        processInstances = setAuditLogServiceAndExecute(new org.jbpm.process.audit.command.FindProcessInstancesCommand(PROCESS_ID));
        Assert.assertEquals(0, processInstances.size());
    }
}

