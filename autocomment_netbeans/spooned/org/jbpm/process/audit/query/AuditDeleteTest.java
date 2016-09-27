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
/**
 * import org.jbpm.process.instance.impl.util.LoggingPrintStream;
 */


package org.jbpm.process.audit.query;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.jbpm.process.audit.AuditLogServiceTest;
import org.junit.Before;
import org.junit.BeforeClass;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.kie.api.runtime.EnvironmentName;
import java.util.GregorianCalendar;
import java.util.HashMap;
import org.jbpm.process.audit.JPAAuditLogService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jbpm.process.instance.impl.util.LoggingPrintStream;
import org.jbpm.process.audit.NodeInstanceLog;
import org.kie.internal.runtime.manager.audit.query.NodeInstanceLogDeleteBuilder;
import org.jbpm.persistence.util.PersistenceUtil;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.kie.internal.runtime.manager.audit.query.ProcessInstanceLogDeleteBuilder;
import java.util.Random;
import org.jbpm.process.audit.strategy.StandaloneJtaStrategy;
import org.junit.Test;
import java.util.UUID;
import org.jbpm.process.audit.VariableInstanceLog;
import org.kie.internal.runtime.manager.audit.query.VariableInstanceLogDeleteBuilder;

public class AuditDeleteTest extends JPAAuditLogService {
    private static HashMap<String, Object> context;

    private static EntityManagerFactory emf;

    private static final Logger logger = LoggerFactory.getLogger(AuditLogServiceTest.class);

    private ProcessInstanceLog[] pilTestData;

    private VariableInstanceLog[] vilTestData;

    private NodeInstanceLog[] nilTestData;

    @BeforeClass
    public static void configure() {
        LoggingPrintStream.interceptSysOutSysErr();
    }

    @AfterClass
    public static void reset() {
        LoggingPrintStream.resetInterceptSysOutSysErr();
    }

    @Before
    public void setUp() throws Exception {
        AuditDeleteTest.context = PersistenceUtil.setupWithPoolingDataSource(PersistenceUtil.JBPM_PERSISTENCE_UNIT_NAME);
        AuditDeleteTest.emf = ((EntityManagerFactory) (AuditDeleteTest.context.get(EnvironmentName.ENTITY_MANAGER_FACTORY)));
        if ((pilTestData) == null) {
            pilTestData = createTestProcessInstanceLogData();
            vilTestData = createTestVariableInstanceLogData();
            nilTestData = createTestNodeInstanceLogData();
        } 
        AuditDeleteTest.this.persistenceStrategy = new StandaloneJtaStrategy(AuditDeleteTest.emf);
    }

    @After
    public void cleanup() {
        PersistenceUtil.cleanUp(AuditDeleteTest.context);
    }

    private static Random random = new Random();

    private long randomLong() {
        long result = ((long) (Math.abs(AuditDeleteTest.random.nextInt())));
        while (result == 23L) {
            result = ((long) (Math.abs(AuditDeleteTest.random.nextInt())));
        }
        return result;
    }

    private String randomString() {
        return UUID.randomUUID().toString();
    }

    private Calendar randomCal() {
        Calendar cal = GregorianCalendar.getInstance();
        cal.roll(Calendar.DAY_OF_YEAR, ((-1) * (AuditDeleteTest.random.nextInt((10 * 365)))));
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        return cal;
    }

