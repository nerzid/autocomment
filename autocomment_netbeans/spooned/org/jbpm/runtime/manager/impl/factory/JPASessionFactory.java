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


package org.jbpm.runtime.manager.impl.factory;

import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.SessionFactory;
import org.drools.persistence.SingleSessionCommandService;

/**
 * SessionFactory implementation that is backed by a database for storing <code>KieSession</code> data.
 */
public class JPASessionFactory implements SessionFactory {
    private RuntimeEnvironment environment;

    public JPASessionFactory(RuntimeEnvironment environment) {
        JPASessionFactory.this.environment = environment;
    }

    @Override
    public KieSession newKieSession() {
        KieSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(environment.getKieBase(), environment.getConfiguration(), environment.getEnvironment());
        addInterceptors(ksession);
        return ksession;
    }

    @Override
    public KieSession findKieSessionById(Long sessionId) {
        if (sessionId == null) {
            return null;
        } 
        KieSession ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId, environment.getKieBase(), environment.getConfiguration(), environment.getEnvironment());
        addInterceptors(ksession);
        return ksession;
    }

    @Override
    public void close() {
    }

    protected void addInterceptors(KieSession ksession) {
        SingleSessionCommandService sscs = ((SingleSessionCommandService) (((CommandBasedStatefulKnowledgeSession) (ksession)).getCommandService()));
        sscs.addInterceptor(new org.drools.persistence.jpa.OptimisticLockRetryInterceptor());
        // even though it's added always TransactionLockInterceptor is by default disabled so won't do anything
        sscs.addInterceptor(new org.drools.persistence.jta.TransactionLockInterceptor(ksession.getEnvironment()));
    }
}

