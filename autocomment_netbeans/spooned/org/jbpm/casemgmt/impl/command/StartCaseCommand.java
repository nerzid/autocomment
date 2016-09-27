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
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.kie.internal.command.Context;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationKeyFactory;
import java.util.HashMap;
import org.kie.api.command.KieCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.jbpm.services.api.ProcessService;

public class StartCaseCommand extends CaseCommand<Void> {
    private static final long serialVersionUID = 6811181095390934146L;

    private static final Logger logger = LoggerFactory.getLogger(StartCaseCommand.class);

    private static CorrelationKeyFactory correlationKeyFactory = KieInternalServices.Factory.get().newCorrelationKeyFactory();

    private static KieCommands commandsFactory = KieServices.Factory.get().getCommands();

    private String caseId;

    private String deploymentId;

    private String caseDefinitionId;

    private CaseFileInstance caseFile;

    private transient ProcessService processService;

    public StartCaseCommand(String caseId, String deploymentId, String caseDefinitionId, CaseFileInstance caseFile, ProcessService processService) {
        StartCaseCommand.this.caseId = caseId;
        StartCaseCommand.this.deploymentId = deploymentId;
        StartCaseCommand.this.caseDefinitionId = caseDefinitionId;
        StartCaseCommand.this.caseFile = caseFile;
        StartCaseCommand.this.processService = processService;
    }

    @SuppressWarnings(value = "unchecked")
    @Override
    public Void execute(Context context) {
        CaseEventSupport caseEventSupport = getCaseEventSupport(context);
        caseEventSupport.fireBeforeCaseStarted(caseId, deploymentId, caseDefinitionId, caseFile);
        StartCaseCommand.logger.debug("Inserting case file into working memory");
        processService.execute(deploymentId, CaseContext.get(caseId), StartCaseCommand.commandsFactory.newInsert(caseFile));
        StartCaseCommand.logger.debug("Starting process instance for case {} and case definition {}", caseId, caseDefinitionId);
        CorrelationKey correlationKey = StartCaseCommand.correlationKeyFactory.newCorrelationKey(caseId);
        Map<String, Object> params = new HashMap<>();
        // set case id to allow it to use CaseContext when creating runtime engine
        params.put(EnvironmentName.CASE_ID, caseId);
        long processInstanceId = processService.startProcess(deploymentId, caseDefinitionId, correlationKey, params);
        StartCaseCommand.logger.debug("Case {} successfully started (process instance id {})", caseId, processInstanceId);
        caseEventSupport.fireAfterCaseStarted(caseId, deploymentId, caseDefinitionId, caseFile, processInstanceId);
        return null;
    }

    public void setProcessService(ProcessService processService) {
        StartCaseCommand.this.processService = processService;
    }
}

