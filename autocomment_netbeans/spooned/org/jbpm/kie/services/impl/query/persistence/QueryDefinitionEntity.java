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


package org.jbpm.kie.services.impl.query.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import org.jbpm.services.api.query.model.QueryDefinition;
import javax.persistence.SequenceGenerator;
import org.jbpm.kie.services.impl.query.SqlQueryDefinition;
import javax.persistence.Table;
import org.jbpm.services.api.query.model.QueryDefinition.Target;
import javax.persistence.UniqueConstraint;

/* Named queries defined in services orm file */
@Entity
@Table(name = "QueryDefinitionStore", uniqueConstraints = { @UniqueConstraint(columnNames = "qName") })
@SequenceGenerator(allocationSize = 1, name = "queryDefIdSeq", sequenceName = "QUERY_DEF_ID_SEQ")
public class QueryDefinitionEntity {
    @Id
    @GeneratedValue(generator = "queryDefIdSeq", strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(length = 65535, name = "qExpression")
    private String expression;

    @Column(name = "qName")
    private String name;

    @Column(name = "qSource")
    private String source;

    @Column(name = "qTarget")
    private String target;

    public QueryDefinitionEntity() {
    }

    public QueryDefinitionEntity(QueryDefinition queryDefinition) {
        QueryDefinitionEntity.this.name = queryDefinition.getName();
        QueryDefinitionEntity.this.source = queryDefinition.getSource();
        QueryDefinitionEntity.this.expression = queryDefinition.getExpression();
        QueryDefinitionEntity.this.target = queryDefinition.getTarget().toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        QueryDefinitionEntity.this.id = id;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        QueryDefinitionEntity.this.expression = expression;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        QueryDefinitionEntity.this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        QueryDefinitionEntity.this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        QueryDefinitionEntity.this.target = target;
    }

    @Override
    public String toString() {
        return ((((((((("QueryDefinitionEntity [id=" + (id)) + ", name=") + (name)) + ", source=") + (source)) + ", target=") + (target)) + ", {expression=") + (expression)) + "}]";
    }

    public QueryDefinition toQueryDefinition() {
        SqlQueryDefinition queryDefinition = new SqlQueryDefinition(name, source);
        queryDefinition.setExpression(expression);
        queryDefinition.setTarget(Target.valueOf(target));
        return queryDefinition;
    }
}

