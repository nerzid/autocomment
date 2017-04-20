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


package org.jbpm.workflow.instance.node;

import org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE;
import ExceptionScope.EXCEPTION_SCOPE;
import org.jbpm.process.instance.context.exception.ExceptionScopeInstance;
import org.jbpm.workflow.core.node.FaultNode;
import org.slf4j.Logger;
import VariableScope.VARIABLE_SCOPE;
import org.slf4j.LoggerFactory;
import org.kie.api.runtime.process.NodeInstance;
import org.jbpm.workflow.instance.NodeInstanceContainer;
import org.jbpm.workflow.instance.impl.NodeInstanceImpl;
import org.jbpm.process.instance.ProcessInstance;
import ProcessInstance.STATE_ABORTED;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;

/**
 * Runtime counterpart of a fault node.
 */
public class FaultNodeInstance extends NodeInstanceImpl {
    private static final long serialVersionUID = 510L;

    private static final Logger logger = LoggerFactory.getLogger(FaultNodeInstance.class);

    protected FaultNode getFaultNode() {
        return ((FaultNode) (getNode()));
    }

    public void internalTrigger(final NodeInstance from, String type) {
        if (!(CONNECTION_DEFAULT_TYPE.equals(type))) {
            throw new IllegalArgumentException("A FaultNode only accepts default incoming connections!");
        }
        String faultName = getFaultName();
        ExceptionScopeInstance exceptionScopeInstance = getExceptionScopeInstance(faultName);
        NodeInstanceContainer nodeInstanceContainer = ((NodeInstanceContainer) (getNodeInstanceContainer()));
        // remove node FaultNodeInstance{this} to NodeInstanceContainer{nodeInstanceContainer}
        nodeInstanceContainer.removeNodeInstance(this);
        boolean exceptionHandled = false;
        if (getFaultNode().isTerminateParent()) {
            // handle exception before canceling nodes to allow boundary event to catch the events
            if (exceptionScopeInstance != null) {
                exceptionHandled = true;
                handleException(faultName, exceptionScopeInstance);
            }
            if (nodeInstanceContainer instanceof CompositeNodeInstance) {
                ((CompositeNodeInstance) (nodeInstanceContainer)).cancel();
            }else
                if (nodeInstanceContainer instanceof org.kie.api.runtime.process.WorkflowProcessInstance) {
                    java.util.Collection<NodeInstance> nodeInstances = ((org.kie.api.runtime.process.WorkflowProcessInstance) (nodeInstanceContainer)).getNodeInstances();
                    for (NodeInstance nodeInstance : nodeInstances) {
                        ((org.kie.api.runtime.process.org.jbpm.workflow.instance.NodeInstance) (nodeInstance)).cancel();
                    }
                }
            
        }
        if (exceptionScopeInstance != null) {
            if (!exceptionHandled) {
                handleException(faultName, exceptionScopeInstance);
            }
        }else {
            ((ProcessInstance) (getProcessInstance())).setState(STATE_ABORTED, faultName, getFaultData());
        }
    }

    protected ExceptionScopeInstance getExceptionScopeInstance(String faultName) {
        return ((ExceptionScopeInstance) (resolveContextInstance(EXCEPTION_SCOPE, faultName)));
    }

    protected String getFaultName() {
        return getFaultNode().getFaultName();
    }

    protected Object getFaultData() {
        Object value = null;
        String faultVariable = getFaultNode().getFaultVariable();
        if (faultVariable != null) {
            VariableScopeInstance variableScopeInstance = ((VariableScopeInstance) (resolveContextInstance(VARIABLE_SCOPE, faultVariable)));
            if (variableScopeInstance != null) {
                value = variableScopeInstance.getVariable(faultVariable);
            }else {
                FaultNodeInstance.logger.error("Could not find variable scope for variable {}", faultVariable);
                FaultNodeInstance.logger.error("when trying to execute fault node {}", getFaultNode().getName());
                FaultNodeInstance.logger.error("Continuing without setting value.");
            }
        }
        return value;
    }

    protected void handleException(String faultName, ExceptionScopeInstance exceptionScopeInstance) {
        // handle exception String{faultName} to ExceptionScopeInstance{exceptionScopeInstance}
        exceptionScopeInstance.handleException(faultName, getFaultData());
    }
}

