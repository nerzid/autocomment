

package org.jbpm.services.task.persistence;

import java.util.ArrayList;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.CollectionAttribute;
import java.util.concurrent.ConcurrentHashMap;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.Date;
import javax.persistence.criteria.Expression;
import java.util.HashSet;
import org.jbpm.services.task.impl.model.I18NTextImpl;
import javax.persistence.criteria.Join;
import java.util.List;
import javax.persistence.metamodel.ListAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.jbpm.services.task.impl.model.OrganizationalEntityImpl;
import org.jbpm.services.task.impl.model.PeopleAssignmentsImpl;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.criteria.Predicate;
import org.jbpm.query.jpa.data.QueryCriteria;
import org.jbpm.query.jpa.data.QueryWhere.QueryCriteriaType;
import org.jbpm.query.jpa.service.QueryModificationService;
import org.kie.internal.query.QueryParameterIdentifiers;
import org.jbpm.query.jpa.data.QueryParameterIdentifiersUtil;
import org.jbpm.query.jpa.data.QueryWhere;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import java.util.ServiceLoader;
import java.util.Set;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import org.kie.api.task.model.Status;
import org.kie.internal.task.api.model.SubTasksStrategy;
import org.jbpm.services.task.impl.model.TaskDataImpl;
import org.jbpm.services.task.impl.model.TaskImpl;
import org.jbpm.services.task.impl.model.TaskImpl_;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.api.task.model.TaskSummary;
import org.jbpm.services.task.query.TaskSummaryImpl;
import javax.persistence.Tuple;
import org.kie.api.task.UserGroupCallback;
import org.jbpm.services.task.impl.model.UserImpl;

public class TaskSummaryQueryCriteriaUtil extends AbstractTaskQueryCriteriaUtil {
    public static final Logger logger = LoggerFactory.getLogger(TaskSummaryQueryCriteriaUtil.class);

    // Query Field Info -----------------------------------------------------------------------------------------------------------
    public static final Map<Class, Map<String, Attribute>> criteriaAttributes = new ConcurrentHashMap<Class, Map<String, Attribute>>();

    private ServiceLoader<QueryModificationService> queryModificationServiceLoader = ServiceLoader.load(QueryModificationService.class);

