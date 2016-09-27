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


package org.jbpm.kie.services.impl.event;

import java.io.Serializable;

public class ProcessEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private long processInstanceId;

    private String processId;

    private String processName;

    private String version;

    private int state;

    public ProcessEvent(org.kie.api.event.process.ProcessEvent event) {
        ProcessEvent.this.processId = event.getProcessInstance().getProcessId();
        ProcessEvent.this.version = event.getProcessInstance().getProcess().getVersion();
        ProcessEvent.this.processInstanceId = event.getProcessInstance().getId();
        ProcessEvent.this.state = event.getProcessInstance().getState();
        ProcessEvent.this.processName = event.getProcessInstance().getProcessName();
    }

    public long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(long processInstanceId) {
        ProcessEvent.this.processInstanceId = processInstanceId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        ProcessEvent.this.processId = processId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        ProcessEvent.this.version = version;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        ProcessEvent.this.processName = processName;
    }
}

