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

import org.jbpm.compiler.xml.processes.AbstractNodeHandler;
import org.jbpm.process.core.context.exception.ActionExceptionHandler;
import java.util.ArrayList;
import java.util.Collection;
import org.kie.api.definition.process.Connection;
import org.drools.core.process.core.datatype.DataType;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.process.core.context.exception.ExceptionHandler;
import org.jbpm.process.core.context.exception.ExceptionScope;
import org.drools.core.xml.Handler;
import java.util.List;
import java.util.Map;
import org.kie.api.definition.process.Node;
import org.drools.core.process.core.datatype.impl.type.ObjectDataType;
import org.drools.core.xml.SemanticModule;
import org.jbpm.process.core.context.swimlane.Swimlane;
import org.jbpm.process.core.context.swimlane.SwimlaneContext;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.kie.api.definition.process.WorkflowProcess;
import org.drools.compiler.compiler.xml.XmlDumper;

public class XmlWorkflowProcessDumper {
    private static final String EOL = System.getProperty("line.separator");

    private String type;

    private String namespace;

    private String schemaLocation;

    private SemanticModule semanticModule;

    public XmlWorkflowProcessDumper(String type, String namespace, String schemaLocation, SemanticModule semanticModule) {
        XmlWorkflowProcessDumper.this.type = type;
        XmlWorkflowProcessDumper.this.namespace = namespace;
        XmlWorkflowProcessDumper.this.schemaLocation = schemaLocation;
        XmlWorkflowProcessDumper.this.semanticModule = semanticModule;
    }

    public String dump(WorkflowProcess process) {
        return dump(process, true);
    }

    public String dump(WorkflowProcess process, boolean includeMeta) {
        StringBuilder xmlDump = new StringBuilder();
        visitProcess(process, xmlDump, includeMeta);
        return xmlDump.toString();
    }

