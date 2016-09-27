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
import java.util.List;
import org.kie.internal.task.api.model.TaskDef;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

@XmlRootElement(name = "get-all-task-definitions-command")
@XmlAccessorType(value = XmlAccessType.NONE)
public class GetAllTaskDefinitionsCommand extends TaskCommand<List<TaskDef>> {
    private static final long serialVersionUID = 5077599352603072633L;

    @XmlElement
    @XmlSchemaType(name = "string")
    private String filter;

    public GetAllTaskDefinitionsCommand() {
    }

    public GetAllTaskDefinitionsCommand(String filter) {
        GetAllTaskDefinitionsCommand.this.filter = filter;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        GetAllTaskDefinitionsCommand.this.filter = filter;
    }

    public List<TaskDef> execute(Context cntxt) {
        TaskContext context = ((TaskContext) (cntxt));
        return context.getTaskDefService().getAllTaskDef(filter);
    }
}

