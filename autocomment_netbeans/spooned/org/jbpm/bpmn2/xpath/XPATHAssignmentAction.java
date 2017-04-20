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


package org.jbpm.bpmn2.xpath;

import org.jbpm.workflow.core.node.Assignment;
import org.jbpm.process.instance.impl.AssignmentAction;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.kie.api.runtime.process.ProcessContext;
import org.drools.core.process.instance.WorkItem;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

public class XPATHAssignmentAction implements AssignmentAction {
    private String sourceExpr;

    private String targetExpr;

    private Assignment assignment;

    private boolean isInput;

    public XPATHAssignmentAction(Assignment assignment, String sourceExpr, String targetExpr, boolean isInput) {
        this.assignment = assignment;
        this.sourceExpr = sourceExpr;
        this.targetExpr = targetExpr;
        this.isInput = isInput;
    }

    public void execute(WorkItem workItem, ProcessContext context) throws Exception {
        String from = assignment.getFrom();
        String to = assignment.getTo();
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpathFrom = factory.newXPath();
        XPathExpression exprFrom = xpathFrom.compile(from);
        XPath xpathTo = factory.newXPath();
        XPathExpression exprTo = xpathTo.compile(to);
        Object target = null;
        Object source = null;
        if (isInput) {
            source = context.getVariable(sourceExpr);
            target = ((WorkItem) (workItem)).getParameter(targetExpr);
        }else {
            target = context.getVariable(targetExpr);
            source = ((WorkItem) (workItem)).getResult(sourceExpr);
        }
        Object targetElem = null;
        // XPATHExpressionModifier modifier = new XPATHExpressionModifier();
        // // modify the tree, returning the root node
        // target = modifier.insertMissingData(to, (org.w3c.dom.Node) target);
        // now pick the leaf for this operation
        if (target != null) {
            Node parent = null;
            parent = ((Node) (target)).getParentNode();
            targetElem = exprTo.evaluate(parent, XPathConstants.NODE);
            if (targetElem == null) {
                throw new RuntimeException(((("Nothing was selected by the to expression " + to) + " on ") + (targetExpr)));
            }
        }
        NodeList nl = null;
        // quirky: create a temporary element, use its nodelist
        // don't throw errors yet ?
        if (source instanceof Node) {
            nl = ((NodeList) (exprFrom.evaluate(source, XPathConstants.NODESET)));
        }// quirky: create a temporary element, use its nodelist
        // don't throw errors yet ?
        else
            if (source instanceof String) {
                javax.xml.parsers.DocumentBuilder builder = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder();
                org.w3c.dom.Document doc = builder.newDocument();
                Element temp = doc.createElementNS(null, "temp");
                temp.appendChild(doc.createTextNode(((String) (source))));
                nl = temp.getChildNodes();
            }else
                if (source == null) {
                    throw new RuntimeException(("Source value was null for source " + (sourceExpr)));
                }
            
        
        if ((nl.getLength()) == 0) {
            throw new RuntimeException(((("Nothing was selected by the from expression " + from) + " on ") + (sourceExpr)));
        }
        for (int i = 0; i < (nl.getLength()); i++) {
            if (!(targetElem instanceof Node)) {
                if ((nl.item(i)) instanceof Attr) {
                    targetElem = ((Attr) (nl.item(i))).getValue();
                }else
                    if ((nl.item(i)) instanceof org.w3c.dom.Text) {
                        targetElem = ((org.w3c.dom.Text) (nl.item(i))).getWholeText();
                    }else {
                        javax.xml.parsers.DocumentBuilder builder = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder();
                        org.w3c.dom.Document doc = builder.newDocument();
                        targetElem = doc.importNode(nl.item(i), true);
                    }
                
                target = targetElem;
            }else {
                Node n = ((Node) (targetElem)).getOwnerDocument().importNode(nl.item(i), true);
                if (n instanceof Attr) {
                    ((Element) (targetElem)).setAttributeNode(((Attr) (n)));
                }else {
                    ((Node) (targetElem)).appendChild(n);
                }
            }
        }
        if (isInput) {
            ((WorkItem) (workItem)).setParameter(targetExpr, target);
        }else {
            context.setVariable(targetExpr, target);
        }
    }
}

