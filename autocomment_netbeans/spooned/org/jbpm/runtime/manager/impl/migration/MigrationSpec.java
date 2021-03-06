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


package org.jbpm.runtime.manager.impl.migration;

import java.io.Serializable;

public class MigrationSpec implements Serializable {
    static final long serialVersionUID = 1L;

    private String deploymentId;

    private Long processInstanceId;

    private String toProcessId;

    private String toDeploymentId;

    public MigrationSpec() {
    }

    /**
     * Creates new migration definition to be used to migrate single process instance
     * @param deploymentId source deployment id - one that process instance belongs to
     * @param processInstanceId actual process instance id -  must be active process instance
     * @param toDeploymentId target deployment id where process instance should be migrated to
     * @param toProcessId target process id within the target deployment id
     */
    public MigrationSpec(String deploymentId, Long processInstanceId, String toDeploymentId, String toProcessId) {
        MigrationSpec.this.deploymentId = deploymentId;
        MigrationSpec.this.processInstanceId = processInstanceId;
        MigrationSpec.this.toProcessId = toProcessId;
        MigrationSpec.this.toDeploymentId = toDeploymentId;
    }

    public String getDeploymentId() {
        return MigrationSpec.this.deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        MigrationSpec.this.deploymentId = deploymentId;
    }

    public Long getProcessInstanceId() {
        return MigrationSpec.this.processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        MigrationSpec.this.processInstanceId = processInstanceId;
    }

    public String getToProcessId() {
        return MigrationSpec.this.toProcessId;
    }

    public void setToProcessId(String toProcessId) {
        MigrationSpec.this.toProcessId = toProcessId;
    }

    public String getToDeploymentId() {
        return MigrationSpec.this.toDeploymentId;
    }

    public void setToDeploymentId(String toDeploymentId) {
        MigrationSpec.this.toDeploymentId = toDeploymentId;
    }

    @Override
    public String toString() {
        return ((((((("ProcessData [deploymentId=" + (deploymentId)) + ", processInstanceId=") + (processInstanceId)) + ", toProcessId=") + (toProcessId)) + ", toDeploymentId=") + (toDeploymentId)) + "]";
    }
}

