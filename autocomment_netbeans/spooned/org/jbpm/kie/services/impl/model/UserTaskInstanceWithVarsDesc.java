/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

public class UserTaskInstanceWithVarsDesc extends UserTaskInstanceDesc implements Serializable , org.jbpm.services.api.model.UserTaskInstanceWithVarsDesc {
    private static final long serialVersionUID = -4594921035584546643L;

    private Map<String, Object> variables;

    public UserTaskInstanceWithVarsDesc(Long taskId, String status, Date activationTime, String name, String description, Integer priority, String actualOwner, String createdBy, String deploymentId, String processId, Long processInstanceId, Date createdOn, Date dueDate) {
        super(taskId, status, activationTime, name, description, priority, actualOwner, createdBy, deploymentId, processId, processInstanceId, createdOn, dueDate);
    }

    @Override
    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        UserTaskInstanceWithVarsDesc.this.variables = variables;
    }

    public void addVariable(String variable, Object variableValue) {
        if ((UserTaskInstanceWithVarsDesc.this.variables) == null) {
            UserTaskInstanceWithVarsDesc.this.variables = new HashMap<String, Object>();
        } 
        UserTaskInstanceWithVarsDesc.this.variables.put(variable, variableValue);
    }
}

