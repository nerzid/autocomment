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
import org.kie.internal.process.CorrelationKey;
import java.util.Date;
import org.jbpm.process.audit.JPAAuditLogService;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.internal.runtime.manager.audit.query.ProcessInstanceLogQueryBuilder;
import org.kie.internal.query.QueryParameterIdentifiers;

public class ProcInstLogQueryBuilderImpl extends AbstractAuditQueryBuilderImpl<ProcessInstanceLogQueryBuilder, ProcessInstanceLog> implements ProcessInstanceLogQueryBuilder {
    public ProcInstLogQueryBuilderImpl(CommandExecutor cmdExecutor) {
        super(cmdExecutor);
    }

    public ProcInstLogQueryBuilderImpl(JPAAuditLogService jpaAuditService) {
        super(jpaAuditService);
    }

    @Override
    public ProcessInstanceLogQueryBuilder status(int... status) {
        addIntParameter(QueryParameterIdentifiers.PROCESS_INSTANCE_STATUS_LIST, "status", status);
        return ProcInstLogQueryBuilderImpl.this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder duration(long... duration) {
        addLongParameter(QueryParameterIdentifiers.DURATION_LIST, "duration", duration);
        return ProcInstLogQueryBuilderImpl.this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder durationMin(long durationMin) {
        addRangeParameter(QueryParameterIdentifiers.DURATION_LIST, "duration min", durationMin, true);
        return ProcInstLogQueryBuilderImpl.this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder durationMax(long durationMax) {
        addRangeParameter(QueryParameterIdentifiers.DURATION_LIST, "duration max", durationMax, false);
        return ProcInstLogQueryBuilderImpl.this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder identity(String... identity) {
        addObjectParameter(QueryParameterIdentifiers.IDENTITY_LIST, "identity", identity);
        return ProcInstLogQueryBuilderImpl.this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder processVersion(String... version) {
        addObjectParameter(QueryParameterIdentifiers.PROCESS_VERSION_LIST, "process version", version);
        return ProcInstLogQueryBuilderImpl.this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder processName(String... processName) {
        addObjectParameter(QueryParameterIdentifiers.PROCESS_NAME_LIST, "process name", processName);
        return ProcInstLogQueryBuilderImpl.this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder startDate(Date... date) {
        addObjectParameter(QueryParameterIdentifiers.START_DATE_LIST, "start date", date);
        return ProcInstLogQueryBuilderImpl.this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder startDateRangeStart(Date rangeStart) {
        addRangeParameter(QueryParameterIdentifiers.START_DATE_LIST, "start date range, start", rangeStart, true);
        return ProcInstLogQueryBuilderImpl.this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder startDateRangeEnd(Date rangeEnd) {
        addRangeParameter(QueryParameterIdentifiers.START_DATE_LIST, "start date range, end", rangeEnd, false);
        return ProcInstLogQueryBuilderImpl.this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder endDate(Date... date) {
        addObjectParameter(QueryParameterIdentifiers.END_DATE_LIST, "end date", date);
        return ProcInstLogQueryBuilderImpl.this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder endDateRangeStart(Date rangeStart) {
        addRangeParameter(QueryParameterIdentifiers.END_DATE_LIST, "end date range, start", rangeStart, true);
        return ProcInstLogQueryBuilderImpl.this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder endDateRangeEnd(Date rangeEnd) {
        addRangeParameter(QueryParameterIdentifiers.END_DATE_LIST, "end date range, end", rangeEnd, false);
        return ProcInstLogQueryBuilderImpl.this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder outcome(String... outcome) {
        addObjectParameter(QueryParameterIdentifiers.OUTCOME_LIST, "outcome", outcome);
        return ProcInstLogQueryBuilderImpl.this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder correlationKey(CorrelationKey... correlationKeys) {
        String[] correlationKeysExternal = new String[correlationKeys];
        for (int i = 0; i < correlationKeys; i++) {
            correlationKeysExternal[i] = correlationKeys[i].toExternalForm();
        }
        addObjectParameter(QueryParameterIdentifiers.CORRELATION_KEY_LIST, "correlation key", correlationKeysExternal);
        return ProcInstLogQueryBuilderImpl.this;
    }

    @Override
    protected Class<ProcessInstanceLog> getResultType() {
        return ProcessInstanceLog.class;
    }

    @Override
    protected Class getQueryType() {
        return ProcessInstanceLog.class;
    }
}

