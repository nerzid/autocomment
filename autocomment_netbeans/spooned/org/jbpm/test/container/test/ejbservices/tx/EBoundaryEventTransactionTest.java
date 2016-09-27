/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.test.container.test.ejbservices.tx;

import org.jbpm.test.container.AbstractRuntimeEJBServicesTest;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.jbpm.test.container.groups.EAP;
import javax.naming.InitialContext;
import org.junit.Test;
import javax.transaction.UserTransaction;
import org.jbpm.test.container.groups.WAS;
import org.jbpm.test.container.groups.WLS;

@Category(value = { EAP.class , WAS.class , WLS.class })
public class EBoundaryEventTransactionTest extends AbstractRuntimeEJBServicesTest {
    private static final String USER_TRANSACTION_NAME = "java:comp/UserTransaction";

    private static final String PROCESS_ID = "boundary-event-transactions";

    @Override
    @Before
    public void deployKieJar() {
        if ((kieJar) == null) {
            kieJar = archive.deployTransactionKieJar().getIdentifier();
        } 
    }

    @Test
    public void testErrorBoundaryEventRollback() throws Exception {
        Long processInstanceId = startProcessInstance(EBoundaryEventTransactionTest.PROCESS_ID);
        UserTransaction ut = InitialContext.doLookup(EBoundaryEventTransactionTest.USER_TRANSACTION_NAME);
        ut.begin();
        try {
            processService.signalProcessInstance(processInstanceId, "Continue", null);
            Assertions.assertThat(hasProcessInstanceCompleted(processInstanceId)).isTrue();
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }
        ut.rollback();
        Assertions.assertThat(hasNodeLeft(processInstanceId, "Test Wait")).isFalse();
        Assertions.assertThat(hasProcessInstanceCompleted(processInstanceId)).isFalse();
        abortProcessInstance(processInstanceId);
    }
}