    @Override
    protected synchronized boolean initializeCriteriaAttributes() {
        if ((TaskImpl_.id) == null) {
            // EMF/persistence has not been initialized:
            // When a persistence unit (EntityManagerFactory) is initialized,
            // the fields of classes annotated with @StaticMetamodel are filled using reflection
            return false;
        } 
        // do not do initialization twice (slow performance, otherwise it doesn't matter)
        if (!(TaskSummaryQueryCriteriaUtil.criteriaAttributes.isEmpty())) {
            return true;
        } 
        // TaskImpl
        addCriteria(TaskSummaryQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.TASK_ACTIVATION_TIME_LIST, TaskImpl.class, TaskDataImpl_.activationTime);
        addCriteria(TaskSummaryQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.ARCHIVED, TaskImpl_.archived);
        addCriteria(TaskSummaryQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.CREATED_ON_LIST, TaskImpl.class, TaskDataImpl_.createdOn);
        addCriteria(TaskSummaryQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.DEPLOYMENT_ID_LIST, TaskImpl.class, TaskDataImpl_.deploymentId);
        addCriteria(TaskSummaryQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.EXPIRATION_TIME_LIST, TaskImpl.class, TaskDataImpl_.expirationTime);
        addCriteria(TaskSummaryQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.TASK_FORM_NAME_LIST, TaskImpl_.formName);
        addCriteria(TaskSummaryQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.PROCESS_ID_LIST, TaskImpl.class, TaskDataImpl_.processId);
        addCriteria(TaskSummaryQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.PROCESS_INSTANCE_ID_LIST, TaskImpl.class, TaskDataImpl_.processInstanceId);
        addCriteria(TaskSummaryQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.PROCESS_SESSION_ID_LIST, TaskImpl.class, TaskDataImpl_.processSessionId);
        addCriteria(TaskSummaryQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.SKIPPABLE, TaskImpl.class, TaskDataImpl_.skipable);
        addCriteria(TaskSummaryQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.TASK_STATUS_LIST, TaskImpl.class, TaskDataImpl_.status);
        addCriteria(TaskSummaryQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.SUB_TASKS_STRATEGY, TaskImpl_.subTaskStrategy);
        addCriteria(TaskSummaryQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.TASK_ID_LIST, TaskImpl_.id);
        addCriteria(TaskSummaryQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.TASK_PARENT_ID_LIST, TaskImpl.class, TaskDataImpl_.parentId);
        addCriteria(TaskSummaryQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.TYPE_LIST, TaskImpl_.taskType);
        addCriteria(TaskSummaryQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.WORK_ITEM_ID_LIST, TaskImpl.class, TaskDataImpl_.workItemId);
        addCriteria(TaskSummaryQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.TASK_PRIORITY_LIST, TaskImpl.class, TaskImpl_.priority);
        addCriteria(TaskSummaryQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.TASK_DESCRIPTION_LIST, TaskImpl_.descriptions);
        addCriteria(TaskSummaryQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.TASK_NAME_LIST, TaskImpl_.names);
        addCriteria(TaskSummaryQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.TASK_SUBJECT_LIST, TaskImpl_.subjects);
        addCriteria(TaskSummaryQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.ACTUAL_OWNER_ID_LIST, TaskImpl.class, TaskDataImpl_.actualOwner);
        addCriteria(TaskSummaryQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.CREATED_BY_LIST, TaskImpl.class, TaskDataImpl_.createdBy);// initiator
        
        addCriteria(TaskSummaryQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.BUSINESS_ADMIN_ID_LIST, TaskImpl.class, PeopleAssignmentsImpl_.businessAdministrators);
        addCriteria(TaskSummaryQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.POTENTIAL_OWNER_ID_LIST, TaskImpl.class, PeopleAssignmentsImpl_.potentialOwners);
        addCriteria(TaskSummaryQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.STAKEHOLDER_ID_LIST, TaskImpl.class, PeopleAssignmentsImpl_.taskStakeholders);
        addCriteria(TaskSummaryQueryCriteriaUtil.criteriaAttributes, QueryParameterIdentifiers.EXCLUDED_OWNER_ID_LIST, TaskImpl.class, PeopleAssignmentsImpl_.excludedOwners);
        return true;
    }

    private static final Set<String> taskUserRoleLimitingListIds = new HashSet<String>();

    static {
        TaskSummaryQueryCriteriaUtil.taskUserRoleLimitingListIds.add(QueryParameterIdentifiers.ACTUAL_OWNER_ID_LIST);
        TaskSummaryQueryCriteriaUtil.taskUserRoleLimitingListIds.add(QueryParameterIdentifiers.CREATED_BY_LIST);
        TaskSummaryQueryCriteriaUtil.taskUserRoleLimitingListIds.add(QueryParameterIdentifiers.BUSINESS_ADMIN_ID_LIST);
        TaskSummaryQueryCriteriaUtil.taskUserRoleLimitingListIds.add(QueryParameterIdentifiers.POTENTIAL_OWNER_ID_LIST);
        TaskSummaryQueryCriteriaUtil.taskUserRoleLimitingListIds.add(QueryParameterIdentifiers.STAKEHOLDER_ID_LIST);
    }

    public TaskSummaryQueryCriteriaUtil(TaskPersistenceContext persistenceContext) {
        super(persistenceContext);
        initialize(TaskSummaryQueryCriteriaUtil.criteriaAttributes);
    }

    public TaskSummaryQueryCriteriaUtil() {
        initialize(TaskSummaryQueryCriteriaUtil.criteriaAttributes);
    }

