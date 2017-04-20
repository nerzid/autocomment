/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.executor.cdi;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.enterprise.inject.Produces;
import org.jbpm.shared.services.impl.TransactionalCommandService;

@ApplicationScoped
public class ExecutorDatabaseProducer {
    private EntityManagerFactory emf;

    @PersistenceUnit(unitName = "org.jbpm.executor")
    @ApplicationScoped
    @Produces
    public EntityManagerFactory getEntityManagerFactory() {
        if ((this.emf) == null) {
            // this needs to be here for non EE containers
            this.emf = Persistence.createEntityManagerFactory("org.jbpm.executor");
        }
        return this.emf;
    }

    @Produces
    public TransactionalCommandService produceCommandService(EntityManagerFactory emf) {
        return new TransactionalCommandService(emf);
    }
}

