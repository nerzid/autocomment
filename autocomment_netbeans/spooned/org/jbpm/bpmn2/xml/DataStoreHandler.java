/**
 * Copyright 2010 Intalio Inc
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

import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.drools.core.xml.BaseAbstractHandler;
import org.drools.core.process.core.datatype.DataType;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:atoulme@intalio.com">Antoine Toulme</a>
 */
public class DataStoreHandler extends BaseAbstractHandler implements Handler {
    @SuppressWarnings(value = "rawtypes")
    public DataStoreHandler() {
        if (((DataStoreHandler.this.validParents) == null) && ((DataStoreHandler.this.validPeers) == null)) {
            this.validParents = new HashSet<Class<?>>();
            DataStoreHandler.this.validParents.add(Definitions.class);
            this.validPeers = new HashSet<Class<?>>();
            DataStoreHandler.this.validPeers.add(null);
            DataStoreHandler.this.validPeers.add(ItemDefinition.class);
            DataStoreHandler.this.validPeers.add(Message.class);
            DataStoreHandler.this.validPeers.add(Interface.class);
            DataStoreHandler.this.validPeers.add(Escalation.class);
            DataStoreHandler.this.validPeers.add(Error.class);
            DataStoreHandler.this.validPeers.add(Signal.class);
            DataStoreHandler.this.validPeers.add(DataStore.class);
            DataStoreHandler.this.validPeers.add(RuleFlowProcess.class);
            this.allowNesting = false;
        } 
    }

    public Object start(final String uri, final String localName, final Attributes attrs, final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder(localName, attrs);
        DataStore store = new DataStore();
        store.setId(attrs.getValue("id"));
        store.setName(attrs.getValue("name"));
        final String itemSubjectRef = attrs.getValue("itemSubjectRef");
        store.setItemSubjectRef(itemSubjectRef);
        Map<String, ItemDefinition> itemDefinitions = ((Map<String, ItemDefinition>) (((ProcessBuildData) (parser.getData())).getMetaData("ItemDefinitions")));
        // retrieve type from item definition
        // FIXME we bypass namespace resolving here. That's not a good idea when we start having several documents, with imports.
        String localItemSubjectRef = itemSubjectRef.substring(((itemSubjectRef.indexOf(":")) + 1));
        DataType dataType = new org.drools.core.process.core.datatype.impl.type.ObjectDataType();
        if (itemDefinitions != null) {
            ItemDefinition itemDefinition = itemDefinitions.get(localItemSubjectRef);
            if (itemDefinition != null) {
                dataType = new org.drools.core.process.core.datatype.impl.type.ObjectDataType(itemDefinition.getStructureRef(), parser.getClassLoader());
            } 
        } 
        store.setType(dataType);
        Definitions parent = ((Definitions) (parser.getParent()));
        List<DataStore> dataStores = parent.getDataStores();
        if (dataStores == null) {
            dataStores = new ArrayList<DataStore>();
            parent.setDataStores(dataStores);
        } 
        dataStores.add(store);
        return store;
    }

    @SuppressWarnings(value = "unchecked")
    public Object end(final String uri, final String localName, final ExtensibleXmlParser parser) throws SAXException {
        parser.endElementBuilder();
        return parser.getCurrent();
    }

    public Class<?> generateNodeFor() {
        return DataStore.class;
    }
}

