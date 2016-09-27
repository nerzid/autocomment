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

import org.kie.internal.command.Context;
import org.kie.api.runtime.Environment;
import org.jbpm.services.task.internals.lifecycle.LifeCycleManager;
import org.jbpm.services.task.rule.impl.RuleContextProviderImpl;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.TaskAdminService;
import org.kie.internal.task.api.TaskAttachmentService;
import org.kie.internal.task.api.TaskCommentService;
import org.kie.internal.task.api.TaskContentService;
import org.kie.internal.task.api.TaskDeadlinesService;
import org.kie.internal.task.api.TaskDefService;
import org.jbpm.services.task.events.TaskEventSupport;
import org.kie.internal.task.api.TaskIdentityService;
import org.kie.internal.task.api.TaskInstanceService;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.internal.task.api.TaskQueryService;
import org.jbpm.services.task.rule.TaskRuleService;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.command.World;

/**
 */
public class TaskContext implements org.kie.internal.task.api.TaskContext {
    private TaskPersistenceContext persistenceContext;

    private Environment environment;

    private TaskEventSupport taskEventSupport;

    private org.kie.internal.task.api.TaskContext delegate;

    public TaskContext() {
    }

    public TaskContext(Context context, Environment environment, TaskEventSupport taskEventSupport) {
        if (context instanceof org.kie) {
            TaskContext.this.delegate = ((org.kie.internal.task.api.TaskContext) (context));
            TaskContext.this.persistenceContext = ((org.kie.internal.task.api.TaskContext) (context)).getPersistenceContext();
        } 
        TaskContext.this.environment = environment;
        TaskContext.this.taskEventSupport = taskEventSupport;
    }

    public TaskInstanceService getTaskInstanceService() {
        return new org.jbpm.services.task.impl.TaskInstanceServiceImpl(TaskContext.this, persistenceContext, getMvelLifeCycleManager(), taskEventSupport, environment);
    }

    public TaskDefService getTaskDefService() {
        return new org.jbpm.services.task.impl.TaskDefServiceImpl(persistenceContext);
    }

    public TaskQueryService getTaskQueryService() {
        return new org.jbpm.services.task.impl.TaskQueryServiceImpl(persistenceContext, getUserGroupCallback());
    }

    public TaskContentService getTaskContentService() {
        return new org.jbpm.services.task.impl.TaskContentServiceImpl(TaskContext.this, persistenceContext, taskEventSupport);
    }

    public TaskCommentService getTaskCommentService() {
        return new org.jbpm.services.task.impl.TaskCommentServiceImpl(persistenceContext);
    }

    public TaskAttachmentService getTaskAttachmentService() {
        return new org.jbpm.services.task.impl.TaskAttachmentServiceImpl(persistenceContext);
    }

    public TaskIdentityService getTaskIdentityService() {
        return new org.jbpm.services.task.impl.TaskIdentityServiceImpl(persistenceContext);
    }

    public TaskAdminService getTaskAdminService() {
        return new org.jbpm.services.task.impl.TaskAdminServiceImpl(persistenceContext);
    }

    public TaskDeadlinesService getTaskDeadlinesService() {
        return new org.jbpm.services.task.impl.TaskDeadlinesServiceImpl(persistenceContext);
    }

    public TaskRuleService getTaskRuleService() {
        return new org.jbpm.services.task.rule.impl.TaskRuleServiceImpl(RuleContextProviderImpl.get());
    }

    public TaskPersistenceContext getPersistenceContext() {
        if ((persistenceContext) == null) {
            throw new IllegalStateException("No task persistence context available");
        } 
        return persistenceContext;
    }

    public void setPersistenceContext(TaskPersistenceContext persistenceContext) {
        TaskContext.this.persistenceContext = persistenceContext;
    }

    public Object get(String string) {
        if (string.startsWith("local:")) {
            return delegate.get(string);
        } 
        return TaskContext.this.environment.get(string);
    }

    public void set(String string, Object o) {
        if (string.startsWith("local:")) {
            delegate.set(string, o);
            return ;
        } 
        if ((TaskContext.this.environment.get(string)) != null) {
            throw new IllegalArgumentException(("Cannot override value for property " + string));
        } 
        TaskContext.this.environment.set(string, o);
    }

    @Override
    public UserGroupCallback getUserGroupCallback() {
        return ((UserGroupCallback) (get(EnvironmentName.TASK_USER_GROUP_CALLBACK)));
    }

    private LifeCycleManager getMvelLifeCycleManager() {
        return new org.jbpm.services.task.internals.lifecycle.MVELLifeCycleManager(TaskContext.this, persistenceContext, getTaskContentService(), taskEventSupport);
    }

    public TaskEventSupport getTaskEventSupport() {
        return TaskContext.this.taskEventSupport;
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

