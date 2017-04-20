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

import org.kie.internal.command.Context;
import org.kie.internal.task.api.AuditTask;
import org.jbpm.services.task.utils.ClassUtil;
import java.util.List;
import org.kie.internal.query.QueryFilter;
import org.jbpm.services.task.commands.TaskCommand;
import org.kie.internal.task.api.TaskContext;
import org.kie.internal.task.api.TaskPersistenceContext;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "get-all-audit-tasks-by-user-command")
@XmlAccessorType(value = XmlAccessType.NONE)
public class GetAllHistoryAuditTasksByUserCommand extends TaskCommand<List<AuditTask>> {
    private QueryFilter filter;

    private String owner;

    public GetAllHistoryAuditTasksByUserCommand() {
        this.filter = new QueryFilter(0, 0);
    }

    public GetAllHistoryAuditTasksByUserCommand(String owner, QueryFilter filter) {
        this.owner = owner;
        this.filter = filter;
    }

    @Override
    public List<AuditTask> execute(Context context) {
        TaskPersistenceContext persistenceContext = ((TaskContext) (context)).getPersistenceContext();
        return persistenceContext.queryWithParametersInTransaction("getAllAuditTasksByUser", persistenceContext.addParametersToMap("owner", owner, "firstResult", filter.getOffset(), "maxResults", filter.getCount()), ClassUtil.<List<AuditTask>>castClass(List.class));
    }
}

