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

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Assert;
import java.math.BigDecimal;
import java.util.Collections;
import org.kie.internal.task.api.model.Deadlines;
import org.kie.internal.task.api.model.EmailNotification;
import org.kie.internal.task.api.model.EmailNotificationHeader;
import org.kie.internal.task.api.model.Language;
import org.kie.internal.task.api.model.Notification;
import org.kie.internal.task.api.model.Reassignment;
import org.kie.internal.task.api.TaskModelProvider;
import org.junit.Test;
import org.drools.core.process.instance.WorkItem;

public class HumanTaskHandlerHelperTest extends AbstractBaseTest {
    @Test
    public void testSetDeadlinesNotStartedReassign() {
        WorkItem workItem = new org.drools.core.process.instance.impl.WorkItemImpl();
        workItem.setParameter("NotStartedReassign", "[users:john]@[4h]");
        @SuppressWarnings(value = "unchecked")
        Deadlines deadlines = HumanTaskHandlerHelper.setDeadlines(workItem, Collections.EMPTY_LIST, null);
        Assert.assertNotNull(deadlines);
        Assert.assertEquals(1, deadlines.getStartDeadlines().size());
        Assert.assertEquals(0, deadlines.getEndDeadlines().size());
        Assert.assertEquals(1, deadlines.getStartDeadlines().get(0).getEscalations().size());
        Assert.assertEquals(1, deadlines.getStartDeadlines().get(0).getEscalations().get(0).getReassignments().size());
        Assert.assertEquals(0, deadlines.getStartDeadlines().get(0).getEscalations().get(0).getNotifications().size());
        // verify reassignment
        Reassignment reassignment = deadlines.getStartDeadlines().get(0).getEscalations().get(0).getReassignments().get(0);
        Assert.assertEquals(1, reassignment.getPotentialOwners().size());
        Assert.assertEquals("john", reassignment.getPotentialOwners().get(0).getId());
        // check deadline expiration time
        Assert.assertNotNull(deadlines.getStartDeadlines().get(0).getDate());
        long expirationTime = (deadlines.getStartDeadlines().get(0).getDate().getTime()) - (System.currentTimeMillis());
        Assert.assertEquals(4, roundExpirationTime(expirationTime));
    }

    @Test
    public void testSetDeadlinesNotStartedReassignWithGroups() {
        WorkItem workItem = new org.drools.core.process.instance.impl.WorkItemImpl();
        workItem.setParameter("NotStartedReassign", "[users:john|groups:sales]@[4h]");
        @SuppressWarnings(value = "unchecked")
        Deadlines deadlines = HumanTaskHandlerHelper.setDeadlines(workItem, Collections.EMPTY_LIST, null);
        Assert.assertNotNull(deadlines);
        Assert.assertEquals(1, deadlines.getStartDeadlines().size());
        Assert.assertEquals(0, deadlines.getEndDeadlines().size());
        Assert.assertEquals(1, deadlines.getStartDeadlines().get(0).getEscalations().size());
        Assert.assertEquals(1, deadlines.getStartDeadlines().get(0).getEscalations().get(0).getReassignments().size());
        Assert.assertEquals(0, deadlines.getStartDeadlines().get(0).getEscalations().get(0).getNotifications().size());
        // verify reassignment
        Reassignment reassignment = deadlines.getStartDeadlines().get(0).getEscalations().get(0).getReassignments().get(0);
        Assert.assertEquals(2, reassignment.getPotentialOwners().size());
        Assert.assertEquals("john", reassignment.getPotentialOwners().get(0).getId());
        Assert.assertEquals("sales", reassignment.getPotentialOwners().get(1).getId());
        // check deadline expiration time
        Assert.assertNotNull(deadlines.getStartDeadlines().get(0).getDate());
        long expirationTime = (deadlines.getStartDeadlines().get(0).getDate().getTime()) - (System.currentTimeMillis());
        Assert.assertEquals(4, roundExpirationTime(expirationTime));
    }

