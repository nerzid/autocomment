/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.process.core.impl;

import org.jbpm.process.core.context.AbstractContext;
import java.util.ArrayList;
import org.jbpm.process.core.Context;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.ContextResolver;
import java.util.HashMap;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.ObjectInputStream;
import org.kie.api.io.Resource;
import java.io.Serializable;
import java.util.Set;

/**
 * Default implementation of a Process
 */
public class ProcessImpl implements Serializable , Process , ContextResolver {
    private static final long serialVersionUID = 510L;

    private String id;

    private String name;

    private String version;

    private String type;

    private String packageName;

    private Resource resource;

    private ContextContainer contextContainer = new ContextContainerImpl();

    private Map<String, Object> metaData = new HashMap<String, Object>();

    private transient Map<String, Object> runtimeMetaData = new HashMap<String, Object>();

    private Set<String> imports;

    private Map<String, String> globals;

    private List<String> functionImports;

    public void setId(final String id) {
        org.jbpm.process.core.impl.ProcessImpl.this.id = id;
    }

    public String getId() {
        return org.jbpm.process.core.impl.ProcessImpl.this.id;
    }

    public void setName(final String name) {
        org.jbpm.process.core.impl.ProcessImpl.this.name = name;
    }

    public String getName() {
        return org.jbpm.process.core.impl.ProcessImpl.this.name;
    }

    public void setVersion(final String version) {
        org.jbpm.process.core.impl.ProcessImpl.this.version = version;
    }

    public String getVersion() {
        return org.jbpm.process.core.impl.ProcessImpl.this.version;
    }

    public String getType() {
        return org.jbpm.process.core.impl.ProcessImpl.this.type;
    }

    public void setType(final String type) {
        org.jbpm.process.core.impl.ProcessImpl.this.type = type;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        org.jbpm.process.core.impl.ProcessImpl.this.packageName = packageName;
    }

    public List<Context> getContexts(String contextType) {
        return org.jbpm.process.core.impl.ProcessImpl.this.contextContainer.getContexts(contextType);
    }

    public void addContext(Context context) {
        org.jbpm.process.core.impl.ProcessImpl.this.contextContainer.addContext(context);
        ((AbstractContext) (context)).setContextContainer(org.jbpm.process.core.impl.ProcessImpl.this);
    }

    public Context getContext(String contextType, long id) {
        return org.jbpm.process.core.impl.ProcessImpl.this.contextContainer.getContext(contextType, id);
    }

    public void setDefaultContext(Context context) {
        org.jbpm.process.core.impl.ProcessImpl.this.contextContainer.setDefaultContext(context);
        ((AbstractContext) (context)).setContextContainer(org.jbpm.process.core.impl.ProcessImpl.this);
    }

    public Context getDefaultContext(String contextType) {
        return org.jbpm.process.core.impl.ProcessImpl.this.contextContainer.getDefaultContext(contextType);
    }

    public boolean equals(final Object o) {
        if (o instanceof org.jbpm.process.core.impl.ProcessImpl) {
            if ((org.jbpm.process.core.impl.ProcessImpl.this.id) == null) {
                return (((org.jbpm.process.core.impl.ProcessImpl) (o)).getId()) == null;
            } 
            return org.jbpm.process.core.impl.ProcessImpl.this.id.equals(((org.jbpm.process.core.impl.ProcessImpl) (o)).getId());
        } 
        return false;
    }

    public int hashCode() {
        return (org.jbpm.process.core.impl.ProcessImpl.this.id) == null ? 0 : 3 * (org.jbpm.process.core.impl.ProcessImpl.this.id.hashCode());
    }

    public Context resolveContext(String contextId, Object param) {
        Context context = getDefaultContext(contextId);
        if (context != null) {
            context = context.resolveContext(param);
            if (context != null) {
                return context;
            } 
        } 
        return null;
    }

    public Map<String, Object> getMetaData() {
        return org.jbpm.process.core.impl.ProcessImpl.this.metaData;
    }

    public void setMetaData(String name, Object data) {
        org.jbpm.process.core.impl.ProcessImpl.this.metaData.put(name, data);
    }

    public Object getMetaData(String name) {
        return org.jbpm.process.core.impl.ProcessImpl.this.metaData.get(name);
    }

    public Resource getResource() {
        return org.jbpm.process.core.impl.ProcessImpl.this.resource;
    }

    public void setResource(Resource resource) {
        org.jbpm.process.core.impl.ProcessImpl.this.resource = resource;
    }

    public Set<String> getImports() {
        return imports;
    }

    public void setImports(Set<String> imports) {
        org.jbpm.process.core.impl.ProcessImpl.this.imports = imports;
    }

    public List<String> getFunctionImports() {
        return functionImports;
    }

    public void setFunctionImports(List<String> functionImports) {
        org.jbpm.process.core.impl.ProcessImpl.this.functionImports = functionImports;
    }

    public Map<String, String> getGlobals() {
        return globals;
    }

    public void setGlobals(Map<String, String> globals) {
        org.jbpm.process.core.impl.ProcessImpl.this.globals = globals;
    }

    public String[] getGlobalNames() {
        final List<String> result = new ArrayList<String>();
        if ((org.jbpm.process.core.impl.ProcessImpl.this.globals) != null) {
            for (Iterator<String> iterator = org.jbpm.process.core.impl.ProcessImpl.this.globals.keySet().iterator(); iterator.hasNext();) {
                result.add(iterator.next());
            }
        } 
        return result.toArray(new String[result.size()]);
    }

    public KnowledgeType getKnowledgeType() {
        return KnowledgeType.PROCESS;
    }

    public String getNamespace() {
        return packageName;
    }

    public Map<String, Object> getRuntimeMetaData() {
        return runtimeMetaData;
    }

    public void setRuntimeMetaData(Map<String, Object> runtimeMetaData) {
        org.jbpm.process.core.impl.ProcessImpl.this.runtimeMetaData = runtimeMetaData;
    }

    /* Special handling for serialization to initialize transient (runtime related) meta data */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        org.jbpm.process.core.impl.ProcessImpl.this.runtimeMetaData = new HashMap<String, Object>();
    }
}

