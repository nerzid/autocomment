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


package org.jbpm.runtime.manager.impl;

import org.kie.api.event.rule.AgendaEventListener;
import org.kie.internal.runtime.manager.CacheManager;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.manager.Disposable;
import org.kie.internal.runtime.manager.DisposeListener;
import org.kie.api.runtime.Environment;
import org.kie.internal.task.api.EventService;
import org.jbpm.services.task.wih.ExternalTaskEventListener;
import org.jbpm.process.core.timer.GlobalSchedulerService;
import org.jbpm.process.core.timer.impl.GlobalTimerService;
import org.kie.internal.runtime.manager.InternalRegisterableItemsFactory;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.api.runtime.KieContainer;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.drools.persistence.OrderedTransactionSynchronization;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import java.util.concurrent.locks.ReentrantLock;
import org.kie.api.runtime.manager.RegisterableItemsFactory;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.jbpm.runtime.manager.api.SchedulerProvider;
import org.kie.internal.runtime.manager.SessionNotFoundException;
import org.jbpm.services.task.impl.TaskContentRegistry;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.drools.core.time.TimerService;
import org.jbpm.process.core.timer.TimerServiceRegistry;
import org.drools.persistence.TransactionManager;
import org.drools.persistence.TransactionManagerFactory;
import org.drools.persistence.TransactionManagerHelper;
import org.drools.persistence.TransactionSynchronization;
import org.kie.api.runtime.process.WorkItemHandler;

/**
 * Common implementation that all <code>RuntimeManager</code> implementations should inherit from.
 * Provides the following capabilities:
 * <ul>
 *  <li>keeps track of all active managers by their identifier and prevents multiple managers from having the same id</li>
 *  <li>provides a common close operation</li>
 *  <li>injects the RuntimeManager into the ksession's environment for further reference</li>
 *  <li>registers dispose callbacks (via transaction synchronization)
 *  to dispose of the runtime engine automatically on transaction completion</li>
 *  <li>registers all defined items (work item handlers, event listeners)</li>
 * </ul>
 * Additionally, this provides a abstract <code>init</code> method that will be called on RuntimeManager instantiation.
 */
public abstract class AbstractRuntimeManager implements InternalRuntimeManager {
    private static final Logger logger = LoggerFactory.getLogger(AbstractRuntimeManager.class);

    protected RuntimeManagerRegistry registry = RuntimeManagerRegistry.get();

    protected RuntimeEnvironment environment;

    protected DeploymentDescriptor deploymentDescriptor;

    protected KieContainer kieContainer;

    protected CacheManager cacheManager = new CacheManagerImpl();

    protected boolean engineInitEager = Boolean.parseBoolean(System.getProperty("org.jbpm.rm.engine.eager", "false"));

    protected String identifier;

    protected boolean closed = false;

    protected SecurityManager securityManager = null;

    protected ConcurrentMap<Long, ReentrantLock> engineLocks = new ConcurrentHashMap<Long, ReentrantLock>();

    public AbstractRuntimeManager(RuntimeEnvironment environment, String identifier) {
        AbstractRuntimeManager.this.environment = environment;
        AbstractRuntimeManager.this.identifier = identifier;
        if (registry.isRegistered(identifier)) {
            throw new IllegalStateException((("RuntimeManager with id " + identifier) + " is already active"));
        } 
        internalSetDeploymentDescriptor();
        internalSetKieContainer();
        ((InternalRegisterableItemsFactory) (environment.getRegisterableItemsFactory())).setRuntimeManager(AbstractRuntimeManager.this);
        String eagerInit = ((String) (((SimpleRuntimeEnvironment) (environment)).getEnvironmentTemplate().get("RuntimeEngineEagerInit")));
        if (eagerInit != null) {
            engineInitEager = Boolean.parseBoolean(eagerInit);
        } 
    }

    private void internalSetDeploymentDescriptor() {
        AbstractRuntimeManager.this.deploymentDescriptor = ((DeploymentDescriptor) (((SimpleRuntimeEnvironment) (environment)).getEnvironmentTemplate().get("KieDeploymentDescriptor")));
        if ((AbstractRuntimeManager.this.deploymentDescriptor) == null) {
            AbstractRuntimeManager.this.deploymentDescriptor = new org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorManager().getDefaultDescriptor();
        } 
    }

