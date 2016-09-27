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


package org.jbpm.services.api.query.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.Serializable;

/**
 * Basic data carrier to provide filtering capabilities on top of query definition.
 */
public class QueryParam implements Serializable {
    private static final long serialVersionUID = -7751811350486978746L;

    private String column;

    private String operator;

    private List<?> value;

    public QueryParam(String column, String operator, List<?> value) {
        QueryParam.this.column = column;
        QueryParam.this.operator = operator;
        QueryParam.this.value = value;
    }

    public static QueryParam isNull(String column) {
        return new QueryParam(column, "IS_NULL", null);
    }

    public static QueryParam isNotNull(String column) {
        return new QueryParam(column, "NOT_NULL", null);
    }

    public static QueryParam equalsTo(String column, Comparable<?>... values) {
        return new QueryParam(column, "EQUALS_TO", Arrays.asList(values));
    }

    public static QueryParam notEqualsTo(String column, Comparable<?>... values) {
        return new QueryParam(column, "NOT_EQUALS_TO", Arrays.asList(values));
    }

    @SuppressWarnings(value = "unchecked")
    public static QueryParam likeTo(String column, boolean caseSensitive, Comparable<?> value) {
        return new QueryParam(column, "LIKE_TO", Arrays.asList(value, caseSensitive));
    }

    @SuppressWarnings(value = "unchecked")
    public static QueryParam greaterThan(String column, Comparable<?> value) {
        return new QueryParam(column, "GREATER_THAN", Arrays.asList(value));
    }

    @SuppressWarnings(value = "unchecked")
    public static QueryParam greaterOrEqualTo(String column, Comparable<?> value) {
        return new QueryParam(column, "GREATER_OR_EQUALS_TO", Arrays.asList(value));
    }

    @SuppressWarnings(value = "unchecked")
    public static QueryParam lowerThan(String column, Comparable<?> value) {
        return new QueryParam(column, "LOWER_THAN", Arrays.asList(value));
    }

    @SuppressWarnings(value = "unchecked")
    public static QueryParam lowerOrEqualTo(String column, Comparable<?> value) {
        return new QueryParam(column, "LOWER_OR_EQUALS_TO", Arrays.asList(value));
    }

    @SuppressWarnings(value = "unchecked")
    public static QueryParam between(String column, Comparable<?> start, Comparable<?> end) {
        return new QueryParam(column, "BETWEEN", Arrays.asList(start, end));
    }

    public static QueryParam in(String column, List<?> values) {
        return new QueryParam(column, "IN", values);
    }

    public static QueryParam notIn(String column, List<?> values) {
        return new QueryParam(column, "NOT_IN", values);
    }

    public static QueryParam count(String column) {
        return new QueryParam(column, "COUNT", Arrays.asList(column));
    }

    public static QueryParam distinct(String column) {
        return new QueryParam(column, "DISTINCT", Arrays.asList(column));
    }

    public static QueryParam average(String column) {
        return new QueryParam(column, "AVERAGE", Arrays.asList(column));
    }

    public static QueryParam sum(String column) {
        return new QueryParam(column, "SUM", Arrays.asList(column));
    }

    public static QueryParam min(String column) {
        return new QueryParam(column, "MIN", Arrays.asList(column));
    }

    public static QueryParam max(String column) {
        return new QueryParam(column, "MAX", Arrays.asList(column));
    }

    public static QueryParam[] groupBy(String column) {
        return new QueryParam[]{ new QueryParam(column, "group", Arrays.asList(column)) , new QueryParam(column, null, Arrays.asList(column)) };
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        QueryParam.this.column = column;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        QueryParam.this.operator = operator;
    }

    public List<?> getValue() {
        return value;
    }

    public void setValue(List<?> value) {
        QueryParam.this.value = value;
    }

    public static QueryParam.Builder getBuilder() {
        return new QueryParam.Builder();
    }

    public static class Builder {
        private List<QueryParam> parameters = new ArrayList<QueryParam>();

        public QueryParam.Builder append(QueryParam... params) {
            QueryParam.Builder.this.parameters.addAll(Arrays.asList(params));
            return QueryParam.Builder.this;
        }

        public QueryParam[] get() {
            return QueryParam.Builder.this.parameters.toArray(new QueryParam[QueryParam.Builder.this.parameters.size()]);
        }
    }
}

