/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.kie.test.util;

import org.junit.AfterClass;
import org.junit.Assert;
import org.jbpm.kie.services.impl.bpmn2.BPMN2DataServiceImpl;
import org.junit.BeforeClass;
import java.util.concurrent.BrokenBarrierException;
import org.kie.api.runtime.conf.ClockTypeOption;
import java.util.concurrent.CyclicBarrier;
import org.dashbuilder.DataSetCore;
import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.api.DeploymentService;
import javax.persistence.EntityManagerFactory;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import java.io.File;
import java.io.FilenameFilter;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.kie.services.impl.KModuleDeploymentService;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.KieServices;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jbpm.process.instance.impl.util.LoggingPrintStream;
import java.util.Map;
import org.kie.api.builder.Message;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import org.jbpm.services.api.ProcessService;
import org.jbpm.kie.services.impl.ProcessServiceImpl;
import org.jbpm.services.api.query.QueryService;
import org.jbpm.kie.services.impl.query.QueryServiceImpl;
import org.kie.api.builder.ReleaseId;
import org.kie.internal.io.ResourceFactory;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.kie.services.impl.RuntimeDataServiceImpl;
import org.jbpm.services.task.audit.TaskAuditServiceFactory;
import org.kie.api.task.TaskService;
import org.jbpm.kie.services.test.TestIdentityProvider;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.kie.services.impl.UserTaskServiceImpl;

