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


package org.jbpm.process.builder.dialect.mvel;

import MVELCompilationUnit.Scope.EXPRESSION;
import org.jbpm.process.core.ContextResolver;
import VariableScope.VARIABLE_SCOPE;
import java.util.HashMap;
import org.drools.compiler.rule.builder.dialect.mvel.MVELAnalysisResult;
import org.drools.core.base.mvel.MVELCompilationUnit;
import org.drools.compiler.rule.builder.dialect.mvel.MVELDialect;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.jbpm.process.instance.impl.MVELReturnValueEvaluator;
import org.jbpm.process.core.context.variable.VariableScope;
import java.util.Set;
import java.util.Map;
import org.jbpm.process.builder.ReturnValueEvaluatorBuilder;
import org.drools.compiler.rule.builder.PackageBuildContext;
import org.kie.api.runtime.process.ProcessContext;
import org.jbpm.process.instance.impl.ReturnValueConstraintEvaluator;
import org.drools.compiler.compiler.ReturnValueDescr;
import org.jbpm.process.builder.ProcessBuildContext;

public class MVELReturnValueEvaluatorBuilder extends AbstractMVELBuilder implements ReturnValueEvaluatorBuilder {
    public MVELReturnValueEvaluatorBuilder() {
    }

    public void build(final PackageBuildContext context, final ReturnValueConstraintEvaluator constraintNode, final ReturnValueDescr descr, final ContextResolver contextResolver) {
        String text = descr.getText();
        Map<String, Class<?>> variables = new HashMap<String, Class<?>>();
        try {
            MVELDialect dialect = ((MVELDialect) (context.getDialect("mvel")));
            MVELAnalysisResult analysis = getAnalysis(context, descr, dialect, text, variables);
            if (analysis == null) {
                // not possible to get the analysis results
                return ;
            }
            buildReturnValueEvaluator(context, constraintNode, descr, contextResolver, dialect, analysis, text, variables);
        } catch (final Exception e) {
            context.getErrors().add(new DescrBuildError(context.getParentDescr(), descr, null, ((("Unable to build expression for 'constraint' " + (descr.getText())) + "': ") + e)));
        }
    }

    public void buildReturnValueEvaluator(final PackageBuildContext context, final ReturnValueConstraintEvaluator constraintNode, final ReturnValueDescr descr, final ContextResolver contextResolver, final MVELDialect dialect, final MVELAnalysisResult analysis, final String text, Map<String, Class<?>> variables) throws Exception {
        Set<String> variableNames = analysis.getNotBoundedIdentifiers();
        if (contextResolver != null) {
            for (String variableName : variableNames) {
                if (((analysis.getMvelVariables().keySet().contains(variableName)) || (variableName.equals("kcontext"))) || (variableName.equals("context"))) {
                    continue;
                }
                VariableScope variableScope = ((VariableScope) (contextResolver.resolveContext(VARIABLE_SCOPE, variableName)));
                if (variableScope == null) {
                    context.getErrors().add(new DescrBuildError(context.getParentDescr(), descr, null, (((("Could not find variable '" + variableName) + "' for action '") + (descr.getText())) + "'")));
                }else {
                    variables.put(variableName, context.getDialect().getTypeResolver().resolveType(variableScope.findVariable(variableName).getType().getStringType()));
                }
            }
        }
        MVELCompilationUnit unit = dialect.getMVELCompilationUnit(text, analysis, null, null, variables, context, "context", ProcessContext.class, false, EXPRESSION);
        // MVELReturnValueExpression expr = new MVELReturnValueExpression( unit, context.getDialect().getId() );
        MVELReturnValueEvaluator expr = new MVELReturnValueEvaluator(unit, dialect.getId());
        // expr.setVariableNames(variableNames);
        // set evaluator MVELReturnValueEvaluator{expr} to ReturnValueConstraintEvaluator{constraintNode}
        constraintNode.setEvaluator(expr);
        MVELDialectRuntimeData data = ((MVELDialectRuntimeData) (context.getPkg().getDialectRuntimeRegistry().getDialectData(dialect.getId())));
        // add compileable ReturnValueConstraintEvaluator{constraintNode} to MVELDialectRuntimeData{data}
        data.addCompileable(constraintNode, expr);
        // compile MVELDialectRuntimeData{data} to MVELReturnValueEvaluator{expr}
        expr.compile(data);
        // collect types String{"MVELReturnValue"} to MVELReturnValueEvaluatorBuilder{}
        collectTypes("MVELReturnValue", analysis, ((ProcessBuildContext) (context)));
    }
}

