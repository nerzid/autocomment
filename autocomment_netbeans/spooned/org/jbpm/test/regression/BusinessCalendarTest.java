/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.jbpm.test.regression;

import org.assertj.core.api.Assertions;
import qa.tools.ikeeper.annotation.BZ;
import org.junit.Before;
import org.jbpm.process.core.timer.BusinessCalendar;
import java.util.Calendar;
import java.util.Date;
import org.jbpm.test.JbpmTestCase;
import org.kie.api.runtime.KieSession;
import java.util.List;
import org.kie.api.task.model.OrganizationalEntity;
import java.text.ParseException;
import java.util.Properties;
import java.text.SimpleDateFormat;
import org.kie.api.task.model.Task;
import org.kie.api.task.TaskService;
import org.junit.Test;

@BZ(value = "958384")
public class BusinessCalendarTest extends JbpmTestCase {
    private static final String TIMER = "org/jbpm/test/regression/BusinessCalendar-timer.bpmn2";

    private static final String TIMER_ID = "org.jbpm.test.regression.BusinessCalendar-timer";

    private static final String ESCALATION = "org/jbpm/test/regression/BusinessCalendar-escalation.bpmn2";

    private static final String ESCALATION_ID = "org.jbpm.test.regression.BusinessCalendar-escalation";

    private KieSession ksession;

    private TaskService taskService;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        createRuntimeManager(BusinessCalendarTest.TIMER, BusinessCalendarTest.ESCALATION);
        ksession = getRuntimeEngine().getKieSession();
        taskService = getRuntimeEngine().getTaskService();
    }

    @Test
    public void testTimerBusinessHour() throws InterruptedException, ParseException {
        configureBusinessCalendar(true);
        long instance = ksession.startProcess(BusinessCalendarTest.TIMER_ID).getId();
        // wait for timer (1s)
        Thread.sleep(2000);
        assertProcessInstanceCompleted(instance);
    }

    @Test
    public void testTimerNonBusinessHour() throws InterruptedException, ParseException {
        configureBusinessCalendar(false);
        long instance = ksession.startProcess(BusinessCalendarTest.TIMER_ID).getId();
        // wait for timer (1s)
        Thread.sleep(2000);
        assertProcessInstanceActive(instance);
        ksession.abortProcessInstance(instance);
    }

    @Test
    public void testHumanTaskEscalationBusinessHour() throws InterruptedException, ParseException {
        configureBusinessCalendar(true);
        long instance = ksession.startProcess(BusinessCalendarTest.ESCALATION_ID).getId();
        long taskId = taskService.getTasksByProcessInstanceId(instance).get(0);
        // wait for task escalation (1s)
        Thread.sleep(2000);
        Assertions.assertThat(getTaskPotentialOwner(taskId)).isEqualToIgnoringCase("mary");
        ksession.abortProcessInstance(instance);
    }

    @Test
    public void testHumanTaskEscalationNonBusinessHour() throws InterruptedException, ParseException {
        configureBusinessCalendar(false);
        long instance = ksession.startProcess(BusinessCalendarTest.ESCALATION_ID).getId();
        long taskId = taskService.getTasksByProcessInstanceId(instance).get(0);
        // wait for task escalation (1s)
        Thread.sleep(2000);
        Assertions.assertThat(getTaskPotentialOwner(taskId)).isEqualToIgnoringCase("john");
        ksession.abortProcessInstance(instance);
    }

    private String getTaskPotentialOwner(long taskId) {
        Task task = taskService.getTaskById(taskId);
        Assertions.assertThat(task).isNotNull();
        List<OrganizationalEntity> potentialOwners = task.getPeopleAssignments().getPotentialOwners();
        Assertions.assertThat(potentialOwners).isNotEmpty();
        return potentialOwners.get(0).getId();
    }

    private void configureBusinessCalendar(boolean businessHour) {
        Properties configuration = new Properties();
        if (businessHour) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, (-1));
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            configuration.setProperty(BusinessCalendarImpl.START_HOUR, "0");
            configuration.setProperty(BusinessCalendarImpl.END_HOUR, "24");
            configuration.setProperty(BusinessCalendarImpl.HOURS_PER_DAY, "24");
            configuration.setProperty(BusinessCalendarImpl.DAYS_PER_WEEK, "7");
            configuration.setProperty(BusinessCalendarImpl.WEEKEND_DAYS, Integer.toString(dayOfWeek));
        } else {
            Date today = new Date();
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, 1);
            Date tomorrow = c.getTime();
            String dateFormat = "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            configuration.setProperty(BusinessCalendarImpl.HOLIDAYS, (((sdf.format(today)) + ",") + (sdf.format(tomorrow))));
            configuration.setProperty(BusinessCalendarImpl.HOLIDAY_DATE_FORMAT, dateFormat);
        }
        BusinessCalendar businessCalendar = new org.jbpm.process.core.timer.BusinessCalendarImpl(configuration);
        ksession.getEnvironment().set("jbpm.business.calendar", businessCalendar);
    }
}

