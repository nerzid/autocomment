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


package org.jbpm.casemgmt.impl.model;

import org.jbpm.casemgmt.api.model.AdHocFragment;
import org.jbpm.casemgmt.api.model.CaseDefinition;
import org.jbpm.casemgmt.api.model.CaseMilestone;
import org.jbpm.casemgmt.api.model.CaseRole;
import org.jbpm.casemgmt.api.model.CaseStage;
import java.util.Collection;
import org.jbpm.kie.services.impl.model.ProcessAssetDesc;
import java.io.Serializable;

public class CaseDefinitionImpl implements Serializable , CaseDefinition {
    private static final long serialVersionUID = -9012849633311384541L;

    private ProcessAssetDesc processDef;

    private String identifierPrefix = DEFAULT_PREFIX;

    private Collection<CaseStage> caseStages;

    private Collection<CaseMilestone> caseMilestones;

    private Collection<CaseRole> caseRoles;

    private Collection<AdHocFragment> adHocFragments;

    public CaseDefinitionImpl() {
    }

    public CaseDefinitionImpl(ProcessAssetDesc processDef, String identifierPrefix, Collection<CaseStage> caseStages, Collection<CaseMilestone> caseMilestones, Collection<CaseRole> caseRoles, Collection<AdHocFragment> adHocFragments) {
        CaseDefinitionImpl.this.processDef = processDef;
        CaseDefinitionImpl.this.identifierPrefix = identifierPrefix;
        CaseDefinitionImpl.this.caseStages = caseStages;
        CaseDefinitionImpl.this.caseMilestones = caseMilestones;
        CaseDefinitionImpl.this.caseRoles = caseRoles;
        CaseDefinitionImpl.this.adHocFragments = adHocFragments;
    }

    @Override
    public String getIdentifierPrefix() {
        return identifierPrefix;
    }

    @Override
    public Collection<CaseStage> getCaseStages() {
        return caseStages;
    }

    @Override
    public Collection<CaseMilestone> getCaseMilestones() {
        return caseMilestones;
    }

    @Override
    public Collection<CaseRole> getCaseRoles() {
        return caseRoles;
    }

    @Override
    public String getDeploymentId() {
        return processDef.getDeploymentId();
    }

    public boolean isActive() {
        return processDef.isActive();
    }

    @Override
    public String getName() {
        return processDef.getName();
    }

    @Override
    public String getId() {
        return processDef.getId();
    }

    @Override
    public String getVersion() {
        return processDef.getVersion();
    }

    @Override
    public Collection<AdHocFragment> getAdHocFragments() {
        return adHocFragments;
    }

    @Override
    public String toString() {
        return ((((((("CaseDefinitionImpl [identifierPrefix=" + (identifierPrefix)) + ", caseStages=") + (caseStages)) + ", caseMilestones=") + (caseMilestones)) + ", caseRoles=") + (caseRoles)) + "]";
    }
}

