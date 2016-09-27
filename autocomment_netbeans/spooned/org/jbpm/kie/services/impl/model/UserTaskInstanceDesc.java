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
import java.io.Serializable;

public class UserTaskInstanceDesc implements Serializable , org.jbpm.services.api.model.UserTaskInstanceDesc {
    private static final long serialVersionUID = -4594921035584546643L;

    private Long taskId;

    private String status;

    private Date activationTime;

    private String name;

    private String description;

    private Integer priority;

    private String actualOwner;

    private String createdBy;

    private String deploymentId;

    private String processId;

    private Long processInstanceId;

    private Date createdOn;

    private Date dueDate;

    public UserTaskInstanceDesc(Long taskId, String status, Date activationTime, String name, String description, Integer priority, String actualOwner, String createdBy, String deploymentId, String processId, Long processInstanceId, Date createdOn, Date dueDate) {
        super();
        UserTaskInstanceDesc.this.taskId = taskId;
        UserTaskInstanceDesc.this.status = status;
        UserTaskInstanceDesc.this.activationTime = activationTime;
        UserTaskInstanceDesc.this.name = name;
        UserTaskInstanceDesc.this.description = description;
        UserTaskInstanceDesc.this.priority = priority;
        UserTaskInstanceDesc.this.actualOwner = actualOwner;
        UserTaskInstanceDesc.this.createdBy = createdBy;
        UserTaskInstanceDesc.this.deploymentId = deploymentId;
        UserTaskInstanceDesc.this.processId = processId;
        UserTaskInstanceDesc.this.processInstanceId = processInstanceId;
        UserTaskInstanceDesc.this.createdOn = createdOn;
        UserTaskInstanceDesc.this.dueDate = dueDate;
    }

    @Override
    public Long getTaskId() {
        return UserTaskInstanceDesc.this.taskId;
    }

    @Override
    public String getStatus() {
        return UserTaskInstanceDesc.this.status;
    }

    @Override
    public Date getActivationTime() {
        return UserTaskInstanceDesc.this.activationTime;
    }

    @Override
    public String getName() {
        return UserTaskInstanceDesc.this.name;
    }

    @Override
    public String getDescription() {
        return UserTaskInstanceDesc.this.description;
    }

    @Override
    public Integer getPriority() {
        return UserTaskInstanceDesc.this.priority;
    }

    @Override
    public String getCreatedBy() {
        return UserTaskInstanceDesc.this.createdBy;
    }

    @Override
    public Date getCreatedOn() {
        return UserTaskInstanceDesc.this.createdOn;
    }

    @Override
    public Date getDueDate() {
        return UserTaskInstanceDesc.this.dueDate;
    }

    @Override
    public Long getProcessInstanceId() {
        return UserTaskInstanceDesc.this.processInstanceId;
    }

    @Override
    public String getProcessId() {
        return UserTaskInstanceDesc.this.processId;
    }

    @Override
    public String getActualOwner() {
        return UserTaskInstanceDesc.this.actualOwner;
    }

    @Override
    public String getDeploymentId() {
        return UserTaskInstanceDesc.this.deploymentId;
    }

    @Override
    public String toString() {
        return ((((((("UserTaskInstanceDesc [taskId=" + (taskId)) + ", name=") + (name)) + ", deploymentId=") + (deploymentId)) + ", processInstanceId=") + (processInstanceId)) + "]";
    }
}

