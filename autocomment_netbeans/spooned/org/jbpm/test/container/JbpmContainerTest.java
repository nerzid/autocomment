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


package org.jbpm.test.container;

import org.junit.After;
import org.jboss.arquillian.junit.Arquillian;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.runner.Description;
import javax.persistence.EntityManagerFactory;
import org.kie.api.runtime.Environment;
import javax.naming.InitialContext;
import org.kie.api.KieBase;
import org.kie.api.builder.KieBuilder;
import org.kie.api.command.KieCommands;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.io.KieResources;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieSession;
import org.kie.api.persistence.jpa.KieStoreServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import org.kie.api.io.Resource;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.rules.TestName;
import org.junit.rules.TestWatcher;
import javax.transaction.UserTransaction;

@RunWith(value = Arquillian.class)
public abstract class JbpmContainerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(JbpmContainerTest.class);

    protected static final String REMOTE_CONTAINER = "remote-container";

    protected boolean persistence;

    protected EntityManagerFactory emf;

    // Allows the tests to retrieve their names
    @Rule
    public TestName name = new TestName();

    /**
     * Gets the name of current test method.
     */
    public final String getTestName() {
        return name.getMethodName();
    }

    // prints the test class name before executing it
    @ClassRule
    public static TestWatcher classWatcher = new TestWatcher() {
        @Override
        protected void starting(Description description) {
            JbpmContainerTest.LOGGER.info(String.format("%n%n%25s Starting [%s]%n", "", description.getClassName()));
        }
    };

    // prints test method name before executing it and results after execution
    @Rule
    public TestWatcher methodWatcher = new TestWatcher() {
        @Override
        protected void starting(Description description) {
            JbpmContainerTest.LOGGER.info(String.format("==== %s ====", description.getMethodName()));
        }

        @Override
        protected void succeeded(Description description) {
            JbpmContainerTest.LOGGER.info("succeded {} - {}", description.getClassName(), description.getMethodName());
        }

        @Override
        protected void failed(Throwable e, Description description) {
            JbpmContainerTest.LOGGER.warn("failed {} - {}", description.getClassName(), description.getMethodName());
        }
    };

    @Before
    public void createEMF() {
        if (persistence) {
            try {
                emf = Persistence.createEntityManagerFactory("containerPU");
            } catch (PersistenceException ex) {
                if (ex.getMessage().equals("No Persistence provider for EntityManager named containerPU")) {
                    // https://community.jboss.org/thread/173265
                    // BeforeMethod is run outside container first, therefore
                    // this exception can be ignored
                    JbpmContainerTest.LOGGER.warn("Unable to create EntityManagerFactory", ex);
                } else {
                    throw ex;
                }
            }
        } 
    }

    protected Environment getEnvironment() {
        if ((emf) == null) {
            throw new IllegalStateException("Uninitialised EntityManagerFactory");
        } 
        Environment env = JbpmContainerTest.getServices().newEnvironment();
        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
        try {
            Object transaction = new InitialContext().lookup("java:comp/UserTransaction");
            JbpmContainerTest.LOGGER.debug(("User transaction class: " + (transaction.getClass())));
            Assertions.assertThat((transaction instanceof UserTransaction)).as("%s should be instance of %s", transaction.getClass(), UserTransaction.class).isTrue();
        } catch (Exception ex) {
            JbpmContainerTest.LOGGER.debug("Something went wrong", ex);
            throw new RuntimeException(ex);
        }
        return env;
    }

    @After
    public void closeEMF() {
        if ((emf) != null) {
            emf.close();
        } 
        emf = null;
    }

    protected KieSession createJPASession(KieBase kbase) {
        return JbpmContainerTest.getStore().newKieSession(kbase, null, getEnvironment());
    }

    protected KieSession loadJPASession(KieBase kbase, long sessionId) {
        return JbpmContainerTest.getStore().loadKieSession(sessionId, kbase, null, getEnvironment());
    }

    protected KieSession reloadSession(KieSession ksession) {
        long id = ksession.getIdentifier();
        KieBase kbase = ksession.getKieBase();
        ksession.dispose();
        return loadJPASession(kbase, id);
    }

    protected static KieServices getServices() {
        return KieServices.Factory.get();
    }

    protected static KieResources getResources() {
        return JbpmContainerTest.getServices().getResources();
    }

    protected static KieStoreServices getStore() {
        return JbpmContainerTest.getServices().getStoreServices();
    }

    protected static KieCommands getCommands() {
        return JbpmContainerTest.getServices().getCommands();
    }

    protected static KieBase getKieBase(KieBuilder kbuilder) {
        return JbpmContainerTest.getServices().newKieContainer(kbuilder.getKieModule().getReleaseId()).getKieBase();
    }

    public KieSession getSession(Resource... resources) {
        KieBase kbase = getKBase(resources);
        KieSession ksession = kbase.newKieSession();
        return ksession;
    }

    public KieBase getKBase(Resource... resources) {
        KieServices kservies = KieServices.Factory.get();
        KieFileSystem kfilesystem = kservies.newKieFileSystem();
        for (int i = 0; (resources != null) && (i < resources); ++i) {
            kfilesystem.write(resources[i]);
        }
        KieBuilder kbuilder = kservies.newKieBuilder(kfilesystem);
        kbuilder.buildAll();
        if (kbuilder.getResults().hasMessages(Level.ERROR)) {
            Assertions.fail(kbuilder.getResults().toString());
        } 
        KieBase kbase = kservies.newKieContainer(kbuilder.getKieModule().getReleaseId()).getKieBase();
        return kbase;
    }
}

