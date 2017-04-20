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


package org.jbpm.kie.services.impl.cmd;

import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.command.Context;
import org.drools.core.command.impl.GenericCommand;
import javax.xml.bind.annotation.XmlSchemaType;
import org.kie.api.runtime.KieSession;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.kie.internal.command.ProcessInstanceIdCommand;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(value = XmlAccessType.NONE)
@XmlRootElement(name = "start-process-instance-with-parent-command")
public class StartProcessInstanceWithParentCommand implements GenericCommand<ProcessInstance> , ProcessInstanceIdCommand {
    /**
     * * Generated serial version UID
     */
    private static final long serialVersionUID = 7634752111656248015L;

    @XmlAttribute(required = true)
    @XmlSchemaType(name = "long")
    private Long processInstanceId;

    @XmlAttribute(required = true)
    @XmlSchemaType(name = "long")
    private Long parentProcessInstanceId;

    public StartProcessInstanceWithParentCommand() {
        // JAXB constructor
    }

    public StartProcessInstanceWithParentCommand(Long processInstanceId, Long parentProcessInstanceId) {
        this.processInstanceId = processInstanceId;
        this.parentProcessInstanceId = parentProcessInstanceId;
    }

    public Long getParentProcessInstanceId() {
        return parentProcessInstanceId;
    }

    public void setParentProcessInstanceId(Long parentProcessInstanceId) {
        this.parentProcessInstanceId = parentProcessInstanceId;
    }

    @Override
    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    @Override
    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    @Override
    public ProcessInstance execute(Context context) {
        KieSession ksession = ((KnowledgeCommandContext) (context)).getKieSession();
        ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId.longValue());
        if ((parentProcessInstanceId) > 0) {
            ((ProcessInstanceImpl) (processInstance)).setMetaData("ParentProcessInstanceId", parentProcessInstanceId);
        }
        return ksession.startProcessInstance(processInstanceId.longValue());
    }
}

