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


package org.jbpm.services.cdi.test.ext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.kie.api.task.TaskEvent;
import org.kie.api.task.TaskLifeCycleEventListener;

public class DebugTaskLifeCycleEventListener implements TaskLifeCycleEventListener {
    private static final Logger logger = LoggerFactory.getLogger(DebugTaskLifeCycleEventListener.class);

    private static int eventCounter = 0;

    @Override
    public void beforeTaskActivatedEvent(TaskEvent event) {
        DebugTaskLifeCycleEventListener.logger.info("beforeTaskActivatedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void beforeTaskClaimedEvent(TaskEvent event) {
        DebugTaskLifeCycleEventListener.logger.info("beforeTaskClaimedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void beforeTaskSkippedEvent(TaskEvent event) {
        DebugTaskLifeCycleEventListener.logger.info("beforeTaskSkippedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void beforeTaskStartedEvent(TaskEvent event) {
        DebugTaskLifeCycleEventListener.logger.info("beforeTaskStartedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void beforeTaskStoppedEvent(TaskEvent event) {
        DebugTaskLifeCycleEventListener.logger.info("beforeTaskStoppedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void beforeTaskCompletedEvent(TaskEvent event) {
        DebugTaskLifeCycleEventListener.logger.info("beforeTaskCompletedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void beforeTaskFailedEvent(TaskEvent event) {
        DebugTaskLifeCycleEventListener.logger.info("beforeTaskFailedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void beforeTaskAddedEvent(TaskEvent event) {
        DebugTaskLifeCycleEventListener.logger.info("beforeTaskAddedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void beforeTaskExitedEvent(TaskEvent event) {
        DebugTaskLifeCycleEventListener.logger.info("beforeTaskExitedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void beforeTaskReleasedEvent(TaskEvent event) {
        DebugTaskLifeCycleEventListener.logger.info("beforeTaskReleasedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void beforeTaskResumedEvent(TaskEvent event) {
        DebugTaskLifeCycleEventListener.logger.info("beforeTaskResumedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void beforeTaskSuspendedEvent(TaskEvent event) {
        DebugTaskLifeCycleEventListener.logger.info("beforeTaskSuspendedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void beforeTaskForwardedEvent(TaskEvent event) {
        DebugTaskLifeCycleEventListener.logger.info("beforeTaskForwardedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void beforeTaskDelegatedEvent(TaskEvent event) {
        DebugTaskLifeCycleEventListener.logger.info("beforeTaskDelegatedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void beforeTaskNominatedEvent(TaskEvent event) {
        DebugTaskLifeCycleEventListener.logger.info("beforeTaskNominatedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void afterTaskActivatedEvent(TaskEvent event) {
        DebugTaskLifeCycleEventListener.logger.info("afterTaskActivatedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void afterTaskClaimedEvent(TaskEvent event) {
        DebugTaskLifeCycleEventListener.logger.info("afterTaskClaimedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void afterTaskSkippedEvent(TaskEvent event) {
        DebugTaskLifeCycleEventListener.logger.info("afterTaskSkippedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void afterTaskStartedEvent(TaskEvent event) {
        DebugTaskLifeCycleEventListener.logger.info("afterTaskStartedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void afterTaskStoppedEvent(TaskEvent event) {
        DebugTaskLifeCycleEventListener.logger.info("afterTaskStoppedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void afterTaskCompletedEvent(TaskEvent event) {
        DebugTaskLifeCycleEventListener.logger.info("afterTaskCompletedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void afterTaskFailedEvent(TaskEvent event) {
        DebugTaskLifeCycleEventListener.logger.info("afterTaskFailedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void afterTaskAddedEvent(TaskEvent event) {
        DebugTaskLifeCycleEventListener.logger.info("afterTaskAddedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void afterTaskExitedEvent(TaskEvent event) {
        DebugTaskLifeCycleEventListener.logger.info("afterTaskExitedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void afterTaskReleasedEvent(TaskEvent event) {
        DebugTaskLifeCycleEventListener.logger.info("afterTaskReleasedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void afterTaskResumedEvent(TaskEvent event) {
        DebugTaskLifeCycleEventListener.logger.info("afterTaskResumedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void afterTaskSuspendedEvent(TaskEvent event) {
        DebugTaskLifeCycleEventListener.logger.info("afterTaskSuspendedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void afterTaskForwardedEvent(TaskEvent event) {
        DebugTaskLifeCycleEventListener.logger.info("afterTaskForwardedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void afterTaskDelegatedEvent(TaskEvent event) {
        DebugTaskLifeCycleEventListener.logger.info("afterTaskDelegatedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void afterTaskNominatedEvent(TaskEvent event) {
        DebugTaskLifeCycleEventListener.logger.info("afterTaskNominatedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    public static int getEventCounter() {
        return DebugTaskLifeCycleEventListener.eventCounter;
    }

    public static void resetEventCounter() {
        DebugTaskLifeCycleEventListener.eventCounter = 0;
    }
}

