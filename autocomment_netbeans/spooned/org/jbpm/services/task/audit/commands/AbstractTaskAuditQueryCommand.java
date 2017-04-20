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
import java.util.List;
import org.jbpm.query.jpa.impl.QueryCriteriaUtil;
import org.jbpm.query.jpa.data.QueryWhere;
import org.jbpm.services.task.audit.service.TaskAuditQueryCriteriaUtil;
import org.jbpm.services.task.commands.TaskCommand;
import org.jbpm.services.task.commands.TaskContext;

public abstract class AbstractTaskAuditQueryCommand<R, Q> extends TaskCommand<List<R>> {
    public AbstractTaskAuditQueryCommand() {
        // JAXB constructor
    }

    public abstract QueryWhere getQueryWhere();

    protected abstract Class<R> getResultType();

    protected abstract Class<Q> getQueryType();

    @Override
    public List<R> execute(Context cntxt) {
        TaskContext context = ((TaskContext) (cntxt));
        TaskAuditQueryCriteriaUtil queryCriteriaUtil = new TaskAuditQueryCriteriaUtil(context.getPersistenceContext());
        List<Q> implResult = queryCriteriaUtil.doCriteriaQuery(getQueryWhere(), getQueryType());
        return QueryCriteriaUtil.convertListToInterfaceList(implResult, getResultType());
    }
}

