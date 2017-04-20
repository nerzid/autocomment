/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */


package org.jbpm.services.task.impl;

import java.util.Date;
import Status.Completed;
import QueryParameterIdentifiers.ACTUAL_OWNER_ID_LIST;
import QueryParameterIdentifiers.TASK_ID_LIST;
import QueryParameterIdentifiers.ASCENDING_VALUE;
import Status.InProgress;
import org.jbpm.query.jpa.data.QueryWhere;
import QueryParameterIdentifiers.WORK_ITEM_ID_LIST;
import org.kie.api.task.model.Task;
import QueryParameterIdentifiers.FIRST_RESULT;
import java.util.Map;
import Status.Suspended;
import java.util.Set;
import QueryParameterIdentifiers.ORDER_TYPE;
import java.util.ArrayList;
import java.util.Arrays;
import QueryParameterIdentifiers.BUSINESS_ADMIN_ID_LIST;
import org.jbpm.services.task.utils.ClassUtil;
import org.kie.internal.task.api.model.InternalTaskSummary;
import org.kie.internal.query.QueryContext;
import java.util.Collections;
import org.kie.api.task.model.OrganizationalEntity;
import Status.Reserved;
import java.util.List;
import org.kie.internal.task.api.TaskPersistenceContext;
import java.util.HashMap;
import Status.Created;
import java.util.HashSet;
import org.kie.api.task.model.Status;
import QueryParameterIdentifiers.DESCENDING_VALUE;
import QueryParameterIdentifiers.FILTER;
import QueryParameterIdentifiers.POTENTIAL_OWNER_ID_LIST;
import org.kie.api.task.model.TaskSummary;
import Status.Ready;
import org.slf4j.Logger;
import QueryParameterIdentifiers.ORDER_BY;
import org.kie.internal.query.QueryFilter;
import QueryParameterIdentifiers.PROCESS_INSTANCE_ID_LIST;
import org.kie.api.task.UserGroupCallback;
import QueryParameterIdentifiers.TASK_STATUS_LIST;
import QueryParameterIdentifiers.MAX_RESULTS;
import org.kie.internal.task.api.TaskQueryService;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class TaskQueryServiceImpl implements TaskQueryService {
    private static final Logger logger = LoggerFactory.getLogger(TaskQueryServiceImpl.class);

    private TaskPersistenceContext persistenceContext;

    private UserGroupCallback userGroupCallback;

    protected List<?> adoptList(List<?> source, List<?> values) {
        if ((source == null) || (source.isEmpty())) {
            List<Object> data = new ArrayList<Object>();
            for (Object value : values) {
                data.add(value);
            }
            return data;
        }
        return source;
    }

    protected void applyQueryFilter(Map<String, Object> params, QueryFilter queryFilter) {
        if (queryFilter != null) {
            applyQueryContext(params, queryFilter);
            if (((queryFilter.getFilterParams()) != null) && (!(queryFilter.getFilterParams().isEmpty()))) {
                params.put(FILTER, queryFilter.getFilterParams());
                for (String key : queryFilter.getParams().keySet()) {
                    params.put(key, queryFilter.getParams().get(key));
                }
            }
        }
    }

    protected void applyQueryContext(Map<String, Object> params, QueryContext queryContext) {
        if (queryContext != null) {
            Integer offset = queryContext.getOffset();
            if ((offset != null) && (offset > 0)) {
                params.put(FIRST_RESULT, offset);
            }
            Integer count = queryContext.getCount();
            if ((count != null) && (count > 0)) {
                params.put(MAX_RESULTS, count);
            }
            if (((queryContext.getOrderBy()) != null) && (!(queryContext.getOrderBy().isEmpty()))) {
                params.put(ORDER_BY, queryContext.getOrderBy());
                if ((queryContext.isAscending()) != null) {
                    if (queryContext.isAscending()) {
                        params.put(ORDER_TYPE, ASCENDING_VALUE);
                    }else {
                        params.put(ORDER_TYPE, DESCENDING_VALUE);
                    }
                }
            }
        }
    }

    private static final List<Status> allActiveStatus = new ArrayList<Status>() {
        {
            this.add(Created);
            this.add(Ready);
            this.add(Reserved);
            this.add(InProgress);
            this.add(Suspended);
        }
    };

    public TaskQueryServiceImpl() {
    }

    public TaskQueryServiceImpl(TaskPersistenceContext persistenceContext, UserGroupCallback userGroupCallback) {
        this.persistenceContext = persistenceContext;
        this.userGroupCallback = userGroupCallback;
    }

    public void setPersistenceContext(TaskPersistenceContext persistenceContext) {
        this.persistenceContext = persistenceContext;
    }

    public void setUserGroupCallback(UserGroupCallback userGroupCallback) {
        this.userGroupCallback = userGroupCallback;
    }

    public List<TaskSummary> getTasksAssignedAsBusinessAdministrator(String userId, List<String> groupIds) {
        return getTasksAssignedAsBusinessAdministratorByStatus(userId, groupIds, TaskQueryServiceImpl.allActiveStatus);
    }

    public List<TaskSummary> getTasksAssignedAsExcludedOwner(String userId) {
        return ((List<TaskSummary>) (persistenceContext.queryWithParametersInTransaction("TasksAssignedAsExcludedOwner", persistenceContext.addParametersToMap("userId", userId), ClassUtil.<List<TaskSummary>>castClass(List.class))));
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId) {
        return ((List<TaskSummary>) (persistenceContext.queryWithParametersInTransaction("TasksAssignedAsPotentialOwner", persistenceContext.addParametersToMap("userId", userId), ClassUtil.<List<TaskSummary>>castClass(List.class))));
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds) {
        if ((groupIds == null) || (groupIds.isEmpty())) {
            return getTasksAssignedAsPotentialOwner(userId);
        }
        Map<String, Object> params = new HashMap<String, Object>();
        // put String{"userId"} to Map{params}
        params.put("userId", userId);
        // put String{"groupIds"} to Map{params}
        params.put("groupIds", groupIds);
        return ((List<TaskSummary>) (persistenceContext.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerWithGroups", params, ClassUtil.<List<TaskSummary>>castClass(List.class))));
    }

    public List<TaskSummary> getTasksAssignedByGroup(String groupId) {
        if ((groupId == null) || (groupId.isEmpty())) {
            return Collections.EMPTY_LIST;
        }
        return ((List<TaskSummary>) (persistenceContext.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerByGroup", persistenceContext.addParametersToMap("groupId", groupId), ClassUtil.<List<TaskSummary>>castClass(List.class))));
    }

    public List<TaskSummary> getTasksAssignedByGroupsByExpirationDateOptional(List<String> groupIds, Date expirationDate) {
        if ((groupIds == null) || (groupIds.isEmpty())) {
            return Collections.EMPTY_LIST;
        }
        List<Object[]> tasksByGroups = ((List<Object[]>) (persistenceContext.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerByGroupsByExpirationDateOptional", persistenceContext.addParametersToMap("groupIds", groupIds, "expirationDate", expirationDate), ClassUtil.<List<Object[]>>castClass(List.class))));
        return collectTasksByPotentialOwners(tasksByGroups);
    }

    protected List<TaskSummary> collectTasksByPotentialOwners(List<Object[]> tasksByGroups) {
        Set<Long> tasksIds = Collections.synchronizedSet(new HashSet<Long>());
        Map<Long, List<String>> potentialOwners = Collections.synchronizedMap(new HashMap<Long, List<String>>());
        for (Object o : tasksByGroups) {
            Object[] get = ((Object[]) (o));
            tasksIds.add(((Long) (get[0])));
            if ((potentialOwners.get(((Long) (get[0])))) == null) {
                potentialOwners.put(((Long) (get[0])), new ArrayList<String>());
            }
            potentialOwners.get(((Long) (get[0]))).add(((String) (get[1])));
        }
        if (!(tasksIds.isEmpty())) {
            List<TaskSummary> tasks = ((List<TaskSummary>) (persistenceContext.queryWithParametersInTransaction("TaskSummariesByIds", persistenceContext.addParametersToMap("taskIds", tasksIds), ClassUtil.<List<TaskSummary>>castClass(List.class))));
            for (TaskSummary ts : tasks) {
                ((InternalTaskSummary) (ts)).setPotentialOwners(potentialOwners.get(ts.getId()));
            }
            return tasks;
        }
        return new ArrayList<TaskSummary>();
    }

    public List<TaskSummary> getTasksAssignedByGroupsByExpirationDate(List<String> groupIds, Date expirationDate) {
        if ((groupIds == null) || (groupIds.isEmpty())) {
            return Collections.EMPTY_LIST;
        }
        List<Object[]> tasksByGroups = ((List<Object[]>) (persistenceContext.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerByGroupsByExpirationDate", persistenceContext.addParametersToMap("groupIds", groupIds, "expirationDate", expirationDate), ClassUtil.<List<Object[]>>castClass(List.class))));
        return collectTasksByPotentialOwners(tasksByGroups);
    }

    public List<TaskSummary> getTasksAssignedByGroups(List<String> groupIds) {
        if ((groupIds == null) || (groupIds.isEmpty())) {
            return Collections.EMPTY_LIST;
        }
        List<Object[]> tasksByGroups = ((List<Object[]>) (persistenceContext.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerByGroups", persistenceContext.addParametersToMap("groupIds", groupIds), ClassUtil.<List<Object[]>>castClass(List.class))));
        Set<Long> tasksIds = Collections.synchronizedSet(new HashSet<Long>());
        Map<Long, List<String>> potentialOwners = Collections.synchronizedMap(new HashMap<Long, List<String>>());
        for (Object o : tasksByGroups) {
            Object[] get = ((Object[]) (o));
            tasksIds.add(((Long) (get[0])));
            if ((potentialOwners.get(((Long) (get[0])))) == null) {
                potentialOwners.put(((Long) (get[0])), new ArrayList<String>());
            }
            potentialOwners.get(((Long) (get[0]))).add(((String) (get[1])));
        }
        if (!(tasksIds.isEmpty())) {
            List<TaskSummary> tasks = ((List<TaskSummary>) (persistenceContext.queryWithParametersInTransaction("TaskSummariesByIds", persistenceContext.addParametersToMap("taskIds", tasksIds), ClassUtil.<List<TaskSummary>>castClass(List.class))));
            for (TaskSummary ts : tasks) {
                ((InternalTaskSummary) (ts)).setPotentialOwners(potentialOwners.get(ts.getId()));
            }
            return tasks;
        }
        return new ArrayList<TaskSummary>();
    }

    public Map<Long, List<OrganizationalEntity>> getPotentialOwnersForTaskIds(List<Long> taskIds) {
        List<Object[]> potentialOwners = persistenceContext.queryWithParametersInTransaction("GetPotentialOwnersForTaskIds", persistenceContext.addParametersToMap("taskIds", taskIds), ClassUtil.<List<Object[]>>castClass(List.class));
        Map<Long, List<OrganizationalEntity>> potentialOwnersMap = new HashMap<Long, List<OrganizationalEntity>>();
        Long currentTaskId = 0L;
        for (Object[] item : potentialOwners) {
            Long taskId = ((Long) (item[0]));
            OrganizationalEntity potentialOwner = ((OrganizationalEntity) (item[1]));
            if (currentTaskId != taskId) {
                currentTaskId = taskId;
            }
            if ((potentialOwnersMap.get(currentTaskId)) == null) {
                potentialOwnersMap.put(currentTaskId, new ArrayList<OrganizationalEntity>());
            }
            potentialOwnersMap.get(currentTaskId).add(potentialOwner);
        }
        return potentialOwnersMap;
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, int firstResult, int maxResults) {
        if ((groupIds == null) || (groupIds.isEmpty())) {
            return ((List<TaskSummary>) (persistenceContext.queryWithParametersInTransaction("TasksAssignedAsPotentialOwner", persistenceContext.addParametersToMap("userId", userId, "firstResult", firstResult, "maxResults", maxResults), ClassUtil.<List<TaskSummary>>castClass(List.class))));
        }
        return ((List<TaskSummary>) (persistenceContext.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerWithGroups", persistenceContext.addParametersToMap("userId", userId, "groupIds", groupIds, "firstResult", firstResult, "maxResults", maxResults), ClassUtil.<List<TaskSummary>>castClass(List.class))));
    }

    public List<TaskSummary> getTasksAssignedAsRecipient(String userId) {
        return ((List<TaskSummary>) (persistenceContext.queryWithParametersInTransaction("TasksAssignedAsRecipient", persistenceContext.addParametersToMap("userId", userId), ClassUtil.<List<TaskSummary>>castClass(List.class))));
    }

    public List<TaskSummary> getTasksAssignedAsTaskInitiator(String userId) {
        return ((List<TaskSummary>) (persistenceContext.queryWithParametersInTransaction("TasksAssignedAsTaskInitiator", persistenceContext.addParametersToMap("userId", userId), ClassUtil.<List<TaskSummary>>castClass(List.class))));
    }

    public List<TaskSummary> getTasksAssignedAsTaskStakeholder(String userId) {
        return ((List<TaskSummary>) (persistenceContext.queryWithParametersInTransaction("TasksAssignedAsTaskStakeholder", persistenceContext.addParametersToMap("userId", userId), ClassUtil.<List<TaskSummary>>castClass(List.class))));
    }

    public List<TaskSummary> getTasksOwned(String userId) {
        return getTasksOwned(userId, null, null);
    }

    public List<TaskSummary> getTasksOwnedByStatus(String userId, List<Status> status) {
        List<TaskSummary> taskOwned = getTasksOwned(userId, null, null);
        if (!(taskOwned.isEmpty())) {
            Set<Long> tasksIds = new HashSet<Long>();
            for (TaskSummary ts : taskOwned) {
                tasksIds.add(ts.getId());
            }
            List<Object[]> tasksPotentialOwners = ((List<Object[]>) (persistenceContext.queryWithParametersInTransaction("TasksOwnedPotentialOwnersByTaskIds", persistenceContext.addParametersToMap("taskIds", tasksIds), ClassUtil.<List<Object[]>>castClass(List.class))));
            Map<Long, List<String>> potentialOwners = new HashMap<Long, List<String>>();
            for (Object o : tasksPotentialOwners) {
                Object[] get = ((Object[]) (o));
                tasksIds.add(((Long) (get[0])));
                if ((potentialOwners.get(((Long) (get[0])))) == null) {
                    potentialOwners.put(((Long) (get[0])), new ArrayList<String>());
                }
                potentialOwners.get(((Long) (get[0]))).add(((String) (get[1])));
            }
            for (TaskSummary ts : taskOwned) {
                ((InternalTaskSummary) (ts)).setPotentialOwners(potentialOwners.get(ts.getId()));
            }
        }else {
            return new ArrayList<TaskSummary>(0);
        }
        return taskOwned;
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatus(String userId, List<Status> status) {
        return getTasksAssignedAsPotentialOwner(userId, null, status, null);
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, List<Status> status, QueryFilter filter) {
        Map<String, Object> params = new HashMap<String, Object>();
        // put String{"userId"} to Map{params}
        params.put("userId", userId);
        // put String{"status"} to Map{params}
        params.put("status", adoptList(status, TaskQueryServiceImpl.allActiveStatus));
        // put String{"groupIds"} to Map{params}
        params.put("groupIds", adoptList(groupIds, Collections.singletonList("")));
        // apply query Map{params} to TaskQueryServiceImpl{}
        applyQueryFilter(params, filter);
        return ((List<TaskSummary>) (persistenceContext.queryWithParametersInTransaction("NewTasksAssignedAsPotentialOwner", params, ClassUtil.<List<TaskSummary>>castClass(List.class))));
    }

    public List<TaskSummary> getTasksOwned(String userId, List<Status> status, QueryFilter filter) {
        Map<String, Object> params = new HashMap<String, Object>();
        // put String{"userId"} to Map{params}
        params.put("userId", userId);
        if (status == null) {
            status = new ArrayList<Status>();
            status.add(Reserved);
            status.add(InProgress);
        }
        // put String{"status"} to Map{params}
        params.put("status", status);
        // apply query Map{params} to TaskQueryServiceImpl{}
        applyQueryFilter(params, filter);
        return ((List<TaskSummary>) (persistenceContext.queryWithParametersInTransaction("NewTasksOwned", params, ClassUtil.<List<TaskSummary>>castClass(List.class))));
    }

    public List<TaskSummary> getSubTasksAssignedAsPotentialOwner(long parentId, String userId) {
        return ((List<TaskSummary>) (persistenceContext.queryWithParametersInTransaction("SubTasksAssignedAsPotentialOwner", persistenceContext.addParametersToMap("parentId", parentId, "userId", userId), ClassUtil.<List<TaskSummary>>castClass(List.class))));
    }

    public List<TaskSummary> getSubTasksByParent(long parentId) {
        return ((List<TaskSummary>) (persistenceContext.queryWithParametersInTransaction("GetSubTasksByParentTaskId", persistenceContext.addParametersToMap("parentId", parentId), ClassUtil.<List<TaskSummary>>castClass(List.class))));
    }

    public int getPendingSubTasksByParent(long parentId) {
        return persistenceContext.queryWithParametersInTransaction("GetSubTasksByParentTaskId", persistenceContext.addParametersToMap("parentId", parentId), ClassUtil.<List<TaskSummary>>castClass(List.class)).size();
    }

    public Task getTaskInstanceById(long taskId) {
        Task taskInstance = persistenceContext.findTask(taskId);
        return taskInstance;
    }

    public Task getTaskByWorkItemId(long workItemId) {
        List<Task> tasks = ((List<Task>) (persistenceContext.queryWithParametersInTransaction("TaskByWorkItemId", persistenceContext.addParametersToMap("workItemId", workItemId, "maxResults", 1), ClassUtil.<List<Task>>castClass(List.class))));
        if (tasks.isEmpty())
            return null;
        else
            return ((Task) (tasks.get(0)));
        
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDate(String userId, List<String> groupIds, List<Status> status, Date expirationDate) {
        Map<String, Object> params = new HashMap<String, Object>();
        // put String{"expirationDate"} to Map{params}
        params.put("expirationDate", expirationDate);
        return ((List<TaskSummary>) (getTasksAssignedAsPotentialOwner(userId, groupIds, status, new QueryFilter("t.taskData.expirationTime = :expirationDate", params, "order by t.id", false))));
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDateOptional(String userId, List<String> groupIds, List<Status> status, Date expirationDate) {
        Map<String, Object> params = new HashMap<String, Object>();
        // put String{"expirationDate"} to Map{params}
        params.put("expirationDate", expirationDate);
        return ((List<TaskSummary>) (getTasksAssignedAsPotentialOwner(userId, groupIds, status, new QueryFilter("(t.taskData.expirationTime = :expirationDate or t.taskData.expirationTime is null)", params, "order by t.id", false))));
    }

    @Override
    public List<TaskSummary> getTasksOwnedByExpirationDate(String userId, List<Status> status, Date expirationDate) {
        Map<String, Object> params = new HashMap<String, Object>();
        // put String{"expirationDate"} to Map{params}
        params.put("expirationDate", expirationDate);
        return ((List<TaskSummary>) (getTasksOwned(userId, status, new QueryFilter("t.taskData.expirationTime = :expirationDate", params, "order by t.id", false))));
    }

    @Override
    public List<TaskSummary> getTasksOwnedByExpirationDateOptional(String userId, List<Status> status, Date expirationDate) {
        Map<String, Object> params = new HashMap<String, Object>();
        // put String{"expirationDate"} to Map{params}
        params.put("expirationDate", expirationDate);
        return ((List<TaskSummary>) (getTasksOwned(userId, status, new QueryFilter("(t.taskData.expirationTime = :expirationDate or t.taskData.expirationTime is null)", params, "order by t.id", false))));
    }

    @Override
    public List<TaskSummary> getTasksOwnedByExpirationDateBeforeSpecifiedDate(String userId, List<Status> status, Date date) {
        if ((status == null) || (status.isEmpty())) {
            status = TaskQueryServiceImpl.allActiveStatus;
        }
        return ((List<TaskSummary>) (persistenceContext.queryWithParametersInTransaction("TasksOwnedWithParticularStatusByExpirationDateBeforeSpecifiedDate", persistenceContext.addParametersToMap("userId", userId, "status", status, "date", date), ClassUtil.<List<TaskSummary>>castClass(List.class))));
    }

    @Override
    public List<TaskSummary> getTasksByStatusByProcessInstanceId(long processInstanceId, List<Status> status) {
        if ((status == null) || (status.isEmpty())) {
            status = TaskQueryServiceImpl.allActiveStatus;
        }
        List<TaskSummary> tasks = ((List<TaskSummary>) (persistenceContext.queryWithParametersInTransaction("TasksByStatusByProcessId", persistenceContext.addParametersToMap("processInstanceId", processInstanceId, "status", status), ClassUtil.<List<TaskSummary>>castClass(List.class))));
        return tasks;
    }

    @Override
    public List<TaskSummary> getTasksByStatusByProcessInstanceIdByTaskName(long processInstanceId, List<Status> status, String taskName) {
        if ((status == null) || (status.isEmpty())) {
            status = TaskQueryServiceImpl.allActiveStatus;
        }
        List<TaskSummary> tasks = ((List<TaskSummary>) (persistenceContext.queryWithParametersInTransaction("TasksByStatusByProcessIdByTaskName", persistenceContext.addParametersToMap("processInstanceId", processInstanceId, "status", status, "taskName", taskName), ClassUtil.<List<TaskSummary>>castClass(List.class))));
        return tasks;
    }

    @Override
    public List<Long> getTasksByProcessInstanceId(long processInstanceId) {
        List<Long> tasks = ((List<Long>) (persistenceContext.queryWithParametersInTransaction("TasksByProcessInstanceId", persistenceContext.addParametersToMap("processInstanceId", processInstanceId), ClassUtil.<List<Long>>castClass(List.class))));
        return tasks;
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDate(String userId, List<Status> status, Date expirationDate) {
        if ((status == null) || (status.isEmpty())) {
            status = TaskQueryServiceImpl.allActiveStatus;
        }
        return ((List<TaskSummary>) (persistenceContext.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerStatusByExpirationDate", persistenceContext.addParametersToMap("userId", userId, "groupIds", "", "status", status, "expirationDate", expirationDate), ClassUtil.<List<TaskSummary>>castClass(List.class))));
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDateOptional(String userId, List<Status> status, Date expirationDate) {
        if ((status == null) || (status.isEmpty())) {
            status = TaskQueryServiceImpl.allActiveStatus;
        }
        return ((List<TaskSummary>) (persistenceContext.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerStatusByExpirationDateOptional", persistenceContext.addParametersToMap("userId", userId, "groupIds", "", "status", status, "expirationDate", expirationDate), ClassUtil.<List<TaskSummary>>castClass(List.class))));
    }

    // This method should be deleted in jBPM 7.x+
    @Deprecated
    public List<TaskSummary> getTasksByVariousFields(String userId, List<Long> workItemIds, List<Long> taskIds, List<Long> procInstIds, List<String> busAdmins, List<String> potOwners, List<String> taskOwners, List<Status> status, boolean union, Integer maxResults) {
        Map<String, List<?>> params = new HashMap<String, List<?>>();
        // put void{WORK_ITEM_ID_LIST} to Map{params}
        params.put(WORK_ITEM_ID_LIST, workItemIds);
        // put void{TASK_ID_LIST} to Map{params}
        params.put(TASK_ID_LIST, taskIds);
        // put void{PROCESS_INSTANCE_ID_LIST} to Map{params}
        params.put(PROCESS_INSTANCE_ID_LIST, procInstIds);
        // put void{BUSINESS_ADMIN_ID_LIST} to Map{params}
        params.put(BUSINESS_ADMIN_ID_LIST, busAdmins);
        // put void{POTENTIAL_OWNER_ID_LIST} to Map{params}
        params.put(POTENTIAL_OWNER_ID_LIST, potOwners);
        // put void{ACTUAL_OWNER_ID_LIST} to Map{params}
        params.put(ACTUAL_OWNER_ID_LIST, taskOwners);
        if ((status == null) || (status.isEmpty())) {
            status = TaskQueryServiceImpl.allActiveStatus;
        }
        // put void{TASK_STATUS_LIST} to Map{params}
        params.put(TASK_STATUS_LIST, status);
        if (maxResults != null) {
            if (maxResults <= 0) {
                return new ArrayList<TaskSummary>();
            }
            Integer[] maxResultsArr = new Integer[]{ maxResults };
            params.put(MAX_RESULTS, Arrays.asList(maxResultsArr));
        }
        return getTasksByVariousFields(userId, params, union);
    }

    // This method should be deleted in jBPM 7.x+
    @Deprecated
    public List<TaskSummary> getTasksByVariousFields(String userId, Map<String, List<?>> parameters, boolean union) {
        QueryWhere queryWhere = new QueryWhere();
        // set ascending void{TASK_ID_LIST} to QueryWhere{queryWhere}
        queryWhere.setAscending(TASK_ID_LIST);
        List<?> maxResultsList = parameters.remove(MAX_RESULTS);
        if ((maxResultsList != null) && (!(maxResultsList.isEmpty()))) {
            Object maxResults = maxResultsList.get(0);
            if (maxResults instanceof Integer) {
                queryWhere.setCount(((Integer) (maxResults)));
            }
        }
        // convert parameters to query data
        if (union) {
            queryWhere.setToUnion();
        }else {
            queryWhere.setToIntersection();
        }
        for (Map.Entry<String, List<?>> paramEntry : parameters.entrySet()) {
            List<?> paramList = paramEntry.getValue();
            if ((paramList != null) && (!(paramList.isEmpty()))) {
                queryWhere.addParameter(paramEntry.getKey(), convertToTypedArray(paramList, paramList.get(0)));
            }
        }
        return query(userId, queryWhere);
    }

    private <T> T[] convertToTypedArray(List<?> paramList, T... firstElem) {
        return paramList.toArray(firstElem);
    }

    public int getCompletedTaskByUserId(String userId) {
        List<Status> statuses = new ArrayList<Status>();
        // add void{Completed} to List{statuses}
        statuses.add(Completed);
        List<TaskSummary> tasksCompleted = getTasksAssignedAsPotentialOwnerByStatus(userId, statuses);
        return tasksCompleted.size();
    }

    public int getPendingTaskByUserId(String userId) {
        List<TaskSummary> tasksAssigned = getTasksAssignedAsPotentialOwner(userId, null, null, null);
        return tasksAssigned.size();
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatusByGroup(String userId, List<String> groupIds, List<Status> status) {
        Map<String, Object> params = new HashMap<String, Object>();
        // put String{"userId"} to Map{params}
        params.put("userId", userId);
        // put String{"status"} to Map{params}
        params.put("status", adoptList(status, TaskQueryServiceImpl.allActiveStatus));
        // put String{"groupIds"} to Map{params}
        params.put("groupIds", adoptList(groupIds, Collections.singletonList("")));
        return ((List<TaskSummary>) (persistenceContext.queryWithParametersInTransaction("QuickTasksAssignedAsPotentialOwnerWithGroupsByStatus", params, ClassUtil.<List<TaskSummary>>castClass(List.class))));
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsBusinessAdministratorByStatus(String userId, List<String> groupIds, List<Status> status) {
        Map<String, Object> params = new HashMap<String, Object>();
        // put String{"userId"} to Map{params}
        params.put("userId", userId);
        // put String{"status"} to Map{params}
        params.put("status", status);
        // put String{"groupIds"} to Map{params}
        params.put("groupIds", adoptList(groupIds, Collections.singletonList("")));
        return ((List<TaskSummary>) (persistenceContext.queryWithParametersInTransaction("TasksAssignedAsBusinessAdministratorByStatus", params, ClassUtil.<List<TaskSummary>>castClass(List.class))));
    }

    @Override
    public List<TaskSummary> query(String userId, Object queryObj) {
        QueryWhere queryWhere = ((QueryWhere) (queryObj));
        return persistenceContext.doTaskSummaryCriteriaQuery(userId, userGroupCallback, ((QueryWhere) (queryWhere)));
    }
}

