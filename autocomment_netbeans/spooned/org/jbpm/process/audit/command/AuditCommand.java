/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.process.audit.command;

import org.jbpm.process.audit.AuditLogService;
import org.kie.internal.command.Context;
import org.drools.core.command.impl.FixedKnowledgeCommandContext;
import org.drools.core.command.impl.GenericCommand;
import org.kie.api.runtime.KieSession;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.jbpm.process.audit.strategy.PersistenceStrategyType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

@XmlAccessorType(value = XmlAccessType.NONE)
public abstract class AuditCommand<T> implements GenericCommand<T> {
    @XmlTransient
    protected AuditLogService auditLogService = null;

    public AuditCommand() {
    }

    public void setAuditLogService(AuditLogService auditLogService) {
        AuditCommand.this.auditLogService = auditLogService;
    }

    protected void setLogEnvironment(Context cntxt) {
        if ((auditLogService) != null) {
            return ;
        } 
        if (!(cntxt instanceof KnowledgeCommandContext)) {
            throw new UnsupportedOperationException((("This command must be executed by a " + (KieSession.class.getSimpleName())) + " instance!"));
        } 
        KnowledgeCommandContext realContext = ((FixedKnowledgeCommandContext) (cntxt));
        AuditCommand.this.auditLogService = new org.jbpm.process.audit.JPAAuditLogService(realContext.getKieSession().getEnvironment(), PersistenceStrategyType.KIE_SESSION);
    }
}

