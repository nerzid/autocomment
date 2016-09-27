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


package org.jbpm.workflow.core.node;

import java.util.ArrayList;
import org.kie.api.definition.process.Connection;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.kie.api.definition.process.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.impl.NodeImpl;
import java.io.Serializable;
import org.kie.api.definition.process.org.jbpm.workflow.core.Node;

/**
 */
public class CompositeNode extends StateBasedNode implements EventNodeInterface , NodeContainer {
    private static final long serialVersionUID = 510L;

    private NodeContainer nodeContainer;

    private Map<String, CompositeNode.NodeAndType> inConnectionMap = new HashMap<String, CompositeNode.NodeAndType>();

    private Map<String, CompositeNode.NodeAndType> outConnectionMap = new HashMap<String, CompositeNode.NodeAndType>();

    private boolean cancelRemainingInstances = true;

    public CompositeNode() {
        CompositeNode.this.nodeContainer = new org.jbpm.workflow.core.impl.NodeContainerImpl();
    }

    public Node getNode(long id) {
        return nodeContainer.getNode(id);
    }

    public Node internalGetNode(long id) {
        return getNode(id);
    }

    public Node[] getNodes() {
        List<Node> subNodes = new ArrayList<Node>();
        for (Node node : nodeContainer.getNodes()) {
            if ((!(node instanceof CompositeNode.CompositeNodeStart)) && (!(node instanceof CompositeNode.CompositeNodeEnd))) {
                subNodes.add(node);
            } 
        }
        return subNodes.toArray(new Node[subNodes.size()]);
    }

    public Node[] internalGetNodes() {
        return getNodes();
    }

    public void addNode(Node node) {
        // TODO find a more elegant solution for this
        // preferrable remove id setting from this class
        // and delegate to GUI command that drops node
        if ((node.getId()) <= 0) {
            long id = 0;
            for (Node n : nodeContainer.getNodes()) {
                if ((n.getId()) > id) {
                    id = n.getId();
                } 
            }
            ((org.jbpm.workflow.core.Node) (node)).setId((++id));
        } 
        nodeContainer.addNode(node);
        ((org.jbpm.workflow.core.Node) (node)).setNodeContainer(CompositeNode.this);
    }

    protected void internalAddNode(Node node) {
        addNode(node);
    }

    public void removeNode(Node node) {
        nodeContainer.removeNode(node);
        ((org.jbpm.workflow.core.Node) (node)).setNodeContainer(null);
    }

    protected void internalRemoveNode(Node node) {
        removeNode(node);
    }

    public boolean acceptsEvent(String type, Object event) {
        for (Node node : internalGetNodes()) {
            if (node instanceof EventNodeInterface) {
                if (((EventNodeInterface) (node)).acceptsEvent(type, event)) {
                    return true;
                } 
            } 
        }
        return false;
    }

    public void linkIncomingConnections(String inType, long inNodeId, String inNodeType) {
        linkIncomingConnections(inType, new CompositeNode.NodeAndType(inNodeId, inNodeType));
    }

    public void linkIncomingConnections(String inType, CompositeNode.NodeAndType inNode) {
        CompositeNode.NodeAndType oldNodeAndType = inConnectionMap.get(inType);
        if (oldNodeAndType != null) {
            if (oldNodeAndType.equals(inNode)) {
                return ;
            } else {
                // remove old start nodes + connections
                List<Connection> oldInConnections = oldNodeAndType.getNode().getIncomingConnections(oldNodeAndType.getType());
                if (oldInConnections != null) {
                    for (Connection connection : new ArrayList<Connection>(oldInConnections)) {
                        if ((connection.getFrom()) instanceof CompositeNode.CompositeNodeStart) {
                            removeNode(connection.getFrom());
                            ((ConnectionImpl) (connection)).terminate();
                        } 
                    }
                } 
            }
        } 
        inConnectionMap.put(inType, inNode);
        if (inNode != null) {
            List<Connection> connections = getIncomingConnections(inType);
            for (Connection connection : connections) {
                CompositeNode.CompositeNodeStart start = new CompositeNode.CompositeNodeStart(connection.getFrom(), inType);
                internalAddNode(start);
                if ((inNode.getNode()) != null) {
                    new ConnectionImpl(start, Node.CONNECTION_DEFAULT_TYPE, inNode.getNode(), inNode.getType());
                } 
            }
        } 
    }

