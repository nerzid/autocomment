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


package org.jbpm.bpmn2.xml;

import java.util.ArrayList;
import org.jbpm.bpmn2.core.Association;
import org.jbpm.workflow.core.node.CompositeNode;
import org.kie.api.definition.process.Connection;
import java.util.List;
import org.kie.api.definition.process.Node;

public abstract class AbstractCompositeNodeHandler extends AbstractNodeHandler {
    protected void visitConnectionsAndAssociations(org.jbpm.workflow.core.Node node, StringBuilder xmlDump, int metaDataType) {
        // add associations
        List<Connection> connections = getSubConnections(((CompositeNode) (node)));
        xmlDump.append(("    <!-- connections -->" + (EOL)));
        for (Connection connection : connections) {
            XmlBPMNProcessDumper.INSTANCE.visitConnection(connection, xmlDump, metaDataType);
        }
        // add associations
        List<Association> associations = ((List<Association>) (node.getMetaData().get(ProcessHandler.ASSOCIATIONS)));
        if (associations != null) {
            for (Association association : associations) {
                XmlBPMNProcessDumper.INSTANCE.visitAssociation(association, xmlDump);
            }
        } 
    }

    protected List<Connection> getSubConnections(CompositeNode compositeNode) {
        List<Connection> connections = new ArrayList<Connection>();
        for (Node subNode : compositeNode.getNodes()) {
            // filter out composite start and end nodes as they can be regenerated
            if (!(subNode instanceof CompositeNode)) {
                for (Connection connection : subNode.getIncomingConnections(Node.CONNECTION_DEFAULT_TYPE)) {
                    if (!((connection.getFrom()) instanceof CompositeNode)) {
                        connections.add(connection);
                    } 
                }
            } 
        }
        return connections;
    }
}