    @Test
    public void testSetDeadlinesNotStartedReassignTwoTimes() {
        WorkItem workItem = new org.drools.core.process.instance.impl.WorkItemImpl();
        workItem.setParameter("NotStartedReassign", "[users:john]@[4h,6h]");
        @SuppressWarnings(value = "unchecked")
        Deadlines deadlines = HumanTaskHandlerHelper.setDeadlines(workItem, Collections.EMPTY_LIST, null);
        Assert.assertNotNull(deadlines);
        Assert.assertEquals(2, deadlines.getStartDeadlines().size());
        Assert.assertEquals(0, deadlines.getEndDeadlines().size());
        Assert.assertEquals(1, deadlines.getStartDeadlines().get(0).getEscalations().size());
        Assert.assertEquals(1, deadlines.getStartDeadlines().get(0).getEscalations().get(0).getReassignments().size());
        Assert.assertEquals(0, deadlines.getStartDeadlines().get(0).getEscalations().get(0).getNotifications().size());
        Assert.assertEquals(1, deadlines.getStartDeadlines().get(1).getEscalations().size());
        Assert.assertEquals(1, deadlines.getStartDeadlines().get(1).getEscalations().get(0).getReassignments().size());
        Assert.assertEquals(0, deadlines.getStartDeadlines().get(1).getEscalations().get(0).getNotifications().size());
        // verify reassignment
        Reassignment reassignment = deadlines.getStartDeadlines().get(0).getEscalations().get(0).getReassignments().get(0);
        Assert.assertEquals(1, reassignment.getPotentialOwners().size());
        Assert.assertEquals("john", reassignment.getPotentialOwners().get(0).getId());
        // check deadline expiration time
        Assert.assertNotNull(deadlines.getStartDeadlines().get(0).getDate());
        long expirationTime = (deadlines.getStartDeadlines().get(0).getDate().getTime()) - (System.currentTimeMillis());
        Assert.assertEquals(4, roundExpirationTime(expirationTime));
        // verify reassignment
        reassignment = deadlines.getStartDeadlines().get(1).getEscalations().get(0).getReassignments().get(0);
        Assert.assertEquals(1, reassignment.getPotentialOwners().size());
        Assert.assertEquals("john", reassignment.getPotentialOwners().get(0).getId());
        // check deadline expiration time
        Assert.assertNotNull(deadlines.getStartDeadlines().get(1).getDate());
        expirationTime = (deadlines.getStartDeadlines().get(1).getDate().getTime()) - (System.currentTimeMillis());
        Assert.assertEquals(6, roundExpirationTime(expirationTime));
    }

    @Test
    public void testSetDeadlinesNotCompletedReassign() {
        WorkItem workItem = new org.drools.core.process.instance.impl.WorkItemImpl();
        workItem.setParameter("NotCompletedReassign", "[users:john]@[4h]");
        @SuppressWarnings(value = "unchecked")
        Deadlines deadlines = HumanTaskHandlerHelper.setDeadlines(workItem, Collections.EMPTY_LIST, null);
        Assert.assertNotNull(deadlines);
        Assert.assertEquals(1, deadlines.getEndDeadlines().size());
        Assert.assertEquals(0, deadlines.getStartDeadlines().size());
        Assert.assertEquals(1, deadlines.getEndDeadlines().get(0).getEscalations().size());
        Assert.assertEquals(1, deadlines.getEndDeadlines().get(0).getEscalations().get(0).getReassignments().size());
        Assert.assertEquals(0, deadlines.getEndDeadlines().get(0).getEscalations().get(0).getNotifications().size());
        // verify reassignment
        Reassignment reassignment = deadlines.getEndDeadlines().get(0).getEscalations().get(0).getReassignments().get(0);
        Assert.assertEquals(1, reassignment.getPotentialOwners().size());
        Assert.assertEquals("john", reassignment.getPotentialOwners().get(0).getId());
        // check deadline expiration time
        Assert.assertNotNull(deadlines.getEndDeadlines().get(0).getDate());
        long expirationTime = (deadlines.getEndDeadlines().get(0).getDate().getTime()) - (System.currentTimeMillis());
        Assert.assertEquals(4, roundExpirationTime(expirationTime));
    }

