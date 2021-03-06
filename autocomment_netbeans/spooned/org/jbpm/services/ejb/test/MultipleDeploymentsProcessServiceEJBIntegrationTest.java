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


package org.jbpm.services.ejb.test;

import org.junit.After;
import org.jboss.arquillian.junit.Arquillian;
import java.util.ArrayList;
import org.junit.Assert;
import org.jbpm.services.ejb.api.DefinitionServiceEJBLocal;
import org.jboss.arquillian.container.test.api.Deployment;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.jbpm.services.ejb.api.DeploymentServiceEJBLocal;
import org.jbpm.services.api.model.DeploymentUnit;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import java.io.File;
import java.io.FileOutputStream;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.kie.api.KieServices;
import java.util.List;
import org.kie.scanner.MavenRepository;
import org.kie.api.runtime.process.ProcessInstance;
import org.jbpm.services.ejb.api.ProcessServiceEJBLocal;
import org.kie.api.builder.ReleaseId;
import org.junit.runner.RunWith;
import org.jbpm.services.ejb.api.RuntimeDataServiceEJBLocal;
import org.kie.internal.runtime.manager.SessionNotFoundException;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Test;
import org.jboss.shrinkwrap.api.spec.WebArchive;

@RunWith(value = Arquillian.class)
public class MultipleDeploymentsProcessServiceEJBIntegrationTest extends AbstractTestSupport {
    protected static final String ARTIFACT_ID2 = "second-test-module";

    protected static final String GROUP_ID2 = "org.jbpm.test";

    protected static final String VERSION2 = "1.0.0-SNAPSHOT";

    @Deployment
    public static WebArchive createDeployment() {
        File archive = new File("target/sample-war-ejb-app.war");
        if (!(archive.exists())) {
            throw new IllegalStateException("There is no archive yet generated, run maven build or mvn assembly:assembly");
        } 
        WebArchive war = ShrinkWrap.createFromZipFile(WebArchive.class, archive);
        war.addPackage("org.jbpm.services.ejb.test");// test cases
        
        // deploy test kjar
        MultipleDeploymentsProcessServiceEJBIntegrationTest.deployKjar();
        return war;
    }

    protected static void deployKjar() {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("processes/signal.bpmn");
        processes.add("processes/import.bpmn");
        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes);
        File pom = new File("target/kmodule", "pom.xml");
        pom.getParentFile().mkdir();
        try {
            FileOutputStream fs = new FileOutputStream(pom);
            fs.write(getPom(releaseId).getBytes());
            fs.close();
        } catch (Exception e) {
        }
        // second kjar
        ReleaseId releaseId2 = ks.newReleaseId(MultipleDeploymentsProcessServiceEJBIntegrationTest.GROUP_ID2, MultipleDeploymentsProcessServiceEJBIntegrationTest.ARTIFACT_ID2, MultipleDeploymentsProcessServiceEJBIntegrationTest.VERSION2);
        List<String> processes2 = new ArrayList<String>();
        processes2.add("processes/customtask.bpmn");
        processes2.add("processes/humanTask.bpmn");
        InternalKieModule kJar2 = createKieJar(ks, releaseId2, processes2);
        File pom2 = new File("target/kmodule2", "pom.xml");
        pom2.getParentFile().mkdir();
        try {
            FileOutputStream fs = new FileOutputStream(pom2);
            fs.write(getPom(releaseId2).getBytes());
            fs.close();
        } catch (Exception e) {
        }
        MavenRepository repository = MavenRepository.getMavenRepository();
        repository.installArtifact(releaseId, kJar1, pom);
        repository.installArtifact(releaseId2, kJar2, pom2);
    }

    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();

    @After
    public void cleanup() {
        cleanupSingletonSessionId();
        if (((units) != null) && (!(units.isEmpty()))) {
            for (DeploymentUnit unit : units) {
                deploymentService.undeploy(unit);
            }
            units.clear();
        } 
    }

    @EJB
    private DefinitionServiceEJBLocal bpmn2Service;

    @EJB
    private DeploymentServiceEJBLocal deploymentService;

    @EJB
    private ProcessServiceEJBLocal processService;

    @EJB
    private RuntimeDataServiceEJBLocal runtimeDataService;

    @Test
    public void testStartProcessFromDifferentDeployments() {
        Assert.assertNotNull(deploymentService);
        DeploymentDescriptor customDescriptor = new org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorImpl("org.jbpm.domain");
        customDescriptor.getBuilder().runtimeStrategy(RuntimeStrategy.PER_PROCESS_INSTANCE);
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentUnit.setDeploymentDescriptor(customDescriptor);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        DeploymentDescriptor customDescriptor2 = new org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorImpl("org.jbpm.domain");
        customDescriptor.getBuilder().runtimeStrategy(RuntimeStrategy.PER_PROCESS_INSTANCE);
        KModuleDeploymentUnit deploymentUnit2 = new KModuleDeploymentUnit(MultipleDeploymentsProcessServiceEJBIntegrationTest.GROUP_ID2, MultipleDeploymentsProcessServiceEJBIntegrationTest.ARTIFACT_ID2, MultipleDeploymentsProcessServiceEJBIntegrationTest.VERSION2);
        deploymentUnit2.setDeploymentDescriptor(customDescriptor2);
        deploymentService.deploy(deploymentUnit2);
        units.add(deploymentUnit2);
        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
        Assert.assertTrue(isDeployed);
        isDeployed = deploymentService.isDeployed(deploymentUnit2.getIdentifier());
        Assert.assertTrue(isDeployed);
        Assert.assertNotNull(processService);
        // first process from deployment 1
        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "Import");
        Assert.assertNotNull(processInstanceId);
        try {
            ProcessInstance pi = processService.getProcessInstance(processInstanceId);
            Assert.assertNull(pi);
        } catch (EJBException e) {
            if ((e.getCause()) instanceof SessionNotFoundException) {
                // ignore as this is expected when per process instance is used
            } else {
                throw e;
            }
        }
        // second process from deployment 2
        long processInstanceId2 = processService.startProcess(deploymentUnit2.getIdentifier(), "customtask");
        Assert.assertNotNull(processInstanceId2);
        try {
            ProcessInstance pi2 = processService.getProcessInstance(processInstanceId2);
            Assert.assertNull(pi2);
        } catch (EJBException e) {
            if ((e.getCause()) instanceof SessionNotFoundException) {
                // ignore as this is expected when per process instance is used
            } else {
                throw e;
            }
        }
    }
}

