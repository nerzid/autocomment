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


package org.jbpm.workflow.instance.impl;

import org.jbpm.process.instance.impl.Action;
import java.util.ArrayList;
import java.util.Collection;
import org.jbpm.workflow.instance.node.CompositeNodeInstance;
import org.kie.api.definition.process.Connection;
import org.jbpm.process.instance.impl.ConstraintEvaluator;
import org.jbpm.process.core.Context;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.instance.ContextInstance;
import org.jbpm.process.instance.ContextInstanceContainer;
import org.jbpm.process.instance.context.exception.ExceptionScopeInstance;
import org.jbpm.process.instance.context.exclusive.ExclusiveGroupInstance;
import java.util.HashMap;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.jbpm.process.instance.InternalProcessRuntime;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.kie.api.definition.process.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.NodeInstanceContainer;
import org.drools.core.spi.ProcessContext;
import org.jbpm.process.instance.ProcessInstance;
import java.io.Serializable;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.jbpm.workflow.instance.WorkflowRuntimeException;
import org.kie.api.runtime.process.org.jbpm.workflow.instance.NodeInstance;
import org.kie.api.runtime.process.org.jbpm.workflow.instance.NodeInstanceContainer;

/**
 * Default implementation of a RuleFlow node instance.
 */
public abstract class NodeInstanceImpl implements Serializable , org.jbpm.workflow.instance.NodeInstance {
    private static final long serialVersionUID = 510L;

    protected static final Logger logger = LoggerFactory.getLogger(NodeInstanceImpl.class);

    private long id;

    private long nodeId;

    private WorkflowProcessInstance processInstance;

    private NodeInstanceContainer nodeInstanceContainer;

    private Map<String, Object> metaData = new HashMap<String, Object>();

    private int level;

    protected transient Map<String, Object> dynamicParameters;

    public void setId(final long id) {
        NodeInstanceImpl.this.id = id;
    }

    public long getId() {
        return NodeInstanceImpl.this.id;
    }

    public void setNodeId(final long nodeId) {
        NodeInstanceImpl.this.nodeId = nodeId;
    }

    public long getNodeId() {
        return NodeInstanceImpl.this.nodeId;
    }

    public String getNodeName() {
        Node node = getNode();
        return node == null ? "" : node.getName();
    }

    public int getLevel() {
        return NodeInstanceImpl.this.level;
    }

    public void setLevel(int level) {
        NodeInstanceImpl.this.level = level;
    }

    public void setProcessInstance(final WorkflowProcessInstance processInstance) {
        NodeInstanceImpl.this.processInstance = processInstance;
    }

    public WorkflowProcessInstance getProcessInstance() {
        return NodeInstanceImpl.this.processInstance;
    }

    public NodeInstanceContainer getNodeInstanceContainer() {
        return NodeInstanceImpl.this.nodeInstanceContainer;
    }

    public void setNodeInstanceContainer(NodeInstanceContainer nodeInstanceContainer) {
        NodeInstanceImpl.this.nodeInstanceContainer = ((org.jbpm.workflow.instance.NodeInstanceContainer) (nodeInstanceContainer));
        if (nodeInstanceContainer != null) {
            NodeInstanceImpl.this.nodeInstanceContainer.addNodeInstance(NodeInstanceImpl.this);
        } 
    }

