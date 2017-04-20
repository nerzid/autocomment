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
import org.jbpm.executor.entities.RequestInfo;
import org.jbpm.executor.entities.ErrorInfo;
import org.kie.api.executor.STATUS;
import org.kie.api.executor.ExecutorAdminService;
import org.drools.core.command.impl.GenericCommand;
import org.jbpm.shared.services.impl.JpaPersistenceContext;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jbpm.executor.RequeueAware;
import STATUS.QUEUED;

/**
 * Default implementation of <code>ExecutorAdminService</code> backed with JPA
 * IMPORTANT: please keep all classes from package org.jbpm.shared.services.impl as FQCN
 * inside method body to avoid exception logged by CDI when used with in memory mode
 */
public class ExecutorRequestAdminServiceImpl implements RequeueAware , ExecutorAdminService {
    private CommandService commandService;

    public ExecutorRequestAdminServiceImpl() {
    }

    public void setCommandService(CommandService commandService) {
        this.commandService = commandService;
    }

    /**
     * {@inheritDoc}
     */
    public int clearAllRequests() {
        List<RequestInfo> requests = commandService.execute(new org.jbpm.shared.services.impl.commands.QueryStringCommand<List<RequestInfo>>("select r from RequestInfo r"));
        // execute RemoveObjectCommand{new RemoveObjectCommand(requests.toArray())} to CommandService{commandService}
        commandService.execute(new RemoveObjectCommand(requests.toArray()));
        return requests.size();
    }

    /**
     * {@inheritDoc}
     */
    public int clearAllErrors() {
        List<ErrorInfo> errors = commandService.execute(new org.jbpm.shared.services.impl.commands.QueryStringCommand<List<ErrorInfo>>("select e from ErrorInfo e"));
        // execute RemoveObjectCommand{new RemoveObjectCommand(errors.toArray())} to CommandService{commandService}
        commandService.execute(new RemoveObjectCommand(errors.toArray()));
        return errors.size();
    }

    @Override
    public void requeue(Long olderThan) {
        // execute RequeueRunningJobsCommand{new ExecutorRequestAdminServiceImpl.RequeueRunningJobsCommand(olderThan)} to CommandService{commandService}
        commandService.execute(new ExecutorRequestAdminServiceImpl.RequeueRunningJobsCommand(olderThan));
    }

    @Override
    public void requeueById(Long requestId) {
        // execute RequeueRunningJobCommand{new ExecutorRequestAdminServiceImpl.RequeueRunningJobCommand(requestId)} to CommandService{commandService}
        commandService.execute(new ExecutorRequestAdminServiceImpl.RequeueRunningJobCommand(requestId));
    }

    private class RequeueRunningJobsCommand implements GenericCommand<Void> {
        private Logger logger = LoggerFactory.getLogger(ExecutorRequestAdminServiceImpl.RequeueRunningJobsCommand.class);

        private static final long serialVersionUID = 8670412133363766161L;

        private Long upperLimitTime;

        public RequeueRunningJobsCommand(Long maxRunningTime) {
            this.upperLimitTime = (System.currentTimeMillis()) - maxRunningTime;
        }

        @SuppressWarnings(value = "unchecked")
        @Override
        public Void execute(Context context) {
            List<RequestInfo> requests = null;
            try {
                JpaPersistenceContext ctx = ((JpaPersistenceContext) (context));
                requests = ctx.queryInTransaction("RunningRequests", List.class);
                for (RequestInfo request : requests) {
                    if ((request != null) && (maxRunningTimeExceeded(request.getTime()))) {
                        logger.info("Requeing request as the time exceeded for its running state id : {}, key : {}, start time : {}, max time {}", request.getId(), request.getKey(), request.getTime(), new Date(upperLimitTime));
                        request.setStatus(QUEUED);
                        ctx.merge(request);
                    }
                }
            } catch (Exception e) {
                logger.warn("Error while trying to requeue jobs that runs for too long {}", e.getMessage());
            }
            return null;
        }

        private boolean maxRunningTimeExceeded(Date actualDate) {
            if ((actualDate.getTime()) < (upperLimitTime)) {
                return true;
            }
            return false;
        }
    }

    private class RequeueRunningJobCommand implements GenericCommand<Void> {
        private Logger logger = LoggerFactory.getLogger(ExecutorRequestAdminServiceImpl.RequeueRunningJobCommand.class);

        private static final long serialVersionUID = 8670412133363766161L;

        private Long requestId;

        public RequeueRunningJobCommand(Long id) {
            this.requestId = id;
        }

        @Override
        public Void execute(Context context) {
            try {
                JpaPersistenceContext ctx = ((JpaPersistenceContext) (context));
                RequestInfo request = ctx.find(RequestInfo.class, requestId);
                if (((request != null) && ((request.getStatus()) != (STATUS.CANCELLED))) && ((request.getStatus()) != (STATUS.DONE))) {
                    logger.info("Requeing request with id : {}, key : {}, start time : {}", request.getId(), request.getKey(), request.getTime());
                    request.setStatus(QUEUED);
                    ctx.merge(request);
                }else {
                    throw new IllegalArgumentException((("Retrying completed or cancelled job is not allowed (job id " + (requestId)) + ")"));
                }
            } catch (Exception e) {
                logger.warn("Error while trying to requeue jobs that runs for too long {}", e.getMessage());
            }
            return null;
        }
    }
}

