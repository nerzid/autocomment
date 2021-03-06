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


package org.jbpm.persistence.processinstance;

import java.util.Arrays;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import java.util.Date;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import org.kie.api.runtime.Environment;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.util.HashSet;
import java.io.IOException;
import javax.persistence.Id;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.common.InternalKnowledgeRuntime;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.drools.core.marshalling.impl.PersisterHelper;
import org.kie.api.runtime.process.ProcessInstance;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.jbpm.marshalling.impl.ProcessInstanceMarshaller;
import org.drools.core.marshalling.impl.ProcessMarshallerWriteContext;
import org.drools.core.marshalling.impl.ProtobufMarshaller;
import org.jbpm.marshalling.impl.ProtobufRuleFlowProcessInstanceMarshaller;
import javax.persistence.SequenceGenerator;
import java.util.Set;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.persistence.Transformable;
import javax.persistence.Transient;
import javax.persistence.Version;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;

@Entity
@SequenceGenerator(name = "processInstanceInfoIdSeq", sequenceName = "PROCESS_INSTANCE_INFO_ID_SEQ")
public class ProcessInstanceInfo implements Transformable {
    @Id
    @GeneratedValue(generator = "processInstanceInfoIdSeq", strategy = GenerationType.AUTO)
    @Column(name = "InstanceId")
    private Long processInstanceId;

    @Version
    @Column(name = "OPTLOCK")
    private int version;

    private String processId;

    private Date startDate;

    private Date lastReadDate;

    private Date lastModificationDate;

    private int state;

    @Lob
    @Column(length = 2147483647)
    byte[] processInstanceByteArray;

    @ElementCollection
    @CollectionTable(joinColumns = @JoinColumn(name = "InstanceId")
    , name = "EventTypes")
    @Column(name = "element")
    private Set<String> eventTypes = new HashSet<String>();

    @Transient
    ProcessInstance processInstance;

    @Transient
    Environment env;

    protected ProcessInstanceInfo() {
    }

    public ProcessInstanceInfo(ProcessInstance processInstance) {
        ProcessInstanceInfo.this.processInstance = processInstance;
        ProcessInstanceInfo.this.processId = processInstance.getProcessId();
        startDate = new Date();
    }

    public ProcessInstanceInfo(ProcessInstance processInstance, Environment env) {
        this(processInstance);
        ProcessInstanceInfo.this.env = env;
    }

