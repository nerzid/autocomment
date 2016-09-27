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

import java.util.ArrayList;
import java.util.Arrays;
import org.jbpm.workflow.core.node.Assignment;
import org.jbpm.process.instance.impl.AssignmentAction;
import org.jbpm.process.core.Context;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.instance.ContextInstance;
import org.jbpm.process.instance.ContextInstanceContainer;
import org.jbpm.process.instance.impl.ContextInstanceFactory;
import org.jbpm.workflow.core.node.DataAssociation;
import org.kie.api.runtime.process.DataTransformer;
import org.jbpm.process.core.impl.DataTransformerRegistry;
import org.drools.core.process.core.datatype.DataType;
import org.kie.api.runtime.process.EventListener;
import org.jbpm.process.instance.context.exception.ExceptionScopeInstance;
import java.util.HashMap;
import java.util.Iterator;
import org.kie.internal.runtime.KnowledgeRuntime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.drools.core.util.MVELSafeHelper;
import java.util.Map;
import java.util.regex.Matcher;
import org.kie.api.definition.process.Node;
import org.kie.api.runtime.process.NodeInstance;
import org.drools.core.spi.ProcessContext;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.workflow.core.node.Transformation;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.drools.core.process.core.Work;
import org.drools.core.process.instance.WorkItem;
import org.drools.core.WorkItemHandlerNotFoundException;
import org.drools.core.process.instance.WorkItemManager;
import org.jbpm.workflow.core.node.WorkItemNode;

/**
 * Runtime counterpart of a work item node.
 */
public class WorkItemNodeInstance extends StateBasedNodeInstance implements ContextInstanceContainer , EventListener {
    private static final long serialVersionUID = 510L;

    private static final Logger logger = LoggerFactory.getLogger(WorkItemNodeInstance.class);

    private static boolean variableStrictEnabled = Boolean.parseBoolean(System.getProperty("org.jbpm.variable.strict", "false"));

    private static List<String> defaultOutputVariables = Arrays.asList(new String[]{ "ActorId" });

    // NOTE: ContetxInstances are not persisted as current functionality (exception scope) does not require it
    private Map<String, ContextInstance> contextInstances = new HashMap<String, ContextInstance>();

    private Map<String, List<ContextInstance>> subContextInstances = new HashMap<String, List<ContextInstance>>();

    private long workItemId = -1;

    private transient WorkItem workItem;

    protected WorkItemNode getWorkItemNode() {
        return ((WorkItemNode) (getNode()));
    }

    public WorkItem getWorkItem() {
        if (((workItem) == null) && ((workItemId) >= 0)) {
            workItem = ((WorkItemManager) (((ProcessInstance) (getProcessInstance())).getKnowledgeRuntime().getWorkItemManager())).getWorkItem(workItemId);
        } 
        return workItem;
    }

    public long getWorkItemId() {
        return workItemId;
    }

    public void internalSetWorkItemId(long workItemId) {
        WorkItemNodeInstance.this.workItemId = workItemId;
    }

    public void internalSetWorkItem(WorkItem workItem) {
        WorkItemNodeInstance.this.workItem = workItem;
    }

    public boolean isInversionOfControl() {
        // TODO WorkItemNodeInstance.isInversionOfControl
        return false;
    }

