/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.jbpm.process.audit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.kie.api.runtime.Environment;
import javax.persistence.FlushModeType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import javax.persistence.NoResultException;
import org.kie.internal.runtime.manager.audit.query.NodeInstanceLogDeleteBuilder;
import org.kie.internal.runtime.manager.audit.query.NodeInstanceLogQueryBuilder;
import org.jbpm.process.audit.strategy.PersistenceStrategyType;
import org.kie.internal.runtime.manager.audit.query.ProcessInstanceLogDeleteBuilder;
import org.kie.internal.runtime.manager.audit.query.ProcessInstanceLogQueryBuilder;
import javax.persistence.Query;
import org.jbpm.query.jpa.impl.QueryAndParameterAppender;
import org.jbpm.query.jpa.data.QueryCriteria;
import org.jbpm.query.jpa.impl.QueryCriteriaUtil;
import org.kie.internal.query.QueryParameterIdentifiers;
import org.jbpm.query.jpa.data.QueryWhere;
import java.util.TreeMap;
import org.kie.internal.runtime.manager.audit.query.VariableInstanceLogDeleteBuilder;
import org.kie.internal.runtime.manager.audit.query.VariableInstanceLogQueryBuilder;

public class JPAAuditLogService extends JPAService implements AuditLogService {
    private static final Logger logger = LoggerFactory.getLogger(JPAAuditLogService.class);

    private static final String AUDIT_LOG_PERSISTENCE_UNIT_NAME = "org.jbpm.persistence.jpa";

    public JPAAuditLogService() {
        super(JPAAuditLogService.AUDIT_LOG_PERSISTENCE_UNIT_NAME);
    }

    public JPAAuditLogService(Environment env) {
        super(env, JPAAuditLogService.AUDIT_LOG_PERSISTENCE_UNIT_NAME);
    }

    public JPAAuditLogService(Environment env, PersistenceStrategyType type) {
        super(env, type);
        this.persistenceUnitName = JPAAuditLogService.AUDIT_LOG_PERSISTENCE_UNIT_NAME;
    }

    public JPAAuditLogService(EntityManagerFactory emf) {
        super(emf);
        this.persistenceUnitName = JPAAuditLogService.AUDIT_LOG_PERSISTENCE_UNIT_NAME;
    }

    public JPAAuditLogService(EntityManagerFactory emf, PersistenceStrategyType type) {
        super(emf, type);
        this.persistenceUnitName = JPAAuditLogService.AUDIT_LOG_PERSISTENCE_UNIT_NAME;
    }

    /* (non-Javadoc)
    @see org.jbpm.process.audit.AuditLogService#findProcessInstances()
     */
    @Override
    public List<ProcessInstanceLog> findProcessInstances() {
        EntityManager em = getEntityManager();
        Query query = em.createQuery("FROM ProcessInstanceLog");
        return executeQuery(query, em, ProcessInstanceLog.class);
    }

    /* (non-Javadoc)
    @see org.jbpm.process.audit.AuditLogService#findActiveProcessInstances(java.lang.String)
     */
    @Override
    public List<ProcessInstanceLog> findActiveProcessInstances() {
        EntityManager em = getEntityManager();
        Query query = em.createQuery("FROM ProcessInstanceLog p WHERE p.end is null");
        return executeQuery(query, em, ProcessInstanceLog.class);
    }

    /* (non-Javadoc)
    @see org.jbpm.process.audit.AuditLogService#findProcessInstances(java.lang.String)
     */
    @Override
    public List<ProcessInstanceLog> findProcessInstances(String processId) {
        EntityManager em = getEntityManager();
        Query query = em.createQuery("FROM ProcessInstanceLog p WHERE p.processId = :processId").setParameter("processId", processId);
        return executeQuery(query, em, ProcessInstanceLog.class);
    }

    /* (non-Javadoc)
    @see org.jbpm.process.audit.AuditLogService#findActiveProcessInstances(java.lang.String)
     */
    @Override
    public List<ProcessInstanceLog> findActiveProcessInstances(String processId) {
        EntityManager em = getEntityManager();
        Query query = em.createQuery("FROM ProcessInstanceLog p WHERE p.processId = :processId AND p.end is null").setParameter("processId", processId);
        return executeQuery(query, em, ProcessInstanceLog.class);
    }

