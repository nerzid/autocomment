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


package org.jbpm.persistence.processinstance;

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.After;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import java.util.Collection;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.runtime.Environment;
import java.util.HashMap;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import java.util.List;
import java.util.Map;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.jbpm.persistence.util.PersistenceUtil;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.internal.io.ResourceFactory;
import org.junit.runner.RunWith;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.junit.Test;

@RunWith(value = Parameterized.class)
public class ParameterMappingTest extends AbstractBaseTest {
    private HashMap<String, Object> context;

    private static final String PROCESS_ID = "org.jbpm.processinstance.subprocess";

    private static final String SUBPROCESS_ID = "org.jbpm.processinstance.helloworld";

    private StatefulKnowledgeSession ksession;

    private ParameterMappingTest.ProcessListener listener;

    public ParameterMappingTest(boolean locking) {
        this.useLocking = locking;
    }

    @Parameters
    public static Collection<Object[]> persistence() {
        Object[][] data = new Object[][]{ new Object[]{ false } , new Object[]{ true } };
        return Arrays.asList(data);
    }

    @Before
    public void before() {
        context = PersistenceUtil.setupWithPoolingDataSource(PersistenceUtil.JBPM_PERSISTENCE_UNIT_NAME);
        Environment env = PersistenceUtil.createEnvironment(context);
        if (useLocking) {
            env.set(EnvironmentName.USE_PESSIMISTIC_LOCKING, true);
        } 
        ksession = JPAKnowledgeService.newStatefulKnowledgeSession(createKnowledgeBase(), null, env);
        Assert.assertTrue("Valid KnowledgeSession could not be created.", (((ksession) != null) && ((ksession.getIdentifier()) > 0)));
        listener = new ParameterMappingTest.ProcessListener();
        ksession.addEventListener(listener);
    }

    private KnowledgeBase createKnowledgeBase() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("processinstance/Subprocess.rf"), ResourceType.DRF);
        kbuilder.add(ResourceFactory.newClassPathResource("processinstance/HelloWorld.rf"), ResourceType.DRF);
        return kbuilder.newKnowledgeBase();
    }

    @After
    public void after() {
        if ((ksession) != null) {
            ksession.dispose();
        } 
        PersistenceUtil.cleanUp(context);
    }

    // org.jbpm.processinstance.subprocess
    @Test
    public void testChangingVariableByScript() throws Exception {
        Map<String, Object> mapping = new HashMap<String, Object>();
        mapping.put("type", "script");
        mapping.put("var", "value");
        ksession.startProcess(ParameterMappingTest.PROCESS_ID, mapping);
        Assert.assertTrue(listener.isProcessStarted(ParameterMappingTest.PROCESS_ID));
        Assert.assertTrue(listener.isProcessStarted(ParameterMappingTest.SUBPROCESS_ID));
        Assert.assertTrue(listener.isProcessCompleted(ParameterMappingTest.SUBPROCESS_ID));
        Assert.assertTrue(listener.isProcessCompleted(ParameterMappingTest.PROCESS_ID));
    }

    @Test
    public void testChangingVariableByEvent() throws Exception {
        Map<String, Object> mapping = new HashMap<String, Object>();
        mapping.put("type", "event");
        mapping.put("var", "value");
        ksession.startProcess(ParameterMappingTest.PROCESS_ID, mapping).getId();
        ksession.signalEvent("pass", "new value");
        Assert.assertTrue(listener.isProcessStarted(ParameterMappingTest.PROCESS_ID));
        Assert.assertTrue(listener.isProcessStarted(ParameterMappingTest.SUBPROCESS_ID));
        Assert.assertTrue(listener.isProcessCompleted(ParameterMappingTest.SUBPROCESS_ID));
        Assert.assertTrue(listener.isProcessCompleted(ParameterMappingTest.PROCESS_ID));
    }

    @Test
    public void testChangingVariableByEventSignalWithProcessId() throws Exception {
        Map<String, Object> mapping = new HashMap<String, Object>();
        mapping.put("type", "event");
        mapping.put("var", "value");
        long processId = ksession.startProcess(ParameterMappingTest.PROCESS_ID, mapping).getId();
        ksession.signalEvent("pass", "new value", processId);
        Assert.assertTrue(listener.isProcessStarted(ParameterMappingTest.PROCESS_ID));
        Assert.assertTrue(listener.isProcessStarted(ParameterMappingTest.SUBPROCESS_ID));
        Assert.assertTrue(listener.isProcessCompleted(ParameterMappingTest.SUBPROCESS_ID));
        Assert.assertTrue(listener.isProcessCompleted(ParameterMappingTest.PROCESS_ID));
    }

    @Test
    public void testNotChangingVariable() throws Exception {
        Map<String, Object> mapping = new HashMap<String, Object>();
        mapping.put("type", "default");
        mapping.put("var", "value");
        ksession.startProcess(ParameterMappingTest.PROCESS_ID, mapping);
        Assert.assertTrue(listener.isProcessStarted(ParameterMappingTest.PROCESS_ID));
        Assert.assertTrue(listener.isProcessStarted(ParameterMappingTest.SUBPROCESS_ID));
        Assert.assertTrue(listener.isProcessCompleted(ParameterMappingTest.SUBPROCESS_ID));
        Assert.assertTrue(listener.isProcessCompleted(ParameterMappingTest.PROCESS_ID));
    }

    @Test
    public void testNotSettingVariable() throws Exception {
        Map<String, Object> mapping = new HashMap<String, Object>();
        mapping.put("type", "default");
        ksession.startProcess(ParameterMappingTest.PROCESS_ID, mapping);
        Assert.assertTrue(listener.isProcessStarted(ParameterMappingTest.PROCESS_ID));
        Assert.assertTrue(listener.isProcessStarted(ParameterMappingTest.SUBPROCESS_ID));
        Assert.assertTrue(listener.isProcessCompleted(ParameterMappingTest.SUBPROCESS_ID));
        Assert.assertTrue(listener.isProcessCompleted(ParameterMappingTest.PROCESS_ID));
    }

    public static class ProcessListener extends DefaultProcessEventListener {
        private final List<String> processesStarted = new ArrayList<String>();

        private final List<String> processesCompleted = new ArrayList<String>();

        public void afterProcessStarted(ProcessStartedEvent event) {
            processesStarted.add(event.getProcessInstance().getProcessId());
        }

        public void afterProcessCompleted(ProcessCompletedEvent event) {
            processesCompleted.add(event.getProcessInstance().getProcessId());
        }

        public boolean isProcessStarted(String processId) {
            return processesStarted.contains(processId);
        }

        public boolean isProcessCompleted(String processId) {
            return processesCompleted.contains(processId);
        }
    }
}

