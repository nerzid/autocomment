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


package org.jbpm.executor;

import org.jbpm.executor.impl.event.ExecutorEventSupport;
import org.jbpm.executor.impl.AvailableJobsExecutor;
import org.slf4j.LoggerFactory;
import org.jbpm.executor.impl.ClassCacheManager;
import javax.persistence.EntityManagerFactory;
import org.kie.api.executor.Executor;
import org.jbpm.executor.impl.ExecutorServiceImpl;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import javax.naming.NamingException;
import org.kie.api.executor.ExecutorAdminService;
import org.jbpm.executor.impl.ExecutorImpl;
import org.jbpm.executor.impl.jpa.JPAExecutorStoreService;
import org.jbpm.executor.impl.mem.InMemoryExecutorAdminServiceImpl;
import org.jbpm.executor.impl.mem.InMemoryExecutorQueryServiceImpl;
import org.kie.api.executor.ExecutorQueryService;
import org.slf4j.Logger;
import org.kie.api.executor.ExecutorStoreService;
import org.jbpm.executor.impl.jpa.ExecutorQueryServiceImpl;
import org.kie.api.executor.ExecutorService;
import javax.naming.InitialContext;
import org.jbpm.executor.impl.jpa.ExecutorRequestAdminServiceImpl;
import org.jbpm.executor.impl.ExecutorRunnable;
import org.jbpm.executor.impl.mem.InMemoryExecutorStoreService;

/**
 * Creates singleton instance of <code>ExecutorService</code> that shall be used outside of CDI
 * environment.
 */
public class ExecutorServiceFactory {
    private static final String mode = System.getProperty("org.jbpm.cdi.executor.mode", "singleton");

    private static final Logger logger = LoggerFactory.getLogger(ExecutorServiceFactory.class);

    private static ExecutorService serviceInstance;

    public static synchronized ExecutorService newExecutorService(EntityManagerFactory emf) {
        if (ExecutorServiceFactory.mode.equalsIgnoreCase("singleton")) {
            if ((ExecutorServiceFactory.serviceInstance) == null) {
                ExecutorServiceFactory.serviceInstance = ExecutorServiceFactory.configure(emf);
            }
            return ExecutorServiceFactory.serviceInstance;
        }else {
            return ExecutorServiceFactory.configure(emf);
        }
    }

    public static synchronized ExecutorService newExecutorService() {
        if (ExecutorServiceFactory.mode.equalsIgnoreCase("singleton")) {
            if ((ExecutorServiceFactory.serviceInstance) == null) {
                ExecutorServiceFactory.serviceInstance = ExecutorServiceFactory.configure();
            }
            return ExecutorServiceFactory.serviceInstance;
        }else {
            return ExecutorServiceFactory.configure();
        }
    }

    public static synchronized void resetExecutorService(ExecutorService executorService) {
        if (executorService.equals(ExecutorServiceFactory.serviceInstance)) {
            ExecutorServiceFactory.serviceInstance = null;
        }
    }

