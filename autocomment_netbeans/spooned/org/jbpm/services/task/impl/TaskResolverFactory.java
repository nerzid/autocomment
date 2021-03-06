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

import org.mvel2.integration.impl.ImmutableDefaultFactory;
import org.kie.api.task.model.Task;
import org.mvel2.integration.VariableResolver;

public class TaskResolverFactory extends ImmutableDefaultFactory {
    private static final long serialVersionUID = 8019024969834990593L;

    private Task task;

    public TaskResolverFactory(Task task) {
        TaskResolverFactory.this.task = task;
    }

    public boolean isResolveable(String name) {
        return "task".equals(name);
    }

    public VariableResolver getVariableResolver(String name) {
        return new org.mvel2.integration.impl.SimpleValueResolver(task);
    }
}

