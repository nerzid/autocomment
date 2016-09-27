/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.jbpm.process.audit;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.kie.api.runtime.Environment;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.persistence.Persistence;
import org.jbpm.process.audit.strategy.PersistenceStrategy;
import org.jbpm.process.audit.strategy.PersistenceStrategyType;
import javax.persistence.Query;

/**
 * </p>
 * The idea here is that we have a entity manager factory (similar to a session
 * factory) that we repeatedly use to generate an entity manager (which is a
 * persistence context) for the specific service command.
 * </p>
 * To start with, not all of our entities contain LOB's ("Large Objects" see
 * https://en.wikibooks.org/wiki/Java_Persistence/Basic_Attributes#LOBs.2C_BLOBs
 * .2C_CLOBs_and_Serialization) which sometimes necessitate the use of tx's even
 * in <i>read</i> situations.
 * </p>
 * However, we use transactions here none-the-less, just to be safe. Obviously,
 * if there is already a running transaction present, we don't do anything to
 * it.
 * </p>
 * At the end of every command or operation, make sure to close the entity
 * manager you've been using -- which also means that you should detach any
 * entities that might be associated with the entity manager/persistence
 * context.
 * </p>
 * After all, this is a <i>service</i> which means our philosophy here is to
 * provide a real interface, and not a leaky absraction.
 * (https://en.wikipedia.org/wiki/Leaky_abstraction)
 */
public class JPAService {
    private static final Logger logger = LoggerFactory.getLogger(JPAService.class);

    protected PersistenceStrategy persistenceStrategy;

    protected String persistenceUnitName;

    public JPAService(String persistenceUnitName) {
        JPAService.this.persistenceUnitName = persistenceUnitName;
        EntityManagerFactory emf = null;
        try {
            emf = Persistence.createEntityManagerFactory(persistenceUnitName);
        } catch (Exception e) {
            JPAService.logger.info(((("The '" + persistenceUnitName) + "' peristence unit is not available, no persistence strategy set for ") + (JPAService.this.getClass().getSimpleName())));
        }
        if (emf != null) {
            persistenceStrategy = new org.jbpm.process.audit.strategy.StandaloneJtaStrategy(emf);
        } 
    }

    public JPAService(Environment env, PersistenceStrategyType type) {
        persistenceStrategy = PersistenceStrategyType.getPersistenceStrategy(type, env);
    }

    public JPAService(Environment env, String peristenceUnitName) {
        EntityManagerFactory emf = ((EntityManagerFactory) (env.get(EnvironmentName.ENTITY_MANAGER_FACTORY)));
        String localTransactions = ((String) (env.get(EnvironmentName.USE_LOCAL_TRANSACTIONS)));
        if (emf != null) {
            if (localTransactions != null) {
                if (localTransactions.equals("true")) {
                    persistenceStrategy = new org.jbpm.process.audit.strategy.StandaloneLocalStrategy(emf);
                } 
            } else {
                persistenceStrategy = new org.jbpm.process.audit.strategy.StandaloneJtaStrategy(emf);
            }
        } else {
            if (localTransactions != null) {
                if (localTransactions.equals("true")) {
                    persistenceStrategy = new org.jbpm.process.audit.strategy.StandaloneLocalStrategy(Persistence.createEntityManagerFactory(persistenceUnitName));
                } 
            } else {
                persistenceStrategy = new org.jbpm.process.audit.strategy.StandaloneJtaStrategy(Persistence.createEntityManagerFactory(persistenceUnitName));
            }
        }
    }

    public JPAService(EntityManagerFactory emf) {
        persistenceStrategy = new org.jbpm.process.audit.strategy.StandaloneJtaStrategy(emf);
    }

    public JPAService(EntityManagerFactory emf, PersistenceStrategyType type) {
        persistenceStrategy = PersistenceStrategyType.getPersistenceStrategy(type, emf);
    }

    public void setPersistenceUnitName(String persistenceUnitName) {
        persistenceStrategy = new org.jbpm.process.audit.strategy.StandaloneJtaStrategy(Persistence.createEntityManagerFactory(persistenceUnitName));
        JPAService.this.persistenceUnitName = persistenceUnitName;
    }

    public String getPersistenceUnitName() {
        return persistenceUnitName;
    }

    public void dispose() {
        persistenceStrategy.dispose();
    }

    // DO NOT MAKE THIS METHOD PUBLIC!
    // This is an internal method, and we do NOT want to expose the entity
    // manager to users or other logic!
    protected EntityManager getEntityManager() {
        return persistenceStrategy.getEntityManager();
    }

    // DO NOT MAKE THIS METHOD PUBLIC!
    // This is an internal method, and we do NOT want to expose the entity
    // manager to users or other logic!
    protected Object joinTransaction(EntityManager em) {
        return persistenceStrategy.joinTransaction(em);
    }

    // DO NOT MAKE THIS METHOD PUBLIC!
    // This is an internal method, and we do NOT want to expose the entity
    // manager to users or other logic!
    protected void closeEntityManager(EntityManager em, Object transaction) {
        persistenceStrategy.leaveTransaction(em, transaction);
    }

    public <T> List<T> executeQuery(Query query, EntityManager em, Class<T> type) {
        Object newTx = joinTransaction(em);
        List<T> result;
        try {
            result = query.getResultList();
        } finally {
            closeEntityManager(em, newTx);
        }
        return result;
    }
}

