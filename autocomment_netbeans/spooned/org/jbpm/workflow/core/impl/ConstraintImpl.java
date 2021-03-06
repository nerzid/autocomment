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


package org.jbpm.workflow.core.impl;

import org.jbpm.workflow.core.Constraint;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

/**
 * Default implementation of a constraint.
 */
public class ConstraintImpl implements Serializable , Constraint {
    private static final long serialVersionUID = 510L;

    private Map<String, Object> metaData = new HashMap<String, Object>();

    private String name;

    private String constraint;

    private int priority;

    private String dialect = "mvel";

    private String type = "rule";

    private boolean isDefault = false;

    public String getConstraint() {
        return ConstraintImpl.this.constraint;
    }

    public void setConstraint(final String constraint) {
        ConstraintImpl.this.constraint = constraint;
    }

    public String getName() {
        return ConstraintImpl.this.name;
    }

    public void setName(final String name) {
        ConstraintImpl.this.name = name;
    }

    public String toString() {
        return ConstraintImpl.this.name;
    }

    public int getPriority() {
        return ConstraintImpl.this.priority;
    }

    public void setPriority(final int priority) {
        ConstraintImpl.this.priority = priority;
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        ConstraintImpl.this.dialect = dialect;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        ConstraintImpl.this.type = type;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        ConstraintImpl.this.isDefault = isDefault;
    }

    public void setMetaData(String name, Object value) {
        ConstraintImpl.this.metaData.put(name, value);
    }

    public Object getMetaData(String name) {
        return ConstraintImpl.this.metaData.get(name);
    }
}