    // JBPM-4291
    @Test
    public void testSetDeadlinesNotCompletedReassignWithGroups() {
        WorkItem workItem = new org.drools.core.process.instance.impl.WorkItemImpl();
        workItem.setParameter("NotCompletedReassign", "[users:john|groups:sales]@[4h]");
        @SuppressWarnings(value = "unchecked")
        Deadlines deadlines = HumanTaskHandlerHelper.setDeadlines(workItem, Collections.EMPTY_LIST, null);
        Assert.assertNotNull(deadlines);
        Assert.assertEquals(1, deadlines.getEndDeadlines().size());
        Assert.assertEquals(0, deadlines.getStartDeadlines().size());
        Assert.assertEquals(1, deadlines.getEndDeadlines().get(0).getEscalations().size());
        Assert.assertEquals(1, deadlines.getEndDeadlines().get(0).getEscalations().get(0).getReassignments().size());
        Assert.assertEquals(0, deadlines.getEndDeadlines().get(0).getEscalations().get(0).getNotifications().size());
        // verify reassignment
        Reassignment reassignment = deadlines.getEndDeadlines().get(0).getEscalations().get(0).getReassignments().get(0);
        Assert.assertEquals(2, reassignment.getPotentialOwners().size());
        Assert.assertEquals("john", reassignment.getPotentialOwners().get(0).getId());
        Assert.assertEquals("sales", reassignment.getPotentialOwners().get(1).getId());
        // check deadline expiration time
        Assert.assertNotNull(deadlines.getEndDeadlines().get(0).getDate());
        long expirationTime = (deadlines.getEndDeadlines().get(0).getDate().getTime()) - (System.currentTimeMillis());
        Assert.assertEquals(4, roundExpirationTime(expirationTime));
    }

    @Test
    public void testSetDeadlinesNotCompletedReassignTwoTimes() {
        WorkItem workItem = new org.drools.core.process.instance.impl.WorkItemImpl();
        workItem.setParameter("NotCompletedReassign", "[users:john]@[4h,6h]");
        @SuppressWarnings(value = "unchecked")
        Deadlines deadlines = HumanTaskHandlerHelper.setDeadlines(workItem, Collections.EMPTY_LIST, null);
        Assert.assertNotNull(deadlines);
        Assert.assertEquals(2, deadlines.getEndDeadlines().size());
        Assert.assertEquals(0, deadlines.getStartDeadlines().size());
        Assert.assertEquals(1, deadlines.getEndDeadlines().get(0).getEscalations().size());
        Assert.assertEquals(1, deadlines.getEndDeadlines().get(0).getEscalations().get(0).getReassignments().size());
        Assert.assertEquals(0, deadlines.getEndDeadlines().get(0).getEscalations().get(0).getNotifications().size());
        Assert.assertEquals(1, deadlines.getEndDeadlines().get(1).getEscalations().size());
        Assert.assertEquals(1, deadlines.getEndDeadlines().get(1).getEscalations().get(0).getReassignments().size());
        Assert.assertEquals(0, deadlines.getEndDeadlines().get(1).getEscalations().get(0).getNotifications().size());
        // verify reassignment
        Reassignment reassignment = deadlines.getEndDeadlines().get(0).getEscalations().get(0).getReassignments().get(0);
        Assert.assertEquals(1, reassignment.getPotentialOwners().size());
        Assert.assertEquals("john", reassignment.getPotentialOwners().get(0).getId());
        // check deadline expiration time
        Assert.assertNotNull(deadlines.getEndDeadlines().get(0).getDate());
        long expirationTime = (deadlines.getEndDeadlines().get(0).getDate().getTime()) - (System.currentTimeMillis());
        Assert.assertEquals(4, roundExpirationTime(expirationTime));
        // verify reassignment
        reassignment = deadlines.getEndDeadlines().get(1).getEscalations().get(0).getReassignments().get(0);
        Assert.assertEquals(1, reassignment.getPotentialOwners().size());
        Assert.assertEquals("john", reassignment.getPotentialOwners().get(0).getId());
        // check deadline expiration time
        Assert.assertNotNull(deadlines.getEndDeadlines().get(1).getDate());
        expirationTime = (deadlines.getEndDeadlines().get(1).getDate().getTime()) - (System.currentTimeMillis());
        Assert.assertEquals(6, roundExpirationTime(expirationTime));
    }

