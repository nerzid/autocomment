

package org.jbpm.process.builder;

import org.jbpm.test.util.AbstractBaseTest;
import org.jbpm.process.instance.impl.Action;
import org.drools.compiler.lang.descr.ActionDescr;
import org.jbpm.workflow.core.node.ActionNode;
import java.util.Arrays;
import org.junit.Assert;
import java.util.Collection;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.jbpm.workflow.core.DroolsAction;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.common.InternalWorkingMemory;
import org.jbpm.process.builder.dialect.javascript.JavaScriptActionBuilder;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.spi.ProcessContext;
import org.drools.compiler.lang.descr.ProcessDescr;
import org.jbpm.process.builder.dialect.ProcessDialect;
import org.jbpm.process.builder.dialect.ProcessDialectRegistry;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import java.io.StringReader;
import org.junit.Test;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;

public class JavaScriptActionBuilderTest extends AbstractBaseTest {
    @Test
    public void testSimpleAction() throws Exception {
        final InternalKnowledgePackage pkg = new org.drools.core.definitions.impl.KnowledgePackageImpl("pkg1");
        ActionDescr actionDescr = new ActionDescr();
        actionDescr.setText("var testString; print('Hello')");
        KnowledgeBuilderImpl pkgBuilder = new KnowledgeBuilderImpl(pkg);
        DialectCompiletimeRegistry dialectRegistry = pkgBuilder.getPackageRegistry(pkg.getName()).getDialectCompiletimeRegistry();
        ProcessDescr processDescr = new ProcessDescr();
        processDescr.setClassName("Process1");
        processDescr.setName("Process1");
        WorkflowProcessImpl process = new WorkflowProcessImpl();
        process.setName("Process1");
        process.setPackageName("pkg1");
        ProcessBuildContext context = new ProcessBuildContext(pkgBuilder, pkgBuilder.getPackage(), null, processDescr, dialectRegistry, null);
        context.init(pkgBuilder, pkg, null, dialectRegistry, null, null);
        pkgBuilder.addPackageFromDrl(new StringReader("package pkg1;\nglobal String testField;\n"));
        ActionNode actionNode = new ActionNode();
        DroolsAction action = new org.jbpm.workflow.core.impl.DroolsConsequenceAction("JavaScript", null);
        actionNode.setAction(action);
        ProcessDialect dialect = ProcessDialectRegistry.getDialect("JavaScript");
        dialect.getActionBuilder().build(context, action, actionDescr, actionNode);
        dialect.addProcess(context);
        final JavaScriptActionBuilder builder = new JavaScriptActionBuilder();
        builder.build(context, action, actionDescr, actionNode);
        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(((Collection) (Arrays.asList(pkgBuilder.getPackage()))));
        final StatefulKnowledgeSession wm = kbase.newStatefulKnowledgeSession();
        wm.setGlobal("testField", "vagon");
        ProcessContext processContext = new ProcessContext(((InternalWorkingMemory) (wm)).getKnowledgeRuntime());
        ((Action) (actionNode.getAction().getMetaData("Action"))).execute(processContext);
        Assert.assertEquals("vagon", wm.getGlobal("testField").toString());
    }
}