    private static ExecutorService configure(EntityManagerFactory emf) {
        ExecutorEventSupport eventSupport = new ExecutorEventSupport();
        // create instances of executor services
        ExecutorQueryService queryService = new ExecutorQueryServiceImpl(true);
        Executor executor = new ExecutorImpl();
        ExecutorAdminService adminService = new ExecutorRequestAdminServiceImpl();
        // create executor for persistence handling
        TransactionalCommandService commandService = new TransactionalCommandService(emf);
        ExecutorStoreService storeService = new JPAExecutorStoreService(true);
        // set command TransactionalCommandService{commandService} to ExecutorStoreService{((JPAExecutorStoreService) (storeService))}
        ((JPAExecutorStoreService) (storeService)).setCommandService(commandService);
        // set emf EntityManagerFactory{emf} to ExecutorStoreService{((JPAExecutorStoreService) (storeService))}
        ((JPAExecutorStoreService) (storeService)).setEmf(emf);
        // set event ExecutorEventSupport{eventSupport} to ExecutorStoreService{((JPAExecutorStoreService) (storeService))}
        ((JPAExecutorStoreService) (storeService)).setEventSupport(eventSupport);
        // set executor ExecutorStoreService{storeService} to Executor{((ExecutorImpl) (executor))}
        ((ExecutorImpl) (executor)).setExecutorStoreService(storeService);
        // set event ExecutorEventSupport{eventSupport} to Executor{((ExecutorImpl) (executor))}
        ((ExecutorImpl) (executor)).setEventSupport(eventSupport);
        // set executor on all instances that requires it
        // set command TransactionalCommandService{commandService} to ExecutorQueryService{((ExecutorQueryServiceImpl) (queryService))}
        ((ExecutorQueryServiceImpl) (queryService)).setCommandService(commandService);
        // set command TransactionalCommandService{commandService} to ExecutorAdminService{((ExecutorRequestAdminServiceImpl) (adminService))}
        ((ExecutorRequestAdminServiceImpl) (adminService)).setCommandService(commandService);
        // configure services
        ExecutorService service = new ExecutorServiceImpl(executor);
        // set query ExecutorQueryService{queryService} to ExecutorService{((ExecutorServiceImpl) (service))}
        ((ExecutorServiceImpl) (service)).setQueryService(queryService);
        // set executor Executor{executor} to ExecutorService{((ExecutorServiceImpl) (service))}
        ((ExecutorServiceImpl) (service)).setExecutor(executor);
        // set admin ExecutorAdminService{adminService} to ExecutorService{((ExecutorServiceImpl) (service))}
        ((ExecutorServiceImpl) (service)).setAdminService(adminService);
        // set event ExecutorEventSupport{eventSupport} to ExecutorService{((ExecutorServiceImpl) (service))}
        ((ExecutorServiceImpl) (service)).setEventSupport(eventSupport);
        return service;
    }

    private static ExecutorService configure() {
        // create instances of executor services
        ExecutorEventSupport eventSupport = new ExecutorEventSupport();
        ExecutorQueryService queryService = new InMemoryExecutorQueryServiceImpl(true);
        Executor executor = new ExecutorImpl();
        ExecutorAdminService adminService = new InMemoryExecutorAdminServiceImpl(true);
        InMemoryExecutorStoreService storeService = new InMemoryExecutorStoreService(true);
        // set event ExecutorEventSupport{eventSupport} to InMemoryExecutorStoreService{((InMemoryExecutorStoreService) (storeService))}
        ((InMemoryExecutorStoreService) (storeService)).setEventSupport(eventSupport);
        // set executor InMemoryExecutorStoreService{storeService} to Executor{((ExecutorImpl) (executor))}
        ((ExecutorImpl) (executor)).setExecutorStoreService(storeService);
        // set event ExecutorEventSupport{eventSupport} to Executor{((ExecutorImpl) (executor))}
        ((ExecutorImpl) (executor)).setEventSupport(eventSupport);
        // set executor on all instances that requires it
        // set store InMemoryExecutorStoreService{storeService} to ExecutorQueryService{((InMemoryExecutorQueryServiceImpl) (queryService))}
        ((InMemoryExecutorQueryServiceImpl) (queryService)).setStoreService(storeService);
        // set store InMemoryExecutorStoreService{storeService} to ExecutorAdminService{((InMemoryExecutorAdminServiceImpl) (adminService))}
        ((InMemoryExecutorAdminServiceImpl) (adminService)).setStoreService(storeService);
        // configure services
        ExecutorService service = new ExecutorServiceImpl(executor);
        // set query ExecutorQueryService{queryService} to ExecutorService{((ExecutorServiceImpl) (service))}
        ((ExecutorServiceImpl) (service)).setQueryService(queryService);
        // set executor Executor{executor} to ExecutorService{((ExecutorServiceImpl) (service))}
        ((ExecutorServiceImpl) (service)).setExecutor(executor);
        // set admin ExecutorAdminService{adminService} to ExecutorService{((ExecutorServiceImpl) (service))}
        ((ExecutorServiceImpl) (service)).setAdminService(adminService);
        // set event ExecutorEventSupport{eventSupport} to ExecutorService{((ExecutorServiceImpl) (service))}
        ((ExecutorServiceImpl) (service)).setEventSupport(eventSupport);
        return service;
    }

