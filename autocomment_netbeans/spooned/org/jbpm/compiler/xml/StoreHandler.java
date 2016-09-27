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


package org.jbpm.compiler.xml;

import org.jbpm.workflow.core.node.ActionNode;
import org.xml.sax.Attributes;
import org.drools.core.xml.BaseAbstractHandler;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.w3c.dom.Element;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import java.util.HashSet;
import org.xml.sax.SAXException;
import org.jbpm.workflow.core.node.StartNode;
import org.w3c.dom.Text;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;

public class StoreHandler extends BaseAbstractHandler implements Handler {
    public StoreHandler() {
        if (((StoreHandler.this.validParents) == null) && ((StoreHandler.this.validPeers) == null)) {
            this.validParents = new HashSet<Class<?>>();
            StoreHandler.this.validParents.add(Process.class);
            this.validPeers = new HashSet<Class<?>>();
            StoreHandler.this.validPeers.add(StartNode.class);
            StoreHandler.this.validPeers.add(ActionNode.class);
            this.allowNesting = false;
        } 
    }

    public Object start(final String uri, final String localName, final Attributes attrs, final ExtensibleXmlParser xmlPackageReader) throws SAXException {
        xmlPackageReader.startElementBuilder(localName, attrs);
        WorkflowProcessImpl process = ((WorkflowProcessImpl) (xmlPackageReader.getParent()));
        ActionNode actionNode = new ActionNode();
        final String name = attrs.getValue("name");
        emptyAttributeCheck(localName, "name", name, xmlPackageReader);
        actionNode.setName(name);
        final String id = attrs.getValue("id");
        emptyAttributeCheck(localName, "id", name, xmlPackageReader);
        actionNode.setId(new Long(id));
        process.addNode(actionNode);
        ((ProcessBuildData) (xmlPackageReader.getData())).addNode(actionNode);
        return actionNode;
    }

    public Object end(final String uri, final String localName, final ExtensibleXmlParser xmlPackageReader) throws SAXException {
        final Element element = xmlPackageReader.endElementBuilder();
        ActionNode actionNode = ((ActionNode) (xmlPackageReader.getCurrent()));
        String text = ((Text) (element.getChildNodes().item(0))).getWholeText();
        DroolsConsequenceAction actionText = new DroolsConsequenceAction("mvel", (("list.add(\"" + text) + "\")"));
        actionNode.setAction(actionText);
        return actionNode;
    }

    public Class<?> generateNodeFor() {
        return ActionNode.class;
    }
}

