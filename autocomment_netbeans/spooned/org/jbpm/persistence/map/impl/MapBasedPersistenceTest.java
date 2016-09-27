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


package org.jbpm.persistence.map.impl;

import java.util.ArrayList;
import org.junit.Before;
import org.kie.api.runtime.Environment;
import org.drools.persistence.map.EnvironmentBuilder;
import java.util.HashMap;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.api.KieBase;
import org.kie.internal.KnowledgeBaseFactory;
import java.util.List;
import java.util.Map;
import org.jbpm.persistence.processinstance.ProcessInstanceInfo;
import org.jbpm.persistence.ProcessStorage;
import org.drools.persistence.info.SessionInfo;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.drools.persistence.info.WorkItemInfo;

public class MapBasedPersistenceTest extends MapPersistenceTest {
    private MapBasedPersistenceTest.SimpleProcessStorage storage;

    @Before
    public void createStorage() {
        storage = new MapBasedPersistenceTest.SimpleProcessStorage();
    }

    @Override
    protected StatefulKnowledgeSession createSession(KieBase kbase) {
        EnvironmentBuilder envBuilder = new org.jbpm.persistence.ProcessStorageEnvironmentBuilder(storage);
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set(EnvironmentName.TRANSACTION_MANAGER, envBuilder.getTransactionManager());
        env.set(EnvironmentName.PERSISTENCE_CONTEXT_MANAGER, envBuilder.getPersistenceContextManager());
        return JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
    }

    @Override
    protected StatefulKnowledgeSession disposeAndReloadSession(StatefulKnowledgeSession ksession, long ksessionId, KieBase kbase) {
        ksession.dispose();
        EnvironmentBuilder envBuilder = new org.jbpm.persistence.ProcessStorageEnvironmentBuilder(storage);
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set(EnvironmentName.TRANSACTION_MANAGER, envBuilder.getTransactionManager());
        env.set(EnvironmentName.PERSISTENCE_CONTEXT_MANAGER, envBuilder.getPersistenceContextManager());
        return JPAKnowledgeService.loadStatefulKnowledgeSession(ksessionId, kbase, null, env);
    }

    @Override
    protected int getProcessInstancesCount() {
        return storage.processes.size();
    }

    @Override
    protected int getKnowledgeSessionsCount() {
        return storage.ksessions.size();
    }

    private static class SimpleProcessStorage implements ProcessStorage {
        private Map<Long, SessionInfo> ksessions = new HashMap<Long, SessionInfo>();

        private Map<Long, ProcessInstanceInfo> processes = new HashMap<Long, ProcessInstanceInfo>();

        private Map<Long, WorkItemInfo> workItems = new HashMap<Long, WorkItemInfo>();

        public void saveOrUpdate(SessionInfo ksessionInfo) {
            ksessionInfo.transform();
            ksessions.put(ksessionInfo.getId(), ksessionInfo);
        }

        public SessionInfo findSessionInfo(Long id) {
            return ksessions.get(id);
        }

        public ProcessInstanceInfo findProcessInstanceInfo(Long processInstanceId) {
            ProcessInstanceInfo processInstanceInfo = processes.get(processInstanceId);
            if (processInstanceInfo != null) {
                // FIXME need a way to clone a processInstance before saving
                processInstanceInfo.clearProcessInstance();
            } 
            return processInstanceInfo;
        }

        public void saveOrUpdate(ProcessInstanceInfo processInstanceInfo) {
            processInstanceInfo.transform();
            processes.put(processInstanceInfo.getId(), processInstanceInfo);
        }

        public long getNextProcessInstanceId() {
            return (processes.size()) + 1;
        }

        public void removeProcessInstanceInfo(Long id) {
            processes.remove(id);
        }

        public List<Long> getProcessInstancesWaitingForEvent(String type) {
            List<Long> processInstancesWaitingForEvent = new ArrayList<Long>();
            for (ProcessInstanceInfo processInstanceInfo : processes.values()) {
                if (processInstanceInfo.getEventTypes().contains(type))
                    processInstancesWaitingForEvent.add(processInstanceInfo.getId());
                
            }
            return processInstancesWaitingForEvent;
        }

        public void saveOrUpdate(WorkItemInfo workItemInfo) {
            workItems.put(workItemInfo.getId(), workItemInfo);
        }

        public Long getNextWorkItemId() {
            return new Long(((workItems.size()) + 1));
        }

        public WorkItemInfo findWorkItemInfo(Long id) {
            return workItems.get(id);
        }

        public void remove(WorkItemInfo workItemInfo) {
            workItems.remove(workItemInfo.getId());
        }

        public Long getNextStatefulKnowledgeSessionId() {
            return new Long(((ksessions.size()) + 1));
        }

        @Override
        public void lock(SessionInfo sessionInfo) {
            throw new UnsupportedOperationException("Map based persistence does not support locking.");
        }

        @Override
        public void lock(WorkItemInfo workItemInfo) {
            throw new UnsupportedOperationException("Map based persistence does not support locking.");
        }
    }
}

