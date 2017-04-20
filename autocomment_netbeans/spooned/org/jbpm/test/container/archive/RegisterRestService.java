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
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import KieServices.Factory;
import java.io.File;
import org.jbpm.test.container.tools.IntegrationMavenResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.kie.api.io.Resource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

public class RegisterRestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterRestService.class);

    public static final String ARCHIVE_NAME = "register-rest-1.0";

    public static final String SERVICE_URL = ((("http://localhost:" + (System.getProperty("container.port"))) + "/") + (RegisterRestService.ARCHIVE_NAME)) + "/";

    public static final String PROCESS_CALL_REST_SERVICE = "org.jboss.qa.jbpm.CallREST";

    public static final String PROCESS_CALL_REST_SERVICE_SIMPLE = "org.jboss.qa.jbpm.CallRESTSimple";

    public static final String BPMN_CALL_REST_SERVICE = "call-rest-service.bpmn";

    public static final String BPMN_CALL_REST_SERVICE_SIMPLE = "call-rest-service-simple.bpmn";

    public static final String REGISTER_REST_SERVICE_PACKAGE = "org.jbpm.test.container.archive.registerrestservice";

    public static final String REGISTER_REST_SERVICE_PATH = "org/jbpm/test/container/archive/registerrestservice/";

    private WebArchive war;

    public Archive<?> buildArchive() {
        // println String{(("### Building archive '" + (RegisterRestService.ARCHIVE_NAME)) + ".war'")} to PrintStream{System.out}
        System.out.println((("### Building archive '" + (RegisterRestService.ARCHIVE_NAME)) + ".war'"));
        PomEquippedResolveStage resolver = IntegrationMavenResolver.get("rest");
        File[] dependencies = resolver.importCompileAndRuntimeDependencies().resolve().withTransitivity().asFile();
        // debug String{"Archive dependencies:"} to Logger{RegisterRestService.LOGGER}
        RegisterRestService.LOGGER.debug("Archive dependencies:");
        for (File d : dependencies) {
            RegisterRestService.LOGGER.debug(d.getName());
        }
        war = ShrinkWrap.create(WebArchive.class, ((RegisterRestService.ARCHIVE_NAME) + ".war")).addPackage(RegisterRestService.REGISTER_REST_SERVICE_PACKAGE).setWebXML(getClass().getResource("registerrestservice/WEB-INF/web.xml")).addAsWebInfResource(getClass().getResource("registerrestservice/WEB-INF/weblogic.xml"), ArchivePaths.create("weblogic.xml")).addAsLibraries(dependencies);
        // If we are on a WebSphere
        if (System.getProperty("container.port").equals("9080")) {
            EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, ((RegisterRestService.ARCHIVE_NAME) + ".ear"));
            ear.addAsModule(war).setApplicationXML(getClass().getResource("registerrestservice/application.xml")).addAsLibraries(resolver.importCompileAndRuntimeDependencies().resolve().withTransitivity().asFile());
            // META-INF resources for WAS
            ear.addAsApplicationResource(getClass().getResource("registerrestservice/ibm-application-bnd.xml"), ArchivePaths.create("ibm-application-bnd.xml"));
            return ear;
        }
        return war;
    }

    public Resource getResource(String resourceName) {
        return Factory.get().getResources().newClassPathResource(((RegisterRestService.REGISTER_REST_SERVICE_PATH) + resourceName));
    }

    public WebArchive getWar() {
        return war;
    }

    public static String getContext() {
        return RegisterRestService.SERVICE_URL;
    }

    public void setWar(WebArchive war) {
        this.war = war;
    }
}

