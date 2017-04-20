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

import org.junit.Assert;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class QueryManagerTest {
    @Test
    public void testLoadQueriesNotFound() {
        QueryManager manager = new QueryManager();
        // add named String{"test-orm.xml"} to QueryManager{manager}
        manager.addNamedQueries("test-orm.xml");
        String query = manager.getQuery("test-query-1", null);
        // assert null String{query} to void{Assert}
        Assert.assertNull(query);
    }

    @Test
    public void testLoadQueriesFound() {
        QueryManager manager = new QueryManager();
        // add named String{"test-orm.xml"} to QueryManager{manager}
        manager.addNamedQueries("test-orm.xml");
        Map<String, Object> params = new HashMap<String, Object>();
        // put String{"orderby"} to Map{params}
        params.put("orderby", "log.date");
        String query = manager.getQuery("test-query-3", params);
        // assert not String{query} to void{Assert}
        Assert.assertNotNull(query);
        // assert true boolean{query.endsWith("ORDER BY log.date")} to void{Assert}
        Assert.assertTrue(query.endsWith("ORDER BY log.date"));
    }

    @Test
    public void testLoadQueriesFoundAsc() {
        QueryManager manager = new QueryManager();
        // add named String{"test-orm.xml"} to QueryManager{manager}
        manager.addNamedQueries("test-orm.xml");
        Map<String, Object> params = new HashMap<String, Object>();
        // put String{"orderby"} to Map{params}
        params.put("orderby", "log.date");
        // put String{"asc"} to Map{params}
        params.put("asc", "true");
        String query = manager.getQuery("test-query-3", params);
        // assert not String{query} to void{Assert}
        Assert.assertNotNull(query);
        // assert true boolean{query.endsWith("ORDER BY log.date ASC")} to void{Assert}
        Assert.assertTrue(query.endsWith("ORDER BY log.date ASC"));
    }

    @Test
    public void testLoadQueriesFoundDesc() {
        QueryManager manager = new QueryManager();
        // add named String{"test-orm.xml"} to QueryManager{manager}
        manager.addNamedQueries("test-orm.xml");
        Map<String, Object> params = new HashMap<String, Object>();
        // put String{"orderby"} to Map{params}
        params.put("orderby", "log.date");
        // put String{"desc"} to Map{params}
        params.put("desc", "true");
        String query = manager.getQuery("test-query-3", params);
        // assert not String{query} to void{Assert}
        Assert.assertNotNull(query);
        // assert true boolean{query.endsWith("ORDER BY log.date DESC")} to void{Assert}
        Assert.assertTrue(query.endsWith("ORDER BY log.date DESC"));
    }
}

