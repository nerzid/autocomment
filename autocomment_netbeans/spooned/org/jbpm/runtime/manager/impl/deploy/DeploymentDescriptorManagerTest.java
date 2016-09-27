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


package org.jbpm.runtime.manager.impl.deploy;

import org.junit.Assert;
import org.kie.internal.runtime.conf.AuditMode;
import java.io.ByteArrayInputStream;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.KieServices;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.kie.internal.runtime.conf.NamedObjectModel;
import org.kie.internal.runtime.conf.ObjectModel;
import java.lang.reflect.ParameterizedType;
import org.kie.internal.runtime.conf.PersistenceMode;
import java.util.Random;
import org.kie.api.builder.ReleaseId;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import java.util.Set;
import org.junit.Test;
import java.lang.reflect.Type;
import java.util.UUID;
import javax.xml.bind.annotation.XmlElement;

public class DeploymentDescriptorManagerTest extends AbstractDeploymentDescriptorTest {
    private static final String SIMPLE_DRL = "package org.jbpm; " + ("\trule \"Start Hello1\"" + ("	  when" + ("	  then" + ("\t    System.out.println(\"Hello\");" + "	end"))));

    protected static final String ARTIFACT_ID = "test-module";

    protected static final String GROUP_ID = "org.jbpm.test";

    protected static final String VERSION = "1.0.0-SNAPSHOT";

    @Test
    public void testDefaultDeploymentDescriptor() {
        DeploymentDescriptorManager manager = new DeploymentDescriptorManager("org.jbpm.domain");
        DeploymentDescriptor descriptor = manager.getDefaultDescriptor();
        Assert.assertNotNull(descriptor);
        Assert.assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
        Assert.assertEquals("org.jbpm.domain", descriptor.getAuditPersistenceUnit());
        Assert.assertEquals(AuditMode.JPA, descriptor.getAuditMode());
        Assert.assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
        Assert.assertEquals(RuntimeStrategy.SINGLETON, descriptor.getRuntimeStrategy());
        Assert.assertEquals(0, descriptor.getMarshallingStrategies().size());
        Assert.assertEquals(0, descriptor.getConfiguration().size());
        Assert.assertEquals(0, descriptor.getEnvironmentEntries().size());
        Assert.assertEquals(0, descriptor.getEventListeners().size());
        Assert.assertEquals(0, descriptor.getGlobals().size());
        Assert.assertEquals(0, descriptor.getTaskEventListeners().size());
        Assert.assertEquals(0, descriptor.getWorkItemHandlers().size());
    }

    @Test
    public void testDefaultDeploymentDescriptorFromClasspath() {
        System.setProperty("org.kie.deployment.desc.location", "classpath:/deployment/deployment-descriptor-defaults-and-ms.xml");
        DeploymentDescriptorManager manager = new DeploymentDescriptorManager("org.jbpm.domain");
        DeploymentDescriptor descriptor = manager.getDefaultDescriptor();
        Assert.assertNotNull(descriptor);
        Assert.assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
        Assert.assertEquals("org.jbpm.domain", descriptor.getAuditPersistenceUnit());
        Assert.assertEquals(AuditMode.JPA, descriptor.getAuditMode());
        Assert.assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
        Assert.assertEquals(RuntimeStrategy.SINGLETON, descriptor.getRuntimeStrategy());
        Assert.assertEquals(1, descriptor.getMarshallingStrategies().size());
        Assert.assertEquals(0, descriptor.getConfiguration().size());
        Assert.assertEquals(0, descriptor.getEnvironmentEntries().size());
        Assert.assertEquals(0, descriptor.getEventListeners().size());
        Assert.assertEquals(0, descriptor.getGlobals().size());
        Assert.assertEquals(0, descriptor.getTaskEventListeners().size());
        Assert.assertEquals(0, descriptor.getWorkItemHandlers().size());
    }

