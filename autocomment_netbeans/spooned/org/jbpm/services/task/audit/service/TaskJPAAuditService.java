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


package org.jbpm.services.task.audit.service;

import org.kie.internal.task.query.AuditTaskDeleteBuilder;
import org.jbpm.services.task.audit.impl.model.AuditTaskImpl;
import org.kie.internal.task.query.AuditTaskQueryBuilder;
import org.jbpm.services.task.audit.impl.model.BAMTaskSummaryImpl;
import org.jbpm.services.task.audit.BAMTaskSummaryQueryBuilder;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.jbpm.process.audit.JPAAuditLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jbpm.query.jpa.impl.QueryCriteriaUtil;
import org.kie.internal.task.query.TaskEventDeleteBuilder;
import org.jbpm.services.task.audit.impl.model.TaskEventImpl;
import org.kie.internal.task.query.TaskEventQueryBuilder;
import org.kie.internal.task.query.TaskVariableQueryBuilder;

public class TaskJPAAuditService extends JPAAuditLogService {
    private static final Logger logger = LoggerFactory.getLogger(TaskJPAAuditService.class);

    static {
        addCriteria(CREATED_ON_LIST, "l.createdOn", Date.class);
        addCriteria(DEPLOYMENT_ID_LIST, "l.deploymentId", String.class);
        addCriteria(TASK_EVENT_DATE_ID_LIST, "l.logTime", Date.class);
        addCriteria(TASK_ID_LIST, "l.taskId", Long.class);
        addCriteria(TASK_NAME_LIST, "l.name", String.class);
        addCriteria(TASK_DESCRIPTION_LIST, "l.description", String.class);
        addCriteria(TASK_STATUS_LIST, "l.status", String.class);
    }

    public TaskJPAAuditService() {
        super();
    }

    public TaskJPAAuditService(EntityManagerFactory emf) {
        super(emf);
    }

    // Methods needed by the TaskAuditQueryCriteriaUtil
    protected EntityManager getEntityManager() {
        return super.getEntityManager();
    }

    protected Object joinTransaction(EntityManager em) {
        return super.joinTransaction(em);
    }

    protected void closeEntityManager(EntityManager em, Object transaction) {
        super.closeEntityManager(em, transaction);
    }

    // Query API Delete methods ---------------------------------------------------------------------------------------------------
    public AuditTaskDeleteBuilder auditTaskDelete() {
        return new AuditTaskDeleteBuilderImpl(TaskJPAAuditService.this);
    }

    public TaskEventDeleteBuilder taskEventInstanceLogDelete() {
        return new TaskEventDeleteBuilderImpl(TaskJPAAuditService.this);
    }

    @Override
    public void clear() {
        try {
            super.clear();
        } catch (Exception e) {
            TaskJPAAuditService.logger.warn("Unable to clear using {} due to {}", super.getClass().getName(), e.getMessage());
        }
        auditTaskDelete().build().execute();
        taskEventInstanceLogDelete().build().execute();
    }

    // Query API Query methods ---------------------------------------------------------------------------------------------------
    public AuditTaskQueryBuilder auditTaskQuery() {
        return new AuditTaskQueryBuilderImpl(TaskJPAAuditService.this);
    }

    public BAMTaskSummaryQueryBuilder bamTaskSummaryQuery() {
        return new BAMTaskSummaryQueryBuilderImpl(TaskJPAAuditService.this);
    }

    public TaskEventQueryBuilder taskEventQuery() {
        return new TaskEventQueryBuilderImpl(TaskJPAAuditService.this);
    }

    public TaskVariableQueryBuilder taskVariableQuery() {
        return new TaskVariableQueryBuilderImpl(TaskJPAAuditService.this);
    }

    private final TaskAuditQueryCriteriaUtil queryCriteriaUtil = new TaskAuditQueryCriteriaUtil(TaskJPAAuditService.this);

    @Override
    protected QueryCriteriaUtil getQueryCriteriaUtil(Class queryType) {
        if (((queryType.equals(AuditTaskImpl.class)) || (queryType.equals(TaskEventImpl.class))) || (queryType.equals(BAMTaskSummaryImpl.class))) {
            return queryCriteriaUtil;
        } else {
            return super.getQueryCriteriaUtil(queryType);
        }
    }
}

