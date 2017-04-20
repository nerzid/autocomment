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

import org.jbpm.workflow.core.node.ConstraintTrigger;
import java.util.List;
import java.util.Map;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.Trigger;
import org.drools.compiler.compiler.xml.XmlDumper;

public class StartNodeHandler extends AbstractNodeHandler {
    protected Node createNode() {
        return new StartNode();
    }

    @SuppressWarnings(value = "unchecked")
    public Class generateNodeFor() {
        return StartNode.class;
    }

    public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
        StartNode startNode = ((StartNode) (node));
        // write node String{"start"} to StartNodeHandler{}
        writeNode("start", startNode, xmlDump, includeMeta);
        List<Trigger> triggers = startNode.getTriggers();
        if (((triggers == null) || (triggers.isEmpty())) && ((!includeMeta) || (!(containsMetaData(startNode))))) {
            endNode(xmlDump);
        }else {
            xmlDump.append((">" + (EOL)));
            if (includeMeta) {
                writeMetaData(startNode, xmlDump);
            }
            if (triggers != null) {
                xmlDump.append(("      <triggers>" + (EOL)));
                for (Trigger trigger : triggers) {
                    if (trigger instanceof ConstraintTrigger) {
                        xmlDump.append(("        <trigger type=\"constraint\" >" + (EOL)));
                        xmlDump.append(((("          <constraint type=\"rule\" dialect=\"mvel\" >" + (((ConstraintTrigger) (trigger)).getConstraint())) + "</constraint>") + (EOL)));
                        Map<String, String> inMappings = trigger.getInMappings();
                        if ((inMappings != null) && (!(inMappings.isEmpty()))) {
                            for (Map.Entry<String, String> entry : inMappings.entrySet()) {
                                xmlDump.append(((((("          <mapping type=\"in\" from=\"" + (XmlDumper.replaceIllegalChars(entry.getValue()))) + "\" to=\"") + (entry.getKey())) + "\" />") + (EOL)));
                            }
                        }
                        xmlDump.append(("        </trigger>" + (EOL)));
                    }else
                        if (trigger instanceof org.jbpm.workflow.core.node.EventTrigger) {
                            xmlDump.append(("        <trigger type=\"event\" >" + (EOL)));
                            xmlDump.append(("          <eventFilters>" + (EOL)));
                            for (org.jbpm.process.core.event.EventFilter filter : ((org.jbpm.workflow.core.node.EventTrigger) (trigger)).getEventFilters()) {
                                if (filter instanceof org.jbpm.process.core.event.EventTypeFilter) {
                                    xmlDump.append((((("             <eventFilter " + ("type=\"eventType\" " + "eventType=\"")) + (((org.jbpm.process.core.event.EventTypeFilter) (filter)).getType())) + "\" />") + (EOL)));
                                }else {
                                    throw new IllegalArgumentException(("Unknown filter type: " + filter));
                                }
                            }
                            xmlDump.append(("          </eventFilters>" + (EOL)));
                            Map<String, String> inMappings = trigger.getInMappings();
                            if ((inMappings != null) && (!(inMappings.isEmpty()))) {
                                for (Map.Entry<String, String> entry : inMappings.entrySet()) {
                                    xmlDump.append(((((("          <mapping type=\"in\" from=\"" + (XmlDumper.replaceIllegalChars(entry.getValue()))) + "\" to=\"") + (entry.getKey())) + "\" />") + (EOL)));
                                }
                            }
                            xmlDump.append(("        </trigger>" + (EOL)));
                        }else {
                            throw new IllegalArgumentException(("Unknown trigger type " + trigger));
                        }
                    
                }
                xmlDump.append(("      </triggers>" + (EOL)));
            }
            endNode("start", xmlDump);
        }
    }
}

