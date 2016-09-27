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


package org.jbpm.process.builder;

import org.kie.api.runtime.process.DataTransformer;
import org.jbpm.process.core.impl.DataTransformerRegistry;
import java.util.HashMap;
import java.util.Map;
import org.kie.api.definition.process.Node;
import org.drools.compiler.lang.descr.ProcessDescr;
import org.jbpm.workflow.core.node.Transformation;
import org.jbpm.workflow.core.WorkflowProcess;

public class StartNodeBuilder extends ExtendedNodeBuilder {
    @Override
    public void build(Process process, ProcessDescr processDescr, ProcessBuildContext context, Node node) {
        super.build(process, processDescr, context, node);
        Transformation transformation = ((Transformation) (node.getMetaData().get("Transformation")));
        if (transformation != null) {
            WorkflowProcess wfProcess = ((WorkflowProcess) (process));
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("imports", wfProcess.getImports());
            parameters.put("classloader", context.getConfiguration().getClassLoader());
            DataTransformer transformer = DataTransformerRegistry.get().find(transformation.getLanguage());
            transformation.setCompiledExpression(transformer.compile(transformation.getExpression(), parameters));
        } 
    }
}

