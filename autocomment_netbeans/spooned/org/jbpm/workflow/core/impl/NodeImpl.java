/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Collections;
import org.jbpm.workflow.core.node.CompositeNode;
import org.kie.api.definition.process.Connection;
import org.jbpm.workflow.core.Constraint;
import org.jbpm.process.core.Context;
import org.jbpm.process.core.ContextResolver;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jbpm.workflow.core.Node;
import org.kie.api.definition.process.NodeContainer;
import java.io.Serializable;
import org.kie.api.definition.process.org.jbpm.workflow.core.NodeContainer;

/**
 * Default implementation of a node.
 */
public abstract class NodeImpl implements Serializable , ContextResolver , Node {
    private static final long serialVersionUID = 510L;

    protected static final NodeImpl[] EMPTY_NODE_ARRAY = new NodeImpl[0];

    private long id;

    private static final AtomicLong uniqueIdGen = new AtomicLong(0);

    private String name;

    private Map<String, List<Connection>> incomingConnections;

    private Map<String, List<Connection>> outgoingConnections;

    private NodeContainer parentNodeContainer;

    private Map<String, Context> contexts = new HashMap<String, Context>();

    private Map<String, Object> metaData = new HashMap<String, Object>();

    protected Map<ConnectionRef, Constraint> constraints = new HashMap<ConnectionRef, Constraint>();

    public NodeImpl() {
        NodeImpl.this.id = -1;
        NodeImpl.this.incomingConnections = new HashMap<String, List<Connection>>();
        NodeImpl.this.outgoingConnections = new HashMap<String, List<Connection>>();
    }

    public long getId() {
        return NodeImpl.this.id;
    }

    public String getUniqueId() {
        String result = (id) + "";
        NodeContainer nodeContainer = getNodeContainer();
        while (nodeContainer instanceof CompositeNode) {
            CompositeNode composite = ((CompositeNode) (nodeContainer));
            result = ((composite.getId()) + ":") + result;
            nodeContainer = composite.getNodeContainer();
        }
        return result;
    }

    public void setId(final long id) {
        NodeImpl.this.id = id;
        String uniqueId = ((String) (getMetaData("UniqueId")));
        if (uniqueId == null) {
            setMetaData("UniqueId", ("_jbpm-unique-" + (NodeImpl.uniqueIdGen.getAndIncrement())));
        } 
    }

    public String getName() {
        return NodeImpl.this.name;
    }

    public void setName(final String name) {
        NodeImpl.this.name = name;
    }

    public Map<String, List<Connection>> getIncomingConnections() {
        // TODO: users can still modify the lists inside this Map
        return Collections.unmodifiableMap(NodeImpl.this.incomingConnections);
    }

    public Map<String, List<Connection>> getOutgoingConnections() {
        // TODO: users can still modify the lists inside this Map
        return Collections.unmodifiableMap(NodeImpl.this.outgoingConnections);
    }

    public void addIncomingConnection(final String type, final Connection connection) {
        validateAddIncomingConnection(type, connection);
        List<Connection> connections = NodeImpl.this.incomingConnections.get(type);
        if (connections == null) {
            connections = new ArrayList<Connection>();
            NodeImpl.this.incomingConnections.put(type, connections);
        } 
        connections.add(connection);
    }

