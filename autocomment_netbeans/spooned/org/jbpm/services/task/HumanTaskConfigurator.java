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


package org.jbpm.services.task;

import EnvironmentName.ENTITY_MANAGER_FACTORY;
import java.lang.reflect.Constructor;
import javax.persistence.EntityManagerFactory;
import org.kie.api.runtime.Environment;
import org.drools.core.impl.EnvironmentFactory;
import org.kie.internal.task.api.EventService;
import org.jbpm.services.task.commands.TaskCommandExecutorImpl;
import org.kie.api.task.TaskLifeCycleEventListener;
import java.util.HashSet;
import org.drools.core.command.Interceptor;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.kie.api.task.TaskService;
import org.kie.internal.task.api.UserInfo;
import org.kie.api.task.UserGroupCallback;
import org.slf4j.LoggerFactory;
import java.util.Set;
import EnvironmentName.TASK_USER_GROUP_CALLBACK;
import org.jbpm.services.task.impl.TaskDeadlinesServiceImpl;
import org.jbpm.services.task.events.TaskEventSupport;
import EnvironmentName.TASK_USER_INFO;

/**
 * Task service configurator that provides fluent API approach to building <code>TaskService</code>
 * instances. Most of the attributes have their defaults but there is one that must be explicitly set
 * <ul>
 * 	<li>entityManagerFactory</li>
 * </ul>
 * Important to notice is defaults for:
 * <ul>
 * 	<li>userInfo - DefaultUserInfo by default</li>
 * 	<li>userGroupCallback - uses MvelUserGroupCallbackImpl by default</li>
 * </ul>
 *
 * @see DefaultUserInfo
 * @see MvelUserGroupCallbackImpl
 */
public class HumanTaskConfigurator {
    private static final Logger logger = LoggerFactory.getLogger(HumanTaskConfigurator.class);

    private static final String DEFAULT_INTERCEPTOR = "org.jbpm.services.task.persistence.TaskTransactionInterceptor";

    private static final String TX_LOCK_INTERCEPTOR = "org.drools.persistence.jta.TransactionLockInterceptor";

    private static final String OPTIMISTIC_LOCK_INTERCEPTOR = "org.drools.persistence.jpa.OptimisticLockRetryInterceptor";

    private TaskService service;

    private TaskCommandExecutorImpl commandExecutor;

    private Environment environment = EnvironmentFactory.newEnvironment();

    private UserGroupCallback userGroupCallback;

    private UserInfo userInfo;

    private Set<HumanTaskConfigurator.PriorityInterceptor> interceptors = new TreeSet<HumanTaskConfigurator.PriorityInterceptor>();

    private Set<TaskLifeCycleEventListener> listeners = new HashSet<TaskLifeCycleEventListener>();

    public HumanTaskConfigurator interceptor(int priority, Interceptor interceptor) {
        if (interceptor == null) {
            return this;
        }
        // add PriorityInterceptor{new HumanTaskConfigurator.PriorityInterceptor(priority, interceptor)} to Set{this.interceptors}
        this.interceptors.add(new HumanTaskConfigurator.PriorityInterceptor(priority, interceptor));
        return this;
    }

    public HumanTaskConfigurator listener(TaskLifeCycleEventListener listener) {
        if (listener == null) {
            return this;
        }
        // add TaskLifeCycleEventListener{listener} to Set{this.listeners}
        this.listeners.add(listener);
        return this;
    }

    public HumanTaskConfigurator environment(Environment environment) {
        if (environment == null) {
            return this;
        }
        this.environment = environment;
        return this;
    }

    public HumanTaskConfigurator entityManagerFactory(EntityManagerFactory emf) {
        if (emf == null) {
            return this;
        }
        // set void{ENTITY_MANAGER_FACTORY} to Environment{environment}
        environment.set(ENTITY_MANAGER_FACTORY, emf);
        return this;
    }

    public HumanTaskConfigurator userInfo(UserInfo userInfo) {
        if (userInfo == null) {
            return this;
        }
        this.userInfo = userInfo;
        return this;
    }

    public HumanTaskConfigurator userGroupCallback(UserGroupCallback userGroupCallback) {
        if (userGroupCallback == null) {
            return this;
        }
        this.userGroupCallback = userGroupCallback;
        return this;
    }

