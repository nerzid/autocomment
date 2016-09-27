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

public class ErrorHandler extends BaseAbstractHandler implements Handler {
    @SuppressWarnings(value = "unchecked")
    public ErrorHandler() {
        if (((ErrorHandler.this.validParents) == null) && ((ErrorHandler.this.validPeers) == null)) {
            this.validParents = new HashSet();
            ErrorHandler.this.validParents.add(Definitions.class);
            this.validPeers = new HashSet();
            ErrorHandler.this.validPeers.add(null);
            ErrorHandler.this.validPeers.add(ItemDefinition.class);
            ErrorHandler.this.validPeers.add(Message.class);
            ErrorHandler.this.validPeers.add(Interface.class);
            ErrorHandler.this.validPeers.add(Escalation.class);
            ErrorHandler.this.validPeers.add(Error.class);
            ErrorHandler.this.validPeers.add(Signal.class);
            ErrorHandler.this.validPeers.add(DataStore.class);
            ErrorHandler.this.validPeers.add(RuleFlowProcess.class);
            this.allowNesting = false;
        } 
    }

    @SuppressWarnings(value = "unchecked")
    public Object start(final String uri, final String localName, final Attributes attrs, final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder(localName, attrs);
        String id = attrs.getValue("id");
        String errorCode = attrs.getValue("errorCode");
        String structureRef = attrs.getValue("structureRef");
        Definitions definitions = ((Definitions) (parser.getParent()));
        List<Error> errors = definitions.getErrors();
        if (errors == null) {
            errors = new ArrayList<Error>();
            definitions.setErrors(errors);
            ((ProcessBuildData) (parser.getData())).setMetaData("Errors", errors);
        } 
        Error e = new Error(id, errorCode, structureRef);
        errors.add(e);
        return e;
    }

    public Object end(final String uri, final String localName, final ExtensibleXmlParser parser) throws SAXException {
        parser.endElementBuilder();
        return parser.getCurrent();
    }

    public Class<?> generateNodeFor() {
        return Error.class;
    }
}

