

package org.jbpm.services.task.query;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import javax.persistence.EntityManagerFactory;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.HumanTaskServicesBaseTest;
import org.kie.internal.task.api.InternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jbpm.query.QueryBuilderCoverageTestUtil.ModuleSpecificInputFiller;
import TaskSummaryQueryBuilder.OrderBy;
import javax.persistence.Persistence;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import org.jbpm.query.QueryBuilderCoverageTestUtil;
import org.kie.api.task.model.Status;
import org.kie.internal.task.api.model.SubTasksStrategy;
import org.kie.internal.task.api.model.TaskEvent.TaskEventType;
import org.kie.internal.task.query.TaskSummaryQueryBuilder;
import org.junit.Test;

public class TaskSummaryQueryBuilderCoverageTest extends HumanTaskServicesBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(TaskSummaryQueryBuilderCoverageTest.class);

    private static PoolingDataSource pds;

    private static EntityManagerFactory emf;

    private InternalTaskService taskService;

    @BeforeClass
    public static void configure() {
        TaskSummaryQueryBuilderCoverageTest.pds = setupPoolingDataSource();
        TaskSummaryQueryBuilderCoverageTest.emf = Persistence.createEntityManagerFactory("org.jbpm.services.task");
        QueryBuilderCoverageTestUtil.hackTheDatabaseMetadataLoggerBecauseTheresALogbackXmlInTheClasspath();
    }

    @Before
    public void setup() {
        TaskSummaryQueryBuilderCoverageTest.this.taskService = ((InternalTaskService) (HumanTaskServiceFactory.newTaskServiceConfigurator().entityManagerFactory(TaskSummaryQueryBuilderCoverageTest.emf).getTaskService()));
    }

    @AfterClass
    public static void reset() {
        if ((TaskSummaryQueryBuilderCoverageTest.emf) != null) {
            TaskSummaryQueryBuilderCoverageTest.emf.close();
            TaskSummaryQueryBuilderCoverageTest.emf = null;
        } 
        if ((TaskSummaryQueryBuilderCoverageTest.pds) != null) {
            TaskSummaryQueryBuilderCoverageTest.pds.close();
            TaskSummaryQueryBuilderCoverageTest.pds = null;
        } 
    }

    private static ModuleSpecificInputFiller inputFiller = new ModuleSpecificInputFiller() {
        private int taskQueryOrderBy = 0;

        @Override
        public Object fillInput(Class type) {
            if (type.equals(OrderBy.class)) {
                int typeCase = ((taskQueryOrderBy)++) % 6;
                switch (typeCase) {
                    case 0 :
                        return OrderBy.createdBy;
                    case 1 :
                        return OrderBy.createdOn;
                    case 2 :
                        return OrderBy.processInstanceId;
                    case 3 :
                        return OrderBy.taskId;
                    case 4 :
                        return OrderBy.taskName;
                    case 5 :
                        return OrderBy.taskStatus;
                }
            } else if (type.isArray()) {
                Class elemType = type.getComponentType();
                if (elemType.equals(TaskEventType.class)) {
                    TaskEventType[] typeArr = new TaskEventType[]{ TaskEventType.ACTIVATED , TaskEventType.ADDED , TaskEventType.CLAIMED };
                    return typeArr;
                } else if (elemType.equals(Status.class)) {
                    Status[] statusArr = new Status[]{ Status.Completed , Status.Suspended };
                    return statusArr;
                } else if (elemType.equals(SubTasksStrategy.class)) {
                    SubTasksStrategy[] strategyArr = new SubTasksStrategy[]{ SubTasksStrategy.EndParentOnAllSubTasksEnd , SubTasksStrategy.NoAction };
                    return strategyArr;
                } 
            } 
            return null;
        }
    };

    @Test
    public void taskQueryBuilderCoverageTest() {
        TaskSummaryQueryBuilder queryBuilder = taskService.taskSummaryQuery("userId");
        Class builderClass = TaskSummaryQueryBuilder.class;
        QueryBuilderCoverageTestUtil.queryBuilderCoverageTest(queryBuilder, builderClass, TaskSummaryQueryBuilderCoverageTest.inputFiller, "variableName", "variableValue");
    }
}

