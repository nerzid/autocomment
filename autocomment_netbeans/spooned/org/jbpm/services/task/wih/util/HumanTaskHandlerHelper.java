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


package org.jbpm.services.task.wih.util;

import java.util.ArrayList;
import org.jbpm.process.core.timer.BusinessCalendar;
import java.util.Date;
import org.kie.internal.task.api.model.Deadline;
import org.kie.internal.task.api.model.Deadlines;
import org.kie.internal.task.api.model.EmailNotification;
import org.kie.internal.task.api.model.EmailNotificationHeader;
import org.kie.api.runtime.Environment;
import org.kie.internal.task.api.model.Escalation;
import org.kie.api.task.model.Group;
import java.util.HashMap;
import org.kie.api.task.model.I18NText;
import org.kie.internal.task.api.model.InternalI18NText;
import org.kie.internal.task.api.model.InternalOrganizationalEntity;
import org.kie.internal.task.api.model.Language;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.kie.internal.task.api.model.Notification;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.internal.task.api.model.Reassignment;
import org.kie.internal.task.api.TaskModelProvider;
import org.drools.core.time.TimeUtils;
import org.kie.api.task.model.User;
import org.kie.api.runtime.process.WorkItem;

public class HumanTaskHandlerHelper {
    private static final Logger logger = LoggerFactory.getLogger(HumanTaskHandlerHelper.class);

    private static final String COMPONENT_SEPARATOR = "\\^";

    private static final String ELEMENT_SEPARATOR = "@";

    private static final String ATTRIBUTES_SEPARATOR = "\\|";

    private static final String ATTRIBUTES_ELEMENTS_SEPARATOR = ",";

    private static final String KEY_VALUE_SEPARATOR = ":";

    private static final String[] KNOWN_KEYS = new String[]{ "users" , "groups" , "from" , "tousers" , "togroups" , "replyto" , "subject" , "body" };

    public static Deadlines setDeadlines(WorkItem workItem, List<OrganizationalEntity> businessAdministrators, Environment environment) {
        String notStartedReassign = ((String) (workItem.getParameter("NotStartedReassign")));
        String notStartedNotify = ((String) (workItem.getParameter("NotStartedNotify")));
        String notCompletedReassign = ((String) (workItem.getParameter("NotCompletedReassign")));
        String notCompletedNotify = ((String) (workItem.getParameter("NotCompletedNotify")));
        Deadlines deadlinesTotal = TaskModelProvider.getFactory().newDeadlines();
        List<Deadline> startDeadlines = new ArrayList<Deadline>();
        startDeadlines.addAll(HumanTaskHandlerHelper.parseDeadlineString(notStartedNotify, businessAdministrators, environment));
        startDeadlines.addAll(HumanTaskHandlerHelper.parseDeadlineString(notStartedReassign, businessAdministrators, environment));
        List<Deadline> endDeadlines = new ArrayList<Deadline>();
        endDeadlines.addAll(HumanTaskHandlerHelper.parseDeadlineString(notCompletedNotify, businessAdministrators, environment));
        endDeadlines.addAll(HumanTaskHandlerHelper.parseDeadlineString(notCompletedReassign, businessAdministrators, environment));
        if (!(startDeadlines.isEmpty())) {
            deadlinesTotal.setStartDeadlines(startDeadlines);
        } 
        if (!(endDeadlines.isEmpty())) {
            deadlinesTotal.setEndDeadlines(endDeadlines);
        } 
        return deadlinesTotal;
    }

