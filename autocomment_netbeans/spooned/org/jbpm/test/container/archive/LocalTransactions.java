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

import org.jboss.shrinkwrap.api.ArchivePaths;
import java.io.File;
import org.jbpm.test.container.tools.IntegrationMavenResolver;
import org.jbpm.test.container.tools.KieUtils;
import org.jbpm.test.container.handlers.ListWorkItemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.kie.api.io.Resource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jbpm.test.container.listeners.TrackingAgendaEventListener;
import org.jbpm.test.container.listeners.TrackingProcessEventListener;
import org.jboss.shrinkwrap.api.spec.WebArchive;

public class LocalTransactions {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalTransactions.class);

    public static final String ARCHIVE_NAME = "local-transactions";

    public static final String SERVICE_URL = ((("http://localhost:" + (System.getProperty("container.port"))) + "/") + (LocalTransactions.ARCHIVE_NAME)) + "/";

    public static final String PROCESS_TRANSACTIONS = "transactions";

    public static final String BPMN_TRANSACTIONS = "transactions-process.bpmn";

    public static final String BPMN_HELLO_WORLD = "hello-world_1.0.bpmn";

    public static final String RULES_TRANSACTIONS = "transactions-rules.drl";

    public static final String LOCAL_TRANSACTIONS_PATH = "org/jbpm/test/container/archive/localtransactions/";

    private WebArchive war;

    public WebArchive buildArchive() {
        System.out.println((("### Building archive '" + (LocalTransactions.ARCHIVE_NAME)) + ".war'"));
        PomEquippedResolveStage resolver = IntegrationMavenResolver.get("jbpm", "jbpm-persistence");
        File[] dependencies = resolver.importCompileAndRuntimeDependencies().resolve().withTransitivity().asFile();
        LocalTransactions.LOGGER.debug("Archive dependencies:");
        for (File d : dependencies) {
            LocalTransactions.LOGGER.debug(d.getName());
        }
        war = ShrinkWrap.create(WebArchive.class, ((LocalTransactions.ARCHIVE_NAME) + ".war")).addAsResource(((LocalTransactions.LOCAL_TRANSACTIONS_PATH) + (LocalTransactions.BPMN_TRANSACTIONS))).addAsResource(((LocalTransactions.LOCAL_TRANSACTIONS_PATH) + (LocalTransactions.BPMN_HELLO_WORLD))).addAsResource(((LocalTransactions.LOCAL_TRANSACTIONS_PATH) + (LocalTransactions.RULES_TRANSACTIONS))).addAsLibraries(dependencies);
        war.addClass(LocalTransactions.class).addClass(ListWorkItemHandler.class).addClass(TrackingAgendaEventListener.class).addClass(TrackingProcessEventListener.class).addClass(KieUtils.class);
        war.addPackages(true, "org.jbpm.test.container.groups");
        // WEB-INF resources
        war.addAsWebResource(getClass().getResource("/logback.xml"), ArchivePaths.create("logback.xml"));
        // CDI beans.xml
        war.addAsWebInfResource(EmptyAsset.INSTANCE, ArchivePaths.create("classes/META-INF/beans.xml"));
        war.addAsResource(getClass().getResource("/persistence.xml"), ArchivePaths.create("META-INF/persistence.xml"));
        war.addAsWebResource(getClass().getResource("localtransactions/tomcat-context.xml"), ArchivePaths.create("META-INF/context.xml"));
        return war;
    }

    public Resource getResource(String resourceName) {
        return KieServices.Factory.get().getResources().newClassPathResource(((LocalTransactions.LOCAL_TRANSACTIONS_PATH) + resourceName));
    }

    public WebArchive getWar() {
        return war;
    }

    public static String getContext() {
        return LocalTransactions.SERVICE_URL;
    }

    public void setWar(WebArchive war) {
        LocalTransactions.this.war = war;
    }
}

