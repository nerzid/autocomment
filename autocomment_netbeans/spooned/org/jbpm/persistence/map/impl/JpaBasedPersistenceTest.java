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


package org.jbpm.persistence.map.impl;

import org.junit.After;
import java.util.Arrays;
import org.junit.Before;
import java.util.Collection;
import javax.persistence.EntityManagerFactory;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import java.util.HashMap;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.drools.persistence.jta.JtaTransactionManager;
import org.kie.api.KieBase;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.jbpm.persistence.util.PersistenceUtil;
import org.junit.runner.RunWith;
import org.kie.internal.runtime.StatefulKnowledgeSession;

@RunWith(value = Parameterized.class)
public class JpaBasedPersistenceTest extends MapPersistenceTest {
    private HashMap<String, Object> context;

    private EntityManagerFactory emf;

    private JtaTransactionManager txm;

    private boolean useTransactions = false;

    public JpaBasedPersistenceTest(boolean locking) {
        this.useLocking = locking;
    }

    @Parameters
    public static Collection<Object[]> persistence() {
        Object[][] data = new Object[][]{ new Object[]{ false } , new Object[]{ true } };
        return Arrays.asList(data);
    }

    @Before
    public void setUp() throws Exception {
        context = PersistenceUtil.PersistenceUtil.setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME);
        emf = ((EntityManagerFactory) (context.get(EnvironmentName.ENTITY_MANAGER_FACTORY)));
        if (useTransactions()) {
            useTransactions = true;
            Environment env = createEnvironment(context);
            Object tm = env.get(EnvironmentName.TRANSACTION_MANAGER);
            JpaBasedPersistenceTest.this.txm = new JtaTransactionManager(env.get(EnvironmentName.TRANSACTION), env.get(EnvironmentName.TRANSACTION_SYNCHRONIZATION_REGISTRY), tm);
        } 
    }

    @After
    public void tearDown() throws Exception {
        cleanUp(context);
    }

    @Override
    protected StatefulKnowledgeSession createSession(KieBase kbase) {
        Environment env = createEnvironment(context);
        if (JpaBasedPersistenceTest.this.useLocking) {
            env.set(EnvironmentName.USE_PESSIMISTIC_LOCKING, true);
        } 
        return JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
    }

    @Override
    protected StatefulKnowledgeSession disposeAndReloadSession(StatefulKnowledgeSession ksession, long ksessionId, KieBase kbase) {
        ksession.dispose();
        return JPAKnowledgeService.loadStatefulKnowledgeSession(ksessionId, kbase, null, createEnvironment(context));
    }

    @Override
    protected int getProcessInstancesCount() {
        boolean txOwner = false;
        if (useTransactions) {
            txOwner = txm.begin();
        } 
        int size = emf.createEntityManager().createQuery("FROM ProcessInstanceInfo").getResultList().size();
        if (useTransactions) {
            txm.commit(txOwner);
        } 
        return size;
    }

    @Override
    protected int getKnowledgeSessionsCount() {
        boolean transactionOwner = false;
        if (useTransactions) {
            transactionOwner = txm.begin();
        } 
        int size = emf.createEntityManager().createQuery("FROM SessionInfo").getResultList().size();
        if (useTransactions) {
            txm.commit(transactionOwner);
        } 
        return size;
    }
}