    public void validateAddIncomingConnection(final String type, final Connection connection) {
        if (type == null) {
            throw new IllegalArgumentException("Connection type cannot be null");
        } 
        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        } 
    }

    public List<Connection> getIncomingConnections(String type) {
        List<Connection> result = incomingConnections.get(type);
        if (result == null) {
            return new ArrayList<Connection>();
        } 
        return result;
    }

    public void addOutgoingConnection(final String type, final Connection connection) {
        validateAddOutgoingConnection(type, connection);
        List<Connection> connections = NodeImpl.this.outgoingConnections.get(type);
        if (connections == null) {
            connections = new ArrayList<Connection>();
            NodeImpl.this.outgoingConnections.put(type, connections);
        } 
        connections.add(connection);
    }

    public void validateAddOutgoingConnection(final String type, final Connection connection) {
        if (type == null) {
            throw new IllegalArgumentException("Connection type cannot be null");
        } 
        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        } 
    }

    public List<Connection> getOutgoingConnections(String type) {
        List<Connection> result = outgoingConnections.get(type);
        if (result == null) {
            return new ArrayList<Connection>();
        } 
        return result;
    }

    public void removeIncomingConnection(final String type, final Connection connection) {
        validateRemoveIncomingConnection(type, connection);
        NodeImpl.this.incomingConnections.get(type).remove(connection);
    }

    public void clearIncomingConnection() {
        NodeImpl.this.incomingConnections.clear();
    }

    public void clearOutgoingConnection() {
        NodeImpl.this.outgoingConnections.clear();
    }

    public void validateRemoveIncomingConnection(final String type, final Connection connection) {
        if (type == null) {
            throw new IllegalArgumentException("Connection type cannot be null");
        } 
        if (connection == null) {
            throw new IllegalArgumentException("Connection is null");
        } 
        if (!(incomingConnections.get(type).contains(connection))) {
            throw new IllegalArgumentException((("Given connection <" + connection) + "> is not part of the incoming connections"));
        } 
    }

    public void removeOutgoingConnection(final String type, final Connection connection) {
        validateRemoveOutgoingConnection(type, connection);
        NodeImpl.this.outgoingConnections.get(type).remove(connection);
    }

    public void validateRemoveOutgoingConnection(final String type, final Connection connection) {
        if (type == null) {
            throw new IllegalArgumentException("Connection type cannot be null");
        } 
        if (connection == null) {
            throw new IllegalArgumentException("Connection is null");
        } 
        if (!(NodeImpl.this.outgoingConnections.get(type).contains(connection))) {
            throw new IllegalArgumentException((("Given connection <" + connection) + "> is not part of the outgoing connections"));
        } 
    }

    /**
     * * Helper method for nodes that have at most one default incoming connection
     */
    public Connection getFrom() {
        final List<Connection> list = getIncomingConnections(org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE);
        if ((list.size()) == 0) {
            return null;
        } 
        if ((list.size()) == 1) {
            return list.get(0);
        } 
        if ("true".equals(System.getProperty("jbpm.enable.multi.con"))) {
            return list.get(0);
        } else {
            throw new IllegalArgumentException("Trying to retrieve the from connection but multiple connections are present");
        }
    }

    /**
     * * Helper method for nodes that have at most one default outgoing connection
     */
    public Connection getTo() {
        final List<Connection> list = getOutgoingConnections(org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE);
        if ((list.size()) == 0) {
            return null;
        } 
        if ((list.size()) == 1) {
            return list.get(0);
        } 
        if ("true".equals(System.getProperty("jbpm.enable.multi.con"))) {
            return list.get(0);
        } else {
            throw new IllegalArgumentException("Trying to retrieve the to connection but multiple connections are present");
        }
    }

    /**
     * * Helper method for nodes that have multiple default incoming connections
     */
    public List<Connection> getDefaultIncomingConnections() {
        return getIncomingConnections(org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE);
    }

    /**
     * * Helper method for nodes that have multiple default outgoing connections
     */
    public List<Connection> getDefaultOutgoingConnections() {
        return getOutgoingConnections(org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE);
    }

    public NodeContainer getNodeContainer() {
        return parentNodeContainer;
    }

    public void setNodeContainer(NodeContainer nodeContainer) {
        NodeImpl.this.parentNodeContainer = nodeContainer;
    }

    public void setContext(String contextId, Context context) {
        NodeImpl.this.contexts.put(contextId, context);
    }

    public Context getContext(String contextId) {
        return NodeImpl.this.contexts.get(contextId);
    }

    public Context resolveContext(String contextId, Object param) {
        Context context = getContext(contextId);
        if (context != null) {
            context = context.resolveContext(param);
            if (context != null) {
                return context;
            } 
        } 
        return ((org.jbpm.workflow.core.NodeContainer) (parentNodeContainer)).resolveContext(contextId, param);
    }

    public void setMetaData(String name, Object value) {
        NodeImpl.this.metaData.put(name, value);
    }

    public Object getMetaData(String name) {
        return NodeImpl.this.metaData.get(name);
    }

    public Map<String, Object> getMetaData() {
        return NodeImpl.this.metaData;
    }

    public void setMetaData(Map<String, Object> metaData) {
        NodeImpl.this.metaData = metaData;
    }

    public Constraint getConstraint(final Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("connection is null");
        } 
        ConnectionRef ref = new ConnectionRef(connection.getTo().getId(), connection.getToType());
        return NodeImpl.this.constraints.get(ref);
    }

    public Constraint internalGetConstraint(final ConnectionRef ref) {
        return NodeImpl.this.constraints.get(ref);
    }

    public void setConstraint(final Connection connection, final Constraint constraint) {
        if (connection == null) {
            throw new IllegalArgumentException("connection is null");
        } 
        if (!(getDefaultOutgoingConnections().contains(connection))) {
            throw new IllegalArgumentException(("connection is unknown:" + connection));
        } 
        addConstraint(new ConnectionRef(connection.getTo().getId(), connection.getToType()), constraint);
    }

    public void addConstraint(ConnectionRef connectionRef, Constraint constraint) {
        if (connectionRef == null) {
            throw new IllegalArgumentException((("A " + (NodeImpl.this.getName())) + " node only accepts constraints linked to a connection"));
        } 
        NodeImpl.this.constraints.put(connectionRef, constraint);
    }

    public Map<ConnectionRef, Constraint> getConstraints() {
        return Collections.unmodifiableMap(NodeImpl.this.constraints);
    }
}

