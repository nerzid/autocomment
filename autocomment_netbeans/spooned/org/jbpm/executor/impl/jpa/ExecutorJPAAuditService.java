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


package org.jbpm.executor.impl.jpa;

import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.jbpm.executor.entities.ErrorInfo;
import org.kie.internal.runtime.manager.audit.query.ErrorInfoDeleteBuilder;
import org.kie.internal.runtime.manager.audit.query.ErrorInfoQueryBuilder;
import org.jbpm.query.jpa.impl.QueryCriteriaUtil;
import org.kie.internal.query.QueryParameterIdentifiers;
import org.jbpm.executor.entities.RequestInfo;
import org.kie.internal.runtime.manager.audit.query.RequestInfoLogDeleteBuilder;
import org.kie.internal.runtime.manager.audit.query.RequestInfoQueryBuilder;
import org.kie.api.executor.STATUS;
import org.jbpm.services.task.audit.service.TaskJPAAuditService;

public class ExecutorJPAAuditService extends TaskJPAAuditService {
    public ExecutorJPAAuditService(EntityManagerFactory emf) {
        super(emf);
    }

    @Override
    protected EntityManager getEntityManager() {
        return super.getEntityManager();
    }

    @Override
    protected Object joinTransaction(EntityManager em) {
        return super.joinTransaction(em);
    }

    @Override
    protected void closeEntityManager(EntityManager em, Object transaction) {
        super.closeEntityManager(em, transaction);
    }

    // Delete Query API -----------------------------------------------------------------------------------------------------------
    static {
        addCriteria(QueryParameterIdentifiers.EXECUTOR_TIME_LIST, "l.time", Date.class);
        addCriteria(QueryParameterIdentifiers.EXECUTOR_STATUS_LIST, "l.status", STATUS.class);
    }

    public ErrorInfoDeleteBuilder errorInfoLogDeleteBuilder() {
        return new ErrorInfoDeleteBuilderImpl(ExecutorJPAAuditService.this);
    }

    public RequestInfoLogDeleteBuilder requestInfoLogDeleteBuilder() {
        return new RequestInfoDeleteBuilderImpl(ExecutorJPAAuditService.this);
    }

    // Query Query API -----------------------------------------------------------------------------------------------------------
    private final ExecutorQueryCriteriaUtil queryUtil = new ExecutorQueryCriteriaUtil(ExecutorJPAAuditService.this);

    @Override
    protected QueryCriteriaUtil getQueryCriteriaUtil(Class queryType) {
        if ((ErrorInfo.class.equals(queryType)) || (RequestInfo.class.equals(queryType))) {
            return queryUtil;
        } else {
            return super.getQueryCriteriaUtil(queryType);
        }
    }

    public ErrorInfoQueryBuilder errorInfoQueryBuilder() {
        return new ErrorInfoQueryBuilderImpl(ExecutorJPAAuditService.this);
    }

    public RequestInfoQueryBuilder requestInfoQueryBuilder() {
        return new RequestInfoQueryBuilderImpl(ExecutorJPAAuditService.this);
    }
}

