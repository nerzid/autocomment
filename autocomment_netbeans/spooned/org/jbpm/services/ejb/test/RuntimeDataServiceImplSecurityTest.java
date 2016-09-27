/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.services.ejb.test;

import org.junit.After;
import org.jboss.arquillian.junit.Arquillian;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Before;
import java.util.Collection;
import org.kie.internal.process.CorrelationKey;
import org.jboss.arquillian.container.test.api.Deployment;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.jbpm.services.ejb.api.DeploymentServiceEJBLocal;
import org.jbpm.services.api.model.DeploymentUnit;
import javax.ejb.EJB;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import javax.inject.Inject;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.kie.api.KieServices;
import java.util.List;
import java.util.Map;
import org.kie.scanner.MavenRepository;
import org.kie.api.runtime.process.ProcessInstance;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.ejb.api.ProcessServiceEJBLocal;
import org.kie.api.builder.ReleaseId;
import org.junit.runner.RunWith;
import org.jbpm.services.ejb.api.RuntimeDataServiceEJBLocal;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.kie.api.task.model.TaskSummary;
import org.junit.Test;
import org.jbpm.services.ejb.test.identity.TestIdentityProvider;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.jbpm.services.ejb.impl.tx.TransactionalCommandServiceEJBImpl;
import org.jbpm.services.api.model.UserTaskInstanceDesc;
import org.jboss.shrinkwrap.api.spec.WebArchive;

@RunWith(value = Arquillian.class)
public class RuntimeDataServiceImplSecurityTest extends AbstractTestSupport {
    @Deployment
    public static WebArchive createDeployment() {
        File archive = new File("target/sample-war-ejb-app.war");
        if (!(archive.exists())) {
            throw new IllegalStateException("There is no archive yet generated, run maven build or mvn assembly:assembly");
        } 
        WebArchive war = ShrinkWrap.createFromZipFile(WebArchive.class, archive);
        war.addPackage("org.jbpm.services.ejb.test");// test cases
        
        war.addPackage("org.jbpm.services.ejb.test.identity");// test identity provider
        
        // deploy test kjar
        RuntimeDataServiceImplSecurityTest.deployKjar();
        return war;
    }

    private Long processInstanceId = null;

    private KModuleDeploymentUnit deploymentUnit = null;

    @Before
    public void prepare() {
        Assert.assertNotNull(deploymentService);
        deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        Assert.assertNotNull(processService);
    }

