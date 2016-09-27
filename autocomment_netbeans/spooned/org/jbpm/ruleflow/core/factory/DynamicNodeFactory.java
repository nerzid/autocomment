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


package org.jbpm.ruleflow.core.factory;

import org.jbpm.process.core.context.exception.ActionExceptionHandler;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.drools.core.process.core.datatype.DataType;
import org.jbpm.workflow.core.node.DynamicNode;
import org.jbpm.process.core.context.exception.ExceptionHandler;
import org.jbpm.process.core.context.exception.ExceptionScope;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;

/**
 */
public class DynamicNodeFactory extends RuleFlowNodeContainerFactory {
    private RuleFlowNodeContainerFactory nodeContainerFactory;

    private NodeContainer nodeContainer;

    private long linkedIncomingNodeId = -1;

    private long linkedOutgoingNodeId = -1;

    public DynamicNodeFactory(RuleFlowNodeContainerFactory nodeContainerFactory, NodeContainer nodeContainer, long id) {
        DynamicNodeFactory.this.nodeContainerFactory = nodeContainerFactory;
        DynamicNodeFactory.this.nodeContainer = nodeContainer;
        DynamicNode compositeNode = new DynamicNode();
        compositeNode.setId(id);
        setNodeContainer(compositeNode);
    }

    protected CompositeContextNode getCompositeNode() {
        return ((CompositeContextNode) (getNodeContainer()));
    }

    protected DynamicNode getDynamicNode() {
        return ((DynamicNode) (getNodeContainer()));
    }

    public DynamicNodeFactory variable(String name, DataType type) {
        return variable(name, type, null);
    }

    public DynamicNodeFactory variable(String name, DataType type, Object value) {
        Variable variable = new Variable();
        variable.setName(name);
        variable.setType(type);
        variable.setValue(value);
        VariableScope variableScope = ((VariableScope) (getCompositeNode().getDefaultContext(VariableScope.VARIABLE_SCOPE)));
        if (variableScope == null) {
            variableScope = new VariableScope();
            getCompositeNode().addContext(variableScope);
            getCompositeNode().setDefaultContext(variableScope);
        } 
        variableScope.getVariables().add(variable);
        return DynamicNodeFactory.this;
    }

    public DynamicNodeFactory exceptionHandler(String exception, ExceptionHandler exceptionHandler) {
        ExceptionScope exceptionScope = ((ExceptionScope) (getCompositeNode().getDefaultContext(ExceptionScope.EXCEPTION_SCOPE)));
        if (exceptionScope == null) {
            exceptionScope = new ExceptionScope();
            getCompositeNode().addContext(exceptionScope);
            getCompositeNode().setDefaultContext(exceptionScope);
        } 
        exceptionScope.setExceptionHandler(exception, exceptionHandler);
        return DynamicNodeFactory.this;
    }

    public DynamicNodeFactory exceptionHandler(String exception, String dialect, String action) {
        ActionExceptionHandler exceptionHandler = new ActionExceptionHandler();
        exceptionHandler.setAction(new org.jbpm.workflow.core.impl.DroolsConsequenceAction(dialect, action));
        return exceptionHandler(exception, exceptionHandler);
    }

    public DynamicNodeFactory autoComplete(boolean autoComplete) {
        getDynamicNode().setAutoComplete(autoComplete);
        return DynamicNodeFactory.this;
    }

    public DynamicNodeFactory linkIncomingConnections(long nodeId) {
        DynamicNodeFactory.this.linkedIncomingNodeId = nodeId;
        return DynamicNodeFactory.this;
    }

    public DynamicNodeFactory linkOutgoingConnections(long nodeId) {
        DynamicNodeFactory.this.linkedOutgoingNodeId = nodeId;
        return DynamicNodeFactory.this;
    }

    public RuleFlowNodeContainerFactory done() {
        if ((linkedIncomingNodeId) != (-1)) {
            getCompositeNode().linkIncomingConnections(Node.CONNECTION_DEFAULT_TYPE, linkedIncomingNodeId, Node.CONNECTION_DEFAULT_TYPE);
        } 
        if ((linkedOutgoingNodeId) != (-1)) {
            getCompositeNode().linkOutgoingConnections(linkedOutgoingNodeId, Node.CONNECTION_DEFAULT_TYPE, Node.CONNECTION_DEFAULT_TYPE);
        } 
        nodeContainer.addNode(getCompositeNode());
        return nodeContainerFactory;
    }
}

