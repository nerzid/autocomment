/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package org.jbpm.services.task.impl;

import java.util.ArrayList;
import org.jbpm.services.task.utils.ClassUtil;
import org.kie.api.task.model.Content;
import org.kie.internal.task.api.model.ContentData;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import java.util.Date;
import org.kie.api.runtime.Environment;
import org.kie.internal.task.api.model.FaultData;
import java.util.HashMap;
import org.kie.api.task.model.I18NText;
import org.kie.internal.task.api.model.InternalContent;
import org.kie.internal.task.api.model.InternalI18NText;
import org.kie.internal.task.api.model.InternalPeopleAssignments;
import org.kie.internal.task.api.model.InternalTask;
import org.kie.internal.task.api.model.InternalTaskData;
import org.jbpm.services.task.internals.lifecycle.LifeCycleManager;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.drools.core.util.MVELSafeHelper;
import java.util.Map;
import java.util.regex.Matcher;
import org.kie.api.task.model.OrganizationalEntity;
import java.util.regex.Pattern;
import org.kie.api.task.model.Status;
import org.kie.internal.task.api.model.SubTasksStrategy;
import org.kie.api.task.model.Task;
import org.jbpm.services.task.commands.TaskCommand;
import org.jbpm.services.task.commands.TaskContext;
import org.jbpm.services.task.events.TaskEventSupport;
import org.kie.internal.task.api.TaskInstanceService;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.api.task.model.TaskSummary;
import org.kie.api.task.model.User;

/**
 */
public class TaskInstanceServiceImpl implements TaskInstanceService {
    private static final Logger logger = LoggerFactory.getLogger(TaskInstanceServiceImpl.class);

    protected static final Pattern PARAMETER_MATCHER = Pattern.compile("\\$\\{([\\S&&[^\\}]]+)\\}", Pattern.DOTALL);

    private LifeCycleManager lifeCycleManager;

    private TaskContext context;

    private TaskPersistenceContext persistenceContext;

    private TaskEventSupport taskEventSupport;

    private Environment environment;

    public TaskInstanceServiceImpl() {
    }

    public TaskInstanceServiceImpl(TaskContext context, TaskPersistenceContext persistenceContext, LifeCycleManager lifeCycleManager, TaskEventSupport taskEventSupport, Environment environment) {
        TaskInstanceServiceImpl.this.context = context;
        TaskInstanceServiceImpl.this.persistenceContext = persistenceContext;
        TaskInstanceServiceImpl.this.lifeCycleManager = lifeCycleManager;
        TaskInstanceServiceImpl.this.taskEventSupport = taskEventSupport;
        TaskInstanceServiceImpl.this.environment = environment;
    }

    public void setLifeCycleManager(LifeCycleManager lifeCycleManager) {
        TaskInstanceServiceImpl.this.lifeCycleManager = lifeCycleManager;
    }

    public void setTaskEventSupport(TaskEventSupport taskEventSupport) {
        TaskInstanceServiceImpl.this.taskEventSupport = taskEventSupport;
    }

    public void setPersistenceContext(TaskPersistenceContext persistenceContext) {
        TaskInstanceServiceImpl.this.persistenceContext = persistenceContext;
    }

    public long addTask(Task task, Map<String, Object> params) {
        taskEventSupport.fireBeforeTaskAdded(task, context);
        persistenceContext.persistTask(task);
        resolveTaskDetailsForTaskProperties(task);
        if (params != null) {
            resolveTaskDetails(params, task);
            ContentData contentData = ContentMarshallerHelper.marshal(task, params, TaskContentRegistry.get().getMarshallerContext(task).getEnvironment());
            Content content = TaskModelProvider.getFactory().newContent();
            ((InternalContent) (content)).setContent(contentData.getContent());
            persistenceContext.persistContent(content);
            ((InternalTaskData) (task.getTaskData())).setDocument(content.getId(), contentData);
            taskEventSupport.fireAfterTaskInputVariablesChanged(task, context, params);
        } 
        taskEventSupport.fireAfterTaskAdded(task, context);
        return task.getId();
    }

    public long addTask(Task task, ContentData contentData) {
        taskEventSupport.fireBeforeTaskAdded(task, context);
        persistenceContext.persistTask(task);
        resolveTaskDetailsForTaskProperties(task);
        if (contentData != null) {
            Content content = TaskModelProvider.getFactory().newContent();
            ((InternalContent) (content)).setContent(contentData.getContent());
            persistenceContext.persistContent(content);
            ((InternalTaskData) (task.getTaskData())).setDocument(content.getId(), contentData);
        } 
        taskEventSupport.fireAfterTaskAdded(task, context);
        return task.getId();
    }

    public void activate(long taskId, String userId) {
        lifeCycleManager.taskOperation(Operation.Activate, taskId, userId, null, null, toGroups(null));
    }

