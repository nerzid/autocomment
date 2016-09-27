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


package org.jbpm.runtime.manager.impl.tx;

import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.core.command.CommandService;
import org.kie.internal.command.Context;
import org.drools.core.command.impl.GenericCommand;
import org.kie.api.runtime.KieSession;
import org.drools.persistence.OrderedTransactionSynchronization;
import org.drools.core.command.SingleSessionCommandService;

/**
 * Transaction synchronization implementation that destroys the <code>KieSession</code> instance
 * in the <code>beforeCompletion</code> call executed for the current transaction.
 */
public class DestroySessionTransactionSynchronization extends OrderedTransactionSynchronization {
    private KieSession ksession;

    public DestroySessionTransactionSynchronization(KieSession ksession) {
        super(5, ("DestroySessionTransactionSynchronization" + (ksession.getIdentifier())));
        DestroySessionTransactionSynchronization.this.ksession = ksession;
    }

    @Override
    public void beforeCompletion() {
        ksession.execute(new GenericCommand<Void>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Void execute(Context context) {
                if ((ksession) instanceof CommandBasedStatefulKnowledgeSession) {
                    CommandService commandService = ((CommandBasedStatefulKnowledgeSession) (ksession)).getCommandService();
                    ((SingleSessionCommandService) (commandService)).destroy();
                } 
                return null;
            }
        });
    }

    @Override
    public void afterCompletion(int status) {
    }
}

