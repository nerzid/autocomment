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


package org.jbpm.services.task.admin.listener.internal;

import java.util.ArrayList;
import org.kie.internal.command.Context;
import java.util.List;
import org.kie.internal.command.ProcessInstanceIdCommand;
import java.util.Set;
import org.jbpm.services.task.commands.TaskCommand;
import org.kie.api.task.model.TaskSummary;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

@XmlRootElement(name = "get-current-tx-tasks-command")
@XmlAccessorType(value = XmlAccessType.NONE)
public class GetCurrentTxTasksCommand extends TaskCommand<List<TaskSummary>> implements ProcessInstanceIdCommand {
    /**
     * * Generated serial version UID
     */
    private static final long serialVersionUID = 6474368266134150938L;

    @XmlElement(required = true)
    @XmlSchemaType(name = "long")
    private Long processInstanceId;

    public GetCurrentTxTasksCommand() {
        // default constructor
    }

    public GetCurrentTxTasksCommand(Long processInstanceId) {
        GetCurrentTxTasksCommand.this.processInstanceId = processInstanceId;
    }

    @Override
    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    @Override
    public void setProcessInstanceId(Long processInstanceId) {
        GetCurrentTxTasksCommand.this.processInstanceId = processInstanceId;
    }

    @SuppressWarnings(value = "unchecked")
    @Override
    public List<TaskSummary> execute(Context context) {
        List<TaskSummary> tasks = new ArrayList<TaskSummary>();
        Set<TaskSummary> tasksToRemove = ((Set<TaskSummary>) (context.get("local:current-tasks")));
        if (tasksToRemove != null) {
            for (TaskSummary task : tasksToRemove) {
                if ((task.getProcessInstanceId()) == (processInstanceId)) {
                    tasks.add(task);
                } 
            }
        } 
        return tasks;
    }
}