    // Implementation specific methods --------------------------------------------------------------------------------------------
    public List<TaskSummary> doCriteriaQuery(String userId, UserGroupCallback userGroupCallback, QueryWhere queryWhere) {
        // 1. create builder and query instances
        CriteriaBuilder builder = getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery<Tuple> criteriaQuery = builder.createTupleQuery();
        // 2. query base
        Root<TaskImpl> taskRoot = criteriaQuery.from(TaskImpl.class);
        criteriaQuery = // 0
        // 1
        // 2
        // 3
        // 4
        // 5
        // 6
        // 7
        // 8
        // 9
        // 10
        // 11
        // 12
        // 13
        // 14
        // 15
        // 16
        // 17
        criteriaQuery.multiselect(taskRoot.get(TaskImpl_.id), taskRoot.get(TaskImpl_.name), taskRoot.get(TaskImpl_.subject), taskRoot.get(TaskImpl_.description), taskRoot.get(TaskImpl_.taskData).get(TaskDataImpl_.status), taskRoot.get(TaskImpl_.priority), taskRoot.get(TaskImpl_.taskData).get(TaskDataImpl_.skipable), taskRoot.get(TaskImpl_.taskData).get(TaskDataImpl_.actualOwner).get(UserImpl_.id), taskRoot.get(TaskImpl_.taskData).get(TaskDataImpl_.createdBy).get(UserImpl_.id), taskRoot.get(TaskImpl_.taskData).get(TaskDataImpl_.createdOn), taskRoot.get(TaskImpl_.taskData).get(TaskDataImpl_.activationTime), taskRoot.get(TaskImpl_.taskData).get(TaskDataImpl_.expirationTime), taskRoot.get(TaskImpl_.taskData).get(TaskDataImpl_.processId), taskRoot.get(TaskImpl_.taskData).get(TaskDataImpl_.processSessionId), taskRoot.get(TaskImpl_.taskData).get(TaskDataImpl_.processInstanceId), taskRoot.get(TaskImpl_.taskData).get(TaskDataImpl_.deploymentId), taskRoot.get(TaskImpl_.subTaskStrategy), taskRoot.get(TaskImpl_.taskData).get(TaskDataImpl_.parentId));
        taskRoot.join(TaskImpl_.taskData);// added for convienence sake, since other logic expects to find this join
        
        checkExistingCriteriaForUserBasedLimit(queryWhere, userId, userGroupCallback);
        for (QueryModificationService queryModificationService : queryModificationServiceLoader) {
            queryModificationService.optimizeCriteria(queryWhere);
        }
        // 4. process query criteria
        fillCriteriaQuery(criteriaQuery, queryWhere, builder, TaskImpl.class);
        // 5. retrieve result (after also applying meta-criteria)
        useDistinctWhenLefOuterJoinsPresent(criteriaQuery);
        List<Tuple> result = createQueryAndCallApplyMetaCriteriaAndGetResult(queryWhere, criteriaQuery, builder);
        List<TaskSummary> taskSummaryList = new ArrayList<TaskSummary>(result.size());
        for (Tuple tupleRow : result) {
            int i = 0;
            // @formatter:off
            TaskSummaryImpl taskSummaryImpl = // id
            // name, subject, description
            // status, prio, skippable
            // actual owner, created by
            // created on, activation time, expiration time
            // process id, process session id, process inst id, deployment id
            new TaskSummaryImpl(tupleRow.get((i++), Long.class), tupleRow.get((i++), String.class), tupleRow.get((i++), String.class), tupleRow.get((i++), String.class), tupleRow.get((i++), Status.class), tupleRow.get((i++), Integer.class), tupleRow.get((i++), Boolean.class), tupleRow.get((i++), String.class), tupleRow.get((i++), String.class), tupleRow.get((i++), Date.class), tupleRow.get((i++), Date.class), tupleRow.get((i++), Date.class), tupleRow.get((i++), String.class), tupleRow.get((i++), Long.class), tupleRow.get((i++), Long.class), tupleRow.get((i++), String.class), tupleRow.get((i++), SubTasksStrategy.class), tupleRow.get((i++), Long.class));
            // @formatter:on
            taskSummaryList.add(taskSummaryImpl);
        }
        return taskSummaryList;
    }

    /**
     * This method checks whether or not the query *already* contains a limiting criteria (in short, a criteria that limits results
     * of the query) that refers to the user or (the user's groups).  If there is no user/group limiting criteria, then a
     * {@link QueryCriteria} with the user and group ids is added to the {@link QueryWhere} instance
     * </p>
     * If this method returns true, then there is no need to add additional criteria to the query to limit the query results
     * to tasks that the user may see -- there is already query present in the query that takes care of this.
     * 
     * @param queryWhere The {@link QueryWhere} instance containing the query criteria
     * @param userId The string user id of the user calling the query
     * @param userGroupCallback A {@link UserGroupCallback} instance in order to retrieve the groups of the given user
     */
    private void checkExistingCriteriaForUserBasedLimit(QueryWhere queryWhere, String userId, UserGroupCallback userGroupCallback) {
        List<String> groupIds = userGroupCallback.getGroupsForUser(userId, null, null);
        Set<String> userAndGroupIds = new HashSet<String>();
        if (groupIds != null) {
            userAndGroupIds.addAll(groupIds);
        } 
        userAndGroupIds.add(userId);
        if (!(TaskSummaryQueryCriteriaUtil.criteriaListForcesUserLimitation(userAndGroupIds, queryWhere.getCriteria()))) {
            addUserRolesLimitCriteria(queryWhere, userId, groupIds);
        } 
    }

