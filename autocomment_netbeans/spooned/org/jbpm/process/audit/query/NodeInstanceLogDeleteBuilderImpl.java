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
import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.audit.NodeInstanceLog;
import org.kie.internal.runtime.manager.audit.query.NodeInstanceLogDeleteBuilder;
import org.kie.internal.query.QueryParameterIdentifiers;

public class NodeInstanceLogDeleteBuilderImpl extends AbstractAuditDeleteBuilderImpl<NodeInstanceLogDeleteBuilder> implements NodeInstanceLogDeleteBuilder {
    private static String NODE_INSTANCE_LOG_DELETE = "DELETE\n" + "FROM NodeInstanceLog l\n";

    public NodeInstanceLogDeleteBuilderImpl(JPAAuditLogService jpaService) {
        super(jpaService);
        intersect();
    }

    public NodeInstanceLogDeleteBuilderImpl(CommandExecutor cmdExecutor) {
        super(cmdExecutor);
        intersect();
    }

    @Override
    public NodeInstanceLogDeleteBuilder workItemId(long... workItemId) {
        if (checkIfNull(workItemId)) {
            return NodeInstanceLogDeleteBuilderImpl.this;
        } 
        addLongParameter(QueryParameterIdentifiers.WORK_ITEM_ID_LIST, "work item id", workItemId);
        return NodeInstanceLogDeleteBuilderImpl.this;
    }

    @Override
    public NodeInstanceLogDeleteBuilder nodeInstanceId(String... nodeInstanceId) {
        if (checkIfNull(nodeInstanceId)) {
            return NodeInstanceLogDeleteBuilderImpl.this;
        } 
        addObjectParameter(QueryParameterIdentifiers.NODE_INSTANCE_ID_LIST, "node instance id", nodeInstanceId);
        return NodeInstanceLogDeleteBuilderImpl.this;
    }

    @Override
    public NodeInstanceLogDeleteBuilder nodeId(String... nodeId) {
        if (checkIfNull(nodeId)) {
            return NodeInstanceLogDeleteBuilderImpl.this;
        } 
        addObjectParameter(QueryParameterIdentifiers.NODE_ID_LIST, "node id", nodeId);
        return NodeInstanceLogDeleteBuilderImpl.this;
    }

    @Override
    public NodeInstanceLogDeleteBuilder nodeName(String... name) {
        if (checkIfNull(name)) {
            return NodeInstanceLogDeleteBuilderImpl.this;
        } 
        addObjectParameter(QueryParameterIdentifiers.NODE_NAME_LIST, "node name", name);
        return NodeInstanceLogDeleteBuilderImpl.this;
    }

    @Override
    public NodeInstanceLogDeleteBuilder externalId(String... externalId) {
        if (checkIfNull(externalId)) {
            return NodeInstanceLogDeleteBuilderImpl.this;
        } 
        addObjectParameter(QueryParameterIdentifiers.EXTERNAL_ID_LIST, "external id", externalId);
        return NodeInstanceLogDeleteBuilderImpl.this;
    }

    @Override
    protected Class getQueryType() {
        return NodeInstanceLog.class;
    }

    @Override
    protected String getQueryBase() {
        return NodeInstanceLogDeleteBuilderImpl.NODE_INSTANCE_LOG_DELETE;
    }
}

