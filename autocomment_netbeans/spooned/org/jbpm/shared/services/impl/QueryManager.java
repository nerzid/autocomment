/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.shared.services.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class QueryManager {
    public static final String ORDER_BY_KEY = "orderby";

    public static final String ASCENDING_KEY = "asc";

    public static final String DESCENDING_KEY = "desc";

    public static final String FILTER = "filter";

    private Map<String, String> queries = new ConcurrentHashMap<String, String>();

    private static QueryManager instance;

    public static QueryManager get() {
        if ((QueryManager.instance) == null) {
            QueryManager.instance = new QueryManager();
        } 
        return QueryManager.instance;
    }

    protected QueryManager() {
    }

    public void addNamedQueries(String ormFile) {
        try {
            parse(ormFile);
        } catch (XMLStreamException e) {
            throw new RuntimeException(("Unable to read orm file due to " + (e.getMessage())), e);
        }
    }

    public String getQuery(String name, Map<String, Object> params) {
        StringBuffer query = null;
        if (!(queries.containsKey(name))) {
            return null;
        } 
        String operand = " and ";
        StringBuffer buf = new StringBuffer(queries.get(name));
        if ((buf.indexOf("where")) == (-1)) {
            operand = " where ";
        } 
        if ((params != null) && (params.containsKey(QueryManager.FILTER))) {
            buf.append((operand + (params.get(QueryManager.FILTER))));
            query = buf;
        } 
        if ((params != null) && (params.containsKey(QueryManager.ORDER_BY_KEY))) {
            buf.append((" \n ORDER BY " + (adaptOrderBy(((String) (params.get("orderby")))))));
            if (params.containsKey(QueryManager.ASCENDING_KEY)) {
                buf.append(" ASC");
            } else if (params.containsKey(QueryManager.DESCENDING_KEY)) {
                buf.append(" DESC");
            } 
            query = buf;
        } 
        return query == null ? null : query.toString();
    }

    protected void parse(String ormFile) throws XMLStreamException {
        String name = null;
        StringBuffer tagContent = new StringBuffer();
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(ormFile));
        while (reader.hasNext()) {
            int event = reader.next();
            switch (event) {
                case XMLStreamConstants.START_ELEMENT :
                    if ("named-query".equals(reader.getLocalName())) {
                        name = reader.getAttributeValue(0);
                    } 
                    break;
                case XMLStreamConstants.CHARACTERS :
                    if (name != null) {
                        tagContent.append(reader.getText());
                    } 
                    break;
                case XMLStreamConstants.END_ELEMENT :
                    if ("named-query".equals(reader.getLocalName())) {
                        String origQuery = tagContent.toString();
                        String alteredQuery = origQuery;
                        int orderByIndex = origQuery.toLowerCase().indexOf("order by");
                        if (orderByIndex != (-1)) {
                            alteredQuery = origQuery.substring(0, orderByIndex);
                        } 
                        queries.put(name, alteredQuery);
                        name = null;
                        tagContent = new StringBuffer();
                    } 
                    // remove order by clause as it will be provided on request
                    break;
            }
        }
    }

    private String adaptOrderBy(String orderBy) {
        if (orderBy != null) {
            if (orderBy.equals("ProcessInstanceId")) {
                return "log.processInstanceId";
            } else if (orderBy.equals("ProcessName")) {
                return "log.processName";
            } else if (orderBy.equals("Initiator")) {
                return "log.identity";
            } else if (orderBy.equals("ProcessVersion")) {
                return "log.processVersion";
            } else if (orderBy.equals("Status")) {
                return "log.status";
            } else if (orderBy.equals("StartDate")) {
                return "log.start";
            } 
        } 
        return orderBy;
    }
}

