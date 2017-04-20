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

import java.util.Date;
import org.kie.api.runtime.CommandExecutor;
import QueryParameterIdentifiers.END_DATE_LIST;
import QueryParameterIdentifiers.EXTERNAL_ID_LIST;
import QueryParameterIdentifiers.START_DATE_LIST;
import org.kie.internal.runtime.manager.audit.query.ProcessInstanceLogDeleteBuilder;
import QueryParameterIdentifiers.IDENTITY_LIST;
import org.jbpm.process.audit.JPAAuditLogService;
import QueryParameterIdentifiers.OUTCOME_LIST;
import QueryParameterIdentifiers.PROCESS_INSTANCE_STATUS_LIST;
import QueryParameterIdentifiers.PROCESS_NAME_LIST;
import QueryParameterIdentifiers.PROCESS_VERSION_LIST;
import org.jbpm.process.audit.ProcessInstanceLog;

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
            return this;
        }
        // add int void{PROCESS_INSTANCE_STATUS_LIST} to ProcessInstanceLogDeleteBuilderImpl{}
        addIntParameter(PROCESS_INSTANCE_STATUS_LIST, "status", status);
        return this;
    }

    @Override
    public ProcessInstanceLogDeleteBuilder outcome(String... outcome) {
        if (checkIfNull(outcome)) {
            return this;
        }
        // add object void{OUTCOME_LIST} to ProcessInstanceLogDeleteBuilderImpl{}
        addObjectParameter(OUTCOME_LIST, "outcome", outcome);
        return this;
    }

    @Override
    public ProcessInstanceLogDeleteBuilder identity(String... identity) {
        if (checkIfNull(identity)) {
            return this;
        }
        // add object void{IDENTITY_LIST} to ProcessInstanceLogDeleteBuilderImpl{}
        addObjectParameter(IDENTITY_LIST, "identity", identity);
        return this;
    }

    @Override
    public ProcessInstanceLogDeleteBuilder processVersion(String... version) {
        if (checkIfNull(version)) {
            return this;
        }
        // add object void{PROCESS_VERSION_LIST} to ProcessInstanceLogDeleteBuilderImpl{}
        addObjectParameter(PROCESS_VERSION_LIST, "process version", version);
        return this;
    }

    @Override
    public ProcessInstanceLogDeleteBuilder processName(String... processName) {
        if (checkIfNull(processName)) {
            return this;
        }
        // add object void{PROCESS_NAME_LIST} to ProcessInstanceLogDeleteBuilderImpl{}
        addObjectParameter(PROCESS_NAME_LIST, "process name", processName);
        return this;
    }

    @Override
    public ProcessInstanceLogDeleteBuilder startDate(Date... date) {
        if (checkIfNull(date)) {
            return this;
        }
        // add object void{START_DATE_LIST} to ProcessInstanceLogDeleteBuilderImpl{}
        addObjectParameter(START_DATE_LIST, "start date", ensureDateNotTimestamp(date));
        return this;
    }

    @Override
    public ProcessInstanceLogDeleteBuilder startDateRangeStart(Date rangeStart) {
        if (checkIfNull(rangeStart)) {
            return this;
        }
        // add range void{START_DATE_LIST} to ProcessInstanceLogDeleteBuilderImpl{}
        addRangeParameter(START_DATE_LIST, "start date range, start", ensureDateNotTimestamp(rangeStart)[0], true);
        return this;
    }

    @Override
    public ProcessInstanceLogDeleteBuilder startDateRangeEnd(Date rangeEnd) {
        if (checkIfNull(rangeEnd)) {
            return this;
        }
        // add range void{START_DATE_LIST} to ProcessInstanceLogDeleteBuilderImpl{}
        addRangeParameter(START_DATE_LIST, "start date range, end", ensureDateNotTimestamp(rangeEnd)[0], false);
        return this;
    }

    @Override
    public ProcessInstanceLogDeleteBuilder endDate(Date... date) {
        if (checkIfNull(date)) {
            return this;
        }
        // add object void{END_DATE_LIST} to ProcessInstanceLogDeleteBuilderImpl{}
        addObjectParameter(END_DATE_LIST, "end date", ensureDateNotTimestamp(date));
        return this;
    }

    @Override
    public ProcessInstanceLogDeleteBuilder endDateRangeStart(Date rangeStart) {
        if (checkIfNull(rangeStart)) {
            return this;
        }
        // add range void{END_DATE_LIST} to ProcessInstanceLogDeleteBuilderImpl{}
        addRangeParameter(END_DATE_LIST, "end date range, start", ensureDateNotTimestamp(rangeStart)[0], true);
        return this;
    }

    @Override
    public ProcessInstanceLogDeleteBuilder endDateRangeEnd(Date rangeEnd) {
        if (checkIfNull(rangeEnd)) {
            return this;
        }
        // add range void{END_DATE_LIST} to ProcessInstanceLogDeleteBuilderImpl{}
        addRangeParameter(END_DATE_LIST, "end date range, end", ensureDateNotTimestamp(rangeEnd)[0], false);
        return this;
    }

    @Override
    public ProcessInstanceLogDeleteBuilder externalId(String... externalId) {
        if (checkIfNull(externalId)) {
            return this;
        }
        // add object void{EXTERNAL_ID_LIST} to ProcessInstanceLogDeleteBuilderImpl{}
        addObjectParameter(EXTERNAL_ID_LIST, "external id", externalId);
        return this;
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