    private void internalSetKieContainer() {
        AbstractRuntimeManager.this.kieContainer = ((KieContainer) (((SimpleRuntimeEnvironment) (environment)).getEnvironmentTemplate().get("KieContainer")));
    }

    public abstract void init();

    protected void registerItems(RuntimeEngine runtime) {
        RegisterableItemsFactory factory = environment.getRegisterableItemsFactory();
        // process handlers
        Map<String, WorkItemHandler> handlers = factory.getWorkItemHandlers(runtime);
        for (Map.Entry<String, WorkItemHandler> entry : handlers.entrySet()) {
            runtime.getKieSession().getWorkItemManager().registerWorkItemHandler(entry.getKey(), entry.getValue());
        }
        // register globals
        Map<String, Object> globals = factory.getGlobals(runtime);
        for (Map.Entry<String, Object> entry : globals.entrySet()) {
            runtime.getKieSession().setGlobal(entry.getKey(), entry.getValue());
        }
        // process listeners
        List<ProcessEventListener> processListeners = factory.getProcessEventListeners(runtime);
        for (ProcessEventListener listener : processListeners) {
            runtime.getKieSession().addEventListener(listener);
        }
        // agenda listeners
        List<AgendaEventListener> agendaListeners = factory.getAgendaEventListeners(runtime);
        for (AgendaEventListener listener : agendaListeners) {
            runtime.getKieSession().addEventListener(listener);
        }
        // working memory listeners
        List<RuleRuntimeEventListener> wmListeners = factory.getRuleRuntimeEventListeners(runtime);
        for (RuleRuntimeEventListener listener : wmListeners) {
            runtime.getKieSession().addEventListener(listener);
        }
    }

    protected void registerDisposeCallback(RuntimeEngine runtime, TransactionSynchronization sync) {
        if (hasEnvironmentEntry("IS_JTA_TRANSACTION", false)) {
            return ;
        } 
        // register it if there is an active transaction as we assume then to be running in a managed environment e.g CMT
        TransactionManager tm = getTransactionManager(runtime.getKieSession().getEnvironment());
        if ((((tm.getStatus()) != (TransactionManager.STATUS_NO_TRANSACTION)) && ((tm.getStatus()) != (TransactionManager.STATUS_ROLLEDBACK))) && ((tm.getStatus()) != (TransactionManager.STATUS_COMMITTED))) {
            TransactionManagerHelper.registerTransactionSyncInContainer(tm, ((OrderedTransactionSynchronization) (sync)));
        } 
    }

    protected boolean canDispose(RuntimeEngine runtime) {
        // avoid duplicated dispose
        if (((RuntimeEngineImpl) (runtime)).isDisposed()) {
            return false;
        } 
        // if this method was called as part of afterCompletion or is no JTA at all, allow to dispose
        if ((((RuntimeEngineImpl) (runtime)).isAfterCompletion()) || (hasEnvironmentEntry("IS_JTA_TRANSACTION", false))) {
            return true;
        } 
        try {
            // check tx status to disallow dispose when within active transaction
            TransactionManager tm = getTransactionManager(runtime.getKieSession().getEnvironment());
            if ((((tm.getStatus()) != (TransactionManager.STATUS_NO_TRANSACTION)) && ((tm.getStatus()) != (TransactionManager.STATUS_ROLLEDBACK))) && ((tm.getStatus()) != (TransactionManager.STATUS_COMMITTED))) {
                return false;
            } 
        } catch (SessionNotFoundException e) {
            // ignore it as it might be thrown for per process instance
        }
        return true;
    }

    protected void attachManager(RuntimeEngine runtime) {
        runtime.getKieSession().getEnvironment().set(EnvironmentName.RUNTIME_MANAGER, AbstractRuntimeManager.this);
        runtime.getKieSession().getEnvironment().set(EnvironmentName.DEPLOYMENT_ID, AbstractRuntimeManager.this.getIdentifier());
    }

    @Override
    public boolean isClosed() {
        return AbstractRuntimeManager.this.closed;
    }

    @Override
    public void close() {
        close(false);
    }

    public void close(boolean removeJobs) {
        cacheManager.dispose();
        environment.close();
        registry.remove(identifier);
        TimerService timerService = TimerServiceRegistry.getInstance().remove(((getIdentifier()) + (TimerServiceRegistry.TIMER_SERVICE_SUFFIX)));
        if (timerService != null) {
            if (removeJobs && (timerService instanceof GlobalTimerService)) {
                ((GlobalTimerService) (timerService)).destroy();
            } 
            timerService.shutdown();
            GlobalSchedulerService schedulerService = ((SchedulerProvider) (environment)).getSchedulerService();
            if (schedulerService != null) {
                schedulerService.shutdown();
            } 
        } 
        AbstractRuntimeManager.this.closed = true;
    }