    /**
     * This method calls itself recursively to determine whether or not the given {@link List}<{@link QueryCriteria}> contains a
     * user/group limiting criteria.
     * 
     * @param userAndGroupIds A set containing the user id and all group ids
     * @param criteriaList The {@link List} of {@link QueryCriteria} to search
     * @return Whether or not the {@link QueryCriteria} list contains a user/group limiting criteria
     */
    private static boolean criteriaListForcesUserLimitation(Set<String> userAndGroupIds, List<QueryCriteria> criteriaList) {
        boolean userLimitiationIntersection = false;
        if (criteriaList.isEmpty()) {
            return false;
        } 
        for (QueryCriteria criteria : criteriaList) {
            if (criteria.isUnion()) {
                return false;
            } 
            if (criteria.isGroupCriteria()) {
                if (TaskSummaryQueryCriteriaUtil.criteriaListForcesUserLimitation(userAndGroupIds, criteria.getCriteria())) {
                    return true;
                } 
                continue;
            } 
            // intersection criteria
            if (TaskSummaryQueryCriteriaUtil.taskUserRoleLimitingListIds.contains(criteria.getListId())) {
                for (Object param : criteria.getParameters()) {
                    if (userAndGroupIds.contains(param)) {
                        return true;
                    } 
                }
            } 
        }
        return userLimitiationIntersection;
    }

    /**
     * Adds an (intersecting) {@link QueryCriteria} that limits the results to results that the user is allowed to see
     * 
     * @param queryWhere The {@link QueryWhere} instance that defines the query criteria
     * @param userId The user id
     * @param groupIds The user's group ids
     */
    private void addUserRolesLimitCriteria(QueryWhere queryWhere, String userId, List<String> groupIds) {
        List<QueryCriteria> newBaseCriteriaList = new ArrayList<QueryCriteria>(2);
        // user role limiting criteria
        QueryCriteria userRolesLimitingCriteria = new QueryCriteria(QueryParameterIdentifiers.TASK_USER_ROLES_LIMIT_LIST, false, QueryCriteriaType.NORMAL, 2);
        userRolesLimitingCriteria.setFirst(true);
        userRolesLimitingCriteria.getValues().add(userId);
        userRolesLimitingCriteria.getValues().add(groupIds);
        newBaseCriteriaList.add(userRolesLimitingCriteria);
        // original criteria list in a new group
        if (!(queryWhere.getCriteria().isEmpty())) {
            QueryCriteria originalBaseCriteriaGroup = new QueryCriteria(false);
            originalBaseCriteriaGroup.setCriteria(queryWhere.getCriteria());
            newBaseCriteriaList.add(originalBaseCriteriaGroup);
        } 
        queryWhere.setCriteria(newBaseCriteriaList);
    }

    /* (non-Javadoc)
    @see org.jbpm.query.jpa.impl.QueryCriteriaUtil#getEntityField(javax.persistence.criteria.CriteriaQuery, java.lang.Class, java.lang.String)
     */
    @Override
    protected <T> Expression getEntityField(CriteriaQuery<T> query, String listId, Attribute attr) {
        if (attr == null) {
            return null;
        } 
        Root<TaskImpl> taskRoot = null;
        Join<TaskImpl, TaskDataImpl> taskDataJoin = null;
        Join<TaskImpl, PeopleAssignmentsImpl> peopAssignJoin = null;
        for (Root root : query.getRoots()) {
            if (TaskImpl.class.equals(root.getJavaType())) {
                taskRoot = ((Root<TaskImpl>) (root));
                for (Join<TaskImpl, ?> join : taskRoot.getJoins()) {
                    if (TaskDataImpl.class.equals(join.getJavaType())) {
                        taskDataJoin = ((Join<TaskImpl, TaskDataImpl>) (join));
                    } else if (PeopleAssignmentsImpl.class.equals(join.getJavaType())) {
                        peopAssignJoin = ((Join<TaskImpl, PeopleAssignmentsImpl>) (join));
                    } 
                }
            } 
        }
        assert taskRoot != null : "Unable to find TaskImpl Root in query!";
        if (taskDataJoin == null) {
            taskDataJoin = taskRoot.join(TaskImpl_.taskData);
        } 
        assert taskDataJoin != null : "Unable to find TaskDataImpl Join in query!";
        return TaskSummaryQueryCriteriaUtil.taskImplSpecificGetEntityField(query, taskRoot, taskDataJoin, peopAssignJoin, listId, attr);
    }

