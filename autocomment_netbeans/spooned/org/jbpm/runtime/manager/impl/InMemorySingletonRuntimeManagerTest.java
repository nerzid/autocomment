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


package org.jbpm.runtime.manager.impl;

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.api.runtime.KieSession;
import java.util.Properties;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.junit.Test;
import org.jbpm.runtime.manager.util.TestUtil;
import org.kie.internal.task.api.UserGroupCallback;

public class InMemorySingletonRuntimeManagerTest extends AbstractBaseTest {
    private UserGroupCallback userGroupCallback;

    private RuntimeManager manager;

    @Before
    public void setup() {
        TestUtil.cleanupSingletonSessionId();
        Properties properties = new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        userGroupCallback = new org.jbpm.services.task.identity.JBossUserGroupCallbackImpl(properties);
    }

    @After
    public void teardown() {
        if ((manager) != null) {
            manager.close();
        } 
    }

    @Test
    public void testCreationOfSessionInMemory() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultInMemoryBuilder().userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2).get();
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        Assert.assertNotNull(manager);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        Assert.assertNotNull(ksession);
        long sessionId = ksession.getIdentifier();
        Assert.assertTrue((sessionId == 0));
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();
        Assert.assertEquals(sessionId, ksession.getIdentifier());
        // dispose session that should not have affect on the session at all
        manager.disposeRuntimeEngine(runtime);
        ksession = manager.getRuntimeEngine(EmptyContext.get()).getKieSession();
        Assert.assertEquals(sessionId, ksession.getIdentifier());
        // close manager which will close session maintained by the manager
        manager.close();
    }
}

