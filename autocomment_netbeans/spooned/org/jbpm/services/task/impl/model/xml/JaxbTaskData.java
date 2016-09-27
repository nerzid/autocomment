/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.jbpm.services.task.impl.model.xml;

import org.kie.internal.task.api.model.AccessType;
import java.util.ArrayList;
import org.kie.api.task.model.Attachment;
import java.util.Collections;
import org.kie.api.task.model.Comment;
import java.util.Date;
import java.util.HashMap;
import org.kie.internal.task.api.model.InternalTaskData;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import java.util.List;
import java.util.Map;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskData;
import org.kie.api.task.model.User;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "task-data")
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlSeeAlso(value = { JaxbComment.class , JaxbAttachment.class })
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class JaxbTaskData extends AbstractJaxbTaskObject<TaskData> implements TaskData {
    @XmlElement
    private Status status;

    @XmlElement
    private Status previousStatus;

    @XmlElement(name = "actual-owner")
    private String actualOwner;

    @XmlElement(name = "created-by")
    private String createdBy;

    @XmlElement(name = "created-on")
    @XmlSchemaType(name = "dateTime")
    private Date createdOn;

    @XmlElement(name = "activation-time")
    @XmlSchemaType(name = "dateTime")
    private Date activationTime;

    @XmlElement(name = "expiration-time")
    @XmlSchemaType(name = "dateTime")
    private Date expirationTime;

    @XmlElement
    @XmlSchemaType(name = "boolean")
    private Boolean skipable;

    @XmlElement(name = "work-item-id")
    @XmlSchemaType(name = "long")
    private Long workItemId;

    @XmlElement(name = "process-instance-id")
    @XmlSchemaType(name = "long")
    private Long processInstanceId;

    @XmlElement(name = "document-type")
    @XmlSchemaType(name = "string")
    private String documentType;

    @XmlElement(name = "document-access-type")
    private AccessType documentAccessType;

    @XmlElement(name = "document-content-id")
    @XmlSchemaType(name = "long")
    private Long documentContentId;

    @XmlElement(name = "output-type")
    @XmlSchemaType(name = "string")
    private String outputType;

    @XmlElement(name = "output-access-type")
    private AccessType outputAccessType;

    @XmlElement(name = "output-content-id")
    @XmlSchemaType(name = "long")
    private Long outputContentId;

    @XmlElement(name = "fault-name")
    @XmlSchemaType(name = "string")
    private String faultName;

    @XmlElement(name = "fault-access-type")
    private AccessType faultAccessType;

    @XmlElement(name = "fault-type")
    @XmlSchemaType(name = "string")
    private String faultType;

    @XmlElement(name = "fault-content-id")
    @XmlSchemaType(name = "long")
    private Long faultContentId;

    @XmlElement(name = "parent-id")
    @XmlSchemaType(name = "long")
    private Long parentId;

    @XmlElement(name = "process-id")
    @XmlSchemaType(name = "string")
    private String processId;

    @XmlElement(name = "process-session-id")
    @XmlSchemaType(name = "long")
    private Long processSessionId;

    @XmlElement
    private List<JaxbComment> comments;

    @XmlElement
    private List<JaxbAttachment> attachments;

    @XmlElement(name = "deployment-id")
    @XmlSchemaType(name = "string")
    private String deploymentId;

    public JaxbTaskData() {
        super(TaskData.class);
    }

    public JaxbTaskData(TaskData taskData) {
        super(TaskData.class);
        JaxbTaskData.this.status = taskData.getStatus();
        JaxbTaskData.this.previousStatus = taskData.getPreviousStatus();
        User actualOwnerUser = taskData.getActualOwner();
        if (actualOwnerUser != null) {
            JaxbTaskData.this.actualOwner = actualOwnerUser.getId();
        } 
        User createdByUser = taskData.getCreatedBy();
        if (createdByUser != null) {
            JaxbTaskData.this.createdBy = createdByUser.getId();
        } 
        JaxbTaskData.this.createdOn = taskData.getCreatedOn();
        JaxbTaskData.this.activationTime = taskData.getActivationTime();
        JaxbTaskData.this.expirationTime = taskData.getExpirationTime();
        JaxbTaskData.this.skipable = taskData.isSkipable();
        JaxbTaskData.this.workItemId = taskData.getWorkItemId();
        JaxbTaskData.this.processInstanceId = taskData.getProcessInstanceId();
        JaxbTaskData.this.documentType = taskData.getDocumentType();
        if (taskData instanceof JaxbTaskData) {
            JaxbTaskData jaxbTaskData = ((JaxbTaskData) (taskData));
            JaxbTaskData.this.documentAccessType = jaxbTaskData.getDocumentAccessType();
            JaxbTaskData.this.outputAccessType = jaxbTaskData.getOutputAccessType();
            JaxbTaskData.this.faultAccessType = jaxbTaskData.getFaultAccessType();
        } else if (taskData instanceof InternalTaskData) {
            InternalTaskData internalTaskData = ((InternalTaskData) (taskData));
            JaxbTaskData.this.documentAccessType = internalTaskData.getDocumentAccessType();
            JaxbTaskData.this.outputAccessType = internalTaskData.getOutputAccessType();
            JaxbTaskData.this.faultAccessType = internalTaskData.getFaultAccessType();
        } 
        JaxbTaskData.this.documentContentId = taskData.getDocumentContentId();
        JaxbTaskData.this.outputType = taskData.getOutputType();
        JaxbTaskData.this.outputContentId = taskData.getOutputContentId();
        JaxbTaskData.this.faultName = taskData.getFaultName();
        JaxbTaskData.this.faultType = taskData.getFaultType();
        JaxbTaskData.this.faultContentId = taskData.getFaultContentId();
        JaxbTaskData.this.parentId = taskData.getParentId();
        JaxbTaskData.this.processId = taskData.getProcessId();
        JaxbTaskData.this.processSessionId = taskData.getProcessSessionId();
        if ((taskData.getComments()) != null) {
            List<JaxbComment> commentList = new ArrayList<JaxbComment>();
            for (Object comment : taskData.getComments()) {
                commentList.add(new JaxbComment(((Comment) (comment))));
            }
            JaxbTaskData.this.comments = commentList;
        } 
        List<JaxbAttachment> attachList = new ArrayList<JaxbAttachment>();
        for (Object attach : taskData.getAttachments()) {
            attachList.add(new JaxbAttachment(((Attachment) (attach))));
        }
        JaxbTaskData.this.attachments = attachList;
        JaxbTaskData.this.deploymentId = taskData.getDeploymentId();
    }

    @Override
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        JaxbTaskData.this.status = status;
    }

    @Override
    public Status getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(Status previousStatus) {
        JaxbTaskData.this.previousStatus = previousStatus;
    }

    @Override
    public User getActualOwner() {
        return new org.jbpm.services.task.impl.model.xml.InternalJaxbWrapper.GetterUser(actualOwner);
    }

    public String getActualOwnerId() {
        return actualOwner;
    }

    public void setActualOwnerId(String actualOwnerId) {
        JaxbTaskData.this.actualOwner = actualOwnerId;
    }

    @Override
    public User getCreatedBy() {
        return new org.jbpm.services.task.impl.model.xml.InternalJaxbWrapper.GetterUser(createdBy);
    }

    public String getCreatedById() {
        return createdBy;
    }

    public void setCreatedById(String createdById) {
        JaxbTaskData.this.createdBy = createdById;
    }

    @Override
    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        JaxbTaskData.this.createdOn = createdOn;
    }

    @Override
    public Date getActivationTime() {
        return activationTime;
    }

    public void setActivationTime(Date activationTime) {
        JaxbTaskData.this.activationTime = activationTime;
    }

    @Override
    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        JaxbTaskData.this.expirationTime = expirationTime;
    }

    @Override
    public boolean isSkipable() {
        return skipable;
    }

    public void setSkipable(Boolean skipable) {
        JaxbTaskData.this.skipable = skipable;
    }

    @Override
    public long getWorkItemId() {
        return whenNull(workItemId, (-1L));
    }

    public void setWorkItemId(Long workItemId) {
        JaxbTaskData.this.workItemId = workItemId;
    }

    @Override
    public long getProcessInstanceId() {
        return whenNull(processInstanceId, (-1L));
    }

    public void setProcessInstanceId(Long processInstanceId) {
        JaxbTaskData.this.processInstanceId = processInstanceId;
    }

    @Override
    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        JaxbTaskData.this.documentType = documentType;
    }

    public AccessType getDocumentAccessType() {
        return documentAccessType;
    }

    public void setDocumentAccessType(AccessType documentAccessType) {
        JaxbTaskData.this.documentAccessType = documentAccessType;
    }

    @Override
    public long getDocumentContentId() {
        return whenNull(documentContentId, (-1L));
    }

    public void setDocumentContentId(Long documentContentId) {
        JaxbTaskData.this.documentContentId = documentContentId;
    }

    @Override
    public String getOutputType() {
        return outputType;
    }

    public void setOutputType(String outputType) {
        JaxbTaskData.this.outputType = outputType;
    }

    public AccessType getOutputAccessType() {
        return outputAccessType;
    }

    public void setOutputAccessType(AccessType outputAccessType) {
        JaxbTaskData.this.outputAccessType = outputAccessType;
    }

    @Override
    public Long getOutputContentId() {
        return whenNull(outputContentId, (-1L));
    }

    public void setOutputContentId(Long outputContentId) {
        JaxbTaskData.this.outputContentId = outputContentId;
    }

    @Override
    public String getFaultName() {
        return faultName;
    }

    public void setFaultName(String faultName) {
        JaxbTaskData.this.faultName = faultName;
    }

    public AccessType getFaultAccessType() {
        return faultAccessType;
    }

    public void setFaultAccessType(AccessType faultAccessType) {
        JaxbTaskData.this.faultAccessType = faultAccessType;
    }

    @Override
    public String getFaultType() {
        return faultType;
    }

    public void setFaultType(String faultType) {
        JaxbTaskData.this.faultType = faultType;
    }

    @Override
    public long getFaultContentId() {
        return whenNull(faultContentId, (-1L));
    }

    public void setFaultContentId(Long faultContentId) {
        JaxbTaskData.this.faultContentId = faultContentId;
    }

    @Override
    public long getParentId() {
        return whenNull(parentId, (-1L));
    }

    public void setParentId(Long parentId) {
        JaxbTaskData.this.parentId = parentId;
    }

    @Override
    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        JaxbTaskData.this.processId = processId;
    }

    @Override
    public long getProcessSessionId() {
        return whenNull(processSessionId, (-1L));
    }

    public void setProcessSessionId(Long processSessionId) {
        JaxbTaskData.this.processSessionId = processSessionId;
    }

    @Override
    public List<Comment> getComments() {
        List<Comment> commentList = new ArrayList<Comment>();
        if ((comments) != null) {
            for (JaxbComment jaxbComment : comments) {
                commentList.add(jaxbComment);
            }
        } 
        return Collections.unmodifiableList(commentList);
    }

    public void setJaxbComments(List<JaxbComment> comments) {
        JaxbTaskData.this.comments = comments;
    }

    @Override
    public List<Attachment> getAttachments() {
        List<Attachment> attachmentList = new ArrayList<Attachment>();
        if ((attachments) != null) {
            for (JaxbAttachment jaxbAttachment : attachments) {
                attachmentList.add(jaxbAttachment);
            }
        } 
        return Collections.unmodifiableList(attachmentList);
    }

    @Override
    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        JaxbTaskData.this.deploymentId = deploymentId;
    }

    @Override
    public Map<String, Object> getTaskInputVariables() {
        return new HashMap<String, Object>();
    }

    @Override
    public Map<String, Object> getTaskOutputVariables() {
        return new HashMap<String, Object>();
    }
}