    /**
     * Added in order to satisfy Hibernate AND the JBPMorm.xml:<ul>
     * <li> Hibernate needs getter/setters for a the field that's mapped.
     *   <ul><li>(field access is inefficient/dangerous, and not necessary)</li></ul></li>
     * <li>The JBPMorm.xml queries reference .processInstanceId as well.</li>
     * </ul>
     * If we mapped the field using 'name="id"', the queries would thus fail.
     * </p>
     * So instead of that, we just add the getters and use 'name="processInstanceId"'.
     * @return The processInstanceId field value.
     */
    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        ProcessInstanceInfo.this.processInstanceId = processInstanceId;
    }

    public Long getId() {
        return processInstanceId;
    }

    public void setId(Long processInstanceId) {
        ProcessInstanceInfo.this.processInstanceId = processInstanceId;
    }

    public String getProcessId() {
        return processId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getLastModificationDate() {
        return lastModificationDate;
    }

    public Date getLastReadDate() {
        return lastReadDate;
    }

    public void updateLastReadDate() {
        Date updateTo = new Date();
        if (((lastReadDate) == null) || ((lastReadDate.compareTo(updateTo)) < 0)) {
            lastReadDate = updateTo;
        } else {
            lastReadDate = new Date(((lastReadDate.getTime()) + 1));
        }
    }

    public int getState() {
        return state;
    }

    public ProcessInstance getProcessInstance(InternalKnowledgeRuntime kruntime, Environment env) {
        return getProcessInstance(kruntime, env, false);
    }

    public ProcessInstance getProcessInstance(InternalKnowledgeRuntime kruntime, Environment env, boolean readOnly) {
        ProcessInstanceInfo.this.env = env;
        if ((processInstance) == null) {
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(processInstanceByteArray);
                MarshallerReaderContext context = new MarshallerReaderContext(bais, ((InternalKnowledgeBase) (kruntime.getKieBase())), null, null, ProtobufMarshaller.TIMER_READERS, ProcessInstanceInfo.this.env);
                ProcessInstanceMarshaller marshaller = getMarshallerFromContext(context);
                context = ((StatefulKnowledgeSessionImpl) (kruntime)).getInternalWorkingMemory();
                processInstance = marshaller.readProcessInstance(context);
                ((WorkflowProcessInstanceImpl) (processInstance)).setPersisted(false);
                if (readOnly) {
                    ((WorkflowProcessInstanceImpl) (processInstance)).disconnect();
                } 
                context.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalArgumentException(("IOException while loading process instance: " + (e.getMessage())), e);
            }
        } 
        return processInstance;
    }

    private ProcessInstanceMarshaller getMarshallerFromContext(MarshallerReaderContext context) throws IOException {
        ObjectInputStream stream = context;
        String processInstanceType = stream.readUTF();
        return ProcessMarshallerRegistry.INSTANCE.getMarshaller(processInstanceType);
    }

    private void saveProcessInstanceType(MarshallerWriteContext context, ProcessInstance processInstance, String processInstanceType) throws IOException {
        ObjectOutputStream stream = context;
        // saves the processInstance type first
        stream.writeUTF(processInstanceType);
    }

    public void transform() {
        // if (processInstance == null) {
        // return;
        // }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean variablesChanged = false;
        try {
            ProcessMarshallerWriteContext context = new ProcessMarshallerWriteContext(baos, null, null, null, null, ProcessInstanceInfo.this.env);
            context.setProcessInstanceId(processInstance.getId());
            context.setState(((processInstance.getState()) == (ProcessInstance.STATE_ACTIVE) ? ProcessMarshallerWriteContext.STATE_ACTIVE : ProcessMarshallerWriteContext.STATE_COMPLETED));
            String processType = ((ProcessInstanceImpl) (processInstance)).getProcess().getType();
            saveProcessInstanceType(context, processInstance, processType);
            ProcessInstanceMarshaller marshaller = ProcessMarshallerRegistry.INSTANCE.getMarshaller(processType);
            Object result = marshaller.writeProcessInstance(context, processInstance);
            if ((marshaller instanceof ProtobufRuleFlowProcessInstanceMarshaller) && (result != null)) {
                JBPMMessages.ProcessInstance _instance = ((JBPMMessages.ProcessInstance) (result));
                PersisterHelper.writeToStreamWithHeader(context, _instance);
            } 
            context.close();
        } catch (IOException e) {
            throw new IllegalArgumentException(((("IOException while storing process instance " + (processInstance.getId())) + ": ") + (e.getMessage())), e);
        }
        byte[] newByteArray = baos.toByteArray();
        if (variablesChanged || (!(Arrays.equals(newByteArray, processInstanceByteArray)))) {
            ProcessInstanceInfo.this.state = processInstance.getState();
            ProcessInstanceInfo.this.lastModificationDate = new Date();
            ProcessInstanceInfo.this.processInstanceByteArray = newByteArray;
            ProcessInstanceInfo.this.eventTypes.clear();
            for (String type : processInstance.getEventTypes()) {
                eventTypes.add(type);
            }
        } 
        if (!(processInstance.getProcessId().equals(ProcessInstanceInfo.this.processId))) {
            ProcessInstanceInfo.this.processId = processInstance.getProcessId();
        } 
        ((WorkflowProcessInstanceImpl) (processInstance)).setPersisted(true);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } 
        if ((getClass()) != (obj.getClass())) {
            return false;
        } 
        final ProcessInstanceInfo other = ((ProcessInstanceInfo) (obj));
        if (((ProcessInstanceInfo.this.processInstanceId) != (other.processInstanceId)) && (((ProcessInstanceInfo.this.processInstanceId) == null) || (!(ProcessInstanceInfo.this.processInstanceId.equals(other.processInstanceId))))) {
            return false;
        } 
        if ((ProcessInstanceInfo.this.version) != (other.version)) {
            return false;
        } 
        if ((ProcessInstanceInfo.this.processId) == null ? (other.processId) != null : !(ProcessInstanceInfo.this.processId.equals(other.processId))) {
            return false;
        } 
        if (((ProcessInstanceInfo.this.startDate) != (other.startDate)) && (((ProcessInstanceInfo.this.startDate) == null) || (!(ProcessInstanceInfo.this.startDate.equals(other.startDate))))) {
            return false;
        } 
        if (((ProcessInstanceInfo.this.lastReadDate) != (other.lastReadDate)) && (((ProcessInstanceInfo.this.lastReadDate) == null) || (!(ProcessInstanceInfo.this.lastReadDate.equals(other.lastReadDate))))) {
            return false;
        } 
        if (((ProcessInstanceInfo.this.lastModificationDate) != (other.lastModificationDate)) && (((ProcessInstanceInfo.this.lastModificationDate) == null) || (!(ProcessInstanceInfo.this.lastModificationDate.equals(other.lastModificationDate))))) {
            return false;
        } 
        if ((ProcessInstanceInfo.this.state) != (other.state)) {
            return false;
        } 
        if (!(Arrays.equals(ProcessInstanceInfo.this.processInstanceByteArray, other.processInstanceByteArray))) {
            return false;
        } 
        if (((ProcessInstanceInfo.this.eventTypes) != (other.eventTypes)) && (((ProcessInstanceInfo.this.eventTypes) == null) || (!(ProcessInstanceInfo.this.eventTypes.equals(other.eventTypes))))) {
            return false;
        } 
        if (((ProcessInstanceInfo.this.processInstance) != (other.processInstance)) && (((ProcessInstanceInfo.this.processInstance) == null) || (!(ProcessInstanceInfo.this.processInstance.equals(other.processInstance))))) {
            return false;
        } 
        if (((ProcessInstanceInfo.this.env) != (other.env)) && (((ProcessInstanceInfo.this.env) == null) || (!(ProcessInstanceInfo.this.env.equals(other.env))))) {
            return false;
        } 
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = (61 * hash) + ((ProcessInstanceInfo.this.processInstanceId) != null ? ProcessInstanceInfo.this.processInstanceId.hashCode() : 0);
        hash = (61 * hash) + (ProcessInstanceInfo.this.version);
        hash = (61 * hash) + ((ProcessInstanceInfo.this.processId) != null ? ProcessInstanceInfo.this.processId.hashCode() : 0);
        hash = (61 * hash) + ((ProcessInstanceInfo.this.startDate) != null ? ProcessInstanceInfo.this.startDate.hashCode() : 0);
        hash = (61 * hash) + ((ProcessInstanceInfo.this.lastReadDate) != null ? ProcessInstanceInfo.this.lastReadDate.hashCode() : 0);
        hash = (61 * hash) + ((ProcessInstanceInfo.this.lastModificationDate) != null ? ProcessInstanceInfo.this.lastModificationDate.hashCode() : 0);
        hash = (61 * hash) + (ProcessInstanceInfo.this.state);
        hash = (61 * hash) + (Arrays.hashCode(ProcessInstanceInfo.this.processInstanceByteArray));
        hash = (61 * hash) + ((ProcessInstanceInfo.this.eventTypes) != null ? ProcessInstanceInfo.this.eventTypes.hashCode() : 0);
        hash = (61 * hash) + ((ProcessInstanceInfo.this.processInstance) != null ? ProcessInstanceInfo.this.processInstance.hashCode() : 0);
        hash = (61 * hash) + ((ProcessInstanceInfo.this.env) != null ? ProcessInstanceInfo.this.env.hashCode() : 0);
        return hash;
    }

    public int getVersion() {
        return version;
    }

    public Set<String> getEventTypes() {
        return eventTypes;
    }

    public byte[] getProcessInstanceByteArray() {
        return processInstanceByteArray;
    }

    public void clearProcessInstance() {
        processInstance = null;
    }

    public Environment getEnv() {
        return env;
    }

    public void setEnv(Environment env) {
        ProcessInstanceInfo.this.env = env;
    }
}

