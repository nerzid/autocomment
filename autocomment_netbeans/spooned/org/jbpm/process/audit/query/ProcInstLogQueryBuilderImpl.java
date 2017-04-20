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

import QueryParameterIdentifiers.CORRELATION_KEY_LIST;
import org.kie.api.runtime.CommandExecutor;
import QueryParameterIdentifiers.PROCESS_VERSION_LIST;
import org.kie.internal.process.CorrelationKey;
import QueryParameterIdentifiers.DURATION_LIST;
import java.util.Date;
import QueryParameterIdentifiers.END_DATE_LIST;
import QueryParameterIdentifiers.IDENTITY_LIST;
import org.jbpm.process.audit.JPAAuditLogService;
import QueryParameterIdentifiers.START_DATE_LIST;
import QueryParameterIdentifiers.OUTCOME_LIST;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.internal.runtime.manager.audit.query.ProcessInstanceLogQueryBuilder;
import QueryParameterIdentifiers.PROCESS_INSTANCE_STATUS_LIST;
import QueryParameterIdentifiers.PROCESS_NAME_LIST;

public class ProcInstLogQueryBuilderImpl extends AbstractAuditQueryBuilderImpl<ProcessInstanceLogQueryBuilder, ProcessInstanceLog> implements ProcessInstanceLogQueryBuilder {
    public ProcInstLogQueryBuilderImpl(CommandExecutor cmdExecutor) {
        super(cmdExecutor);
    }

    public ProcInstLogQueryBuilderImpl(JPAAuditLogService jpaAuditService) {
        super(jpaAuditService);
    }

    @Override
    public ProcessInstanceLogQueryBuilder status(int... status) {
        // add int void{PROCESS_INSTANCE_STATUS_LIST} to ProcInstLogQueryBuilderImpl{}
        addIntParameter(PROCESS_INSTANCE_STATUS_LIST, "status", status);
        return this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder duration(long... duration) {
        // add long void{DURATION_LIST} to ProcInstLogQueryBuilderImpl{}
        addLongParameter(DURATION_LIST, "duration", duration);
        return this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder durationMin(long durationMin) {
        // add range void{DURATION_LIST} to ProcInstLogQueryBuilderImpl{}
        addRangeParameter(DURATION_LIST, "duration min", durationMin, true);
        return this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder durationMax(long durationMax) {
        // add range void{DURATION_LIST} to ProcInstLogQueryBuilderImpl{}
        addRangeParameter(DURATION_LIST, "duration max", durationMax, false);
        return this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder identity(String... identity) {
        // add object void{IDENTITY_LIST} to ProcInstLogQueryBuilderImpl{}
        addObjectParameter(IDENTITY_LIST, "identity", identity);
        return this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder processVersion(String... version) {
        // add object void{PROCESS_VERSION_LIST} to ProcInstLogQueryBuilderImpl{}
        addObjectParameter(PROCESS_VERSION_LIST, "process version", version);
        return this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder processName(String... processName) {
        // add object void{PROCESS_NAME_LIST} to ProcInstLogQueryBuilderImpl{}
        addObjectParameter(PROCESS_NAME_LIST, "process name", processName);
        return this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder startDate(Date... date) {
        // add object void{START_DATE_LIST} to ProcInstLogQueryBuilderImpl{}
        addObjectParameter(START_DATE_LIST, "start date", date);
        return this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder startDateRangeStart(Date rangeStart) {
        // add range void{START_DATE_LIST} to ProcInstLogQueryBuilderImpl{}
        addRangeParameter(START_DATE_LIST, "start date range, start", rangeStart, true);
        return this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder startDateRangeEnd(Date rangeEnd) {
        // add range void{START_DATE_LIST} to ProcInstLogQueryBuilderImpl{}
        addRangeParameter(START_DATE_LIST, "start date range, end", rangeEnd, false);
        return this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder endDate(Date... date) {
        // add object void{END_DATE_LIST} to ProcInstLogQueryBuilderImpl{}
        addObjectParameter(END_DATE_LIST, "end date", date);
        return this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder endDateRangeStart(Date rangeStart) {
        // add range void{END_DATE_LIST} to ProcInstLogQueryBuilderImpl{}
        addRangeParameter(END_DATE_LIST, "end date range, start", rangeStart, true);
        return this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder endDateRangeEnd(Date rangeEnd) {
        // add range void{END_DATE_LIST} to ProcInstLogQueryBuilderImpl{}
        addRangeParameter(END_DATE_LIST, "end date range, end", rangeEnd, false);
        return this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder outcome(String... outcome) {
        // add object void{OUTCOME_LIST} to ProcInstLogQueryBuilderImpl{}
        addObjectParameter(OUTCOME_LIST, "outcome", outcome);
        return this;
    }

    @Override
    public ProcessInstanceLogQueryBuilder correlationKey(CorrelationKey... correlationKeys) {
        String[] correlationKeysExternal = new String[correlationKeys];
        for (int i = 0; i < correlationKeys; i++) {
            correlationKeysExternal[i] = correlationKeys[i].toExternalForm();
        }
        // add object void{CORRELATION_KEY_LIST} to ProcInstLogQueryBuilderImpl{}
        addObjectParameter(CORRELATION_KEY_LIST, "correlation key", correlationKeysExternal);
        return this;
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

