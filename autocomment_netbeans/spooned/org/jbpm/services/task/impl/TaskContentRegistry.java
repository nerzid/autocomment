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


package org.jbpm.services.task.impl;

import java.util.concurrent.ConcurrentHashMap;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.kie.api.task.model.Task;

public class TaskContentRegistry {
    private static TaskContentRegistry INSTANCE = new TaskContentRegistry();

    private ConcurrentHashMap<String, ContentMarshallerContext> marhsalContexts = new ConcurrentHashMap<String, ContentMarshallerContext>();

    private TaskContentRegistry() {
    }

    public static TaskContentRegistry get() {
        return TaskContentRegistry.INSTANCE;
    }

    public synchronized void addMarshallerContext(String ownerId, ContentMarshallerContext context) {
        TaskContentRegistry.this.marhsalContexts.put(ownerId, context);
    }

    public synchronized void removeMarshallerContext(String ownerId) {
        TaskContentRegistry.this.marhsalContexts.remove(ownerId);
    }

    public ContentMarshallerContext getMarshallerContext(Task task) {
        if (((task.getTaskData().getDeploymentId()) != null) && (TaskContentRegistry.this.marhsalContexts.containsKey(task.getTaskData().getDeploymentId()))) {
            return TaskContentRegistry.this.marhsalContexts.get(task.getTaskData().getDeploymentId());
        } 
        return new ContentMarshallerContext();
    }

    public ContentMarshallerContext getMarshallerContext(String deploymentId) {
        if ((deploymentId != null) && (TaskContentRegistry.this.marhsalContexts.containsKey(deploymentId))) {
            return TaskContentRegistry.this.marhsalContexts.get(deploymentId);
        } 
        return new ContentMarshallerContext();
    }
}