    @Test
    public void testNotStartedNotifyMinimal() {
        WorkItem workItem = new org.drools.core.process.instance.impl.WorkItemImpl();
        workItem.setParameter("NotStartedNotify", "[tousers:john|subject:Test of notification|body:And here is the body]@[4h]");
        @SuppressWarnings(value = "unchecked")
        Deadlines deadlines = HumanTaskHandlerHelper.setDeadlines(workItem, Collections.EMPTY_LIST, null);
        Assert.assertNotNull(deadlines);
        Assert.assertEquals(1, deadlines.getStartDeadlines().size());
        Assert.assertEquals(0, deadlines.getEndDeadlines().size());
        Assert.assertEquals(1, deadlines.getStartDeadlines().get(0).getEscalations().size());
        Assert.assertEquals(1, deadlines.getStartDeadlines().get(0).getEscalations().get(0).getNotifications().size());
        Assert.assertEquals(0, deadlines.getStartDeadlines().get(0).getEscalations().get(0).getReassignments().size());
        // verify notification
        Notification notification = deadlines.getStartDeadlines().get(0).getEscalations().get(0).getNotifications().get(0);
        Assert.assertNotNull(notification);
        Assert.assertEquals(1, notification.getRecipients().size());
        Assert.assertEquals("john", notification.getRecipients().get(0).getId());
        Assert.assertEquals(1, notification.getSubjects().size());
        Assert.assertEquals("Test of notification", notification.getSubjects().get(0).getText());
        EmailNotification emailNotification = ((EmailNotification) (notification));
        Assert.assertEquals(1, emailNotification.getEmailHeaders().size());
        Language lang = TaskModelProvider.getFactory().newLanguage();
        lang.setMapkey("en-UK");
        EmailNotificationHeader header = emailNotification.getEmailHeaders().get(lang);
        Assert.assertNotNull(header);
        Assert.assertEquals("Test of notification", header.getSubject());
        Assert.assertEquals("And here is the body", header.getBody());
        // check deadline expiration time
        Assert.assertNotNull(deadlines.getStartDeadlines().get(0).getDate());
        long expirationTime = (deadlines.getStartDeadlines().get(0).getDate().getTime()) - (System.currentTimeMillis());
        Assert.assertEquals(4, roundExpirationTime(expirationTime));
    }

    @Test
    public void testNotStartedNotifyAllElements() {
        WorkItem workItem = new org.drools.core.process.instance.impl.WorkItemImpl();
        workItem.setParameter("NotStartedNotify", "[from:mike|tousers:john,mary|togroups:sales,hr|replyto:mike|subject:Test of notification|body:And here is the body]@[4h]");
        @SuppressWarnings(value = "unchecked")
        Deadlines deadlines = HumanTaskHandlerHelper.setDeadlines(workItem, Collections.EMPTY_LIST, null);
        Assert.assertNotNull(deadlines);
        Assert.assertEquals(1, deadlines.getStartDeadlines().size());
        Assert.assertEquals(0, deadlines.getEndDeadlines().size());
        Assert.assertEquals(1, deadlines.getStartDeadlines().get(0).getEscalations().size());
        Assert.assertEquals(1, deadlines.getStartDeadlines().get(0).getEscalations().get(0).getNotifications().size());
        Assert.assertEquals(0, deadlines.getStartDeadlines().get(0).getEscalations().get(0).getReassignments().size());
        // verify notification
        Notification notification = deadlines.getStartDeadlines().get(0).getEscalations().get(0).getNotifications().get(0);
        Assert.assertNotNull(notification);
        Assert.assertEquals(4, notification.getRecipients().size());
        Assert.assertEquals("john", notification.getRecipients().get(0).getId());
        Assert.assertEquals("mary", notification.getRecipients().get(1).getId());
        Assert.assertEquals("sales", notification.getRecipients().get(2).getId());
        Assert.assertEquals("hr", notification.getRecipients().get(3).getId());
        Assert.assertEquals(1, notification.getSubjects().size());
        Assert.assertEquals("Test of notification", notification.getSubjects().get(0).getText());
        EmailNotification emailNotification = ((EmailNotification) (notification));
        Assert.assertEquals(1, emailNotification.getEmailHeaders().size());
        Language lang = TaskModelProvider.getFactory().newLanguage();
        lang.setMapkey("en-UK");
        EmailNotificationHeader header = emailNotification.getEmailHeaders().get(lang);
        Assert.assertNotNull(header);
        Assert.assertEquals("Test of notification", header.getSubject());
        Assert.assertEquals("And here is the body", header.getBody());
        Assert.assertEquals("mike", header.getFrom());
        Assert.assertEquals("mike", header.getReplyTo());
        // check deadline expiration time
        Assert.assertNotNull(deadlines.getStartDeadlines().get(0).getDate());
        long expirationTime = (deadlines.getStartDeadlines().get(0).getDate().getTime()) - (System.currentTimeMillis());
        Assert.assertEquals(4, roundExpirationTime(expirationTime));
    }

