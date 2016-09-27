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

import org.assertj.core.api.Assertions;
import qa.tools.ikeeper.annotation.BZ;
import org.drools.core.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import org.jbpm.test.entity.DocumentVariable;
import javax.persistence.EntityManager;
import java.util.HashMap;
import org.jbpm.test.JbpmTestCase;
import org.kie.api.runtime.KieSession;
import java.util.List;
import java.util.Map;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.junit.Test;

public class ProcessInstanceTest extends JbpmTestCase {
    private static final String EQUALS = "org/jbpm/test/regression/ProcessInstance-equals.bpmn";

    private static final String EQUALS_ID = "org.jbpm.test.regression.ProcessInstance-equals";

    private static final String VARIABLE_PERSISTENCE = "org/jbpm/test/regression/ProcessInstance-variablePersistence.bpmn2";

    private static final String VARIABLE_PERSISTENCE_ID = "org.jbpm.test.regression.ProcessInstance-variablePersistence";

    @Test
    @BZ(value = "949973")
    public void testProcessEquals() throws Exception {
        KieSession ksession = createKSession(ProcessInstanceTest.EQUALS);
        ProcessInstance pi = ksession.startProcess(ProcessInstanceTest.EQUALS_ID);
        Assertions.assertThat(pi.equals(pi)).isTrue();
    }

    @Test
    @BZ(value = "1062346")
    public void testJPAStrategy() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().entityManagerFactory(getEmf()).addEnvironmentEntry(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES, new ObjectMarshallingStrategy[]{ new org.drools.persistence.jpa.marshaller.JPAPlaceholderResolverStrategy(getEmf()) , new org.drools.core.marshalling.impl.SerializablePlaceholderResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT) }).addAsset(ResourceFactory.newClassPathResource(ProcessInstanceTest.VARIABLE_PERSISTENCE, getClass()), ResourceType.BPMN2).get();
        createRuntimeManager(Strategy.SINGLETON, new HashMap<String, org.kie.api.io.ResourceType>(), environment, "custom-rm");
        RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        DocumentVariable dv = new DocumentVariable();
        dv.setContent("empty");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("document", dv);
        ProcessInstance pi = engine.getKieSession().startProcess(ProcessInstanceTest.VARIABLE_PERSISTENCE_ID, params);
        EntityManager em = getEmf().createEntityManager();
        List<DocumentVariable> documents = em.createQuery("from DocumentVariable").getResultList();
        Assertions.assertThat(documents).hasSize(1);
        Assertions.assertThat(documents.get(0).getContent()).isEqualTo("content-changed");
        engine.getKieSession().abortProcessInstance(pi.getId());
    }
}

