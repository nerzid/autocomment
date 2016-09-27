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


package org.jbpm.services.task.audit.service;

import org.jbpm.process.audit.query.AbstractAuditQueryBuilderImpl;
import org.kie.internal.task.api.AuditTask;
import org.jbpm.services.task.audit.impl.model.AuditTaskImpl;
import org.kie.internal.task.query.AuditTaskQueryBuilder;
import org.kie.api.runtime.CommandExecutor;
import java.util.Date;
import org.jbpm.process.audit.JPAAuditLogService;
import org.kie.internal.task.query.AuditTaskQueryBuilder.OrderBy;
import org.kie.internal.query.QueryParameterIdentifiers;
import org.kie.api.task.model.Status;

public class AuditTaskQueryBuilderImpl extends AbstractAuditQueryBuilderImpl<AuditTaskQueryBuilder, AuditTask> implements AuditTaskQueryBuilder {
    public AuditTaskQueryBuilderImpl(CommandExecutor cmdService) {
        super(cmdService);
    }

    public AuditTaskQueryBuilderImpl(JPAAuditLogService jpaAuditService) {
        super(jpaAuditService);
    }

    @Override
    public AuditTaskQueryBuilder taskId(long... taskId) {
        addLongParameter(QueryParameterIdentifiers.TASK_ID_LIST, "task id", taskId);
        return AuditTaskQueryBuilderImpl.this;
    }

    @Override
    public AuditTaskQueryBuilder taskIdRange(Long taskIdMin, Long taskIdMax) {
        long[] params = new long[]{ taskIdMin , taskIdMax };
        addRangeParameters(QueryParameterIdentifiers.TASK_ID_LIST, "task id", taskIdMin, taskIdMax);
        return AuditTaskQueryBuilderImpl.this;
    }

    @Override
    public AuditTaskQueryBuilder taskStatus(Status... status) {
        String[] stringStatuses = null;
        if (status != null) {
            stringStatuses = new String[status];
            for (int i = 0; i < status; ++i) {
                stringStatuses[i] = status[i].toString();
            }
        } 
        addObjectParameter(QueryParameterIdentifiers.TASK_STATUS_LIST, "task status", stringStatuses);
        return AuditTaskQueryBuilderImpl.this;
    }

    @Override
    public AuditTaskQueryBuilder actualOwner(String... actualOwnerUserId) {
        addObjectParameter(QueryParameterIdentifiers.ACTUAL_OWNER_ID_LIST, "actual owner", actualOwnerUserId);
        return AuditTaskQueryBuilderImpl.this;
    }

    @Override
    public AuditTaskQueryBuilder deploymentId(String... deploymentId) {
        addObjectParameter(QueryParameterIdentifiers.DEPLOYMENT_ID_LIST, "deployment id", deploymentId);
        return AuditTaskQueryBuilderImpl.this;
    }

    @Override
    public AuditTaskQueryBuilder id(long... id) {
        addLongParameter(QueryParameterIdentifiers.ID_LIST, "id", id);
        return AuditTaskQueryBuilderImpl.this;
    }

    @Override
    public AuditTaskQueryBuilder createdOn(Date... createdOn) {
        addObjectParameter(QueryParameterIdentifiers.CREATED_ON_LIST, "created on", createdOn);
        return AuditTaskQueryBuilderImpl.this;
    }

    @Override
    public AuditTaskQueryBuilder createdOnRange(Date createdOnMin, Date createdOnMax) {
        addRangeParameters(QueryParameterIdentifiers.CREATED_ON_LIST, "created on", createdOnMin, createdOnMax);
        return AuditTaskQueryBuilderImpl.this;
    }

    @Override
    public AuditTaskQueryBuilder taskParentId(long... parentId) {
        addLongParameter(QueryParameterIdentifiers.TASK_PARENT_ID_LIST, "parent id", parentId);
        return AuditTaskQueryBuilderImpl.this;
    }

    @Override
    public AuditTaskQueryBuilder createdBy(String... createdByUserId) {
        addObjectParameter(QueryParameterIdentifiers.CREATED_BY_LIST, "created by", createdByUserId);
        return AuditTaskQueryBuilderImpl.this;
    }

