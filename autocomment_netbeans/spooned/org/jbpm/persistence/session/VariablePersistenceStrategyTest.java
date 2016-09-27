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


package org.jbpm.persistence.session;

import org.jbpm.test.util.AbstractBaseTest;
import org.jbpm.process.instance.impl.Action;
import org.jbpm.workflow.core.node.ActionNode;
import org.junit.After;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import org.drools.core.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import java.util.Collection;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.node.EndNode;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.kie.api.runtime.Environment;
import java.util.HashMap;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.naming.InitialContext;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.drools.persistence.jpa.marshaller.JPAPlaceholderResolverStrategy;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.jbpm.persistence.session.objects.MyEntity;
import org.jbpm.persistence.session.objects.MyEntityMethods;
import org.jbpm.persistence.session.objects.MyEntityOnlyFields;
import org.jbpm.persistence.session.objects.MySubEntity;
import org.jbpm.persistence.session.objects.MySubEntityMethods;
import org.jbpm.persistence.session.objects.MyVariableExtendingSerializable;
import org.jbpm.persistence.session.objects.MyVariableSerializable;
import javax.naming.NamingException;
import org.jbpm.workflow.core.Node;
import javax.transaction.NotSupportedException;
import org.drools.core.process.core.datatype.impl.type.ObjectDataType;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.jbpm.persistence.util.PersistenceUtil;
import org.kie.api.runtime.process.ProcessContext;
import org.kie.api.runtime.process.ProcessInstance;
import javax.transaction.RollbackException;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.junit.runner.RunWith;
import org.jbpm.workflow.core.node.StartNode;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import javax.transaction.Status;
import javax.transaction.SystemException;
import org.junit.Test;
import org.jbpm.persistence.session.objects.TestWorkItemHandler;
import javax.transaction.UserTransaction;
import org.jbpm.process.core.context.variable.Variable;
import org.drools.core.process.core.Work;
import org.kie.api.runtime.process.WorkItem;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.kie.api.runtime.process.WorkflowProcessInstance;

