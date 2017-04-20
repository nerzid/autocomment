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
import org.kie.internal.task.query.AuditTaskDeleteBuilder;
import org.jbpm.services.task.audit.impl.model.AuditTaskImpl;
import QueryParameterIdentifiers.CREATED_ON_LIST;
import org.kie.api.runtime.CommandExecutor;
import QueryParameterIdentifiers.DEPLOYMENT_ID_LIST;
import java.util.Date;
import org.jbpm.process.audit.JPAAuditLogService;

public class AuditTaskDeleteBuilderImpl extends AbstractAuditDeleteBuilderImpl<AuditTaskDeleteBuilder> implements AuditTaskDeleteBuilder {
    private static String AUDIT_TASK_IMPL_DELETE = "DELETE\n" + "FROM AuditTaskImpl l\n";

    public AuditTaskDeleteBuilderImpl(CommandExecutor cmdExecutor) {
        super(cmdExecutor);
        intersect();
    }

    public AuditTaskDeleteBuilderImpl(JPAAuditLogService jpaAuditService) {
        super(jpaAuditService);
        intersect();
    }

    @Override
    public AuditTaskDeleteBuilder date(Date... date) {
        if (checkIfNull(date)) {
            return this;
        }
        // add object void{CREATED_ON_LIST} to AuditTaskDeleteBuilderImpl{}
        addObjectParameter(CREATED_ON_LIST, "created on date", ensureDateNotTimestamp(date));
        return this;
    }

    @Override
    public AuditTaskDeleteBuilder dateRangeStart(Date rangeStart) {
        if (checkIfNull(rangeStart)) {
            return this;
        }
        // add range void{CREATED_ON_LIST} to AuditTaskDeleteBuilderImpl{}
        addRangeParameter(CREATED_ON_LIST, "created on date range end", ensureDateNotTimestamp(rangeStart)[0], true);
        return this;
    }

    @Override
    public AuditTaskDeleteBuilder dateRangeEnd(Date rangeStart) {
        if (checkIfNull(rangeStart)) {
            return this;
        }
        // add range void{CREATED_ON_LIST} to AuditTaskDeleteBuilderImpl{}
        addRangeParameter(CREATED_ON_LIST, "created on date range end", ensureDateNotTimestamp(rangeStart)[0], false);
        return this;
    }

    @Override
    public AuditTaskDeleteBuilder deploymentId(String... deploymentId) {
        if (checkIfNull(deploymentId)) {
            return this;
        }
        // add object void{DEPLOYMENT_ID_LIST} to AuditTaskDeleteBuilderImpl{}
        addObjectParameter(DEPLOYMENT_ID_LIST, "deployment id", deploymentId);
        return this;
    }

    @Override
    protected Class getQueryType() {
        return AuditTaskImpl.class;
    }

    @Override
    protected String getQueryBase() {
        return AuditTaskDeleteBuilderImpl.AUDIT_TASK_IMPL_DELETE;
    }
}

