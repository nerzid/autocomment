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


package org.jbpm.workflow.instance.node;

import java.util.ArrayList;
import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.core.command.CommandService;
import org.kie.internal.command.Context;
import org.kie.internal.process.CorrelationAwareProcessRuntime;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationKeyFactory;
import org.drools.core.command.impl.GenericCommand;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.kie.api.runtime.KieRuntime;
import org.drools.core.command.impl.KnowledgeCommandContext;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.kie.api.runtime.process.NodeInstance;
import org.jbpm.workflow.instance.impl.NodeInstanceImpl;
import org.drools.core.event.ProcessEventSupport;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.drools.core.process.instance.WorkItemManager;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;

public class DynamicUtils {
    private static final Logger logger = LoggerFactory.getLogger(DynamicUtils.class);

    public static void addDynamicWorkItem(final DynamicNodeInstance dynamicContext, KieRuntime ksession, String workItemName, Map<String, Object> parameters) {
        final WorkflowProcessInstance processInstance = dynamicContext.getProcessInstance();
        DynamicUtils.internalAddDynamicWorkItem(processInstance, dynamicContext, ksession, workItemName, parameters);
    }

    public static void addDynamicWorkItem(final ProcessInstance dynamicProcessInstance, KieRuntime ksession, String workItemName, Map<String, Object> parameters) {
        DynamicUtils.internalAddDynamicWorkItem(((WorkflowProcessInstance) (dynamicProcessInstance)), null, ksession, workItemName, parameters);
    }

    private static void internalAddDynamicWorkItem(final WorkflowProcessInstance processInstance, final DynamicNodeInstance dynamicContext, KieRuntime ksession, String workItemName, Map<String, Object> parameters) {
        final WorkItemImpl workItem = new WorkItemImpl();
        workItem.setState(WorkItem.ACTIVE);
        workItem.setProcessInstanceId(processInstance.getId());
        workItem.setDeploymentId(((String) (ksession.getEnvironment().get(EnvironmentName.DEPLOYMENT_ID))));
        workItem.setName(workItemName);
        workItem.setParameters(parameters);
        final WorkItemNodeInstance workItemNodeInstance = new WorkItemNodeInstance();
        workItemNodeInstance.internalSetWorkItem(workItem);
        workItemNodeInstance.setMetaData("NodeType", workItemName);
        workItem.setNodeInstanceId(workItemNodeInstance.getId());
        if (ksession instanceof StatefulKnowledgeSessionImpl) {
            workItemNodeInstance.setProcessInstance(processInstance);
            workItemNodeInstance.setNodeInstanceContainer((dynamicContext == null ? processInstance : dynamicContext));
            workItemNodeInstance.addEventListeners();
            DynamicUtils.executeWorkItem(((StatefulKnowledgeSessionImpl) (ksession)), workItem, workItemNodeInstance);
        } else if (ksession instanceof CommandBasedStatefulKnowledgeSession) {
            CommandService commandService = ((CommandBasedStatefulKnowledgeSession) (ksession)).getCommandService();
            commandService.execute(new GenericCommand<Void>() {
                private static final long serialVersionUID = 5L;

                public Void execute(Context context) {
                    StatefulKnowledgeSession ksession = ((StatefulKnowledgeSession) (((KnowledgeCommandContext) (context)).getKieSession()));
                    WorkflowProcessInstance realProcessInstance = ((WorkflowProcessInstance) (ksession.getProcessInstance(processInstance.getId())));
                    workItemNodeInstance.setProcessInstance(realProcessInstance);
                    if (dynamicContext == null) {
                        workItemNodeInstance.setNodeInstanceContainer(realProcessInstance);
                    } else {
                        DynamicNodeInstance realDynamicContext = DynamicUtils.findDynamicContext(realProcessInstance, dynamicContext.getUniqueId());
                        workItemNodeInstance.setNodeInstanceContainer(realDynamicContext);
                    }
                    workItemNodeInstance.addEventListeners();
                    DynamicUtils.executeWorkItem(((StatefulKnowledgeSessionImpl) (ksession)), workItem, workItemNodeInstance);
                    return null;
                }
            });
        } else {
            throw new IllegalArgumentException((("Unsupported ksession: " + ksession) == null ? "null" : ksession.getClass().getName()));
        }
    }

    private static void executeWorkItem(StatefulKnowledgeSessionImpl ksession, WorkItemImpl workItem, WorkItemNodeInstance workItemNodeInstance) {
        ProcessEventSupport eventSupport = ((InternalProcessRuntime) (ksession.getProcessRuntime())).getProcessEventSupport();
        eventSupport.fireBeforeNodeTriggered(workItemNodeInstance, ksession);
        ((WorkItemManager) (ksession.getWorkItemManager())).internalExecuteWorkItem(workItem);
        workItemNodeInstance.internalSetWorkItemId(workItem.getId());
        eventSupport.fireAfterNodeTriggered(workItemNodeInstance, ksession);
    }

    private static DynamicNodeInstance findDynamicContext(WorkflowProcessInstance processInstance, String uniqueId) {
        for (NodeInstance nodeInstance : ((WorkflowProcessInstanceImpl) (processInstance)).getNodeInstances(true)) {
            if (uniqueId.equals(((NodeInstanceImpl) (nodeInstance)).getUniqueId())) {
                return ((DynamicNodeInstance) (nodeInstance));
            } 
        }
        throw new IllegalArgumentException(("Could not find node instance " + uniqueId));
    }