    /* (non-Javadoc)
    @see org.jbpm.process.audit.AuditLogService#findProcessInstance(long)
     */
    @Override
    public ProcessInstanceLog findProcessInstance(long processInstanceId) {
        EntityManager em = getEntityManager();
        Object newTx = joinTransaction(em);
        try {
            return ((ProcessInstanceLog) (em.createQuery("FROM ProcessInstanceLog p WHERE p.processInstanceId = :processInstanceId").setParameter("processInstanceId", processInstanceId).getSingleResult()));
        } catch (NoResultException e) {
            return null;
        } finally {
            closeEntityManager(em, newTx);
        }
    }

    /* (non-Javadoc)
    @see org.jbpm.process.audit.AuditLogService#findSubProcessInstances(long)
     */
    @Override
    public List<ProcessInstanceLog> findSubProcessInstances(long processInstanceId) {
        EntityManager em = getEntityManager();
        Query query = em.createQuery("FROM ProcessInstanceLog p WHERE p.parentProcessInstanceId = :processInstanceId").setParameter("processInstanceId", processInstanceId);
        return executeQuery(query, em, ProcessInstanceLog.class);
    }

    /* (non-Javadoc)
    @see org.jbpm.process.audit.AuditLogService#findNodeInstances(long)
     */
    @Override
    public List<NodeInstanceLog> findNodeInstances(long processInstanceId) {
        EntityManager em = getEntityManager();
        Query query = em.createQuery("FROM NodeInstanceLog n WHERE n.processInstanceId = :processInstanceId ORDER BY date,id").setParameter("processInstanceId", processInstanceId);
        return executeQuery(query, em, NodeInstanceLog.class);
    }

    /* (non-Javadoc)
    @see org.jbpm.process.audit.AuditLogService#findNodeInstances(long, java.lang.String)
     */
    @Override
    public List<NodeInstanceLog> findNodeInstances(long processInstanceId, String nodeId) {
        EntityManager em = getEntityManager();
        Query query = em.createQuery("FROM NodeInstanceLog n WHERE n.processInstanceId = :processInstanceId AND n.nodeId = :nodeId ORDER BY date,id").setParameter("processInstanceId", processInstanceId).setParameter("nodeId", nodeId);
        return executeQuery(query, em, NodeInstanceLog.class);
    }

    /* (non-Javadoc)
    @see org.jbpm.process.audit.AuditLogService#findVariableInstances(long)
     */
    @Override
    public List<VariableInstanceLog> findVariableInstances(long processInstanceId) {
        EntityManager em = getEntityManager();
        Query query = em.createQuery("FROM VariableInstanceLog v WHERE v.processInstanceId = :processInstanceId ORDER BY date").setParameter("processInstanceId", processInstanceId);
        return executeQuery(query, em, VariableInstanceLog.class);
    }

    /* (non-Javadoc)
    @see org.jbpm.process.audit.AuditLogService#findVariableInstances(long, java.lang.String)
     */
    @Override
    public List<VariableInstanceLog> findVariableInstances(long processInstanceId, String variableId) {
        EntityManager em = getEntityManager();
        Query query = em.createQuery("FROM VariableInstanceLog v WHERE v.processInstanceId = :processInstanceId AND v.variableId = :variableId ORDER BY date").setParameter("processInstanceId", processInstanceId).setParameter("variableId", variableId);
        return executeQuery(query, em, VariableInstanceLog.class);
    }

    @Override
    public List<VariableInstanceLog> findVariableInstancesByName(String variableId, boolean onlyActiveProcesses) {
        EntityManager em = getEntityManager();
        Query query;
        if (!onlyActiveProcesses) {
            query = em.createQuery("FROM VariableInstanceLog v WHERE v.variableId = :variableId ORDER BY date");
        } else {
            query = em.createQuery(("SELECT v " + ("FROM VariableInstanceLog v, ProcessInstanceLog p " + ("WHERE v.processInstanceId = p.processInstanceId " + ("AND v.variableId = :variableId " + ("AND p.end is null " + "ORDER BY v.date"))))));
        }
        query.setParameter("variableId", variableId);
        return executeQuery(query, em, VariableInstanceLog.class);
    }

