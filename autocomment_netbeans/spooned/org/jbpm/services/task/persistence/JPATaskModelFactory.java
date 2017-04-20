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

import org.kie.internal.task.api.model.Deadline;
import org.kie.api.task.model.Attachment;
import org.kie.internal.task.api.model.BooleanExpression;
import org.kie.api.task.model.Comment;
import org.kie.api.task.model.Content;
import org.kie.internal.task.api.model.ContentData;
import org.kie.api.task.model.PeopleAssignments;
import org.kie.internal.task.api.model.Escalation;
import org.kie.api.task.model.TaskData;
import org.kie.internal.task.api.model.DeadlineSummary;
import org.kie.internal.task.api.model.Deadlines;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.TaskModelFactory;
import org.kie.internal.task.api.model.Reassignment;
import org.kie.internal.task.api.model.FaultData;
import org.kie.api.task.model.I18NText;
import org.kie.internal.task.api.model.TaskDef;
import org.kie.internal.task.api.model.Delegation;
import org.kie.internal.task.api.model.Language;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.task.api.model.Notification;
import org.kie.api.task.model.Group;
import org.kie.internal.task.api.model.EmailNotification;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.model.EmailNotificationHeader;

public class JPATaskModelFactory implements TaskModelFactory {
    @Override
    public Attachment newAttachment() {
        return new AttachmentImpl();
    }

    @Override
    public BooleanExpression newBooleanExpression() {
        return new BooleanExpressionImpl();
    }

    @Override
    public Comment newComment() {
        return new CommentImpl();
    }

    @Override
    public ContentData newContentData() {
        return new ContentDataImpl();
    }

    @Override
    public Content newContent() {
        return new ContentImpl();
    }

    @Override
    public Deadline newDeadline() {
        return new DeadlineImpl();
    }

    @Override
    public DeadlineSummary newDeadlineSummary() {
        return new DeadlineSummaryImpl();
    }

    @Override
    public Deadlines newDeadlines() {
        return new DeadlinesImpl();
    }

    @Override
    public Delegation newDelegation() {
        return new DelegationImpl();
    }

    @Override
    public EmailNotificationHeader newEmailNotificationHeader() {
        return new EmailNotificationHeaderImpl();
    }

    @Override
    public EmailNotification newEmialNotification() {
        return new EmailNotificationImpl();
    }

    @Override
    public Escalation newEscalation() {
        return new EscalationImpl();
    }

    @Override
    public FaultData newFaultData() {
        return new FaultDataImpl();
    }

    @Override
    public Group newGroup() {
        return new GroupImpl();
    }

    @Override
    public Group newGroup(String id) {
        return new GroupImpl(id);
    }

    @Override
    public I18NText newI18NText() {
        return new I18NTextImpl();
    }

    @Override
    public Language newLanguage() {
        return new LanguageImpl();
    }

    @Override
    public Notification newNotification() {
        return new NotificationImpl();
    }

    @Override
    public OrganizationalEntity newOrgEntity() {
        throw new UnsupportedOperationException("OrganizationalEntity not supported");
    }

    @Override
    public PeopleAssignments newPeopleAssignments() {
        return new PeopleAssignmentsImpl();
    }

    @Override
    public Reassignment newReassignment() {
        return new ReassignmentImpl();
    }

    @Override
    public TaskData newTaskData() {
        return new TaskDataImpl();
    }

    @Override
    public TaskDef newTaskDef() {
        return new TaskDefImpl();
    }

    @Override
    public Task newTask() {
        return new TaskImpl();
    }

    @Override
    public User newUser() {
        return new UserImpl();
    }

    @Override
    public User newUser(String id) {
        return new UserImpl(id);
    }

    @Override
    public TaskSummary newTaskSummary() {
        return new TaskSummaryImpl();
    }
}

