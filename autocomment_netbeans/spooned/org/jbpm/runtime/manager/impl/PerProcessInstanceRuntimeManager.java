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

import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.core.command.CommandService;
import org.kie.api.runtime.manager.Context;
import org.kie.internal.runtime.manager.context.CorrelationKeyContext;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.internal.runtime.manager.Disposable;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.drools.core.command.impl.GenericCommand;
import java.util.HashMap;
import org.jbpm.runtime.manager.impl.mapper.InMemoryMapper;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.jbpm.runtime.manager.impl.mapper.InternalMapper;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.internal.task.api.InternalTaskService;
import org.jbpm.runtime.manager.impl.mapper.JPAMapper;
import org.kie.api.runtime.KieSession;
import org.drools.core.command.impl.KnowledgeCommandContext;
import java.util.List;
import org.jbpm.runtime.manager.impl.factory.LocalTaskServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.kie.internal.runtime.manager.Mapper;
import org.drools.persistence.OrderedTransactionSynchronization;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.SessionFactory;
import org.drools.core.command.SingleSessionCommandService;
import org.jbpm.services.task.impl.TaskContentRegistry;
import org.kie.api.task.TaskService;
import org.kie.internal.runtime.manager.TaskServiceFactory;
import org.drools.persistence.TransactionManager;
import org.drools.persistence.TransactionManagerHelper;

/**
 * A RuntimeManager implementation that is backed by the "Per Process Instance" strategy. This means that every
 * process instance will be bound to a ksession for it's entire life time.  Once started, whenever other operations are invoked,
 * this manager will ensure that the correct ksession will be provided.
 * <br/>
 * This also applies to sub processes (reusable sub processes) that create new process instances: the sub process instance
 * will have its own ksession independent of the parent one.
 * <br/>
 * This manager will ensure that as soon as the process instance completes, the ksession will be disposed of and destroyed.
 * <br/>
 * This implementation supports the following <code>Context</code> implementations:
 * <ul>
 *  <li>ProcessInstanceIdContext</li>
 *  <li>CorrelationKeyContext</li>
 *  <li>EmptyContext - for initial RuntimeEngine to start process only</li>
 * </ul>
 */
public class PerProcessInstanceRuntimeManager extends AbstractRuntimeManager {
    private static final Logger logger = LoggerFactory.getLogger(PerProcessInstanceRuntimeManager.class);

    private boolean useLocking = Boolean.parseBoolean(System.getProperty("org.jbpm.runtime.manager.ppi.lock", "true"));

    private SessionFactory factory;

    private TaskServiceFactory taskServiceFactory;

    private static ThreadLocal<Map<Object, RuntimeEngine>> local = new ThreadLocal<Map<Object, RuntimeEngine>>();

    private Mapper mapper;

    public PerProcessInstanceRuntimeManager(RuntimeEnvironment environment, SessionFactory factory, TaskServiceFactory taskServiceFactory, String identifier) {
        super(environment, identifier);
        PerProcessInstanceRuntimeManager.this.factory = factory;
        PerProcessInstanceRuntimeManager.this.taskServiceFactory = taskServiceFactory;
        PerProcessInstanceRuntimeManager.this.mapper = ((org.kie.internal.runtime.manager.RuntimeEnvironment) (environment)).getMapper();
        PerProcessInstanceRuntimeManager.this.registry.register(PerProcessInstanceRuntimeManager.this);
    }

