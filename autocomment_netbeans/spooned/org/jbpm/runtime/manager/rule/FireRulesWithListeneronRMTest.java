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


package org.jbpm.runtime.manager.rule;

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import java.util.Date;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.KieSession;
import java.text.ParseException;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import java.util.Properties;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import java.text.SimpleDateFormat;
import org.junit.Test;
import org.jbpm.runtime.manager.util.TestUtil;

public class FireRulesWithListeneronRMTest extends AbstractBaseTest {
    private PoolingDataSource pds;

    private RuntimeManager manager;

    @Before
    public void setup() {
        Properties properties = new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        pds = TestUtil.setupPoolingDataSource();
    }

    @After
    public void teardown() {
        manager.close();
        pds.close();
    }

    @Test
    public void testCreationOfSessionWithPersistence() throws ParseException {
        String accountRule = "";
        accountRule += "package org.kie.test\n";
        accountRule += "import org.jbpm.runtime.manager.rule.Account\n";
        accountRule += "rule \"Account Eligibility\"\n";
        accountRule += "dialect \"java\" \n";
        accountRule += "when\n";
        accountRule += "$account:Account( getAccountStatus().equals(\"O\"))\n";
        accountRule += "then\n";
        accountRule += "$account.setAccountEligible(true);\n";
        accountRule += "System.out.println(\" Account Eligibility Rule called - then part after changing value  \"+$account);\n";
        accountRule += "end\n";
        accountRule += "\n";
        String customerRule = "";
        customerRule += "package org.kie.test\n";
        customerRule += "import org.jbpm.runtime.manager.rule.OrderEligibility\n";
        customerRule += "import function org.jbpm.runtime.manager.rule.OrderEligibilityCheck.dateDifference\n";
        customerRule += "rule \"Customer Eligibility\"\n";
        customerRule += "dialect \"java\" \n";
        customerRule += "when\n";
        customerRule += " $orderEligibility: OrderEligibility(dateDifference(getOrderDetails().getEndDate(), getOrderDetails().getStartDate()))\n";
        customerRule += "then\n";
        customerRule += " $orderEligibility.setOrderEligibile(true);\n";
        customerRule += "System.out.println(\" Order Eligibility Rule called - then part after changing value  \"+ $orderEligibility);\n";
        customerRule += "end\n";
        customerRule += "\n";
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().addAsset(ResourceFactory.newByteArrayResource(accountRule.getBytes()), ResourceType.DRL).addAsset(ResourceFactory.newByteArrayResource(customerRule.getBytes()), ResourceType.DRL).get();
        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        Assert.assertNotNull(manager);
        // ksession for process instance #1
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();
        Account accountOpen = new Account("O");
        Account accountClosed = new Account("C");
        FactHandle accountOpen_FH = ksession.insert(accountOpen);
        FactHandle accountClosed_FH = ksession.insert(accountClosed);
        SimpleDateFormat stdDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date today = stdDateFormat.parse("2014/03/10");
        Date yesterday = stdDateFormat.parse("2014/03/09");
        Date lastYear = stdDateFormat.parse("2013/03/10");
        OrderDetails lastYearOrder = new OrderDetails(lastYear, today);
        OrderDetails thisYearOrder = new OrderDetails(yesterday, today);
        OrderEligibility lastYearEligibility = new OrderEligibility(lastYearOrder);
        OrderEligibility thisYearEligibility = new OrderEligibility(thisYearOrder);
        FactHandle lye_FH = ksession.insert(lastYearEligibility);
        FactHandle tye_FH = ksession.insert(thisYearEligibility);
        ksession.fireAllRules();
        Assert.assertTrue(accountOpen.getAccountEligible());
        Assert.assertTrue((!(accountClosed.getAccountEligible())));
        ksession.delete(accountOpen_FH);
        ksession.delete(accountClosed_FH);
        Assert.assertTrue(lastYearEligibility.getOrderEligibile());
        Assert.assertTrue((!(thisYearEligibility.getOrderEligibile())));
        ksession.delete(lye_FH);
        ksession.delete(tye_FH);
        manager.close();
    }
}

