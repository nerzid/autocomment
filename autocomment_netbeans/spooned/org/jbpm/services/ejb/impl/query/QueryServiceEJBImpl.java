/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.services.ejb.impl.query;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import org.kie.internal.identity.IdentityProvider;
import javax.inject.Inject;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.annotation.PostConstruct;
import org.jbpm.services.api.query.QueryAlreadyRegisteredException;
import org.jbpm.services.api.query.model.QueryDefinition;
import org.jbpm.services.api.query.QueryNotFoundException;
import org.jbpm.services.ejb.api.query.QueryServiceEJBLocal;
import org.jbpm.services.ejb.api.query.QueryServiceEJBRemote;
import org.jbpm.kie.services.impl.query.QueryServiceImpl;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.jbpm.services.ejb.impl.tx.TransactionalCommandServiceEJBImpl;

@Singleton
@ConcurrencyManagement(value = ConcurrencyManagementType.CONTAINER)
@Lock(value = LockType.READ)
public class QueryServiceEJBImpl extends QueryServiceImpl implements QueryServiceEJBLocal , QueryServiceEJBRemote {
    @Inject
    private Instance<IdentityProvider> identityProvider;

    @Resource
    private EJBContext context;

    // inject resources
    @PostConstruct
    public void configure() {
        if (identityProvider.isUnsatisfied()) {
            setIdentityProvider(new org.jbpm.services.ejb.impl.identity.EJBContextIdentityProvider(context));
        } else {
            setIdentityProvider(identityProvider.get());
        }
        super.init();
    }

    @EJB(beanInterface = TransactionalCommandServiceEJBImpl.class)
    @Override
    public void setCommandService(TransactionalCommandService commandService) {
        super.setCommandService(commandService);
    }

    @Lock(value = LockType.WRITE)
    @Override
    public void registerQuery(QueryDefinition queryDefinition) throws QueryAlreadyRegisteredException {
        super.registerQuery(queryDefinition);
    }

    @Lock(value = LockType.WRITE)
    @Override
    public void replaceQuery(QueryDefinition queryDefinition) {
        super.replaceQuery(queryDefinition);
    }

    @Lock(value = LockType.WRITE)
    @Override
    public void unregisterQuery(String uniqueQueryName) throws QueryNotFoundException {
        super.unregisterQuery(uniqueQueryName);
    }
}