    private ProcessInstanceLog[] createTestProcessInstanceLogData() {
        StandaloneJtaStrategy jtaHelper = new StandaloneJtaStrategy(AuditDeleteTest.emf);
        EntityManager em = jtaHelper.getEntityManager();
        int numEntities = 11;
        ProcessInstanceLog[] testData = new ProcessInstanceLog[numEntities];
        Calendar cal = randomCal();
        for (int i = 0; i < numEntities; ++i) {
            ProcessInstanceLog pil = new ProcessInstanceLog(randomLong(), randomString());
            pil.setDuration(randomLong());
            pil.setExternalId(randomString());
            pil.setIdentity(randomString());
            pil.setOutcome(randomString());
            pil.setParentProcessInstanceId(randomLong());
            pil.setProcessId(randomString());
            pil.setProcessName(randomString());
            pil.setProcessVersion(randomString());
            pil.setStatus(AuditDeleteTest.random.nextInt());
            cal.add(Calendar.MINUTE, 1);
            pil.setStart(cal.getTime());
            cal.add(Calendar.DAY_OF_YEAR, 1);
            pil.setEnd(cal.getTime());
            testData[i] = pil;
        }
        for (int i = 0; i < numEntities; ++i) {
            switch (i) {
                case 1 :
                    testData[(i - 1)].setDuration(testData[i].getDuration());
                    break;
                case 2 :
                    testData[(i - 1)].setEnd(testData[i].getEnd());
                    break;
                case 3 :
                    testData[(i - 1)].setIdentity(testData[i].getIdentity());
                    break;
                case 4 :
                    testData[(i - 1)].setProcessId(testData[i].getProcessId());
                    break;
                case 5 :
                    testData[(i - 1)].setProcessInstanceId(testData[i].getProcessInstanceId());
                    break;
                case 6 :
                    testData[(i - 1)].setProcessName(testData[i].getProcessName());
                    break;
                case 7 :
                    testData[(i - 1)].setProcessVersion(testData[i].getProcessVersion());
                    break;
                case 8 :
                    testData[(i - 1)].setStart(testData[i].getStart());
                    break;
                case 9 :
                    testData[(i - 1)].setStatus(testData[i].getStatus());
                    break;
                case 10 :
                    testData[(i - 1)].setOutcome(testData[i].getOutcome());
                    break;
            }
        }
        Object tx = jtaHelper.joinTransaction(em);
        for (int i = 0; i < numEntities; ++i) {
            em.persist(testData[i]);
        }
        jtaHelper.leaveTransaction(em, tx);
        return testData;
    }

    private NodeInstanceLog[] createTestNodeInstanceLogData() {
        StandaloneJtaStrategy jtaHelper = new StandaloneJtaStrategy(AuditDeleteTest.emf);
        EntityManager em = jtaHelper.getEntityManager();
        int numEntities = 9;
        NodeInstanceLog[] testData = new NodeInstanceLog[numEntities];
        Calendar cal = randomCal();
        for (int i = 0; i < numEntities; ++i) {
            NodeInstanceLog nil = new NodeInstanceLog();
            nil.setProcessInstanceId(randomLong());
            nil.setProcessId(randomString());
            cal.add(Calendar.SECOND, 1);
            nil.setDate(cal.getTime());
            nil.setType(Math.abs(AuditDeleteTest.random.nextInt()));
            nil.setNodeInstanceId(randomString());
            nil.setNodeId(randomString());
            nil.setNodeName(randomString());
            nil.setNodeType(randomString());
            nil.setWorkItemId(randomLong());
            nil.setConnection(randomString());
            nil.setExternalId(randomString());
            testData[i] = nil;
        }
        for (int i = 0; i < numEntities; ++i) {
            switch (i) {
                case 1 :
                    testData[(i - 1)].setDate(testData[i].getDate());
                    break;
                case 2 :
                    testData[(i - 1)].setNodeId(testData[i].getNodeId());
                    break;
                case 3 :
                    testData[(i - 1)].setNodeInstanceId(testData[i].getNodeInstanceId());
                    break;
                case 4 :
                    testData[(i - 1)].setNodeName(testData[i].getNodeName());
                    break;
                case 5 :
                    testData[(i - 1)].setNodeType(testData[i].getNodeType());
                    break;
                case 6 :
                    testData[(i - 1)].setProcessId(testData[i].getProcessId());
                    break;
                case 7 :
                    testData[(i - 1)].setProcessInstanceId(testData[i].getProcessInstanceId());
                    break;
                case 8 :
                    testData[(i - 1)].setWorkItemId(testData[i].getWorkItemId());
                    break;
            }
        }
        Object tx = jtaHelper.joinTransaction(em);
        for (int i = 0; i < numEntities; ++i) {
            em.persist(testData[i]);
        }
        jtaHelper.leaveTransaction(em, tx);
        return testData;
    }

