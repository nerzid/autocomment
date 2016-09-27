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
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import java.util.HashSet;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.jbpm.process.core.context.swimlane.Swimlane;
import org.jbpm.process.core.context.swimlane.SwimlaneContext;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;

public class SwimlaneHandler extends BaseAbstractHandler implements Handler {
    public SwimlaneHandler() {
        if (((SwimlaneHandler.this.validParents) == null) && ((SwimlaneHandler.this.validPeers) == null)) {
            this.validParents = new HashSet();
            SwimlaneHandler.this.validParents.add(Process.class);
            this.validPeers = new HashSet();
            SwimlaneHandler.this.validPeers.add(null);
            this.allowNesting = false;
        } 
    }

    public Object start(final String uri, final String localName, final Attributes attrs, final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder(localName, attrs);
        WorkflowProcessImpl process = ((WorkflowProcessImpl) (parser.getParent()));
        final String name = attrs.getValue("name");
        emptyAttributeCheck(localName, "name", name, parser);
        SwimlaneContext swimlaneContext = ((SwimlaneContext) (process.getDefaultContext(SwimlaneContext.SWIMLANE_SCOPE)));
        if (swimlaneContext != null) {
            Swimlane swimlane = new Swimlane();
            swimlane.setName(name);
            swimlaneContext.addSwimlane(swimlane);
        } else {
            throw new SAXParseException("Could not find default swimlane context.", parser.getLocator());
        }
        return null;
    }

    public Object end(final String uri, final String localName, final ExtensibleXmlParser parser) throws SAXException {
        parser.endElementBuilder();
        return null;
    }

    public Class generateNodeFor() {
        return Swimlane.class;
    }
}

