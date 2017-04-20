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

import java.util.List;
import org.kie.internal.command.Context;
import org.kie.internal.query.QueryFilter;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "get-tasks-owned-command")
@XmlAccessorType(value = XmlAccessType.NONE)
public class GetTasksOwnedCommand extends UserGroupCallbackTaskCommand<List<TaskSummary>> {
    private static final long serialVersionUID = -1763215272466075367L;

    @XmlElement
    private List<Status> statuses;

    @XmlElement(type = QueryFilter.class)
    private QueryFilter filter;

    public GetTasksOwnedCommand() {
    }

    public GetTasksOwnedCommand(String userId) {
        this.userId = userId;
    }

    public GetTasksOwnedCommand(String userId, List<Status> status) {
        this.userId = userId;
        this.statuses = status;
    }

    public GetTasksOwnedCommand(String userId, List<Status> status, QueryFilter filter) {
        this.userId = userId;
        this.statuses = status;
        this.filter = filter;
    }

    public List<Status> getStatus() {
        return statuses;
    }

    public QueryFilter getFilter() {
        return filter;
    }

    public List<TaskSummary> execute(Context cntxt) {
        TaskContext context = ((TaskContext) (cntxt));
        // do callback GetTasksOwnedCommand{userId} to GetTasksOwnedCommand{}
        doCallbackUserOperation(userId, context);
        // do user GetTasksOwnedCommand{userId} to GetTasksOwnedCommand{}
        doUserGroupCallbackOperation(userId, null, context);
        return context.getTaskQueryService().getTasksOwned(userId, statuses, filter);
    }
}

