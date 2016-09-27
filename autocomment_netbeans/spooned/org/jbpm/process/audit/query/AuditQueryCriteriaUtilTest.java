

package org.jbpm.process.audit.query;

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.jbpm.process.audit.AuditQueryCriteriaUtil;
import org.junit.BeforeClass;
import javax.persistence.EntityManagerFactory;
import org.kie.api.runtime.EnvironmentName;
import java.util.HashMap;
import org.jbpm.process.audit.JPAAuditLogService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.persistence.util.PersistenceUtil;
import org.kie.internal.query.QueryParameterIdentifiers;
import org.jbpm.query.jpa.data.QueryWhere;
import org.junit.Test;

public class AuditQueryCriteriaUtilTest {
    private static HashMap<String, Object> context;

    private static EntityManagerFactory emf;

    private static AuditQueryCriteriaUtil util;

    private static JPAAuditLogService auditLogService;

    private static final Logger logger = LoggerFactory.getLogger(AuditQueryCriteriaUtilTest.class);

    @BeforeClass
    public static void configure() {
        AbstractBaseTest.hackTheDatabaseMetadataLoggerBecauseTheresALogbackXmlInTheClasspath();
        AuditQueryCriteriaUtilTest.context = PersistenceUtil.setupWithPoolingDataSource(PersistenceUtil.JBPM_PERSISTENCE_UNIT_NAME);
        AuditQueryCriteriaUtilTest.emf = ((EntityManagerFactory) (AuditQueryCriteriaUtilTest.context.get(EnvironmentName.ENTITY_MANAGER_FACTORY)));
        AuditQueryCriteriaUtilTest.auditLogService = new JPAAuditLogService(AuditQueryCriteriaUtilTest.emf);
        AuditQueryCriteriaUtilTest.util = new AuditQueryCriteriaUtil(AuditQueryCriteriaUtilTest.auditLogService);
    }

    @AfterClass
    public static void reset() {
        PersistenceUtil.cleanUp(AuditQueryCriteriaUtilTest.context);
    }

    @Test
    public void auditQueryCriteriaWhereTest() {
        QueryWhere where = new QueryWhere();
        // OR
        where.setToUnion();
        where.addParameter(QueryParameterIdentifiers.NODE_ID_LIST, "node.id");
        where.addParameter(QueryParameterIdentifiers.NODE_INSTANCE_ID_LIST, "node-inst");
        where.addParameter(QueryParameterIdentifiers.TYPE_LIST, "type");
        // OR (
        where.newGroup();
        where.setToLike();
        where.addParameter(QueryParameterIdentifiers.NODE_NAME_LIST, "n*ends.X");
        where.setToNormal();
        where.setToIntersection();
        where.addParameter(QueryParameterIdentifiers.TYPE_LIST, "oneOf3", "twoOf3", "thrOf3");
        where.endGroup();
        where.setToIntersection();
        where.addRangeParameter(QueryParameterIdentifiers.PROCESS_INSTANCE_ID_LIST, 0L, true);
        where.addRangeParameter(QueryParameterIdentifiers.PROCESS_INSTANCE_ID_LIST, 10L, false);
        where.addParameter(QueryParameterIdentifiers.PROCESS_ID_LIST, "org.process.id");
        List<NodeInstanceLog> result = AuditQueryCriteriaUtilTest.util.doCriteriaQuery(where, NodeInstanceLog.class);
        Assert.assertNotNull("Null result from 1rst query.", result);
    }

    @Test
    public void auditQueryCriteriaMetaTest() {
        QueryWhere where = new QueryWhere();
        where.setAscending(QueryParameterIdentifiers.NODE_INSTANCE_ID_LIST);
        where.setCount(10);
        where.setOffset(2);
        List<NodeInstanceLog> result = AuditQueryCriteriaUtilTest.util.doCriteriaQuery(where, NodeInstanceLog.class);
        Assert.assertNotNull("Null result from 1rst query.", result);
    }
}

