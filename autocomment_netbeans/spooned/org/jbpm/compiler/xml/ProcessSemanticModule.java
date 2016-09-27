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

import org.drools.core.xml.DefaultSemanticModule;
import org.drools.core.xml.SemanticModule;

public class ProcessSemanticModule extends DefaultSemanticModule implements SemanticModule {
    public static final String URI = "http://drools.org/drools-5.0/process";

    public ProcessSemanticModule() {
        super(ProcessSemanticModule.URI);
        addHandler("process", new org.jbpm.compiler.xml.processes.ProcessHandler());
        addHandler("start", new org.jbpm.compiler.xml.processes.StartNodeHandler());
        addHandler("end", new org.jbpm.compiler.xml.processes.EndNodeHandler());
        addHandler("actionNode", new org.jbpm.compiler.xml.processes.ActionNodeHandler());
        addHandler("ruleSet", new org.jbpm.compiler.xml.processes.RuleSetNodeHandler());
        addHandler("subProcess", new org.jbpm.compiler.xml.processes.SubProcessNodeHandler());
        addHandler("workItem", new org.jbpm.compiler.xml.processes.WorkItemNodeHandler());
        addHandler("split", new org.jbpm.compiler.xml.processes.SplitNodeHandler());
        addHandler("join", new org.jbpm.compiler.xml.processes.JoinNodeHandler());
        addHandler("milestone", new org.jbpm.compiler.xml.processes.MilestoneNodeHandler());
        addHandler("timerNode", new org.jbpm.compiler.xml.processes.TimerNodeHandler());
        addHandler("humanTask", new org.jbpm.compiler.xml.processes.HumanTaskNodeHandler());
        addHandler("forEach", new org.jbpm.compiler.xml.processes.ForEachNodeHandler());
        addHandler("composite", new org.jbpm.compiler.xml.processes.CompositeNodeHandler());
        addHandler("connection", new org.jbpm.compiler.xml.processes.ConnectionHandler());
        addHandler("import", new org.jbpm.compiler.xml.processes.ImportHandler());
        addHandler("functionImport", new org.jbpm.compiler.xml.processes.FunctionImportHandler());
        addHandler("global", new org.jbpm.compiler.xml.processes.GlobalHandler());
        addHandler("variable", new org.jbpm.compiler.xml.processes.VariableHandler());
        addHandler("swimlane", new org.jbpm.compiler.xml.processes.SwimlaneHandler());
        addHandler("type", new org.jbpm.compiler.xml.processes.TypeHandler());
        addHandler("value", new org.jbpm.compiler.xml.processes.ValueHandler());
        addHandler("work", new org.jbpm.compiler.xml.processes.WorkHandler());
        addHandler("parameter", new org.jbpm.compiler.xml.processes.ParameterHandler());
        addHandler("mapping", new org.jbpm.compiler.xml.processes.MappingHandler());
        addHandler("constraint", new org.jbpm.compiler.xml.processes.ConstraintHandler());
        addHandler("in-port", new org.jbpm.compiler.xml.processes.InPortHandler());
        addHandler("out-port", new org.jbpm.compiler.xml.processes.OutPortHandler());
        addHandler("eventNode", new org.jbpm.compiler.xml.processes.EventNodeHandler());
        addHandler("eventFilter", new org.jbpm.compiler.xml.processes.EventFilterHandler());
        addHandler("fault", new org.jbpm.compiler.xml.processes.FaultNodeHandler());
        addHandler("exceptionHandler", new org.jbpm.compiler.xml.processes.ExceptionHandlerHandler());
        addHandler("timer", new org.jbpm.compiler.xml.processes.TimerHandler());
        addHandler("trigger", new org.jbpm.compiler.xml.processes.TriggerHandler());
        addHandler("state", new org.jbpm.compiler.xml.processes.StateNodeHandler());
        addHandler("dynamic", new org.jbpm.compiler.xml.processes.DynamicNodeHandler());
        addHandler("metaData", new org.jbpm.compiler.xml.processes.MetaDataHandler());
    }
}

