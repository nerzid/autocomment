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
import org.jbpm.workflow.core.node.DynamicNode;
import org.w3c.dom.Element;
import org.drools.core.xml.ExtensibleXmlParser;
import java.util.List;
import org.jbpm.workflow.core.Node;
import org.xml.sax.SAXException;
import org.jbpm.bpmn2.core.SequenceFlow;
import org.jbpm.process.core.context.variable.VariableScope;

public class AdHocSubProcessHandler extends CompositeContextNodeHandler {
    protected Node createNode(Attributes attrs) {
        DynamicNode result = new DynamicNode();
        VariableScope variableScope = new VariableScope();
        result.addContext(variableScope);
        result.setDefaultContext(variableScope);
        return result;
    }

    @SuppressWarnings(value = "unchecked")
    public Class generateNodeFor() {
        return DynamicNode.class;
    }

    @SuppressWarnings(value = "unchecked")
    protected void handleNode(final Node node, final Element element, final String uri, final String localName, final ExtensibleXmlParser parser) throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        DynamicNode dynamicNode = ((DynamicNode) (node));
        String cancelRemainingInstances = element.getAttribute("cancelRemainingInstances");
        if ("false".equals(cancelRemainingInstances)) {
            dynamicNode.setCancelRemainingInstances(false);
        } 
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("completionCondition".equals(nodeName)) {
                String expression = xmlNode.getTextContent();
                if ("getActivityInstanceAttribute(\"numberOfActiveInstances\") == 0".equals(expression)) {
                    dynamicNode.setAutoComplete(true);
                } else {
                    dynamicNode.setCompletionExpression((expression == null ? "" : expression));
                }
                org.w3c.dom.Node languageNode = xmlNode.getAttributes().getNamedItem("language");
                if (languageNode != null) {
                    String language = languageNode.getNodeValue();
                    if (XmlBPMNProcessDumper.MVEL_LANGUAGE.equals(language)) {
                        dynamicNode.setLanguage("mvel");
                    } else if (XmlBPMNProcessDumper.RULE_LANGUAGE.equals(language)) {
                        dynamicNode.setLanguage("rule");
                    } else {
                        throw new IllegalArgumentException(("Unknown language " + language));
                    }
                } else {
                    dynamicNode.setLanguage("mvel");
                }
            } 
            xmlNode = xmlNode.getNextSibling();
        }
        List<SequenceFlow> connections = ((List<SequenceFlow>) (dynamicNode.getMetaData(ProcessHandler.CONNECTIONS)));
        ProcessHandler.linkConnections(dynamicNode, connections);
        ProcessHandler.linkBoundaryEvents(dynamicNode);
        handleScript(dynamicNode, element, "onEntry");
        handleScript(dynamicNode, element, "onExit");
    }

    public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
        DynamicNode dynamicNode = ((DynamicNode) (node));
        writeNode("adHocSubProcess", dynamicNode, xmlDump, metaDataType);
        if (!(dynamicNode.isCancelRemainingInstances())) {
            xmlDump.append(" cancelRemainingInstances=\"false\"");
        } 
        xmlDump.append((" ordering=\"Parallel\" >" + (EOL)));
        writeExtensionElements(dynamicNode, xmlDump);
        // nodes
        List<Node> subNodes = getSubNodes(dynamicNode);
        XmlBPMNProcessDumper.INSTANCE.visitNodes(subNodes, xmlDump, metaDataType);
        // connections
        visitConnectionsAndAssociations(dynamicNode, xmlDump, metaDataType);
        if (dynamicNode.isAutoComplete()) {
            xmlDump.append(("    <completionCondition xsi:type=\"tFormalExpression\">getActivityInstanceAttribute(\"numberOfActiveInstances\") == 0</completionCondition>" + (EOL)));
        } 
        endNode("adHocSubProcess", xmlDump);
    }
}

