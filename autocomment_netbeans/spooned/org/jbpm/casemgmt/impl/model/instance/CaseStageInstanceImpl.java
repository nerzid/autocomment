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

import org.jbpm.casemgmt.api.model.AdHocFragment;
import org.jbpm.casemgmt.api.model.instance.CaseStageInstance;
import java.util.Collection;
import java.util.Collections;
import org.jbpm.services.api.model.NodeInstanceDesc;
import java.io.Serializable;
import org.jbpm.casemgmt.api.model.instance.StageStatus;

public class CaseStageInstanceImpl implements Serializable , CaseStageInstance {
    private static final long serialVersionUID = -2162687165597961845L;

    private String id;

    private String name;

    private Collection<AdHocFragment> adHocFragments;

    private Collection<NodeInstanceDesc> activeNodes;

    private StageStatus status;

    public CaseStageInstanceImpl(String id, String name) {
        CaseStageInstanceImpl.this.id = id;
        CaseStageInstanceImpl.this.name = name;
        CaseStageInstanceImpl.this.adHocFragments = Collections.emptyList();
        CaseStageInstanceImpl.this.activeNodes = Collections.emptyList();
        CaseStageInstanceImpl.this.status = StageStatus.Active;
    }

    public CaseStageInstanceImpl(String id, String name, Collection<AdHocFragment> adHocFragments, Collection<NodeInstanceDesc> activeNodes) {
        CaseStageInstanceImpl.this.id = id;
        CaseStageInstanceImpl.this.name = name;
        CaseStageInstanceImpl.this.adHocFragments = adHocFragments;
        CaseStageInstanceImpl.this.activeNodes = activeNodes;
        CaseStageInstanceImpl.this.status = StageStatus.Active;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Collection<AdHocFragment> getAdHocFragments() {
        return adHocFragments;
    }

    @Override
    public Collection<NodeInstanceDesc> getActiveNodes() {
        return activeNodes;
    }

    @Override
    public StageStatus getStatus() {
        return status;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return ((((("CaseStageInstanceImpl [id=" + (id)) + ", name=") + (name)) + ", status=") + (status)) + "]";
    }
}

