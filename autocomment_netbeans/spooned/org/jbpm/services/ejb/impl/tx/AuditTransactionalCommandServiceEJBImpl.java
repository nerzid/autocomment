/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.services.ejb.impl.tx;

import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorManager;
import javax.persistence.EntityManagerFactory;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import javax.persistence.PersistenceUnit;
import javax.ejb.Stateless;
import org.jbpm.shared.services.impl.TransactionalCommandService;

@Stateless
public class AuditTransactionalCommandServiceEJBImpl extends TransactionalCommandService {
    @PersistenceUnit(unitName = "org.jbpm.domain")
    @Override
    public void setEmf(EntityManagerFactory emf) {
        DeploymentDescriptorManager manager = new DeploymentDescriptorManager("org.jbpm.domain");
        DeploymentDescriptor descriptor = manager.getDefaultDescriptor();
        if (!("org.jbpm.domain".equals(descriptor.getAuditPersistenceUnit()))) {
            super.setEmf(EntityManagerFactoryManager.get().getOrCreate(descriptor.getAuditPersistenceUnit()));
        }else {
            super.setEmf(emf);
        }
    }

    public AuditTransactionalCommandServiceEJBImpl() {
        super(null);
        // entity manager will be set by setter method
    }
}