    protected static List<Deadline> parseDeadlineString(String deadlineInfo, List<OrganizationalEntity> businessAdministrators, Environment environment) {
        if ((deadlineInfo == null) || ((deadlineInfo.length()) == 0)) {
            return new ArrayList<Deadline>();
        } 
        List<Deadline> deadlines = new ArrayList<Deadline>();
        String[] allComponents = deadlineInfo.split(HumanTaskHandlerHelper.COMPONENT_SEPARATOR);
        BusinessCalendar businessCalendar = null;
        if ((environment != null) && ((environment.get("jbpm.business.calendar")) != null)) {
            businessCalendar = ((BusinessCalendar) (environment.get("jbpm.business.calendar")));
        } 
        for (String component : allComponents) {
            String[] mainComponents = component.split(HumanTaskHandlerHelper.ELEMENT_SEPARATOR);
            if ((mainComponents != null) && ((mainComponents.length) == 2)) {
                String actionComponent = mainComponents[0].substring(1, ((mainComponents[0].length()) - 1));
                String expireComponents = mainComponents[1].substring(1, ((mainComponents[1].length()) - 1));
                String[] expireElements = expireComponents.split(HumanTaskHandlerHelper.ATTRIBUTES_ELEMENTS_SEPARATOR);
                Deadline taskDeadline = null;
                for (String expiresAt : expireElements) {
                    HumanTaskHandlerHelper.logger.debug("Expires at is {}", expiresAt);
                    taskDeadline = TaskModelProvider.getFactory().newDeadline();
                    if (businessCalendar != null) {
                        taskDeadline.setDate(businessCalendar.calculateBusinessTimeAsDate(expiresAt));
                    } else {
                        taskDeadline.setDate(new Date(((System.currentTimeMillis()) + (TimeUtils.parseTimeString(expiresAt)))));
                    }
                    HumanTaskHandlerHelper.logger.debug("Calculated date of execution is {} and current date {}", taskDeadline.getDate(), new Date());
                    List<Escalation> escalations = new ArrayList<Escalation>();
                    Escalation escalation = TaskModelProvider.getFactory().newEscalation();
                    escalations.add(escalation);
                    escalation.setName("Default escalation");
                    taskDeadline.setEscalations(escalations);
                    escalation.setReassignments(HumanTaskHandlerHelper.parseReassignment(actionComponent));
                    escalation.setNotifications(HumanTaskHandlerHelper.parseNotifications(actionComponent, businessAdministrators));
                    deadlines.add(taskDeadline);
                }
            } else {
                HumanTaskHandlerHelper.logger.warn("Incorrect syntax of deadline property {}", deadlineInfo);
            }
        }
        return deadlines;
    }

    protected static List<Notification> parseNotifications(String notificationString, List<OrganizationalEntity> businessAdministrators) {
        List<Notification> notifications = new ArrayList<Notification>();
        Map<String, String> parameters = HumanTaskHandlerHelper.asMap(notificationString);
        if ((parameters.containsKey("tousers")) || (parameters.containsKey("togroups"))) {
            String locale = parameters.get("locale");
            if (locale == null) {
                locale = "en-UK";
            } 
            EmailNotification emailNotification = TaskModelProvider.getFactory().newEmialNotification();
            notifications.add(emailNotification);
            emailNotification.setBusinessAdministrators(businessAdministrators);
            Map<Language, EmailNotificationHeader> emailHeaders = new HashMap<Language, EmailNotificationHeader>();
            List<I18NText> subjects = new ArrayList<I18NText>();
            List<I18NText> names = new ArrayList<I18NText>();
            List<OrganizationalEntity> notificationRecipients = new ArrayList<OrganizationalEntity>();
            EmailNotificationHeader emailHeader = TaskModelProvider.getFactory().newEmailNotificationHeader();
            emailHeader.setBody(parameters.get("body"));
            emailHeader.setFrom(parameters.get("from"));
            emailHeader.setReplyTo(parameters.get("replyto"));
            emailHeader.setLanguage(locale);
            emailHeader.setSubject(parameters.get("subject"));
            Language lang = TaskModelProvider.getFactory().newLanguage();
            lang.setMapkey(locale);
            emailHeaders.put(lang, emailHeader);
            I18NText subject = TaskModelProvider.getFactory().newI18NText();
            ((InternalI18NText) (subject)).setLanguage(locale);
            ((InternalI18NText) (subject)).setText(emailHeader.getSubject());
            subjects.add(subject);
            names.add(subject);
            String recipients = parameters.get("tousers");
            if ((recipients != null) && ((recipients.trim().length()) > 0)) {
                String[] recipientsIds = recipients.split(HumanTaskHandlerHelper.ATTRIBUTES_ELEMENTS_SEPARATOR);
                for (String id : recipientsIds) {
                    User user = TaskModelProvider.getFactory().newUser();
                    ((InternalOrganizationalEntity) (user)).setId(id.trim());
                    notificationRecipients.add(user);
                }
            } 
            String groupRecipients = parameters.get("togroups");
            if ((groupRecipients != null) && ((groupRecipients.trim().length()) > 0)) {
                String[] groupRecipientsIds = groupRecipients.split(HumanTaskHandlerHelper.ATTRIBUTES_ELEMENTS_SEPARATOR);
                for (String id : groupRecipientsIds) {
                    Group group = TaskModelProvider.getFactory().newGroup();
                    ((InternalOrganizationalEntity) (group)).setId(id.trim());
                    notificationRecipients.add(group);
                }
            } 
            emailNotification.setEmailHeaders(emailHeaders);
            emailNotification.setNames(names);
            emailNotification.setRecipients(notificationRecipients);
            emailNotification.setSubjects(subjects);
        } 
        return notifications;
    }

