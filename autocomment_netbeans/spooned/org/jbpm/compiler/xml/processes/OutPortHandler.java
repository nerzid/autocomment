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
import org.jbpm.workflow.core.node.CompositeNode;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import java.util.HashSet;
import org.xml.sax.SAXException;

public class OutPortHandler extends BaseAbstractHandler implements Handler {
    public OutPortHandler() {
        if (((OutPortHandler.this.validParents) == null) && ((OutPortHandler.this.validPeers) == null)) {
            this.validParents = new HashSet();
            OutPortHandler.this.validParents.add(CompositeNode.class);
            this.validPeers = new HashSet();
            OutPortHandler.this.validPeers.add(null);
            this.allowNesting = false;
        } 
    }

    public Object start(final String uri, final String localName, final Attributes attrs, final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder(localName, attrs);
        CompositeNode compositeNode = ((CompositeNode) (parser.getParent()));
        final String type = attrs.getValue("type");
        emptyAttributeCheck(localName, "type", type, parser);
        final String nodeId = attrs.getValue("nodeId");
        emptyAttributeCheck(localName, "nodeId", nodeId, parser);
        final String nodeOutType = attrs.getValue("nodeOutType");
        emptyAttributeCheck(localName, "nodeOutType", nodeOutType, parser);
        compositeNode.linkOutgoingConnections(new Long(nodeId), nodeOutType, type);
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

