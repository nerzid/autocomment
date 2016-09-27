/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.executor;

import org.junit.After;
import org.junit.Assert;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Before;
import java.io.ByteArrayInputStream;
import java.util.Calendar;
import org.kie.api.executor.CommandContext;
import org.jbpm.executor.test.CountDownAsyncJobListener;
import javax.persistence.EntityManagerFactory;
import org.kie.api.executor.ErrorInfo;
import org.kie.api.executor.ExecutionResults;
import org.jbpm.executor.impl.jpa.ExecutorJPAAuditService;
import org.kie.api.executor.ExecutorService;
import org.jbpm.executor.impl.ExecutorServiceImpl;
import java.util.HashMap;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.io.ObjectInputStream;
import org.kie.api.runtime.query.QueryContext;
import org.kie.api.executor.RequestInfo;
import org.junit.Test;
import java.util.UUID;

public abstract class BasicExecutorBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(BasicExecutorBaseTest.class);

    protected ExecutorService executorService;

    public static final Map<String, Object> cachedEntities = new HashMap<String, Object>();

    protected EntityManagerFactory emf = null;

    @Before
    public void setUp() {
        executorService.setThreadPoolSize(1);
        executorService.setInterval(3);
    }

    @After
    public void tearDown() {
        executorService.clearAllRequests();
        executorService.clearAllErrors();
        System.clearProperty("org.kie.executor.msg.length");
        System.clearProperty("org.kie.executor.stacktrace.length");
    }

    protected CountDownAsyncJobListener configureListener(int threads) {
        CountDownAsyncJobListener countDownListener = new CountDownAsyncJobListener(threads);
        ((ExecutorServiceImpl) (executorService)).addAsyncJobListener(countDownListener);
        return countDownListener;
    }

    @Test
    public void simpleExecutionTest() throws InterruptedException {
        CountDownAsyncJobListener countDownListener = configureListener(1);
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());
        executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", ctxCMD);
        countDownListener.waitTillCompleted();
        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        Assert.assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executorService.getQueuedRequests(new QueryContext());
        Assert.assertEquals(0, queuedRequests.size());
        List<RequestInfo> executedRequests = executorService.getCompletedRequests(new QueryContext());
        Assert.assertEquals(1, executedRequests.size());
    }

    @Test
    public void callbackTest() throws InterruptedException {
        CountDownAsyncJobListener countDownListener = configureListener(1);
        CommandContext commandContext = new CommandContext();
        commandContext.setData("businessKey", UUID.randomUUID().toString());
        BasicExecutorBaseTest.cachedEntities.put(((String) (commandContext.getData("businessKey"))), new AtomicLong(1));
        commandContext.setData("callbacks", "org.jbpm.executor.SimpleIncrementCallback");
        executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", commandContext);
        countDownListener.waitTillCompleted();
        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        Assert.assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executorService.getQueuedRequests(new QueryContext());
        Assert.assertEquals(0, queuedRequests.size());
        List<RequestInfo> executedRequests = executorService.getCompletedRequests(new QueryContext());
        Assert.assertEquals(1, executedRequests.size());
        Assert.assertEquals(2, ((AtomicLong) (BasicExecutorBaseTest.cachedEntities.get(((String) (commandContext.getData("businessKey")))))).longValue());
    }

    @Test
    public void addAnotherCallbackTest() throws InterruptedException {
        CountDownAsyncJobListener countDownListener = configureListener(1);
        CommandContext commandContext = new CommandContext();
        commandContext.setData("businessKey", UUID.randomUUID().toString());
        BasicExecutorBaseTest.cachedEntities.put(((String) (commandContext.getData("businessKey"))), new AtomicLong(1));
        commandContext.setData("callbacks", "org.jbpm.executor.SimpleIncrementCallback");
        executorService.scheduleRequest("org.jbpm.executor.test.AddAnotherCallbackCommand", commandContext);
        countDownListener.waitTillCompleted();
        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        Assert.assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executorService.getQueuedRequests(new QueryContext());
        Assert.assertEquals(0, queuedRequests.size());
        List<RequestInfo> executedRequests = executorService.getCompletedRequests(new QueryContext());
        Assert.assertEquals(1, executedRequests.size());
        Assert.assertEquals(2, ((AtomicLong) (BasicExecutorBaseTest.cachedEntities.get(((String) (commandContext.getData("businessKey")))))).longValue());
        ExecutionResults results = null;
        byte[] responseData = executedRequests.get(0).getResponseData();
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new ByteArrayInputStream(responseData));
            results = ((ExecutionResults) (in.readObject()));
        } catch (Exception e) {
            BasicExecutorBaseTest.logger.warn("Exception while serializing context data", e);
            return ;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } 
        }
        String result = ((String) (results.getData("custom")));
        Assert.assertNotNull(result);
        Assert.assertEquals("custom callback invoked", result);
    }

    @Test
    public void multipleCallbackTest() throws InterruptedException {
        CountDownAsyncJobListener countDownListener = configureListener(1);
        CommandContext commandContext = new CommandContext();
        commandContext.setData("businessKey", UUID.randomUUID().toString());
        BasicExecutorBaseTest.cachedEntities.put(((String) (commandContext.getData("businessKey"))), new AtomicLong(1));
        commandContext.setData("callbacks", "org.jbpm.executor.SimpleIncrementCallback, org.jbpm.executor.test.CustomCallback");
        executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", commandContext);
        countDownListener.waitTillCompleted();
        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        Assert.assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executorService.getQueuedRequests(new QueryContext());
        Assert.assertEquals(0, queuedRequests.size());
        List<RequestInfo> executedRequests = executorService.getCompletedRequests(new QueryContext());
        Assert.assertEquals(1, executedRequests.size());
        Assert.assertEquals(2, ((AtomicLong) (BasicExecutorBaseTest.cachedEntities.get(((String) (commandContext.getData("businessKey")))))).longValue());
        ExecutionResults results = null;
        byte[] responseData = executedRequests.get(0).getResponseData();
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new ByteArrayInputStream(responseData));
            results = ((ExecutionResults) (in.readObject()));
        } catch (Exception e) {
            BasicExecutorBaseTest.logger.warn("Exception while serializing context data", e);
            return ;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } 
        }
        String result = ((String) (results.getData("custom")));
        Assert.assertNotNull(result);
        Assert.assertEquals("custom callback invoked", result);
    }

    @Test
    public void executorExceptionTest() throws InterruptedException {
        CountDownAsyncJobListener countDownListener = configureListener(1);
        CommandContext commandContext = new CommandContext();
        commandContext.setData("businessKey", UUID.randomUUID().toString());
        BasicExecutorBaseTest.cachedEntities.put(((String) (commandContext.getData("businessKey"))), new AtomicLong(1));
        commandContext.setData("callbacks", "org.jbpm.executor.SimpleIncrementCallback");
        commandContext.setData("retries", 0);
        executorService.scheduleRequest("org.jbpm.executor.ThrowExceptionCommand", commandContext);
        BasicExecutorBaseTest.logger.info("{} Sleeping for 10 secs", System.currentTimeMillis());
        countDownListener.waitTillCompleted();
        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        Assert.assertEquals(1, inErrorRequests.size());
        BasicExecutorBaseTest.logger.info("Error: {}", inErrorRequests.get(0));
        List<ErrorInfo> errors = executorService.getAllErrors(new QueryContext());
        BasicExecutorBaseTest.logger.info("Errors: {}", errors);
        Assert.assertEquals(1, errors.size());
    }

    @Test
    public void defaultRequestRetryTest() throws InterruptedException {
        CountDownAsyncJobListener countDownListener = configureListener(4);
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());
        executorService.scheduleRequest("org.jbpm.executor.ThrowExceptionCommand", ctxCMD);
        countDownListener.waitTillCompleted();
        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        Assert.assertEquals(1, inErrorRequests.size());
        List<ErrorInfo> errors = executorService.getAllErrors(new QueryContext());
        BasicExecutorBaseTest.logger.info("Errors: {}", errors);
        // Three retries means 4 executions in total 1(regular) + 3(retries)
        Assert.assertEquals(4, errors.size());
    }

    @Test
    public void cancelRequestTest() throws InterruptedException {
        // The executor is on purpose not started to not fight against race condition
        // with the request cancelations.
        CommandContext ctxCMD = new CommandContext();
        String businessKey = UUID.randomUUID().toString();
        ctxCMD.setData("businessKey", businessKey);
        Long requestId = executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", ctxCMD);
        List<RequestInfo> requests = executorService.getRequestsByBusinessKey(businessKey, new QueryContext());
        Assert.assertNotNull(requests);
        Assert.assertEquals(1, requests.size());
        Assert.assertEquals(requestId, requests.get(0).getId());
        // cancel the task immediately
        executorService.cancelRequest(requestId);
        List<RequestInfo> cancelledRequests = executorService.getCancelledRequests(new QueryContext());
        Assert.assertEquals(1, cancelledRequests.size());
    }

    @Test
    public void executorExceptionTrimmingTest() throws InterruptedException {
        System.setProperty("org.kie.executor.msg.length", "10");
        System.setProperty("org.kie.executor.stacktrace.length", "20");
        CountDownAsyncJobListener countDownListener = configureListener(1);
        CommandContext commandContext = new CommandContext();
        commandContext.setData("businessKey", UUID.randomUUID().toString());
        BasicExecutorBaseTest.cachedEntities.put(((String) (commandContext.getData("businessKey"))), new AtomicLong(1));
        commandContext.setData("callbacks", "org.jbpm.executor.SimpleIncrementCallback");
        commandContext.setData("retries", 0);
        executorService.scheduleRequest("org.jbpm.executor.ThrowExceptionCommand", commandContext);
        BasicExecutorBaseTest.logger.info("{} Sleeping for 10 secs", System.currentTimeMillis());
        countDownListener.waitTillCompleted();
        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        Assert.assertEquals(1, inErrorRequests.size());
        BasicExecutorBaseTest.logger.info("Error: {}", inErrorRequests.get(0));
        List<ErrorInfo> errors = executorService.getAllErrors(new QueryContext());
        BasicExecutorBaseTest.logger.info("Errors: {}", errors);
        Assert.assertEquals(1, errors.size());
        ErrorInfo error = errors.get(0);
        Assert.assertEquals(10, error.getMessage().length());
        Assert.assertEquals(20, error.getStacktrace().length());
    }

    @Test
    public void reoccurringExecutionTest() throws InterruptedException {
        CountDownAsyncJobListener countDownListener = configureListener(3);
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());
        executorService.scheduleRequest("org.jbpm.executor.commands.ReoccurringPrintOutCommand", ctxCMD);
        countDownListener.waitTillCompleted();
        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        Assert.assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executorService.getQueuedRequests(new QueryContext());
        Assert.assertEquals(1, queuedRequests.size());
        List<RequestInfo> executedRequests = executorService.getCompletedRequests(new QueryContext());
        Assert.assertEquals(3, executedRequests.size());
    }

    @Test
    public void cleanupLogExecutionTest() throws InterruptedException {
        CountDownAsyncJobListener countDownListener = configureListener(3);
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());
        Long requestId = executorService.scheduleRequest("org.jbpm.executor.commands.ReoccurringPrintOutCommand", ctxCMD);
        countDownListener.waitTillCompleted();
        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        Assert.assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executorService.getQueuedRequests(new QueryContext());
        Assert.assertEquals(1, queuedRequests.size());
        List<RequestInfo> executedRequests = executorService.getCompletedRequests(new QueryContext());
        Assert.assertEquals(3, executedRequests.size());
        executorService.cancelRequest((requestId + 3));
        List<RequestInfo> canceled = executorService.getCancelledRequests(new QueryContext());
        ExecutorJPAAuditService auditService = new ExecutorJPAAuditService(emf);
        int resultCount = auditService.requestInfoLogDeleteBuilder().date(canceled.get(0).getTime()).status(STATUS.ERROR).build().execute();
        Assert.assertEquals(0, resultCount);
        resultCount = auditService.errorInfoLogDeleteBuilder().date(canceled.get(0).getTime()).build().execute();
        Assert.assertEquals(0, resultCount);
        ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());
        ctxCMD.setData("SingleRun", "true");
        ctxCMD.setData("EmfName", "org.jbpm.executor");
        ctxCMD.setData("SkipProcessLog", "true");
        ctxCMD.setData("SkipTaskLog", "true");
        executorService.scheduleRequest("org.jbpm.executor.commands.LogCleanupCommand", ctxCMD);
        countDownListener.reset(1);
        countDownListener.waitTillCompleted();
        inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        Assert.assertEquals(0, inErrorRequests.size());
        queuedRequests = executorService.getQueuedRequests(new QueryContext());
        Assert.assertEquals(0, queuedRequests.size());
        executedRequests = executorService.getCompletedRequests(new QueryContext());
        Assert.assertEquals(1, executedRequests.size());
    }

    @Test
    public void testCustomConstantRequestRetry() throws InterruptedException {
        CountDownAsyncJobListener countDownListener = configureListener(3);
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());
        ctxCMD.setData("retryDelay", "5s");
        ctxCMD.setData("retries", 2);
        executorService.scheduleRequest("org.jbpm.executor.ThrowExceptionCommand", ctxCMD);
        countDownListener.waitTillCompleted();
        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        Assert.assertEquals(1, inErrorRequests.size());
        List<ErrorInfo> errors = executorService.getAllErrors(new QueryContext());
        // Three retries means 4 executions in total 1(regular) + 2(retries)
        Assert.assertEquals(3, errors.size());
        long firstError = errors.get(0).getTime().getTime();
        long secondError = errors.get(1).getTime().getTime();
        long thirdError = errors.get(2).getTime().getTime();
        // time difference between first and second should be at least 3 seconds
        long diff = secondError - firstError;
        Assert.assertTrue((diff > 5000));
        // time difference between second and third should be at least 6 seconds
        diff = thirdError - secondError;
        Assert.assertTrue((diff > 5000));
    }

    @Test
    public void testCustomIncrementingRequestRetry() throws InterruptedException {
        CountDownAsyncJobListener countDownListener = configureListener(3);
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());
        ctxCMD.setData("retryDelay", "3s, 6s");
        ctxCMD.setData("retries", 2);
        executorService.scheduleRequest("org.jbpm.executor.ThrowExceptionCommand", ctxCMD);
        countDownListener.waitTillCompleted();
        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        Assert.assertEquals(1, inErrorRequests.size());
        List<ErrorInfo> errors = executorService.getAllErrors(new QueryContext());
        // Three retries means 4 executions in total 1(regular) + 3(retries)
        Assert.assertEquals(3, errors.size());
        long firstError = errors.get(0).getTime().getTime();
        long secondError = errors.get(1).getTime().getTime();
        long thirdError = errors.get(2).getTime().getTime();
        // time difference between first and second should be at least 3 seconds
        long diff = secondError - firstError;
        Assert.assertTrue((diff > 3000));
        // time difference between second and third should be at least 6 seconds
        diff = thirdError - secondError;
        Assert.assertTrue((diff > 6000));
    }

    @Test
    public void testCustomIncrementingRequestRetrySpecialValues() throws InterruptedException {
        CountDownAsyncJobListener countDownListener = configureListener(2);
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());
        ctxCMD.setData("retryDelay", "-1ms, 1m 80s");
        ctxCMD.setData("retries", 2);
        executorService.scheduleRequest("org.jbpm.executor.ThrowExceptionCommand", ctxCMD);
        countDownListener.waitTillCompleted();
        List<ErrorInfo> errors = executorService.getAllErrors(new QueryContext());
        // 2 executions in total 1(regular) + 1(retry)
        Assert.assertEquals(2, errors.size());
        long firstError = errors.get(0).getTime().getTime();
        long secondError = errors.get(1).getTime().getTime();
        // Time difference between first and second shouldn't be bigger than 4 seconds as executor has 3 second interval and
        // should start executing second command immediately.
        long diff = secondError - firstError;
        Assert.assertTrue((diff < 4000));
        List<RequestInfo> allRequests = executorService.getAllRequests(new QueryContext());
        Assert.assertEquals(1, allRequests.size());
        // Future execution is planned to be started 2 minutes and 20 seconds after last fail.
        // Time difference vary because of test thread sleeping for 10 seconds.
        diff = (allRequests.get(0).getTime().getTime()) - (Calendar.getInstance().getTimeInMillis());
        Assert.assertTrue((diff < 140000));
        Assert.assertTrue((diff > 130000));
        executorService.clearAllRequests();
    }

    @Test
    public void cancelRequestWithSearchByCommandTest() throws InterruptedException {
        CommandContext ctxCMD = new CommandContext();
        String businessKey = UUID.randomUUID().toString();
        ctxCMD.setData("businessKey", businessKey);
        Long requestId = executorService.scheduleRequest("org.jbpm.executor.test.CustomCommand", ctxCMD);
        List<RequestInfo> requests = executorService.getRequestsByCommand("org.jbpm.executor.test.CustomCommand", new QueryContext());
        Assert.assertNotNull(requests);
        Assert.assertEquals(1, requests.size());
        Assert.assertEquals(requestId, requests.get(0).getId());
        // cancel the task immediately
        executorService.cancelRequest(requestId);
        List<RequestInfo> cancelledRequests = executorService.getCancelledRequests(new QueryContext());
        Assert.assertEquals(1, cancelledRequests.size());
    }

    @Test
    public void executorPagingTest() throws InterruptedException {
        CommandContext ctxCMD = new CommandContext();
        String businessKey = UUID.randomUUID().toString();
        ctxCMD.setData("businessKey", businessKey);
        Long requestId1 = executorService.scheduleRequest("org.jbpm.executor.test.CustomCommand", ctxCMD);
        Long requestId2 = executorService.scheduleRequest("org.jbpm.executor.test.CustomCommand", ctxCMD);
        QueryContext queryContextFirstPage = new QueryContext(0, 1);
        QueryContext queryContextSecondPage = new QueryContext(1, 1);
        List<RequestInfo> firstRequests = executorService.getRequestsByCommand("org.jbpm.executor.test.CustomCommand", queryContextFirstPage);
        List<RequestInfo> secondRequests = executorService.getRequestsByCommand("org.jbpm.executor.test.CustomCommand", queryContextSecondPage);
        compareRequestsAreNotSame(firstRequests.get(0), secondRequests.get(0));
        firstRequests = executorService.getRequestsByBusinessKey(businessKey, queryContextFirstPage);
        secondRequests = executorService.getRequestsByBusinessKey(businessKey, queryContextSecondPage);
        compareRequestsAreNotSame(firstRequests.get(0), secondRequests.get(0));
        firstRequests = executorService.getQueuedRequests(queryContextFirstPage);
        secondRequests = executorService.getQueuedRequests(queryContextSecondPage);
        compareRequestsAreNotSame(firstRequests.get(0), secondRequests.get(0));
        // cancel the task immediately
        executorService.cancelRequest(requestId1);
        executorService.cancelRequest(requestId2);
        firstRequests = executorService.getCancelledRequests(queryContextFirstPage);
        secondRequests = executorService.getCancelledRequests(queryContextSecondPage);
        compareRequestsAreNotSame(firstRequests.get(0), secondRequests.get(0));
        firstRequests = executorService.getAllRequests(queryContextFirstPage);
        secondRequests = executorService.getAllRequests(queryContextSecondPage);
        compareRequestsAreNotSame(firstRequests.get(0), secondRequests.get(0));
        // Setting too far page
        QueryContext queryContextBigOffset = new QueryContext(10, 1);
        List<RequestInfo> offsetRequests = executorService.getCancelledRequests(queryContextBigOffset);
        Assert.assertNotNull(offsetRequests);
        Assert.assertEquals(0, offsetRequests.size());
    }

    @Test
    public void clearAllRequestsTest() throws InterruptedException {
        CommandContext ctxCMD = new CommandContext();
        String businessKey = UUID.randomUUID().toString();
        ctxCMD.setData("businessKey", businessKey);
        // Testing clearing of active request.
        Long requestId = executorService.scheduleRequest("org.jbpm.executor.test.CustomCommand", ctxCMD);
        List<RequestInfo> allRequests = executorService.getAllRequests(new QueryContext());
        Assert.assertEquals(1, allRequests.size());
        executorService.clearAllRequests();
        allRequests = executorService.getAllRequests(new QueryContext());
        Assert.assertEquals(0, allRequests.size());
        // Testing clearing of cancelled request.
        requestId = executorService.scheduleRequest("org.jbpm.executor.test.CustomCommand", ctxCMD);
        allRequests = executorService.getAllRequests(new QueryContext());
        Assert.assertEquals(1, allRequests.size());
        executorService.cancelRequest(requestId);
        executorService.clearAllRequests();
        allRequests = executorService.getAllRequests(new QueryContext());
        Assert.assertEquals(0, allRequests.size());
    }

    @Test
    public void testReturnNullCommand() throws InterruptedException {
        CountDownAsyncJobListener countDownListener = configureListener(1);
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());
        executorService.scheduleRequest("org.jbpm.executor.test.ReturnNullCommand", ctxCMD);
        countDownListener.waitTillCompleted();
        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        Assert.assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executorService.getQueuedRequests(new QueryContext());
        Assert.assertEquals(0, queuedRequests.size());
        List<RequestInfo> executedRequests = executorService.getCompletedRequests(new QueryContext());
        Assert.assertEquals(1, executedRequests.size());
    }

    @Test
    public void testPrioritizedJobsExecution() throws InterruptedException {
        CountDownAsyncJobListener countDownListener = configureListener(1);
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", "low priority");
        ctxCMD.setData("priority", 2);
        executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", ctxCMD);
        CommandContext ctxCMD2 = new CommandContext();
        ctxCMD2.setData("businessKey", "high priority");
        ctxCMD2.setData("priority", 8);
        executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", ctxCMD2);
        countDownListener.waitTillCompleted();
        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        Assert.assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executorService.getQueuedRequests(new QueryContext());
        Assert.assertEquals(1, queuedRequests.size());
        List<RequestInfo> executedRequests = executorService.getCompletedRequests(new QueryContext());
        Assert.assertEquals(1, executedRequests.size());
        RequestInfo executed = executedRequests.get(0);
        Assert.assertNotNull(executed);
        Assert.assertEquals("high priority", executed.getKey());
        countDownListener.reset(1);
        countDownListener.waitTillCompleted();
        inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        Assert.assertEquals(0, inErrorRequests.size());
        queuedRequests = executorService.getQueuedRequests(new QueryContext());
        Assert.assertEquals(0, queuedRequests.size());
        executedRequests = executorService.getCompletedRequests(new QueryContext());
        Assert.assertEquals(2, executedRequests.size());
        executed = executedRequests.get(0);
        Assert.assertNotNull(executed);
        Assert.assertEquals("low priority", executed.getKey());
    }

    @Test
    public void testPrioritizedJobsExecutionInvalidProrities() throws InterruptedException {
        CountDownAsyncJobListener countDownListener = configureListener(1);
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", "low priority");
        ctxCMD.setData("priority", (-1));
        executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", ctxCMD);
        CommandContext ctxCMD2 = new CommandContext();
        ctxCMD2.setData("businessKey", "high priority");
        ctxCMD2.setData("priority", 10);
        executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", ctxCMD2);
        countDownListener.waitTillCompleted();
        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        Assert.assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executorService.getQueuedRequests(new QueryContext());
        Assert.assertEquals(1, queuedRequests.size());
        List<RequestInfo> executedRequests = executorService.getCompletedRequests(new QueryContext());
        Assert.assertEquals(1, executedRequests.size());
        RequestInfo executed = executedRequests.get(0);
        Assert.assertNotNull(executed);
        Assert.assertEquals("high priority", executed.getKey());
        countDownListener.reset(1);
        countDownListener.waitTillCompleted();
        inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        Assert.assertEquals(0, inErrorRequests.size());
        queuedRequests = executorService.getQueuedRequests(new QueryContext());
        Assert.assertEquals(0, queuedRequests.size());
        executedRequests = executorService.getCompletedRequests(new QueryContext());
        Assert.assertEquals(2, executedRequests.size());
        executed = executedRequests.get(0);
        Assert.assertNotNull(executed);
        Assert.assertEquals("low priority", executed.getKey());
    }

    private void compareRequestsAreNotSame(RequestInfo firstRequest, RequestInfo secondRequest) {
        Assert.assertNotNull(firstRequest);
        Assert.assertNotNull(secondRequest);
        Assert.assertNotEquals("Requests are same!", firstRequest.getId(), secondRequest.getId());
    }
}

