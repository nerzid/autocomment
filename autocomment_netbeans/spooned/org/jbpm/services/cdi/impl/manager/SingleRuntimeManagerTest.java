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


package org.jbpm.services.cdi.impl.manager;

import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.junit.After;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.arquillian.junit.Arquillian;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.jboss.arquillian.container.test.api.Deployment;
import org.kie.internal.runtime.manager.context.EmptyContext;
import javax.inject.Inject;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.kie.api.runtime.KieSession;
import java.util.List;
import org.kie.internal.runtime.manager.cdi.qualifier.PerProcessInstance;
import org.kie.internal.runtime.manager.cdi.qualifier.PerRequest;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import java.util.Properties;
import org.junit.runner.RunWith;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.kie.internal.runtime.manager.cdi.qualifier.Singleton;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.junit.Test;
import org.jbpm.runtime.manager.util.TestUtil;

@RunWith(value = Arquillian.class)
public class SingleRuntimeManagerTest extends AbstractKieServicesBaseTest {
    @Deployment
    public static Archive<?> createDeployment() {
        return // Identity Provider Test Impl here
        // deadlines
        // This should not be required here
        // work items org.jbpm.services.task.wih
        ShrinkWrap.create(JavaArchive.class, "jbpm-runtime-manager.jar").addPackage("org.jbpm.services.task").addPackage("org.jbpm.services.task.wih").addPackage("org.jbpm.services.task.annotations").addPackage("org.jbpm.services.task.api").addPackage("org.jbpm.services.task.impl").addPackage("org.jbpm.services.task.events").addPackage("org.jbpm.services.task.exception").addPackage("org.jbpm.services.task.identity").addPackage("org.jbpm.services.task.factories").addPackage("org.jbpm.services.task.internals").addPackage("org.jbpm.services.task.internals.lifecycle").addPackage("org.jbpm.services.task.lifecycle.listeners").addPackage("org.jbpm.services.task.query").addPackage("org.jbpm.services.task.util").addPackage("org.jbpm.services.task.commands").addPackage("org.jbpm.services.task.deadlines").addPackage("org.jbpm.services.task.deadlines.notifications.impl").addPackage("org.jbpm.services.task.subtask").addPackage("org.jbpm.services.task.rule").addPackage("org.jbpm.services.task.rule.impl").addPackage("org.jbpm.services.task.audit.service").addPackage("org.kie.internal.runtime.manager").addPackage("org.kie.internal.runtime.manager.context").addPackage("org.kie.internal.runtime.manager.cdi.qualifier").addPackage("org.jbpm.runtime.manager.impl").addPackage("org.jbpm.runtime.manager.impl.cdi").addPackage("org.jbpm.runtime.manager.impl.factory").addPackage("org.jbpm.runtime.manager.impl.jpa").addPackage("org.jbpm.runtime.manager.impl.manager").addPackage("org.jbpm.runtime.manager.impl.task").addPackage("org.jbpm.runtime.manager.impl.tx").addPackage("org.jbpm.shared.services.api").addPackage("org.jbpm.shared.services.impl").addPackage("org.jbpm.shared.services.impl.tx").addPackage("org.jbpm.kie.services.api").addPackage("org.jbpm.kie.services.impl").addPackage("org.jbpm.kie.services.api.bpmn2").addPackage("org.jbpm.kie.services.impl.bpmn2").addPackage("org.jbpm.kie.services.impl.event.listeners").addPackage("org.jbpm.kie.services.impl.audit").addPackage("org.jbpm.kie.services.impl.form").addPackage("org.jbpm.kie.services.impl.form.provider").addPackage("org.jbpm.kie.services.impl.query").addPackage("org.jbpm.kie.services.impl.query.mapper").addPackage("org.jbpm.kie.services.impl.query.persistence").addPackage("org.jbpm.kie.services.impl.query.preprocessor").addPackage("org.jbpm.services.cdi").addPackage("org.jbpm.services.cdi.impl").addPackage("org.jbpm.services.cdi.impl.form").addPackage("org.jbpm.services.cdi.impl.manager").addPackage("org.jbpm.services.cdi.producer").addPackage("org.jbpm.services.cdi.impl.security").addPackage("org.jbpm.services.cdi.impl.query").addPackage("org.jbpm.kie.services.test").addPackage("org.jbpm.services.cdi.test").addClass("org.jbpm.services.cdi.test.util.CDITestHelperNoTaskService").addClass("org.jbpm.services.cdi.test.util.CountDownDeploymentListenerCDIImpl").addClass("org.jbpm.kie.services.test.objects.CoundDownDeploymentListener").addAsResource("jndi.properties", "jndi.properties").addAsManifestResource("META-INF/persistence.xml", ArchivePaths.create("persistence.xml")).addAsManifestResource("META-INF/Taskorm.xml", ArchivePaths.create("Taskorm.xml")).addAsManifestResource("META-INF/beans.xml", ArchivePaths.create("beans.xml"));
    }

    @BeforeClass
    public static void setup() {
        TestUtil.cleanupSingletonSessionId();
        Properties props = new Properties();
        props.setProperty("john", "user");
    }

    @After
    public void close() {
        singletonManager.close();
        perRequestManager.close();
        perProcessInstanceManager.close();
    }

    @Override
    protected void configureServices() {
        // do nothing here and let CDI configure services
    }

    /* end of initialization code, tests start here */
    @Inject
    @Singleton
    private RuntimeManager singletonManager;

    @Inject
    @PerRequest
    private RuntimeManager perRequestManager;

    @Inject
    @PerProcessInstance
    private RuntimeManager perProcessInstanceManager;

    @Test
    public void testSingleSingletonManager() {
        Assert.assertNotNull(singletonManager);
        RuntimeEngine runtime = singletonManager.getRuntimeEngine(EmptyContext.get());
        Assert.assertNotNull(runtime);
        testProcessStartOnManager(runtime);
        singletonManager.disposeRuntimeEngine(runtime);
    }

    @Test
    public void testSinglePerRequestManager() {
        Assert.assertNotNull(perRequestManager);
        RuntimeEngine runtime = perRequestManager.getRuntimeEngine(EmptyContext.get());
        Assert.assertNotNull(runtime);
        testProcessStartOnManager(runtime);
        perRequestManager.disposeRuntimeEngine(runtime);
    }

    @Test
    public void testSinglePerProcessInstanceManager() {
        Assert.assertNotNull(perProcessInstanceManager);
        RuntimeEngine runtime = perProcessInstanceManager.getRuntimeEngine(ProcessInstanceIdContext.get());
        Assert.assertNotNull(runtime);
        testProcessStartOnManager(runtime);
        perProcessInstanceManager.disposeRuntimeEngine(runtime);
    }

    private void testProcessStartOnManager(RuntimeEngine runtime) {
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        ProcessInstance processInstance = ksession.startProcess("UserTask");
        Assert.assertNotNull(processInstance);
        List<Status> statuses = new ArrayList<Status>();
        statuses.add(Status.Reserved);
        List<TaskSummary> tasks = runtime.getTaskService().getTasksOwnedByStatus("john", statuses, "en-UK");
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());
        runtime.getTaskService().start(tasks.get(0).getId(), "john");
        runtime.getTaskService().complete(tasks.get(0).getId(), "john", null);
        processInstance = ksession.getProcessInstance(processInstance.getId());
        Assert.assertNull(processInstance);
    }
}

