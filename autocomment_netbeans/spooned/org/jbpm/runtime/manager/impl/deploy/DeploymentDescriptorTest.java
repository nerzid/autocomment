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

import java.util.ArrayList;
import org.junit.Assert;
import java.io.ByteArrayInputStream;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import java.io.InputStream;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.kie.internal.runtime.conf.ObjectModel;
import org.junit.Test;

public class DeploymentDescriptorTest {
    private static final Logger logger = LoggerFactory.getLogger(DeploymentDescriptorTest.class);

    @Test
    public void testWriteDeploymentDescriptorXml() {
        DeploymentDescriptor descriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
        descriptor.getBuilder().addMarshalingStrategy(new ObjectModel("org.jbpm.testCustomStrategy", new Object[]{ new ObjectModel("java.lang.String", new Object[]{ "param1" }) , "param2" })).addRequiredRole("experts");
        String deploymentDescriptorXml = descriptor.toXml();
        Assert.assertNotNull(deploymentDescriptorXml);
        DeploymentDescriptorTest.logger.info(deploymentDescriptorXml);
        ByteArrayInputStream stream = new ByteArrayInputStream(deploymentDescriptorXml.getBytes());
        DeploymentDescriptor fromXml = DeploymentDescriptorIO.fromXml(stream);
        Assert.assertNotNull(fromXml);
        Assert.assertEquals("org.jbpm.domain", fromXml.getPersistenceUnit());
        Assert.assertEquals("org.jbpm.domain", fromXml.getAuditPersistenceUnit());
        Assert.assertEquals(AuditMode.JPA, fromXml.getAuditMode());
        Assert.assertEquals(PersistenceMode.JPA, fromXml.getPersistenceMode());
        Assert.assertEquals(RuntimeStrategy.SINGLETON, fromXml.getRuntimeStrategy());
        Assert.assertEquals(1, fromXml.getMarshallingStrategies().size());
        Assert.assertEquals(0, fromXml.getConfiguration().size());
        Assert.assertEquals(0, fromXml.getEnvironmentEntries().size());
        Assert.assertEquals(0, fromXml.getEventListeners().size());
        Assert.assertEquals(0, fromXml.getGlobals().size());
        Assert.assertEquals(0, fromXml.getTaskEventListeners().size());
        Assert.assertEquals(0, fromXml.getWorkItemHandlers().size());
        Assert.assertEquals(1, fromXml.getRequiredRoles().size());
    }

    @Test
    public void testReadDeploymentDescriptorFromXml() throws Exception {
        InputStream input = DeploymentDescriptorTest.this.getClass().getResourceAsStream("/deployment/deployment-descriptor-defaults.xml");
        DeploymentDescriptor descriptor = DeploymentDescriptorIO.fromXml(input);
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
        Assert.assertEquals(0, descriptor.getRequiredRoles().size());
    }

    @Test
    public void testReadDeploymentDescriptorMSFromXml() throws Exception {
        InputStream input = DeploymentDescriptorTest.this.getClass().getResourceAsStream("/deployment/deployment-descriptor-defaults-and-ms.xml");
        DeploymentDescriptor descriptor = DeploymentDescriptorIO.fromXml(input);
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
        Assert.assertEquals(1, descriptor.getRequiredRoles().size());
    }

    @Test
    public void testReadPartialDeploymentDescriptorFromXml() throws Exception {
        InputStream input = DeploymentDescriptorTest.this.getClass().getResourceAsStream("/deployment/partial-deployment-descriptor.xml");
        DeploymentDescriptor descriptor = DeploymentDescriptorIO.fromXml(input);
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
        Assert.assertEquals(0, descriptor.getRequiredRoles().size());
    }

    @Test
    public void testCreateDeploymentDescriptorWithSetters() {
        DeploymentDescriptorImpl descriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
        descriptor.setAuditMode(AuditMode.JMS);
        descriptor.setEnvironmentEntries(null);
        List<ObjectModel> marshallingStrategies = new ArrayList<ObjectModel>();
        marshallingStrategies.add(new ObjectModel("org.jbpm.testCustomStrategy", new Object[]{ new ObjectModel("java.lang.String", new Object[]{ "param1" }) , "param2" }));
        descriptor.setMarshallingStrategies(marshallingStrategies);
        List<String> roles = new ArrayList<String>();
        roles.add("experts");
        descriptor.setRequiredRoles(roles);
        Assert.assertNotNull(descriptor);
        Assert.assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
        Assert.assertEquals("org.jbpm.domain", descriptor.getAuditPersistenceUnit());
        Assert.assertEquals(AuditMode.JMS, descriptor.getAuditMode());
        Assert.assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
        Assert.assertEquals(RuntimeStrategy.SINGLETON, descriptor.getRuntimeStrategy());
        Assert.assertEquals(1, descriptor.getMarshallingStrategies().size());
        Assert.assertEquals(0, descriptor.getConfiguration().size());
        Assert.assertEquals(0, descriptor.getEnvironmentEntries().size());
        Assert.assertEquals(0, descriptor.getEventListeners().size());
        Assert.assertEquals(0, descriptor.getGlobals().size());
        Assert.assertEquals(0, descriptor.getTaskEventListeners().size());
        Assert.assertEquals(0, descriptor.getWorkItemHandlers().size());
        Assert.assertEquals(1, descriptor.getRequiredRoles().size());
    }

