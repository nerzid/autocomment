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


package org.jbpm.bpmn2.core;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class IntermediateLink implements Serializable {
    private static final long serialVersionUID = 201105091147L;

    private static final String THROW = "throw";

    private String uniqueId;

    private String target;

    private String name;

    private String type = null;

    private List<String> sources;

    public IntermediateLink() {
        IntermediateLink.this.sources = new ArrayList<String>();
    }

    public void setUniqueId(String id) {
        IntermediateLink.this.uniqueId = id;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setTarget(String target) {
        IntermediateLink.this.target = target;
    }

    public String getTarget() {
        return target;
    }

    public void addSource(String sources) {
        IntermediateLink.this.sources.add(sources);
    }

    public List<String> getSources() {
        return sources;
    }

    public void setName(String name) {
        IntermediateLink.this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isThrowLink() {
        return IntermediateLink.THROW.equals(type);
    }

    /**
     * Turn this link into a throw link.
     */
    public void configureThrow() {
        IntermediateLink.this.type = IntermediateLink.THROW;
    }
}

