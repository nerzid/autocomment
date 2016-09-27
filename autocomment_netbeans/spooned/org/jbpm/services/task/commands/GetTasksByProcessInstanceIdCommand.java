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


package org.jbpm.services.task.commands;

import org.kie.internal.command.Context;
import java.util.List;
import org.kie.internal.command.ProcessInstanceIdCommand;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

@XmlRootElement(name = "get-tasks-by-process-instance-id-command")
@XmlAccessorType(value = XmlAccessType.NONE)
public class GetTasksByProcessInstanceIdCommand extends TaskCommand<List<Long>> implements ProcessInstanceIdCommand {
    private static final long serialVersionUID = -2328845811017055632L;

    @XmlElement(name = "process-instance-id")
    @XmlSchemaType(name = "long")
    private Long processInstanceId;

    public GetTasksByProcessInstanceIdCommand() {
    }

    public GetTasksByProcessInstanceIdCommand(Long processInstanceId) {
        GetTasksByProcessInstanceIdCommand.this.processInstanceId = processInstanceId;
    }

    @Override
    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    @Override
    public void setProcessInstanceId(Long processInstanceId) {
        GetTasksByProcessInstanceIdCommand.this.processInstanceId = processInstanceId;
    }

    public List<Long> execute(Context cntxt) {
        TaskContext context = ((TaskContext) (cntxt));
        return context.getTaskQueryService().getTasksByProcessInstanceId(processInstanceId);
    }
}

