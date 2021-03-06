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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Operation.Exit
 * : [ new OperationCommand().{
 * status = [ Status.Created, Status.Ready, Status.Reserved, Status.InProgress, Status.Suspended ],
 * allowed = [ Allowed.BusinessAdministrator ],
 * newStatus = Status.Exited
 * } ]
 */
@XmlRootElement(name = "exit-task-command")
@XmlAccessorType(value = XmlAccessType.NONE)
public class ExitTaskCommand extends UserGroupCallbackTaskCommand<Void> {
    private static final long serialVersionUID = 6514448276718693595L;

    public ExitTaskCommand() {
    }

    public ExitTaskCommand(long taskId, String userId) {
        this.taskId = taskId;
        this.userId = userId;
    }

    public Void execute(Context cntxt) {
        TaskContext context = ((TaskContext) (cntxt));
        doCallbackUserOperation(userId, context);
        groupIds = doUserGroupCallbackOperation(userId, null, context);
        context.set("local:groups", groupIds);
        context.getTaskInstanceService().exit(taskId, userId);
        return null;
    }
}