    @SuppressWarnings(value = "unchecked")
    public static <T> Expression taskImplSpecificGetEntityField(CriteriaQuery<T> query, org.jbpm.services.task.persistence.Root<TaskImpl> taskRoot, org.jbpm.services.task.persistence.Join<TaskImpl, TaskDataImpl> taskDataJoin, org.jbpm.services.task.persistence.Join<TaskImpl, PeopleAssignmentsImpl> peopleAssignJoin, String listId, Attribute attr) {
        Expression entityField = null;
        if (attr != null) {
            if (((listId.equals(QueryParameterIdentifiers.TASK_DESCRIPTION_LIST)) || (listId.equals(QueryParameterIdentifiers.TASK_NAME_LIST))) || (listId.equals(QueryParameterIdentifiers.TASK_SUBJECT_LIST))) {
                // we can *not* query on @LOB annotated fields (and it would be inefficient for a query api to do this?)
                // so we query on the (max 255 char length) string equivalent fields
                entityField = TaskSummaryQueryCriteriaUtil.getJoinedEntityField(taskRoot, ((Attribute<TaskImpl, I18NTextImpl>) (attr)), I18NTextImpl_.shortText);
            } else if ((listId.equals(QueryParameterIdentifiers.ACTUAL_OWNER_ID_LIST)) || (listId.equals(QueryParameterIdentifiers.CREATED_BY_LIST))) {
                if (taskDataJoin == null) {
                    taskDataJoin = taskRoot.join(TaskImpl_.taskData);
                } 
                // task -> taskData -> (actualOwn/createdBy) UserImpl ->  id
                entityField = TaskSummaryQueryCriteriaUtil.getJoinedEntityField(taskDataJoin, ((Attribute<TaskDataImpl, UserImpl>) (attr)), UserImpl_.id);
            } else if ((((listId.equals(QueryParameterIdentifiers.BUSINESS_ADMIN_ID_LIST)) || (listId.equals(QueryParameterIdentifiers.POTENTIAL_OWNER_ID_LIST))) || (listId.equals(QueryParameterIdentifiers.STAKEHOLDER_ID_LIST))) || (listId.equals(QueryParameterIdentifiers.EXCLUDED_OWNER_ID_LIST))) {
                if (peopleAssignJoin == null) {
                    peopleAssignJoin = taskRoot.join(TaskImpl_.peopleAssignments);
                } 
                // task -> peopleAssignments -> (bus admin/pot owner/stake holder/excl user) OrganizationalEntityImpl ->  id
                entityField = TaskSummaryQueryCriteriaUtil.getJoinedEntityField(peopleAssignJoin, ((Attribute<PeopleAssignmentsImpl, OrganizationalEntityImpl>) (attr)), OrganizationalEntityImpl_.id);
            } else {
                if (taskDataJoin == null) {
                    taskDataJoin = taskRoot.join(TaskImpl_.taskData);
                } 
                Class attrType = attr.getDeclaringType().getJavaType();
                javax.persistence.criteria.From[] taskRoots = new javax.persistence.criteria.From[]{ taskRoot , taskDataJoin };
                for (javax.persistence.criteria.From from : taskRoots) {
                    if (from.getJavaType().equals(attrType)) {
                        if (attr != null) {
                            if (attr instanceof SingularAttribute) {
                                entityField = from.get(((SingularAttribute) (attr)));
                            } else if (attr instanceof PluralAttribute) {
                                entityField = from.get(((PluralAttribute) (attr)));
                            } else {
                                throw new IllegalStateException(((("Unexpected attribute type when processing criteria with list id " + listId) + ": ") + (attr.getClass().getName())));
                            }
                            break;
                        } 
                    } 
                }
            }
        } 
        return entityField;
    }

