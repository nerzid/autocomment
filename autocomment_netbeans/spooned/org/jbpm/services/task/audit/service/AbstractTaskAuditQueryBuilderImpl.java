

package org.jbpm.services.task.audit.service;

import QueryParameterIdentifiers.PROCESS_INSTANCE_ID_LIST;
import org.jbpm.query.jpa.builder.impl.AbstractQueryBuilderImpl;
import QueryParameterIdentifiers.DATE_LIST;
import java.util.Date;
import org.kie.internal.task.api.InternalTaskService;
import java.util.List;
import QueryParameterIdentifiers.PROCESS_ID_LIST;
import org.kie.internal.query.ParametrizedQuery;
import org.jbpm.query.jpa.impl.QueryCriteriaUtil;
import org.jbpm.query.jpa.data.QueryWhere;
import org.jbpm.services.task.commands.TaskCommand;

/**
 * @param <Q> The type of the interface of the specific {@link AbstractQueryBuilderImpl} implementation
 * @param <R> The type of result
 */
public abstract class AbstractTaskAuditQueryBuilderImpl<Q, R> extends AbstractQueryBuilderImpl<Q> {
    private final TaskAuditQueryCriteriaUtil queryCriteriaUtil;

    private final InternalTaskService taskService;

    public AbstractTaskAuditQueryBuilderImpl(TaskJPAAuditService jpaAuditService) {
        this(jpaAuditService, null);
    }

    public AbstractTaskAuditQueryBuilderImpl(InternalTaskService taskService) {
        this(null, taskService);
    }

    private AbstractTaskAuditQueryBuilderImpl(TaskJPAAuditService jpaService, InternalTaskService taskService) {
        if (jpaService != null) {
            this.queryCriteriaUtil = new TaskAuditQueryCriteriaUtil(jpaService);
            this.taskService = null;
        }else
            if (taskService != null) {
                this.queryCriteriaUtil = null;
                this.taskService = taskService;
            }else {
                throw new IllegalStateException((("At least one of the " + (this.getClass().getSimpleName())) + " constructor arguments must be non-null!"));
            }
        
    }

    // query builder result methods
    public Q processInstanceId(long... processInstanceId) {
        // add long void{PROCESS_INSTANCE_ID_LIST} to AbstractTaskAuditQueryBuilderImpl{}
        addLongParameter(PROCESS_INSTANCE_ID_LIST, "process instance id", processInstanceId);
        return ((Q) (this));
    }

    public Q processInstanceIdRange(Long processInstanceIdMin, Long processInstanceIdMax) {
        // add range void{PROCESS_INSTANCE_ID_LIST} to AbstractTaskAuditQueryBuilderImpl{}
        addRangeParameters(PROCESS_INSTANCE_ID_LIST, "process instance id", processInstanceIdMin, processInstanceIdMax);
        return ((Q) (this));
    }

    public Q processId(String... processId) {
        // add object void{PROCESS_ID_LIST} to AbstractTaskAuditQueryBuilderImpl{}
        addObjectParameter(PROCESS_ID_LIST, "process id", processId);
        return ((Q) (this));
    }

    public Q date(Date... date) {
        // add object void{DATE_LIST} to AbstractTaskAuditQueryBuilderImpl{}
        addObjectParameter(DATE_LIST, "date", date);
        return ((Q) (this));
    }

    public Q dateRangeStart(Date rangeStart) {
        // add range void{DATE_LIST} to AbstractTaskAuditQueryBuilderImpl{}
        addRangeParameter(DATE_LIST, "date range start", rangeStart, true);
        return ((Q) (this));
    }

    public Q dateRangeEnd(Date rangeStart) {
        // add range void{DATE_LIST} to AbstractTaskAuditQueryBuilderImpl{}
        addRangeParameter(DATE_LIST, "date range end", rangeStart, false);
        return ((Q) (this));
    }

    protected abstract Class<R> getResultType();

    protected abstract Class getQueryType();

    protected abstract TaskCommand getCommand();

    public ParametrizedQuery<R> build() {
        return new ParametrizedQuery<R>() {
            private QueryWhere queryWhere;

            @Override
            public List<R> getResultList() {
                if ((queryCriteriaUtil) != null) {
                    List implResult = queryCriteriaUtil.doCriteriaQuery(queryWhere, getQueryType());
                    return QueryCriteriaUtil.convertListToInterfaceList(implResult, getResultType());
                }else {
                    return ((List<R>) (taskService.execute(getCommand())));
                }
            }
        };
    }
}

