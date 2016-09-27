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


package org.jbpm.bpmn2.persistence;

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.After;
import org.junit.Before;
import java.util.Calendar;
import org.jbpm.test.util.CountDownProcessEventListener;
import org.kie.api.runtime.Environment;
import java.util.GregorianCalendar;
import java.util.HashMap;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.jbpm.bpmn2.concurrency.MultipleProcessesPerThreadTest;
import org.jbpm.persistence.util.PersistenceUtil;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.io.ResourceFactory;
import java.text.SimpleDateFormat;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.junit.Test;

public class UnmarshallingOverdueTimersTest extends AbstractBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(MultipleProcessesPerThreadTest.class);

    private HashMap<String, Object> context;

    @Before
    public void setup() {
        context = PersistenceUtil.setupWithPoolingDataSource(PersistenceUtil.JBPM_PERSISTENCE_UNIT_NAME);
    }

    @After
    public void tearDown() throws Exception {
        PersistenceUtil.cleanUp(context);
    }

    private static KnowledgeBase loadKnowledgeBase(String bpmn2FileName) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource(bpmn2FileName, UnmarshallingOverdueTimersTest.class), ResourceType.BPMN2);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        return kbase;
    }

    private StatefulKnowledgeSession createStatefulKnowledgeSession(KnowledgeBase kbase) {
        Environment env = PersistenceUtil.createEnvironment(context);
        return JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
    }

    private static long knowledgeSessionDispose(StatefulKnowledgeSession ksession) {
        long ksessionId = ksession.getIdentifier();
        UnmarshallingOverdueTimersTest.logger.debug("disposing of ksesssion");
        ksession.dispose();
        return ksessionId;
    }

    private StatefulKnowledgeSession reloadStatefulKnowledgeSession(String bpmn2FileName, int ksessionId) {
        KnowledgeBase kbase = UnmarshallingOverdueTimersTest.loadKnowledgeBase(bpmn2FileName);
        UnmarshallingOverdueTimersTest.logger.debug("reloading ksession {}", ksessionId);
        Environment env = null;
        env = PersistenceUtil.createEnvironment(context);
        return JPAKnowledgeService.loadStatefulKnowledgeSession(ksessionId, kbase, null, env);
    }

    private static long seconds = 2;

    private static String timeUnit = "s";

    private static String bpmn2FileName = "BPMN2-TimerInterrupted.bpmn2";

    private static boolean debug = true;

    @Test(timeout = 10000)
    public void startDisposeAndReloadTimerProcess() throws Exception {
        CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("timer", 1);
        if (UnmarshallingOverdueTimersTest.debug) {
            String shellVar = "TEST";
            String shellVarVal = System.getenv(shellVar);
            if (shellVarVal != null) {
                UnmarshallingOverdueTimersTest.debug = false;
            } 
        } 
        String sessionPropName = "KSESSION_ID";
        String sessionPropVal = System.getenv(sessionPropName);
        String processPropName = "PROCESS_ID";
        String processPropVal = System.getenv(sessionPropName);
        if ((sessionPropVal == null) || (UnmarshallingOverdueTimersTest.debug)) {
            KnowledgeBase kbase = UnmarshallingOverdueTimersTest.loadKnowledgeBase(UnmarshallingOverdueTimersTest.bpmn2FileName);
            StatefulKnowledgeSession ksession = createStatefulKnowledgeSession(kbase);
            ksession.addEventListener(countDownListener);
            // setup parameters
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("time", ((UnmarshallingOverdueTimersTest.seconds) + (UnmarshallingOverdueTimersTest.timeUnit)));
            // note process start time
            Calendar cal = GregorianCalendar.getInstance();
            // start process
            ProcessInstance processInstance = ksession.startProcess("interruptedTimer", params);
            long processId = processInstance.getId();
            // print info for next test
            if (UnmarshallingOverdueTimersTest.debug) {
                processPropVal = Long.toString(processId);
            } else {
                UnmarshallingOverdueTimersTest.logger.info("export {}={}", processPropName, processId);
            }
            // dispose of session
            KieSessionConfiguration config = ksession.getSessionConfiguration();
            long ksessionId = UnmarshallingOverdueTimersTest.knowledgeSessionDispose(ksession);
            // print info for next test
            if (UnmarshallingOverdueTimersTest.debug) {
                sessionPropVal = Long.toString(ksessionId);
            } else {
                UnmarshallingOverdueTimersTest.logger.info("export {}={}", sessionPropName, ksessionId);
            }
            if (!(UnmarshallingOverdueTimersTest.debug)) {
                cal.add(Calendar.SECOND, ((int) (UnmarshallingOverdueTimersTest.seconds)));
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
                UnmarshallingOverdueTimersTest.logger.info("Please wait at least {} [{}]", ((UnmarshallingOverdueTimersTest.seconds) + (UnmarshallingOverdueTimersTest.timeUnit)), sdf.format(cal.getTime()));
            } 
        } 
        if ((sessionPropVal != null) || (UnmarshallingOverdueTimersTest.debug)) {
            // reload session
            int ksessionId = Integer.parseInt(sessionPropVal);
            StatefulKnowledgeSession ksession = reloadStatefulKnowledgeSession(UnmarshallingOverdueTimersTest.bpmn2FileName, ksessionId);
            ksession.addEventListener(countDownListener);
            long processInstanceId = Integer.parseInt(processPropVal);
            UnmarshallingOverdueTimersTest.logger.debug("! waiting 5 seconds for timer to fire");
            countDownListener.waitTillCompleted();
            ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
            if (processInstance != null) {
                assertTrue("Process has not terminated.", ((processInstance.getState()) == (ProcessInstance.STATE_COMPLETED)));
            } 
        } 
    }
}

