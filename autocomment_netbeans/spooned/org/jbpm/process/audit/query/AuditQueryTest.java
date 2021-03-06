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


package org.jbpm.process.audit.query;

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.jbpm.process.audit.AuditLogServiceTest;
import org.junit.Before;
import org.junit.BeforeClass;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import org.kie.internal.process.CorrelationKey;
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
import org.kie.internal.runtime.manager.audit.query.NodeInstanceLogQueryBuilder;
import org.kie.internal.query.ParametrizedQuery;
import org.jbpm.persistence.util.PersistenceUtil;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.kie.internal.runtime.manager.audit.query.ProcessInstanceLogQueryBuilder;
import org.jbpm.process.audit.strategy.StandaloneJtaStrategy;
import org.junit.Test;
import org.jbpm.process.audit.VariableInstanceLog;
import org.kie.internal.runtime.manager.audit.query.VariableInstanceLogQueryBuilder;

public class AuditQueryTest extends JPAAuditLogService {
    private static HashMap<String, Object> context;

    private static EntityManagerFactory emf;

    private static final Logger logger = LoggerFactory.getLogger(AuditLogServiceTest.class);

    private ProcessInstanceLog[] pilTestData;

    private VariableInstanceLog[] vilTestData;

    private NodeInstanceLog[] nilTestData;

    @AfterClass
    public static void resetLogging() {
        AbstractBaseTest.reset();
    }

    @BeforeClass
    public static void configure() {
        AbstractBaseTest.hackTheDatabaseMetadataLoggerBecauseTheresALogbackXmlInTheClasspath();
        LoggingPrintStream.interceptSysOutSysErr();
        AuditQueryTest.context = PersistenceUtil.setupWithPoolingDataSource(PersistenceUtil.JBPM_PERSISTENCE_UNIT_NAME);
        AuditQueryTest.emf = ((EntityManagerFactory) (AuditQueryTest.context.get(EnvironmentName.ENTITY_MANAGER_FACTORY)));
    }

    @AfterClass
    public static void reset() {
        LoggingPrintStream.resetInterceptSysOutSysErr();
        PersistenceUtil.cleanUp(AuditQueryTest.context);
    }

    @Before
    public void setUp() throws Exception {
        if ((pilTestData) == null) {
            pilTestData = AuditQueryDataUtil.createTestProcessInstanceLogData(AuditQueryTest.emf);
            vilTestData = AuditQueryDataUtil.createTestVariableInstanceLogData(AuditQueryTest.emf);
            nilTestData = AuditQueryDataUtil.createTestNodeInstanceLogData(AuditQueryTest.emf);
        } 
        AuditQueryTest.this.persistenceStrategy = new StandaloneJtaStrategy(AuditQueryTest.emf);
    }