    public RuntimeEnvironment getEnvironment() {
        return ((org.kie.internal.runtime.manager.RuntimeEnvironment) (environment));
    }

    public void setEnvironment(RuntimeEnvironment environment) {
        AbstractRuntimeManager.this.environment = environment;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        AbstractRuntimeManager.this.identifier = identifier;
    }

    @SuppressWarnings(value = { "unchecked" , "rawtypes" })
    protected void configureRuntimeOnTaskService(InternalTaskService internalTaskService, RuntimeEngine engine) {
        if (internalTaskService != null) {
            ExternalTaskEventListener listener = new ExternalTaskEventListener();
            if (internalTaskService instanceof EventService) {
                ((EventService) (internalTaskService)).registerTaskEventListener(listener);
            } 
            // register task listeners if any
            RegisterableItemsFactory factory = environment.getRegisterableItemsFactory();
            for (TaskLifeCycleEventListener taskListener : factory.getTaskListeners()) {
                ((EventService<TaskLifeCycleEventListener>) (internalTaskService)).registerTaskEventListener(taskListener);
            }
            if ((engine != null) && (engine instanceof Disposable)) {
                ((Disposable) (engine)).addDisposeListener(new DisposeListener() {
                    @Override
                    public void onDispose(RuntimeEngine runtime) {
                        if ((runtime.getTaskService()) instanceof EventService) {
                            ((EventService) (runtime.getTaskService())).clearTaskEventListeners();
                        } 
                    }
                });
            } 
        } 
    }

    protected void removeRuntimeFromTaskService() {
        TaskContentRegistry.get().removeMarshallerContext(getIdentifier());
    }

    /**
     * Soft dispose means it will be invoked as sort of preparation step before actual dispose.
     * Mainly used with transaction synchronization to be invoked as part of beforeCompletion
     * to clean up any thread state - like thread local settings as afterCompletion can be invoked from another thread
     */
    public void softDispose(RuntimeEngine runtimeEngine) {
    }

    protected boolean canDestroy(RuntimeEngine runtime) {
        if ((hasEnvironmentEntry("IS_JTA_TRANSACTION", false)) || (((RuntimeEngineImpl) (runtime)).isAfterCompletion())) {
            return false;
        } 
        TransactionManager tm = getTransactionManager(runtime.getKieSession().getEnvironment());
        if (((tm.getStatus()) == (TransactionManager.STATUS_NO_TRANSACTION)) || ((tm.getStatus()) == (TransactionManager.STATUS_ACTIVE))) {
            return true;
        } 
        return false;
    }

    protected boolean hasEnvironmentEntry(String name, Object value) {
        Object envEntry = environment.getEnvironment().get(name);
        if (value == null) {
            return envEntry == null;
        } 
        return value.equals(envEntry);
    }

    protected TransactionManager getTransactionManager(Environment env) {
        if (env == null) {
            env = environment.getEnvironment();
        } 
        Object txm = env.get(EnvironmentName.TRANSACTION_MANAGER);
        if ((txm != null) && (txm instanceof TransactionManager)) {
            return ((TransactionManager) (txm));
        } 
        return TransactionManagerFactory.get().newTransactionManager(env);
    }

    protected TransactionManager getTransactionManagerInternal(Environment env) {
        try {
            return getTransactionManager(env);
        } catch (Exception e) {
            // return no op transaction manager as none were found so let the ksession manage the tx instead
            return new TransactionManager() {
                @Override
                public void rollback(boolean transactionOwner) {
                }

                @Override
                public void registerTransactionSynchronization(TransactionSynchronization ts) {
                }

                @Override
                public void putResource(Object key, Object resource) {
                }

                @Override
                public int getStatus() {
                    return STATUS_NO_TRANSACTION;
                }

                @Override
                public Object getResource(Object key) {
                    return null;
                }

                @Override
                public void commit(boolean transactionOwner) {
                }

                @Override
                public boolean begin() {
                    return false;
                }
            };
        }
    }

    @Override
    public DeploymentDescriptor getDeploymentDescriptor() {
        return deploymentDescriptor;
    }

