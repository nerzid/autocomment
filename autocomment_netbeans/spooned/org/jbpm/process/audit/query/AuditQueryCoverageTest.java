/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.jbpm.process.audit.query;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.kie.internal.process.CorrelationKey;
import javax.persistence.EntityManagerFactory;
import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.persistence.correlation.JPACorrelationKeyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jbpm.query.QueryBuilderCoverageTestUtil.ModuleSpecificInputFiller;
import org.jbpm.process.audit.NodeInstanceLog;
import org.kie.internal.runtime.manager.audit.query.NodeInstanceLogQueryBuilder;
import org.kie.internal.runtime.manager.audit.query.AuditLogQueryBuilder.OrderBy;
import org.jbpm.persistence.util.PersistenceUtil;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.kie.internal.runtime.manager.audit.query.ProcessInstanceLogQueryBuilder;
import org.jbpm.query.QueryBuilderCoverageTestUtil;
import org.junit.Test;
import org.jbpm.process.audit.VariableInstanceLog;
import org.kie.internal.runtime.manager.audit.query.VariableInstanceLogQueryBuilder;

public class AuditQueryCoverageTest extends JPAAuditLogService {
    private static final Logger logger = LoggerFactory.getLogger(AuditQueryCoverageTest.class);

    private static EntityManagerFactory emf;

    private ProcessInstanceLog[] pilTestData;

    private VariableInstanceLog[] vilTestData;

    private NodeInstanceLog[] nilTestData;

    @BeforeClass
    public static void configure() {
        AuditQueryCoverageTest.emf = QueryBuilderCoverageTestUtil.beforeClass(PersistenceUtil.JBPM_PERSISTENCE_UNIT_NAME);
    }

    @AfterClass
    public static void reset() {
        QueryBuilderCoverageTestUtil.afterClass();
    }

    @Before
    public void setUp() throws Exception {
        if ((pilTestData) == null) {
            // this is not really necessary..
            pilTestData = AuditQueryDataUtil.createTestProcessInstanceLogData(AuditQueryCoverageTest.emf);
            vilTestData = AuditQueryDataUtil.createTestVariableInstanceLogData(AuditQueryCoverageTest.emf);
            nilTestData = AuditQueryDataUtil.createTestNodeInstanceLogData(AuditQueryCoverageTest.emf);
        } 
        AuditQueryCoverageTest.this.persistenceStrategy = new org.jbpm.process.audit.strategy.StandaloneJtaStrategy(AuditQueryCoverageTest.emf);
    }

    private static ModuleSpecificInputFiller inputFiller = new ModuleSpecificInputFiller() {
        private final JPACorrelationKeyFactory correlationKeyFactory = new JPACorrelationKeyFactory();

        private int orderByType = 0;

        @Override
        public Object fillInput(Class type) {
            if (type.equals(CorrelationKey.class)) {
                return correlationKeyFactory.newCorrelationKey("business-key");
            } else if (type.equals(OrderBy.class)) {
                return (((orderByType)++) % 2) == 0 ? OrderBy.processId : OrderBy.processInstanceId;
            } else if (type.isArray()) {
                CorrelationKey[] corrKeyArr = new CorrelationKey[]{ correlationKeyFactory.newCorrelationKey("key:one") , correlationKeyFactory.newCorrelationKey("key:two") };
                return corrKeyArr;
            } 
            return null;
        }
    };

    @Test
    public void processInstanceLogQueryCoverageTest() {
        ProcessInstanceLogQueryBuilder queryBuilder = AuditQueryCoverageTest.this.processInstanceLogQuery();
        Class builderClass = ProcessInstanceLogQueryBuilder.class;
        QueryBuilderCoverageTestUtil.queryBuilderCoverageTest(queryBuilder, builderClass, AuditQueryCoverageTest.inputFiller);
    }

    @Test
    public void variableInstanceLogQueryBuilderCoverageTest() {
        VariableInstanceLogQueryBuilder queryBuilder = AuditQueryCoverageTest.this.variableInstanceLogQuery();
        Class builderClass = VariableInstanceLogQueryBuilder.class;
        QueryBuilderCoverageTestUtil.queryBuilderCoverageTest(queryBuilder, builderClass, AuditQueryCoverageTest.inputFiller);
    }

    @Test
    public void nodeInstanceLogQueryBuilderCoverageTest() {
        NodeInstanceLogQueryBuilder queryBuilder = AuditQueryCoverageTest.this.nodeInstanceLogQuery();
        Class builderClass = NodeInstanceLogQueryBuilder.class;
        QueryBuilderCoverageTestUtil.queryBuilderCoverageTest(queryBuilder, builderClass, AuditQueryCoverageTest.inputFiller);
    }
}

