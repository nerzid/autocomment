/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.test.container.test.ejbservices.task;

import org.jbpm.test.container.AbstractRuntimeEJBServicesTest;
import org.assertj.core.api.Assertions;
import org.junit.experimental.categories.Category;
import org.jbpm.test.container.groups.EAP;
import java.util.List;
import org.kie.api.task.model.TaskSummary;
import org.junit.Test;
import org.jbpm.services.api.model.UserTaskInstanceDesc;
import org.jbpm.test.container.groups.WAS;
import org.jbpm.test.container.groups.WLS;

@Category(value = { EAP.class , WAS.class , WLS.class })
public class EGetTaskTest extends AbstractRuntimeEJBServicesTest {
    @Test
    public void testGetTaskInstanceInfo() throws Exception {
        Long processInstanceId = archive.startProcess(kieJar, HUMAN_TASK_PROCESS_ID);
        Long taskId = runtimeDataService.getTasksByProcessInstanceId(processInstanceId).get(0);
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        System.out.println((((((task.getActualOwner()) + ",") + (task.getTaskId())) + ",") + (task.getStatus())));
        Assertions.assertThat(userId).isEqualTo(task.getActualOwner());
        Assertions.assertThat(Status.Reserved.name()).isEqualTo(task.getStatus());
    }

    @Test
    public void testTaskQuery() throws Exception {
        Long processInstanceId = archive.startProcess(kieJar, HUMAN_TASK_PROCESS_ID);
        List<TaskSummary> tasks = runtimeDataService.getTasksOwned(userId, new org.kie.internal.query.QueryFilter(0, 5));
        TaskSummary task = null;
        for (TaskSummary potientialTask : tasks) {
            System.out.println(("id=" + (potientialTask.getProcessInstanceId())));
            if ((potientialTask.getProcessInstanceId()) == processInstanceId) {
                task = potientialTask;
            } 
        }
        Assertions.assertThat(task).isNotNull();
        Assertions.assertThat(task.getActualOwner().getId()).isEqualTo(userId);
    }
}

