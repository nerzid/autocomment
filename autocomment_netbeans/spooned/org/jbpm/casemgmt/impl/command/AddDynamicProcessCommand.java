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
 * Adds subprocess (identified by processId) to selected ad hoc process instance with given parameters
 */
public class AddDynamicProcessCommand extends CaseCommand<Void> {
    private static final long serialVersionUID = 6345222909719335953L;

    private String caseId;

    private String processId;

    private long processInstanceId;

    private Map<String, Object> parameters;

    public AddDynamicProcessCommand(String caseId, Long processInstanceId, String processId, Map<String, Object> parameters) {
        this.caseId = caseId;
        this.processInstanceId = processInstanceId;
        this.processId = processId;
        this.parameters = parameters;
        if ((processInstanceId == null) || (processId == null)) {
            throw new IllegalArgumentException("Mandatory parameters are missing - process instance id and process id");
        }
    }

    @Override
    public Void execute(Context context) {
        KieSession ksession = ((KnowledgeCommandContext) (context)).getKieSession();
        ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
        if (processInstance == null) {
            throw new ProcessInstanceNotFoundException(("No process instance found with id " + (processInstanceId)));
        }
        CaseEventSupport caseEventSupport = getCaseEventSupport(context);
        // fire before String{caseId} to CaseEventSupport{caseEventSupport}
        caseEventSupport.fireBeforeDynamicProcessAdded(caseId, processInstanceId, processId, parameters);
        long subProcessInstanceId = DynamicUtils.addDynamicSubProcess(processInstance, ksession, processId, parameters);
        // fire after String{caseId} to CaseEventSupport{caseEventSupport}
        caseEventSupport.fireAfterDynamicProcessAdded(caseId, processInstanceId, processId, parameters, subProcessInstanceId);
        return null;
    }
}

