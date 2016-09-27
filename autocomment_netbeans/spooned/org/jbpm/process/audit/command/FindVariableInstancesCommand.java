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
import java.util.List;
import org.kie.internal.command.ProcessInstanceIdCommand;
import org.jbpm.process.audit.VariableInstanceLog;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

@XmlRootElement
@XmlAccessorType(value = XmlAccessType.NONE)
public class FindVariableInstancesCommand extends AuditCommand<List<VariableInstanceLog>> implements ProcessInstanceIdCommand {
    /**
     * * generated serial version UID
     */
    private static final long serialVersionUID = 7087452375594067164L;

    @XmlAttribute(name = "process-instance-id", required = true)
    @XmlSchemaType(name = "long")
    private Long processInstanceId;

    @XmlAttribute(required = true)
    @XmlSchemaType(name = "string")
    private String variableId;

    public FindVariableInstancesCommand() {
        // no-arg for JAXB
    }

    public FindVariableInstancesCommand(long processInstanceId) {
        FindVariableInstancesCommand.this.processInstanceId = processInstanceId;
        FindVariableInstancesCommand.this.variableId = null;
    }

    public FindVariableInstancesCommand(long processInstanceId, String variableId) {
        FindVariableInstancesCommand.this.processInstanceId = processInstanceId;
        FindVariableInstancesCommand.this.variableId = variableId;
        if ((variableId == null) || (variableId.isEmpty())) {
            throw new IllegalArgumentException("The variableId field must not be null or empty.");
        } 
    }

    public List<VariableInstanceLog> execute(Context cntxt) {
        setLogEnvironment(cntxt);
        if (((variableId) == null) || (variableId.isEmpty())) {
            return FindVariableInstancesCommand.this.auditLogService.findVariableInstances(processInstanceId);
        } else {
            return FindVariableInstancesCommand.this.auditLogService.findVariableInstances(processInstanceId, variableId);
        }
    }

    @Override
    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    @Override
    public void setProcessInstanceId(Long processInstanceId) {
        FindVariableInstancesCommand.this.processInstanceId = processInstanceId;
    }

    public String getVariableId() {
        return variableId;
    }

    public void setVariableId(String variableId) {
        FindVariableInstancesCommand.this.variableId = variableId;
    }

    public String toString() {
        if (((variableId) == null) || (variableId.isEmpty())) {
            return (((AuditLogService.class.getSimpleName()) + ".findVariableInstances(") + (processInstanceId)) + ")";
        } else {
            return (((((AuditLogService.class.getSimpleName()) + ".findVariableInstances(") + (processInstanceId)) + ", ") + (variableId)) + ")";
        }
    }
}

