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

/**
 * This strategy is used by instances that are<ul>
 * <li>used outside the {@link KieSession}</li>
 * <li>use their own {@link EntityManager} instance per operation</li>
 * </ul>
 */
public class StandaloneLocalStrategy implements PersistenceStrategy {
    protected EntityManagerFactory emf;

    public StandaloneLocalStrategy(EntityManagerFactory emf) {
        StandaloneLocalStrategy.this.emf = emf;
    }

    @Override
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public Object joinTransaction(EntityManager em) {
        em.getTransaction().begin();
        return true;
    }

    @Override
    public void leaveTransaction(EntityManager em, Object transaction) {
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void dispose() {
        // NEVER close the emf, you don't know what it is also being used for!
        emf = null;
    }
}

