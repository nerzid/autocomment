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


package org.jbpm.test.functional.event;

import java.util.Arrays;
import org.assertj.core.api.Assertions;
import java.util.Collection;
import org.kie.api.runtime.manager.Context;
import org.kie.internal.runtime.manager.context.EmptyContext;
import java.util.HashMap;
import org.jbpm.test.JbpmTestCase;
import org.kie.api.runtime.KieSession;
import java.util.List;
import java.util.Map;
import org.junit.runners.Parameterized;
import Parameterized.Parameters;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.junit.runner.RunWith;
import org.junit.Test;

@RunWith(value = Parameterized.class)
public class SignalEventTest extends JbpmTestCase {
    private static final String END_THROW_DEFAULT = "org/jbpm/test/functional/event/Signal-endThrow-default.bpmn2";

    private static final String END_THROW_DEFAULT_ID = "org.jbpm.test.functional.event.Signal-endThrow-default";

    private static final String END_THROW_INSTANCE = "org/jbpm/test/functional/event/Signal-endThrow-instance.bpmn2";

    private static final String END_THROW_INSTANCE_ID = "org.jbpm.test.functional.event.Signal-endThrow-instance";

    private static final String END_THROW_PROJECT = "org/jbpm/test/functional/event/Signal-endThrow-project.bpmn2";

    private static final String END_THROW_PROJECT_ID = "org.jbpm.test.functional.event.Signal-endThrow-project";

    private static final String INTERMEDIATE_CATCH = "org/jbpm/test/functional/event/Signal-intermediateCatch.bpmn2";

    private static final String INTERMEDIATE_CATCH_ID = "org.jbpm.test.functional.event.Signal-intermediateCatch";

    private static final String INTERMEDIATE_THROW_DEFAULT = "org/jbpm/test/functional/event/Signal-intermediateThrow-default.bpmn2";

    private static final String INTERMEDIATE_THROW_DEFAULT_ID = "org.jbpm.test.functional.event.Signal-intermediateThrow-default";

    private static final String INTERMEDIATE_THROW_INSTANCE = "org/jbpm/test/functional/event/Signal-intermediateThrow-instance.bpmn2";

    private static final String INTERMEDIATE_THROW_INSTANCE_ID = "org.jbpm.test.functional.event.Signal-intermediateThrow-instance";

    private static final String INTERMEDIATE_THROW_PROJECT = "org/jbpm/test/functional/event/Signal-intermediateThrow-project.bpmn2";

    private static final String INTERMEDIATE_THROW_PROJECT_ID = "org.jbpm.test.functional.event.Signal-intermediateThrow-project";

    private static final String START_CATCH = "org/jbpm/test/functional/event/Signal-startCatch.bpmn2";

    private static final String START_CATCH_ID = "org.jbpm.test.functional.event.Signal-startCatch";

    private static final String SUBPROCESS_CATCH = "org/jbpm/test/functional/event/Signal-subprocessCatch.bpmn2";

    private static final String SUBPROCESS_CATCH_ID = "org.jbpm.test.functional.event.Signal-subprocessCatch";

    private enum Scope {
DEFAULT, PROCESS_INSTANCE, PROJECT;    }

    private final Strategy strategy;

    private final SignalEventTest.Scope scope;

    public SignalEventTest(Strategy strategy, SignalEventTest.Scope scope) {
        this.strategy = strategy;
        this.scope = scope;
    }

    @Parameters(name = "{0} strategy, {1} scope")
    public static Collection<Object[]> parameters() {
        Object[][] combinations = new Object[][]{ new Object[]{ Strategy.SINGLETON , SignalEventTest.Scope.DEFAULT } , new Object[]{ Strategy.SINGLETON , SignalEventTest.Scope.PROJECT } , new Object[]{ Strategy.SINGLETON , SignalEventTest.Scope.PROCESS_INSTANCE } , new Object[]{ Strategy.PROCESS_INSTANCE , SignalEventTest.Scope.DEFAULT } , new Object[]{ Strategy.PROCESS_INSTANCE , SignalEventTest.Scope.PROJECT } , new Object[]{ Strategy.PROCESS_INSTANCE , SignalEventTest.Scope.PROCESS_INSTANCE } };
        return Arrays.asList(combinations);
    }

    private KieSession getKieSession() {
        Context<?> context = (strategy) == (Strategy.PROCESS_INSTANCE) ? ProcessInstanceIdContext.get() : EmptyContext.get();
        return getRuntimeEngine(context).getKieSession();
    }

