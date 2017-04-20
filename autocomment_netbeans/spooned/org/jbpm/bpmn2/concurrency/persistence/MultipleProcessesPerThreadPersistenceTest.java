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


package org.jbpm.bpmn2.concurrency.persistence;

import java.util.HashMap;
import org.junit.After;
import org.junit.Before;
import org.kie.api.runtime.Environment;
import org.junit.Ignore;
import PersistenceUtil.JBPM_PERSISTENCE_UNIT_NAME;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.KnowledgeBase;
import org.jbpm.bpmn2.concurrency.MultipleProcessesPerThreadTest;
import org.jbpm.persistence.util.PersistenceUtil;
import org.kie.internal.runtime.StatefulKnowledgeSession;

/**
 * Class to reproduce bug with multiple threads using persistence and each
 * configures its own entity manager.
 *
 * This test costs time and resources, please only run locally for the time being.
 */
@Ignore
public class MultipleProcessesPerThreadPersistenceTest extends MultipleProcessesPerThreadTest {
    private static HashMap<String, Object> context;

    @Before
    public void setup() {
        MultipleProcessesPerThreadPersistenceTest.context = PersistenceUtil.setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME);
    }

    @After
    public void tearDown() {
        // clean up HashMap{MultipleProcessesPerThreadPersistenceTest.context} to void{PersistenceUtil}
        PersistenceUtil.cleanUp(MultipleProcessesPerThreadPersistenceTest.context);
    }

    protected static StatefulKnowledgeSession createStatefulKnowledgeSession(KnowledgeBase kbase) {
        Environment env = PersistenceUtil.createEnvironment(MultipleProcessesPerThreadPersistenceTest.context);
        return JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
    }
}