    public Node getNode() {
        try {
            return ((NodeContainer) (NodeInstanceImpl.this.nodeInstanceContainer.getNodeContainer())).internalGetNode(NodeInstanceImpl.this.nodeId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(((((("Unknown node id: " + (NodeInstanceImpl.this.nodeId)) + " for node instance ") + (getUniqueId())) + " for process instance ") + (NodeInstanceImpl.this.processInstance)), e);
        }
    }

    public boolean isInversionOfControl() {
        return false;
    }

    public void cancel() {
        nodeInstanceContainer.removeNodeInstance(NodeInstanceImpl.this);
        boolean hidden = false;
        Node node = getNode();
        if ((node != null) && ((node.getMetaData().get("hidden")) != null)) {
            hidden = true;
        } 
        if (!hidden) {
            InternalKnowledgeRuntime kruntime = getProcessInstance().getKnowledgeRuntime();
            ((InternalProcessRuntime) (kruntime.getProcessRuntime())).getProcessEventSupport().fireAfterNodeLeft(NodeInstanceImpl.this, kruntime);
        } 
    }

    public final void trigger(NodeInstance from, String type) {
        boolean hidden = false;
        if ((getNode().getMetaData().get("hidden")) != null) {
            hidden = true;
        } 
        if (from != null) {
            int level = ((org.jbpm.workflow.instance.NodeInstance) (from)).getLevel();
            ((org.jbpm.workflow.instance.NodeInstanceContainer) (getNodeInstanceContainer())).setCurrentLevel(level);
            Collection<Connection> incoming = getNode().getIncomingConnections(type);
            for (Connection conn : incoming) {
                if ((conn.getFrom().getId()) == (from.getNodeId())) {
                    NodeInstanceImpl.this.metaData.put("IncomingConnection", conn.getMetaData().get("UniqueId"));
                    break;
                } 
            }
        } 
        InternalKnowledgeRuntime kruntime = getProcessInstance().getKnowledgeRuntime();
        if (!hidden) {
            ((InternalProcessRuntime) (kruntime.getProcessRuntime())).getProcessEventSupport().fireBeforeNodeTriggered(NodeInstanceImpl.this, kruntime);
        } 
        try {
            internalTrigger(from, type);
        } catch (WorkflowRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new WorkflowRuntimeException(NodeInstanceImpl.this, getProcessInstance(), e);
        }
        if (!hidden) {
            ((InternalProcessRuntime) (kruntime.getProcessRuntime())).getProcessEventSupport().fireAfterNodeTriggered(NodeInstanceImpl.this, kruntime);
        } 
    }

    public abstract void internalTrigger(NodeInstance from, String type);

    /**
     * This method is used in both instances of the {@link ExtendedNodeInstanceImpl}
     * and {@link ActionNodeInstance} instances in order to handle
     * exceptions thrown when executing actions.
     * 
     * @param action An {@link Action} instance.
     */
    protected void executeAction(Action action) {
        ProcessContext context = new ProcessContext(getProcessInstance().getKnowledgeRuntime());
        context.setNodeInstance(NodeInstanceImpl.this);
        try {
            action.execute(context);
        } catch (Exception e) {
            String exceptionName = e.getClass().getName();
            ExceptionScopeInstance exceptionScopeInstance = ((ExceptionScopeInstance) (resolveContextInstance(ExceptionScope.EXCEPTION_SCOPE, exceptionName)));
            if (exceptionScopeInstance == null) {
                throw new WorkflowRuntimeException(NodeInstanceImpl.this, getProcessInstance(), ("Unable to execute Action: " + (e.getMessage())), e);
            } 
            exceptionScopeInstance.handleException(exceptionName, e);
        }
    }

    protected void triggerCompleted(String type, boolean remove) {
        Node node = getNode();
        if (node != null) {
            String uniqueId = ((String) (node.getMetaData().get("UniqueId")));
            if (uniqueId == null) {
                uniqueId = ((NodeImpl) (node)).getUniqueId();
            } 
            ((WorkflowProcessInstanceImpl) (processInstance)).addCompletedNodeId(uniqueId);
            ((WorkflowProcessInstanceImpl) (processInstance)).getIterationLevels().remove(uniqueId);
        } 
        // if node instance was cancelled, or containing container instance was cancelled
        if (((getNodeInstanceContainer().getNodeInstance(getId())) == null) || ((((org.jbpm.workflow.instance.NodeInstanceContainer) (getNodeInstanceContainer())).getState()) != (ProcessInstance.STATE_ACTIVE))) {
            return ;
        } 
        if (remove) {
            ((org.jbpm.workflow.instance.NodeInstanceContainer) (getNodeInstanceContainer())).removeNodeInstance(NodeInstanceImpl.this);
        } 
        List<Connection> connections = null;
        if (node != null) {
            if (("true".equals(System.getProperty("jbpm.enable.multi.con"))) && ((((NodeImpl) (node)).getConstraints().size()) > 0)) {
                int priority = Integer.MAX_VALUE;
                connections = ((NodeImpl) (node)).getDefaultOutgoingConnections();
                boolean found = false;
                List<NodeInstanceImpl.NodeInstanceTrigger> nodeInstances = new ArrayList<NodeInstanceImpl.NodeInstanceTrigger>();
                List<Connection> outgoingCopy = new ArrayList<Connection>(connections);
                while (!(outgoingCopy.isEmpty())) {
                    priority = Integer.MAX_VALUE;
                    Connection selectedConnection = null;
                    ConstraintEvaluator selectedConstraint = null;
                    for (final Iterator<Connection> iterator = outgoingCopy.iterator(); iterator.hasNext();) {
                        final Connection connection = ((Connection) (iterator.next()));
                        ConstraintEvaluator constraint = ((ConstraintEvaluator) (((NodeImpl) (node)).getConstraint(connection)));
                        if (((constraint != null) && ((constraint.getPriority()) < priority)) && (!(constraint.isDefault()))) {
                            priority = constraint.getPriority();
                            selectedConnection = connection;
                            selectedConstraint = constraint;
                        } 
                    }
                    if (selectedConstraint == null) {
                        break;
                    } 
                    if (selectedConstraint.evaluate(NodeInstanceImpl.this, selectedConnection, selectedConstraint)) {
                        nodeInstances.add(new NodeInstanceImpl.NodeInstanceTrigger(followConnection(selectedConnection), selectedConnection.getToType()));
                        found = true;
                    } 
                    outgoingCopy.remove(selectedConnection);
                }
                for (NodeInstanceImpl.NodeInstanceTrigger nodeInstance : nodeInstances) {
                    // stop if this process instance has been aborted / completed
                    if ((((org.jbpm.workflow.instance.NodeInstanceContainer) (getNodeInstanceContainer())).getState()) != (ProcessInstance.STATE_ACTIVE)) {
                        return ;
                    } 
                    triggerNodeInstance(nodeInstance.getNodeInstance(), nodeInstance.getToType());
                }
                if (!found) {
                    for (final Iterator<Connection> iterator = connections.iterator(); iterator.hasNext();) {
                        final Connection connection = ((Connection) (iterator.next()));
                        ConstraintEvaluator constraint = ((ConstraintEvaluator) (((NodeImpl) (node)).getConstraint(connection)));
                        if (constraint.isDefault()) {
                            triggerConnection(connection);
                            found = true;
                            break;
                        } 
                    }
                } 
                if (!found) {
                    throw new IllegalArgumentException(("Uncontrolled flow node could not find at least one valid outgoing connection " + (getNode().getName())));
                } 
                return ;
            } else {
                connections = node.getOutgoingConnections(type);
            }
        } 
        if ((connections == null) || (connections.isEmpty())) {
            boolean hidden = false;
            Node currentNode = getNode();
            if ((currentNode != null) && ((currentNode.getMetaData().get("hidden")) != null)) {
                hidden = true;
            } 
            InternalKnowledgeRuntime kruntime = getProcessInstance().getKnowledgeRuntime();
            if (!hidden) {
                ((InternalProcessRuntime) (kruntime.getProcessRuntime())).getProcessEventSupport().fireBeforeNodeLeft(NodeInstanceImpl.this, kruntime);
            } 
            // notify container
            ((org.jbpm.workflow.instance.NodeInstanceContainer) (getNodeInstanceContainer())).nodeInstanceCompleted(NodeInstanceImpl.this, type);
            if (!hidden) {
                ((InternalProcessRuntime) (kruntime.getProcessRuntime())).getProcessEventSupport().fireAfterNodeLeft(NodeInstanceImpl.this, kruntime);
            } 
        } else {
            Map<org.jbpm.workflow.instance.NodeInstance, String> nodeInstances = new HashMap<org.jbpm.workflow.instance.NodeInstance, String>();
            for (Connection connection : connections) {
                nodeInstances.put(followConnection(connection), connection.getToType());
            }
            for (Map.Entry<org.jbpm.workflow.instance.NodeInstance, String> nodeInstance : nodeInstances.entrySet()) {
                // stop if this process instance has been aborted / completed
                if ((((org.jbpm.workflow.instance.NodeInstanceContainer) (getNodeInstanceContainer())).getState()) != (ProcessInstance.STATE_ACTIVE)) {
                    return ;
                } 
                triggerNodeInstance(nodeInstance.getKey(), nodeInstance.getValue());
            }
        }
    }

    protected NodeInstance followConnection(Connection connection) {
        // check for exclusive group first
        NodeInstanceContainer parent = getNodeInstanceContainer();
        if (parent instanceof ContextInstanceContainer) {
            List<ContextInstance> contextInstances = ((ContextInstanceContainer) (parent)).getContextInstances(ExclusiveGroup.EXCLUSIVE_GROUP);
            if (contextInstances != null) {
                for (ContextInstance contextInstance : new ArrayList<ContextInstance>(contextInstances)) {
                    ExclusiveGroupInstance groupInstance = ((ExclusiveGroupInstance) (contextInstance));
                    if (groupInstance.containsNodeInstance(NodeInstanceImpl.this)) {
                        for (NodeInstance nodeInstance : groupInstance.getNodeInstances()) {
                            if (nodeInstance != (NodeInstanceImpl.this)) {
                                ((org.jbpm.workflow.instance.NodeInstance) (nodeInstance)).cancel();
                            } 
                        }
                        ((ContextInstanceContainer) (parent)).removeContextInstance(ExclusiveGroup.EXCLUSIVE_GROUP, contextInstance);
                    } 
                }
            } 
        } 
        return ((org.jbpm.workflow.instance.NodeInstance) (((org.jbpm.workflow.instance.NodeInstanceContainer) (getNodeInstanceContainer())).getNodeInstance(connection.getTo())));
    }

    protected void triggerNodeInstance(NodeInstance nodeInstance, String type) {
        boolean hidden = false;
        if ((getNode().getMetaData().get("hidden")) != null) {
            hidden = true;
        } 
        InternalKnowledgeRuntime kruntime = getProcessInstance().getKnowledgeRuntime();
        if (!hidden) {
            ((InternalProcessRuntime) (kruntime.getProcessRuntime())).getProcessEventSupport().fireBeforeNodeLeft(NodeInstanceImpl.this, kruntime);
        } 
        // trigger next node
        nodeInstance.trigger(NodeInstanceImpl.this, type);
        Collection<Connection> outgoing = getNode().getOutgoingConnections(type);
        for (Connection conn : outgoing) {
            if ((conn.getTo().getId()) == (nodeInstance.getNodeId())) {
                NodeInstanceImpl.this.metaData.put("OutgoingConnection", conn.getMetaData().get("UniqueId"));
                break;
            } 
        }
        if (!hidden) {
            ((InternalProcessRuntime) (kruntime.getProcessRuntime())).getProcessEventSupport().fireAfterNodeLeft(NodeInstanceImpl.this, kruntime);
        } 
    }

    protected void triggerConnection(Connection connection) {
        triggerNodeInstance(followConnection(connection), connection.getToType());
    }

    public void retrigger(boolean remove) {
        if (remove) {
            cancel();
        } 
        triggerNode(getNodeId());
    }

    public void triggerNode(long nodeId) {
        org.jbpm.workflow.instance.NodeInstance nodeInstance = ((org.jbpm.workflow.instance.NodeInstance) (((org.jbpm.workflow.instance.NodeInstanceContainer) (getNodeInstanceContainer())).getNodeInstance(getNode().getNodeContainer().getNode(nodeId))));
        triggerNodeInstance(nodeInstance, null);
    }

    public Context resolveContext(String contextId, Object param) {
        return ((NodeImpl) (getNode())).resolveContext(contextId, param);
    }

    public ContextInstance resolveContextInstance(String contextId, Object param) {
        Context context = resolveContext(contextId, param);
        if (context == null) {
            return null;
        } 
        ContextInstanceContainer contextInstanceContainer = getContextInstanceContainer(context.getContextContainer());
        if (contextInstanceContainer == null) {
            throw new IllegalArgumentException("Could not find context instance container for context");
        } 
        return contextInstanceContainer.getContextInstance(context);
    }

    private ContextInstanceContainer getContextInstanceContainer(ContextContainer contextContainer) {
        ContextInstanceContainer contextInstanceContainer = null;
        if ((NodeInstanceImpl.this) instanceof ContextInstanceContainer) {
            contextInstanceContainer = ((ContextInstanceContainer) (NodeInstanceImpl.this));
        } else {
            contextInstanceContainer = getEnclosingContextInstanceContainer(NodeInstanceImpl.this);
        }
        while (contextInstanceContainer != null) {
            if ((contextInstanceContainer.getContextContainer()) == contextContainer) {
                return contextInstanceContainer;
            } 
            contextInstanceContainer = getEnclosingContextInstanceContainer(((NodeInstance) (contextInstanceContainer)));
        }
        return null;
    }

    private ContextInstanceContainer getEnclosingContextInstanceContainer(NodeInstance nodeInstance) {
        NodeInstanceContainer nodeInstanceContainer = nodeInstance.getNodeInstanceContainer();
        while (true) {
            if (nodeInstanceContainer instanceof ContextInstanceContainer) {
                return ((ContextInstanceContainer) (nodeInstanceContainer));
            } 
            if (nodeInstanceContainer instanceof NodeInstance) {
                nodeInstanceContainer = ((NodeInstance) (nodeInstanceContainer)).getNodeInstanceContainer();
            } else {
                return null;
            }
        }
    }

    public Object getVariable(String variableName) {
        VariableScopeInstance variableScope = ((VariableScopeInstance) (resolveContextInstance(VariableScope.VARIABLE_SCOPE, variableName)));
        if (variableScope == null) {
            variableScope = ((VariableScopeInstance) (((ProcessInstance) (getProcessInstance())).getContextInstance(VariableScope.VARIABLE_SCOPE)));
        } 
        return variableScope.getVariable(variableName);
    }

    public void setVariable(String variableName, Object value) {
        VariableScopeInstance variableScope = ((VariableScopeInstance) (resolveContextInstance(VariableScope.VARIABLE_SCOPE, variableName)));
        if (variableScope == null) {
            variableScope = ((VariableScopeInstance) (getProcessInstance().getContextInstance(VariableScope.VARIABLE_SCOPE)));
            if ((variableScope.getVariableScope().findVariable(variableName)) == null) {
                variableScope = null;
            } 
        } 
        if (variableScope == null) {
            NodeInstanceImpl.logger.error("Could not find variable {}", variableName);
            NodeInstanceImpl.logger.error("Using process-level scope");
            variableScope = ((VariableScopeInstance) (((ProcessInstance) (getProcessInstance())).getContextInstance(VariableScope.VARIABLE_SCOPE)));
        } 
        variableScope.setVariable(variableName, value);
    }

    public String getUniqueId() {
        String result = "" + (getId());
        NodeInstanceContainer parent = getNodeInstanceContainer();
        while (parent instanceof CompositeNodeInstance) {
            CompositeNodeInstance nodeInstance = ((CompositeNodeInstance) (parent));
            result = ((nodeInstance.getId()) + ":") + result;
            parent = nodeInstance.getNodeInstanceContainer();
        }
        return result;
    }

    public Map<String, Object> getMetaData() {
        return NodeInstanceImpl.this.metaData;
    }

    public Object getMetaData(String name) {
        return NodeInstanceImpl.this.metaData.get(name);
    }

    public void setMetaData(String name, Object data) {
        NodeInstanceImpl.this.metaData.put(name, data);
    }

    protected class NodeInstanceTrigger {
        private NodeInstance nodeInstance;

        private String toType;

        public NodeInstanceTrigger(NodeInstance nodeInstance, String toType) {
            NodeInstanceImpl.NodeInstanceTrigger.this.nodeInstance = nodeInstance;
            NodeInstanceImpl.NodeInstanceTrigger.this.toType = toType;
        }

        public NodeInstance getNodeInstance() {
            return nodeInstance;
        }

        public String getToType() {
            return toType;
        }
    }

    public void setDynamicParameters(Map<String, Object> dynamicParameters) {
        NodeInstanceImpl.this.dynamicParameters = dynamicParameters;
    }
}

