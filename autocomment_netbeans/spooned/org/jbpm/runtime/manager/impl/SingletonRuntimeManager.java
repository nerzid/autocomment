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

import org.kie.api.runtime.manager.Context;
import org.kie.internal.runtime.manager.Disposable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.SessionFactory;
import org.jbpm.services.task.impl.TaskContentRegistry;
import org.kie.internal.runtime.manager.TaskServiceFactory;

/**
 * This RuntimeManager is backed by a "Singleton" strategy, meaning that only one <code>RuntimeEngine</code> instance will
 * exist for for the given RuntimeManager instance. The RuntimeEngine will be synchronized to make sure it will work
 * properly in multi-threaded environments. However, this might cause some performance issues due to sequential execution.
 * <br/>
 * An important aspect of this manager is that it will persists it's identifier as a temporary file to keep track of the
 * <code>KieSession</code> it was using to maintain its state: for example, the session state including (drools) facts, etc.
 * The mentioned file is named as follows:<br>
 * <code>manager.getIdentifier()-jbpmSessionId.ser</code>
 * For example, for default named manager it will be:<br/>
 * default-singleton-jbpmSessionId.ser
 * <br/>
 * The location of the file will be one of the following, it is resolved in below order:
 * <ul>
 *  <li>system property named: jbpm.data.dir</li>
 *  <li>system property named: jboss.server.data.dir - shall be used by default on JBoss AS</li>
 *  <li>system property named: java.io.tmpdir</li>
 * </ul>
 * In case there is a need to reset the state, simply removing of the *-jbpm.SessionId.ser from the mentioned location
 * will do the trick.
 */
public class SingletonRuntimeManager extends AbstractRuntimeManager {
    private static final Logger logger = LoggerFactory.getLogger(SingletonRuntimeManager.class);

    private RuntimeEngine singleton;

    private SessionFactory factory;

    private TaskServiceFactory taskServiceFactory;

    public SingletonRuntimeManager() {
        super(null, null);
        // no-op just for cdi, spring and other frameworks
    }

    public SingletonRuntimeManager(RuntimeEnvironment environment, SessionFactory factory, TaskServiceFactory taskServiceFactory, String identifier) {
        super(environment, identifier);
        SingletonRuntimeManager.this.factory = factory;
        SingletonRuntimeManager.this.taskServiceFactory = taskServiceFactory;
        this.identifier = identifier;
    }

    public void init() {
        // TODO should we proxy/wrap the ksession so we capture dispose.destroy method calls?
        String location = getLocation();
        Long knownSessionId = getPersistedSessionId(location, identifier);
        InternalTaskService internalTaskService = ((InternalTaskService) (taskServiceFactory.newTaskService()));
        if (knownSessionId > 0) {
            try {
                SingletonRuntimeManager.this.singleton = new SynchronizedRuntimeImpl(factory.findKieSessionById(knownSessionId), internalTaskService);
            } catch (RuntimeException e) {
                // in case session with known id was found
            }
        } 
        if ((SingletonRuntimeManager.this.singleton) == null) {
            SingletonRuntimeManager.this.singleton = new SynchronizedRuntimeImpl(factory.newKieSession(), internalTaskService);
            persistSessionId(location, identifier, singleton.getKieSession().getIdentifier());
        } 
        ((RuntimeEngineImpl) (singleton)).setManager(SingletonRuntimeManager.this);
        TaskContentRegistry.get().addMarshallerContext(getIdentifier(), new org.kie.internal.task.api.ContentMarshallerContext(environment.getEnvironment(), environment.getClassLoader()));
        configureRuntimeOnTaskService(internalTaskService, singleton);
        registerItems(SingletonRuntimeManager.this.singleton);
        attachManager(SingletonRuntimeManager.this.singleton);
        SingletonRuntimeManager.this.registry.register(SingletonRuntimeManager.this);
    }

    @SuppressWarnings(value = "rawtypes")
    @Override
    public RuntimeEngine getRuntimeEngine(Context context) {
        if (isClosed()) {
            throw new IllegalStateException((("Runtime manager " + (identifier)) + " is already closed"));
        } 
        checkPermission();
        // always return the same instance
        return SingletonRuntimeManager.this.singleton;
    }

