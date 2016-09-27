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

import javax.enterprise.context.ApplicationScoped;
import org.kie.internal.runtime.cdi.BootOnLoad;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.kie.services.impl.store.DeploymentStore;
import org.jbpm.kie.services.impl.store.DeploymentSyncInvoker;
import org.jbpm.kie.services.impl.store.DeploymentSynchronizer;
import javax.naming.InitialContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.jbpm.shared.services.impl.TransactionalCommandService;

@Named(value = "DeploymentSyncManager-startable")
@BootOnLoad
@ApplicationScoped
public class DeploymentSyncManager {
    @Inject
    private DeploymentService deploymentService;

    @Inject
    private TransactionalCommandService commandService;

    private DeploymentSyncInvoker invoker;

    private DeploymentSynchronizer synchronizer;

    @PostConstruct
    public void configureAndStart() {
        try {
            InitialContext.doLookup("java:module/DeploymentSynchronizerCDInvoker");
        } catch (Exception e) {
            DeploymentStore store = new DeploymentStore();
            store.setCommandService(commandService);
            synchronizer = new DeploymentSynchronizer();
            synchronizer.setDeploymentService(deploymentService);
            synchronizer.setDeploymentStore(store);
            invoker = new DeploymentSyncInvoker(synchronizer);
            invoker.start();
        }
    }

    @PreDestroy
    public void cleanup() {
        if ((invoker) != null) {
            invoker.stop();
        } 
    }
}

