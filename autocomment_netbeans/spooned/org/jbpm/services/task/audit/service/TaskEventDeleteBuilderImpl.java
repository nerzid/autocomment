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

import org.jbpm.process.audit.query.AbstractAuditDeleteBuilderImpl;
import org.kie.api.runtime.CommandExecutor;
import java.util.Date;
import org.jbpm.process.audit.JPAAuditLogService;
import QueryParameterIdentifiers.TASK_EVENT_DATE_ID_LIST;
import org.kie.internal.task.query.TaskEventDeleteBuilder;
import org.jbpm.services.task.audit.impl.model.TaskEventImpl;

public class TaskEventDeleteBuilderImpl extends AbstractAuditDeleteBuilderImpl<TaskEventDeleteBuilder> implements TaskEventDeleteBuilder {
    private static String TASK_EVENT_IMPL_DELETE = "DELETE\n" + "FROM TaskEventImpl l\n";

    public TaskEventDeleteBuilderImpl(CommandExecutor cmdExecutor) {
        super(cmdExecutor);
        intersect();
    }

    public TaskEventDeleteBuilderImpl(JPAAuditLogService jpaAuditService) {
        super(jpaAuditService);
        intersect();
    }

    @Override
    public TaskEventDeleteBuilder date(Date... date) {
        if (checkIfNull(date)) {
            return this;
        }
        // add object void{TASK_EVENT_DATE_ID_LIST} to TaskEventDeleteBuilderImpl{}
        addObjectParameter(TASK_EVENT_DATE_ID_LIST, "created on date", ensureDateNotTimestamp(date));
        return this;
    }

    @Override
    public TaskEventDeleteBuilder dateRangeStart(Date rangeStart) {
        if (checkIfNull(rangeStart)) {
            return this;
        }
        // add range void{TASK_EVENT_DATE_ID_LIST} to TaskEventDeleteBuilderImpl{}
        addRangeParameter(TASK_EVENT_DATE_ID_LIST, "created on date range end", ensureDateNotTimestamp(rangeStart)[0], true);
        return this;
    }

    @Override
    public TaskEventDeleteBuilder dateRangeEnd(Date rangeStart) {
        if (checkIfNull(rangeStart)) {
            return this;
        }
        // add range void{TASK_EVENT_DATE_ID_LIST} to TaskEventDeleteBuilderImpl{}
        addRangeParameter(TASK_EVENT_DATE_ID_LIST, "created on date range end", ensureDateNotTimestamp(rangeStart)[0], false);
        return this;
    }

    @Override
    protected Class getQueryType() {
        return TaskEventImpl.class;
    }

    @Override
    protected String getQueryBase() {
        return TaskEventDeleteBuilderImpl.TASK_EVENT_IMPL_DELETE;
    }
}

