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
import org.jbpm.query.jpa.data.QueryWhere;
import org.kie.api.task.model.TaskSummary;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "task-summary-query-command")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class TaskSummaryQueryCommand extends TaskCommand<List<TaskSummary>> {
    /**
     * * generated serial version UID
     */
    private static final long serialVersionUID = -6879337395030142688L;

    @XmlElement
    private QueryWhere queryWhere;

    public TaskSummaryQueryCommand() {
        // JAXB constructor
    }

    public TaskSummaryQueryCommand(QueryWhere queryWhere) {
        TaskSummaryQueryCommand.this.queryWhere = queryWhere;
    }

    public QueryWhere getQueryWhere() {
        return queryWhere;
    }

    public void setQueryWhere(QueryWhere queryWhere) {
        TaskSummaryQueryCommand.this.queryWhere = queryWhere;
    }

    @Override
    public List<TaskSummary> execute(Context cntxt) {
        TaskContext context = ((TaskContext) (cntxt));
        return context.getTaskQueryService().query(userId, queryWhere);
    }
}

