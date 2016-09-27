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
import java.util.Arrays;
import org.junit.Assert;
import java.util.Collection;
import java.util.HashMap;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import java.util.Map;
import org.kie.api.runtime.process.ProcessInstance;
import java.io.Reader;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import java.io.StringReader;
import org.junit.Test;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;

public class ProcessSubProcessTest extends AbstractBaseTest {
    @Test
    public void testSubProcess() throws Exception {
        StatefulKnowledgeSession workingMemory = ProcessSubProcessTest.createStatefulKnowledgeSessionFromRule(true);
        ProcessInstance processInstance = ((ProcessInstance) (workingMemory.startProcess("com.sample.ruleflow")));
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        Assert.assertEquals(2, workingMemory.getProcessInstances().size());
        workingMemory.insert(new org.jbpm.integrationtests.test.Person());
        workingMemory.fireAllRules();
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        Assert.assertEquals(0, workingMemory.getProcessInstances().size());
    }

    @Test
    public void testSubProcessCancel() throws Exception {
        StatefulKnowledgeSession workingMemory = ProcessSubProcessTest.createStatefulKnowledgeSessionFromRule(true);
        org.jbpm.process.instance.ProcessInstance processInstance = ((org.jbpm.process.instance.ProcessInstance) (workingMemory.startProcess("com.sample.ruleflow")));
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        Assert.assertEquals(2, workingMemory.getProcessInstances().size());
        processInstance.setState(ProcessInstance.STATE_ABORTED);
        Assert.assertEquals(1, workingMemory.getProcessInstances().size());
    }

    @Test
    public void testIndependentSubProcessCancel() throws Exception {
        StatefulKnowledgeSession workingMemory = ProcessSubProcessTest.createStatefulKnowledgeSessionFromRule(false);
        org.jbpm.process.instance.ProcessInstance processInstance = ((org.jbpm.process.instance.ProcessInstance) (workingMemory.startProcess("com.sample.ruleflow")));
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        Assert.assertEquals(2, workingMemory.getProcessInstances().size());
        processInstance.setState(ProcessInstance.STATE_ABORTED);
        Assert.assertEquals(0, workingMemory.getProcessInstances().size());
    }

    @Test
    public void testVariableMapping() throws Exception {
        StatefulKnowledgeSession workingMemory = ProcessSubProcessTest.createStatefulKnowledgeSessionFromRule(true);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("x", "x-value");
        org.jbpm.process.instance.ProcessInstance processInstance = ((org.jbpm.process.instance.ProcessInstance) (workingMemory.startProcess("com.sample.ruleflow", map)));
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        Assert.assertEquals(2, workingMemory.getProcessInstances().size());
        for (ProcessInstance p : workingMemory.getProcessInstances()) {
            VariableScopeInstance variableScopeInstance = ((VariableScopeInstance) (((org.jbpm.process.instance.ProcessInstance) (p)).getContextInstance(VariableScope.VARIABLE_SCOPE)));
            if ("com.sample.ruleflow".equals(p.getProcessId())) {
                Assert.assertEquals("x-value", variableScopeInstance.getVariable("x"));
            } else if ("com.sample.subflow".equals(p.getProcessId())) {
                Assert.assertEquals("x-value", variableScopeInstance.getVariable("y"));
                Assert.assertEquals("z-value", variableScopeInstance.getVariable("z"));
                Assert.assertEquals(7, variableScopeInstance.getVariable("n"));
                Assert.assertEquals(10, variableScopeInstance.getVariable("o"));
            } 
        }
        workingMemory.insert(new org.jbpm.integrationtests.test.Person());
        workingMemory.fireAllRules();
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        VariableScopeInstance variableScopeInstance = ((VariableScopeInstance) (processInstance.getContextInstance(VariableScope.VARIABLE_SCOPE)));
        Assert.assertEquals("z-value", variableScopeInstance.getVariable("x"));
        Assert.assertEquals(10, variableScopeInstance.getVariable("m"));
        Assert.assertEquals(0, workingMemory.getProcessInstances().size());
    }