    protected void visitProcess(WorkflowProcess process, StringBuilder xmlDump, boolean includeMeta) {
        xmlDump.append((((((((((((((((("<?xml version=\"1.0\" encoding=\"UTF-8\"?> " + (XmlWorkflowProcessDumper.EOL)) + "<process xmlns=\"") + (namespace)) + "\"") + (XmlWorkflowProcessDumper.EOL)) + "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"") + (XmlWorkflowProcessDumper.EOL)) + "         xs:schemaLocation=\"") + (namespace)) + " ") + (schemaLocation)) + "\"") + (XmlWorkflowProcessDumper.EOL)) + "         type=\"") + (type)) + "\" "));
        if ((process.getName()) != null) {
            xmlDump.append((("name=\"" + (process.getName())) + "\" "));
        } 
        if ((process.getId()) != null) {
            xmlDump.append((("id=\"" + (process.getId())) + "\" "));
        } 
        if ((process.getPackageName()) != null) {
            xmlDump.append((("package-name=\"" + (process.getPackageName())) + "\" "));
        } 
        if ((process.getVersion()) != null) {
            xmlDump.append((("version=\"" + (process.getVersion())) + "\" "));
        } 
        if (includeMeta) {
            Integer routerLayout = ((Integer) (process.getMetaData().get("routerLayout")));
            if ((routerLayout != null) && (routerLayout != 0)) {
                xmlDump.append((("routerLayout=\"" + routerLayout) + "\" "));
            } 
        } 
        xmlDump.append(((">" + (XmlWorkflowProcessDumper.EOL)) + (XmlWorkflowProcessDumper.EOL)));
        visitHeader(process, xmlDump, includeMeta);
        visitNodes(process, xmlDump, includeMeta);
        visitConnections(process.getNodes(), xmlDump, includeMeta);
        xmlDump.append("</process>");
    }

    protected void visitHeader(WorkflowProcess process, StringBuilder xmlDump, boolean includeMeta) {
        xmlDump.append(("  <header>" + (XmlWorkflowProcessDumper.EOL)));
        visitImports(((org.jbpm.process.core.Process) (process)).getImports(), xmlDump);
        visitGlobals(((org.jbpm.process.core.Process) (process)).getGlobals(), xmlDump);
        visitFunctionImports(((org.jbpm.process.core.Process) (process)).getFunctionImports(), xmlDump);
        VariableScope variableScope = ((VariableScope) (((org.jbpm.process.core.Process) (process)).getDefaultContext(VariableScope.VARIABLE_SCOPE)));
        if (variableScope != null) {
            XmlWorkflowProcessDumper.visitVariables(variableScope.getVariables(), xmlDump);
        } 
        SwimlaneContext swimlaneContext = ((SwimlaneContext) (((org.jbpm.process.core.Process) (process)).getDefaultContext(SwimlaneContext.SWIMLANE_SCOPE)));
        if (swimlaneContext != null) {
            visitSwimlanes(swimlaneContext.getSwimlanes(), xmlDump);
        } 
        ExceptionScope exceptionScope = ((ExceptionScope) (((org.jbpm.process.core.Process) (process)).getDefaultContext(ExceptionScope.EXCEPTION_SCOPE)));
        if (exceptionScope != null) {
            XmlWorkflowProcessDumper.visitExceptionHandlers(exceptionScope.getExceptionHandlers(), xmlDump);
        } 
        xmlDump.append((("  </header>" + (XmlWorkflowProcessDumper.EOL)) + (XmlWorkflowProcessDumper.EOL)));
    }

    private void visitImports(Collection<String> imports, StringBuilder xmlDump) {
        if ((imports != null) && ((imports.size()) > 0)) {
            xmlDump.append(("    <imports>" + (XmlWorkflowProcessDumper.EOL)));
            for (String importString : imports) {
                xmlDump.append(((("      <import name=\"" + importString) + "\" />") + (XmlWorkflowProcessDumper.EOL)));
            }
            xmlDump.append(("    </imports>" + (XmlWorkflowProcessDumper.EOL)));
        } 
    }

    private void visitFunctionImports(List<String> imports, StringBuilder xmlDump) {
        if ((imports != null) && ((imports.size()) > 0)) {
            xmlDump.append(("    <functionImports>" + (XmlWorkflowProcessDumper.EOL)));
            for (String importString : imports) {
                xmlDump.append(((("      <functionImport name=\"" + importString) + "\" />") + (XmlWorkflowProcessDumper.EOL)));
            }
            xmlDump.append(("    </functionImports>" + (XmlWorkflowProcessDumper.EOL)));
        } 
    }

    private void visitGlobals(Map<String, String> globals, StringBuilder xmlDump) {
        if ((globals != null) && ((globals.size()) > 0)) {
            xmlDump.append(("    <globals>" + (XmlWorkflowProcessDumper.EOL)));
            for (Map.Entry<String, String> global : globals.entrySet()) {
                xmlDump.append(((((("      <global identifier=\"" + (global.getKey())) + "\" type=\"") + (global.getValue())) + "\" />") + (XmlWorkflowProcessDumper.EOL)));
            }
            xmlDump.append(("    </globals>" + (XmlWorkflowProcessDumper.EOL)));
        } 
    }

    public static void visitVariables(List<Variable> variables, StringBuilder xmlDump) {
        if ((variables != null) && ((variables.size()) > 0)) {
            xmlDump.append(("    <variables>" + (XmlWorkflowProcessDumper.EOL)));
            for (Variable variable : variables) {
                xmlDump.append(((("      <variable name=\"" + (variable.getName())) + "\" >") + (XmlWorkflowProcessDumper.EOL)));
                XmlWorkflowProcessDumper.visitDataType(variable.getType(), xmlDump);
                Object value = variable.getValue();
                if (value != null) {
                    XmlWorkflowProcessDumper.visitValue(variable.getValue(), variable.getType(), xmlDump);
                } 
                xmlDump.append(("      </variable>" + (XmlWorkflowProcessDumper.EOL)));
            }
            xmlDump.append(("    </variables>" + (XmlWorkflowProcessDumper.EOL)));
        } 
    }

    private void visitSwimlanes(Collection<Swimlane> swimlanes, StringBuilder xmlDump) {
        if ((swimlanes != null) && ((swimlanes.size()) > 0)) {
            xmlDump.append(("    <swimlanes>" + (XmlWorkflowProcessDumper.EOL)));
            for (Swimlane swimlane : swimlanes) {
                xmlDump.append(((("      <swimlane name=\"" + (swimlane.getName())) + "\" />") + (XmlWorkflowProcessDumper.EOL)));
            }
            xmlDump.append(("    </swimlanes>" + (XmlWorkflowProcessDumper.EOL)));
        } 
    }

    public static void visitExceptionHandlers(Map<String, ExceptionHandler> exceptionHandlers, StringBuilder xmlDump) {
        if ((exceptionHandlers != null) && ((exceptionHandlers.size()) > 0)) {
            xmlDump.append(("    <exceptionHandlers>" + (XmlWorkflowProcessDumper.EOL)));
            for (Map.Entry<String, ExceptionHandler> entry : exceptionHandlers.entrySet()) {
                ExceptionHandler exceptionHandler = entry.getValue();
                if (exceptionHandler instanceof ActionExceptionHandler) {
                    ActionExceptionHandler actionExceptionHandler = ((ActionExceptionHandler) (exceptionHandler));
                    xmlDump.append((("      <exceptionHandler faultName=\"" + (entry.getKey())) + "\" type=\"action\" "));
                    String faultVariable = actionExceptionHandler.getFaultVariable();
                    if ((faultVariable != null) && ((faultVariable.length()) > 0)) {
                        xmlDump.append((("faultVariable=\"" + faultVariable) + "\" "));
                    } 
                    xmlDump.append((">" + (XmlWorkflowProcessDumper.EOL)));
                    DroolsAction action = actionExceptionHandler.getAction();
                    if (action != null) {
                        AbstractNodeHandler.writeAction(action, xmlDump);
                    } 
                    xmlDump.append(("      </exceptionHandler>" + (XmlWorkflowProcessDumper.EOL)));
                } else {
                    throw new IllegalArgumentException(("Unknown exception handler type: " + exceptionHandler));
                }
            }
            xmlDump.append(("    </exceptionHandlers>" + (XmlWorkflowProcessDumper.EOL)));
        } 
    }

    public static void visitDataType(DataType dataType, StringBuilder xmlDump) {
        xmlDump.append((("        <type name=\"" + (dataType.getClass().getName())) + "\" "));
        // TODO make this pluggable so datatypes can write out other properties as well
        if (dataType instanceof ObjectDataType) {
            String className = ((ObjectDataType) (dataType)).getClassName();
            if (((className != null) && ((className.trim().length()) > 0)) && (!("java.lang.Object".equals(className)))) {
                xmlDump.append((("className=\"" + className) + "\" "));
            } 
        } 
        xmlDump.append(("/>" + (XmlWorkflowProcessDumper.EOL)));
    }

    public static void visitValue(Object value, DataType dataType, StringBuilder xmlDump) {
        xmlDump.append(((("        <value>" + (XmlDumper.replaceIllegalChars(dataType.writeValue(value)))) + "</value>") + (XmlWorkflowProcessDumper.EOL)));
    }

    private void visitNodes(WorkflowProcess process, StringBuilder xmlDump, boolean includeMeta) {
        xmlDump.append(("  <nodes>" + (XmlWorkflowProcessDumper.EOL)));
        for (Node node : process.getNodes()) {
            visitNode(node, xmlDump, includeMeta);
        }
        xmlDump.append((("  </nodes>" + (XmlWorkflowProcessDumper.EOL)) + (XmlWorkflowProcessDumper.EOL)));
    }

    public void visitNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
        Handler handler = semanticModule.getHandlerByClass(node.getClass());
        if (handler != null) {
            ((AbstractNodeHandler) (handler)).writeNode(((org.jbpm.workflow.core.Node) (node)), xmlDump, includeMeta);
        } else {
            throw new IllegalArgumentException(("Unknown node type: " + node));
        }
    }

    private void visitConnections(Node[] nodes, StringBuilder xmlDump, boolean includeMeta) {
        List<Connection> connections = new ArrayList<Connection>();
        for (Node node : nodes) {
            for (List<Connection> connectionList : node.getIncomingConnections().values()) {
                connections.addAll(connectionList);
            }
        }
        xmlDump.append(("  <connections>" + (XmlWorkflowProcessDumper.EOL)));
        for (Connection connection : connections) {
            visitConnection(connection, xmlDump, includeMeta);
        }
        xmlDump.append((("  </connections>" + (XmlWorkflowProcessDumper.EOL)) + (XmlWorkflowProcessDumper.EOL)));
    }

    public void visitConnection(Connection connection, StringBuilder xmlDump, boolean includeMeta) {
        xmlDump.append((("    <connection from=\"" + (connection.getFrom().getId())) + "\" "));
        if (!(NodeImpl.CONNECTION_DEFAULT_TYPE.equals(connection.getFromType()))) {
            xmlDump.append((("fromType=\"" + (connection.getFromType())) + "\" "));
        } 
        xmlDump.append((("to=\"" + (connection.getTo().getId())) + "\" "));
        if (!(NodeImpl.CONNECTION_DEFAULT_TYPE.equals(connection.getToType()))) {
            xmlDump.append((("toType=\"" + (connection.getToType())) + "\" "));
        } 
        if (includeMeta) {
            String bendpoints = ((String) (connection.getMetaData().get("bendpoints")));
            if (bendpoints != null) {
                xmlDump.append((("bendpoints=\"" + bendpoints) + "\" "));
            } 
        } 
        xmlDump.append(("/>" + (XmlWorkflowProcessDumper.EOL)));
    }
}
