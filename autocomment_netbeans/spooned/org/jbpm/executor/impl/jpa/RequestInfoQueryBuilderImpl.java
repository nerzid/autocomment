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
import java.util.List;
import RequestInfoQueryBuilder.OrderBy;
import org.kie.internal.query.QueryParameterIdentifiers;
import org.jbpm.query.jpa.data.QueryWhere;
import org.kie.api.executor.RequestInfo;
import org.kie.internal.runtime.manager.audit.query.RequestInfoQueryBuilder;
import org.kie.api.executor.STATUS;

public class RequestInfoQueryBuilderImpl extends AbstractQueryBuilderImpl<RequestInfoQueryBuilder> implements RequestInfoQueryBuilder {
    private final ExecutorJPAAuditService jpaAuditService;

    public RequestInfoQueryBuilderImpl(ExecutorJPAAuditService jpaAuditService) {
        this.jpaAuditService = jpaAuditService;
    }

    @Override
    public RequestInfoQueryBuilder commandName(String... commandName) {
        addObjectParameter(COMMAND_NAME_LIST, "command name", commandName);
        return RequestInfoQueryBuilderImpl.this;
    }

    @Override
    public RequestInfoQueryBuilder deploymentId(String... deploymentId) {
        addObjectParameter(QueryParameterIdentifiers.DEPLOYMENT_ID_LIST, "deployment id", deploymentId);
        return RequestInfoQueryBuilderImpl.this;
    }

    @Override
    public RequestInfoQueryBuilder executions(int... executions) {
        addIntParameter(QueryParameterIdentifiers.EXECUTOR_EXECUTIONS_LIST, "executions", executions);
        return RequestInfoQueryBuilderImpl.this;
    }

    @Override
    public RequestInfoQueryBuilder id(long... id) {
        addLongParameter(QueryParameterIdentifiers.ID_LIST, "id", id);
        return RequestInfoQueryBuilderImpl.this;
    }

    @Override
    public RequestInfoQueryBuilder key(String... key) {
        addObjectParameter(QueryParameterIdentifiers.EXECUTOR_KEY_LIST, "key", key);
        return RequestInfoQueryBuilderImpl.this;
    }

    @Override
    public RequestInfoQueryBuilder message(String... message) {
        addObjectParameter(QueryParameterIdentifiers.MESSAGE_LIST, "message", message);
        return RequestInfoQueryBuilderImpl.this;
    }

    @Override
    public RequestInfoQueryBuilder owner(String... owner) {
        addObjectParameter(QueryParameterIdentifiers.EXECUTOR_OWNER_LIST, "owner", owner);
        return RequestInfoQueryBuilderImpl.this;
    }

    @Override
    public RequestInfoQueryBuilder retries(int... retries) {
        addIntParameter(QueryParameterIdentifiers.EXECUTOR_RETRIES_LIST, "retries", retries);
        return RequestInfoQueryBuilderImpl.this;
    }

    @Override
    public RequestInfoQueryBuilder status(STATUS... status) {
        addObjectParameter(QueryParameterIdentifiers.EXECUTOR_STATUS_LIST, "status", status);
        return RequestInfoQueryBuilderImpl.this;
    }

    @Override
    public RequestInfoQueryBuilder time(Date... time) {
        addObjectParameter(EXECUTOR_TIME_LIST, "time", time);
        return RequestInfoQueryBuilderImpl.this;
    }

    @Override
    public RequestInfoQueryBuilder timeRange(Date timeMin, Date timeMax) {
        addRangeParameters(EXECUTOR_TIME_LIST, "time", timeMin, timeMax);
        return RequestInfoQueryBuilderImpl.this;
    }

    @Override
    public RequestInfoQueryBuilder ascending(OrderBy field) {
        String listId = convertOrderByToListId(field);
        RequestInfoQueryBuilderImpl.this.queryWhere.setAscending(listId);
        return RequestInfoQueryBuilderImpl.this;
    }

    @Override
    public RequestInfoQueryBuilder descending(OrderBy field) {
        String listId = convertOrderByToListId(field);
        RequestInfoQueryBuilderImpl.this.queryWhere.setDescending(listId);
        return RequestInfoQueryBuilderImpl.this;
    }

    private String convertOrderByToListId(OrderBy field) {
        String listId;
        switch (field) {
            case deploymentId :
                listId = QueryParameterIdentifiers.QueryParameterIdentifiers.DEPLOYMENT_ID_LIST;
                break;
            case executions :
                listId = QueryParameterIdentifiers.QueryParameterIdentifiers.EXECUTOR_EXECUTIONS_LIST;
                break;
            case id :
                listId = QueryParameterIdentifiers.QueryParameterIdentifiers.ID_LIST;
                break;
            case retries :
                listId = QueryParameterIdentifiers.QueryParameterIdentifiers.EXECUTOR_RETRIES_LIST;
                break;
            case status :
                listId = QueryParameterIdentifiers.QueryParameterIdentifiers.EXECUTOR_STATUS_LIST;
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
    public ParametrizedQuery<RequestInfo> build() {
        return new org.kie.internal.query.ParametrizedQuery<RequestInfo>() {
            private QueryWhere queryData = new QueryWhere(getQueryWhere());

            @Override
            public List<RequestInfo> getResultList() {
                return jpaAuditService.queryLogs(queryData, RequestInfo.class, RequestInfo.class);
            }
        };
    }
}

