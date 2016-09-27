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


package org.jbpm.process.builder.dialect.java;

import org.drools.compiler.compiler.AnalysisResult;
import java.util.ArrayList;
import org.drools.compiler.lang.descr.BaseDescr;
import org.jbpm.process.core.ContextResolver;
import java.util.HashMap;
import java.util.HashSet;
import org.drools.compiler.rule.builder.dialect.java.JavaAnalysisResult;
import org.drools.compiler.rule.builder.dialect.java.parser.JavaLocalDeclarationDescr;
import java.util.List;
import java.util.Map;
import org.jbpm.process.builder.ProcessBuildContext;
import java.util.Set;
import org.drools.core.util.StringUtils;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRegistry;
import org.mvel2.templates.TemplateRuntime;
import org.jbpm.process.core.context.variable.VariableScope;

public class AbstractJavaProcessBuilder {
    protected static final TemplateRegistry RULE_REGISTRY = new org.mvel2.templates.SimpleTemplateRegistry();

    protected static final TemplateRegistry INVOKER_REGISTRY = new org.mvel2.templates.SimpleTemplateRegistry();

    static {
        AbstractJavaProcessBuilder.RULE_REGISTRY.addNamedTemplate("rules", TemplateCompiler.compileTemplate(AbstractJavaProcessBuilder.class.getResourceAsStream("javaRule.mvel")));
        AbstractJavaProcessBuilder.INVOKER_REGISTRY.addNamedTemplate("invokers", TemplateCompiler.compileTemplate(AbstractJavaProcessBuilder.class.getResourceAsStream("javaInvokers.mvel")));
        /**
         * Process these templates
         */
        TemplateRuntime.execute(AbstractJavaProcessBuilder.RULE_REGISTRY.getNamedTemplate("rules"), null, AbstractJavaProcessBuilder.RULE_REGISTRY);
        TemplateRuntime.execute(AbstractJavaProcessBuilder.INVOKER_REGISTRY.getNamedTemplate("invokers"), null, AbstractJavaProcessBuilder.INVOKER_REGISTRY);
    }

    public TemplateRegistry getRuleTemplateRegistry() {
        return AbstractJavaProcessBuilder.RULE_REGISTRY;
    }

    public TemplateRegistry getInvokerTemplateRegistry() {
        return AbstractJavaProcessBuilder.INVOKER_REGISTRY;
    }

    public Map createVariableContext(final String className, final String text, final ProcessBuildContext context, final String[] globals) {
        final Map map = new HashMap();
        map.put("methodName", className);
        map.put("package", context.getPkg().getName());
        map.put("processClassName", StringUtils.ucFirst(context.getProcessDescr().getClassName()));
        map.put("invokerClassName", (((context.getProcessDescr().getClassName()) + (StringUtils.ucFirst(className))) + "Invoker"));
        if (text != null) {
            map.put("text", text);
            map.put("hashCode", new Integer(text.hashCode()));
        } 
        final List globalTypes = new ArrayList(globals.length);
        for (int i = 0, length = globals.length; i < length; i++) {
            globalTypes.add(context.getPkg().getGlobals().get(globals[i]).replace('$', '.'));
        }
        map.put("globals", globals);
        map.put("globalTypes", globalTypes);
        return map;
    }

    public Map createVariableContext(final String className, final String text, final ProcessBuildContext context, final String[] globals, final Set<String> unboundIdentifiers, final ContextResolver contextResolver) {
        Map map = createVariableContext(className, text, context, globals);
        List<String> variables = new ArrayList<String>();
        final List variableTypes = new ArrayList(globals.length);
        for (String variableName : unboundIdentifiers) {
            VariableScope variableScope = ((VariableScope) (contextResolver.resolveContext(VariableScope.VARIABLE_SCOPE, variableName)));
            if (variableScope != null) {
                variables.add(variableName);
                variableTypes.add(variableScope.findVariable(variableName).getType().getStringType());
            } 
        }
        map.put("variables", variables);
        map.put("variableTypes", variableTypes);
        return map;
    }

    public void generateTemplates(final String ruleTemplate, final String invokerTemplate, final ProcessBuildContext context, final String className, final Map vars, final Object invokerLookup, final BaseDescr descrLookup) {
        TemplateRegistry registry = getRuleTemplateRegistry();
        context.getMethods().add(((String) (TemplateRuntime.execute(registry.getNamedTemplate(ruleTemplate), null, new org.mvel2.integration.impl.MapVariableResolverFactory(vars), registry))));
        registry = getInvokerTemplateRegistry();
        final String invokerClassName = ((((context.getPkg().getName()) + ".") + (context.getProcessDescr().getClassName())) + (StringUtils.ucFirst(className))) + "Invoker";
        context.getInvokers().put(invokerClassName, ((String) (TemplateRuntime.execute(registry.getNamedTemplate(invokerTemplate), null, new org.mvel2.integration.impl.MapVariableResolverFactory(vars), registry))));
        context.getInvokerLookups().put(invokerClassName, invokerLookup);
        context.getDescrLookups().put(invokerClassName, descrLookup);
    }

    protected void collectTypes(String key, AnalysisResult analysis, ProcessBuildContext context) {
        if ((context.getProcess()) != null) {
            Set<String> referencedTypes = new HashSet<String>();
            Set<String> unqualifiedClasses = new HashSet<String>();
            JavaAnalysisResult javaAnalysis = ((JavaAnalysisResult) (analysis));
            LOCAL_VAR : for (JavaLocalDeclarationDescr localDeclDescr : javaAnalysis.getLocalVariablesMap().values()) {
                String type = localDeclDescr.getRawType();
                if (type.contains(".")) {
                    referencedTypes.add(type);
                } else {
                    for (String alreadyRefdType : referencedTypes) {
                        String alreadyRefdSimpleName = alreadyRefdType.substring(((alreadyRefdType.lastIndexOf(".")) + 1));
                        if (type.equals(alreadyRefdSimpleName)) {
                            continue LOCAL_VAR;
                        } 
                    }
                    unqualifiedClasses.add(type);
                }
            }
            context.getProcess().getMetaData().put((key + "ReferencedTypes"), referencedTypes);
            context.getProcess().getMetaData().put((key + "UnqualifiedTypes"), unqualifiedClasses);
        } 
    }
}

