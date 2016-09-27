/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.kie.services.impl;

import org.jbpm.kie.services.api.AttributesAware;
import java.util.Collections;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.jbpm.services.api.model.DeploymentUnit;
import java.util.HashMap;
import org.kie.api.runtime.KieContainer;
import java.util.Map;
import org.kie.internal.runtime.conf.MergeMode;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import java.io.Serializable;
import org.drools.core.util.StringUtils;

public class KModuleDeploymentUnit implements Serializable , AttributesAware , DeploymentUnit {
    private static final long serialVersionUID = 1L;

    private String artifactId;

    private String groupId;

    private String version;

    private String kbaseName;

    private String ksessionName;

    private RuntimeStrategy strategy = RuntimeStrategy.SINGLETON;

    private MergeMode mergeMode = MergeMode.MERGE_COLLECTIONS;

    private DeploymentDescriptor deploymentDescriptor;

    private boolean deployed = false;

    private boolean strategyUnset = true;

    private transient KieContainer kieContainer;

    private Map<String, String> attributes = new HashMap<String, String>();

    public KModuleDeploymentUnit(String groupId, String artifactId, String version) {
        KModuleDeploymentUnit.this.groupId = groupId;
        KModuleDeploymentUnit.this.artifactId = artifactId;
        KModuleDeploymentUnit.this.version = version;
    }

    public KModuleDeploymentUnit(String groupId, String artifactId, String version, String kbaseName, String ksessionName) {
        this(groupId, artifactId, version);
        KModuleDeploymentUnit.this.kbaseName = kbaseName;
        KModuleDeploymentUnit.this.ksessionName = ksessionName;
    }

    public KModuleDeploymentUnit(String groupId, String artifactId, String version, String kbaseName, String ksessionName, String strategy) {
        this(groupId, artifactId, version, kbaseName, ksessionName);
        KModuleDeploymentUnit.this.strategy = RuntimeStrategy.valueOf(strategy);
        KModuleDeploymentUnit.this.strategyUnset = false;
    }

    @Override
    public String getIdentifier() {
        String id = ((((getGroupId()) + ":") + (getArtifactId())) + ":") + (getVersion());
        boolean kbaseFilled = !(StringUtils.isEmpty(kbaseName));
        boolean ksessionFilled = !(StringUtils.isEmpty(ksessionName));
        if (kbaseFilled || ksessionFilled) {
            id = id.concat(":");
            if (kbaseFilled) {
                id = id.concat(kbaseName);
            } 
            if (ksessionFilled) {
                id = id.concat((":" + (ksessionName)));
            } 
        } 
        return id;
    }

    @Override
    public RuntimeStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(RuntimeStrategy strategy) {
        if (strategyUnset) {
            KModuleDeploymentUnit.this.strategy = strategy;
            KModuleDeploymentUnit.this.strategyUnset = false;
        } 
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        KModuleDeploymentUnit.this.artifactId = artifactId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        KModuleDeploymentUnit.this.groupId = groupId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        KModuleDeploymentUnit.this.version = version;
    }

    public String getKsessionName() {
        return ksessionName;
    }

    public void setKsessionName(String ksessionName) {
        KModuleDeploymentUnit.this.ksessionName = ksessionName;
    }

    public String getKbaseName() {
        return kbaseName;
    }

    public void setKbaseName(String kbaseName) {
        KModuleDeploymentUnit.this.kbaseName = kbaseName;
    }

    @Override
    public String toString() {
        return (((getIdentifier()) + " [strategy=") + (strategy)) + "]";
    }

    public MergeMode getMergeMode() {
        if ((mergeMode) == null) {
            mergeMode = MergeMode.MERGE_COLLECTIONS;
        } 
        return mergeMode;
    }

    public void setMergeMode(MergeMode mergeMode) {
        KModuleDeploymentUnit.this.mergeMode = mergeMode;
    }

    public DeploymentDescriptor getDeploymentDescriptor() {
        return deploymentDescriptor;
    }

    public void setDeploymentDescriptor(DeploymentDescriptor deploymentDescriptor) {
        KModuleDeploymentUnit.this.deploymentDescriptor = deploymentDescriptor;
    }

    public boolean isDeployed() {
        return deployed;
    }

    public void setDeployed(boolean deployed) {
        KModuleDeploymentUnit.this.deployed = deployed;
    }

    public void resetStrategy() {
        KModuleDeploymentUnit.this.strategyUnset = true;
    }

    @Override
    public void addAttribute(String name, String value) {
        KModuleDeploymentUnit.this.attributes.put(name, value);
    }

    @Override
    public String removeAttribute(String name) {
        return KModuleDeploymentUnit.this.attributes.remove(name);
    }

    @Override
    public Map<String, String> getAttributes() {
        if ((KModuleDeploymentUnit.this.attributes) == null) {
            return Collections.EMPTY_MAP;
        } 
        return Collections.unmodifiableMap(KModuleDeploymentUnit.this.attributes);
    }

    public KieContainer getKieContainer() {
        return kieContainer;
    }

    public void setKieContainer(KieContainer kieContainer) {
        KModuleDeploymentUnit.this.kieContainer = kieContainer;
    }
}

