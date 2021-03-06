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
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.xml.sax.SAXException;

public class MessageHandler extends BaseAbstractHandler implements Handler {
    @SuppressWarnings(value = "unchecked")
    public MessageHandler() {
        if (((MessageHandler.this.validParents) == null) && ((MessageHandler.this.validPeers) == null)) {
            this.validParents = new HashSet();
            MessageHandler.this.validParents.add(Definitions.class);
            this.validPeers = new HashSet();
            MessageHandler.this.validPeers.add(null);
            MessageHandler.this.validPeers.add(ItemDefinition.class);
            MessageHandler.this.validPeers.add(Message.class);
            MessageHandler.this.validPeers.add(Interface.class);
            MessageHandler.this.validPeers.add(Escalation.class);
            MessageHandler.this.validPeers.add(Error.class);
            MessageHandler.this.validPeers.add(Signal.class);
            MessageHandler.this.validPeers.add(DataStore.class);
            MessageHandler.this.validPeers.add(RuleFlowProcess.class);
            this.allowNesting = false;
        } 
    }

    @SuppressWarnings(value = "unchecked")
    public Object start(final String uri, final String localName, final Attributes attrs, final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder(localName, attrs);
        String id = attrs.getValue("id");
        String itemRef = attrs.getValue("itemRef");
        Map<String, ItemDefinition> itemDefinitions = ((Map<String, ItemDefinition>) (((ProcessBuildData) (parser.getData())).getMetaData("ItemDefinitions")));
        if (itemDefinitions == null) {
            throw new IllegalArgumentException("No item definitions found");
        } 
        ItemDefinition itemDefinition = itemDefinitions.get(itemRef);
        if (itemDefinition == null) {
            throw new IllegalArgumentException(("Could not find itemDefinition " + itemRef));
        } 
        ProcessBuildData buildData = ((ProcessBuildData) (parser.getData()));
        Map<String, Message> messages = ((Map<String, Message>) (((ProcessBuildData) (parser.getData())).getMetaData("Messages")));
        if (messages == null) {
            messages = new HashMap<String, Message>();
            buildData.setMetaData("Messages", messages);
        } 
        Message message = new Message(id);
        message.setType(itemDefinition.getStructureRef());
        messages.put(id, message);
        return message;
    }

    public Object end(final String uri, final String localName, final ExtensibleXmlParser parser) throws SAXException {
        parser.endElementBuilder();
        return parser.getCurrent();
    }

    public Class<?> generateNodeFor() {
        return Message.class;
    }
}