    @Test
    public void simpleProcessInstanceLogQueryBuilderTest() {
        int p = 0;
        long duration = pilTestData[(p++)].getDuration();
        ProcessInstanceLogQueryBuilder builder = AuditQueryTest.this.processInstanceLogQuery().duration(duration);
        List<org.kie.api.runtime.manager.audit.ProcessInstanceLog> resultList = builder.build().getResultList();
        Assert.assertEquals("duration query result", 2, resultList.size());
        {
            Date end = pilTestData[(p++)].getEnd();
            builder = AuditQueryTest.this.processInstanceLogQuery().endDate(end);
            resultList = builder.build().getResultList();
            Assert.assertEquals("end date query result", 2, resultList.size());
        }
        {
            String identity = pilTestData[(p++)].getIdentity();
            builder = AuditQueryTest.this.processInstanceLogQuery().identity(identity);
            resultList = builder.build().getResultList();
            Assert.assertEquals("identity query result", 2, resultList.size());
        }
        {
            String processId = pilTestData[(p++)].getProcessId();
            builder = AuditQueryTest.this.processInstanceLogQuery().processId(processId);
            resultList = builder.build().getResultList();
            Assert.assertEquals("process id query result", 2, resultList.size());
        }
        {
            long processInstanceId = pilTestData[(p++)].getProcessInstanceId();
            builder = AuditQueryTest.this.processInstanceLogQuery().processInstanceId(processInstanceId);
            resultList = builder.build().getResultList();
            Assert.assertEquals("process instance id query result", 2, resultList.size());
        }
        {
            String processName = pilTestData[(p++)].getProcessName();
            builder = AuditQueryTest.this.processInstanceLogQuery().processName(processName);
            resultList = builder.build().getResultList();
            Assert.assertEquals("process name query result", 2, resultList.size());
        }
        {
            String version = pilTestData[(p++)].getProcessVersion();
            builder = AuditQueryTest.this.processInstanceLogQuery().processVersion(version);
            resultList = builder.build().getResultList();
            Assert.assertEquals("process version query result", 2, resultList.size());
        }
        {
            Date start = pilTestData[(p++)].getStart();
            builder = AuditQueryTest.this.processInstanceLogQuery().startDate(start);
            resultList = builder.build().getResultList();
            Assert.assertEquals("start date query result", 2, resultList.size());
        }
        {
            int status = pilTestData[(p++)].getStatus();
            builder = AuditQueryTest.this.processInstanceLogQuery().status(status);
            resultList = builder.build().getResultList();
            Assert.assertEquals("status query result", 2, resultList.size());
        }
        {
            String outcome = pilTestData[(p++)].getOutcome();
            builder = AuditQueryTest.this.processInstanceLogQuery().outcome(outcome);
            resultList = builder.build().getResultList();
            Assert.assertEquals("outcome query result", 2, resultList.size());
        }
        {
            String correlationKey = pilTestData[(p++)].getCorrelationKey();
            CorrelationKey ck = KieInternalServices.Factory.get().newCorrelationKeyFactory().newCorrelationKey(correlationKey);
            builder = AuditQueryTest.this.processInstanceLogQuery().correlationKey(ck);
            resultList = builder.build().getResultList();
            Assert.assertEquals("identity query result", 1, resultList.size());
        }
    }

    @Test
    public void simpleVariableInstanceLogQueryBuilderTest() {
        int p = 0;
        Date date = vilTestData[(p++)].getDate();
        VariableInstanceLogQueryBuilder builder = AuditQueryTest.this.variableInstanceLogQuery().date(date);
        List<org.kie.api.runtime.manager.audit.VariableInstanceLog> resultList = builder.build().getResultList();
        Assert.assertEquals("date query result", 2, resultList.size());
        {
            String oldValue = vilTestData[(p++)].getOldValue();
            builder = AuditQueryTest.this.variableInstanceLogQuery().oldValue(oldValue);
            resultList = builder.build().getResultList();
            Assert.assertEquals("old value query result", 2, resultList.size());
        }
        {
            String processId = vilTestData[(p++)].getProcessId();
            builder = AuditQueryTest.this.variableInstanceLogQuery().processId(processId);
            resultList = builder.build().getResultList();
            Assert.assertEquals("process id query result", 2, resultList.size());
        }
        {
            long processInstanceId = vilTestData[(p++)].getProcessInstanceId();
            builder = AuditQueryTest.this.variableInstanceLogQuery().processInstanceId(processInstanceId);
            resultList = builder.build().getResultList();
            Assert.assertEquals("process instance id query result", 2, resultList.size());
        }
        {
            String value = vilTestData[(p++)].getValue();
            builder = AuditQueryTest.this.variableInstanceLogQuery().value(value);
            resultList = builder.build().getResultList();
            Assert.assertEquals("value query result", 2, resultList.size());
        }
        {
            String variableId = vilTestData[(p++)].getVariableId();
            builder = AuditQueryTest.this.variableInstanceLogQuery().variableId(variableId);
            resultList = builder.build().getResultList();
            Assert.assertEquals("variable id query result", 2, resultList.size());
        }
        {
            String varInstId = vilTestData[(p++)].getVariableInstanceId();
            builder = AuditQueryTest.this.variableInstanceLogQuery().variableInstanceId(varInstId);
            resultList = builder.build().getResultList();
            Assert.assertEquals("variable instance id query result", 2, resultList.size());
        }
    }