    @Override
    public void setDeploymentDescriptor(DeploymentDescriptor deploymentDescriptor) {
        AbstractRuntimeManager.this.deploymentDescriptor = deploymentDescriptor;
    }

    @Override
    public void setSecurityManager(SecurityManager securityManager) {
        if ((AbstractRuntimeManager.this.securityManager) != null) {
            throw new IllegalStateException((("Security Manager for " + (AbstractRuntimeManager.this.identifier)) + " manager is already set"));
        } 
        AbstractRuntimeManager.this.securityManager = securityManager;
    }

    protected void checkPermission() {
        if ((AbstractRuntimeManager.this.securityManager) != null) {
            AbstractRuntimeManager.this.securityManager.checkPermission();
        } 
    }

    @Override
    public void setCacheManager(CacheManager cacheManager) {
        if (cacheManager != null) {
            AbstractRuntimeManager.this.cacheManager = cacheManager;
        } 
    }

    @Override
    public CacheManager getCacheManager() {
        return cacheManager;
    }

    @Override
    public KieContainer getKieContainer() {
        return kieContainer;
    }

    @Override
    public void setKieContainer(KieContainer kieContainer) {
        AbstractRuntimeManager.this.kieContainer = kieContainer;
    }

    /* locking support for same context - runtime engine that deals with exact same process instance context */
    protected boolean isUseLocking() {
        return false;
    }

    protected void createLockOnGetEngine(Context<?> context, RuntimeEngine runtime) {
        if (!(isUseLocking())) {
            AbstractRuntimeManager.logger.debug("Locking on runtime manager disabled");
            return ;
        } 
        if (context instanceof ProcessInstanceIdContext) {
            Long piId = ((ProcessInstanceIdContext) (context)).getContextId();
            createLockOnGetEngine(piId, runtime);
        } 
    }

    protected void createLockOnGetEngine(Long id, RuntimeEngine runtime) {
        if (!(isUseLocking())) {
            AbstractRuntimeManager.logger.debug("Locking on runtime manager disabled");
            return ;
        } 
        if (id != null) {
            ReentrantLock newLock = new ReentrantLock();
            ReentrantLock lock = engineLocks.putIfAbsent(id, newLock);
            if (lock == null) {
                lock = newLock;
                AbstractRuntimeManager.logger.debug("New lock created as it did not exist before");
            } else {
                AbstractRuntimeManager.logger.debug("Lock exists with {} waiting threads", lock.getQueueLength());
            }
            AbstractRuntimeManager.logger.debug("Trying to get a lock {} for {} by {}", lock, id, runtime);
            lock.lock();
            AbstractRuntimeManager.logger.debug("Lock {} taken for {} by {} for waiting threads by {}", lock, id, runtime, lock.hasQueuedThreads());
        } 
    }

    protected void createLockOnNewProcessInstance(Long id, RuntimeEngine runtime) {
        if (!(isUseLocking())) {
            AbstractRuntimeManager.logger.debug("Locking on runtime manager disabled");
            return ;
        } 
        ReentrantLock newLock = new ReentrantLock();
        ReentrantLock lock = engineLocks.putIfAbsent(id, newLock);
        if (lock == null) {
            lock = newLock;
        } 
        lock.lock();
        AbstractRuntimeManager.logger.debug("[on new process instance] Lock {} created and stored in list by {}", lock, runtime);
    }

    protected void releaseAndCleanLock(RuntimeEngine runtime) {
        if (!(isUseLocking())) {
            AbstractRuntimeManager.logger.debug("Locking on runtime manager disabled");
            return ;
        } 
        if ((((RuntimeEngineImpl) (runtime)).getContext()) instanceof ProcessInstanceIdContext) {
            Long piId = ((ProcessInstanceIdContext) (((RuntimeEngineImpl) (runtime)).getContext())).getContextId();
            if (piId != null) {
                releaseAndCleanLock(piId, runtime);
            } 
        } 
    }

    protected void releaseAndCleanLock(Long id, RuntimeEngine runtime) {
        if (id != null) {
            ReentrantLock lock = engineLocks.get(id);
            if (lock != null) {
                if (!(lock.hasQueuedThreads())) {
                    AbstractRuntimeManager.logger.debug("Removing lock {} from list as non is waiting for it by {}", lock, runtime);
                    engineLocks.remove(id);
                } 
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                    AbstractRuntimeManager.logger.debug("{} unlocked by {}", lock, runtime);
                } 
            } 
        } 
    }
}

