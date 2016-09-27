/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.services.cdi.test;

import org.jbpm.process.audit.AbstractAuditLogger;
import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.junit.After;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.jbpm.process.audit.AuditLoggerFactory;
import javax.enterprise.inject.spi.BeanManager;
import org.junit.BeforeClass;
import java.util.Collection;
import org.jboss.arquillian.container.test.api.Deployment;
import org.kie.internal.runtime.manager.context.EmptyContext;
import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import javax.inject.Inject;
import org.jbpm.services.cdi.impl.manager.InjectableRegisterableItemsFactory;
import java.util.Iterator;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.kie.api.runtime.KieSession;
import org.jbpm.services.api.model.NodeInstanceDesc;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.io.ResourceFactory;
import org.junit.runner.RunWith;
import org.jbpm.services.api.RuntimeDataService;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.jbpm.kie.services.impl.audit.ServicesAwareAuditEventBuilder;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Test;
import org.jbpm.runtime.manager.util.TestUtil;

@RunWith(value = Arquillian.class)
public class RuntimeDataServiceTest extends AbstractKieServicesBaseTest {
    @Deployment
    public static Archive<?> createDeployment() {
        return // Identity Provider Test Impl here
        // deadlines
        // This should not be required here
        // work items org.jbpm.services.task.wih
        ShrinkWrap.create(JavaArchive.class, "domain-services.jar").addPackage("org.jbpm.services.task").addPackage("org.jbpm.services.task.wih").addPackage("org.jbpm.services.task.annotations").addPackage("org.jbpm.services.task.api").addPackage("org.jbpm.services.task.impl").addPackage("org.jbpm.services.task.events").addPackage("org.jbpm.services.task.exception").addPackage("org.jbpm.services.task.identity").addPackage("org.jbpm.services.task.factories").addPackage("org.jbpm.services.task.internals").addPackage("org.jbpm.services.task.internals.lifecycle").addPackage("org.jbpm.services.task.lifecycle.listeners").addPackage("org.jbpm.services.task.query").addPackage("org.jbpm.services.task.util").addPackage("org.jbpm.services.task.commands").addPackage("org.jbpm.services.task.deadlines").addPackage("org.jbpm.services.task.deadlines.notifications.impl").addPackage("org.jbpm.services.task.subtask").addPackage("org.jbpm.services.task.rule").addPackage("org.jbpm.services.task.rule.impl").addPackage("org.jbpm.services.task.audit.service").addPackage("org.kie.internal.runtime.manager").addPackage("org.kie.internal.runtime.manager.context").addPackage("org.kie.internal.runtime.manager.cdi.qualifier").addPackage("org.jbpm.runtime.manager.impl").addPackage("org.jbpm.runtime.manager.impl.cdi").addPackage("org.jbpm.runtime.manager.impl.factory").addPackage("org.jbpm.runtime.manager.impl.jpa").addPackage("org.jbpm.runtime.manager.impl.manager").addPackage("org.jbpm.runtime.manager.impl.task").addPackage("org.jbpm.runtime.manager.impl.tx").addPackage("org.jbpm.shared.services.api").addPackage("org.jbpm.shared.services.impl").addPackage("org.jbpm.shared.services.impl.tx").addPackage("org.jbpm.kie.services.api").addPackage("org.jbpm.kie.services.impl").addPackage("org.jbpm.kie.services.api.bpmn2").addPackage("org.jbpm.kie.services.impl.bpmn2").addPackage("org.jbpm.kie.services.impl.event.listeners").addPackage("org.jbpm.kie.services.impl.audit").addPackage("org.jbpm.kie.services.impl.form").addPackage("org.jbpm.kie.services.impl.form.provider").addPackage("org.jbpm.kie.services.impl.query").addPackage("org.jbpm.kie.services.impl.query.mapper").addPackage("org.jbpm.kie.services.impl.query.persistence").addPackage("org.jbpm.kie.services.impl.query.preprocessor").addPackage("org.jbpm.services.cdi").addPackage("org.jbpm.services.cdi.impl").addPackage("org.jbpm.services.cdi.impl.form").addPackage("org.jbpm.services.cdi.impl.manager").addPackage("org.jbpm.services.cdi.producer").addPackage("org.jbpm.services.cdi.impl.security").addPackage("org.jbpm.services.cdi.impl.query").addPackage("org.jbpm.kie.services.test").addPackage("org.jbpm.services.cdi.test").addClass("org.jbpm.services.cdi.test.util.CDITestHelperNoTaskService").addClass("org.jbpm.services.cdi.test.util.CountDownDeploymentListenerCDIImpl").addClass("org.jbpm.kie.services.test.objects.CoundDownDeploymentListener").addAsResource("jndi.properties", "jndi.properties").addAsManifestResource("META-INF/persistence.xml", ArchivePaths.create("persistence.xml")).addAsManifestResource("META-INF/beans.xml", ArchivePaths.create("beans.xml"));
    }