    @Test
    public void testDefaultDeploymentDescriptorFromFile() {
        System.setProperty("org.kie.deployment.desc.location", "file:src/test/resources/deployment/deployment-descriptor-defaults-and-ms.xml");
        DeploymentDescriptorManager manager = new DeploymentDescriptorManager("org.jbpm.domain");
        DeploymentDescriptor descriptor = manager.getDefaultDescriptor();
        Assert.assertNotNull(descriptor);
        Assert.assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
        Assert.assertEquals("org.jbpm.domain", descriptor.getAuditPersistenceUnit());
        Assert.assertEquals(AuditMode.JPA, descriptor.getAuditMode());
        Assert.assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
        Assert.assertEquals(RuntimeStrategy.SINGLETON, descriptor.getRuntimeStrategy());
        Assert.assertEquals(1, descriptor.getMarshallingStrategies().size());
        Assert.assertEquals(0, descriptor.getConfiguration().size());
        Assert.assertEquals(0, descriptor.getEnvironmentEntries().size());
        Assert.assertEquals(0, descriptor.getEventListeners().size());
        Assert.assertEquals(0, descriptor.getGlobals().size());
        Assert.assertEquals(0, descriptor.getTaskEventListeners().size());
        Assert.assertEquals(0, descriptor.getWorkItemHandlers().size());
    }

    @Test
    public void testDeploymentDescriptorFromKieContainerNoDescInKjar() {
        DeploymentDescriptorManager manager = new DeploymentDescriptorManager("org.jbpm.domain");
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(DeploymentDescriptorManagerTest.GROUP_ID, DeploymentDescriptorManagerTest.ARTIFACT_ID, DeploymentDescriptorManagerTest.VERSION);
        Map<String, String> resources = new HashMap<String, String>();
        resources.put("src/main/resources/simple.drl", DeploymentDescriptorManagerTest.SIMPLE_DRL);
        InternalKieModule kJar1 = createKieJar(ks, releaseId, resources);
        installKjar(releaseId, kJar1);
        KieContainer kieContainer = ks.newKieContainer(releaseId);
        Assert.assertNotNull(kieContainer);
        List<DeploymentDescriptor> descriptorHierarchy = manager.getDeploymentDescriptorHierarchy(kieContainer);
        Assert.assertNotNull(descriptorHierarchy);
        Assert.assertEquals(1, descriptorHierarchy.size());
        DeploymentDescriptor descriptor = descriptorHierarchy.get(0);
        Assert.assertNotNull(descriptor);
        Assert.assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
        Assert.assertEquals("org.jbpm.domain", descriptor.getAuditPersistenceUnit());
        Assert.assertEquals(AuditMode.JPA, descriptor.getAuditMode());
        Assert.assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
        Assert.assertEquals(RuntimeStrategy.SINGLETON, descriptor.getRuntimeStrategy());
        Assert.assertEquals(0, descriptor.getMarshallingStrategies().size());
        Assert.assertEquals(0, descriptor.getConfiguration().size());
        Assert.assertEquals(0, descriptor.getEnvironmentEntries().size());
        Assert.assertEquals(0, descriptor.getEventListeners().size());
        Assert.assertEquals(0, descriptor.getGlobals().size());
        Assert.assertEquals(0, descriptor.getTaskEventListeners().size());
        Assert.assertEquals(0, descriptor.getWorkItemHandlers().size());
    }

