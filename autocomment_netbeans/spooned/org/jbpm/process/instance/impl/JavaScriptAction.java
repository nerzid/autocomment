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


package org.jbpm.process.instance.impl;

import org.kie.api.runtime.Globals;
import java.io.Externalizable;
import java.io.IOException;
import java.util.Map;
import java.io.ObjectInput;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import java.io.ObjectOutput;
import org.kie.api.runtime.process.ProcessContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import VariableScope.VARIABLE_SCOPE;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;

public class JavaScriptAction implements Action , Externalizable {
    private static final long serialVersionUID = 630L;

    private String expr;

    public JavaScriptAction() {
    }

    public JavaScriptAction(final String expr) {
        this.expr = expr;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        expr = in.readUTF();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        // write utf String{expr} to ObjectOutput{out}
        out.writeUTF(expr);
    }

    public void execute(ProcessContext context) throws Exception {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        // put String{"kcontext"} to ScriptEngine{engine}
        engine.put("kcontext", context);
        // insert globals into context
        Globals globals = context.getKieRuntime().getGlobals();
        if ((globals != null) && ((globals.getGlobalKeys()) != null)) {
            for (String gKey : globals.getGlobalKeys()) {
                engine.put(gKey, globals.get(gKey));
            }
        }
        if (((context.getProcessInstance()) != null) && ((context.getProcessInstance().getProcess()) != null)) {
            // insert process variables
            VariableScopeInstance variableScope = ((VariableScopeInstance) (((WorkflowProcessInstance) (context.getProcessInstance())).getContextInstance(VARIABLE_SCOPE)));
            Map<String, Object> variables = variableScope.getVariables();
            if (variables != null) {
                for (Map.Entry<String, Object> variable : variables.entrySet()) {
                    engine.put(variable.getKey(), variable.getValue());
                }
            }
        }
        // eval String{expr} to ScriptEngine{engine}
        engine.eval(expr);
    }
}