    @Override
    public List<VariableInstanceLog> findVariableInstancesByNameAndValue(String variableId, String value, boolean onlyActiveProcesses) {
        EntityManager em = getEntityManager();
        Query query;
        if (!onlyActiveProcesses) {
            query = em.createQuery("FROM VariableInstanceLog v WHERE v.variableId = :variableId AND v.value = :value ORDER BY date");
        } else {
            query = em.createQuery(("SELECT v " + ("FROM VariableInstanceLog v, ProcessInstanceLog p " + ("WHERE v.processInstanceId = p.processInstanceId " + ("AND v.variableId = :variableId " + ("AND v.value = :value " + ("AND p.end is null " + "ORDER BY v.date")))))));
        }
        query.setParameter("variableId", variableId).setParameter("value", value);
        return executeQuery(query, em, VariableInstanceLog.class);
    }

    /* (non-Javadoc)
    @see org.jbpm.process.audit.AuditLogService#clear()
     */
    @Override
    public void clear() {
        EntityManager em = getEntityManager();
        Object newTx = joinTransaction(em);
        try {
            List<ProcessInstanceLog> processInstances = em.createQuery("FROM ProcessInstanceLog").getResultList();
            for (ProcessInstanceLog processInstance : processInstances) {
                em.remove(processInstance);
            }
            List<NodeInstanceLog> nodeInstances = em.createQuery("FROM NodeInstanceLog").getResultList();
            for (NodeInstanceLog nodeInstance : nodeInstances) {
                em.remove(nodeInstance);
            }
            List<VariableInstanceLog> variableInstances = em.createQuery("FROM VariableInstanceLog").getResultList();
            for (VariableInstanceLog variableInstance : variableInstances) {
                em.remove(variableInstance);
            }
        } finally {
            closeEntityManager(em, newTx);
        }
    }

    // query methods
    @Override
    public NodeInstanceLogQueryBuilder nodeInstanceLogQuery() {
        return new org.jbpm.process.audit.query.NodeInstLogQueryBuilderImpl(JPAAuditLogService.this);
    }

    @Override
    public VariableInstanceLogQueryBuilder variableInstanceLogQuery() {
        return new org.jbpm.process.audit.query.VarInstLogQueryBuilderImpl(JPAAuditLogService.this);
    }

    @Override
    public ProcessInstanceLogQueryBuilder processInstanceLogQuery() {
        return new org.jbpm.process.audit.query.ProcInstLogQueryBuilderImpl(JPAAuditLogService.this);
    }

    @Override
    public ProcessInstanceLogDeleteBuilder processInstanceLogDelete() {
        return new org.jbpm.process.audit.query.ProcessInstanceLogDeleteBuilderImpl(JPAAuditLogService.this);
    }

    @Override
    public NodeInstanceLogDeleteBuilder nodeInstanceLogDelete() {
        return new org.jbpm.process.audit.query.NodeInstanceLogDeleteBuilderImpl(JPAAuditLogService.this);
    }

    @Override
    public VariableInstanceLogDeleteBuilder variableInstanceLogDelete() {
        return new org.jbpm.process.audit.query.VarInstanceLogDeleteBuilderImpl(JPAAuditLogService.this);
    }

    // internal query methods/logic
    @Override
    public <T, R> List<R> queryLogs(QueryWhere queryData, Class<T> queryClass, Class<R> resultClass) {
        List<T> results = doQuery(queryData, queryClass);
        return QueryCriteriaUtil.convertListToInterfaceList(results, resultClass);
    }

    private final AuditQueryCriteriaUtil queryUtil = new AuditQueryCriteriaUtil(JPAAuditLogService.this);

    protected QueryCriteriaUtil getQueryCriteriaUtil(Class queryType) {
        return queryUtil;
    }

    /**
     * @param queryWhere
     * @param queryType
     * @return The result of the query, a list of type T
     */
    public <T> List<T> doQuery(QueryWhere queryWhere, Class<T> queryType) {
        return getQueryCriteriaUtil(queryType).doCriteriaQuery(queryWhere, queryType);
    }

