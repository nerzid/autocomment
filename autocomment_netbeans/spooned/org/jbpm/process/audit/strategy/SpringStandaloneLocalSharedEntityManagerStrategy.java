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


package org.jbpm.process.audit.strategy;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class SpringStandaloneLocalSharedEntityManagerStrategy implements PersistenceStrategy {
    private EntityManager em;

    private boolean manageTx;

    public SpringStandaloneLocalSharedEntityManagerStrategy(EntityManagerFactory emf) {
        SpringStandaloneLocalSharedEntityManagerStrategy.this.em = emf.createEntityManager();
        SpringStandaloneLocalSharedEntityManagerStrategy.this.manageTx = true;
    }

    public SpringStandaloneLocalSharedEntityManagerStrategy(EntityManager em) {
        SpringStandaloneLocalSharedEntityManagerStrategy.this.em = em;
        SpringStandaloneLocalSharedEntityManagerStrategy.this.manageTx = false;
    }

    @Override
    public EntityManager getEntityManager() {
        return SpringStandaloneLocalSharedEntityManagerStrategy.this.em;
    }

    @Override
    public Object joinTransaction(EntityManager em) {
        if (manageTx) {
            em.getTransaction().begin();
        } 
        return manageTx;
    }

    @Override
    public void leaveTransaction(EntityManager em, Object transaction) {
        if (manageTx) {
            em.getTransaction().commit();
        } 
    }

    @Override
    public void dispose() {
        // do nothing, because the em is SHARED..
        em = null;
    }
}

