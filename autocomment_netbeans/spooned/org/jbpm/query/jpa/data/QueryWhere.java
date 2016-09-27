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

import java.util.HashMap;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.Predicate;
import java.util.Stack;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * THIS CLASS SHOULD NEVER BE EXPOSED IN THE PUBLIC API!!
 * </p>
 * EXTERNAL USE OF THIS CLASS IS **NOT** SUPPORTED!
 * </p>
 * 
 * This object can be seen as a (dynamic) representation of the <code>WHERE</code> part of a query.
 * </p>
 * It has the following responsibilities: <ol>
 * <li>Hold a list of the added query criteria </li>
 * <li>Keep track of the criteria preferences:<ul>
 *   <li>Are we adding a range, a regexp or just a normal criteria?</li>
 *   <li>Is this the start or end of a group?</li></ul>
 * </li>
 * </ol>
 */
// @formatter:off
// transient fields
// @formatter:on
@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
@JsonIgnoreProperties(value = { "union" , "type" , "currentGroupCriteria" , "ancestry" , "currentParent" , "addedJoins" })
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class QueryWhere {
    @XmlEnum
    public static enum QueryCriteriaType {
@XmlEnumValue(value = "N")
        NORMAL, @XmlEnumValue(value = "L")
        REGEXP, @XmlEnumValue(value = "R")
        RANGE, @XmlEnumValue(value = "R")
        GROUP;    }

    @XmlElement(name = "queryCriteria")
    private List<QueryCriteria> criteria = new LinkedList<QueryCriteria>();

    @XmlElement
    private Boolean ascOrDesc = null;

    @XmlElement
    private String orderByListId = null;

    @XmlElement
    private Integer maxResults = null;

    @XmlElement
    private Integer offset = null;

    @JsonIgnore
    private transient boolean union = true;

    @JsonIgnore
    private transient QueryWhere.QueryCriteriaType type = QueryWhere.QueryCriteriaType.NORMAL;

    @JsonIgnore
    private transient List<QueryCriteria> currentCriteria = criteria;

    @JsonIgnore
    private transient Stack<Object> ancestry = new Stack<Object>();

    @JsonIgnore
    private transient Object currentParent = QueryWhere.this;

    @JsonIgnore
    private transient Map<String, Predicate> joinPredicates = null;

    public QueryWhere() {
        // JAXB constructor
    }

    // add logic
    /**
     * This method should be used for<ol>
     * <li>Normal parameters</li>
     * <li>Regular expression parameters</li>
     * </ol>
     * This method should <b>not</b> be used for<ol>
     * <li>Range parameters</li>
     * </ol>
     * @param listId
     * @param param
     * @return
     */
    public <T> QueryCriteria addParameter(String listId, T... param) {
        if ((param.length) == 0) {
            return null;
        } 
        if ((QueryWhere.QueryCriteriaType.REGEXP.equals(QueryWhere.this.type)) && (!((param[0]) instanceof String))) {
            throw new IllegalArgumentException("Only String parameters may be used in regular expressions.");
        } 
        QueryCriteria criteria = new QueryCriteria(listId, QueryWhere.this.union, QueryWhere.this.type, param.length);
        for (T paramElem : param) {
            criteria.addParameter(paramElem);
        }
        addCriteria(criteria);
        return criteria;
    }

    public <T> void addRangeParameter(String listId, T param, boolean start) {
        QueryWhere.QueryCriteriaType origType = QueryWhere.this.type;
        QueryWhere.this.type = QueryWhere.QueryCriteriaType.RANGE;
        // should be the same as before!
        QueryCriteria criteria = new QueryCriteria(listId, QueryWhere.this.union, QueryWhere.this.type, 2);
        int index = start ? 0 : 1;
        criteria.setParameter(index, param, 2);
        addCriteria(criteria);
        QueryWhere.this.type = origType;
    }

    public <T> void addRangeParameters(String listId, T paramMin, T paramMax) {
        QueryWhere.QueryCriteriaType origType = QueryWhere.this.type;
        QueryWhere.this.type = QueryWhere.QueryCriteriaType.RANGE;
        // should be the same as before!
        QueryCriteria criteria = new QueryCriteria(listId, QueryWhere.this.union, QueryWhere.this.type, 2);
        criteria.addParameter(paramMin);
        criteria.addParameter(paramMax);
        addCriteria(criteria);
        QueryWhere.this.type = origType;
    }

    private void addCriteria(QueryCriteria criteria) {
        if (QueryWhere.this.currentCriteria.isEmpty()) {
            criteria.setFirst(true);
        } else if ((QueryWhere.this.currentCriteria.size()) == 1) {
            QueryWhere.this.currentCriteria.get(0).setUnion(criteria.isUnion());
        } 
        QueryWhere.this.currentCriteria.add(criteria);
    }

    // group management
    public void newGroup() {
        // create parent
        QueryCriteria newCriteriaGroupParent = new QueryCriteria(QueryWhere.this.union);
        addCriteria(newCriteriaGroupParent);
        // add parent to parent stack
        ancestry.push(currentParent);
        currentParent = newCriteriaGroupParent;
        // set group criteria list to new list
        currentCriteria = newCriteriaGroupParent.getCriteria();
    }

    public void endGroup() {
        if (ancestry.isEmpty()) {
            throw new IllegalStateException("Can not end group: no group has been started!");
        } 
        // set current group criteria to point to correct list
        Object grandparent = ancestry.pop();
        if (grandparent instanceof QueryWhere) {
            currentCriteria = ((QueryWhere) (grandparent)).getCriteria();
        } else {
            currentCriteria = ((QueryCriteria) (grandparent)).getCriteria();
        }
        currentParent = grandparent;
    }

    @JsonIgnore
    public void setAscending(String listId) {
        QueryWhere.this.ascOrDesc = true;
        QueryWhere.this.orderByListId = listId;
    }

    @JsonIgnore
    public void setDescending(String listId) {
        QueryWhere.this.ascOrDesc = false;
        QueryWhere.this.orderByListId = listId;
    }

    public List<QueryCriteria> getCurrentCriteria() {
        return currentCriteria;
    }

    // getters & setters
    public List<QueryCriteria> getCriteria() {
        return criteria;
    }

    public void setCriteria(List<QueryCriteria> criteria) {
        QueryWhere.this.criteria = criteria;
    }

    public void setParameters(List<QueryCriteria> parameters) {
        QueryWhere.this.criteria = parameters;
    }

    public void setAscOrDesc(Boolean ascendingOrDescending) {
        QueryWhere.this.ascOrDesc = ascendingOrDescending;
    }

    public Boolean getAscOrDesc() {
        return QueryWhere.this.ascOrDesc;
    }

    public void setOrderByListId(String listId) {
        QueryWhere.this.orderByListId = listId;
    }

    public String getOrderByListId() {
        return QueryWhere.this.orderByListId;
    }

    public void setCount(Integer maxResults) {
        QueryWhere.this.maxResults = maxResults;
    }

    public Integer getCount() {
        return QueryWhere.this.maxResults;
    }

    public void setOffset(Integer offset) {
        QueryWhere.this.offset = offset;
    }

    public Integer getOffset() {
        return QueryWhere.this.offset;
    }

    public QueryWhere.QueryCriteriaType getCriteriaType() {
        return QueryWhere.this.type;
    }

    public void setToUnion() {
        QueryWhere.this.union = true;
    }

    public void setToIntersection() {
        QueryWhere.this.union = false;
    }

    public boolean isUnion() {
        return QueryWhere.this.union;
    }

    public void setToLike() {
        QueryWhere.this.type = QueryWhere.QueryCriteriaType.REGEXP;
    }

    public boolean isLike() {
        return QueryWhere.this.type.equals(QueryWhere.QueryCriteriaType.REGEXP);
    }

    public void setToNormal() {
        QueryWhere.this.type = QueryWhere.QueryCriteriaType.NORMAL;
    }

    public void setToRange() {
        QueryWhere.this.type = QueryWhere.QueryCriteriaType.RANGE;
    }

    public boolean isRange() {
        return QueryWhere.this.type.equals(QueryWhere.QueryCriteriaType.RANGE);
    }

    public void setToGroup() {
        QueryWhere.this.type = QueryWhere.QueryCriteriaType.GROUP;
    }

    public Map<String, Predicate> getJoinPredicates() {
        if ((QueryWhere.this.joinPredicates) == null) {
            QueryWhere.this.joinPredicates = new HashMap<String, Predicate>(3);
        } 
        return QueryWhere.this.joinPredicates;
    }

    // clear & clone
    public void clear() {
        QueryWhere.this.union = true;
        QueryWhere.this.type = QueryWhere.QueryCriteriaType.NORMAL;
        QueryWhere.this.ancestry.clear();
        if ((QueryWhere.this.criteria) != null) {
            QueryWhere.this.criteria.clear();
        } 
        QueryWhere.this.currentCriteria = QueryWhere.this.criteria;
        QueryWhere.this.maxResults = null;
        QueryWhere.this.offset = null;
        QueryWhere.this.orderByListId = null;
        QueryWhere.this.ascOrDesc = null;
        QueryWhere.this.joinPredicates = null;
    }

    public QueryWhere(QueryWhere queryWhere) {
        QueryWhere.this.union = queryWhere.union;
        QueryWhere.this.type = queryWhere.type;
        if ((queryWhere.criteria) != null) {
            QueryWhere.this.criteria = new LinkedList<QueryCriteria>(queryWhere.criteria);
        } 
        QueryWhere.this.ascOrDesc = queryWhere.ascOrDesc;
        QueryWhere.this.orderByListId = queryWhere.orderByListId;
        QueryWhere.this.maxResults = queryWhere.maxResults;
        QueryWhere.this.offset = queryWhere.offset;
        QueryWhere.this.joinPredicates = queryWhere.joinPredicates;
    }
}

