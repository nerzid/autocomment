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


package org.jbpm.examples.quickstarts;

import java.util.HashMap;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.kie.api.runtime.KieSession;
import java.util.Map;
import org.junit.Test;

/**
 * This is a sample file to test a process.
 */
public class JavaServiceQuickstartTest extends JbpmJUnitBaseTestCase {
    @Test
    public void testProcess() {
        KieSession ksession = createRuntimeManager("quickstarts/ScriptTask.bpmn").getRuntimeEngine(null).getKieSession();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("person", new Person("krisv"));
        ksession.startProcess("com.sample.script", params);
    }
}

