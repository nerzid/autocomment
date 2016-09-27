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

import org.jbpm.process.core.async.AsyncSignalEventCommand;
import org.kie.api.executor.CommandContext;
import org.kie.api.runtime.process.EventListener;
import org.kie.api.executor.ExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.kie.api.definition.process.Node;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.manager.RuntimeManager;
import java.io.Serializable;
import java.util.UUID;
import org.kie.api.runtime.process.org.jbpm.workflow.instance.NodeInstance;

/**
 * Runtime counterpart of an event node.
 */
public class AsyncEventNodeInstance extends EventNodeInstance {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(AsyncEventNodeInstance.class);

    private String eventType = UUID.randomUUID().toString();

    private EventListener listener = new AsyncEventNodeInstance.AsyncExternalEventListener();

    public void internalTrigger(final NodeInstance from, String type) {
        super.internalTrigger(from, type);
        ExecutorService executorService = ((ExecutorService) (getProcessInstance().getKnowledgeRuntime().getEnvironment().get("ExecutorService")));
        if (executorService != null) {
            RuntimeManager runtimeManager = ((RuntimeManager) (getProcessInstance().getKnowledgeRuntime().getEnvironment().get("RuntimeManager")));
            CommandContext ctx = new CommandContext();
            ctx.setData("DeploymentId", runtimeManager.getIdentifier());
            ctx.setData("ProcessInstanceId", getProcessInstance().getId());
            ctx.setData("Signal", getEventType());
            ctx.setData("Event", null);
            executorService.scheduleRequest(AsyncSignalEventCommand.class.getName(), ctx);
        } else {
            AsyncEventNodeInstance.logger.warn("No async executor service found continuing as sync operation...");
            // if there is no executor service available move as sync node
            triggerCompleted();
        }
    }

    @Override
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        AsyncEventNodeInstance.this.eventType = eventType;
    }

    @Override
    public Node getNode() {
        return new org.jbpm.workflow.core.node.AsyncEventNode(super.getNode());
    }

    @Override
    public void triggerCompleted() {
        getProcessInstance().removeEventListener(getEventType(), getEventListener(), true);
        ((org.jbpm.workflow.instance.NodeInstanceContainer) (getNodeInstanceContainer())).setCurrentLevel(getLevel());
        ((org.jbpm.workflow.instance.NodeInstanceContainer) (getNodeInstanceContainer())).removeNodeInstance(AsyncEventNodeInstance.this);
        NodeInstance instance = ((org.jbpm.workflow.instance.NodeInstanceContainer) (getNodeInstanceContainer())).getNodeInstance(getNode());
        triggerNodeInstance(((org.jbpm.workflow.instance.NodeInstance) (instance)), NodeImpl.CONNECTION_DEFAULT_TYPE);
    }

    @Override
    protected EventListener getEventListener() {
        return AsyncEventNodeInstance.this.listener;
    }

    private class AsyncExternalEventListener implements Serializable , EventListener {
        private static final long serialVersionUID = 5L;

        public String[] getEventTypes() {
            return null;
        }

        public void signalEvent(String type, Object event) {
            triggerCompleted();
        }
    }
}