    @Test
    public void simpleNodeInstanceLogQueryBuilderTest() {
        int p = 0;
        Date date = nilTestData[(p++)].getDate();
        NodeInstanceLogQueryBuilder builder = AuditQueryTest.this.nodeInstanceLogQuery().date(date);
        List<org.kie.api.runtime.manager.audit.NodeInstanceLog> resultList = builder.build().getResultList();
        Assert.assertEquals("date query result", 2, resultList.size());
        {
            String nodeId = nilTestData[(p++)].getNodeId();
            builder = AuditQueryTest.this.nodeInstanceLogQuery().nodeId(nodeId);
            resultList = builder.build().getResultList();
            Assert.assertEquals("node id query result", 2, resultList.size());
        }
        {
            String nodeInstId = nilTestData[(p++)].getNodeInstanceId();
            builder = AuditQueryTest.this.nodeInstanceLogQuery().nodeInstanceId(nodeInstId);
            resultList = builder.build().getResultList();
            Assert.assertEquals("node instance id query result", 2, resultList.size());
        }
        {
            String name = nilTestData[(p++)].getNodeName();
            builder = AuditQueryTest.this.nodeInstanceLogQuery().nodeName(name);
            resultList = builder.build().getResultList();
            Assert.assertEquals("node name query result", 2, resultList.size());
        }
        {
            String nodeType = nilTestData[(p++)].getNodeType();
            builder = AuditQueryTest.this.nodeInstanceLogQuery().nodeType(nodeType);
            resultList = builder.build().getResultList();
            Assert.assertEquals("node type query result", 2, resultList.size());
        }
        {
            String processId = nilTestData[(p++)].getProcessId();
            builder = AuditQueryTest.this.nodeInstanceLogQuery().processId(processId);
            resultList = builder.build().getResultList();
            Assert.assertEquals("process id query result", 2, resultList.size());
        }
        {
            long processInstanceId = nilTestData[(p++)].getProcessInstanceId();
            builder = AuditQueryTest.this.nodeInstanceLogQuery().processInstanceId(processInstanceId);
            resultList = builder.build().getResultList();
            Assert.assertEquals("process instance id query result", 2, resultList.size());
        }
        {
            long workItemId = nilTestData[(p++)].getWorkItemId();
            builder = AuditQueryTest.this.nodeInstanceLogQuery().workItemId(workItemId);
            resultList = builder.build().getResultList();
            Assert.assertEquals("work item id query result", 2, resultList.size());
        }
        // pagination
        int maxResults = 5;
        resultList = AuditQueryTest.this.nodeInstanceLogQuery().build().getResultList();
        Assert.assertTrue("Not enough to do pagination test", ((resultList.size()) > maxResults));
        resultList = AuditQueryTest.this.nodeInstanceLogQuery().maxResults(maxResults).ascending(org.kie.internal.runtime.manager.audit.query.NodeInstanceLogQueryBuilder.OrderBy.processInstanceId).build().getResultList();
        Assert.assertTrue(((("Only expected " + maxResults) + " results, not ") + (resultList.size())), ((resultList.size()) <= 5));
        int offset = 3;
        List<org.kie.api.runtime.manager.audit.NodeInstanceLog> newResultList = AuditQueryTest.this.nodeInstanceLogQuery().maxResults(maxResults).offset(offset).ascending(org.kie.internal.runtime.manager.audit.query.NodeInstanceLogQueryBuilder.OrderBy.processInstanceId).build().getResultList();
        Assert.assertTrue(((("Only expected" + maxResults) + " results, not ") + (newResultList.size())), ((newResultList.size()) <= 5));
        Assert.assertEquals(((((("Offset should have been " + offset) + ": ") + (resultList.get(offset).getProcessInstanceId())) + " != ") + (newResultList.get(0).getProcessInstanceId())), resultList.get(offset).getProcessInstanceId(), newResultList.get(0).getProcessInstanceId());
    }

    @Test
    public void unionQueryBuilderTest() {
        ProcessInstanceLogQueryBuilder builder = AuditQueryTest.this.processInstanceLogQuery();
        builder.duration(pilTestData[4].getDuration());
        List<org.kie.api.runtime.manager.audit.ProcessInstanceLog> resultList = builder.build().getResultList();
        Assert.assertEquals("duration result", 1, resultList.size());
        builder.endDate(pilTestData[5].getEnd(), pilTestData[6].getEnd());
        resultList = builder.build().getResultList();
        Assert.assertEquals("union: duration OR end result", 3, resultList.size());
        builder.identity(pilTestData[7].getIdentity(), pilTestData[8].getIdentity());
        resultList = builder.build().getResultList();
        Assert.assertEquals("union: duration OR end OR identity result", 5, resultList.size());
    }