    // Delete queries -------------------------------------------------------------------------------------------------------------
    public static Map<String, String> criteriaFields = new ConcurrentHashMap<String, String>();

    public static Map<String, Class<?>> criteriaFieldClasses = new ConcurrentHashMap<String, Class<?>>();

    static {
        JPAAuditLogService.addCriteria(QueryParameterIdentifiers.PROCESS_INSTANCE_ID_LIST, "l.processInstanceId", Long.class);
        JPAAuditLogService.addCriteria(QueryParameterIdentifiers.PROCESS_ID_LIST, "l.processId", String.class);
        JPAAuditLogService.addCriteria(QueryParameterIdentifiers.WORK_ITEM_ID_LIST, "l.workItemId", Long.class);
        JPAAuditLogService.addCriteria(QueryParameterIdentifiers.EXTERNAL_ID_LIST, "l.externalId", String.class);
        // process instance log
        JPAAuditLogService.addCriteria(QueryParameterIdentifiers.START_DATE_LIST, "l.start", Date.class);
        JPAAuditLogService.addCriteria(QueryParameterIdentifiers.DURATION_LIST, "l.duration", Long.class);
        JPAAuditLogService.addCriteria(QueryParameterIdentifiers.END_DATE_LIST, "l.end", Date.class);
        JPAAuditLogService.addCriteria(QueryParameterIdentifiers.IDENTITY_LIST, "l.identity", String.class);
        JPAAuditLogService.addCriteria(QueryParameterIdentifiers.PROCESS_NAME_LIST, "l.processName", String.class);
        JPAAuditLogService.addCriteria(QueryParameterIdentifiers.PROCESS_VERSION_LIST, "l.processVersion", String.class);
        JPAAuditLogService.addCriteria(QueryParameterIdentifiers.PROCESS_INSTANCE_STATUS_LIST, "l.status", Integer.class);
        JPAAuditLogService.addCriteria(QueryParameterIdentifiers.OUTCOME_LIST, "l.outcome", String.class);
        JPAAuditLogService.addCriteria(QueryParameterIdentifiers.CORRELATION_KEY_LIST, "l.correlationKey", String.class);
        // node instance log
        JPAAuditLogService.addCriteria(QueryParameterIdentifiers.NODE_ID_LIST, "l.nodeId", String.class);
        JPAAuditLogService.addCriteria(QueryParameterIdentifiers.NODE_INSTANCE_ID_LIST, "l.nodeInstanceId", String.class);
        JPAAuditLogService.addCriteria(QueryParameterIdentifiers.NODE_NAME_LIST, "l.nodeName", String.class);
        JPAAuditLogService.addCriteria(QueryParameterIdentifiers.TYPE_LIST, "l.nodeType", String.class);
        // variable instance log
        JPAAuditLogService.addCriteria(QueryParameterIdentifiers.DATE_LIST, "l.date", Date.class);
        JPAAuditLogService.addCriteria(QueryParameterIdentifiers.OLD_VALUE_LIST, "l.oldValue", String.class);
        JPAAuditLogService.addCriteria(QueryParameterIdentifiers.VALUE_LIST, "l.value", String.class);
        JPAAuditLogService.addCriteria(QueryParameterIdentifiers.VARIABLE_ID_LIST, "l.variableId", String.class);
        JPAAuditLogService.addCriteria(QueryParameterIdentifiers.VARIABLE_INSTANCE_ID_LIST, "l.variableInstanceId", String.class);
    }

    protected static void addCriteria(String listId, String fieldName, Class type) {
        JPAAuditLogService.criteriaFields.put(listId, fieldName);
        JPAAuditLogService.criteriaFieldClasses.put(listId, type);
    }

