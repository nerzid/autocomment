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


package org.jbpm.persistence.jta;

import org.kie.internal.command.Context;
import org.drools.core.command.impl.GenericCommand;
import org.kie.api.runtime.KieSession;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.drools.persistence.TransactionManager;
import org.drools.persistence.TransactionSynchronization;

/**
 * Dispose command that is dedicated to be used in container managed environments instead of
 * default way of disposing ksession
 * <br/>
 * <code>ksession.dispose()</code>.
 * <br/>
 * If transaction is active it will delegate the actual disposal to afterCompletion
 * phase of transaction instead of executing it directly. Although when there is no active
 * transaction or no transaction at all it will dispose ksession immediately.
 *  <br/>
 * It relies on <code>TransactionManager</code> being available in ksession's environment:
 * <br/>
 * <code>ksession.getEnvironment().get(EnvironmentName.TRANSACTION_MANAGER)</code>
 */
public class ContainerManagedTransactionDisposeCommand implements GenericCommand<Void> {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(ContainerManagedTransactionDisposeCommand.class);

    @Override
    public Void execute(Context context) {
        final KieSession ksession = ((KnowledgeCommandContext) (context)).getKieSession();
        ContainerManagedTransactionDisposeCommand.logger.debug("Trying to dispose KieSession ({}). Checking for active transactions.", ksession);
        TransactionManager tm = ((TransactionManager) (ksession.getEnvironment().get(EnvironmentName.TRANSACTION_MANAGER)));
        int txStatus = TransactionManager.STATUS_NO_TRANSACTION;
        if (tm != null) {
            txStatus = tm.getStatus();
        } 
        if (txStatus == (TransactionManager.STATUS_NO_TRANSACTION)) {
            ContainerManagedTransactionDisposeCommand.logger.debug("No active transaction: disposing KieSession ({}) directly", ksession);
            ksession.dispose();
            ContainerManagedTransactionDisposeCommand.logger.debug("KieSession disposed {}", ksession);
        } else {
            try {
                ContainerManagedTransactionDisposeCommand.logger.debug("Active transaction: registering KieSession ({}) for dispose", ksession);
                tm.registerTransactionSynchronization(new TransactionSynchronization() {
                    @Override
                    public void beforeCompletion() {
                        // not used here
                    }

                    @Override
                    public void afterCompletion(int arg0) {
                        ksession.dispose();
                        ContainerManagedTransactionDisposeCommand.logger.debug("KieSession disposed {}", ksession);
                    }
                });
            } catch (Exception e) {
                ContainerManagedTransactionDisposeCommand.logger.error("Error while registering transaction synchronization for cmt dispose: {}", e.getMessage(), e);
            }
        }
        return null;
    }
}

