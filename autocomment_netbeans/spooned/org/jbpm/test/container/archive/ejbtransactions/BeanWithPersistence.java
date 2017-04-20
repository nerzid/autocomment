/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.test.container.archive.ejbtransactions;

import EnvironmentName.ENTITY_MANAGER_FACTORY;
import javax.persistence.EntityManagerFactory;
import org.kie.api.runtime.Environment;
import org.kie.internal.KnowledgeBaseFactory;
import javax.persistence.PersistenceUnit;

/**
 * Common base of all EJBs using persistence. Contains: - entity manager factory
 * injection - session creation
 */
public abstract class BeanWithPersistence implements ProcessEJB {
    @PersistenceUnit(unitName = "containerPU")
    protected EntityManagerFactory emf;

    public EntityManagerFactory getEmf() {
        return this.emf;
    }

    protected Environment getEnvironment() {
        Environment env = KnowledgeBaseFactory.newEnvironment();
        // set void{ENTITY_MANAGER_FACTORY} to Environment{env}
        env.set(ENTITY_MANAGER_FACTORY, emf);
        return env;
    }
}

