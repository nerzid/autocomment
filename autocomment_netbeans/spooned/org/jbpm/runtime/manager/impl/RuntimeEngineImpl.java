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

import org.kie.api.runtime.manager.audit.AuditService;
import java.util.concurrent.CopyOnWriteArrayList;
import org.kie.internal.runtime.manager.Disposable;
import org.kie.internal.runtime.manager.DisposeListener;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.api.runtime.KieSession;
import java.util.List;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.task.TaskService;

/**
 * An implementation of the <code>RuntimeEngine</code> that additionally implements the <code>Disposable</code>
 * interface to allow other components to register listeners on it. The usual case for this is that listeners
 * and work item handlers might be interested in receiving notification when the runtime engine is disposed of,
 * in order deactivate themselves too and not receive any other events.
 */
public class RuntimeEngineImpl implements RuntimeEngine , Disposable {
    private RuntimeEngineInitlializer initializer;

    private Context<?> context;

    private KieSession ksession;

    private Long kieSessionId;

    private TaskService taskService;

    private AuditService auditService;

    private RuntimeManager manager;

    private boolean disposed = false;

    private boolean afterCompletion = false;

    private List<DisposeListener> listeners = new CopyOnWriteArrayList<DisposeListener>();

    public RuntimeEngineImpl(KieSession ksession, TaskService taskService) {
        RuntimeEngineImpl.this.ksession = ksession;
        RuntimeEngineImpl.this.kieSessionId = ksession.getIdentifier();
        RuntimeEngineImpl.this.taskService = taskService;
    }

    public RuntimeEngineImpl(Context<?> context, RuntimeEngineInitlializer initializer) {
        RuntimeEngineImpl.this.context = context;
        RuntimeEngineImpl.this.initializer = initializer;
    }

    @Override
    public KieSession getKieSession() {
        if (RuntimeEngineImpl.this.disposed) {
            throw new IllegalStateException("This runtime is already diposed");
        } 
        if (((ksession) == null) && ((initializer) != null)) {
            ksession = initializer.initKieSession(context, ((InternalRuntimeManager) (manager)), RuntimeEngineImpl.this);
            RuntimeEngineImpl.this.kieSessionId = ksession.getIdentifier();
        } 
        return RuntimeEngineImpl.this.ksession;
    }

    @Override
    public TaskService getTaskService() {
        if (RuntimeEngineImpl.this.disposed) {
            throw new IllegalStateException("This runtime is already diposed");
        } 
        if ((taskService) == null) {
            if ((initializer) != null) {
                taskService = initializer.initTaskService(context, ((InternalRuntimeManager) (manager)), RuntimeEngineImpl.this);
            } 
            if ((taskService) == null) {
                throw new UnsupportedOperationException("TaskService was not configured");
            } 
        } 
        return RuntimeEngineImpl.this.taskService;
    }

    @Override
    public void dispose() {
        if (!(RuntimeEngineImpl.this.disposed)) {
            // first call listeners and then dispose itself
            for (DisposeListener listener : listeners) {
                listener.onDispose(RuntimeEngineImpl.this);
            }
            if ((ksession) != null) {
                try {
                    ksession.dispose();
                } catch (IllegalStateException e) {
                    // do nothing most likely ksession was already disposed
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } 
            if ((auditService) != null) {
                auditService.dispose();
            } 
            RuntimeEngineImpl.this.disposed = true;
        } 
    }

    @Override
    public void addDisposeListener(DisposeListener listener) {
        if (RuntimeEngineImpl.this.disposed) {
            throw new IllegalStateException("This runtime is already diposed");
        } 
        RuntimeEngineImpl.this.listeners.add(listener);
    }

    public RuntimeManager getManager() {
        return manager;
    }

    public void setManager(RuntimeManager manager) {
        RuntimeEngineImpl.this.manager = manager;
    }

    public boolean isDisposed() {
        return disposed;
    }

    @Override
    public AuditService getAuditService() {
        if ((auditService) == null) {
            boolean usePersistence = ((InternalRuntimeManager) (manager)).getEnvironment().usePersistence();
            if (usePersistence) {
                auditService = new org.jbpm.process.audit.JPAAuditLogService(getKieSession().getEnvironment());
            } else {
                throw new UnsupportedOperationException("AuditService was not configured, supported only with persistence");
            }
        } 
        return auditService;
    }

    public KieSession internalGetKieSession() {
        return ksession;
    }

    public void internalSetKieSession(KieSession ksession) {
        RuntimeEngineImpl.this.ksession = ksession;
        RuntimeEngineImpl.this.kieSessionId = ksession.getIdentifier();
    }

    public boolean isAfterCompletion() {
        return afterCompletion;
    }

    public void setAfterCompletion(boolean completing) {
        RuntimeEngineImpl.this.afterCompletion = completing;
    }

    public Context<?> getContext() {
        return context;
    }

    public void setContext(Context<?> context) {
        RuntimeEngineImpl.this.context = context;
    }

    public Long getKieSessionId() {
        return kieSessionId;
    }
}

