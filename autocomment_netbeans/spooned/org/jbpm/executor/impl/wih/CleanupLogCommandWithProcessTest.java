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


package org.jbpm.executor.impl.wih;

import org.jbpm.test.util.AbstractExecutorBaseTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.kie.api.executor.CommandContext;
import org.jbpm.executor.test.CountDownAsyncJobListener;
import java.util.Date;
import org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;
import javax.persistence.EntityManagerFactory;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.kie.api.executor.ExecutorService;
import org.jbpm.executor.ExecutorServiceFactory;
import org.jbpm.executor.impl.ExecutorServiceImpl;
import org.jbpm.test.util.ExecutorTestUtil;
import org.kie.api.runtime.KieSession;
import java.util.Map;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import org.kie.api.runtime.process.ProcessInstance;
import java.util.Properties;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import java.text.SimpleDateFormat;
import org.junit.Test;
import org.kie.api.task.UserGroupCallback;
import org.kie.api.runtime.process.WorkItemHandler;

public class CleanupLogCommandWithProcessTest extends AbstractExecutorBaseTest {
    private PoolingDataSource pds;

    private UserGroupCallback userGroupCallback;

    private RuntimeManager manager;

    private ExecutorService executorService;

    private EntityManagerFactory emf = null;

    @Before
    public void setup() {
        ExecutorTestUtil.cleanupSingletonSessionId();
        pds = ExecutorTestUtil.setupPoolingDataSource();
        Properties properties = new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        userGroupCallback = new org.jbpm.services.task.identity.JBossUserGroupCallbackImpl(properties);
        executorService = buildExecutorService();
    }

    @After
    public void teardown() {
        executorService.destroy();
        if ((manager) != null) {
            RuntimeManagerRegistry.get().remove(manager.getIdentifier());
            manager.close();
        } 
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
    public void testRunProcessWithAsyncHandler() throws Exception {
        CountDownAsyncJobListener countDownListener = configureListener(1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(userGroupCallback).entityManagerFactory(emf).addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2).registerableItemsFactory(new DefaultRegisterableItemsFactory() {
            @Override
            public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
                Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                handlers.put("async", new org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler());
                return handlers;
            }
        }).get();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        Assert.assertEquals(0, getProcessLogSize("ScriptTask"));
        Assert.assertEquals(0, getNodeInstanceLogSize("ScriptTask"));
        Assert.assertEquals(0, getTaskLogSize("ScriptTask"));
        Assert.assertEquals(0, getVariableLogSize("ScriptTask"));
        Date startDate = new Date();
        ProcessInstance processInstance = ksession.startProcess("ScriptTask");
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        Assert.assertEquals(1, getProcessLogSize("ScriptTask"));
        Assert.assertEquals(5, getNodeInstanceLogSize("ScriptTask"));
        Assert.assertEquals(0, getTaskLogSize("ScriptTask"));
        Assert.assertEquals(0, getVariableLogSize("ScriptTask"));
        scheduleLogCleanup(false, true, false, startDate, "ScriptTask", "yyyy-MM-dd", manager.getIdentifier());
        countDownListener.waitTillCompleted();
        System.out.println(("Aborting process instance " + (processInstance.getId())));
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNotNull(processInstance);
        Assert.assertEquals(1, getProcessLogSize("ScriptTask"));
        Assert.assertEquals(5, getNodeInstanceLogSize("ScriptTask"));
        Assert.assertEquals(0, getTaskLogSize("ScriptTask"));
        Assert.assertEquals(0, getVariableLogSize("ScriptTask"));
        runtime.getKieSession().abortProcessInstance(processInstance.getId());
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        Assert.assertNull(processInstance);
        Assert.assertEquals(1, getProcessLogSize("ScriptTask"));
        Assert.assertEquals(6, getNodeInstanceLogSize("ScriptTask"));
        Assert.assertEquals(0, getTaskLogSize("ScriptTask"));
        Assert.assertEquals(0, getVariableLogSize("ScriptTask"));
        Thread.sleep(1000);
        scheduleLogCleanup(false, false, false, new Date(), "ScriptTask", "yyyy-MM-dd HH:mm:ss", manager.getIdentifier());
        countDownListener.reset(1);
        countDownListener.waitTillCompleted();
        Assert.assertEquals(0, getProcessLogSize("ScriptTask"));
        Assert.assertEquals(0, getNodeInstanceLogSize("ScriptTask"));
        Assert.assertEquals(0, getTaskLogSize("ScriptTask"));
        Assert.assertEquals(0, getVariableLogSize("ScriptTask"));
    }

    private ExecutorService buildExecutorService() {
        emf = EntityManagerFactoryManager.get().getOrCreate("org.jbpm.persistence.complete");
        executorService = ExecutorServiceFactory.newExecutorService(emf);
        executorService.init();
        return executorService;
    }

    private void scheduleLogCleanup(boolean skipProcessLog, boolean skipTaskLog, boolean skipExecutorLog, Date olderThan, String forProcess, String dateFormat, String identifier) {
        CommandContext commandContext = new CommandContext();
        commandContext.setData("EmfName", "org.jbpm.persistence.complete");
        commandContext.setData("SkipProcessLog", String.valueOf(skipProcessLog));
        commandContext.setData("SkipTaskLog", String.valueOf(skipTaskLog));
        commandContext.setData("SkipExecutorLog", String.valueOf(skipExecutorLog));
        commandContext.setData("SingleRun", "true");
        commandContext.setData("OlderThan", new SimpleDateFormat(dateFormat).format(olderThan));
        commandContext.setData("DateFormat", dateFormat);
        commandContext.setData("ForDeployment", identifier);
        // commandContext.setData("OlderThanPeriod", olderThanPeriod);
        commandContext.setData("ForProcess", forProcess);
        executorService.scheduleRequest("org.jbpm.executor.commands.LogCleanupCommand", commandContext);
    }

    private int getProcessLogSize(String processId) {
        return new org.jbpm.process.audit.JPAAuditLogService(emf).processInstanceLogQuery().processId(processId).build().getResultList().size();
    }

    private int getTaskLogSize(String processId) {
        return new org.jbpm.services.task.audit.service.TaskJPAAuditService(emf).auditTaskQuery().processId(processId).build().getResultList().size();
    }

    private int getNodeInstanceLogSize(String processId) {
        return new org.jbpm.process.audit.JPAAuditLogService(emf).nodeInstanceLogQuery().processId(processId).build().getResultList().size();
    }

    private int getVariableLogSize(String processId) {
        return new org.jbpm.process.audit.JPAAuditLogService(emf).variableInstanceLogQuery().processId(processId).build().getResultList().size();
    }
}