    @SuppressWarnings(value = "unchecked")
    public TaskService getTaskService() {
        if ((service) == null) {
            TaskEventSupport taskEventSupport = new TaskEventSupport();
            this.commandExecutor = new TaskCommandExecutorImpl(this.environment, taskEventSupport);
            if ((userGroupCallback) == null) {
                userGroupCallback = new MvelUserGroupCallbackImpl(true);
            }
            environment.set(TASK_USER_GROUP_CALLBACK, userGroupCallback);
            if ((userInfo) == null) {
                userInfo = new DefaultUserInfo(true);
            }
            environment.set(TASK_USER_INFO, userInfo);
            addDefaultInterceptor();
            addTransactionLockInterceptor();
            addOptimisticLockInterceptor();
            for (HumanTaskConfigurator.PriorityInterceptor pInterceptor : interceptors) {
                this.commandExecutor.addInterceptor(pInterceptor.getInterceptor());
            }
            service = new CommandBasedTaskService(this.commandExecutor, taskEventSupport);
            // register listeners
            for (TaskLifeCycleEventListener listener : listeners) {
                ((EventService<TaskLifeCycleEventListener>) (service)).registerTaskEventListener(listener);
            }
            // initialize deadline service with command executor for processing
            if ((TaskDeadlinesServiceImpl.getInstance()) == null) {
                TaskDeadlinesServiceImpl.initialize(commandExecutor);
            }
        }
        return service;
    }

    @SuppressWarnings(value = "unchecked")
    protected void addDefaultInterceptor() {
        // add default interceptor if present
        try {
            Class<Interceptor> defaultInterceptorClass = ((Class<Interceptor>) (Class.forName(HumanTaskConfigurator.DEFAULT_INTERCEPTOR)));
            Constructor<Interceptor> constructor = defaultInterceptorClass.getConstructor(new Class[]{ Environment.class });
            Interceptor defaultInterceptor = constructor.newInstance(this.environment);
            interceptor(5, defaultInterceptor);
        } catch (Exception e) {
            HumanTaskConfigurator.logger.warn("No default interceptor found of type {} might be mssing jbpm-human-task-jpa module on classpath (error {}", HumanTaskConfigurator.DEFAULT_INTERCEPTOR, e.getMessage(), e);
        }
    }

    @SuppressWarnings(value = "unchecked")
    protected void addTransactionLockInterceptor() {
        // add default interceptor if present
        try {
            Class<Interceptor> defaultInterceptorClass = ((Class<Interceptor>) (Class.forName(HumanTaskConfigurator.TX_LOCK_INTERCEPTOR)));
            Constructor<Interceptor> constructor = defaultInterceptorClass.getConstructor(new Class[]{ Environment.class , String.class });
            Interceptor defaultInterceptor = constructor.newInstance(this.environment, "task-service-tx-unlock");
            interceptor(6, defaultInterceptor);
        } catch (Exception e) {
            HumanTaskConfigurator.logger.warn("No tx lock interceptor found of type {} might be mssing drools-persistence-jpa module on classpath (error {}", HumanTaskConfigurator.TX_LOCK_INTERCEPTOR, e.getMessage(), e);
        }
    }

    @SuppressWarnings(value = "unchecked")
    protected void addOptimisticLockInterceptor() {
        // add default interceptor if present
        try {
            Class<Interceptor> defaultInterceptorClass = ((Class<Interceptor>) (Class.forName(HumanTaskConfigurator.OPTIMISTIC_LOCK_INTERCEPTOR)));
            Constructor<Interceptor> constructor = defaultInterceptorClass.getConstructor(new Class[]{  });
            Interceptor defaultInterceptor = constructor.newInstance();
            interceptor(7, defaultInterceptor);
        } catch (Exception e) {
            HumanTaskConfigurator.logger.warn("No optimistic lock interceptor found of type {} might be mssing drools-persistence-jpa module on classpath (error {}", HumanTaskConfigurator.OPTIMISTIC_LOCK_INTERCEPTOR, e.getMessage(), e);
        }
    }

    private static class PriorityInterceptor implements Comparable<HumanTaskConfigurator.PriorityInterceptor> {
        private Integer priority;

        private Interceptor interceptor;

        PriorityInterceptor(Integer priority, Interceptor interceptor) {
            this.priority = priority;
            this.interceptor = interceptor;
        }

        public Integer getPriority() {
            return priority;
        }

        public Interceptor getInterceptor() {
            return interceptor;
        }

        @Override
        public int compareTo(HumanTaskConfigurator.PriorityInterceptor other) {
            return this.getPriority().compareTo(other.getPriority());
        }

        @Override
        public String toString() {
            return ((("PriorityInterceptor [priority=" + (priority)) + ", interceptor=") + (interceptor)) + "]";
        }
    }
}

