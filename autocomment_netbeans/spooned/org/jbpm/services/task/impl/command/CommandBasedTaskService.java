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


package org.jbpm.services.task.impl.command;

import org.jbpm.services.task.commands.AddContentFromUserCommand;
import org.kie.api.task.model.Attachment;
import java.util.Collections;
import org.drools.core.command.CommandService;
import org.kie.api.task.model.Comment;
import org.kie.api.task.model.Content;
import org.kie.internal.task.api.model.ContentData;
import org.kie.internal.task.api.ContentMarshallerContext;
import java.util.Date;
import org.kie.internal.task.api.EventService;
import org.kie.internal.task.api.model.FaultData;
import org.jbpm.services.task.commands.GetContentByIdForUserCommand;
import org.jbpm.services.task.commands.GetTasksByVariousFieldsCommand;
import org.kie.api.task.model.Group;
import java.util.HashMap;
import org.kie.api.task.model.I18NText;
import org.kie.internal.task.api.InternalTaskService;
import java.util.List;
import java.util.Map;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.internal.query.QueryFilter;
import org.jbpm.services.task.commands.SetTaskPropertyCommand;
import org.kie.api.task.model.Status;
import org.kie.internal.task.api.model.SubTasksStrategy;
import org.kie.api.task.model.Task;
import org.jbpm.services.task.impl.TaskContentRegistry;
import org.kie.internal.task.api.model.TaskDef;
import org.kie.internal.task.api.model.TaskEvent;
import org.jbpm.services.task.events.TaskEventSupport;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.task.query.TaskSummaryQueryBuilder;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.UserInfo;

public class CommandBasedTaskService implements EventService<TaskLifeCycleEventListener> , InternalTaskService {
    private CommandService executor;

    private TaskEventSupport taskEventSupport;

    private QueryFilter addLanguageFilter(String language) {
        if (language == null) {
            return null;
        } 
        QueryFilter filter = new QueryFilter();
        filter.setCount(null);
        filter.setOffset(null);
        filter.setLanguage(language);
        return filter;
    }

    public CommandBasedTaskService(CommandService executor, TaskEventSupport taskEventSupport) {
        CommandBasedTaskService.this.executor = executor;
        CommandBasedTaskService.this.taskEventSupport = taskEventSupport;
    }

    @Override
    public <T> T execute(Command<T> command) {
        return executor.execute(command);
    }

    public void activate(long taskId, String userId) {
        executor.execute(new org.jbpm.services.task.commands.ActivateTaskCommand(taskId, userId));
    }

    public void claim(long taskId, String userId) {
        executor.execute(new org.jbpm.services.task.commands.ClaimTaskCommand(taskId, userId));
    }

    // TODO: does not filter on language
    public void claimNextAvailable(String userId, String language) {
        executor.execute(new org.jbpm.services.task.commands.ClaimNextAvailableTaskCommand(userId));
    }

    public void complete(long taskId, String userId, Map<String, Object> data) {
        executor.execute(new org.jbpm.services.task.commands.CompositeCommand<java.lang.Void>(new org.jbpm.services.task.commands.CompleteTaskCommand(taskId, userId, data), new org.jbpm.services.task.commands.ProcessSubTaskCommand(taskId, userId, data), new org.jbpm.services.task.commands.CancelDeadlineCommand(taskId, true, true)));
    }

    public void delegate(long taskId, String userId, String targetUserId) {
        executor.execute(new org.jbpm.services.task.commands.DelegateTaskCommand(taskId, userId, targetUserId));
    }

    public void exit(long taskId, String userId) {
        executor.execute(new org.jbpm.services.task.commands.CompositeCommand<java.lang.Void>(new org.jbpm.services.task.commands.ExitTaskCommand(taskId, userId), new org.jbpm.services.task.commands.CancelDeadlineCommand(taskId, true, true)));
    }

    public void fail(long taskId, String userId, Map<String, Object> faultData) {
        executor.execute(new org.jbpm.services.task.commands.CompositeCommand<java.lang.Void>(new org.jbpm.services.task.commands.FailTaskCommand(taskId, userId, faultData), new org.jbpm.services.task.commands.CancelDeadlineCommand(taskId, true, true)));
    }