    public void claim(long taskId, String userId) {
        lifeCycleManager.taskOperation(Operation.Claim, taskId, userId, null, null, toGroups(null));
    }

    public void claim(long taskId, String userId, List<String> groupIds) {
        lifeCycleManager.taskOperation(Operation.Claim, taskId, userId, null, null, groupIds);
    }

    public void claimNextAvailable(String userId) {
        List<Status> status = new ArrayList<Status>();
        status.add(Status.Ready);
        List<TaskSummary> queryTasks = persistenceContext.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerByStatus", persistenceContext.addParametersToMap("userId", userId, "status", status), ClassUtil.<List<TaskSummary>>castClass(List.class));
        if ((queryTasks.size()) > 0) {
            lifeCycleManager.taskOperation(Operation.Claim, queryTasks.get(0).getId(), userId, null, null, toGroups(null));
        } else {
            TaskInstanceServiceImpl.logger.info("No task available to assign for user {}", userId);
        }
    }

    public void claimNextAvailable(String userId, List<String> groupIds) {
        List<Status> status = new ArrayList<Status>();
        status.add(Status.Ready);
        List<TaskSummary> queryTasks = persistenceContext.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerByStatusByGroup", persistenceContext.addParametersToMap("userId", userId, "status", status, "groupIds", groupIds), ClassUtil.<List<TaskSummary>>castClass(List.class));
        if ((queryTasks.size()) > 0) {
            lifeCycleManager.taskOperation(Operation.Claim, queryTasks.get(0).getId(), userId, null, null, groupIds);
        } else {
            TaskInstanceServiceImpl.logger.info("No task available to assign for user {} and groups {}", userId, groupIds);
        }
    }

    public void complete(long taskId, String userId, Map<String, Object> data) {
        lifeCycleManager.taskOperation(Operation.Complete, taskId, userId, null, data, toGroups(null));
    }

    public void delegate(long taskId, String userId, String targetUserId) {
        lifeCycleManager.taskOperation(Operation.Delegate, taskId, userId, targetUserId, null, toGroups(null));
    }

    public void deleteFault(long taskId, String userId) {
        Task task = persistenceContext.findTask(taskId);
        long contentId = task.getTaskData().getFaultContentId();
        Content content = persistenceContext.findContent(contentId);
        FaultData data = TaskModelProvider.getFactory().newFaultData();
        persistenceContext.removeContent(content);
        ((InternalTaskData) (task.getTaskData())).setFault(0, data);
    }

    public void deleteOutput(long taskId, String userId) {
        Task task = persistenceContext.findTask(taskId);
        long contentId = task.getTaskData().getOutputContentId();
        Content content = persistenceContext.findContent(contentId);
        ContentData data = TaskModelProvider.getFactory().newContentData();
        persistenceContext.removeContent(content);
        ((InternalTaskData) (task.getTaskData())).setOutput(0, data);
        taskEventSupport.fireAfterTaskOutputVariablesChanged(task, context, null);
    }

    public void exit(long taskId, String userId) {
        lifeCycleManager.taskOperation(Operation.Exit, taskId, userId, null, null, toGroups(null));
    }

    public void fail(long taskId, String userId, Map<String, Object> faultData) {
        lifeCycleManager.taskOperation(Operation.Fail, taskId, userId, null, faultData, toGroups(null));
    }

    public void forward(long taskId, String userId, String targetEntityId) {
        lifeCycleManager.taskOperation(Operation.Forward, taskId, userId, targetEntityId, null, toGroups(null));
    }

    public void release(long taskId, String userId) {
        lifeCycleManager.taskOperation(Operation.Release, taskId, userId, null, null, toGroups(null));
    }

    public void remove(long taskId, String userId) {
        Task task = persistenceContext.findTask(taskId);
        User user = persistenceContext.findUser(userId);
        if (((InternalPeopleAssignments) (task.getPeopleAssignments())).getRecipients().contains(user)) {
            ((InternalPeopleAssignments) (task.getPeopleAssignments())).getRecipients().remove(user);
        } else {
            throw new RuntimeException((("Couldn't remove user " + userId) + " since it isn't a notification recipient"));
        }
    }

    public void resume(long taskId, String userId) {
        lifeCycleManager.taskOperation(Operation.Resume, taskId, userId, null, null, toGroups(null));
    }

    public void setFault(long taskId, String userId, FaultData fault) {
        Task task = persistenceContext.findTask(taskId);
        Content content = TaskModelProvider.getFactory().newContent();
        ((InternalContent) (content)).setContent(fault.getContent());
        persistenceContext.persistContent(content);
        ((InternalTaskData) (task.getTaskData())).setFault(content.getId(), fault);
    }

