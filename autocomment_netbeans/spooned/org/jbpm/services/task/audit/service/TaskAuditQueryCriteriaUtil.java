

package org.jbpm.services.task.audit.service;

import javax.persistence.criteria.CriteriaBuilder;
import org.jbpm.services.task.audit.impl.model.AuditTaskImpl_;
import QueryParameterIdentifiers.ACTUAL_OWNER_ID_LIST;
import org.jbpm.services.task.persistence.AbstractTaskQueryCriteriaUtil;
import QueryParameterIdentifiers.TASK_PRIORITY_LIST;
import org.kie.internal.task.api.TaskPersistenceContext;
import QueryParameterIdentifiers.TASK_DESCRIPTION_LIST;
import QueryParameterIdentifiers.END_DATE_LIST;
import QueryParameterIdentifiers.TASK_DUE_DATE_LIST;
import AuditTaskImpl_.taskId;
import QueryParameterIdentifiers.TASK_STATUS_LIST;
import java.util.Map;
import AuditTaskImpl_.dueDate;
import AuditTaskImpl_.deploymentId;
import TaskVariableImpl_.modificationDate;
import TaskVariableImpl_.value;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.Attribute;
import QueryParameterIdentifiers.MESSAGE_LIST;
import BAMTaskSummaryImpl_.startDate;
import QueryParameterIdentifiers.PROCESS_INSTANCE_ID_LIST;
import QueryParameterIdentifiers.TASK_NAME_LIST;
import TaskEventImpl_.message;
import BAMTaskSummaryImpl_.taskName;
import QueryParameterIdentifiers.TASK_PARENT_ID_LIST;
import AuditTaskImpl_.priority;
import AuditTaskImpl_.processId;
import QueryParameterIdentifiers.ID_LIST;
import AuditTaskImpl_.processInstanceId;
import QueryParameterIdentifiers.WORK_ITEM_ID_LIST;
import AuditTaskImpl_.name;
import AuditTaskImpl_.actualOwner;
import QueryParameterIdentifiers.TASK_VARIABLE_NAME_ID_LIST;
import QueryParameterIdentifiers.TASK_VARIABLE_VALUE_ID_LIST;
import QueryParameterIdentifiers.START_DATE_LIST;
import QueryParameterIdentifiers.TASK_ID_LIST;
import BAMTaskSummaryImpl_.userId;
import QueryParameterIdentifiers.CREATED_BY_LIST;
import AuditTaskImpl_.createdBy;
import TaskEventImpl_.logTime;
import BAMTaskSummaryImpl_.endDate;
import QueryParameterIdentifiers.CREATED_ON_LIST;
import QueryParameterIdentifiers.TYPE_LIST;
import BAMTaskSummaryImpl_.pk;
import QueryParameterIdentifiers.TASK_ACTIVATION_TIME_LIST;
import AuditTaskImpl_.parentId;
import AuditTaskImpl_.description;
import BAMTaskSummaryImpl_.duration;
import QueryParameterIdentifiers.USER_ID_LIST;
import BAMTaskSummaryImpl_.createdDate;
import java.util.concurrent.ConcurrentHashMap;
import QueryParameterIdentifiers.DATE_LIST;
import AuditTaskImpl_.status;
import AuditTaskImpl_.createdOn;
import AuditTaskImpl_.id;
import org.jbpm.query.jpa.data.QueryCriteria;
import org.jbpm.query.jpa.data.QueryWhere;
import QueryParameterIdentifiers.TASK_PROCESS_SESSION_ID_LIST;
import QueryParameterIdentifiers.PROCESS_ID_LIST;
import javax.persistence.criteria.Predicate;
import TaskEventImpl_.type;
import AuditTaskImpl_.activationTime;
import AuditTaskImpl_.workItemId;
import AuditTaskImpl_.processSessionId;
import QueryParameterIdentifiers.DEPLOYMENT_ID_LIST;
import QueryParameterIdentifiers.DURATION_LIST;

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
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, TASK_ID_LIST, taskId);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, PROCESS_ID_LIST, processId);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, TASK_STATUS_LIST, status);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, ACTUAL_OWNER_ID_LIST, actualOwner);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, DEPLOYMENT_ID_LIST, deploymentId);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, ID_LIST, id);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, CREATED_ON_LIST, createdOn);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, TASK_PARENT_ID_LIST, parentId);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, CREATED_BY_LIST, createdBy);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, PROCESS_INSTANCE_ID_LIST, processInstanceId);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, TASK_ACTIVATION_TIME_LIST, activationTime);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, TASK_DESCRIPTION_LIST, description);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, TASK_PRIORITY_LIST, priority);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, TASK_NAME_LIST, name);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, TASK_PROCESS_SESSION_ID_LIST, processSessionId);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, TASK_DUE_DATE_LIST, dueDate);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, WORK_ITEM_ID_LIST, workItemId);
        // BAMTaskSummaryImpl
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, TASK_ID_LIST, BAMTaskSummaryImpl_.taskId);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, START_DATE_LIST, startDate);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, DURATION_LIST, duration);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, PROCESS_INSTANCE_ID_LIST, BAMTaskSummaryImpl_.processInstanceId);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, TASK_STATUS_LIST, BAMTaskSummaryImpl_.status);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, USER_ID_LIST, userId);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, END_DATE_LIST, endDate);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, CREATED_ON_LIST, createdDate);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, TASK_NAME_LIST, taskName);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, ID_LIST, pk);
        // TaskEventImpl
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, MESSAGE_LIST, message);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, TASK_ID_LIST, TaskEventImpl_.taskId);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, ID_LIST, TaskEventImpl_.id);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, PROCESS_INSTANCE_ID_LIST, TaskEventImpl_.processInstanceId);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, DATE_LIST, logTime);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, USER_ID_LIST, TaskEventImpl_.userId);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, TYPE_LIST, type);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, WORK_ITEM_ID_LIST, TaskEventImpl_.workItemId);
        // TaskVariableImpl
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, ID_LIST, TaskVariableImpl_.id);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, TASK_ID_LIST, TaskVariableImpl_.taskId);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, PROCESS_INSTANCE_ID_LIST, TaskVariableImpl_.processInstanceId);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, PROCESS_ID_LIST, TaskVariableImpl_.processId);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, TASK_VARIABLE_NAME_ID_LIST, TaskVariableImpl_.name);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, TASK_VARIABLE_VALUE_ID_LIST, value);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, DATE_LIST, modificationDate);
        // add criteria Map{TaskAuditQueryCriteriaUtil.criteriaAttributes} to TaskAuditQueryCriteriaUtil{}
        addCriteria(TaskAuditQueryCriteriaUtil.criteriaAttributes, TYPE_LIST, TaskVariableImpl_.type);
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
        if ((this.persistenceContext) == null) {
            return this.taskAuditService.getEntityManager();
        }else {
            return super.getEntityManager();
        }
    }

    @Override
    protected Object joinTransaction(EntityManager em) {
        if ((this.persistenceContext) == null) {
            return this.taskAuditService.joinTransaction(em);
        }else {
            super.joinTransaction(em);
            return true;
        }
    }

    @Override
    protected void closeEntityManager(EntityManager em, Object transaction) {
        if ((this.persistenceContext) == null) {
            this.taskAuditService.closeEntityManager(em, transaction);
        }
        // em closed outside of this class when used within HT
    }

    // Implementation specific methods --------------------------------------------------------------------------------------------
    @Override
    protected <R, T> Predicate implSpecificCreatePredicateFromSingleCriteria(CriteriaQuery<R> query, CriteriaBuilder builder, Class queryType, QueryCriteria criteria, QueryWhere queryWhere) {
        throw new IllegalStateException((((("List id " + (criteria.getListId())) + " is not supported for queries on ") + (queryType.getSimpleName())) + "."));
    }
}

