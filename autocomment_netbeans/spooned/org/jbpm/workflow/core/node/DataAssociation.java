/**
 * Copyright 2010 Intalio Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.jbpm.workflow.core.node;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class DataAssociation implements Serializable {
    private static final long serialVersionUID = 5L;

    private List<String> sources;

    private String target;

    private List<Assignment> assignments;

    private Transformation transformation;

    public DataAssociation(List<String> sources, String target, List<Assignment> assignments, Transformation transformation) {
        DataAssociation.this.sources = sources;
        DataAssociation.this.target = target;
        DataAssociation.this.assignments = assignments;
        DataAssociation.this.transformation = transformation;
    }

    public DataAssociation(final String source, String target, List<Assignment> assignments, Transformation transformation) {
        DataAssociation.this.sources = new ArrayList<String>();
        DataAssociation.this.sources.add(source);
        DataAssociation.this.target = target;
        DataAssociation.this.assignments = assignments;
        DataAssociation.this.transformation = transformation;
    }

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        DataAssociation.this.sources = sources;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        DataAssociation.this.target = target;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<Assignment> assignments) {
        DataAssociation.this.assignments = assignments;
    }

    public Transformation getTransformation() {
        return transformation;
    }

    public void setTransformation(Transformation transformation) {
        if (transformation != null) {
            throw new UnsupportedOperationException("Transformations are not supported");
        } 
        // this.transformation = transformation;
    }
}

