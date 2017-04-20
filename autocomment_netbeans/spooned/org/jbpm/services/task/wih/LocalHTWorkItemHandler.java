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


package org.jbpm.services.task.wih;

import org.slf4j.LoggerFactory;
import OnErrorAction.ABORT;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.kie.api.runtime.process.WorkItem;
import java.util.Map;
import org.jbpm.services.task.exception.PermissionDeniedException;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.task.model.Task;

public class LocalHTWorkItemHandler extends AbstractHTWorkItemHandler {
    private static final Logger logger = LoggerFactory.getLogger(LocalHTWorkItemHandler.class);

    private RuntimeManager runtimeManager;

    public RuntimeManager getRuntimeManager() {
        return runtimeManager;
    }

    public void setRuntimeManager(RuntimeManager runtimeManager) {
        this.runtimeManager = runtimeManager;
    }

    public LocalHTWorkItemHandler() {
    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        RuntimeEngine runtime = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get(workItem.getProcessInstanceId()));
        KieSession ksessionById = runtime.getKieSession();
        Task task = createTaskBasedOnWorkItemParams(ksessionById, workItem);
        // ContentData content = createTaskContentBasedOnWorkItemParams(ksessionById, workItem);
        Map<String, Object> content = createTaskDataBasedOnWorkItemParams(ksessionById, workItem);
        try {
            long taskId = ((InternalTaskService) (runtime.getTaskService())).addTask(task, content);
            if (isAutoClaim(workItem, task)) {
                try {
                    runtime.getTaskService().claim(taskId, ((String) (workItem.getParameter("SwimlaneActorId"))));
                } catch (PermissionDeniedException e) {
                    LocalHTWorkItemHandler.logger.warn("User {} is not allowed to auto claim task due to permission violation", workItem.getParameter("SwimlaneActorId"));
                }
            }
        } catch (Exception e) {
            // rethrow to cancel processing if the exception is not recoverable
            if (action.equals(ABORT)) {
                manager.abortWorkItem(workItem.getId());
            }// rethrow to cancel processing if the exception is not recoverable
            else
                if (action.equals(OnErrorAction.RETHROW)) {
                    if (e instanceof RuntimeException) {
                        throw ((RuntimeException) (e));
                    }else {
                        throw new RuntimeException(e);
                    }
                }else
                    if (action.equals(OnErrorAction.LOG)) {
                        StringBuilder logMsg = new StringBuilder();
                        logMsg.append(new java.util.Date()).append(": Error when creating task on task server for work item id ").append(workItem.getId());
                        logMsg.append(". Error reported by task server: ").append(e.getMessage());
                        LocalHTWorkItemHandler.logger.error(logMsg.toString(), e);
                        if ((!(e instanceof org.kie.internal.task.exception.TaskException)) || ((e instanceof org.kie.internal.task.exception.TaskException) && (!(((org.kie.internal.task.exception.TaskException) (e)).isRecoverable())))) {
                            if (e instanceof RuntimeException) {
                                throw ((RuntimeException) (e));
                            }else {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                
            
        }
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        RuntimeEngine runtime = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get(workItem.getProcessInstanceId()));
        Task task = runtime.getTaskService().getTaskByWorkItemId(workItem.getId());
        if (task != null) {
            try {
                runtime.getTaskService().exit(task.getId(), "Administrator");
            } catch (PermissionDeniedException e) {
                LocalHTWorkItemHandler.logger.info(e.getMessage());
            }
        }
    }
}