    @Test
    public void testNotStartedNotifyMinimalMultipleExpirations() {
        WorkItem workItem = new org.drools.core.process.instance.impl.WorkItemImpl();
        workItem.setParameter("NotStartedNotify", "[tousers:john|subject:Test of notification|body:And here is the body]@[4h,6h]");
        @SuppressWarnings(value = "unchecked")
        Deadlines deadlines = HumanTaskHandlerHelper.setDeadlines(workItem, Collections.EMPTY_LIST, null);
        Assert.assertNotNull(deadlines);
        Assert.assertEquals(2, deadlines.getStartDeadlines().size());
        Assert.assertEquals(0, deadlines.getEndDeadlines().size());
        Assert.assertEquals(1, deadlines.getStartDeadlines().get(0).getEscalations().size());
        Assert.assertEquals(1, deadlines.getStartDeadlines().get(0).getEscalations().get(0).getNotifications().size());
        Assert.assertEquals(0, deadlines.getStartDeadlines().get(0).getEscalations().get(0).getReassignments().size());
        // verify notification
        Notification notification = deadlines.getStartDeadlines().get(0).getEscalations().get(0).getNotifications().get(0);
        Assert.assertNotNull(notification);
        Assert.assertEquals(1, notification.getRecipients().size());
        Assert.assertEquals("john", notification.getRecipients().get(0).getId());
        Assert.assertEquals(1, notification.getSubjects().size());
        Assert.assertEquals("Test of notification", notification.getSubjects().get(0).getText());
        EmailNotification emailNotification = ((EmailNotification) (notification));
        Assert.assertEquals(1, emailNotification.getEmailHeaders().size());
        Language lang = TaskModelProvider.getFactory().newLanguage();
        lang.setMapkey("en-UK");
        EmailNotificationHeader header = emailNotification.getEmailHeaders().get(lang);
        Assert.assertNotNull(header);
        Assert.assertEquals("Test of notification", header.getSubject());
        Assert.assertEquals("And here is the body", header.getBody());
        // check deadline expiration time
        Assert.assertNotNull(deadlines.getStartDeadlines().get(0).getDate());
        long expirationTime = (deadlines.getStartDeadlines().get(0).getDate().getTime()) - (System.currentTimeMillis());
        Assert.assertEquals(4, roundExpirationTime(expirationTime));
        // verify notification
        notification = deadlines.getStartDeadlines().get(1).getEscalations().get(0).getNotifications().get(0);
        Assert.assertNotNull(notification);
        Assert.assertEquals(1, notification.getRecipients().size());
        Assert.assertEquals("john", notification.getRecipients().get(0).getId());
        Assert.assertEquals(1, notification.getSubjects().size());
        Assert.assertEquals("Test of notification", notification.getSubjects().get(0).getText());
        emailNotification = ((EmailNotification) (notification));
        Assert.assertEquals(1, emailNotification.getEmailHeaders().size());
        lang = TaskModelProvider.getFactory().newLanguage();
        lang.setMapkey("en-UK");
        header = emailNotification.getEmailHeaders().get(lang);
        Assert.assertNotNull(header);
        Assert.assertEquals("Test of notification", header.getSubject());
        Assert.assertEquals("And here is the body", header.getBody());
        // check deadline expiration time
        Assert.assertNotNull(deadlines.getStartDeadlines().get(1).getDate());
        expirationTime = (deadlines.getStartDeadlines().get(1).getDate().getTime()) - (System.currentTimeMillis());
        Assert.assertEquals(6, roundExpirationTime(expirationTime));
    }

