/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.executor.cdi.impl;

import javax.enterprise.context.ApplicationScoped;
import org.kie.internal.runtime.cdi.BootOnLoad;
import org.kie.api.executor.ExecutorService;
import javax.inject.Inject;
import javax.inject.Named;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Named(value = "ExecutorServiceLifeCycleController-startable")
@BootOnLoad
@ApplicationScoped
public class ExecutorServiceLifeCycleController {
    @Inject
    private ExecutorService executorService;

    @PostConstruct
    public void init() {
        executorService.init();
    }

    @PreDestroy
    public void destroy() {
        executorService.destroy();
    }
}

