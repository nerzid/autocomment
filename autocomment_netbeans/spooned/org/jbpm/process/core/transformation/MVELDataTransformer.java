/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.process.core.transformation;

import org.kie.api.runtime.process.DataTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mvel2.MVEL;
import org.drools.core.util.MVELSafeHelper;
import java.util.Map;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import java.util.Set;

/**
 * MVEL based <code>DataTransformer</code> implementation
 */
public class MVELDataTransformer implements DataTransformer {
    private static final Logger logger = LoggerFactory.getLogger(MVELDataTransformer.class);

    @Override
    public Object compile(String expression, Map<String, Object> parameters) {
        MVELDataTransformer.logger.debug("About to compile mvel expression {}", expression);
        ClassLoader classLoader = ((ClassLoader) (parameters.get("classloader")));
        if (classLoader == null) {
            classLoader = MVELDataTransformer.this.getClass().getClassLoader();
        } 
        ParserConfiguration config = new ParserConfiguration();
        config.setClassLoader(classLoader);
        ParserContext context = new ParserContext(config);
        if (parameters != null) {
            @SuppressWarnings(value = "unchecked")
            Set<String> imports = ((Set<String>) (parameters.get("imports")));
            if (imports != null) {
                for (String clazz : imports) {
                    try {
                        Class<?> cl = Class.forName(clazz, true, classLoader);
                        context.addImport(cl.getSimpleName(), cl);
                    } catch (ClassNotFoundException e) {
                        MVELDataTransformer.logger.warn("Unable to load class {} due to {}", clazz, e.getException());
                    }
                }
            } 
        } 
        return MVEL.compileExpression(expression, context);
    }

    @Override
    public Object transform(Object expression, Map<String, Object> parameters) {
        MVELDataTransformer.logger.debug("About to execute mvel expression {} with parameters {}", expression, parameters);
        return MVELSafeHelper.getEvaluator().executeExpression(expression, parameters);
    }
}

