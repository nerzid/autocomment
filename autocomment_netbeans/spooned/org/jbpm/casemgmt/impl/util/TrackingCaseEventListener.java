/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.casemgmt.impl.util;

import org.jbpm.casemgmt.api.event.CaseRoleAssignmentEvent;
import org.jbpm.casemgmt.api.event.CaseCancelEvent;
import org.jbpm.casemgmt.api.event.CaseCommentEvent;
import org.jbpm.casemgmt.api.event.CaseDataEvent;
import org.jbpm.casemgmt.api.event.CaseDestroyEvent;
import org.jbpm.casemgmt.api.event.CaseDynamicSubprocessEvent;
import org.jbpm.casemgmt.api.event.CaseDynamicTaskEvent;
import org.jbpm.casemgmt.api.event.CaseEventListener;
import org.jbpm.casemgmt.api.event.CaseStartEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrackingCaseEventListener implements CaseEventListener {
    private static final Logger logger = LoggerFactory.getLogger(TrackingCaseEventListener.class);

    @Override
    public void beforeCaseStarted(CaseStartEvent event) {
        // debug String{"Before case started {}"} to Logger{TrackingCaseEventListener.logger}
        TrackingCaseEventListener.logger.debug("Before case started {}", event);
    }

    @Override
    public void afterCaseStarted(CaseStartEvent event) {
        // debug String{"After case started {}"} to Logger{TrackingCaseEventListener.logger}
        TrackingCaseEventListener.logger.debug("After case started {}", event);
    }

    @Override
    public void beforeCaseCancelled(CaseCancelEvent event) {
        // debug String{"Before case cancelled {}"} to Logger{TrackingCaseEventListener.logger}
        TrackingCaseEventListener.logger.debug("Before case cancelled {}", event);
    }

    @Override
    public void afterCaseCancelled(CaseCancelEvent event) {
        // debug String{"After case cancelled {}"} to Logger{TrackingCaseEventListener.logger}
        TrackingCaseEventListener.logger.debug("After case cancelled {}", event);
    }

    @Override
    public void beforeCaseDestroyed(CaseDestroyEvent event) {
        // debug String{"Before case destroyed {}"} to Logger{TrackingCaseEventListener.logger}
        TrackingCaseEventListener.logger.debug("Before case destroyed {}", event);
    }

    @Override
    public void afterCaseDestroyed(CaseDestroyEvent event) {
        // debug String{"After case destroyed {}"} to Logger{TrackingCaseEventListener.logger}
        TrackingCaseEventListener.logger.debug("After case destroyed {}", event);
    }

    @Override
    public void beforeCaseCommentAdded(CaseCommentEvent event) {
        // debug String{"Before case comment added {}"} to Logger{TrackingCaseEventListener.logger}
        TrackingCaseEventListener.logger.debug("Before case comment added {}", event);
    }

    @Override
    public void afterCaseCommentAdded(CaseCommentEvent event) {
        // debug String{"After case comment added {}"} to Logger{TrackingCaseEventListener.logger}
        TrackingCaseEventListener.logger.debug("After case comment added {}", event);
    }

    @Override
    public void beforeCaseCommentUpdated(CaseCommentEvent event) {
        // debug String{"Before case comment updated {}"} to Logger{TrackingCaseEventListener.logger}
        TrackingCaseEventListener.logger.debug("Before case comment updated {}", event);
    }

    @Override
    public void afterCaseCommentUpdated(CaseCommentEvent event) {
        // debug String{"After case comment updated {}"} to Logger{TrackingCaseEventListener.logger}
        TrackingCaseEventListener.logger.debug("After case comment updated {}", event);
    }

    @Override
    public void beforeCaseCommentRemoved(CaseCommentEvent event) {
        // debug String{"Before case comment removed {}"} to Logger{TrackingCaseEventListener.logger}
        TrackingCaseEventListener.logger.debug("Before case comment removed {}", event);
    }

    @Override
    public void afterCaseCommentRemoved(CaseCommentEvent event) {
        // debug String{"After case comment removed {}"} to Logger{TrackingCaseEventListener.logger}
        TrackingCaseEventListener.logger.debug("After case comment removed {}", event);
    }

    @Override
    public void beforeCaseRoleAssignmentAdded(CaseRoleAssignmentEvent event) {
        // debug String{"Before case role assignment added {}"} to Logger{TrackingCaseEventListener.logger}
        TrackingCaseEventListener.logger.debug("Before case role assignment added {}", event);
    }

    @Override
    public void afterCaseRoleAssignmentAdded(CaseRoleAssignmentEvent event) {
        // debug String{"After case role assignment added {}"} to Logger{TrackingCaseEventListener.logger}
        TrackingCaseEventListener.logger.debug("After case role assignment added {}", event);
    }

    @Override
    public void beforeCaseRoleAssignmentRemoved(CaseRoleAssignmentEvent event) {
        // debug String{"Before case role assignment removed {}"} to Logger{TrackingCaseEventListener.logger}
        TrackingCaseEventListener.logger.debug("Before case role assignment removed {}", event);
    }

    @Override
    public void afterCaseRoleAssignmentRemoved(CaseRoleAssignmentEvent event) {
        // debug String{"After case role assignment removed {}"} to Logger{TrackingCaseEventListener.logger}
        TrackingCaseEventListener.logger.debug("After case role assignment removed {}", event);
    }

    @Override
    public void beforeCaseDataAdded(CaseDataEvent event) {
        // debug String{"Before case data added {}"} to Logger{TrackingCaseEventListener.logger}
        TrackingCaseEventListener.logger.debug("Before case data added {}", event);
    }

    @Override
    public void afterCaseDataAdded(CaseDataEvent event) {
        // debug String{"After case data added {}"} to Logger{TrackingCaseEventListener.logger}
        TrackingCaseEventListener.logger.debug("After case data added {}", event);
    }

    @Override
    public void beforeCaseDataRemoved(CaseDataEvent event) {
        // debug String{"Before case case data removed {}"} to Logger{TrackingCaseEventListener.logger}
        TrackingCaseEventListener.logger.debug("Before case case data removed {}", event);
    }

    @Override
    public void afterCaseDataRemoved(CaseDataEvent event) {
        // debug String{"After case case data removed {}"} to Logger{TrackingCaseEventListener.logger}
        TrackingCaseEventListener.logger.debug("After case case data removed {}", event);
    }

    @Override
    public void beforeDynamicTaskAdded(CaseDynamicTaskEvent event) {
        // debug String{"Before dynamic task added {}"} to Logger{TrackingCaseEventListener.logger}
        TrackingCaseEventListener.logger.debug("Before dynamic task added {}", event);
    }

    @Override
    public void afterDynamicTaskAdded(CaseDynamicTaskEvent event) {
        // debug String{"After dynamic task added {}"} to Logger{TrackingCaseEventListener.logger}
        TrackingCaseEventListener.logger.debug("After dynamic task added {}", event);
    }

    @Override
    public void beforeDynamicProcessAdded(CaseDynamicSubprocessEvent event) {
        // debug String{"Before dynamic subprocess added {}"} to Logger{TrackingCaseEventListener.logger}
        TrackingCaseEventListener.logger.debug("Before dynamic subprocess added {}", event);
    }

    @Override
    public void afterDynamicProcessAdded(CaseDynamicSubprocessEvent event) {
        // debug String{"After dynamic subprocess added {}"} to Logger{TrackingCaseEventListener.logger}
        TrackingCaseEventListener.logger.debug("After dynamic subprocess added {}", event);
    }
}