    @Test
    public void intersectQueryBuilderTest() {
        ProcessInstanceLogQueryBuilder builder = AuditQueryTest.this.processInstanceLogQuery();
        builder.intersect();
        builder.duration(pilTestData[4].getDuration());
        List<org.kie.api.runtime.manager.audit.ProcessInstanceLog> resultList = builder.build().getResultList();
        Assert.assertEquals("duration result", 1, resultList.size());
        builder.endDate(pilTestData[5].getEnd());
        resultList = builder.build().getResultList();
        Assert.assertEquals("intersect: duration AND end result", 0, resultList.size());
        builder.identity(pilTestData[6].getIdentity());
        resultList = builder.build().getResultList();
        Assert.assertEquals("intersect: duration AND end AND identity result", 0, resultList.size());
    }

    @Test
    public void intersectUnionQueryBuilderTest() {
        ProcessInstanceLogQueryBuilder builder = AuditQueryTest.this.processInstanceLogQuery();
        builder.duration(pilTestData[0].getDuration(), pilTestData[2].getDuration());
        List<org.kie.api.runtime.manager.audit.ProcessInstanceLog> resultList = builder.build().getResultList();
        Assert.assertEquals("duration result", 3, resultList.size());
        builder.intersect().endDate(pilTestData[0].getEnd());
        resultList = builder.build().getResultList();
        Assert.assertEquals("intersect: duration AND end result", 1, resultList.size());
        builder.union().processId(pilTestData[10].getProcessId());
        resultList = builder.build().getResultList();
        Assert.assertEquals("intersect/union: duration AND end OR processId result", (1 + 1), resultList.size());
    }

    @Test
    public void likeRegexQueryBuilderTest() {
        ProcessInstanceLogQueryBuilder builder = AuditQueryTest.this.processInstanceLogQuery();
        builder.like();
        boolean parameterFailed = false;
        try {
            builder.duration(pilTestData[0].getDuration(), pilTestData[2].getDuration());
        } catch (Exception e) {
            parameterFailed = true;
        }
        Assert.assertTrue("adding critera should have failed because of like()", parameterFailed);
        String regex = pilTestData[0].getIdentity();
        builder.identity(regex);
        List<org.kie.api.runtime.manager.audit.ProcessInstanceLog> resultList = builder.build().getResultList();
        Assert.assertEquals("literal regex identity result", 1, resultList.size());
        String externalId = resultList.get(0).getExternalId();
        builder = AuditQueryTest.this.processInstanceLogQuery();
        regex = (regex.substring(0, ((regex.length()) - 1))) + ".";
        builder.like().identity(regex);
        resultList = builder.build().getResultList();
        Assert.assertEquals("literal regex identity result", 1, resultList.size());
        Assert.assertEquals(externalId, resultList.get(0).getExternalId());
        builder = AuditQueryTest.this.processInstanceLogQuery();
        regex = (regex.substring(0, 10)) + "*";
        builder.like().identity(regex);
        resultList = builder.build().getResultList();
        Assert.assertEquals("literal regex identity result", 1, resultList.size());
        Assert.assertEquals(externalId, resultList.get(0).getExternalId());
        builder = AuditQueryTest.this.processInstanceLogQuery();
        String regex2 = "*" + (pilTestData[0].getIdentity().substring(10));
        builder.like().intersect().identity(regex, regex2);
        resultList = builder.build().getResultList();
        Assert.assertEquals("literal regex identity result", 1, resultList.size());
        Assert.assertEquals(externalId, resultList.get(0).getExternalId());
        builder = AuditQueryTest.this.processInstanceLogQuery();
        regex2 = "*" + (pilTestData[5].getIdentity().substring(10));
        builder.like().intersect().identity(regex, regex2);
        resultList = builder.build().getResultList();
        Assert.assertEquals("literal regex identity result", 0, resultList.size());
        builder = AuditQueryTest.this.processInstanceLogQuery();
        builder.like().union().identity(regex, regex2);
        resultList = builder.build().getResultList();
        Assert.assertEquals("literal regex identity result", 2, resultList.size());
        builder = AuditQueryTest.this.processInstanceLogQuery();
        builder.like().union().identity("*");
        resultList = builder.build().getResultList();
        Assert.assertEquals("literal regex identity result", AuditQueryTest.this.processInstanceLogQuery().build().getResultList().size(), resultList.size());
    }

