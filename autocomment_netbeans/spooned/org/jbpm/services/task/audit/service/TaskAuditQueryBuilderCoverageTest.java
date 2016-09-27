

package org.jbpm.services.task.audit.service;

import org.junit.AfterClass;
import org.kie.internal.task.query.AuditTaskQueryBuilder;
import org.jbpm.services.task.audit.BAMTaskSummaryQueryBuilder;
import org.junit.Before;
import org.junit.BeforeClass;
import org.kie.internal.process.CorrelationKey;
import javax.persistence.EntityManagerFactory;
import org.jbpm.services.task.HumanTaskServicesBaseTest;
import org.jbpm.persistence.correlation.JPACorrelationKeyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jbpm.query.QueryBuilderCoverageTestUtil.ModuleSpecificInputFiller;
import AuditTaskQueryBuilder.OrderBy;
import javax.persistence.Persistence;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import org.jbpm.query.QueryBuilderCoverageTestUtil;
import org.kie.api.task.model.Status;
import org.kie.internal.task.query.TaskEventQueryBuilder;
import org.kie.internal.task.api.model.TaskEvent.TaskEventType;
import org.kie.internal.task.query.TaskVariableQueryBuilder;
import org.junit.Test;
import org.kie.internal.task.api.TaskVariable.VariableType;