    @Test
    public void testPrintDescriptor() {
        DeploymentDescriptor descriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
        descriptor.getBuilder().addWorkItemHandler(new org.kie.internal.runtime.conf.NamedObjectModel("mvel", "Log", "new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler()")).addWorkItemHandler(new org.kie.internal.runtime.conf.NamedObjectModel("mvel", "WebService", "new org.jbpm.process.workitem.webservice.WebServiceWorkItemHandler(ksession)")).addWorkItemHandler(new org.kie.internal.runtime.conf.NamedObjectModel("mvel", "Rest", "new org.jbpm.process.workitem.rest.RESTWorkItemHandler()")).addWorkItemHandler(new org.kie.internal.runtime.conf.NamedObjectModel("mvel", "Service Task", "new org.jbpm.process.workitem.bpmn2.ServiceTaskHandler(ksession)"));
        DeploymentDescriptorTest.logger.debug(descriptor.toXml());
    }

    @Test
    public void testWriteDeploymentDescriptorXmlWithDuplicateNamedObjects() {
        DeploymentDescriptor descriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
        descriptor.getBuilder().addWorkItemHandler(new org.kie.internal.runtime.conf.NamedObjectModel("mvel", "Log", "new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler()")).addWorkItemHandler(new org.kie.internal.runtime.conf.NamedObjectModel("mvel", "Log", "new org.jbpm.process.instance.impl.demo.CustomSystemOutWorkItemHandler()")).addRequiredRole("experts");
        String deploymentDescriptorXml = descriptor.toXml();
        Assert.assertNotNull(deploymentDescriptorXml);
        DeploymentDescriptorTest.logger.info(deploymentDescriptorXml);
        ByteArrayInputStream stream = new ByteArrayInputStream(deploymentDescriptorXml.getBytes());
        DeploymentDescriptor fromXml = DeploymentDescriptorIO.fromXml(stream);
        Assert.assertNotNull(fromXml);
        Assert.assertEquals("org.jbpm.domain", fromXml.getPersistenceUnit());
        Assert.assertEquals("org.jbpm.domain", fromXml.getAuditPersistenceUnit());
        Assert.assertEquals(AuditMode.JPA, fromXml.getAuditMode());
        Assert.assertEquals(PersistenceMode.JPA, fromXml.getPersistenceMode());
        Assert.assertEquals(RuntimeStrategy.SINGLETON, fromXml.getRuntimeStrategy());
        Assert.assertEquals(0, fromXml.getMarshallingStrategies().size());
        Assert.assertEquals(0, fromXml.getConfiguration().size());
        Assert.assertEquals(0, fromXml.getEnvironmentEntries().size());
        Assert.assertEquals(0, fromXml.getEventListeners().size());
        Assert.assertEquals(0, fromXml.getGlobals().size());
        Assert.assertEquals(0, fromXml.getTaskEventListeners().size());
        Assert.assertEquals(1, fromXml.getWorkItemHandlers().size());
        Assert.assertEquals(1, fromXml.getRequiredRoles().size());
    }

    @Test
    public void testCreateDeploymentDescriptorWithPrefixedRoles() {
        DeploymentDescriptorImpl descriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
        descriptor.setAuditMode(AuditMode.JMS);
        descriptor.setEnvironmentEntries(null);
        List<ObjectModel> marshallingStrategies = new ArrayList<ObjectModel>();
        marshallingStrategies.add(new ObjectModel("org.jbpm.testCustomStrategy", new Object[]{ new ObjectModel("java.lang.String", new Object[]{ "param1" }) , "param2" }));
        descriptor.setMarshallingStrategies(marshallingStrategies);
        List<String> roles = new ArrayList<String>();
        roles.add("view:managers");
        roles.add("execute:experts");
        roles.add("all:everyone");
        roles.add("employees");
        descriptor.setRequiredRoles(roles);
        Assert.assertNotNull(descriptor);
        Assert.assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
        Assert.assertEquals("org.jbpm.domain", descriptor.getAuditPersistenceUnit());
        Assert.assertEquals(AuditMode.JMS, descriptor.getAuditMode());
        Assert.assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
        Assert.assertEquals(RuntimeStrategy.SINGLETON, descriptor.getRuntimeStrategy());
        Assert.assertEquals(1, descriptor.getMarshallingStrategies().size());
        Assert.assertEquals(0, descriptor.getConfiguration().size());
        Assert.assertEquals(0, descriptor.getEnvironmentEntries().size());
        Assert.assertEquals(0, descriptor.getEventListeners().size());
        Assert.assertEquals(0, descriptor.getGlobals().size());
        Assert.assertEquals(0, descriptor.getTaskEventListeners().size());
        Assert.assertEquals(0, descriptor.getWorkItemHandlers().size());
        Assert.assertEquals(4, descriptor.getRequiredRoles().size());
        List<String> toVerify = descriptor.getRequiredRoles();
        Assert.assertEquals(4, toVerify.size());
        Assert.assertTrue(toVerify.contains("view:managers"));
        Assert.assertTrue(toVerify.contains("execute:experts"));
        Assert.assertTrue(toVerify.contains("all:everyone"));
        Assert.assertTrue(toVerify.contains("employees"));
        toVerify = descriptor.getRequiredRoles(DeploymentDescriptor.TYPE_ALL);
        Assert.assertEquals(4, toVerify.size());
        Assert.assertTrue(toVerify.contains("managers"));
        Assert.assertTrue(toVerify.contains("experts"));
        Assert.assertTrue(toVerify.contains("everyone"));
        Assert.assertTrue(toVerify.contains("employees"));
        toVerify = descriptor.getRequiredRoles(DeploymentDescriptor.TYPE_EXECUTE);
        Assert.assertEquals(2, toVerify.size());
        Assert.assertTrue(toVerify.contains("experts"));
        Assert.assertTrue(toVerify.contains("employees"));
        toVerify = descriptor.getRequiredRoles(DeploymentDescriptor.TYPE_VIEW);
        Assert.assertEquals(2, toVerify.size());
        Assert.assertTrue(toVerify.contains("managers"));
        Assert.assertTrue(toVerify.contains("employees"));
    }