    @Override
    public AuditTaskQueryBuilder activationTime(Date... activationTime) {
        addObjectParameter(QueryParameterIdentifiers.TASK_ACTIVATION_TIME_LIST, "activation time", activationTime);
        return AuditTaskQueryBuilderImpl.this;
    }

    @Override
    public AuditTaskQueryBuilder activationTimeRange(Date activationTimeMin, Date activationTimeMax) {
        addRangeParameters(QueryParameterIdentifiers.TASK_ACTIVATION_TIME_LIST, "activation time", activationTimeMin, activationTimeMax);
        return AuditTaskQueryBuilderImpl.this;
    }

    @Override
    public AuditTaskQueryBuilder taskName(String... name) {
        addObjectParameter(QueryParameterIdentifiers.TASK_NAME_LIST, "task name", name);
        return AuditTaskQueryBuilderImpl.this;
    }

    @Override
    public AuditTaskQueryBuilder description(String... description) {
        addObjectParameter(QueryParameterIdentifiers.TASK_DESCRIPTION_LIST, "task description", description);
        return AuditTaskQueryBuilderImpl.this;
    }

    @Override
    public AuditTaskQueryBuilder workItemId(long... workItemId) {
        addLongParameter(QueryParameterIdentifiers.WORK_ITEM_ID_LIST, "work item id", workItemId);
        return AuditTaskQueryBuilderImpl.this;
    }

    @Override
    public AuditTaskQueryBuilder priority(int... priority) {
        addIntParameter(QueryParameterIdentifiers.TASK_PRIORITY_LIST, "priority", priority);
        return AuditTaskQueryBuilderImpl.this;
    }

    @Override
    public AuditTaskQueryBuilder processSessionId(long... processSessionId) {
        addLongParameter(QueryParameterIdentifiers.TASK_PROCESS_SESSION_ID_LIST, "priority session id", processSessionId);
        return AuditTaskQueryBuilderImpl.this;
    }

    @Override
    public AuditTaskQueryBuilder dueDate(Date... dueDate) {
        addObjectParameter(QueryParameterIdentifiers.TASK_DUE_DATE_LIST, "due date", dueDate);
        return AuditTaskQueryBuilderImpl.this;
    }

    @Override
    public AuditTaskQueryBuilder dueDateRange(Date dueDateMin, Date dueDateMax) {
        addRangeParameters(QueryParameterIdentifiers.TASK_DUE_DATE_LIST, "due date", dueDateMin, dueDateMax);
        return AuditTaskQueryBuilderImpl.this;
    }

    @Override
    public AuditTaskQueryBuilder ascending(OrderBy field) {
        String listId = convertOrderByToListId(field);
        AuditTaskQueryBuilderImpl.this.queryWhere.setAscending(listId);
        return AuditTaskQueryBuilderImpl.this;
    }

    @Override
    public AuditTaskQueryBuilder descending(OrderBy field) {
        String listId = convertOrderByToListId(field);
        AuditTaskQueryBuilderImpl.this.queryWhere.setDescending(listId);
        return AuditTaskQueryBuilderImpl.this;
    }

    private String convertOrderByToListId(OrderBy field) {
        String listId;
        switch (field) {
            case activationTime :
                listId = QueryParameterIdentifiers.TASK_ACTIVATION_TIME_LIST;
                break;
            case taskId :
                listId = QueryParameterIdentifiers.TASK_ID_LIST;
                break;
            case createdOn :
                listId = QueryParameterIdentifiers.CREATED_ON_LIST;
                break;
            case processId :
                listId = QueryParameterIdentifiers.PROCESS_ID_LIST;
                break;
            case processInstanceId :
                listId = QueryParameterIdentifiers.PROCESS_INSTANCE_ID_LIST;
                break;
            default :
                throw new IllegalArgumentException(("Unknown 'order-by' field: " + (field.toString())));
        }
        return listId;
    }

    @Override
    protected Class<AuditTask> getResultType() {
        return AuditTask.class;
    }

    @Override
    protected Class<AuditTaskImpl> getQueryType() {
        return AuditTaskImpl.class;
    }
}