    public void forward(long taskId, String userId, String targetEntityId) {
        executor.execute(new org.jbpm.services.task.commands.ForwardTaskCommand(taskId, userId, targetEntityId));
    }

    public Task getTaskByWorkItemId(long workItemId) {
        return executor.execute(new org.jbpm.services.task.commands.GetTaskByWorkItemIdCommand(workItemId));
    }

    public Task getTaskById(long taskId) {
        return executor.execute(new org.jbpm.services.task.commands.GetTaskCommand(taskId));
    }

    // TODO: does not filter on language
    public List<TaskSummary> getTasksAssignedAsBusinessAdministrator(String userId, String language) {
        return executor.execute(new org.jbpm.services.task.commands.GetTaskAssignedAsBusinessAdminCommand(userId));
    }

    // TODO: does not filter on language
    public List<TaskSummary> getTasksAssignedAsBusinessAdministratorByStatus(String userId, String language, List<Status> statuses) {
        return executor.execute(new org.jbpm.services.task.commands.GetTaskAssignedAsBusinessAdminCommand(userId, statuses));
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, String language) {
        QueryFilter filter = addLanguageFilter(language);
        return getTasksAssignedAsPotentialOwner(userId, null, null, filter);
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds) {
        return getTasksAssignedAsPotentialOwner(userId, groupIds, null, null);
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, List<Status> status, QueryFilter filter) {
        return executor.execute(new org.jbpm.services.task.commands.GetTaskAssignedAsPotentialOwnerCommand(userId, groupIds, status, filter));
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatus(String userId, List<Status> status, String language) {
        QueryFilter filter = addLanguageFilter(language);
        return getTasksAssignedAsPotentialOwner(userId, null, status, filter);
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDate(String userId, List<Status> statuses, Date expirationDate) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("expirationDate", expirationDate);
        return getTasksAssignedAsPotentialOwner(userId, null, statuses, new QueryFilter("t.taskData.expirationTime = :expirationDate", params, "t.id", false));
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDateOptional(String userId, List<Status> statuses, Date expirationDate) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("expirationDate", expirationDate);
        return getTasksAssignedAsPotentialOwner(userId, null, statuses, new QueryFilter("(t.taskData.expirationTime = :expirationDate or t.taskData.expirationTime is null)", params, "t.id", false));
    }

    // TODO: does not filter on language
    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language, int firstResult, int maxResults) {
        return getTasksAssignedAsPotentialOwner(userId, groupIds, null, new QueryFilter(firstResult, maxResults));
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatusByGroup(String userId, List<String> groupIds, List<Status> status) {
        return getTasksAssignedAsPotentialOwner(userId, groupIds, status, null);
    }

    public List<TaskSummary> getTasksOwned(String userId, List<Status> status, QueryFilter filter) {
        return executor.execute(new org.jbpm.services.task.commands.GetTasksOwnedCommand(userId, status, filter));
    }

    public List<TaskSummary> getTasksOwned(String userId, String language) {
        QueryFilter filter = addLanguageFilter(language);
        return getTasksOwned(userId, null, filter);
    }

    public List<TaskSummary> getTasksOwnedByStatus(String userId, List<Status> status, String language) {
        QueryFilter filter = addLanguageFilter(language);
        return getTasksOwned(userId, status, filter);
    }

    // TODO: does not filter on language
    public List<TaskSummary> getTasksByStatusByProcessInstanceId(long processInstanceId, List<Status> status, String language) {
        return executor.execute(new org.jbpm.services.task.commands.GetTasksByStatusByProcessInstanceIdCommand(processInstanceId, status));
    }

    public List<Long> getTasksByProcessInstanceId(long processInstanceId) {
        return executor.execute(new org.jbpm.services.task.commands.GetTasksByProcessInstanceIdCommand(processInstanceId));
    }

    /**
     * This method should be deleted in jBPM 7.x
     *  @see {@link CommandBasedTaskService#fluentTaskQuery}
     */
    @Override
    @Deprecated
    public List<TaskSummary> getTasksByVariousFields(String userId, List<Long> workItemIds, List<Long> taskIds, List<Long> procInstIds, List<String> busAdmins, List<String> potOwners, List<String> taskOwners, List<Status> statuses, List<String> languages, boolean union) {
        GetTasksByVariousFieldsCommand cmd = new GetTasksByVariousFieldsCommand(workItemIds, taskIds, procInstIds, busAdmins, potOwners, taskOwners, statuses, union);
        cmd.setUserId(userId);
        return executor.execute(cmd);
    }

    /**
     * This method should be deleted in jBPM 7.x
     *  @see {@link CommandBasedTaskService#fluentTaskQuery}
     */
    @Override
    @Deprecated
    public List<TaskSummary> getTasksByVariousFields(String userId, Map<String, List<?>> parameters, boolean union) {
        GetTasksByVariousFieldsCommand cmd = new GetTasksByVariousFieldsCommand(parameters, union);
        cmd.setUserId(userId);
        return executor.execute(cmd);
    }

    @Override
    public TaskSummaryQueryBuilder taskSummaryQuery(String userId) {
        return new org.jbpm.services.task.impl.TaskSummaryQueryBuilderImpl(userId, CommandBasedTaskService.this);
    }

    public long addTask(Task task, Map<String, Object> params) {
        return executor.execute(new org.jbpm.services.task.commands.AddTaskCommand(task, params));
    }

    public void release(long taskId, String userId) {
        executor.execute(new org.jbpm.services.task.commands.ReleaseTaskCommand(taskId, userId));
    }

    public void resume(long taskId, String userId) {
        executor.execute(new org.jbpm.services.task.commands.ResumeTaskCommand(taskId, userId));
    }

    public void skip(long taskId, String userId) {
        executor.execute(new org.jbpm.services.task.commands.CompositeCommand<java.lang.Void>(new org.jbpm.services.task.commands.SkipTaskCommand(taskId, userId), new org.jbpm.services.task.commands.ProcessSubTaskCommand(taskId, userId), new org.jbpm.services.task.commands.CancelDeadlineCommand(taskId, true, true)));
    }

    public void start(long taskId, String userId) {
        executor.execute(new org.jbpm.services.task.commands.CompositeCommand<java.lang.Void>(new org.jbpm.services.task.commands.StartTaskCommand(taskId, userId), new org.jbpm.services.task.commands.CancelDeadlineCommand(taskId, true, false)));
    }

    public void stop(long taskId, String userId) {
        executor.execute(new org.jbpm.services.task.commands.StopTaskCommand(taskId, userId));
    }

    public void suspend(long taskId, String userId) {
        executor.execute(new org.jbpm.services.task.commands.SuspendTaskCommand(taskId, userId));
    }

    public void nominate(long taskId, String userId, List<OrganizationalEntity> potentialOwners) {
        executor.execute(new org.jbpm.services.task.commands.NominateTaskCommand(taskId, userId, potentialOwners));
    }

    public Content getContentById(long contentId) {
        return executor.execute(new org.jbpm.services.task.commands.GetContentByIdCommand(contentId));
    }

    public Attachment getAttachmentById(long attachId) {
        return executor.execute(new org.jbpm.services.task.commands.GetAttachmentCommand(attachId));
    }

    @Override
    public void addGroup(Group group) {
        executor.execute(new org.jbpm.services.task.commands.AddGroupCommand(group.getId()));
    }

    @Override
    public void addUser(User user) {
        executor.execute(new org.jbpm.services.task.commands.AddUserCommand(user.getId()));
    }

    @Override
    public int archiveTasks(List<TaskSummary> tasks) {
        return executor.execute(new org.jbpm.services.task.commands.ArchiveTasksCommand(tasks));
    }

    // TODO: groupIds argument is not processed!
    @Override
    public void claim(long taskId, String userId, List<String> groupIds) {
        executor.execute(new org.jbpm.services.task.commands.ClaimTaskCommand(taskId, userId));
    }

    // TODO: groupIds argument is not processed!
    @Override
    public void claimNextAvailable(String userId, List<String> groupIds) {
        executor.execute(new org.jbpm.services.task.commands.ClaimNextAvailableTaskCommand(userId));
    }

    @Override
    public void deleteFault(long taskId, String userId) {
        executor.execute(new org.jbpm.services.task.commands.DeleteFaultCommand(taskId, userId));
    }

    @Override
    public void deleteOutput(long taskId, String userId) {
        executor.execute(new org.jbpm.services.task.commands.DeleteOutputCommand(taskId, userId));
    }

    @Override
    public void deployTaskDef(TaskDef def) {
        CommandBasedTaskService.this.executor.execute(new org.jbpm.services.task.commands.DeployTaskDefCommand(def));
    }

    @Override
    public List<TaskSummary> getActiveTasks() {
        return executor.execute(new org.jbpm.services.task.commands.GetActiveTasksCommand());
    }

    @Override
    public List<TaskSummary> getActiveTasks(Date since) {
        return executor.execute(new org.jbpm.services.task.commands.GetActiveTasksCommand(since));
    }

    @Override
    public List<TaskDef> getAllTaskDef(String filter) {
        return executor.execute(new org.jbpm.services.task.commands.GetAllTaskDefinitionsCommand(filter));
    }

    @Override
    public List<TaskSummary> getArchivedTasks() {
        return executor.execute(new org.jbpm.services.task.commands.GetArchivedTasksCommand());
    }

    @Override
    public List<TaskSummary> getCompletedTasks() {
        return executor.execute(new org.jbpm.services.task.commands.GetCompletedTasksCommand());
    }

    @Override
    public List<TaskSummary> getCompletedTasks(Date since) {
        return executor.execute(new org.jbpm.services.task.commands.GetCompletedTasksCommand(since));
    }

    @Override
    public List<TaskSummary> getCompletedTasksByProcessId(Long processId) {
        return executor.execute(new org.jbpm.services.task.commands.GetCompletedTasksCommand(processId));
    }

    @Override
    public Group getGroupById(String groupId) {
        return executor.execute(new org.jbpm.services.task.commands.GetGroupCommand(groupId));
    }

    @Override
    public List<Group> getGroups() {
        return executor.execute(new org.jbpm.services.task.commands.GetGroupsCommand());
    }

    @Override
    public List<TaskSummary> getSubTasksAssignedAsPotentialOwner(long parentId, String userId) {
        return executor.execute(new org.jbpm.services.task.commands.GetSubTasksCommand(parentId, userId));
    }

    @Override
    public List<TaskSummary> getSubTasksByParent(long parentId) {
        return executor.execute(new org.jbpm.services.task.commands.GetSubTasksCommand(parentId));
    }

    @Override
    public int getPendingSubTasksByParent(long parentId) {
        return executor.execute(new org.jbpm.services.task.commands.GetPendingSubTasksCommand(parentId));
    }

    @Override
    public TaskDef getTaskDefById(String id) {
        return executor.execute(new org.jbpm.services.task.commands.GetTaskDefinitionCommand(id));
    }

    @Override
    public List<TaskSummary> getTasksOwnedByExpirationDate(String userId, List<Status> statuses, Date expirationDate) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("expirationDate", expirationDate);
        return getTasksOwned(userId, statuses, new QueryFilter("t.taskData.expirationTime = :expirationDate", params, "t.id", false));
    }

