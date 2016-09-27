/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.jbpm.test.regression;

import org.w3c.dom.Attr;
import qa.tools.ikeeper.annotation.BZ;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import java.util.HashMap;
import org.jbpm.test.JbpmTestCase;
import org.kie.api.runtime.KieSession;
import java.util.Map;
import org.kie.api.runtime.process.ProcessInstance;
import org.junit.Test;

public class GatewayTest extends JbpmTestCase {
    private static final String INCLUSIVE_DEFAULT = "org/jbpm/test/regression/Gateway-inclusiveDefault.bpmn2";

    private static final String INCLUSIVE_DEFAULT_ID = "org.jbpm.test.regression.Gateway-inclusiveDefault";

    private static final String XPATH_EVALUATION = "org/jbpm/test/regression/Gateway-xPathEvaluation.bpmn2";

    private static final String XPATH_EVALUATION_ID = "org.jbpm.test.regression.Gateway-xPathEvaluation";

    @Test
    @BZ(value = "1146829")
    public void testInclusiveGatewayDefaultGate() {
        KieSession ksession = createKSession(GatewayTest.INCLUSIVE_DEFAULT);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("test", "c");
        ProcessInstance processInstance = ksession.startProcess(GatewayTest.INCLUSIVE_DEFAULT_ID, params);
        assertProcessInstanceCompleted(processInstance.getId());
    }

    @Test
    @BZ(value = "1071000")
    public void testExclusiveSplitXPathAdvanced() throws Exception {
        KieSession ksession = createKSession(GatewayTest.XPATH_EVALUATION);
        ksession.getWorkItemManager().registerWorkItemHandler("Email", new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element hi = doc.createElement("hi");
        Element ho = doc.createElement("ho");
        hi.appendChild(ho);
        Attr attr = doc.createAttribute("value");
        ho.setAttributeNode(attr);
        attr.setValue("a");
        params.put("x", hi);
        params.put("y", "Second");
        ProcessInstance processInstance = ksession.startProcess(GatewayTest.XPATH_EVALUATION_ID, params);
        assertProcessInstanceCompleted(processInstance.getId());
    }
}

