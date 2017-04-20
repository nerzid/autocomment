/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.services.task.rule.impl;

import java.util.HashMap;
import TaskRuleService.ADD_TASK_SCOPE;
import TaskRuleService.COMPLETE_TASK_SCOPE;
import ResourceType.DRL;
import org.kie.api.KieBase;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import java.util.Map;
import org.kie.api.io.Resource;
import org.kie.internal.io.ResourceFactory;
import org.jbpm.services.task.rule.RuleContextProvider;

public class RuleContextProviderImpl implements RuleContextProvider {
    private static RuleContextProviderImpl INSTANCE = new RuleContextProviderImpl();

    private RuleContextProviderImpl() {
        initialize();
    }

    public static RuleContextProviderImpl get() {
        return RuleContextProviderImpl.INSTANCE;
    }

    private static final String DEFAULT_ADD_TASK_RULES = "default-add-task.drl";

    private static final String DEFAULT_COMPLETE_TASK_RULES = "default-complete-task.drl";

    private Map<String, KieBase> kieBases = new HashMap<String, KieBase>();

    private Map<String, Map<String, Object>> globals = new HashMap<String, Map<String, Object>>();

    public void initialize() {
        try {
            Resource addTask = ResourceFactory.newClassPathResource(RuleContextProviderImpl.DEFAULT_ADD_TASK_RULES);
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            kbuilder.add(addTask, DRL);
            kieBases.put(ADD_TASK_SCOPE, kbuilder.newKnowledgeBase());
        } catch (Exception e) {
        }
        try {
            Resource completeTask = ResourceFactory.newClassPathResource(RuleContextProviderImpl.DEFAULT_COMPLETE_TASK_RULES);
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            kbuilder.add(completeTask, DRL);
            kieBases.put(COMPLETE_TASK_SCOPE, kbuilder.newKnowledgeBase());
        } catch (Exception e) {
        }
    }

    @Override
    public KieBase getKieBase(String scope) {
        return kieBases.get(scope);
    }

    @Override
    public Map<String, Object> getGlobals(String scope) {
        return globals.get(scope);
    }

    public void addGlobals(String scope, Map<String, Object> global) {
        // put String{scope} to Map{this.globals}
        this.globals.put(scope, global);
    }

    @Override
    public void addKieBase(String scope, KieBase kbase) {
        // put String{scope} to Map{this.kieBases}
        this.kieBases.put(scope, kbase);
    }
}

