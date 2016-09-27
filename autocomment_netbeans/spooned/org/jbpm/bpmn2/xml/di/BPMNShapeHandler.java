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


package org.jbpm.bpmn2.xml.di;

import org.xml.sax.Attributes;
import org.drools.core.xml.BaseAbstractHandler;
import org.jbpm.bpmn2.xml.di.BPMNEdgeHandler.ConnectionInfo;
import org.w3c.dom.Element;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import java.util.HashSet;
import org.w3c.dom.Node;
import org.jbpm.bpmn2.xml.di.BPMNPlaneHandler.ProcessInfo;
import org.xml.sax.SAXException;

public class BPMNShapeHandler extends BaseAbstractHandler implements Handler {
    public BPMNShapeHandler() {
        initValidParents();
        initValidPeers();
        this.allowNesting = true;
    }

    protected void initValidParents() {
        BPMNShapeHandler.this.validParents = new HashSet<Class<?>>();
        BPMNShapeHandler.this.validParents.add(ProcessInfo.class);
    }

    protected void initValidPeers() {
        BPMNShapeHandler.this.validPeers = new HashSet<Class<?>>();
        BPMNShapeHandler.this.validPeers.add(null);
        BPMNShapeHandler.this.validPeers.add(BPMNShapeHandler.NodeInfo.class);
        BPMNShapeHandler.this.validPeers.add(ConnectionInfo.class);
    }

    public Object start(final String uri, final String localName, final Attributes attrs, final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder(localName, attrs);
        final String elementRef = attrs.getValue("bpmnElement");
        BPMNShapeHandler.NodeInfo nodeInfo = new BPMNShapeHandler.NodeInfo(elementRef);
        ProcessInfo processInfo = ((ProcessInfo) (parser.getParent()));
        processInfo.addNodeInfo(nodeInfo);
        return nodeInfo;
    }

    public Object end(final String uri, final String localName, final ExtensibleXmlParser parser) throws SAXException {
        Element element = parser.endElementBuilder();
        BPMNShapeHandler.NodeInfo nodeInfo = ((BPMNShapeHandler.NodeInfo) (parser.getCurrent()));
        Node xmlNode = element.getFirstChild();
        while (xmlNode instanceof Element) {
            String nodeName = xmlNode.getNodeName();
            if ("Bounds".equals(nodeName)) {
                // ignore first and last waypoint
                String x = ((Element) (xmlNode)).getAttribute("x");
                String y = ((Element) (xmlNode)).getAttribute("y");
                String width = ((Element) (xmlNode)).getAttribute("width");
                String height = ((Element) (xmlNode)).getAttribute("height");
                try {
                    int xValue = 0;
                    if ((x != null) && ((x.trim().length()) != 0)) {
                        xValue = new Float(x).intValue();
                    } 
                    int yValue = 0;
                    if ((y != null) && ((y.trim().length()) != 0)) {
                        yValue = new Float(y).intValue();
                    } 
                    int widthValue = 20;
                    if ((width != null) && ((width.trim().length()) != 0)) {
                        widthValue = new Float(width).intValue();
                    } 
                    int heightValue = 20;
                    if ((height != null) && ((height.trim().length()) != 0)) {
                        heightValue = new Float(height).intValue();
                    } 
                    nodeInfo.setX(xValue);
                    nodeInfo.setY(yValue);
                    nodeInfo.setWidth(widthValue);
                    nodeInfo.setHeight(heightValue);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(("Invalid bounds for node " + (nodeInfo.getNodeRef())), e);
                }
            } 
            xmlNode = xmlNode.getNextSibling();
        }
        return parser.getCurrent();
    }

    public Class<?> generateNodeFor() {
        return BPMNShapeHandler.NodeInfo.class;
    }

    public static class NodeInfo {
        private String nodeRef;

        private Integer x;

        private Integer y;

        private Integer width;

        private Integer height;

        public NodeInfo(String nodeRef) {
            BPMNShapeHandler.NodeInfo.this.nodeRef = nodeRef;
        }

        public String getNodeRef() {
            return nodeRef;
        }

        public Integer getX() {
            return x;
        }

        public void setX(Integer x) {
            BPMNShapeHandler.NodeInfo.this.x = x;
        }

        public Integer getY() {
            return y;
        }

        public void setY(Integer y) {
            BPMNShapeHandler.NodeInfo.this.y = y;
        }

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            BPMNShapeHandler.NodeInfo.this.width = width;
        }

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            BPMNShapeHandler.NodeInfo.this.height = height;
        }
    }
}
