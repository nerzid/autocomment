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
import org.w3c.dom.Element;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import java.util.HashSet;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.xml.sax.SAXException;
import org.w3c.dom.Text;

public class DocumentationHandler extends BaseAbstractHandler implements Handler {
    @SuppressWarnings(value = "unchecked")
    public DocumentationHandler() {
        if (((DocumentationHandler.this.validParents) == null) && ((DocumentationHandler.this.validPeers) == null)) {
            this.validParents = new HashSet();
            DocumentationHandler.this.validParents.add(Object.class);
            this.validPeers = new HashSet();
            DocumentationHandler.this.validPeers.add(null);
            DocumentationHandler.this.validPeers.add(Object.class);
            this.allowNesting = false;
        } 
    }

    public Object start(final String uri, final String localName, final Attributes attrs, final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder(localName, attrs);
        return null;
    }

    public Object end(final String uri, final String localName, final ExtensibleXmlParser parser) throws SAXException {
        Element element = parser.endElementBuilder();
        Object parent = parser.getParent();
        if (parent instanceof NodeImpl) {
            String text = ((Text) (element.getChildNodes().item(0))).getWholeText();
            if (text != null) {
                text = text.trim();
                if ("".equals(text)) {
                    text = null;
                } 
            } 
            ((NodeImpl) (parent)).getMetaData().put("Documentation", text);
        } 
        return parser.getCurrent();
    }

    public Class<?> generateNodeFor() {
        return null;
    }
}