    private static StatefulKnowledgeSession createStatefulKnowledgeSessionFromRule(boolean independentSubProcess) throws Exception {
        KnowledgeBase ruleBase = ProcessSubProcessTest.readRule(independentSubProcess);
        return ruleBase.newStatefulKnowledgeSession();
    }

    private static KnowledgeBase readRule(boolean independent) throws Exception {
        KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
        Reader source = new StringReader(((((((((((((((((("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ("<process xmlns=\"http://drools.org/drools-5.0/process\"\n" + ("         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + ("         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" + ("         type=\"RuleFlow\" name=\"ruleflow\" id=\"com.sample.ruleflow\" package-name=\"com.sample\" >\n" + ("\n" + ("  <header>\n" + ("    <variables>\n" + ("      <variable name=\"x\" >\n" + ("        <type name=\"org.drools.core.process.core.datatype.impl.type.StringDataType\" />\n" + ("        <value></value>\n" + ("      </variable>\n" + ("      <variable name=\"m\" >\n" + ("        <type name=\"org.drools.core.process.core.datatype.impl.type.IntegerDataType\" />\n" + ("        <value></value>\n" + ("      </variable>\n" + ("    </variables>\n" + ("  </header>\n" + ("\n" + ("  <nodes>\n" + ("    <start id=\"1\" name=\"Start\" />\n" + "    <subProcess id=\"2\" name=\"SubProcess\" processId=\"com.sample.subflow\" independent=\""))))))))))))))))))))) + independent) + "\" >\n") + "      <mapping type=\"in\" from=\"x\" to=\"y\" />\n") + "      <mapping type=\"in\" from=\"x.length()\" to=\"n\" />\n") + "      <mapping type=\"out\" from=\"z\" to=\"x\" />\n") + "      <mapping type=\"out\" from=\"o\" to=\"m\" />\n") + "    </subProcess>\n") + "    <end id=\"3\" name=\"End\" />\n") + "  </nodes>\n") + "\n") + "  <connections>\n") + "    <connection from=\"1\" to=\"2\" />\n") + "    <connection from=\"2\" to=\"3\" />\n") + "  </connections>\n") + "\n") + "</process>"));
        builder.addRuleFlow(source);
        source = new StringReader(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ("<process xmlns=\"http://drools.org/drools-5.0/process\"\n" + ("         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + ("         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" + ("         type=\"RuleFlow\" name=\"flow\" id=\"com.sample.subflow\" package-name=\"com.sample\" >\n" + ("\n" + ("  <header>\n" + ("    <imports>\n" + ("      <import name=\"org.jbpm.integrationtests.test.Person\" />\n" + ("    </imports>\n" + ("    <variables>\n" + ("      <variable name=\"y\" >\n" + ("        <type name=\"org.drools.core.process.core.datatype.impl.type.StringDataType\" />\n" + ("        <value></value>\n" + ("      </variable>\n" + ("      <variable name=\"z\" >\n" + ("        <type name=\"org.drools.core.process.core.datatype.impl.type.StringDataType\" />\n" + ("        <value>z-value</value>\n" + ("      </variable>\n" + ("      <variable name=\"n\" >\n" + ("        <type name=\"org.drools.core.process.core.datatype.impl.type.IntegerDataType\" />\n" + ("      </variable>\n" + ("      <variable name=\"o\" >\n" + ("        <type name=\"org.drools.core.process.core.datatype.impl.type.IntegerDataType\" />\n" + ("        <value>10</value>\n" + ("      </variable>\n" + ("    </variables>\n" + ("  </header>\n" + ("\n" + ("  <nodes>\n" + ("    <start id=\"1\" name=\"Start\" />\n" + ("    <milestone id=\"2\" name=\"Event Wait\" >\n" + ("      <constraint type=\"rule\" dialect=\"mvel\" >Person( )</constraint>" + ("    </milestone>\n" + ("    <end id=\"3\" name=\"End\" />\n" + ("  </nodes>\n" + ("\n" + ("  <connections>\n" + ("    <connection from=\"1\" to=\"2\" />\n" + ("    <connection from=\"2\" to=\"3\" />\n" + ("  </connections>\n" + ("\n" + "</process>")))))))))))))))))))))))))))))))))))))))))));
        builder.addRuleFlow(source);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(((Collection) (Arrays.asList(builder.getPackage()))));
        return kbase;
    }

    @Test
    public void testDynamicSubProcess() throws Exception {
        KnowledgeBase kbase = ProcessSubProcessTest.readDynamicSubProcess();
        StatefulKnowledgeSession workingMemory = kbase.newStatefulKnowledgeSession();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "subflow");
        ProcessInstance processInstance = ((ProcessInstance) (workingMemory.startProcess("com.sample.ruleflow", params)));
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        Assert.assertEquals(2, workingMemory.getProcessInstances().size());
        workingMemory.insert(new org.jbpm.integrationtests.test.Person());
        workingMemory.fireAllRules();
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        Assert.assertEquals(0, workingMemory.getProcessInstances().size());
    }

    private static KnowledgeBase readDynamicSubProcess() throws Exception {
        KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
        Reader source = new StringReader(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ("<process xmlns=\"http://drools.org/drools-5.0/process\"\n" + ("         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + ("         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" + ("         type=\"RuleFlow\" name=\"ruleflow\" id=\"com.sample.ruleflow\" package-name=\"com.sample\" >\n" + ("\n" + ("  <header>\n" + ("    <variables>\n" + ("      <variable name=\"x\" >\n" + ("        <type name=\"org.drools.core.process.core.datatype.impl.type.StringDataType\" />\n" + ("        <value></value>\n" + ("      </variable>\n" + ("    </variables>\n" + ("  </header>\n" + ("\n" + ("  <nodes>\n" + ("    <start id=\"1\" name=\"Start\" />\n" + ("    <subProcess id=\"2\" name=\"SubProcess\" processId=\"com.sample.#{x}\" />\n" + ("    <end id=\"3\" name=\"End\" />\n" + ("  </nodes>\n" + ("\n" + ("  <connections>\n" + ("    <connection from=\"1\" to=\"2\" />\n" + ("    <connection from=\"2\" to=\"3\" />\n" + ("  </connections>\n" + ("\n" + "</process>")))))))))))))))))))))))))));
        builder.addRuleFlow(source);
        source = new StringReader(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ("<process xmlns=\"http://drools.org/drools-5.0/process\"\n" + ("         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + ("         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" + ("         type=\"RuleFlow\" name=\"flow\" id=\"com.sample.subflow\" package-name=\"com.sample\" >\n" + ("\n" + ("  <header>\n" + ("    <imports>\n" + ("      <import name=\"org.jbpm.integrationtests.test.Person\" />\n" + ("    </imports>\n" + ("  </header>\n" + ("\n" + ("  <nodes>\n" + ("    <start id=\"1\" name=\"Start\" />\n" + ("    <milestone id=\"2\" name=\"Event Wait\" >\n" + ("      <constraint type=\"rule\" dialect=\"mvel\" >Person( )</constraint>" + ("    </milestone>\n" + ("    <end id=\"3\" name=\"End\" />\n" + ("  </nodes>\n" + ("\n" + ("  <connections>\n" + ("    <connection from=\"1\" to=\"2\" />\n" + ("    <connection from=\"2\" to=\"3\" />\n" + ("  </connections>\n" + ("\n" + "</process>"))))))))))))))))))))))))));
        builder.addRuleFlow(source);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(((Collection) (Arrays.asList(builder.getPackage()))));
        return kbase;
    }
}

