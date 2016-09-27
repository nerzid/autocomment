/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.runtime.manager.impl.migration;

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.After;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import java.util.Collections;
import org.kie.internal.runtime.manager.context.EmptyContext;
import javax.persistence.EntityManagerFactory;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.process.audit.JPAAuditLogService;
import org.kie.api.runtime.KieSession;
import java.util.List;
import java.util.Map;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import java.util.Properties;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.junit.Test;
import org.jbpm.runtime.manager.util.TestUtil;
import org.kie.internal.task.api.UserGroupCallback;

public class MigrationManagerTest extends AbstractBaseTest {
    private PoolingDataSource pds;

    private EntityManagerFactory emf;

    private UserGroupCallback userGroupCallback;

    private RuntimeManager managerV1;

    private RuntimeManager managerV2;

    // general info
    private static final String DEPLOYMENT_ID_V1 = "managerV1";

    private static final String DEPLOYMENT_ID_V2 = "managerV2";

    private static final String USER_JOHN = "john";

    // simple user task process
    private static final String PROCESS_ID_V1 = "UserTask-1";

    private static final String PROCESS_ID_V2 = "UserTask-2";

    private static final String PROCESS_NAME_V1 = "User Task v1";

    private static final String PROCESS_NAME_V2 = "User Task v2";

    private static final String TASK_NAME_V1 = "Hello v1";

    private static final String TASK_NAME_V2 = "Hello v2";

    private static final String ADDTASKAFTERACTIVE_ID_V1 = "process-migration-testv1.AddTaskAfterActive";

    private static final String ADDTASKAFTERACTIVE_ID_V2 = "process-migration-testv2.AddTaskAfterActive";

    private static final String ADDTASKBEFOREACTIVE_ID_V1 = "process-migration-testv1.AddTaskBeforeActive";

    private static final String ADDTASKBEFOREACTIVE_ID_V2 = "process-migration-testv2.AddTaskBeforeActive";

    private static final String REMOVEACTIVETASK_ID_V1 = "process-migration-testv1.RemoveActiveTask";

    private static final String REMOVEACTIVETASK_ID_V2 = "process-migration-testv2.RemoveActiveTask";

    private static final String REMOVENONACTIVETASK_ID_V1 = "process-migration-testv1.RemoveNonActiveTask";

    private static final String REMOVENONACTIVETASK_ID_V2 = "process-migration-testv2.RemoveNonActiveTask";

    private static final String REPLACEACTIVETASK_ID_V1 = "process-migration-testv1.ReplaceActiveTask";

    private static final String REPLACEACTIVETASK_ID_V2 = "process-migration-testv2.ReplaceActiveTask";

    private static final String REMOVENONACTIVEBEFORETASK_ID_V1 = "process-migration-testv1.RemoveNonActiveBeforeTask";

    private static final String REMOVENONACTIVEBEFORETASK_ID_V2 = "process-migration-testv2.RemoveNonActiveBeforeTask";

    private JPAAuditLogService auditService;

    @Before
    public void setup() {
        TestUtil.cleanupSingletonSessionId();
        pds = TestUtil.setupPoolingDataSource();
        emf = EntityManagerFactoryManager.get().getOrCreate("org.jbpm.persistence.jpa");
        Properties properties = new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        userGroupCallback = new org.jbpm.services.task.identity.JBossUserGroupCallbackImpl(properties);
        auditService = new JPAAuditLogService(emf);
    }

    @After
    public void teardown() {
        auditService.dispose();
        if ((managerV1) != null) {
            managerV1.close();
        } 
        if ((managerV2) != null) {
            managerV2.close();
        } 
        EntityManagerFactoryManager.get().clear();
        pds.close();
    }

