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


package org.jbpm.services.cdi.test.util;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.BeanManager;
import org.jbpm.services.api.DeploymentService;
import javax.persistence.EntityManagerFactory;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import javax.inject.Inject;
import org.jbpm.services.cdi.impl.manager.InjectableRegisterableItemsFactory;
import org.jbpm.services.cdi.Kjar;
import org.kie.internal.runtime.manager.cdi.qualifier.PerProcessInstance;
import org.kie.internal.runtime.manager.cdi.qualifier.PerRequest;
import javax.annotation.PreDestroy;
import javax.enterprise.inject.Produces;
import java.util.Properties;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.cdi.qualifier.Singleton;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.api.task.UserGroupCallback;

@ApplicationScoped
public class CDITestHelperNoTaskService {
    @Inject
    private BeanManager beanManager;

    private EntityManagerFactory emf;

    @Inject
    @Kjar
    private DeploymentService deploymentService;

    @Produces
    @Singleton
    @PerRequest
    @PerProcessInstance
    public RuntimeEnvironment produceEnvironment(EntityManagerFactory emf) {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().entityManagerFactory(emf).userGroupCallback(getUserGroupCallback()).registerableItemsFactory(InjectableRegisterableItemsFactory.getFactory(beanManager, new org.jbpm.runtime.manager.impl.ManagedAuditEventBuilderImpl())).addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2).addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTask.bpmn2"), ResourceType.BPMN2).get();
        return environment;
    }

    @Produces
    public EntityManagerFactory produceEntityManagerFactory() {
        if ((CDITestHelperNoTaskService.this.emf) == null) {
            CDITestHelperNoTaskService.this.emf = EntityManagerFactoryManager.get().getOrCreate("org.jbpm.domain");
        } 
        return CDITestHelperNoTaskService.this.emf;
    }

    @PreDestroy
    public void doCleanUp() {
        EntityManagerFactoryManager.get().clear();
    }

    @Produces
    public UserGroupCallback getUserGroupCallback() {
        Properties properties = new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR,Accounting");
        properties.setProperty("salaboy", "HR,IT,Accounting");
        properties.setProperty("katy", "HR,IT,Accounting");
        return new org.jbpm.services.task.identity.JBossUserGroupCallbackImpl(properties);
    }

    @Produces
    public DeploymentService produceKjarDeployService() {
        return deploymentService;
    }

    @Produces
    public TaskLifeCycleEventListener produceTaskAuditLogger() {
        return new org.jbpm.services.task.audit.JPATaskLifeCycleEventListener(true);
    }
}

