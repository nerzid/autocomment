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


package org.jbpm.executor.impl.jpa;

import org.jbpm.executor.ExecutorServiceFactory;
import STATUS.CANCELLED;
import org.drools.core.command.CommandService;
import org.kie.api.executor.RequestInfo;
import org.kie.internal.command.Context;
import javax.persistence.EntityManagerFactory;
import org.kie.api.executor.ErrorInfo;
import org.jbpm.executor.impl.event.ExecutorEventSupport;
import javax.persistence.NoResultException;
import org.kie.api.executor.ExecutorStoreService;
import org.drools.core.command.impl.GenericCommand;
import java.util.Map;
import java.util.HashMap;
import org.jbpm.shared.services.impl.JpaPersistenceContext;

/**
 * IMPORTANT: please keep all classes from package org.jbpm.shared.services.impl as FQCN
 * inside method body to avoid exception logged by CDI when used with in memory mode
 */
public class JPAExecutorStoreService implements ExecutorStoreService {
    private EntityManagerFactory emf;

    private CommandService commandService;

    private ExecutorEventSupport eventSupport = new ExecutorEventSupport();

    public JPAExecutorStoreService(boolean active) {
    }

    public void setEventSupport(ExecutorEventSupport eventSupport) {
        this.eventSupport = eventSupport;
    }

    public void setCommandService(CommandService commandService) {
        this.commandService = commandService;
    }

    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void persistRequest(RequestInfo request) {
        // execute PersistObjectCommand{new PersistObjectCommand(request)} to CommandService{commandService}
        commandService.execute(new PersistObjectCommand(request));
    }

    @Override
    public void updateRequest(RequestInfo request) {
        // execute MergeObjectCommand{new MergeObjectCommand(request)} to CommandService{commandService}
        commandService.execute(new MergeObjectCommand(request));
    }

    @Override
    public RequestInfo removeRequest(Long requestId) {
        return commandService.execute(new JPAExecutorStoreService.LockAndCancelRequestInfoCommand(requestId));
    }

    @Override
    public RequestInfo findRequest(Long id) {
        return commandService.execute(new org.jbpm.shared.services.impl.commands.FindObjectCommand<RequestInfo>(id, byte.class));
    }

    @Override
    public void persistError(ErrorInfo error) {
        // execute PersistObjectCommand{new PersistObjectCommand(error)} to CommandService{commandService}
        commandService.execute(new PersistObjectCommand(error));
    }

    @Override
    public void updateError(ErrorInfo error) {
        // execute MergeObjectCommand{new MergeObjectCommand(error)} to CommandService{commandService}
        commandService.execute(new MergeObjectCommand(error));
    }

    @Override
    public ErrorInfo removeError(Long errorId) {
        ErrorInfo error = findError(errorId);
        // execute RemoveObjectCommand{new RemoveObjectCommand(error)} to CommandService{commandService}
        commandService.execute(new RemoveObjectCommand(error));
        return error;
    }

    @Override
    public ErrorInfo findError(Long id) {
        return commandService.execute(new org.jbpm.shared.services.impl.commands.FindObjectCommand<ErrorInfo>(id, byte.class));
    }

    @Override
    public Runnable buildExecutorRunnable() {
        return ExecutorServiceFactory.buildRunable(emf, eventSupport);
    }

    private class LockAndCancelRequestInfoCommand implements GenericCommand<RequestInfo> {
        private static final long serialVersionUID = 8670412133363766161L;

        private Long requestId;

        LockAndCancelRequestInfoCommand(Long requestId) {
            this.requestId = requestId;
        }

        @Override
        public RequestInfo execute(Context context) {
            Map<String, Object> params = new HashMap<String, Object>();
            // put String{"id"} to Map{params}
            params.put("id", requestId);
            // put String{"firstResult"} to Map{params}
            params.put("firstResult", 0);
            // put String{"maxResults"} to Map{params}
            params.put("maxResults", 1);
            RequestInfo request = null;
            try {
                JpaPersistenceContext ctx = ((JpaPersistenceContext) (context));
                request = ctx.queryAndLockWithParametersInTransaction("PendingRequestById", params, true, RequestInfo.class);
                if (request != null) {
                    request.setStatus(CANCELLED);
                    ctx.merge(request);
                }
            } catch (NoResultException e) {
            }
            return request;
        }
    }
}

