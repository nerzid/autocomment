/**
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.process.core.context.variable;

import org.drools.core.process.core.datatype.DataType;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;
import org.drools.core.process.core.TypeObject;
import org.drools.core.process.core.datatype.impl.type.UndefinedDataType;
import org.jbpm.process.core.ValueObject;

/**
 * Default implementation of a variable.
 */
public class Variable implements Serializable , TypeObject , ValueObject {
    private static final long serialVersionUID = 510L;

    private String name;

    private DataType type;

    private Object value;

    private Map<String, Object> metaData = new HashMap<String, Object>();

    public Variable() {
        Variable.this.type = UndefinedDataType.getInstance();
    }

    public String getName() {
        return Variable.this.name;
    }

    public void setName(final String name) {
        Variable.this.name = name;
    }

    public DataType getType() {
        return Variable.this.type;
    }

    public void setType(final DataType type) {
        if (type == null) {
            throw new IllegalArgumentException("type is null");
        } 
        Variable.this.type = type;
    }

    public Object getValue() {
        return Variable.this.value;
    }

    public void setValue(final Object value) {
        if (Variable.this.type.verifyDataType(value)) {
            Variable.this.value = value;
        } else {
            final StringBuilder sb = new StringBuilder();
            sb.append("Value <");
            sb.append(value);
            sb.append("> is not valid for datatype: ");
            sb.append(Variable.this.type);
            throw new IllegalArgumentException(sb.toString());
        }
    }

    public void setMetaData(String name, Object value) {
        Variable.this.metaData.put(name, value);
    }

    public Object getMetaData(String name) {
        return Variable.this.metaData.get(name);
    }

    public Map<String, Object> getMetaData() {
        return Variable.this.metaData;
    }

    public String toString() {
        return Variable.this.name;
    }
}

