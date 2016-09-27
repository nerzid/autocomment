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
import org.junit.Before;
import org.kie.api.executor.CommandContext;
import org.jbpm.executor.test.CountDownAsyncJobListener;
import javax.persistence.EntityManagerFactory;
import org.kie.api.executor.ExecutorService;
import org.jbpm.executor.impl.ExecutorServiceImpl;
import org.jbpm.test.util.ExecutorTestUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Persistence;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import org.kie.api.executor.RequestInfo;
import org.junit.Test;
import java.util.concurrent.TimeUnit;
import java.util.UUID;

public class ReconfiguredExecutorTest {
    protected ExecutorService executorService;

    public static final Map<String, Object> cachedEntities = new HashMap<String, Object>();

    private PoolingDataSource pds;

    private EntityManagerFactory emf = null;

    @Before
    public void setUp() {
        pds = ExecutorTestUtil.setupPoolingDataSource();
        emf = Persistence.createEntityManagerFactory("org.jbpm.executor");
        executorService = ExecutorServiceFactory.newExecutorService(emf);
        executorService.setThreadPoolSize(2);
        executorService.setInterval(3000);
        executorService.setTimeunit(TimeUnit.MILLISECONDS);
        executorService.init();
    }

    @After
    public void tearDown() {
        executorService.clearAllRequests();
        executorService.clearAllErrors();
        System.clearProperty("org.kie.executor.msg.length");
        System.clearProperty("org.kie.executor.stacktrace.length");
        executorService.destroy();
        if ((emf) != null) {
            emf.close();
        } 
        pds.close();
    }

    protected CountDownAsyncJobListener configureListener(int threads) {
        CountDownAsyncJobListener countDownListener = new CountDownAsyncJobListener(threads);
        ((ExecutorServiceImpl) (executorService)).addAsyncJobListener(countDownListener);
        return countDownListener;
    }

    @Test
    public void simpleExcecutionTest() throws InterruptedException {
        CountDownAsyncJobListener countDownListener = configureListener(1);
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());
        executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", ctxCMD);
        countDownListener.waitTillCompleted();
        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new org.kie.api.runtime.query.QueryContext());
        Assert.assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executorService.getQueuedRequests(new org.kie.api.runtime.query.QueryContext());
        Assert.assertEquals(0, queuedRequests.size());
        List<RequestInfo> executedRequests = executorService.getCompletedRequests(new org.kie.api.runtime.query.QueryContext());
        Assert.assertEquals(1, executedRequests.size());
    }
}

