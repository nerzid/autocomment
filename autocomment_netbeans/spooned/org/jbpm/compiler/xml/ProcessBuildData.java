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
/**
 *
 */


package org.jbpm.compiler.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.jbpm.workflow.core.Node;
import java.util.ServiceLoader;

public class ProcessBuildData {
    private static final Logger logger = LoggerFactory.getLogger(ProcessBuildData.class);

    private static List<ProcessDataEventListenerProvider> providers = ProcessBuildData.collectProviders();

    private List<Process> processes = new ArrayList<Process>();

    private Map<Long, Node> nodes = new HashMap<Long, Node>();

    private Map<String, Object> metaData = new HashMap<String, Object>();

    private List<ProcessDataEventListener> listeners = new ArrayList<ProcessDataEventListener>();

    public ProcessBuildData() {
        if ((ProcessBuildData.providers) != null) {
            for (ProcessDataEventListenerProvider provider : ProcessBuildData.providers) {
                listeners.add(provider.newInstance());
            }
        }
    }

    public List<Process> getProcesses() {
        for (Process process : processes) {
            onComplete(process);
        }
        return processes;
    }

    public void addProcess(Process process) {
        // on process Process{process} to ProcessBuildData{}
        onProcess(process);
        // add Process{process} to List{this.processes}
        this.processes.add(process);
    }

    public void setProcesses(List<Process> process) {
        this.processes = process;
    }

    public Map<Long, Node> getNodes() {
        return nodes;
    }

    public boolean addNode(Node node) {
        // on node Node{node} to ProcessBuildData{}
        onNode(node);
        return (this.nodes.put(node.getId(), node)) != null;
    }

    public Node getNode(Long id) {
        return this.nodes.get(id);
    }

    public Object getMetaData(String name) {
        return metaData.get(name);
    }

    public void setMetaData(String name, Object data) {
        // on meta String{name} to ProcessBuildData{}
        onMetaData(name, data);
        // put String{name} to Map{this.metaData}
        this.metaData.put(name, data);
    }

    // listener support
    protected void onNode(Node node) {
        for (ProcessDataEventListener listener : listeners) {
            listener.onNodeAdded(node);
        }
    }

    protected void onProcess(Process process) {
        for (ProcessDataEventListener listener : listeners) {
            listener.onProcessAdded(process);
        }
    }

    protected void onMetaData(String name, Object data) {
        for (ProcessDataEventListener listener : listeners) {
            listener.onMetaDataAdded(name, data);
        }
    }

    protected void onComplete(Process process) {
        for (ProcessDataEventListener listener : listeners) {
            listener.onComplete(process);
        }
    }

    public void onBuildComplete(Process process) {
        for (ProcessDataEventListener listener : listeners) {
            listener.onBuildComplete(process);
        }
    }

    private static List<ProcessDataEventListenerProvider> collectProviders() {
        ServiceLoader<ProcessDataEventListenerProvider> availableProviders = ServiceLoader.load(ProcessDataEventListenerProvider.class);
        List<ProcessDataEventListenerProvider> collected = new ArrayList<ProcessDataEventListenerProvider>();
        try {
            for (ProcessDataEventListenerProvider provider : availableProviders) {
                collected.add(provider);
            }
        } catch (Throwable e) {
            ProcessBuildData.logger.debug("Unable to collect process data event listeners due to {}", e.getMessage());
        }
        return collected;
    }
}

