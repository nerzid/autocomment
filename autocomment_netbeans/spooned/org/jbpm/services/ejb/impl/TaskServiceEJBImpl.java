/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.services.ejb.impl;

import org.kie.api.task.model.Attachment;
import org.jbpm.services.task.lifecycle.listeners.BAMTaskEventListener;
import org.kie.api.task.model.Comment;
import org.kie.api.task.model.Content;
import org.kie.internal.task.api.model.ContentData;
import org.kie.internal.task.api.ContentMarshallerContext;
import java.util.Date;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorManager;
import javax.persistence.EntityManagerFactory;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.kie.internal.task.api.model.FaultData;
import org.kie.api.task.model.Group;
import org.jbpm.services.task.HumanTaskConfigurator;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.kie.api.task.model.I18NText;
import org.kie.internal.task.api.InternalTaskService;
import org.jbpm.services.task.audit.JPATaskLifeCycleEventListener;
import java.util.List;
import java.util.Map;
import org.kie.api.task.model.OrganizationalEntity;
import javax.persistence.PersistenceUnit;
import javax.annotation.PostConstruct;
import org.kie.internal.query.QueryFilter;
import javax.ejb.Stateless;
import org.kie.api.task.model.Status;
import org.kie.internal.task.api.model.SubTasksStrategy;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.model.TaskDef;
import org.kie.internal.task.api.model.TaskEvent;
import org.kie.api.task.TaskService;
import org.jbpm.services.ejb.TaskServiceEJBLocal;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.task.query.TaskSummaryQueryBuilder;
import org.kie.api.task.model.User;
import org.jbpm.runtime.manager.impl.identity.UserDataServiceProvider;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.task.api.UserInfo;
import org.jbpm.services.api.UserTaskService;

@Stateless
public class TaskServiceEJBImpl implements TaskServiceEJBLocal , TaskService , InternalTaskService {
    private InternalTaskService delegate;

    @PersistenceUnit(unitName = "org.jbpm.domain")
    private EntityManagerFactory emf;

    @PostConstruct
    public void configureDelegate() {
        UserGroupCallback callback = UserDataServiceProvider.getUserGroupCallback();
        HumanTaskConfigurator configurator = HumanTaskServiceFactory.newTaskServiceConfigurator().entityManagerFactory(emf).userGroupCallback(callback);
        DeploymentDescriptorManager manager = new DeploymentDescriptorManager("org.jbpm.domain");
        DeploymentDescriptor descriptor = manager.getDefaultDescriptor();
        // in case there is descriptor with enabled audit register then by default
        if (!(descriptor.getAuditMode().equals(AuditMode.NONE))) {
            JPATaskLifeCycleEventListener listener = new JPATaskLifeCycleEventListener(false);
            BAMTaskEventListener bamListener = new BAMTaskEventListener(false);
            // if the audit persistence unit is different than default for the engine perform proper init
            if (!("org.jbpm.domain".equals(descriptor.getAuditPersistenceUnit()))) {
                EntityManagerFactory emf = EntityManagerFactoryManager.get().getOrCreate(descriptor.getAuditPersistenceUnit());
                listener = new JPATaskLifeCycleEventListener(emf);
                bamListener = new BAMTaskEventListener(emf);
            } 
            configurator.listener(listener);
            configurator.listener(bamListener);
        } 
        delegate = ((InternalTaskService) (configurator.getTaskService()));
    }