    @Override
    public List<TaskSummary> getTasksOwnedByExpirationDateOptional(String userId, List<Status> statuses, Date expirationDate) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("expirationDate", expirationDate);
        return getTasksOwned(userId, statuses, new QueryFilter("(t.taskData.expirationTime = :expirationDate or t.taskData.expirationTime is null)", params, "t.id", false));
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsExcludedOwner(String userId) {
        return executor.execute(new org.jbpm.services.task.commands.GetTaskAssignedAsExcludedOwnerCommand(userId));
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsRecipient(String userId) {
        return executor.execute(new org.jbpm.services.task.commands.GetTaskAssignedAsRecipientCommand(userId));
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsTaskInitiator(String userId) {
        return executor.execute(new org.jbpm.services.task.commands.GetTaskAssignedAsInitiatorCommand(userId));
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsTaskStakeholder(String userId) {
        return executor.execute(new org.jbpm.services.task.commands.GetTaskAssignedAsStakeholderCommand(userId));
    }

    @Override
    public List<TaskSummary> getTasksOwnedByExpirationDateBeforeSpecifiedDate(String userId, List<Status> status, Date date) {
        return executor.execute(new org.jbpm.services.task.commands.GetTaskOwnedByExpDateBeforeDateCommand(userId, status, date));
    }

    @Override
    public List<TaskSummary> getTasksByStatusByProcessInstanceIdByTaskName(long processInstanceId, List<Status> status, String taskName) {
        return executor.execute(new org.jbpm.services.task.commands.GetTasksByStatusByProcessInstanceIdCommand(processInstanceId, status, taskName));
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByProcessId(String userId, String processId) {
        return CommandBasedTaskService.this.taskSummaryQuery(userId).intersect().potentialOwner(userId).processId(processId).build().getResultList();
    }

    @Override
    public Map<Long, List<OrganizationalEntity>> getPotentialOwnersForTaskIds(List<Long> taskIds) {
        return executor.execute(new org.jbpm.services.task.commands.GetPotentialOwnersForTaskCommand(taskIds));
    }

    @Override
    public User getUserById(String userId) {
        return executor.execute(new org.jbpm.services.task.commands.GetUserCommand(userId));
    }

    @Override
    public List<User> getUsers() {
        return executor.execute(new org.jbpm.services.task.commands.GetUsersCommand());
    }

    @Override
    public long addTask(Task task, ContentData data) {
        return executor.execute(new org.jbpm.services.task.commands.AddTaskCommand(task, data));
    }

    @Override
    public void remove(long taskId, String userId) {
        executor.execute(new org.jbpm.services.task.commands.RemoveTaskCommand(taskId, userId));
    }

    @Override
    public void removeGroup(String groupId) {
        executor.execute(new org.jbpm.services.task.commands.RemoveGroupCommand(groupId));
    }

    @Override
    public int removeTasks(List<TaskSummary> tasks) {
        return executor.execute(new org.jbpm.services.task.commands.RemoveTasksCommand(tasks));
    }

    @Override
    public void removeUser(String userId) {
        executor.execute(new org.jbpm.services.task.commands.RemoveUserCommand(userId));
    }

    @Override
    public void setFault(long taskId, String userId, FaultData fault) {
        executor.execute(new SetTaskPropertyCommand(taskId, userId, SetTaskPropertyCommand.FAULT_PROPERTY, fault));
    }

    @Override
    public void setOutput(long taskId, String userId, Object outputContentData) {
        executor.execute(new SetTaskPropertyCommand(taskId, userId, SetTaskPropertyCommand.OUTPUT_PROPERTY, outputContentData));
    }

    @Override
    public void setPriority(long taskId, int priority) {
        executor.execute(new SetTaskPropertyCommand(taskId, null, SetTaskPropertyCommand.PRIORITY_PROPERTY, priority));
    }

    @Override
    public void setTaskNames(long taskId, List<I18NText> taskNames) {
        executor.execute(new SetTaskPropertyCommand(taskId, null, SetTaskPropertyCommand.TASK_NAMES_PROPERTY, taskNames));
    }

    @Override
    public void undeployTaskDef(String id) {
        executor.execute(new org.jbpm.services.task.commands.UndeployTaskDefCommand(id));
    }

    @Override
    public UserInfo getUserInfo() {
        return executor.execute(new org.jbpm.services.task.commands.GetUserInfoCommand());
    }

    @Override
    public void setUserInfo(UserInfo userInfo) {
        throw new UnsupportedOperationException("Set UserInfo object on TaskService creation");
    }

    @Override
    public void addUsersAndGroups(Map<String, User> users, Map<String, Group> groups) {
        executor.execute(new org.jbpm.services.task.commands.AddUsersGroupsCommand(users, groups));
    }

    @Override
    public int removeAllTasks() {
        return executor.execute(new org.jbpm.services.task.commands.RemoveAllTasksCommand());
    }

    @Override
    public long addContent(long taskId, Content content) {
        return executor.execute(new org.jbpm.services.task.commands.AddContentCommand(taskId, content));
    }

    @Override
    public long addContent(long taskId, Map<String, Object> params) {
        return executor.execute(new org.jbpm.services.task.commands.AddContentCommand(taskId, params));
    }

    @Override
    public long setDocumentContentFromUser(long taskId, String userId, byte[] content) {
        AddContentFromUserCommand cmd = new AddContentFromUserCommand(taskId, userId);
        cmd.setDocumentContentBytes(content);
        return executor.execute(cmd);
    }

    @Override
    public long addOutputContentFromUser(long taskId, String userId, Map<String, Object> params) {
        AddContentFromUserCommand cmd = new AddContentFromUserCommand(taskId, userId);
        cmd.setOutputContentMap(params);
        return executor.execute(cmd);
    }

    @Override
    public void deleteContent(long taskId, long contentId) {
        executor.execute(new org.jbpm.services.task.commands.DeleteContentCommand(taskId, contentId));
    }

    @Override
    public List<Content> getAllContentByTaskId(long taskId) {
        return executor.execute(new org.jbpm.services.task.commands.GetAllContentCommand(taskId));
    }

    @Override
    public Content getContentByIdForUser(long contentId, String userId) {
        GetContentByIdForUserCommand cmd = new GetContentByIdForUserCommand(contentId);
        cmd.setContentId(contentId);
        cmd.setUserId(userId);
        return executor.execute(cmd);
    }

    @Override
    public Map<String, Object> getOutputContentMapForUser(long taskId, String userId) {
        return executor.execute(new org.jbpm.services.task.commands.GetContentMapForUserCommand(taskId, userId));
    }

    @Override
    public long addAttachment(long taskId, Attachment attachment, Content content) {
        return executor.execute(new org.jbpm.services.task.commands.AddAttachmentCommand(taskId, attachment, content));
    }

    @Override
    public void deleteAttachment(long taskId, long attachmentId) {
        executor.execute(new org.jbpm.services.task.commands.DeleteAttachmentCommand(taskId, attachmentId));
    }

    @Override
    public List<Attachment> getAllAttachmentsByTaskId(long taskId) {
        return executor.execute(new org.jbpm.services.task.commands.GetAllAttachmentsCommand(taskId));
    }

    @Override
    public OrganizationalEntity getOrganizationalEntityById(String entityId) {
        return executor.execute(new org.jbpm.services.task.commands.GetOrgEntityCommand(entityId));
    }

    @Override
    public void setExpirationDate(long taskId, Date date) {
        executor.execute(new SetTaskPropertyCommand(taskId, null, SetTaskPropertyCommand.EXPIRATION_DATE_PROPERTY, date));
    }

    @Override
    public void setDescriptions(long taskId, List<I18NText> descriptions) {
        executor.execute(new SetTaskPropertyCommand(taskId, null, SetTaskPropertyCommand.DESCRIPTION_PROPERTY, descriptions));
    }

    @Override
    public void setSkipable(long taskId, boolean skipable) {
        executor.execute(new SetTaskPropertyCommand(taskId, null, SetTaskPropertyCommand.SKIPPABLE_PROPERTY, skipable));
    }

    @Override
    public void setSubTaskStrategy(long taskId, SubTasksStrategy strategy) {
        executor.execute(new SetTaskPropertyCommand(taskId, null, SetTaskPropertyCommand.SUB_TASK_STRATEGY_PROPERTY, strategy));
    }

    @Override
    public int getPriority(long taskId) {
        return ((Integer) (executor.execute(new org.jbpm.services.task.commands.GetTaskPropertyCommand(taskId, null, SetTaskPropertyCommand.PRIORITY_PROPERTY))));
    }

    @Override
    public Date getExpirationDate(long taskId) {
        return ((Date) (executor.execute(new org.jbpm.services.task.commands.GetTaskPropertyCommand(taskId, null, SetTaskPropertyCommand.EXPIRATION_DATE_PROPERTY))));
    }

    @SuppressWarnings(value = "unchecked")
    @Override
    public List<I18NText> getDescriptions(long taskId) {
        return ((List<I18NText>) (executor.execute(new org.jbpm.services.task.commands.GetTaskPropertyCommand(taskId, null, SetTaskPropertyCommand.DESCRIPTION_PROPERTY))));
    }

    @Override
    public boolean isSkipable(long taskId) {
        return ((Boolean) (executor.execute(new org.jbpm.services.task.commands.GetTaskPropertyCommand(taskId, null, SetTaskPropertyCommand.SKIPPABLE_PROPERTY))));
    }

    @Override
    public SubTasksStrategy getSubTaskStrategy(long taskId) {
        return ((SubTasksStrategy) (executor.execute(new org.jbpm.services.task.commands.GetTaskPropertyCommand(taskId, null, SetTaskPropertyCommand.SUB_TASK_STRATEGY_PROPERTY))));
    }

    @Override
    public Task getTaskInstanceById(long taskId) {
        return executor.execute(new org.jbpm.services.task.commands.GetTaskCommand(taskId));
    }

    @Override
    public int getCompletedTaskByUserId(String userId) {
        return executor.execute(new org.jbpm.services.task.commands.GetCompletedTasksByUserCommand(userId));
    }

    @Override
    public int getPendingTaskByUserId(String userId) {
        return executor.execute(new org.jbpm.services.task.commands.GetPendingTasksByUserCommand(userId));
    }

    @Override
    public List<TaskSummary> getTasksAssignedByGroup(String groupId) {
        return executor.execute(new org.jbpm.services.task.commands.GetTaskAssignedByGroupsCommand(Collections.singletonList(groupId)));
    }

    @Override
    public List<TaskSummary> getTasksAssignedByGroups(List<String> groupIds) {
        return executor.execute(new org.jbpm.services.task.commands.GetTaskAssignedByGroupsCommand(groupIds));
    }

    @Override
    public Long addComment(long taskId, Comment comment) {
        return executor.execute(new org.jbpm.services.task.commands.AddCommentCommand(taskId, comment));
    }

    @Override
    public Long addComment(long taskId, String addedByUserId, String commentText) {
        return executor.execute(new org.jbpm.services.task.commands.AddCommentCommand(taskId, addedByUserId, commentText));
    }

    @Override
    public void deleteComment(long taskId, long commentId) {
        executor.execute(new org.jbpm.services.task.commands.DeleteCommentCommand(taskId, commentId));
    }

    @Override
    public List<Comment> getAllCommentsByTaskId(long taskId) {
        return executor.execute(new org.jbpm.services.task.commands.GetAllCommentsCommand(taskId));
    }

    @Override
    public Comment getCommentById(long commentId) {
        return executor.execute(new org.jbpm.services.task.commands.GetCommentCommand(commentId));
    }

    @Override
    public Map<String, Object> getTaskContent(long taskId) {
        return executor.execute(new org.jbpm.services.task.commands.GetTaskContentCommand(taskId));
    }

    // marshaller context methods
    @Override
    public void addMarshallerContext(String ownerId, ContentMarshallerContext context) {
        TaskContentRegistry.get().addMarshallerContext(ownerId, context);
    }

    @Override
    public void removeMarshallerContext(String ownerId) {
        TaskContentRegistry.get().removeMarshallerContext(ownerId);
    }

    @Override
    public ContentMarshallerContext getMarshallerContext(Task task) {
        return TaskContentRegistry.get().getMarshallerContext(task);
    }

    @Override
    public void removeTaskEventsById(long taskId) {
        throw new UnsupportedOperationException((((Thread.currentThread().getStackTrace()[1].getMethodName()) + " is not supported on the ") + (CommandBasedTaskService.this.getClass().getSimpleName())));
    }

    @Override
    public List<TaskEvent> getTaskEventsById(long taskId) {
        throw new UnsupportedOperationException((((Thread.currentThread().getStackTrace()[1].getMethodName()) + " is not supported on the ") + (CommandBasedTaskService.this.getClass().getSimpleName())));
    }

    // notification service methods
    @Override
    public void registerTaskEventListener(TaskLifeCycleEventListener listener) {
        taskEventSupport.addEventListener(listener);
    }

    @Override
    public List<TaskLifeCycleEventListener> getTaskEventListeners() {
        return taskEventSupport.getEventListeners();
    }

    @Override
    public void clearTaskEventListeners() {
        taskEventSupport.clear();
    }

    @Override
    public void removeTaskEventListener(TaskLifeCycleEventListener listener) {
        taskEventSupport.removeEventListener(listener);
    }

    public void executeReminderForTask(long taskId, String fromUser) {
        executor.execute(new org.jbpm.services.task.commands.ExecuteReminderCommand(taskId, fromUser));
    }
}