    @Test
    public void testDeploymentDescriptorFromKieContainer() {
        DeploymentDescriptorManager manager = new DeploymentDescriptorManager("org.jbpm.domain");
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(DeploymentDescriptorManagerTest.GROUP_ID, DeploymentDescriptorManagerTest.ARTIFACT_ID, DeploymentDescriptorManagerTest.VERSION);
        DeploymentDescriptor descriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
        descriptor.getBuilder().runtimeStrategy(RuntimeStrategy.PER_PROCESS_INSTANCE);
        Map<String, String> resources = new HashMap<String, String>();
        resources.put("src/main/resources/simple.drl", DeploymentDescriptorManagerTest.SIMPLE_DRL);
        resources.put("src/main/resources/META-INF/kie-deployment-descriptor.xml", descriptor.toXml());
        InternalKieModule kJar1 = createKieJar(ks, releaseId, resources);
        installKjar(releaseId, kJar1);
        KieContainer kieContainer = ks.newKieContainer(releaseId);
        Assert.assertNotNull(kieContainer);
        List<DeploymentDescriptor> descriptorHierarchy = manager.getDeploymentDescriptorHierarchy(kieContainer);
        Assert.assertNotNull(descriptorHierarchy);
        Assert.assertEquals(2, descriptorHierarchy.size());
        descriptor = descriptorHierarchy.get(0);
        Assert.assertNotNull(descriptor);
        Assert.assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
        Assert.assertEquals("org.jbpm.domain", descriptor.getAuditPersistenceUnit());
        Assert.assertEquals(AuditMode.JPA, descriptor.getAuditMode());
        Assert.assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
        Assert.assertEquals(RuntimeStrategy.PER_PROCESS_INSTANCE, descriptor.getRuntimeStrategy());
        Assert.assertEquals(0, descriptor.getMarshallingStrategies().size());
        Assert.assertEquals(0, descriptor.getConfiguration().size());
        Assert.assertEquals(0, descriptor.getEnvironmentEntries().size());
        Assert.assertEquals(0, descriptor.getEventListeners().size());
        Assert.assertEquals(0, descriptor.getGlobals().size());
        Assert.assertEquals(0, descriptor.getTaskEventListeners().size());
        Assert.assertEquals(0, descriptor.getWorkItemHandlers().size());
        descriptor = descriptorHierarchy.get(1);
        Assert.assertNotNull(descriptor);
        Assert.assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
        Assert.assertEquals("org.jbpm.domain", descriptor.getAuditPersistenceUnit());
        Assert.assertEquals(AuditMode.JPA, descriptor.getAuditMode());
        Assert.assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
        Assert.assertEquals(RuntimeStrategy.SINGLETON, descriptor.getRuntimeStrategy());
        Assert.assertEquals(0, descriptor.getMarshallingStrategies().size());
        Assert.assertEquals(0, descriptor.getConfiguration().size());
        Assert.assertEquals(0, descriptor.getEnvironmentEntries().size());
        Assert.assertEquals(0, descriptor.getEventListeners().size());
        Assert.assertEquals(0, descriptor.getGlobals().size());
        Assert.assertEquals(0, descriptor.getTaskEventListeners().size());
        Assert.assertEquals(0, descriptor.getWorkItemHandlers().size());
    }

