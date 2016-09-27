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

import org.jbpm.workflow.core.Connection;
import java.util.HashMap;
import java.util.Map;
import org.kie.api.definition.process.Node;
import java.io.Serializable;
import org.kie.api.definition.process.org.jbpm.workflow.core.Node;

/**
 * Default implementation of a connection.
 */
public class ConnectionImpl implements Serializable , Connection {
    private static final long serialVersionUID = 510L;

    private Node from;

    private Node to;

    private String fromType;

    private String toType;

    private Map<String, Object> metaData = new HashMap<String, Object>();

    public ConnectionImpl() {
    }

    /**
     * This constructor calls {@link #connect()} itself! That means
     * that simply creating the object also adds it to the appropriate
     * {@link List} fields in other objects.
     * </p>
     * Creates a new connection, given a from node, a to node
     * and a type.
     * 
     * @param from      The from node
     * @param fromType  The node type
     * @param to        The to node
     * @param toType    The connection type
     */
    public ConnectionImpl(final Node from, final String fromType, final Node to, final String toType) {
        if (from == null) {
            throw new IllegalArgumentException("From node is null!");
        } 
        if (fromType == null) {
            throw new IllegalArgumentException("From type is null!");
        } 
        if (to == null) {
            throw new IllegalArgumentException("To node is null!");
        } 
        if (toType == null) {
            throw new IllegalArgumentException("To type is null!");
        } 
        ConnectionImpl.this.from = from;
        ConnectionImpl.this.fromType = fromType;
        ConnectionImpl.this.to = to;
        ConnectionImpl.this.toType = toType;
        connect();
    }

    public void connect() {
        ((org.jbpm.workflow.core.Node) (ConnectionImpl.this.from)).addOutgoingConnection(fromType, ConnectionImpl.this);
        ((org.jbpm.workflow.core.Node) (ConnectionImpl.this.to)).addIncomingConnection(toType, ConnectionImpl.this);
    }

    public synchronized void terminate() {
        ((org.jbpm.workflow.core.Node) (ConnectionImpl.this.from)).removeOutgoingConnection(fromType, ConnectionImpl.this);
        ((org.jbpm.workflow.core.Node) (ConnectionImpl.this.to)).removeIncomingConnection(toType, ConnectionImpl.this);
        ConnectionImpl.this.from = null;
        ConnectionImpl.this.fromType = null;
        ConnectionImpl.this.to = null;
        ConnectionImpl.this.toType = null;
    }

    public Node getFrom() {
        return ConnectionImpl.this.from;
    }

    public Node getTo() {
        return ConnectionImpl.this.to;
    }

    public String getFromType() {
        return ConnectionImpl.this.fromType;
    }

    public String getToType() {
        return ConnectionImpl.this.toType;
    }

    public void setFrom(Node from) {
        ConnectionImpl.this.from = from;
    }

    public void setTo(Node to) {
        ConnectionImpl.this.to = to;
    }

    public void setFromType(String fromType) {
        ConnectionImpl.this.fromType = fromType;
    }

    public void setToType(String toType) {
        ConnectionImpl.this.toType = toType;
    }

    public Map<String, Object> getMetaData() {
        return ConnectionImpl.this.metaData;
    }

    public void setMetaData(String name, Object value) {
        ConnectionImpl.this.metaData.put(name, value);
    }

    public Object getMetaData(String name) {
        return ConnectionImpl.this.metaData.get(name);
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder("Connection ");
        sb.append(((getFrom()) == null ? "null" : getFrom().getName()));
        sb.append(" [type=");
        sb.append(getFromType());
        sb.append("]");
        sb.append(" - ");
        sb.append(((getTo()) == null ? "null" : getTo().getName()));
        sb.append(" [type=");
        sb.append(getToType());
        sb.append("]");
        return sb.toString();
    }
}