    public void linkOutgoingConnections(long outNodeId, String outNodeType, String outType) {
        linkOutgoingConnections(new CompositeNode.NodeAndType(outNodeId, outNodeType), outType);
    }

    public void linkOutgoingConnections(CompositeNode.NodeAndType outNode, String outType) {
        CompositeNode.NodeAndType oldNodeAndType = outConnectionMap.get(outType);
        if (oldNodeAndType != null) {
            if (oldNodeAndType.equals(outNode)) {
                return ;
            } else {
                // remove old end nodes + connections
                List<Connection> oldOutConnections = oldNodeAndType.getNode().getOutgoingConnections(oldNodeAndType.getType());
                for (Connection connection : new ArrayList<Connection>(oldOutConnections)) {
                    if ((connection.getTo()) instanceof CompositeNode.CompositeNodeEnd) {
                        removeNode(connection.getTo());
                        ((ConnectionImpl) (connection)).terminate();
                    } 
                }
            }
        } 
        outConnectionMap.put(outType, outNode);
        if (outNode != null) {
            List<Connection> connections = getOutgoingConnections(outType);
            for (Connection connection : connections) {
                CompositeNode.CompositeNodeEnd end = new CompositeNode.CompositeNodeEnd(connection.getTo(), outType);
                internalAddNode(end);
                if ((outNode.getNode()) != null) {
                    new ConnectionImpl(outNode.getNode(), outNode.getType(), end, Node.CONNECTION_DEFAULT_TYPE);
                } 
            }
        } 
    }

    public CompositeNode.NodeAndType getLinkedIncomingNode(String inType) {
        return inConnectionMap.get(inType);
    }

    public CompositeNode.NodeAndType internalGetLinkedIncomingNode(String inType) {
        return inConnectionMap.get(inType);
    }

    public CompositeNode.NodeAndType getLinkedOutgoingNode(String outType) {
        return outConnectionMap.get(outType);
    }

    public CompositeNode.NodeAndType internalGetLinkedOutgoingNode(String outType) {
        return outConnectionMap.get(outType);
    }

    public Map<String, CompositeNode.NodeAndType> getLinkedIncomingNodes() {
        return inConnectionMap;
    }

    public Map<String, CompositeNode.NodeAndType> getLinkedOutgoingNodes() {
        return outConnectionMap;
    }

    public void validateAddIncomingConnection(final String type, final Connection connection) {
        CompositeNode.NodeAndType nodeAndType = internalGetLinkedIncomingNode(type);
        if ((connection.getFrom().getNodeContainer()) == (CompositeNode.this)) {
            if (nodeAndType != null) {
                throw new IllegalArgumentException(("Cannot link incoming connection type more than once: " + type));
            } 
        } else {
            if (nodeAndType != null) {
                NodeImpl node = ((NodeImpl) (nodeAndType.getNode()));
                if (node != null) {
                    node.validateAddIncomingConnection(nodeAndType.getType(), connection);
                } 
            } 
        }
    }

    public void addIncomingConnection(String type, Connection connection) {
        if ((connection.getFrom().getNodeContainer()) == (CompositeNode.this)) {
            linkOutgoingConnections(connection.getFrom().getId(), connection.getFromType(), org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE);
        } else {
            super.addIncomingConnection(type, connection);
            CompositeNode.NodeAndType inNode = internalGetLinkedIncomingNode(type);
            if (inNode != null) {
                CompositeNode.CompositeNodeStart start = new CompositeNode.CompositeNodeStart(connection.getFrom(), type);
                internalAddNode(start);
                NodeImpl node = ((NodeImpl) (inNode.getNode()));
                if (node != null) {
                    new ConnectionImpl(start, Node.CONNECTION_DEFAULT_TYPE, inNode.getNode(), inNode.getType());
                } 
            } 
        }
    }