    @Test
    public void testDeploymentDescriptorFromKieContainerWithDependency() {
        DeploymentDescriptorManager manager = new DeploymentDescriptorManager("org.jbpm.domain");
        KieServices ks = KieServices.Factory.get();
        // create dependency kjar
        ReleaseId releaseIdDep = ks.newReleaseId(DeploymentDescriptorManagerTest.GROUP_ID, "dependency-data", DeploymentDescriptorManagerTest.VERSION);
        DeploymentDescriptor descriptorDep = new DeploymentDescriptorImpl("org.jbpm.domain");
        descriptorDep.getBuilder().runtimeStrategy(RuntimeStrategy.PER_PROCESS_INSTANCE).auditPersistenceUnit("org.jbpm.audit");
        Map<String, String> resourcesDep = new HashMap<String, String>();
        resourcesDep.put("src/main/resources/simple.drl", DeploymentDescriptorManagerTest.SIMPLE_DRL);
        resourcesDep.put("src/main/resources/META-INF/kie-deployment-descriptor.xml", descriptorDep.toXml());
        InternalKieModule kJarDep = createKieJar(ks, releaseIdDep, resourcesDep);
        installKjar(releaseIdDep, kJarDep);
        // create first kjar that will have dependency to another
        ReleaseId releaseId = ks.newReleaseId(DeploymentDescriptorManagerTest.GROUP_ID, DeploymentDescriptorManagerTest.ARTIFACT_ID, DeploymentDescriptorManagerTest.VERSION);
        DeploymentDescriptor descriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
        descriptor.getBuilder().runtimeStrategy(RuntimeStrategy.PER_PROCESS_INSTANCE);
        Map<String, String> resources = new HashMap<String, String>();
        resources.put("src/main/resources/simple.drl", DeploymentDescriptorManagerTest.SIMPLE_DRL);
        resources.put("src/main/resources/META-INF/kie-deployment-descriptor.xml", descriptor.toXml());
        InternalKieModule kJar1 = createKieJar(ks, releaseId, resources, releaseIdDep);
        installKjar(releaseId, kJar1);
        KieContainer kieContainer = ks.newKieContainer(releaseId);
        Assert.assertNotNull(kieContainer);
        List<DeploymentDescriptor> descriptorHierarchy = manager.getDeploymentDescriptorHierarchy(kieContainer);
        Assert.assertNotNull(descriptorHierarchy);
        Assert.assertEquals(3, descriptorHierarchy.size());
        descriptor = descriptorHierarchy.get(0);
        Assert.assertNotNull(descriptor);
        Assert.assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
        Assert.assertEquals("org.jbpm.domain", descriptor.getAuditPersistenceUnit());
        Assert.assertEquals(AuditMode.JPA, descriptor.getAuditMode());
        Assert.assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
        Assert.assertEquals(RuntimeStrategy.PER_PROCESS_INSTANCE, descriptor.getRuntimeStrategy());
        Assert.assertEquals(0, descriptor.getMarshallingStrategies().size());
        Assert.assertEquals(0, descriptor.getConfiguration().size());
        Assert.assertEquals(0, descriptor.getEnvironmentEntries().size());
        Assert.assertEquals(0, descriptor.getEventListeners().size());
        Assert.assertEquals(0, descriptor.getGlobals().size());
        Assert.assertEquals(0, descriptor.getTaskEventListeners().size());
        Assert.assertEquals(0, descriptor.getWorkItemHandlers().size());
        descriptor = descriptorHierarchy.get(1);
        Assert.assertNotNull(descriptor);
        Assert.assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
        Assert.assertEquals("org.jbpm.audit", descriptor.getAuditPersistenceUnit());
        Assert.assertEquals(AuditMode.JPA, descriptor.getAuditMode());
        Assert.assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
        Assert.assertEquals(RuntimeStrategy.PER_PROCESS_INSTANCE, descriptor.getRuntimeStrategy());
        Assert.assertEquals(0, descriptor.getMarshallingStrategies().size());
        Assert.assertEquals(0, descriptor.getConfiguration().size());
        Assert.assertEquals(0, descriptor.getEnvironmentEntries().size());
        Assert.assertEquals(0, descriptor.getEventListeners().size());
        Assert.assertEquals(0, descriptor.getGlobals().size());
        Assert.assertEquals(0, descriptor.getTaskEventListeners().size());
        Assert.assertEquals(0, descriptor.getWorkItemHandlers().size());
        descriptor = descriptorHierarchy.get(2);
        Assert.assertNotNull(descriptor);
        Assert.assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
        Assert.assertEquals("org.jbpm.domain", descriptor.getAuditPersistenceUnit());
        Assert.assertEquals(AuditMode.JPA, descriptor.getAuditMode());
        Assert.assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
        Assert.assertEquals(RuntimeStrategy.SINGLETON, descriptor.getRuntimeStrategy());
        Assert.assertEquals(0, descriptor.getMarshallingStrategies().size());
        Assert.assertEquals(0, descriptor.getConfiguration().size());
        Assert.assertEquals(0, descriptor.getEnvironmentEntries().size());
        Assert.assertEquals(0, descriptor.getEventListeners().size());
        Assert.assertEquals(0, descriptor.getGlobals().size());
        Assert.assertEquals(0, descriptor.getTaskEventListeners().size());
        Assert.assertEquals(0, descriptor.getWorkItemHandlers().size());
    }

