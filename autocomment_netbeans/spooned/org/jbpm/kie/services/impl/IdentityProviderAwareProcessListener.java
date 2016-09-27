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


package org.jbpm.kie.services.impl;

import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.internal.identity.IdentityProvider;
import org.kie.api.runtime.KieSession;
import org.kie.api.event.process.ProcessStartedEvent;
import org.jbpm.workflow.instance.WorkflowProcessInstance;

public class IdentityProviderAwareProcessListener extends DefaultProcessEventListener {
    private KieSession kieSession;

    private IdentityProvider identityProvider;

    public IdentityProviderAwareProcessListener(final KieSession kieSession) {
        IdentityProviderAwareProcessListener.this.kieSession = kieSession;
    }

    protected void resolveIdentityProvider() {
        if ((identityProvider) != null) {
            return ;
        } 
        final Object identityProvider = kieSession.getEnvironment().get("IdentityProvider");
        if (identityProvider instanceof IdentityProvider) {
            IdentityProviderAwareProcessListener.this.identityProvider = ((IdentityProvider) (identityProvider));
        } 
    }

    public void beforeProcessStarted(final ProcessStartedEvent event) {
        resolveIdentityProvider();
        if ((identityProvider) != null) {
            final WorkflowProcessInstance wpi = ((WorkflowProcessInstance) (event.getProcessInstance()));
            final String name = identityProvider.getName();
            wpi.setVariable("initiator", name);
            wpi.getMetaData().put("OwnerId", name);
        } 
    }
}

