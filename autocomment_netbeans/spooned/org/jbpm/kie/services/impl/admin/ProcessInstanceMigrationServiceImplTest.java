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


package org.jbpm.kie.services.impl.admin;

import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.junit.After;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import org.jbpm.services.api.model.DeploymentUnit;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import java.util.Iterator;
import org.jbpm.kie.services.test.KModuleDeploymentServiceTest;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.kie.api.KieServices;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.kie.scanner.MavenRepository;
import org.jbpm.services.api.admin.MigrationReport;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.api.admin.ProcessInstanceMigrationService;
import org.kie.api.builder.ReleaseId;
import org.kie.api.task.model.TaskSummary;
import org.junit.Test;

public class ProcessInstanceMigrationServiceImplTest extends AbstractKieServicesBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(KModuleDeploymentServiceTest.class);

    protected static final String MIGRATION_ARTIFACT_ID = "test-migration";

    protected static final String MIGRATION_GROUP_ID = "org.jbpm.test";

    protected static final String MIGRATION_VERSION_V1 = "1.0.0";

    protected static final String MIGRATION_VERSION_V2 = "2.0.0";

    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();

    private KModuleDeploymentUnit deploymentUnitV1;

    private KModuleDeploymentUnit deploymentUnitV2;

    protected ProcessInstanceMigrationService migrationService;

    @Before
    public void prepare() {
        configureServices();
        ProcessInstanceMigrationServiceImplTest.logger.debug("Preparing kjar");
        KieServices ks = KieServices.Factory.get();
        // version 1 of kjar
        ReleaseId releaseId = ks.newReleaseId(ProcessInstanceMigrationServiceImplTest.MIGRATION_GROUP_ID, ProcessInstanceMigrationServiceImplTest.MIGRATION_ARTIFACT_ID, ProcessInstanceMigrationServiceImplTest.MIGRATION_VERSION_V1);
        List<String> processes = new ArrayList<String>();
        processes.add("migration/v1/AddTaskAfterActive-v1.bpmn2");
        processes.add("migration/v1/RemoveActiveTask-v1.bpmn2");
        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes);
        File pom = new File("target/migration-v1", "pom.xml");
        pom.getParentFile().mkdir();
        try {
            FileOutputStream fs = new FileOutputStream(pom);
            fs.write(getPom(releaseId).getBytes());
            fs.close();
        } catch (Exception e) {
        }
        MavenRepository repository = MavenRepository.getMavenRepository();
        repository.installArtifact(releaseId, kJar1, pom);
        // version 2 of kjar
        ReleaseId releaseId2 = ks.newReleaseId(ProcessInstanceMigrationServiceImplTest.MIGRATION_GROUP_ID, ProcessInstanceMigrationServiceImplTest.MIGRATION_ARTIFACT_ID, ProcessInstanceMigrationServiceImplTest.MIGRATION_VERSION_V2);
        processes = new ArrayList<String>();
        processes.add("migration/v2/AddTaskAfterActive-v2.bpmn2");
        processes.add("migration/v2/RemoveActiveTask-v2.bpmn2");
        InternalKieModule kJar2 = createKieJar(ks, releaseId2, processes);
        File pom2 = new File("target/migration-v2", "pom.xml");
        pom2.getParentFile().mkdirs();
        try {
            FileOutputStream fs = new FileOutputStream(pom2);
            fs.write(getPom(releaseId2).getBytes());
            fs.close();
        } catch (Exception e) {
        }
        repository = MavenRepository.getMavenRepository();
        repository.installArtifact(releaseId2, kJar2, pom2);
        migrationService = new ProcessInstanceMigrationServiceImpl();
        // now let's deploy to runtime both kjars
        deploymentUnitV1 = new KModuleDeploymentUnit(ProcessInstanceMigrationServiceImplTest.MIGRATION_GROUP_ID, ProcessInstanceMigrationServiceImplTest.MIGRATION_ARTIFACT_ID, ProcessInstanceMigrationServiceImplTest.MIGRATION_VERSION_V1);
        deploymentService.deploy(deploymentUnitV1);
        units.add(deploymentUnitV1);
        deploymentUnitV2 = new KModuleDeploymentUnit(ProcessInstanceMigrationServiceImplTest.MIGRATION_GROUP_ID, ProcessInstanceMigrationServiceImplTest.MIGRATION_ARTIFACT_ID, ProcessInstanceMigrationServiceImplTest.MIGRATION_VERSION_V2);
        deploymentService.deploy(deploymentUnitV2);
        units.add(deploymentUnitV2);
    }

    @After
    public void cleanup() {
        cleanupSingletonSessionId();
        if (((units) != null) && (!(units.isEmpty()))) {
            for (DeploymentUnit unit : units) {
                try {
                    deploymentService.undeploy(unit);
                } catch (Exception e) {
                    // do nothing in case of some failed tests to avoid next test to fail as well
                }
            }
            units.clear();
        } 
        close();
    }

    public void setMigrationService(ProcessInstanceMigrationService migrationService) {
        ProcessInstanceMigrationServiceImplTest.this.migrationService = migrationService;
    }

    private static final String ADDTASKAFTERACTIVE_ID_V1 = "process-migration-testv1.AddTaskAfterActive";

    private static final String ADDTASKAFTERACTIVE_ID_V2 = "process-migration-testv2.AddTaskAfterActive";

    @Test
    public void testMigrateSingleProcessInstance() {
        long processInstanceId = processService.startProcess(deploymentUnitV1.getIdentifier(), ProcessInstanceMigrationServiceImplTest.ADDTASKAFTERACTIVE_ID_V1);
        Assert.assertNotNull(processInstanceId);
        MigrationReport report = migrationService.migrate(deploymentUnitV1.getIdentifier(), processInstanceId, deploymentUnitV2.getIdentifier(), ProcessInstanceMigrationServiceImplTest.ADDTASKAFTERACTIVE_ID_V2);
        Assert.assertNotNull(report);
        Assert.assertTrue(report.isSuccessful());
        assertMigratedProcessInstance(ProcessInstanceMigrationServiceImplTest.ADDTASKAFTERACTIVE_ID_V2, processInstanceId, ProcessInstance.STATE_ACTIVE);
        assertMigratedTaskAndComplete(ProcessInstanceMigrationServiceImplTest.ADDTASKAFTERACTIVE_ID_V2, processInstanceId, "Active Task");
        assertMigratedTaskAndComplete(ProcessInstanceMigrationServiceImplTest.ADDTASKAFTERACTIVE_ID_V2, processInstanceId, "Added Task");
        assertMigratedProcessInstance(ProcessInstanceMigrationServiceImplTest.ADDTASKAFTERACTIVE_ID_V2, processInstanceId, ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testMigrateMultipleProcessInstances() {
        List<Long> ids = new ArrayList<Long>();
        for (int i = 0; i < 5; i++) {
            long processInstanceId = processService.startProcess(deploymentUnitV1.getIdentifier(), ProcessInstanceMigrationServiceImplTest.ADDTASKAFTERACTIVE_ID_V1);
            Assert.assertNotNull(processInstanceId);
            ids.add(processInstanceId);
        }
        List<MigrationReport> reports = migrationService.migrate(deploymentUnitV1.getIdentifier(), ids, deploymentUnitV2.getIdentifier(), ProcessInstanceMigrationServiceImplTest.ADDTASKAFTERACTIVE_ID_V2);
        Assert.assertNotNull(reports);
        Iterator<MigrationReport> reportsIt = reports.iterator();
        for (Long processInstanceId : ids) {
            MigrationReport report = reportsIt.next();
            Assert.assertTrue(report.isSuccessful());
            assertMigratedProcessInstance(ProcessInstanceMigrationServiceImplTest.ADDTASKAFTERACTIVE_ID_V2, processInstanceId, ProcessInstance.STATE_ACTIVE);
            assertMigratedTaskAndComplete(ProcessInstanceMigrationServiceImplTest.ADDTASKAFTERACTIVE_ID_V2, processInstanceId, "Active Task");
            assertMigratedTaskAndComplete(ProcessInstanceMigrationServiceImplTest.ADDTASKAFTERACTIVE_ID_V2, processInstanceId, "Added Task");
            assertMigratedProcessInstance(ProcessInstanceMigrationServiceImplTest.ADDTASKAFTERACTIVE_ID_V2, processInstanceId, ProcessInstance.STATE_COMPLETED);
        }
    }

    private static final String REMOVEACTIVETASK_ID_V1 = "process-migration-testv1.RemoveActiveTask";

    private static final String REMOVEACTIVETASK_ID_V2 = "process-migration-testv2.RemoveActiveTask";

    @Test
    public void testMigrateSingleProcessInstanceWithNodeMapping() {
        String activeNodeId = "_ECEDD1CE-7380-418C-B7A6-AF8ECB90B820";
        String nextNodeId = "_9EF3CAE0-D978-4E96-9C00-8A80082EB68E";
        Map<String, String> nodeMapping = new HashMap<String, String>();
        nodeMapping.put(activeNodeId, nextNodeId);
        long processInstanceId = processService.startProcess(deploymentUnitV1.getIdentifier(), ProcessInstanceMigrationServiceImplTest.REMOVEACTIVETASK_ID_V1);
        Assert.assertNotNull(processInstanceId);
        MigrationReport report = migrationService.migrate(deploymentUnitV1.getIdentifier(), processInstanceId, deploymentUnitV2.getIdentifier(), ProcessInstanceMigrationServiceImplTest.REMOVEACTIVETASK_ID_V2, nodeMapping);
        Assert.assertNotNull(report);
        Assert.assertTrue(report.isSuccessful());
        assertMigratedProcessInstance(ProcessInstanceMigrationServiceImplTest.REMOVEACTIVETASK_ID_V2, processInstanceId, ProcessInstance.STATE_ACTIVE);
        assertMigratedTaskAndComplete(ProcessInstanceMigrationServiceImplTest.REMOVEACTIVETASK_ID_V2, processInstanceId, "Mapped Task");
        assertMigratedProcessInstance(ProcessInstanceMigrationServiceImplTest.REMOVEACTIVETASK_ID_V2, processInstanceId, ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testMigrateMultipleProcessInstancesWithNodeMapping() {
        List<Long> ids = new ArrayList<Long>();
        for (int i = 0; i < 5; i++) {
            long processInstanceId = processService.startProcess(deploymentUnitV1.getIdentifier(), ProcessInstanceMigrationServiceImplTest.REMOVEACTIVETASK_ID_V1);
            Assert.assertNotNull(processInstanceId);
            ids.add(processInstanceId);
        }
        String activeNodeId = "_ECEDD1CE-7380-418C-B7A6-AF8ECB90B820";
        String nextNodeId = "_9EF3CAE0-D978-4E96-9C00-8A80082EB68E";
        Map<String, String> nodeMapping = new HashMap<String, String>();
        nodeMapping.put(activeNodeId, nextNodeId);
        List<MigrationReport> reports = migrationService.migrate(deploymentUnitV1.getIdentifier(), ids, deploymentUnitV2.getIdentifier(), ProcessInstanceMigrationServiceImplTest.REMOVEACTIVETASK_ID_V2, nodeMapping);
        Assert.assertNotNull(reports);
        Iterator<MigrationReport> reportsIt = reports.iterator();
        for (Long processInstanceId : ids) {
            MigrationReport report = reportsIt.next();
            Assert.assertTrue(report.isSuccessful());
            assertMigratedProcessInstance(ProcessInstanceMigrationServiceImplTest.REMOVEACTIVETASK_ID_V2, processInstanceId, ProcessInstance.STATE_ACTIVE);
            assertMigratedTaskAndComplete(ProcessInstanceMigrationServiceImplTest.REMOVEACTIVETASK_ID_V2, processInstanceId, "Mapped Task");
            assertMigratedProcessInstance(ProcessInstanceMigrationServiceImplTest.REMOVEACTIVETASK_ID_V2, processInstanceId, ProcessInstance.STATE_COMPLETED);
        }
    }

    /* Helper methods */
    protected void assertMigratedTaskAndComplete(String processId, Long processInstanceId, String taskName) {
        List<TaskSummary> tasks = runtimeDataService.getTasksByStatusByProcessInstanceId(processInstanceId, Arrays.asList(Status.Reserved), new org.kie.internal.query.QueryFilter());
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        Assert.assertNotNull(task);
        Assert.assertEquals(processId, task.getProcessId());
        Assert.assertEquals(deploymentUnitV2.getIdentifier(), task.getDeploymentId());
        Assert.assertEquals(taskName, task.getName());
        userTaskService.completeAutoProgress(task.getId(), "john", null);
    }

    protected void assertMigratedProcessInstance(String processId, long processInstanceId, int status) {
        ProcessInstanceDesc instance = runtimeDataService.getProcessInstanceById(processInstanceId);
        Assert.assertNotNull(instance);
        Assert.assertEquals(processId, instance.getProcessId());
        Assert.assertEquals(deploymentUnitV2.getIdentifier(), instance.getDeploymentId());
        Assert.assertEquals(status, instance.getState().intValue());
    }
}