    public void validateAddOutgoingConnection(final String type, final Connection connection) {
        CompositeNode.NodeAndType nodeAndType = internalGetLinkedOutgoingNode(type);
        if ((connection.getTo().getNodeContainer()) == (CompositeNode.this)) {
            if (nodeAndType != null) {
                throw new IllegalArgumentException(("Cannot link outgoing connection type more than once: " + type));
            } 
        } else {
            if (nodeAndType != null) {
                NodeImpl node = ((NodeImpl) (nodeAndType.getNode()));
                if (node != null) {
                    ((NodeImpl) (nodeAndType.getNode())).validateAddOutgoingConnection(nodeAndType.getType(), connection);
                } 
            } 
        }
    }

    public void addOutgoingConnection(String type, Connection connection) {
        if ((connection.getTo().getNodeContainer()) == (CompositeNode.this)) {
            linkIncomingConnections(org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE, connection.getTo().getId(), connection.getToType());
        } else {
            super.addOutgoingConnection(type, connection);
            CompositeNode.NodeAndType outNode = internalGetLinkedOutgoingNode(type);
            if (outNode != null) {
                CompositeNode.CompositeNodeEnd end = new CompositeNode.CompositeNodeEnd(connection.getTo(), type);
                internalAddNode(end);
                NodeImpl node = ((NodeImpl) (outNode.getNode()));
                if (node != null) {
                    new ConnectionImpl(outNode.getNode(), outNode.getType(), end, Node.CONNECTION_DEFAULT_TYPE);
                } 
            } 
        }
    }

    public void validateRemoveIncomingConnection(final String type, final Connection connection) {
        CompositeNode.NodeAndType nodeAndType = internalGetLinkedIncomingNode(type);
        if (nodeAndType != null) {
            for (Connection inConnection : nodeAndType.getNode().getIncomingConnections(nodeAndType.getType())) {
                if ((((CompositeNode.CompositeNodeStart) (inConnection.getFrom())).getInNodeId()) == (connection.getFrom().getId())) {
                    ((NodeImpl) (nodeAndType.getNode())).validateRemoveIncomingConnection(nodeAndType.getType(), inConnection);
                    return ;
                } 
            }
            throw new IllegalArgumentException("Could not find internal incoming connection for node");
        } 
    }

    public void removeIncomingConnection(String type, Connection connection) {
        super.removeIncomingConnection(type, connection);
        CompositeNode.NodeAndType nodeAndType = internalGetLinkedIncomingNode(type);
        if (nodeAndType != null) {
            for (Connection inConnection : nodeAndType.getNode().getIncomingConnections(nodeAndType.getType())) {
                if ((((CompositeNode.CompositeNodeStart) (inConnection.getFrom())).getInNodeId()) == (connection.getFrom().getId())) {
                    Node compositeNodeStart = inConnection.getFrom();
                    ((ConnectionImpl) (inConnection)).terminate();
                    internalRemoveNode(compositeNodeStart);
                    return ;
                } 
            }
            throw new IllegalArgumentException("Could not find internal incoming connection for node");
        } 
    }

    public void validateRemoveOutgoingConnection(final String type, final Connection connection) {
        CompositeNode.NodeAndType nodeAndType = internalGetLinkedOutgoingNode(type);
        if (nodeAndType != null) {
            for (Connection outConnection : nodeAndType.getNode().getOutgoingConnections(nodeAndType.getType())) {
                if ((((CompositeNode.CompositeNodeEnd) (outConnection.getTo())).getOutNodeId()) == (connection.getTo().getId())) {
                    ((NodeImpl) (nodeAndType.getNode())).validateRemoveOutgoingConnection(nodeAndType.getType(), outConnection);
                    return ;
                } 
            }
            throw new IllegalArgumentException("Could not find internal outgoing connection for node");
        } 
    }

