/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.jbpm.process.instance.context.exception;

import org.jbpm.workflow.core.node.BoundaryEventNode;
import java.util.Collection;
import org.jbpm.process.core.context.exception.CompensationHandler;
import org.jbpm.process.core.context.exception.CompensationScope;
import org.jbpm.workflow.instance.node.EventNodeInstance;
import org.jbpm.workflow.core.node.EventSubProcessNode;
import org.jbpm.workflow.instance.node.EventSubProcessNodeInstance;
import org.jbpm.process.core.context.exception.ExceptionHandler;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.kie.api.definition.process.Node;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.instance.NodeInstance;
import org.jbpm.workflow.instance.NodeInstanceContainer;
import org.jbpm.process.instance.ProcessInstance;
import java.util.Stack;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;

public class CompensationScopeInstance extends ExceptionScopeInstance {
    private static final long serialVersionUID = 510L;

    private Stack<NodeInstance> compensationInstances = new Stack<NodeInstance>();

    public String getContextType() {
        return CompensationScope.COMPENSATION_SCOPE;
    }

    public void addCompensationInstances(Collection<NodeInstance> generatedInstances) {
        CompensationScopeInstance.this.compensationInstances.addAll(generatedInstances);
    }

    public void handleException(String activityRef, Object dunno) {
        assert activityRef != null : "It should not be possible for the compensation activity reference to be null here.";
        CompensationScope compensationScope = ((CompensationScope) (getExceptionScope()));
        // broadcast/general compensation in reverse order
        if (activityRef.startsWith(CompensationScope.IMPLICIT_COMPENSATION_PREFIX)) {
            activityRef = activityRef.substring(CompensationScope.IMPLICIT_COMPENSATION_PREFIX.length());
            assert activityRef.equals(compensationScope.getContextContainerId()) : (((("Compensation activity ref [" + activityRef) + "] does not match") + " Compensation Scope container id [") + (compensationScope.getContextContainerId())) + "]";
            Map<String, ExceptionHandler> handlers = compensationScope.getExceptionHandlers();
            List<String> completedNodeIds = ((WorkflowProcessInstanceImpl) (getProcessInstance())).getCompletedNodeIds();
            ListIterator<String> iter = completedNodeIds.listIterator(completedNodeIds.size());
            while (iter.hasPrevious()) {
                String completedId = iter.previous();
                ExceptionHandler handler = handlers.get(completedId);
                if (handler != null) {
                    handleException(handler, completedId, null);
                } 
            }
        } else {
            // Specific compensation
            ExceptionHandler handler = compensationScope.getExceptionHandler(activityRef);
            if (handler == null) {
                throw new IllegalArgumentException(("Could not find CompensationHandler for " + activityRef));
            } 
            handleException(handler, activityRef, null);
        }
        // Cancel all node instances created for compensation
        while (!(compensationInstances.isEmpty())) {
            NodeInstance generatedInstance = compensationInstances.pop();
            ((NodeInstanceContainer) (generatedInstance.getNodeInstanceContainer())).removeNodeInstance(generatedInstance);
        }
    }

    public void handleException(ExceptionHandler handler, String compensationActivityRef, Object dunno) {
        WorkflowProcessInstanceImpl processInstance = ((WorkflowProcessInstanceImpl) (getProcessInstance()));
        NodeInstanceContainer nodeInstanceContainer = ((NodeInstanceContainer) (getContextInstanceContainer()));
        if (handler instanceof CompensationHandler) {
            CompensationHandler compensationHandler = ((CompensationHandler) (handler));
            try {
                Node handlerNode = compensationHandler.getnode();
                if (handlerNode instanceof BoundaryEventNode) {
                    NodeInstance compensationHandlerNodeInstance = nodeInstanceContainer.getNodeInstance(handlerNode);
                    compensationInstances.add(compensationHandlerNodeInstance);
                    // The BoundaryEventNodeInstance.signalEvent() contains the necessary logic
                    // to check whether or not compensation may proceed (? : (not-active + completed))
                    EventNodeInstance eventNodeInstance = ((EventNodeInstance) (compensationHandlerNodeInstance));
                    eventNodeInstance.signalEvent("Compensation", compensationActivityRef);
                } else if (handlerNode instanceof EventSubProcessNode) {
                    // Check that subprocess parent has completed.
                    List<String> completedIds = processInstance.getCompletedNodeIds();
                    if (completedIds.contains(((NodeImpl) (handlerNode.getNodeContainer())).getMetaData("UniqueId"))) {
                        NodeInstance subProcessNodeInstance = ((NodeInstanceContainer) (nodeInstanceContainer)).getNodeInstance(((Node) (handlerNode.getNodeContainer())));
                        compensationInstances.add(subProcessNodeInstance);
                        NodeInstance compensationHandlerNodeInstance = ((NodeInstanceContainer) (subProcessNodeInstance)).getNodeInstance(handlerNode);
                        compensationInstances.add(compensationHandlerNodeInstance);
                        EventSubProcessNodeInstance eventNodeInstance = ((EventSubProcessNodeInstance) (compensationHandlerNodeInstance));
                        eventNodeInstance.signalEvent("Compensation", compensationActivityRef);
                    } 
                } 
                assert (handlerNode instanceof BoundaryEventNode) || (handlerNode instanceof EventSubProcessNode) : "Unexpected compensation handler node type : " + (handlerNode.getClass().getSimpleName());
            } catch (Exception e) {
                throwWorkflowRuntimeException(nodeInstanceContainer, processInstance, "Unable to execute compensation.", e);
            }
        } else {
            Exception e = new IllegalArgumentException(("Unsupported compensation handler: " + handler));
            throwWorkflowRuntimeException(nodeInstanceContainer, processInstance, e.getMessage(), e);
        }
    }

    private void throwWorkflowRuntimeException(NodeInstanceContainer nodeInstanceContainer, ProcessInstance processInstance, String msg, Exception e) {
        if (nodeInstanceContainer instanceof NodeInstance) {
            throw new org.jbpm.workflow.instance.WorkflowRuntimeException(((org.kie.api.runtime.process.NodeInstance) (nodeInstanceContainer)), processInstance, msg, e);
        } else {
            throw new org.jbpm.workflow.instance.WorkflowRuntimeException(null, processInstance, msg, e);
        }
    }
}

