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
        // info String{"beforeTaskActivatedEvent"} to Logger{DebugTaskLifeCycleEventListener.logger}
        DebugTaskLifeCycleEventListener.logger.info("beforeTaskActivatedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void beforeTaskClaimedEvent(TaskEvent event) {
        // info String{"beforeTaskClaimedEvent"} to Logger{DebugTaskLifeCycleEventListener.logger}
        DebugTaskLifeCycleEventListener.logger.info("beforeTaskClaimedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void beforeTaskSkippedEvent(TaskEvent event) {
        // info String{"beforeTaskSkippedEvent"} to Logger{DebugTaskLifeCycleEventListener.logger}
        DebugTaskLifeCycleEventListener.logger.info("beforeTaskSkippedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void beforeTaskStartedEvent(TaskEvent event) {
        // info String{"beforeTaskStartedEvent"} to Logger{DebugTaskLifeCycleEventListener.logger}
        DebugTaskLifeCycleEventListener.logger.info("beforeTaskStartedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void beforeTaskStoppedEvent(TaskEvent event) {
        // info String{"beforeTaskStoppedEvent"} to Logger{DebugTaskLifeCycleEventListener.logger}
        DebugTaskLifeCycleEventListener.logger.info("beforeTaskStoppedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void beforeTaskCompletedEvent(TaskEvent event) {
        // info String{"beforeTaskCompletedEvent"} to Logger{DebugTaskLifeCycleEventListener.logger}
        DebugTaskLifeCycleEventListener.logger.info("beforeTaskCompletedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void beforeTaskFailedEvent(TaskEvent event) {
        // info String{"beforeTaskFailedEvent"} to Logger{DebugTaskLifeCycleEventListener.logger}
        DebugTaskLifeCycleEventListener.logger.info("beforeTaskFailedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void beforeTaskAddedEvent(TaskEvent event) {
        // info String{"beforeTaskAddedEvent"} to Logger{DebugTaskLifeCycleEventListener.logger}
        DebugTaskLifeCycleEventListener.logger.info("beforeTaskAddedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void beforeTaskExitedEvent(TaskEvent event) {
        // info String{"beforeTaskExitedEvent"} to Logger{DebugTaskLifeCycleEventListener.logger}
        DebugTaskLifeCycleEventListener.logger.info("beforeTaskExitedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void beforeTaskReleasedEvent(TaskEvent event) {
        // info String{"beforeTaskReleasedEvent"} to Logger{DebugTaskLifeCycleEventListener.logger}
        DebugTaskLifeCycleEventListener.logger.info("beforeTaskReleasedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void beforeTaskResumedEvent(TaskEvent event) {
        // info String{"beforeTaskResumedEvent"} to Logger{DebugTaskLifeCycleEventListener.logger}
        DebugTaskLifeCycleEventListener.logger.info("beforeTaskResumedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void beforeTaskSuspendedEvent(TaskEvent event) {
        // info String{"beforeTaskSuspendedEvent"} to Logger{DebugTaskLifeCycleEventListener.logger}
        DebugTaskLifeCycleEventListener.logger.info("beforeTaskSuspendedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void beforeTaskForwardedEvent(TaskEvent event) {
        // info String{"beforeTaskForwardedEvent"} to Logger{DebugTaskLifeCycleEventListener.logger}
        DebugTaskLifeCycleEventListener.logger.info("beforeTaskForwardedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void beforeTaskDelegatedEvent(TaskEvent event) {
        // info String{"beforeTaskDelegatedEvent"} to Logger{DebugTaskLifeCycleEventListener.logger}
        DebugTaskLifeCycleEventListener.logger.info("beforeTaskDelegatedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void beforeTaskNominatedEvent(TaskEvent event) {
        // info String{"beforeTaskNominatedEvent"} to Logger{DebugTaskLifeCycleEventListener.logger}
        DebugTaskLifeCycleEventListener.logger.info("beforeTaskNominatedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void afterTaskActivatedEvent(TaskEvent event) {
        // info String{"afterTaskActivatedEvent"} to Logger{DebugTaskLifeCycleEventListener.logger}
        DebugTaskLifeCycleEventListener.logger.info("afterTaskActivatedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void afterTaskClaimedEvent(TaskEvent event) {
        // info String{"afterTaskClaimedEvent"} to Logger{DebugTaskLifeCycleEventListener.logger}
        DebugTaskLifeCycleEventListener.logger.info("afterTaskClaimedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void afterTaskSkippedEvent(TaskEvent event) {
        // info String{"afterTaskSkippedEvent"} to Logger{DebugTaskLifeCycleEventListener.logger}
        DebugTaskLifeCycleEventListener.logger.info("afterTaskSkippedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void afterTaskStartedEvent(TaskEvent event) {
        // info String{"afterTaskStartedEvent"} to Logger{DebugTaskLifeCycleEventListener.logger}
        DebugTaskLifeCycleEventListener.logger.info("afterTaskStartedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void afterTaskStoppedEvent(TaskEvent event) {
        // info String{"afterTaskStoppedEvent"} to Logger{DebugTaskLifeCycleEventListener.logger}
        DebugTaskLifeCycleEventListener.logger.info("afterTaskStoppedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void afterTaskCompletedEvent(TaskEvent event) {
        // info String{"afterTaskCompletedEvent"} to Logger{DebugTaskLifeCycleEventListener.logger}
        DebugTaskLifeCycleEventListener.logger.info("afterTaskCompletedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void afterTaskFailedEvent(TaskEvent event) {
        // info String{"afterTaskFailedEvent"} to Logger{DebugTaskLifeCycleEventListener.logger}
        DebugTaskLifeCycleEventListener.logger.info("afterTaskFailedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void afterTaskAddedEvent(TaskEvent event) {
        // info String{"afterTaskAddedEvent"} to Logger{DebugTaskLifeCycleEventListener.logger}
        DebugTaskLifeCycleEventListener.logger.info("afterTaskAddedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void afterTaskExitedEvent(TaskEvent event) {
        // info String{"afterTaskExitedEvent"} to Logger{DebugTaskLifeCycleEventListener.logger}
        DebugTaskLifeCycleEventListener.logger.info("afterTaskExitedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void afterTaskReleasedEvent(TaskEvent event) {
        // info String{"afterTaskReleasedEvent"} to Logger{DebugTaskLifeCycleEventListener.logger}
        DebugTaskLifeCycleEventListener.logger.info("afterTaskReleasedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void afterTaskResumedEvent(TaskEvent event) {
        // info String{"afterTaskResumedEvent"} to Logger{DebugTaskLifeCycleEventListener.logger}
        DebugTaskLifeCycleEventListener.logger.info("afterTaskResumedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void afterTaskSuspendedEvent(TaskEvent event) {
        // info String{"afterTaskSuspendedEvent"} to Logger{DebugTaskLifeCycleEventListener.logger}
        DebugTaskLifeCycleEventListener.logger.info("afterTaskSuspendedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void afterTaskForwardedEvent(TaskEvent event) {
        // info String{"afterTaskForwardedEvent"} to Logger{DebugTaskLifeCycleEventListener.logger}
        DebugTaskLifeCycleEventListener.logger.info("afterTaskForwardedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void afterTaskDelegatedEvent(TaskEvent event) {
        // info String{"afterTaskDelegatedEvent"} to Logger{DebugTaskLifeCycleEventListener.logger}
        DebugTaskLifeCycleEventListener.logger.info("afterTaskDelegatedEvent");
        (DebugTaskLifeCycleEventListener.eventCounter)++;
    }

    @Override
    public void afterTaskNominatedEvent(TaskEvent event) {
        // info String{"afterTaskNominatedEvent"} to Logger{DebugTaskLifeCycleEventListener.logger}
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

