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


package org.jbpm.examples.looping;

import java.util.HashMap;
import org.kie.api.runtime.KieSession;
import java.util.Map;
import org.kie.api.runtime.manager.RuntimeEnvironment;

public class LoopingExample {
    public static final void main(String[] args) {
        try {
            // load up the knowledge session
            KieSession ksession = LoopingExample.getKieSession();
            // start a new process instance
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("count", 5);
            ksession.startProcess("com.sample.looping", params);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static KieSession getKieSession() throws Exception {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newEmptyBuilder().addAsset(KieServices.Factory.get().getResources().newClassPathResource("looping/Looping.bpmn"), ResourceType.BPMN2).get();
        return RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment).getRuntimeEngine(null).getKieSession();
    }
}

