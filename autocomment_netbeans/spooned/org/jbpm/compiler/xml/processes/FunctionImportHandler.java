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

import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.drools.core.xml.BaseAbstractHandler;
import org.w3c.dom.Element;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import java.util.HashSet;
import java.util.List;
import org.xml.sax.SAXException;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;

public class FunctionImportHandler extends BaseAbstractHandler implements Handler {
    public FunctionImportHandler() {
        if (((FunctionImportHandler.this.validParents) == null) && ((FunctionImportHandler.this.validPeers) == null)) {
            this.validParents = new HashSet();
            FunctionImportHandler.this.validParents.add(Process.class);
            this.validPeers = new HashSet();
            FunctionImportHandler.this.validPeers.add(null);
            this.allowNesting = false;
        } 
    }

    public Object start(final String uri, final String localName, final Attributes attrs, final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder(localName, attrs);
        WorkflowProcessImpl process = ((WorkflowProcessImpl) (parser.getParent()));
        final String name = attrs.getValue("name");
        emptyAttributeCheck(localName, "name", name, parser);
        List<String> list = process.getFunctionImports();
        if (list == null) {
            list = new ArrayList<String>();
            process.setFunctionImports(list);
        } 
        list.add(name);
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