    @Test
    public void testWriteDeploymentDescriptorXmlWithTransientElements() {
        DeploymentDescriptor descriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
        descriptor.getBuilder().addMarshalingStrategy(new TransientObjectModel("org.jbpm.testCustomStrategy", new Object[]{ new ObjectModel("java.lang.String", new Object[]{ "param1" }) , "param2" })).addWorkItemHandler(new TransientNamedObjectModel("mvel", "Log", "new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler()")).addRequiredRole("experts");
        String deploymentDescriptorXml = descriptor.toXml();
        Assert.assertNotNull(deploymentDescriptorXml);
        DeploymentDescriptorTest.logger.info(deploymentDescriptorXml);
        ByteArrayInputStream stream = new ByteArrayInputStream(deploymentDescriptorXml.getBytes());
        DeploymentDescriptor fromXml = DeploymentDescriptorIO.fromXml(stream);
        Assert.assertNotNull(fromXml);
        Assert.assertEquals("org.jbpm.domain", fromXml.getPersistenceUnit());
        Assert.assertEquals("org.jbpm.domain", fromXml.getAuditPersistenceUnit());
        Assert.assertEquals(AuditMode.JPA, fromXml.getAuditMode());
        Assert.assertEquals(PersistenceMode.JPA, fromXml.getPersistenceMode());
        Assert.assertEquals(RuntimeStrategy.SINGLETON, fromXml.getRuntimeStrategy());
        Assert.assertEquals(0, fromXml.getMarshallingStrategies().size());
        Assert.assertEquals(0, fromXml.getConfiguration().size());
        Assert.assertEquals(0, fromXml.getEnvironmentEntries().size());
        Assert.assertEquals(0, fromXml.getEventListeners().size());
        Assert.assertEquals(0, fromXml.getGlobals().size());
        Assert.assertEquals(0, fromXml.getTaskEventListeners().size());
        Assert.assertEquals(0, fromXml.getWorkItemHandlers().size());
        Assert.assertEquals(1, fromXml.getRequiredRoles().size());
    }

    @Test
    public void testEmptyDeploymentDescriptor() {
        DeploymentDescriptorImpl descriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
        descriptor.getBuilder().addMarshalingStrategy(new ObjectModel("org.jbpm.testCustomStrategy", new Object[]{ new ObjectModel("java.lang.String", new Object[]{ "param1" }) , "param2" })).addRequiredRole("experts");
        Assert.assertFalse(descriptor.isEmpty());
        InputStream input = DeploymentDescriptorTest.this.getClass().getResourceAsStream("/deployment/empty-descriptor.xml");
        DeploymentDescriptor fromXml = DeploymentDescriptorIO.fromXml(input);
        Assert.assertNotNull(fromXml);
        Assert.assertTrue(((DeploymentDescriptorImpl) (fromXml)).isEmpty());
        Assert.assertNull(fromXml.getPersistenceUnit());
        Assert.assertNull(fromXml.getAuditPersistenceUnit());
        Assert.assertEquals(AuditMode.JPA, fromXml.getAuditMode());
        Assert.assertEquals(PersistenceMode.JPA, fromXml.getPersistenceMode());
        Assert.assertEquals(RuntimeStrategy.SINGLETON, fromXml.getRuntimeStrategy());
        Assert.assertEquals(0, fromXml.getMarshallingStrategies().size());
        Assert.assertEquals(0, fromXml.getConfiguration().size());
        Assert.assertEquals(0, fromXml.getEnvironmentEntries().size());
        Assert.assertEquals(0, fromXml.getEventListeners().size());
        Assert.assertEquals(0, fromXml.getGlobals().size());
        Assert.assertEquals(0, fromXml.getTaskEventListeners().size());
        Assert.assertEquals(0, fromXml.getWorkItemHandlers().size());
        Assert.assertEquals(0, fromXml.getRequiredRoles().size());
    }
}