    @Test
    public void testNotStartedNotifyMinimalWithHtml() {
        WorkItem workItem = new org.drools.core.process.instance.impl.WorkItemImpl();
        workItem.setParameter("NotStartedNotify", ("[tousers:john|subject:Test of notification|body:&lt;html&gt;" + ("&lt;body&gt;" + ("Reason {s}&lt;br/&gt;" + ("body of notification:&lt;br/&gt;" + ("work item id - ${workItemId}&lt;br/&gt;" + ("process instance id - ${processInstanceId}&lt;br/&gt;" + ("task id - ${taskId}&lt;br/&gt;" + ("http://localhost:8080/taskserver-url" + ("expiration time - ${doc['Deadlines'][0].expires}&lt;br/&gt;" + ("&lt;/body&gt;" + "&lt;/html&gt;]@[4h]")))))))))));
        @SuppressWarnings(value = "unchecked")
        Deadlines deadlines = HumanTaskHandlerHelper.setDeadlines(workItem, Collections.EMPTY_LIST, null);
        Assert.assertNotNull(deadlines);
        Assert.assertEquals(1, deadlines.getStartDeadlines().size());
        Assert.assertEquals(0, deadlines.getEndDeadlines().size());
        Assert.assertEquals(1, deadlines.getStartDeadlines().get(0).getEscalations().size());
        Assert.assertEquals(1, deadlines.getStartDeadlines().get(0).getEscalations().get(0).getNotifications().size());
        Assert.assertEquals(0, deadlines.getStartDeadlines().get(0).getEscalations().get(0).getReassignments().size());
        // verify notification
        Notification notification = deadlines.getStartDeadlines().get(0).getEscalations().get(0).getNotifications().get(0);
        Assert.assertNotNull(notification);
        Assert.assertEquals(1, notification.getRecipients().size());
        Assert.assertEquals("john", notification.getRecipients().get(0).getId());
        Assert.assertEquals(1, notification.getSubjects().size());
        Assert.assertEquals("Test of notification", notification.getSubjects().get(0).getText());
        EmailNotification emailNotification = ((EmailNotification) (notification));
        Assert.assertEquals(1, emailNotification.getEmailHeaders().size());
        Language lang = TaskModelProvider.getFactory().newLanguage();
        lang.setMapkey("en-UK");
        EmailNotificationHeader header = emailNotification.getEmailHeaders().get(lang);
        Assert.assertNotNull(header);
        Assert.assertEquals("Test of notification", header.getSubject());
        Assert.assertTrue(((header.getBody().indexOf("http://localhost:8080/taskserver-url")) != (-1)));
        // check deadline expiration time
        Assert.assertNotNull(deadlines.getStartDeadlines().get(0).getDate());
        long expirationTime = (deadlines.getStartDeadlines().get(0).getDate().getTime()) - (System.currentTimeMillis());
        Assert.assertEquals(4, roundExpirationTime(expirationTime));
    }

    private long roundExpirationTime(long expirationTime) {
        BigDecimal a = new BigDecimal(expirationTime);
        a = a.setScale(1, 1);
        BigDecimal b = new BigDecimal(((60 * 60) * 1000));
        b = b.setScale(1, 1);
        double devided = (a.doubleValue()) / (b.doubleValue());
        long roundedValue = Math.round(devided);
        return roundedValue;
    }
}

