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
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

@XmlRootElement(name = "get-tasks-by-status-by-process-instance-id-command")
@XmlAccessorType(value = XmlAccessType.NONE)
public class GetTasksByStatusByProcessInstanceIdCommand extends TaskCommand<List<TaskSummary>> implements ProcessInstanceIdCommand {
    private static final long serialVersionUID = -6059681013108594344L;

    @XmlElement(name = "process-instance-id")
    @XmlSchemaType(name = "long")
    private Long processInstanceId;

    @XmlElement
    @XmlSchemaType(name = "string")
    private String taskName;

    @XmlElement
    private List<Status> statuses;

    public GetTasksByStatusByProcessInstanceIdCommand() {
    }

    public GetTasksByStatusByProcessInstanceIdCommand(long processInstanceId, List<Status> status) {
        GetTasksByStatusByProcessInstanceIdCommand.this.processInstanceId = processInstanceId;
        GetTasksByStatusByProcessInstanceIdCommand.this.statuses = status;
    }

    public GetTasksByStatusByProcessInstanceIdCommand(long processInstanceId, List<Status> status, String taskName) {
        GetTasksByStatusByProcessInstanceIdCommand.this.processInstanceId = processInstanceId;
        GetTasksByStatusByProcessInstanceIdCommand.this.statuses = status;
        GetTasksByStatusByProcessInstanceIdCommand.this.taskName = taskName;
    }

    @Override
    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    @Override
    public void setProcessInstanceId(Long processInstanceId) {
        GetTasksByStatusByProcessInstanceIdCommand.this.processInstanceId = processInstanceId;
    }

    public List<Status> getStatus() {
        return statuses;
    }

    public void setStatus(List<Status> status) {
        GetTasksByStatusByProcessInstanceIdCommand.this.statuses = status;
    }

    public List<TaskSummary> execute(Context cntxt) {
        TaskContext context = ((TaskContext) (cntxt));
        if ((taskName) != null) {
            return context.getTaskQueryService().getTasksByStatusByProcessInstanceIdByTaskName(processInstanceId, statuses, taskName);
        } else {
            return context.getTaskQueryService().getTasksByStatusByProcessInstanceId(processInstanceId, statuses);
        }
    }
}

