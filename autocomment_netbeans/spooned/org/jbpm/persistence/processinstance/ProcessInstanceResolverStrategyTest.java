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
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import org.drools.core.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import java.util.Collection;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.kie.api.runtime.Environment;
import java.util.HashMap;
import javax.naming.InitialContext;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.jbpm.persistence.processinstance.objects.NonSerializableClass;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.jbpm.persistence.util.PersistenceUtil;
import org.kie.api.runtime.process.ProcessInstance;
import org.junit.runner.RunWith;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.junit.Test;
import javax.transaction.UserTransaction;
import org.kie.api.runtime.process.WorkflowProcessInstance;

@RunWith(value = Parameterized.class)
public class ProcessInstanceResolverStrategyTest extends AbstractBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(ProcessInstanceResolverStrategyTest.class);

    private HashMap<String, Object> context;

    private StatefulKnowledgeSession ksession;

    private static final String RF_FILE = "SimpleProcess.rf";

    private static final String PROCESS_ID = "org.jbpm.persistence.TestProcess";

    private static final String VAR_NAME = "persistVar";

    public ProcessInstanceResolverStrategyTest(boolean locking) {
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
        // load up the knowledge base
        Environment env = PersistenceUtil.createEnvironment(context);
        env.set(OBJECT_MARSHALLING_STRATEGIES, new ObjectMarshallingStrategy[]{ new org.jbpm.marshalling.impl.ProcessInstanceResolverStrategy() , new org.drools.persistence.jpa.marshaller.JPAPlaceholderResolverStrategy(env) , new org.drools.core.marshalling.impl.SerializablePlaceholderResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT) });
        if (useLocking) {
            env.set(USE_PESSIMISTIC_LOCKING, true);
        } 
        KnowledgeBase kbase = loadKnowledgeBase();
        // create session
        ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        Assert.assertTrue("Valid KnowledgeSession could not be created.", (((ksession) != null) && ((ksession.getIdentifier()) > 0)));
    }

    private KnowledgeBase loadKnowledgeBase() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(new org.drools.core.io.impl.ClassPathResource(ProcessInstanceResolverStrategyTest.RF_FILE), ResourceType.DRF);
        KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        return kbase;
    }

    @After
    public void after() {
        if ((ksession) != null) {
            ksession.dispose();
        } 
        PersistenceUtil.cleanUp(context);
    }

    @Test
    public void testWithDatabaseAndStartProcess() throws Exception {
        // Create variable
        Map<String, Object> params = new HashMap<String, Object>();
        NonSerializableClass processVar = new NonSerializableClass();
        processVar.setString("1234567890");
        params.put(ProcessInstanceResolverStrategyTest.VAR_NAME, processVar);
        params.put("logger", ProcessInstanceResolverStrategyTest.logger);
        // Persist variable
        UserTransaction ut = ((UserTransaction) (new InitialContext().lookup("java:comp/UserTransaction")));
        ut.begin();
        EntityManagerFactory emf = ((EntityManagerFactory) (context.get(ENTITY_MANAGER_FACTORY)));
        EntityManager em = emf.createEntityManager();
        em.setFlushMode(FlushModeType.COMMIT);
        em.joinTransaction();
        em.persist(processVar);
        em.close();
        ut.commit();
        // Generate, insert, and start process
        ProcessInstance processInstance = ksession.startProcess(ProcessInstanceResolverStrategyTest.PROCESS_ID, params);
        // Test resuls
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        processVar = ((NonSerializableClass) (((WorkflowProcessInstance) (processInstance)).getVariable(ProcessInstanceResolverStrategyTest.VAR_NAME)));
        Assert.assertNotNull(processVar);
    }

    @Test
    public void testWithDatabaseAndStartProcessInstance() throws Exception {
        // Create variable
        Map<String, Object> params = new HashMap<String, Object>();
        NonSerializableClass processVar = new NonSerializableClass();
        processVar.setString("1234567890");
        params.put(ProcessInstanceResolverStrategyTest.VAR_NAME, processVar);
        // Persist variable
        UserTransaction ut = ((UserTransaction) (new InitialContext().lookup("java:comp/UserTransaction")));
        ut.begin();
        EntityManagerFactory emf = ((EntityManagerFactory) (context.get(ENTITY_MANAGER_FACTORY)));
        EntityManager em = emf.createEntityManager();
        em.setFlushMode(FlushModeType.COMMIT);
        em.joinTransaction();
        em.persist(processVar);
        em.close();
        ut.commit();
        // Create process,
        ProcessInstance processInstance = ksession.createProcessInstance(ProcessInstanceResolverStrategyTest.PROCESS_ID, params);
        long processInstanceId = processInstance.getId();
        Assert.assertTrue((processInstanceId > 0));
        Assert.assertEquals(ProcessInstance.STATE_PENDING, processInstance.getState());
        // insert process,
        ksession.insert(processInstance);
        // and start process
        ksession.startProcessInstance(processInstanceId);
        ksession.fireAllRules();
        // Test results
        processInstance = ksession.getProcessInstance(processInstanceId);
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        processVar = ((NonSerializableClass) (((WorkflowProcessInstance) (processInstance)).getVariable(ProcessInstanceResolverStrategyTest.VAR_NAME)));
        Assert.assertNotNull(processVar);
    }
}

