

package org.jbpm.executor.impl.jpa;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import javax.persistence.EntityManagerFactory;
import org.kie.internal.runtime.manager.audit.query.ErrorInfoQueryBuilder;
import org.jbpm.test.util.ExecutorTestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jbpm.query.QueryBuilderCoverageTestUtil.ModuleSpecificInputFiller;
import ErrorInfoQueryBuilder.OrderBy;
import javax.persistence.Persistence;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import org.jbpm.query.QueryBuilderCoverageTestUtil;
import org.kie.internal.runtime.manager.audit.query.RequestInfoQueryBuilder;
import org.kie.api.executor.STATUS;
import org.junit.Test;

public class ExecutorQueryBuilderCoverageTest {
    private static final Logger logger = LoggerFactory.getLogger(ExecutorQueryBuilderCoverageTest.class);

    private static PoolingDataSource pds;

    private static EntityManagerFactory emf;

    private ExecutorJPAAuditService auditService;

    @BeforeClass
    public static void configure() {
        ExecutorQueryBuilderCoverageTest.pds = ExecutorTestUtil.setupPoolingDataSource();
        ExecutorQueryBuilderCoverageTest.emf = Persistence.createEntityManagerFactory("org.jbpm.executor");
        QueryBuilderCoverageTestUtil.hackTheDatabaseMetadataLoggerBecauseTheresALogbackXmlInTheClasspath();
    }

    @Before
    public void setup() {
        auditService = new ExecutorJPAAuditService(ExecutorQueryBuilderCoverageTest.emf);
    }

    @AfterClass
    public static void reset() {
        if ((ExecutorQueryBuilderCoverageTest.emf) != null) {
            ExecutorQueryBuilderCoverageTest.emf.close();
            ExecutorQueryBuilderCoverageTest.emf = null;
        } 
        if ((ExecutorQueryBuilderCoverageTest.pds) != null) {
            ExecutorQueryBuilderCoverageTest.pds.close();
            ExecutorQueryBuilderCoverageTest.pds = null;
        } 
    }

    private static ModuleSpecificInputFiller inputFiller = new ModuleSpecificInputFiller() {
        private int errorInfoOrderByType = 0;

        private int requestInfoOrderByType = 0;

        @Override
        public Object fillInput(Class type) {
            if (OrderBy.class.equals(type)) {
                return (((errorInfoOrderByType)++) % 2) == 0 ? OrderBy.id : OrderBy.time;
            } else if (RequestInfoQueryBuilder.OrderBy.class.equals(type)) {
                switch (((requestInfoOrderByType)++) % 6) {
                    case 0 :
                        return RequestInfoQueryBuilder.OrderBy.deploymentId;
                    case 1 :
                        return RequestInfoQueryBuilder.OrderBy.executions;
                    case 2 :
                        return RequestInfoQueryBuilder.OrderBy.id;
                    case 3 :
                        return RequestInfoQueryBuilder.OrderBy.retries;
                    case 4 :
                        return RequestInfoQueryBuilder.OrderBy.status;
                    case 5 :
                        return RequestInfoQueryBuilder.OrderBy.time;
                }
            } else if (type.isArray()) {
                Class elemType = type.getComponentType();
                if (STATUS.class.equals(elemType)) {
                    STATUS[] statusArr = new STATUS[]{ STATUS.DONE , STATUS.CANCELLED , STATUS.ERROR };
                    return statusArr;
                } 
            } 
            return null;
        }
    };

    @Test
    public void errorInfoQueryBuilderCoverageTest() {
        ErrorInfoQueryBuilder queryBuilder = auditService.errorInfoQueryBuilder();
        Class builderClass = ErrorInfoQueryBuilder.class;
        QueryBuilderCoverageTestUtil.queryBuilderCoverageTest(queryBuilder, builderClass, ExecutorQueryBuilderCoverageTest.inputFiller);
    }

    @Test
    public void requestInfoQueryBuilderCoverageTest() {
        RequestInfoQueryBuilder queryBuilder = auditService.requestInfoQueryBuilder();
        Class builderClass = RequestInfoQueryBuilder.class;
        QueryBuilderCoverageTestUtil.queryBuilderCoverageTest(queryBuilder, builderClass, ExecutorQueryBuilderCoverageTest.inputFiller);
    }
}

