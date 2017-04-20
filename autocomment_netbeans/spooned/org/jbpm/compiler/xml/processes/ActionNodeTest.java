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


package org.jbpm.compiler.xml.processes;

import org.jbpm.test.util.AbstractBaseTest;
import java.util.ArrayList;
import org.junit.Assert;
import ResourceType.DRF;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import java.util.List;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.junit.Test;

public class ActionNodeTest extends AbstractBaseTest {
    @Test
    public void testSingleActionNode() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        // add ClassPathResource{new ClassPathResource("ActionNodeTest.xml", ActionNodeTest.class)} to KnowledgeBuilder{kbuilder}
        kbuilder.add(new ClassPathResource("ActionNodeTest.xml", ActionNodeTest.class), DRF);
        KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        // set global String{"list"} to StatefulKnowledgeSession{ksession}
        ksession.setGlobal("list", list);
        // start process String{"process name"} to StatefulKnowledgeSession{ksession}
        ksession.startProcess("process name");
        // assert equals int{1} to void{Assert}
        Assert.assertEquals(1, list.size());
        // assert equals String{"action node was here"} to void{Assert}
        Assert.assertEquals("action node was here", list.get(0));
    }
}