    protected static List<Reassignment> parseReassignment(String reassignString) {
        List<Reassignment> reassignments = new ArrayList<Reassignment>();
        Map<String, String> parameters = HumanTaskHandlerHelper.asMap(reassignString);
        if ((parameters.containsKey("users")) || (parameters.containsKey("groups"))) {
            Reassignment reassignment = TaskModelProvider.getFactory().newReassignment();
            List<OrganizationalEntity> reassignmentUsers = new ArrayList<OrganizationalEntity>();
            String recipients = parameters.get("users");
            if ((recipients != null) && ((recipients.trim().length()) > 0)) {
                String[] recipientsIds = recipients.split(HumanTaskHandlerHelper.ATTRIBUTES_ELEMENTS_SEPARATOR);
                for (String id : recipientsIds) {
                    User user = TaskModelProvider.getFactory().newUser();
                    ((InternalOrganizationalEntity) (user)).setId(id.trim());
                    reassignmentUsers.add(user);
                }
            } 
            recipients = parameters.get("groups");
            if ((recipients != null) && ((recipients.trim().length()) > 0)) {
                String[] recipientsIds = recipients.split(HumanTaskHandlerHelper.ATTRIBUTES_ELEMENTS_SEPARATOR);
                for (String id : recipientsIds) {
                    Group group = TaskModelProvider.getFactory().newGroup();
                    ((InternalOrganizationalEntity) (group)).setId(id.trim());
                    reassignmentUsers.add(group);
                }
            } 
            reassignment.setPotentialOwners(reassignmentUsers);
            reassignments.add(reassignment);
        } 
        return reassignments;
    }

    protected static Map<String, String> asMap(String parsableString) {
        String[] actionElements = parsableString.split(HumanTaskHandlerHelper.ATTRIBUTES_SEPARATOR);
        Map<String, String> parameters = new HashMap<String, String>();
        for (String actionElem : actionElements) {
            for (String knownKey : HumanTaskHandlerHelper.KNOWN_KEYS) {
                if (actionElem.startsWith(knownKey)) {
                    try {
                        parameters.put(knownKey, actionElem.substring(((knownKey.length()) + (HumanTaskHandlerHelper.KEY_VALUE_SEPARATOR.length()))));
                    } catch (IndexOutOfBoundsException e) {
                        parameters.put(knownKey, "");
                    }
                } 
            }
        }
        return parameters;
    }
}

