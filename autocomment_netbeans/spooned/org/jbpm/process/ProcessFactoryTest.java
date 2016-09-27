/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.process;

import org.jbpm.test.util.AbstractBaseTest;
import org.slf4j.LoggerFactory;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.junit.Test;

public class ProcessFactoryTest extends AbstractBaseTest {
    public void addLogger() {
        logger = LoggerFactory.getLogger(ProcessFactoryTest.this.getClass());
    }

    @Test
    public void testProcessFactory() {
        RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("org.drools.core.process");
        // connections
        // nodes
        // header
        factory.name("My process").packageName("org.drools").startNode(1).name("Start").done().actionNode(2).name("Action").action("java", "System.out.println(\"Action\");").done().endNode(3).name("End").done().connection(1, 2).connection(2, 3);
        factory.validate().getProcess();
    }
}
