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

import javax.persistence.Column;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import java.io.Serializable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@SequenceGenerator(name = "errorInfoIdSeq", sequenceName = "ERROR_INFO_ID_SEQ")
public class ErrorInfo implements Serializable , org.kie.internal.executor.api.ErrorInfo {
    private static final Logger logger = LoggerFactory.getLogger(ErrorInfo.class);

    private static final long serialVersionUID = 1548071325967795108L;

    @Transient
    private final int MESSAGE_LOG_LENGTH = Integer.parseInt(System.getProperty("org.kie.executor.msg.length", "255"));

    @Transient
    private final int STACKTRACE_LOG_LENGTH = Integer.parseInt(System.getProperty("org.kie.executor.stacktrace.length", "5000"));

    @Id
    @GeneratedValue(generator = "errorInfoIdSeq", strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "timestamp")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date time;

    private String message;

    @Column(length = 5000)
    private String stacktrace;

    @ManyToOne
    @JoinColumn(name = "REQUEST_ID", nullable = false)
    private RequestInfo requestInfo;

    public ErrorInfo() {
    }

    public ErrorInfo(String message, String stacktrace) {
        ErrorInfo.this.message = message;
        ErrorInfo.this.stacktrace = stacktrace;
        ErrorInfo.this.time = new Date();
        trimToSize();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        ErrorInfo.this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        ErrorInfo.this.message = message;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(String stacktrace) {
        ErrorInfo.this.stacktrace = stacktrace;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        ErrorInfo.this.time = time;
    }

    public RequestInfo getRequestInfo() {
        return requestInfo;
    }

    public void setRequestInfo(RequestInfo requestInfo) {
        ErrorInfo.this.requestInfo = requestInfo;
    }

    @Override
    public String toString() {
        return (((((((((("ErrorInfo{" + "id=") + (id)) + ", time=") + (time)) + ", message=") + (message)) + ", stacktrace=") + (stacktrace)) + ", requestInfo=") + (requestInfo.getId())) + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } 
        if ((getClass()) != (obj.getClass())) {
            return false;
        } 
        final ErrorInfo other = ((ErrorInfo) (obj));
        if (((ErrorInfo.this.id) != (other.id)) && (((ErrorInfo.this.id) == null) || (!(ErrorInfo.this.id.equals(other.id))))) {
            return false;
        } 
        if (((ErrorInfo.this.time) != (other.time)) && (((ErrorInfo.this.time) == null) || (!(ErrorInfo.this.time.equals(other.time))))) {
            return false;
        } 
        if ((ErrorInfo.this.message) == null ? (other.message) != null : !(ErrorInfo.this.message.equals(other.message))) {
            return false;
        } 
        if ((ErrorInfo.this.stacktrace) == null ? (other.stacktrace) != null : !(ErrorInfo.this.stacktrace.equals(other.stacktrace))) {
            return false;
        } 
        if (((ErrorInfo.this.requestInfo) != (other.requestInfo)) && (((ErrorInfo.this.requestInfo) == null) || (!(ErrorInfo.this.requestInfo.equals(other.requestInfo))))) {
            return false;
        } 
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = (37 * hash) + ((ErrorInfo.this.id) != null ? ErrorInfo.this.id.hashCode() : 0);
        hash = (37 * hash) + ((ErrorInfo.this.time) != null ? ErrorInfo.this.time.hashCode() : 0);
        hash = (37 * hash) + ((ErrorInfo.this.message) != null ? ErrorInfo.this.message.hashCode() : 0);
        hash = (37 * hash) + ((ErrorInfo.this.stacktrace) != null ? ErrorInfo.this.stacktrace.hashCode() : 0);
        hash = (37 * hash) + ((ErrorInfo.this.requestInfo) != null ? ErrorInfo.this.requestInfo.hashCode() : 0);
        return hash;
    }

    protected void trimToSize() {
        if (((ErrorInfo.this.message) != null) && ((ErrorInfo.this.message.length()) > (MESSAGE_LOG_LENGTH))) {
            ErrorInfo.logger.warn("trimming message as it's too long : {}", ErrorInfo.this.message.length());
            ErrorInfo.this.message = message.substring(0, MESSAGE_LOG_LENGTH);
        } 
        if (((ErrorInfo.this.stacktrace) != null) && ((ErrorInfo.this.stacktrace.length()) > (STACKTRACE_LOG_LENGTH))) {
            ErrorInfo.logger.warn("trimming stacktrace as it's too long : {}", ErrorInfo.this.stacktrace.length());
            ErrorInfo.this.stacktrace = stacktrace.substring(0, STACKTRACE_LOG_LENGTH);
        } 
    }
}

