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

import java.io.Externalizable;
import org.drools.core.spi.GlobalResolver;
import java.io.IOException;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.base.mvel.MVELCompilationUnit;
import org.drools.core.base.mvel.MVELCompileable;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.core.util.MVELSafeHelper;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.kie.api.runtime.process.ProcessContext;
import org.drools.core.definitions.rule.impl.RuleImpl;
import java.io.Serializable;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.impl.StatelessKnowledgeSessionImpl;
import org.mvel2.integration.VariableResolverFactory;

public class MVELAction implements Action , Externalizable , MVELCompileable {
    private static final long serialVersionUID = 510L;

    private MVELCompilationUnit unit;

    private String id;

    private Serializable expr;

    public MVELAction() {
    }

    public MVELAction(final MVELCompilationUnit unit, final String id) {
        MVELAction.this.unit = unit;
        MVELAction.this.id = id;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        id = in.readUTF();
        unit = ((MVELCompilationUnit) (in.readObject()));
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(id);
        out.writeObject(unit);
    }

    public void compile(MVELDialectRuntimeData data) {
        expr = unit.getCompiledExpression(data);
    }

    public void compile(MVELDialectRuntimeData data, RuleImpl rule) {
        expr = unit.getCompiledExpression(data);
    }

    public String getDialect() {
        return id;
    }

    public void execute(ProcessContext context) throws Exception {
        int length = unit.getOtherIdentifiers().length;
        Object[] vars = new Object[length];
        if ((unit.getOtherIdentifiers()) != null) {
            for (int i = 0; i < length; i++) {
                vars[i] = context.getVariable(unit.getOtherIdentifiers()[i]);
            }
        } 
        InternalWorkingMemory internalWorkingMemory = null;
        if ((context.getKieRuntime()) instanceof StatefulKnowledgeSessionImpl) {
            internalWorkingMemory = ((StatefulKnowledgeSessionImpl) (context.getKieRuntime())).getInternalWorkingMemory();
        } else if ((context.getKieRuntime()) instanceof StatelessKnowledgeSessionImpl) {
            StatefulKnowledgeSession statefulKnowledgeSession = ((StatelessKnowledgeSessionImpl) (context.getKieRuntime())).newWorkingMemory();
            internalWorkingMemory = ((StatefulKnowledgeSessionImpl) (statefulKnowledgeSession)).getInternalWorkingMemory();
        } 
        VariableResolverFactory factory = // No previous declarations
        // No rule
        // No "right object"
        // No (left) tuples
        unit.getFactory(context, null, null, null, null, vars, internalWorkingMemory, ((GlobalResolver) (context.getKieRuntime().getGlobals())));
        // KnowledgePackage pkg = context.getKnowledgeRuntime().getKnowledgeBase().getKnowledgePackage( "MAIN" );
        // if ( pkg != null && pkg instanceof KnowledgePackageImp) {
        // MVELDialectRuntimeData data = ( MVELDialectRuntimeData ) ((KnowledgePackageImp) pkg).pkg.getDialectRuntimeRegistry().getDialectData( id );
        // factory.setNextFactory( data.getFunctionFactory() );
        // }
        // 
        MVELSafeHelper.getEvaluator().executeExpression(MVELAction.this.expr, null, factory);
    }

    public Serializable getCompExpr() {
        return expr;
    }
}