    public void internalTrigger(final NodeInstance from, String type) {
        super.internalTrigger(from, type);
        // if node instance was cancelled, abort
        if ((getNodeInstanceContainer().getNodeInstance(getId())) == null) {
            return ;
        } 
        // TODO this should be included for ruleflow only, not for BPEL
        // if (!Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
        // throw new IllegalArgumentException(
        // "A WorkItemNode only accepts default incoming connections!");
        // }
        WorkItemNode workItemNode = getWorkItemNode();
        createWorkItem(workItemNode);
        if (workItemNode.isWaitForCompletion()) {
            addWorkItemListener();
        } 
        String deploymentId = ((String) (getProcessInstance().getKnowledgeRuntime().getEnvironment().get(EnvironmentName.DEPLOYMENT_ID)));
        ((WorkItem) (workItem)).setDeploymentId(deploymentId);
        ((WorkItem) (workItem)).setNodeInstanceId(WorkItemNodeInstance.this.getId());
        ((WorkItem) (workItem)).setNodeId(getNodeId());
        if (isInversionOfControl()) {
            ((ProcessInstance) (getProcessInstance())).getKnowledgeRuntime().update(((ProcessInstance) (getProcessInstance())).getKnowledgeRuntime().getFactHandle(WorkItemNodeInstance.this), WorkItemNodeInstance.this);
        } else {
            try {
                ((WorkItemManager) (((ProcessInstance) (getProcessInstance())).getKnowledgeRuntime().getWorkItemManager())).internalExecuteWorkItem(((WorkItem) (workItem)));
            } catch (WorkItemHandlerNotFoundException wihnfe) {
                getProcessInstance().setState(ProcessInstance.STATE_ABORTED);
                throw wihnfe;
            } catch (Exception e) {
                String exceptionName = e.getClass().getName();
                ExceptionScopeInstance exceptionScopeInstance = ((ExceptionScopeInstance) (resolveContextInstance(ExceptionScope.EXCEPTION_SCOPE, exceptionName)));
                if (exceptionScopeInstance == null) {
                    throw new org.jbpm.workflow.instance.WorkflowRuntimeException(WorkItemNodeInstance.this, getProcessInstance(), ("Unable to execute Action: " + (e.getMessage())), e);
                } 
                // workItemId must be set otherwise cancel activity will not find the right work item
                WorkItemNodeInstance.this.workItemId = workItem.getId();
                exceptionScopeInstance.handleException(exceptionName, e);
            }
        }
        if (!(workItemNode.isWaitForCompletion())) {
            triggerCompleted();
        } 
        WorkItemNodeInstance.this.workItemId = workItem.getId();
    }

    protected WorkItem createWorkItem(WorkItemNode workItemNode) {
        Work work = workItemNode.getWork();
        workItem = new org.drools.core.process.instance.impl.WorkItemImpl();
        ((WorkItem) (workItem)).setName(work.getName());
        ((WorkItem) (workItem)).setProcessInstanceId(getProcessInstance().getId());
        ((WorkItem) (workItem)).setParameters(new HashMap<String, Object>(work.getParameters()));
        // if there are any dynamic parameters add them
        if ((dynamicParameters) != null) {
            ((WorkItem) (workItem)).getParameters().putAll(dynamicParameters);
        } 
        for (Iterator<DataAssociation> iterator = workItemNode.getInAssociations().iterator(); iterator.hasNext();) {
            DataAssociation association = iterator.next();
            if ((association.getTransformation()) != null) {
                Transformation transformation = association.getTransformation();
                DataTransformer transformer = DataTransformerRegistry.get().find(transformation.getLanguage());
                if (transformer != null) {
                    Object parameterValue = transformer.transform(transformation.getCompiledExpression(), getSourceParameters(association));
                    if (parameterValue != null) {
                        ((WorkItem) (workItem)).setParameter(association.getTarget(), parameterValue);
                    } 
                } 
            } else if (((association.getAssignments()) == null) || (association.getAssignments().isEmpty())) {
                Object parameterValue = null;
                VariableScopeInstance variableScopeInstance = ((VariableScopeInstance) (resolveContextInstance(VariableScope.VARIABLE_SCOPE, association.getSources().get(0))));
                if (variableScopeInstance != null) {
                    parameterValue = variableScopeInstance.getVariable(association.getSources().get(0));
                } else {
                    try {
                        parameterValue = MVELSafeHelper.getEvaluator().eval(association.getSources().get(0), new org.jbpm.workflow.instance.impl.NodeInstanceResolverFactory(WorkItemNodeInstance.this));
                    } catch (Throwable t) {
                        WorkItemNodeInstance.logger.error("Could not find variable scope for variable {}", association.getSources().get(0));
                        WorkItemNodeInstance.logger.error("when trying to execute Work Item {}", work.getName());
                        WorkItemNodeInstance.logger.error("Continuing without setting parameter.");
                    }
                }
                if (parameterValue != null) {
                    ((WorkItem) (workItem)).setParameter(association.getTarget(), parameterValue);
                } 
            } else {
                for (Iterator<Assignment> it = association.getAssignments().iterator(); it.hasNext();) {
                    handleAssignment(it.next());
                }
            }
        }
        for (Map.Entry<String, Object> entry : workItem.getParameters().entrySet()) {
            if ((entry.getValue()) instanceof String) {
                String s = ((String) (entry.getValue()));
                Map<String, String> replacements = new HashMap<String, String>();
                Matcher matcher = PARAMETER_MATCHER.matcher(s);
                while (matcher.find()) {
                    String paramName = matcher.group(1);
                    if ((replacements.get(paramName)) == null) {
                        VariableScopeInstance variableScopeInstance = ((VariableScopeInstance) (resolveContextInstance(VariableScope.VARIABLE_SCOPE, paramName)));
                        if (variableScopeInstance != null) {
                            Object variableValue = variableScopeInstance.getVariable(paramName);
                            String variableValueString = variableValue == null ? "" : variableValue.toString();
                            replacements.put(paramName, variableValueString);
                        } else {
                            try {
                                Object variableValue = MVELSafeHelper.getEvaluator().eval(paramName, new org.jbpm.workflow.instance.impl.NodeInstanceResolverFactory(WorkItemNodeInstance.this));
                                String variableValueString = variableValue == null ? "" : variableValue.toString();
                                replacements.put(paramName, variableValueString);
                            } catch (Throwable t) {
                                WorkItemNodeInstance.logger.error("Could not find variable scope for variable {}", paramName);
                                WorkItemNodeInstance.logger.error("when trying to replace variable in string for Work Item {}", work.getName());
                                WorkItemNodeInstance.logger.error("Continuing without setting parameter.");
                            }
                        }
                    } 
                }
                for (Map.Entry<String, String> replacement : replacements.entrySet()) {
                    s = s.replace((("#{" + (replacement.getKey())) + "}"), replacement.getValue());
                }
                ((WorkItem) (workItem)).setParameter(entry.getKey(), s);
            } 
        }
        return workItem;
    }

