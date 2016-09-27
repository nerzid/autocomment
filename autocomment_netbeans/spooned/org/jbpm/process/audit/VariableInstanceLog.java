/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.process.audit;

import org.jbpm.process.audit.event.AuditEvent;
import javax.persistence.Column;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.persistence.SequenceGenerator;
import java.io.Serializable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@SequenceGenerator(allocationSize = 1, name = "variableInstanceLogIdSeq", sequenceName = "VAR_INST_LOG_ID_SEQ")
public class VariableInstanceLog implements Serializable , AuditEvent , org.kie.api.runtime.manager.audit.VariableInstanceLog {
    private static final Logger logger = LoggerFactory.getLogger(VariableInstanceLog.class);

    private static final long serialVersionUID = 510L;

    @Transient
    private final int VARIABLE_LOG_LENGTH = Integer.parseInt(System.getProperty("org.jbpm.var.log.length", "255"));

    // entity fields
    @Id
    @GeneratedValue(generator = "variableInstanceLogIdSeq", strategy = GenerationType.AUTO)
    private long id;

    private long processInstanceId;

    private String processId;

    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "log_date")
    private Date date;

    private String variableInstanceId;

    private String variableId;

    private String value;

    private String oldValue;

    private String externalId;

    // constructors
    public VariableInstanceLog() {
    }

    public VariableInstanceLog(long processInstanceId, String processId, String variableInstanceId, String variableId, String value, String oldValue) {
        VariableInstanceLog.this.processInstanceId = processInstanceId;
        VariableInstanceLog.this.processId = processId;
        VariableInstanceLog.this.variableInstanceId = variableInstanceId;
        VariableInstanceLog.this.variableId = variableId;
        setValue(value);
        setOldValue(oldValue);
        VariableInstanceLog.this.date = new Date();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        VariableInstanceLog.this.id = id;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(long processInstanceId) {
        VariableInstanceLog.this.processInstanceId = processInstanceId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        VariableInstanceLog.this.processId = processId;
    }

    public String getVariableInstanceId() {
        return variableInstanceId;
    }

    public void setVariableInstanceId(String variableInstanceId) {
        VariableInstanceLog.this.variableInstanceId = variableInstanceId;
    }

    public String getVariableId() {
        return variableId;
    }

    public void setVariableId(String variableId) {
        VariableInstanceLog.this.variableId = variableId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if ((value != null) && ((value.length()) > (VARIABLE_LOG_LENGTH))) {
            value = value.substring(0, VARIABLE_LOG_LENGTH);
            VariableInstanceLog.logger.warn("Variable content was trimmed as it was too long (more than {} characters)", VARIABLE_LOG_LENGTH);
        } 
        VariableInstanceLog.this.value = value;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        if ((oldValue != null) && ((oldValue.length()) > (VARIABLE_LOG_LENGTH))) {
            oldValue = oldValue.substring(0, VARIABLE_LOG_LENGTH);
            VariableInstanceLog.logger.warn("Variable content was trimmed as it was too long (more than {} characters)", VARIABLE_LOG_LENGTH);
        } 
        VariableInstanceLog.this.oldValue = oldValue;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        VariableInstanceLog.this.date = date;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String domainId) {
        VariableInstanceLog.this.externalId = domainId;
    }

    public String toString() {
        return ((((((((("Change variable '" + (processId)) + "#") + (variableId)) + "' to '") + (value)) + "' [") + (processInstanceId)) + "#") + (variableInstanceId)) + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((date) == null ? 0 : date.hashCode());
        result = (prime * result) + ((int) (id));
        result = (prime * result) + ((processId) == null ? 0 : processId.hashCode());
        result = (prime * result) + ((int) (processInstanceId));
        result = (prime * result) + ((value) == null ? 0 : value.hashCode());
        result = (prime * result) + ((oldValue) == null ? 0 : oldValue.hashCode());
        result = (prime * result) + ((variableId) == null ? 0 : variableId.hashCode());
        result = (prime * result) + ((variableInstanceId) == null ? 0 : variableInstanceId.hashCode());
        result = (prime * result) + ((externalId) == null ? 0 : externalId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ((VariableInstanceLog.this) == obj)
            return true;
        
        if (obj == null)
            return false;
        
        if ((getClass()) != (obj.getClass()))
            return false;
        
        VariableInstanceLog other = ((VariableInstanceLog) (obj));
        if ((date) == null) {
            if ((other.date) != null)
                return false;
            
        } else if (!(date.equals(other.date)))
            return false;
        
        if ((id) != (other.id))
            return false;
        
        if ((processId) == null) {
            if ((other.processId) != null)
                return false;
            
        } else if (!(processId.equals(other.processId)))
            return false;
        
        if ((processInstanceId) != (other.processInstanceId))
            return false;
        
        if ((value) == null) {
            if ((other.value) != null)
                return false;
            
        } else if (!(value.equals(other.value)))
            return false;
        
        if ((oldValue) == null) {
            if ((other.oldValue) != null)
                return false;
            
        } else if (!(oldValue.equals(other.oldValue)))
            return false;
        
        if ((variableId) == null) {
            if ((other.variableId) != null)
                return false;
            
        } else if (!(variableId.equals(other.variableId)))
            return false;
        
        if ((variableInstanceId) == null) {
            if ((other.variableInstanceId) != null)
                return false;
            
        } else if (!(variableInstanceId.equals(other.variableInstanceId)))
            return false;
        
        if ((externalId) == null) {
            if ((other.externalId) != null)
                return false;
            
        } else if (!(externalId.equals(other.externalId)))
            return false;
        
        return true;
    }
}