    /**
     * This retrieves the correct field ({@link Expression}) that should be used when building the {@link Predicate}.
     * </p>
     * This field is necessary because of the amount of joins and the complexity in the human-task schema.
     * 
     * @param grandparentJoin This is the parent join,
     *                        for example the join between TaskDataImpl -> PeopleAssignments
     * @param parentJoinAttr This is the {@link Attribute} with the information over the join (from the parent) that we need to create,
     *                       for example the {@link SingularAttribute}<{@link PeopleAssignmentsImpl}, {@link OrganizationalEntityImpl}> {@link Attribute}.
     * @param fieldAttr This is the {@link Attribute} with the actual attribute that we create an {@link Expression} to build a {@link Predicate} for,
     *                  for example the {@link OrganizationalEntityImpl_#id} field.
     * @return an {@link Expression} that can be used in a predicate with the values/parameters from a {@link QueryCriteria} instance
     */
    @SuppressWarnings(value = "unchecked")
    public static <F, T> Expression getJoinedEntityField(From<?, F> grandparentJoin, org.jbpm.services.task.persistence.Attribute<?, T> parentJoinAttr, SingularAttribute fieldAttr) {
        // task -> * -> origJoin -> (fieldParentAttr field in) tojoinType ->  fieldAttr
        Class toAttrJoinType;
        if (parentJoinAttr instanceof SingularAttribute) {
            toAttrJoinType = parentJoinAttr.getJavaType();
        } else if (parentJoinAttr instanceof PluralAttribute) {
            toAttrJoinType = ((PluralAttribute) (parentJoinAttr)).getElementType().getJavaType();
        } else {
            String joinName = ((parentJoinAttr.getDeclaringType().getJavaType().getSimpleName()) + ".") + (parentJoinAttr.getName());
            throw new IllegalStateException(("Unknown attribute type encountered when trying to join " + joinName));
        }
        Join<F, T> fieldParentJoin = null;
        for (Join<F, ?> join : grandparentJoin.getJoins()) {
            if (join.getJavaType().equals(toAttrJoinType)) {
                if (join.getAttribute().equals(parentJoinAttr)) {
                    fieldParentJoin = ((Join<F, T>) (join));
                    if (!(JoinType.INNER.equals(fieldParentJoin.getJoinType()))) {
                        // This a criteria set by the user (as opposed to the user-limiting criteria) -- it MUST be followed
                        // This means that the join is not optional (LEFT) but mandatory (INNER)
                        fieldParentJoin = null;
                    } 
                    break;
                } 
            } 
        }
        if (fieldParentJoin == null) {
            if (parentJoinAttr instanceof SingularAttribute) {
                fieldParentJoin = grandparentJoin.join(((SingularAttribute) (parentJoinAttr)));
            } else if (parentJoinAttr instanceof CollectionAttribute) {
                fieldParentJoin = grandparentJoin.join(((CollectionAttribute) (parentJoinAttr)));
            } else if (parentJoinAttr instanceof ListAttribute) {
                fieldParentJoin = grandparentJoin.join(((ListAttribute) (parentJoinAttr)));
            } else if (parentJoinAttr instanceof SetAttribute) {
                fieldParentJoin = grandparentJoin.join(((SetAttribute) (parentJoinAttr)));
            } else {
                throw new IllegalStateException(("Unknown attribute type encountered when trying to join" + (parentJoinAttr.getName())));
            }
        } 
        return fieldParentJoin.get(fieldAttr);
    }

    @Override
    protected <R, T> Predicate implSpecificCreatePredicateFromSingleCriteria(CriteriaQuery<R> query, CriteriaBuilder builder, Class queryType, QueryCriteria criteria, QueryWhere queryWhere) {
        Predicate predicate = null;
        String listId = criteria.getListId();
        if (QueryParameterIdentifiers.TASK_USER_ROLES_LIMIT_LIST.equals(listId)) {
            predicate = TaskSummaryQueryCriteriaUtil.createTaskUserRolesLimitPredicate(criteria, query, builder);
        } else {
            for (QueryModificationService queryModService : queryModificationServiceLoader) {
                if (queryModService.accepts(listId)) {
                    return queryModService.createPredicate(criteria, query, builder);
                } 
            }
            throw new IllegalStateException((((("List id " + (QueryParameterIdentifiersUtil.getQueryParameterIdNameMap().get(Integer.parseInt(criteria.getListId())))) + " is not supported for queries on ") + (TaskImpl.class.getSimpleName())) + "."));
        }
        return predicate;
    }

