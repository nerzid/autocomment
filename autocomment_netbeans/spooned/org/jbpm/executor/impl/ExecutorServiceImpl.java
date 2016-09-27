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


package org.jbpm.executor.impl;

import org.jbpm.executor.AsynchronousJobListener;
import org.kie.api.executor.CommandContext;
import java.util.Date;
import org.kie.api.executor.ErrorInfo;
import org.kie.api.executor.Executor;
import org.kie.api.executor.ExecutorAdminService;
import org.jbpm.executor.impl.event.ExecutorEventSupport;
import org.jbpm.executor.ExecutorNotStartedException;
import org.kie.api.executor.ExecutorQueryService;
import org.kie.internal.executor.api.ExecutorService;
import org.jbpm.executor.ExecutorServiceFactory;
import java.util.List;
import org.kie.api.runtime.query.QueryContext;
import org.kie.api.executor.RequestInfo;
import org.jbpm.executor.RequeueAware;
import org.kie.api.executor.STATUS;
import java.util.concurrent.TimeUnit;

/**
 * Entry point of the executor component. Application should always talk
 * via this service to ensure all internals are properly initialized
 */
public class ExecutorServiceImpl implements RequeueAware , ExecutorService {
    private TimeUnit timeunit = TimeUnit.valueOf(System.getProperty("org.kie.executor.timeunit", "SECONDS"));

    private long maxRunningTime = Long.parseLong(System.getProperty("org.kie.executor.running.max", "600"));

    private Executor executor;

    private boolean executorStarted = false;

    private ExecutorQueryService queryService;

    private ExecutorAdminService adminService;

    private ExecutorEventSupport eventSupport = new ExecutorEventSupport();

    public ExecutorServiceImpl() {
    }

    public ExecutorServiceImpl(Executor executor) {
    }

    public ExecutorEventSupport getEventSupport() {
        return ExecutorServiceImpl.this.eventSupport;
    }

    public void setEventSupport(ExecutorEventSupport eventSupport) {
        ExecutorServiceImpl.this.eventSupport = eventSupport;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        ExecutorServiceImpl.this.executor = executor;
    }

    public ExecutorQueryService getQueryService() {
        return queryService;
    }

    public void setQueryService(ExecutorQueryService queryService) {
        ExecutorServiceImpl.this.queryService = queryService;
    }

    public ExecutorAdminService getAdminService() {
        return adminService;
    }

    public void setAdminService(ExecutorAdminService adminService) {
        ExecutorServiceImpl.this.adminService = adminService;
    }

    public List<RequestInfo> getFutureQueuedRequests() {
        return ((org.kie.internal.executor.api.ExecutorQueryService) (queryService)).getFutureQueuedRequests();
    }

    public List<RequestInfo> getQueuedRequests() {
        return ((org.kie.internal.executor.api.ExecutorQueryService) (queryService)).getQueuedRequests();
    }

    public List<RequestInfo> getCompletedRequests() {
        return ((org.kie.internal.executor.api.ExecutorQueryService) (queryService)).getCompletedRequests();
    }

    public List<RequestInfo> getInErrorRequests() {
        return ((org.kie.internal.executor.api.ExecutorQueryService) (queryService)).getInErrorRequests();
    }

    public List<RequestInfo> getCancelledRequests() {
        return ((org.kie.internal.executor.api.ExecutorQueryService) (queryService)).getCancelledRequests();
    }

    public List<ErrorInfo> getAllErrors() {
        return ((org.kie.internal.executor.api.ExecutorQueryService) (queryService)).getAllErrors();
    }

    public List<RequestInfo> getAllRequests() {
        return ((org.kie.internal.executor.api.ExecutorQueryService) (queryService)).getAllRequests();
    }

    public List<RequestInfo> getRequestsByStatus(List<STATUS> statuses) {
        return ((org.kie.internal.executor.api.ExecutorQueryService) (queryService)).getRequestsByStatus(statuses);
    }

    public int clearAllRequests() {
        return adminService.clearAllRequests();
    }

    public int clearAllErrors() {
        return adminService.clearAllErrors();
    }

    public Long scheduleRequest(String commandName, CommandContext ctx) {
        return executor.scheduleRequest(commandName, ctx);
    }

    public void cancelRequest(Long requestId) {
        executor.cancelRequest(requestId);
    }

    public void init() {
        if (!(executorStarted)) {
            if ((maxRunningTime) > (-1)) {
                requeue(maxRunningTime);
            } 
            try {
                executor.init();
                ExecutorServiceImpl.this.executorStarted = true;
            } catch (ExecutorNotStartedException e) {
                ExecutorServiceImpl.this.executorStarted = false;
            }
        } 
    }