    @Override
    public RuntimeEngine getRuntimeEngine(org.jbpm.runtime.manager.impl.Context<?> context) {
        if (isClosed()) {
            throw new IllegalStateException((("Runtime manager " + (identifier)) + " is already closed"));
        } 
        checkPermission();
        RuntimeEngine runtime = null;
        Object contextId = context.getContextId();
        if (!((context instanceof ProcessInstanceIdContext) || (context instanceof CorrelationKeyContext))) {
            PerProcessInstanceRuntimeManager.logger.warn("ProcessInstanceIdContext or CorrelationKeyContext shall be used when interacting with PerProcessInstance runtime manager");
        } 
        if (engineInitEager) {
            KieSession ksession = null;
            Long ksessionId = null;
            if ((contextId == null) || (context instanceof EmptyContext)) {
                ksession = factory.newKieSession();
                ksessionId = ksession.getIdentifier();
            } else {
                RuntimeEngine localRuntime = findLocalRuntime(contextId);
                if (localRuntime != null) {
                    return localRuntime;
                } 
                ksessionId = mapper.findMapping(context, PerProcessInstanceRuntimeManager.this.identifier);
                if (ksessionId == null) {
                    throw new org.kie.internal.runtime.manager.SessionNotFoundException(("No session found for context " + (context.getContextId())));
                } 
                ksession = factory.findKieSessionById(ksessionId);
            }
            InternalTaskService internalTaskService = ((InternalTaskService) (taskServiceFactory.newTaskService()));
            runtime = new RuntimeEngineImpl(ksession, internalTaskService);
            ((RuntimeEngineImpl) (runtime)).setManager(PerProcessInstanceRuntimeManager.this);
            ((RuntimeEngineImpl) (runtime)).setContext(context);
            configureRuntimeOnTaskService(internalTaskService, runtime);
            registerDisposeCallback(runtime, new org.jbpm.runtime.manager.impl.tx.DisposeSessionTransactionSynchronization(PerProcessInstanceRuntimeManager.this, runtime));
            registerItems(runtime);
            attachManager(runtime);
            ksession.addEventListener(new PerProcessInstanceRuntimeManager.MaintainMappingListener(ksessionId, runtime, PerProcessInstanceRuntimeManager.this.identifier));
        } else {
            RuntimeEngine localRuntime = findLocalRuntime(contextId);
            if (localRuntime != null) {
                return localRuntime;
            } 
            // lazy initialization of ksession and task service
            runtime = new RuntimeEngineImpl(context, new PerProcessInstanceRuntimeManager.PerProcessInstanceInitializer());
            ((RuntimeEngineImpl) (runtime)).setManager(PerProcessInstanceRuntimeManager.this);
        }
        createLockOnGetEngine(context, runtime);
        saveLocalRuntime(contextId, runtime);
        return runtime;
    }

    @Override
    public void signalEvent(String type, Object event) {
        // first signal with new context in case there are start event with signal
        RuntimeEngine runtimeEngine = getRuntimeEngine(ProcessInstanceIdContext.get());
        runtimeEngine.getKieSession().signalEvent(type, event);
        disposeRuntimeEngine(runtimeEngine);
        // next find out all instances waiting for given event type
        List<String> processInstances = ((InternalMapper) (mapper)).findContextIdForEvent(type, getIdentifier());
        for (String piId : processInstances) {
            runtimeEngine = getRuntimeEngine(ProcessInstanceIdContext.get(Long.parseLong(piId)));
            runtimeEngine.getKieSession().signalEvent(type, event);
            disposeRuntimeEngine(runtimeEngine);
        }
        // process currently active runtime engines
        Map<Object, RuntimeEngine> currentlyActive = PerProcessInstanceRuntimeManager.local.get();
        if ((currentlyActive != null) && (!(currentlyActive.isEmpty()))) {
            RuntimeEngine[] activeEngines = currentlyActive.values().toArray(new RuntimeEngine[currentlyActive.size()]);
            for (RuntimeEngine engine : activeEngines) {
                Context<?> context = ((RuntimeEngineImpl) (engine)).getContext();
                if (((context != null) && (context instanceof ProcessInstanceIdContext)) && ((((ProcessInstanceIdContext) (context)).getContextId()) != null)) {
                    engine.getKieSession().signalEvent(type, event, ((ProcessInstanceIdContext) (context)).getContextId());
                } 
            }
        } 
    }

    @Override
    public void validate(KieSession ksession, org.jbpm.runtime.manager.impl.Context<?> context) throws IllegalStateException {
        if (isClosed()) {
            throw new IllegalStateException((("Runtime manager " + (identifier)) + " is already closed"));
        } 
        if ((context == null) || ((context.getContextId()) == null)) {
            return ;
        } 
        Long ksessionId = mapper.findMapping(context, PerProcessInstanceRuntimeManager.this.identifier);
        if (ksessionId == null) {
            // make sure ksession is not use by any other context
            Object contextId = mapper.findContextId(ksession.getIdentifier(), PerProcessInstanceRuntimeManager.this.identifier);
            if (contextId != null) {
                throw new IllegalStateException((("KieSession with id " + (ksession.getIdentifier())) + " is already used by another context"));
            } 
            return ;
        } 
        if ((ksession.getIdentifier()) != ksessionId) {
            throw new IllegalStateException(("Invalid session was used for this context " + context));
        } 
    }

