/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.casemgmt.impl.command;

import org.jbpm.casemgmt.impl.event.CaseEventSupport;
import org.kie.internal.command.Context;
import org.jbpm.workflow.instance.node.DynamicNodeInstance;
import org.jbpm.workflow.instance.node.DynamicUtils;
import org.kie.api.runtime.KieSession;
import org.drools.core.command.impl.KnowledgeCommandContext;
import java.util.Map;
import org.kie.api.runtime.process.ProcessInstance;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;

/**
 * Adds task to given stage within selected ad hoc process instance with given parameters
 */
public class AddDynamicTaskToStageCommand extends CaseCommand<Void> {
    private static final long serialVersionUID = 6345222909719335954L;

    private String caseId;

    private String nodeType;

    private String stageId;

    private long processInstanceId;

    private Map<String, Object> parameters;

    public AddDynamicTaskToStageCommand(String caseId, String nodeType, Long processInstanceId, String stageId, Map<String, Object> parameters) {
        AddDynamicTaskToStageCommand.this.caseId = caseId;
        AddDynamicTaskToStageCommand.this.nodeType = nodeType;
        AddDynamicTaskToStageCommand.this.processInstanceId = processInstanceId;
        AddDynamicTaskToStageCommand.this.stageId = stageId;
        AddDynamicTaskToStageCommand.this.parameters = parameters;
        if ((processInstanceId == null) || (stageId == null)) {
            throw new IllegalArgumentException("Process instance id and stage id are mandatory");
        } 
    }

    @Override
    public Void execute(Context context) {
        KieSession ksession = ((KnowledgeCommandContext) (context)).getKieSession();
        ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
        DynamicNodeInstance dynamicContext = ((DynamicNodeInstance) (((WorkflowProcessInstanceImpl) (processInstance)).getNodeInstances(true).stream().filter(( ni) -> (ni instanceof ) && (stageId.equals(ni.getNode().getMetaData().get("UniqueId")))).findFirst().orElse(null)));
        if (dynamicContext == null) {
            throw new org.jbpm.casemgmt.api.StageNotFoundException(("No stage found with id " + (stageId)));
        } 
        CaseEventSupport caseEventSupport = getCaseEventSupport(context);
        caseEventSupport.fireBeforeDynamicTaskAdded(caseId, processInstanceId, nodeType, parameters);
        DynamicUtils.addDynamicWorkItem(dynamicContext, ksession, nodeType, parameters);
        caseEventSupport.fireAfterDynamicTaskAdded(caseId, processInstanceId, nodeType, parameters);
        return null;
    }
}

