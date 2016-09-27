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


package org.jbpm.services.cdi.impl.store;

import org.jbpm.services.cdi.Activate;
import javax.enterprise.context.ApplicationScoped;
import org.jbpm.services.cdi.Deactivate;
import org.jbpm.services.cdi.Deploy;
import org.jbpm.services.api.DeploymentEvent;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.kie.services.impl.store.DeploymentStore;
import org.jbpm.kie.services.impl.store.DeploymentSynchronizer;
import javax.inject.Inject;
import javax.enterprise.event.Observes;
import javax.annotation.PostConstruct;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.jbpm.services.cdi.Undeploy;

@ApplicationScoped
public class DeploymentSynchronizerCDIImpl extends DeploymentSynchronizer {
    @Inject
    private TransactionalCommandService commandService;

    @PostConstruct
    public void configure() {
        DeploymentStore store = new DeploymentStore();
        store.setCommandService(commandService);
        setDeploymentStore(store);
    }

    @Inject
    @Override
    public void setDeploymentService(DeploymentService deploymentService) {
        super.setDeploymentService(deploymentService);
    }

    public void onDeploy(@Observes
    @Deploy
    DeploymentEvent event) {
        super.onDeploy(event);
    }

    public void onUnDeploy(@Observes
    @Undeploy
    DeploymentEvent event) {
        super.onUnDeploy(event);
    }

    @Override
    public void onActivate(@Observes
    @Activate
    DeploymentEvent event) {
        super.onActivate(event);
    }

    @Override
    public void onDeactivate(@Observes
    @Deactivate
    DeploymentEvent event) {
        super.onDeactivate(event);
    }
}

