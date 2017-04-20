/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.services.task.audit.service;

import org.junit.After;
import org.junit.Before;
import javax.persistence.EntityManagerFactory;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.kie.internal.task.api.InternalTaskService;
import javax.persistence.Persistence;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import org.jbpm.services.task.audit.TaskAuditServiceFactory;

public class LocalTaskAuditTest extends TaskAuditBaseTest {
    private PoolingDataSource pds;

    private EntityManagerFactory emf;

    @Before
    public void setup() {
        pds = setupPoolingDataSource();
        emf = Persistence.createEntityManagerFactory("org.jbpm.services.task");
        this.taskService = ((InternalTaskService) (HumanTaskServiceFactory.newTaskServiceConfigurator().entityManagerFactory(emf).listener(new org.jbpm.services.task.audit.JPATaskLifeCycleEventListener(true)).listener(new BAMTaskEventListener(true)).getTaskService()));
        this.taskAuditService = TaskAuditServiceFactory.newTaskAuditServiceConfigurator().setTaskService(taskService).getTaskAuditService();
    }

    @After
    public void clean() {
        if ((emf) != null) {
            emf.close();
        }
        if ((pds) != null) {
            pds.close();
        }
    }
}

