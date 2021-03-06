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
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.xml.sax.SAXException;
import java.util.Set;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;

public class ImportHandler extends BaseAbstractHandler implements Handler {
    public ImportHandler() {
        if (((ImportHandler.this.validParents) == null) && ((ImportHandler.this.validPeers) == null)) {
            this.validParents = new HashSet();
            ImportHandler.this.validParents.add(Process.class);
            this.validPeers = new HashSet();
            ImportHandler.this.validPeers.add(null);
            this.allowNesting = false;
        } 
    }

    public Object start(final String uri, final String localName, final Attributes attrs, final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder(localName, attrs);
        WorkflowProcessImpl process = ((WorkflowProcessImpl) (parser.getParent()));
        final String name = attrs.getValue("name");
        final String type = attrs.getValue("importType");
        final String location = attrs.getValue("location");
        final String namespace = attrs.getValue("namespace");
        emptyAttributeCheck(localName, "name", name, parser);
        if (((type != null) && (location != null)) && (namespace != null)) {
            Map<String, String> typedImports = ((Map<String, String>) (process.getMetaData(type)));
            if (typedImports == null) {
                typedImports = new HashMap<String, String>();
                process.setMetaData(type, typedImports);
            } 
            typedImports.put(namespace, location);
        } else {
            Set<String> list = process.getImports();
            if (list == null) {
                list = new HashSet<String>();
                process.setImports(list);
            } 
            list.add(name);
        }
        return null;
    }

    public Object end(final String uri, final String localName, final ExtensibleXmlParser parser) throws SAXException {
        parser.endElementBuilder();
        return null;
    }

    public Class generateNodeFor() {
        return null;
    }
}