    @Test
    public void testDeploymentDescriptorFromKieContainerWithDependencyMerged() {
        DeploymentDescriptorManager manager = new DeploymentDescriptorManager("org.jbpm.domain");
        KieServices ks = KieServices.Factory.get();
        // create dependency kjar
        ReleaseId releaseIdDep = ks.newReleaseId(DeploymentDescriptorManagerTest.GROUP_ID, "dependency-data", DeploymentDescriptorManagerTest.VERSION);
        DeploymentDescriptor descriptorDep = new DeploymentDescriptorImpl("org.jbpm.domain");
        descriptorDep.getBuilder().runtimeStrategy(RuntimeStrategy.PER_PROCESS_INSTANCE).auditPersistenceUnit("org.jbpm.audit").addGlobal(new NamedObjectModel("service", "org.jbpm.global.Service"));
        Map<String, String> resourcesDep = new HashMap<String, String>();
        resourcesDep.put("src/main/resources/simple.drl", DeploymentDescriptorManagerTest.SIMPLE_DRL);
        resourcesDep.put("src/main/resources/META-INF/kie-deployment-descriptor.xml", descriptorDep.toXml());
        InternalKieModule kJarDep = createKieJar(ks, releaseIdDep, resourcesDep);
        installKjar(releaseIdDep, kJarDep);
        // create first kjar that will have dependency to another
        ReleaseId releaseId = ks.newReleaseId(DeploymentDescriptorManagerTest.GROUP_ID, DeploymentDescriptorManagerTest.ARTIFACT_ID, DeploymentDescriptorManagerTest.VERSION);
        DeploymentDescriptor descriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
        descriptor.getBuilder().runtimeStrategy(RuntimeStrategy.PER_PROCESS_INSTANCE);
        Map<String, String> resources = new HashMap<String, String>();
        resources.put("src/main/resources/simple.drl", DeploymentDescriptorManagerTest.SIMPLE_DRL);
        resources.put("src/main/resources/META-INF/kie-deployment-descriptor.xml", descriptor.toXml());
        InternalKieModule kJar1 = createKieJar(ks, releaseId, resources, releaseIdDep);
        installKjar(releaseId, kJar1);
        KieContainer kieContainer = ks.newKieContainer(releaseId);
        Assert.assertNotNull(kieContainer);
        List<DeploymentDescriptor> descriptorHierarchy = manager.getDeploymentDescriptorHierarchy(kieContainer);
        Assert.assertNotNull(descriptorHierarchy);
        Assert.assertEquals(3, descriptorHierarchy.size());
        descriptor = descriptorHierarchy.get(0);
        Assert.assertNotNull(descriptor);
        Assert.assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
        Assert.assertEquals("org.jbpm.domain", descriptor.getAuditPersistenceUnit());
        Assert.assertEquals(AuditMode.JPA, descriptor.getAuditMode());
        Assert.assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
        Assert.assertEquals(RuntimeStrategy.PER_PROCESS_INSTANCE, descriptor.getRuntimeStrategy());
        Assert.assertEquals(0, descriptor.getMarshallingStrategies().size());
        Assert.assertEquals(0, descriptor.getConfiguration().size());
        Assert.assertEquals(0, descriptor.getEnvironmentEntries().size());
        Assert.assertEquals(0, descriptor.getEventListeners().size());
        Assert.assertEquals(0, descriptor.getGlobals().size());
        Assert.assertEquals(0, descriptor.getTaskEventListeners().size());
        Assert.assertEquals(0, descriptor.getWorkItemHandlers().size());
        descriptor = descriptorHierarchy.get(1);
        Assert.assertNotNull(descriptor);
        Assert.assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
        Assert.assertEquals("org.jbpm.audit", descriptor.getAuditPersistenceUnit());
        Assert.assertEquals(AuditMode.JPA, descriptor.getAuditMode());
        Assert.assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
        Assert.assertEquals(RuntimeStrategy.PER_PROCESS_INSTANCE, descriptor.getRuntimeStrategy());
        Assert.assertEquals(0, descriptor.getMarshallingStrategies().size());
        Assert.assertEquals(0, descriptor.getConfiguration().size());
        Assert.assertEquals(0, descriptor.getEnvironmentEntries().size());
        Assert.assertEquals(0, descriptor.getEventListeners().size());
        Assert.assertEquals(1, descriptor.getGlobals().size());
        Assert.assertEquals(0, descriptor.getTaskEventListeners().size());
        Assert.assertEquals(0, descriptor.getWorkItemHandlers().size());
        descriptor = descriptorHierarchy.get(2);
        Assert.assertNotNull(descriptor);
        Assert.assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
        Assert.assertEquals("org.jbpm.domain", descriptor.getAuditPersistenceUnit());
        Assert.assertEquals(AuditMode.JPA, descriptor.getAuditMode());
        Assert.assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
        Assert.assertEquals(RuntimeStrategy.SINGLETON, descriptor.getRuntimeStrategy());
        Assert.assertEquals(0, descriptor.getMarshallingStrategies().size());
        Assert.assertEquals(0, descriptor.getConfiguration().size());
        Assert.assertEquals(0, descriptor.getEnvironmentEntries().size());
        Assert.assertEquals(0, descriptor.getEventListeners().size());
        Assert.assertEquals(0, descriptor.getGlobals().size());
        Assert.assertEquals(0, descriptor.getTaskEventListeners().size());
        Assert.assertEquals(0, descriptor.getWorkItemHandlers().size());
        DeploymentDescriptorMerger merger = new DeploymentDescriptorMerger();
        DeploymentDescriptor outcome = merger.merge(descriptorHierarchy, MergeMode.MERGE_COLLECTIONS);
        Assert.assertNotNull(outcome);
        Assert.assertEquals("org.jbpm.domain", outcome.getPersistenceUnit());
        Assert.assertEquals("org.jbpm.domain", outcome.getAuditPersistenceUnit());
        Assert.assertEquals(AuditMode.JPA, outcome.getAuditMode());
        Assert.assertEquals(PersistenceMode.JPA, outcome.getPersistenceMode());
        Assert.assertEquals(RuntimeStrategy.PER_PROCESS_INSTANCE, outcome.getRuntimeStrategy());
        Assert.assertEquals(0, outcome.getMarshallingStrategies().size());
        Assert.assertEquals(0, outcome.getConfiguration().size());
        Assert.assertEquals(0, outcome.getEnvironmentEntries().size());
        Assert.assertEquals(0, outcome.getEventListeners().size());
        Assert.assertEquals(1, outcome.getGlobals().size());
        Assert.assertEquals(0, outcome.getTaskEventListeners().size());
        Assert.assertEquals(0, outcome.getWorkItemHandlers().size());
    }

