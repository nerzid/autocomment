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


package org.jbpm.casemgmt.api.event;

import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;

/**
 * Represents occurrence of start case operation
 */
public class CaseStartEvent extends CaseEvent {
    private String deploymentId;

    private String caseDefinitionId;

    private CaseFileInstance caseFile;

    private Long processInstanceId;

    public CaseStartEvent(String caseId, String deploymentId, String caseDefinitionId, CaseFileInstance caseFile) {
        super(caseId);
        CaseStartEvent.this.deploymentId = deploymentId;
        CaseStartEvent.this.caseDefinitionId = caseDefinitionId;
        CaseStartEvent.this.caseFile = caseFile;
    }

    public CaseStartEvent(String caseId, String deploymentId, String caseDefinitionId, CaseFileInstance caseFile, Long processInstanceId) {
        super(caseId);
        CaseStartEvent.this.deploymentId = deploymentId;
        CaseStartEvent.this.caseDefinitionId = caseDefinitionId;
        CaseStartEvent.this.caseFile = caseFile;
        CaseStartEvent.this.processInstanceId = processInstanceId;
    }

    /**
     * Returns deployment id that case belongs to
     */
    public String getDeploymentId() {
        return deploymentId;
    }

    /**
     * Returns case definition id
     */
    public String getCaseDefinitionId() {
        return caseDefinitionId;
    }

    /**
     * Returns case file associated with the case
     */
    public CaseFileInstance getCaseFile() {
        return caseFile;
    }

    /**
     * Returns process instance id of the case process instance
     */
    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    @Override
    public String toString() {
        return ((((("StartCaseEvent [deploymentId=" + (deploymentId)) + ", caseDefinitionId=") + (caseDefinitionId)) + ", caseId=") + (getCaseId())) + "]";
    }
}

