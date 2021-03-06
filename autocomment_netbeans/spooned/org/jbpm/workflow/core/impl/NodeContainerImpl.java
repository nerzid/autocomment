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

import org.jbpm.process.core.Context;
import java.util.HashMap;
import java.util.Map;
import org.kie.api.definition.process.Node;
import org.jbpm.workflow.core.NodeContainer;
import java.io.Serializable;

/**
 */
public class NodeContainerImpl implements Serializable , NodeContainer {
    private static final long serialVersionUID = 510L;

    private Map<Long, Node> nodes;

    public NodeContainerImpl() {
        NodeContainerImpl.this.nodes = new HashMap<Long, Node>();
    }

    public void addNode(final Node node) {
        validateAddNode(node);
        if (!(NodeContainerImpl.this.nodes.containsValue(node))) {
            NodeContainerImpl.this.nodes.put(new Long(node.getId()), node);
        } 
    }

    protected void validateAddNode(Node node) {
        if (node == null) {
            throw new IllegalArgumentException("Node is null!");
        } 
    }

    public Node[] getNodes() {
        return ((Node[]) (NodeContainerImpl.this.nodes.values().toArray(new Node[NodeContainerImpl.this.nodes.size()])));
    }

    public Node getNode(final long id) {
        Node node = NodeContainerImpl.this.nodes.get(id);
        if (node == null) {
            throw new IllegalArgumentException(("Unknown node id: " + id));
        } 
        return node;
    }

    public Node internalGetNode(long id) {
        return getNode(id);
    }

    public void removeNode(final Node node) {
        validateRemoveNode(node);
        NodeContainerImpl.this.nodes.remove(new Long(node.getId()));
    }

    protected void validateRemoveNode(Node node) {
        if (node == null) {
            throw new IllegalArgumentException("Node is null");
        } 
        if ((NodeContainerImpl.this.nodes.get(node.getId())) == null) {
            throw new IllegalArgumentException(("Unknown node: " + node));
        } 
    }

    public Context resolveContext(String contextId, Object param) {
        return null;
    }
}