@RunWith(value = Parameterized.class)
public class VariablePersistenceStrategyTest extends AbstractBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(VariablePersistenceStrategyTest.class);

    private HashMap<String, Object> context;

    private EntityManagerFactory emf;

    public VariablePersistenceStrategyTest(boolean locking) {
        this.useLocking = locking;
    }

    @Parameters
    public static Collection<Object[]> persistence() {
        Object[][] data = new Object[][]{ new Object[]{ false } , new Object[]{ true } };
        return Arrays.asList(data);
    }

    @Before
    public void setUp() throws Exception {
        context = setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME);
        emf = ((EntityManagerFactory) (context.get(EnvironmentName.ENTITY_MANAGER_FACTORY)));
    }

    @After
    public void tearDown() throws Exception {
        cleanUp(context);
    }

    @Test
    public void testExtendingInterfaceVariablePersistence() throws Exception {
        // Setup
        Environment env = createEnvironment();
        String processId = "extendingInterfaceVariablePersistence";
        String variableText = "my extending serializable variable text";
        KnowledgeBase kbase = getKnowledgeBaseForExtendingInterfaceVariablePersistence(processId, variableText);
        StatefulKnowledgeSession ksession = createSession(kbase, env);
        Map<String, Object> initialParams = new HashMap<String, Object>();
        initialParams.put("x", new MyVariableExtendingSerializable(variableText));
        // Start process and execute workItem
        long processInstanceId = ksession.startProcess(processId, initialParams).getId();
        ksession = reloadSession(ksession, kbase, env);
        long workItemId = TestWorkItemHandler.getInstance().getWorkItem().getId();
        ksession.getWorkItemManager().completeWorkItem(workItemId, null);
        // Test
        Assert.Assert.assertNull(ksession.getProcessInstance(processInstanceId));
    }

    private KnowledgeBase getKnowledgeBaseForExtendingInterfaceVariablePersistence(String processId, final String variableText) {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId(processId);
        List<Variable> variables = new ArrayList<Variable>();
        Variable variable = new Variable();
        variable.setName("x");
        ObjectDataType extendingSerializableDataType = new ObjectDataType();
        extendingSerializableDataType.setClassName(MyVariableExtendingSerializable.class.getName());
        variable.setType(extendingSerializableDataType);
        variables.add(variable);
        process.getVariableScope().setVariables(variables);
        StartNode startNode = new StartNode();
        startNode.setName("Start");
        startNode.setId(1);
        WorkItemNode workItemNode = new WorkItemNode();
        workItemNode.setName("workItemNode");
        workItemNode.setId(2);
        Work work = new org.drools.core.process.core.impl.WorkImpl();
        work.setName("MyWork");
        workItemNode.setWork(work);
        ActionNode actionNode = new ActionNode();
        actionNode.setName("Print");
        DroolsAction action = new org.jbpm.workflow.core.impl.DroolsConsequenceAction("java", null);
        action.setMetaData("Action", new Action() {
            public void execute(ProcessContext context) throws Exception {
                Assert.Assert.assertEquals(variableText, ((MyVariableExtendingSerializable) (context.getVariable("x"))).getText());
            }
        });
        actionNode.setAction(action);
        actionNode.setId(3);
        EndNode endNode = new EndNode();
        endNode.setName("EndNode");
        endNode.setId(4);
        connect(startNode, workItemNode);
        connect(workItemNode, actionNode);
        connect(actionNode, endNode);
        process.addNode(startNode);
        process.addNode(workItemNode);
        process.addNode(actionNode);
        process.addNode(endNode);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        ((KnowledgeBaseImpl) (kbase)).addProcess(process);
        return kbase;
    }

    @Test
    public void testPersistenceVariables() throws NamingException, HeuristicMixedException, HeuristicRollbackException, NotSupportedException, RollbackException, SystemException {
        EntityManager em = emf.createEntityManager();
        UserTransaction utx = ((UserTransaction) (new InitialContext().lookup("java:comp/UserTransaction")));
        if ((utx.getStatus()) == (Status.STATUS_NO_TRANSACTION)) {
            utx.begin();
            em.joinTransaction();
        } 
        int origNumMyEntities = em.createQuery("select i from MyEntity i").getResultList().size();
        int origNumMyEntityMethods = em.createQuery("select i from MyEntityMethods i").getResultList().size();
        int origNumMyEntityOnlyFields = em.createQuery("select i from MyEntityOnlyFields i").getResultList().size();
        if ((utx.getStatus()) == (Status.STATUS_ACTIVE)) {
            utx.commit();
        } 
        // Setup entities
        MyEntity myEntity = new MyEntity("This is a test Entity with annotation in fields");
        MyEntityMethods myEntityMethods = new MyEntityMethods("This is a test Entity with annotations in methods");
        MyEntityOnlyFields myEntityOnlyFields = new MyEntityOnlyFields("This is a test Entity with annotations in fields and without accesors methods");
        MyVariableSerializable myVariableSerializable = new MyVariableSerializable("This is a test SerializableObject");
        // persist entities
        utx = ((UserTransaction) (new InitialContext().lookup("java:comp/UserTransaction")));
        utx.begin();
        em.joinTransaction();
        em.persist(myEntity);
        em.persist(myEntityMethods);
        em.persist(myEntityOnlyFields);
        utx.commit();
        em.close();
        // More setup
        Environment env = createEnvironment();
        KnowledgeBase kbase = createKnowledgeBase("VariablePersistenceStrategyProcess.rf");
        StatefulKnowledgeSession ksession = createSession(kbase, env);
        VariablePersistenceStrategyTest.logger.debug("### Starting process ###");
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("x", "SomeString");
        parameters.put("y", myEntity);
        parameters.put("m", myEntityMethods);
        parameters.put("f", myEntityOnlyFields);
        parameters.put("z", myVariableSerializable);
        // Start process
        long processInstanceId = ksession.startProcess("com.sample.ruleflow", parameters).getId();
        TestWorkItemHandler handler = TestWorkItemHandler.getInstance();
        WorkItem workItem = handler.getWorkItem();
        assertNotNull(workItem);
        // Test results
        List<?> result = emf.createEntityManager().createQuery("select i from MyEntity i").getResultList();
        assertEquals((origNumMyEntities + 1), result.size());
        result = emf.createEntityManager().createQuery("select i from MyEntityMethods i").getResultList();
        assertEquals((origNumMyEntityMethods + 1), result.size());
        result = emf.createEntityManager().createQuery("select i from MyEntityOnlyFields i").getResultList();
        assertEquals((origNumMyEntityOnlyFields + 1), result.size());
        VariablePersistenceStrategyTest.logger.debug("### Retrieving process instance ###");
        ksession = reloadSession(ksession, kbase, env);
        WorkflowProcessInstance processInstance = ((WorkflowProcessInstance) (ksession.getProcessInstance(processInstanceId)));
        assertNotNull(processInstance);
        assertEquals("SomeString", processInstance.getVariable("x"));
        assertEquals("This is a test Entity with annotation in fields", ((MyEntity) (processInstance.getVariable("y"))).getTest());
        assertEquals("This is a test Entity with annotations in methods", ((MyEntityMethods) (processInstance.getVariable("m"))).getTest());
        assertEquals("This is a test Entity with annotations in fields and without accesors methods", ((MyEntityOnlyFields) (processInstance.getVariable("f"))).test);
        assertEquals("This is a test SerializableObject", ((MyVariableSerializable) (processInstance.getVariable("z"))).getText());
        assertNull(processInstance.getVariable("a"));
        assertNull(processInstance.getVariable("b"));
        assertNull(processInstance.getVariable("c"));
        VariablePersistenceStrategyTest.logger.debug("### Completing first work item ###");
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        workItem = handler.getWorkItem();
        assertNotNull(workItem);
        VariablePersistenceStrategyTest.logger.debug("### Retrieving process instance ###");
        ksession = reloadSession(ksession, kbase, env);
        processInstance = ((WorkflowProcessInstance) (ksession.getProcessInstance(processInstanceId)));
        assertNotNull(processInstance);
        assertEquals("SomeString", processInstance.getVariable("x"));
        assertEquals("This is a test Entity with annotation in fields", ((MyEntity) (processInstance.getVariable("y"))).getTest());
        assertEquals("This is a test Entity with annotations in methods", ((MyEntityMethods) (processInstance.getVariable("m"))).getTest());
        assertEquals("This is a test Entity with annotations in fields and without accesors methods", ((MyEntityOnlyFields) (processInstance.getVariable("f"))).test);
        assertEquals("This is a test SerializableObject", ((MyVariableSerializable) (processInstance.getVariable("z"))).getText());
        assertEquals("Some new String", processInstance.getVariable("a"));
        assertEquals("This is a new test Entity", ((MyEntity) (processInstance.getVariable("b"))).getTest());
        assertEquals("This is a new test SerializableObject", ((MyVariableSerializable) (processInstance.getVariable("c"))).getText());
        VariablePersistenceStrategyTest.logger.debug("### Completing second work item ###");
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        workItem = handler.getWorkItem();
        assertNotNull(workItem);
        VariablePersistenceStrategyTest.logger.debug("### Retrieving process instance ###");
        ksession = reloadSession(ksession, kbase, env);
        processInstance = ((WorkflowProcessInstance) (ksession.getProcessInstance(processInstanceId)));
        assertNotNull(processInstance);
        assertEquals("SomeString", processInstance.getVariable("x"));
        assertEquals("This is a test Entity with annotation in fields", ((MyEntity) (processInstance.getVariable("y"))).getTest());
        assertEquals("This is a test Entity with annotations in methods", ((MyEntityMethods) (processInstance.getVariable("m"))).getTest());
        assertEquals("This is a test Entity with annotations in fields and without accesors methods", ((MyEntityOnlyFields) (processInstance.getVariable("f"))).test);
        assertEquals("This is a test SerializableObject", ((MyVariableSerializable) (processInstance.getVariable("z"))).getText());
        assertEquals("Some changed String", processInstance.getVariable("a"));
        assertEquals("This is a changed test Entity", ((MyEntity) (processInstance.getVariable("b"))).getTest());
        assertEquals("This is a changed test SerializableObject", ((MyVariableSerializable) (processInstance.getVariable("c"))).getText());
        VariablePersistenceStrategyTest.logger.debug("### Completing third work item ###");
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        workItem = handler.getWorkItem();
        assertNull(workItem);
        ksession = reloadSession(ksession, kbase, env);
        processInstance = ((WorkflowProcessInstance) (ksession.getProcessInstance(processInstanceId)));
        assertNull(processInstance);
    }

    @Test
    public void testPersistenceVariablesWithTypeChange() throws NamingException, HeuristicMixedException, HeuristicRollbackException, NotSupportedException, RollbackException, SystemException {
        MyEntity myEntity = new MyEntity("This is a test Entity with annotation in fields");
        MyEntityMethods myEntityMethods = new MyEntityMethods("This is a test Entity with annotations in methods");
        MyEntityOnlyFields myEntityOnlyFields = new MyEntityOnlyFields("This is a test Entity with annotations in fields and without accesors methods");
        MyVariableSerializable myVariableSerializable = new MyVariableSerializable("This is a test SerializableObject");
        EntityManager em = emf.createEntityManager();
        UserTransaction utx = ((UserTransaction) (new InitialContext().lookup("java:comp/UserTransaction")));
        int s = utx.getStatus();
        if ((utx.getStatus()) == (Status.STATUS_NO_TRANSACTION)) {
            utx.begin();
        } 
        em.joinTransaction();
        em.persist(myEntity);
        em.persist(myEntityMethods);
        em.persist(myEntityOnlyFields);
        if ((utx.getStatus()) == (Status.STATUS_ACTIVE)) {
            utx.commit();
        } 
        em.close();
        Environment env = createEnvironment();
        KnowledgeBase kbase = createKnowledgeBase("VariablePersistenceStrategyProcessTypeChange.rf");
        StatefulKnowledgeSession ksession = createSession(kbase, env);
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("x", "SomeString");
        parameters.put("y", myEntity);
        parameters.put("m", myEntityMethods);
        parameters.put("f", myEntityOnlyFields);
        parameters.put("z", myVariableSerializable);
        long processInstanceId = ksession.startProcess("com.sample.ruleflow", parameters).getId();
        TestWorkItemHandler handler = TestWorkItemHandler.getInstance();
        WorkItem workItem = handler.getWorkItem();
        assertNotNull(workItem);
        ksession = reloadSession(ksession, kbase, env);
        ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
        assertNotNull(processInstance);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        workItem = handler.getWorkItem();
        assertNotNull(workItem);
        ksession = reloadSession(ksession, kbase, env);
        processInstance = ksession.getProcessInstance(processInstanceId);
        assertNotNull(processInstance);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        workItem = handler.getWorkItem();
        assertNull(workItem);
        ksession = reloadSession(ksession, kbase, env);
        processInstance = ksession.getProcessInstance(processInstanceId);
        assertNull(processInstance);
    }

    @Test
    public void testPersistenceVariablesSubProcess() throws NamingException, HeuristicMixedException, HeuristicRollbackException, NotSupportedException, RollbackException, SystemException {
        MyEntity myEntity = new MyEntity("This is a test Entity with annotation in fields");
        MyEntityMethods myEntityMethods = new MyEntityMethods("This is a test Entity with annotations in methods");
        MyEntityOnlyFields myEntityOnlyFields = new MyEntityOnlyFields("This is a test Entity with annotations in fields and without accesors methods");
        MyVariableSerializable myVariableSerializable = new MyVariableSerializable("This is a test SerializableObject");
        EntityManager em = emf.createEntityManager();
        UserTransaction utx = ((UserTransaction) (new InitialContext().lookup("java:comp/UserTransaction")));
        utx.begin();
        em.joinTransaction();
        em.persist(myEntity);
        em.persist(myEntityMethods);
        em.persist(myEntityOnlyFields);
        utx.commit();
        em.close();
        Environment env = createEnvironment();
        KnowledgeBase kbase = createKnowledgeBase("VariablePersistenceStrategySubProcess.rf");
        StatefulKnowledgeSession ksession = createSession(kbase, env);
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("x", "SomeString");
        parameters.put("y", myEntity);
        parameters.put("m", myEntityMethods);
        parameters.put("f", myEntityOnlyFields);
        parameters.put("z", myVariableSerializable);
        long processInstanceId = ksession.startProcess("com.sample.ruleflow", parameters).getId();
        TestWorkItemHandler handler = TestWorkItemHandler.getInstance();
        WorkItem workItem = handler.getWorkItem();
        assertNotNull(workItem);
        ksession = reloadSession(ksession, kbase, env);
        ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
        assertNotNull(processInstance);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        workItem = handler.getWorkItem();
        assertNotNull(workItem);
        ksession = reloadSession(ksession, kbase, env);
        processInstance = ksession.getProcessInstance(processInstanceId);
        assertNotNull(processInstance);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        workItem = handler.getWorkItem();
        assertNotNull(workItem);
        ksession = reloadSession(ksession, kbase, env);
        processInstance = ksession.getProcessInstance(processInstanceId);
        assertNotNull(processInstance);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        workItem = handler.getWorkItem();
        assertNull(workItem);
        ksession = reloadSession(ksession, kbase, env);
        processInstance = ksession.getProcessInstance(processInstanceId);
        assertNull(processInstance);
    }

    @Test
    public void testWorkItemWithVariablePersistence() throws Exception {
        MyEntity myEntity = new MyEntity("This is a test Entity");
        MyVariableSerializable myVariableSerializable = new MyVariableSerializable("This is a test SerializableObject");
        EntityManager em = emf.createEntityManager();
        UserTransaction utx = ((UserTransaction) (new InitialContext().lookup("java:comp/UserTransaction")));
        utx.begin();
        em.joinTransaction();
        em.persist(myEntity);
        utx.commit();
        em.close();
        Environment env = createEnvironment();
        KnowledgeBase kbase = createKnowledgeBase("VPSProcessWithWorkItems.rf");
        StatefulKnowledgeSession ksession = createSession(kbase, env);
        VariablePersistenceStrategyTest.logger.debug("### Starting process ###");
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("x", "SomeString");
        parameters.put("y", myEntity);
        parameters.put("z", myVariableSerializable);
        long processInstanceId = ksession.startProcess("com.sample.ruleflow", parameters).getId();
        TestWorkItemHandler handler = TestWorkItemHandler.getInstance();
        WorkItem workItem = handler.getWorkItem();
        assertNotNull(workItem);
        VariablePersistenceStrategyTest.logger.debug("### Retrieving process instance ###");
        ksession = reloadSession(ksession, kbase, env);
        WorkflowProcessInstance processInstance = ((WorkflowProcessInstance) (ksession.getProcessInstance(processInstanceId)));
        assertNotNull(processInstance);
        assertEquals("SomeString", processInstance.getVariable("x"));
        assertEquals("This is a test Entity", ((MyEntity) (processInstance.getVariable("y"))).getTest());
        assertEquals("This is a test SerializableObject", ((MyVariableSerializable) (processInstance.getVariable("z"))).getText());
        assertNull(processInstance.getVariable("a"));
        assertNull(processInstance.getVariable("b"));
        assertNull(processInstance.getVariable("c"));
        VariablePersistenceStrategyTest.logger.debug("### Completing first work item ###");
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("zeta", processInstance.getVariable("z"));
        results.put("equis", ((processInstance.getVariable("x")) + "->modifiedResult"));
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), results);
        workItem = handler.getWorkItem();
        assertNotNull(workItem);
        VariablePersistenceStrategyTest.logger.debug("### Retrieving process instance ###");
        ksession = reloadSession(ksession, kbase, env);
        processInstance = ((WorkflowProcessInstance) (ksession.getProcessInstance(processInstanceId)));
        assertNotNull(processInstance);
        VariablePersistenceStrategyTest.logger.debug("######## Getting the already Persisted Variables #########");
        assertEquals("SomeString->modifiedResult", processInstance.getVariable("x"));
        assertEquals("This is a test Entity", ((MyEntity) (processInstance.getVariable("y"))).getTest());
        assertEquals("This is a test SerializableObject", ((MyVariableSerializable) (processInstance.getVariable("z"))).getText());
        assertEquals("Some new String", processInstance.getVariable("a"));
        assertEquals("This is a new test Entity", ((MyEntity) (processInstance.getVariable("b"))).getTest());
        assertEquals("This is a new test SerializableObject", ((MyVariableSerializable) (processInstance.getVariable("c"))).getText());
        VariablePersistenceStrategyTest.logger.debug("### Completing second work item ###");
        results = new HashMap<String, Object>();
        results.put("zeta", processInstance.getVariable("z"));
        results.put("equis", processInstance.getVariable("x"));
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), results);
        workItem = handler.getWorkItem();
        assertNotNull(workItem);
        VariablePersistenceStrategyTest.logger.debug("### Retrieving process instance ###");
        ksession = reloadSession(ksession, kbase, env);
        processInstance = ((WorkflowProcessInstance) (ksession.getProcessInstance(processInstanceId)));
        assertNotNull(processInstance);
        assertEquals("SomeString->modifiedResult", processInstance.getVariable("x"));
        assertEquals("This is a test Entity", ((MyEntity) (processInstance.getVariable("y"))).getTest());
        assertEquals("This is a test SerializableObject", ((MyVariableSerializable) (processInstance.getVariable("z"))).getText());
        assertEquals("Some changed String", processInstance.getVariable("a"));
        assertEquals("This is a changed test Entity", ((MyEntity) (processInstance.getVariable("b"))).getTest());
        assertEquals("This is a changed test SerializableObject", ((MyVariableSerializable) (processInstance.getVariable("c"))).getText());
        VariablePersistenceStrategyTest.logger.debug("### Completing third work item ###");
        results = new HashMap<String, Object>();
        results.put("zeta", processInstance.getVariable("z"));
        results.put("equis", processInstance.getVariable("x"));
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), results);
        workItem = handler.getWorkItem();
        assertNull(workItem);
        ksession = reloadSession(ksession, kbase, env);
        processInstance = ((WorkflowProcessInstance) (ksession.getProcessInstance(processInstanceId)));
        assertNull(processInstance);
    }

    @Test
    public void testEntityWithSuperClassAnnotationField() throws Exception {
        MySubEntity subEntity = new MySubEntity();
        subEntity.setId(3L);
        assertEquals(3L, JPAPlaceholderResolverStrategy.getClassIdValue(subEntity));
    }

    @Test
    public void testEntityWithSuperClassAnnotationMethod() throws Exception {
        MySubEntityMethods subEntity = new MySubEntityMethods();
        subEntity.setId(3L);
        assertEquals(3L, JPAPlaceholderResolverStrategy.getClassIdValue(subEntity));
    }

    @Test
    public void testAbortWorkItemWithVariablePersistence() throws Exception {
        MyEntity myEntity = new MyEntity("This is a test Entity");
        MyVariableSerializable myVariableSerializable = new MyVariableSerializable("This is a test SerializableObject");
        EntityManager em = emf.createEntityManager();
        UserTransaction utx = ((UserTransaction) (new InitialContext().lookup("java:comp/UserTransaction")));
        utx.begin();
        em.joinTransaction();
        em.persist(myEntity);
        utx.commit();
        em.close();
        Environment env = createEnvironment();
        KnowledgeBase kbase = createKnowledgeBase("VPSProcessWithWorkItems.rf");
        StatefulKnowledgeSession ksession = createSession(kbase, env);
        VariablePersistenceStrategyTest.logger.debug("### Starting process ###");
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("x", "SomeString");
        parameters.put("y", myEntity);
        parameters.put("z", myVariableSerializable);
        long processInstanceId = ksession.startProcess("com.sample.ruleflow", parameters).getId();
        TestWorkItemHandler handler = TestWorkItemHandler.getInstance();
        WorkItem workItem = handler.getWorkItem();
        assertNotNull(workItem);
        VariablePersistenceStrategyTest.logger.debug("### Retrieving process instance ###");
        ksession = reloadSession(ksession, kbase, env);
        WorkflowProcessInstance processInstance = ((WorkflowProcessInstance) (ksession.getProcessInstance(processInstanceId)));
        assertNotNull(processInstance);
        assertEquals("SomeString", processInstance.getVariable("x"));
        assertEquals("This is a test Entity", ((MyEntity) (processInstance.getVariable("y"))).getTest());
        assertEquals("This is a test SerializableObject", ((MyVariableSerializable) (processInstance.getVariable("z"))).getText());
        assertNull(processInstance.getVariable("a"));
        assertNull(processInstance.getVariable("b"));
        assertNull(processInstance.getVariable("c"));
        VariablePersistenceStrategyTest.logger.debug("### Completing first work item ###");
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("zeta", processInstance.getVariable("z"));
        results.put("equis", ((processInstance.getVariable("x")) + "->modifiedResult"));
        // we simulate a failure here, aborting the work item
        ksession.getWorkItemManager().abortWorkItem(workItem.getId());
        workItem = handler.getWorkItem();
        assertNotNull(workItem);
        VariablePersistenceStrategyTest.logger.debug("### Retrieving process instance ###");
        ksession = reloadSession(ksession, kbase, env);
        processInstance = ((WorkflowProcessInstance) (ksession.getProcessInstance(processInstanceId)));
        assertNotNull(processInstance);
        VariablePersistenceStrategyTest.logger.debug("######## Getting the already Persisted Variables #########");
        // we expect the variables to be unmodifed
        assertEquals("SomeString", processInstance.getVariable("x"));
        assertEquals("This is a test Entity", ((MyEntity) (processInstance.getVariable("y"))).getTest());
        assertEquals("This is a test SerializableObject", ((MyVariableSerializable) (processInstance.getVariable("z"))).getText());
        assertEquals("Some new String", processInstance.getVariable("a"));
        assertEquals("This is a new test Entity", ((MyEntity) (processInstance.getVariable("b"))).getTest());
        assertEquals("This is a new test SerializableObject", ((MyVariableSerializable) (processInstance.getVariable("c"))).getText());
    }

    private StatefulKnowledgeSession createSession(KnowledgeBase kbase, Environment env) {
        return JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
    }

    private StatefulKnowledgeSession reloadSession(StatefulKnowledgeSession ksession, KnowledgeBase kbase, Environment env) {
        long sessionId = ksession.getIdentifier();
        ksession.dispose();
        return JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId, kbase, null, env);
    }

    private KnowledgeBase createKnowledgeBase(String flowFile) {
        KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        conf.setProperty("drools.dialect.java.compiler", "JANINO");
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(conf);
        kbuilder.add(new org.drools.core.io.impl.ClassPathResource(flowFile), ResourceType.DRF);
        if (kbuilder.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            for (KnowledgeBuilderError error : kbuilder.getErrors()) {
                errorMessage.append(error.getMessage());
                errorMessage.append(System.getProperty("line.separator"));
            }
            fail(errorMessage.toString());
        } 
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        return kbase;
    }

    private Environment createEnvironment() {
        Environment env = PersistenceUtil.PersistenceUtil.createEnvironment(context);
        env.set(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES, new ObjectMarshallingStrategy[]{ new JPAPlaceholderResolverStrategy(env) , new org.drools.core.marshalling.impl.SerializablePlaceholderResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT) });
        return env;
    }

    private void connect(Node sourceNode, Node targetNode) {
        new org.jbpm.workflow.core.impl.ConnectionImpl(sourceNode, Node.CONNECTION_DEFAULT_TYPE, targetNode, Node.CONNECTION_DEFAULT_TYPE);
    }
}