    public int doDelete(String queryBase, QueryWhere queryData, Class<?> resultType) {
        // create query
        Map<String, Object> queryParams = new HashMap<String, Object>();
        String queryString = JPAAuditLogService.createDeleteQuery(queryBase, queryData, queryParams, true);
        // logging
        JPAAuditLogService.logger.debug("DELETE statement:\n {}", queryString);
        if (JPAAuditLogService.logger.isDebugEnabled()) {
            StringBuilder paramsStr = new StringBuilder("PARAMS:");
            Map<String, Object> orderedParams = new TreeMap<String, Object>(queryParams);
            for (Map.Entry<String, Object> entry : orderedParams.entrySet()) {
                paramsStr.append((((("\n " + (entry.getKey())) + " : '") + (entry.getValue())) + "'"));
            }
            JPAAuditLogService.logger.debug(paramsStr.toString());
        } 
        // execute query
        EntityManager em = getEntityManager();
        Object newTx = joinTransaction(em);
        Query query = em.createQuery(queryString);
        int result = executeWithParameters(queryParams, query);
        closeEntityManager(em, newTx);
        return result;
    }

    private static String createDeleteQuery(String queryBase, QueryWhere queryWhere, Map<String, Object> queryParams, boolean skipMetaParams) {
        // setup
        StringBuilder queryBuilder = new StringBuilder(queryBase);
        QueryAndParameterAppender queryAppender = new QueryAndParameterAppender(queryBuilder, queryParams);
        boolean addLastCriteria = false;
        List<Object[]> varValCriteriaList = new ArrayList<Object[]>();
        List<QueryCriteria> queryWhereCriteriaList = queryWhere.getCriteria();
        // 3. apply normal query parameters
        JPAAuditLogService.checkVarValCriteria(queryWhere, varValCriteriaList);
        // last criteria
        Iterator<QueryCriteria> iter = queryWhereCriteriaList.iterator();
        while (iter.hasNext()) {
            QueryCriteria criteria = iter.next();
            if (criteria.getListId().equals(QueryParameterIdentifiers.LAST_VARIABLE_LIST)) {
                addLastCriteria = true;
                iter.remove();
            } 
        }
        for (QueryCriteria criteria : queryWhere.getCriteria()) {
            String listId = criteria.getListId();
            switch (criteria.getType()) {
                case NORMAL :
                    queryAppender.addQueryParameters(criteria.getParameters(), listId, JPAAuditLogService.criteriaFieldClasses.get(listId), JPAAuditLogService.criteriaFields.get(listId), criteria.isUnion());
                    break;
                case RANGE :
                    queryAppender.addRangeQueryParameters(criteria.getParameters(), listId, JPAAuditLogService.criteriaFieldClasses.get(listId), JPAAuditLogService.criteriaFields.get(listId), criteria.isUnion());
                    break;
                case REGEXP :
                    List<String> stringParams = JPAAuditLogService.castToStringList(criteria.getParameters());
                    queryAppender.addRegexQueryParameters(stringParams, listId, JPAAuditLogService.criteriaFields.get(listId), criteria.isUnion());
                    break;
                default :
                    throw new IllegalArgumentException(("Unknown criteria type in delete query builder: " + (criteria.getType().toString())));
            }
        }
        while ((queryAppender.getParenthesesNesting()) > 0) {
            queryAppender.closeParentheses();
        }
        // 6. Add special criteria
        boolean addWhereClause = !(queryAppender.hasBeenUsed());
        if (!(varValCriteriaList.isEmpty())) {
            JPAAuditLogService.addVarValCriteria(addWhereClause, queryAppender, "l", varValCriteriaList);
            addWhereClause = false;
        } 
        if (addLastCriteria) {
            JPAAuditLogService.addLastInstanceCriteria(queryAppender);
        } 
        // meta criteria (order, asc/desc) does not apply to delete queries
        // 8. return query
        return queryBuilder.toString();
    }

    private static List<String> castToStringList(List<Object> objectList) {
        List<String> stringList = new ArrayList<String>(objectList.size());
        for (Object obj : objectList) {
            stringList.add(obj.toString());
        }
        return stringList;
    }

