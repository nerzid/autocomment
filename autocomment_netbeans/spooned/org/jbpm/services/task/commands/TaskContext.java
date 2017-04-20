/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.services.task.commands;

import EnvironmentName.TASK_USER_GROUP_CALLBACK;
import org.kie.internal.command.Context;
import org.kie.api.runtime.Environment;
import org.jbpm.services.task.internals.lifecycle.LifeCycleManager;
import org.jbpm.services.task.rule.impl.RuleContextProviderImpl;
import org.kie.api.task.model.Task;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.command.World;
import org.jbpm.services.task.events.TaskEventSupport;
import org.kie.internal.task.api.TaskIdentityService;
import org.kie.internal.task.api.TaskAdminService;
import org.kie.internal.task.api.TaskAttachmentService;
import org.kie.internal.task.api.TaskInstanceService;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.internal.task.api.TaskCommentService;
import org.kie.internal.task.api.TaskDefService;
import org.kie.internal.task.api.TaskQueryService;
import org.kie.internal.task.api.TaskContentService;
import org.kie.internal.task.api.TaskDeadlinesService;
import org.jbpm.services.task.rule.TaskRuleService;

/**
 *
 */
public class TaskContext implements TaskContext {
    private TaskPersistenceContext persistenceContext;

    private Environment environment;

    private TaskEventSupport taskEventSupport;

    private org.kie.internal.task.api.TaskContext delegate;

    public TaskContext() {
    }

    public TaskContext(Context context, Environment environment, TaskEventSupport taskEventSupport) {
        if (context instanceof org.kie) {
            this.delegate = ((TaskContext) (context));
            this.persistenceContext = ((TaskContext) (context)).getPersistenceContext();
        }
        this.environment = environment;
        this.taskEventSupport = taskEventSupport;
    }

    public TaskInstanceService getTaskInstanceService() {
        return new TaskInstanceServiceImpl(this, persistenceContext, getMvelLifeCycleManager(), taskEventSupport, environment);
    }

    public TaskDefService getTaskDefService() {
        return new TaskDefServiceImpl(persistenceContext);
    }

    public TaskQueryService getTaskQueryService() {
        return new TaskQueryServiceImpl(persistenceContext, getUserGroupCallback());
    }

    public TaskContentService getTaskContentService() {
        return new TaskContentServiceImpl(this, persistenceContext, taskEventSupport);
    }

    public TaskCommentService getTaskCommentService() {
        return new TaskCommentServiceImpl(persistenceContext);
    }

    public TaskAttachmentService getTaskAttachmentService() {
        return new TaskAttachmentServiceImpl(persistenceContext);
    }

    public TaskIdentityService getTaskIdentityService() {
        return new TaskIdentityServiceImpl(persistenceContext);
    }

    public TaskAdminService getTaskAdminService() {
        return new TaskAdminServiceImpl(persistenceContext);
    }

    public TaskDeadlinesService getTaskDeadlinesService() {
        return new TaskDeadlinesServiceImpl(persistenceContext);
    }

    public TaskRuleService getTaskRuleService() {
        return new TaskRuleServiceImpl(RuleContextProviderImpl.get());
    }

    public TaskPersistenceContext getPersistenceContext() {
        if ((persistenceContext) == null) {
            throw new IllegalStateException("No task persistence context available");
        }
        return persistenceContext;
    }

    public void setPersistenceContext(TaskPersistenceContext persistenceContext) {
        this.persistenceContext = persistenceContext;
    }

    public Object get(String string) {
        if (string.startsWith("local:")) {
            return delegate.get(string);
        }
        return this.environment.get(string);
    }

    public void set(String string, Object o) {
        if (string.startsWith("local:")) {
            delegate.set(string, o);
            return ;
        }
        if ((this.environment.get(string)) != null) {
            throw new IllegalArgumentException(("Cannot override value for property " + string));
        }
        // set String{string} to Environment{this.environment}
        this.environment.set(string, o);
    }

    @Override
    public UserGroupCallback getUserGroupCallback() {
        return ((UserGroupCallback) (get(TASK_USER_GROUP_CALLBACK)));
    }

    private LifeCycleManager getMvelLifeCycleManager() {
        return new MVELLifeCycleManager(this, persistenceContext, getTaskContentService(), taskEventSupport);
    }

    public TaskEventSupport getTaskEventSupport() {
        return this.taskEventSupport;
    }

    /* currently not used methods */
    public World getContextManager() {
        throw new UnsupportedOperationException("Not supported for this type of context.");
    }

    public String getName() {
        throw new UnsupportedOperationException("Not supported for this type of context.");
    }

    public void remove(String string) {
        throw new UnsupportedOperationException("Not supported for this type of context.");
    }

    @Override
    public Task loadTaskVariables(Task task) {
        return getTaskContentService().loadTaskVariables(task);
    }
}

