/**
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.jbpm.workflow.core.impl;

import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.NodeContainer;
import org.jbpm.workflow.core.WorkflowProcess;
import org.kie.api.definition.process.org.jbpm.workflow.core.Node;
import org.kie.api.definition.process.org.jbpm.workflow.core.NodeContainer;

/**
 * Default implementation of a RuleFlow process.
 */
public class WorkflowProcessImpl extends org.jbpm.process.core.impl.ProcessImpl implements WorkflowProcess , org.jbpm.workflow.core.NodeContainer {
    private static final long serialVersionUID = 510L;

    private boolean autoComplete = false;

    private boolean dynamic = false;

    private NodeContainer nodeContainer;

    public WorkflowProcessImpl() {
        nodeContainer = ((org.jbpm.workflow.core.NodeContainer) (createNodeContainer()));
    }

    protected NodeContainer createNodeContainer() {
        return new NodeContainerImpl();
    }

    public Node[] getNodes() {
        return nodeContainer.getNodes();
    }

    public Node getNode(final long id) {
        return nodeContainer.getNode(id);
    }

    public Node internalGetNode(long id) {
        try {
            return getNode(id);
        } catch (IllegalArgumentException e) {
            if (dynamic) {
                return null;
            } else {
                throw e;
            }
        }
    }

    public void removeNode(final Node node) {
        nodeContainer.removeNode(node);
        ((org.jbpm.workflow.core.Node) (node)).setNodeContainer(null);
    }

    public void addNode(final Node node) {
        nodeContainer.addNode(node);
        ((org.jbpm.workflow.core.Node) (node)).setNodeContainer(WorkflowProcessImpl.this);
    }

    public boolean isAutoComplete() {
        return autoComplete;
    }

    public void setAutoComplete(boolean autoComplete) {
        WorkflowProcessImpl.this.autoComplete = autoComplete;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        WorkflowProcessImpl.this.dynamic = dynamic;
    }

    @Override
    public Integer getProcessType() {
        if (dynamic) {
            return CASE_TYPE;
        } 
        return PROCESS_TYPE;
    }
}

