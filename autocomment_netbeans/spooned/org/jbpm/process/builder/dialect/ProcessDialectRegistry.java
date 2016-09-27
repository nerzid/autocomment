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


package org.jbpm.process.builder.dialect;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ProcessDialectRegistry {
    private static ConcurrentMap<String, ProcessDialect> dialects;

    static {
        dialects = new ConcurrentHashMap<String, ProcessDialect>();
        ProcessDialectRegistry.dialects.put("java", new org.jbpm.process.builder.dialect.java.JavaProcessDialect());
        ProcessDialectRegistry.dialects.put("mvel", new org.jbpm.process.builder.dialect.mvel.MVELProcessDialect());
        ProcessDialectRegistry.dialects.put("JavaScript", new org.jbpm.process.builder.dialect.javascript.JavaScriptProcessDialect());
    }

    public static ProcessDialect getDialect(String dialect) {
        return ProcessDialectRegistry.dialects.get(dialect);
    }

    public static void setDialect(String dialectName, ProcessDialect dialect) {
        ProcessDialectRegistry.dialects.put(dialectName, dialect);
    }
}

