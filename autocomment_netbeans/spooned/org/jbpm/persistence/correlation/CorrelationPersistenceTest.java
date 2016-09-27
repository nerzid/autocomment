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


package org.jbpm.persistence.correlation;

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.After;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import java.util.Collection;
import org.kie.internal.process.CorrelationKeyFactory;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import javax.naming.InitialContext;
import java.util.List;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.jbpm.persistence.util.PersistenceUtil;
import javax.persistence.Query;
import org.junit.runner.RunWith;
import org.junit.Test;
import javax.transaction.UserTransaction;

@RunWith(value = Parameterized.class)
public class CorrelationPersistenceTest extends AbstractBaseTest {
    private HashMap<String, Object> context;

    public CorrelationPersistenceTest(boolean locking) {
        this.useLocking = locking;
    }

    @Parameters
    public static Collection<Object[]> persistence() {
        Object[][] data = new Object[][]{ new Object[]{ false } , new Object[]{ true } };
        return Arrays.asList(data);
    }

    @Before
    public void before() throws Exception {
        context = PersistenceUtil.setupWithPoolingDataSource(PersistenceUtil.JBPM_PERSISTENCE_UNIT_NAME);
        CorrelationKeyFactory factory = KieInternalServices.Factory.get().newCorrelationKeyFactory();
        // populate table with test data
        EntityManagerFactory emf = ((EntityManagerFactory) (context.get(EnvironmentName.ENTITY_MANAGER_FACTORY)));
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        EntityManager em = emf.createEntityManager();
        em.persist(factory.newCorrelationKey("test123"));
        List<String> props = new ArrayList<String>();
        props.add("test123");
        props.add("123test");
        em.persist(factory.newCorrelationKey(props));
        ut.commit();
    }

    @After
    public void after() {
        try {
            EntityManagerFactory emf = ((EntityManagerFactory) (context.get(EnvironmentName.ENTITY_MANAGER_FACTORY)));
            UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
            ut.begin();
            EntityManager em = emf.createEntityManager();
            em.createQuery("delete from CorrelationPropertyInfo").executeUpdate();
            em.createQuery("delete from CorrelationKeyInfo").executeUpdate();
            ut.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        PersistenceUtil.cleanUp(context);
    }

    @Test
    public void testCreateCorrelation() throws Exception {
        EntityManagerFactory emf = ((EntityManagerFactory) (context.get(EnvironmentName.ENTITY_MANAGER_FACTORY)));
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("GetProcessInstanceIdByCorrelation");
        query.setParameter("properties", Arrays.asList(new String[]{ "test123" }));
        query.setParameter("elem_count", new Long(1));
        List<Long> processInstances = query.getResultList();
        em.close();
        Assert.assertNotNull(processInstances);
        Assert.assertEquals(1, processInstances.size());
    }

    @Test
    public void testCreateCorrelationMultiValueDoesNotMatch() throws Exception {
        EntityManagerFactory emf = ((EntityManagerFactory) (context.get(EnvironmentName.ENTITY_MANAGER_FACTORY)));
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("GetProcessInstanceIdByCorrelation");
        query.setParameter("properties", Arrays.asList(new String[]{ "test123" }));
        query.setParameter("elem_count", new Long(2));
        List<Long> processInstances = query.getResultList();
        em.close();
        Assert.assertNotNull(processInstances);
        Assert.assertEquals(0, processInstances.size());
    }

    @Test
    public void testCreateCorrelationMultiValueDoesMatch() throws Exception {
        EntityManagerFactory emf = ((EntityManagerFactory) (context.get(EnvironmentName.ENTITY_MANAGER_FACTORY)));
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("GetProcessInstanceIdByCorrelation");
        query.setParameter("properties", Arrays.asList(new String[]{ "test123" , "123test" }));
        query.setParameter("elem_count", new Long(2));
        List<Long> processInstances = query.getResultList();
        em.close();
        Assert.assertNotNull(processInstances);
        Assert.assertEquals(1, processInstances.size());
    }
}

