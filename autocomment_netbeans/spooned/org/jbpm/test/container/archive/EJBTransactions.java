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


package org.jbpm.test.container.archive;

import org.jbpm.test.container.AbstractEJBTransactionsTest;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jbpm.test.container.test.EJBTransactionsTest;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import java.io.File;
import org.jbpm.test.container.tools.IntegrationMavenResolver;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jbpm.test.container.JbpmContainerTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.kie.api.io.Resource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jbpm.test.container.tools.TrackingListenerAssert;
import org.jbpm.test.container.listeners.TrackingProcessEventListener;

public class EJBTransactions {
    private static final Logger LOGGER = LoggerFactory.getLogger(EJBTransactions.class);

    public static final String ARCHIVE_NAME = "ejb-transactions";

    public static final String SERVICE_URL = ((("http://localhost:" + (System.getProperty("container.port"))) + "/") + (EJBTransactions.ARCHIVE_NAME)) + "/";

    public static final String PROCESS_DOUBLE_HUMAN_TASKS = "double-human-tasks";

    public static final String BPMN_DOUBLE_HUMAN_TASKS = "double-human-tasks.bpmn";

    public static final String EJB_TRANSACTIONS_PACKAGE = "org.jbpm.test.container.archive.ejbtransactions";

    public static final String EJB_TRANSACTIONS_PATH = "org/jbpm/test/container/archive/ejbtransactions/";

    private EnterpriseArchive ear;

    public EnterpriseArchive buildArchive() {
        System.out.println((("### Building archive '" + (EJBTransactions.ARCHIVE_NAME)) + ".war'"));
        PomEquippedResolveStage resolver = IntegrationMavenResolver.get("jbpm", "jbpm-persistence");
        File[] dependencies = resolver.importCompileAndRuntimeDependencies().resolve().withTransitivity().asFile();
        EJBTransactions.LOGGER.debug("Archive dependencies:");
        for (File d : dependencies) {
            EJBTransactions.LOGGER.debug(d.getName());
        }
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class, ((EJBTransactions.ARCHIVE_NAME) + ".jar")).addPackages(true, "org.jbpm.test.container.groups", EJBTransactions.EJB_TRANSACTIONS_PACKAGE).addClass(EJBTransactions.class).addClass(JbpmContainerTest.class).addClass(AbstractEJBTransactionsTest.class).addClass(EJBTransactionsTest.class).addClass(TrackingProcessEventListener.class).addClass(TrackingListenerAssert.class).addAsResource(getClass().getResource(("ejbtransactions/" + (EJBTransactions.BPMN_DOUBLE_HUMAN_TASKS))), ArchivePaths.create(((EJBTransactions.EJB_TRANSACTIONS_PATH) + (EJBTransactions.BPMN_DOUBLE_HUMAN_TASKS)))).addAsResource(getClass().getResource("/persistence.xml"), ArchivePaths.create("META-INF/persistence.xml")).addAsResource(EmptyAsset.INSTANCE, ArchivePaths.create("META-INF/beans.xml"));
        ear = ShrinkWrap.create(EnterpriseArchive.class, ((EJBTransactions.ARCHIVE_NAME) + ".ear"));
        ear.addAsModule(jar).setApplicationXML(getClass().getResource("ejbtransactions/ejb-application.xml")).addAsLibraries(dependencies);
        // META-INF resources
        ear.addAsResource(getClass().getResource("/logback.xml"), ArchivePaths.create("logback.xml"));
        return ear;
    }

    public Resource getResource(String resourceName) {
        return KieServices.Factory.get().getResources().newClassPathResource(((EJBTransactions.EJB_TRANSACTIONS_PATH) + resourceName));
    }

    public EnterpriseArchive getEar() {
        return ear;
    }

    public static String getContext() {
        return EJBTransactions.SERVICE_URL;
    }

    public void setEar(EnterpriseArchive ear) {
        EJBTransactions.this.ear = ear;
    }
}

