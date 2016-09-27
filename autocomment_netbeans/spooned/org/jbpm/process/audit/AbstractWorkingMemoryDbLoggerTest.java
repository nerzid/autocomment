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
import org.junit.After;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import bitronix.tm.BitronixTransactionManager;
import java.util.Collection;
import org.kie.api.runtime.Environment;
import org.drools.core.impl.EnvironmentFactory;
import java.util.HashMap;
import java.io.InputStreamReader;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jbpm.persistence.util.PersistenceUtil;
import org.kie.api.runtime.process.ProcessInstance;
import java.io.Reader;
import org.junit.Test;
import bitronix.tm.TransactionManagerServices;

/**
 * This class tests the following classes:
 * <ul>
 * <li>WorkingMemoryDbLogger</li>
 * </ul>
 */
public abstract class AbstractWorkingMemoryDbLoggerTest extends AbstractBaseTest {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractWorkingMemoryDbLoggerTest.class);

    protected HashMap<String, Object> context;

    protected AuditLogService logService;

    @Before
    public void setUp() throws Exception {
        context = PersistenceUtil.setupWithPoolingDataSource(PersistenceUtil.JBPM_PERSISTENCE_UNIT_NAME);
        Environment env = EnvironmentFactory.newEnvironment();
        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, context.get(EnvironmentName.ENTITY_MANAGER_FACTORY));
        logService = new JPAAuditLogService(env);
    }

    @After
    public void tearDown() throws Exception {
        BitronixTransactionManager txm = TransactionManagerServices.getTransactionManager();
        Assert.assertTrue("There is still a transaction running!", ((txm.getCurrentTransaction()) == null));
        PersistenceUtil.cleanUp(context);
        logService.dispose();
    }

    protected static KnowledgeBase createKnowledgeBase() {
        // create a builder
        KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
        // load the process
        Reader source = new InputStreamReader(AbstractWorkingMemoryDbLoggerTest.class.getResourceAsStream("/ruleflow.rf"));
        builder.addProcessFromXml(source);
        source = new InputStreamReader(AbstractWorkingMemoryDbLoggerTest.class.getResourceAsStream("/ruleflow2.rf"));
        builder.addProcessFromXml(source);
        source = new InputStreamReader(AbstractWorkingMemoryDbLoggerTest.class.getResourceAsStream("/ruleflow3.rf"));
        builder.addProcessFromXml(source);
        // create the knowledge base
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(((Collection) (Arrays.asList(builder.getPackage()))));
        return kbase;
    }

    public abstract ProcessInstance startProcess(String processName);

    @Test
    public void testLogger1() {
        // start process instance
        long processInstanceId = startProcess("com.sample.ruleflow").getId();
        AbstractWorkingMemoryDbLoggerTest.logger.debug("Checking process instances for process 'com.sample.ruleflow'");
        List<ProcessInstanceLog> processInstances = logService.findProcessInstances("com.sample.ruleflow");
        Assert.assertEquals(1, processInstances.size());
        ProcessInstanceLog processInstance = processInstances.get(0);
        AbstractWorkingMemoryDbLoggerTest.logger.debug("{}", processInstance);
        Assert.assertNotNull(processInstance.getStart());
        Assert.assertNotNull(processInstance.getEnd());
        Assert.assertEquals(processInstanceId, processInstance.getProcessInstanceId().longValue());
        Assert.assertEquals("com.sample.ruleflow", processInstance.getProcessId());
        List<NodeInstanceLog> nodeInstances = logService.findNodeInstances(processInstanceId);
        Assert.assertEquals(6, nodeInstances.size());
        for (NodeInstanceLog nodeInstance : nodeInstances) {
            AbstractWorkingMemoryDbLoggerTest.logger.debug("{}", nodeInstance);
            Assert.assertEquals(processInstanceId, processInstance.getProcessInstanceId().longValue());
            Assert.assertEquals("com.sample.ruleflow", processInstance.getProcessId());
            Assert.assertNotNull(nodeInstance.getDate());
        }
        logService.clear();
    }

    @Test
    public void testLogger2() {
        // start process instance
        startProcess("com.sample.ruleflow");
        startProcess("com.sample.ruleflow");
        AbstractWorkingMemoryDbLoggerTest.logger.debug("Checking process instances for process 'com.sample.ruleflow'");
        List<ProcessInstanceLog> processInstances = logService.findProcessInstances("com.sample.ruleflow");
        Assert.assertEquals(2, processInstances.size());
        for (ProcessInstanceLog processInstance : processInstances) {
            AbstractWorkingMemoryDbLoggerTest.logger.debug("{}", processInstance);
            AbstractWorkingMemoryDbLoggerTest.logger.debug(" -> {} - {}", processInstance.getStart(), processInstance.getEnd());
            List<NodeInstanceLog> nodeInstances = logService.findNodeInstances(processInstance.getProcessInstanceId());
            for (NodeInstanceLog nodeInstance : nodeInstances) {
                AbstractWorkingMemoryDbLoggerTest.logger.debug("{}", nodeInstance);
                AbstractWorkingMemoryDbLoggerTest.logger.debug(" -> {}", nodeInstance.getDate());
            }
            Assert.assertEquals(6, nodeInstances.size());
        }
        logService.clear();
    }

    @Test
    public void testLogger3() {
        long processInstanceId = startProcess("com.sample.ruleflow2").getId();
        AbstractWorkingMemoryDbLoggerTest.logger.debug("Checking process instances for process 'com.sample.ruleflow2'");
        List<ProcessInstanceLog> processInstances = logService.findProcessInstances("com.sample.ruleflow2");
        Assert.assertEquals(1, processInstances.size());
        ProcessInstanceLog processInstance = processInstances.get(0);
        AbstractWorkingMemoryDbLoggerTest.logger.debug("{}", processInstance);
        AbstractWorkingMemoryDbLoggerTest.logger.debug(" -> {} - {} ", processInstance.getStart(), processInstance.getEnd());
        Assert.assertNotNull(processInstance.getStart());
        Assert.assertNotNull(processInstance.getEnd());
        Assert.assertEquals(processInstanceId, processInstance.getProcessInstanceId().longValue());
        Assert.assertEquals("com.sample.ruleflow2", processInstance.getProcessId());
        List<NodeInstanceLog> nodeInstances = logService.findNodeInstances(processInstanceId);
        for (NodeInstanceLog nodeInstance : nodeInstances) {
            AbstractWorkingMemoryDbLoggerTest.logger.debug("{}", nodeInstance);
            AbstractWorkingMemoryDbLoggerTest.logger.debug(" -> {}", nodeInstance.getDate());
            Assert.assertEquals(processInstanceId, processInstance.getProcessInstanceId().longValue());
            Assert.assertEquals("com.sample.ruleflow2", processInstance.getProcessId());
            Assert.assertNotNull(nodeInstance.getDate());
        }
        Assert.assertEquals(14, nodeInstances.size());
        logService.clear();
    }
}

