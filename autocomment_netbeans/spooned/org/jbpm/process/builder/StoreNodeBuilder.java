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

import org.drools.compiler.lang.descr.ActionDescr;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.kie.api.definition.process.Node;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.drools.compiler.lang.descr.ProcessDescr;
import org.jbpm.process.builder.dialect.ProcessDialect;
import org.jbpm.process.builder.dialect.ProcessDialectRegistry;

public class StoreNodeBuilder implements ProcessNodeBuilder {
    public void build(Process process, ProcessDescr processDescr, ProcessBuildContext context, Node node) {
        ActionNode actionNode = ((ActionNode) (node));
        DroolsConsequenceAction action = ((DroolsConsequenceAction) (actionNode.getAction()));
        ActionDescr actionDescr = new ActionDescr();
        actionDescr.setText(action.getConsequence());
        actionDescr.setResource(processDescr.getResource());
        ProcessDialect dialect = ProcessDialectRegistry.getDialect(action.getDialect());
        dialect.getActionBuilder().build(context, action, actionDescr, ((NodeImpl) (node)));
    }
}

