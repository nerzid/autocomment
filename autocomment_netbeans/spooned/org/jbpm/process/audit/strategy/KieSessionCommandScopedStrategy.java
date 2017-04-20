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

import EnvironmentName.CMD_SCOPED_ENTITY_MANAGER;
import javax.persistence.EntityManager;
import org.kie.api.runtime.Environment;

/**
 * This strategy is used by instances that are<ul>
 * <li>used inside the {@link KieSession}</li>
 * <li>use the same (command-scoped) {@link EntityManager} instance as the {@link KieSession}</li>
 * </ul>
 */
public class KieSessionCommandScopedStrategy implements PersistenceStrategy {
    private Environment env;

    public KieSessionCommandScopedStrategy(Environment env) {
        this.env = env;
    }

    @Override
    public EntityManager getEntityManager() {
        EntityManager em = ((EntityManager) (env.get(CMD_SCOPED_ENTITY_MANAGER)));
        if (em == null) {
            throw new IllegalStateException("The command scoped entity manager could not be found!");
        }
        return em;
    }

    @Override
    public Object joinTransaction(EntityManager em) {
        // This is taken care of by the SingleSessionCommandService
        return false;
    }

    @Override
    public void leaveTransaction(EntityManager em, Object transaction) {
        // This is taken care of by the SingleSessionCommandService
    }

    @Override
    public void dispose() {
        env = null;
    }
}

