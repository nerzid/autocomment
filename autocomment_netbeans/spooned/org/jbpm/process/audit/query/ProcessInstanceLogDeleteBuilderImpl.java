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


package org.jbpm.process.audit.query;

import org.kie.api.runtime.CommandExecutor;
import java.util.Date;
import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.kie.internal.runtime.manager.audit.query.ProcessInstanceLogDeleteBuilder;
import org.kie.internal.query.QueryParameterIdentifiers;

public class ProcessInstanceLogDeleteBuilderImpl extends AbstractAuditDeleteBuilderImpl<ProcessInstanceLogDeleteBuilder> implements ProcessInstanceLogDeleteBuilder {
    private static String PROCESS_INSTANCE_LOG_DELETE = "DELETE\n" + "FROM ProcessInstanceLog l\n";

    public ProcessInstanceLogDeleteBuilderImpl(JPAAuditLogService jpaService) {
        super(jpaService);
        intersect();
    }

    public ProcessInstanceLogDeleteBuilderImpl(CommandExecutor cmdExecutor) {
        super(cmdExecutor);
        intersect();
    }

    @Override
    public ProcessInstanceLogDeleteBuilder status(int... status) {
        if (checkIfNull(status)) {
            return ProcessInstanceLogDeleteBuilderImpl.this;
        } 
        addIntParameter(QueryParameterIdentifiers.PROCESS_INSTANCE_STATUS_LIST, "status", status);
        return ProcessInstanceLogDeleteBuilderImpl.this;
    }

    @Override
    public ProcessInstanceLogDeleteBuilder outcome(String... outcome) {
        if (checkIfNull(outcome)) {
            return ProcessInstanceLogDeleteBuilderImpl.this;
        } 
        addObjectParameter(QueryParameterIdentifiers.OUTCOME_LIST, "outcome", outcome);
        return ProcessInstanceLogDeleteBuilderImpl.this;
    }

    @Override
    public ProcessInstanceLogDeleteBuilder identity(String... identity) {
        if (checkIfNull(identity)) {
            return ProcessInstanceLogDeleteBuilderImpl.this;
        } 
        addObjectParameter(QueryParameterIdentifiers.IDENTITY_LIST, "identity", identity);
        return ProcessInstanceLogDeleteBuilderImpl.this;
    }

    @Override
    public ProcessInstanceLogDeleteBuilder processVersion(String... version) {
        if (checkIfNull(version)) {
            return ProcessInstanceLogDeleteBuilderImpl.this;
        } 
        addObjectParameter(QueryParameterIdentifiers.PROCESS_VERSION_LIST, "process version", version);
        return ProcessInstanceLogDeleteBuilderImpl.this;
    }

    @Override
    public ProcessInstanceLogDeleteBuilder processName(String... processName) {
        if (checkIfNull(processName)) {
            return ProcessInstanceLogDeleteBuilderImpl.this;
        } 
        addObjectParameter(QueryParameterIdentifiers.PROCESS_NAME_LIST, "process name", processName);
        return ProcessInstanceLogDeleteBuilderImpl.this;
    }

    @Override
    public ProcessInstanceLogDeleteBuilder startDate(Date... date) {
        if (checkIfNull(date)) {
            return ProcessInstanceLogDeleteBuilderImpl.this;
        } 
        addObjectParameter(QueryParameterIdentifiers.START_DATE_LIST, "start date", ensureDateNotTimestamp(date));
        return ProcessInstanceLogDeleteBuilderImpl.this;
    }

    @Override
    public ProcessInstanceLogDeleteBuilder startDateRangeStart(Date rangeStart) {
        if (checkIfNull(rangeStart)) {
            return ProcessInstanceLogDeleteBuilderImpl.this;
        } 
        addRangeParameter(QueryParameterIdentifiers.START_DATE_LIST, "start date range, start", ensureDateNotTimestamp(rangeStart)[0], true);
        return ProcessInstanceLogDeleteBuilderImpl.this;
    }

    @Override
    public ProcessInstanceLogDeleteBuilder startDateRangeEnd(Date rangeEnd) {
        if (checkIfNull(rangeEnd)) {
            return ProcessInstanceLogDeleteBuilderImpl.this;
        } 
        addRangeParameter(QueryParameterIdentifiers.START_DATE_LIST, "start date range, end", ensureDateNotTimestamp(rangeEnd)[0], false);
        return ProcessInstanceLogDeleteBuilderImpl.this;
    }

    @Override
    public ProcessInstanceLogDeleteBuilder endDate(Date... date) {
        if (checkIfNull(date)) {
            return ProcessInstanceLogDeleteBuilderImpl.this;
        } 
        addObjectParameter(QueryParameterIdentifiers.END_DATE_LIST, "end date", ensureDateNotTimestamp(date));
        return ProcessInstanceLogDeleteBuilderImpl.this;
    }

    @Override
    public ProcessInstanceLogDeleteBuilder endDateRangeStart(Date rangeStart) {
        if (checkIfNull(rangeStart)) {
            return ProcessInstanceLogDeleteBuilderImpl.this;
        } 
        addRangeParameter(QueryParameterIdentifiers.END_DATE_LIST, "end date range, start", ensureDateNotTimestamp(rangeStart)[0], true);
        return ProcessInstanceLogDeleteBuilderImpl.this;
    }

    @Override
    public ProcessInstanceLogDeleteBuilder endDateRangeEnd(Date rangeEnd) {
        if (checkIfNull(rangeEnd)) {
            return ProcessInstanceLogDeleteBuilderImpl.this;
        } 
        addRangeParameter(QueryParameterIdentifiers.END_DATE_LIST, "end date range, end", ensureDateNotTimestamp(rangeEnd)[0], false);
        return ProcessInstanceLogDeleteBuilderImpl.this;
    }

    @Override
    public ProcessInstanceLogDeleteBuilder externalId(String... externalId) {
        if (checkIfNull(externalId)) {
            return ProcessInstanceLogDeleteBuilderImpl.this;
        } 
        addObjectParameter(QueryParameterIdentifiers.EXTERNAL_ID_LIST, "external id", externalId);
        return ProcessInstanceLogDeleteBuilderImpl.this;
    }

    @Override
    protected Class getQueryType() {
        return ProcessInstanceLog.class;
    }

    @Override
    protected String getQueryBase() {
        return ProcessInstanceLogDeleteBuilderImpl.PROCESS_INSTANCE_LOG_DELETE;
    }
}

