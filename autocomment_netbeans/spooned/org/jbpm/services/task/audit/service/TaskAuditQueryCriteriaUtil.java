

package org.jbpm.services.task.audit.service;

import org.jbpm.services.task.persistence.AbstractTaskQueryCriteriaUtil;
import javax.persistence.metamodel.Attribute;
import org.jbpm.services.task.audit.impl.model.AuditTaskImpl_;
import java.util.concurrent.ConcurrentHashMap;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.EntityManager;
import java.util.Map;
import javax.persistence.criteria.Predicate;
import org.jbpm.query.jpa.data.QueryCriteria;
import org.kie.internal.query.QueryParameterIdentifiers;
import org.jbpm.query.jpa.data.QueryWhere;
import org.kie.internal.task.api.TaskPersistenceContext;

public class TaskAuditQueryCriteriaUtil extends AbstractTaskQueryCriteriaUtil {
    // Query Field Info -----------------------------------------------------------------------------------------------------------
    public static final Map<Class, Map<String, Attribute>> criteriaAttributes = new ConcurrentHashMap<Class, Map<String, Attribute>>();

    @Override
    protected synchronized boolean initializeCriteriaAttributes() {
        if ((AuditTaskImpl_.taskId) == null) {
            // EMF/persistence has not been initialized:
            // When a persistence unit (EntityManagerFactory) is initialized,
            // the fields of classes annotated with @StaticMetamodel are filled using reflection
            return false;
        } 
        // do not do initialization twice (slow performance, otherwise it doesn't matter)
        if (!(TaskAuditQueryCriteriaUtil.criteriaAttributes.isEmpty())) {
            return true;
        } 
        // AuditTaskImpl
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.TASK_ID_LIST, AuditTaskImpl_.taskId);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.PROCESS_ID_LIST, AuditTaskImpl_.processId);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.TASK_STATUS_LIST, AuditTaskImpl_.status);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.ACTUAL_OWNER_ID_LIST, AuditTaskImpl_.actualOwner);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.DEPLOYMENT_ID_LIST, AuditTaskImpl_.deploymentId);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.ID_LIST, AuditTaskImpl_.id);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.CREATED_ON_LIST, AuditTaskImpl_.createdOn);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.TASK_PARENT_ID_LIST, AuditTaskImpl_.parentId);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.CREATED_BY_LIST, AuditTaskImpl_.createdBy);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.PROCESS_INSTANCE_ID_LIST, AuditTaskImpl_.processInstanceId);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.TASK_ACTIVATION_TIME_LIST, AuditTaskImpl_.activationTime);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.TASK_DESCRIPTION_LIST, AuditTaskImpl_.description);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.TASK_PRIORITY_LIST, AuditTaskImpl_.priority);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.TASK_NAME_LIST, AuditTaskImpl_.name);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.TASK_PROCESS_SESSION_ID_LIST, AuditTaskImpl_.processSessionId);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.TASK_DUE_DATE_LIST, AuditTaskImpl_.dueDate);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.WORK_ITEM_ID_LIST, AuditTaskImpl_.workItemId);
        // BAMTaskSummaryImpl
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.TASK_ID_LIST, BAMTaskSummaryImpl_.taskId);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.START_DATE_LIST, BAMTaskSummaryImpl_.startDate);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.DURATION_LIST, BAMTaskSummaryImpl_.duration);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.PROCESS_INSTANCE_ID_LIST, BAMTaskSummaryImpl_.processInstanceId);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.TASK_STATUS_LIST, BAMTaskSummaryImpl_.status);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.USER_ID_LIST, BAMTaskSummaryImpl_.userId);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.END_DATE_LIST, BAMTaskSummaryImpl_.endDate);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.CREATED_ON_LIST, BAMTaskSummaryImpl_.createdDate);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.TASK_NAME_LIST, BAMTaskSummaryImpl_.taskName);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.ID_LIST, BAMTaskSummaryImpl_.pk);
        // TaskEventImpl
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.MESSAGE_LIST, TaskEventImpl_.message);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.TASK_ID_LIST, TaskEventImpl_.taskId);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.ID_LIST, TaskEventImpl_.id);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.PROCESS_INSTANCE_ID_LIST, TaskEventImpl_.processInstanceId);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.DATE_LIST, TaskEventImpl_.logTime);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.USER_ID_LIST, TaskEventImpl_.userId);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.TYPE_LIST, TaskEventImpl_.type);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.WORK_ITEM_ID_LIST, TaskEventImpl_.workItemId);
        // TaskVariableImpl
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.ID_LIST, TaskVariableImpl_.id);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.TASK_ID_LIST, TaskVariableImpl_.taskId);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.PROCESS_INSTANCE_ID_LIST, TaskVariableImpl_.processInstanceId);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.PROCESS_ID_LIST, TaskVariableImpl_.processId);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.TASK_VARIABLE_NAME_ID_LIST, TaskVariableImpl_.name);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.TASK_VARIABLE_VALUE_ID_LIST, TaskVariableImpl_.value);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.DATE_LIST, TaskVariableImpl_.modificationDate);
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.TYPE_LIST, TaskVariableImpl_.type);
        return true;
    }

    // Implementation specific logic ----------------------------------------------------------------------------------------------
    private final TaskJPAAuditService taskAuditService;

    public TaskAuditQueryCriteriaUtil(TaskJPAAuditService service) {
        super(null);
        initialize(TaskAuditQueryCriteriaUtil.criteriaAttributes);
        this.taskAuditService = service;
    }

    public TaskAuditQueryCriteriaUtil(TaskPersistenceContext context) {
        super(context);
        initialize(TaskAuditQueryCriteriaUtil.criteriaAttributes);
        this.taskAuditService = null;
    }

    @Override
    protected EntityManager getEntityManager() {
        if ((TaskAuditQueryCriteriaUtil.this.persistenceContext) == null) {
            return TaskAuditQueryCriteriaUtil.this.taskAuditService.getEntityManager();
        } else {
            return super.getEntityManager();
        }
    }

    @Override
    protected Object joinTransaction(EntityManager em) {
        if ((TaskAuditQueryCriteriaUtil.this.persistenceContext) == null) {
            return TaskAuditQueryCriteriaUtil.this.taskAuditService.joinTransaction(em);
        } else {
            super.joinTransaction(em);
            return true;
        }
    }

    @Override
    protected void closeEntityManager(EntityManager em, Object transaction) {
        if ((TaskAuditQueryCriteriaUtil.this.persistenceContext) == null) {
            TaskAuditQueryCriteriaUtil.this.taskAuditService.closeEntityManager(em, transaction);
        } 
        // em closed outside of this class when used within HT
    }

    // Implementation specific methods --------------------------------------------------------------------------------------------
    @Override
    protected <R, T> Predicate implSpecificCreatePredicateFromSingleCriteria(CriteriaQuery<R> query, CriteriaBuilder builder, Class queryType, QueryCriteria criteria, QueryWhere queryWhere) {
        throw new IllegalStateException((((("List id " + (criteria.getListId())) + " is not supported for queries on ") + (queryType.getSimpleName())) + "."));
    }
}

