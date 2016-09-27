/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
import org.junit.Assume;
import org.kie.api.runtime.manager.audit.AuditService;
import org.junit.Before;
import java.util.Collection;
import org.jbpm.kie.services.api.DeploymentIdResolver;
import org.jbpm.services.api.model.DeploymentUnit;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.io.IOException;
import org.junit.Ignore;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieSession;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.kie.scanner.MavenRepository;
import org.jbpm.kie.test.objects.OtherPerson;
import org.jbpm.kie.test.objects.Person;
import org.jbpm.services.api.model.ProcessDefinition;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.builder.ReleaseId;
import java.util.Set;
import org.junit.Test;
import org.jbpm.kie.test.objects.Thing;
import org.kie.api.runtime.manager.audit.VariableInstanceLog;

public class BPMN2DataServicesReferencesTest extends AbstractKieServicesBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(DeploymentIdResolver.class);

    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();

    private DeploymentUnit deploymentUnit;

    private String deploymentId;

    private boolean loadBusinesRuleProcesses = true;

    private static final String PROC_ID_BUSINESS_RULE = "org.jbpm.kie.test.references.business";

    private static final String RULE_BUSINESS_RULE_PROCESS = "org.jbpm.kie.test.references.rules";

    private boolean loadCallActivityProcesses = true;

    private static final String PROC_ID_CALL_ACTIVITY = "org.jbpm.kie.test.references.parent";

    private static final String PROC_ID_CALL_ACTIVITY_BY_NAME = "org.jbpm.kie.test.references.parent.name";

    private static final String PROC_ID_ACTIVITY_CALLED = "org.jbpm.kie.test.references.subprocess";

    private boolean loadGlobalImportProcesses = true;

    private static final String PROC_ID_GLOBAL = "org.jbpm.kie.test.references.global";

    private static final String PROC_ID_IMPORT = "org.jbpm.kie.test.references.import";

    private boolean loadJavaMvelScriptProcesses = true;

    private static final String PROC_ID_JAVA_SCRIPT_QUALIFIED_CLASS = "org.jbpm.kie.test.references.qualified.java";

    private static final String PROC_ID_JAVA_THING_SCRIPT_QUALIFIED_CLASS = "org.jbpm.kie.test.references.qualified.java.thing";

    private static final String PROC_ID_MVEL_SCRIPT_QUALIFIED_CLASS = "org.jbpm.kie.test.references.qualified.mvel";

    private static final String PROC_ID_RULE_SCRIPT_QUALIFIED_CLASS = "org.jbpm.kie.test.references.qualified.drools";

    private boolean loadSignalGlobalInfoProcess = true;

    private static final String PROC_ID_SIGNAL = "org.jbpm.signal";

    @Before
    public void prepare() {
        configureServices();
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> resources = new ArrayList<String>();
        // extension
        if (loadGlobalImportProcesses) {
            resources.add("repo/processes/references/importWithScriptTask.bpmn2");
            resources.add("repo/processes/references/globalBasedOnEntryExitScriptProcess.bpmn2");
        } 
        // call activity (referenced sub processes)
        if (loadCallActivityProcesses) {
            resources.add("repo/processes/references/callActivityByNameParent.bpmn2");
            resources.add("repo/processes/references/callActivityParent.bpmn2");
            resources.add("repo/processes/references/callActivitySubProcess.bpmn2");
        } 
        // item def, business rules
        if (loadBusinesRuleProcesses) {
            resources.add("repo/processes/references/businessRuleTask.bpmn2");
            resources.add("repo/processes/references/businessRuleTask.drl");
        } 
        // qualified class in script
        if (loadJavaMvelScriptProcesses) {
            resources.add("repo/processes/references/javaScriptTaskWithQualifiedClass.bpmn2");
            resources.add("repo/processes/references/javaScriptTaskWithQualifiedClassItemDefinition.bpmn2");
            resources.add("repo/processes/references/mvelScriptTaskWithQualifiedClass.bpmn2");
            resources.add("repo/processes/references/rulesScriptTaskWithQualifiedClass.bpmn2");
        } 
        // qualified class in script
        if (loadSignalGlobalInfoProcess) {
            resources.add("repo/processes/general/signal.bpmn");
        } 
        InternalKieModule kJar1 = createKieJar(ks, releaseId, resources);
        File pom = new File("target/kmodule", "pom.xml");
        pom.getParentFile().mkdir();
        FileOutputStream fs = null;
        try {
            fs = new FileOutputStream(pom);
            fs.write(getPom(releaseId).getBytes());
        } catch (Exception e) {
            Assert.fail(("Unable to write pom.xml to filesystem: " + (e.getMessage())));
        }
        try {
            fs.close();
        } catch (Exception e) {
            BPMN2DataServicesReferencesTest.logger.info("Unable to close fileystem used to write pom.xml");
        }
        MavenRepository repository = MavenRepository.getMavenRepository();
        repository.deployArtifact(releaseId, kJar1, pom);
        // check
        Assert.assertNotNull(deploymentService);
        deploymentUnit = new org.jbpm.kie.services.impl.KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        deploymentId = deploymentUnit.getIdentifier();
        procInstIds.clear();
    }

    private Set<Long> procInstIds = new HashSet<>();

    @After
    public void cleanup() {
        for (long procInstId : procInstIds) {
            try {
                processService.abortProcessInstance(procInstId);
            } catch (Exception e) {
                // ignore if it fails..
            }
        }
        cleanupSingletonSessionId();
        if (((units) != null) && (!(units.isEmpty()))) {
            for (DeploymentUnit unit : units) {
                deploymentService.undeploy(unit);
            }
            units.clear();
        } 
        close();
    }

    @SafeVarargs
    private final long startProcess(String deploymentId, String processId, Map<String, Object>... params) {
        Long procInstId;
        if ((params != null) && ((params.length) > 0)) {
            procInstId = processService.startProcess(deploymentId, processId, params[0]);
        } else {
            procInstId = processService.startProcess(deploymentId, processId);
        }
        procInstIds.add(procInstId);
        return procInstId;
    }

    @Test
    public void testImport() throws IOException {
        Assume.assumeTrue("Skip import/global tests", loadGlobalImportProcesses);
        String processId = BPMN2DataServicesReferencesTest.PROC_ID_IMPORT;
        ProcessDefinition procDef = bpmn2Service.getProcessDefinition(deploymentId, processId);
        Assert.assertNotNull(procDef);
        // check that process runs
        Person person = new Person();
        person.setName("Max Rockatansky");
        person.setId(1979L);
        person.setTime(3L);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("person", person);
        Long procId = startProcess(deploymentId, processId, params);
        ProcessInstance procInst = processService.getProcessInstance(procId);
        Assert.assertNull(procInst);
        // verify process information
        Collection<String> refClasses = bpmn2Service.getJavaClasses(deploymentId, processId);
        Assert.assertNotNull("Null set of referenced classes", refClasses);
        Assert.assertFalse("Empty set of referenced classes", refClasses.isEmpty());
        Assert.assertEquals("Number referenced classes", 1, refClasses.size());
        Assert.assertEquals("Imported class in process", refClasses.iterator().next(), Person.class.getName());
    }

    @Test
    public void testGlobal() throws IOException {
        Assume.assumeTrue("Skip import/global tests", loadGlobalImportProcesses);
        String processId = BPMN2DataServicesReferencesTest.PROC_ID_GLOBAL;
        ProcessDefinition procDef = bpmn2Service.getProcessDefinition(deploymentId, processId);
        Assert.assertNotNull(procDef);
        // check that process runs
        Person person = new Person();
        person.setName("Max Rockatansky");
        person.setId(1979L);
        person.setTime(3L);
        KieSession ksession = deploymentService.getRuntimeManager(deploymentId).getRuntimeEngine(null).getKieSession();
        ksession.setGlobal("person", person);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask", new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler());
        Long procId = startProcess(deploymentId, processId);
        ProcessInstance procInst = processService.getProcessInstance(procId);
        Assert.assertNull(procInst);
        String log = person.getLog();
        Assert.assertFalse("Empty log", ((log == null) || (log.trim().isEmpty())));
        Assert.assertEquals("Empty log", log.split(":").length, 4);
        // verify process information
        Collection<String> refClasses = bpmn2Service.getJavaClasses(deploymentId, processId);
        Assert.assertNotNull("Null set of referenced classes", refClasses);
        Assert.assertFalse("Empty set of referenced classes", refClasses.isEmpty());
        Assert.assertEquals("Number referenced classes", 1, refClasses.size());
        Assert.assertEquals("Imported class in process", refClasses.iterator().next(), Person.class.getName());
    }

    @Test
    public void testCallActivityByName() throws IOException {
        Assume.assumeTrue("Skip call activity tests", loadCallActivityProcesses);
        String processId = BPMN2DataServicesReferencesTest.PROC_ID_CALL_ACTIVITY_BY_NAME;
        ProcessDefinition procDef = bpmn2Service.getProcessDefinition(deploymentId, processId);
        Assert.assertNotNull(procDef);
        // run process (to verify that it works)
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "oldValue");
        Long procId = startProcess(deploymentId, processId, params);
        ProcessInstance procInst = processService.getProcessInstance(procId);
        Assert.assertNull(procInst);
        AuditService auditService = deploymentService.getRuntimeManager(deploymentId).getRuntimeEngine(null).getAuditService();
        List<? extends VariableInstanceLog> logs = auditService.findVariableInstances(procId);
        boolean foundY = false;
        for (VariableInstanceLog log : logs) {
            if ((log.getVariableId().equals("y")) && (log.getValue().equals("new value"))) {
                foundY = true;
            } 
        }
        Assert.assertTrue("Parent process did not call sub process", foundY);
        // check information about process
        Collection<String> refProcesses = bpmn2Service.getReusableSubProcesses(deploymentId, processId);
        Assert.assertNotNull("Null set of referenced processes", refProcesses);
        Assert.assertFalse("Empty set of referenced processes", refProcesses.isEmpty());
        Assert.assertEquals("Number referenced processes", 1, refProcesses.size());
        Assert.assertEquals("Imported class in processes", BPMN2DataServicesReferencesTest.PROC_ID_ACTIVITY_CALLED, refProcesses.iterator().next());
    }

    @Test
    public void testCallActivity() throws IOException {
        Assume.assumeTrue("Skip call activity tests", loadCallActivityProcesses);
        String processId = BPMN2DataServicesReferencesTest.PROC_ID_CALL_ACTIVITY;
        ProcessDefinition procDef = bpmn2Service.getProcessDefinition(deploymentId, processId);
        Assert.assertNotNull(procDef);
        // run process (to verify that it works)
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "oldValue");
        Long procId = startProcess(deploymentId, processId, params);
        ProcessInstance procInst = processService.getProcessInstance(procId);
        Assert.assertNull(procInst);
        AuditService auditService = deploymentService.getRuntimeManager(deploymentId).getRuntimeEngine(null).getAuditService();
        List<? extends VariableInstanceLog> logs = auditService.findVariableInstances(procId);
        boolean foundY = false;
        for (VariableInstanceLog log : logs) {
            if ((log.getVariableId().equals("y")) && (log.getValue().equals("new value"))) {
                foundY = true;
            } 
        }
        Assert.assertTrue("Parent process did not call sub process", foundY);
        // check information about process
        Collection<String> refProcesses = bpmn2Service.getReusableSubProcesses(deploymentId, processId);
        Assert.assertNotNull("Null set of referenced processes", refProcesses);
        Assert.assertFalse("Empty set of referenced processes", refProcesses.isEmpty());
        Assert.assertEquals("Number referenced processes", 1, refProcesses.size());
        Assert.assertEquals("Imported class in processes", BPMN2DataServicesReferencesTest.PROC_ID_ACTIVITY_CALLED, refProcesses.iterator().next());
    }

    @Test
    public void testBusinessRuleTask() throws IOException {
        Assume.assumeTrue("Skip business rule tests", loadBusinesRuleProcesses);
        String processId = BPMN2DataServicesReferencesTest.PROC_ID_BUSINESS_RULE;
        ProcessDefinition procDef = bpmn2Service.getProcessDefinition(deploymentId, processId);
        Assert.assertNotNull(procDef);
        // check that process runs
        Person person = new Person();
        person.setName("Max Rockatansky");
        person.setId(1979L);
        person.setTime(3L);
        KieSession ksession = deploymentService.getRuntimeManager(deploymentId).getRuntimeEngine(null).getKieSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        Long procId = startProcess(deploymentId, processId);
        String ruleOutput = "Executed";
        Assert.assertEquals("Global did not contain output from rule!", 1, list.size());
        Assert.assertEquals("Global did not contain correct output of rule!", ruleOutput, list.get(0));
        ProcessInstance procInst = processService.getProcessInstance(procId);
        Assert.assertNull("Process instance did not complete!", procInst);
        // check information about process
        Collection<String> refRules = bpmn2Service.getRuleSets(deploymentId, processId);
        Assert.assertNotNull("Null set of imported rules", refRules);
        Assert.assertFalse("Empty set of imported rules", refRules.isEmpty());
        Assert.assertEquals("Number imported rules", 1, refRules.size());
        Assert.assertEquals("Name of imported ruleset", BPMN2DataServicesReferencesTest.RULE_BUSINESS_RULE_PROCESS, refRules.iterator().next());
        refRules = procDef.getReferencedRules();
        Assert.assertNotNull("Null set of imported rules", refRules);
        Assert.assertFalse("Empty set of imported rules", refRules.isEmpty());
        Assert.assertEquals("Number imported rules", 1, refRules.size());
        Assert.assertEquals("Name of imported ruleset", BPMN2DataServicesReferencesTest.RULE_BUSINESS_RULE_PROCESS, refRules.iterator().next());
    }

    @Test
    public void testJavaScriptWithQualifiedClass() throws IOException {
        runScriptTest(BPMN2DataServicesReferencesTest.PROC_ID_JAVA_SCRIPT_QUALIFIED_CLASS);
    }

    @Test
    public void testJavaThingScriptWithQualifiedClass() throws IOException {
        runScriptTest(BPMN2DataServicesReferencesTest.PROC_ID_JAVA_THING_SCRIPT_QUALIFIED_CLASS);
    }

    @Test
    public void testMvelScriptWithQualifiedClass() throws IOException {
        runScriptTest(BPMN2DataServicesReferencesTest.PROC_ID_MVEL_SCRIPT_QUALIFIED_CLASS);
    }

    private void runScriptTest(String processId) {
        Assume.assumeTrue("Skip script/expr tests", loadJavaMvelScriptProcesses);
        ProcessDefinition procDef = bpmn2Service.getProcessDefinition(deploymentId, processId);
        Assert.assertNotNull(procDef);
        // check that process runs
        Person person = new Person();
        person.setName("Max Rockatansky");
        person.setId(1979L);
        person.setTime(3L);
        Map<String, Object> params = new HashMap<String, Object>(1);
        params.put("person", person);
        Long procId = startProcess(deploymentId, processId, params);
        Assert.assertNull("Process instance did not complete", processService.getProcessInstance(procId));
        Assert.assertTrue("Script did not modify variable!", person.getLog().startsWith("Hello"));
        Collection<String> javaClasses = bpmn2Service.getJavaClasses(deploymentId, processId);
        Assert.assertNotNull("Null set of java classes", javaClasses);
        Assert.assertFalse("Empty set of java classes", javaClasses.isEmpty());
        Assert.assertEquals("Number java classes", 4, javaClasses.size());
        String[] expected = new String[]{ "java.lang.Object" , Person.class.getCanonicalName() , OtherPerson.class.getCanonicalName() , Thing.class.getCanonicalName() };
        Set<String> expectedClasses = new HashSet<String>(Arrays.asList(expected));
        for (String className : javaClasses) {
            Assert.assertTrue(("Class name is not qualified: " + className), className.contains("."));
            Assert.assertTrue(("Unexpected class: " + className), expectedClasses.remove(className));
        }
        if (!(expectedClasses.isEmpty())) {
            Assert.fail(("Expected class not found to be referenced: " + (expectedClasses.iterator().next())));
        } 
    }

    // TODO!
    @Test
    @Ignore
    public void testDroolsScriptWithQualifiedClass() throws Exception {
        Assume.assumeTrue("Skip script/expr tests", loadJavaMvelScriptProcesses);
        String processId = BPMN2DataServicesReferencesTest.PROC_ID_RULE_SCRIPT_QUALIFIED_CLASS;
        ProcessDefinition procDef = bpmn2Service.getProcessDefinition(deploymentId, processId);
        Assert.assertNotNull(procDef);
        // check that process runs
        Person person = new Person();
        person.setName("Max");
        person.setId(1979L);
        person.setTime(3L);
        KieSession ksession = deploymentService.getRuntimeManager(deploymentId).getRuntimeEngine(null).getKieSession();
        ksession.insert(person);
        ksession.insert(new Thing());
        ksession.insert(new OtherPerson(person));
        ksession.insert(person.getName());
        ksession.insert(person.getId());
        Long procId = startProcess(deploymentId, processId);
        Assert.assertNull("Process instance did not complete:", processService.getProcessInstance(procId));
        Collection<String> javaClasses = bpmn2Service.getJavaClasses(deploymentId, processId);
        Assert.assertNotNull("Null set of java classes", javaClasses);
        Assert.assertFalse("Empty set of java classes", javaClasses.isEmpty());
        Assert.assertEquals("Number java classes", 4, javaClasses.size());
        String[] expected = new String[]{ "java.lang.Object" , Person.class.getCanonicalName() , OtherPerson.class.getCanonicalName() , Thing.class.getCanonicalName() };
        Set<String> expectedClasses = new HashSet<String>(Arrays.asList(expected));
        for (String className : javaClasses) {
            Assert.assertTrue(("Class name is not qualified: " + className), className.contains("."));
            Assert.assertTrue(("Unexpected class: " + className), expectedClasses.remove(className));
        }
        if (!(expectedClasses.isEmpty())) {
            Assert.fail(("Expected class not found to be referenced: " + (expectedClasses.iterator().next())));
        } 
    }

    @Test
    public void testSignalsAndGlobals() throws IOException {
        Assume.assumeTrue("Skip signal/global tests", loadSignalGlobalInfoProcess);
        String processId = BPMN2DataServicesReferencesTest.PROC_ID_SIGNAL;
        // check that process starts
        KieSession ksession = deploymentService.getRuntimeManager(deploymentId).getRuntimeEngine(null).getKieSession();
        ksession.setGlobal("person", new Person());
        long procInstId = startProcess(deploymentId, processId);
        ProcessDefinition procDef = bpmn2Service.getProcessDefinition(deploymentId, processId);
        Assert.assertNotNull(procDef);
        // check information about process
        Assert.assertNotNull("Null signals list", procDef.getSignals());
        Assert.assertFalse("Empty signals list", procDef.getSignals().isEmpty());
        Assert.assertEquals("Unexpected signal", "MySignal", procDef.getSignals().iterator().next());
        Collection<String> globalNames = procDef.getGlobals();
        Assert.assertNotNull("Null globals list", globalNames);
        Assert.assertFalse("Empty globals list", globalNames.isEmpty());
        Assert.assertEquals("globals list size", 2, globalNames.size());
        for (String globalName : globalNames) {
            Assert.assertTrue(("Unexpected global: " + globalName), (("person".equals(globalName)) || ("name".equals(globalName))));
        }
        // cleanup
        processService.abortProcessInstance(procInstId);
    }
}

