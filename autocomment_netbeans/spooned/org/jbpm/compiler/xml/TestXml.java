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


package org.jbpm.compiler.xml;

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Assert;
import java.io.InputStreamReader;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.drools.core.xml.SemanticModules;
import java.io.StringReader;
import org.junit.Test;

public class TestXml extends AbstractBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(TestXml.class);

    @Test
    public void testSimpleXml() throws Exception {
        SemanticModules modules = new SemanticModules();
        modules.addSemanticModule(new ProcessSemanticModule());
        XmlProcessReader reader = new XmlProcessReader(modules, getClass().getClassLoader());
        reader.read(new InputStreamReader(TestXml.class.getResourceAsStream("XmlTest.xml")));
        List<Process> processes = reader.getProcess();
        Assert.assertNotNull(processes);
        Assert.assertEquals(1, processes.size());
        RuleFlowProcess process = ((RuleFlowProcess) (processes.get(0)));
        Assert.assertNotNull(process);
        String output = XmlRuleFlowProcessDumper.INSTANCE.dump(process);
        TestXml.logger.info(output);
        reader = new XmlProcessReader(new SemanticModules(), getClass().getClassLoader());
        reader.read(new StringReader(output));
    }
}

