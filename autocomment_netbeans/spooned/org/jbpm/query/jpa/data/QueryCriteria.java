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


package org.jbpm.query.jpa.data;

import java.util.ArrayList;
import java.util.Date;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import java.util.List;
import org.jbpm.query.jpa.data.QueryWhere.QueryCriteriaType;
import java.text.SimpleDateFormat;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This object contains the following information:
 * <ol>
 * <li>The listId, which refers to the field that this criteria applies to<ul>
 *   <li>See {@link QueryParameterIdentifiers}</li></ul>
 * </li>
 * <li>The values of the criteria, which will be applied to the listId field<ul>
 *   <li>For example, it could be a list of numbers "1, 22, 3"</li></ul>
 * </li>
 * <li>Whether this is a union ("OR") or intersection ("AND") critieria</li>
 * <li>The type of criteria: normal, like (JPQL regex) or range</li>
 * <li>The grouping information of the phrase (see below)</li>
 * </ol>
 * </p>
 * With regard to the grouping information in this class, we treat JPQL/SQL as a "prefix" language here, which means that
 * this class represents the following regular expression/BNF string:
 * <pre>
 *   [)]{0,} [OR|AND] [(]{0,} &lt;CRITERIA&gt;
 * </pre>
 * This structure is then represented by the following fields:
 * <pre>
 *   [endGroups] [union] [startGroupos] [values]
 * </pre>
 * 
 * The main reason to include the grouping status in this object is that other data structures (nested lists, etc)
 * are much harder to de/serialize correctly.
 */
@XmlRootElement
@XmlType
@XmlAccessorType(value = XmlAccessType.FIELD)
@JsonIgnoreProperties(value = "parameters")
public class QueryCriteria {
    @XmlAttribute
    private String listId;

    @XmlAttribute
    private boolean union = true;

    @XmlAttribute
    private boolean first = false;

    @XmlAttribute
    private QueryCriteriaType type = QueryCriteriaType.NORMAL;

    @XmlElement(name = "parameter")
    @JsonTypeInfo(include = JsonTypeInfo.As.PROPERTY, property = "class", use = JsonTypeInfo.Id.CLASS)
    private List<Object> values;

    @XmlElement(name = "date-parameter")
    private List<Date> dateValues;

    @XmlElement
    private List<QueryCriteria> criteria;

    public QueryCriteria() {
        // default (JAXB/JSON) constructor
    }

    /**
     * Used when creating a group criteria
     * @param union Whether or not the group is part of an intersection or disjunction
     */
    public QueryCriteria(boolean union) {
        QueryCriteria.this.union = union;
        QueryCriteria.this.type = QueryCriteriaType.GROUP;
    }

    private QueryCriteria(String listId, QueryCriteriaType type) {
        QueryCriteria.this.listId = listId;
        QueryCriteria.this.type = type;
    }

    /**
     * Used for all other criteria
     * @param listId The {@link QueryParameterIdentifiers} list id
     * @param union Whether or not the criteria is part of an intersection or disjunction
     * @param type The type: {@link QueryCriteriaType#NORMAL}, {@link QueryCriteriaType#REGEXP}, or {@link QueryCriteriaType#RANGE},
     * @param valueListSize The size of the value list
     */
    public QueryCriteria(String listId, boolean union, QueryCriteriaType type, int valueListSize) {
        this(listId, type);
        QueryCriteria.this.union = union;
        QueryCriteria.this.values = new ArrayList<Object>(valueListSize);
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        QueryCriteria.this.listId = listId;
    }

    public boolean isUnion() {
        return union;
    }

    public void setUnion(boolean union) {
        QueryCriteria.this.union = union;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        QueryCriteria.this.first = first;
    }

    public QueryCriteriaType getType() {
        return type;
    }

    public void setType(QueryCriteriaType type) {
        QueryCriteria.this.type = type;
    }

    public List<Object> getValues() {
        if ((QueryCriteria.this.values) == null) {
            QueryCriteria.this.values = new ArrayList<Object>();
        } 
        return values;
    }

    public void setValues(List<Object> values) {
        QueryCriteria.this.values = values;
    }

    public List<Date> getDateValues() {
        if ((QueryCriteria.this.dateValues) == null) {
            QueryCriteria.this.dateValues = new ArrayList<Date>();
        } 
        return dateValues;
    }

    public void setDateValues(List<Date> dateValues) {
        QueryCriteria.this.dateValues = dateValues;
    }

    // other methods
    @JsonIgnore
    public boolean isGroupCriteria() {
        return QueryCriteria.this.type.equals(QueryCriteriaType.GROUP);
    }

    @JsonIgnore
    public boolean hasValues() {
        return ((QueryCriteria.this.values) != null) && (!(QueryCriteria.this.values.isEmpty()));
    }

    @JsonIgnore
    public boolean hasDateValues() {
        return ((QueryCriteria.this.dateValues) != null) && (!(QueryCriteria.this.dateValues.isEmpty()));
    }

    @JsonIgnore
    public boolean hasCriteria() {
        return ((QueryCriteria.this.criteria) != null) && (!(QueryCriteria.this.criteria.isEmpty()));
    }

    public List<QueryCriteria> getCriteria() {
        if ((QueryCriteria.this.criteria) == null) {
            QueryCriteria.this.criteria = new ArrayList<QueryCriteria>();
        } 
        return criteria;
    }

    public void setCriteria(List<QueryCriteria> criteria) {
        QueryCriteria.this.criteria = criteria;
    }

    /**
     * This method returns a list that should only be read
     * @return
     */
    public List<Object> getParameters() {
        List<Object> parameters = new ArrayList<Object>(getValues());
        if (((QueryCriteria.this.dateValues) != null) && (!(QueryCriteria.this.dateValues.isEmpty()))) {
            parameters.addAll(QueryCriteria.this.dateValues);
        } 
        if (parameters.isEmpty()) {
            return parameters;
        } 
        return parameters;
    }

    void addParameter(Object value) {
        if (value instanceof Date) {
            getDateValues().add(((Date) (value)));
        } else {
            getValues().add(value);
        }
    }

    @SuppressWarnings(value = "unchecked")
    void setParameter(int index, Object value, int listSize) {
        List addValues;
        if (value instanceof Date) {
            addValues = getDateValues();
        } else {
            addValues = getValues();
        }
        while ((addValues.size()) <= index) {
            addValues.add(null);
        }
        addValues.set(index, value);// throws NPE for (index > 1) if (list < index)
        
        while ((addValues.size()) < listSize) {
            addValues.add(null);
        }
    }

    public void addCriteria(QueryCriteria criteria) {
        getCriteria().add(criteria);
    }

    public QueryCriteria(QueryCriteria queryCriteria) {
        QueryCriteria.this.listId = queryCriteria.listId;
        QueryCriteria.this.union = queryCriteria.union;
        QueryCriteria.this.first = queryCriteria.first;
        QueryCriteria.this.type = queryCriteria.type;
        if ((queryCriteria.values) != null) {
            QueryCriteria.this.values = new ArrayList<Object>(queryCriteria.values);
        } 
        if ((queryCriteria.dateValues) != null) {
            QueryCriteria.this.dateValues = new ArrayList<Date>(queryCriteria.dateValues);
        } 
        if ((queryCriteria.criteria) != null) {
            QueryCriteria.this.criteria = new ArrayList<QueryCriteria>(queryCriteria.criteria);
        } 
    }

    private static SimpleDateFormat toStringSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        if (!(first)) {
            out.append((union ? "OR" : "AND")).append(" ");
        } 
        if ((listId) != null) {
            out.append(listId);
        } 
        if (((QueryCriteria.this.values) != null) && (!(QueryCriteria.this.values.isEmpty()))) {
            out.append(" =");
            if (type.equals(QueryCriteriaType.REGEXP)) {
                out.append("~");
            } 
            out.append(" ");
            if (type.equals(QueryCriteriaType.RANGE)) {
                out.append("[");
            } 
            out.append(QueryCriteria.this.values.get(0));
            for (int i = 1; i < (QueryCriteria.this.values.size()); ++i) {
                out.append(", ").append(QueryCriteria.this.values.get(i));
            }
            if (type.equals(QueryCriteriaType.RANGE)) {
                out.append("]");
            } 
        } else if (((QueryCriteria.this.dateValues) != null) && (!(QueryCriteria.this.dateValues.isEmpty()))) {
            out.append(" =");
            if (type.equals(QueryCriteriaType.REGEXP)) {
                out.append("~");
            } 
            out.append(" ");
            if (type.equals(QueryCriteriaType.RANGE)) {
                out.append("[");
            } 
            Date date = QueryCriteria.this.dateValues.get(0);
            String dateStr = date != null ? QueryCriteria.toStringSdf.format(date) : "null";
            out.append(dateStr);
            for (int i = 1; i < (QueryCriteria.this.dateValues.size()); ++i) {
                date = QueryCriteria.this.dateValues.get(i);
                dateStr = (date != null) ? QueryCriteria.toStringSdf.format(date) : "null";
                out.append(", ").append(dateStr);
            }
            if (type.equals(QueryCriteriaType.RANGE)) {
                out.append("]");
            } 
        } 
        if ((criteria) != null) {
            if ((out.length()) > 0) {
                out.append(" ");
            } 
            out.append("(");
            int size = criteria.size();
            if (size > 0) {
                out.append(criteria.get(0).toString());
            } 
            for (int i = 1; i < size; ++i) {
                out.append(", ");
                out.append(criteria.get(i).toString());
            }
            out.append(")");
        } 
        return out.toString();
    }
}

