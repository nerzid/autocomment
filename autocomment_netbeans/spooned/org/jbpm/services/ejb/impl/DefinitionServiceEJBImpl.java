/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.services.ejb.impl;

import org.jbpm.kie.services.impl.bpmn2.BPMN2DataServiceImpl;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.ejb.api.DefinitionServiceEJBLocal;
import org.jbpm.services.ejb.api.DefinitionServiceEJBRemote;
import org.jbpm.services.api.DeploymentEvent;
import org.jbpm.services.api.DeploymentEventListener;
import org.kie.api.runtime.KieContainer;
import javax.ejb.Lock;
import javax.ejb.LockType;
import org.jbpm.services.api.model.ProcessDefinition;
import javax.ejb.Singleton;

@Singleton
@ConcurrencyManagement(value = ConcurrencyManagementType.CONTAINER)
@Lock(value = LockType.READ)
public class DefinitionServiceEJBImpl extends BPMN2DataServiceImpl implements DefinitionService , DeploymentEventListener , DefinitionServiceEJBLocal , DefinitionServiceEJBRemote {
    @Lock(value = LockType.WRITE)
    @Override
    public ProcessDefinition buildProcessDefinition(String deploymentId, String bpmn2Content, KieContainer kieContainer, boolean cache) throws IllegalArgumentException {
        return super.buildProcessDefinition(deploymentId, bpmn2Content, kieContainer, cache);
    }

    @Lock(value = LockType.WRITE)
    @Override
    public void onDeploy(DeploymentEvent event) {
        super.onDeploy(event);
    }

    @Lock(value = LockType.WRITE)
    @Override
    public void onUnDeploy(DeploymentEvent event) {
        super.onUnDeploy(event);
    }

    @Lock(value = LockType.WRITE)
    @Override
    public void onActivate(DeploymentEvent event) {
        super.onActivate(event);
    }

    @Lock(value = LockType.WRITE)
    @Override
    public void onDeactivate(DeploymentEvent event) {
        super.onDeactivate(event);
    }
}