    // implemented methods
    @Override
    public List<TaskSummary> getTasksAssignedAsBusinessAdministrator(String userId, String language) {
        return delegate.getTasksAssignedAsBusinessAdministrator(userId, language);
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsBusinessAdministratorByStatus(String userId, String language, List<Status> statuses) {
        return delegate.getTasksAssignedAsBusinessAdministratorByStatus(userId, language, statuses);
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, String language) {
        return delegate.getTasksAssignedAsPotentialOwner(userId, language);
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatus(String userId, List<Status> status, String language) {
        return delegate.getTasksAssignedAsPotentialOwnerByStatus(userId, status, language);
    }

    @Override
    public List<TaskSummary> getTasksOwned(String userId, String language) {
        return delegate.getTasksOwned(userId, language);
    }

    @Override
    public List<TaskSummary> getTasksOwnedByStatus(String userId, List<Status> status, String language) {
        return delegate.getTasksOwnedByStatus(userId, status, language);
    }

    @Override
    public List<TaskSummary> getTasksByStatusByProcessInstanceId(long processInstanceId, List<Status> status, String language) {
        return delegate.getTasksByStatusByProcessInstanceId(processInstanceId, status, language);
    }

    @Override
    public List<Long> getTasksByProcessInstanceId(long processInstanceId) {
        return delegate.getTasksByProcessInstanceId(processInstanceId);
    }

    @Override
    public List<TaskSummary> getActiveTasks() {
        return delegate.getActiveTasks();
    }

    @Override
    public List<TaskSummary> getActiveTasks(Date since) {
        return delegate.getActiveTasks(since);
    }

    @Override
    public List<TaskSummary> getTasksOwned(String userId, List<Status> status, QueryFilter filter) {
        return delegate.getTasksOwned(userId, status, filter);
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, List<Status> status, QueryFilter filter) {
        return delegate.getTasksAssignedAsPotentialOwner(userId, groupIds, status, filter);
    }

    @Override
    public List<TaskSummary> getArchivedTasks() {
        return delegate.getArchivedTasks();
    }

    @Override
    public List<TaskSummary> getCompletedTasks() {
        return delegate.getCompletedTasks();
    }

    @Override
    public List<TaskSummary> getCompletedTasks(Date since) {
        return delegate.getCompletedTasks(since);
    }

    @Override
    public List<TaskSummary> getCompletedTasksByProcessId(Long processId) {
        return delegate.getCompletedTasksByProcessId(processId);
    }

    @Override
    public List<TaskSummary> getSubTasksAssignedAsPotentialOwner(long parentId, String userId) {
        return delegate.getSubTasksAssignedAsPotentialOwner(parentId, userId);
    }

    @Override
    public List<TaskSummary> getSubTasksByParent(long parentId) {
        return delegate.getSubTasksByParent(parentId);
    }

    @Override
    public int getPendingSubTasksByParent(long parentId) {
        return delegate.getPendingSubTasksByParent(parentId);
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDate(String userId, List<Status> statuses, Date expirationDate) {
        return delegate.getTasksAssignedAsPotentialOwnerByExpirationDate(userId, statuses, expirationDate);
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDateOptional(String userId, List<Status> statuses, Date expirationDate) {
        return delegate.getTasksAssignedAsPotentialOwnerByExpirationDateOptional(userId, statuses, expirationDate);
    }

    @Override
    public List<TaskSummary> getTasksOwnedByExpirationDate(String userId, List<Status> statuses, Date expirationDate) {
        return delegate.getTasksOwnedByExpirationDate(userId, statuses, expirationDate);
    }

    @Override
    public List<TaskSummary> getTasksOwnedByExpirationDateOptional(String userId, List<Status> statuses, Date expirationDate) {
        return delegate.getTasksOwnedByExpirationDateOptional(userId, statuses, expirationDate);
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsExcludedOwner(String userId) {
        return delegate.getTasksAssignedAsExcludedOwner(userId);
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds) {
        return delegate.getTasksAssignedAsPotentialOwner(userId, groupIds);
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language, int firstResult, int maxResults) {
        return delegate.getTasksAssignedAsPotentialOwner(userId, groupIds, language, firstResult, maxResults);
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatusByGroup(String userId, List<String> groupIds, List<Status> status) {
        return delegate.getTasksAssignedAsPotentialOwnerByStatusByGroup(userId, groupIds, status);
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByProcessId(String userId, String processId) {
        return delegate.getTasksAssignedAsPotentialOwnerByProcessId(userId, processId);
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsRecipient(String userId) {
        return delegate.getTasksAssignedAsRecipient(userId);
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsTaskInitiator(String userId) {
        return delegate.getTasksAssignedAsTaskInitiator(userId);
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsTaskStakeholder(String userId) {
        return delegate.getTasksAssignedAsTaskStakeholder(userId);
    }

    @Override
    public List<TaskSummary> getTasksOwnedByExpirationDateBeforeSpecifiedDate(String userId, List<Status> status, Date date) {
        return delegate.getTasksOwnedByExpirationDateBeforeSpecifiedDate(userId, status, date);
    }

    @Override
    public List<TaskSummary> getTasksByStatusByProcessInstanceIdByTaskName(long processInstanceId, List<Status> status, String taskName) {
        return delegate.getTasksByStatusByProcessInstanceIdByTaskName(processInstanceId, status, taskName);
    }

    @Override
    public Map<Long, List<OrganizationalEntity>> getPotentialOwnersForTaskIds(List<Long> taskIds) {
        return delegate.getPotentialOwnersForTaskIds(taskIds);
    }

    @Override
    public List<TaskSummary> getTasksAssignedByGroup(String groupId) {
        return delegate.getTasksAssignedByGroup(groupId);
    }

    @Override
    public List<TaskSummary> getTasksAssignedByGroups(List<String> groupIds) {
        return delegate.getTasksAssignedByGroups(groupIds);
    }

    @Override
    public List<TaskSummary> getTasksByVariousFields(String userId, List<Long> workItemIds, List<Long> taskIds, List<Long> procInstIds, List<String> busAdmins, List<String> potOwners, List<String> taskOwners, List<Status> status, List<String> languages, boolean union) {
        return delegate.getTasksByVariousFields(userId, workItemIds, taskIds, procInstIds, busAdmins, potOwners, taskOwners, status, languages, union);
    }

    @Override
    public List<TaskSummary> getTasksByVariousFields(String userId, Map<String, List<?>> parameters, boolean union) {
        return delegate.getTasksByVariousFields(userId, parameters, union);
    }

    // unsupported method
    @Override
    public void activate(long taskId, String userId) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public void claim(long taskId, String userId) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public void claimNextAvailable(String userId, String language) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public void complete(long taskId, String userId, Map<String, Object> data) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public void delegate(long taskId, String userId, String targetUserId) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public void exit(long taskId, String userId) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public void fail(long taskId, String userId, Map<String, Object> faultData) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public void forward(long taskId, String userId, String targetEntityId) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public Task getTaskByWorkItemId(long workItemId) {
        return TaskServiceEJBImpl.unsupported(Task.class);
    }

    @Override
    public Task getTaskById(long taskId) {
        return TaskServiceEJBImpl.unsupported(Task.class);
    }

    @Override
    public long addTask(Task task, Map<String, Object> params) {
        return TaskServiceEJBImpl.unsupported(long.class);
    }

    @Override
    public void release(long taskId, String userId) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public void resume(long taskId, String userId) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public void skip(long taskId, String userId) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public void start(long taskId, String userId) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public void stop(long taskId, String userId) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public void suspend(long taskId, String userId) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public void nominate(long taskId, String userId, List<OrganizationalEntity> potentialOwners) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public Content getContentById(long contentId) {
        return TaskServiceEJBImpl.unsupported(Content.class);
    }

    @Override
    public Attachment getAttachmentById(long attachId) {
        return TaskServiceEJBImpl.unsupported(Attachment.class);
    }

    @Override
    public Map<String, Object> getTaskContent(long taskId) {
        return TaskServiceEJBImpl.unsupported(Map.class);
    }

    @Override
    public <T> T execute(Command<T> command) {
        return delegate.execute(command);
    }

    @Override
    public void addGroup(Group group) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public void addUser(User user) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public int archiveTasks(List<TaskSummary> tasks) {
        return TaskServiceEJBImpl.unsupported(int.class);
    }

    @Override
    public void claim(long taskId, String userId, List<String> groupIds) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public void claimNextAvailable(String userId, List<String> groupIds) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public void deleteFault(long taskId, String userId) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public void deleteOutput(long taskId, String userId) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public void deployTaskDef(TaskDef def) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public List<TaskDef> getAllTaskDef(String filter) {
        return TaskServiceEJBImpl.unsupported(List.class);
    }

    @Override
    public Group getGroupById(String groupId) {
        return TaskServiceEJBImpl.unsupported(Group.class);
    }

    @Override
    public List<Group> getGroups() {
        return TaskServiceEJBImpl.unsupported(List.class);
    }

    @Override
    public TaskDef getTaskDefById(String id) {
        return TaskServiceEJBImpl.unsupported(TaskDef.class);
    }

    @Override
    public User getUserById(String userId) {
        return TaskServiceEJBImpl.unsupported(User.class);
    }

    @Override
    public List<User> getUsers() {
        return TaskServiceEJBImpl.unsupported(List.class);
    }

    @Override
    public long addTask(Task task, ContentData data) {
        return TaskServiceEJBImpl.unsupported(long.class);
    }

    @Override
    public void remove(long taskId, String userId) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public void removeGroup(String groupId) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public int removeTasks(List<TaskSummary> tasks) {
        return TaskServiceEJBImpl.unsupported(int.class);
    }

    @Override
    public void removeUser(String userId) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public void setFault(long taskId, String userId, FaultData fault) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public void setOutput(long taskId, String userId, Object outputContentData) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public void setPriority(long taskId, int priority) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public void setTaskNames(long taskId, List<I18NText> taskNames) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public void undeployTaskDef(String id) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public List<TaskEvent> getTaskEventsById(long taskId) {
        return TaskServiceEJBImpl.unsupported(List.class);
    }

    @Override
    public UserInfo getUserInfo() {
        return TaskServiceEJBImpl.unsupported(UserInfo.class);
    }

    @Override
    public void setUserInfo(UserInfo userInfo) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public void addUsersAndGroups(Map<String, User> users, Map<String, Group> groups) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public int removeAllTasks() {
        return TaskServiceEJBImpl.unsupported(int.class);
    }

    @Override
    public long addContent(long taskId, Content content) {
        return TaskServiceEJBImpl.unsupported(long.class);
    }

    @Override
    public long addContent(long taskId, Map<String, Object> params) {
        return TaskServiceEJBImpl.unsupported(long.class);
    }

    @Override
    public void deleteContent(long taskId, long contentId) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public List<Content> getAllContentByTaskId(long taskId) {
        return TaskServiceEJBImpl.unsupported(List.class);
    }

    @Override
    public long addAttachment(long taskId, Attachment attachment, Content content) {
        return TaskServiceEJBImpl.unsupported(long.class);
    }

    @Override
    public void deleteAttachment(long taskId, long attachmentId) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public List<Attachment> getAllAttachmentsByTaskId(long taskId) {
        return TaskServiceEJBImpl.unsupported(List.class);
    }

    @Override
    public void removeTaskEventsById(long taskId) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public OrganizationalEntity getOrganizationalEntityById(String entityId) {
        return TaskServiceEJBImpl.unsupported(OrganizationalEntity.class);
    }

    @Override
    public void setExpirationDate(long taskId, Date date) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public void setDescriptions(long taskId, List<I18NText> descriptions) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public void setSkipable(long taskId, boolean skipable) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public void setSubTaskStrategy(long taskId, SubTasksStrategy strategy) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public int getPriority(long taskId) {
        return TaskServiceEJBImpl.unsupported(int.class);
    }

    @Override
    public Date getExpirationDate(long taskId) {
        return TaskServiceEJBImpl.unsupported(Date.class);
    }

    @Override
    public List<I18NText> getDescriptions(long taskId) {
        return TaskServiceEJBImpl.unsupported(List.class);
    }

    @Override
    public boolean isSkipable(long taskId) {
        return TaskServiceEJBImpl.unsupported(boolean.class);
    }

    @Override
    public SubTasksStrategy getSubTaskStrategy(long taskId) {
        return TaskServiceEJBImpl.unsupported(SubTasksStrategy.class);
    }

    @Override
    public Task getTaskInstanceById(long taskId) {
        return TaskServiceEJBImpl.unsupported(Task.class);
    }

    @Override
    public int getCompletedTaskByUserId(String userId) {
        return TaskServiceEJBImpl.unsupported(int.class);
    }

    @Override
    public int getPendingTaskByUserId(String userId) {
        return TaskServiceEJBImpl.unsupported(int.class);
    }

    @Override
    public Long addComment(long taskId, Comment comment) {
        return TaskServiceEJBImpl.unsupported(Long.class);
    }

    @Override
    public void deleteComment(long taskId, long commentId) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public List<Comment> getAllCommentsByTaskId(long taskId) {
        return TaskServiceEJBImpl.unsupported(List.class);
    }

    @Override
    public Comment getCommentById(long commentId) {
        return TaskServiceEJBImpl.unsupported(Comment.class);
    }

    @Override
    public void addMarshallerContext(String ownerId, ContentMarshallerContext context) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public void removeMarshallerContext(String ownerId) {
        TaskServiceEJBImpl.unsupported(Void.class);
    }

    @Override
    public ContentMarshallerContext getMarshallerContext(Task task) {
        return TaskServiceEJBImpl.unsupported(ContentMarshallerContext.class);
    }

    @Override
    public TaskSummaryQueryBuilder taskSummaryQuery(String userId) {
        return new org.jbpm.services.task.impl.TaskSummaryQueryBuilderImpl(userId, delegate);
    }

    @Override
    public void executeReminderForTask(long taskId, String initiator) {
        delegate.executeReminderForTask(taskId, initiator);
    }

    @Override
    public Long addComment(long taskId, String addedByUserId, String commentText) {
        return TaskServiceEJBImpl.unsupported(Long.class);
    }

    private static <T> T unsupported(Class<T> returnType) {
        String methodName = new Throwable().getStackTrace()[1].getMethodName();
        throw new UnsupportedOperationException(((((methodName + " is not supported on the TaskService EJB implementation, ") + "please use the ") + (UserTaskService.class)) + " implementation instead!"));
    }

    @Override
    public long setDocumentContentFromUser(long taskId, String userId, byte[] byteContent) {
        return TaskServiceEJBImpl.unsupported(long.class);
    }

    @Override
    public long addOutputContentFromUser(long taskId, String userId, Map<String, Object> params) {
        return TaskServiceEJBImpl.unsupported(long.class);
    }

    @Override
    public Content getContentByIdForUser(long contentId, String userId) {
        return TaskServiceEJBImpl.unsupported(Content.class);
    }

    @Override
    public Map<String, Object> getOutputContentMapForUser(long taskId, String userId) {
        return TaskServiceEJBImpl.unsupported(Map.class);
    }
}

