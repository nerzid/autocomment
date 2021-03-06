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
import org.jbpm.process.instance.impl.Action;
import org.drools.compiler.lang.descr.ActionDescr;
import org.jbpm.workflow.core.node.ActionNode;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Assert;
import java.util.Collection;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.jbpm.workflow.core.DroolsAction;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.compiler.rule.builder.dialect.java.JavaDialect;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import java.util.List;
import org.drools.core.spi.ProcessContext;
import org.drools.compiler.lang.descr.ProcessDescr;
import org.jbpm.process.builder.dialect.ProcessDialect;
import org.jbpm.process.builder.dialect.ProcessDialectRegistry;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import java.io.StringReader;
import org.junit.Test;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;

public class JavaActionBuilderTest extends AbstractBaseTest {
    @Test
    public void testSimpleAction() throws Exception {
        final InternalKnowledgePackage pkg = new org.drools.core.definitions.impl.KnowledgePackageImpl("pkg1");
        ActionDescr actionDescr = new ActionDescr();
        actionDescr.setText("list.add( \"hello world\" );");
        KnowledgeBuilderImpl pkgBuilder = new KnowledgeBuilderImpl(pkg);
        DialectCompiletimeRegistry dialectRegistry = pkgBuilder.getPackageRegistry(pkg.getName()).getDialectCompiletimeRegistry();
        JavaDialect javaDialect = ((JavaDialect) (dialectRegistry.getDialect("java")));
        ProcessDescr processDescr = new ProcessDescr();
        processDescr.setClassName("Process1");
        processDescr.setName("Process1");
        WorkflowProcessImpl process = new WorkflowProcessImpl();
        process.setName("Process1");
        process.setPackageName("pkg1");
        ProcessBuildContext context = new ProcessBuildContext(pkgBuilder, pkgBuilder.getPackage(), null, processDescr, dialectRegistry, javaDialect);
        context.init(pkgBuilder, pkg, null, dialectRegistry, javaDialect, null);
        pkgBuilder.addPackageFromDrl(new StringReader("package pkg1;\nglobal java.util.List list;\n"));
        ActionNode actionNode = new ActionNode();
        DroolsAction action = new org.jbpm.workflow.core.impl.DroolsConsequenceAction("java", null);
        actionNode.setAction(action);
        ProcessDialect dialect = ProcessDialectRegistry.getDialect("java");
        dialect.getActionBuilder().build(context, action, actionDescr, actionNode);
        dialect.addProcess(context);
        javaDialect.compileAll();
        Assert.assertEquals(0, javaDialect.getResults().size());
        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(((Collection) (Arrays.asList(pkgBuilder.getPackage()))));
        final StatefulKnowledgeSession wm = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        wm.setGlobal("list", list);
        ProcessContext processContext = new ProcessContext(((InternalWorkingMemory) (wm)).getKnowledgeRuntime());
        ((Action) (actionNode.getAction().getMetaData("Action"))).execute(processContext);
        Assert.assertEquals("hello world", list.get(0));
    }
}

