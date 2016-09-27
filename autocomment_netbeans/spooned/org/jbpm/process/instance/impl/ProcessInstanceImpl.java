/**
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.process.instance.impl;

import org.kie.api.runtime.rule.Agenda;
import java.util.ArrayList;
import org.jbpm.process.core.Context;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.instance.ContextInstance;
import java.util.HashMap;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.jbpm.process.instance.InternalProcessRuntime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.drools.core.util.MVELSafeHelper;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jbpm.process.instance.ProcessInstance;
import java.io.Serializable;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.jbpm.process.core.impl.XmlProcessDumper;
import org.jbpm.process.core.impl.XmlProcessDumperFactory;

/**
 * Default implementation of a process instance.
 */
public abstract class ProcessInstanceImpl implements Serializable , ProcessInstance {
    protected static final Pattern PARAMETER_MATCHER = Pattern.compile("#\\{([\\S&&[^\\}]]+)\\}", Pattern.DOTALL);

    private static final Logger logger = LoggerFactory.getLogger(ProcessInstanceImpl.class);

    private static final long serialVersionUID = 510L;

    private long id;

    private String processId;

    private transient Process process;

    private String processXml;

    private int state = STATE_PENDING;

    private Map<String, ContextInstance> contextInstances = new HashMap<String, ContextInstance>();

    private Map<String, List<ContextInstance>> subContextInstances = new HashMap<String, List<ContextInstance>>();

    private transient InternalKnowledgeRuntime kruntime;

    private Map<String, Object> metaData = new HashMap<String, Object>();

    private String outcome;

    private long parentProcessInstanceId;

    private String description;

    public void setId(final long id) {
        ProcessInstanceImpl.this.id = id;
    }

    public long getId() {
        return ProcessInstanceImpl.this.id;
    }

    public void setProcess(final Process process) {
        ProcessInstanceImpl.this.processId = process.getId();
        ProcessInstanceImpl.this.process = ((Process) (process));
    }

    public void updateProcess(final Process process) {
        setProcess(process);
        XmlProcessDumper dumper = XmlProcessDumperFactory.newXmlProcessDumperFactory();
        ProcessInstanceImpl.this.processXml = dumper.dumpProcess(process);
    }

    public String getProcessXml() {
        return processXml;
    }

    public void setProcessXml(String processXml) {
        if ((processXml != null) && ((processXml.trim().length()) > 0)) {
            ProcessInstanceImpl.this.processXml = processXml;
        } 
    }

    public Process getProcess() {
        if ((ProcessInstanceImpl.this.process) == null) {
            if ((processXml) == null) {
                if ((kruntime) == null) {
                    throw new IllegalStateException((((("Process instance " + (id)) + "[") + (processId)) + "] is disconnected."));
                } 
                ProcessInstanceImpl.this.process = kruntime.getKieBase().getProcess(processId);
            } else {
                XmlProcessDumper dumper = XmlProcessDumperFactory.newXmlProcessDumperFactory();
                ProcessInstanceImpl.this.process = dumper.readProcess(processXml);
            }
        } 
        return ProcessInstanceImpl.this.process;
    }

    public void setProcessId(String processId) {
        ProcessInstanceImpl.this.processId = processId;
    }

    public String getProcessId() {
        return processId;
    }

    public String getProcessName() {
        return getProcess().getName();
    }

    public void setState(final int state) {
        internalSetState(state);
    }

    public void setState(final int state, String outcome) {
        ProcessInstanceImpl.this.outcome = outcome;
        internalSetState(state);
    }

    public void internalSetState(final int state) {
        ProcessInstanceImpl.this.state = state;
    }

    public int getState() {
        return ProcessInstanceImpl.this.state;
    }

    public void setKnowledgeRuntime(final InternalKnowledgeRuntime kruntime) {
        if ((ProcessInstanceImpl.this.kruntime) != null) {
            throw new IllegalArgumentException("Runtime can only be set once.");
        } 
        ProcessInstanceImpl.this.kruntime = kruntime;
    }

    public InternalKnowledgeRuntime getKnowledgeRuntime() {
        return ProcessInstanceImpl.this.kruntime;
    }

    public Agenda getAgenda() {
        if ((getKnowledgeRuntime()) == null) {
            return null;
        } 
        return getKnowledgeRuntime().getAgenda();
    }

    public ContextContainer getContextContainer() {
        return ((ContextContainer) (getProcess()));
    }

    public void setContextInstance(String contextId, ContextInstance contextInstance) {
        ProcessInstanceImpl.this.contextInstances.put(contextId, contextInstance);
    }

    public ContextInstance getContextInstance(String contextId) {
        ContextInstance contextInstance = ProcessInstanceImpl.this.contextInstances.get(contextId);
        if (contextInstance != null) {
            return contextInstance;
        } 
        Context context = ((ContextContainer) (getProcess())).getDefaultContext(contextId);
        if (context != null) {
            contextInstance = getContextInstance(context);
            return contextInstance;
        } 
        return null;
    }

