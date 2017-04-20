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


package org.jbpm.executor.cdi.impl.jpa;

import org.kie.internal.runtime.cdi.Activate;
import javax.persistence.EntityManagerFactory;
import org.kie.api.executor.ExecutorAdminService;
import javax.enterprise.inject.Produces;
import org.kie.api.executor.ExecutorQueryService;
import org.jbpm.executor.impl.jpa.ExecutorQueryServiceImpl;
import org.jbpm.executor.impl.jpa.ExecutorRequestAdminServiceImpl;
import org.kie.api.executor.ExecutorService;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.jbpm.executor.ExecutorServiceFactory;
import org.kie.api.executor.ExecutorStoreService;
import javax.persistence.PersistenceUnit;
import javax.inject.Inject;
import org.jbpm.executor.impl.jpa.JPAExecutorStoreService;

/**
 * IMPORTANT: please keep all classes from package org.jbpm.shared.services.impl as FQCN
 * inside method body to avoid exception logged by CDI when used with in memory mode
 */
@Activate(whenAvailable = "org.jbpm.runtime.manager.impl.RuntimeManagerFactoryImpl")
public class JPAExecutorServiceProducer {
    @Inject
    @PersistenceUnit(unitName = "org.jbpm.domain")
    private EntityManagerFactory emf;

    @Produces
    public ExecutorService produceExecutorService() {
        ExecutorService service = ExecutorServiceFactory.newExecutorService(emf);
        return service;
    }

    @Produces
    public ExecutorStoreService produceStoreService() {
        ExecutorStoreService storeService = new JPAExecutorStoreService(true);
        TransactionalCommandService commandService = new TransactionalCommandService(emf);
        // set command TransactionalCommandService{commandService} to ExecutorStoreService{((JPAExecutorStoreService) (storeService))}
        ((JPAExecutorStoreService) (storeService)).setCommandService(commandService);
        // set emf EntityManagerFactory{emf} to ExecutorStoreService{((JPAExecutorStoreService) (storeService))}
        ((JPAExecutorStoreService) (storeService)).setEmf(emf);
        return storeService;
    }

    @Produces
    public ExecutorAdminService produceAdminService() {
        ExecutorAdminService adminService = new ExecutorRequestAdminServiceImpl();
        TransactionalCommandService commandService = new TransactionalCommandService(emf);
        // set command TransactionalCommandService{commandService} to ExecutorAdminService{((ExecutorRequestAdminServiceImpl) (adminService))}
        ((ExecutorRequestAdminServiceImpl) (adminService)).setCommandService(commandService);
        return adminService;
    }

    @Produces
    public ExecutorQueryService produceQueryService() {
        ExecutorQueryService queryService = new ExecutorQueryServiceImpl(true);
        TransactionalCommandService commandService = new TransactionalCommandService(emf);
        // set command TransactionalCommandService{commandService} to ExecutorQueryService{((ExecutorQueryServiceImpl) (queryService))}
        ((ExecutorQueryServiceImpl) (queryService)).setCommandService(commandService);
        return queryService;
    }
}

