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


package org.jbpm.executor.impl.jpa;

import org.drools.core.command.CommandService;
import org.kie.internal.command.Context;
import java.util.Date;
import org.kie.api.executor.ErrorInfo;
import org.kie.internal.executor.api.ExecutorQueryService;
import org.jbpm.shared.services.impl.commands.FindObjectCommand;
import org.drools.core.command.impl.GenericCommand;
import java.util.HashMap;
import org.jbpm.shared.services.impl.JpaPersistenceContext;
import java.util.List;
import java.util.Map;
import javax.persistence.NoResultException;
import org.kie.api.runtime.query.QueryContext;
import org.jbpm.shared.services.impl.QueryManager;
import org.jbpm.shared.services.impl.commands.QueryNameCommand;
import org.kie.api.executor.RequestInfo;
import org.kie.api.executor.STATUS;

/**
 * Default implementation of <code>ExecutorQueryService</code> that is backed with JPA
 * IMPORTANT: please keep all classes from package org.jbpm.shared.services.impl as FQCN
 * inside method body to avoid exception logged by CDI when used with in memory mode
 */
public class ExecutorQueryServiceImpl implements ExecutorQueryService {
    private CommandService commandService;

    public ExecutorQueryServiceImpl(boolean active) {
        QueryManager.get().addNamedQueries("META-INF/Executor-orm.xml");
    }

