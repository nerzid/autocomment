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
import org.kie.api.runtime.CommandExecutor;
import java.util.Date;
import org.jbpm.process.audit.JPAAuditLogService;
import org.kie.internal.task.query.TaskEventQueryBuilder.OrderBy;
import org.kie.internal.query.QueryParameterIdentifiers;
import org.kie.internal.task.api.model.TaskEvent;
import org.jbpm.services.task.audit.impl.model.TaskEventImpl;
import org.kie.internal.task.query.TaskEventQueryBuilder;
import org.kie.internal.task.api.model.TaskEvent.TaskEventType;

public class TaskEventQueryBuilderImpl extends AbstractAuditQueryBuilderImpl<TaskEventQueryBuilder, TaskEvent> implements TaskEventQueryBuilder {
    public TaskEventQueryBuilderImpl(CommandExecutor cmdService) {
        super(cmdService);
    }

    public TaskEventQueryBuilderImpl(JPAAuditLogService jpaAuditService) {
        super(jpaAuditService);
    }

    @Override
    public TaskEventQueryBuilder message(String... name) {
        addObjectParameter(QueryParameterIdentifiers.MESSAGE_LIST, "message", name);
        return TaskEventQueryBuilderImpl.this;
    }

    @Override
    public TaskEventQueryBuilder taskId(long... taskId) {
        addLongParameter(QueryParameterIdentifiers.TASK_ID_LIST, "task id", taskId);
        return TaskEventQueryBuilderImpl.this;
    }

    @Override
    public TaskEventQueryBuilder taskIdRange(Long taskIdMin, Long taskIdMax) {
        addRangeParameters(QueryParameterIdentifiers.TASK_ID_LIST, "task id range", taskIdMin, taskIdMax);
        return TaskEventQueryBuilderImpl.this;
    }

    @Override
    public TaskEventQueryBuilder id(long... id) {
        addLongParameter(QueryParameterIdentifiers.ID_LIST, "task id", id);
        return TaskEventQueryBuilderImpl.this;
    }

    @Override
    public TaskEventQueryBuilder logTime(Date... logTime) {
        addObjectParameter(QueryParameterIdentifiers.DATE_LIST, "log time", logTime);
        return TaskEventQueryBuilderImpl.this;
    }

    @Override
    public TaskEventQueryBuilder logTimeRange(Date logTimeMin, Date logTimeMax) {
        addRangeParameters(QueryParameterIdentifiers.DATE_LIST, "log time range", logTimeMin, logTimeMax);
        return TaskEventQueryBuilderImpl.this;
    }

    @Override
    public TaskEventQueryBuilder userId(String... userId) {
        addObjectParameter(QueryParameterIdentifiers.USER_ID_LIST, "user id", userId);
        return TaskEventQueryBuilderImpl.this;
    }

    @Override
    public TaskEventQueryBuilder workItemId(long... workItemId) {
        addLongParameter(QueryParameterIdentifiers.WORK_ITEM_ID_LIST, "work item id", workItemId);
        return TaskEventQueryBuilderImpl.this;
    }

    @Override
    public TaskEventQueryBuilder type(TaskEventType... taskEventType) {
        addObjectParameter(QueryParameterIdentifiers.TYPE_LIST, "task event type", taskEventType);
        return TaskEventQueryBuilderImpl.this;
    }

    @Override
    public TaskEventQueryBuilder ascending(OrderBy field) {
        String listId = convertOrderByToListId(field);
        TaskEventQueryBuilderImpl.this.queryWhere.setAscending(listId);
        return TaskEventQueryBuilderImpl.this;
    }

    @Override
    public TaskEventQueryBuilder descending(OrderBy field) {
        String listId = convertOrderByToListId(field);
        TaskEventQueryBuilderImpl.this.queryWhere.setDescending(listId);
        return TaskEventQueryBuilderImpl.this;
    }

    private String convertOrderByToListId(OrderBy field) {
        String listId;
        switch (field) {
            case taskId :
                listId = QueryParameterIdentifiers.TASK_ID_LIST;
                break;
            case logTime :
                listId = QueryParameterIdentifiers.DATE_LIST;
            case processInstanceId :
                listId = QueryParameterIdentifiers.PROCESS_INSTANCE_ID_LIST;
                break;
            default :
                throw new IllegalArgumentException(("Unknown 'order-by' field: " + (field.toString())));
        }
        return listId;
    }

    @Override
    protected Class<TaskEvent> getResultType() {
        return TaskEvent.class;
    }

    @Override
    protected Class<TaskEventImpl> getQueryType() {
        return TaskEventImpl.class;
    }
}

