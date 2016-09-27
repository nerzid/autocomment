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

import org.jbpm.process.audit.query.AbstractAuditDeleteBuilderImpl;
import org.kie.api.runtime.CommandExecutor;
import java.util.Date;
import org.jbpm.executor.entities.ErrorInfo;
import org.kie.internal.runtime.manager.audit.query.ErrorInfoDeleteBuilder;
import org.jbpm.process.audit.JPAAuditLogService;
import org.kie.internal.query.QueryParameterIdentifiers;

public class ErrorInfoDeleteBuilderImpl extends AbstractAuditDeleteBuilderImpl<ErrorInfoDeleteBuilder> implements ErrorInfoDeleteBuilder {
    private static String ERROR_INFO_LOG_DELETE = "DELETE\n" + "FROM ErrorInfo l\n";

    public ErrorInfoDeleteBuilderImpl(CommandExecutor cmdExecutor) {
        super(cmdExecutor);
        intersect();
    }

    public ErrorInfoDeleteBuilderImpl(JPAAuditLogService jpaAuditService) {
        super(jpaAuditService);
        intersect();
    }

    @Override
    public ErrorInfoDeleteBuilder date(Date... date) {
        if (checkIfNull(date)) {
            return ErrorInfoDeleteBuilderImpl.this;
        } 
        addObjectParameter(QueryParameterIdentifiers.EXECUTOR_TIME_LIST, "date", ensureDateNotTimestamp(date));
        return ErrorInfoDeleteBuilderImpl.this;
    }

    @Override
    public ErrorInfoDeleteBuilder dateRangeStart(Date rangeStart) {
        if (checkIfNull(rangeStart)) {
            return ErrorInfoDeleteBuilderImpl.this;
        } 
        addRangeParameter(QueryParameterIdentifiers.EXECUTOR_TIME_LIST, "date range end", ensureDateNotTimestamp(rangeStart)[0], true);
        return ErrorInfoDeleteBuilderImpl.this;
    }

    @Override
    public ErrorInfoDeleteBuilder dateRangeEnd(Date rangeStart) {
        if (checkIfNull(rangeStart)) {
            return ErrorInfoDeleteBuilderImpl.this;
        } 
        addRangeParameter(QueryParameterIdentifiers.EXECUTOR_TIME_LIST, "date range end", ensureDateNotTimestamp(rangeStart)[0], false);
        return ErrorInfoDeleteBuilderImpl.this;
    }

    @Override
    protected Class getQueryType() {
        return ErrorInfo.class;
    }

    @Override
    protected String getQueryBase() {
        return ErrorInfoDeleteBuilderImpl.ERROR_INFO_LOG_DELETE;
    }
}
