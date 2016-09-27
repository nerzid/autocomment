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
/**
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package org.jbpm.services.task.impl;

import org.jbpm.services.task.utils.ClassUtil;
import java.util.List;
import org.kie.internal.task.api.model.TaskEvent;
import org.kie.internal.task.api.TaskEventsService;
import org.kie.internal.task.api.TaskPersistenceContext;

/**
 */
public class TaskEventsServiceImpl implements TaskEventsService {
    private TaskPersistenceContext persistenceContext;

    public TaskEventsServiceImpl() {
    }

    public TaskEventsServiceImpl(TaskPersistenceContext persistenceContext) {
        TaskEventsServiceImpl.this.persistenceContext = persistenceContext;
    }

    public List<TaskEvent> getTaskEventsById(long taskId) {
        return persistenceContext.queryStringWithParametersInTransaction("select te from TaskEvent te where te.taskId =:taskId ", persistenceContext.addParametersToMap("taskId", taskId), ClassUtil.<List<TaskEvent>>castClass(List.class));
    }

    public void removeTaskEventsById(long taskId) {
        List<TaskEvent> taskEventsById = getTaskEventsById(taskId);
        for (TaskEvent e : taskEventsById) {
            persistenceContext.remove(e);
        }
    }
}