    public void setOutput(long taskId, String userId, Object outputContentData) {
        Task task = persistenceContext.findTask(taskId);
        ContentData contentData = ContentMarshallerHelper.marshal(task, outputContentData, environment);
        Content content = TaskModelProvider.getFactory().newContent();
        ((InternalContent) (content)).setContent(contentData.getContent());
        persistenceContext.persistContent(content);
        ((InternalTaskData) (task.getTaskData())).setOutput(content.getId(), contentData);
    }

    public void setPriority(long taskId, int priority) {
        Task task = persistenceContext.findTask(taskId);
        taskEventSupport.fireBeforeTaskUpdated(task, context);
        ((InternalTask) (task)).setPriority(priority);
        taskEventSupport.fireAfterTaskUpdated(task, context);
    }

    public void setTaskNames(long taskId, List<I18NText> inputTaskNames) {
        Task task = persistenceContext.findTask(taskId);
        taskEventSupport.fireBeforeTaskUpdated(task, context);
        List<I18NText> taskNames = new ArrayList<I18NText>(inputTaskNames.size());
        for (I18NText inputText : inputTaskNames) {
            I18NText text = TaskModelProvider.getFactory().newI18NText();
            ((InternalI18NText) (text)).setLanguage(inputText.getLanguage());
            ((InternalI18NText) (text)).setText(inputText.getText());
            taskNames.add(text);
        }
        ((InternalTask) (task)).setNames(taskNames);
        ((InternalTask) (task)).setName(taskNames.get(0).getText());
        taskEventSupport.fireAfterTaskUpdated(task, context);
    }

    public void skip(long taskId, String userId) {
        lifeCycleManager.taskOperation(Operation.Skip, taskId, userId, null, null, toGroups(null));
    }

    public void start(long taskId, String userId) {
        lifeCycleManager.taskOperation(Operation.Start, taskId, userId, null, null, toGroups(null));
    }

    public void stop(long taskId, String userId) {
        lifeCycleManager.taskOperation(Operation.Stop, taskId, userId, null, null, toGroups(null));
    }

    public void suspend(long taskId, String userId) {
        lifeCycleManager.taskOperation(Operation.Suspend, taskId, userId, null, null, toGroups(null));
    }

    public void nominate(long taskId, String userId, List<OrganizationalEntity> potentialOwners) {
        lifeCycleManager.taskOperation(Operation.Nominate, taskId, userId, null, null, toGroups(null), potentialOwners.toArray(new OrganizationalEntity[potentialOwners.size()]));
    }

    public void setSubTaskStrategy(long taskId, SubTasksStrategy strategy) {
        Task task = persistenceContext.findTask(taskId);
        ((InternalTask) (task)).setSubTaskStrategy(strategy);
    }

    public void setExpirationDate(long taskId, Date date) {
        Task task = persistenceContext.findTask(taskId);
        taskEventSupport.fireBeforeTaskUpdated(task, context);
        ((InternalTaskData) (task.getTaskData())).setExpirationTime(date);
        taskEventSupport.fireAfterTaskUpdated(task, context);
    }

    public void setDescriptions(long taskId, List<I18NText> inputDescriptions) {
        Task task = persistenceContext.findTask(taskId);
        taskEventSupport.fireBeforeTaskUpdated(task, context);
        List<I18NText> descriptions = new ArrayList<I18NText>(inputDescriptions.size());
        for (I18NText inputText : inputDescriptions) {
            I18NText text = TaskModelProvider.getFactory().newI18NText();
            ((InternalI18NText) (text)).setLanguage(inputText.getLanguage());
            ((InternalI18NText) (text)).setText(inputText.getText());
            descriptions.add(text);
        }
        ((InternalTask) (task)).setDescriptions(descriptions);
        ((InternalTask) (task)).setDescription(descriptions.get(0).getText());
        taskEventSupport.fireAfterTaskUpdated(task, context);
    }

    public void setSkipable(long taskId, boolean skipable) {
        Task task = persistenceContext.findTask(taskId);
        ((InternalTaskData) (task.getTaskData())).setSkipable(skipable);
    }

    public int getPriority(long taskId) {
        Task task = persistenceContext.findTask(taskId);
        return task.getPriority();
    }

    public Date getExpirationDate(long taskId) {
        Task task = persistenceContext.findTask(taskId);
        return task.getTaskData().getExpirationTime();
    }

    public List<I18NText> getDescriptions(long taskId) {
        Task task = persistenceContext.findTask(taskId);
        return ((List<I18NText>) (task.getDescriptions()));
    }

    public boolean isSkipable(long taskId) {
        Task task = persistenceContext.findTask(taskId);
        return task.getTaskData().isSkipable();
    }

    public SubTasksStrategy getSubTaskStrategy(long taskId) {
        Task task = persistenceContext.findTask(taskId);
        return ((InternalTask) (task)).getSubTaskStrategy();
    }

