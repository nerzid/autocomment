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


package org.jbpm.persistence.session.objects;

import java.io.Serializable;

/**
 * @author salaboy
 */
public class MyVariableSerializable implements Serializable {
    private static final long serialVersionUID = 510L;

    private String text = "";

    public MyVariableSerializable(String string) {
        MyVariableSerializable.this.text = string;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        MyVariableSerializable.this.text = text;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } 
        if ((getClass()) != (obj.getClass())) {
            return false;
        } 
        final MyVariableSerializable other = ((MyVariableSerializable) (obj));
        if ((MyVariableSerializable.this.text) == null ? (other.text) != null : !(MyVariableSerializable.this.text.equals(other.text))) {
            return false;
        } 
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = (37 * hash) + ((MyVariableSerializable.this.text) != null ? MyVariableSerializable.this.text.hashCode() : 0);
        return hash;
    }

    public String toString() {
        return "Serializable Variable: " + (MyVariableSerializable.this.getText());
    }
}