    @Test
    public void testMigrateUserTaskProcessInstance() {
        createRuntimeManagers("migration/v1/BPMN2-UserTask-v1.bpmn2", "migration/v2/BPMN2-UserTask-v2.bpmn2");
        Assert.assertNotNull(managerV1);
        Assert.assertNotNull(managerV2);
        RuntimeEngine runtime = managerV1.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        ProcessInstance pi1 = ksession.startProcess(MigrationManagerTest.PROCESS_ID_V1);
        Assert.assertNotNull(pi1);
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        JPAAuditLogService auditService = new JPAAuditLogService(emf);
        ProcessInstanceLog log = auditService.findProcessInstance(pi1.getId());
        Assert.assertNotNull(log);
        Assert.assertEquals(MigrationManagerTest.PROCESS_ID_V1, log.getProcessId());
        Assert.assertEquals(MigrationManagerTest.PROCESS_NAME_V1, log.getProcessName());
        Assert.assertEquals(MigrationManagerTest.DEPLOYMENT_ID_V1, log.getExternalId());
        TaskService taskService = runtime.getTaskService();
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner(MigrationManagerTest.USER_JOHN, "en-UK");
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        Assert.assertNotNull(task);
        Assert.assertEquals(MigrationManagerTest.PROCESS_ID_V1, task.getProcessId());
        Assert.assertEquals(MigrationManagerTest.DEPLOYMENT_ID_V1, task.getDeploymentId());
        Assert.assertEquals(MigrationManagerTest.TASK_NAME_V1, task.getName());
        managerV1.disposeRuntimeEngine(runtime);
        MigrationSpec migrationSpec = new MigrationSpec(MigrationManagerTest.DEPLOYMENT_ID_V1, pi1.getId(), MigrationManagerTest.DEPLOYMENT_ID_V2, MigrationManagerTest.PROCESS_ID_V2);
        MigrationManager migrationManager = new MigrationManager(migrationSpec);
        MigrationReport report = migrationManager.migrate();
        Assert.assertNotNull(report);
        Assert.assertTrue(report.isSuccessful());
        log = auditService.findProcessInstance(pi1.getId());
        Assert.assertNotNull(log);
        Assert.assertEquals(MigrationManagerTest.PROCESS_ID_V2, log.getProcessId());
        Assert.assertEquals(MigrationManagerTest.PROCESS_NAME_V2, log.getProcessName());
        Assert.assertEquals(MigrationManagerTest.DEPLOYMENT_ID_V2, log.getExternalId());
        auditService.dispose();
        runtime = managerV2.getRuntimeEngine(EmptyContext.get());
        taskService = runtime.getTaskService();
        tasks = taskService.getTasksAssignedAsPotentialOwner(MigrationManagerTest.USER_JOHN, "en-UK");
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());
        task = tasks.get(0);
        Assert.assertNotNull(task);
        Assert.assertEquals(MigrationManagerTest.PROCESS_ID_V2, task.getProcessId());
        Assert.assertEquals(MigrationManagerTest.DEPLOYMENT_ID_V2, task.getDeploymentId());
        Assert.assertEquals(MigrationManagerTest.TASK_NAME_V2, task.getName());// same name as the node mapping was not given
        
