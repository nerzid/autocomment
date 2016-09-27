

package org.jbpm.process.audit;

import javax.persistence.metamodel.Attribute;
import java.util.concurrent.ConcurrentHashMap;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.EntityManager;
import javax.persistence.criteria.Expression;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.Query;
import org.jbpm.query.jpa.data.QueryCriteria;
import org.jbpm.query.jpa.impl.QueryCriteriaUtil;
import org.kie.internal.query.QueryParameterIdentifiers;
import org.jbpm.query.jpa.data.QueryWhere;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.criteria.Subquery;

public class AuditQueryCriteriaUtil extends QueryCriteriaUtil {
    // Query Field Info -----------------------------------------------------------------------------------------------------------
    public static final Map<Class, Map<String, Attribute>> criteriaAttributes = new ConcurrentHashMap<Class, Map<String, Attribute>>();

    @Override
    protected synchronized boolean initializeCriteriaAttributes() {
        if ((ProcessInstanceLog_.correlationKey) == null) {
            // EMF/persistence has not been initialized:
            // When a persistence unit (EntityManagerFactory) is initialized,
            // the fields of classes annotated with @StaticMetamodel are filled using reflection
            return false;
        } 
        // do not do initialization twice (slow performance, otherwise it doesn't matter)
        if (!(AuditQueryCriteriaUtil.criteriaAttributes.isEmpty())) {
            return true;
        } 
        // ProcessInstanceLog
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.PROCESS_INSTANCE_ID_LIST, ProcessInstanceLog_.processInstanceId);
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.PROCESS_ID_LIST, ProcessInstanceLog_.processId);
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.START_DATE_LIST, ProcessInstanceLog_.start);
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.END_DATE_LIST, ProcessInstanceLog_.end);
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.PROCESS_INSTANCE_STATUS_LIST, ProcessInstanceLog_.status);
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.PROCESS_INSTANCE_PARENT_ID_LIST, ProcessInstanceLog_.parentProcessInstanceId);
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.OUTCOME_LIST, ProcessInstanceLog_.outcome);
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.DURATION_LIST, ProcessInstanceLog_.duration);
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.IDENTITY_LIST, ProcessInstanceLog_.identity);
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.PROCESS_VERSION_LIST, ProcessInstanceLog_.processVersion);
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.PROCESS_NAME_LIST, ProcessInstanceLog_.processName);
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.CORRELATION_KEY_LIST, ProcessInstanceLog_.correlationKey);
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.EXTERNAL_ID_LIST, ProcessInstanceLog_.externalId);
        // NodeInstanceLog
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.PROCESS_INSTANCE_ID_LIST, NodeInstanceLog_.processInstanceId);
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.PROCESS_ID_LIST, NodeInstanceLog_.processId);
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.EXTERNAL_ID_LIST, NodeInstanceLog_.externalId);
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.DATE_LIST, NodeInstanceLog_.date);
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.NODE_INSTANCE_ID_LIST, NodeInstanceLog_.nodeInstanceId);
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.NODE_ID_LIST, NodeInstanceLog_.nodeId);
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.NODE_NAME_LIST, NodeInstanceLog_.nodeName);
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.TYPE_LIST, NodeInstanceLog_.nodeType);
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.WORK_ITEM_ID_LIST, NodeInstanceLog_.workItemId);
        // Var
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.PROCESS_INSTANCE_ID_LIST, VariableInstanceLog_.processInstanceId);
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.PROCESS_ID_LIST, VariableInstanceLog_.processId);
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.DATE_LIST, VariableInstanceLog_.date);
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.EXTERNAL_ID_LIST, VariableInstanceLog_.externalId);
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.VARIABLE_INSTANCE_ID_LIST, VariableInstanceLog_.variableInstanceId);
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.VARIABLE_ID_LIST, VariableInstanceLog_.variableId);
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.VALUE_LIST, VariableInstanceLog_.value);
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.OLD_VALUE_LIST, VariableInstanceLog_.oldValue);
        return true;
    }

    // Implementation specific logic ----------------------------------------------------------------------------------------------
    protected JPAService jpaService;

    public AuditQueryCriteriaUtil(JPAService service) {
        super(AuditQueryCriteriaUtil.criteriaAttributes);
        AuditQueryCriteriaUtil.this.jpaService = service;
    }

    /**
     * This protected constructor is used in the kie-remote-services module
     * 
     * @param criteriaAttributes
     * @param service
     */
    protected AuditQueryCriteriaUtil(Map<Class, Map<String, Attribute>> criteriaAttributes, JPAService service) {
        super(criteriaAttributes);
        AuditQueryCriteriaUtil.this.jpaService = service;
    }

    protected EntityManager getEntityManager() {
        return AuditQueryCriteriaUtil.this.jpaService.getEntityManager();
    }

    protected Object joinTransaction(EntityManager em) {
        return AuditQueryCriteriaUtil.this.jpaService.joinTransaction(em);
    }

    protected void closeEntityManager(EntityManager em, Object transaction) {
        AuditQueryCriteriaUtil.this.jpaService.closeEntityManager(em, transaction);
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
    @SuppressWarnings(value = "unchecked")
    protected <R, T> Predicate implSpecificCreatePredicateFromSingleCriteria(CriteriaQuery<R> query, CriteriaBuilder builder, Class queryType, QueryCriteria criteria, QueryWhere queryWhere) {
        javax.persistence.criteria.Root<?> table = getRoot(query, queryType);
        return AuditQueryCriteriaUtil.variableInstanceLogSpecificCreatePredicateFromSingleCriteria(query, builder, criteria, table);
    }

    @SuppressWarnings(value = "unchecked")
    public static <Q, T> Predicate variableInstanceLogSpecificCreatePredicateFromSingleCriteria(CriteriaQuery<Q> query, CriteriaBuilder builder, QueryCriteria criteria, Root<T> table) {
        Predicate predicate;
        if (QueryParameterIdentifiers.LAST_VARIABLE_LIST.equals(criteria.getListId())) {
            Subquery<VariableInstanceLog> maxIdSubQuery = query.subquery(VariableInstanceLog.class);
            javax.persistence.criteria.Root from = maxIdSubQuery.from(VariableInstanceLog.class);
            maxIdSubQuery.select(builder.max(from.get(VariableInstanceLog_.id)));
            maxIdSubQuery.groupBy(from.get(VariableInstanceLog_.variableId), from.get(VariableInstanceLog_.processInstanceId));
            Attribute varIdField = VariableInstanceLog_.id;
            // TODO: add the current group's criteria list to the subquery,
            // in order to make the subquery more efficient
            // -- but that requires making the criteria list available here as an argument.. :/
            Expression expr;
            if (varIdField instanceof SingularAttribute) {
                expr = table.get(((SingularAttribute<T, ?>) (varIdField)));
            } else {
                throw new IllegalStateException((("Unexpected " + (varIdField.getClass().getName())) + " when processing last variable query criteria!"));
            }
            predicate = builder.in(expr).value(maxIdSubQuery);
        } else if (QueryParameterIdentifiers.VAR_VALUE_ID_LIST.equals(criteria.getListId())) {
            assert (criteria.getValues().size()) == 1 : "Only 1 var id/value parameter expected!";
            // extract var/val information from criteria
            Object varVal = criteria.getValues().get(0);
            String[] parts = ((String) (varVal)).split(QueryParameterIdentifiers.VAR_VAL_SEPARATOR, 2);
            String varId = parts[1].substring(0, Integer.parseInt(parts[0]));
            String val = parts[1].substring(((Integer.parseInt(parts[0])) + 1));
            // create predicates
            SingularAttribute varVarIdField = VariableInstanceLog_.variableId;
            Path varVarIdPath = table.get(varVarIdField);
            SingularAttribute varValField = VariableInstanceLog_.value;
            Path varValIdPath = table.get(varValField);
            Predicate varIdPredicate = builder.equal(varVarIdPath, varId);
            Predicate valPredicate;
            if (QueryCriteriaType.REGEXP.equals(criteria.getType())) {
                val = convertRegexToJPALikeExpression(val);
                valPredicate = builder.like(varValIdPath, val);
            } else {
                valPredicate = builder.equal(varValIdPath, val);
            }
            // intersect predicates
            predicate = builder.and(varIdPredicate, valPredicate);
        } else {
            throw new IllegalStateException((((("List id [" + (getQueryParameterIdNameMap().get(Integer.parseInt(criteria.getListId())))) + "] is not supported for queries on ") + (table.getJavaType().getSimpleName())) + "."));
        }
        return predicate;
    }
}