    @Test
    public void rangeQueryBuilderTest() {
        ProcessInstanceLogQueryBuilder builder = AuditQueryTest.this.processInstanceLogQuery();
        long duration = pilTestData[5].getDuration();
        builder.intersect().durationMin((duration - 1)).durationMax((duration + 1));
        List<org.kie.api.runtime.manager.audit.ProcessInstanceLog> resultList = builder.build().getResultList();
        Assert.assertEquals("duration min + max result", 1, resultList.size());
        List<org.kie.api.runtime.manager.audit.ProcessInstanceLog> durationOrderedProcInstLogList = AuditQueryTest.this.processInstanceLogQuery().build().getResultList();
        Collections.sort(durationOrderedProcInstLogList, new Comparator<org.kie.api.runtime.manager.audit.ProcessInstanceLog>() {
            @Override
            public int compare(ProcessInstanceLog o1, ProcessInstanceLog o2) {
                return o1.getDuration().compareTo(o2.getDuration());
            }
        });
        int lastElemIndex = (durationOrderedProcInstLogList.size()) - 1;
        builder = AuditQueryTest.this.processInstanceLogQuery();
        long max = durationOrderedProcInstLogList.get(0).getDuration();
        builder.durationMax(max);
        resultList = builder.build().getResultList();
        AuditQueryDataUtil.verifyMaxMinDuration(resultList, AuditQueryDataUtil.MAX, max);
        builder = AuditQueryTest.this.processInstanceLogQuery();
        long min = durationOrderedProcInstLogList.get(lastElemIndex).getDuration();
        builder.durationMin(min);
        resultList = builder.build().getResultList();
        duration = resultList.get(0).getDuration();
        AuditQueryDataUtil.verifyMaxMinDuration(resultList, AuditQueryDataUtil.MIN, min);
        // union max and min
        builder = AuditQueryTest.this.processInstanceLogQuery();
        min = durationOrderedProcInstLogList.get(lastElemIndex).getDuration();
        builder.durationMin(min);
        max = durationOrderedProcInstLogList.get(0).getDuration();
        builder.durationMax(max);
        resultList = builder.build().getResultList();
        for (org.kie.api.runtime.manager.audit.ProcessInstanceLog log : resultList) {
            long dur = log.getDuration();
            Assert.assertTrue(((((("Duration " + dur) + " is neither larger than min + ") + min) + " nor smaller than max") + max), ((dur >= min) || (dur <= max)));
        }
        // empty intersection (larger than large min, smaller than small max )
        builder = AuditQueryTest.this.processInstanceLogQuery().intersect();
        min = durationOrderedProcInstLogList.get(lastElemIndex).getDuration();
        builder.durationMin(min);
        max = durationOrderedProcInstLogList.get(0).getDuration();
        builder.durationMax(max);
        resultList = builder.build().getResultList();
        AuditQueryDataUtil.verifyMaxMinDuration(resultList, AuditQueryDataUtil.BOTH, min, max);
        builder = AuditQueryTest.this.processInstanceLogQuery().intersect();
        min = durationOrderedProcInstLogList.get(2).getDuration();
        max = durationOrderedProcInstLogList.get(3).getDuration();
        builder.durationMin(min);
        builder.durationMax(max);
        resultList = builder.build().getResultList();
        // there are 2 ProcessInstanceLog's with the same duration
        AuditQueryDataUtil.verifyMaxMinDuration(resultList, AuditQueryDataUtil.BOTH, min, max);
    }

