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

import java.io.Serializable;

public class Transformation implements Serializable {
    private static final long serialVersionUID = 1641905060375832661L;

    private String source;

    private String language;

    private String expression;

    private Object compiledExpression;

    public Transformation(String lang, String expression) {
        Transformation.this.language = lang;
        Transformation.this.expression = expression;
    }

    public Transformation(String lang, String expression, String source) {
        Transformation.this.language = lang;
        Transformation.this.expression = expression;
        Transformation.this.source = source;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        Transformation.this.language = language;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        Transformation.this.expression = expression;
    }

    public Object getCompiledExpression() {
        return compiledExpression;
    }

    public void setCompiledExpression(Object compliedExpression) {
        Transformation.this.compiledExpression = compliedExpression;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        Transformation.this.source = source;
    }
}