    @SuppressWarnings(value = "unchecked")
    private static <T> Predicate createTaskUserRolesLimitPredicate(QueryCriteria criteria, CriteriaQuery<T> criteriaQuery, CriteriaBuilder builder) {
        String userId = ((String) (criteria.getValues().get(0)));
        List<String> groupIds = ((List<String>) (criteria.getValues().get(1)));
        Root<TaskImpl> taskRoot = getRoot(criteriaQuery, TaskImpl.class);
        assert taskRoot != null : "TaskImpl Root instance could not be found in query!";
        boolean groupIdsPresent = (groupIds.size()) > 0;
        int numPredicates = groupIdsPresent ? 5 : 2;
        // joins that apply to the user id's
        Predicate[] userGroupLimitingPredicates = new Predicate[numPredicates];
        userGroupLimitingPredicates[0] = builder.equal(taskRoot.get(TaskImpl_.taskData).get(TaskDataImpl_.actualOwner).get(UserImpl_.id), userId);
        userGroupLimitingPredicates[1] = builder.equal(taskRoot.get(TaskImpl_.taskData).get(TaskDataImpl_.createdBy).get(UserImpl_.id), userId);
        Join<TaskImpl, TaskDataImpl> taskDataJoin = null;
        Join<TaskImpl, PeopleAssignmentsImpl> peopleAssignJoin = null;
        if (taskRoot != null) {
            for (Join<TaskImpl, ?> join : taskRoot.getJoins()) {
                if (join.getJavaType().equals(PeopleAssignmentsImpl.class)) {
                    peopleAssignJoin = ((Join<TaskImpl, PeopleAssignmentsImpl>) (join));
                } else if (join.getJavaType().equals(TaskDataImpl.class)) {
                    taskDataJoin = ((Join<TaskImpl, TaskDataImpl>) (join));
                } 
            }
        } 
        assert taskDataJoin != null : "TaskImpl -> TaskDataImpl join could not be found in query!";
        // joins that apply to the group id's
        if (groupIdsPresent) {
            if (peopleAssignJoin == null) {
                peopleAssignJoin = taskRoot.join(TaskImpl_.peopleAssignments);
            } 
            ListJoin<PeopleAssignmentsImpl, OrganizationalEntityImpl>[] groupJoins = TaskSummaryQueryCriteriaUtil.getPeopleAssignmentsJoins(peopleAssignJoin);
            for (int i = 0; i < groupJoins; ++i) {
                userGroupLimitingPredicates[(i + 2)] = builder.or(builder.equal(groupJoins[i].get(OrganizationalEntityImpl_.id), userId), groupJoins[i].get(OrganizationalEntityImpl_.id).in(groupIds));
            }
        } 
        return builder.or(userGroupLimitingPredicates);
    }

    private static ListJoin<PeopleAssignmentsImpl, OrganizationalEntityImpl>[] getPeopleAssignmentsJoins(org.jbpm.services.task.persistence.Join<TaskImpl, PeopleAssignmentsImpl> peopleAssignJoin) {
        ListJoin<PeopleAssignmentsImpl, OrganizationalEntityImpl>[] joins = new javax.persistence.criteria.ListJoin[3];
        for (Join<PeopleAssignmentsImpl, ?> join : peopleAssignJoin.getJoins()) {
            String joinFieldName = join.getAttribute().getName();
            if (PeopleAssignmentsImpl_.businessAdministrators.getName().equals(joinFieldName)) {
                joins[0] = ((javax.persistence.criteria.ListJoin<PeopleAssignmentsImpl, OrganizationalEntityImpl>) (join));
            } else if (PeopleAssignmentsImpl_.potentialOwners.getName().equals(joinFieldName)) {
                joins[1] = ((javax.persistence.criteria.ListJoin<PeopleAssignmentsImpl, OrganizationalEntityImpl>) (join));
            } else if (PeopleAssignmentsImpl_.taskStakeholders.getName().equals(joinFieldName)) {
                joins[2] = ((javax.persistence.criteria.ListJoin<PeopleAssignmentsImpl, OrganizationalEntityImpl>) (join));
            } 
        }
        if ((joins[0]) == null) {
            joins[0] = peopleAssignJoin.join(PeopleAssignmentsImpl_.businessAdministrators, JoinType.LEFT);
        } 
        if ((joins[1]) == null) {
            joins[1] = peopleAssignJoin.join(PeopleAssignmentsImpl_.potentialOwners, JoinType.LEFT);
        } 
        if ((joins[2]) == null) {
            joins[2] = peopleAssignJoin.join(PeopleAssignmentsImpl_.taskStakeholders, JoinType.LEFT);
        } 
        assert (joins[0]) != null : "Could not find business administrators join!";
        assert (joins[1]) != null : "Could not find potential owners join!";
        assert (joins[2]) != null : "Could not find task stakeholders join!";
        return joins;
    }

