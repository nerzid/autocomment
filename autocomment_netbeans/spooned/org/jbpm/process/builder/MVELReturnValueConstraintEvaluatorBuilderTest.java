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


package org.jbpm.process.builder;

import org.jbpm.test.util.AbstractBaseTest;
import java.util.ArrayList;
import org.junit.Assert;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.kie.internal.definition.KnowledgePackage;
import java.util.List;
import org.drools.compiler.rule.builder.dialect.mvel.MVELDialect;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.jbpm.process.instance.impl.MVELReturnValueEvaluator;
import org.jbpm.process.builder.dialect.mvel.MVELReturnValueEvaluatorBuilder;
import org.drools.compiler.rule.builder.PackageBuildContext;
import org.jbpm.process.instance.impl.ReturnValueConstraintEvaluator;
import org.drools.compiler.compiler.ReturnValueDescr;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.workflow.instance.node.SplitInstance;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import java.io.StringReader;
import org.junit.Test;

public class MVELReturnValueConstraintEvaluatorBuilderTest extends AbstractBaseTest {
    @Test
    public void testSimpleReturnValueConstraintEvaluator() throws Exception {
        final InternalKnowledgePackage pkg = new org.drools.core.definitions.impl.KnowledgePackageImpl("pkg1");
        ReturnValueDescr descr = new ReturnValueDescr();
        descr.setText("return value");
        KnowledgeBuilderImpl pkgBuilder = new KnowledgeBuilderImpl(pkg);
        DialectCompiletimeRegistry dialectRegistry = pkgBuilder.getPackageRegistry(pkg.getName()).getDialectCompiletimeRegistry();
        MVELDialect mvelDialect = ((MVELDialect) (dialectRegistry.getDialect("mvel")));
        PackageBuildContext context = new PackageBuildContext();
        context.init(pkgBuilder, pkg, null, dialectRegistry, mvelDialect, null);
        pkgBuilder.addPackageFromDrl(new StringReader("package pkg1;\nglobal Boolean value;"));
        ReturnValueConstraintEvaluator node = new ReturnValueConstraintEvaluator();
        final MVELReturnValueEvaluatorBuilder builder = new MVELReturnValueEvaluatorBuilder();
        builder.build(context, node, descr, null);
        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        List<KnowledgePackage> packages = new ArrayList<KnowledgePackage>();
        packages.add(pkgBuilder.getPackage());
        kbase.addKnowledgePackages(packages);
        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.setGlobal("value", true);
        RuleFlowProcessInstance processInstance = new RuleFlowProcessInstance();
        processInstance.setKnowledgeRuntime(((InternalKnowledgeRuntime) (ksession)));
        SplitInstance splitInstance = new SplitInstance();
        splitInstance.setProcessInstance(processInstance);
        MVELDialectRuntimeData data = ((MVELDialectRuntimeData) (pkgBuilder.getPackage().getDialectRuntimeRegistry().getDialectData("mvel")));
        ((MVELReturnValueEvaluator) (node.getReturnValueEvaluator())).compile(data);
        Assert.assertTrue(node.evaluate(splitInstance, null, null));
        ksession.setGlobal("value", false);
        Assert.assertFalse(node.evaluate(splitInstance, null, null));
    }
}