    @Test
    public void orderByQueryBuilderTest() {
        ProcessInstanceLogQueryBuilder builder = AuditQueryTest.this.processInstanceLogQuery();
        List<org.kie.api.runtime.manager.audit.ProcessInstanceLog> resultList = builder.build().getResultList();
        for (int i = 1; i < (resultList.size()); ++i) {
            ProcessInstanceLog pilB = ((ProcessInstanceLog) (resultList.get(i)));
            ProcessInstanceLog pilA = ((ProcessInstanceLog) (resultList.get((i - 1))));
            Assert.assertTrue(((pilA.getId()) < (pilB.getId())));
        }
        builder.ascending(OrderBy.processInstanceId);
        resultList = builder.build().getResultList();
        for (int i = 1; i < (resultList.size()); ++i) {
            ProcessInstanceLog pilB = ((ProcessInstanceLog) (resultList.get(i)));
            ProcessInstanceLog pilA = ((ProcessInstanceLog) (resultList.get((i - 1))));
            Assert.assertTrue(((("order by process instance id failed:  " + (pilA.getProcessInstanceId())) + " ? ") + (pilB.getProcessInstanceId())), ((pilA.getProcessInstanceId()) <= (pilB.getProcessInstanceId())));
        }
        builder.descending(OrderBy.processInstanceId);
        resultList = builder.build().getResultList();
        for (int i = 1; i < (resultList.size()); ++i) {
            ProcessInstanceLog pilB = ((ProcessInstanceLog) (resultList.get(i)));
            ProcessInstanceLog pilA = ((ProcessInstanceLog) (resultList.get((i - 1))));
            Assert.assertTrue("order desc by process instance id failed", ((pilA.getProcessInstanceId()) >= (pilB.getProcessInstanceId())));
        }
        builder.ascending(OrderBy.processId);
        resultList = builder.build().getResultList();
        for (int i = 1; i < (resultList.size()); ++i) {
            ProcessInstanceLog pilA = ((ProcessInstanceLog) (resultList.get((i - 1))));
            ProcessInstanceLog pilB = ((ProcessInstanceLog) (resultList.get(i)));
            Assert.assertTrue("order desc by process id failed", ((pilA.getProcessId().compareTo(pilB.getProcessId())) <= 0));
        }
    }

    @Test
    public void lastVariableTest() throws Exception {
        StandaloneJtaStrategy jtaHelper = new StandaloneJtaStrategy(AuditQueryTest.emf);
        EntityManager em = jtaHelper.getEntityManager();
        int numLogs = 10;
        VariableInstanceLog[] testData = new VariableInstanceLog[numLogs];
        Calendar cal = GregorianCalendar.getInstance();
        for (int i = 0; i < 5; ++i) {
            cal.roll(Calendar.SECOND, 1);
            testData[i] = new VariableInstanceLog(23L, "org.lots.of.vars", "inst", "first-var", "val-a", ("oldVal-" + i));
            testData[(i + 5)] = new VariableInstanceLog(23L, "org.lots.of.vars", "inst", "second-var", "val-b", ("oldVal-" + i));
            testData[i].setDate(cal.getTime());
            testData[(i + 5)].setDate(cal.getTime());
        }
        Object tx = jtaHelper.joinTransaction(em);
        for (int i = 0; i < numLogs; ++i) {
            em.persist(testData[i]);
        }
        jtaHelper.leaveTransaction(em, tx);
        VariableInstanceLogQueryBuilder queryBuilder;
        ParametrizedQuery<org.kie.api.runtime.manager.audit.VariableInstanceLog> query;
        List<org.kie.api.runtime.manager.audit.VariableInstanceLog> logs;
        queryBuilder = AuditQueryTest.this.variableInstanceLogQuery();
        query = queryBuilder.last().intersect().processInstanceId(23L).build();
        logs = query.getResultList();
        Assert.assertEquals("2 logs", 2, logs.size());
        queryBuilder = AuditQueryTest.this.variableInstanceLogQuery();
        query = queryBuilder.value("val-a").intersect().last().build();
        logs = query.getResultList();
        Assert.assertEquals("Only 1 log expected", 1, logs.size());
        Assert.assertEquals("Incorrect variable val", "val-a", logs.get(0).getValue());
        Assert.assertEquals("Incorrect variable old val", "oldVal-4", logs.get(0).getOldValue());
    }

