/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.services.cdi.impl.producers;

import java.util.ArrayList;
import org.kie.internal.runtime.manager.EventListenerProducer;
import java.util.List;
import java.util.Map;
import org.jbpm.runtime.manager.api.qualifiers.Task;
import org.kie.api.task.TaskLifeCycleEventListener;

@Task
public class CustomTaskEventListenerProducer implements EventListenerProducer<TaskLifeCycleEventListener> {
    @Override
    public List<TaskLifeCycleEventListener> getEventListeners(String identifier, Map<String, Object> params) {
        List<TaskLifeCycleEventListener> taskEventListeners = new ArrayList<TaskLifeCycleEventListener>();
        taskEventListeners.add(new org.jbpm.services.task.audit.JPATaskLifeCycleEventListener(true));
        return taskEventListeners;
    }
}

