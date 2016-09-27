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


package org.jbpm.test.container.test.ejbservices.process;

import org.jbpm.test.container.AbstractRuntimeEJBServicesTest;
import org.assertj.core.api.Assertions;
import org.junit.experimental.categories.Category;
import org.jbpm.test.container.groups.EAP;
import org.kie.api.runtime.process.ProcessInstance;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.junit.Test;
import org.jbpm.test.container.groups.WAS;
import org.jbpm.test.container.groups.WLS;

@Category(value = { EAP.class , WAS.class , WLS.class })
public class EStartProcessTest extends AbstractRuntimeEJBServicesTest {
    @Test
    public void testStartScriptTaskProcess() {
        Long pid = archive.startProcess(kieJar, SCRIPT_TASK_PROCESS_ID);
        ProcessInstanceDesc log = runtimeDataService.getProcessInstanceById(pid);
        Assertions.assertThat(log).isNotNull();
        Assertions.assertThat(log.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testExecuteStartProcessCommand() {
        ProcessInstance pi = processService.execute(kieJar, new org.drools.core.command.runtime.process.StartProcessCommand(SCRIPT_TASK_PROCESS_ID));
        Assertions.assertThat(pi).isNotNull();
        Assertions.assertThat(pi.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }
}

