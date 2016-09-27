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
import org.w3c.dom.Element;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.xml.sax.SAXException;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;

public class GlobalHandler extends BaseAbstractHandler implements Handler {
    public GlobalHandler() {
        if (((GlobalHandler.this.validParents) == null) && ((GlobalHandler.this.validPeers) == null)) {
            this.validParents = new HashSet();
            GlobalHandler.this.validParents.add(Process.class);
            this.validPeers = new HashSet();
            GlobalHandler.this.validPeers.add(null);
            // this.validPeers.add( ImportDescr.class );
            this.allowNesting = false;
        } 
    }

    public Object start(final String uri, final String localName, final Attributes attrs, final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder(localName, attrs);
        WorkflowProcessImpl process = ((WorkflowProcessImpl) (parser.getParent()));
        final String identifier = attrs.getValue("identifier");
        final String type = attrs.getValue("type");
        emptyAttributeCheck(localName, "identifier", identifier, parser);
        emptyAttributeCheck(localName, "type", type, parser);
        Map<String, String> map = process.getGlobals();
        if (map == null) {
            map = new HashMap<String, String>();
            process.setGlobals(map);
        } 
        map.put(identifier, type);
        return null;
    }

    public Object end(final String uri, final String localName, final ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();
        return null;
    }

    public Class generateNodeFor() {
        return null;
    }
}