    @Test
    public void testSignalManually() {
        createRuntimeManager(strategy, ((String) (null)), SignalEventTest.INTERMEDIATE_CATCH);
        KieSession ksession = getKieSession();
        ProcessInstance pi1 = ksession.startProcess(SignalEventTest.INTERMEDIATE_CATCH_ID);
        getKieSession().startProcess(SignalEventTest.INTERMEDIATE_CATCH_ID);
        switch (scope) {
            case DEFAULT :
                ksession.signalEvent("commonSignal", null);
                break;
            case PROCESS_INSTANCE :
                ksession.signalEvent("commonSignal", null, pi1.getId());
                break;
            case PROJECT :
                manager.signalEvent("commonSignal", null);
                break;
            default :
                throw new IllegalArgumentException("unknown scope");
        }
        List<? extends ProcessInstanceLog> instances = getRuntimeEngine().getAuditService().findProcessInstances();
        Assertions.assertThat(instances).hasSize(2);
        Assertions.assertThat(instances.get(0).getProcessId()).isEqualTo(SignalEventTest.INTERMEDIATE_CATCH_ID);
        Assertions.assertThat(instances.get(0).getStatus()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        Assertions.assertThat(instances.get(1).getProcessId()).isEqualTo(SignalEventTest.INTERMEDIATE_CATCH_ID);
        if ((strategy) == (Strategy.SINGLETON)) {
            Assertions.assertThat(instances.get(1).getStatus()).isEqualTo(((scope) == (SignalEventTest.Scope.PROCESS_INSTANCE) ? ProcessInstance.STATE_ACTIVE : ProcessInstance.STATE_COMPLETED));
        } else if ((strategy) == (Strategy.PROCESS_INSTANCE)) {
            Assertions.assertThat(instances.get(1).getStatus()).isEqualTo(((scope) == (SignalEventTest.Scope.PROJECT) ? ProcessInstance.STATE_COMPLETED : ProcessInstance.STATE_ACTIVE));
        } 
    }

    @Test
    public void testSignalEndThrowIntermediateCatch() {
        String throwingProcessFile;
        String throwingProcessId;
        switch (scope) {
            case DEFAULT :
                throwingProcessFile = SignalEventTest.END_THROW_DEFAULT;
                throwingProcessId = SignalEventTest.END_THROW_DEFAULT_ID;
                break;
            case PROJECT :
                throwingProcessFile = SignalEventTest.END_THROW_PROJECT;
                throwingProcessId = SignalEventTest.END_THROW_PROJECT_ID;
                break;
            case PROCESS_INSTANCE :
                throwingProcessFile = SignalEventTest.END_THROW_INSTANCE;
                throwingProcessId = SignalEventTest.END_THROW_INSTANCE_ID;
                break;
            default :
                throw new IllegalArgumentException("unknown scope");
        }
        createRuntimeManager(strategy, null, throwingProcessFile, SignalEventTest.INTERMEDIATE_CATCH);
        getKieSession().startProcess(SignalEventTest.INTERMEDIATE_CATCH_ID);
        getKieSession().startProcess(throwingProcessId);
        List<? extends ProcessInstanceLog> instances = getRuntimeEngine().getAuditService().findProcessInstances();
        Assertions.assertThat(instances).hasSize(2);
        Assertions.assertThat(instances.get(0).getProcessId()).isEqualTo(SignalEventTest.INTERMEDIATE_CATCH_ID);
        Assertions.assertThat(instances.get(1).getProcessId()).isEqualTo(throwingProcessId);
        if ((strategy) == (Strategy.SINGLETON)) {
            Assertions.assertThat(instances.get(0).getStatus()).isEqualTo(((scope) == (SignalEventTest.Scope.PROCESS_INSTANCE) ? ProcessInstance.STATE_ACTIVE : ProcessInstance.STATE_COMPLETED));
            Assertions.assertThat(instances.get(1).getStatus()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        } else if ((strategy) == (Strategy.PROCESS_INSTANCE)) {
            Assertions.assertThat(instances.get(0).getStatus()).isEqualTo(((scope) == (SignalEventTest.Scope.PROJECT) ? ProcessInstance.STATE_COMPLETED : ProcessInstance.STATE_ACTIVE));
            Assertions.assertThat(instances.get(1).getStatus()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        } 
    }

    @Test
    public void testSignalIntermediateThrowStartCatch() {
        String throwingProcessFile;
        String throwingProcessId;
        switch (scope) {
            case DEFAULT :
                throwingProcessFile = SignalEventTest.INTERMEDIATE_THROW_DEFAULT;
                throwingProcessId = SignalEventTest.INTERMEDIATE_THROW_DEFAULT_ID;
                break;
            case PROJECT :
                throwingProcessFile = SignalEventTest.INTERMEDIATE_THROW_PROJECT;
                throwingProcessId = SignalEventTest.INTERMEDIATE_THROW_PROJECT_ID;
                break;
            case PROCESS_INSTANCE :
                throwingProcessFile = SignalEventTest.INTERMEDIATE_THROW_INSTANCE;
                throwingProcessId = SignalEventTest.INTERMEDIATE_THROW_INSTANCE_ID;
                break;
            default :
                throw new IllegalArgumentException("unknown scope");
        }
        createRuntimeManager(strategy, null, throwingProcessFile, SignalEventTest.START_CATCH);
        getKieSession().startProcess(throwingProcessId);
        boolean run = ((((strategy) == (Strategy.SINGLETON)) && ((scope) != (SignalEventTest.Scope.PROCESS_INSTANCE))) || (((strategy) == (Strategy.PROCESS_INSTANCE)) && ((scope) == (SignalEventTest.Scope.PROJECT)))) || (((strategy) == (Strategy.PROCESS_INSTANCE)) && ((scope) == (SignalEventTest.Scope.DEFAULT)));
        List<? extends ProcessInstanceLog> instances = getRuntimeEngine().getAuditService().findProcessInstances();
        Assertions.assertThat(instances).hasSize((run ? 2 : 1));
        Assertions.assertThat(instances.get(0).getProcessId()).isEqualTo(throwingProcessId);
        Assertions.assertThat(instances.get(0).getStatus()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        if (run) {
            Assertions.assertThat(instances.get(1).getProcessId()).isEqualTo(SignalEventTest.START_CATCH_ID);
            Assertions.assertThat(instances.get(1).getStatus()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        } 
    }

    @Test
    public void testSignalSubProcess() {
        String throwingProcessFile;
        String throwingProcessId;
        switch (scope) {
            case DEFAULT :
                throwingProcessFile = SignalEventTest.INTERMEDIATE_THROW_DEFAULT;
                throwingProcessId = SignalEventTest.INTERMEDIATE_THROW_DEFAULT_ID;
                break;
            case PROJECT :
                throwingProcessFile = SignalEventTest.INTERMEDIATE_THROW_PROJECT;
                throwingProcessId = SignalEventTest.INTERMEDIATE_THROW_PROJECT_ID;
                break;
            case PROCESS_INSTANCE :
                throwingProcessFile = SignalEventTest.INTERMEDIATE_THROW_INSTANCE;
                throwingProcessId = SignalEventTest.INTERMEDIATE_THROW_INSTANCE_ID;
                break;
            default :
                throw new IllegalArgumentException("unknown scope");
        }
        createRuntimeManager(strategy, null, SignalEventTest.SUBPROCESS_CATCH, throwingProcessFile, SignalEventTest.INTERMEDIATE_CATCH);
        getKieSession().startProcess(SignalEventTest.INTERMEDIATE_CATCH_ID);
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("subprocess", throwingProcessId);
        getKieSession().startProcess(SignalEventTest.SUBPROCESS_CATCH_ID, parameters);
        List<? extends ProcessInstanceLog> instances = getRuntimeEngine().getAuditService().findProcessInstances();
        Assertions.assertThat(instances).hasSize(3);
        Assertions.assertThat(instances.get(0).getProcessId()).isEqualTo(SignalEventTest.INTERMEDIATE_CATCH_ID);
        Assertions.assertThat(instances.get(1).getProcessId()).isEqualTo(SignalEventTest.SUBPROCESS_CATCH_ID);
        Assertions.assertThat(instances.get(2).getProcessId()).isEqualTo(throwingProcessId);
        if ((strategy) == (Strategy.SINGLETON)) {
            Assertions.assertThat(instances.get(0).getStatus()).isEqualTo(((scope) == (SignalEventTest.Scope.PROCESS_INSTANCE) ? ProcessInstance.STATE_ACTIVE : ProcessInstance.STATE_COMPLETED));
            Assertions.assertThat(instances.get(1).getStatus()).isEqualTo(((scope) == (SignalEventTest.Scope.PROCESS_INSTANCE) ? ProcessInstance.STATE_ACTIVE : ProcessInstance.STATE_COMPLETED));
        } else if ((strategy) == (Strategy.PROCESS_INSTANCE)) {
            Assertions.assertThat(instances.get(0).getStatus()).isEqualTo(((scope) == (SignalEventTest.Scope.PROJECT) ? ProcessInstance.STATE_COMPLETED : ProcessInstance.STATE_ACTIVE));
            Assertions.assertThat(instances.get(1).getStatus()).isEqualTo(((scope) == (SignalEventTest.Scope.PROJECT) ? ProcessInstance.STATE_COMPLETED : ProcessInstance.STATE_ACTIVE));
        } 
        Assertions.assertThat(instances.get(2).getStatus()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }
}

