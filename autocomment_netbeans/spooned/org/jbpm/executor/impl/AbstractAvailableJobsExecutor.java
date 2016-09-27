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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.apache.commons.io.input.ClassLoaderObjectInputStream;
import org.kie.api.executor.Command;
import org.kie.api.executor.CommandCallback;
import org.kie.api.executor.CommandContext;
import java.util.Date;
import org.jbpm.executor.entities.ErrorInfo;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.kie.api.executor.ExecutionResults;
import org.jbpm.executor.impl.event.ExecutorEventSupport;
import org.kie.api.executor.ExecutorQueryService;
import org.kie.api.executor.ExecutorStoreService;
import java.util.HashMap;
import java.io.IOException;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.kie.api.executor.Reoccurring;
import org.jbpm.executor.entities.RequestInfo;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;

/**
 * Heart of the executor component - executes the actual tasks.
 * Handles retries and error management. Based on results of execution notifies
 * defined callbacks about the execution results.
 */
public abstract class AbstractAvailableJobsExecutor {
    private static final Logger logger = LoggerFactory.getLogger(AbstractAvailableJobsExecutor.class);

    protected int retries = Integer.parseInt(System.getProperty("org.kie.executor.retry.count", "3"));

    protected Map<String, Object> contextData = new HashMap<String, Object>();

    protected ExecutorQueryService queryService;

    protected ClassCacheManager classCacheManager;

    protected ExecutorStoreService executorStoreService;

    protected ExecutorEventSupport eventSupport = new ExecutorEventSupport();

    public void setEventSupport(ExecutorEventSupport eventSupport) {
        AbstractAvailableJobsExecutor.this.eventSupport = eventSupport;
    }

    public void setQueryService(ExecutorQueryService queryService) {
        AbstractAvailableJobsExecutor.this.queryService = queryService;
    }

    public void setClassCacheManager(ClassCacheManager classCacheManager) {
        AbstractAvailableJobsExecutor.this.classCacheManager = classCacheManager;
    }

    public void setExecutorStoreService(ExecutorStoreService executorStoreService) {
        AbstractAvailableJobsExecutor.this.executorStoreService = executorStoreService;
    }

    public void executeGivenJob(RequestInfo request) {
        Throwable exception = null;
        try {
            eventSupport.fireBeforeJobExecuted(request, null);
            if (request != null) {
                boolean processReoccurring = false;
                Command cmd = null;
                CommandContext ctx = null;
                List<CommandCallback> callbacks = null;
                ClassLoader cl = getClassLoader(request.getDeploymentId());
                try {
                    AbstractAvailableJobsExecutor.logger.debug("Processing Request Id: {}, status {} command {}", request.getId(), request.getStatus(), request.getCommandName());
                    byte[] reqData = request.getRequestData();
                    if (reqData != null) {
                        ObjectInputStream in = null;
                        try {
                            in = new ClassLoaderObjectInputStream(cl, new ByteArrayInputStream(reqData));
                            ctx = ((CommandContext) (in.readObject()));
                        } catch (IOException e) {
                            AbstractAvailableJobsExecutor.logger.warn("Exception while serializing context data", e);
                            return ;
                        } finally {
                            if (in != null) {
                                in.close();
                            } 
                        }
                    } 
                    for (Map.Entry<String, Object> entry : contextData.entrySet()) {
                        ctx.setData(entry.getKey(), entry.getValue());
                    }
                    // add class loader so internally classes can be created with valid (kjar) deployment
                    ctx.setData("ClassLoader", cl);
                    cmd = classCacheManager.findCommand(request.getCommandName(), cl);
                    ExecutionResults results = cmd.execute(ctx);
                    callbacks = classCacheManager.buildCommandCallback(ctx, cl);
                    for (CommandCallback handler : callbacks) {
                        handler.onCommandDone(ctx, results);
                    }
                    if (results != null) {
                        try {
                            ByteArrayOutputStream bout = new ByteArrayOutputStream();
                            ObjectOutputStream out = new ObjectOutputStream(bout);
                            out.writeObject(results);
                            byte[] respData = bout.toByteArray();
                            request.setResponseData(respData);
                        } catch (IOException e) {
                            request.setResponseData(null);
                        }
                    } 
                    request.setStatus(STATUS.DONE);
                    executorStoreService.updateRequest(request);
                    processReoccurring = true;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Throwable e) {
                    exception = e;
                    callbacks = classCacheManager.buildCommandCallback(ctx, cl);
                    processReoccurring = handleException(request, e, ctx, callbacks);
                } finally {
                    handleCompletion(processReoccurring, cmd, ctx);
                    eventSupport.fireAfterJobExecuted(request, exception);
                }
            } 
        } catch (Exception e) {
            AbstractAvailableJobsExecutor.logger.warn("Unexpected error while processin executor's job {}", e.getMessage(), e);
        }
    }

