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


package org.jbpm.test.functional.timer;

import java.util.ArrayList;
import org.junit.Before;
import java.sql.Blob;
import java.util.Collection;
import java.sql.Connection;
import javax.persistence.EntityManager;
import java.util.HashMap;
import javax.naming.InitialContext;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.test.JbpmTestCase;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.kie.api.runtime.process.ProcessInstance;
import org.drools.core.marshalling.impl.ProtobufMarshaller;
import java.sql.ResultSet;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import java.sql.Statement;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.junit.Test;
import org.jbpm.process.instance.timer.TimerInstance;
import org.jbpm.process.instance.timer.TimerManager;
import javax.transaction.TransactionManager;
import bitronix.tm.TransactionManagerServices;
import javax.transaction.UserTransaction;

public class SerializedTimerRollbackTest extends JbpmTestCase {
    private static final Logger logger = LoggerFactory.getLogger(SerializedTimerRollbackTest.class);

    public SerializedTimerRollbackTest() {
        super(true, true);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        try {
            ut.begin();
            EntityManager em = getEmf().createEntityManager();
            em.createQuery("delete from SessionInfo").executeUpdate();
            em.close();
            ut.commit();
        } catch (Exception e) {
            ut.rollback();
            SerializedTimerRollbackTest.logger.error("Something went wrong deleting the Session Info", e);
        }
    }

    @Test
    public void testSerizliableTestsWithExternalRollback() {
        try {
            createRuntimeManager("org/jbpm/test/functional/timer/HumanTaskWithBoundaryTimer.bpmn");
            RuntimeEngine runtimeEngine = getRuntimeEngine();
            KieSession ksession = runtimeEngine.getKieSession();
            TaskService taskService = runtimeEngine.getTaskService();
            SerializedTimerRollbackTest.logger.debug("Created knowledge session");
            TransactionManager tm = TransactionManagerServices.getTransactionManager();
            List<Long> committedProcessInstanceIds = new ArrayList<Long>();
            for (int i = 0; i < 10; i++) {
                tm.begin();
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("test", "john");
                SerializedTimerRollbackTest.logger.debug("Creating process instance: {}", i);
                ProcessInstance pi = ksession.startProcess("PROCESS_1", params);
                if ((i % 2) == 0) {
                    committedProcessInstanceIds.add(pi.getId());
                    tm.commit();
                } else {
                    tm.rollback();
                }
            }
            Connection c = getDs().getConnection();
            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery("select rulesbytearray from sessioninfo");
            rs.next();
            Blob b = rs.getBlob("rulesbytearray");
            assertNotNull(b);
            KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            ProtobufMarshaller marshaller = new ProtobufMarshaller(builder.newKnowledgeBase(), new org.drools.core.marshalling.impl.MarshallingConfigurationImpl());
            StatefulKnowledgeSession session = marshaller.unmarshall(b.getBinaryStream());
            assertNotNull(session);
            TimerManager timerManager = ((InternalProcessRuntime) (((InternalKnowledgeRuntime) (session)).getProcessRuntime())).getTimerManager();
            assertNotNull(timerManager);
            Collection<TimerInstance> timers = timerManager.getTimers();
            assertNotNull(timers);
            assertEquals(5, timers.size());
            for (TimerInstance timerInstance : timers) {
                assertTrue(committedProcessInstanceIds.contains(timerInstance.getProcessInstanceId()));
                ksession.abortProcessInstance(timerInstance.getProcessInstanceId());
            }
            List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
            assertEquals(0, tasks.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown");
        }
    }

    @Test
    public void testSerizliableTestsWithEngineRollback() {
        try {
            createRuntimeManager("org/jbpm/test/functional/timer/HumanTaskWithBoundaryTimer.bpmn");
            RuntimeEngine runtimeEngine = getRuntimeEngine();
            KieSession ksession = runtimeEngine.getKieSession();
            SerializedTimerRollbackTest.logger.debug("Created knowledge session");
            TaskService taskService = runtimeEngine.getTaskService();
            SerializedTimerRollbackTest.logger.debug("Task service created");
            List<Long> committedProcessInstanceIds = new ArrayList<Long>();
            for (int i = 0; i < 10; i++) {
                if ((i % 2) == 0) {
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("test", "john");
                    SerializedTimerRollbackTest.logger.debug("Creating process instance: {}", i);
                    ProcessInstance pi = ksession.startProcess("PROCESS_1", params);
                    committedProcessInstanceIds.add(pi.getId());
                } else {
                    try {
                        Map<String, Object> params = new HashMap<String, Object>();
                        // set test variable to null so engine will rollback
                        params.put("test", null);
                        SerializedTimerRollbackTest.logger.debug("Creating process instance: {}", i);
                        ksession.startProcess("PROCESS_1", params);
                    } catch (Exception e) {
                        SerializedTimerRollbackTest.logger.debug("Process rolled back");
                    }
                }
            }
            Connection c = getDs().getConnection();
            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery("select rulesbytearray from sessioninfo");
            rs.next();
            Blob b = rs.getBlob("rulesbytearray");
            assertNotNull(b);
            KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            ProtobufMarshaller marshaller = new ProtobufMarshaller(builder.newKnowledgeBase(), new org.drools.core.marshalling.impl.MarshallingConfigurationImpl());
            StatefulKnowledgeSession session = marshaller.unmarshall(b.getBinaryStream());
            assertNotNull(session);
            TimerManager timerManager = ((InternalProcessRuntime) (((InternalKnowledgeRuntime) (session)).getProcessRuntime())).getTimerManager();
            assertNotNull(timerManager);
            Collection<TimerInstance> timers = timerManager.getTimers();
            assertNotNull(timers);
            assertEquals(5, timers.size());
            for (TimerInstance timerInstance : timers) {
                assertTrue(committedProcessInstanceIds.contains(timerInstance.getProcessInstanceId()));
                ksession.abortProcessInstance(timerInstance.getProcessInstanceId());
            }
            List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
            assertEquals(0, tasks.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown");
        }
    }
}

