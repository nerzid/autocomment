

package org.jbpm.services.task.audit.service;

import java.util.Collections;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import javax.persistence.criteria.Predicate;
import org.jbpm.query.jpa.data.QueryCriteria;
import org.jbpm.query.jpa.impl.QueryCriteriaUtil;
import org.jbpm.query.jpa.service.QueryModificationService;
import org.kie.internal.query.QueryParameterIdentifiers;
import org.jbpm.query.jpa.data.QueryWhere;
import javax.persistence.criteria.Root;
import java.util.Set;
import javax.persistence.criteria.Subquery;
import org.jbpm.services.task.impl.model.TaskImpl;
import org.jbpm.services.task.audit.impl.model.TaskVariableImpl;

/**
 * A {@link QueryModificationService} instace for the jbpm-human-task-audit module
 */
public class TaskAuditQueryModificationService implements QueryModificationService {
    private static Set<String> listIds = new HashSet<String>(3);

    static {
        TaskAuditQueryModificationService.listIds.add(QueryParameterIdentifiers.TASK_VARIABLE_NAME_ID_LIST);
        TaskAuditQueryModificationService.listIds.add(QueryParameterIdentifiers.TASK_VARIABLE_VALUE_ID_LIST);
        TaskAuditQueryModificationService.listIds.add(QueryParameterIdentifiers.TASK_VARIABLE_COMBINED_ID);
    }

    /* (non-Javadoc)
    @see org.jbpm.query.jpa.service.QueryModificationService#accepts(java.lang.String)
     */
    @Override
    public boolean accepts(String listId) {
        return TaskAuditQueryModificationService.listIds.contains(listId);
    }

    /* (non-Javadoc)
    @see org.jbpm.query.jpa.service.QueryModificationService#optimizeCriteria(org.jbpm.query.jpa.data.QueryWhere)
     */
    public void optimizeCriteria(QueryWhere queryWhere) {
        optimizeCriteria(queryWhere.getCriteria());
    }

    /**
     * This method combines multiple intersecting {@link TaskVariableImpl} criteria in order
     * to make sure that the number of ({@link TaskVariableImpl}) subqueries created is minimal.
     * </p>
     * The following logic is applied:
     * </p>
     * Go through the given list of {@link QueryCriteria} and if an intersecting group or criteria
     * contains multiple {@link TaskVariableImpl} criteria, then replace those task variable criteria
     * with a single "combined" task variable criteria. This is then later processed correctly so as to create
     * only one subquery.
     * </p>
     * Obviously, if we run into a group criteria, recurse.
     * </p>
     * Continue to go through the criteria list until we've reached the end of the list: the loop
     * might have broken off earlier because it hit a union criteria and stopped to process group of intersecting
     * task variable criteria that had already been found. With the successive loops, all intersecting groups
     * containing task variable criteria will have been removed, allowing the last loop to reach the end
     * of the list.
     * 
     * @param criteriaList The list of {@link QueryCriteria} to process
     */
    public void optimizeCriteria(List<QueryCriteria> criteriaList) {
        // we don't expect subqueries with task variable criteria
        Set<QueryCriteria> optimizedCriteria = Collections.newSetFromMap(new IdentityHashMap<QueryCriteria, java.lang.Boolean>(1));
        boolean endOfListNotYetReached = true;
        while (endOfListNotYetReached) {
            Set<QueryCriteria> taskVarCriteria = Collections.newSetFromMap(new IdentityHashMap<QueryCriteria, java.lang.Boolean>(2));
            boolean endOfListReached = true;
            for (QueryCriteria criteria : criteriaList) {
                if ((!(criteria.isFirst())) && (criteria.isUnion())) {
                    if ((taskVarCriteria.size()) > 1) {
                        endOfListReached = false;
                        break;
                    } 
                    taskVarCriteria.clear();
                } 
                // if group, recurse
                if (criteria.isGroupCriteria()) {
                    if (optimizedCriteria.add(criteria)) {
                        optimizeCriteria(criteria.getCriteria());
                    } 
                    continue;
                } 
                String listId = criteria.getListId();
                if ((listId.equals(QueryParameterIdentifiers.TASK_VARIABLE_NAME_ID_LIST)) || (listId.equals(QueryParameterIdentifiers.TASK_VARIABLE_VALUE_ID_LIST))) {
                    taskVarCriteria.add(criteria);
                } 
            }
            if (endOfListReached) {
                endOfListNotYetReached = false;
            } 
            if ((taskVarCriteria.size()) > 1) {
                Iterator<QueryCriteria> criteriaIter = criteriaList.iterator();
                QueryCriteria combinedTaskVarCriteria = null;
                while (criteriaIter.hasNext()) {
                    QueryCriteria criteria = criteriaIter.next();
                    if (taskVarCriteria.contains(criteria)) {
                        if (combinedTaskVarCriteria == null) {
                            combinedTaskVarCriteria = criteria;
                            criteria = new QueryCriteria(criteria);
                            // combined criteria replaces the original, and thus:
                            // 1. KEEPS the original union flag!
                            // 2. KEEPS the original first flag!
                            combinedTaskVarCriteria.setListId(QueryParameterIdentifiers.TASK_VARIABLE_COMBINED_ID);
                            combinedTaskVarCriteria.setType(QueryCriteriaType.NORMAL);
                            combinedTaskVarCriteria.getValues().clear();
                            combinedTaskVarCriteria.getDateValues().clear();
                            // processed as a normal, even though it's group
                            combinedTaskVarCriteria.addCriteria(criteria);
                        } else {
                            combinedTaskVarCriteria.addCriteria(criteria);
                            criteriaIter.remove();
                        }
                    } 
                    if ((combinedTaskVarCriteria != null) && (criteria.isUnion())) {
                        break;
                    } 
                }
            } 
        }
    }

