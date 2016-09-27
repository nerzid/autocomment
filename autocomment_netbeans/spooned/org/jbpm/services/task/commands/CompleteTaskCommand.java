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


package org.jbpm.services.task.commands;

import org.kie.internal.command.Context;
import org.kie.internal.task.api.model.InternalTaskData;
import org.drools.core.xml.jaxb.util.JaxbMapAdapter;
import java.util.Map;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.TaskInstanceService;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Operation.Start : [ new OperationCommand().{ status = [ Status.Ready ],
 * allowed = [ Allowed.PotentialOwner ], setNewOwnerToUser = true, newStatus =
 * Status.InProgress }, new OperationCommand().{ status = [ Status.Reserved ],
 * allowed = [ Allowed.Owner ], newStatus = Status.InProgress } ], *
 */
@XmlRootElement(name = "complete-task-command")
@XmlAccessorType(value = XmlAccessType.NONE)
public class CompleteTaskCommand extends UserGroupCallbackTaskCommand<Void> {
    private static final long serialVersionUID = 412409697422083299L;

    @XmlJavaTypeAdapter(value = JaxbMapAdapter.class)
    @XmlElement
    protected Map<String, Object> data;

    public CompleteTaskCommand() {
    }

    public CompleteTaskCommand(long taskId, String userId, Map<String, Object> data) {
        this.taskId = taskId;
        this.userId = userId;
        CompleteTaskCommand.this.data = data;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        CompleteTaskCommand.this.data = data;
    }

    public Void execute(Context cntxt) {
        TaskContext context = ((TaskContext) (cntxt));
        doCallbackUserOperation(userId, context);
        groupIds = doUserGroupCallbackOperation(userId, null, context);
        context.set("local:groups", groupIds);
        Task task = context.getTaskQueryService().getTaskInstanceById(taskId);
        context.getTaskRuleService().executeRules(task, userId, data, TaskRuleService.COMPLETE_TASK_SCOPE);
        ((InternalTaskData) (task.getTaskData())).setTaskOutputVariables(data);
        TaskInstanceService instanceService = context.getTaskInstanceService();
        instanceService.complete(taskId, userId, data);
        return null;
    }
}

