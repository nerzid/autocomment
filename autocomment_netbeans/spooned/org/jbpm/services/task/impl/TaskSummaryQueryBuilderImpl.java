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


package org.jbpm.services.task.impl;

import org.jbpm.query.jpa.builder.impl.AbstractQueryBuilderImpl;
import org.kie.api.runtime.CommandExecutor;
import java.util.Date;
import java.util.List;
import org.kie.internal.query.QueryParameterIdentifiers;
import org.jbpm.query.jpa.data.QueryWhere;
import org.kie.api.task.model.Status;
import org.kie.internal.task.api.model.SubTasksStrategy;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.task.query.TaskSummaryQueryBuilder;
import org.jbpm.services.task.commands.TaskSummaryQueryCommand;

/**
 * Main Implementation of the {@link TaskSummaryQueryBuilder}. See the {@link TaskSummaryQueryBuilder} interface
 * for more information.
 * </p>
 * This implementation defaults to an ascending orderby of "Id". It's important to
 * have a default ordering of results so that optional ({@link QueryContext}) offset and count
 * parameters then will actually be useful. Without an ordering, subsequent queries can retrieve
 * different randomly ordered lists.
 */
public class TaskSummaryQueryBuilderImpl extends AbstractQueryBuilderImpl<TaskSummaryQueryBuilder> implements TaskSummaryQueryBuilder {
    private final CommandExecutor executor;

    private final String userId;

    // for buiding QueryWhere instances
    public TaskSummaryQueryBuilderImpl() {
        this.userId = null;
        this.executor = null;
    }

    public TaskSummaryQueryBuilderImpl(String userId, TaskService taskService) {
        this.executor = taskService;
        this.userId = userId;
        TaskSummaryQueryBuilderImpl.this.queryWhere.setAscending(QueryParameterIdentifiers.TASK_ID_LIST);
    }