    /* (non-Javadoc)
    @see org.jbpm.query.jpa.service.QueryModificationService#createPredicate(org.jbpm.query.jpa.data.QueryCriteria, javax.persistence.criteria.CriteriaQuery, javax.persistence.criteria.CriteriaBuilder)
     */
    public <R> Predicate createPredicate(QueryCriteria criteria, CriteriaQuery<R> query, CriteriaBuilder builder) {
        // subquery and root
        Root<TaskImpl> taskRoot = QueryCriteriaUtil.getRoot(query, TaskImpl.class);
        Subquery<Long> subQuery = query.subquery(Long.class);
        Root<TaskVariableImpl> taskVarRoot = subQuery.from(TaskVariableImpl.class);
        subQuery.select(taskVarRoot.get(TaskVariableImpl_.taskId));
        // task variable predicate (in subquery)
        Predicate taskVariablePredicate = null;
        String listId = criteria.getListId();
        if (QueryParameterIdentifiers.TASK_VARIABLE_COMBINED_ID.equals(listId)) {
            List<QueryCriteria> taskVarSubCriteriaList = criteria.getCriteria();
            int size = taskVarSubCriteriaList.size();
            Predicate[] taskVarSubPredicates = new Predicate[size];
            for (int i = 0; i < size; ++i) {
                taskVarSubPredicates[i] = TaskAuditQueryModificationService.createSingleTaskVariableCriteriaPredicate(builder, taskVarRoot, taskVarSubCriteriaList.get(i));
            }
            taskVariablePredicate = builder.and(taskVarSubPredicates);
        } else {
            taskVariablePredicate = TaskAuditQueryModificationService.createSingleTaskVariableCriteriaPredicate(builder, taskVarRoot, criteria);
        }
        // add predicate to subquery
        subQuery.where(taskVariablePredicate);
        // create predicate for actual query that references subquery
        return taskRoot.get(TaskImpl_.id).in(subQuery);
    }

    private static Predicate createSingleTaskVariableCriteriaPredicate(CriteriaBuilder builder, org.jbpm.services.task.audit.service.Root<TaskVariableImpl> taskVarRoot, QueryCriteria criteria) {
        String listId = criteria.getListId();
        Expression entityField = null;
        if (QueryParameterIdentifiers.TASK_VARIABLE_NAME_ID_LIST.equals(listId)) {
            entityField = taskVarRoot.get(TaskVariableImpl_.name);
        } else if (QueryParameterIdentifiers.TASK_VARIABLE_VALUE_ID_LIST.equals(listId)) {
            entityField = taskVarRoot.get(TaskVariableImpl_.value);
        } 
        return QueryCriteriaUtil.basicCreatePredicateFromSingleCriteria(builder, entityField, criteria);
    }
}

