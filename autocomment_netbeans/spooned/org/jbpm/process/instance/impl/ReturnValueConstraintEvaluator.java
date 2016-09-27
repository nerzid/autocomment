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

import org.drools.core.spi.CompiledInvoker;
import org.kie.api.definition.process.Connection;
import org.jbpm.workflow.core.Constraint;
import java.io.Externalizable;
import java.io.IOException;
import org.jbpm.workflow.instance.NodeInstance;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.drools.core.spi.ProcessContext;
import org.jbpm.process.instance.ProcessInstance;
import org.drools.core.spi.Wireable;

/**
 * Default implementation of a constraint.
 */
public class ReturnValueConstraintEvaluator implements ConstraintEvaluator , Externalizable , Wireable , Constraint {
    private static final long serialVersionUID = 510L;

    private String name;

    private String constraint;

    private int priority;

    private String dialect;

    private String type;

    private boolean isDefault = false;

    public ReturnValueConstraintEvaluator() {
    }

    private ReturnValueEvaluator evaluator;

    public String getConstraint() {
        return ReturnValueConstraintEvaluator.this.constraint;
    }

    public void setConstraint(final String constraint) {
        ReturnValueConstraintEvaluator.this.constraint = constraint;
    }

    public String getName() {
        return ReturnValueConstraintEvaluator.this.name;
    }

    public void setName(final String name) {
        ReturnValueConstraintEvaluator.this.name = name;
    }

    public String toString() {
        return ReturnValueConstraintEvaluator.this.name;
    }

    public int getPriority() {
        return ReturnValueConstraintEvaluator.this.priority;
    }

    public void setPriority(final int priority) {
        ReturnValueConstraintEvaluator.this.priority = priority;
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        ReturnValueConstraintEvaluator.this.dialect = dialect;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        ReturnValueConstraintEvaluator.this.type = type;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        ReturnValueConstraintEvaluator.this.isDefault = isDefault;
    }

    public void wire(Object object) {
        setEvaluator(((ReturnValueEvaluator) (object)));
    }

    public void setEvaluator(ReturnValueEvaluator evaluator) {
        ReturnValueConstraintEvaluator.this.evaluator = evaluator;
    }

    public ReturnValueEvaluator getReturnValueEvaluator() {
        return ReturnValueConstraintEvaluator.this.evaluator;
    }

    public boolean evaluate(NodeInstance instance, Connection connection, Constraint constraint) {
        Object value;
        try {
            ProcessContext context = new ProcessContext(((ProcessInstance) (instance.getProcessInstance())).getKnowledgeRuntime());
            context.setNodeInstance(instance);
            value = ReturnValueConstraintEvaluator.this.evaluator.evaluate(context);
        } catch (Exception e) {
            throw new RuntimeException("unable to execute ReturnValueEvaluator: ", e);
        }
        if (!(value instanceof Boolean)) {
            throw new RuntimeException(((("Constraints must return boolean values: " + value) + " for expression ") + constraint));
        } 
        return ((Boolean) (value)).booleanValue();
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        ReturnValueConstraintEvaluator.this.evaluator = ((ReturnValueEvaluator) (in.readObject()));
        ReturnValueConstraintEvaluator.this.name = in.readUTF();
        ReturnValueConstraintEvaluator.this.constraint = ((String) (in.readObject()));
        ReturnValueConstraintEvaluator.this.priority = in.readInt();
        ReturnValueConstraintEvaluator.this.dialect = in.readUTF();
        ReturnValueConstraintEvaluator.this.type = ((String) (in.readObject()));
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        if ((ReturnValueConstraintEvaluator.this.evaluator) instanceof CompiledInvoker) {
            out.writeObject(null);
        } else {
            out.writeObject(ReturnValueConstraintEvaluator.this.evaluator);
        }
        out.writeUTF(ReturnValueConstraintEvaluator.this.name);
        out.writeObject(ReturnValueConstraintEvaluator.this.constraint);
        out.writeInt(ReturnValueConstraintEvaluator.this.priority);
        out.writeUTF(dialect);
        out.writeObject(type);
    }

    public void setMetaData(String name, Object value) {
        // Do nothing
    }

    public Object getMetaData(String name) {
        return null;
    }
}

