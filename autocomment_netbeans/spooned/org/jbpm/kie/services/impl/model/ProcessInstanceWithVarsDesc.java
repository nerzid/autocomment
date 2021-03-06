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


package org.jbpm.kie.services.impl.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public class ProcessInstanceWithVarsDesc extends ProcessInstanceDesc implements Serializable , org.jbpm.services.api.model.ProcessInstanceWithVarsDesc {
    private static final long serialVersionUID = 7310019271033570922L;

    private Map<String, Object> variables;

    public ProcessInstanceWithVarsDesc() {
        super();
    }

    public ProcessInstanceWithVarsDesc(long id, String processId, String processName, String processVersion, int state, String deploymentId, Date dataTimeStamp, String initiator, String processInstanceDescription, String correlationKey, Long parentId) {
        super(id, processId, processName, processVersion, state, deploymentId, dataTimeStamp, initiator, processInstanceDescription, correlationKey, parentId);
    }

    public ProcessInstanceWithVarsDesc(long id, String processId, String processName, String processVersion, int state, String deploymentId, Date dataTimeStamp, String initiator, String processInstanceDescription, String correlationKey) {
        super(id, processId, processName, processVersion, state, deploymentId, dataTimeStamp, initiator, processInstanceDescription, correlationKey);
    }

    public ProcessInstanceWithVarsDesc(long id, String processId, String processName, String processVersion, int state, String deploymentId, Date dataTimeStamp, String initiator, String correlationKey) {
        super(id, processId, processName, processVersion, state, deploymentId, dataTimeStamp, initiator, correlationKey);
    }

    @Override
    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        ProcessInstanceWithVarsDesc.this.variables = variables;
    }

    public void addVariable(String variable, Object variableValue) {
        if ((ProcessInstanceWithVarsDesc.this.variables) == null) {
            ProcessInstanceWithVarsDesc.this.variables = new HashMap<String, Object>();
        } 
        ProcessInstanceWithVarsDesc.this.variables.put(variable, variableValue);
    }
}

