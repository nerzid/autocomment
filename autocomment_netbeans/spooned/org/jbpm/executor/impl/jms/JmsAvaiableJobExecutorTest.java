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


package org.jbpm.executor.impl.jms;

import org.junit.After;
import java.util.ArrayList;
import org.junit.Assert;
import org.jbpm.executor.AsynchronousJobEvent;
import org.jbpm.executor.AsynchronousJobListener;
import org.junit.Before;
import org.kie.api.executor.CommandContext;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import org.jbpm.executor.test.CountDownAsyncJobListener;
import org.hornetq.jms.server.embedded.EmbeddedJMS;
import javax.persistence.EntityManagerFactory;
import org.jbpm.executor.impl.ExecutorImpl;
import org.kie.api.executor.ExecutorService;
import org.jbpm.executor.ExecutorServiceFactory;
import org.jbpm.executor.impl.ExecutorServiceImpl;
import org.jbpm.test.util.ExecutorTestUtil;
import javax.naming.InitialContext;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.persistence.Persistence;
import bitronix.tm.resource.jms.PoolingConnectionFactory;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import javax.jms.Queue;
import org.kie.api.executor.RequestInfo;
import javax.jms.Session;
import org.junit.Test;
import java.util.UUID;
import javax.transaction.UserTransaction;
import javax.jms.XAConnectionFactory;

public class JmsAvaiableJobExecutorTest {
    private static final Logger logger = LoggerFactory.getLogger(JmsAvaiableJobExecutorTest.class);

    private ConnectionFactory factory;

    private Queue queue;

    private EmbeddedJMS jmsServer;

    protected ExecutorService executorService;

    protected PoolingDataSource pds;

    protected EntityManagerFactory emf = null;

    @Before
    public void setUp() throws Exception {
        startHornetQServer();
        pds = ExecutorTestUtil.setupPoolingDataSource();
        emf = Persistence.createEntityManagerFactory("org.jbpm.executor");
        executorService = ExecutorServiceFactory.newExecutorService(emf);
        ((ExecutorImpl) (((ExecutorServiceImpl) (executorService)).getExecutor())).setConnectionFactory(factory);
        ((ExecutorImpl) (((ExecutorServiceImpl) (executorService)).getExecutor())).setQueue(queue);
        executorService.setThreadPoolSize(0);
        executorService.setInterval(10000);
        executorService.init();
    }

    @After
    public void tearDown() throws Exception {
        executorService.clearAllRequests();
        executorService.clearAllErrors();
        executorService.destroy();
        if ((emf) != null) {
            emf.close();
        } 
        pds.close();
        System.clearProperty("org.kie.executor.msg.length");
        System.clearProperty("org.kie.executor.stacktrace.length");
        stopHornetQServer();
    }

    protected CountDownAsyncJobListener configureListener(int threads) {
        CountDownAsyncJobListener countDownListener = new CountDownAsyncJobListener(threads);
        ((ExecutorServiceImpl) (executorService)).addAsyncJobListener(countDownListener);
        return countDownListener;
    }

