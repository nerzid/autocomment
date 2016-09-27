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


package org.jbpm.services.task;

import org.junit.After;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Before;
import org.kie.internal.utils.ChainedProperties;
import org.kie.internal.utils.ClassLoaderUtil;
import org.jbpm.services.task.util.CountDownTaskEventListener;
import java.util.Date;
import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.io.InputStreamReader;
import org.kie.internal.task.api.model.InternalTask;
import org.kie.internal.task.api.InternalTaskService;
import java.util.List;
import java.util.Map;
import javax.mail.internet.MimeMessage;
import javax.persistence.Persistence;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import java.io.Reader;
import org.jbpm.services.task.impl.factories.TaskFactory;
import org.junit.Test;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

public class TaskReminderTest extends HumanTaskServicesBaseTest {
    private PoolingDataSource pds;

    private EntityManagerFactory emf;

    private Wiser wiser;

    @Before
    public void setup() {
        final ChainedProperties props = new ChainedProperties("email.conf", ClassLoaderUtil.getClassLoader(null, getClass(), false));
        wiser = new Wiser();
        wiser.setHostname(props.getProperty("mail.smtp.host", "localhost"));
        wiser.setPort(Integer.parseInt(props.getProperty("mail.smtp.port", "2345")));
        wiser.start();
        try {
            Thread.sleep(1000);
        } catch (Throwable t) {
            // Do nothing
        }
        pds = setupPoolingDataSource();
        emf = Persistence.createEntityManagerFactory("org.jbpm.services.task");
        TaskReminderTest.this.taskService = ((InternalTaskService) (HumanTaskServiceFactory.newTaskServiceConfigurator().entityManagerFactory(emf).getTaskService()));
    }

    @After
    public void clean() {
        if ((wiser) != null) {
            wiser.stop();
            try {
                Thread.sleep(1000);
            } catch (Throwable t) {
                // Do nothing
            }
        } 
        super.tearDown();
        if ((emf) != null) {
            emf.close();
        } 
        if ((pds) != null) {
            pds.close();
        } 
    }

    @Test(timeout = 10000)
    public void testTaskReminderWithoutNotification() throws Exception {
        CountDownTaskEventListener countDownListener = new CountDownTaskEventListener(1, false, true);
        addCountDownListner(countDownListener);
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());
        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.ReminderWithoutNotification));
        InternalTask task = ((InternalTask) (TaskFactory.evalTask(reader, vars)));
        System.out.println(("testTaskReminderWithoutNotification " + (task.getTaskData().getStatus())));
        Assert.assertNull(task.getDeadlines());
        long taskId = taskService.addTask(task, new HashMap<String, Object>());
        taskService.executeReminderForTask(taskId, "Luke Cage");
        countDownListener.waitTillCompleted();
        Assert.assertEquals(1, wiser.getMessages().size());
        String receiver = wiser.getMessages().get(0).getEnvelopeReceiver();
        Assert.assertEquals("tony@domain.com", receiver);
        MimeMessage msg = ((WiserMessage) (wiser.getMessages().get(0))).getMimeMessage();
        Assert.assertEquals("You have a task ( Simple Test Task ) of process ( taskReminder )", msg.getSubject());
    }

    @Test(timeout = 10000)
    public void testTaskReminderWithNotificationByTaskNostarted() throws Exception {
        CountDownTaskEventListener countDownListener = new CountDownTaskEventListener(1, false, true);
        addCountDownListner(countDownListener);
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());
        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.ReminderWithNotificationReserved));
        InternalTask task = ((InternalTask) (TaskFactory.evalTask(reader, vars)));
        System.out.println(("testTaskReminderWithNotificationByTaskNostarted " + (task.getTaskData().getStatus())));
        Assert.assertEquals(1, task.getDeadlines().getEndDeadlines().size());
        Assert.assertEquals(1, task.getDeadlines().getStartDeadlines().size());
        long taskId = taskService.addTask(task, new HashMap<String, Object>());
        taskService.executeReminderForTask(taskId, "Luke Cage");
        countDownListener.waitTillCompleted();
        Assert.assertEquals(2, wiser.getMessages().size());
        final List<String> list = new ArrayList<String>(2);
        list.add(wiser.getMessages().get(0).getEnvelopeReceiver());
        list.add(wiser.getMessages().get(1).getEnvelopeReceiver());
        Assert.assertTrue(list.contains("tony@domain.com"));
        Assert.assertTrue(list.contains("darth@domain.com"));
        MimeMessage msg = ((WiserMessage) (wiser.getMessages().get(0))).getMimeMessage();
        Assert.assertEquals("ReminderWithNotificationReserved:you have new task to be started", msg.getSubject());
        Assert.assertEquals("task is not started", msg.getContent());
        msg = ((WiserMessage) (wiser.getMessages().get(1))).getMimeMessage();
        Assert.assertEquals("ReminderWithNotificationReserved:you have new task to be started", msg.getSubject());
        Assert.assertEquals("task is not started", msg.getContent());
    }

    @Test(timeout = 10000)
    public void testTaskReminderWithNotificationByTaskNoCompleted() throws Exception {
        CountDownTaskEventListener countDownListener = new CountDownTaskEventListener(1, false, true);
        addCountDownListner(countDownListener);
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());
        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.ReminderWithNotificationInProgress));
        InternalTask task = ((InternalTask) (TaskFactory.evalTask(reader, vars)));
        System.out.println(("testTaskReminderWithNotificationByTaskNoCompleted " + (task.getTaskData().getStatus())));
        Assert.assertEquals(1, task.getDeadlines().getEndDeadlines().size());
        Assert.assertEquals(1, task.getDeadlines().getStartDeadlines().size());
        long taskId = taskService.addTask(task, new HashMap<String, Object>());
        taskService.executeReminderForTask(taskId, "Luke Cage");
        countDownListener.waitTillCompleted();
        Assert.assertEquals(2, wiser.getMessages().size());
        List<String> list = new ArrayList<String>(2);
        list.add(wiser.getMessages().get(0).getEnvelopeReceiver());
        list.add(wiser.getMessages().get(1).getEnvelopeReceiver());
        Assert.assertTrue(list.contains("tony@domain.com"));
        Assert.assertTrue(list.contains("darth@domain.com"));
        MimeMessage msg = ((WiserMessage) (wiser.getMessages().get(0))).getMimeMessage();
        Assert.assertEquals("ReminderWithNotificationInProgress:you have new task to be completed", msg.getSubject());
        Assert.assertEquals("task is not completed", msg.getContent());
        msg = ((WiserMessage) (wiser.getMessages().get(1))).getMimeMessage();
        Assert.assertEquals("ReminderWithNotificationInProgress:you have new task to be completed", msg.getSubject());
        Assert.assertEquals("task is not completed", msg.getContent());
    }
}

