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


package org.jbpm.process.audit;

import java.util.Arrays;
import java.util.Collection;
import java.io.InputStreamReader;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import java.io.Reader;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.jbpm.process.instance.impl.demo.UIWorkItemHandler;

public class ProcessInstanceExecutor {
    public static final void main(String[] args) {
        try {
            // load the process
            KnowledgeBase kbase = ProcessInstanceExecutor.createKnowledgeBase();
            // create a new session
            StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
            new JPAWorkingMemoryDbLogger(session);
            UIWorkItemHandler uiHandler = new UIWorkItemHandler();
            session.getWorkItemManager().registerWorkItemHandler("Human Task", uiHandler);
            uiHandler.setVisible(true);
            new ProcessInstanceExecutorFrame(session).setVisible(true);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Creates the knowledge base by loading the process definition.
     */
    private static KnowledgeBase createKnowledgeBase() throws Exception {
        // create a builder
        KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
        // load the process
        Reader source = new InputStreamReader(ProcessInstanceExecutor.class.getResourceAsStream("/ruleflow.rf"));
        // add process Reader{source} to KnowledgeBuilderImpl{builder}
        builder.addProcessFromXml(source);
        source = new InputStreamReader(ProcessInstanceExecutor.class.getResourceAsStream("/ruleflow2.rf"));
        // add process Reader{source} to KnowledgeBuilderImpl{builder}
        builder.addProcessFromXml(source);
        // create the knowledge base
        InternalKnowledgePackage pkg = builder.getPackage();
        KnowledgeBase ruleBase = KnowledgeBaseFactory.newKnowledgeBase();
        // add knowledge List{((Collection) (Arrays.asList(pkg)))} to KnowledgeBase{ruleBase}
        ruleBase.addKnowledgePackages(((Collection) (Arrays.asList(pkg))));
        return ruleBase;
    }
}

