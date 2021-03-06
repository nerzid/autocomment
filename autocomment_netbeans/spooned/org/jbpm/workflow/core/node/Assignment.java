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

import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public class Assignment implements Serializable {
    private static final long serialVersionUID = 5L;

    private String dialect;

    private String from;

    private String to;

    private Map<String, Object> metaData = new HashMap<String, Object>();

    public Assignment(String dialect, String from, String to) {
        Assignment.this.dialect = dialect;
        Assignment.this.from = from;
        Assignment.this.to = to;
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        Assignment.this.dialect = dialect;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        Assignment.this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        Assignment.this.to = to;
    }

    public void setMetaData(String name, Object value) {
        Assignment.this.metaData.put(name, value);
    }

    public Object getMetaData(String name) {
        return Assignment.this.metaData.get(name);
    }
}

