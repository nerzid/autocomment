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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jbpm.executor.entities.RequestInfo;

/**
 * Heart of the executor component - executes the actual tasks.
 * Handles retries and error management. Based on results of execution notifies
 * defined callbacks about the execution results.
 */
public class AvailableJobsExecutor extends AbstractAvailableJobsExecutor {
    private static final Logger logger = LoggerFactory.getLogger(AvailableJobsExecutor.class);

    public void executeJob() {
        // debug String{"Executor Thread {} Waking Up!!!"} to Logger{AvailableJobsExecutor.logger}
        AvailableJobsExecutor.logger.debug("Executor Thread {} Waking Up!!!", this.toString());
        try {
            RequestInfo request = ((RequestInfo) (queryService.getRequestForProcessing()));
            if (request != null) {
                executeGivenJob(request);
            }
        } catch (Exception e) {
            AvailableJobsExecutor.logger.warn("Unexpected error while processin executor's job {}", e.getMessage(), e);
        }
    }
}

