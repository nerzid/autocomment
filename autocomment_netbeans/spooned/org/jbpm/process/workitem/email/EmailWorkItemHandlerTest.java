/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.process.workitem.email;

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.After;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Before;
import org.kie.internal.utils.ChainedProperties;
import org.kie.internal.utils.ClassLoaderUtil;
import javax.mail.internet.InternetAddress;
import java.util.List;
import javax.mail.internet.MimeMessage;
import org.junit.Test;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;
import org.jbpm.bpmn2.handler.WorkItemHandlerRuntimeException;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.kie.api.runtime.process.WorkItemManager;

public class EmailWorkItemHandlerTest extends AbstractBaseTest {
    private Wiser wiser;

    private String emailHost;

    private String emailPort;

    @Before
    public void setUp() throws Exception {
        ChainedProperties props = new ChainedProperties("email.conf", ClassLoaderUtil.getClassLoader(null, getClass(), false));
        emailHost = props.getProperty("mail.smtp.host", "localhost");
        emailPort = props.getProperty("mail.smtp.port", "2345");
        wiser = new Wiser();
        wiser.setHostname(emailHost);
        wiser.setPort(Integer.parseInt(emailPort));
        wiser.start();
        Thread.sleep(200);
    }

    @After
    public void tearDown() throws Exception {
        if ((wiser) != null) {
            wiser.getMessages().clear();
            wiser.stop();
            wiser = null;
            Thread.sleep(1000);
        } 
    }

    @Test
    public void testSingleTo() throws Exception {
        EmailWorkItemHandler handler = new EmailWorkItemHandler();
        handler.setConnection(emailHost, emailPort, null, null);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("To", "person1@domain.com");
        workItem.setParameter("From", "person2@domain.com");
        workItem.setParameter("Reply-To", "person3@domain.com");
        workItem.setParameter("Subject", "Subject 1");
        workItem.setParameter("Body", "Body 1");
        WorkItemManager manager = new org.drools.core.process.instance.impl.DefaultWorkItemManager(null);
        handler.executeWorkItem(workItem, manager);
        Assert.assertEquals(1, wiser.getMessages().size());
        MimeMessage msg = ((WiserMessage) (wiser.getMessages().get(0))).getMimeMessage();
        // Side effect of MIME encoding (I think.. ): \r\n..
        String content = ((String) (msg.getContent())).replace("\r\n", "");
        Assert.assertEquals(workItem.getParameter("Body"), content);
        Assert.assertEquals(workItem.getParameter("Subject"), msg.getSubject());
        Assert.assertEquals(workItem.getParameter("From"), ((InternetAddress) (msg.getFrom()[0])).getAddress());
        Assert.assertEquals(workItem.getParameter("Reply-To"), ((InternetAddress) (msg.getReplyTo()[0])).getAddress());
        Assert.assertEquals(workItem.getParameter("To"), ((InternetAddress) (msg.getRecipients(RecipientType.TO)[0])).getAddress());
        Assert.assertNull(msg.getRecipients(RecipientType.CC));
        Assert.assertNull(msg.getRecipients(RecipientType.BCC));
    }

    @Test
    public void testSingleToWithSingleCCAndBCC() throws Exception {
        EmailWorkItemHandler handler = new EmailWorkItemHandler();
        handler.setConnection(emailHost, emailPort, null, null);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("To", "person1@domain.com");
        workItem.setParameter("Cc", "person2@domain.com");
        workItem.setParameter("Bcc", "person3@domain.com");
        workItem.setParameter("From", "person4@domain.com");
        workItem.setParameter("Reply-To", "person5@domain.com");
        workItem.setParameter("Subject", "Subject 1");
        workItem.setParameter("Body", "Body 1");
        WorkItemManager manager = new org.drools.core.process.instance.impl.DefaultWorkItemManager(null);
        handler.executeWorkItem(workItem, manager);
        Assert.assertEquals(3, wiser.getMessages().size());
        List<String> list = new ArrayList<String>(3);
        list.add(wiser.getMessages().get(0).getEnvelopeReceiver());
        list.add(wiser.getMessages().get(1).getEnvelopeReceiver());
        list.add(wiser.getMessages().get(2).getEnvelopeReceiver());
        Assert.assertTrue(list.contains("person1@domain.com"));
        Assert.assertTrue(list.contains("person2@domain.com"));
        Assert.assertTrue(list.contains("person3@domain.com"));
        for (int i = 0; i < (wiser.getMessages().size()); ++i) {
            MimeMessage msg = ((WiserMessage) (wiser.getMessages().get(i))).getMimeMessage();
            Assert.assertEquals(workItem.getParameter("From"), wiser.getMessages().get(i).getEnvelopeSender());
            String content = ((String) (msg.getContent())).replace("\r\n", "");
            Assert.assertEquals(workItem.getParameter("Body"), content);
            Assert.assertEquals(workItem.getParameter("Subject"), msg.getSubject());
            Assert.assertEquals(workItem.getParameter("From"), ((InternetAddress) (msg.getFrom()[0])).getAddress());
            Assert.assertEquals(workItem.getParameter("Reply-To"), ((InternetAddress) (msg.getReplyTo()[0])).getAddress());
            Assert.assertEquals(workItem.getParameter("To"), ((InternetAddress) (msg.getRecipients(RecipientType.TO)[0])).getAddress());
            Assert.assertEquals(workItem.getParameter("Cc"), ((InternetAddress) (msg.getRecipients(RecipientType.CC)[0])).getAddress());
        }
    }