public class TaskAuditQueryBuilderCoverageTest extends HumanTaskServicesBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(TaskAuditQueryBuilderCoverageTest.class);

    private static PoolingDataSource pds;

    private static EntityManagerFactory emf;

    private TaskJPAAuditService auditService;

    @BeforeClass
    public static void configure() {
        TaskAuditQueryBuilderCoverageTest.pds = setupPoolingDataSource();
        TaskAuditQueryBuilderCoverageTest.emf = Persistence.createEntityManagerFactory("org.jbpm.services.task");
        QueryBuilderCoverageTestUtil.hackTheDatabaseMetadataLoggerBecauseTheresALogbackXmlInTheClasspath();
    }

    @Before
    public void setup() {
        auditService = new TaskJPAAuditService(TaskAuditQueryBuilderCoverageTest.emf);
    }

    @AfterClass
    public static void reset() {
        if ((TaskAuditQueryBuilderCoverageTest.emf) != null) {
            TaskAuditQueryBuilderCoverageTest.emf.close();
            TaskAuditQueryBuilderCoverageTest.emf = null;
        } 
        if ((TaskAuditQueryBuilderCoverageTest.pds) != null) {
            TaskAuditQueryBuilderCoverageTest.pds.close();
            TaskAuditQueryBuilderCoverageTest.pds = null;
        } 
    }

    private static ModuleSpecificInputFiller inputFiller = new ModuleSpecificInputFiller() {
        private final JPACorrelationKeyFactory correlationKeyFactory = new JPACorrelationKeyFactory();

        private int auditTaskOrderByType = 0;

        private int taskEventOrderByType = 0;

        private int bamTaskSummaryOrderByType = 0;

        private int taskVariableOrderByType = 0;

        private int taskVariableType = 0;

        @Override
        public Object fillInput(Class type) {
            if (type.equals(CorrelationKey.class)) {
                return correlationKeyFactory.newCorrelationKey("business-key");
            } 
            if (type.equals(OrderBy.class)) {
                return (((auditTaskOrderByType)++) % 2) == 0 ? OrderBy.processId : OrderBy.processInstanceId;
            } else if (type.equals(TaskEventQueryBuilder.OrderBy.class)) {
                int typeCase = ((taskEventOrderByType)++) % 3;
                switch (typeCase) {
                    case 0 :
                        return TaskEventQueryBuilder.OrderBy.logTime;
                    case 1 :
                        return TaskEventQueryBuilder.OrderBy.processInstanceId;
                    case 2 :
                        return TaskEventQueryBuilder.OrderBy.taskId;
                }
            } else if (type.equals(TaskVariableQueryBuilder.OrderBy.class)) {
                int typeCase = ((taskVariableOrderByType)++) % 4;
                switch (typeCase) {
                    case 0 :
                        return TaskVariableQueryBuilder.OrderBy.id;
                    case 1 :
                        return TaskVariableQueryBuilder.OrderBy.processInstanceId;
                    case 2 :
                        return TaskVariableQueryBuilder.OrderBy.modificationDate;
                    case 3 :
                        return TaskVariableQueryBuilder.OrderBy.taskId;
                }
            } else if (type.equals(BAMTaskSummaryQueryBuilder.OrderBy.class)) {
                int typeCase = ((bamTaskSummaryOrderByType)++) % 6;
                switch (typeCase) {
                    case 0 :
                        return BAMTaskSummaryQueryBuilder.OrderBy.createdDate;
                    case 1 :
                        return BAMTaskSummaryQueryBuilder.OrderBy.endDate;
                    case 2 :
                        return BAMTaskSummaryQueryBuilder.OrderBy.processInstanceId;
                    case 3 :
                        return BAMTaskSummaryQueryBuilder.OrderBy.startDate;
                    case 4 :
                        return BAMTaskSummaryQueryBuilder.OrderBy.taskId;
                    case 5 :
                        return BAMTaskSummaryQueryBuilder.OrderBy.taskName;
                }
            } else if (type.isArray()) {
                Class elemType = type.getComponentType();
                if (elemType.equals(CorrelationKey.class)) {
                    CorrelationKey[] corrKeyArr = new CorrelationKey[]{ correlationKeyFactory.newCorrelationKey("key:one") , correlationKeyFactory.newCorrelationKey("key:two") };
                    return corrKeyArr;
                } else if (elemType.equals(TaskEventType.class)) {
                    TaskEventType[] typeArr = new TaskEventType[]{ TaskEventType.ACTIVATED , TaskEventType.ADDED , TaskEventType.CLAIMED };
                    return typeArr;
                } else if (elemType.equals(Status.class)) {
                    Status[] statusArr = new Status[]{ Status.Completed , Status.Suspended };
                    return statusArr;
                } else if (elemType.equals(VariableType.class)) {
                    VariableType[] typeArr = new VariableType[]{ (((taskVariableType)++) % 2) == 0 ? VariableType.INPUT : VariableType.OUTPUT };
                    return typeArr;
                } 
            } 
            return null;
        }
    };

    @Test
    public void auditTaskQueryBuilderCoverageTest() {
        AuditTaskQueryBuilder queryBuilder = auditService.auditTaskQuery();
        Class builderClass = AuditTaskQueryBuilder.class;
        QueryBuilderCoverageTestUtil.queryBuilderCoverageTest(queryBuilder, builderClass, TaskAuditQueryBuilderCoverageTest.inputFiller);
    }

    @Test
    public void taskEventQueryBuilderCoverageTest() {
        TaskEventQueryBuilder queryBuilder = auditService.taskEventQuery();
        Class builderClass = TaskEventQueryBuilder.class;
        QueryBuilderCoverageTestUtil.queryBuilderCoverageTest(queryBuilder, builderClass, TaskAuditQueryBuilderCoverageTest.inputFiller);
    }

    @Test
    public void bamTaskSummaryQueryBuilderCoverageTest() {
        BAMTaskSummaryQueryBuilder queryBuilder = auditService.bamTaskSummaryQuery();
        Class builderClass = BAMTaskSummaryQueryBuilder.class;
        QueryBuilderCoverageTestUtil.queryBuilderCoverageTest(queryBuilder, builderClass, TaskAuditQueryBuilderCoverageTest.inputFiller);
    }

    @Test
    public void taskVariableQueryBuilderCoverageTest() {
        TaskVariableQueryBuilder queryBuilder = auditService.taskVariableQuery();
        Class builderClass = TaskVariableQueryBuilder.class;
        QueryBuilderCoverageTestUtil.queryBuilderCoverageTest(queryBuilder, builderClass, TaskAuditQueryBuilderCoverageTest.inputFiller);
    }
}

