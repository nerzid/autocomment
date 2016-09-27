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

import org.xml.sax.Attributes;
import org.drools.core.xml.BaseAbstractHandler;
import org.jbpm.process.core.ContextContainer;
import org.drools.core.process.core.datatype.DataType;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import java.util.HashSet;
import org.jbpm.bpmn2.core.ItemDefinition;
import java.util.List;
import java.util.Map;
import org.jbpm.workflow.core.Node;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.xml.sax.SAXException;
import org.jbpm.bpmn2.core.SequenceFlow;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;

public class DataObjectHandler extends BaseAbstractHandler implements Handler {
    public DataObjectHandler() {
        initValidParents();
        initValidPeers();
        this.allowNesting = false;
    }

    protected void initValidParents() {
        DataObjectHandler.this.validParents = new HashSet<Class<?>>();
        DataObjectHandler.this.validParents.add(ContextContainer.class);
    }

    protected void initValidPeers() {
        DataObjectHandler.this.validPeers = new HashSet<Class<?>>();
        DataObjectHandler.this.validPeers.add(null);
        DataObjectHandler.this.validPeers.add(Variable.class);
        DataObjectHandler.this.validPeers.add(Node.class);
        DataObjectHandler.this.validPeers.add(SequenceFlow.class);
    }

    @SuppressWarnings(value = "unchecked")
    public Object start(final String uri, final String localName, final Attributes attrs, final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder(localName, attrs);
        final String id = attrs.getValue("id");
        final String itemSubjectRef = attrs.getValue("itemSubjectRef");
        Object parent = parser.getParent();
        if (parent instanceof ContextContainer) {
            ContextContainer contextContainer = ((ContextContainer) (parent));
            VariableScope variableScope = ((VariableScope) (contextContainer.getDefaultContext(VariableScope.VARIABLE_SCOPE)));
            List variables = variableScope.getVariables();
            Variable variable = new Variable();
            variable.setMetaData("DataObject", "true");
            variable.setName(id);
            // retrieve type from item definition
            DataType dataType = new org.drools.core.process.core.datatype.impl.type.ObjectDataType();
            Map<String, ItemDefinition> itemDefinitions = ((Map<String, ItemDefinition>) (((ProcessBuildData) (parser.getData())).getMetaData("ItemDefinitions")));
            if (itemDefinitions != null) {
                ItemDefinition itemDefinition = itemDefinitions.get(itemSubjectRef);
                if (itemDefinition != null) {
                    String structureRef = itemDefinition.getStructureRef();
                    if (("java.lang.Boolean".equals(structureRef)) || ("Boolean".equals(structureRef))) {
                        dataType = new org.drools.core.process.core.datatype.impl.type.BooleanDataType();
                    } else if (("java.lang.Integer".equals(structureRef)) || ("Integer".equals(structureRef))) {
                        dataType = new org.drools.core.process.core.datatype.impl.type.IntegerDataType();
                    } else if (("java.lang.Float".equals(structureRef)) || ("Float".equals(structureRef))) {
                        dataType = new org.drools.core.process.core.datatype.impl.type.FloatDataType();
                    } else if (("java.lang.String".equals(structureRef)) || ("String".equals(structureRef))) {
                        dataType = new org.drools.core.process.core.datatype.impl.type.StringDataType();
                    } else if (("java.lang.Object".equals(structureRef)) || ("Object".equals(structureRef))) {
                        // use FQCN of Object
                        dataType = new org.drools.core.process.core.datatype.impl.type.ObjectDataType("java.lang.Object");
                    } else {
                        dataType = new org.drools.core.process.core.datatype.impl.type.ObjectDataType(structureRef, parser.getClassLoader());
                    }
                } 
            } 
            variable.setType(dataType);
            variables.add(variable);
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

