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

import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.drools.core.xml.BaseAbstractHandler;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import java.util.HashSet;
import java.util.List;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.xml.sax.SAXException;

public class InterfaceHandler extends BaseAbstractHandler implements Handler {
    @SuppressWarnings(value = "unchecked")
    public InterfaceHandler() {
        if (((InterfaceHandler.this.validParents) == null) && ((InterfaceHandler.this.validPeers) == null)) {
            this.validParents = new HashSet();
            InterfaceHandler.this.validParents.add(Definitions.class);
            this.validPeers = new HashSet();
            InterfaceHandler.this.validPeers.add(null);
            InterfaceHandler.this.validPeers.add(ItemDefinition.class);
            InterfaceHandler.this.validPeers.add(Message.class);
            InterfaceHandler.this.validPeers.add(Interface.class);
            InterfaceHandler.this.validPeers.add(Escalation.class);
            InterfaceHandler.this.validPeers.add(Error.class);
            InterfaceHandler.this.validPeers.add(Signal.class);
            InterfaceHandler.this.validPeers.add(DataStore.class);
            InterfaceHandler.this.validPeers.add(RuleFlowProcess.class);
            this.allowNesting = false;
        } 
    }

    @SuppressWarnings(value = "unchecked")
    public Object start(final String uri, final String localName, final Attributes attrs, final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder(localName, attrs);
        String id = attrs.getValue("id");
        String name = attrs.getValue("name");
        String implRef = attrs.getValue("implementationRef");
        if ((name == null) || (name.isEmpty())) {
            throw new IllegalArgumentException("Interface name is required attribute");
        } 
        ProcessBuildData buildData = ((ProcessBuildData) (parser.getData()));
        List<Interface> interfaces = ((List<Interface>) (buildData.getMetaData("Interfaces")));
        if (interfaces == null) {
            interfaces = new ArrayList<Interface>();
            buildData.setMetaData("Interfaces", interfaces);
        } 
        Interface i = new Interface(id, name);
        if (implRef != null) {
            i.setImplementationRef(implRef);
        } 
        interfaces.add(i);
        return i;
    }

    public Object end(final String uri, final String localName, final ExtensibleXmlParser parser) throws SAXException {
        parser.endElementBuilder();
        return parser.getCurrent();
    }

    public Class<?> generateNodeFor() {
        return Interface.class;
    }
}