    // Task query builder methods
    @Override
    public TaskSummaryQueryBuilder activationTime(Date... activationTime) {
        addObjectParameter(QueryParameterIdentifiers.TASK_ACTIVATION_TIME_LIST, "activation time", activationTime);
        return TaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public TaskSummaryQueryBuilder activationTimeRange(Date activationTimeMin, Date activationTimeMax) {
        addRangeParameters(QueryParameterIdentifiers.TASK_ACTIVATION_TIME_LIST, "activation time range", activationTimeMin, activationTimeMax);
        return TaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public TaskSummaryQueryBuilder actualOwner(String... taskOwnerId) {
        addObjectParameter(QueryParameterIdentifiers.ACTUAL_OWNER_ID_LIST, "task owner id", taskOwnerId);
        return TaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public TaskSummaryQueryBuilder archived(boolean archived) {
        Short realValue = archived ? new Short(((short) (1))) : new Short(((short) (0)));
        addObjectParameter(QueryParameterIdentifiers.ARCHIVED, "archived", realValue);
        return TaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public TaskSummaryQueryBuilder businessAdmin(String... businessAdminId) {
        addObjectParameter(QueryParameterIdentifiers.BUSINESS_ADMIN_ID_LIST, "business administrator id", businessAdminId);
        return TaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public TaskSummaryQueryBuilder createdBy(String... createdById) {
        addObjectParameter(QueryParameterIdentifiers.CREATED_BY_LIST, "created by id", createdById);
        return TaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public TaskSummaryQueryBuilder createdOn(Date... createdOnDate) {
        addObjectParameter(QueryParameterIdentifiers.CREATED_ON_LIST, "created on", createdOnDate);
        return TaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public TaskSummaryQueryBuilder createdOnRange(Date createdOnMin, Date createdOnMax) {
        addRangeParameters(QueryParameterIdentifiers.CREATED_ON_LIST, "created on range", createdOnMin, createdOnMax);
        return TaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public TaskSummaryQueryBuilder deploymentId(String... deploymentId) {
        addObjectParameter(QueryParameterIdentifiers.DEPLOYMENT_ID_LIST, "deployment id", deploymentId);
        return TaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public TaskSummaryQueryBuilder description(String... description) {
        addObjectParameter(QueryParameterIdentifiers.TASK_DESCRIPTION_LIST, "description", description);
        for (String desc : description) {
            if ((desc.length()) > 255) {
                throw new IllegalArgumentException((("String argument is longer than 255 characters: [" + desc) + "]"));
            } 
        }
        return TaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public TaskSummaryQueryBuilder expirationTime(Date... expirationTime) {
        addObjectParameter(QueryParameterIdentifiers.EXPIRATION_TIME_LIST, "expiration time", expirationTime);
        return TaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public TaskSummaryQueryBuilder expirationTimeRange(Date expirationTimeMin, Date expirationTimeMax) {
        addRangeParameters(QueryParameterIdentifiers.EXPIRATION_TIME_LIST, "expiration time range", expirationTimeMin, expirationTimeMax);
        return TaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public TaskSummaryQueryBuilder formName(String... formName) {
        addObjectParameter(QueryParameterIdentifiers.TASK_FORM_NAME_LIST, "form name", formName);
        return TaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public TaskSummaryQueryBuilder potentialOwner(String... potentialOwnerId) {
        addObjectParameter(QueryParameterIdentifiers.POTENTIAL_OWNER_ID_LIST, "potential owner id", potentialOwnerId);
        return TaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public TaskSummaryQueryBuilder processInstanceId(long... processInstanceId) {
        addLongParameter(QueryParameterIdentifiers.PROCESS_INSTANCE_ID_LIST, "process instance id", processInstanceId);
        return TaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public TaskSummaryQueryBuilder name(String... names) {
        addObjectParameter(QueryParameterIdentifiers.TASK_NAME_LIST, "task name", names);
        for (String name : names) {
            if ((name.length()) > 255) {
                throw new IllegalArgumentException((("String argument is longer than 255 characters: [" + name) + "]"));
            } 
        }
        return TaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public TaskSummaryQueryBuilder priority(int... priority) {
        addIntParameter(QueryParameterIdentifiers.TASK_PRIORITY_LIST, "priority", priority);
        return TaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public TaskSummaryQueryBuilder processId(String... processId) {
        addObjectParameter(QueryParameterIdentifiers.PROCESS_ID_LIST, "process id", processId);
        return TaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public TaskSummaryQueryBuilder processInstanceIdRange(Long processInstanceIdMin, Long processInstanceIdMax) {
        addRangeParameters(QueryParameterIdentifiers.PROCESS_INSTANCE_ID_LIST, "process instance id range", processInstanceIdMin, processInstanceIdMax);
        return TaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public TaskSummaryQueryBuilder processSessionId(long... processSessionId) {
        addLongParameter(QueryParameterIdentifiers.PROCESS_SESSION_ID_LIST, "process session id", processSessionId);
        return TaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public TaskSummaryQueryBuilder skippable(boolean skippable) {
        addObjectParameter(QueryParameterIdentifiers.SKIPPABLE, "skippable", skippable);
        return TaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public TaskSummaryQueryBuilder stakeHolder(String... stakeHolderId) {
        addObjectParameter(QueryParameterIdentifiers.STAKEHOLDER_ID_LIST, "stakeholder id", stakeHolderId);
        return TaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public TaskSummaryQueryBuilder status(Status... status) {
        addObjectParameter(QueryParameterIdentifiers.TASK_STATUS_LIST, "status", status);
        return TaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public TaskSummaryQueryBuilder subject(String... subjects) {
        addObjectParameter(QueryParameterIdentifiers.TASK_SUBJECT_LIST, "subject", subjects);
        for (String subject : subjects) {
            if ((subject.length()) > 255) {
                throw new IllegalArgumentException((("String argument is longer than 255 characters: [" + subject) + "]"));
            } 
        }
        return TaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public TaskSummaryQueryBuilder subTaskStrategy(SubTasksStrategy... subTasksStrategy) {
        addObjectParameter(QueryParameterIdentifiers.SUB_TASKS_STRATEGY, "sub tasks strategy", subTasksStrategy);
        return TaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public TaskSummaryQueryBuilder taskId(long... taskId) {
        addLongParameter(QueryParameterIdentifiers.TASK_ID_LIST, "task id", taskId);
        return TaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public TaskSummaryQueryBuilder taskIdRange(Long taskIdMin, Long taskIdMax) {
        addRangeParameters(QueryParameterIdentifiers.TASK_ID_LIST, "task id range", taskIdMin, taskIdMax);
        return TaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public TaskSummaryQueryBuilder taskParentId(long... taskParentId) {
        addLongParameter(QueryParameterIdentifiers.TASK_PARENT_ID_LIST, "task parent id", taskParentId);
        return TaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public TaskSummaryQueryBuilder taskType(String... taskType) {
        addObjectParameter(QueryParameterIdentifiers.TYPE_LIST, "created on", taskType);
        return TaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public TaskSummaryQueryBuilder variableName(String... varName) {
        addObjectParameter(QueryParameterIdentifiers.TASK_VARIABLE_NAME_ID_LIST, "task variable name", varName);
        return TaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public TaskSummaryQueryBuilder variableValue(String... varValue) {
        addObjectParameter(QueryParameterIdentifiers.TASK_VARIABLE_VALUE_ID_LIST, "task variable value", varValue);
        return TaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public TaskSummaryQueryBuilder workItemId(long... workItemId) {
        addLongParameter(QueryParameterIdentifiers.WORK_ITEM_ID_LIST, "work item id", workItemId);
        return TaskSummaryQueryBuilderImpl.this;
    }

    // Other methods
    @Override
    public TaskSummaryQueryBuilder clear() {
        super.clear();
        getQueryWhere().setAscending(QueryParameterIdentifiers.TASK_ID_LIST);
        return TaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public TaskSummaryQueryBuilder ascending(OrderBy field) {
        String listId = getOrderByListId(field);
        TaskSummaryQueryBuilderImpl.this.queryWhere.setAscending(listId);
        return TaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public TaskSummaryQueryBuilder descending(OrderBy field) {
        String listId = getOrderByListId(field);
        TaskSummaryQueryBuilderImpl.this.queryWhere.setDescending(listId);
        return TaskSummaryQueryBuilderImpl.this;
    }

    private String getOrderByListId(OrderBy field) {
        if (field == null) {
            throw new IllegalArgumentException("A null order by criteria is invalid.");
        } 
        String orderByString;
        switch (field) {
            case taskId :
                orderByString = QueryParameterIdentifiers.TASK_ID_LIST;
                break;
            case processInstanceId :
                orderByString = QueryParameterIdentifiers.PROCESS_INSTANCE_ID_LIST;
                break;
            case taskName :
                orderByString = QueryParameterIdentifiers.TASK_NAME_LIST;
                break;
            case taskStatus :
                orderByString = QueryParameterIdentifiers.TASK_STATUS_LIST;
                break;
            case createdOn :
                orderByString = QueryParameterIdentifiers.CREATED_ON_LIST;
                break;
            case createdBy :
                orderByString = QueryParameterIdentifiers.CREATED_BY_LIST;
                break;
            default :
                throw new UnsupportedOperationException(("Unsupported order by arqument: " + (field.toString())));
        }
        return orderByString;
    }

    @Override
    public ParametrizedQuery<TaskSummary> build() {
        return new org.kie.internal.query.ParametrizedQuery<TaskSummary>() {
            private QueryWhere queryWhere = new QueryWhere(getQueryWhere());

            @Override
            public List<TaskSummary> getResultList() {
                TaskSummaryQueryCommand cmd = new TaskSummaryQueryCommand(queryWhere);
                cmd.setUserId(userId);
                return executor.execute(cmd);
            }
        };
    }
}

