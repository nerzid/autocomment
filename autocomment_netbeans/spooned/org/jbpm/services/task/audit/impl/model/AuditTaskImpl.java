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


package org.jbpm.services.task.audit.impl.model;

import org.kie.internal.task.api.AuditTask;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.io.Serializable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author salaboy
 */
@Entity
@SequenceGenerator(allocationSize = 1, name = "auditIdSeq", sequenceName = "AUDIT_ID_SEQ")
public class AuditTaskImpl implements Serializable , AuditTask {
    private static final long serialVersionUID = 5388016330549830043L;

    @Id
    @GeneratedValue(generator = "auditIdSeq", strategy = GenerationType.AUTO)
    private Long id;

    private Long taskId;

    private String status;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date activationTime;

    private String name;

    private String description;

    private int priority;

    private String createdBy;

    private String actualOwner;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date createdOn;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date dueDate;

    private long processInstanceId;

    private String processId;

    private long processSessionId;

    private long parentId;

    private String deploymentId;

    private Long workItemId;

    public AuditTaskImpl() {
    }

    public AuditTaskImpl(long taskId, String name, String status, Date activationTime, String actualOwner, String description, int priority, String createdBy, Date createdOn, Date dueDate, long processInstanceId, String processId, long processSessionId, String deploymentId, long parentId, long workItemId) {
        AuditTaskImpl.this.taskId = taskId;
        AuditTaskImpl.this.status = status;
        AuditTaskImpl.this.activationTime = activationTime;
        AuditTaskImpl.this.name = name;
        AuditTaskImpl.this.description = description;
        AuditTaskImpl.this.priority = priority;
        AuditTaskImpl.this.createdBy = createdBy;
        AuditTaskImpl.this.createdOn = createdOn;
        AuditTaskImpl.this.actualOwner = actualOwner;
        AuditTaskImpl.this.dueDate = dueDate;
        AuditTaskImpl.this.processInstanceId = processInstanceId;
        AuditTaskImpl.this.processId = processId;
        AuditTaskImpl.this.processSessionId = processSessionId;
        AuditTaskImpl.this.deploymentId = deploymentId;
        AuditTaskImpl.this.parentId = parentId;
        AuditTaskImpl.this.workItemId = workItemId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        AuditTaskImpl.this.id = id;
    }

    @Override
    public long getTaskId() {
        return taskId;
    }

    @Override
    public void setTaskId(long taskId) {
        AuditTaskImpl.this.taskId = taskId;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        AuditTaskImpl.this.status = status;
    }

    @Override
    public Date getActivationTime() {
        return activationTime;
    }

    @Override
    public void setActivationTime(Date activationTime) {
        AuditTaskImpl.this.activationTime = activationTime;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        AuditTaskImpl.this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        AuditTaskImpl.this.description = description;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(int priority) {
        AuditTaskImpl.this.priority = priority;
    }

    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreatedBy(String createdBy) {
        AuditTaskImpl.this.createdBy = createdBy;
    }

    @Override
    public Date getCreatedOn() {
        return createdOn;
    }

    @Override
    public void setCreatedOn(Date createdOn) {
        AuditTaskImpl.this.createdOn = createdOn;
    }

    @Override
    public Date getDueDate() {
        return dueDate;
    }

    @Override
    public void setDueDate(Date dueDate) {
        AuditTaskImpl.this.dueDate = dueDate;
    }

    @Override
    public long getProcessInstanceId() {
        return processInstanceId;
    }

    @Override
    public void setProcessInstanceId(long processInstanceId) {
        AuditTaskImpl.this.processInstanceId = processInstanceId;
    }

    @Override
    public String getProcessId() {
        return processId;
    }

    @Override
    public void setProcessId(String processId) {
        AuditTaskImpl.this.processId = processId;
    }

    @Override
    public long getProcessSessionId() {
        return processSessionId;
    }

    @Override
    public void setProcessSessionId(long processSessionId) {
        AuditTaskImpl.this.processSessionId = processSessionId;
    }

    @Override
    public long getParentId() {
        return parentId;
    }

    @Override
    public void setParentId(long parentId) {
        AuditTaskImpl.this.parentId = parentId;
    }

    public String getActualOwner() {
        return actualOwner;
    }

    public void setActualOwner(String actualOwner) {
        AuditTaskImpl.this.actualOwner = actualOwner;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        AuditTaskImpl.this.deploymentId = deploymentId;
    }

    @Override
    public long getWorkItemId() {
        return workItemId;
    }

    @Override
    public void setWorkItemId(long workItemId) {
        AuditTaskImpl.this.workItemId = workItemId;
    }
}