    @BeforeClass
    public static void setup() {
        TestUtil.cleanupSingletonSessionId();
    }

    @Override
    protected void close() {
        // do nothing here and let CDI close resources
    }

    @Override
    protected void configureServices() {
        // do nothing here and let CDI configure services
    }

    @After
    public void tearDownTest() {
    }

    /* end of initialization code, tests start here */
    @Inject
    private EntityManagerFactory emf;

    @Inject
    private BeanManager beanManager;

    @Inject
    private RuntimeDataService runtimeDataService;

    @Inject
    private RuntimeManagerFactory managerFactory;

    @Test
    public void testGetProcessInstanceHistory() throws IOException {
        // BZ1048741
        Assert.assertNotNull(managerFactory);
        String id = "custom-manager";
        AbstractAuditLogger auditLogger = AuditLoggerFactory.newJPAInstance();
        ServicesAwareAuditEventBuilder auditEventBuilder = new ServicesAwareAuditEventBuilder();
        auditEventBuilder.setIdentityProvider(new TestIdentifyProviderCDI());
        auditEventBuilder.setDeploymentUnitId(id);
        auditLogger.setBuilder(auditEventBuilder);
        RuntimeEnvironmentBuilder builder = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().entityManagerFactory(emf).registerableItemsFactory(InjectableRegisterableItemsFactory.getFactory(beanManager, auditLogger));
        builder.addAsset(ResourceFactory.newClassPathResource("repo/processes/general/hello.bpmn"), ResourceType.BPMN2);
        RuntimeManager manager = managerFactory.newSingletonRuntimeManager(builder.get(), id);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        ProcessInstance processInstance = ksession.startProcess("hello");
        Collection<NodeInstanceDesc> nodeInstanceLogs = runtimeDataService.getProcessInstanceHistoryCompleted(processInstance.getId(), new org.kie.api.runtime.query.QueryContext());
        // assert if logs are ordered by log.date DESC, log.id DESC
        Iterator<NodeInstanceDesc> iterator = nodeInstanceLogs.iterator();
        NodeInstanceDesc log0 = iterator.next();
        Assert.assertEquals("End", log0.getName());
        NodeInstanceDesc log1 = iterator.next();
        Assert.assertEquals("Hello", log1.getName());
        NodeInstanceDesc log2 = iterator.next();
        Assert.assertEquals("Start", log2.getName());
        Collection<NodeInstanceDesc> fullNodeInstanceLogs = runtimeDataService.getProcessInstanceFullHistory(processInstance.getId(), new org.kie.api.runtime.query.QueryContext());
        // assert if logs are ordered by log.date DESC, log.id DESC
        Iterator<NodeInstanceDesc> fullIterator = fullNodeInstanceLogs.iterator();
        NodeInstanceDesc fullLog0 = fullIterator.next();
        Assert.assertEquals("Start", fullLog0.getName());
        Assert.assertEquals(true, fullLog0.isCompleted());
        NodeInstanceDesc fullLog1 = fullIterator.next();
        Assert.assertEquals("Hello", fullLog1.getName());
        Assert.assertEquals(true, fullLog1.isCompleted());
        NodeInstanceDesc fullLog2 = fullIterator.next();
        Assert.assertEquals("End", fullLog2.getName());
        Assert.assertEquals(true, fullLog2.isCompleted());
        NodeInstanceDesc fullLog3 = fullIterator.next();
        Assert.assertEquals("End", fullLog3.getName());
        Assert.assertEquals(false, fullLog3.isCompleted());
        NodeInstanceDesc fullLog4 = fullIterator.next();
        Assert.assertEquals("Hello", fullLog4.getName());
        Assert.assertEquals(false, fullLog4.isCompleted());
        NodeInstanceDesc fullLog5 = fullIterator.next();
        Assert.assertEquals("Start", fullLog5.getName());
        Assert.assertEquals(false, fullLog5.isCompleted());
        manager.close();
    }
}
