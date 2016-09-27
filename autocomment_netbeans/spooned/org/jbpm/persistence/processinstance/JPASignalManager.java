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


package org.jbpm.persistence.processinstance;

import org.jbpm.process.core.async.AsyncSignalEventCommand;
import org.kie.api.executor.CommandContext;
import org.jbpm.process.instance.event.DefaultSignalManager;
import org.kie.api.executor.ExecutorService;
import org.drools.core.common.InternalKnowledgeRuntime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jbpm.persistence.ProcessPersistenceContext;
import org.jbpm.persistence.ProcessPersistenceContextManager;
import org.kie.api.runtime.manager.RuntimeManager;

public class JPASignalManager extends DefaultSignalManager {
    private static final String ASYNC_SIGNAL_PREFIX = "ASYNC-";

    private static final Logger logger = LoggerFactory.getLogger(JPASignalManager.class);

    public JPASignalManager(InternalKnowledgeRuntime kruntime) {
        super(kruntime);
    }

    public void signalEvent(String type, Object event) {
        String actualSignalType = type.replaceFirst(JPASignalManager.ASYNC_SIGNAL_PREFIX, "");
        ProcessPersistenceContextManager contextManager = ((ProcessPersistenceContextManager) (getKnowledgeRuntime().getEnvironment().get(EnvironmentName.PERSISTENCE_CONTEXT_MANAGER)));
        ProcessPersistenceContext context = contextManager.getProcessPersistenceContext();
        List<Long> processInstancesToSignalList = context.getProcessInstancesWaitingForEvent(actualSignalType);
        // handle signal asynchronously
        if (type.startsWith(JPASignalManager.ASYNC_SIGNAL_PREFIX)) {
            RuntimeManager runtimeManager = ((RuntimeManager) (getKnowledgeRuntime().getEnvironment().get("RuntimeManager")));
            ExecutorService executorService = ((ExecutorService) (getKnowledgeRuntime().getEnvironment().get("ExecutorService")));
            if ((runtimeManager != null) && (executorService != null)) {
                for (Long processInstanceId : processInstancesToSignalList) {
                    CommandContext ctx = new CommandContext();
                    ctx.setData("DeploymentId", runtimeManager.getIdentifier());
                    ctx.setData("ProcessInstanceId", processInstanceId);
                    ctx.setData("Signal", actualSignalType);
                    ctx.setData("Event", event);
                    executorService.scheduleRequest(AsyncSignalEventCommand.class.getName(), ctx);
                }
                return ;
            } else {
                JPASignalManager.logger.warn("Signal should be sent asynchronously but there is no executor service available, continuing sync...");
            }
        } 
        for (long id : processInstancesToSignalList) {
            try {
                getKnowledgeRuntime().getProcessInstance(id);
            } catch (IllegalStateException e) {
                // IllegalStateException can be thrown when using RuntimeManager
                // and invalid ksession was used for given context
            } catch (RuntimeException e) {
                JPASignalManager.logger.warn("Exception when loading process instance for signal '{}', instance with id {} will not be signaled", e.getMessage(), id);
            }
        }
        super.signalEvent(actualSignalType, event);
    }
}