    @SuppressWarnings(value = { "unchecked" , "rawtypes" })
    public <T> T execute(Command<T> command) {
        return ((T) (((TaskCommand) (command)).execute(new TaskContext())));
    }

    @Override
    public void setName(long taskId, String name) {
        Task task = persistenceContext.findTask(taskId);
        ((InternalTask) (task)).setName(name);
    }

    @Override
    public void setDescription(long taskId, String description) {
        Task task = persistenceContext.findTask(taskId);
        ((InternalTask) (task)).setDescription(description);
    }

    @Override
    public void setSubject(long taskId, String subject) {
        Task task = persistenceContext.findTask(taskId);
        ((InternalTask) (task)).setSubject(subject);
    }

    @Override
    public long addOutputContentFromUser(long taskId, String userId, Map<String, Object> params) {
        // check permissions
        TaskInstanceServiceImpl.this.lifeCycleManager.taskOperation(Operation.Modify, taskId, userId, null, null, toGroups(null));
        return new TaskContentServiceImpl(context, TaskInstanceServiceImpl.this.persistenceContext, taskEventSupport).addOutputContent(taskId, params);
    }

    @Override
    public Content getContentByIdForUser(long contentId, String userId) {
        long taskId = persistenceContext.findTaskIdByContentId(contentId);
        // check permissions
        TaskInstanceServiceImpl.this.lifeCycleManager.taskOperation(Operation.View, taskId, userId, null, null, toGroups(null));
        return TaskInstanceServiceImpl.this.persistenceContext.findContent(contentId);
    }

    @Override
    public Map<String, Object> getContentMapForUser(Long taskId, String userId) {
        // check permissions
        TaskInstanceServiceImpl.this.lifeCycleManager.taskOperation(Operation.View, taskId, userId, null, null, toGroups(null));
        Task task = TaskInstanceServiceImpl.this.persistenceContext.findTask(taskId);
        if (((task.getTaskData()) != null) && ((task.getTaskData().getOutputContentId()) != null)) {
            Content content = TaskInstanceServiceImpl.this.persistenceContext.findContent(task.getTaskData().getOutputContentId());
            ContentMarshallerContext mContext = TaskContentRegistry.get().getMarshallerContext(task);
            Object outputContent = ContentMarshallerHelper.unmarshall(content.getContent(), mContext.getEnvironment(), mContext.getClassloader());
            if (outputContent instanceof Map) {
                return ((Map<String, Object>) (outputContent));
            } else {
                throw new IllegalStateException((("Output content for task " + taskId) + " is not a Map<String, Object>!"));
            }
        } 
        return null;
    }

    @SuppressWarnings(value = "unchecked")
    protected List<String> toGroups(List<String> groups) {
        if (groups == null) {
            return ((List<String>) (context.get("local:groups")));
        } 
        return groups;
    }

    protected Map<String, Object> resolveTaskDetails(Map<String, Object> parameters, Task task) {
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            if (((entry.getValue()) != null) && ((entry.getValue()) instanceof String)) {
                String s = ((String) (entry.getValue()));
                Map<String, String> replacements = new HashMap<String, String>();
                Matcher matcher = TaskInstanceServiceImpl.PARAMETER_MATCHER.matcher(s);
                while (matcher.find()) {
                    String paramName = matcher.group(1);
                    if ((replacements.get(paramName)) == null) {
                        try {
                            Object variableValue = MVELSafeHelper.getEvaluator().eval(paramName, new TaskResolverFactory(task));
                            String variableValueString = variableValue == null ? "" : variableValue.toString();
                            replacements.put(paramName, variableValueString);
                        } catch (Throwable t) {
                            TaskInstanceServiceImpl.logger.error("Continuing without setting parameter.");
                        }
                    } 
                }
                for (Map.Entry<String, String> replacement : replacements.entrySet()) {
                    s = s.replace((("${" + (replacement.getKey())) + "}"), replacement.getValue());
                }
                parameters.put(entry.getKey(), s);
            } 
        }
        return parameters;
    }

    protected void resolveTaskDetailsForTaskProperties(Task task) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("name", task.getName());
        parameters.put("description", task.getDescription());
        parameters.put("subject", task.getSubject());
        parameters.put("formName", ((InternalTask) (task)).getFormName());
        Map<String, Object> replacements = resolveTaskDetails(parameters, task);
        ((InternalTask) (task)).setName(((String) (replacements.get("name"))));
        ((InternalTask) (task)).setDescription(((String) (replacements.get("description"))));
        ((InternalTask) (task)).setSubject(((String) (replacements.get("subject"))));
        ((InternalTask) (task)).setFormName(((String) (replacements.get("formName"))));
    }
}