    @Override
    public void signalEvent(String type, Object event) {
        if (isClosed()) {
            throw new IllegalStateException((("Runtime manager " + (identifier)) + " is already closed"));
        } 
        checkPermission();
        SingletonRuntimeManager.this.singleton.getKieSession().signalEvent(type, event);
    }

    @Override
    public void validate(KieSession ksession, org.jbpm.runtime.manager.impl.Context<?> context) throws IllegalStateException {
        if (isClosed()) {
            throw new IllegalStateException((("Runtime manager " + (identifier)) + " is already closed"));
        } 
        if (((SingletonRuntimeManager.this.singleton) != null) && ((SingletonRuntimeManager.this.singleton.getKieSession().getIdentifier()) != (ksession.getIdentifier()))) {
            throw new IllegalStateException(("Invalid session was used for this context " + context));
        } 
    }

    @Override
    public void disposeRuntimeEngine(RuntimeEngine runtime) {
        // no-op, singleton session is always active
    }

    @Override
    public void close() {
        if ((singleton) == null) {
            return ;
        } 
        super.close();
        // dispose singleton session only when manager is closing
        try {
            removeRuntimeFromTaskService();
        } catch (UnsupportedOperationException e) {
            SingletonRuntimeManager.logger.debug("Exception while closing task service, was it initialized? {}", e.getMessage());
        }
        if ((SingletonRuntimeManager.this.singleton) instanceof Disposable) {
            ((Disposable) (SingletonRuntimeManager.this.singleton)).dispose();
        } 
        factory.close();
        SingletonRuntimeManager.this.singleton = null;
    }

    /**
     * Retrieves session id from serialized file named jbpmSessionId.ser from given location.
     * @param location directory where jbpmSessionId.ser file should be
     * @param identifier of the manager owning this ksessionId
     * @return sessionId if file was found otherwise 0
     */
    protected Long getPersistedSessionId(String location, String identifier) {
        File sessionIdStore = new File((((location + (File.separator)) + identifier) + "-jbpmSessionId.ser"));
        if (sessionIdStore.exists()) {
            Long knownSessionId = null;
            FileInputStream fis = null;
            ObjectInputStream in = null;
            try {
                fis = new FileInputStream(sessionIdStore);
                in = new ObjectInputStream(fis);
                Object tmp = in.readObject();
                if (tmp instanceof Integer) {
                    tmp = new Long(((Integer) (tmp)));
                } 
                knownSessionId = ((Long) (tmp));
                return knownSessionId.longValue();
            } catch (Exception e) {
                return 0L;
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
                } 
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                    }
                } 
            }
        } else {
            return 0L;
        }
    }

    /**
     * Stores gives ksessionId in a serialized file in given location under jbpmSessionId.ser file name
     * @param location directory where serialized file should be stored
     * @param identifier of the manager owning this ksessionId
     * @param ksessionId value of ksessionId to be stored
     */
    protected void persistSessionId(String location, String identifier, Long ksessionId) {
        if (location == null) {
            return ;
        } 
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            fos = new FileOutputStream((((location + (File.separator)) + identifier) + "-jbpmSessionId.ser"));
            out = new ObjectOutputStream(fos);
            out.writeObject(Long.valueOf(ksessionId));
            out.close();
        } catch (IOException ex) {
            // logger.warn("Error when persisting known session id", ex);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            } 
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            } 
        }
    }

    protected String getLocation() {
        String location = System.getProperty("jbpm.data.dir", System.getProperty("jboss.server.data.dir"));
        if (location == null) {
            location = System.getProperty("java.io.tmpdir");
        } 
        return location;
    }

    public SessionFactory getFactory() {
        return factory;
    }

    public void setFactory(SessionFactory factory) {
        SingletonRuntimeManager.this.factory = factory;
    }

    public TaskServiceFactory getTaskServiceFactory() {
        return taskServiceFactory;
    }

    public void setTaskServiceFactory(TaskServiceFactory taskServiceFactory) {
        SingletonRuntimeManager.this.taskServiceFactory = taskServiceFactory;
    }
}
