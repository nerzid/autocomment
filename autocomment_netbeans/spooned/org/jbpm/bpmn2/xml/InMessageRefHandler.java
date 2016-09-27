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
import java.util.Map;
import org.jbpm.bpmn2.core.Message;
import org.jbpm.bpmn2.core.Interface.Operation;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.xml.sax.SAXException;

public class InMessageRefHandler extends BaseAbstractHandler implements Handler {
    @SuppressWarnings(value = "unchecked")
    public InMessageRefHandler() {
        if (((InMessageRefHandler.this.validParents) == null) && ((InMessageRefHandler.this.validPeers) == null)) {
            this.validParents = new HashSet();
            InMessageRefHandler.this.validParents.add(Operation.class);
            this.validPeers = new HashSet();
            InMessageRefHandler.this.validPeers.add(null);
            this.allowNesting = false;
        } 
    }

    public Object start(final String uri, final String localName, final Attributes attrs, final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder(localName, attrs);
        return null;
    }

    @SuppressWarnings(value = "unchecked")
    public Object end(final String uri, final String localName, final ExtensibleXmlParser parser) throws SAXException {
        Element element = parser.endElementBuilder();
        String messageId = element.getTextContent();
        Map<String, Message> messages = ((Map<String, Message>) (((ProcessBuildData) (parser.getData())).getMetaData("Messages")));
        if (messages == null) {
            throw new IllegalArgumentException("No messages found");
        } 
        Message message = messages.get(messageId);
        if (message == null) {
            throw new IllegalArgumentException(("Could not find message " + messageId));
        } 
        Operation operation = ((Operation) (parser.getParent()));
        operation.setMessage(message);
        return parser.getCurrent();
    }

    public Class<?> generateNodeFor() {
        return Message.class;
    }
}