    public List<ContextInstance> getContextInstances(String contextId) {
        return ProcessInstanceImpl.this.subContextInstances.get(contextId);
    }

    public void addContextInstance(String contextId, ContextInstance contextInstance) {
        List<ContextInstance> list = ProcessInstanceImpl.this.subContextInstances.get(contextId);
        if (list == null) {
            list = new ArrayList<ContextInstance>();
            ProcessInstanceImpl.this.subContextInstances.put(contextId, list);
        } 
        list.add(contextInstance);
    }

    public void removeContextInstance(String contextId, ContextInstance contextInstance) {
        List<ContextInstance> list = ProcessInstanceImpl.this.subContextInstances.get(contextId);
        if (list != null) {
            list.remove(contextInstance);
        } 
    }

    public ContextInstance getContextInstance(String contextId, long id) {
        List<ContextInstance> contextInstances = subContextInstances.get(contextId);
        if (contextInstances != null) {
            for (ContextInstance contextInstance : contextInstances) {
                if ((contextInstance.getContextId()) == id) {
                    return contextInstance;
                } 
            }
        } 
        return null;
    }

    public ContextInstance getContextInstance(final Context context) {
        ContextInstanceFactory conf = ContextInstanceFactoryRegistry.INSTANCE.getContextInstanceFactory(context);
        if (conf == null) {
            throw new IllegalArgumentException(("Illegal context type (registry not found): " + (context.getClass())));
        } 
        ContextInstance contextInstance = ((ContextInstance) (conf.getContextInstance(context, ProcessInstanceImpl.this, ProcessInstanceImpl.this)));
        if (contextInstance == null) {
            throw new IllegalArgumentException(("Illegal context type (instance not found): " + (context.getClass())));
        } 
        return contextInstance;
    }

    public void signalEvent(String type, Object event) {
    }

    public void start() {
        start(null);
    }

    public void start(String trigger) {
        synchronized(ProcessInstanceImpl.this) {
            if ((getState()) != (STATE_PENDING)) {
                throw new IllegalArgumentException("A process instance can only be started once");
            } 
            setState(STATE_ACTIVE);
            internalStart(trigger);
        }
    }

    protected abstract void internalStart(String trigger);

    public void disconnect() {
        ((InternalProcessRuntime) (kruntime.getProcessRuntime())).getProcessInstanceManager().internalRemoveProcessInstance(ProcessInstanceImpl.this);
        process = null;
        kruntime = null;
    }

    public void reconnect() {
        ((InternalProcessRuntime) (kruntime.getProcessRuntime())).getProcessInstanceManager().internalAddProcessInstance(ProcessInstanceImpl.this);
    }

    public String[] getEventTypes() {
        return null;
    }

    public String toString() {
        final StringBuilder b = new StringBuilder("ProcessInstance ");
        b.append(getId());
        b.append(" [processId=");
        b.append(ProcessInstanceImpl.this.process.getId());
        b.append(",state=");
        b.append(ProcessInstanceImpl.this.state);
        b.append("]");
        return b.toString();
    }

    public Map<String, Object> getMetaData() {
        return ProcessInstanceImpl.this.metaData;
    }

    public void setMetaData(String name, Object data) {
        ProcessInstanceImpl.this.metaData.put(name, data);
    }

    public void setOutcome(String outcome) {
        ProcessInstanceImpl.this.outcome = outcome;
    }

    public String getOutcome() {
        return outcome;
    }

    public long getParentProcessInstanceId() {
        return parentProcessInstanceId;
    }

    public void setParentProcessInstanceId(long parentProcessInstanceId) {
        ProcessInstanceImpl.this.parentProcessInstanceId = parentProcessInstanceId;
    }

    public String getDescription() {
        if ((description) == null) {
            description = process.getName();
            if ((process) != null) {
                Object metaData = process.getMetaData().get("customDescription");
                if (metaData instanceof String) {
                    String customDescription = ((String) (metaData));
                    Map<String, String> replacements = new HashMap<String, String>();
                    Matcher matcher = ProcessInstanceImpl.PARAMETER_MATCHER.matcher(customDescription);
                    while (matcher.find()) {
                        String paramName = matcher.group(1);
                        if ((replacements.get(paramName)) == null) {
                            try {
                                String value = ((String) (MVELSafeHelper.getEvaluator().eval(paramName, new org.jbpm.workflow.instance.impl.ProcessInstanceResolverFactory(((WorkflowProcessInstance) (ProcessInstanceImpl.this))))));
                                replacements.put(paramName, value);
                            } catch (Throwable t) {
                                ProcessInstanceImpl.logger.error(("Could not resolve customDescription, parameter " + paramName), t);
                                ProcessInstanceImpl.logger.error("Continuing without setting description.");
                            }
                        } 
                    }
                    for (Map.Entry<String, String> replacement : replacements.entrySet()) {
                        customDescription = customDescription.replace((("#{" + (replacement.getKey())) + "}"), replacement.getValue());
                    }
                    description = customDescription;
                } 
            } 
        } 
        return description;
    }

    public void setDescription(String description) {
        ProcessInstanceImpl.this.description = description;
    }
}