    @Test
    public void testAsyncAuditProducer() throws Exception {
        CountDownAsyncJobListener countDownListener = configureListener(1);
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", ctxCMD);
        ut.commit();
        JmsAvaiableJobExecutorTest.MessageReceiver receiver = new JmsAvaiableJobExecutorTest.MessageReceiver();
        receiver.receiveAndProcess(queue, countDownListener);
        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new org.kie.api.runtime.query.QueryContext());
        Assert.assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executorService.getQueuedRequests(new org.kie.api.runtime.query.QueryContext());
        Assert.assertEquals(0, queuedRequests.size());
        List<RequestInfo> executedRequests = executorService.getCompletedRequests(new org.kie.api.runtime.query.QueryContext());
        Assert.assertEquals(1, executedRequests.size());
    }

    @Test
    public void testAsyncAuditProducerPrioritizedJobs() throws Exception {
        CountDownAsyncJobListener countDownListener = configureListener(2);
        final List<String> executedJobs = new ArrayList<String>();
        ((ExecutorServiceImpl) (executorService)).addAsyncJobListener(new AsynchronousJobListener() {
            @Override
            public void beforeJobScheduled(AsynchronousJobEvent event) {
            }

            @Override
            public void beforeJobExecuted(AsynchronousJobEvent event) {
            }

            @Override
            public void beforeJobCancelled(AsynchronousJobEvent event) {
            }

            @Override
            public void afterJobScheduled(AsynchronousJobEvent event) {
            }

            @Override
            public void afterJobExecuted(AsynchronousJobEvent event) {
                executedJobs.add(event.getJob().getKey());
            }

            @Override
            public void afterJobCancelled(AsynchronousJobEvent event) {
            }
        });
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", "low priority");
        ctxCMD.setData("priority", 2);
        CommandContext ctxCMD2 = new CommandContext();
        ctxCMD2.setData("businessKey", "high priority");
        ctxCMD2.setData("priority", 8);
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", ctxCMD);
        executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", ctxCMD2);
        ut.commit();
        JmsAvaiableJobExecutorTest.MessageReceiver receiver = new JmsAvaiableJobExecutorTest.MessageReceiver();
        receiver.receiveAndProcess(queue, countDownListener);
        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new org.kie.api.runtime.query.QueryContext());
        Assert.assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executorService.getQueuedRequests(new org.kie.api.runtime.query.QueryContext());
        Assert.assertEquals(0, queuedRequests.size());
        List<RequestInfo> executedRequests = executorService.getCompletedRequests(new org.kie.api.runtime.query.QueryContext());
        Assert.assertEquals(2, executedRequests.size());
        Assert.assertEquals(2, executedJobs.size());
        Assert.assertEquals("high priority", executedJobs.get(0));
        Assert.assertEquals("low priority", executedJobs.get(1));
    }

    private void startHornetQServer() throws Exception {
        jmsServer = new EmbeddedJMS();
        jmsServer.start();
        JmsAvaiableJobExecutorTest.logger.debug("Started Embedded JMS Server");
        BitronixHornetQXAConnectionFactory.connectionFactory = ((XAConnectionFactory) (jmsServer.lookup("ConnectionFactory")));
        PoolingConnectionFactory myConnectionFactory = new PoolingConnectionFactory();
        myConnectionFactory.setClassName("org.jbpm.executor.impl.jms.BitronixHornetQXAConnectionFactory");
        myConnectionFactory.setUniqueName("ConnectionFactory");
        myConnectionFactory.setMaxPoolSize(5);
        myConnectionFactory.setAllowLocalTransactions(true);
        myConnectionFactory.init();
        factory = myConnectionFactory;
        queue = ((Queue) (jmsServer.lookup("/queue/exampleQueue")));
    }

    private void stopHornetQServer() throws Exception {
        ((PoolingConnectionFactory) (factory)).close();
        jmsServer.stop();
        jmsServer = null;
    }

    private class MessageReceiver {
        void receiveAndProcess(Queue queue, CountDownAsyncJobListener countDownListener) throws Exception {
            Connection qconnetion = factory.createConnection();
            Session qsession = qconnetion.createSession(true, QueueSession.AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = qsession.createConsumer(queue);
            qconnetion.start();
            JmsAvailableJobsExecutor jmsExecutor = new JmsAvailableJobsExecutor();
            jmsExecutor.setClassCacheManager(new org.jbpm.executor.impl.ClassCacheManager());
            jmsExecutor.setExecutorStoreService(((ExecutorImpl) (((ExecutorServiceImpl) (executorService)).getExecutor())).getExecutorStoreService());
            jmsExecutor.setQueryService(((ExecutorServiceImpl) (executorService)).getQueryService());
            jmsExecutor.setEventSupport(((ExecutorServiceImpl) (executorService)).getEventSupport());
            consumer.setMessageListener(jmsExecutor);
            // since we use message listener allow it to complete the async processing
            countDownListener.waitTillCompleted();
            consumer.close();
            qsession.close();
            qconnetion.close();
        }

        public List<Message> receive(Queue queue) throws Exception {
            List<Message> messages = new ArrayList<Message>();
            Connection qconnetion = factory.createConnection();
            Session qsession = qconnetion.createSession(true, QueueSession.AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = qsession.createConsumer(queue);
            qconnetion.start();
            Message m = null;
            while ((m = consumer.receiveNoWait()) != null) {
                messages.add(m);
            }
            consumer.close();
            qsession.close();
            qconnetion.close();
            return messages;
        }
    }
}

