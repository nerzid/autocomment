/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.process.instance.command;

import org.kie.internal.command.ProcessInstanceIdCommand;
import org.kie.internal.command.Context;
import org.drools.core.command.impl.GenericCommand;
import org.kie.api.runtime.KieSession;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.kie.api.runtime.process.ProcessInstance;
import ProcessInstance.STATE_SUSPENDED;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlAccessType;
import org.kie.api.runtime.process.org.jbpm.process.instance.ProcessInstance;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "suspend-process-instance-command")
@XmlAccessorType(value = XmlAccessType.NONE)
public class SuspendProcessInstanceCommand implements GenericCommand<Object> , ProcessInstanceIdCommand {
    /**
     * * Generated serial version UID
     */
    private static final long serialVersionUID = 5824052805419980114L;

    @XmlAttribute
    @XmlSchemaType(name = "long")
    private Long processInstanceId;

    @Override
    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    @Override
    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public Object execute(Context context) {
        KieSession ksession = ((KnowledgeCommandContext) (context)).getKieSession();
        if ((processInstanceId) == null) {
            return null;
        }
        ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
        if (processInstance == null) {
            throw new IllegalArgumentException(("Could not find process instance for id " + (processInstanceId)));
        }
        if ((processInstance.getState()) != (ProcessInstance.STATE_ACTIVE)) {
            throw new IllegalArgumentException(((("Process instance with id " + (processInstanceId)) + " in state ") + (processInstance.getState())));
        }
        // set state void{STATE_SUSPENDED} to ProcessInstance{((org.jbpm.process.instance.ProcessInstance) (processInstance))}
        ((org.jbpm.process.instance.ProcessInstance) (processInstance)).setState(STATE_SUSPENDED);
        return null;
    }

    public String toString() {
        return ("session.abortProcessInstance(" + (processInstanceId)) + ");";
    }
}

