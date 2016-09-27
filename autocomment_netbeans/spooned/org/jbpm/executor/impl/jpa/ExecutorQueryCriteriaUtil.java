

package org.jbpm.executor.impl.jpa;

import javax.persistence.metamodel.Attribute;
import java.util.concurrent.ConcurrentHashMap;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.EntityManager;
import org.jbpm.executor.entities.ErrorInfo_;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.Predicate;
import javax.persistence.Query;
import org.jbpm.query.jpa.data.QueryCriteria;
import org.jbpm.query.jpa.impl.QueryCriteriaUtil;
import org.kie.internal.query.QueryParameterIdentifiers;
import org.jbpm.query.jpa.data.QueryWhere;

public class ExecutorQueryCriteriaUtil extends QueryCriteriaUtil {
    // Query Field Info -----------------------------------------------------------------------------------------------------------
    public static final Map<Class, Map<String, Attribute>> criteriaAttributes = new ConcurrentHashMap<Class, Map<String, Attribute>>();

    @Override
    protected synchronized boolean initializeCriteriaAttributes() {
        if ((ErrorInfo_.id) == null) {
            // EMF/persistence has not been initialized:
            // When a persistence unit (EntityManagerFactory) is initialized,
            // the fields of classes annotated with @StaticMetamodel are filled using reflection
            return false;
        } 
        // do not do initialization twice (slow performance, otherwise it doesn't matter)
        if (!(ExecutorQueryCriteriaUtil.criteriaAttributes.isEmpty())) {
            return true;
        } 
        // ErrorInfoImpl
        addCriteria(ExecutorQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.MESSAGE_LIST, ErrorInfo_.message);
        addCriteria(ExecutorQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.ID_LIST, ErrorInfo_.id);
        addCriteria(ExecutorQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.STACK_TRACE_LIST, ErrorInfo_.stacktrace);
        addCriteria(ExecutorQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.EXECUTOR_TIME_LIST, ErrorInfo_.time);
        // RequestInfo
        addCriteria(ExecutorQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.COMMAND_NAME_LIST, RequestInfo_.commandName);
        addCriteria(ExecutorQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.DEPLOYMENT_ID_LIST, RequestInfo_.deploymentId);
        addCriteria(ExecutorQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.EXECUTOR_EXECUTIONS_LIST, RequestInfo_.executions);
        addCriteria(ExecutorQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.ID_LIST, RequestInfo_.id);
        addCriteria(ExecutorQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.EXECUTOR_KEY_LIST, RequestInfo_.key);
        addCriteria(ExecutorQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.MESSAGE_LIST, RequestInfo_.message);
        addCriteria(ExecutorQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.EXECUTOR_OWNER_LIST, RequestInfo_.owner);
        addCriteria(ExecutorQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.EXECUTOR_RETRIES_LIST, RequestInfo_.retries);
        addCriteria(ExecutorQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.EXECUTOR_STATUS_LIST, RequestInfo_.status);
        addCriteria(ExecutorQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.EXECUTOR_TIME_LIST, RequestInfo_.time);
        return true;
    }

    // Implementation specific logic ----------------------------------------------------------------------------------------------
    private ExecutorJPAAuditService executorAuditService;

    public ExecutorQueryCriteriaUtil(ExecutorJPAAuditService service) {
        super(ExecutorQueryCriteriaUtil.criteriaAttributes);
        ExecutorQueryCriteriaUtil.this.executorAuditService = service;
    }

    private EntityManager getEntityManager() {
        return ExecutorQueryCriteriaUtil.this.executorAuditService.getEntityManager();
    }

    private Object joinTransaction(EntityManager em) {
        return ExecutorQueryCriteriaUtil.this.executorAuditService.joinTransaction(em);
    }

    private void closeEntityManager(EntityManager em, Object transaction) {
        ExecutorQueryCriteriaUtil.this.executorAuditService.closeEntityManager(em, transaction);
    }

    // Implementation specific methods --------------------------------------------------------------------------------------------
    protected CriteriaBuilder getCriteriaBuilder() {
        return getEntityManager().getCriteriaBuilder();
    }

    @Override
    protected <T> List<T> createQueryAndCallApplyMetaCriteriaAndGetResult(QueryWhere queryWhere, CriteriaQuery<T> criteriaQuery, CriteriaBuilder builder) {
        EntityManager em = getEntityManager();
        Object newTx = joinTransaction(em);
        Query query = em.createQuery(criteriaQuery);
        applyMetaCriteriaToQuery(query, queryWhere);
        // execute query
        List<T> result = query.getResultList();
        closeEntityManager(em, newTx);
        return result;
    }

    @Override
    protected <R, T> Predicate implSpecificCreatePredicateFromSingleCriteria(CriteriaQuery<R> query, CriteriaBuilder builder, Class queryType, QueryCriteria criteria, QueryWhere queryWhere) {
        throw new IllegalStateException((((("List id " + (criteria.getListId())) + " is not supported for queries on ") + (queryType.getSimpleName())) + "."));
    }
}

