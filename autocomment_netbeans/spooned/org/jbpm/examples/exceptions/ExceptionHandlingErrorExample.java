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


package org.jbpm.examples.exceptions;

import java.util.HashMap;
import org.kie.api.runtime.KieSession;
import java.util.Map;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.jbpm.bpmn2.handler.ServiceTaskHandler;
import org.jbpm.bpmn2.handler.SignallingTaskHandlerDecorator;

public class ExceptionHandlingErrorExample {
    public static final void main(String[] args) {
        ExceptionHandlingErrorExample.runExample();
    }

    public static ProcessInstance runExample() {
        // load up the knowledge base
        RuntimeManager manager = ExceptionHandlingErrorExample.createManager();
        KieSession ksession = manager.getRuntimeEngine(null).getKieSession();
        String eventType = "Error-code";
        SignallingTaskHandlerDecorator signallingTaskWrapper = new SignallingTaskHandlerDecorator(ServiceTaskHandler.class, eventType);
        signallingTaskWrapper.setWorkItemExceptionParameterName(ExceptionService.exceptionParameterName);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", signallingTaskWrapper);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("serviceInputItem", "Input to Original Service");
        ProcessInstance processInstance = ksession.startProcess("ProcessWithExceptionHandlingError", params);
        manager.close();
        return processInstance;
    }

    private static RuntimeManager createManager() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newEmptyBuilder().addAsset(KieServices.Factory.get().getResources().newClassPathResource("exceptions/ExceptionHandlingWithError.bpmn2"), ResourceType.BPMN2).get();
        return RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
    }
}

