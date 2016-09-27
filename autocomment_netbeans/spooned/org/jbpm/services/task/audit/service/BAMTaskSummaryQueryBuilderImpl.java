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
import org.jbpm.services.task.audit.impl.model.BAMTaskSummaryImpl;
import org.jbpm.services.task.audit.BAMTaskSummaryQueryBuilder;
import org.kie.api.runtime.CommandExecutor;
import java.util.Date;
import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.services.task.audit.BAMTaskSummaryQueryBuilder.OrderBy;
import org.kie.internal.query.QueryParameterIdentifiers;
import org.kie.api.task.model.Status;

public class BAMTaskSummaryQueryBuilderImpl extends AbstractAuditQueryBuilderImpl<BAMTaskSummaryQueryBuilder, BAMTaskSummaryImpl> implements BAMTaskSummaryQueryBuilder {
    public BAMTaskSummaryQueryBuilderImpl(CommandExecutor cmdService) {
        super(cmdService);
    }

    public BAMTaskSummaryQueryBuilderImpl(JPAAuditLogService jpaAuditService) {
        super(jpaAuditService);
    }

    @Override
    public BAMTaskSummaryQueryBuilder taskId(long... taskId) {
        addLongParameter(QueryParameterIdentifiers.TASK_ID_LIST, "task id", taskId);
        return BAMTaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public BAMTaskSummaryQueryBuilder taskIdRange(Long taskIdMin, Long taskIdMax) {
        addRangeParameters(QueryParameterIdentifiers.TASK_ID_LIST, "task id", taskIdMin, taskIdMax);
        return BAMTaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public BAMTaskSummaryQueryBuilder startDate(Date... startDate) {
        addObjectParameter(QueryParameterIdentifiers.START_DATE_LIST, "start date", startDate);
        return BAMTaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public BAMTaskSummaryQueryBuilder startDateRange(Date startDateMin, Date startDateMax) {
        addRangeParameters(QueryParameterIdentifiers.START_DATE_LIST, "start date", startDateMin, startDateMax);
        return BAMTaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public BAMTaskSummaryQueryBuilder duration(long... duration) {
        addLongParameter(QueryParameterIdentifiers.DURATION_LIST, "duration", duration);
        return BAMTaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public BAMTaskSummaryQueryBuilder taskStatus(Status... status) {
        String[] stringStatuses = null;
        if (status != null) {
            stringStatuses = new String[status];
            for (int i = 0; i < status; ++i) {
                stringStatuses[i] = status[i].toString();
            }
        } 
        addObjectParameter(QueryParameterIdentifiers.TASK_STATUS_LIST, "task status", stringStatuses);
        return BAMTaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public BAMTaskSummaryQueryBuilder userId(String... userId) {
        addObjectParameter(QueryParameterIdentifiers.USER_ID_LIST, "user id", userId);
        return BAMTaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public BAMTaskSummaryQueryBuilder endDate(Date... endDate) {
        addObjectParameter(QueryParameterIdentifiers.END_DATE_LIST, "end date", endDate);
        return BAMTaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public BAMTaskSummaryQueryBuilder endDateRange(Date endDateMin, Date endDateMax) {
        addRangeParameters(QueryParameterIdentifiers.END_DATE_LIST, "end date", endDateMin, endDateMax);
        return BAMTaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public BAMTaskSummaryQueryBuilder createdOn(Date... createdOn) {
        addObjectParameter(QueryParameterIdentifiers.CREATED_ON_LIST, "created on", createdOn);
        return BAMTaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public BAMTaskSummaryQueryBuilder createdOnRange(Date createdOnMin, Date createdOnMax) {
        addRangeParameters(QueryParameterIdentifiers.CREATED_ON_LIST, "created on", createdOnMin, createdOnMax);
        return BAMTaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public BAMTaskSummaryQueryBuilder taskName(String... name) {
        addObjectParameter(QueryParameterIdentifiers.TASK_NAME_LIST, "task name", name);
        return BAMTaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public BAMTaskSummaryQueryBuilder id(long... id) {
        addLongParameter(QueryParameterIdentifiers.ID_LIST, "id", id);
        return BAMTaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public BAMTaskSummaryQueryBuilder ascending(OrderBy field) {
        String listId = convertOrderByToListId(field);
        BAMTaskSummaryQueryBuilderImpl.this.queryWhere.setAscending(listId);
        return BAMTaskSummaryQueryBuilderImpl.this;
    }

    @Override
    public BAMTaskSummaryQueryBuilder descending(OrderBy field) {
        String listId = convertOrderByToListId(field);
        BAMTaskSummaryQueryBuilderImpl.this.queryWhere.setDescending(listId);
        return BAMTaskSummaryQueryBuilderImpl.this;
    }

    private String convertOrderByToListId(OrderBy field) {
        String listId;
        switch (field) {
            case taskId :
                listId = QueryParameterIdentifiers.TASK_ID_LIST;
                break;
            case startDate :
                listId = QueryParameterIdentifiers.START_DATE_LIST;
                break;
            case endDate :
                listId = QueryParameterIdentifiers.END_DATE_LIST;
                break;
            case createdDate :
                listId = QueryParameterIdentifiers.CREATED_ON_LIST;
                break;
            case taskName :
                listId = QueryParameterIdentifiers.TASK_NAME_LIST;
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
    protected Class<BAMTaskSummaryImpl> getResultType() {
        return BAMTaskSummaryImpl.class;
    }

    @Override
    protected Class<BAMTaskSummaryImpl> getQueryType() {
        return BAMTaskSummaryImpl.class;
    }
}

