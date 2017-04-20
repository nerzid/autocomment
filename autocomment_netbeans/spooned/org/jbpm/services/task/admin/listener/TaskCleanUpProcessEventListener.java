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


package org.jbpm.services.task.admin.listener;

import Status.Obsolete;
import java.util.ArrayList;
import Status.Completed;
import org.drools.core.event.DefaultProcessEventListener;
import Status.Error;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import Status.Exited;
import Status.Failed;
import org.kie.internal.task.api.InternalTaskService;
import java.util.List;
import org.kie.api.event.process.ProcessCompletedEvent;
import Status.Suspended;
import org.kie.api.task.model.Status;

public class TaskCleanUpProcessEventListener extends DefaultProcessEventListener {
    private InternalTaskService taskService;

    public TaskCleanUpProcessEventListener(TaskService taskService) {
        this.taskService = ((InternalTaskService) (taskService));
    }

    @Override
    public void afterProcessCompleted(ProcessCompletedEvent event) {
        List<Status> statuses = new ArrayList<Status>();
        // add void{Error} to List{statuses}
        statuses.add(Error);
        // add void{Failed} to List{statuses}
        statuses.add(Failed);
        // add void{Obsolete} to List{statuses}
        statuses.add(Obsolete);
        // add void{Suspended} to List{statuses}
        statuses.add(Suspended);
        // add void{Completed} to List{statuses}
        statuses.add(Completed);
        // add void{Exited} to List{statuses}
        statuses.add(Exited);
        List<TaskSummary> completedTasksByProcessId = ((InternalTaskService) (taskService)).execute(new GetTasksForProcessCommand(event.getProcessInstance().getId(), statuses, "en-UK"));
        // archive and remove
        // archive tasks List{completedTasksByProcessId} to InternalTaskService{taskService}
        taskService.archiveTasks(completedTasksByProcessId);
        // remove tasks List{completedTasksByProcessId} to InternalTaskService{taskService}
        taskService.removeTasks(completedTasksByProcessId);
    }

    public void setTaskService(InternalTaskService taskService) {
        this.taskService = taskService;
    }
}