    public static long addDynamicSubProcess(final DynamicNodeInstance dynamicContext, KieRuntime ksession, final String processId, final Map<String, Object> parameters) {
        final WorkflowProcessInstance processInstance = dynamicContext.getProcessInstance();
        return DynamicUtils.internalAddDynamicSubProcess(processInstance, dynamicContext, ksession, processId, parameters);
    }

    public static long addDynamicSubProcess(final ProcessInstance processInstance, KieRuntime ksession, final String processId, final Map<String, Object> parameters) {
        return DynamicUtils.internalAddDynamicSubProcess(((WorkflowProcessInstance) (processInstance)), null, ksession, processId, parameters);
    }

    public static long internalAddDynamicSubProcess(final WorkflowProcessInstance processInstance, final DynamicNodeInstance dynamicContext, KieRuntime ksession, final String processId, final Map<String, Object> parameters) {
        final SubProcessNodeInstance subProcessNodeInstance = new SubProcessNodeInstance();
        subProcessNodeInstance.setNodeInstanceContainer((dynamicContext == null ? processInstance : dynamicContext));
        subProcessNodeInstance.setProcessInstance(processInstance);
        if (ksession instanceof StatefulKnowledgeSessionImpl) {
            return DynamicUtils.executeSubProcess(((StatefulKnowledgeSessionImpl) (ksession)), processId, parameters, processInstance, subProcessNodeInstance);
        } else if (ksession instanceof CommandBasedStatefulKnowledgeSession) {
            CommandService commandService = ((CommandBasedStatefulKnowledgeSession) (ksession)).getCommandService();
            return commandService.execute(new GenericCommand<Long>() {
                private static final long serialVersionUID = 5L;

                public Long execute(Context context) {
                    StatefulKnowledgeSession ksession = ((StatefulKnowledgeSession) (((KnowledgeCommandContext) (context)).getKieSession()));
                    WorkflowProcessInstance realProcessInstance = ((WorkflowProcessInstance) (ksession.getProcessInstance(processInstance.getId())));
                    subProcessNodeInstance.setProcessInstance(realProcessInstance);
                    if (dynamicContext == null) {
                        subProcessNodeInstance.setNodeInstanceContainer(realProcessInstance);
                    } else {
                        DynamicNodeInstance realDynamicContext = DynamicUtils.findDynamicContext(realProcessInstance, dynamicContext.getUniqueId());
                        subProcessNodeInstance.setNodeInstanceContainer(realDynamicContext);
                    }
                    return DynamicUtils.executeSubProcess(((StatefulKnowledgeSessionImpl) (ksession)), processId, parameters, processInstance, subProcessNodeInstance);
                }
            });
        } else {
            throw new IllegalArgumentException((("Unsupported ksession: " + ksession) == null ? "null" : ksession.getClass().getName()));
        }
    }

    private static long executeSubProcess(StatefulKnowledgeSessionImpl ksession, String processId, Map<String, Object> parameters, ProcessInstance processInstance, SubProcessNodeInstance subProcessNodeInstance) {
        Process process = ksession.getKieBase().getProcess(processId);
        if (process == null) {
            DynamicUtils.logger.error("Could not find process {}", processId);
            DynamicUtils.logger.error("Aborting process");
            processInstance.setState(ProcessInstance.STATE_ABORTED);
            return -1;
        } else {
            ProcessEventSupport eventSupport = ((InternalProcessRuntime) (((InternalKnowledgeRuntime) (ksession)).getProcessRuntime())).getProcessEventSupport();
            eventSupport.fireBeforeNodeTriggered(subProcessNodeInstance, ksession);
            ProcessInstance subProcessInstance = null;
            if ((((WorkflowProcessInstanceImpl) (processInstance)).getCorrelationKey()) != null) {
                List<String> businessKeys = new ArrayList<String>();
                businessKeys.add(((WorkflowProcessInstanceImpl) (processInstance)).getCorrelationKey());
                businessKeys.add(processId);
                businessKeys.add(String.valueOf(System.currentTimeMillis()));
                CorrelationKeyFactory correlationKeyFactory = KieInternalServices.Factory.get().newCorrelationKeyFactory();
                CorrelationKey subProcessCorrelationKey = correlationKeyFactory.newCorrelationKey(businessKeys);
                subProcessInstance = ((ProcessInstance) (((CorrelationAwareProcessRuntime) (ksession)).createProcessInstance(processId, subProcessCorrelationKey, parameters)));
            } else {
                subProcessInstance = ((ProcessInstance) (ksession.createProcessInstance(processId, parameters)));
            }
            ((ProcessInstanceImpl) (subProcessInstance)).setMetaData("ParentProcessInstanceId", processInstance.getId());
            ((ProcessInstanceImpl) (subProcessInstance)).setParentProcessInstanceId(processInstance.getId());
            subProcessInstance = ((ProcessInstance) (ksession.startProcessInstance(subProcessInstance.getId())));
            eventSupport.fireAfterNodeTriggered(subProcessNodeInstance, ksession);
            if ((subProcessInstance.getState()) == (ProcessInstance.STATE_COMPLETED)) {
                subProcessNodeInstance.triggerCompleted();
            } else {
                subProcessNodeInstance.internalSetProcessInstanceId(subProcessInstance.getId());
                subProcessNodeInstance.addEventListeners();
            }
            return subProcessInstance.getId();
        }
    }
}