    public static void checkVarValCriteria(QueryWhere queryWhere, List<Object[]> varValCriteriaList) {
        List<QueryCriteria> varValCriteria = new LinkedList<QueryCriteria>();
        Iterator<QueryCriteria> iter = queryWhere.getCriteria().iterator();
        while (iter.hasNext()) {
            QueryCriteria criteria = iter.next();
            if (criteria.getListId().equals(QueryParameterIdentifiers.VAR_VALUE_ID_LIST)) {
                varValCriteria.add(criteria);
                iter.remove();
            } 
        }
        if (varValCriteria.isEmpty()) {
            return ;
        } 
        for (QueryCriteria criteria : varValCriteria) {
            for (Object varVal : criteria.getParameters()) {
                String[] parts = ((String) (varVal)).split(QueryParameterIdentifiers.VAR_VAL_SEPARATOR, 2);
                String varId = parts[1].substring(0, Integer.parseInt(parts[0]));
                String val = parts[1].substring(((Integer.parseInt(parts[0])) + 1));
                int type = (criteria.isUnion() ? 0 : 1) + (criteria.getType().equals(QueryCriteriaType.REGEXP) ? 2 : 0);
                Object[] varValCrit = new Object[]{ type , varId , val };
                varValCriteriaList.add(varValCrit);
            }
        }
    }

    public static void addVarValCriteria(boolean addWhereClause, QueryAndParameterAppender queryAppender, String tableId, List<Object[]> varValCriteriaList) {
        // for each var/val criteria
        for (Object[] varValCriteria : varValCriteriaList) {
            boolean union = (((Integer) (varValCriteria[0])) % 2) == 0;
            // var id: add query parameter
            String varIdQueryParamName = queryAppender.generateParamName();
            queryAppender.addNamedQueryParam(varIdQueryParamName, varValCriteria[1]);
            // var id: append to the query
            StringBuilder queryPhraseBuilder = new StringBuilder(" ( ").append(tableId).append(".variableId = :").append(varIdQueryParamName).append(" ");
            // val: append to the query
            queryPhraseBuilder.append("AND ").append(tableId).append(".value ");
            String valQueryParamName = queryAppender.generateParamName();
            String val;
            if (((Integer) (varValCriteria[0])) >= 2) {
                val = ((String) (varValCriteria[2])).replace('*', '%').replace('.', '_');
                queryPhraseBuilder.append("like :").append(valQueryParamName);
            } else {
                val = ((String) (varValCriteria[2]));
                queryPhraseBuilder.append("= :").append(valQueryParamName);
            }
            queryPhraseBuilder.append(" ) ");
            String[] valArr = new String[]{ val };
            queryAppender.addToQueryBuilder(queryPhraseBuilder.toString(), union, valQueryParamName, Arrays.asList(valArr));
        }
    }

    private static void addLastInstanceCriteria(QueryAndParameterAppender queryAppender) {
        String lastQueryPhrase = new StringBuilder("(l.id IN ").append("(SELECT MAX(ll.id) FROM VariableInstanceLog ll GROUP BY ll.variableId, ll.processInstanceId)").append(") ").toString();
        queryAppender.addToQueryBuilder(lastQueryPhrase, false);
    }

    private void applyMetaQueryParameters(Map<String, Object> params, Query query) {
        if ((params != null) && (!(params.isEmpty()))) {
            for (String name : params.keySet()) {
                Object paramVal = params.get(name);
                if (paramVal == null) {
                    continue;
                } 
                if (QueryParameterIdentifiers.FIRST_RESULT.equals(name)) {
                    if (((Integer) (paramVal)) > 0) {
                        query.setFirstResult(((Integer) (params.get(name))));
                    } 
                    continue;
                } 
                if (QueryParameterIdentifiers.MAX_RESULTS.equals(name)) {
                    if (((Integer) (paramVal)) > 0) {
                        query.setMaxResults(((Integer) (params.get(name))));
                    } 
                    continue;
                } 
                if (QueryParameterIdentifiers.FLUSH_MODE.equals(name)) {
                    query.setFlushMode(FlushModeType.valueOf(((String) (params.get(name)))));
                    continue;
                } else // skip control parameters
                if (((QueryParameterIdentifiers.ORDER_TYPE.equals(name)) || (QueryParameterIdentifiers.ORDER_BY.equals(name))) || (QueryParameterIdentifiers.FILTER.equals(name))) {
                    continue;
                } 
                query.setParameter(name, params.get(name));
            }
        } 
    }

    private int executeWithParameters(Map<String, Object> params, Query query) {
        applyMetaQueryParameters(params, query);
        return query.executeUpdate();
    }
}