    public static ExecutorRunnable buildRunable(EntityManagerFactory emf, ExecutorEventSupport eventSupport) {
        ExecutorRunnable runnable = new ExecutorRunnable();
        AvailableJobsExecutor jobExecutor = null;
        try {
            jobExecutor = InitialContext.doLookup("java:module/AvailableJobsExecutor");
        } catch (Exception e) {
            jobExecutor = ExecutorServiceFactory.buildJobExecutor(emf, eventSupport);
        }
        // set available AvailableJobsExecutor{jobExecutor} to ExecutorRunnable{runnable}
        runnable.setAvailableJobsExecutor(jobExecutor);
        return runnable;
    }

    private static AvailableJobsExecutor buildJobExecutor(EntityManagerFactory emf, ExecutorEventSupport eventSupport) {
        AvailableJobsExecutor jobExecutor;
        jobExecutor = new AvailableJobsExecutor();
        ClassCacheManager classCacheManager = new ClassCacheManager();
        ExecutorQueryService queryService = new ExecutorQueryServiceImpl(true);
        TransactionalCommandService cmdService = new TransactionalCommandService(emf);
        ExecutorStoreService storeService = new JPAExecutorStoreService(true);
        // set command TransactionalCommandService{cmdService} to ExecutorStoreService{((JPAExecutorStoreService) (storeService))}
        ((JPAExecutorStoreService) (storeService)).setCommandService(cmdService);
        // set emf EntityManagerFactory{emf} to ExecutorStoreService{((JPAExecutorStoreService) (storeService))}
        ((JPAExecutorStoreService) (storeService)).setEmf(emf);
        // set command TransactionalCommandService{cmdService} to ExecutorQueryService{((ExecutorQueryServiceImpl) (queryService))}
        ((ExecutorQueryServiceImpl) (queryService)).setCommandService(cmdService);
        // set class ClassCacheManager{classCacheManager} to AvailableJobsExecutor{jobExecutor}
        jobExecutor.setClassCacheManager(classCacheManager);
        // set query ExecutorQueryService{queryService} to AvailableJobsExecutor{jobExecutor}
        jobExecutor.setQueryService(queryService);
        // set executor ExecutorStoreService{storeService} to AvailableJobsExecutor{jobExecutor}
        jobExecutor.setExecutorStoreService(storeService);
        // set event ExecutorEventSupport{eventSupport} to AvailableJobsExecutor{jobExecutor}
        jobExecutor.setEventSupport(eventSupport);
        // provide bean manager instance as context data as it might not be available to
        // be looked up from JNDI in non managed threads
        try {
            Object beanManager = InitialContext.doLookup("java:comp/BeanManager");
            jobExecutor.addContextData("BeanManager", beanManager);
        } catch (NamingException ex) {
            ExecutorServiceFactory.logger.debug("CDI beans cannot be used in executor commands, because no CDI manager has been found in JNDI.");
        }
        return jobExecutor;
    }

    public static ExecutorRunnable buildRunable(ExecutorEventSupport eventSupport) {
        ExecutorRunnable runnable = new ExecutorRunnable();
        AvailableJobsExecutor jobExecutor = null;
        try {
            jobExecutor = InitialContext.doLookup("java:module/AvailableJobsExecutor");
        } catch (Exception e) {
            jobExecutor = new AvailableJobsExecutor();
            ClassCacheManager classCacheManager = new ClassCacheManager();
            InMemoryExecutorStoreService storeService = new InMemoryExecutorStoreService(true);
            InMemoryExecutorQueryServiceImpl queryService = new InMemoryExecutorQueryServiceImpl(true);
            queryService.setStoreService(storeService);
            jobExecutor.setClassCacheManager(classCacheManager);
            jobExecutor.setQueryService(queryService);
            jobExecutor.setExecutorStoreService(storeService);
            jobExecutor.setEventSupport(eventSupport);
            // provide bean manager instance as context data as it might not be available to
            // be looked up from JNDI in non managed threads
            try {
                Object beanManager = InitialContext.doLookup("java:comp/BeanManager");
                jobExecutor.addContextData("BeanManager", beanManager);
            } catch (NamingException ex) {
                ExecutorServiceFactory.logger.debug("CDI beans cannot be used in executor commands, because no CDI manager has been found in JNDI.");
            }
        }
        // set available AvailableJobsExecutor{jobExecutor} to ExecutorRunnable{runnable}
        runnable.setAvailableJobsExecutor(jobExecutor);
        return runnable;
    }
}

