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

import org.kie.internal.runtime.manager.context.CaseContext;
import org.jbpm.casemgmt.impl.event.CaseEventSupport;
import java.util.Collection;
import java.util.stream.Collectors;
import org.kie.internal.command.Context;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationKeyFactory;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jbpm.runtime.manager.impl.PerCaseRuntimeManager;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.kie.api.runtime.manager.RuntimeManager;

public class CancelCaseCommand extends CaseCommand<Void> {
    private static final long serialVersionUID = 6811181095390934149L;

    private static final Logger logger = LoggerFactory.getLogger(CancelCaseCommand.class);

    private static CorrelationKeyFactory correlationKeyFactory = KieInternalServices.Factory.get().newCorrelationKeyFactory();

    private String caseId;

    private transient ProcessService processService;

    private transient RuntimeDataService runtimeDataService;

    private boolean destroy;

    public CancelCaseCommand(String caseId, ProcessService processService, RuntimeDataService runtimeDataService, boolean destroy) {
        CancelCaseCommand.this.caseId = caseId;
        CancelCaseCommand.this.processService = processService;
        CancelCaseCommand.this.runtimeDataService = runtimeDataService;
        CancelCaseCommand.this.destroy = destroy;
    }

    @Override
    public Void execute(Context context) {
        CorrelationKey correlationKey = CancelCaseCommand.correlationKeyFactory.newCorrelationKey(caseId);
        Collection<ProcessInstanceDesc> caseProcesses = runtimeDataService.getProcessInstancesByCorrelationKey(correlationKey, new org.kie.api.runtime.query.QueryContext(0, 1000));
        if (caseProcesses.isEmpty()) {
            throw new org.jbpm.casemgmt.api.CaseNotFoundException((("Case with id " + (caseId)) + " was not found"));
        } 
        List<Long> processInstanceIds = caseProcesses.stream().filter(( pi) -> pi.getState().equals(ProcessInstance.STATE_ACTIVE)).map(( pi) -> pi.getId()).collect(Collectors.toList());
        CaseEventSupport caseEventSupport = getCaseEventSupport(context);
        caseEventSupport.fireBeforeCaseCancelled(caseId, processInstanceIds);
        CancelCaseCommand.logger.debug("Case {} consists of following process instances (ids) {}", caseId, processInstanceIds);
        processService.abortProcessInstances(processInstanceIds);
        caseEventSupport.fireAfterCaseCancelled(caseId, processInstanceIds);
        if (destroy) {
            RuntimeManager runtimeManager = getRuntimeManager(context);
            if (runtimeManager instanceof PerCaseRuntimeManager) {
                caseEventSupport.fireBeforeCaseDestroyed(caseId, processInstanceIds);
                CancelCaseCommand.logger.debug("Case {} aborted, destroying case data including per case runtime engine (including working memory)", caseId);
                ((PerCaseRuntimeManager) (runtimeManager)).destroyCase(CaseContext.get(caseId));
                caseEventSupport.fireAfterCaseDestroyed(caseId, processInstanceIds);
            } 
        } 
        return null;
    }

    public void setProcessService(ProcessService processService) {
        CancelCaseCommand.this.processService = processService;
    }
}

