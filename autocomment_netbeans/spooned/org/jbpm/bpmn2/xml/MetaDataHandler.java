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


package org.jbpm.bpmn2.xml;

import org.xml.sax.Attributes;
import org.drools.core.xml.BaseAbstractHandler;
import org.drools.core.process.core.datatype.DataType;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import java.util.HashSet;
import org.jbpm.bpmn2.core.Lane;
import java.util.Map;
import org.jbpm.workflow.core.Node;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.xml.sax.SAXException;
import org.jbpm.bpmn2.core.SequenceFlow;
import org.jbpm.process.core.ValueObject;
import org.jbpm.process.core.context.variable.Variable;

public class MetaDataHandler extends BaseAbstractHandler implements Handler {
    public MetaDataHandler() {
        if (((MetaDataHandler.this.validParents) == null) && ((MetaDataHandler.this.validPeers) == null)) {
            this.validParents = new HashSet();
            MetaDataHandler.this.validParents.add(Node.class);
            MetaDataHandler.this.validParents.add(RuleFlowProcess.class);
            MetaDataHandler.this.validParents.add(Variable.class);
            MetaDataHandler.this.validParents.add(SequenceFlow.class);
            MetaDataHandler.this.validParents.add(Lane.class);
            this.validPeers = new HashSet();
            MetaDataHandler.this.validPeers.add(null);
            this.allowNesting = false;
        } 
    }

    public Object start(final String uri, final String localName, final Attributes attrs, final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder(localName, attrs);
        Object parent = parser.getParent();
        final String name = attrs.getValue("name");
        emptyAttributeCheck(localName, "name", name, parser);
        return new MetaDataHandler.MetaDataWrapper(parent, name);
    }

    public Object end(final String uri, final String localName, final ExtensibleXmlParser parser) throws SAXException {
        parser.endElementBuilder();
        return null;
    }

    public Class generateNodeFor() {
        return MetaDataHandler.MetaDataWrapper.class;
    }

    public class MetaDataWrapper implements ValueObject {
        private Object parent;

        private String name;

        public MetaDataWrapper(Object parent, String name) {
            MetaDataHandler.MetaDataWrapper.this.parent = parent;
            MetaDataHandler.MetaDataWrapper.this.name = name;
        }

        public Object getValue() {
            return getMetaData().get(name);
        }

        public void setValue(Object value) {
            getMetaData().put(name, value);
        }

        public Map<String, Object> getMetaData() {
            if ((parent) instanceof Node) {
                return ((Node) (parent)).getMetaData();
            } else if ((parent) instanceof RuleFlowProcess) {
                return ((RuleFlowProcess) (parent)).getMetaData();
            } else if ((parent) instanceof Variable) {
                return ((Variable) (parent)).getMetaData();
            } else if ((parent) instanceof SequenceFlow) {
                return ((SequenceFlow) (parent)).getMetaData();
            } else if ((parent) instanceof Lane) {
                return ((Lane) (parent)).getMetaData();
            } else {
                throw new IllegalArgumentException(("Unknown parent " + (parent)));
            }
        }

        public DataType getType() {
            return new org.drools.core.process.core.datatype.impl.type.StringDataType();
        }

        public void setType(DataType type) {
        }
    }
}

