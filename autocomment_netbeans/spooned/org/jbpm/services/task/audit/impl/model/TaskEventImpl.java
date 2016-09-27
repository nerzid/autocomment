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
/**
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package org.jbpm.services.task.audit.impl.model;

import javax.persistence.Column;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Externalizable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.IOException;
import javax.persistence.Id;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.kie.internal.task.api.model.TaskEvent;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

/**
 */
@Entity
@Table(name = "TaskEvent")
@SequenceGenerator(name = "taskEventIdSeq", sequenceName = "TASK_EVENT_ID_SEQ")
public class TaskEventImpl implements Externalizable , TaskEvent {
    @Id
    @GeneratedValue(generator = "taskEventIdSeq", strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "OPTLOCK")
    private Integer version;

    private Long taskId;

    private Long workItemId;

    @Enumerated(value = EnumType.STRING)
    private TaskEventType type;

    private Long processInstanceId;

    private String userId;

    private String message;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date logTime;

    public TaskEventImpl() {
    }

    public TaskEventImpl(long taskId, TaskEventType type, String userId) {
        TaskEventImpl.this.taskId = taskId;
        TaskEventImpl.this.type = type;
        TaskEventImpl.this.userId = userId;
        TaskEventImpl.this.logTime = new Date();
    }

    public TaskEventImpl(Long taskId, TaskEventType type, String userId, Date logTime) {
        TaskEventImpl.this.taskId = taskId;
        TaskEventImpl.this.type = type;
        TaskEventImpl.this.userId = userId;
        TaskEventImpl.this.logTime = logTime;
    }

    public TaskEventImpl(Long taskId, TaskEventType type, Long processInstanceId, Long workItemId, String userId, Date logTime) {
        TaskEventImpl.this.taskId = taskId;
        TaskEventImpl.this.type = type;
        TaskEventImpl.this.processInstanceId = processInstanceId;
        TaskEventImpl.this.workItemId = workItemId;
        TaskEventImpl.this.userId = userId;
        TaskEventImpl.this.logTime = logTime;
    }

    public TaskEventImpl(Long taskId, TaskEventType type, Long processInstanceId, Long workItemId, String userId) {
        this(taskId, type, processInstanceId, workItemId, userId, new Date());
    }

    public TaskEventImpl(Long taskId, TaskEventType type, Long processInstanceId, Long workItemId, String userId, String message) {
        this(taskId, type, processInstanceId, workItemId, userId, new Date());
        TaskEventImpl.this.message = message;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public long getTaskId() {
        return taskId;
    }

    @Override
    public TaskEventType getType() {
        return type;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public Date getLogTime() {
        return logTime;
    }

    @Override
    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public Long getWorkItemId() {
        return workItemId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        TaskEventImpl.this.message = message;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        id = in.readLong();
        processInstanceId = in.readLong();
        taskId = in.readLong();
        type = TaskEventType.valueOf(in.readUTF());
        message = in.readUTF();
        userId = in.readUTF();
        workItemId = in.readLong();
        if (in.readBoolean()) {
            logTime = new Date(in.readLong());
        } 
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(id);
        out.writeLong(processInstanceId);
        out.writeLong(taskId);
        if ((type) != null) {
            out.writeUTF(type.name());
        } else {
            out.writeUTF("");
        }
        if ((message) != null) {
            out.writeUTF(message);
        } else {
            out.writeUTF("");
        }
        if ((userId) != null) {
            out.writeUTF(userId);
        } else {
            out.writeUTF("");
        }
        out.writeLong(workItemId);
        if ((logTime) != null) {
            out.writeBoolean(true);
            out.writeLong(logTime.getTime());
        } else {
            out.writeBoolean(false);
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = (97 * hash) + ((TaskEventImpl.this.id) != null ? TaskEventImpl.this.id.hashCode() : 0);
        hash = (97 * hash) + ((TaskEventImpl.this.version) != null ? TaskEventImpl.this.version.hashCode() : 0);
        hash = (97 * hash) + ((TaskEventImpl.this.taskId) != null ? TaskEventImpl.this.taskId.hashCode() : 0);
        hash = (97 * hash) + ((TaskEventImpl.this.workItemId) != null ? TaskEventImpl.this.workItemId.hashCode() : 0);
        hash = (97 * hash) + ((TaskEventImpl.this.type) != null ? TaskEventImpl.this.type.hashCode() : 0);
        hash = (97 * hash) + ((TaskEventImpl.this.message) != null ? TaskEventImpl.this.message.hashCode() : 0);
        hash = (97 * hash) + ((TaskEventImpl.this.processInstanceId) != null ? TaskEventImpl.this.processInstanceId.hashCode() : 0);
        hash = (97 * hash) + ((TaskEventImpl.this.userId) != null ? TaskEventImpl.this.userId.hashCode() : 0);
        hash = (97 * hash) + ((TaskEventImpl.this.logTime) != null ? TaskEventImpl.this.logTime.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } 
        if ((getClass()) != (obj.getClass())) {
            return false;
        } 
        final TaskEventImpl other = ((TaskEventImpl) (obj));
        if (((TaskEventImpl.this.id) != (other.id)) && (((TaskEventImpl.this.id) == null) || (!(TaskEventImpl.this.id.equals(other.id))))) {
            return false;
        } 
        if (((TaskEventImpl.this.version) != (other.version)) && (((TaskEventImpl.this.version) == null) || (!(TaskEventImpl.this.version.equals(other.version))))) {
            return false;
        } 
        if (((TaskEventImpl.this.taskId) != (other.taskId)) && (((TaskEventImpl.this.taskId) == null) || (!(TaskEventImpl.this.taskId.equals(other.taskId))))) {
            return false;
        } 
        if (((TaskEventImpl.this.workItemId) != (other.workItemId)) && (((TaskEventImpl.this.workItemId) == null) || (!(TaskEventImpl.this.workItemId.equals(other.workItemId))))) {
            return false;
        } 
        if ((TaskEventImpl.this.type) != (other.type)) {
            return false;
        } 
        if (!(TaskEventImpl.this.message.equals(other.message))) {
            return false;
        } 
        if (((TaskEventImpl.this.processInstanceId) != (other.processInstanceId)) && (((TaskEventImpl.this.processInstanceId) == null) || (!(TaskEventImpl.this.processInstanceId.equals(other.processInstanceId))))) {
            return false;
        } 
        if ((TaskEventImpl.this.userId) == null ? (other.userId) != null : !(TaskEventImpl.this.userId.equals(other.userId))) {
            return false;
        } 
        if (((TaskEventImpl.this.logTime) != (other.logTime)) && (((TaskEventImpl.this.logTime) == null) || (!(TaskEventImpl.this.logTime.equals(other.logTime))))) {
            return false;
        } 
        return true;
    }
}