        managerV2.disposeRuntimeEngine(runtime);
    }

    @Test
    public void testMigrateUserTaskProcessInstanceWithNodeMapping() {
        createRuntimeManagers("migration/v1/BPMN2-UserTask-v1.bpmn2", "migration/v2/BPMN2-UserTask-v2.bpmn2");
        Assert.assertNotNull(managerV1);
        Assert.assertNotNull(managerV2);
        RuntimeEngine runtime = managerV1.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        ProcessInstance pi1 = ksession.startProcess(MigrationManagerTest.PROCESS_ID_V1);
        Assert.assertNotNull(pi1);
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        TaskService taskService = runtime.getTaskService();
        TaskSummary task = getTask(taskService);
        managerV1.disposeRuntimeEngine(runtime);
        Assert.assertEquals(MigrationManagerTest.PROCESS_ID_V1, task.getProcessId());
        Assert.assertEquals(MigrationManagerTest.DEPLOYMENT_ID_V1, task.getDeploymentId());
        Assert.assertEquals(MigrationManagerTest.TASK_NAME_V1, task.getName());
        MigrationSpec migrationSpec = new MigrationSpec(MigrationManagerTest.DEPLOYMENT_ID_V1, pi1.getId(), MigrationManagerTest.DEPLOYMENT_ID_V2, MigrationManagerTest.PROCESS_ID_V2);
        MigrationManager migrationManager = new MigrationManager(migrationSpec);
        MigrationReport report = migrationManager.migrate(Collections.singletonMap("_2", "_2"));
        Assert.assertNotNull(report);
        Assert.assertTrue(report.isSuccessful());
        assertMigratedProcessInstance(MigrationManagerTest.PROCESS_ID_V2, pi1.getId(), ProcessInstance.STATE_ACTIVE);
        runtime = managerV2.getRuntimeEngine(EmptyContext.get());
        taskService = runtime.getTaskService();
        assertMigratedTaskAndComplete(taskService, MigrationManagerTest.PROCESS_ID_V2, pi1.getId(), MigrationManagerTest.TASK_NAME_V2);
        assertMigratedProcessInstance(MigrationManagerTest.PROCESS_ID_V2, pi1.getId(), ProcessInstance.STATE_COMPLETED);
        managerV2.disposeRuntimeEngine(runtime);
    }

    @SuppressWarnings(value = { "unchecked" , "rawtypes" })
    @Test
    public void testMigrateUserTaskProcessInstanceWithRollback() {
        createRuntimeManagers("migration/v1/BPMN2-UserTask-v1.bpmn2", "migration/v2/BPMN2-UserTask-v2.bpmn2");
        Assert.assertNotNull(managerV1);
        Assert.assertNotNull(managerV2);
        RuntimeEngine runtime = managerV1.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        ProcessInstance pi1 = ksession.startProcess(MigrationManagerTest.PROCESS_ID_V1);
        Assert.assertNotNull(pi1);
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        managerV1.disposeRuntimeEngine(runtime);
        MigrationSpec migrationSpec = new MigrationSpec(MigrationManagerTest.DEPLOYMENT_ID_V1, pi1.getId(), MigrationManagerTest.DEPLOYMENT_ID_V2, MigrationManagerTest.PROCESS_ID_V2);
        MigrationManager migrationManager = new MigrationManager(migrationSpec);
        MigrationReport report = null;
        try {
            // explicitly without generic to cause error (class cast) in migration process to test rollback
            Map erronousMapping = Collections.singletonMap("_2", 2);
            migrationManager.migrate(erronousMapping);
        } catch (MigrationException e) {
            report = e.getReport();
        }
        Assert.assertNotNull(report);
        Assert.assertFalse(report.isSuccessful());
        JPAAuditLogService auditService = new JPAAuditLogService(emf);
        ProcessInstanceLog log = auditService.findProcessInstance(pi1.getId());
        Assert.assertNotNull(log);
        Assert.assertEquals(MigrationManagerTest.PROCESS_ID_V1, log.getProcessId());
        Assert.assertEquals(MigrationManagerTest.DEPLOYMENT_ID_V1, log.getExternalId());
        auditService.dispose();
    }

    @Test
    public void testAddTaskAfterActive() {
        createRuntimeManagers("migration/v1/AddTaskAfterActive-v1.bpmn2", "migration/v2/AddTaskAfterActive-v2.bpmn2");
        Assert.assertNotNull(managerV1);
        Assert.assertNotNull(managerV2);
        RuntimeEngine runtime = managerV1.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        ProcessInstance pi1 = ksession.startProcess(MigrationManagerTest.ADDTASKAFTERACTIVE_ID_V1);
        Assert.assertNotNull(pi1);
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        managerV1.disposeRuntimeEngine(runtime);
        MigrationSpec migrationSpec = new MigrationSpec(MigrationManagerTest.DEPLOYMENT_ID_V1, pi1.getId(), MigrationManagerTest.DEPLOYMENT_ID_V2, MigrationManagerTest.ADDTASKAFTERACTIVE_ID_V2);
        MigrationManager migrationManager = new MigrationManager(migrationSpec);
        MigrationReport report = migrationManager.migrate();
        Assert.assertNotNull(report);
        Assert.assertTrue(report.isSuccessful());
        assertMigratedProcessInstance(MigrationManagerTest.ADDTASKAFTERACTIVE_ID_V2, pi1.getId(), ProcessInstance.STATE_ACTIVE);
        runtime = managerV2.getRuntimeEngine(EmptyContext.get());
        TaskService taskService = runtime.getTaskService();
        assertMigratedTaskAndComplete(taskService, MigrationManagerTest.ADDTASKAFTERACTIVE_ID_V2, pi1.getId(), "Active Task");
        managerV2.disposeRuntimeEngine(runtime);
        assertMigratedProcessInstance(MigrationManagerTest.ADDTASKAFTERACTIVE_ID_V2, pi1.getId(), ProcessInstance.STATE_ACTIVE);
        runtime = managerV2.getRuntimeEngine(EmptyContext.get());
        taskService = runtime.getTaskService();
        assertMigratedTaskAndComplete(taskService, MigrationManagerTest.ADDTASKAFTERACTIVE_ID_V2, pi1.getId(), "Added Task");
        assertMigratedProcessInstance(MigrationManagerTest.ADDTASKAFTERACTIVE_ID_V2, pi1.getId(), ProcessInstance.STATE_COMPLETED);
        managerV2.disposeRuntimeEngine(runtime);
    }

    @Test
    public void testAddTaskBeforeActive() {
        String activeNodeId = "_18771A1A-9DB9-4CA1-8C2E-19DEE24A1776";
        String addedNodeId = "_94643E69-BD97-4E4A-8B4A-364FEB95CA3C";
        createRuntimeManagers("migration/v1/AddTaskBeforeActive-v1.bpmn2", "migration/v2/AddTaskBeforeActive-v2.bpmn2");
        Assert.assertNotNull(managerV1);
        Assert.assertNotNull(managerV2);
        RuntimeEngine runtime = managerV1.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        ProcessInstance pi1 = ksession.startProcess(MigrationManagerTest.ADDTASKBEFOREACTIVE_ID_V1);
        Assert.assertNotNull(pi1);
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        managerV1.disposeRuntimeEngine(runtime);
        MigrationSpec migrationSpec = new MigrationSpec(MigrationManagerTest.DEPLOYMENT_ID_V1, pi1.getId(), MigrationManagerTest.DEPLOYMENT_ID_V2, MigrationManagerTest.ADDTASKBEFOREACTIVE_ID_V2);
        MigrationManager migrationManager = new MigrationManager(migrationSpec);
        MigrationReport report = migrationManager.migrate(Collections.singletonMap(activeNodeId, addedNodeId));
        Assert.assertNotNull(report);
        Assert.assertTrue(report.isSuccessful());
        assertMigratedProcessInstance(MigrationManagerTest.ADDTASKBEFOREACTIVE_ID_V2, pi1.getId(), ProcessInstance.STATE_ACTIVE);
        runtime = managerV2.getRuntimeEngine(EmptyContext.get());
        TaskService taskService = runtime.getTaskService();
        assertMigratedTaskAndComplete(taskService, MigrationManagerTest.ADDTASKBEFOREACTIVE_ID_V2, pi1.getId(), "Added Task");
        managerV2.disposeRuntimeEngine(runtime);
        assertMigratedProcessInstance(MigrationManagerTest.ADDTASKBEFOREACTIVE_ID_V2, pi1.getId(), ProcessInstance.STATE_ACTIVE);
        runtime = managerV2.getRuntimeEngine(EmptyContext.get());
        taskService = runtime.getTaskService();
        assertMigratedTaskAndComplete(taskService, MigrationManagerTest.ADDTASKBEFOREACTIVE_ID_V2, pi1.getId(), "Active Task");
        managerV2.disposeRuntimeEngine(runtime);
        assertMigratedProcessInstance(MigrationManagerTest.ADDTASKBEFOREACTIVE_ID_V2, pi1.getId(), ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testRemoveActiveTask() {
        String activeNodeId = "_ECEDD1CE-7380-418C-B7A6-AF8ECB90B820";
        String nextNodeId = "_9EF3CAE0-D978-4E96-9C00-8A80082EB68E";
        createRuntimeManagers("migration/v1/RemoveActiveTask-v1.bpmn2", "migration/v2/RemoveActiveTask-v2.bpmn2");
        Assert.assertNotNull(managerV1);
        Assert.assertNotNull(managerV2);
        RuntimeEngine runtime = managerV1.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        ProcessInstance pi1 = ksession.startProcess(MigrationManagerTest.REMOVEACTIVETASK_ID_V1);
        Assert.assertNotNull(pi1);
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        managerV1.disposeRuntimeEngine(runtime);
        MigrationSpec migrationSpec = new MigrationSpec(MigrationManagerTest.DEPLOYMENT_ID_V1, pi1.getId(), MigrationManagerTest.DEPLOYMENT_ID_V2, MigrationManagerTest.REMOVEACTIVETASK_ID_V2);
        MigrationManager migrationManager = new MigrationManager(migrationSpec);
        MigrationReport report = migrationManager.migrate(Collections.singletonMap(activeNodeId, nextNodeId));
        Assert.assertNotNull(report);
        Assert.assertTrue(report.isSuccessful());
        assertMigratedProcessInstance(MigrationManagerTest.REMOVEACTIVETASK_ID_V2, pi1.getId(), ProcessInstance.STATE_ACTIVE);
        runtime = managerV2.getRuntimeEngine(EmptyContext.get());
        TaskService taskService = runtime.getTaskService();
        assertMigratedTaskAndComplete(taskService, MigrationManagerTest.REMOVEACTIVETASK_ID_V2, pi1.getId(), "Mapped Task");
        assertMigratedProcessInstance(MigrationManagerTest.REMOVEACTIVETASK_ID_V2, pi1.getId(), ProcessInstance.STATE_COMPLETED);
        managerV2.disposeRuntimeEngine(runtime);
    }

    @Test
    public void testRemoveNonActiveTask() {
        createRuntimeManagers("migration/v1/RemoveNonActiveTask-v1.bpmn2", "migration/v2/RemoveNonActiveTask-v2.bpmn2");
        Assert.assertNotNull(managerV1);
        Assert.assertNotNull(managerV2);
        RuntimeEngine runtime = managerV1.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        ProcessInstance pi1 = ksession.startProcess(MigrationManagerTest.REMOVENONACTIVETASK_ID_V1);
        Assert.assertNotNull(pi1);
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        managerV1.disposeRuntimeEngine(runtime);
        MigrationSpec migrationSpec = new MigrationSpec(MigrationManagerTest.DEPLOYMENT_ID_V1, pi1.getId(), MigrationManagerTest.DEPLOYMENT_ID_V2, MigrationManagerTest.REMOVENONACTIVETASK_ID_V2);
        MigrationManager migrationManager = new MigrationManager(migrationSpec);
        MigrationReport report = migrationManager.migrate();
        Assert.assertNotNull(report);
        Assert.assertTrue(report.isSuccessful());
        assertMigratedProcessInstance(MigrationManagerTest.REMOVENONACTIVETASK_ID_V2, pi1.getId(), ProcessInstance.STATE_ACTIVE);
        runtime = managerV2.getRuntimeEngine(EmptyContext.get());
        TaskService taskService = runtime.getTaskService();
        assertMigratedTaskAndComplete(taskService, MigrationManagerTest.REMOVENONACTIVETASK_ID_V2, pi1.getId(), "Active Task");
        assertMigratedProcessInstance(MigrationManagerTest.REMOVENONACTIVETASK_ID_V2, pi1.getId(), ProcessInstance.STATE_COMPLETED);
        managerV2.disposeRuntimeEngine(runtime);
    }

    @Test
    public void testReplaceActiveTask() {
        String activeNodeId = "_E9140EE9-1B5A-46B1-871E-A735402B69F4";
        String replaceNodeId = "_9B25FCC5-C718-4941-A4AE-DD8D6E368F48";
        createRuntimeManagers("migration/v1/ReplaceActiveTask-v1.bpmn2", "migration/v2/ReplaceActiveTask-v2.bpmn2");
        Assert.assertNotNull(managerV1);
        Assert.assertNotNull(managerV2);
        RuntimeEngine runtime = managerV1.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        ProcessInstance pi1 = ksession.startProcess(MigrationManagerTest.REPLACEACTIVETASK_ID_V1);
        Assert.assertNotNull(pi1);
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        managerV1.disposeRuntimeEngine(runtime);
        MigrationSpec migrationSpec = new MigrationSpec(MigrationManagerTest.DEPLOYMENT_ID_V1, pi1.getId(), MigrationManagerTest.DEPLOYMENT_ID_V2, MigrationManagerTest.REPLACEACTIVETASK_ID_V2);
        MigrationManager migrationManager = new MigrationManager(migrationSpec);
        MigrationReport report = migrationManager.migrate(Collections.singletonMap(activeNodeId, replaceNodeId));
        Assert.assertNotNull(report);
        Assert.assertTrue(report.isSuccessful());
        assertMigratedProcessInstance(MigrationManagerTest.REPLACEACTIVETASK_ID_V2, pi1.getId(), ProcessInstance.STATE_ACTIVE);
        runtime = managerV2.getRuntimeEngine(EmptyContext.get());
        TaskService taskService = runtime.getTaskService();
        assertMigratedTaskAndComplete(taskService, MigrationManagerTest.REPLACEACTIVETASK_ID_V2, pi1.getId(), "Mapped Task");
        assertMigratedProcessInstance(MigrationManagerTest.REPLACEACTIVETASK_ID_V2, pi1.getId(), ProcessInstance.STATE_COMPLETED);
        managerV2.disposeRuntimeEngine(runtime);
    }

    @Test
    public void testRemoveNonActiveBeforeActiveTask() {
        createRuntimeManagers("migration/v1/RemoveNonActiveBeforeTask-v1.bpmn2", "migration/v2/RemoveNonActiveBeforeTask-v2.bpmn2");
        Assert.assertNotNull(managerV1);
        Assert.assertNotNull(managerV2);
        RuntimeEngine runtime = managerV1.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        ProcessInstance pi1 = ksession.startProcess(MigrationManagerTest.REMOVENONACTIVEBEFORETASK_ID_V1);
        Assert.assertNotNull(pi1);
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        assertTaskAndComplete(runtime.getTaskService(), MigrationManagerTest.REMOVENONACTIVEBEFORETASK_ID_V1, pi1.getId(), "Active Task");
        managerV1.disposeRuntimeEngine(runtime);
        MigrationSpec migrationSpec = new MigrationSpec(MigrationManagerTest.DEPLOYMENT_ID_V1, pi1.getId(), MigrationManagerTest.DEPLOYMENT_ID_V2, MigrationManagerTest.REMOVENONACTIVEBEFORETASK_ID_V2);
        MigrationManager migrationManager = new MigrationManager(migrationSpec);
        MigrationReport report = migrationManager.migrate();
        Assert.assertNotNull(report);
        Assert.assertTrue(report.isSuccessful());
        assertMigratedProcessInstance(MigrationManagerTest.REMOVENONACTIVEBEFORETASK_ID_V2, pi1.getId(), ProcessInstance.STATE_ACTIVE);
        runtime = managerV2.getRuntimeEngine(EmptyContext.get());
        TaskService taskService = runtime.getTaskService();
        assertMigratedTaskAndComplete(taskService, MigrationManagerTest.REMOVENONACTIVEBEFORETASK_ID_V2, pi1.getId(), "Non-active Task");
        assertMigratedProcessInstance(MigrationManagerTest.REMOVENONACTIVEBEFORETASK_ID_V2, pi1.getId(), ProcessInstance.STATE_COMPLETED);
        managerV2.disposeRuntimeEngine(runtime);
    }

    /* Helper methods */
    protected TaskSummary getTask(TaskService taskService) {
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner(MigrationManagerTest.USER_JOHN, "en-UK");
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        Assert.assertNotNull(task);
        return task;
    }

    protected void assertTaskAndComplete(TaskService taskService, String processId, Long processInstanceId, String taskName) {
        List<TaskSummary> tasks = taskService.getTasksByStatusByProcessInstanceId(processInstanceId, Arrays.asList(Status.Reserved), "en-UK");
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        Assert.assertNotNull(task);
        Assert.assertEquals(processId, task.getProcessId());
        Assert.assertEquals(MigrationManagerTest.DEPLOYMENT_ID_V1, task.getDeploymentId());
        Assert.assertEquals(taskName, task.getName());
        taskService.start(task.getId(), MigrationManagerTest.USER_JOHN);
        taskService.complete(task.getId(), MigrationManagerTest.USER_JOHN, null);
    }

    protected void assertMigratedTaskAndComplete(TaskService taskService, String processId, Long processInstanceId, String taskName) {
        List<TaskSummary> tasks = taskService.getTasksByStatusByProcessInstanceId(processInstanceId, Arrays.asList(Status.Reserved), "en-UK");
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        Assert.assertNotNull(task);
        Assert.assertEquals(processId, task.getProcessId());
        Assert.assertEquals(MigrationManagerTest.DEPLOYMENT_ID_V2, task.getDeploymentId());
        Assert.assertEquals(taskName, task.getName());
        taskService.start(task.getId(), MigrationManagerTest.USER_JOHN);
        taskService.complete(task.getId(), MigrationManagerTest.USER_JOHN, null);
    }

    protected void assertMigratedProcessInstance(String processId, long processInstanceId, int status) {
        ProcessInstanceLog instance = auditService.findProcessInstance(processInstanceId);
        Assert.assertNotNull(instance);
        Assert.assertEquals(processId, instance.getProcessId());
        Assert.assertEquals(MigrationManagerTest.DEPLOYMENT_ID_V2, instance.getExternalId());
        Assert.assertEquals(status, instance.getStatus().intValue());
    }

    protected void createRuntimeManagers(String processV1, String processV2) {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().entityManagerFactory(emf).userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource(processV1), ResourceType.BPMN2).get();
        managerV1 = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment, MigrationManagerTest.DEPLOYMENT_ID_V1);
        RuntimeEnvironment environment2 = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().entityManagerFactory(emf).userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource(processV2), ResourceType.BPMN2).get();
        managerV2 = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment2, MigrationManagerTest.DEPLOYMENT_ID_V2);
    }
}

