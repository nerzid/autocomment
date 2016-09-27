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


package org.jbpm.persistence;

import java.util.ArrayList;
import java.util.Collections;
import org.kie.internal.process.CorrelationKey;
import org.jbpm.persistence.correlation.CorrelationKeyInfo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.drools.persistence.map.MapBasedPersistenceContext;
import org.jbpm.persistence.processinstance.ProcessInstanceInfo;

public class MapBasedProcessPersistenceContext extends MapBasedPersistenceContext implements NonTransactionalProcessPersistentSession , ProcessPersistenceContext {
    private ProcessStorage storage;

    private Map<Long, ProcessInstanceInfo> processes;

    private Map<CorrelationKeyInfo, ProcessInstanceInfo> processInstancesByBusinessKey;

    public MapBasedProcessPersistenceContext(ProcessStorage storage) {
        super(storage);
        MapBasedProcessPersistenceContext.this.storage = storage;
        MapBasedProcessPersistenceContext.this.processes = new HashMap<Long, ProcessInstanceInfo>();
        MapBasedProcessPersistenceContext.this.processInstancesByBusinessKey = new HashMap<CorrelationKeyInfo, ProcessInstanceInfo>();
    }

    public ProcessInstanceInfo persist(ProcessInstanceInfo processInstanceInfo) {
        if ((processInstanceInfo.getId()) == null) {
            processInstanceInfo.setId(storage.getNextProcessInstanceId());
        } 
        processes.put(processInstanceInfo.getId(), processInstanceInfo);
        return processInstanceInfo;
    }

    public ProcessInstanceInfo findProcessInstanceInfo(Long processId) {
        ProcessInstanceInfo processInstanceInfo = processes.get(processId);
        if (processInstanceInfo == null) {
            processInstanceInfo = storage.findProcessInstanceInfo(processId);
        } 
        return processInstanceInfo;
    }

    public List<ProcessInstanceInfo> getStoredProcessInstances() {
        return Collections.unmodifiableList(new ArrayList<ProcessInstanceInfo>(processes.values()));
    }

    @Override
    public void close() {
        super.close();
        clearStoredProcessInstances();
    }

    public void remove(ProcessInstanceInfo processInstanceInfo) {
        storage.removeProcessInstanceInfo(processInstanceInfo.getId());
    }

    public List<Long> getProcessInstancesWaitingForEvent(String type) {
        return storage.getProcessInstancesWaitingForEvent(type);
    }

    public void clearStoredProcessInstances() {
        processes.clear();
    }

    @Override
    public CorrelationKeyInfo persist(CorrelationKeyInfo correlationKeyInfo) {
        ProcessInstanceInfo piInfo = MapBasedProcessPersistenceContext.this.processes.get(correlationKeyInfo.getProcessInstanceId());
        if (piInfo != null) {
            MapBasedProcessPersistenceContext.this.processInstancesByBusinessKey.put(correlationKeyInfo, piInfo);
        } 
        return correlationKeyInfo;
    }

    @Override
    public Long getProcessInstanceByCorrelationKey(CorrelationKey correlationKey) {
        ProcessInstanceInfo piInfo = MapBasedProcessPersistenceContext.this.processInstancesByBusinessKey.get(correlationKey);
        return piInfo.getId();
    }
}

