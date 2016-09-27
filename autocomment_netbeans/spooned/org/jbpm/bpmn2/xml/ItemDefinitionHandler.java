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

public class ItemDefinitionHandler extends BaseAbstractHandler implements Handler {
    @SuppressWarnings(value = "unchecked")
    public ItemDefinitionHandler() {
        if (((ItemDefinitionHandler.this.validParents) == null) && ((ItemDefinitionHandler.this.validPeers) == null)) {
            this.validParents = new HashSet();
            ItemDefinitionHandler.this.validParents.add(Definitions.class);
            this.validPeers = new HashSet();
            ItemDefinitionHandler.this.validPeers.add(null);
            ItemDefinitionHandler.this.validPeers.add(ItemDefinition.class);
            ItemDefinitionHandler.this.validPeers.add(Message.class);
            ItemDefinitionHandler.this.validPeers.add(Interface.class);
            ItemDefinitionHandler.this.validPeers.add(Escalation.class);
            ItemDefinitionHandler.this.validPeers.add(Error.class);
            ItemDefinitionHandler.this.validPeers.add(Signal.class);
            ItemDefinitionHandler.this.validPeers.add(DataStore.class);
            ItemDefinitionHandler.this.validPeers.add(RuleFlowProcess.class);
            this.allowNesting = false;
        } 
    }

    @SuppressWarnings(value = "unchecked")
    public Object start(final String uri, final String localName, final Attributes attrs, final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder(localName, attrs);
        String id = attrs.getValue("id");
        String type = attrs.getValue("structureRef");
        if ((type == null) || ((type.trim().length()) == 0)) {
            type = "java.lang.Object";
        } 
        ProcessBuildData buildData = ((ProcessBuildData) (parser.getData()));
        Map<String, ItemDefinition> itemDefinitions = ((Map<String, ItemDefinition>) (buildData.getMetaData("ItemDefinitions")));
        if (itemDefinitions == null) {
            itemDefinitions = new HashMap<String, ItemDefinition>();
            buildData.setMetaData("ItemDefinitions", itemDefinitions);
        } 
        ItemDefinition itemDefinition = new ItemDefinition(id);
        itemDefinition.setStructureRef(type);
        itemDefinitions.put(id, itemDefinition);
        return itemDefinition;
    }

    public Object end(final String uri, final String localName, final ExtensibleXmlParser parser) throws SAXException {
        parser.endElementBuilder();
        return parser.getCurrent();
    }

    public Class<?> generateNodeFor() {
        return ItemDefinition.class;
    }
}

