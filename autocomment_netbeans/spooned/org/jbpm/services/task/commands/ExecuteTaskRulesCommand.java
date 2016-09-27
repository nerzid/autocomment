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


package org.jbpm.services.task.commands;

import org.kie.internal.command.Context;
import org.drools.core.xml.jaxb.util.JaxbMapAdapter;
import java.util.Map;
import org.kie.api.task.model.Task;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

@XmlRootElement(name = "execute-task-rules-command")
@XmlAccessorType(value = XmlAccessType.NONE)
public class ExecuteTaskRulesCommand extends TaskCommand<Void> {
    private static final long serialVersionUID = 1852525453931482868L;

    @XmlElement
    @XmlJavaTypeAdapter(value = JaxbMapAdapter.class)
    protected Map<String, Object> data;

    @XmlElement
    @XmlSchemaType(name = "string")
    protected String scope;

    public ExecuteTaskRulesCommand() {
    }

    public ExecuteTaskRulesCommand(long taskId, String userId, Map<String, Object> data, String scope) {
        this.taskId = taskId;
        this.userId = userId;
        ExecuteTaskRulesCommand.this.data = data;
        ExecuteTaskRulesCommand.this.scope = scope;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        ExecuteTaskRulesCommand.this.data = data;
    }

    @Override
    public Void execute(Context ctx) {
        TaskContext context = ((TaskContext) (ctx));
        Task task = context.getTaskQueryService().getTaskInstanceById(taskId);
        context.getTaskRuleService().executeRules(task, userId, data, scope);
        return null;
    }
}

