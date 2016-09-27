/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.executor.cdi.impl.runtime;

import org.kie.internal.runtime.cdi.Activate;
import javax.enterprise.context.ApplicationScoped;
import org.kie.api.executor.ExecutorService;
import java.util.HashMap;
import java.util.Map;
import org.jbpm.executor.commands.PrintOutCommand;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.internal.runtime.manager.WorkItemHandlerProducer;

/**
 * Dedicated <code>WorkItemHandlerProducer</code> to register <code>AsyncWorkItemHandler</code>
 * in CDI environment when using deployment services (jbpm-kie-services).
 */
@Activate(whenAvailable = "org.jbpm.runtime.manager.impl.RuntimeManagerFactoryImpl")
@ApplicationScoped
public class AsyncHandlerProducer implements WorkItemHandlerProducer {
    @Override
    public Map<String, WorkItemHandler> getWorkItemHandlers(String identifier, Map<String, Object> params) {
        Map<String, WorkItemHandler> handlers = new HashMap<String, WorkItemHandler>();
        ExecutorService executorService = ((ExecutorService) (params.get("executorService")));
        if (executorService != null) {
            handlers.put("async", new org.jbpm.executor.impl.wih.AsyncWorkItemHandler(executorService, PrintOutCommand.class.getName()));
        } 
        return handlers;
    }
}

