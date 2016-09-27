/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.executor.entities;

import java.util.ArrayList;
import java.util.Arrays;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import org.kie.api.executor.STATUS;
import javax.persistence.SequenceGenerator;
import java.io.Serializable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@SequenceGenerator(name = "requestInfoIdSeq", sequenceName = "REQUEST_INFO_ID_SEQ")
public class RequestInfo implements Serializable , org.kie.internal.executor.api.RequestInfo {
    private static final long serialVersionUID = 5823083735663566537L;

    @Id
    @GeneratedValue(generator = "requestInfoIdSeq", strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "timestamp")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date time;

    @Enumerated(value = EnumType.STRING)
    private STATUS status;

    private String commandName;

    private String message;

    // Business Key for callback
    @Column(name = "businessKey")
    private String key;

    // Number of times that this request must be retried
    private int retries = 0;

    // Number of times that this request has been executed
    private int executions = 0;

    private String deploymentId;

    // owning component of this request, meaning when set only same component can execute it
    private String owner;

    private int priority = 0;

    @Lob
    @Column(length = 2147483647)
    private byte[] requestData;

    @Lob
    @Column(length = 2147483647)
    private byte[] responseData;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "requestInfo")
    private List<ErrorInfo> errorInfo = new ArrayList<ErrorInfo>();

    public RequestInfo() {
    }

    public List<? extends ErrorInfo> getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(List<ErrorInfo> errorInfo) {
        RequestInfo.this.errorInfo = errorInfo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        RequestInfo.this.id = id;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        RequestInfo.this.retries = retries;
    }

    public int getExecutions() {
        return executions;
    }

    public void setExecutions(int executions) {
        RequestInfo.this.executions = executions;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        RequestInfo.this.commandName = commandName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        RequestInfo.this.key = key;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        RequestInfo.this.deploymentId = deploymentId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        RequestInfo.this.message = message;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        RequestInfo.this.status = status;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        RequestInfo.this.time = time;
    }

    public byte[] getRequestData() {
        return requestData;
    }

    public void setRequestData(byte[] requestData) {
        RequestInfo.this.requestData = requestData;
    }

    public byte[] getResponseData() {
        return responseData;
    }

    public void setResponseData(byte[] responseData) {
        RequestInfo.this.responseData = responseData;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        RequestInfo.this.owner = owner;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        RequestInfo.this.priority = priority;
    }

    @Override
    public String toString() {
        return (((((((((((((((((((("RequestInfo{" + "id=") + (id)) + ", time=") + (time)) + ", status=") + (status)) + ", commandName=") + (commandName)) + ", message=") + (message)) + ", owner=") + (owner)) + ", key=") + (key)) + ", requestData=") + (requestData)) + ", responseData=") + (responseData)) + ", error=") + (errorInfo)) + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } 
        if ((getClass()) != (obj.getClass())) {
            return false;
        } 
        final RequestInfo other = ((RequestInfo) (obj));
        if (((RequestInfo.this.id) != (other.id)) && (((RequestInfo.this.id) == null) || (!(RequestInfo.this.id.equals(other.id))))) {
            return false;
        } 
        if (((RequestInfo.this.time) != (other.time)) && (((RequestInfo.this.time) == null) || (!(RequestInfo.this.time.equals(other.time))))) {
            return false;
        } 
        if ((RequestInfo.this.status) != (other.status)) {
            return false;
        } 
        if ((RequestInfo.this.commandName) == null ? (other.commandName) != null : !(RequestInfo.this.commandName.equals(other.commandName))) {
            return false;
        } 
        if ((RequestInfo.this.message) == null ? (other.message) != null : !(RequestInfo.this.message.equals(other.message))) {
            return false;
        } 
        if ((RequestInfo.this.key) == null ? (other.key) != null : !(RequestInfo.this.key.equals(other.key))) {
            return false;
        } 
        if ((RequestInfo.this.owner) == null ? (other.owner) != null : !(RequestInfo.this.owner.equals(other.owner))) {
            return false;
        } 
        if ((RequestInfo.this.deploymentId) == null ? (other.deploymentId) != null : !(RequestInfo.this.deploymentId.equals(other.deploymentId))) {
            return false;
        } 
        if (!(Arrays.equals(RequestInfo.this.requestData, other.requestData))) {
            return false;
        } 
        if (!(Arrays.equals(RequestInfo.this.responseData, other.responseData))) {
            return false;
        } 
        if (((RequestInfo.this.errorInfo) != (other.errorInfo)) && (((RequestInfo.this.errorInfo) == null) || (!(RequestInfo.this.errorInfo.equals(other.errorInfo))))) {
            return false;
        } 
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = (79 * hash) + ((RequestInfo.this.id) != null ? RequestInfo.this.id.hashCode() : 0);
        hash = (79 * hash) + ((RequestInfo.this.time) != null ? RequestInfo.this.time.hashCode() : 0);
        hash = (79 * hash) + ((RequestInfo.this.status) != null ? RequestInfo.this.status.hashCode() : 0);
        hash = (79 * hash) + ((RequestInfo.this.commandName) != null ? RequestInfo.this.commandName.hashCode() : 0);
        hash = (79 * hash) + ((RequestInfo.this.message) != null ? RequestInfo.this.message.hashCode() : 0);
        hash = (79 * hash) + ((RequestInfo.this.key) != null ? RequestInfo.this.key.hashCode() : 0);
        hash = (79 * hash) + ((RequestInfo.this.owner) != null ? RequestInfo.this.owner.hashCode() : 0);
        hash = (79 * hash) + ((RequestInfo.this.deploymentId) != null ? RequestInfo.this.deploymentId.hashCode() : 0);
        hash = (79 * hash) + (Arrays.hashCode(RequestInfo.this.requestData));
        hash = (79 * hash) + (Arrays.hashCode(RequestInfo.this.responseData));
        hash = (79 * hash) + ((RequestInfo.this.errorInfo) != null ? RequestInfo.this.errorInfo.hashCode() : 0);
        return hash;
    }
}

