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

import org.jbpm.executor.impl.AvailableJobsExecutor;
import org.jbpm.executor.impl.ClassCacheManager;
import javax.persistence.EntityManagerFactory;
import org.kie.api.executor.Executor;
import org.kie.api.executor.ExecutorAdminService;
import org.jbpm.executor.impl.event.ExecutorEventSupport;
import org.jbpm.executor.impl.ExecutorImpl;
import org.kie.api.executor.ExecutorQueryService;
import org.jbpm.executor.impl.jpa.ExecutorQueryServiceImpl;
import org.jbpm.executor.impl.jpa.ExecutorRequestAdminServiceImpl;
import org.jbpm.executor.impl.ExecutorRunnable;
import org.kie.api.executor.ExecutorService;
import org.jbpm.executor.impl.ExecutorServiceImpl;
import org.kie.api.executor.ExecutorStoreService;
import org.jbpm.executor.impl.mem.InMemoryExecutorAdminServiceImpl;
import org.jbpm.executor.impl.mem.InMemoryExecutorQueryServiceImpl;
import org.jbpm.executor.impl.mem.InMemoryExecutorStoreService;
import javax.naming.InitialContext;
import org.jbpm.executor.impl.jpa.JPAExecutorStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.naming.NamingException;
import org.jbpm.shared.services.impl.TransactionalCommandService;

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
        } else {
            return ExecutorServiceFactory.configure(emf);
        }
    }

    public static synchronized ExecutorService newExecutorService() {
        if (ExecutorServiceFactory.mode.equalsIgnoreCase("singleton")) {
            if ((ExecutorServiceFactory.serviceInstance) == null) {
                ExecutorServiceFactory.serviceInstance = ExecutorServiceFactory.configure();
            } 
            return ExecutorServiceFactory.serviceInstance;
        } else {
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
        ((JPAExecutorStoreService) (storeService)).setCommandService(commandService);
        ((JPAExecutorStoreService) (storeService)).setEmf(emf);
        ((JPAExecutorStoreService) (storeService)).setEventSupport(eventSupport);
        ((ExecutorImpl) (executor)).setExecutorStoreService(storeService);
        ((ExecutorImpl) (executor)).setEventSupport(eventSupport);
        // set executor on all instances that requires it
        ((ExecutorQueryServiceImpl) (queryService)).setCommandService(commandService);
        ((ExecutorRequestAdminServiceImpl) (adminService)).setCommandService(commandService);
        // configure services
        ExecutorService service = new ExecutorServiceImpl(executor);
        ((ExecutorServiceImpl) (service)).setQueryService(queryService);
        ((ExecutorServiceImpl) (service)).setExecutor(executor);
        ((ExecutorServiceImpl) (service)).setAdminService(adminService);
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
        ((InMemoryExecutorStoreService) (storeService)).setEventSupport(eventSupport);
        ((ExecutorImpl) (executor)).setExecutorStoreService(storeService);
        ((ExecutorImpl) (executor)).setEventSupport(eventSupport);
        // set executor on all instances that requires it
        ((InMemoryExecutorQueryServiceImpl) (queryService)).setStoreService(storeService);
        ((InMemoryExecutorAdminServiceImpl) (adminService)).setStoreService(storeService);
        // configure services
        ExecutorService service = new ExecutorServiceImpl(executor);
        ((ExecutorServiceImpl) (service)).setQueryService(queryService);
        ((ExecutorServiceImpl) (service)).setExecutor(executor);
        ((ExecutorServiceImpl) (service)).setAdminService(adminService);
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
        ((JPAExecutorStoreService) (storeService)).setCommandService(cmdService);
        ((JPAExecutorStoreService) (storeService)).setEmf(emf);
        ((ExecutorQueryServiceImpl) (queryService)).setCommandService(cmdService);
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
        runnable.setAvailableJobsExecutor(jobExecutor);
        return runnable;
    }
}