    public void removeOutgoingConnection(String type, Connection connection) {
        super.removeOutgoingConnection(type, connection);
        CompositeNode.NodeAndType nodeAndType = internalGetLinkedOutgoingNode(type);
        if (nodeAndType != null) {
            for (Connection outConnection : nodeAndType.getNode().getOutgoingConnections(nodeAndType.getType())) {
                if ((((CompositeNode.CompositeNodeEnd) (outConnection.getTo())).getOutNodeId()) == (connection.getTo().getId())) {
                    Node compositeNodeEnd = outConnection.getTo();
                    ((ConnectionImpl) (outConnection)).terminate();
                    internalRemoveNode(compositeNodeEnd);
                    return ;
                } 
            }
            throw new IllegalArgumentException("Could not find internal outgoing connection for node");
        } 
    }

    public boolean isCancelRemainingInstances() {
        return cancelRemainingInstances;
    }

    public void setCancelRemainingInstances(boolean cancelRemainingInstances) {
        CompositeNode.this.cancelRemainingInstances = cancelRemainingInstances;
    }

    public class NodeAndType implements Serializable {
        private static final long serialVersionUID = 510L;

        private long nodeId;

        private String type;

        private transient Node node;

        public NodeAndType(long nodeId, String type) {
            if (type == null) {
                throw new IllegalArgumentException("Node or type may not be null!");
            } 
            CompositeNode.NodeAndType.this.nodeId = nodeId;
            CompositeNode.NodeAndType.this.type = type;
        }

        public NodeAndType(Node node, String type) {
            if ((node == null) || (type == null)) {
                throw new IllegalArgumentException("Node or type may not be null!");
            } 
            CompositeNode.NodeAndType.this.nodeId = node.getId();
            CompositeNode.NodeAndType.this.node = node;
            CompositeNode.NodeAndType.this.type = type;
        }

        public Node getNode() {
            if ((node) == null) {
                try {
                    node = nodeContainer.getNode(nodeId);
                } catch (IllegalArgumentException e) {
                    // unknown node id, returning null
                }
            } 
            return node;
        }

        public long getNodeId() {
            return nodeId;
        }

        public String getType() {
            return type;
        }

        public boolean equals(Object o) {
            if (o instanceof CompositeNode.NodeAndType) {
                return ((nodeId) == (((CompositeNode.NodeAndType) (o)).nodeId)) && (type.equals(((CompositeNode.NodeAndType) (o)).type));
            } 
            return false;
        }

        public int hashCode() {
            return (7 * ((int) (nodeId))) + (13 * (type.hashCode()));
        }
    }

    public class CompositeNodeStart extends NodeImpl {
        private static final long serialVersionUID = 510L;

        private long inNodeId;

        private transient Node inNode;

        private String inType;

        public CompositeNodeStart(Node outNode, String outType) {
            setName("Composite node start");
            CompositeNode.CompositeNodeStart.this.inNodeId = outNode.getId();
            CompositeNode.CompositeNodeStart.this.inNode = outNode;
            CompositeNode.CompositeNodeStart.this.inType = outType;
            setMetaData("hidden", true);
        }

        public Node getInNode() {
            if ((inNode) == null) {
                inNode = ((NodeContainer) (CompositeNode.this.getNodeContainer())).internalGetNode(inNodeId);
            } 
            return inNode;
        }

        public long getInNodeId() {
            return inNodeId;
        }

        public String getInType() {
            return inType;
        }
    }

    public class CompositeNodeEnd extends NodeImpl {
        private static final long serialVersionUID = 510L;

        private long outNodeId;

        private transient Node outNode;

        private String outType;

        public CompositeNodeEnd(Node outNode, String outType) {
            setName("Composite node end");
            CompositeNode.CompositeNodeEnd.this.outNodeId = outNode.getId();
            CompositeNode.CompositeNodeEnd.this.outNode = outNode;
            CompositeNode.CompositeNodeEnd.this.outType = outType;
            setMetaData("hidden", true);
        }

        public Node getOutNode() {
            if ((outNode) == null) {
                outNode = ((NodeContainer) (CompositeNode.this.getNodeContainer())).internalGetNode(outNodeId);
            } 
            return outNode;
        }

        public long getOutNodeId() {
            return outNodeId;
        }

        public String getOutType() {
            return outType;
        }
    }
}

