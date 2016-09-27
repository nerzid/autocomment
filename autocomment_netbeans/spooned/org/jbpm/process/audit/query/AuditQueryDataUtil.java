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

import org.junit.Assert;
import java.util.Calendar;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.GregorianCalendar;
import java.util.List;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.process.audit.ProcessInstanceLog;
import java.util.Random;
import org.jbpm.process.audit.strategy.StandaloneJtaStrategy;
import java.util.UUID;
import org.jbpm.process.audit.VariableInstanceLog;

public class AuditQueryDataUtil {
    private static Random random = new Random();

    static long randomLong() {
        long result = ((long) (Math.abs(AuditQueryDataUtil.random.nextInt())));
        while (result == 23L) {
            result = ((long) (Math.abs(AuditQueryDataUtil.random.nextInt())));
        }
        return result;
    }

    static String randomString() {
        return UUID.randomUUID().toString();
    }

    static Calendar randomCal() {
        Calendar cal = GregorianCalendar.getInstance();
        cal.roll(Calendar.DAY_OF_YEAR, ((-1) * (AuditQueryDataUtil.random.nextInt((10 * 365)))));
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        return cal;
    }

    static ProcessInstanceLog[] createTestProcessInstanceLogData(EntityManagerFactory emf) {
        StandaloneJtaStrategy jtaHelper = new StandaloneJtaStrategy(emf);
        EntityManager em = jtaHelper.getEntityManager();
        int numEntities = 11;
        ProcessInstanceLog[] testData = new ProcessInstanceLog[numEntities];
        Calendar cal = AuditQueryDataUtil.randomCal();
        for (int i = 0; i < numEntities; ++i) {
            ProcessInstanceLog pil = new ProcessInstanceLog(AuditQueryDataUtil.randomLong(), AuditQueryDataUtil.randomString());
            pil.setDuration(AuditQueryDataUtil.randomLong());
            pil.setExternalId(AuditQueryDataUtil.randomString());
            pil.setIdentity(AuditQueryDataUtil.randomString());
            pil.setOutcome(AuditQueryDataUtil.randomString());
            pil.setParentProcessInstanceId(AuditQueryDataUtil.randomLong());
            pil.setProcessId(AuditQueryDataUtil.randomString());
            pil.setProcessName(AuditQueryDataUtil.randomString());
            pil.setProcessVersion(AuditQueryDataUtil.randomString());
            pil.setStatus(AuditQueryDataUtil.random.nextInt());
            pil.setCorrelationKey(AuditQueryDataUtil.randomString());
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

    static VariableInstanceLog[] createTestVariableInstanceLogData(EntityManagerFactory emf) {
        StandaloneJtaStrategy jtaHelper = new StandaloneJtaStrategy(emf);
        EntityManager em = jtaHelper.getEntityManager();
        int numEntities = 8;
        VariableInstanceLog[] testData = new VariableInstanceLog[numEntities];
        Calendar cal = AuditQueryDataUtil.randomCal();
        for (int i = 0; i < numEntities; ++i) {
            VariableInstanceLog vil = new VariableInstanceLog();
            vil.setProcessInstanceId(AuditQueryDataUtil.randomLong());
            vil.setProcessId(AuditQueryDataUtil.randomString());
            cal.add(Calendar.MINUTE, 1);
            vil.setDate(cal.getTime());
            vil.setVariableInstanceId(AuditQueryDataUtil.randomString());
            vil.setVariableId(AuditQueryDataUtil.randomString());
            vil.setValue(AuditQueryDataUtil.randomString());
            vil.setOldValue(AuditQueryDataUtil.randomString());
            vil.setExternalId(AuditQueryDataUtil.randomString());
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

    static NodeInstanceLog[] createTestNodeInstanceLogData(EntityManagerFactory emf) {
        StandaloneJtaStrategy jtaHelper = new StandaloneJtaStrategy(emf);
        EntityManager em = jtaHelper.getEntityManager();
        int numEntities = 9;
        NodeInstanceLog[] testData = new NodeInstanceLog[numEntities];
        Calendar cal = AuditQueryDataUtil.randomCal();
        for (int i = 0; i < numEntities; ++i) {
            NodeInstanceLog nil = new NodeInstanceLog();
            nil.setProcessInstanceId(AuditQueryDataUtil.randomLong());
            nil.setProcessId(AuditQueryDataUtil.randomString());
            cal.add(Calendar.SECOND, 1);
            nil.setDate(cal.getTime());
            nil.setType(Math.abs(AuditQueryDataUtil.random.nextInt()));
            nil.setNodeInstanceId(AuditQueryDataUtil.randomString());
            nil.setNodeId(AuditQueryDataUtil.randomString());
            nil.setNodeName(AuditQueryDataUtil.randomString());
            nil.setNodeType(AuditQueryDataUtil.randomString());
            nil.setWorkItemId(AuditQueryDataUtil.randomLong());
            nil.setConnection(AuditQueryDataUtil.randomString());
            nil.setExternalId(AuditQueryDataUtil.randomString());
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

    static int MAX = 2;

    static int MIN = 1;

    static int BOTH = 0;

    static void verifyMaxMinDuration(List<ProcessInstanceLog> procInstLogs, int test, long... maxOrMin) {
        for (org.kie.api.runtime.manager.audit.ProcessInstanceLog log : procInstLogs) {
            Assert.assertNotNull("Duration is null", log.getDuration());
            long dur = log.getDuration();
            if (test == (AuditQueryDataUtil.MAX)) {
                Assert.assertTrue(((((("Duration " + dur) + " is larger than max ") + (maxOrMin[0])) + ": ") + dur), (dur <= (maxOrMin[0])));
            } else if (test == (AuditQueryDataUtil.MIN)) {
                Assert.assertTrue(((("Duration " + dur) + " is smaller than min ") + (maxOrMin[0])), (dur >= (maxOrMin[0])));
            } else {
                // BOTH
                Assert.assertTrue(((("Duration " + dur) + " is smaller than min ") + (maxOrMin[0])), (dur >= (maxOrMin[0])));
                Assert.assertTrue(((("Duration " + dur) + " is larger than max ") + (maxOrMin[1])), (dur <= (maxOrMin[1])));
            }
        }
    }
}

