/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.services.cdi.test.humantaskservice;

import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.kie.internal.command.Context;
import org.hamcrest.CoreMatchers;
import org.jbpm.services.cdi.test.humantaskservice.CustomHumanTaskServiceProducer.CustomTaskPersistenceContextManagerInUse;
import org.jboss.arquillian.container.test.api.Deployment;
import javax.inject.Inject;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.runner.RunWith;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jbpm.services.task.commands.TaskCommand;
import org.kie.api.task.TaskService;
import org.junit.Test;

/**
 * This test demonstrates subclassing the HumanTaskServiceProducer to inject
 * TaskPersistenceContextManager and TransactionManager instances into the environment.
 */
@RunWith(value = Arquillian.class)
public class CustomHumanTaskServiceTest extends AbstractKieServicesBaseTest {
    @Deployment
    public static Archive<?> createDeployment() {
        return // Identity Provider Test Impl here
        // deadlines
        // This should not be required here
        // work items org.jbpm.services.task.wih
        ShrinkWrap.create(JavaArchive.class, "jbpm-runtime-manager.jar").addPackage("org.jbpm.services.task").addPackage("org.jbpm.services.task.wih").addPackage("org.jbpm.services.task.annotations").addPackage("org.jbpm.services.task.api").addPackage("org.jbpm.services.task.impl").addPackage("org.jbpm.services.task.events").addPackage("org.jbpm.services.task.exception").addPackage("org.jbpm.services.task.identity").addPackage("org.jbpm.services.task.factories").addPackage("org.jbpm.services.task.internals").addPackage("org.jbpm.services.task.internals.lifecycle").addPackage("org.jbpm.services.task.lifecycle.listeners").addPackage("org.jbpm.services.task.query").addPackage("org.jbpm.services.task.util").addPackage("org.jbpm.services.task.commands").addPackage("org.jbpm.services.task.deadlines").addPackage("org.jbpm.services.task.deadlines.notifications.impl").addPackage("org.jbpm.services.task.subtask").addPackage("org.jbpm.services.task.rule").addPackage("org.jbpm.services.task.rule.impl").addPackage("org.jbpm.services.task.audit.service").addPackage("org.kie.internal.runtime.manager").addPackage("org.kie.internal.runtime.manager.context").addPackage("org.kie.internal.runtime.manager.cdi.qualifier").addPackage("org.jbpm.runtime.manager.impl").addPackage("org.jbpm.runtime.manager.impl.cdi").addPackage("org.jbpm.runtime.manager.impl.factory").addPackage("org.jbpm.runtime.manager.impl.jpa").addPackage("org.jbpm.runtime.manager.impl.manager").addPackage("org.jbpm.runtime.manager.impl.task").addPackage("org.jbpm.runtime.manager.impl.tx").addPackage("org.jbpm.shared.services.api").addPackage("org.jbpm.shared.services.impl").addPackage("org.jbpm.shared.services.impl.tx").addPackage("org.jbpm.kie.services.api").addPackage("org.jbpm.kie.services.impl").addPackage("org.jbpm.kie.services.api.bpmn2").addPackage("org.jbpm.kie.services.impl.bpmn2").addPackage("org.jbpm.kie.services.impl.event.listeners").addPackage("org.jbpm.kie.services.impl.audit").addPackage("org.jbpm.kie.services.impl.form").addPackage("org.jbpm.kie.services.impl.form.provider").addPackage("org.jbpm.kie.services.impl.query").addPackage("org.jbpm.kie.services.impl.query.mapper").addPackage("org.jbpm.kie.services.impl.query.persistence").addPackage("org.jbpm.kie.services.impl.query.preprocessor").addPackage("org.jbpm.services.cdi").addPackage("org.jbpm.services.cdi.impl").addPackage("org.jbpm.services.cdi.impl.form").addPackage("org.jbpm.services.cdi.impl.manager").addPackage("org.jbpm.services.cdi.producer").addPackage("org.jbpm.services.cdi.impl.security").addPackage("org.jbpm.services.cdi.impl.query").addPackage("org.jbpm.kie.services.test").addPackage("org.jbpm.services.cdi.test").addPackage("org.jbpm.services.cdi.test.humantaskservice").addClass("org.jbpm.services.cdi.test.util.CDITestHelperNoTaskService").addClass("org.jbpm.services.cdi.test.util.CountDownDeploymentListenerCDIImpl").addClass("org.jbpm.kie.services.test.objects.CoundDownDeploymentListener").addAsResource("jndi.properties", "jndi.properties").addAsManifestResource("META-INF/persistence.xml", ArchivePaths.create("persistence.xml")).addAsManifestResource("META-INF/Taskorm.xml", ArchivePaths.create("Taskorm.xml")).addAsManifestResource("META-INF/beans.xml", ArchivePaths.create("beans.xml"));
    }

    @Override
    protected void configureServices() {
        // do nothing here and let CDI configure services
    }

    /* end of initialization code, tests start here */
    @Inject
    private TaskService defaultTaskService;

    @Inject
    @CustomHumanTaskService
    private TaskService customTaskService;

    /**
     * TestCommand should execute normally through the TaskService configured by the
     * default HumanTaskServiceProducer.
     * 
     * @throws Exception
     */
    @Test
    public void testDefaultTaskService() throws Exception {
        String result = defaultTaskService.execute(new CustomHumanTaskServiceTest.TestCommand());
        Assert.assertThat(result, CoreMatchers.is("Command Executed"));
    }

    /**
     * The CustomHumanTaskService is configured with a TaskPersistenceContextManager
     * implementation that throws an exception to prove that the custom environment
     * is in use.
     * 
     * @throws Exception
     */
    @Test(expected = CustomTaskPersistenceContextManagerInUse.class)
    public void testCustomTaskService() throws Exception {
        customTaskService.execute(new CustomHumanTaskServiceTest.TestCommand());
    }

    @SuppressWarnings(value = "serial")
    private static final class TestCommand extends TaskCommand<String> {
        @Override
        public String execute(Context context) {
            return "Command Executed";
        }
    }
}

