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


package org.jbpm.osgi.flow.compiler;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import java.util.Hashtable;
import org.drools.compiler.compiler.ProcessBuilderFactory;
import org.drools.compiler.compiler.ProcessBuilderFactoryService;
import org.kie.api.Service;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {
    private ServiceRegistration processBuilderReg;

    public void start(BundleContext bc) throws Exception {
        Activator.this.processBuilderReg = bc.registerService(new String[]{ ProcessBuilderFactoryService.class.getName() , Service.class.getName() }, new org.jbpm.process.builder.ProcessBuilderFactoryServiceImpl(), new Hashtable());
        RuntimeManagerFactory.Factory.reset();
        ProcessBuilderFactory.reInitializeProvider();
    }

    public void stop(BundleContext bc) throws Exception {
        Activator.this.processBuilderReg.unregister();
    }
}

