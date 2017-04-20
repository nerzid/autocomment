

package org.jbpm.process.audit;

import QueryParameterIdentifiers.NODE_INSTANCE_ID_LIST;
import java.util.concurrent.ConcurrentHashMap;
import javax.persistence.metamodel.Attribute;
import QueryParameterIdentifiers.CORRELATION_KEY_LIST;
import javax.persistence.criteria.CriteriaBuilder;
import QueryParameterIdentifiers.DATE_LIST;
import java.util.List;
import QueryParameterIdentifiers.START_DATE_LIST;
import NodeInstanceLog_.workItemId;
import java.util.Map;
import QueryParameterIdentifiers.TYPE_LIST;
import ProcessInstanceLog_.externalId;
import VariableInstanceLog_.variableId;
import QueryParameterIdentifiers.DURATION_LIST;
import QueryParameterIdentifiers.VARIABLE_ID_LIST;
import QueryParameterIdentifiers.LAST_VARIABLE_LIST;
import QueryParameterIdentifiers.PROCESS_INSTANCE_STATUS_LIST;
import VariableInstanceLog_.variableInstanceId;
import QueryParameterIdentifiers.END_DATE_LIST;
import ProcessInstanceLog_.processVersion;
import ProcessInstanceLog_.start;
import QueryParameterIdentifiers.PROCESS_INSTANCE_ID_LIST;
import QueryParameterIdentifiers.PROCESS_NAME_LIST;
import javax.persistence.Query;
import ProcessInstanceLog_.processId;
import ProcessInstanceLog_.outcome;
import ProcessInstanceLog_.identity;
import NodeInstanceLog_.nodeInstanceId;
import QueryParameterIdentifiers.VARIABLE_INSTANCE_ID_LIST;
import QueryParameterIdentifiers.EXTERNAL_ID_LIST;
import ProcessInstanceLog_.status;
import ProcessInstanceLog_.correlationKey;
import NodeInstanceLog_.nodeType;
import javax.persistence.criteria.Subquery;
import NodeInstanceLog_.date;
import QueryParameterIdentifiers.VALUE_LIST;
import javax.persistence.EntityManager;
import javax.persistence.criteria.Predicate;
import ProcessInstanceLog_.end;
import javax.persistence.criteria.Expression;
import QueryParameterIdentifiers.NODE_ID_LIST;
import org.jbpm.query.jpa.data.QueryCriteria;
import NodeInstanceLog_.nodeId;
import ProcessInstanceLog_.processName;
import QueryParameterIdentifiers.PROCESS_VERSION_LIST;
import QueryParameterIdentifiers.WORK_ITEM_ID_LIST;
import ProcessInstanceLog_.processInstanceId;
import org.jbpm.query.jpa.impl.QueryCriteriaUtil;
import VariableInstanceLog_.id;
import ProcessInstanceLog_.duration;
import QueryParameterIdentifiers.OLD_VALUE_LIST;
import QueryParameterIdentifiers.PROCESS_INSTANCE_PARENT_ID_LIST;
import QueryParameterIdentifiers.NODE_NAME_LIST;
import NodeInstanceLog_.nodeName;
import VariableInstanceLog_.oldValue;
import javax.persistence.criteria.Root;
import VariableInstanceLog_.value;
import QueryParameterIdentifiers.IDENTITY_LIST;
import QueryParameterIdentifiers.PROCESS_ID_LIST;
import ProcessInstanceLog_.parentProcessInstanceId;
import org.jbpm.query.jpa.data.QueryWhere;
import javax.persistence.metamodel.SingularAttribute;
import QueryParameterIdentifiers.OUTCOME_LIST;
import static ProcessInstanceLog_.correlationKey;
import static VariableInstanceLog_.id;

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
        // add criteria Map{AuditQueryCriteriaUtil.criteriaAttributes} to AuditQueryCriteriaUtil{}
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, PROCESS_INSTANCE_ID_LIST, processInstanceId);
        // add criteria Map{AuditQueryCriteriaUtil.criteriaAttributes} to AuditQueryCriteriaUtil{}
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, PROCESS_ID_LIST, processId);
        // add criteria Map{AuditQueryCriteriaUtil.criteriaAttributes} to AuditQueryCriteriaUtil{}
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, START_DATE_LIST, start);
        // add criteria Map{AuditQueryCriteriaUtil.criteriaAttributes} to AuditQueryCriteriaUtil{}
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, END_DATE_LIST, end);
        // add criteria Map{AuditQueryCriteriaUtil.criteriaAttributes} to AuditQueryCriteriaUtil{}
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, PROCESS_INSTANCE_STATUS_LIST, status);
        // add criteria Map{AuditQueryCriteriaUtil.criteriaAttributes} to AuditQueryCriteriaUtil{}
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, PROCESS_INSTANCE_PARENT_ID_LIST, parentProcessInstanceId);
        // add criteria Map{AuditQueryCriteriaUtil.criteriaAttributes} to AuditQueryCriteriaUtil{}
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, OUTCOME_LIST, outcome);
        // add criteria Map{AuditQueryCriteriaUtil.criteriaAttributes} to AuditQueryCriteriaUtil{}
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, DURATION_LIST, duration);
        // add criteria Map{AuditQueryCriteriaUtil.criteriaAttributes} to AuditQueryCriteriaUtil{}
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, IDENTITY_LIST, identity);
        // add criteria Map{AuditQueryCriteriaUtil.criteriaAttributes} to AuditQueryCriteriaUtil{}
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, PROCESS_VERSION_LIST, processVersion);
        // add criteria Map{AuditQueryCriteriaUtil.criteriaAttributes} to AuditQueryCriteriaUtil{}
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, PROCESS_NAME_LIST, processName);
        // add criteria Map{AuditQueryCriteriaUtil.criteriaAttributes} to AuditQueryCriteriaUtil{}
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, CORRELATION_KEY_LIST, correlationKey);
        // add criteria Map{AuditQueryCriteriaUtil.criteriaAttributes} to AuditQueryCriteriaUtil{}
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, EXTERNAL_ID_LIST, externalId);
        // NodeInstanceLog
        // add criteria Map{AuditQueryCriteriaUtil.criteriaAttributes} to AuditQueryCriteriaUtil{}
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, PROCESS_INSTANCE_ID_LIST, NodeInstanceLog_.processInstanceId);
        // add criteria Map{AuditQueryCriteriaUtil.criteriaAttributes} to AuditQueryCriteriaUtil{}
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, PROCESS_ID_LIST, NodeInstanceLog_.processId);
        // add criteria Map{AuditQueryCriteriaUtil.criteriaAttributes} to AuditQueryCriteriaUtil{}
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, EXTERNAL_ID_LIST, NodeInstanceLog_.externalId);
        // add criteria Map{AuditQueryCriteriaUtil.criteriaAttributes} to AuditQueryCriteriaUtil{}
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, DATE_LIST, date);
        // add criteria Map{AuditQueryCriteriaUtil.criteriaAttributes} to AuditQueryCriteriaUtil{}
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, NODE_INSTANCE_ID_LIST, nodeInstanceId);
        // add criteria Map{AuditQueryCriteriaUtil.criteriaAttributes} to AuditQueryCriteriaUtil{}
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, NODE_ID_LIST, nodeId);
        // add criteria Map{AuditQueryCriteriaUtil.criteriaAttributes} to AuditQueryCriteriaUtil{}
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, NODE_NAME_LIST, nodeName);
        // add criteria Map{AuditQueryCriteriaUtil.criteriaAttributes} to AuditQueryCriteriaUtil{}
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, TYPE_LIST, nodeType);
        // add criteria Map{AuditQueryCriteriaUtil.criteriaAttributes} to AuditQueryCriteriaUtil{}
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, WORK_ITEM_ID_LIST, workItemId);
        // Var
        // add criteria Map{AuditQueryCriteriaUtil.criteriaAttributes} to AuditQueryCriteriaUtil{}
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, PROCESS_INSTANCE_ID_LIST, VariableInstanceLog_.processInstanceId);
        // add criteria Map{AuditQueryCriteriaUtil.criteriaAttributes} to AuditQueryCriteriaUtil{}
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, PROCESS_ID_LIST, VariableInstanceLog_.processId);
        // add criteria Map{AuditQueryCriteriaUtil.criteriaAttributes} to AuditQueryCriteriaUtil{}
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, DATE_LIST, VariableInstanceLog_.date);
        // add criteria Map{AuditQueryCriteriaUtil.criteriaAttributes} to AuditQueryCriteriaUtil{}
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, EXTERNAL_ID_LIST, VariableInstanceLog_.externalId);
        // add criteria Map{AuditQueryCriteriaUtil.criteriaAttributes} to AuditQueryCriteriaUtil{}
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, VARIABLE_INSTANCE_ID_LIST, variableInstanceId);
        // add criteria Map{AuditQueryCriteriaUtil.criteriaAttributes} to AuditQueryCriteriaUtil{}
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, VARIABLE_ID_LIST, variableId);
        // add criteria Map{AuditQueryCriteriaUtil.criteriaAttributes} to AuditQueryCriteriaUtil{}
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, VALUE_LIST, value);
        // add criteria Map{AuditQueryCriteriaUtil.criteriaAttributes} to AuditQueryCriteriaUtil{}
        addCriteria(AuditQueryCriteriaUtil.criteriaAttributes, OLD_VALUE_LIST, oldValue);
        return true;
    }

    // Implementation specific logic ----------------------------------------------------------------------------------------------
    protected JPAService jpaService;

    public AuditQueryCriteriaUtil(JPAService service) {
        super(AuditQueryCriteriaUtil.criteriaAttributes);
        this.jpaService = service;
    }

    /**
     * This protected constructor is used in the kie-remote-services module
     *
     * @param criteriaAttributes
     * @param service
     */
    protected AuditQueryCriteriaUtil(Map<Class, Map<String, Attribute>> criteriaAttributes, JPAService service) {
        super(criteriaAttributes);
        this.jpaService = service;
    }

    protected EntityManager getEntityManager() {
        return this.jpaService.getEntityManager();
    }

    protected Object joinTransaction(EntityManager em) {
        return this.jpaService.joinTransaction(em);
    }

    protected void closeEntityManager(EntityManager em, Object transaction) {
        // close entity EntityManager{em} to JPAService{this.jpaService}
        this.jpaService.closeEntityManager(em, transaction);
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
        // apply meta Query{query} to AuditQueryCriteriaUtil{}
        applyMetaCriteriaToQuery(query, queryWhere);
        // execute query
        List<T> result = query.getResultList();
        // close entity EntityManager{em} to AuditQueryCriteriaUtil{}
        closeEntityManager(em, newTx);
        return result;
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    protected <R, T> Predicate implSpecificCreatePredicateFromSingleCriteria(CriteriaQuery<R> query, CriteriaBuilder builder, Class queryType, QueryCriteria criteria, QueryWhere queryWhere) {
        Root<?> table = getRoot(query, queryType);
        return AuditQueryCriteriaUtil.variableInstanceLogSpecificCreatePredicateFromSingleCriteria(query, builder, criteria, table);
    }

    @SuppressWarnings(value = "unchecked")
    public static <Q, T> Predicate variableInstanceLogSpecificCreatePredicateFromSingleCriteria(CriteriaQuery<Q> query, CriteriaBuilder builder, QueryCriteria criteria, Root<T> table) {
        Predicate predicate;
        // extract var/val information from criteria
        // create predicates
        // intersect predicates
        if (LAST_VARIABLE_LIST.equals(criteria.getListId())) {
            Subquery<VariableInstanceLog> maxIdSubQuery = query.subquery(VariableInstanceLog.class);
            Root from = maxIdSubQuery.from(VariableInstanceLog.class);
            maxIdSubQuery.select(builder.max(from.get(id)));
            maxIdSubQuery.groupBy(from.get(variableId), from.get(VariableInstanceLog_.processInstanceId));
            Attribute varIdField = VariableInstanceLog_.id;
            // TODO: add the current group's criteria list to the subquery,
            // in order to make the subquery more efficient
            // -- but that requires making the criteria list available here as an argument.. :/
            Expression expr;
            if (varIdField instanceof SingularAttribute) {
                expr = table.get(((SingularAttribute<T, ?>) (varIdField)));
            }else {
                throw new IllegalStateException((("Unexpected " + (varIdField.getClass().getName())) + " when processing last variable query criteria!"));
            }
            predicate = builder.in(expr).value(maxIdSubQuery);
        }// extract var/val information from criteria
        // create predicates
        // intersect predicates
        else
            if (QueryParameterIdentifiers.VAR_VALUE_ID_LIST.equals(criteria.getListId())) {
                assert (criteria.getValues().size()) == 1 : "Only 1 var id/value parameter expected!";
                Object varVal = criteria.getValues().get(0);
                String[] parts = ((String) (varVal)).split(QueryParameterIdentifiers.VAR_VAL_SEPARATOR, 2);
                String varId = parts[1].substring(0, Integer.parseInt(parts[0]));
                String val = parts[1].substring(((Integer.parseInt(parts[0])) + 1));
                SingularAttribute varVarIdField = VariableInstanceLog_.variableId;
                javax.persistence.criteria.Path varVarIdPath = table.get(varVarIdField);
                SingularAttribute varValField = VariableInstanceLog_.value;
                javax.persistence.criteria.Path varValIdPath = table.get(varValField);
                Predicate varIdPredicate = builder.equal(varVarIdPath, varId);
                Predicate valPredicate;
                if (QueryCriteriaType.REGEXP.equals(criteria.getType())) {
                    val = convertRegexToJPALikeExpression(val);
                    valPredicate = builder.like(varValIdPath, val);
                }else {
                    valPredicate = builder.equal(varValIdPath, val);
                }
                predicate = builder.and(varIdPredicate, valPredicate);
            }else {
                throw new IllegalStateException((((("List id [" + (getQueryParameterIdNameMap().get(Integer.parseInt(criteria.getListId())))) + "] is not supported for queries on ") + (table.getJavaType().getSimpleName())) + "."));
            }
        
        return predicate;
    }
}

