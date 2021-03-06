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


package org.jbpm.services.task.audit.commands;

import org.jbpm.query.jpa.data.QueryWhere;
import org.kie.internal.task.api.TaskVariable;
import org.jbpm.services.task.audit.impl.model.TaskVariableImpl;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "task-variable-query-command")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class TaskVariableQueryCommand extends AbstractTaskAuditQueryCommand<TaskVariable, TaskVariableImpl> {
    /**
     * * generated serial version UID
     */
    private static final long serialVersionUID = -6879337395030142688L;

    @XmlElement
    private QueryWhere queryWhere;

    public TaskVariableQueryCommand() {
        // JAXB constructor
    }

    public TaskVariableQueryCommand(QueryWhere queryWhere) {
        TaskVariableQueryCommand.this.queryWhere = queryWhere;
    }

    public QueryWhere getQueryWhere() {
        return queryWhere;
    }

    public void setQueryWhere(QueryWhere queryWhere) {
        TaskVariableQueryCommand.this.queryWhere = queryWhere;
    }

    @Override
    protected Class<TaskVariable> getResultType() {
        return TaskVariable.class;
    }

    @Override
    protected Class<TaskVariableImpl> getQueryType() {
        return TaskVariableImpl.class;
    }
}

