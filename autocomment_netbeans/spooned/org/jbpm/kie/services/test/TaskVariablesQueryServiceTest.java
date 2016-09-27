/**
 * Copyright 2014 JBoss by Red Hat.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.jbpm.kie.services.test;

import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.junit.After;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import org.jbpm.services.api.model.DeployedUnit;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.jbpm.services.api.model.DeploymentUnit;
import java.lang.reflect.Field;
import java.io.File;
import java.util.HashMap;
import org.junit.Ignore;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.kie.api.KieServices;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.kie.scanner.MavenRepository;
import org.jbpm.services.api.model.ProcessDefinition;
import org.jbpm.services.api.query.model.QueryParam;
import java.util.Random;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.manager.RuntimeManager;
import org.jbpm.kie.services.impl.query.SqlQueryDefinition;
import org.jbpm.services.api.query.model.QueryDefinition.Target;
import org.kie.api.task.model.TaskSummary;
import org.junit.Test;
import org.jbpm.kie.services.impl.query.mapper.UserTaskInstanceWithCustomVarsQueryMapper;
import org.jbpm.services.api.model.UserTaskInstanceWithVarsDesc;

/**
 * This test should not be seen as part of the regular test suite... at least not for now
 * it is for demo purpose of task variables search capabilities
 */
