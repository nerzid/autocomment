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


package org.jbpm.runtime.manager.impl;

import org.jbpm.process.audit.event.AuditEvent;
import org.jbpm.process.audit.event.DefaultAuditEventBuilderImpl;
import org.jbpm.process.audit.NodeInstanceLog;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.jbpm.process.audit.VariableInstanceLog;

public class ManagedAuditEventBuilderImpl extends DefaultAuditEventBuilderImpl {
    private String ownerId;

    @Override
    public AuditEvent buildEvent(ProcessStartedEvent pse) {
        ProcessInstanceLog log = ((ProcessInstanceLog) (super.buildEvent(pse)));
        // set external String{ownerId} to ProcessInstanceLog{log}
        log.setExternalId(ownerId);
        return log;
    }

    @Override
    public AuditEvent buildEvent(ProcessCompletedEvent pce, Object log) {
        ProcessInstanceLog instanceLog = ((ProcessInstanceLog) (super.buildEvent(pce, log)));
        // set external String{ownerId} to ProcessInstanceLog{instanceLog}
        instanceLog.setExternalId(ownerId);
        return instanceLog;
    }

    @Override
    public AuditEvent buildEvent(ProcessNodeTriggeredEvent pnte) {
        NodeInstanceLog nodeInstanceLog = ((NodeInstanceLog) (super.buildEvent(pnte)));
        // set external String{ownerId} to NodeInstanceLog{nodeInstanceLog}
        nodeInstanceLog.setExternalId(ownerId);
        return nodeInstanceLog;
    }

    @Override
    public AuditEvent buildEvent(ProcessNodeLeftEvent pnle, Object log) {
        NodeInstanceLog nodeInstanceLog = ((NodeInstanceLog) (super.buildEvent(pnle, log)));
        // set external String{ownerId} to NodeInstanceLog{nodeInstanceLog}
        nodeInstanceLog.setExternalId(ownerId);
        return nodeInstanceLog;
    }

    @Override
    public AuditEvent buildEvent(ProcessVariableChangedEvent pvce) {
        VariableInstanceLog variableLog = ((VariableInstanceLog) (super.buildEvent(pvce)));
        // set external String{ownerId} to VariableInstanceLog{variableLog}
        variableLog.setExternalId(ownerId);
        return variableLog;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}