    @Test
    public void variableValueTest() throws Exception {
        StandaloneJtaStrategy jtaHelper = new StandaloneJtaStrategy(AuditQueryTest.emf);
        EntityManager em = jtaHelper.getEntityManager();
        int numLogs = 9;
        VariableInstanceLog[] testData = new VariableInstanceLog[numLogs];
        Calendar cal = GregorianCalendar.getInstance();
        String processId = "org.variable.value";
        for (int i = 0; i < testData; ++i) {
            cal.roll(Calendar.SECOND, 1);
            testData[i] = new VariableInstanceLog(AuditQueryDataUtil.randomLong(), processId, "varInstId", ("var-" + i), ("val-" + i), ("oldVal-" + i));
        }
        Object tx = jtaHelper.joinTransaction(em);
        for (int i = 0; i < numLogs; ++i) {
            em.persist(testData[i]);
        }
        jtaHelper.leaveTransaction(em, tx);
        VariableInstanceLogQueryBuilder queryBuilder;
        ParametrizedQuery<org.kie.api.runtime.manager.audit.VariableInstanceLog> query;
        List<org.kie.api.runtime.manager.audit.VariableInstanceLog> logs;
        // check
        queryBuilder = AuditQueryTest.this.variableInstanceLogQuery();
        query = queryBuilder.processId(processId).build();
        logs = query.getResultList();
        Assert.assertEquals((numLogs + " logs expected"), numLogs, logs.size());
        // control: don't find any
        queryBuilder = AuditQueryTest.this.variableInstanceLogQuery().intersect().processId(processId);
        query = queryBuilder.variableValue("var-1", "val-2").build();
        logs = query.getResultList();
        Assert.assertEquals("No logs expected", 0, logs.size());
        // control: don't find any
        queryBuilder = AuditQueryTest.this.variableInstanceLogQuery().intersect().processId(processId);
        query = queryBuilder.variableValue("var-1", "val-1").variableValue("var-2", "val-2").build();
        logs = query.getResultList();
        Assert.assertEquals("No logs expected", 0, logs.size());
        // find 1
        queryBuilder = AuditQueryTest.this.variableInstanceLogQuery();
        query = queryBuilder.union().variableValue("var-1", "val-1").build();
        logs = query.getResultList();
        Assert.assertEquals("1 log expected", 1, logs.size());
        Assert.assertEquals("Incorrect variable val", "val-1", logs.get(0).getValue());
        Assert.assertEquals("Incorrect variable id", "var-1", logs.get(0).getVariableId());
        // find 2
        queryBuilder = AuditQueryTest.this.variableInstanceLogQuery();
        query = queryBuilder.union().variableValue("var-2", "val-2").variableValue("var-4", "val-4").build();
        logs = query.getResultList();
        Assert.assertEquals("2 log expected", 2, logs.size());
        for (org.kie.api.runtime.manager.audit.VariableInstanceLog varLog : logs) {
            String id = varLog.getVariableId().substring("var-".length());
            Assert.assertEquals("variable value", ("val-" + id), varLog.getValue());
        }
        // regex: find 1
        queryBuilder = AuditQueryTest.this.variableInstanceLogQuery();
        query = queryBuilder.like().variableValue("var-2", "val-*").build();
        logs = query.getResultList();
        Assert.assertEquals("1 log expected", 1, logs.size());
        Assert.assertEquals("Incorrect variable val", "val-2", logs.get(0).getValue());
        Assert.assertEquals("Incorrect variable id", "var-2", logs.get(0).getVariableId());
        // regex: find 2
        queryBuilder = AuditQueryTest.this.variableInstanceLogQuery();
        query = queryBuilder.like().union().variableValue("var-2", "val-*").variableValue("var-3", "val-*").build();
        logs = query.getResultList();
        Assert.assertEquals("2 log expected", 2, logs.size());
        for (org.kie.api.runtime.manager.audit.VariableInstanceLog varLog : logs) {
            String id = varLog.getVariableId().substring("var-".length());
            Assert.assertEquals("variable value", ("val-" + id), varLog.getValue());
        }
        // regex: find 2 with last
        queryBuilder = AuditQueryTest.this.variableInstanceLogQuery();
        query = queryBuilder.newGroup().like().union().variableValue("var-2", "val-*").variableValue("var-3", "val-*").endGroup().equals().intersect().last().build();
        logs = query.getResultList();
        Assert.assertEquals("2 log expected", 2, logs.size());
        for (org.kie.api.runtime.manager.audit.VariableInstanceLog varLog : logs) {
            String id = varLog.getVariableId().substring("var-".length());
            Assert.assertEquals("variable value", ("val-" + id), varLog.getValue());
        }
    }
}