    @Override
    public void disposeRuntimeEngine(RuntimeEngine runtime) {
        if (isClosed()) {
            throw new IllegalStateException((("Runtime manager " + (identifier)) + " is already closed"));
        } 
        if (canDispose(runtime)) {
            removeLocalRuntime(runtime);
            if (runtime instanceof Disposable) {
                // special handling for in memory to not allow to dispose if there is any context in the mapper
                if (((mapper) instanceof InMemoryMapper) && (((InMemoryMapper) (mapper)).hasContext(runtime.getKieSession().getIdentifier()))) {
                    return ;
                } 
                ((Disposable) (runtime)).dispose();
            } 
            releaseAndCleanLock(runtime);
        } 
    }

    @Override
    public void softDispose(RuntimeEngine runtimeEngine) {
        super.softDispose(runtimeEngine);
        removeLocalRuntime(runtimeEngine);
    }

    @Override
    public void close() {
        try {
            if (!((taskServiceFactory) instanceof LocalTaskServiceFactory)) {
                // if it's CDI based (meaning single application scoped bean) we need to unregister context
                removeRuntimeFromTaskService();
            } 
        } catch (Exception e) {
            // do nothing
        }
        super.close();
        factory.close();
    }

    public boolean validate(Long ksessionId, Long processInstanceId) {
        Long mapped = PerProcessInstanceRuntimeManager.this.mapper.findMapping(ProcessInstanceIdContext.get(processInstanceId), PerProcessInstanceRuntimeManager.this.identifier);
        if (mapped == ksessionId) {
            return true;
        } 
        return false;
    }

    private class MaintainMappingListener extends DefaultProcessEventListener {
        private Long ksessionId;

        private RuntimeEngine runtime;

        private String managerId;

        MaintainMappingListener(Long ksessionId, RuntimeEngine runtime, String managerId) {
            PerProcessInstanceRuntimeManager.MaintainMappingListener.this.ksessionId = ksessionId;
            PerProcessInstanceRuntimeManager.MaintainMappingListener.this.runtime = runtime;
            PerProcessInstanceRuntimeManager.MaintainMappingListener.this.managerId = managerId;
        }

        @Override
        public void afterProcessCompleted(ProcessCompletedEvent event) {
            mapper.removeMapping(new org.jbpm.runtime.manager.impl.mapper.EnvironmentAwareProcessInstanceContext(event.getKieRuntime().getEnvironment(), event.getProcessInstance().getId()), managerId);
            removeLocalRuntime(runtime);
            registerDisposeCallback(runtime, new org.jbpm.runtime.manager.impl.tx.DestroySessionTransactionSynchronization(runtime.getKieSession()));
        }

        @Override
        public void beforeProcessStarted(ProcessStartedEvent event) {
            mapper.saveMapping(new org.jbpm.runtime.manager.impl.mapper.EnvironmentAwareProcessInstanceContext(event.getKieRuntime().getEnvironment(), event.getProcessInstance().getId()), ksessionId, managerId);
            saveLocalRuntime(event.getProcessInstance().getId(), runtime);
            ((RuntimeEngineImpl) (runtime)).setContext(ProcessInstanceIdContext.get(event.getProcessInstance().getId()));
            createLockOnNewProcessInstance(event.getProcessInstance().getId(), runtime);
        }
    }

    public SessionFactory getFactory() {
        return factory;
    }

    public void setFactory(SessionFactory factory) {
        PerProcessInstanceRuntimeManager.this.factory = factory;
    }

    public TaskServiceFactory getTaskServiceFactory() {
        return taskServiceFactory;
    }

    public void setTaskServiceFactory(TaskServiceFactory taskServiceFactory) {
        PerProcessInstanceRuntimeManager.this.taskServiceFactory = taskServiceFactory;
    }

    public Mapper getMapper() {
        return mapper;
    }

    public void setMapper(Mapper mapper) {
        PerProcessInstanceRuntimeManager.this.mapper = mapper;
    }

    protected RuntimeEngine findLocalRuntime(Object processInstanceId) {
        if (processInstanceId == null) {
            return null;
        } 
        Map<Object, RuntimeEngine> map = PerProcessInstanceRuntimeManager.local.get();
        if (map == null) {
            return null;
        } else {
            RuntimeEngine engine = map.get(processInstanceId);
            // check if engine is not already disposed as afterCompletion might be issued from another thread
            if ((engine != null) && (((RuntimeEngineImpl) (engine)).isDisposed())) {
                map.remove(processInstanceId);
                return null;
            } 
            return engine;
        }
    }

