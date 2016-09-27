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
import org.jbpm.workflow.instance.node.DynamicUtils;
import org.kie.api.runtime.KieSession;
import org.drools.core.command.impl.KnowledgeCommandContext;
import java.util.Map;
import org.kie.api.runtime.process.ProcessInstance;

/**
 * Adds task to selected ad hoc process instance with given parameters
 */
public class AddDynamicTaskCommand extends CaseCommand<Void> {
    private static final long serialVersionUID = 6345222909719335953L;

    private String caseId;

    private String nodeType;

    private long processInstanceId;

    private Map<String, Object> parameters;

    public AddDynamicTaskCommand(String caseId, String nodeType, Long processInstanceId, Map<String, Object> parameters) {
        AddDynamicTaskCommand.this.caseId = caseId;
        AddDynamicTaskCommand.this.nodeType = nodeType;
        AddDynamicTaskCommand.this.processInstanceId = processInstanceId;
        AddDynamicTaskCommand.this.parameters = parameters;
    }

    @Override
    public Void execute(Context context) {
        KieSession ksession = ((KnowledgeCommandContext) (context)).getKieSession();
        ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
        if (processInstance == null) {
            throw new org.jbpm.services.api.ProcessInstanceNotFoundException(("No process instance found with id " + (processInstanceId)));
        } 
        CaseEventSupport caseEventSupport = getCaseEventSupport(context);
        caseEventSupport.fireBeforeDynamicTaskAdded(caseId, processInstanceId, nodeType, parameters);
        DynamicUtils.addDynamicWorkItem(processInstance, ksession, nodeType, parameters);
        caseEventSupport.fireAfterDynamicTaskAdded(caseId, processInstanceId, nodeType, parameters);
        return null;
    }
}

