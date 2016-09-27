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


package org.jbpm.test.functional.task;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.jbpm.test.JbpmTestCase;
import org.kie.api.runtime.KieSession;
import java.util.List;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.jbpm.runtime.manager.impl.task.SynchronizedTaskService;
import org.kie.api.task.model.Task;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.junit.Test;

public class TaskSubjectAndDescriptionTest extends JbpmTestCase {
    private static final String EXPECTED_SUBJECT = "Bake a cake";

    private static final String HUMAN_TASK = "org/jbpm/test/functional/task/TaskSubjectAndDescription.bpmn2";

    private static final String HUMAN_TASK_ID = "org.jbpm.test.functional.task.TaskSubjectAndDescription";

    private static final String HUMAN_TASK2 = "org/jbpm/test/functional/task/TaskSubject.bpmn2";

    private static final String HUMAN_TASK2_ID = "org.jbpm.test.functional.task.TaskSubject";

    private KieSession kieSession;

    private TaskService taskService;

    public TaskSubjectAndDescriptionTest() {
        super(true, true);
    }

    @Before
    public void init() throws Exception {
        createRuntimeManager(TaskSubjectAndDescriptionTest.HUMAN_TASK, TaskSubjectAndDescriptionTest.HUMAN_TASK2);
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        kieSession = runtimeEngine.getKieSession();
        taskService = ((SynchronizedTaskService) (runtimeEngine.getTaskService()));
    }

    @Test
    public void testSubjectAndDescriptionProperties() {
        ProcessInstance processInstance = kieSession.startProcess(TaskSubjectAndDescriptionTest.HUMAN_TASK_ID);
        List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        TaskSummary task = list.get(0);
        Task t = taskService.getTaskById(task.getId());
        Assertions.assertThat(task.getDescription()).isEqualTo("This is description of the human task.");
        Assertions.assertThat(task.getSubject()).isNullOrEmpty();
        Assertions.assertThat(t.getSubject()).isEqualTo(TaskSubjectAndDescriptionTest.EXPECTED_SUBJECT);
        taskService.start(task.getId(), "john");
        taskService.complete(task.getId(), "john", null);
        ProcessInstanceLog plog = getLogService().findProcessInstance(processInstance.getId());
        Assertions.assertThat(plog.getStatus()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testSubjectProperty() {
        ProcessInstance processInstance = kieSession.startProcess(TaskSubjectAndDescriptionTest.HUMAN_TASK2_ID);
        List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        TaskSummary task = list.get(0);
        Task t = taskService.getTaskById(task.getId());
        Assertions.assertThat(task.getDescription()).isEqualTo(TaskSubjectAndDescriptionTest.EXPECTED_SUBJECT);
        Assertions.assertThat(task.getSubject()).isNullOrEmpty();
        Assertions.assertThat(t.getSubject()).isEqualTo(TaskSubjectAndDescriptionTest.EXPECTED_SUBJECT);
        taskService.start(task.getId(), "john");
        taskService.complete(task.getId(), "john", null);
        ProcessInstanceLog plog = getLogService().findProcessInstance(processInstance.getId());
        Assertions.assertThat(plog.getStatus()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }
}

