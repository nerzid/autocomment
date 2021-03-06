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


package org.jbpm.test;

import org.kie.internal.KnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

/**
 * This is a sample file to launch a process.
 */
public class ProcessMain {
    private static final Logger logger = LoggerFactory.getLogger(ProcessMain.class);

    public static final void main(String[] args) throws Exception {
        JBPMHelper.startUp();
        // load up the knowledge base
        KnowledgeBase kbase = ProcessMain.readKnowledgeBase();
        StatefulKnowledgeSession ksession = JBPMHelper.newStatefulKnowledgeSession(kbase);
        // start a new process instance
        ksession.startProcess("com.sample.bpmn.hello");
        ProcessMain.logger.info("Process started ...");
    }

    private static KnowledgeBase readKnowledgeBase() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("humantask.bpmn"), ResourceType.BPMN2);
        return kbuilder.newKnowledgeBase();
    }

    private static void startUp() {
        JBPMHelper.startH2Server();
        JBPMHelper.setupDataSource();
        // please comment this line if you already have the task service running,
        // for example when running the jbpm-installer
        JBPMHelper.startTaskService();
    }
}