    protected void saveLocalRuntime(Object processInstanceId, RuntimeEngine runtime) {
        // since this manager is strictly for process instance ids it should only store
        // process instance ids as local cache keys
        if ((processInstanceId == null) || (!(processInstanceId instanceof Long))) {
            return ;
        } 
        Map<Object, RuntimeEngine> map = PerProcessInstanceRuntimeManager.local.get();
        if (map == null) {
            map = new HashMap<Object, RuntimeEngine>();
            PerProcessInstanceRuntimeManager.local.set(map);
        } 
        map.put(processInstanceId, runtime);
    }

    protected void removeLocalRuntime(RuntimeEngine runtime) {
        Map<Object, RuntimeEngine> map = PerProcessInstanceRuntimeManager.local.get();
        Object keyToRemove = -1L;
        if (map != null) {
            for (Map.Entry<Object, RuntimeEngine> entry : map.entrySet()) {
                if (runtime.equals(entry.getValue())) {
                    keyToRemove = entry.getKey();
                    break;
                } 
            }
            map.remove(keyToRemove);
        } 
    }

    @Override
    public void init() {
        TaskContentRegistry.get().addMarshallerContext(getIdentifier(), new org.kie.internal.task.api.ContentMarshallerContext(environment.getEnvironment(), environment.getClassLoader()));
        boolean owner = false;
        TransactionManager tm = null;
        if (environment.usePersistence()) {
            tm = getTransactionManagerInternal(environment.getEnvironment());
            owner = tm.begin();
        } 
        try {
            // need to init one session to bootstrap all case - such as start timers
            KieSession initialKsession = factory.newKieSession();
            // there is a need to call getProcessRuntime otherwise the start listeners are not registered
            initialKsession.execute(new GenericCommand<Void>() {
                private static final long serialVersionUID = 1L;

                @Override
                public Void execute(Context context) {
                    KieSession ksession = ((KnowledgeCommandContext) (context)).getKieSession();
                    ((InternalKnowledgeRuntime) (ksession)).getProcessRuntime();
                    return null;
                }
            });
            initialKsession.execute(new PerProcessInstanceRuntimeManager.DestroyKSessionCommand(initialKsession, PerProcessInstanceRuntimeManager.this));
            if (!("false".equalsIgnoreCase(System.getProperty("org.jbpm.rm.init.timer")))) {
                if ((mapper) instanceof JPAMapper) {
                    List<Long> ksessionsToInit = ((JPAMapper) (mapper)).findKSessionToInit(PerProcessInstanceRuntimeManager.this.identifier);
                    for (Long id : ksessionsToInit) {
                        initialKsession = factory.findKieSessionById(id);
                        initialKsession.execute(new PerProcessInstanceRuntimeManager.DisposeKSessionCommand(initialKsession, PerProcessInstanceRuntimeManager.this));
                    }
                } 
            } 
            if (tm != null) {
                tm.commit(owner);
            } 
        } catch (Exception e) {
            if (tm != null) {
                tm.rollback(owner);
            } 
            throw new RuntimeException(("Exception while initializing runtime manager " + (PerProcessInstanceRuntimeManager.this.identifier)), e);
        }
    }

    private static class DestroyKSessionCommand implements GenericCommand<Void> {
        private static final long serialVersionUID = 1L;

        private KieSession initialKsession;

        private AbstractRuntimeManager manager;

        public DestroyKSessionCommand(KieSession initialKsession, AbstractRuntimeManager manager) {
            PerProcessInstanceRuntimeManager.DestroyKSessionCommand.this.initialKsession = initialKsession;
            PerProcessInstanceRuntimeManager.DestroyKSessionCommand.this.manager = manager;
        }

        @Override
        public Void execute(Context context) {
            TransactionManager tm = ((TransactionManager) (initialKsession.getEnvironment().get(EnvironmentName.TRANSACTION_MANAGER)));
            if (manager.hasEnvironmentEntry("IS_JTA_TRANSACTION", false)) {
                if ((initialKsession) instanceof CommandBasedStatefulKnowledgeSession) {
                    CommandService commandService = ((CommandBasedStatefulKnowledgeSession) (initialKsession)).getCommandService();
                    ((SingleSessionCommandService) (commandService)).destroy();
                } else {
                    ((KnowledgeCommandContext) (context)).getKieSession().destroy();
                }
                return null;
            } 
            if ((((tm != null) && ((tm.getStatus()) != (TransactionManager.STATUS_NO_TRANSACTION))) && ((tm.getStatus()) != (TransactionManager.STATUS_ROLLEDBACK))) && ((tm.getStatus()) != (TransactionManager.STATUS_COMMITTED))) {
                TransactionManagerHelper.registerTransactionSyncInContainer(tm, new OrderedTransactionSynchronization(5, ("PPIRM-" + (initialKsession.getIdentifier()))) {
                    @Override
                    public void beforeCompletion() {
                        if ((initialKsession) instanceof CommandBasedStatefulKnowledgeSession) {
                            CommandService commandService = ((CommandBasedStatefulKnowledgeSession) (initialKsession)).getCommandService();
                            ((SingleSessionCommandService) (commandService)).destroy();
                        } 
                    }

                    @Override
                    public void afterCompletion(int arg0) {
                        initialKsession.dispose();
                    }
                });
            } else {
                initialKsession.destroy();
            }
            return null;
        }
    }