    private VariableInstanceLog[] createTestVariableInstanceLogData() {
        StandaloneJtaStrategy jtaHelper = new StandaloneJtaStrategy(AuditDeleteTest.emf);
        EntityManager em = jtaHelper.getEntityManager();
        int numEntities = 8;
        VariableInstanceLog[] testData = new VariableInstanceLog[numEntities];
        Calendar cal = randomCal();
        for (int i = 0; i < numEntities; ++i) {
            VariableInstanceLog vil = new VariableInstanceLog();
            vil.setProcessInstanceId(randomLong());
            vil.setProcessId(randomString());
            cal.add(Calendar.MINUTE, 1);
            vil.setDate(cal.getTime());
            vil.setVariableInstanceId(randomString());
            vil.setVariableId(randomString());
            vil.setValue(randomString());
            vil.setOldValue(randomString());
            vil.setExternalId(randomString());
            testData[i] = vil;
        }
        for (int i = 0; i < numEntities; ++i) {
            switch (i) {
                case 1 :
                    testData[(i - 1)].setDate(testData[i].getDate());
                    break;
                case 2 :
                    testData[(i - 1)].setOldValue(testData[i].getOldValue());
                    break;
                case 3 :
                    testData[(i - 1)].setProcessId(testData[i].getProcessId());
                    break;
                case 4 :
                    testData[(i - 1)].setProcessInstanceId(testData[i].getProcessInstanceId());
                    break;
                case 5 :
                    testData[(i - 1)].setValue(testData[i].getValue());
                    break;
                case 6 :
                    testData[(i - 1)].setVariableId(testData[i].getVariableId());
                    break;
                case 7 :
                    testData[(i - 1)].setVariableInstanceId(testData[i].getVariableInstanceId());
                    break;
            }
        }
        Object tx = jtaHelper.joinTransaction(em);
        for (int i = 0; i < numEntities; ++i) {
            em.persist(testData[i]);
        }
        jtaHelper.leaveTransaction(em, tx);
        return testData;
    }

    @Test
    public void testDeleteProcessInstanceInfoLogByProcessId() {
        int p = 0;
        String processId = pilTestData[(p++)].getProcessId();
        String processId2 = pilTestData[(p++)].getProcessId();
        ProcessInstanceLogDeleteBuilder updateBuilder = AuditDeleteTest.this.processInstanceLogDelete().processId(processId, processId2);
        int result = updateBuilder.build().execute();
        Assert.assertEquals(2, result);
    }

    @Test
    public void testDeleteProcessInstanceInfoLogByDate() {
        int p = 0;
        Date endDate = pilTestData[(p++)].getEnd();
        ProcessInstanceLogDeleteBuilder updateBuilder = AuditDeleteTest.this.processInstanceLogDelete().endDate(endDate);
        int result = updateBuilder.build().execute();
        Assert.assertEquals(1, result);
    }

    @Test
    public void testDeleteProcessInstanceInfoLogByTimestamp() {
        int p = 0;
        Date endDate = pilTestData[(p++)].getEnd();
        List<org.kie.api.runtime.manager.audit.ProcessInstanceLog> logs = AuditDeleteTest.this.processInstanceLogQuery().endDate(endDate).build().getResultList();
        Assert.assertEquals(1, logs.size());
        ProcessInstanceLogDeleteBuilder updateBuilder = AuditDeleteTest.this.processInstanceLogDelete().endDate(logs.get(0).getEnd());
        int result = updateBuilder.build().execute();
        Assert.assertEquals(1, result);
    }

    @Test
    public void testDeleteProcessInstanceInfoLogByProcessIdAndDate() {
        int p = 0;
        String processId = pilTestData[p].getProcessId();
        Date endDate = pilTestData[p].getEnd();
        ProcessInstanceLogDeleteBuilder updateBuilder = AuditDeleteTest.this.processInstanceLogDelete().endDate(endDate).processId(processId);
        int result = updateBuilder.build().execute();
        Assert.assertEquals(1, result);
    }

    @Test
    public void testDeleteProcessInstanceInfoLogByProcessIdAndNotMatchingDate() {
        int p = 0;
        String processId = pilTestData[(p++)].getProcessId();
        Date endDate = pilTestData[(p++)].getEnd();
        ProcessInstanceLogDeleteBuilder updateBuilder = AuditDeleteTest.this.processInstanceLogDelete().endDate(endDate).processId(processId);
        int result = updateBuilder.build().execute();
        Assert.assertEquals(0, result);
    }

    @Test
    public void testDeleteProcessInstanceInfoLogByStatus() {
        int status = pilTestData[5].getStatus();
        ProcessInstanceLogDeleteBuilder updateBuilder = AuditDeleteTest.this.processInstanceLogDelete().status(status);
        int result = updateBuilder.build().execute();
        Assert.assertEquals(1, result);
    }

