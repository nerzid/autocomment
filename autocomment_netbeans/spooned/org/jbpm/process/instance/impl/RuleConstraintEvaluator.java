/**
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

import org.kie.api.definition.process.Connection;
import org.jbpm.workflow.core.Constraint;
import org.drools.core.common.InternalAgenda;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.instance.NodeInstance;
import org.jbpm.process.instance.ProcessInstance;
import java.io.Serializable;
import org.kie.api.runtime.process.WorkflowProcessInstance;

/**
 * Default implementation of a constraint.
 */
public class RuleConstraintEvaluator implements ConstraintEvaluator , Serializable , Constraint {
    private static final long serialVersionUID = 510L;

    private String name;

    private String constraint;

    private int priority;

    private String dialect;

    private String type;

    private boolean isDefault;

    public String getConstraint() {
        return RuleConstraintEvaluator.this.constraint;
    }

    public void setConstraint(final String constraint) {
        RuleConstraintEvaluator.this.constraint = constraint;
    }

    public String getName() {
        return RuleConstraintEvaluator.this.name;
    }

    public void setName(final String name) {
        RuleConstraintEvaluator.this.name = name;
    }

    public String toString() {
        return RuleConstraintEvaluator.this.name;
    }

    public int getPriority() {
        return RuleConstraintEvaluator.this.priority;
    }

    public void setPriority(final int priority) {
        RuleConstraintEvaluator.this.priority = priority;
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        RuleConstraintEvaluator.this.dialect = dialect;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        RuleConstraintEvaluator.this.type = type;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        RuleConstraintEvaluator.this.isDefault = isDefault;
    }

    public boolean evaluate(NodeInstance instance, Connection connection, Constraint constraint) {
        WorkflowProcessInstance processInstance = instance.getProcessInstance();
        InternalAgenda agenda = ((InternalAgenda) (((ProcessInstance) (processInstance)).getKnowledgeRuntime().getAgenda()));
        String rule = (((((("RuleFlow-Split-" + (processInstance.getProcessId())) + "-") + (((Node) (instance.getNode())).getUniqueId())) + "-") + (((Node) (connection.getTo())).getUniqueId())) + "-") + (connection.getToType());
        return agenda.isRuleActiveInRuleFlowGroup("DROOLS_SYSTEM", rule, processInstance.getId());
    }

    public Object getMetaData(String name) {
        return null;
    }

    public void setMetaData(String name, Object value) {
        // Do nothing
    }
}

