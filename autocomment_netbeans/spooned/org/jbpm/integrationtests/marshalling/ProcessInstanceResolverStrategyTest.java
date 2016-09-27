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


package org.jbpm.integrationtests.marshalling;

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Assert;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.drools.core.impl.EnvironmentFactory;
import java.util.HashMap;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.common.InternalWorkingMemory;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.marshalling.MarshallerFactory;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.kie.api.marshalling.MarshallingConfiguration;
import java.io.ObjectInputStream;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import java.io.ObjectOutputStream;
import org.kie.api.runtime.process.ProcessInstance;
import org.jbpm.process.instance.ProcessInstanceManager;
import org.jbpm.marshalling.impl.ProcessInstanceResolverStrategy;
import org.drools.core.marshalling.impl.ProtobufMarshaller;
import org.drools.core.marshalling.impl.RuleBaseNodes;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.junit.Test;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;

public class ProcessInstanceResolverStrategyTest extends AbstractBaseTest {
    private static final String PROCESS_NAME = "simpleProcess.xml";

    @Test
    public void testAccept() {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        WorkflowProcessImpl process = new WorkflowProcessImpl();
        RuleFlowProcessInstance processInstance = new RuleFlowProcessInstance();
        processInstance.setState(ProcessInstance.STATE_ACTIVE);
        processInstance.setProcess(process);
        processInstance.setKnowledgeRuntime(((InternalKnowledgeRuntime) (ksession)));
        ProcessInstanceResolverStrategy strategy = new ProcessInstanceResolverStrategy();
        Assert.assertTrue(strategy.accept(processInstance));
        Object object = new Object();
        Assert.assertTrue((!(strategy.accept(object))));
    }

    @Test
    public void testProcessInstanceResolverStrategy() throws Exception {
        // Setup
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(new org.drools.core.io.impl.ClassPathResource(ProcessInstanceResolverStrategyTest.PROCESS_NAME, ProcessInstanceResolverStrategyTest.this.getClass()), ResourceType.DRF);
        KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ProcessInstance processInstance = ksession.createProcessInstance("process name", new HashMap<String, Object>());
        ksession.insert(processInstance);
        // strategy setup
        ProcessInstanceResolverStrategy strategy = new ProcessInstanceResolverStrategy();
        ObjectMarshallingStrategy[] strategies = new ObjectMarshallingStrategy[]{ strategy , MarshallerFactory.newSerializeMarshallingStrategy() };
        // Test strategy.write
        MarshallingConfiguration marshallingConfig = new org.drools.core.marshalling.impl.MarshallingConfigurationImpl(strategies, true, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MarshallerWriteContext writerContext = new MarshallerWriteContext(baos, ((InternalKnowledgeBase) (kbase)), ((InternalWorkingMemory) ((StatefulKnowledgeSessionImpl) (ksession))), RuleBaseNodes.getNodeMap(((InternalKnowledgeBase) (kbase))), marshallingConfig.getObjectMarshallingStrategyStore(), marshallingConfig.isMarshallProcessInstances(), marshallingConfig.isMarshallWorkItems(), ksession.getEnvironment());
        strategy.write(writerContext, processInstance);
        baos.close();
        writerContext.close();
        byte[] bytes = baos.toByteArray();
        int numCorrectBytes = calculateNumBytesForLong(processInstance.getId());
        Assert.assertTrue(((("Expected " + numCorrectBytes) + " bytes, not ") + (bytes.length)), ((bytes.length) == numCorrectBytes));
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        long serializedProcessInstanceId = ois.readLong();
        Assert.assertTrue(((("Expected " + (processInstance.getId())) + ", not ") + serializedProcessInstanceId), ((processInstance.getId()) == serializedProcessInstanceId));
        // Test other strategy stuff
        ProcessInstanceManager pim = ProcessInstanceResolverStrategy.retrieveProcessInstanceManager(writerContext);
        Assert.assertNotNull(pim);
        Assert.assertNotNull(ProcessInstanceResolverStrategy.retrieveKnowledgeRuntime(writerContext));
        Assert.assertTrue((processInstance == (pim.getProcessInstance(serializedProcessInstanceId))));
        // Test strategy.read
        bais = new ByteArrayInputStream(bytes);
        MarshallerReaderContext readerContext = new MarshallerReaderContext(bais, ((KnowledgeBaseImpl) (kbase)), RuleBaseNodes.getNodeMap(((KnowledgeBaseImpl) (kbase))), marshallingConfig.getObjectMarshallingStrategyStore(), ProtobufMarshaller.TIMER_READERS, marshallingConfig.isMarshallProcessInstances(), marshallingConfig.isMarshallWorkItems(), EnvironmentFactory.newEnvironment());
        readerContext = ((StatefulKnowledgeSessionImpl) (ksession)).getInternalWorkingMemory();
        Object procInstObject = strategy.read(readerContext);
        Assert.assertTrue(((procInstObject != null) && (procInstObject instanceof ProcessInstance)));
        Assert.assertTrue((processInstance == procInstObject));
    }

    private int calculateNumBytesForLong(Long longVal) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeLong(longVal);
        baos.close();
        oos.close();
        return baos.toByteArray().length;
    }
}

