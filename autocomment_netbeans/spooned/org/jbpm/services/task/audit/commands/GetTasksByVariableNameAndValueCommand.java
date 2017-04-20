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


package org.jbpm.services.task.audit.commands;

import org.kie.api.task.model.Status;
import java.util.ArrayList;
import org.jbpm.services.task.utils.ClassUtil;
import org.kie.internal.command.Context;
import Status.InProgress;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import org.kie.internal.query.QueryFilter;
import Status.Ready;
import Status.Reserved;
import org.jbpm.services.task.commands.TaskContext;
import org.kie.api.task.model.TaskSummary;
import org.jbpm.services.task.commands.UserGroupCallbackTaskCommand;
import javax.xml.bind.annotation.XmlAccessorType;
import org.kie.internal.task.api.TaskPersistenceContext;

@XmlRootElement(name = "get-tasks-by-variable-name-and-value-command")
@XmlAccessorType(value = XmlAccessType.NONE)
public class GetTasksByVariableNameAndValueCommand extends UserGroupCallbackTaskCommand<List<TaskSummary>> {
    private static final long serialVersionUID = 5077599352603072633L;

    @XmlElement
    private List<Status> statuses;

    @XmlElement
    private String variableName;

    @XmlElement
    private String variableValue;

    @XmlElement(type = QueryFilter.class)
    private QueryFilter filter;

    public GetTasksByVariableNameAndValueCommand() {
    }

    public GetTasksByVariableNameAndValueCommand(String userId, String variableName, String variableValue, List<Status> status, QueryFilter filter) {
        this.userId = userId;
        this.variableName = variableName;
        this.variableValue = variableValue;
        this.statuses = status;
        this.filter = filter;
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    public String getVariableName() {
        return variableName;
    }

    public String getVariableValue() {
        return variableValue;
    }

    public List<TaskSummary> execute(Context cntxt) {
        TaskContext context = ((TaskContext) (cntxt));
        // do callback GetTasksByVariableNameAndValueCommand{userId} to GetTasksByVariableNameAndValueCommand{}
        doCallbackUserOperation(userId, context);
        if ((statuses) == null) {
            statuses = new ArrayList<Status>();
            statuses.add(Ready);
            statuses.add(InProgress);
            statuses.add(Reserved);
        }
        if ((groupIds) == null) {
            groupIds = doUserGroupCallbackOperation(userId, null, context);
            if ((groupIds) == null) {
                groupIds = new ArrayList<String>();
            }
        }
        TaskPersistenceContext persistenceContext = context.getPersistenceContext();
        List<TaskSummary> tasks = ((List<TaskSummary>) (persistenceContext.queryWithParametersInTransaction("TasksByStatusByVariableNameAndValue", persistenceContext.addParametersToMap("userId", userId, "groupIds", groupIds, "variableName", variableName, "variableValue", variableValue, "status", statuses), ClassUtil.<List<TaskSummary>>castClass(List.class))));
        return tasks;
    }
}

