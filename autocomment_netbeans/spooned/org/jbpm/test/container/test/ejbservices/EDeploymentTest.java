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


package org.jbpm.test.container.test.ejbservices;

import org.jbpm.test.container.AbstractEJBServicesTest;
import org.assertj.core.api.Assertions;
import org.junit.experimental.categories.Category;
import java.util.Collection;
import org.jbpm.services.api.model.DeployedUnit;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.test.container.groups.EAP;
import org.jbpm.services.api.model.ProcessDefinition;
import org.junit.Test;
import org.jbpm.test.container.groups.WAS;
import org.jbpm.test.container.groups.WLS;

@Category(value = { EAP.class , WAS.class , WLS.class })
public class EDeploymentTest extends AbstractEJBServicesTest {
    @Test
    public void testDeploy() throws Exception {
        DeploymentUnit basicKieJar = archive.deployBasicKieJar();
        DeployedUnit deployed = deploymentService.getDeployedUnit(basicKieJar.getIdentifier());
        Assertions.assertThat(deployed).isNotNull();
        Assertions.assertThat(deployed.getDeploymentUnit()).isNotNull();
        Assertions.assertThat(deployed.getDeploymentUnit()).isEqualTo(basicKieJar);
        Assertions.assertThat(deployed.getRuntimeManager()).isNotNull();
        Collection<ProcessDefinition> processes = runtimeDataService.getProcesses(new org.kie.internal.query.QueryContext());
        Assertions.assertThat(processes).isNotNull().isNotEmpty();
    }

    @Test
    public void testDuplicateDeploy() {
        DeploymentUnit basicKieJar = archive.deployBasicKieJar();
        try {
            deploymentService.deploy(basicKieJar);
            Assertions.assertThat(false).isTrue();// An exception should be thrown
            
        } catch (Exception ex) {
            Assertions.assertThat(ex.getMessage()).contains(((basicKieJar.getIdentifier()) + " is already deployed"));
        }
    }

    @Test
    public void testUndeploy() {
        DeploymentUnit basicKieJar = archive.deployBasicKieJar();
        DeployedUnit deployed = deploymentService.getDeployedUnit(basicKieJar.getIdentifier());
        Assertions.assertThat(deployed).isNotNull();
        archive.undeployDeploymentUnit(basicKieJar);
        DeployedUnit undeployed = deploymentService.getDeployedUnit(basicKieJar.getIdentifier());
        Assertions.assertThat(undeployed).isNull();
    }
}