    private static class DisposeKSessionCommand implements GenericCommand<Void> {
        private static final long serialVersionUID = 1L;

        private KieSession initialKsession;

        private AbstractRuntimeManager manager;

        public DisposeKSessionCommand(KieSession initialKsession, AbstractRuntimeManager manager) {
            PerProcessInstanceRuntimeManager.DisposeKSessionCommand.this.initialKsession = initialKsession;
            PerProcessInstanceRuntimeManager.DisposeKSessionCommand.this.manager = manager;
        }

        @Override
        public Void execute(Context context) {
            if (manager.hasEnvironmentEntry("IS_JTA_TRANSACTION", false)) {
                initialKsession.dispose();
                return null;
            } 
            TransactionManager tm = ((TransactionManager) (initialKsession.getEnvironment().get(EnvironmentName.TRANSACTION_MANAGER)));
            if ((((tm != null) && ((tm.getStatus()) != (TransactionManager.STATUS_NO_TRANSACTION))) && ((tm.getStatus()) != (TransactionManager.STATUS_ROLLEDBACK))) && ((tm.getStatus()) != (TransactionManager.STATUS_COMMITTED))) {
                TransactionManagerHelper.registerTransactionSyncInContainer(tm, new OrderedTransactionSynchronization(5, ("PPIRM-" + (initialKsession.getIdentifier()))) {
                    @Override
                    public void beforeCompletion() {
                    }

                    @Override
                    public void afterCompletion(int arg0) {
                        initialKsession.dispose();
                    }
                });
            } else {
                initialKsession.dispose();
            }
            return null;
        }
    }

    private class PerProcessInstanceInitializer implements RuntimeEngineInitlializer {
        @Override
        public KieSession initKieSession(org.jbpm.runtime.manager.impl.Context<?> context, InternalRuntimeManager manager, RuntimeEngine engine) {
            Object contextId = context.getContextId();
            if ((contextId != null) && (!(context instanceof EmptyContext))) {
                Long found = mapper.findMapping(context, manager.getIdentifier());
                if (found == null) {
                    removeLocalRuntime(engine);
                    throw new org.kie.internal.runtime.manager.SessionNotFoundException(("No session found for context " + (context.getContextId())));
                } 
            } 
            KieSession ksession = null;
            Long ksessionId = null;
            if ((contextId == null) || (context instanceof EmptyContext)) {
                ksession = factory.newKieSession();
                ksessionId = ksession.getIdentifier();
            } else {
                RuntimeEngine localRuntime = ((PerProcessInstanceRuntimeManager) (manager)).findLocalRuntime(contextId);
                if ((localRuntime != null) && ((((RuntimeEngineImpl) (engine)).internalGetKieSession()) != null)) {
                    return localRuntime.getKieSession();
                } 
                ksessionId = mapper.findMapping(context, manager.getIdentifier());
                if (ksessionId == null) {
                    throw new org.kie.internal.runtime.manager.SessionNotFoundException(("No session found for context " + (context.getContextId())));
                } 
                ksession = factory.findKieSessionById(ksessionId);
            }
            ((RuntimeEngineImpl) (engine)).internalSetKieSession(ksession);
            registerItems(engine);
            attachManager(engine);
            registerDisposeCallback(engine, new org.jbpm.runtime.manager.impl.tx.DisposeSessionTransactionSynchronization(manager, engine));
            ksession.addEventListener(new PerProcessInstanceRuntimeManager.MaintainMappingListener(ksessionId, engine, manager.getIdentifier()));
            return ksession;
        }

        @Override
        public TaskService initTaskService(org.jbpm.runtime.manager.impl.Context<?> context, InternalRuntimeManager manager, RuntimeEngine engine) {
            InternalTaskService internalTaskService = ((InternalTaskService) (taskServiceFactory.newTaskService()));
            configureRuntimeOnTaskService(internalTaskService, engine);
            return internalTaskService;
        }
    }

    @Override
    protected boolean isUseLocking() {
        return useLocking;
    }
}

