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


package org.jbpm.workflow.core.node;

import org.kie.api.definition.process.Connection;
import org.jbpm.workflow.core.impl.ExtendedNodeImpl;

/**
 * Default implementation of a fault node.
 */
public class FaultNode extends ExtendedNodeImpl {
    private static final String[] EVENT_TYPES = new String[]{ EVENT_NODE_ENTER };

    private static final long serialVersionUID = 510L;

    private String faultName;

    private String faultVariable;

    private boolean terminateParent = false;

    public String getFaultVariable() {
        return faultVariable;
    }

    public void setFaultVariable(String faultVariable) {
        FaultNode.this.faultVariable = faultVariable;
    }

    public String getFaultName() {
        return faultName;
    }

    public void setFaultName(String faultName) {
        FaultNode.this.faultName = faultName;
    }

    public boolean isTerminateParent() {
        return terminateParent;
    }

    public void setTerminateParent(boolean terminateParent) {
        FaultNode.this.terminateParent = terminateParent;
    }

    public String[] getActionTypes() {
        return FaultNode.EVENT_TYPES;
    }

    public void validateAddIncomingConnection(final String type, final Connection connection) {
        super.validateAddIncomingConnection(type, connection);
        if (!(org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type))) {
            throw new IllegalArgumentException((((("This type of node [" + (connection.getTo().getMetaData().get("UniqueId"))) + ", ") + (connection.getTo().getName())) + "] only accepts default incoming connection type!"));
        } 
        if ((getFrom()) != null) {
            throw new IllegalArgumentException((((("This type of node [" + (connection.getTo().getMetaData().get("UniqueId"))) + ", ") + (connection.getTo().getName())) + "] cannot have more than one incoming connection!"));
        } 
    }

    public void validateAddOutgoingConnection(final String type, final Connection connection) {
        throw new UnsupportedOperationException("A fault node does not have an outgoing connection!");
    }

    public void validateRemoveOutgoingConnection(final String type, final Connection connection) {
        throw new UnsupportedOperationException("A fault node does not have an outgoing connection!");
    }
}