    protected static void deployKjar() {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("processes/EmptyHumanTask.bpmn");
        processes.add("processes/humanTask.bpmn");
        processes.add("processes/SimpleHTProcess.bpmn2");
        DeploymentDescriptor customDescriptor = new org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorImpl("org.jbpm.domain");
        customDescriptor.getBuilder().addRequiredRole("view:managers");
        Map<String, String> resources = new HashMap<String, String>();
        resources.put(("src/main/resources/" + (DeploymentDescriptor.META_INF_LOCATION)), customDescriptor.toXml());
        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes, resources);
        File pom = new File("target/kmodule", "pom.xml");
        pom.getParentFile().mkdir();
        try {
            FileOutputStream fs = new FileOutputStream(pom);
            fs.write(getPom(releaseId).getBytes());
            fs.close();
        } catch (Exception e) {
        }
        MavenRepository repository = MavenRepository.getMavenRepository();
        repository.installArtifact(releaseId, kJar1, pom);
    }

    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();

    @After
    public void cleanup() {
        if ((processInstanceId) != null) {
            // let's abort process instance to leave the system in clear state
            processService.abortProcessInstance(processInstanceId);
            ProcessInstance pi = processService.getProcessInstance(processInstanceId);
            Assert.assertNull(pi);
        } 
        int deleted = 0;
        deleted += commandService.execute(new org.jbpm.shared.services.impl.commands.UpdateStringCommand("delete from  NodeInstanceLog nid"));
        deleted += commandService.execute(new org.jbpm.shared.services.impl.commands.UpdateStringCommand("delete from  ProcessInstanceLog pid"));
        deleted += commandService.execute(new org.jbpm.shared.services.impl.commands.UpdateStringCommand("delete from  VariableInstanceLog vsd"));
        deleted += commandService.execute(new org.jbpm.shared.services.impl.commands.UpdateStringCommand("delete from  AuditTaskImpl vsd"));
        System.out.println(("Deleted " + deleted));
        cleanupSingletonSessionId();
        if (((units) != null) && (!(units.isEmpty()))) {
            for (DeploymentUnit unit : units) {
                deploymentService.undeploy(unit);
            }
            units.clear();
        } 
    }

    @EJB
    private DeploymentServiceEJBLocal deploymentService;

    @EJB
    private ProcessServiceEJBLocal processService;

    @EJB
    private RuntimeDataServiceEJBLocal runtimeDataService;

    @EJB(beanInterface = TransactionalCommandServiceEJBImpl.class)
    private TransactionalCommandService commandService;

    @Inject
    private TestIdentityProvider identityProvider;

    @Test
    public void testGetProcessInstances() {
        // let's grant managers role so process can be started
        List<String> roles = new ArrayList<String>();
        roles.add("managers");
        identityProvider.setRoles(roles);
        Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(0, instances.size());
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        instances = runtimeDataService.getProcessInstances(new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(1, instances.size());
        Assert.assertEquals(1, ((int) (instances.iterator().next().getState())));
        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
        instances = runtimeDataService.getProcessInstances(new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(1, instances.size());
        Assert.assertEquals(3, ((int) (instances.iterator().next().getState())));
    }

    @Test
    public void testGetProcessInstancesByState() {
        // let's grant managers role so process can be started
        List<String> roles = new ArrayList<String>();
        roles.add("managers");
        identityProvider.setRoles(roles);
        Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(0, instances.size());
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        List<Integer> states = new ArrayList<Integer>();
        // search for aborted only
        states.add(3);
        instances = runtimeDataService.getProcessInstances(states, null, new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(0, instances.size());
        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
        instances = runtimeDataService.getProcessInstances(states, null, new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(1, instances.size());
        Assert.assertEquals(3, ((int) (instances.iterator().next().getState())));
    }

    @Test
    public void testGetProcessInstancesByDeploymentIdAndState() {
        // let's grant managers role so process can be started
        List<String> roles = new ArrayList<String>();
        roles.add("managers");
        identityProvider.setRoles(roles);
        Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(0, instances.size());
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        List<Integer> states = new ArrayList<Integer>();
        // search for aborted only
        states.add(3);
        instances = runtimeDataService.getProcessInstancesByDeploymentId(deploymentUnit.getIdentifier(), states, new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(0, instances.size());
        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
        instances = runtimeDataService.getProcessInstancesByDeploymentId(deploymentUnit.getIdentifier(), states, new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(1, instances.size());
        Assert.assertEquals(3, ((int) (instances.iterator().next().getState())));
    }

    @Test
    public void testGetProcessInstancesByProcessId() {
        // let's grant managers role so process can be started
        List<String> roles = new ArrayList<String>();
        roles.add("managers");
        identityProvider.setRoles(roles);
        Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(0, instances.size());
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        instances = runtimeDataService.getProcessInstancesByProcessDefinition("org.jbpm.writedocument", new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(1, instances.size());
        ProcessInstanceDesc instance = instances.iterator().next();
        Assert.assertEquals(1, ((int) (instance.getState())));
        Assert.assertEquals("org.jbpm.writedocument", instance.getProcessId());
        List<TaskSummary> taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new org.kie.internal.query.QueryFilter(0, 10));
        Assert.assertNotNull(taskSummaries);
        Assert.assertEquals(1, taskSummaries.size());
        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
        instances = runtimeDataService.getProcessInstancesByProcessDefinition("org.jbpm.writedocument", new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(1, instances.size());
        instance = instances.iterator().next();
        Assert.assertEquals(3, ((int) (instance.getState())));
        Assert.assertEquals("org.jbpm.writedocument", instance.getProcessId());
    }

    @Test
    public void testGetProcessInstanceById() {
        // let's grant managers role so process can be started
        List<String> roles = new ArrayList<String>();
        roles.add("managers");
        identityProvider.setRoles(roles);
        Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(0, instances.size());
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        ProcessInstanceDesc instance = runtimeDataService.getProcessInstanceById(processInstanceId);
        Assert.assertNotNull(instance);
        Assert.assertEquals(1, ((int) (instance.getState())));
        Assert.assertEquals("org.jbpm.writedocument", instance.getProcessId());
        List<UserTaskInstanceDesc> tasks = instance.getActiveTasks();
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());
        UserTaskInstanceDesc activeTask = tasks.get(0);
        Assert.assertNotNull(activeTask);
        Assert.assertEquals(Status.Reserved.name(), activeTask.getStatus());
        Assert.assertEquals(instance.getId(), activeTask.getProcessInstanceId());
        Assert.assertEquals(instance.getProcessId(), activeTask.getProcessId());
        Assert.assertEquals("Write a Document", activeTask.getName());
        Assert.assertEquals("salaboy", activeTask.getActualOwner());
        Assert.assertEquals(deploymentUnit.getIdentifier(), activeTask.getDeploymentId());
        processService.abortProcessInstance(processInstanceId);
        instance = runtimeDataService.getProcessInstanceById(processInstanceId);
        processInstanceId = null;
        Assert.assertNotNull(instance);
        Assert.assertEquals(3, ((int) (instance.getState())));
        Assert.assertEquals("org.jbpm.writedocument", instance.getProcessId());
    }

    @Test
    public void testGetProcessInstancesByProcessIdAndState() {
        // let's grant managers role so process can be started
        List<String> roles = new ArrayList<String>();
        roles.add("managers");
        identityProvider.setRoles(roles);
        Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(0, instances.size());
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        List<Integer> states = new ArrayList<Integer>();
        // search for aborted only
        states.add(3);
        instances = runtimeDataService.getProcessInstancesByProcessId(states, "org.jbpm.writedocument", null, new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(0, instances.size());
        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
        instances = runtimeDataService.getProcessInstancesByProcessId(states, "org.jbpm.writedocument", null, new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(1, instances.size());
        Assert.assertEquals(3, ((int) (instances.iterator().next().getState())));
    }

    @Test
    public void testGetProcessInstancesByPartialProcessIdAndState() {
        // let's grant managers role so process can be started
        List<String> roles = new ArrayList<String>();
        roles.add("managers");
        identityProvider.setRoles(roles);
        Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(0, instances.size());
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        List<Integer> states = new ArrayList<Integer>();
        // search for aborted only
        states.add(3);
        instances = runtimeDataService.getProcessInstancesByProcessId(states, "org.jbpm%", null, new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(0, instances.size());
        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
        instances = runtimeDataService.getProcessInstancesByProcessId(states, "org.jbpm%", null, new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(1, instances.size());
        Assert.assertEquals(3, ((int) (instances.iterator().next().getState())));
    }

    /* same tests but for user who does not have access rights to that */
    @Test
    public void testGetProcessInstancesNoAccess() {
        List<String> roles = new ArrayList<String>();
        identityProvider.setRoles(roles);
        Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(0, instances.size());
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        instances = runtimeDataService.getProcessInstances(new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(0, instances.size());
        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
        instances = runtimeDataService.getProcessInstances(new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(0, instances.size());
    }

    @Test
    public void testGetProcessInstancesByStateNoAccess() {
        List<String> roles = new ArrayList<String>();
        identityProvider.setRoles(roles);
        Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(0, instances.size());
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        List<Integer> states = new ArrayList<Integer>();
        // search for aborted only
        states.add(3);
        instances = runtimeDataService.getProcessInstances(states, null, new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(0, instances.size());
        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
        instances = runtimeDataService.getProcessInstances(states, null, new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(0, instances.size());
    }

    @Test
    public void testGetProcessInstancesByDeploymentIdAndStateNoAccess() {
        List<String> roles = new ArrayList<String>();
        identityProvider.setRoles(roles);
        Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(0, instances.size());
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        List<Integer> states = new ArrayList<Integer>();
        // search for aborted only
        states.add(3);
        instances = runtimeDataService.getProcessInstancesByDeploymentId(deploymentUnit.getIdentifier(), states, new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(0, instances.size());
        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
        instances = runtimeDataService.getProcessInstancesByDeploymentId(deploymentUnit.getIdentifier(), states, new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(0, instances.size());
    }

    @Test
    public void testGetProcessInstancesByProcessIdNoAccess() {
        List<String> roles = new ArrayList<String>();
        identityProvider.setRoles(roles);
        Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(0, instances.size());
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        instances = runtimeDataService.getProcessInstancesByProcessDefinition("org.jbpm.writedocument", new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(0, instances.size());
        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
        instances = runtimeDataService.getProcessInstancesByProcessDefinition("org.jbpm.writedocument", new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(0, instances.size());
    }

    @Test
    public void testGetProcessInstanceByIdNoAccess() {
        List<String> roles = new ArrayList<String>();
        identityProvider.setRoles(roles);
        Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(0, instances.size());
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        ProcessInstanceDesc instance = runtimeDataService.getProcessInstanceById(processInstanceId);
        Assert.assertNotNull(instance);
        processService.abortProcessInstance(processInstanceId);
        instance = runtimeDataService.getProcessInstanceById(processInstanceId);
        processInstanceId = null;
        Assert.assertNotNull(instance);
    }

    @Test
    public void testGetProcessInstancesByProcessIdAndStateNoAccess() {
        List<String> roles = new ArrayList<String>();
        identityProvider.setRoles(roles);
        Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(0, instances.size());
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        List<Integer> states = new ArrayList<Integer>();
        // search for aborted only
        states.add(3);
        instances = runtimeDataService.getProcessInstancesByProcessId(states, "org.jbpm.writedocument", null, new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(0, instances.size());
        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
        instances = runtimeDataService.getProcessInstancesByProcessId(states, "org.jbpm.writedocument", null, new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(0, instances.size());
    }

    @Test
    public void testGetProcessInstancesByPartialProcessIdAndStateNoAccess() {
        List<String> roles = new ArrayList<String>();
        identityProvider.setRoles(roles);
        Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(0, instances.size());
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assert.assertNotNull(processInstanceId);
        List<Integer> states = new ArrayList<Integer>();
        // search for aborted only
        states.add(3);
        instances = runtimeDataService.getProcessInstancesByProcessId(states, "org.jbpm%", null, new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(0, instances.size());
        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
        instances = runtimeDataService.getProcessInstancesByProcessId(states, "org.jbpm%", null, new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(0, instances.size());
    }

    @Test
    public void testGetProcessInstanceByCorrelationKey() {
        Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(0, instances.size());
        CorrelationKey key = KieInternalServices.Factory.get().newCorrelationKeyFactory().newCorrelationKey("my business key");
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument", key);
        Assert.assertNotNull(processInstanceId);
        ProcessInstanceDesc instance = runtimeDataService.getProcessInstanceByCorrelationKey(key);
        Assert.assertNotNull(instance);
        Assert.assertEquals(1, ((int) (instance.getState())));
        Assert.assertEquals("org.jbpm.writedocument", instance.getProcessId());
        Assert.assertEquals("my business key", instance.getCorrelationKey());
        List<UserTaskInstanceDesc> tasks = instance.getActiveTasks();
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());
        UserTaskInstanceDesc activeTask = tasks.get(0);
        Assert.assertNotNull(activeTask);
        Assert.assertEquals(Status.Reserved.name(), activeTask.getStatus());
        Assert.assertEquals(instance.getId(), activeTask.getProcessInstanceId());
        Assert.assertEquals(instance.getProcessId(), activeTask.getProcessId());
        Assert.assertEquals("Write a Document", activeTask.getName());
        Assert.assertEquals("salaboy", activeTask.getActualOwner());
        Assert.assertEquals(deploymentUnit.getIdentifier(), activeTask.getDeploymentId());
        processService.abortProcessInstance(processInstanceId);
        instance = runtimeDataService.getProcessInstanceByCorrelationKey(key);
        processInstanceId = null;
        Assert.assertNull(instance);
    }

    @Test
    public void testGetProcessInstancesByCorrelationKey() {
        // let's grant managers role so process can be started
        List<String> roles = new ArrayList<String>();
        roles.add("managers");
        identityProvider.setRoles(roles);
        Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(0, instances.size());
        CorrelationKey key = KieInternalServices.Factory.get().newCorrelationKeyFactory().newCorrelationKey("my business key");
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument", key);
        Assert.assertNotNull(processInstanceId);
        Collection<ProcessInstanceDesc> keyedInstances = runtimeDataService.getProcessInstancesByCorrelationKey(key, new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(keyedInstances);
        Assert.assertEquals(1, keyedInstances.size());
        ProcessInstanceDesc instance = keyedInstances.iterator().next();
        Assert.assertNotNull(instance);
        Assert.assertEquals(1, ((int) (instance.getState())));
        Assert.assertEquals("org.jbpm.writedocument", instance.getProcessId());
        Assert.assertEquals("my business key", instance.getCorrelationKey());
        List<UserTaskInstanceDesc> tasks = instance.getActiveTasks();
        Assert.assertNull(tasks);
        processService.abortProcessInstance(processInstanceId);
        instance = runtimeDataService.getProcessInstanceByCorrelationKey(key);
        processInstanceId = null;
        Assert.assertNull(instance);
        keyedInstances = runtimeDataService.getProcessInstancesByCorrelationKey(key, new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(keyedInstances);
        Assert.assertEquals(1, keyedInstances.size());
        instance = keyedInstances.iterator().next();
        Assert.assertEquals(3, ((int) (instance.getState())));
        Assert.assertEquals("org.jbpm.writedocument", instance.getProcessId());
        Assert.assertEquals("my business key", instance.getCorrelationKey());
    }

    @Test
    public void testGetProcessInstancesByPartialCorrelationKey() {
        // let's grant managers role so process can be started
        List<String> roles = new ArrayList<String>();
        roles.add("managers");
        identityProvider.setRoles(roles);
        Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(instances);
        Assert.assertEquals(0, instances.size());
        List<String> props = new ArrayList<String>();
        props.add("first");
        props.add("second");
        props.add("third");
        List<String> partial1props = new ArrayList<String>();
        partial1props.add("first");
        partial1props.add("second");
        List<String> partial2props = new ArrayList<String>();
        partial2props.add("first");
        CorrelationKey key = KieInternalServices.Factory.get().newCorrelationKeyFactory().newCorrelationKey(props);
        CorrelationKey partialKey1 = KieInternalServices.Factory.get().newCorrelationKeyFactory().newCorrelationKey(partial1props);
        CorrelationKey partialKey2 = KieInternalServices.Factory.get().newCorrelationKeyFactory().newCorrelationKey(partial2props);
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument", key);
        Assert.assertNotNull(processInstanceId);
        Collection<ProcessInstanceDesc> keyedInstances = runtimeDataService.getProcessInstancesByCorrelationKey(key, new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(keyedInstances);
        Assert.assertEquals(1, keyedInstances.size());
        ProcessInstanceDesc instance = keyedInstances.iterator().next();
        Assert.assertNotNull(instance);
        Assert.assertEquals(1, ((int) (instance.getState())));
        Assert.assertEquals("org.jbpm.writedocument", instance.getProcessId());
        Assert.assertEquals("first:second:third", instance.getCorrelationKey());
        List<UserTaskInstanceDesc> tasks = instance.getActiveTasks();
        Assert.assertNull(tasks);
        // search by partial key 1
        keyedInstances = runtimeDataService.getProcessInstancesByCorrelationKey(partialKey1, new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(keyedInstances);
        Assert.assertEquals(1, keyedInstances.size());
        instance = keyedInstances.iterator().next();
        Assert.assertNotNull(instance);
        Assert.assertEquals(1, ((int) (instance.getState())));
        Assert.assertEquals("org.jbpm.writedocument", instance.getProcessId());
        Assert.assertEquals("first:second:third", instance.getCorrelationKey());
        // search by partial key 2
        keyedInstances = runtimeDataService.getProcessInstancesByCorrelationKey(partialKey2, new org.kie.api.runtime.query.QueryContext());
        Assert.assertNotNull(keyedInstances);
        Assert.assertEquals(1, keyedInstances.size());
        instance = keyedInstances.iterator().next();
        Assert.assertNotNull(instance);
        Assert.assertEquals(1, ((int) (instance.getState())));
        Assert.assertEquals("org.jbpm.writedocument", instance.getProcessId());
        Assert.assertEquals("first:second:third", instance.getCorrelationKey());
        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
    }
}

