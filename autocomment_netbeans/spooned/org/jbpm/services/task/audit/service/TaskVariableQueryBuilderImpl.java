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

import java.util.Date;
import org.kie.internal.task.api.InternalTaskService;
import java.util.List;
import org.kie.internal.task.query.TaskVariableQueryBuilder.OrderBy;
import org.kie.internal.query.QueryParameterIdentifiers;
import org.kie.internal.task.api.TaskVariable;
import org.jbpm.services.task.audit.impl.model.TaskVariableImpl;
import org.kie.internal.task.query.TaskVariableQueryBuilder;
import org.kie.internal.task.api.TaskVariable.VariableType;

public class TaskVariableQueryBuilderImpl extends AbstractTaskAuditQueryBuilderImpl<TaskVariableQueryBuilder, TaskVariable> implements TaskVariableQueryBuilder {
    public TaskVariableQueryBuilderImpl(InternalTaskService taskService) {
        super(taskService);
    }

    public TaskVariableQueryBuilderImpl(TaskJPAAuditService jpaService) {
        super(jpaService);
    }

    @Override
    public TaskVariableQueryBuilder taskId(long... taskId) {
        addLongParameter(QueryParameterIdentifiers.TASK_ID_LIST, "task id", taskId);
        return TaskVariableQueryBuilderImpl.this;
    }

    @Override
    public TaskVariableQueryBuilder taskIdRange(Long taskIdMin, Long taskIdMax) {
        addRangeParameters(QueryParameterIdentifiers.TASK_ID_LIST, "task id range", taskIdMin, taskIdMax);
        return TaskVariableQueryBuilderImpl.this;
    }

    @Override
    public TaskVariableQueryBuilder id(long... id) {
        addLongParameter(QueryParameterIdentifiers.ID_LIST, "task id", id);
        return TaskVariableQueryBuilderImpl.this;
    }

    @Override
    public TaskVariableQueryBuilder modificationDate(Date... modDate) {
        addObjectParameter(QueryParameterIdentifiers.DATE_LIST, "log time", modDate);
        return TaskVariableQueryBuilderImpl.this;
    }

    @Override
    public TaskVariableQueryBuilder modificationDateRange(Date modDateMin, Date modDateMax) {
        addRangeParameters(QueryParameterIdentifiers.DATE_LIST, "log time range", modDateMin, modDateMax);
        return TaskVariableQueryBuilderImpl.this;
    }

    @Override
    public TaskVariableQueryBuilder name(String... name) {
        addObjectParameter(QueryParameterIdentifiers.TASK_VARIABLE_NAME_ID_LIST, "name", name);
        return TaskVariableQueryBuilderImpl.this;
    }

    @Override
    public TaskVariableQueryBuilder value(String... value) {
        addObjectParameter(QueryParameterIdentifiers.TASK_VARIABLE_VALUE_ID_LIST, "value", value);
        return TaskVariableQueryBuilderImpl.this;
    }

    @Override
    public TaskVariableQueryBuilder type(VariableType... type) {
        addObjectParameter(QueryParameterIdentifiers.TYPE_LIST, "task variable type", type);
        return TaskVariableQueryBuilderImpl.this;
    }

    @Override
    public TaskVariableQueryBuilder ascending(OrderBy field) {
        String listId = convertOrderByToListId(field);
        TaskVariableQueryBuilderImpl.this.queryWhere.setAscending(listId);
        return TaskVariableQueryBuilderImpl.this;
    }

    @Override
    public TaskVariableQueryBuilder descending(OrderBy field) {
        String listId = convertOrderByToListId(field);
        TaskVariableQueryBuilderImpl.this.queryWhere.setDescending(listId);
        return TaskVariableQueryBuilderImpl.this;
    }

    private String convertOrderByToListId(OrderBy field) {
        String listId;
        switch (field) {
            case id :
                listId = QueryParameterIdentifiers.ID_LIST;
                break;
            case taskId :
                listId = QueryParameterIdentifiers.TASK_ID_LIST;
                break;
            case modificationDate :
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
    protected Class<TaskVariableImpl> getQueryType() {
        return TaskVariableImpl.class;
    }

    @Override
    protected Class<TaskVariable> getResultType() {
        return TaskVariable.class;
    }

    @Override
    protected TaskCommand<List<TaskVariable>> getCommand() {
        return new org.jbpm.services.task.audit.commands.TaskVariableQueryCommand(queryWhere);
    }
}

