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


package org.jbpm.workflow.core.node;

import org.jbpm.process.core.context.AbstractContext;
import java.util.Collections;
import org.kie.api.definition.process.Connection;
import org.jbpm.process.core.Context;
import org.jbpm.process.core.ContextContainer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.jbpm.process.core.context.variable.Mappable;

/**
 * Default implementation of a sub-flow node.
 */
public class SubProcessNode extends StateBasedNode implements ContextContainer , Mappable {
    private static final long serialVersionUID = 510L;

    // NOTE: ContetxInstances are not persisted as current functionality (exception scope) does not require it
    private ContextContainer contextContainer = new org.jbpm.process.core.impl.ContextContainerImpl();

    private String processId;

    private String processName;

    private boolean waitForCompletion = true;

    private List<DataAssociation> inMapping = new LinkedList<DataAssociation>();

    private List<DataAssociation> outMapping = new LinkedList<DataAssociation>();

    private boolean independent = true;

    public void setProcessId(final String processId) {
        SubProcessNode.this.processId = processId;
    }

    public String getProcessId() {
        return SubProcessNode.this.processId;
    }

    public boolean isWaitForCompletion() {
        return waitForCompletion;
    }

    public void setWaitForCompletion(boolean waitForCompletion) {
        SubProcessNode.this.waitForCompletion = waitForCompletion;
    }

    public void addInMapping(String parameterName, String variableName) {
        inMapping.add(new DataAssociation(variableName, parameterName, null, null));
    }

    public void addInMapping(String parameterName, String variableName, Transformation transformation) {
        inMapping.add(new DataAssociation(variableName, parameterName, null, transformation));
    }

    public void setInMappings(Map<String, String> inMapping) {
        SubProcessNode.this.inMapping = new LinkedList<DataAssociation>();
        for (Map.Entry<String, String> entry : inMapping.entrySet()) {
            addInMapping(entry.getKey(), entry.getValue());
        }
    }

    public String getInMapping(String parameterName) {
        return getInMappings().get(parameterName);
    }

    public Map<String, String> getInMappings() {
        Map<String, String> in = new HashMap<String, String>();
        for (DataAssociation a : inMapping) {
            if ((((a.getSources().size()) == 1) && (((a.getAssignments()) == null) || ((a.getAssignments().size()) == 0))) && ((a.getTransformation()) == null)) {
                in.put(a.getTarget(), a.getSources().get(0));
            } 
        }
        return in;
    }

    public void addInAssociation(DataAssociation dataAssociation) {
        inMapping.add(dataAssociation);
    }

    public List<DataAssociation> getInAssociations() {
        return Collections.unmodifiableList(inMapping);
    }

    public void addOutMapping(String parameterName, String variableName) {
        outMapping.add(new DataAssociation(parameterName, variableName, null, null));
    }

    public void addOutMapping(String parameterName, String variableName, Transformation transformation) {
        outMapping.add(new DataAssociation(parameterName, variableName, null, transformation));
    }

    public void setOutMappings(Map<String, String> outMapping) {
        SubProcessNode.this.outMapping = new LinkedList<DataAssociation>();
        for (Map.Entry<String, String> entry : outMapping.entrySet()) {
            addOutMapping(entry.getKey(), entry.getValue());
        }
    }

    public String getOutMapping(String parameterName) {
        return getOutMappings().get(parameterName);
    }

    public Map<String, String> getOutMappings() {
        Map<String, String> out = new HashMap<String, String>();
        for (DataAssociation a : outMapping) {
            if ((((a.getSources().size()) == 1) && (((a.getAssignments()) == null) || ((a.getAssignments().size()) == 0))) && ((a.getTransformation()) == null)) {
                out.put(a.getSources().get(0), a.getTarget());
            } 
        }
        return out;
    }

    public void adjustOutMapping(String forEachOutVariable) {
        if (forEachOutVariable == null) {
            return ;
        } 
        Iterator<DataAssociation> it = outMapping.iterator();
        while (it.hasNext()) {
            DataAssociation association = it.next();
            if (forEachOutVariable.equals(association.getTarget())) {
                it.remove();
            } 
        }
    }

    public void addOutAssociation(DataAssociation dataAssociation) {
        outMapping.add(dataAssociation);
    }

    public List<DataAssociation> getOutAssociations() {
        return Collections.unmodifiableList(outMapping);
    }

    public boolean isIndependent() {
        return independent;
    }

    public void setIndependent(boolean independent) {
        SubProcessNode.this.independent = independent;
    }

    public void validateAddIncomingConnection(final String type, final Connection connection) {
        super.validateAddIncomingConnection(type, connection);
        if (!(org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type))) {
            throw new IllegalArgumentException((((("This type of node [" + (connection.getTo().getMetaData().get("UniqueId"))) + ", ") + (connection.getTo().getName())) + "] only accepts default incoming connection type!"));
        } 
        if (((getFrom()) != null) && (!("true".equals(System.getProperty("jbpm.enable.multi.con"))))) {
            throw new IllegalArgumentException((((("This type of node [" + (connection.getTo().getMetaData().get("UniqueId"))) + ", ") + (connection.getTo().getName())) + "] cannot have more than one incoming connection!"));
        } 
    }

    public void validateAddOutgoingConnection(final String type, final Connection connection) {
        super.validateAddOutgoingConnection(type, connection);
        if (!(org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type))) {
            throw new IllegalArgumentException((((("This type of node [" + (connection.getFrom().getMetaData().get("UniqueId"))) + ", ") + (connection.getFrom().getName())) + "] only accepts default outgoing connection type!"));
        } 
        if (((getTo()) != null) && (!("true".equals(System.getProperty("jbpm.enable.multi.con"))))) {
            throw new IllegalArgumentException((((("This type of node [" + (connection.getFrom().getMetaData().get("UniqueId"))) + ", ") + (connection.getFrom().getName())) + "] cannot have more than one outgoing connection!"));
        } 
    }

    public void setProcessName(String processName) {
        SubProcessNode.this.processName = processName;
    }

    public String getProcessName() {
        return processName;
    }

    public List<Context> getContexts(String contextType) {
        return contextContainer.getContexts(contextType);
    }

    public void addContext(Context context) {
        ((AbstractContext) (context)).setContextContainer(SubProcessNode.this);
        contextContainer.addContext(context);
    }

    public Context getContext(String contextType, long id) {
        return contextContainer.getContext(contextType, id);
    }

    public void setDefaultContext(Context context) {
        ((AbstractContext) (context)).setContextContainer(SubProcessNode.this);
        contextContainer.setDefaultContext(context);
    }

    public Context getDefaultContext(String contextType) {
        return contextContainer.getDefaultContext(contextType);
    }

    @Override
    public Context getContext(String contextId) {
        Context context = getDefaultContext(contextId);
        if (context != null) {
            return context;
        } 
        return super.getContext(contextId);
    }
}

