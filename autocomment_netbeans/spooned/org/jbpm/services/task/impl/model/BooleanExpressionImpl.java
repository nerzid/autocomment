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


package org.jbpm.services.task.impl.model;

import org.kie.internal.task.api.model.BooleanExpression;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.IOException;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "BooleanExpression")
@SequenceGenerator(allocationSize = 1, name = "booleanExprIdSeq", sequenceName = "BOOLEANEXPR_ID_SEQ")
public class BooleanExpressionImpl implements BooleanExpression {
    @Id
    @GeneratedValue(generator = "booleanExprIdSeq", strategy = GenerationType.AUTO)
    private Long id;

    private String type;

    @Lob
    @Column(length = 65535)
    private String expression;

    public BooleanExpressionImpl() {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(id);
        if ((type) == null) {
            type = "";
        } 
        out.writeUTF(type);
        if ((expression) == null) {
            expression = "";
        } 
        out.writeUTF(expression);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        id = in.readLong();
        type = in.readUTF();
        expression = in.readUTF();
    }

    public BooleanExpressionImpl(String type, String expression) {
        BooleanExpressionImpl.this.type = type;
        BooleanExpressionImpl.this.expression = expression;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        BooleanExpressionImpl.this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        BooleanExpressionImpl.this.type = type;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        BooleanExpressionImpl.this.expression = expression;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((expression) == null ? 0 : expression.hashCode());
        result = (prime * result) + ((type) == null ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ((BooleanExpressionImpl.this) == obj)
            return true;
        
        if (obj == null)
            return false;
        
        if (!(obj instanceof BooleanExpressionImpl))
            return false;
        
        BooleanExpressionImpl other = ((BooleanExpressionImpl) (obj));
        if ((expression) == null) {
            if ((other.expression) != null)
                return false;
            
        } else if (!(expression.equals(other.expression)))
            return false;
        
        if ((type) == null) {
            if ((other.type) != null)
                return false;
            
        } else if (!(type.equals(other.type)))
            return false;
        
        return true;
    }
}

