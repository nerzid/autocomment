

package org.jbpm.bpmn2;

import org.jbpm.process.builder.ActionBuilder;
import org.drools.compiler.lang.descr.ActionDescr;
import org.drools.compiler.compiler.AnalysisResult;
import java.util.ArrayList;
import org.drools.compiler.lang.descr.BaseDescr;
import org.jbpm.process.core.ContextResolver;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.process.builder.dialect.java.JavaActionBuilder;
import org.jbpm.process.builder.dialect.java.JavaProcessDialect;
import org.jbpm.process.builder.dialect.java.JavaReturnValueEvaluatorBuilder;
import org.kie.api.KieBase;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.drools.compiler.rule.builder.PackageBuildContext;
import org.jbpm.process.builder.dialect.ProcessDialectRegistry;
import org.jbpm.process.instance.impl.ReturnValueConstraintEvaluator;
import org.drools.compiler.compiler.ReturnValueDescr;
import org.jbpm.process.builder.ReturnValueEvaluatorBuilder;
import org.junit.Test;

public class CompilationTest extends JbpmBpmn2TestCase {
    private static final Logger logger = LoggerFactory.getLogger(XMLBPMNProcessDumperTest.class);

    public CompilationTest() {
        super(false);
    }

    @Test
    public void testReturnValueDescrCreation() throws Exception {
        CompilationTest.TestJavaProcessDialect javaProcessDialect = new CompilationTest.TestJavaProcessDialect();
        ProcessDialectRegistry.setDialect("java", javaProcessDialect);
        String filename = "BPMN2-GatewaySplit-SequenceConditions.bpmn2";
        KieBase kbase = createKnowledgeBase(filename);
        assertFalse((("No " + (ActionDescr.class.getSimpleName())) + " instances caught for testing!"), javaProcessDialect.getActionDescrs().isEmpty());
        for (BaseDescr descr : javaProcessDialect.getActionDescrs()) {
            assertNotNull(((descr.getClass().getSimpleName()) + " has a null resource field"), descr.getResource());
        }
        assertFalse((("No " + (ReturnValueDescr.class.getSimpleName())) + " instances caught for testing!"), javaProcessDialect.getReturnValueDescrs().isEmpty());
        for (BaseDescr descr : javaProcessDialect.getReturnValueDescrs()) {
            assertNotNull(((descr.getClass().getSimpleName()) + " has a null resource field"), descr.getResource());
        }
    }

    private static class TestJavaProcessDialect extends JavaProcessDialect {
        private ActionBuilder actionBuilder = new CompilationTest.TestJavaActionBuilder();

        private ReturnValueEvaluatorBuilder returnValueEvaluatorBuilder = new CompilationTest.TestJavaReturnValueEvaluatorBuilder();

        @Override
        public ActionBuilder getActionBuilder() {
            return actionBuilder;
        }

        @Override
        public ReturnValueEvaluatorBuilder getReturnValueEvaluatorBuilder() {
            return returnValueEvaluatorBuilder;
        }

        public List<ActionDescr> getActionDescrs() {
            return ((CompilationTest.TestJavaActionBuilder) (actionBuilder)).actionDescrs;
        }

        public List<ReturnValueDescr> getReturnValueDescrs() {
            return ((CompilationTest.TestJavaReturnValueEvaluatorBuilder) (returnValueEvaluatorBuilder)).returnValueDescrs;
        }
    }

    private static class TestJavaActionBuilder extends JavaActionBuilder {
        List<ActionDescr> actionDescrs = new ArrayList<ActionDescr>();

        @Override
        protected void buildAction(PackageBuildContext context, DroolsAction action, ActionDescr actionDescr, ContextResolver contextResolver, String className, AnalysisResult analysis) {
            actionDescrs.add(actionDescr);
            super.buildAction(context, action, actionDescr, contextResolver, className, analysis);
        }
    }

    private static class TestJavaReturnValueEvaluatorBuilder extends JavaReturnValueEvaluatorBuilder {
        List<ReturnValueDescr> returnValueDescrs = new ArrayList<ReturnValueDescr>();

        @Override
        protected void buildReturnValueEvaluator(PackageBuildContext context, ReturnValueConstraintEvaluator constraintNode, ReturnValueDescr descr, ContextResolver contextResolver, String className, AnalysisResult analysis) {
            returnValueDescrs.add(descr);
            super.buildReturnValueEvaluator(context, constraintNode, descr, contextResolver, className, analysis);
        }
    }
}

