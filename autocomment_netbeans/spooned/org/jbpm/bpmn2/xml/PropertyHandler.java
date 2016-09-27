/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.bpmn2.xml;

import org.jbpm.bpmn2.core.Association;
import org.xml.sax.Attributes;
import org.drools.core.xml.BaseAbstractHandler;
import org.jbpm.process.core.ContextContainer;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import java.util.HashSet;
import org.jbpm.bpmn2.core.Lane;
import java.util.List;
import org.jbpm.workflow.core.Node;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.xml.sax.SAXException;
import org.jbpm.bpmn2.core.SequenceFlow;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.workflow.core.node.WorkItemNode;

public class PropertyHandler extends BaseAbstractHandler implements Handler {
    public PropertyHandler() {
        initValidParents();
        initValidPeers();
        this.allowNesting = false;
    }

    protected void initValidParents() {
        PropertyHandler.this.validParents = new HashSet<Class<?>>();
        PropertyHandler.this.validParents.add(ContextContainer.class);
        PropertyHandler.this.validParents.add(WorkItemNode.class);
    }

    protected void initValidPeers() {
        PropertyHandler.this.validPeers = new HashSet<Class<?>>();
        PropertyHandler.this.validPeers.add(null);
        PropertyHandler.this.validPeers.add(Lane.class);
        PropertyHandler.this.validPeers.add(Variable.class);
        PropertyHandler.this.validPeers.add(Node.class);
        PropertyHandler.this.validPeers.add(SequenceFlow.class);
        PropertyHandler.this.validPeers.add(Lane.class);
        PropertyHandler.this.validPeers.add(Association.class);
    }

    @SuppressWarnings(value = "unchecked")
    public Object start(final String uri, final String localName, final Attributes attrs, final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder(localName, attrs);
        final String id = attrs.getValue("id");
        final String name = attrs.getValue("name");
        final String itemSubjectRef = attrs.getValue("itemSubjectRef");
        Object parent = parser.getParent();
        if (parent instanceof ContextContainer) {
            ContextContainer contextContainer = ((ContextContainer) (parent));
            VariableScope variableScope = ((VariableScope) (contextContainer.getDefaultContext(VariableScope.VARIABLE_SCOPE)));
            List variables = variableScope.getVariables();
            Variable variable = new Variable();
            // if name is given use it as variable name instead of id
            if ((name != null) && ((name.length()) > 0)) {
                variable.setName(name);
            } else {
                variable.setName(id);
            }
            variable.setMetaData("ItemSubjectRef", itemSubjectRef);
            variables.add(variable);
            ((ProcessBuildData) (parser.getData())).setMetaData("Variable", variable);
            return variable;
        } 
        return new Variable();
    }

    public Object end(final String uri, final String localName, final ExtensibleXmlParser parser) throws SAXException {
        parser.endElementBuilder();
        return parser.getCurrent();
    }

    public Class<?> generateNodeFor() {
        return Variable.class;
    }
}