    @Test
    public void roundTripDescriptorMarshallingTest() throws Exception {
        DeploymentDescriptorImpl depDescImpl = new DeploymentDescriptorImpl();
        List<Field> fieldsToFill = new LinkedList<Field>();
        for (Field field : DeploymentDescriptorImpl.class.getDeclaredFields()) {
            if ((field.getAnnotation(XmlElement.class)) != null) {
                fieldsToFill.add(field);
            } 
        }
        for (Field field : fieldsToFill) {
            field.setAccessible(true);
            Class fieldType = field.getType();
            if (fieldType.equals(String.class)) {
                field.set(depDescImpl, DeploymentDescriptorManagerTest.getStringVal());
            } else if (fieldType.equals(Boolean.class)) {
                field.set(depDescImpl, true);
            } else if (fieldType.equals(PersistenceMode.class)) {
                field.set(depDescImpl, PersistenceMode.NONE);
            } else if (fieldType.equals(AuditMode.class)) {
                field.set(depDescImpl, AuditMode.JMS);
            } else if (fieldType.equals(RuntimeStrategy.class)) {
                field.set(depDescImpl, RuntimeStrategy.PER_PROCESS_INSTANCE);
            } else if (Set.class.isAssignableFrom(fieldType)) {
                Type genType = field.getGenericType();
                Type genParamType = ((ParameterizedType) (genType)).getActualTypeArguments()[0];
                Set val = new HashSet();
                if (genParamType.equals(String.class)) {
                    val.add(DeploymentDescriptorManagerTest.getStringVal());
                } else if (genParamType.equals(ObjectModel.class)) {
                    val.add(DeploymentDescriptorManagerTest.getObjectModelParameter(DeploymentDescriptorManagerTest.getStringVal(), false));
                } else if (genParamType.equals(NamedObjectModel.class)) {
                    val.add(DeploymentDescriptorManagerTest.getObjectModelParameter(DeploymentDescriptorManagerTest.getStringVal(), true));
                } 
                field.set(depDescImpl, val);
            } 
        }
        String depDescXml = DeploymentDescriptorIO.toXml(depDescImpl);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(depDescXml.getBytes());
        DeploymentDescriptor copyDepDescImpl = DeploymentDescriptorIO.fromXml(inputStream);
        new org.kie.test.util.compare.ComparePair(depDescImpl, copyDepDescImpl).addNullFields("mappedRoles").useFields().compare();
    }

    private static String getStringVal() {
        String val = UUID.randomUUID().toString();
        return val.substring(0, val.indexOf("-"));
    }

    private static Random random = new Random();

    private static ObjectModel getObjectModelParameter(String resolver, boolean named) {
        if (named) {
            return new NamedObjectModel(resolver, UUID.randomUUID().toString(), Integer.toString(DeploymentDescriptorManagerTest.random.nextInt(100000)));
        } else {
            return new ObjectModel(resolver, UUID.randomUUID().toString(), Integer.toString(DeploymentDescriptorManagerTest.random.nextInt(100000)));
        }
    }
}