    private void handleAssignment(Assignment assignment) {
        AssignmentAction action = ((AssignmentAction) (assignment.getMetaData("Action")));
        try {
            ProcessContext context = new ProcessContext(getProcessInstance().getKnowledgeRuntime());
            context.setNodeInstance(WorkItemNodeInstance.this);
            action.execute(getWorkItem(), context);
        } catch (Exception e) {
            throw new RuntimeException("unable to execute Assignment", e);
        }
    }

    public void triggerCompleted(WorkItem workItem) {
        WorkItemNodeInstance.this.workItem = workItem;
        WorkItemNode workItemNode = getWorkItemNode();
        if ((workItemNode != null) && ((workItem.getState()) == (WorkItem.COMPLETED))) {
            validateWorkItemResultVariable(getProcessInstance().getProcessName(), workItemNode.getOutAssociations(), workItem);
            for (Iterator<DataAssociation> iterator = getWorkItemNode().getOutAssociations().iterator(); iterator.hasNext();) {
                DataAssociation association = iterator.next();
                if ((association.getTransformation()) != null) {
                    Transformation transformation = association.getTransformation();
                    DataTransformer transformer = DataTransformerRegistry.get().find(transformation.getLanguage());
                    if (transformer != null) {
                        Object parameterValue = transformer.transform(transformation.getCompiledExpression(), workItem.getResults());
                        VariableScopeInstance variableScopeInstance = ((VariableScopeInstance) (resolveContextInstance(VariableScope.VARIABLE_SCOPE, association.getTarget())));
                        if ((variableScopeInstance != null) && (parameterValue != null)) {
                            variableScopeInstance.getVariableScope().validateVariable(getProcessInstance().getProcessName(), association.getTarget(), parameterValue);
                            variableScopeInstance.setVariable(association.getTarget(), parameterValue);
                        } else {
                            WorkItemNodeInstance.logger.warn("Could not find variable scope for variable {}", association.getTarget());
                            WorkItemNodeInstance.logger.warn("when trying to complete Work Item {}", workItem.getName());
                            WorkItemNodeInstance.logger.warn("Continuing without setting variable.");
                        }
                        if (parameterValue != null) {
                            ((WorkItem) (workItem)).setParameter(association.getTarget(), parameterValue);
                        } 
                    } 
                } else if (((association.getAssignments()) == null) || (association.getAssignments().isEmpty())) {
                    VariableScopeInstance variableScopeInstance = ((VariableScopeInstance) (resolveContextInstance(VariableScope.VARIABLE_SCOPE, association.getTarget())));
                    if (variableScopeInstance != null) {
                        Object value = workItem.getResult(association.getSources().get(0));
                        if (value == null) {
                            try {
                                value = MVELSafeHelper.getEvaluator().eval(association.getSources().get(0), new org.jbpm.workflow.instance.impl.WorkItemResolverFactory(workItem));
                            } catch (Throwable t) {
                                // do nothing
                            }
                        } 
                        Variable varDef = variableScopeInstance.getVariableScope().findVariable(association.getTarget());
                        DataType dataType = varDef.getType();
                        // exclude java.lang.Object as it is considered unknown type
                        if (((!(dataType.getStringType().endsWith("java.lang.Object"))) && (!(dataType.getStringType().endsWith("Object")))) && (value instanceof String)) {
                            value = dataType.readValue(((String) (value)));
                        } else {
                            variableScopeInstance.getVariableScope().validateVariable(getProcessInstance().getProcessName(), association.getTarget(), value);
                        }
                        variableScopeInstance.setVariable(association.getTarget(), value);
                    } else {
                        WorkItemNodeInstance.logger.warn("Could not find variable scope for variable {}", association.getTarget());
                        WorkItemNodeInstance.logger.warn("when trying to complete Work Item {}", workItem.getName());
                        WorkItemNodeInstance.logger.warn("Continuing without setting variable.");
                    }
                } else {
                    try {
                        for (Iterator<Assignment> it = association.getAssignments().iterator(); it.hasNext();) {
                            handleAssignment(it.next());
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } 
        // handle dynamic nodes
        if ((getNode()) == null) {
            setMetaData("NodeType", workItem.getName());
            Map<String, Object> results = workItem.getResults();
            if ((results != null) && (!(results.isEmpty()))) {
                VariableScope variableScope = ((VariableScope) (((ContextContainer) (getProcessInstance().getProcess())).getDefaultContext(VariableScope.VARIABLE_SCOPE)));
                VariableScopeInstance variableScopeInstance = ((VariableScopeInstance) ((VariableScopeInstance) (getProcessInstance().getContextInstance(VariableScope.VARIABLE_SCOPE))));
                for (Map.Entry<String, Object> result : results.entrySet()) {
                    if ((variableScope.findVariable(result.getKey())) != null) {
                        variableScopeInstance.getVariableScope().validateVariable(getProcessInstance().getProcessName(), result.getKey(), result.getValue());
                        variableScopeInstance.setVariable(result.getKey(), result.getValue());
                    } 
                }
            } 
        } 
        if (isInversionOfControl()) {
            KnowledgeRuntime kruntime = ((ProcessInstance) (getProcessInstance())).getKnowledgeRuntime();
            kruntime.update(kruntime.getFactHandle(WorkItemNodeInstance.this), WorkItemNodeInstance.this);
        } else {
            triggerCompleted();
        }
    }

    public void cancel() {
        WorkItem workItem = getWorkItem();
        if (((workItem != null) && ((workItem.getState()) != (WorkItem.COMPLETED))) && ((workItem.getState()) != (WorkItem.ABORTED))) {
            try {
                ((WorkItemManager) (((ProcessInstance) (getProcessInstance())).getKnowledgeRuntime().getWorkItemManager())).internalAbortWorkItem(workItemId);
            } catch (WorkItemHandlerNotFoundException wihnfe) {
                getProcessInstance().setState(ProcessInstance.STATE_ABORTED);
                throw wihnfe;
            }
        } 
        super.cancel();
    }

    public void addEventListeners() {
        super.addEventListeners();
        addWorkItemListener();
    }

    private void addWorkItemListener() {
        getProcessInstance().addEventListener("workItemCompleted", WorkItemNodeInstance.this, false);
        getProcessInstance().addEventListener("workItemAborted", WorkItemNodeInstance.this, false);
    }

    public void removeEventListeners() {
        super.removeEventListeners();
        getProcessInstance().removeEventListener("workItemCompleted", WorkItemNodeInstance.this, false);
        getProcessInstance().removeEventListener("workItemAborted", WorkItemNodeInstance.this, false);
    }

    @Override
    public void signalEvent(String type, Object event) {
        if ("workItemCompleted".equals(type)) {
            workItemCompleted(((WorkItem) (event)));
        } else if ("workItemAborted".equals(type)) {
            workItemAborted(((WorkItem) (event)));
        } else {
            super.signalEvent(type, event);
        }
    }

    public String[] getEventTypes() {
        return new String[]{ "workItemCompleted" };
    }

    public void workItemAborted(WorkItem workItem) {
        if (((workItemId) == (workItem.getId())) || (((workItemId) == (-1)) && ((getWorkItem().getId()) == (workItem.getId())))) {
            removeEventListeners();
            triggerCompleted(workItem);
        } 
    }

    public void workItemCompleted(WorkItem workItem) {
        if (((workItemId) == (workItem.getId())) || (((workItemId) == (-1)) && ((getWorkItem().getId()) == (workItem.getId())))) {
            removeEventListeners();
            triggerCompleted(workItem);
        } 
    }

    public String getNodeName() {
        Node node = getNode();
        if (node == null) {
            String nodeName = "[Dynamic]";
            WorkItem workItem = getWorkItem();
            if (workItem != null) {
                nodeName += " " + (workItem.getParameter("TaskName"));
            } 
            return nodeName;
        } 
        return super.getNodeName();
    }

    @Override
    public List<ContextInstance> getContextInstances(String contextId) {
        return WorkItemNodeInstance.this.subContextInstances.get(contextId);
    }

    @Override
    public void addContextInstance(String contextId, ContextInstance contextInstance) {
        List<ContextInstance> list = WorkItemNodeInstance.this.subContextInstances.get(contextId);
        if (list == null) {
            list = new ArrayList<ContextInstance>();
            WorkItemNodeInstance.this.subContextInstances.put(contextId, list);
        } 
        list.add(contextInstance);
    }

    @Override
    public void removeContextInstance(String contextId, ContextInstance contextInstance) {
        List<ContextInstance> list = WorkItemNodeInstance.this.subContextInstances.get(contextId);
        if (list != null) {
            list.remove(contextInstance);
        } 
    }

    @Override
    public ContextInstance getContextInstance(String contextId, long id) {
        List<ContextInstance> contextInstances = subContextInstances.get(contextId);
        if (contextInstances != null) {
            for (ContextInstance contextInstance : contextInstances) {
                if ((contextInstance.getContextId()) == id) {
                    return contextInstance;
                } 
            }
        } 
        return null;
    }

    @Override
    public ContextInstance getContextInstance(Context context) {
        ContextInstanceFactory conf = ContextInstanceFactoryRegistry.INSTANCE.getContextInstanceFactory(context);
        if (conf == null) {
            throw new IllegalArgumentException(("Illegal context type (registry not found): " + (context.getClass())));
        } 
        ContextInstance contextInstance = ((ContextInstance) (conf.getContextInstance(context, WorkItemNodeInstance.this, ((ProcessInstance) (getProcessInstance())))));
        if (contextInstance == null) {
            throw new IllegalArgumentException(("Illegal context type (instance not found): " + (context.getClass())));
        } 
        return contextInstance;
    }

    @Override
    public ContextContainer getContextContainer() {
        return getWorkItemNode();
    }

    protected Map<String, Object> getSourceParameters(DataAssociation association) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        for (String sourceParam : association.getSources()) {
            Object parameterValue = null;
            VariableScopeInstance variableScopeInstance = ((VariableScopeInstance) (resolveContextInstance(VariableScope.VARIABLE_SCOPE, sourceParam)));
            if (variableScopeInstance != null) {
                parameterValue = variableScopeInstance.getVariable(sourceParam);
            } else {
                try {
                    parameterValue = MVELSafeHelper.getEvaluator().eval(sourceParam, new org.jbpm.workflow.instance.impl.NodeInstanceResolverFactory(WorkItemNodeInstance.this));
                } catch (Throwable t) {
                    WorkItemNodeInstance.logger.warn("Could not find variable scope for variable {}", sourceParam);
                }
            }
            if (parameterValue != null) {
                parameters.put(association.getTarget(), parameterValue);
            } 
        }
        return parameters;
    }

    public void validateWorkItemResultVariable(String processName, List<DataAssociation> outputs, WorkItem workItem) {
        // in case work item results are skip validation as there is no notion of mandatory data outputs
        if ((!(WorkItemNodeInstance.variableStrictEnabled)) || (workItem.getResults().isEmpty())) {
            return ;
        } 
        List<String> outputNames = new ArrayList<String>();
        for (DataAssociation association : outputs) {
            if ((association.getSources()) != null) {
                outputNames.add(association.getSources().get(0));
            } 
            if ((association.getAssignments()) != null) {
                for (Iterator<Assignment> it = association.getAssignments().iterator(); it.hasNext();) {
                    outputNames.add(it.next().getFrom());
                }
            } 
        }
        for (String outputName : workItem.getResults().keySet()) {
            if ((!(outputNames.contains(outputName))) && (!(WorkItemNodeInstance.defaultOutputVariables.contains(outputName)))) {
                throw new IllegalArgumentException((((((("Data output '" + outputName) + "' is not defined in process '") + processName) + "' for task '") + (workItem.getParameter("NodeName"))) + "'"));
            } 
        }
    }
}

