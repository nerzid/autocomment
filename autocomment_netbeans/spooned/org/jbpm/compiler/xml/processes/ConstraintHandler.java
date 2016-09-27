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


package org.jbpm.compiler.xml.processes;

import org.xml.sax.Attributes;
import org.drools.core.xml.BaseAbstractHandler;
import org.jbpm.workflow.core.impl.ConnectionRef;
import org.jbpm.workflow.core.node.Constrainable;
import org.jbpm.workflow.core.Constraint;
import org.w3c.dom.Element;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import java.util.HashSet;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.xml.sax.SAXException;
import org.w3c.dom.Text;

public class ConstraintHandler extends BaseAbstractHandler implements Handler {
    public ConstraintHandler() {
        if (((ConstraintHandler.this.validParents) == null) && ((ConstraintHandler.this.validPeers) == null)) {
            this.validParents = new HashSet<Class<?>>();
            ConstraintHandler.this.validParents.add(Constrainable.class);
            this.validPeers = new HashSet<Class<?>>();
            ConstraintHandler.this.validPeers.add(null);
            this.allowNesting = false;
        } 
    }

    public Object start(final String uri, final String localName, final Attributes attrs, final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder(localName, attrs);
        return null;
    }

    public Object end(final String uri, final String localName, final ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();
        Constrainable parent = ((Constrainable) (parser.getParent()));
        Constraint constraint = new org.jbpm.workflow.core.impl.ConstraintImpl();
        final String toNodeIdString = element.getAttribute("toNodeId");
        String toType = element.getAttribute("toType");
        ConnectionRef connectionRef = null;
        if ((toNodeIdString != null) && ((toNodeIdString.trim().length()) > 0)) {
            int toNodeId = new Integer(toNodeIdString);
            if ((toType == null) || ((toType.trim().length()) == 0)) {
                toType = NodeImpl.CONNECTION_DEFAULT_TYPE;
            } 
            connectionRef = new ConnectionRef(toNodeId, toType);
        } 
        final String name = element.getAttribute("name");
        constraint.setName(name);
        final String priority = element.getAttribute("priority");
        if ((priority != null) && ((priority.length()) != 0)) {
            constraint.setPriority(new Integer(priority));
        } 
        final String type = element.getAttribute("type");
        constraint.setType(type);
        final String dialect = element.getAttribute("dialect");
        constraint.setDialect(dialect);
        String text = ((Text) (element.getChildNodes().item(0))).getWholeText();
        if (text != null) {
            text = text.trim();
            if ("".equals(text)) {
                text = null;
            } 
        } 
        constraint.setConstraint(text);
        parent.addConstraint(connectionRef, constraint);
        return null;
    }

    @SuppressWarnings(value = "unchecked")
    public Class generateNodeFor() {
        return Constraint.class;
    }
}