    protected ClassLoader getClassLoader(String deploymentId) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (deploymentId == null) {
            return cl;
        } 
        InternalRuntimeManager manager = ((InternalRuntimeManager) (RuntimeManagerRegistry.get().getManager(deploymentId)));
        if ((manager != null) && ((manager.getEnvironment().getClassLoader()) != null)) {
            cl = manager.getEnvironment().getClassLoader();
        } 
        return cl;
    }

    public void addContextData(String name, Object data) {
        AbstractAvailableJobsExecutor.this.contextData.put(name, data);
    }

    @SuppressWarnings(value = "unchecked")
    protected boolean handleException(RequestInfo request, Throwable e, CommandContext ctx, List<CommandCallback> callbacks) {
        AbstractAvailableJobsExecutor.logger.warn("Error during command {} error message {}", request.getCommandName(), e.getMessage(), e);
        ErrorInfo errorInfo = new ErrorInfo(e.getMessage(), ExceptionUtils.getStackTrace(e.fillInStackTrace()));
        errorInfo.setRequestInfo(request);
        ((List<ErrorInfo>) (request.getErrorInfo())).add(errorInfo);
        AbstractAvailableJobsExecutor.logger.debug("Error Number: {}", request.getErrorInfo().size());
        if ((request.getRetries()) > 0) {
            request.setStatus(STATUS.RETRYING);
            request.setRetries(((request.getRetries()) - 1));
            // calculate next retry time
            List<Long> retryDelay = ((List<Long>) (ctx.getData("retryDelay")));
            if (retryDelay != null) {
                long retryAdd = 0L;
                try {
                    retryAdd = retryDelay.get(request.getExecutions());
                } catch (IndexOutOfBoundsException ex) {
                    // in case there is no element matching given execution, use last one
                    retryAdd = retryDelay.get(((retryDelay.size()) - 1));
                }
                request.setTime(new Date(((System.currentTimeMillis()) + retryAdd)));
                request.setExecutions(((request.getExecutions()) + 1));
                AbstractAvailableJobsExecutor.logger.info("Retrying request ( with id {}) - delay configured, next retry at {}", request.getId(), request.getTime());
            } 
            AbstractAvailableJobsExecutor.logger.debug("Retrying ({}) still available!", request.getRetries());
            executorStoreService.updateRequest(request);
            return false;
        } else {
            AbstractAvailableJobsExecutor.logger.debug("Error no retries left!");
            request.setStatus(STATUS.ERROR);
            request.setExecutions(((request.getExecutions()) + 1));
            executorStoreService.updateRequest(request);
            if (callbacks != null) {
                for (CommandCallback handler : callbacks) {
                    handler.onCommandError(ctx, e);
                }
            } 
            return true;
        }
    }

    protected void handleCompletion(boolean processReoccurring, Command cmd, CommandContext ctx) {
        if ((processReoccurring && (cmd != null)) && (cmd instanceof Reoccurring)) {
            Date current = new Date();
            Date nextScheduleTime = ((Reoccurring) (cmd)).getScheduleTime();
            if ((nextScheduleTime != null) && (nextScheduleTime.after(current))) {
                String businessKey = ((String) (ctx.getData("businessKey")));
                RequestInfo requestInfo = new RequestInfo();
                requestInfo.setCommandName(cmd.getClass().getName());
                requestInfo.setKey(businessKey);
                requestInfo.setStatus(STATUS.QUEUED);
                requestInfo.setTime(nextScheduleTime);
                requestInfo.setMessage("Rescheduled reoccurring job");
                requestInfo.setDeploymentId(((String) (ctx.getData("deploymentId"))));
                requestInfo.setOwner(((String) (ctx.getData("owner"))));
                if ((ctx.getData("retries")) != null) {
                    requestInfo.setRetries(Integer.valueOf(String.valueOf(ctx.getData("retries"))));
                } else {
                    requestInfo.setRetries(retries);
                }
                if (ctx != null) {
                    try {
                        // remove transient data
                        ctx.getData().remove("ClassLoader");
                        ByteArrayOutputStream bout = new ByteArrayOutputStream();
                        ObjectOutputStream oout = new ObjectOutputStream(bout);
                        oout.writeObject(ctx);
                        requestInfo.setRequestData(bout.toByteArray());
                    } catch (IOException e) {
                        AbstractAvailableJobsExecutor.logger.warn("Error serializing context data", e);
                        requestInfo.setRequestData(null);
                    }
                } 
                executorStoreService.persistRequest(requestInfo);
            } 
        } 
    }
}

