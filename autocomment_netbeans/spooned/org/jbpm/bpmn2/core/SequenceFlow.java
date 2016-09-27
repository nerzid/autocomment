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


package org.jbpm.bpmn2.core;

import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public class SequenceFlow implements Serializable {
    private static final long serialVersionUID = 510L;

    private String id;

    private String sourceRef;

    private String targetRef;

    private String bendpoints;

    private String expression;

    private String type;

    private String language;

    private String name;

    private int priority;

    private Map<String, Object> metaData = new HashMap<String, Object>();

    public SequenceFlow(String id, String sourceRef, String targetRef) {
        SequenceFlow.this.id = id;
        SequenceFlow.this.sourceRef = sourceRef;
        SequenceFlow.this.targetRef = targetRef;
    }

    public String getId() {
        return id;
    }

    public String getSourceRef() {
        return sourceRef;
    }

    public String getTargetRef() {
        return targetRef;
    }

    public String getBendpoints() {
        return bendpoints;
    }

    public void setBendpoints(String bendpoints) {
        SequenceFlow.this.bendpoints = bendpoints;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        SequenceFlow.this.expression = expression;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        SequenceFlow.this.language = language;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        SequenceFlow.this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        SequenceFlow.this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        SequenceFlow.this.priority = priority;
    }

    public Map<String, Object> getMetaData() {
        return SequenceFlow.this.metaData;
    }

    public void setMetaData(String name, Object data) {
        SequenceFlow.this.metaData.put(name, data);
    }

    public String toString() {
        return ((((("SequenceFlow (" + (SequenceFlow.this.id)) + ") [") + (SequenceFlow.this.sourceRef)) + " -> ") + (SequenceFlow.this.targetRef)) + "]";
    }
}

