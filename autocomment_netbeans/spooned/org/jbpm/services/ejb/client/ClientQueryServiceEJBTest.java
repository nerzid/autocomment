/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.services.ejb.client;

import org.junit.Before;
import org.jbpm.services.ejb.api.DefinitionServiceEJBRemote;
import org.jbpm.services.ejb.api.DeploymentServiceEJBRemote;
import org.junit.Ignore;
import org.kie.api.KieServices;
import org.kie.scanner.MavenRepository;
import org.jbpm.services.ejb.api.ProcessServiceEJBRemote;
import org.jbpm.services.ejb.api.query.QueryServiceEJBRemote;
import org.jbpm.kie.services.test.QueryServiceImplTest;
import org.jbpm.services.ejb.api.RuntimeDataServiceEJBRemote;
import org.junit.Test;
import org.jbpm.services.ejb.api.UserTaskServiceEJBRemote;

public class ClientQueryServiceEJBTest extends QueryServiceImplTest {
    private static final String application = "sample-war-ejb-app";

    @Before
    public void prepare() {
        super.prepare();
        userTaskService.execute((((((GROUP_ID) + ":") + (ARTIFACT_ID)) + ":") + (VERSION)), new org.jbpm.kie.services.helper.CleanUpCommand());
    }

    @Override
    protected void close() {
        // do nothing
    }

    @Override
    protected void configureServices() {
        correctUser = "anonymous";
        try {
            ClientServiceFactory factory = ServiceFactoryProvider.getProvider("JBoss");
            DeploymentServiceEJBRemote deploymentService = factory.getService(ClientQueryServiceEJBTest.application, DeploymentServiceEJBRemote.class);
            ProcessServiceEJBRemote processService = factory.getService(ClientQueryServiceEJBTest.application, ProcessServiceEJBRemote.class);
            RuntimeDataServiceEJBRemote runtimeDataService = factory.getService(ClientQueryServiceEJBTest.application, RuntimeDataServiceEJBRemote.class);
            DefinitionServiceEJBRemote definitionService = factory.getService(ClientQueryServiceEJBTest.application, DefinitionServiceEJBRemote.class);
            UserTaskServiceEJBRemote userTaskService = factory.getService(ClientQueryServiceEJBTest.application, UserTaskServiceEJBRemote.class);
            QueryServiceEJBRemote queryService = factory.getService(ClientQueryServiceEJBTest.application, QueryServiceEJBRemote.class);
            setBpmn2Service(definitionService);
            setProcessService(processService);
            setRuntimeDataService(runtimeDataService);
            setUserTaskService(userTaskService);
            setQueryService(queryService);
            setDeploymentService(new org.jbpm.services.ejb.client.helper.DeploymentServiceWrapper(deploymentService));
            setIdentityProvider(identityProvider);
        } catch (Exception e) {
            throw new RuntimeException("Unable to configure services", e);
        }
    }

    @Override
    protected void prepareJPAModule(KieServices ks, MavenRepository repository) {
        // no op here
    }

    @Override
    protected String getDataSourceJNDI() {
        return "java:jboss/datasources/ExampleDS";
    }

    @Ignore(value = "not supported for remote ejb")
    @Test
    @Override
    public void testGetTaskInstancesWithCustomVariables() throws Exception {
    }

    @Ignore(value = "not supported for remote ejb")
    @Test
    @Override
    public void testGetProcessInstancesWithQueryParamBuilder() {
    }

    @Ignore(value = "Requires actual authentication and users to be configured on remote server")
    @Test
    @Override
    public void testGetTaskInstancesAsPotOwners() {
    }

    @Ignore(value = "Requires actual authentication and users to be configured on remote server")
    @Test
    @Override
    public void testGetTaskInstancesAsBA() {
    }
}

