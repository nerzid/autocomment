/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.executor.impl.wih;

import java.util.Collection;
import org.kie.api.executor.CommandCallback;
import org.kie.api.executor.CommandContext;
import org.kie.internal.command.Context;
import org.jbpm.process.instance.context.exception.ExceptionScopeInstance;
import org.kie.api.executor.ExecutionResults;
import org.drools.core.command.impl.GenericCommand;
import org.kie.api.runtime.KieSession;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.kie.api.runtime.process.NodeInstance;
import org.jbpm.workflow.instance.NodeInstanceContainer;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.api.runtime.process.WorkItem;
import org.jbpm.workflow.instance.node.WorkItemNodeInstance;
import org.jbpm.workflow.instance.WorkflowProcessInstance;

/**
 * Dedicated callback for <code>AsyncWorkItemHandler</code> that is responsible for:
 * <ul>
 *  <li>completing work item in case of successful execution</li>
 *  <li>attempting to handle exception (by utilizing ExceptionScope mechanism) in case of unsuccessful execution</li>
 * </ul>
 */
public class AsyncWorkItemHandlerCmdCallback implements CommandCallback {
    private static final Logger logger = LoggerFactory.getLogger(AsyncWorkItemHandlerCmdCallback.class);

    @Override
    public void onCommandDone(CommandContext ctx, ExecutionResults results) {
        WorkItem workItem = ((WorkItem) (ctx.getData("workItem")));
        AsyncWorkItemHandlerCmdCallback.logger.debug("About to complete work item {}", workItem);
        // find the right runtime to do the complete
        RuntimeManager manager = getRuntimeManager(ctx);
        RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(((Long) (ctx.getData("processInstanceId")))));
        try {
            engine.getKieSession().getWorkItemManager().completeWorkItem(workItem.getId(), (results == null ? null : results.getData()));
        } finally {
            manager.disposeRuntimeEngine(engine);
        }
    }

    @Override
    public void onCommandError(CommandContext ctx, final Throwable exception) {
        final Long processInstanceId = ((Long) (ctx.getData("processInstanceId")));
        final WorkItem workItem = ((WorkItem) (ctx.getData("workItem")));
        // find the right runtime to do the complete
        RuntimeManager manager = getRuntimeManager(ctx);
        RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
        try {
            engine.getKieSession().execute(new GenericCommand<Void>() {
                private static final long serialVersionUID = 1L;

                @Override
                public Void execute(Context context) {
                    KieSession ksession = ((KnowledgeCommandContext) (context)).getKieSession();
                    WorkflowProcessInstance processInstance = ((WorkflowProcessInstance) (ksession.getProcessInstance(processInstanceId)));
                    NodeInstance nodeInstance = getNodeInstance(workItem, processInstance);
                    String exceptionName = exception.getClass().getName();
                    ExceptionScopeInstance exceptionScopeInstance = ((ExceptionScopeInstance) (((org.jbpm.workflow.instance.NodeInstance) (nodeInstance)).resolveContextInstance(ExceptionScope.EXCEPTION_SCOPE, exceptionName)));
                    if (exceptionScopeInstance != null) {
                        exceptionScopeInstance.handleException(exceptionName, exception);
                    } 
                    return null;
                }
            });
        } catch (Exception e) {
            AsyncWorkItemHandlerCmdCallback.logger.error("Error when handling callback from executor", e);
        } finally {
            manager.disposeRuntimeEngine(engine);
        }
    }

    protected RuntimeManager getRuntimeManager(CommandContext ctx) {
        String deploymentId = ((String) (ctx.getData("deploymentId")));
        RuntimeManager runtimeManager = RuntimeManagerRegistry.get().getManager(deploymentId);
        if (runtimeManager == null) {
            throw new IllegalStateException(("There is no runtime manager for deployment " + deploymentId));
        } 
        return runtimeManager;
    }

    protected NodeInstance getNodeInstance(WorkItem workItem, WorkflowProcessInstance processInstance) {
        Collection<NodeInstance> nodeInstances = processInstance.getNodeInstances();
        return getNodeInstance(workItem, nodeInstances);
    }

    protected NodeInstance getNodeInstance(WorkItem workItem, Collection<NodeInstance> nodeInstances) {
        for (NodeInstance nodeInstance : nodeInstances) {
            if (nodeInstance instanceof WorkItemNodeInstance) {
                if ((((WorkItemNodeInstance) (nodeInstance)).getWorkItemId()) == (workItem.getId())) {
                    return nodeInstance;
                } 
            } else if (nodeInstance instanceof NodeInstanceContainer) {
                NodeInstance found = getNodeInstance(workItem, ((NodeInstanceContainer) (nodeInstance)).getNodeInstances());
                if (found != null) {
                    return found;
                } 
            } 
        }
        return null;
    }
}

