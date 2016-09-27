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


package org.jbpm.ruleflow.core;

import org.jbpm.process.core.context.exception.ActionExceptionHandler;
import java.util.Arrays;
import org.drools.core.process.core.datatype.DataType;
import org.jbpm.process.core.context.exception.ExceptionHandler;
import java.util.HashMap;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.jbpm.process.core.validation.ProcessValidationError;
import org.jbpm.ruleflow.core.validation.RuleFlowProcessValidator;
import org.jbpm.process.core.context.swimlane.Swimlane;
import org.jbpm.process.core.context.variable.Variable;

public class RuleFlowProcessFactory extends RuleFlowNodeContainerFactory {
    private static final Logger logger = LoggerFactory.getLogger(RuleFlowProcessFactory.class);

    public static RuleFlowProcessFactory createProcess(String id) {
        return new RuleFlowProcessFactory(id);
    }

    protected RuleFlowProcessFactory(String id) {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId(id);
        setNodeContainer(process);
    }

    protected RuleFlowProcess getRuleFlowProcess() {
        return ((RuleFlowProcess) (getNodeContainer()));
    }

    public RuleFlowProcessFactory name(String name) {
        getRuleFlowProcess().setName(name);
        return RuleFlowProcessFactory.this;
    }

    public RuleFlowProcessFactory version(String version) {
        getRuleFlowProcess().setVersion(version);
        return RuleFlowProcessFactory.this;
    }

    public RuleFlowProcessFactory packageName(String packageName) {
        getRuleFlowProcess().setPackageName(packageName);
        return RuleFlowProcessFactory.this;
    }

    public RuleFlowProcessFactory imports(String... imports) {
        getRuleFlowProcess().setImports(new HashSet<String>(Arrays.asList(imports)));
        return RuleFlowProcessFactory.this;
    }

    public RuleFlowProcessFactory functionImports(String... functionImports) {
        getRuleFlowProcess().setFunctionImports(Arrays.asList(functionImports));
        return RuleFlowProcessFactory.this;
    }

    public RuleFlowProcessFactory globals(Map<String, String> globals) {
        getRuleFlowProcess().setGlobals(globals);
        return RuleFlowProcessFactory.this;
    }

    public RuleFlowProcessFactory global(String name, String type) {
        Map<String, String> globals = getRuleFlowProcess().getGlobals();
        if (globals == null) {
            globals = new HashMap<String, String>();
            getRuleFlowProcess().setGlobals(globals);
        } 
        globals.put(name, type);
        return RuleFlowProcessFactory.this;
    }

    public RuleFlowProcessFactory variable(String name, DataType type) {
        return variable(name, type, null);
    }

    public RuleFlowProcessFactory variable(String name, DataType type, Object value) {
        return variable(name, type, value, null, null);
    }

    public RuleFlowProcessFactory variable(String name, DataType type, String metaDataName, Object metaDataValue) {
        return variable(name, type, null, metaDataName, metaDataValue);
    }

    public RuleFlowProcessFactory variable(String name, DataType type, Object value, String metaDataName, Object metaDataValue) {
        Variable variable = new Variable();
        variable.setName(name);
        variable.setType(type);
        variable.setValue(value);
        if ((metaDataName != null) && (metaDataValue != null)) {
            variable.setMetaData(metaDataName, metaDataValue);
        } 
        getRuleFlowProcess().getVariableScope().getVariables().add(variable);
        return RuleFlowProcessFactory.this;
    }

    public RuleFlowProcessFactory swimlane(String name) {
        Swimlane swimlane = new Swimlane();
        swimlane.setName(name);
        getRuleFlowProcess().getSwimlaneContext().addSwimlane(swimlane);
        return RuleFlowProcessFactory.this;
    }

    public RuleFlowProcessFactory exceptionHandler(String exception, ExceptionHandler exceptionHandler) {
        getRuleFlowProcess().getExceptionScope().setExceptionHandler(exception, exceptionHandler);
        return RuleFlowProcessFactory.this;
    }

    public RuleFlowProcessFactory exceptionHandler(String exception, String dialect, String action) {
        ActionExceptionHandler exceptionHandler = new ActionExceptionHandler();
        exceptionHandler.setAction(new org.jbpm.workflow.core.impl.DroolsConsequenceAction(dialect, action));
        return exceptionHandler(exception, exceptionHandler);
    }

    public RuleFlowProcessFactory validate() {
        ProcessValidationError[] errors = RuleFlowProcessValidator.getInstance().validateProcess(getRuleFlowProcess());
        for (ProcessValidationError error : errors) {
            RuleFlowProcessFactory.logger.error(error.toString());
        }
        if (errors > 0) {
            throw new RuntimeException("Process could not be validated !");
        } 
        return RuleFlowProcessFactory.this;
    }

    public RuleFlowNodeContainerFactory done() {
        throw new IllegalArgumentException("Already on the top-level.");
    }

    public RuleFlowProcess getProcess() {
        return getRuleFlowProcess();
    }
}

