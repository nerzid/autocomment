/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.process.audit.command;

import org.jbpm.process.audit.AuditLogService;
import org.kie.internal.command.Context;
import org.kie.internal.command.ProcessInstanceIdCommand;
import org.jbpm.process.audit.ProcessInstanceLog;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

@XmlRootElement
@XmlAccessorType(value = XmlAccessType.NONE)
public class FindProcessInstanceCommand extends AuditCommand<ProcessInstanceLog> implements ProcessInstanceIdCommand {
    /**
     * * generated serial version UID
     */
    private static final long serialVersionUID = -7548733507155126870L;

    @XmlAttribute(name = "process-instance-id", required = true)
    @XmlSchemaType(name = "long")
    private Long processInstanceId;

    public FindProcessInstanceCommand() {
        // no-arg for JAXB
    }

    public FindProcessInstanceCommand(long processInstanceId) {
        FindProcessInstanceCommand.this.processInstanceId = processInstanceId;
    }

    public ProcessInstanceLog execute(Context cntxt) {
        setLogEnvironment(cntxt);
        return FindProcessInstanceCommand.this.auditLogService.findProcessInstance(processInstanceId);
    }

    @Override
    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    @Override
    public void setProcessInstanceId(Long processInstanceId) {
        FindProcessInstanceCommand.this.processInstanceId = processInstanceId;
    }

    public String toString() {
        return (((AuditLogService.class.getSimpleName()) + ".findProcessInstance(") + (processInstanceId)) + ")";
    }
}

