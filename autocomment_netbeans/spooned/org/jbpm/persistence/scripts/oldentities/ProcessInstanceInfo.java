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


package org.jbpm.persistence.scripts.oldentities;

import java.util.Arrays;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import java.util.Date;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.util.HashSet;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import java.util.Set;
import javax.persistence.Version;

@Entity
@SequenceGenerator(name = "processInstanceInfoIdSeq", sequenceName = "PROCESS_INSTANCE_INFO_ID_SEQ")
public class ProcessInstanceInfo {
    @Id
    @GeneratedValue(generator = "processInstanceInfoIdSeq", strategy = GenerationType.AUTO)
    @Column(name = "InstanceId")
    private Long processInstanceId;

    @Version
    @Column(name = "OPTLOCK")
    private int version;

    private String processId;

    private Date startDate;

    private Date lastReadDate;

    private Date lastModificationDate;

    private int state;

    @Lob
    @Column(length = 2147483647)
    byte[] processInstanceByteArray;

    @ElementCollection
    @CollectionTable(joinColumns = @JoinColumn(name = "InstanceId")
    , name = "EventTypes")
    @Column(name = "element")
    private Set<String> eventTypes = new HashSet<String>();

    public ProcessInstanceInfo() {
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        ProcessInstanceInfo.this.processInstanceId = processInstanceId;
    }

    public Long getId() {
        return processInstanceId;
    }

    public String getProcessId() {
        return processId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getLastModificationDate() {
        return lastModificationDate;
    }

    public Date getLastReadDate() {
        return lastReadDate;
    }

    public int getState() {
        return state;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } 
        if ((getClass()) != (obj.getClass())) {
            return false;
        } 
        final ProcessInstanceInfo other = ((ProcessInstanceInfo) (obj));
        if (((ProcessInstanceInfo.this.processInstanceId) != (other.processInstanceId)) && (((ProcessInstanceInfo.this.processInstanceId) == null) || (!(ProcessInstanceInfo.this.processInstanceId.equals(other.processInstanceId))))) {
            return false;
        } 
        if ((ProcessInstanceInfo.this.version) != (other.version)) {
            return false;
        } 
        if ((ProcessInstanceInfo.this.processId) == null ? (other.processId) != null : !(ProcessInstanceInfo.this.processId.equals(other.processId))) {
            return false;
        } 
        if (((ProcessInstanceInfo.this.startDate) != (other.startDate)) && (((ProcessInstanceInfo.this.startDate) == null) || (!(ProcessInstanceInfo.this.startDate.equals(other.startDate))))) {
            return false;
        } 
        if (((ProcessInstanceInfo.this.lastReadDate) != (other.lastReadDate)) && (((ProcessInstanceInfo.this.lastReadDate) == null) || (!(ProcessInstanceInfo.this.lastReadDate.equals(other.lastReadDate))))) {
            return false;
        } 
        if (((ProcessInstanceInfo.this.lastModificationDate) != (other.lastModificationDate)) && (((ProcessInstanceInfo.this.lastModificationDate) == null) || (!(ProcessInstanceInfo.this.lastModificationDate.equals(other.lastModificationDate))))) {
            return false;
        } 
        if ((ProcessInstanceInfo.this.state) != (other.state)) {
            return false;
        } 
        if (!(Arrays.equals(ProcessInstanceInfo.this.processInstanceByteArray, other.processInstanceByteArray))) {
            return false;
        } 
        if (((ProcessInstanceInfo.this.eventTypes) != (other.eventTypes)) && (((ProcessInstanceInfo.this.eventTypes) == null) || (!(ProcessInstanceInfo.this.eventTypes.equals(other.eventTypes))))) {
            return false;
        } 
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = (61 * hash) + ((ProcessInstanceInfo.this.processInstanceId) != null ? ProcessInstanceInfo.this.processInstanceId.hashCode() : 0);
        hash = (61 * hash) + (ProcessInstanceInfo.this.version);
        hash = (61 * hash) + ((ProcessInstanceInfo.this.processId) != null ? ProcessInstanceInfo.this.processId.hashCode() : 0);
        hash = (61 * hash) + ((ProcessInstanceInfo.this.startDate) != null ? ProcessInstanceInfo.this.startDate.hashCode() : 0);
        hash = (61 * hash) + ((ProcessInstanceInfo.this.lastReadDate) != null ? ProcessInstanceInfo.this.lastReadDate.hashCode() : 0);
        hash = (61 * hash) + ((ProcessInstanceInfo.this.lastModificationDate) != null ? ProcessInstanceInfo.this.lastModificationDate.hashCode() : 0);
        hash = (61 * hash) + (ProcessInstanceInfo.this.state);
        hash = (61 * hash) + (Arrays.hashCode(ProcessInstanceInfo.this.processInstanceByteArray));
        hash = (61 * hash) + ((ProcessInstanceInfo.this.eventTypes) != null ? ProcessInstanceInfo.this.eventTypes.hashCode() : 0);
        return hash;
    }

    public int getVersion() {
        return version;
    }

    public Set<String> getEventTypes() {
        return eventTypes;
    }

    public byte[] getProcessInstanceByteArray() {
        return processInstanceByteArray;
    }

    public void setVersion(int version) {
        ProcessInstanceInfo.this.version = version;
    }

    public void setProcessId(String processId) {
        ProcessInstanceInfo.this.processId = processId;
    }

    public void setStartDate(Date startDate) {
        ProcessInstanceInfo.this.startDate = startDate;
    }

    public void setLastReadDate(Date lastReadDate) {
        ProcessInstanceInfo.this.lastReadDate = lastReadDate;
    }

    public void setLastModificationDate(Date lastModificationDate) {
        ProcessInstanceInfo.this.lastModificationDate = lastModificationDate;
    }

    public void setState(int state) {
        ProcessInstanceInfo.this.state = state;
    }

    public void setProcessInstanceByteArray(byte[] processInstanceByteArray) {
        ProcessInstanceInfo.this.processInstanceByteArray = processInstanceByteArray;
    }

    public void setEventTypes(Set<String> eventTypes) {
        ProcessInstanceInfo.this.eventTypes = eventTypes;
    }
}

