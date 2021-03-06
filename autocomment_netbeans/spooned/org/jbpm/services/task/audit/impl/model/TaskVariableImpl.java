/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import javax.persistence.Column;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.persistence.SequenceGenerator;
import java.io.Serializable;
import org.kie.internal.task.api.TaskVariable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@SequenceGenerator(allocationSize = 1, name = "taskVarIdSeq", sequenceName = "TASK_VAR_ID_SEQ")
public class TaskVariableImpl implements Serializable , TaskVariable {
    private static final long serialVersionUID = 5388016330549830048L;

    private static final Logger logger = LoggerFactory.getLogger(TaskVariableImpl.class);

    @Transient
    private final int VARIABLE_LOG_LENGTH = Integer.parseInt(System.getProperty("org.jbpm.task.var.log.length", "4000"));

    @Id
    @GeneratedValue(generator = "taskVarIdSeq", strategy = GenerationType.AUTO)
    private Long id;

    private Long taskId;

    private Long processInstanceId;

    private String processId;

    private String name;

    @Column(length = 4000)
    private String value;

    @Enumerated(value = EnumType.ORDINAL)
    private VariableType type;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date modificationDate;

    public Long getId() {
        return id;
    }

    @Override
    public Long getTaskId() {
        return taskId;
    }

    @Override
    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    @Override
    public String getProcessId() {
        return processId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public Date getModificationDate() {
        return modificationDate;
    }

    public void setId(Long id) {
        TaskVariableImpl.this.id = id;
    }

    public void setTaskId(Long taskId) {
        TaskVariableImpl.this.taskId = taskId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        TaskVariableImpl.this.processInstanceId = processInstanceId;
    }

    public void setProcessId(String processId) {
        TaskVariableImpl.this.processId = processId;
    }

    public void setName(String name) {
        TaskVariableImpl.this.name = name;
    }

    public void setValue(String value) {
        if ((value != null) && ((value.length()) > (VARIABLE_LOG_LENGTH))) {
            value = value.substring(0, VARIABLE_LOG_LENGTH);
            TaskVariableImpl.logger.warn("Task variable '{}' content was trimmed as it was too long (more than {} characters)", name, VARIABLE_LOG_LENGTH);
        } 
        TaskVariableImpl.this.value = value;
    }

    public void setModificationDate(Date modificationDate) {
        TaskVariableImpl.this.modificationDate = modificationDate;
    }

    public VariableType getType() {
        return type;
    }

    public void setType(VariableType type) {
        TaskVariableImpl.this.type = type;
    }

    @Override
    public String toString() {
        return ((((((((((("TaskVariableImpl [taskId=" + (taskId)) + ", name='") + (name)) + "', value='") + (value)) + "', type=") + (type)) + " (processInstanceId=") + (processInstanceId)) + ", processId=") + (processId)) + ")]";
    }
}