    @Test
    public void testDeleteProcessInstanceInfoLogByDateRangeEnd() {
        Date endDate = pilTestData[4].getEnd();
        ProcessInstanceLogDeleteBuilder updateBuilder = AuditDeleteTest.this.processInstanceLogDelete().endDateRangeEnd(endDate);
        int result = updateBuilder.build().execute();
        Assert.assertEquals(5, result);
    }

    @Test
    public void testDeleteProcessInstanceInfoLogByDateRangeStart() {
        Date endDate = pilTestData[8].getEnd();
        ProcessInstanceLogDeleteBuilder updateBuilder = AuditDeleteTest.this.processInstanceLogDelete().endDateRangeStart(endDate);
        int result = updateBuilder.build().execute();
        Assert.assertEquals(3, result);
    }

    @Test
    public void testDeleteNodeInstanceInfoLogByProcessId() {
        int p = 0;
        String processId = nilTestData[(p++)].getProcessId();
        NodeInstanceLogDeleteBuilder updateBuilder = AuditDeleteTest.this.nodeInstanceLogDelete().processId(processId);
        int result = updateBuilder.build().execute();
        Assert.assertEquals(1, result);
    }

    @Test
    public void testDeleteNodeInstanceInfoLogByDate() {
        int p = 0;
        Date date = nilTestData[(p++)].getDate();
        NodeInstanceLogDeleteBuilder updateBuilder = AuditDeleteTest.this.nodeInstanceLogDelete().date(date);
        int result = updateBuilder.build().execute();
        Assert.assertEquals(2, result);
    }

    @Test
    public void testDeleteNodeInstanceInfoLogByDateRangeEnd() {
        Date endDate = nilTestData[4].getDate();
        NodeInstanceLogDeleteBuilder updateBuilder = AuditDeleteTest.this.nodeInstanceLogDelete().dateRangeEnd(endDate);
        int result = updateBuilder.build().execute();
        Assert.assertEquals(5, result);
    }

    @Test
    public void testDeleteNodeInstanceInfoLogByTimestamp() {
        int p = 0;
        Date date = nilTestData[(p++)].getDate();
        List<org.kie.api.runtime.manager.audit.NodeInstanceLog> logs = AuditDeleteTest.this.nodeInstanceLogQuery().date(date).build().getResultList();
        Assert.assertEquals(2, logs.size());
        NodeInstanceLogDeleteBuilder updateBuilder = AuditDeleteTest.this.nodeInstanceLogDelete().date(logs.get(0).getDate());
        int result = updateBuilder.build().execute();
        Assert.assertEquals(2, result);
    }

    @Test
    public void testDeleteVarInstanceInfoLogByProcessId() {
        int p = 0;
        String processId = vilTestData[(p++)].getProcessId();
        VariableInstanceLogDeleteBuilder updateBuilder = AuditDeleteTest.this.variableInstanceLogDelete().processId(processId);
        int result = updateBuilder.build().execute();
        Assert.assertEquals(1, result);
    }

    @Test
    public void testDeleteVarInstanceInfoLogByDate() {
        int p = 0;
        Date date = vilTestData[(p++)].getDate();
        VariableInstanceLogDeleteBuilder updateBuilder = AuditDeleteTest.this.variableInstanceLogDelete().date(date);
        int result = updateBuilder.build().execute();
        Assert.assertEquals(2, result);
    }

    @Test
    public void testDeleteVarInstanceInfoLogByDateRangeEnd() {
        Date endDate = vilTestData[4].getDate();
        VariableInstanceLogDeleteBuilder updateBuilder = AuditDeleteTest.this.variableInstanceLogDelete().dateRangeEnd(endDate);
        int result = updateBuilder.build().execute();
        Assert.assertEquals(5, result);
    }

    @Test
    public void testDeleteVarInstanceInfoLogByTimestamp() {
        int p = 0;
        Date date = vilTestData[(p++)].getDate();
        List<org.kie.api.runtime.manager.audit.VariableInstanceLog> vars = AuditDeleteTest.this.variableInstanceLogQuery().date(date).build().getResultList();
        Assert.assertEquals(2, vars.size());
        VariableInstanceLogDeleteBuilder updateBuilder = AuditDeleteTest.this.variableInstanceLogDelete().date(vars.get(0).getDate());
        int result = updateBuilder.build().execute();
        Assert.assertEquals(2, result);
    }
}