    private <T> void useDistinctWhenLefOuterJoinsPresent(CriteriaQuery<T> criteriaQuery) {
        boolean useDistinct = false;
        Root<TaskImpl> taskRoot = null;
        ROOTS_FOR : for (Root root : criteriaQuery.getRoots()) {
            if (TaskImpl.class.equals(root.getJavaType())) {
                taskRoot = ((Root<TaskImpl>) (root));
                for (Join<TaskImpl, ?> taskJoin : taskRoot.getJoins()) {
                    if (PeopleAssignmentsImpl.class.equals(taskJoin.getJavaType())) {
                        Join<TaskImpl, PeopleAssignmentsImpl> peopleAssignJoin = ((Join<TaskImpl, PeopleAssignmentsImpl>) (taskJoin));
                        if (JoinType.LEFT.equals(peopleAssignJoin.getJoinType())) {
                            useDistinct = true;
                            break ROOTS_FOR;
                        } 
                        for (Join peopleAssignJoinJoin : peopleAssignJoin.getJoins()) {
                            if (JoinType.LEFT.equals(peopleAssignJoinJoin.getJoinType())) {
                                useDistinct = true;
                                break ROOTS_FOR;
                            } 
                        }
                    } 
                }
            } 
        }
        if (useDistinct) {
            criteriaQuery.distinct(true);
        } 
    }

    @Override
    protected <T, R> Expression getOrderByExpression(CriteriaQuery<R> query, Class<T> queryType, String orderByListId) {
        List<Selection<?>> selections = query.getSelection().getCompoundSelectionItems();
        Selection orderBySelection = null;
        if (orderByListId.equals(QueryParameterIdentifiers.TASK_ID_LIST)) {
            orderBySelection = selections.get(0);
        } else if (orderByListId.equals(QueryParameterIdentifiers.TASK_NAME_LIST)) {
            orderBySelection = selections.get(1);
        } else if (orderByListId.equals(QueryParameterIdentifiers.TASK_STATUS_LIST)) {
            orderBySelection = selections.get(4);
        } else if (orderByListId.equals(QueryParameterIdentifiers.CREATED_BY_LIST)) {
            orderBySelection = selections.get(8);
        } else if (orderByListId.equals(QueryParameterIdentifiers.CREATED_ON_LIST)) {
            orderBySelection = selections.get(9);
        } else if (orderByListId.equals(QueryParameterIdentifiers.PROCESS_INSTANCE_ID_LIST)) {
            orderBySelection = selections.get(14);
        } 
        return ((Expression<?>) (orderBySelection));
    }

    public static <Q, T> Predicate taskSpecificCreatePredicateFromSingleCriteria(CriteriaQuery<Q> query, CriteriaBuilder builder, QueryCriteria criteria, QueryWhere queryWhere) {
        Predicate predicate = null;
        String listId = criteria.getListId();
        if (QueryParameterIdentifiers.TASK_USER_ROLES_LIMIT_LIST.equals(listId)) {
            predicate = TaskSummaryQueryCriteriaUtil.createTaskUserRolesLimitPredicate(criteria, query, builder);
        } else {
            throw new IllegalStateException((((("List id " + (QueryParameterIdentifiersUtil.getQueryParameterIdNameMap().get(Integer.parseInt(criteria.getListId())))) + " is not supported for queries on ") + (TaskImpl.class.getSimpleName())) + "."));
        }
        return predicate;
    }
}

