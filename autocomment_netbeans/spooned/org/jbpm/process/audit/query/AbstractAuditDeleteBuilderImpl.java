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

import org.jbpm.query.jpa.builder.impl.AbstractDeleteBuilderImpl;
import org.kie.internal.runtime.manager.audit.query.AuditDeleteBuilder;
import org.kie.api.runtime.CommandExecutor;
import org.kie.internal.command.Context;
import java.util.Date;
import org.jbpm.process.audit.JPAAuditLogService;
import org.kie.internal.query.ParametrizedUpdate;
import org.kie.internal.query.QueryParameterIdentifiers;
import org.jbpm.query.jpa.data.QueryWhere;
import java.sql.Timestamp;

public abstract class AbstractAuditDeleteBuilderImpl<T> extends AbstractDeleteBuilderImpl<T> implements AuditDeleteBuilder<T> {
    protected final CommandExecutor executor;

    protected final JPAAuditLogService jpaAuditService;

    protected AbstractAuditDeleteBuilderImpl(JPAAuditLogService jpaService) {
        this.executor = null;
        this.jpaAuditService = jpaService;
    }

    protected AbstractAuditDeleteBuilderImpl(CommandExecutor cmdExecutor) {
        this.executor = cmdExecutor;
        this.jpaAuditService = null;
    }

    // service methods
    protected JPAAuditLogService getJpaAuditLogService() {
        JPAAuditLogService jpaAuditLogService = AbstractAuditDeleteBuilderImpl.this.jpaAuditService;
        if (jpaAuditLogService == null) {
            jpaAuditLogService = AbstractAuditDeleteBuilderImpl.this.executor.execute(getJpaAuditLogServiceCommand);
        } 
        return jpaAuditLogService;
    }

    private AuditCommand<JPAAuditLogService> getJpaAuditLogServiceCommand = new org.jbpm.process.audit.command.AuditCommand<JPAAuditLogService>() {
        private static final long serialVersionUID = 101L;

        @Override
        public JPAAuditLogService execute(Context context) {
            setLogEnvironment(context);
            return ((JPAAuditLogService) (this.auditLogService));
        }
    };

    // query builder methods
    @SuppressWarnings(value = "unchecked")
    public T date(Date... date) {
        if (checkIfNull(date)) {
            return ((T) (AbstractAuditDeleteBuilderImpl.this));
        } 
        date = ensureDateNotTimestamp(date);
        addObjectParameter(QueryParameterIdentifiers.DATE_LIST, "date", date);
        return ((T) (AbstractAuditDeleteBuilderImpl.this));
    }

    @SuppressWarnings(value = "unchecked")
    public T dateRangeStart(Date rangeStart) {
        if (checkIfNull(rangeStart)) {
            return ((T) (AbstractAuditDeleteBuilderImpl.this));
        } 
        rangeStart = ensureDateNotTimestamp(rangeStart)[0];
        addRangeParameter(QueryParameterIdentifiers.DATE_LIST, "date range start", rangeStart, true);
        return ((T) (AbstractAuditDeleteBuilderImpl.this));
    }

    @SuppressWarnings(value = "unchecked")
    public T dateRangeEnd(Date rangeEnd) {
        if (checkIfNull(rangeEnd)) {
            return ((T) (AbstractAuditDeleteBuilderImpl.this));
        } 
        rangeEnd = ensureDateNotTimestamp(rangeEnd)[0];
        addRangeParameter(QueryParameterIdentifiers.DATE_LIST, "date range end", rangeEnd, false);
        return ((T) (AbstractAuditDeleteBuilderImpl.this));
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public T processInstanceId(long... processInstanceId) {
        if (checkIfNull(processInstanceId)) {
            return ((T) (AbstractAuditDeleteBuilderImpl.this));
        } 
        addLongParameter(QueryParameterIdentifiers.PROCESS_INSTANCE_ID_LIST, "process instance id", processInstanceId);
        return ((T) (AbstractAuditDeleteBuilderImpl.this));
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public T processId(String... processId) {
        if (checkIfNull(processId)) {
            return ((T) (AbstractAuditDeleteBuilderImpl.this));
        } 
        addObjectParameter(QueryParameterIdentifiers.PROCESS_ID_LIST, "process id", processId);
        return ((T) (AbstractAuditDeleteBuilderImpl.this));
    }

    protected <T> boolean checkIfNull(T... parameter) {
        if (parameter == null) {
            return true;
        } 
        for (int i = 0; i < (parameter.length); ++i) {
            if ((parameter[i]) == null) {
                return true;
            } 
        }
        return false;
    }

    protected Date[] ensureDateNotTimestamp(Date... date) {
        Date[] validated = new Date[date.length];
        for (int i = 0; i < (date.length); ++i) {
            if ((date[i]) instanceof Timestamp) {
                validated[i] = new Date(date[i].getTime());
            } else {
                validated[i] = date[i];
            }
        }
        return validated;
    }

    protected abstract Class getQueryType();

    protected abstract String getQueryBase();

    public ParametrizedUpdate build() {
        return new ParametrizedUpdate() {
            private QueryWhere queryWhere = new QueryWhere(AbstractAuditDeleteBuilderImpl.2.getQueryWhere());

            @Override
            public int execute() {
                int result = getJpaAuditLogService().doDelete(getQueryBase(), queryWhere, getQueryType());
                return result;
            }
        };
    }
}

