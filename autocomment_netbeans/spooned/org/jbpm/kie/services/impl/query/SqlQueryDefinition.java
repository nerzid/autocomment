/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.kie.services.impl.query;

import java.util.HashMap;
import java.util.Map;
import org.jbpm.services.api.query.model.QueryDefinition;
import java.io.Serializable;

public class SqlQueryDefinition implements Serializable , QueryDefinition {
    private static final long serialVersionUID = 1L;

    private String name;

    private String source;

    private String expression;

    private Target target = Target.CUSTOM;

    private Map<String, String> columnsMapping = new HashMap<String, String>();

    public SqlQueryDefinition(String name, String source) {
        SqlQueryDefinition.this.name = name;
        SqlQueryDefinition.this.source = source;
    }

    public SqlQueryDefinition(String name, String source, Target target) {
        SqlQueryDefinition.this.name = name;
        SqlQueryDefinition.this.source = source;
        SqlQueryDefinition.this.target = target;
    }

    @Override
    public String getName() {
        return SqlQueryDefinition.this.name;
    }

    @Override
    public void setName(String name) {
        SqlQueryDefinition.this.name = name;
    }

    @Override
    public String getSource() {
        return SqlQueryDefinition.this.source;
    }

    @Override
    public void setSource(String source) {
        SqlQueryDefinition.this.source = source;
    }

    @Override
    public String getExpression() {
        return SqlQueryDefinition.this.expression;
    }

    @Override
    public void setExpression(String expression) {
        SqlQueryDefinition.this.expression = expression;
    }

    @Override
    public Target getTarget() {
        return SqlQueryDefinition.this.target;
    }

    public void setTarget(Target target) {
        SqlQueryDefinition.this.target = target;
    }

    @Override
    public String toString() {
        return (((((((("SqlQueryDefinition [name=" + (name)) + ", source=") + (source)) + ", target=") + (target)) + ", ") + "{ expression=") + (expression)) + "}]";
    }

    public Map<String, String> getColumnsMapping() {
        return columnsMapping;
    }

    public void setColumnsMapping(Map<String, String> columnsMapping) {
        SqlQueryDefinition.this.columnsMapping = columnsMapping;
    }
}

