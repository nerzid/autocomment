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

import java.util.List;
import org.kie.api.runtime.CommandExecutor;
import QueryParameterIdentifiers.EXTERNAL_ID_LIST;
import org.jbpm.process.audit.JPAAuditLogService;
import QueryParameterIdentifiers.LAST_VARIABLE_LIST;
import QueryParameterIdentifiers.OLD_VALUE_LIST;
import org.jbpm.query.jpa.data.QueryCriteria;
import org.kie.internal.runtime.manager.audit.query.VariableInstanceLogQueryBuilder;
import org.kie.api.runtime.manager.audit.VariableInstanceLog;
import org.kie.internal.query.QueryParameterIdentifiers;
import QueryParameterIdentifiers.VALUE_LIST;
import QueryParameterIdentifiers.VARIABLE_ID_LIST;
import QueryParameterIdentifiers.VAR_VALUE_ID_LIST;
import QueryParameterIdentifiers.VARIABLE_INSTANCE_ID_LIST;

public class VarInstLogQueryBuilderImpl extends AbstractAuditQueryBuilderImpl<VariableInstanceLogQueryBuilder, VariableInstanceLog> implements VariableInstanceLogQueryBuilder {
    public VarInstLogQueryBuilderImpl(CommandExecutor cmdExecutor) {
        super(cmdExecutor);
    }

    public VarInstLogQueryBuilderImpl(JPAAuditLogService jpaAuditService) {
        super(jpaAuditService);
    }

    @Override
    public VariableInstanceLogQueryBuilder variableInstanceId(String... variableInstanceId) {
        // add object void{VARIABLE_INSTANCE_ID_LIST} to VarInstLogQueryBuilderImpl{}
        addObjectParameter(VARIABLE_INSTANCE_ID_LIST, "variable instance id", variableInstanceId);
        return this;
    }

    @Override
    public VariableInstanceLogQueryBuilder variableId(String... variableId) {
        // add object void{VARIABLE_ID_LIST} to VarInstLogQueryBuilderImpl{}
        addObjectParameter(VARIABLE_ID_LIST, "variable id", variableId);
        return this;
    }

    @Override
    public VariableInstanceLogQueryBuilder value(String... value) {
        // add object void{VALUE_LIST} to VarInstLogQueryBuilderImpl{}
        addObjectParameter(VALUE_LIST, "value", value);
        return this;
    }

    @Override
    public VariableInstanceLogQueryBuilder oldValue(String... oldVvalue) {
        // add object void{OLD_VALUE_LIST} to VarInstLogQueryBuilderImpl{}
        addObjectParameter(OLD_VALUE_LIST, "old value", oldVvalue);
        return this;
    }

    @Override
    public VariableInstanceLogQueryBuilder variableValue(String variableId, String value) {
        if (queryWhere.isRange()) {
            throw new IllegalArgumentException("Range values are not supported for the .variableValue(..) method");
        }
        if (variableId == null) {
            throw new IllegalArgumentException("A null variable Id criteria is invalid.");
        }
        if (value == null) {
            throw new IllegalArgumentException("A null variable value criteria is invalid.");
        }
        String varValStr = ((((variableId.length()) + (QueryParameterIdentifiers.VAR_VAL_SEPARATOR)) + variableId) + (QueryParameterIdentifiers.VAR_VAL_SEPARATOR)) + value;
        // add object void{VAR_VALUE_ID_LIST} to VarInstLogQueryBuilderImpl{}
        addObjectParameter(VAR_VALUE_ID_LIST, "value for variable", varValStr);
        return this;
    }

    @Override
    public VariableInstanceLogQueryBuilder externalId(String... externalId) {
        // add object void{EXTERNAL_ID_LIST} to VarInstLogQueryBuilderImpl{}
        addObjectParameter(EXTERNAL_ID_LIST, "external id", externalId);
        return this;
    }

    @Override
    public VariableInstanceLogQueryBuilder last() {
        List<QueryCriteria> criteriaList = queryWhere.getCriteria();
        QueryCriteria lastVariableInstanceLogCriteria = null;
        for (QueryCriteria criteria : criteriaList) {
            if (LAST_VARIABLE_LIST.equals(criteria.getListId())) {
                lastVariableInstanceLogCriteria = criteria;
                break;
            }
        }
        if (lastVariableInstanceLogCriteria == null) {
            queryWhere.addParameter(LAST_VARIABLE_LIST, true);
        }
        return this;
    }

    @Override
    protected Class<VariableInstanceLog> getResultType() {
        return VariableInstanceLog.class;
    }

    @Override
    protected Class getQueryType() {
        return VariableInstanceLog.class;
    }
}

