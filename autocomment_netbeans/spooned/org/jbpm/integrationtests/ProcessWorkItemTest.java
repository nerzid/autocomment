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


package org.jbpm.integrationtests;

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Assert;
import java.util.Collection;
import java.util.HashMap;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.definition.KnowledgePackage;
import java.util.Map;
import org.jbpm.integrationtests.test.Person;
import java.io.Reader;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import java.io.StringReader;
import org.junit.Test;
import org.jbpm.integrationtests.handler.TestWorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.process.WorkflowProcessInstance;

public class ProcessWorkItemTest extends AbstractBaseTest {
    @Test
    public void testWorkItem() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Reader source = new StringReader(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ("<process xmlns=\"http://drools.org/drools-5.0/process\"\n" + ("         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + ("         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" + ("         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.actions\" package-name=\"org.drools\" version=\"1\" >\n" + ("\n" + ("  <header>\n" + ("    <variables>\n" + ("      <variable name=\"UserName\" >\n" + ("        <type name=\"org.drools.core.process.core.datatype.impl.type.StringDataType\" />\n" + ("        <value>John Doe</value>\n" + ("      </variable>\n" + ("      <variable name=\"Person\" >\n" + ("        <type name=\"org.drools.core.process.core.datatype.impl.type.ObjectDataType\" className=\"org.jbpm.integrationtests.test.Person\" />\n" + ("      </variable>\n" + ("      <variable name=\"MyObject\" >\n" + ("        <type name=\"org.drools.core.process.core.datatype.impl.type.StringDataType\" />\n" + ("      </variable>\n" + ("      <variable name=\"Number\" >\n" + ("        <type name=\"org.drools.core.process.core.datatype.impl.type.IntegerDataType\" />\n" + ("      </variable>\n" + ("    </variables>\n" + ("  </header>\n" + ("\n" + ("  <nodes>\n" + ("    <start id=\"1\" name=\"Start\" />\n" + ("    <workItem id=\"2\" name=\"HumanTask\" >\n" + ("      <work name=\"Human Task\" >\n" + ("        <parameter name=\"ActorId\" >\n" + ("          <type name=\"org.drools.core.process.core.datatype.impl.type.StringDataType\" />\n" + ("          <value>#{UserName}</value>\n" + ("        </parameter>\n" + ("        <parameter name=\"Content\" >\n" + ("          <type name=\"org.drools.core.process.core.datatype.impl.type.StringDataType\" />\n" + ("          <value>#{Person.name}</value>\n" + ("        </parameter>\n" + ("        <parameter name=\"TaskName\" >\n" + ("          <type name=\"org.drools.core.process.core.datatype.impl.type.StringDataType\" />\n" + ("          <value>Do something</value>\n" + ("        </parameter>\n" + ("        <parameter name=\"Priority\" >\n" + ("          <type name=\"org.drools.core.process.core.datatype.impl.type.StringDataType\" />\n" + ("        </parameter>\n" + ("        <parameter name=\"Comment\" >\n" + ("          <type name=\"org.drools.core.process.core.datatype.impl.type.StringDataType\" />\n" + ("        </parameter>\n" + ("        <parameter name=\"Attachment\" >\n" + ("          <type name=\"org.drools.core.process.core.datatype.impl.type.StringDataType\" />\n" + ("        </parameter>\n" + ("      </work>\n" + ("      <mapping type=\"in\" from=\"MyObject\" to=\"Attachment\" />" + ("      <mapping type=\"in\" from=\"Person.name\" to=\"Comment\" />" + ("      <mapping type=\"out\" from=\"Result\" to=\"MyObject\" />" + ("      <mapping type=\"out\" from=\"Result.length()\" to=\"Number\" />" + ("    </workItem>\n" + ("    <end id=\"3\" name=\"End\" />\n" + ("  </nodes>\n" + ("\n" + ("  <connections>\n" + ("    <connection from=\"1\" to=\"2\" />\n" + ("    <connection from=\"2\" to=\"3\" />\n" + ("  </connections>\n" + ("\n" + "</process>"))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))));
        kbuilder.add(ResourceFactory.newReaderResource(source), ResourceType.DRF);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("UserName", "John Doe");
        Person person = new Person();
        person.setName("John Doe");
        parameters.put("Person", person);
        WorkflowProcessInstance processInstance = ((WorkflowProcessInstance) (ksession.startProcess("org.drools.actions", parameters)));
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        WorkItem workItem = handler.getWorkItem();
        Assert.assertNotNull(workItem);
        Assert.assertEquals("John Doe", workItem.getParameter("ActorId"));
        Assert.assertEquals("John Doe", workItem.getParameter("Content"));
        Assert.assertEquals("John Doe", workItem.getParameter("Comment"));
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        parameters = new HashMap<String, Object>();
        parameters.put("UserName", "Jane Doe");
        parameters.put("MyObject", "SomeString");
        person = new Person();
        person.setName("Jane Doe");
        parameters.put("Person", person);
        processInstance = ((WorkflowProcessInstance) (ksession.startProcess("org.drools.actions", parameters)));
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        workItem = handler.getWorkItem();
        Assert.assertNotNull(workItem);
        Assert.assertEquals("Jane Doe", workItem.getParameter("ActorId"));
        Assert.assertEquals("SomeString", workItem.getParameter("Attachment"));
        Assert.assertEquals("Jane Doe", workItem.getParameter("Content"));
        Assert.assertEquals("Jane Doe", workItem.getParameter("Comment"));
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("Result", "SomeOtherString");
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), results);
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        Assert.assertEquals("SomeOtherString", processInstance.getVariable("MyObject"));
        Assert.assertEquals(15, processInstance.getVariable("Number"));
    }

    @Test
    public void testWorkItemImmediateCompletion() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Reader source = new StringReader(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ("<process xmlns=\"http://drools.org/drools-5.0/process\"\n" + ("         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + ("         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" + ("         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.actions\" package-name=\"org.drools\" version=\"1\" >\n" + ("\n" + ("  <header>\n" + ("    <variables>\n" + ("      <variable name=\"UserName\" >\n" + ("        <type name=\"org.drools.core.process.core.datatype.impl.type.StringDataType\" />\n" + ("        <value>John Doe</value>\n" + ("      </variable>\n" + ("      <variable name=\"Person\" >\n" + ("        <type name=\"org.drools.core.process.core.datatype.impl.type.ObjectDataType\" className=\"org.jbpm.integrationtests.test.Person\" />\n" + ("      </variable>\n" + ("      <variable name=\"MyObject\" >\n" + ("        <type name=\"org.drools.core.process.core.datatype.impl.type.StringDataType\" />\n" + ("      </variable>\n" + ("      <variable name=\"Number\" >\n" + ("        <type name=\"org.drools.core.process.core.datatype.impl.type.IntegerDataType\" />\n" + ("      </variable>\n" + ("    </variables>\n" + ("  </header>\n" + ("\n" + ("  <nodes>\n" + ("    <start id=\"1\" name=\"Start\" />\n" + ("    <workItem id=\"2\" name=\"HumanTask\" >\n" + ("      <work name=\"Human Task\" >\n" + ("        <parameter name=\"ActorId\" >\n" + ("          <type name=\"org.drools.core.process.core.datatype.impl.type.StringDataType\" />\n" + ("          <value>#{UserName}</value>\n" + ("        </parameter>\n" + ("        <parameter name=\"Content\" >\n" + ("          <type name=\"org.drools.core.process.core.datatype.impl.type.StringDataType\" />\n" + ("          <value>#{Person.name}</value>\n" + ("        </parameter>\n" + ("        <parameter name=\"TaskName\" >\n" + ("          <type name=\"org.drools.core.process.core.datatype.impl.type.StringDataType\" />\n" + ("          <value>Do something</value>\n" + ("        </parameter>\n" + ("        <parameter name=\"Priority\" >\n" + ("          <type name=\"org.drools.core.process.core.datatype.impl.type.StringDataType\" />\n" + ("        </parameter>\n" + ("        <parameter name=\"Comment\" >\n" + ("          <type name=\"org.drools.core.process.core.datatype.impl.type.StringDataType\" />\n" + ("        </parameter>\n" + ("        <parameter name=\"Attachment\" >\n" + ("          <type name=\"org.drools.core.process.core.datatype.impl.type.StringDataType\" />\n" + ("        </parameter>\n" + ("      </work>\n" + ("      <mapping type=\"in\" from=\"MyObject\" to=\"Attachment\" />" + ("      <mapping type=\"in\" from=\"Person.name\" to=\"Comment\" />" + ("      <mapping type=\"out\" from=\"Result\" to=\"MyObject\" />" + ("      <mapping type=\"out\" from=\"Result.length()\" to=\"Number\" />" + ("    </workItem>\n" + ("    <end id=\"3\" name=\"End\" />\n" + ("  </nodes>\n" + ("\n" + ("  <connections>\n" + ("    <connection from=\"1\" to=\"2\" />\n" + ("    <connection from=\"2\" to=\"3\" />\n" + ("  </connections>\n" + ("\n" + "</process>"))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))));
        kbuilder.add(ResourceFactory.newReaderResource(source), ResourceType.DRF);
        Collection<KnowledgePackage> kpkgs = kbuilder.getKnowledgePackages();
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kpkgs);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ProcessWorkItemTest.ImmediateTestWorkItemHandler handler = new ProcessWorkItemTest.ImmediateTestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("UserName", "John Doe");
        Person person = new Person();
        person.setName("John Doe");
        parameters.put("Person", person);
        WorkflowProcessInstance processInstance = ((WorkflowProcessInstance) (ksession.startProcess("org.drools.actions", parameters)));
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    private static class ImmediateTestWorkItemHandler implements WorkItemHandler {
        public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
            manager.completeWorkItem(workItem.getId(), null);
        }

        public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        }
    }
}

