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


package org.jbpm.casemgmt.impl;

import org.jbpm.casemgmt.impl.util.AbstractCaseServicesBaseTest;
import org.jbpm.casemgmt.api.model.AdHocFragment;
import org.junit.After;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Before;
import org.jbpm.casemgmt.api.model.CaseDefinition;
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.api.model.instance.CaseInstance;
import org.jbpm.casemgmt.api.model.CaseMilestone;
import org.jbpm.casemgmt.api.model.CaseRole;
import org.jbpm.casemgmt.api.model.CaseStage;
import java.util.Collection;
import org.jbpm.services.api.model.DeploymentUnit;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.kie.api.KieServices;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.kie.scanner.MavenRepository;
import org.jbpm.services.api.model.NodeInstanceDesc;
import org.kie.api.builder.ReleaseId;
import org.junit.Test;

public class CaseRuntimeDataServiceImplTest extends AbstractCaseServicesBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(CaseRuntimeDataServiceImplTest.class);

    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();

    @Before
    public void prepare() {
        configureServices();
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("cases/EmptyCase.bpmn2");
        processes.add("cases/UserTaskCase.bpmn2");
        processes.add("cases/UserTaskWithStageCase.bpmn2");
        // add processes that can be used by cases but are not cases themselves
        processes.add("processes/DataVerificationProcess.bpmn2");
        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes);
        File pom = new File("target/kmodule", "pom.xml");
        pom.getParentFile().mkdir();
        try {
            FileOutputStream fs = new FileOutputStream(pom);
            fs.write(getPom(releaseId).getBytes());
            fs.close();
        } catch (Exception e) {
        }
        MavenRepository repository = MavenRepository.getMavenRepository();
        repository.deployArtifact(releaseId, kJar1, pom);
    }

    @After
    public void cleanup() {
        cleanupSingletonSessionId();
        if (((units) != null) && (!(units.isEmpty()))) {
            for (DeploymentUnit unit : units) {
                deploymentService.undeploy(unit);
            }
            units.clear();
        } 
        close();
    }

    @Test
    public void testGetCaseDefinitions() {
        Assert.assertNotNull(deploymentService);
        DeploymentUnit deploymentUnit = new org.jbpm.kie.services.impl.KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        Collection<CaseDefinition> cases = caseRuntimeDataService.getCases(new org.kie.internal.query.QueryContext());
        Assert.assertNotNull(cases);
        Assert.assertEquals(3, cases.size());
        Map<String, CaseDefinition> mappedCases = mapCases(cases);
        Assert.assertTrue(mappedCases.containsKey("EmptyCase"));
        Assert.assertTrue(mappedCases.containsKey("UserTaskCase"));
        Assert.assertTrue(mappedCases.containsKey("UserTaskWithStageCase"));
        // EmptyCase asserts
        CaseDefinition caseDef = mappedCases.get("EmptyCase");
        Assert.assertNotNull(caseDef);
        Assert.assertEquals("EmptyCase", caseDef.getId());
        Assert.assertEquals("New Case", caseDef.getName());
        Assert.assertEquals("", caseDef.getVersion());
        Assert.assertEquals(CaseDefinition.DEFAULT_PREFIX, caseDef.getIdentifierPrefix());
        Assert.assertNotNull(caseDef.getCaseMilestones());
        Assert.assertTrue(caseDef.getCaseMilestones().isEmpty());
        Assert.assertNotNull(caseDef.getCaseStages());
        Assert.assertTrue(caseDef.getCaseStages().isEmpty());
        Assert.assertNotNull(caseDef.getCaseRoles());
        Assert.assertTrue(caseDef.getCaseRoles().isEmpty());
        Assert.assertEquals(deploymentUnit.getIdentifier(), caseDef.getDeploymentId());
        // UserTaskCase asserts
        caseDef = mappedCases.get("UserTaskCase");
        Assert.assertNotNull(caseDef);
        Assert.assertEquals("UserTaskCase", caseDef.getId());
        Assert.assertEquals("Simple Case with User Tasks", caseDef.getName());
        Assert.assertEquals("1.0", caseDef.getVersion());
        Assert.assertEquals("HR", caseDef.getIdentifierPrefix());
        Assert.assertNotNull(caseDef.getCaseMilestones());
        Assert.assertEquals(2, caseDef.getCaseMilestones().size());
        Map<String, CaseMilestone> mappedMilestones = mapMilestones(caseDef.getCaseMilestones());
        Assert.assertTrue(mappedMilestones.containsKey("Milestone1"));
        Assert.assertTrue(mappedMilestones.containsKey("Milestone2"));
        CaseMilestone milestone = mappedMilestones.get("Milestone1");
        Assert.assertEquals("_SomeID4", milestone.getId());
        Assert.assertEquals("Milestone1", milestone.getName());
        Assert.assertEquals("", milestone.getAchievementCondition());
        Assert.assertEquals(false, milestone.isMandatory());
        milestone = mappedMilestones.get("Milestone2");
        Assert.assertEquals("_5", milestone.getId());
        Assert.assertEquals("Milestone2", milestone.getName());
        Assert.assertEquals("org.kie.internal.process.CaseData(data.get(\"dataComplete\") == true)", milestone.getAchievementCondition());
        Assert.assertEquals(false, milestone.isMandatory());
        Assert.assertNotNull(caseDef.getCaseStages());
        Assert.assertEquals(0, caseDef.getCaseStages().size());
        Assert.assertNotNull(caseDef.getCaseRoles());
        Assert.assertEquals(3, caseDef.getCaseRoles().size());
        Map<String, CaseRole> mappedRoles = mapRoles(caseDef.getCaseRoles());
        Assert.assertTrue(mappedRoles.containsKey("owner"));
        Assert.assertTrue(mappedRoles.containsKey("contact"));
        Assert.assertTrue(mappedRoles.containsKey("participant"));
        Assert.assertEquals(1, mappedRoles.get("owner").getCardinality().intValue());
        Assert.assertEquals(2, mappedRoles.get("contact").getCardinality().intValue());
        Assert.assertEquals((-1), mappedRoles.get("participant").getCardinality().intValue());
        // UserTaskWithStageCase asserts
        caseDef = mappedCases.get("UserTaskWithStageCase");
        Assert.assertNotNull(caseDef);
        Assert.assertEquals("UserTaskWithStageCase", caseDef.getId());
        Assert.assertEquals("UserTaskWithStageCase", caseDef.getName());
        Assert.assertEquals("1.0", caseDef.getVersion());
        Assert.assertEquals(CaseDefinition.DEFAULT_PREFIX, caseDef.getIdentifierPrefix());
        Assert.assertNotNull(caseDef.getCaseMilestones());
        Assert.assertEquals(0, caseDef.getCaseMilestones().size());
        Assert.assertEquals(deploymentUnit.getIdentifier(), caseDef.getDeploymentId());
        Assert.assertNotNull(caseDef.getCaseStages());
        Assert.assertEquals(1, caseDef.getCaseStages().size());
        Map<String, CaseStage> mappedStages = mapStages(caseDef.getCaseStages());
        Assert.assertTrue(mappedStages.containsKey("Collect input"));
        CaseStage caseStage = mappedStages.get("Collect input");
        Assert.assertNotNull(caseStage);
        Assert.assertEquals("Collect input", caseStage.getName());
        Assert.assertEquals(2, caseStage.getAdHocFragments().size());
        Map<String, AdHocFragment> mappedFragments = mapAdHocFragments(caseStage.getAdHocFragments());
        Assert.assertTrue(mappedFragments.containsKey("Missing data"));
        Assert.assertEquals("HumanTaskNode", mappedFragments.get("Missing data").getType());
        Assert.assertTrue(mappedFragments.containsKey("Verification of data"));
        Assert.assertEquals("SubProcessNode", mappedFragments.get("Verification of data").getType());
        Assert.assertNotNull(caseDef.getCaseRoles());
        Assert.assertEquals(3, caseDef.getCaseRoles().size());
        mappedRoles = mapRoles(caseDef.getCaseRoles());
        Assert.assertTrue(mappedRoles.containsKey("owner"));
        Assert.assertTrue(mappedRoles.containsKey("contact"));
        Assert.assertTrue(mappedRoles.containsKey("participant"));
        Assert.assertEquals(1, mappedRoles.get("owner").getCardinality().intValue());
        Assert.assertEquals(2, mappedRoles.get("contact").getCardinality().intValue());
        Assert.assertEquals((-1), mappedRoles.get("participant").getCardinality().intValue());
    }

    @Test
    public void testGetCaseDefinitionsByDeploymentId() {
        Assert.assertNotNull(deploymentService);
        DeploymentUnit deploymentUnit = new org.jbpm.kie.services.impl.KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        Collection<CaseDefinition> cases = caseRuntimeDataService.getCasesByDeployment(deploymentUnit.getIdentifier(), new org.kie.internal.query.QueryContext());
        Assert.assertNotNull(cases);
        Assert.assertEquals(3, cases.size());
        Map<String, CaseDefinition> mappedCases = mapCases(cases);
        Assert.assertTrue(mappedCases.containsKey("EmptyCase"));
        Assert.assertTrue(mappedCases.containsKey("UserTaskCase"));
        Assert.assertTrue(mappedCases.containsKey("UserTaskWithStageCase"));
        cases = caseRuntimeDataService.getCasesByDeployment("not-existing", new org.kie.internal.query.QueryContext());
        Assert.assertNotNull(cases);
        Assert.assertEquals(0, cases.size());
    }

    @Test
    public void testGetCaseDefinitionsByFilter() {
        Assert.assertNotNull(deploymentService);
        DeploymentUnit deploymentUnit = new org.jbpm.kie.services.impl.KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        Collection<CaseDefinition> cases = caseRuntimeDataService.getCases("empty", new org.kie.internal.query.QueryContext());
        Assert.assertNotNull(cases);
        Assert.assertEquals(1, cases.size());
        Map<String, CaseDefinition> mappedCases = mapCases(cases);
        Assert.assertTrue(mappedCases.containsKey("EmptyCase"));
        cases = caseRuntimeDataService.getCases("User", new org.kie.internal.query.QueryContext());
        Assert.assertNotNull(cases);
        Assert.assertEquals(2, cases.size());
        mappedCases = mapCases(cases);
        Assert.assertTrue(mappedCases.containsKey("UserTaskCase"));
        Assert.assertTrue(mappedCases.containsKey("UserTaskWithStageCase"));
        cases = caseRuntimeDataService.getCases("nomatch", new org.kie.internal.query.QueryContext());
        Assert.assertNotNull(cases);
        Assert.assertEquals(0, cases.size());
    }

    @Test
    public void testGetCaseDefinitionById() {
        Assert.assertNotNull(deploymentService);
        DeploymentUnit deploymentUnit = new org.jbpm.kie.services.impl.KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        CaseDefinition caseDef = caseRuntimeDataService.getCase(deploymentUnit.getIdentifier(), "UserTaskWithStageCase");
        // UserTaskWithStageCase asserts
        Assert.assertNotNull(caseDef);
        Assert.assertEquals("UserTaskWithStageCase", caseDef.getId());
        Assert.assertEquals("UserTaskWithStageCase", caseDef.getName());
        Assert.assertEquals("1.0", caseDef.getVersion());
        Assert.assertEquals(CaseDefinition.DEFAULT_PREFIX, caseDef.getIdentifierPrefix());
        Assert.assertNotNull(caseDef.getCaseMilestones());
        Assert.assertEquals(0, caseDef.getCaseMilestones().size());
        Assert.assertEquals(deploymentUnit.getIdentifier(), caseDef.getDeploymentId());
        Assert.assertNotNull(caseDef.getCaseStages());
        Assert.assertEquals(1, caseDef.getCaseStages().size());
        Map<String, CaseStage> mappedStages = mapStages(caseDef.getCaseStages());
        Assert.assertTrue(mappedStages.containsKey("Collect input"));
        CaseStage caseStage = mappedStages.get("Collect input");
        Assert.assertNotNull(caseStage);
        Assert.assertEquals("Collect input", caseStage.getName());
        Assert.assertEquals(2, caseStage.getAdHocFragments().size());
        Map<String, AdHocFragment> mappedFragments = mapAdHocFragments(caseStage.getAdHocFragments());
        Assert.assertTrue(mappedFragments.containsKey("Missing data"));
        Assert.assertEquals("HumanTaskNode", mappedFragments.get("Missing data").getType());
        Assert.assertTrue(mappedFragments.containsKey("Verification of data"));
        Assert.assertEquals("SubProcessNode", mappedFragments.get("Verification of data").getType());
        Assert.assertNotNull(caseDef.getCaseRoles());
        Assert.assertEquals(3, caseDef.getCaseRoles().size());
        Map<String, CaseRole> mappedRoles = mapRoles(caseDef.getCaseRoles());
        Assert.assertTrue(mappedRoles.containsKey("owner"));
        Assert.assertTrue(mappedRoles.containsKey("contact"));
        Assert.assertTrue(mappedRoles.containsKey("participant"));
        Assert.assertEquals(1, mappedRoles.get("owner").getCardinality().intValue());
        Assert.assertEquals(2, mappedRoles.get("contact").getCardinality().intValue());
        Assert.assertEquals((-1), mappedRoles.get("participant").getCardinality().intValue());
    }

    /* Case instance queries */
    @Test
    public void testStartEmptyCaseWithCaseFile() {
        Assert.assertNotNull(deploymentService);
        DeploymentUnit deploymentUnit = new org.jbpm.kie.services.impl.KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        Map<String, Object> data = new HashMap<>();
        data.put("name", "my first case");
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID, data);
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID, caseFile);
        Assert.assertNotNull(caseId);
        Assert.assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId, true, false, false, false);
            Assert.assertNotNull(cInstance);
            Assert.assertEquals(FIRST_CASE_ID, cInstance.getCaseId());
            Assert.assertNotNull(cInstance.getCaseFile());
            Assert.assertEquals("my first case", cInstance.getCaseFile().getData("name"));
            Collection<CaseInstance> instances = caseRuntimeDataService.getCaseInstances(new org.kie.internal.query.QueryContext());
            Assert.assertNotNull(instances);
            Assert.assertEquals(1, instances.size());
            CaseInstance instance = instances.iterator().next();
            Assert.assertNotNull(instance);
            Assert.assertEquals(FIRST_CASE_ID, instance.getCaseId());
            Assert.assertEquals(EMPTY_CASE_P_ID, instance.getCaseDefinitionId());
            Assert.assertEquals("my first case", instance.getCaseDescription());
            Assert.assertEquals("testUser", instance.getOwner());
            Assert.assertEquals(ProcessInstance.STATE_ACTIVE, instance.getStatus().intValue());
            Assert.assertEquals(deploymentUnit.getIdentifier(), instance.getDeploymentId());
            Assert.assertNotNull(instance.getStartedAt());
            // add dynamic user task to empty case instance - first by case id
            Map<String, Object> parameters = new HashMap<>();
            caseService.addDynamicTask(FIRST_CASE_ID, caseService.newHumanTaskSpec("First task", "test", "john", null, parameters));
            Collection<NodeInstanceDesc> activeNodes = caseRuntimeDataService.getActiveNodesForCase(FIRST_CASE_ID, new org.kie.internal.query.QueryContext());
            Assert.assertNotNull(activeNodes);
            Assert.assertEquals(1, activeNodes.size());
            NodeInstanceDesc activeNode = activeNodes.iterator().next();
            Assert.assertNotNull(activeNodes);
            Assert.assertEquals("[Dynamic] First task", activeNode.getName());
        } catch (Exception e) {
            CaseRuntimeDataServiceImplTest.logger.error("Unexpected error {}", e.getMessage(), e);
            Assert.fail(("Unexpected exception " + (e.getMessage())));
        } finally {
            if (caseId != null) {
                caseService.cancelCase(caseId);
            } 
        }
    }
}