public abstract class AbstractKieServicesBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(AbstractKieServicesBaseTest.class);

    protected static final String ARTIFACT_ID = "test-module";

    protected static final String GROUP_ID = "org.jbpm.test";

    protected static final String VERSION = "1.0.0-SNAPSHOT";

    protected PoolingDataSource ds;

    protected EntityManagerFactory emf;

    protected DeploymentService deploymentService;

    protected DefinitionService bpmn2Service;

    protected RuntimeDataService runtimeDataService;

    protected ProcessService processService;

    protected UserTaskService userTaskService;

    protected QueryService queryService;

    protected TestIdentityProvider identityProvider;

    @BeforeClass
    public static void configure() {
        LoggingPrintStream.interceptSysOutSysErr();
    }

    @AfterClass
    public static void reset() {
        LoggingPrintStream.resetInterceptSysOutSysErr();
    }

    protected void close() {
        DataSetCore.set(null);
        if ((emf) != null) {
            emf.close();
        } 
        EntityManagerFactoryManager.get().clear();
        closeDataSource();
    }

    protected void configureServices() {
        buildDatasource();
        emf = EntityManagerFactoryManager.get().getOrCreate("org.jbpm.domain");
        identityProvider = new TestIdentityProvider();
        // build definition service
        bpmn2Service = new BPMN2DataServiceImpl();
        queryService = new QueryServiceImpl();
        ((QueryServiceImpl) (queryService)).setIdentityProvider(identityProvider);
        ((QueryServiceImpl) (queryService)).setCommandService(new org.jbpm.shared.services.impl.TransactionalCommandService(emf));
        ((QueryServiceImpl) (queryService)).init();
        // build deployment service
        deploymentService = new KModuleDeploymentService();
        ((KModuleDeploymentService) (deploymentService)).setBpmn2Service(bpmn2Service);
        ((KModuleDeploymentService) (deploymentService)).setEmf(emf);
        ((KModuleDeploymentService) (deploymentService)).setIdentityProvider(identityProvider);
        ((KModuleDeploymentService) (deploymentService)).setManagerFactory(new org.jbpm.runtime.manager.impl.RuntimeManagerFactoryImpl());
        ((KModuleDeploymentService) (deploymentService)).setFormManagerService(new org.jbpm.kie.services.impl.FormManagerServiceImpl());
        TaskService taskService = HumanTaskServiceFactory.newTaskServiceConfigurator().entityManagerFactory(emf).getTaskService();
        // build runtime data service
        runtimeDataService = new RuntimeDataServiceImpl();
        ((RuntimeDataServiceImpl) (runtimeDataService)).setCommandService(new org.jbpm.shared.services.impl.TransactionalCommandService(emf));
        ((RuntimeDataServiceImpl) (runtimeDataService)).setIdentityProvider(identityProvider);
        ((RuntimeDataServiceImpl) (runtimeDataService)).setTaskService(taskService);
        ((RuntimeDataServiceImpl) (runtimeDataService)).setTaskAuditService(TaskAuditServiceFactory.newTaskAuditServiceConfigurator().setTaskService(taskService).getTaskAuditService());
        ((KModuleDeploymentService) (deploymentService)).setRuntimeDataService(runtimeDataService);
        // set runtime data service as listener on deployment service
        ((KModuleDeploymentService) (deploymentService)).addListener(((RuntimeDataServiceImpl) (runtimeDataService)));
        ((KModuleDeploymentService) (deploymentService)).addListener(((BPMN2DataServiceImpl) (bpmn2Service)));
        ((KModuleDeploymentService) (deploymentService)).addListener(((QueryServiceImpl) (queryService)));
        // build process service
        processService = new ProcessServiceImpl();
        ((ProcessServiceImpl) (processService)).setDataService(runtimeDataService);
        ((ProcessServiceImpl) (processService)).setDeploymentService(deploymentService);
        // build user task service
        userTaskService = new UserTaskServiceImpl();
        ((UserTaskServiceImpl) (userTaskService)).setDataService(runtimeDataService);
        ((UserTaskServiceImpl) (userTaskService)).setDeploymentService(deploymentService);
    }

    protected String getPom(ReleaseId releaseId, ReleaseId... dependencies) {
        String pom = ((((((((("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ("<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + ("         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" + ("  <modelVersion>4.0.0</modelVersion>\n" + ("\n" + "  <groupId>"))))) + (releaseId.getGroupId())) + "</groupId>\n") + "  <artifactId>") + (releaseId.getArtifactId())) + "</artifactId>\n") + "  <version>") + (releaseId.getVersion())) + "</version>\n") + "\n";
        if ((dependencies != null) && (dependencies > 0)) {
            pom += "<dependencies>\n";
            for (ReleaseId dep : dependencies) {
                pom += "<dependency>\n";
                pom += ("  <groupId>" + (dep.getGroupId())) + "</groupId>\n";
                pom += ("  <artifactId>" + (dep.getArtifactId())) + "</artifactId>\n";
                pom += ("  <version>" + (dep.getVersion())) + "</version>\n";
                pom += "</dependency>\n";
            }
            pom += "</dependencies>\n";
        } 
        pom += "</project>";
        return pom;
    }

    protected InternalKieModule createKieJar(KieServices ks, ReleaseId releaseId, List<String> resources) {
        return createKieJar(ks, releaseId, resources, null);
    }

    protected InternalKieModule createKieJar(KieServices ks, ReleaseId releaseId, List<String> resources, Map<String, String> extraResources) {
        KieFileSystem kfs = createKieFileSystemWithKProject(ks);
        kfs.writePomXML(getPom(releaseId));
        for (String resource : resources) {
            kfs.write(("src/main/resources/KBase-test/" + resource), ResourceFactory.newClassPathResource(resource));
        }
        if (extraResources != null) {
            for (Map.Entry<String, String> entry : extraResources.entrySet()) {
                kfs.write(entry.getKey(), ResourceFactory.newByteArrayResource(entry.getValue().getBytes()));
            }
        } 
        kfs.write("src/main/resources/forms/DefaultProcess.ftl", ResourceFactory.newClassPathResource("repo/globals/forms/DefaultProcess.ftl"));
        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        if (!(kieBuilder.buildAll().getResults().getMessages().isEmpty())) {
            for (Message message : kieBuilder.buildAll().getResults().getMessages()) {
                AbstractKieServicesBaseTest.logger.error("Error Message: ({}) {}", message.getPath(), message.getText());
            }
            throw new RuntimeException("There are errors builing the package, please check your knowledge assets!");
        } 
        return ((InternalKieModule) (kieBuilder.getKieModule()));
    }

    protected KieFileSystem createKieFileSystemWithKProject(KieServices ks) {
        KieModuleModel kproj = ks.newKieModuleModel();
        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase-test").setDefault(true).addPackage("*").setEqualsBehavior(EqualityBehaviorOption.EQUALITY).setEventProcessingMode(EventProcessingOption.STREAM);
        kieBaseModel1.newKieSessionModel("ksession-test").setDefault(true).setType(KieSessionModel.KieSessionType.STATEFUL).setClockType(ClockTypeOption.get("realtime")).newWorkItemHandlerModel("Log", "new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler()");
        kieBaseModel1.newKieSessionModel("ksession-test-2").setDefault(false).setType(KieSessionModel.KieSessionType.STATEFUL).setClockType(ClockTypeOption.get("realtime")).newWorkItemHandlerModel("Log", "new org.jbpm.kie.services.test.objects.KieConteinerSystemOutWorkItemHandler(kieContainer)");
        kieBaseModel1.newKieSessionModel("ksession-test2").setDefault(false).setType(KieSessionModel.KieSessionType.STATEFUL).setClockType(ClockTypeOption.get("realtime"));
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(kproj.toXML());
        return kfs;
    }

    protected void buildDatasource() {
        ds = new PoolingDataSource();
        ds.setUniqueName("jdbc/testDS1");
        // NON XA CONFIGS
        ds.setClassName("org.h2.jdbcx.JdbcDataSource");
        ds.setMaxPoolSize(3);
        ds.setAllowLocalTransactions(true);
        ds.getDriverProperties().put("user", "sa");
        ds.getDriverProperties().put("password", "sasa");
        ds.getDriverProperties().put("URL", "jdbc:h2:mem:mydb");
        ds.init();
    }

    protected void closeDataSource() {
        if ((ds) != null) {
            ds.close();
        } 
    }

    public static void cleanupSingletonSessionId() {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        if (tempDir.exists()) {
            String[] jbpmSerFiles = tempDir.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith("-jbpmSessionId.ser");
                }
            });
            for (String file : jbpmSerFiles) {
                AbstractKieServicesBaseTest.logger.debug("Temp dir to be removed {} file {}", tempDir, file);
                new File(tempDir, file).delete();
            }
        } 
    }

    public void setDeploymentService(DeploymentService deploymentService) {
        AbstractKieServicesBaseTest.this.deploymentService = deploymentService;
    }

    public void setBpmn2Service(DefinitionService bpmn2Service) {
        AbstractKieServicesBaseTest.this.bpmn2Service = bpmn2Service;
    }

    public void setRuntimeDataService(RuntimeDataService runtimeDataService) {
        AbstractKieServicesBaseTest.this.runtimeDataService = runtimeDataService;
    }

    public void setProcessService(ProcessService processService) {
        AbstractKieServicesBaseTest.this.processService = processService;
    }

    public void setUserTaskService(UserTaskService userTaskService) {
        AbstractKieServicesBaseTest.this.userTaskService = userTaskService;
    }

    public void setQueryService(QueryService queryService) {
        AbstractKieServicesBaseTest.this.queryService = queryService;
    }

    public void setIdentityProvider(TestIdentityProvider identityProvider) {
        AbstractKieServicesBaseTest.this.identityProvider = identityProvider;
    }

    protected static void waitForTheOtherThreads(CyclicBarrier barrier) {
        try {
            barrier.await();
        } catch (InterruptedException e) {
            Assert.fail("Thread 1 was interrupted while waiting for the other threads!");
        } catch (BrokenBarrierException e) {
            Assert.fail("Thread 1's barrier was broken while waiting for the other threads!");
        }
    }
}

