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


package org.jbpm.executor.impl.jpa;

import org.jbpm.query.jpa.builder.impl.AbstractQueryBuilderImpl;
import java.util.Date;
import org.kie.api.executor.ErrorInfo;
import org.kie.internal.runtime.manager.audit.query.ErrorInfoQueryBuilder;
import java.util.List;
import ErrorInfoQueryBuilder.OrderBy;
import org.jbpm.query.jpa.data.QueryWhere.QueryCriteriaType;
import org.kie.internal.query.QueryParameterIdentifiers;
import org.jbpm.query.jpa.data.QueryWhere;

public class ErrorInfoQueryBuilderImpl extends AbstractQueryBuilderImpl<ErrorInfoQueryBuilder> implements ErrorInfoQueryBuilder {
    private final ExecutorJPAAuditService jpaAuditService;

    public ErrorInfoQueryBuilderImpl(ExecutorJPAAuditService jpaAuditService) {
        this.jpaAuditService = jpaAuditService;
    }

    @Override
    public ErrorInfoQueryBuilder message(String... message) {
        addObjectParameter(QueryParameterIdentifiers.MESSAGE_LIST, "message", message);
        return ErrorInfoQueryBuilderImpl.this;
    }

    @Override
    public ErrorInfoQueryBuilder id(long... id) {
        addLongParameter(QueryParameterIdentifiers.ID_LIST, "id", id);
        return ErrorInfoQueryBuilderImpl.this;
    }

    @Override
    public ErrorInfoQueryBuilder time(Date... time) {
        addObjectParameter(EXECUTOR_TIME_LIST, "time", time);
        return ErrorInfoQueryBuilderImpl.this;
    }

    @Override
    public ErrorInfoQueryBuilder timeRange(Date timeMin, Date timeMax) {
        addRangeParameters(EXECUTOR_TIME_LIST, "time", timeMin, timeMax);
        return ErrorInfoQueryBuilderImpl.this;
    }

    @Override
    public ErrorInfoQueryBuilder stackTraceRegex(String... stackTraceRegex) {
        QueryWhere queryWhere = getQueryWhere();
        QueryCriteriaType origCriteriaType = queryWhere.getCriteriaType();
        queryWhere.setToLike();
        addObjectParameter(QueryParameterIdentifiers.STACK_TRACE_LIST, "stack trace regex", stackTraceRegex);
        switch (origCriteriaType) {
            case NORMAL :
                queryWhere.setToNormal();
                break;
            case RANGE :
                queryWhere.setToRange();
                break;
            case GROUP :
                queryWhere.setToGroup();
                break;
            case REGEXP :
                // already at like
        }
        return ErrorInfoQueryBuilderImpl.this;
    }

    @Override
    public ErrorInfoQueryBuilder ascending(OrderBy field) {
        String listId = convertOrderByToListId(field);
        ErrorInfoQueryBuilderImpl.this.queryWhere.setAscending(listId);
        return ErrorInfoQueryBuilderImpl.this;
    }

    @Override
    public ErrorInfoQueryBuilder descending(OrderBy field) {
        String listId = convertOrderByToListId(field);
        ErrorInfoQueryBuilderImpl.this.queryWhere.setDescending(listId);
        return ErrorInfoQueryBuilderImpl.this;
    }

    private String convertOrderByToListId(OrderBy field) {
        String listId;
        switch (field) {
            case id :
                listId = QueryParameterIdentifiers.QueryParameterIdentifiers.ID_LIST;
                break;
            case time :
                listId = QueryParameterIdentifiers.QueryParameterIdentifiers.EXECUTOR_TIME_LIST;
                break;
            default :
                throw new IllegalArgumentException(("Unknown 'order-by' field: " + (field.toString())));
        }
        return listId;
    }

    @Override
    public ParametrizedQuery<ErrorInfo> build() {
        return new org.kie.internal.query.ParametrizedQuery<ErrorInfo>() {
            private QueryWhere queryData = new QueryWhere(getQueryWhere());

            @Override
            public List<ErrorInfo> getResultList() {
                return jpaAuditService.queryLogs(queryData, ErrorInfo.class, ErrorInfo.class);
            }
        };
    }
}

