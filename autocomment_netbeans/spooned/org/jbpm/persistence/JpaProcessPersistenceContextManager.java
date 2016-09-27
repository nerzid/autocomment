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


package org.jbpm.persistence;

import javax.persistence.EntityManager;
import org.kie.api.runtime.Environment;
import org.drools.persistence.jpa.JpaPersistenceContextManager;

public class JpaProcessPersistenceContextManager extends JpaPersistenceContextManager implements ProcessPersistenceContextManager {
    public JpaProcessPersistenceContextManager(Environment env) {
        super(env);
    }

    public ProcessPersistenceContext getProcessPersistenceContext() {
        Boolean locking = ((Boolean) (env.get(EnvironmentName.USE_PESSIMISTIC_LOCKING)));
        if (locking == null) {
            locking = false;
        } 
        boolean useJTA = true;
        return new JpaProcessPersistenceContext(getCommandScopedEntityManager(), useJTA, locking, txm);
    }

    @Override
    public EntityManager getCommandScopedEntityManager() {
        EntityManager em = super.getCommandScopedEntityManager();
        // ensure em is set in the environment to cover situation when em is taken from tx directly
        // when using per process instance runtime strategy
        if ((env.get(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER)) == null) {
            env.set(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER, em);
        } 
        return em;
    }
}

