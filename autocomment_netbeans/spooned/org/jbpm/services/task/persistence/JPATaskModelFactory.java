/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.jbpm.services.task.persistence;

import org.kie.api.task.model.Attachment;
import org.kie.internal.task.api.model.BooleanExpression;
import org.kie.api.task.model.Comment;
import org.kie.api.task.model.Content;
import org.kie.internal.task.api.model.ContentData;
import org.kie.internal.task.api.model.Deadline;
import org.kie.internal.task.api.model.DeadlineSummary;
import org.kie.internal.task.api.model.Deadlines;
import org.kie.internal.task.api.model.Delegation;
import org.kie.internal.task.api.model.EmailNotification;
import org.kie.internal.task.api.model.EmailNotificationHeader;
import org.kie.internal.task.api.model.Escalation;
import org.kie.internal.task.api.model.FaultData;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.I18NText;
import org.kie.internal.task.api.model.Language;
import org.kie.internal.task.api.model.Notification;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.PeopleAssignments;
import org.kie.internal.task.api.model.Reassignment;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskData;
import org.kie.internal.task.api.model.TaskDef;
import org.kie.internal.task.api.TaskModelFactory;
import org.kie.api.task.model.TaskSummary;
import org.kie.api.task.model.User;

public class JPATaskModelFactory implements TaskModelFactory {
    @Override
    public Attachment newAttachment() {
        return new org.jbpm.services.task.impl.model.AttachmentImpl();
    }

    @Override
    public BooleanExpression newBooleanExpression() {
        return new org.jbpm.services.task.impl.model.BooleanExpressionImpl();
    }

    @Override
    public Comment newComment() {
        return new org.jbpm.services.task.impl.model.CommentImpl();
    }

    @Override
    public ContentData newContentData() {
        return new org.jbpm.services.task.impl.model.ContentDataImpl();
    }

    @Override
    public Content newContent() {
        return new org.jbpm.services.task.impl.model.ContentImpl();
    }

    @Override
    public Deadline newDeadline() {
        return new org.jbpm.services.task.impl.model.DeadlineImpl();
    }

    @Override
    public DeadlineSummary newDeadlineSummary() {
        return new org.jbpm.services.task.query.DeadlineSummaryImpl();
    }

    @Override
    public Deadlines newDeadlines() {
        return new org.jbpm.services.task.impl.model.DeadlinesImpl();
    }

    @Override
    public Delegation newDelegation() {
        return new org.jbpm.services.task.impl.model.DelegationImpl();
    }

    @Override
    public EmailNotificationHeader newEmailNotificationHeader() {
        return new org.jbpm.services.task.impl.model.EmailNotificationHeaderImpl();
    }

    @Override
    public EmailNotification newEmialNotification() {
        return new org.jbpm.services.task.impl.model.EmailNotificationImpl();
    }

    @Override
    public Escalation newEscalation() {
        return new org.jbpm.services.task.impl.model.EscalationImpl();
    }

    @Override
    public FaultData newFaultData() {
        return new org.jbpm.services.task.impl.model.FaultDataImpl();
    }

    @Override
    public Group newGroup() {
        return new org.jbpm.services.task.impl.model.GroupImpl();
    }

    @Override
    public Group newGroup(String id) {
        return new org.jbpm.services.task.impl.model.GroupImpl(id);
    }

    @Override
    public I18NText newI18NText() {
        return new org.jbpm.services.task.impl.model.I18NTextImpl();
    }

    @Override
    public Language newLanguage() {
        return new org.jbpm.services.task.impl.model.LanguageImpl();
    }

    @Override
    public Notification newNotification() {
        return new org.jbpm.services.task.impl.model.NotificationImpl();
    }

    @Override
    public OrganizationalEntity newOrgEntity() {
        throw new UnsupportedOperationException("OrganizationalEntity not supported");
    }

    @Override
    public PeopleAssignments newPeopleAssignments() {
        return new org.jbpm.services.task.impl.model.PeopleAssignmentsImpl();
    }

    @Override
    public Reassignment newReassignment() {
        return new org.jbpm.services.task.impl.model.ReassignmentImpl();
    }

    @Override
    public TaskData newTaskData() {
        return new org.jbpm.services.task.impl.model.TaskDataImpl();
    }

    @Override
    public TaskDef newTaskDef() {
        return new org.jbpm.services.task.impl.model.TaskDefImpl();
    }

    @Override
    public Task newTask() {
        return new org.jbpm.services.task.impl.model.TaskImpl();
    }

    @Override
    public User newUser() {
        return new org.jbpm.services.task.impl.model.UserImpl();
    }

    @Override
    public User newUser(String id) {
        return new org.jbpm.services.task.impl.model.UserImpl(id);
    }

    @Override
    public TaskSummary newTaskSummary() {
        return new org.jbpm.services.task.query.TaskSummaryImpl();
    }
}