@Ignore
public class TaskVariablesQueryServiceTest extends AbstractKieServicesBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(TaskVariablesQueryServiceTest.class);

    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();

    private static final String ARTIFACT_ID = "task-vars";

    private static final String GROUP_ID = "org.jbpm.test";

    private static final String VERSION = "1.0";

    private static final String SALES_ARTIFACT_ID = "product-sale";

    private static final String SALES_GROUP_ID = "org.jbpm.test";

    private static final String SALES_VERSION = "1.0";

    private static List<String> productCodes = new ArrayList<String>();

    private static List<String> countries = new ArrayList<String>();

    private static List<Integer> zipCodes = new ArrayList<Integer>();

    private String deploymentUnitId;

    private String deploymentUnitSalesId;

    @BeforeClass
    public static void fillTestData() {
        Random random = new Random();
        TaskVariablesQueryServiceTest.productCodes.add("BPMS");
        TaskVariablesQueryServiceTest.productCodes.add("BRMS");
        TaskVariablesQueryServiceTest.productCodes.add("KIE");
        TaskVariablesQueryServiceTest.productCodes.add("Fuse");
        TaskVariablesQueryServiceTest.productCodes.add("EAP");
        TaskVariablesQueryServiceTest.productCodes.add("WILDFLY");
        TaskVariablesQueryServiceTest.productCodes.add("TOMCAT");
        TaskVariablesQueryServiceTest.productCodes.add("APACHE");
        TaskVariablesQueryServiceTest.productCodes.add("WEBSPHERE");
        TaskVariablesQueryServiceTest.productCodes.add("WEBLOGIC");
        TaskVariablesQueryServiceTest.countries.add("United States");
        TaskVariablesQueryServiceTest.countries.add("United Kindgdom");
        TaskVariablesQueryServiceTest.countries.add("Belgium");
        TaskVariablesQueryServiceTest.countries.add("Poland");
        TaskVariablesQueryServiceTest.countries.add("Brazil");
        TaskVariablesQueryServiceTest.countries.add("Australia");
        TaskVariablesQueryServiceTest.countries.add("Netherland");
        TaskVariablesQueryServiceTest.countries.add("Italy");
        TaskVariablesQueryServiceTest.countries.add("Canada");
        TaskVariablesQueryServiceTest.countries.add("Finland");
        TaskVariablesQueryServiceTest.zipCodes.add(random.nextInt(1000));
        TaskVariablesQueryServiceTest.zipCodes.add(random.nextInt(1000));
        TaskVariablesQueryServiceTest.zipCodes.add(random.nextInt(1000));
        TaskVariablesQueryServiceTest.zipCodes.add(random.nextInt(1000));
        TaskVariablesQueryServiceTest.zipCodes.add(random.nextInt(1000));
        TaskVariablesQueryServiceTest.zipCodes.add(random.nextInt(1000));
        TaskVariablesQueryServiceTest.zipCodes.add(random.nextInt(1000));
        TaskVariablesQueryServiceTest.zipCodes.add(random.nextInt(1000));
        TaskVariablesQueryServiceTest.zipCodes.add(random.nextInt(1000));
        TaskVariablesQueryServiceTest.zipCodes.add(random.nextInt(1000));
        System.setProperty("org.jbpm.ht.callback", "jaas");
    }

    protected void buildDatasource() {
        ds = new bitronix.tm.resource.jdbc.PoolingDataSource();
        ds.setUniqueName("jdbc/testDS1");
        ds.setClassName("org.postgresql.xa.PGXADataSource");
        ds.setMaxPoolSize(30);
        ds.setAllowLocalTransactions(true);
        ds.getDriverProperties().put("user", "bpms");
        ds.getDriverProperties().put("password", "bpms");
        ds.getDriverProperties().put("serverName", "localhost");
        ds.getDriverProperties().put("portNumber", "5432");
        ds.getDriverProperties().put("databaseName", "bpms");
        ds.init();
    }

    @Before
    public void prepare() {
        configureServices();
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(TaskVariablesQueryServiceTest.GROUP_ID, TaskVariablesQueryServiceTest.ARTIFACT_ID, TaskVariablesQueryServiceTest.VERSION);
        File kjar = new File("src/test/resources/kjar-task-vars/task-vars-1.0.jar");
        File pom = new File("src/test/resources/kjar-task-vars/pom.xml");
        MavenRepository repository = MavenRepository.getMavenRepository();
        repository.installArtifact(releaseId, kjar, pom);
        ReleaseId releaseIdSales = ks.newReleaseId("org.jbpm.test", "product-sale", "1.0");
        File kjarSales = new File("src/test/resources/kjar-sales/product-sale-1.0.jar");
        File pomSales = new File("src/test/resources/kjar-sales/pom.xml");
        repository.installArtifact(releaseIdSales, kjarSales, pomSales);
        Assert.assertNotNull(deploymentService);
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(TaskVariablesQueryServiceTest.GROUP_ID, TaskVariablesQueryServiceTest.ARTIFACT_ID, TaskVariablesQueryServiceTest.VERSION);
        deploymentService.deploy(deploymentUnit);
        KModuleDeploymentUnit deploymentUnitSales = new KModuleDeploymentUnit(TaskVariablesQueryServiceTest.SALES_GROUP_ID, TaskVariablesQueryServiceTest.SALES_ARTIFACT_ID, TaskVariablesQueryServiceTest.SALES_VERSION);
        DeploymentDescriptor customDescriptor = new org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorImpl("org.jbpm.domain");
        customDescriptor.getBuilder().addMarshalingStrategy(new org.kie.internal.runtime.conf.ObjectModel("mvel", "new org.drools.persistence.jpa.marshaller.JPAPlaceholderResolverStrategy(\"org.jbpm.test:product-sale:1.0\", classLoader)"));
        // disable below line to avoid jpa marshaling of ProductSale variable
        deploymentUnitSales.setDeploymentDescriptor(customDescriptor);
        deploymentService.deploy(deploymentUnitSales);
        deploymentUnitSalesId = deploymentUnitSales.getIdentifier();
        DeployedUnit deployed = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        Assert.assertNotNull(deployed);
        Assert.assertNotNull(deployed.getDeploymentUnit());
        Assert.assertNotNull(runtimeDataService);
        Collection<ProcessDefinition> processes = runtimeDataService.getProcesses(new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(processes);
        Assert.assertEquals(2, processes.size());
        deploymentUnitId = deploymentUnit.getIdentifier();
    }

    @After
    public void cleanup() {
        cleanupSingletonSessionId();
        if (((units) != null) && (!(units.isEmpty()))) {
            for (DeploymentUnit unit : units) {
                deploymentService.undeploy(unit);
            }
            units.clear();
        } 
        close();
    }

    @Test
    public void testTaskVariableQueryOnBigTaskSet() {
        Random random = new Random();
        Map<String, Integer> numberOfInstancesPerProduct = new HashMap<String, Integer>();
        int i = 0;
        for (i = 0; i < 10000; i++) {
            int variablesIndex = random.nextInt(9);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("ProductCode", TaskVariablesQueryServiceTest.productCodes.get(variablesIndex));
            params.put("Country", TaskVariablesQueryServiceTest.countries.get(variablesIndex));
            params.put("ZipCode", TaskVariablesQueryServiceTest.zipCodes.get(variablesIndex));
            params.put("Delivery", ((i % 2) == 0 ? true : false));
            params.put("Actor", ("john,actor" + i));
            params.put("Group", "Crusaders");
            TaskVariablesQueryServiceTest.logger.debug(("Params : " + params));
            processService.startProcess(deploymentUnitId, "task-vars.TaskWithVars", params);
            Integer currentValue = numberOfInstancesPerProduct.get(TaskVariablesQueryServiceTest.productCodes.get(variablesIndex));
            if (currentValue == null) {
                currentValue = 0;
            } 
            numberOfInstancesPerProduct.put(TaskVariablesQueryServiceTest.productCodes.get(variablesIndex), (++currentValue));
        }
        TaskVariablesQueryServiceTest.logger.info("Generated {} process instances... doing searches now", i);
        TaskVariablesQueryServiceTest.logger.info("let's find tasks for product EAP only");
        long timestamp = System.currentTimeMillis();
        List<TaskSummary> myTasks = runtimeDataService.taskSummaryQuery("john").variableName("productCode").and().variableValue("EAP").build().getResultList();
        TaskVariablesQueryServiceTest.logger.info("Task query by variable took {} ms with result size {}", ((System.currentTimeMillis()) - timestamp), myTasks.size());
        Assert.assertEquals(numberOfInstancesPerProduct.get("EAP").intValue(), myTasks.size());
        TaskVariablesQueryServiceTest.logger.info("let's find tasks for product EAP or Wildfly");
        timestamp = System.currentTimeMillis();
        myTasks = runtimeDataService.taskSummaryQuery("john").and().variableName("productCode").variableValue("EAP", "WILDFLY").build().getResultList();
        TaskVariablesQueryServiceTest.logger.info("Task query by variable took {} ms with result size {}", ((System.currentTimeMillis()) - timestamp), myTasks.size());
        int total = (numberOfInstancesPerProduct.get("EAP").intValue()) + (numberOfInstancesPerProduct.get("WILDFLY").intValue());
        Assert.assertEquals(total, myTasks.size());
        TaskVariablesQueryServiceTest.logger.info("let's find tasks for product EAP or Wildfly but take only first 10 results");
        timestamp = System.currentTimeMillis();
        myTasks = runtimeDataService.taskSummaryQuery("john").and().variableName("productCode").variableValue("EAP", "WILDFLY").maxResults(10).offset(0).build().getResultList();
        TaskVariablesQueryServiceTest.logger.info("Task query by variable took {} ms with result size {}", ((System.currentTimeMillis()) - timestamp), myTasks.size());
        Assert.assertEquals(10, myTasks.size());
        TaskVariablesQueryServiceTest.logger.info("let's find tasks for product EAP and country Brazil");
        timestamp = System.currentTimeMillis();
        myTasks = runtimeDataService.taskSummaryQuery("john").and().newGroup().variableName("productCode").variableValue("EAP").endGroup().newGroup().variableName("country").variableValue("Brazil").endGroup().maxResults(30).offset(0).build().getResultList();
        TaskVariablesQueryServiceTest.logger.info("Task query by variable took {} ms with result size {}", ((System.currentTimeMillis()) - timestamp), myTasks.size());
        TaskVariablesQueryServiceTest.logger.info("let's find tasks for product BPMS and BRMS by using wildcard search");
        timestamp = System.currentTimeMillis();
        myTasks = runtimeDataService.taskSummaryQuery("john").and().variableName("productCode").regex().variableValue("B*").build().getResultList();
        TaskVariablesQueryServiceTest.logger.info("Task query by variable took {} ms with result size {}", ((System.currentTimeMillis()) - timestamp), myTasks.size());
        total = (numberOfInstancesPerProduct.get("BPMS").intValue()) + (numberOfInstancesPerProduct.get("BRMS").intValue());
        Assert.assertEquals(total, myTasks.size());
        TaskVariablesQueryServiceTest.logger.info("let's find tasks for product Weblogic or WebSphere by wildcard and country Canada");
        timestamp = System.currentTimeMillis();
        myTasks = runtimeDataService.taskSummaryQuery("john").and().newGroup().variableName("productCode").regex().variableValue("WEB*").endGroup().newGroup().variableName("country").variableValue("Canada").endGroup().maxResults(30).offset(0).build().getResultList();
        TaskVariablesQueryServiceTest.logger.info("Task query by variable took {} ms with result size {}", ((System.currentTimeMillis()) - timestamp), myTasks.size());
        TaskVariablesQueryServiceTest.logger.info("let's find tasks for product Weblogic and WebSphere by wildcard and country starting with United");
        timestamp = System.currentTimeMillis();
        myTasks = runtimeDataService.taskSummaryQuery("john").and().newGroup().variableName("productCode").regex().variableValue("WEBLOGIC").endGroup().newGroup().variableName("productCode").variableValue("WEBSPHERE").endGroup().newGroup().variableName("country").regex().variableValue("United*").endGroup().maxResults(30).offset(0).build().getResultList();
        TaskVariablesQueryServiceTest.logger.info("Task query by variable took {} ms with result size {}", ((System.currentTimeMillis()) - timestamp), myTasks.size());
        Assert.assertEquals(0, myTasks.size());// there is no way to get WEBLOGI and WEBSPHERE in United States or United Kingdom
        
        TaskVariablesQueryServiceTest.logger.info("let's find tasks for product EAP and country Brazil and tasks with status Ready and Reserver");
        timestamp = System.currentTimeMillis();
        myTasks = runtimeDataService.taskSummaryQuery("john").and().newGroup().variableName("productCode").variableValue("EAP").endGroup().newGroup().variableName("country").variableValue("Brazil").endGroup().and().status(Status.Ready, Status.Reserved).maxResults(30).offset(0).build().getResultList();
        TaskVariablesQueryServiceTest.logger.info("Task query by variable took {} ms with result size {}", ((System.currentTimeMillis()) - timestamp), myTasks.size());
    }

    @Test
    public void testTaskVariableQueryOnBigTaskSetAsJPA() throws Exception {
        Map<String, String> variableMap = new HashMap<String, String>();
        variableMap.put("COUNTRY", "string");
        variableMap.put("PRODUCTCODE", "string");
        variableMap.put("QUANTITY", "integer");
        variableMap.put("PRICE", "double");
        variableMap.put("SALEDATE", "date");
        SqlQueryDefinition query = new SqlQueryDefinition("getAllTaskInstancesWithCustomVariables", "jdbc/testDS1");
        query.setExpression(("select ti.*,  c.country, c.productCode, c.quantity, c.price, c.saleDate " + ("from AuditTaskImpl ti " + ("    inner join (select mv.map_var_id, mv.taskid from MappedVariable mv) mv " + ("      on (mv.taskid = ti.taskId) " + ("    inner join ProductSale c " + "      on (c.id = mv.map_var_id)"))))));
        queryService.registerQuery(query);
        SqlQueryDefinition queryTPO = new SqlQueryDefinition("getMyTaskInstancesWithCustomVariables", "jdbc/testDS1", Target.PO_TASK);
        queryTPO.setExpression(("select ti.*,  c.country, c.productCode, c.quantity, c.price, c.saleDate, oe.id oeid " + ("from AuditTaskImpl ti " + ("    inner join (select mv.map_var_id, mv.taskid from MappedVariable mv) mv " + ("      on (mv.taskid = ti.taskId) " + ("    inner join ProductSale c " + ("      on (c.id = mv.map_var_id), " + ("  PeopleAssignments_PotOwners po, OrganizationalEntity oe " + "    where ti.taskId = po.task_id and po.entity_id = oe.id"))))))));
        queryService.registerQuery(queryTPO);
        queryService.query(query.getName(), UserTaskInstanceWithCustomVarsQueryMapper.get(variableMap), new org.kie.api.runtime.query.QueryContext(), QueryParam.equalsTo("productCode", "EAP"));
        long currentTime = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance();
        RuntimeManager manager = deploymentService.getRuntimeManager(deploymentUnitSalesId);
        Assert.assertNotNull(manager);
        Class<?> clazz = Class.forName("org.jbpm.test.ProductSale", true, ((InternalRuntimeManager) (manager)).getEnvironment().getClassLoader());
        Random random = new Random();
        int i = 0;
        for (i = 0; i < 10000; i++) {
            cal.setTimeInMillis(currentTime);
            // add random number of days
            cal.add(Calendar.DAY_OF_YEAR, random.nextInt(60));
            Object product = clazz.newInstance();
            // set fields
            setFieldValue(product, "country", TaskVariablesQueryServiceTest.countries.get(random.nextInt(9)));
            setFieldValue(product, "productCode", TaskVariablesQueryServiceTest.productCodes.get(random.nextInt(9)));
            setFieldValue(product, "quantity", random.nextInt(50));
            setFieldValue(product, "price", ((random.nextDouble()) * 1000));
            setFieldValue(product, "saleDate", cal.getTime());
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("product", product);
            params.put("sales", ("john,actor" + i));
            params.put("salesGroup", "Crusaders");
            TaskVariablesQueryServiceTest.logger.debug(("Params : " + params));
            processService.startProcess(deploymentUnitSalesId, "product-sale.sale-product", params);
        }
        TaskVariablesQueryServiceTest.logger.info("Generated {} process instances... doing searches now", i);
        TaskVariablesQueryServiceTest.logger.info("let's find tasks for product EAP only");
        long timestamp = System.currentTimeMillis();
        List<UserTaskInstanceWithVarsDesc> taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceWithCustomVarsQueryMapper.get(variableMap), new org.kie.api.runtime.query.QueryContext(), QueryParam.equalsTo("productCode", "EAP"));
        TaskVariablesQueryServiceTest.logger.info("Task query by variable took {} ms with result size {}", ((System.currentTimeMillis()) - timestamp), taskInstanceLogs.size());
        TaskVariablesQueryServiceTest.logger.info("let's find tasks for product EAP or Wildfly");
        timestamp = System.currentTimeMillis();
        taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceWithCustomVarsQueryMapper.get(variableMap), new org.kie.api.runtime.query.QueryContext(), QueryParam.in("productCode", Arrays.asList("EAP", "WILDFLY")));
        TaskVariablesQueryServiceTest.logger.info("Task query by variable took {} ms with result size {}", ((System.currentTimeMillis()) - timestamp), taskInstanceLogs.size());
        TaskVariablesQueryServiceTest.logger.info("let's find tasks for product EAP and country Brazil");
        timestamp = System.currentTimeMillis();
        taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceWithCustomVarsQueryMapper.get(variableMap), new org.kie.api.runtime.query.QueryContext(), QueryParam.equalsTo("productCode", "EAP"), QueryParam.equalsTo("country", "Brazil"));
        TaskVariablesQueryServiceTest.logger.info("Task query by variable took {} ms with result size {}", ((System.currentTimeMillis()) - timestamp), taskInstanceLogs.size());
        TaskVariablesQueryServiceTest.logger.info("let's find tasks for product BPMS and BRMS by using wildcard search");
        timestamp = System.currentTimeMillis();
        taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceWithCustomVarsQueryMapper.get(variableMap), new org.kie.api.runtime.query.QueryContext(), QueryParam.likeTo("productCode", false, "B%"));
        TaskVariablesQueryServiceTest.logger.info("Task query by variable took {} ms with result size {}", ((System.currentTimeMillis()) - timestamp), taskInstanceLogs.size());
        TaskVariablesQueryServiceTest.logger.info("let's find tasks for product Weblogic or WebSphere by wildcard and country Canada");
        timestamp = System.currentTimeMillis();
        taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceWithCustomVarsQueryMapper.get(variableMap), new org.kie.api.runtime.query.QueryContext(), QueryParam.likeTo("productCode", false, "WEB%"), QueryParam.equalsTo("country", "Canada"));
        TaskVariablesQueryServiceTest.logger.info("Task query by variable took {} ms with result size {}", ((System.currentTimeMillis()) - timestamp), taskInstanceLogs.size());
        TaskVariablesQueryServiceTest.logger.info("let's find tasks for product EAP and country Brazil and tasks with status Ready and Reserved");
        timestamp = System.currentTimeMillis();
        taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceWithCustomVarsQueryMapper.get(variableMap), new org.kie.api.runtime.query.QueryContext(), QueryParam.equalsTo("productCode", "EAP"), QueryParam.equalsTo("country", "Brazil"), QueryParam.in("status", Arrays.asList(Status.Ready.toString(), Status.Reserved.toString())));
        TaskVariablesQueryServiceTest.logger.info("Task query by variable took {} ms with result size {}", ((System.currentTimeMillis()) - timestamp), taskInstanceLogs.size());
        TaskVariablesQueryServiceTest.logger.info("let's find tasks for product Weblogic or WebSphere by wildcard where quantity is bigger than 20");
        timestamp = System.currentTimeMillis();
        taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceWithCustomVarsQueryMapper.get(variableMap), new org.kie.api.runtime.query.QueryContext(), QueryParam.likeTo("productCode", false, "WEB%"), QueryParam.greaterOrEqualTo("quantity", 20));
        TaskVariablesQueryServiceTest.logger.info("Task query by variable took {} ms with result size {}", ((System.currentTimeMillis()) - timestamp), taskInstanceLogs.size());
        TaskVariablesQueryServiceTest.logger.info("let's find tasks for product EAP where sale was put in one month from now");
        cal.setTimeInMillis(currentTime);
        Date from = cal.getTime();
        cal.add(Calendar.MONTH, 1);
        Date to = cal.getTime();
        timestamp = System.currentTimeMillis();
        taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceWithCustomVarsQueryMapper.get(variableMap), new org.kie.api.runtime.query.QueryContext(), QueryParam.equalsTo("productCode", "EAP"), QueryParam.between("saleDate", from, to));
        TaskVariablesQueryServiceTest.logger.info("Task query by variable took {} ms with result size {}", ((System.currentTimeMillis()) - timestamp), taskInstanceLogs.size());
        // now same queries but with user/group filtering
        TaskVariablesQueryServiceTest.logger.info("################################################");
        TaskVariablesQueryServiceTest.logger.info("Task with user/group filtering");
        TaskVariablesQueryServiceTest.logger.info("################################################");
        identityProvider.setName("john");
        timestamp = System.currentTimeMillis();
        taskInstanceLogs = queryService.query(queryTPO.getName(), UserTaskInstanceWithCustomVarsQueryMapper.get(variableMap), new org.kie.api.runtime.query.QueryContext(), QueryParam.equalsTo("productCode", "EAP"));
        TaskVariablesQueryServiceTest.logger.info("Task query by variable took {} ms with result size {}", ((System.currentTimeMillis()) - timestamp), taskInstanceLogs.size());
        TaskVariablesQueryServiceTest.logger.info("let's find tasks for product EAP or Wildfly");
        timestamp = System.currentTimeMillis();
        taskInstanceLogs = queryService.query(queryTPO.getName(), UserTaskInstanceWithCustomVarsQueryMapper.get(variableMap), new org.kie.api.runtime.query.QueryContext(), QueryParam.in("productCode", Arrays.asList("EAP", "WILDFLY")));
        TaskVariablesQueryServiceTest.logger.info("Task query by variable took {} ms with result size {}", ((System.currentTimeMillis()) - timestamp), taskInstanceLogs.size());
        TaskVariablesQueryServiceTest.logger.info("let's find tasks for product EAP and country Brazil");
        timestamp = System.currentTimeMillis();
        taskInstanceLogs = queryService.query(queryTPO.getName(), UserTaskInstanceWithCustomVarsQueryMapper.get(variableMap), new org.kie.api.runtime.query.QueryContext(), QueryParam.equalsTo("productCode", "EAP"), QueryParam.equalsTo("country", "Brazil"));
        TaskVariablesQueryServiceTest.logger.info("Task query by variable took {} ms with result size {}", ((System.currentTimeMillis()) - timestamp), taskInstanceLogs.size());
        TaskVariablesQueryServiceTest.logger.info("let's find tasks for product BPMS and BRMS by using wildcard search");
        timestamp = System.currentTimeMillis();
        taskInstanceLogs = queryService.query(queryTPO.getName(), UserTaskInstanceWithCustomVarsQueryMapper.get(variableMap), new org.kie.api.runtime.query.QueryContext(), QueryParam.likeTo("productCode", false, "B%"));
        TaskVariablesQueryServiceTest.logger.info("Task query by variable took {} ms with result size {}", ((System.currentTimeMillis()) - timestamp), taskInstanceLogs.size());
        TaskVariablesQueryServiceTest.logger.info("let's find tasks for product Weblogic or WebSphere by wildcard and country Canada");
        timestamp = System.currentTimeMillis();
        taskInstanceLogs = queryService.query(queryTPO.getName(), UserTaskInstanceWithCustomVarsQueryMapper.get(variableMap), new org.kie.api.runtime.query.QueryContext(), QueryParam.likeTo("productCode", false, "WEB%"), QueryParam.equalsTo("country", "Canada"));
        TaskVariablesQueryServiceTest.logger.info("Task query by variable took {} ms with result size {}", ((System.currentTimeMillis()) - timestamp), taskInstanceLogs.size());
        TaskVariablesQueryServiceTest.logger.info("let's find tasks for product EAP and country Brazil and tasks with status Ready and Reserved");
        timestamp = System.currentTimeMillis();
        taskInstanceLogs = queryService.query(queryTPO.getName(), UserTaskInstanceWithCustomVarsQueryMapper.get(variableMap), new org.kie.api.runtime.query.QueryContext(), QueryParam.equalsTo("productCode", "EAP"), QueryParam.equalsTo("country", "Brazil"), QueryParam.in("status", Arrays.asList(Status.Ready.toString(), Status.Reserved.toString())));
        TaskVariablesQueryServiceTest.logger.info("Task query by variable took {} ms with result size {}", ((System.currentTimeMillis()) - timestamp), taskInstanceLogs.size());
        TaskVariablesQueryServiceTest.logger.info("let's find tasks for product Weblogic or WebSphere by wildcard where quantity is bigger than 20");
        timestamp = System.currentTimeMillis();
        taskInstanceLogs = queryService.query(queryTPO.getName(), UserTaskInstanceWithCustomVarsQueryMapper.get(variableMap), new org.kie.api.runtime.query.QueryContext(), QueryParam.likeTo("productCode", false, "WEB%"), QueryParam.greaterOrEqualTo("quantity", 20));
        TaskVariablesQueryServiceTest.logger.info("Task query by variable took {} ms with result size {}", ((System.currentTimeMillis()) - timestamp), taskInstanceLogs.size());
        TaskVariablesQueryServiceTest.logger.info("let's find tasks for product EAP where sale was put in one month from now");
        timestamp = System.currentTimeMillis();
        taskInstanceLogs = queryService.query(queryTPO.getName(), UserTaskInstanceWithCustomVarsQueryMapper.get(variableMap), new org.kie.api.runtime.query.QueryContext(), QueryParam.equalsTo("productCode", "EAP"), QueryParam.between("saleDate", from, to));
        TaskVariablesQueryServiceTest.logger.info("Task query by variable took {} ms with result size {}", ((System.currentTimeMillis()) - timestamp), taskInstanceLogs.size());
    }

    protected void setFieldValue(Object instance, String fieldName, Object value) {
        try {
            Field f = instance.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(instance, value);
        } catch (Exception e) {
        }
    }
}

