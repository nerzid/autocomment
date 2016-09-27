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

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArraySet;
import org.jbpm.services.api.model.DeployedAsset;
import org.jbpm.services.api.model.DeployedUnit;
import org.jbpm.services.api.model.DeploymentUnit;
import java.util.HashMap;
import java.util.Map;
import org.jbpm.kie.services.impl.model.ProcessAssetDesc;
import org.kie.api.runtime.manager.RuntimeManager;
import java.util.Set;

public class DeployedUnitImpl implements DeployedUnit {
    private DeploymentUnit unit;

    private RuntimeManager manager;

    private boolean active = true;

    private Map<String, DeployedAsset> assets = new HashMap<String, DeployedAsset>();

    private Set<Class<?>> classes = new CopyOnWriteArraySet<Class<?>>();

    public DeployedUnitImpl(DeploymentUnit unit) {
        DeployedUnitImpl.this.unit = unit;
    }

    @Override
    public DeploymentUnit getDeploymentUnit() {
        return DeployedUnitImpl.this.unit;
    }

    @Override
    public String getDeployedAssetLocation(String assetId) {
        return DeployedUnitImpl.this.assets.get(assetId).getOriginalPath();
    }

    @Override
    public RuntimeManager getRuntimeManager() {
        return DeployedUnitImpl.this.manager;
    }

    public void addAssetLocation(String assetId, ProcessAssetDesc processAsset) {
        DeployedUnitImpl.this.assets.put(assetId, processAsset);
    }

    public void addClass(Class<?> kModuleClass) {
        DeployedUnitImpl.this.classes.add(kModuleClass);
    }

    public void setRuntimeManager(RuntimeManager manager) {
        if ((DeployedUnitImpl.this.manager) != null) {
            throw new IllegalStateException("RuntimeManager already exists");
        } 
        DeployedUnitImpl.this.manager = manager;
    }

    @Override
    public Collection<DeployedAsset> getDeployedAssets() {
        return Collections.unmodifiableCollection(assets.values());
    }

    @Override
    public Collection<Class<?>> getDeployedClasses() {
        return Collections.unmodifiableCollection(classes);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        DeployedUnitImpl.this.active = active;
    }
}

