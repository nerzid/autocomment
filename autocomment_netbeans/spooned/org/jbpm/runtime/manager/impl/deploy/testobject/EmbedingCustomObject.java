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


package org.jbpm.runtime.manager.impl.deploy.testobject;


public class EmbedingCustomObject {
    private SimpleCustomObject customObject;

    private String description;

    public EmbedingCustomObject() {
    }

    public EmbedingCustomObject(SimpleCustomObject customObject, String description) {
        EmbedingCustomObject.this.setCustomObject(customObject);
        EmbedingCustomObject.this.setDescription(description);
    }

    public SimpleCustomObject getCustomObject() {
        return customObject;
    }

    public void setCustomObject(SimpleCustomObject customObject) {
        EmbedingCustomObject.this.customObject = customObject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        EmbedingCustomObject.this.description = description;
    }
}

