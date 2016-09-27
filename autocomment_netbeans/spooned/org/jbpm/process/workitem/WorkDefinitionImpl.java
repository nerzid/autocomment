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


package org.jbpm.process.workitem;

import org.drools.core.process.core.impl.WorkDefinitionExtensionImpl;

public class WorkDefinitionImpl extends WorkDefinitionExtensionImpl {
    private static final long serialVersionUID = 5L;

    private String[] dependencies;

    private String[] mavenDependencies;

    private String description;

    private String defaultHandler;

    private String category;

    private String path;

    private String file;

    private String documentation;

    private String iconEncoded;

    private String version;

    private String widType;

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        WorkDefinitionImpl.this.documentation = documentation;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        WorkDefinitionImpl.this.file = file;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        WorkDefinitionImpl.this.path = path;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        WorkDefinitionImpl.this.category = category;
    }

    public String[] getDependencies() {
        return dependencies;
    }

    public void setDependencies(String[] dependencies) {
        WorkDefinitionImpl.this.dependencies = dependencies;
    }

    public String getDefaultHandler() {
        return defaultHandler;
    }

    public void setDefaultHandler(String defaultHandler) {
        WorkDefinitionImpl.this.defaultHandler = defaultHandler;
    }

    public String getIconEncoded() {
        return iconEncoded;
    }

    public void setIconEncoded(String iconEncoded) {
        WorkDefinitionImpl.this.iconEncoded = iconEncoded;
    }

    public String[] getMavenDependencies() {
        return mavenDependencies;
    }

    public void setMavenDependencies(String[] mavenDependencies) {
        WorkDefinitionImpl.this.mavenDependencies = mavenDependencies;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        WorkDefinitionImpl.this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        WorkDefinitionImpl.this.description = description;
    }

    public String getWidType() {
        return widType;
    }

    public void setWidType(String widType) {
        WorkDefinitionImpl.this.widType = widType;
    }
}