    @Test
    public void testMultipleToWithSingleCCAndBCC() throws Exception {
        EmailWorkItemHandler handler = new EmailWorkItemHandler();
        handler.setConnection(emailHost, emailPort, null, null);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("To", "person1@domain.com; person2@domain.com");
        workItem.setParameter("Cc", "person3@domain.com; person4@domain.com");
        workItem.setParameter("Bcc", "person5@domain.com; person6@domain.com");
        workItem.setParameter("From", "person4@domain.com");
        workItem.setParameter("Reply-To", "person5@domain.com");
        workItem.setParameter("Subject", "Subject 1");
        workItem.setParameter("Body", "Body 1");
        WorkItemManager manager = new org.drools.core.process.instance.impl.DefaultWorkItemManager(null);
        handler.executeWorkItem(workItem, manager);
        Assert.assertEquals(6, wiser.getMessages().size());
        List<String> list = new ArrayList<String>(6);
        for (int i = 0; i < 6; ++i) {
            list.add(wiser.getMessages().get(i).getEnvelopeReceiver());
        }
        for (int i = 1; i < 7; ++i) {
            Assert.assertTrue(list.contains((("person" + i) + "@domain.com")));
        }
        // We know from previous test that all MimeMessages will be identical
        MimeMessage msg = ((WiserMessage) (wiser.getMessages().get(0))).getMimeMessage();
        Assert.assertEquals(workItem.getParameter("From"), wiser.getMessages().get(0).getEnvelopeSender());
        String content = ((String) (msg.getContent())).replace("\r\n", "");
        Assert.assertEquals(workItem.getParameter("Body"), content);
        Assert.assertEquals(workItem.getParameter("Subject"), msg.getSubject());
        Assert.assertEquals(workItem.getParameter("From"), ((InternetAddress) (msg.getFrom()[0])).getAddress());
        Assert.assertEquals(workItem.getParameter("Reply-To"), ((InternetAddress) (msg.getReplyTo()[0])).getAddress());
        Assert.assertEquals(workItem.getParameter("To"), (((((InternetAddress) (msg.getRecipients(RecipientType.TO)[0])).getAddress()) + "; ") + (((InternetAddress) (msg.getRecipients(RecipientType.TO)[1])).getAddress())));
        Assert.assertEquals(workItem.getParameter("Cc"), (((((InternetAddress) (msg.getRecipients(RecipientType.CC)[0])).getAddress()) + "; ") + (((InternetAddress) (msg.getRecipients(RecipientType.CC)[1])).getAddress())));
    }

    @Test(expected = WorkItemHandlerRuntimeException.class)
    public void testFailedExecuteToHandleException() throws Exception {
        EmailWorkItemHandler handler = new EmailWorkItemHandler();
        handler.setConnection(emailHost, "123", null, null);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("To", "person1@domain.com");
        workItem.setParameter("From", "person2@domain.com");
        workItem.setParameter("Reply-To", "person3@domain.com");
        workItem.setParameter("Subject", "Subject 1");
        workItem.setParameter("Body", "Body 1");
        WorkItemManager manager = new org.drools.core.process.instance.impl.DefaultWorkItemManager(null);
        handler.executeWorkItem(workItem, manager);
    }
}

