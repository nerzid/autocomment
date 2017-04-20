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

import org.kie.internal.task.api.model.InternalTask;
import org.kie.internal.command.Context;
import org.kie.internal.task.api.model.Deadline;
import DeadlineType.END;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Iterator;
import DeadlineType.START;
import org.kie.internal.task.api.TaskDeadlinesService;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.internal.task.api.TaskQueryService;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlRootElement(name = "cancel-deadline-command")
@XmlAccessorType(value = XmlAccessType.NONE)
public class CancelDeadlineCommand extends UserGroupCallbackTaskCommand<Void> {
    private static final long serialVersionUID = -1315897796195789680L;

    @XmlElement
    private boolean removeStart;

    @XmlElement
    private boolean removeEnd;

    public CancelDeadlineCommand() {
    }

    public CancelDeadlineCommand(long taskId, boolean removeStart, boolean removeEnd) {
        this.taskId = taskId;
        this.removeStart = removeStart;
        this.removeEnd = removeEnd;
    }

    @Override
    public Void execute(Context cntxt) {
        TaskContext context = ((TaskContext) (cntxt));
        TaskDeadlinesService deadlineService = context.getTaskDeadlinesService();
        TaskQueryService queryService = context.getTaskQueryService();
        TaskPersistenceContext persistenceContext = context.getPersistenceContext();
        InternalTask task = ((InternalTask) (queryService.getTaskInstanceById(taskId)));
        if ((task == null) || ((task.getDeadlines()) == null)) {
            return null;
        }
        Iterator<? extends Deadline> it = null;
        if (removeStart) {
            if ((task.getDeadlines().getStartDeadlines()) != null) {
                deadlineService.unschedule(taskId, START);
                it = task.getDeadlines().getStartDeadlines().iterator();
                while (it.hasNext()) {
                    persistenceContext.removeDeadline(it.next());
                    it.remove();
                } 
            }
        }
        if (removeEnd) {
            if ((task.getDeadlines().getEndDeadlines()) != null) {
                deadlineService.unschedule(taskId, END);
                it = task.getDeadlines().getEndDeadlines().iterator();
                while (it.hasNext()) {
                    persistenceContext.removeDeadline(it.next());
                    it.remove();
                } 
            }
        }
        return null;
    }
}