    public void setCommandService(CommandService commandService) {
        ExecutorQueryServiceImpl.this.commandService = commandService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RequestInfo> getPendingRequests() {
        return getPendingRequests(new QueryContext(0, 100));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RequestInfo> getPendingRequestById(Long id) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        return commandService.execute(new QueryNameCommand<List<RequestInfo>>("PendingRequestById", params));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RequestInfo getRequestById(Long id) {
        return commandService.execute(new FindObjectCommand<org.jbpm.executor.entities.RequestInfo>(id, .class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RequestInfo> getRunningRequests() {
        return getRunningRequests(new QueryContext(0, 100));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RequestInfo> getQueuedRequests() {
        return getQueuedRequests(new QueryContext(0, 100));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RequestInfo> getFutureQueuedRequests() {
        return getFutureQueuedRequests(new QueryContext(0, 100));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RequestInfo> getCompletedRequests() {
        return getCompletedRequests(new QueryContext(0, 100));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RequestInfo> getInErrorRequests() {
        return getInErrorRequests(new QueryContext(0, 100));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RequestInfo> getCancelledRequests() {
        return getCancelledRequests(new QueryContext(0, 100));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ErrorInfo> getAllErrors() {
        return getAllErrors(new QueryContext(0, 100));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ErrorInfo> getErrorsByRequestId(Long requestId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", requestId);
        return commandService.execute(new QueryNameCommand<List<ErrorInfo>>("GetErrorsByRequestId", params));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RequestInfo> getAllRequests() {
        return getAllRequests(new QueryContext(0, 100));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RequestInfo> getRequestsByStatus(List<STATUS> statuses) {
        return getRequestsByStatus(statuses, new QueryContext(0, 100));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RequestInfo> getRequestByBusinessKey(String businessKey, QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("businessKey", businessKey);
        applyQueryContext(params, queryContext);
        return commandService.execute(new QueryNameCommand<List<RequestInfo>>("GetRequestsByBusinessKey", params));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RequestInfo> getRequestByCommand(String command, QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("command", command);
        applyQueryContext(params, queryContext);
        return commandService.execute(new QueryNameCommand<List<RequestInfo>>("GetRequestsByCommand", params));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RequestInfo getRequestForProcessing() {
        // need to do the lock here to avoid many executor services fetch the same element
        RequestInfo request = commandService.execute(new ExecutorQueryServiceImpl.LockAndUpdateRequestInfoCommand());
        return request;
    }

    public RequestInfo getRequestForProcessing(Long requestId) {
        // need to do the lock here to avoid many executor services fetch the same element
        RequestInfo request = commandService.execute(new ExecutorQueryServiceImpl.LockAndUpdateRequestInfoByIdCommand(requestId));
        return request;
    }

    private class LockAndUpdateRequestInfoCommand implements GenericCommand<RequestInfo> {
        private static final long serialVersionUID = 8670412133363766161L;

        @Override
        public RequestInfo execute(Context context) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("now", new Date());
            params.put("firstResult", 0);
            params.put("maxResults", 1);
            params.put("owner", ExecutorService.EXECUTOR_ID);
            RequestInfo request = null;
            try {
                JpaPersistenceContext ctx = ((JpaPersistenceContext) (context));
                request = ctx.queryAndLockWithParametersInTransaction("PendingRequestsForProcessing", params, true, RequestInfo.class);
                if (request != null) {
                    request.setStatus(STATUS.RUNNING);
                    // update date on when it was started to be executed
                    ((org.jbpm.executor.entities.RequestInfo) (request)).setTime(new Date());
                    ctx.merge(request);
                } 
            } catch (NoResultException e) {
            }
            return request;
        }
    }

    private class LockAndUpdateRequestInfoByIdCommand implements GenericCommand<RequestInfo> {
        private static final long serialVersionUID = 8670412133363766161L;

        private Long requestId;

        LockAndUpdateRequestInfoByIdCommand(Long requestId) {
            ExecutorQueryServiceImpl.LockAndUpdateRequestInfoByIdCommand.this.requestId = requestId;
        }

        @Override
        public RequestInfo execute(Context context) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("requestId", requestId);
            RequestInfo request = null;
            JpaPersistenceContext ctx = ((JpaPersistenceContext) (context));
            List<RequestInfo> foundInstance = ctx.queryAndLockWithParametersInTransaction("PendingRequestByIdForProcessing", params, false, List.class);
            if ((foundInstance != null) && (!(foundInstance.isEmpty()))) {
                request = foundInstance.get(0);
                if (request != null) {
                    request.setStatus(STATUS.RUNNING);
                    // update date on when it was started to be executed
                    ((org.jbpm.executor.entities.RequestInfo) (request)).setTime(new Date());
                    ctx.merge(request);
                } 
            } 
            return request;
        }
    }

    @Override
    public List<RequestInfo> getQueuedRequests(QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        applyQueryContext(params, queryContext);
        return commandService.execute(new QueryNameCommand<List<RequestInfo>>("QueuedRequests", params));
    }

    @Override
    public List<RequestInfo> getCompletedRequests(QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        applyQueryContext(params, queryContext);
        return commandService.execute(new QueryNameCommand<List<RequestInfo>>("CompletedRequests", params));
    }

    @Override
    public List<RequestInfo> getInErrorRequests(QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        applyQueryContext(params, queryContext);
        return commandService.execute(new QueryNameCommand<List<RequestInfo>>("InErrorRequests", params));
    }

    @Override
    public List<RequestInfo> getCancelledRequests(QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        applyQueryContext(params, queryContext);
        return commandService.execute(new QueryNameCommand<List<RequestInfo>>("CancelledRequests", params));
    }

    @Override
    public List<ErrorInfo> getAllErrors(QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        applyQueryContext(params, queryContext);
        return commandService.execute(new QueryNameCommand<List<ErrorInfo>>("GetAllErrors", params));
    }

    @Override
    public List<RequestInfo> getAllRequests(QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        applyQueryContext(params, queryContext);
        return commandService.execute(new QueryNameCommand<List<RequestInfo>>("GetAllRequests", params));
    }

    @Override
    public List<RequestInfo> getRunningRequests(QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        applyQueryContext(params, queryContext);
        return commandService.execute(new QueryNameCommand<List<RequestInfo>>("RunningRequests", params));
    }

    @Override
    public List<RequestInfo> getFutureQueuedRequests(QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        applyQueryContext(params, queryContext);
        params.put("now", new Date());
        return commandService.execute(new QueryNameCommand<List<RequestInfo>>("FutureQueuedRequests", params));
    }

    @Override
    public List<RequestInfo> getRequestsByStatus(List<STATUS> statuses, QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        applyQueryContext(params, queryContext);
        params.put("statuses", statuses);
        return commandService.execute(new QueryNameCommand<List<RequestInfo>>("GetRequestsByStatus", params));
    }

    @Override
    public List<RequestInfo> getPendingRequests(QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        applyQueryContext(params, queryContext);
        params.put("now", new Date());
        return commandService.execute(new QueryNameCommand<List<RequestInfo>>("PendingRequests", params));
    }

    protected void applyQueryContext(Map<String, Object> params, QueryContext queryContext) {
        if (queryContext != null) {
            params.put(JpaPersistenceContext.FIRST_RESULT, queryContext.getOffset());
            params.put(JpaPersistenceContext.MAX_RESULTS, queryContext.getCount());
            if (((queryContext.getOrderBy()) != null) && (!(queryContext.getOrderBy().isEmpty()))) {
                params.put(QueryManager.ORDER_BY_KEY, queryContext.getOrderBy());
                if (queryContext.isAscending()) {
                    params.put(QueryManager.ASCENDING_KEY, "true");
                } else {
                    params.put(QueryManager.DESCENDING_KEY, "true");
                }
            } 
        } 
    }
}

