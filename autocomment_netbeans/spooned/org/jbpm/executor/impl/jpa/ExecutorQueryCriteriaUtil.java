

package org.jbpm.executor.impl.jpa;

import QueryParameterIdentifiers.COMMAND_NAME_LIST;
import java.util.concurrent.ConcurrentHashMap;
import javax.persistence.metamodel.Attribute;
import javax.persistence.criteria.CriteriaBuilder;
import QueryParameterIdentifiers.DEPLOYMENT_ID_LIST;
import RequestInfo_.key;
import RequestInfo_.status;
import org.jbpm.query.jpa.data.QueryCriteria;
import RequestInfo_.commandName;
import QueryParameterIdentifiers.EXECUTOR_EXECUTIONS_LIST;
import javax.persistence.EntityManager;
import ErrorInfo_.id;
import QueryParameterIdentifiers.STACK_TRACE_LIST;
import org.jbpm.executor.entities.ErrorInfo_;
import javax.persistence.criteria.Predicate;
import RequestInfo_.executions;
import RequestInfo_.retries;
import RequestInfo_.deploymentId;
import QueryParameterIdentifiers.EXECUTOR_KEY_LIST;
import QueryParameterIdentifiers.EXECUTOR_OWNER_LIST;
import QueryParameterIdentifiers.EXECUTOR_RETRIES_LIST;
import QueryParameterIdentifiers.EXECUTOR_STATUS_LIST;
import javax.persistence.Query;
import RequestInfo_.owner;
import org.jbpm.query.jpa.data.QueryWhere;
import java.util.Map;
import org.jbpm.query.jpa.impl.QueryCriteriaUtil;
import ErrorInfo_.stacktrace;
import QueryParameterIdentifiers.EXECUTOR_TIME_LIST;
import ErrorInfo_.message;
import QueryParameterIdentifiers.MESSAGE_LIST;
import ErrorInfo_.time;
import QueryParameterIdentifiers.ID_LIST;
import java.util.List;

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
        // add criteria Map{ExecutorQueryCriteriaUtil.criteriaAttributes} to ExecutorQueryCriteriaUtil{}
        addCriteria(ExecutorQueryCriteriaUtil.criteriaAttributes, MESSAGE_LIST, message);
        // add criteria Map{ExecutorQueryCriteriaUtil.criteriaAttributes} to ExecutorQueryCriteriaUtil{}
        addCriteria(ExecutorQueryCriteriaUtil.criteriaAttributes, ID_LIST, id);
        // add criteria Map{ExecutorQueryCriteriaUtil.criteriaAttributes} to ExecutorQueryCriteriaUtil{}
        addCriteria(ExecutorQueryCriteriaUtil.criteriaAttributes, STACK_TRACE_LIST, stacktrace);
        // add criteria Map{ExecutorQueryCriteriaUtil.criteriaAttributes} to ExecutorQueryCriteriaUtil{}
        addCriteria(ExecutorQueryCriteriaUtil.criteriaAttributes, EXECUTOR_TIME_LIST, time);
        // RequestInfo
        // add criteria Map{ExecutorQueryCriteriaUtil.criteriaAttributes} to ExecutorQueryCriteriaUtil{}
        addCriteria(ExecutorQueryCriteriaUtil.criteriaAttributes, COMMAND_NAME_LIST, commandName);
        // add criteria Map{ExecutorQueryCriteriaUtil.criteriaAttributes} to ExecutorQueryCriteriaUtil{}
        addCriteria(ExecutorQueryCriteriaUtil.criteriaAttributes, DEPLOYMENT_ID_LIST, deploymentId);
        // add criteria Map{ExecutorQueryCriteriaUtil.criteriaAttributes} to ExecutorQueryCriteriaUtil{}
        addCriteria(ExecutorQueryCriteriaUtil.criteriaAttributes, EXECUTOR_EXECUTIONS_LIST, executions);
        // add criteria Map{ExecutorQueryCriteriaUtil.criteriaAttributes} to ExecutorQueryCriteriaUtil{}
        addCriteria(ExecutorQueryCriteriaUtil.criteriaAttributes, ID_LIST, RequestInfo_.id);
        // add criteria Map{ExecutorQueryCriteriaUtil.criteriaAttributes} to ExecutorQueryCriteriaUtil{}
        addCriteria(ExecutorQueryCriteriaUtil.criteriaAttributes, EXECUTOR_KEY_LIST, key);
        // add criteria Map{ExecutorQueryCriteriaUtil.criteriaAttributes} to ExecutorQueryCriteriaUtil{}
        addCriteria(ExecutorQueryCriteriaUtil.criteriaAttributes, MESSAGE_LIST, RequestInfo_.message);
        // add criteria Map{ExecutorQueryCriteriaUtil.criteriaAttributes} to ExecutorQueryCriteriaUtil{}
        addCriteria(ExecutorQueryCriteriaUtil.criteriaAttributes, EXECUTOR_OWNER_LIST, owner);
        // add criteria Map{ExecutorQueryCriteriaUtil.criteriaAttributes} to ExecutorQueryCriteriaUtil{}
        addCriteria(ExecutorQueryCriteriaUtil.criteriaAttributes, EXECUTOR_RETRIES_LIST, retries);
        // add criteria Map{ExecutorQueryCriteriaUtil.criteriaAttributes} to ExecutorQueryCriteriaUtil{}
        addCriteria(ExecutorQueryCriteriaUtil.criteriaAttributes, EXECUTOR_STATUS_LIST, status);
        // add criteria Map{ExecutorQueryCriteriaUtil.criteriaAttributes} to ExecutorQueryCriteriaUtil{}
        addCriteria(ExecutorQueryCriteriaUtil.criteriaAttributes, EXECUTOR_TIME_LIST, RequestInfo_.time);
        return true;
    }

    // Implementation specific logic ----------------------------------------------------------------------------------------------
    private ExecutorJPAAuditService executorAuditService;

    public ExecutorQueryCriteriaUtil(ExecutorJPAAuditService service) {
        super(ExecutorQueryCriteriaUtil.criteriaAttributes);
        this.executorAuditService = service;
    }

    private EntityManager getEntityManager() {
        return this.executorAuditService.getEntityManager();
    }

    private Object joinTransaction(EntityManager em) {
        return this.executorAuditService.joinTransaction(em);
    }

    private void closeEntityManager(EntityManager em, Object transaction) {
        // close entity EntityManager{em} to ExecutorJPAAuditService{this.executorAuditService}
        this.executorAuditService.closeEntityManager(em, transaction);
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
        // apply meta Query{query} to ExecutorQueryCriteriaUtil{}
        applyMetaCriteriaToQuery(query, queryWhere);
        // execute query
        List<T> result = query.getResultList();
        // close entity EntityManager{em} to ExecutorQueryCriteriaUtil{}
        closeEntityManager(em, newTx);
        return result;
    }

    @Override
    protected <R, T> Predicate implSpecificCreatePredicateFromSingleCriteria(CriteriaQuery<R> query, CriteriaBuilder builder, Class queryType, QueryCriteria criteria, QueryWhere queryWhere) {
        throw new IllegalStateException((((("List id " + (criteria.getListId())) + " is not supported for queries on ") + (queryType.getSimpleName())) + "."));
    }
}

