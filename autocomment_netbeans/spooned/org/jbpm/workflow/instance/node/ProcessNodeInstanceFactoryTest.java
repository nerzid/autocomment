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


package org.jbpm.workflow.instance.node;

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Assert;
import org.jbpm.workflow.instance.impl.factory.CreateNewNodeFactory;
import org.slf4j.LoggerFactory;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.instance.impl.NodeInstanceFactoryRegistry;
import org.junit.Test;

public class ProcessNodeInstanceFactoryTest extends AbstractBaseTest {
    public void addLogger() {
        logger = LoggerFactory.getLogger(ProcessNodeInstanceFactoryTest.this.getClass());
    }

    @Test
    public void testDefaultEntries() throws Exception {
        Node node = new org.jbpm.workflow.core.node.ActionNode();
        Assert.assertEquals(CreateNewNodeFactory.class, NodeInstanceFactoryRegistry.getInstance(null).getProcessNodeInstanceFactory(node).getClass());
    }

    @Test
    public void testDiscoveredEntry() {
        NodeInstanceFactoryRegistry.getInstance(null).register(MockNode.class, new MockNodeInstanceFactory(new MockNodeInstance(new MockNode())));
        Assert.assertEquals(MockNodeInstanceFactory.class, NodeInstanceFactoryRegistry.getInstance(null).getProcessNodeInstanceFactory(new MockNode()).getClass());
    }
}

