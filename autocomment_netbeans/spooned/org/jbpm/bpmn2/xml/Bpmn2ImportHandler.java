/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

public class Bpmn2ImportHandler extends BaseAbstractHandler implements Handler {
    public Bpmn2ImportHandler() {
        if (((Bpmn2ImportHandler.this.validParents) == null) && ((Bpmn2ImportHandler.this.validPeers) == null)) {
            this.validParents = new HashSet();
            Bpmn2ImportHandler.this.validParents.add(Definitions.class);
            this.validPeers = new HashSet();
            Bpmn2ImportHandler.this.validPeers.add(null);
            Bpmn2ImportHandler.this.validPeers.add(ItemDefinition.class);
            Bpmn2ImportHandler.this.validPeers.add(Message.class);
            Bpmn2ImportHandler.this.validPeers.add(Interface.class);
            Bpmn2ImportHandler.this.validPeers.add(Escalation.class);
            Bpmn2ImportHandler.this.validPeers.add(Error.class);
            Bpmn2ImportHandler.this.validPeers.add(Signal.class);
            Bpmn2ImportHandler.this.validPeers.add(DataStore.class);
            Bpmn2ImportHandler.this.validPeers.add(RuleFlowProcess.class);
            this.allowNesting = false;
        } 
    }

    public Object start(final String uri, final String localName, final Attributes attrs, final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder(localName, attrs);
        final String type = attrs.getValue("importType");
        final String location = attrs.getValue("location");
        final String namespace = attrs.getValue("namespace");
        ProcessBuildData buildData = ((ProcessBuildData) (parser.getData()));
        if (((type != null) && (location != null)) && (namespace != null)) {
            List<Bpmn2Import> typedImports = ((List<Bpmn2Import>) (buildData.getMetaData("Bpmn2Imports")));
            if (typedImports == null) {
                typedImports = new ArrayList<Bpmn2Import>();
                buildData.setMetaData("Bpmn2Imports", typedImports);
            } 
            typedImports.add(new Bpmn2Import(type, location, namespace));
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
