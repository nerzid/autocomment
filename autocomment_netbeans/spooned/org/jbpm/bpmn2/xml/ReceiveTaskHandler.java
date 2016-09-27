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

import org.xml.sax.Attributes;
import org.w3c.dom.Element;
import org.drools.core.xml.ExtensibleXmlParser;
import java.util.Map;
import org.jbpm.bpmn2.core.Message;
import org.jbpm.workflow.core.Node;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.xml.sax.SAXException;
import org.jbpm.workflow.core.node.WorkItemNode;

public class ReceiveTaskHandler extends TaskHandler {
    protected Node createNode(Attributes attrs) {
        return new WorkItemNode();
    }

    @SuppressWarnings(value = "unchecked")
    public Class generateNodeFor() {
        return Node.class;
    }

    @SuppressWarnings(value = "unchecked")
    protected void handleNode(final Node node, final Element element, final String uri, final String localName, final ExtensibleXmlParser parser) throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        WorkItemNode workItemNode = ((WorkItemNode) (node));
        String messageRef = element.getAttribute("messageRef");
        Map<String, Message> messages = ((Map<String, Message>) (((ProcessBuildData) (parser.getData())).getMetaData("Messages")));
        if (messages == null) {
            throw new IllegalArgumentException("No messages found");
        } 
        Message message = messages.get(messageRef);
        if (message == null) {
            throw new IllegalArgumentException(("Could not find message " + messageRef));
        } 
        workItemNode.getWork().setParameter("MessageId", message.getId());
        workItemNode.getWork().setParameter("MessageType", message.getType());
    }

    protected String getTaskName(final Element element) {
        return "Receive Task";
    }

    public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
        throw new IllegalArgumentException("Writing out should be handled by WorkItemNodeHandler");
    }
}