    public void destroy() {
        if (executorStarted) {
            ExecutorServiceFactory.resetExecutorService(ExecutorServiceImpl.this);
            ExecutorServiceImpl.this.executorStarted = false;
            executor.destroy();
        } 
    }

    public boolean isActive() {
        return executorStarted;
    }

    public int getInterval() {
        return executor.getInterval();
    }

    public void setInterval(int waitTime) {
        executor.setInterval(waitTime);
    }

    public int getRetries() {
        return executor.getRetries();
    }

    public void setRetries(int defaultNroOfRetries) {
        executor.setRetries(defaultNroOfRetries);
    }

    public int getThreadPoolSize() {
        return executor.getThreadPoolSize();
    }

    public void setThreadPoolSize(int nroOfThreads) {
        executor.setThreadPoolSize(nroOfThreads);
    }

    public TimeUnit getTimeunit() {
        return executor.getTimeunit();
    }

    public void setTimeunit(TimeUnit timeunit) {
        executor.setTimeunit(timeunit);
    }

    public List<RequestInfo> getPendingRequests() {
        return ((org.kie.internal.executor.api.ExecutorQueryService) (queryService)).getPendingRequests();
    }

    public List<RequestInfo> getPendingRequestById(Long id) {
        return queryService.getPendingRequestById(id);
    }

    public Long scheduleRequest(String commandId, Date date, CommandContext ctx) {
        return executor.scheduleRequest(commandId, date, ctx);
    }

    public List<RequestInfo> getRunningRequests() {
        return ((org.kie.internal.executor.api.ExecutorQueryService) (queryService)).getRunningRequests();
    }

    public RequestInfo getRequestById(Long requestId) {
        return queryService.getRequestById(requestId);
    }

    public List<ErrorInfo> getErrorsByRequestId(Long requestId) {
        return queryService.getErrorsByRequestId(requestId);
    }

    @Override
    public List<RequestInfo> getRequestsByBusinessKey(String businessKey, QueryContext queryContext) {
        return queryService.getRequestByBusinessKey(businessKey, queryContext);
    }

    @Override
    public List<RequestInfo> getRequestsByCommand(String command, QueryContext queryContext) {
        return queryService.getRequestByCommand(command, queryContext);
    }

    @Override
    public void requeue(Long olderThan) {
        if ((adminService) instanceof RequeueAware) {
            if (olderThan == null) {
                olderThan = maxRunningTime;
            } 
            ((RequeueAware) (adminService)).requeue(timeunit.convert(olderThan, TimeUnit.MILLISECONDS));
        } 
    }

    @Override
    public void requeueById(Long requestId) {
        if ((adminService) instanceof RequeueAware) {
            ((RequeueAware) (adminService)).requeueById(requestId);
        } 
    }

    @Override
    public List<RequestInfo> getQueuedRequests(QueryContext queryContext) {
        return queryService.getQueuedRequests(queryContext);
    }

    @Override
    public List<RequestInfo> getCompletedRequests(QueryContext queryContext) {
        return queryService.getCompletedRequests(queryContext);
    }

    @Override
    public List<RequestInfo> getInErrorRequests(QueryContext queryContext) {
        return queryService.getInErrorRequests(queryContext);
    }

    @Override
    public List<RequestInfo> getCancelledRequests(QueryContext queryContext) {
        return queryService.getCancelledRequests(queryContext);
    }

    @Override
    public List<ErrorInfo> getAllErrors(QueryContext queryContext) {
        return queryService.getAllErrors(queryContext);
    }

    @Override
    public List<RequestInfo> getAllRequests(QueryContext queryContext) {
        return queryService.getAllRequests(queryContext);
    }

    @Override
    public List<RequestInfo> getRequestsByStatus(List<STATUS> statuses, QueryContext queryContext) {
        return queryService.getRequestsByStatus(statuses, queryContext);
    }

    @Override
    public List<RequestInfo> getPendingRequests(QueryContext queryContext) {
        return queryService.getPendingRequests(queryContext);
    }

    @Override
    public List<RequestInfo> getRunningRequests(QueryContext queryContext) {
        return queryService.getRunningRequests(queryContext);
    }

    @Override
    public List<RequestInfo> getFutureQueuedRequests(QueryContext queryContext) {
        return queryService.getFutureQueuedRequests(queryContext);
    }

    public void addAsyncJobListener(AsynchronousJobListener listener) {
        ExecutorServiceImpl.this.eventSupport.addEventListener(listener);
    }

    public void removeAsyncJobListener(AsynchronousJobListener listener) {
        ExecutorServiceImpl.this.eventSupport.removeEventListener(listener);
    }

    public List<AsynchronousJobListener> getAsyncJobListeners() {
        return ExecutorServiceImpl.this.eventSupport.getEventListeners();
    }
}

