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
import org.kie.api.runtime.manager.audit.NodeInstanceLog;
import org.kie.internal.runtime.manager.audit.query.NodeInstanceLogQueryBuilder;
import org.kie.internal.query.QueryParameterIdentifiers;
import org.kie.api.runtime.manager.audit.org.jbpm.process.audit.NodeInstanceLog;

public class NodeInstLogQueryBuilderImpl extends AbstractAuditQueryBuilderImpl<NodeInstanceLogQueryBuilder, NodeInstanceLog> implements NodeInstanceLogQueryBuilder {
    public NodeInstLogQueryBuilderImpl(CommandExecutor cmdService) {
        super(cmdService);
    }

    public NodeInstLogQueryBuilderImpl(JPAAuditLogService jpaAuditService) {
        super(jpaAuditService);
    }

    @Override
    public NodeInstanceLogQueryBuilder nodeInstanceId(String... nodeInstanceId) {
        addObjectParameter(QueryParameterIdentifiers.NODE_INSTANCE_ID_LIST, "node instance id", nodeInstanceId);
        return NodeInstLogQueryBuilderImpl.this;
    }

    @Override
    public NodeInstanceLogQueryBuilder nodeId(String... nodeId) {
        addObjectParameter(QueryParameterIdentifiers.NODE_ID_LIST, "node id", nodeId);
        return NodeInstLogQueryBuilderImpl.this;
    }

    @Override
    public NodeInstanceLogQueryBuilder nodeName(String... name) {
        addObjectParameter(QueryParameterIdentifiers.NODE_NAME_LIST, "node name", name);
        return NodeInstLogQueryBuilderImpl.this;
    }

    @Override
    public NodeInstanceLogQueryBuilder nodeType(String... type) {
        addObjectParameter(QueryParameterIdentifiers.TYPE_LIST, "node type", type);
        return NodeInstLogQueryBuilderImpl.this;
    }

    @Override
    public NodeInstanceLogQueryBuilder workItemId(long... workItemId) {
        addLongParameter(QueryParameterIdentifiers.WORK_ITEM_ID_LIST, "work item id", workItemId);
        return NodeInstLogQueryBuilderImpl.this;
    }

    @Override
    protected Class<NodeInstanceLog> getResultType() {
        return NodeInstanceLog.class;
    }

    @Override
    protected Class<org.jbpm.process.audit.NodeInstanceLog> getQueryType() {
        return NodeInstanceLog.class;
    }
}

