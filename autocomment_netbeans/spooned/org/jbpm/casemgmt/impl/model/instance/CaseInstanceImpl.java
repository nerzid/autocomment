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


package org.jbpm.casemgmt.impl.model.instance;

import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.api.model.instance.CaseInstance;
import org.jbpm.casemgmt.api.model.instance.CaseMilestoneInstance;
import org.jbpm.casemgmt.api.model.instance.CaseRoleInstance;
import org.jbpm.casemgmt.api.model.instance.CaseStageInstance;
import java.util.Collection;
import java.util.Date;
import java.io.Serializable;

public class CaseInstanceImpl implements Serializable , CaseInstance {
    private static final long serialVersionUID = 832035193857983082L;

    private String caseId;

    private String caseDescription;

    private Collection<CaseStageInstance> caseStages;

    private Collection<CaseMilestoneInstance> caseMilestones;

    private Collection<CaseRoleInstance> caseRoles;

    private CaseFileInstance caseFile;

    private String caseDefinitionId;

    private Integer status;

    private String deploymentId;

    private String owner;

    private Date startedAt;

    private Date completedAt;

    private Long processInstanceId;

    private String completionMessage;

    public CaseInstanceImpl() {
    }

    public CaseInstanceImpl(String caseId, String caseDescription, Collection<CaseStageInstance> caseStages, Collection<CaseMilestoneInstance> caseMilestones, Collection<CaseRoleInstance> caseRoles, CaseFileInstance caseFile) {
        CaseInstanceImpl.this.caseId = caseId;
        CaseInstanceImpl.this.caseDescription = caseDescription;
        CaseInstanceImpl.this.caseStages = caseStages;
        CaseInstanceImpl.this.caseMilestones = caseMilestones;
        CaseInstanceImpl.this.caseRoles = caseRoles;
        CaseInstanceImpl.this.caseFile = caseFile;
    }

    /**
     * Constructor to be used mainly by persistence provider to create instances automatically
     * @param caseId
     * @param caseDescription
     */
    public CaseInstanceImpl(String caseId, String caseDescription, String caseDefinitionId, Integer status, String deploymentId, String owner, Date startedAt, Date completedAt, Long processInstanceId, String completionMessage) {
        CaseInstanceImpl.this.caseId = caseId;
        CaseInstanceImpl.this.caseDescription = caseDescription;
        CaseInstanceImpl.this.caseDefinitionId = caseDefinitionId;
        CaseInstanceImpl.this.status = status;
        CaseInstanceImpl.this.deploymentId = deploymentId;
        CaseInstanceImpl.this.owner = owner;
        CaseInstanceImpl.this.startedAt = startedAt;
        CaseInstanceImpl.this.completedAt = completedAt;
        CaseInstanceImpl.this.processInstanceId = processInstanceId;
        CaseInstanceImpl.this.completionMessage = completionMessage;
    }

    @Override
    public String getCaseId() {
        return caseId;
    }

    @Override
    public String getCaseDescription() {
        return caseDescription;
    }

    @Override
    public Collection<CaseStageInstance> getCaseStages() {
        return caseStages;
    }

    @Override
    public Collection<CaseMilestoneInstance> getCaseMilestones() {
        return caseMilestones;
    }

    @Override
    public Collection<CaseRoleInstance> getCaseRoles() {
        return caseRoles;
    }

    @Override
    public CaseFileInstance getCaseFile() {
        return caseFile;
    }

    public String getCaseDefinitionId() {
        return caseDefinitionId;
    }

    public void setCaseDefinitionId(String caseDefinitionId) {
        CaseInstanceImpl.this.caseDefinitionId = caseDefinitionId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        CaseInstanceImpl.this.status = status;
    }

    @Override
    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        CaseInstanceImpl.this.deploymentId = deploymentId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        CaseInstanceImpl.this.owner = owner;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        CaseInstanceImpl.this.startedAt = startedAt;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        CaseInstanceImpl.this.processInstanceId = processInstanceId;
    }

    public void setCaseId(String caseId) {
        CaseInstanceImpl.this.caseId = caseId;
    }

    public void setCaseDescription(String caseDescription) {
        CaseInstanceImpl.this.caseDescription = caseDescription;
    }

    public void setCaseStages(Collection<CaseStageInstance> caseStages) {
        CaseInstanceImpl.this.caseStages = caseStages;
    }

    public void setCaseMilestones(Collection<CaseMilestoneInstance> caseMilestones) {
        CaseInstanceImpl.this.caseMilestones = caseMilestones;
    }

    public void setCaseRoles(Collection<CaseRoleInstance> caseRoles) {
        CaseInstanceImpl.this.caseRoles = caseRoles;
    }

    public void setCaseFile(CaseFileInstance caseFile) {
        CaseInstanceImpl.this.caseFile = caseFile;
    }

    public Date getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Date completedAt) {
        CaseInstanceImpl.this.completedAt = completedAt;
    }

    public String getCompletionMessage() {
        return completionMessage;
    }

    public void setCompletionMessage(String completionMessage) {
        CaseInstanceImpl.this.completionMessage = completionMessage;
    }

    @Override
    public String toString() {
        return ((((((((("CaseInstanceImpl [caseId=" + (caseId)) + ", status=") + (status)) + ", deploymentId=") + (deploymentId)) + ", owner=") + (owner)) + ", processInstanceId=") + (processInstanceId)) + "]";
    }
}

