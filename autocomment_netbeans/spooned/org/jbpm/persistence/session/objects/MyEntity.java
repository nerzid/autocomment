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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author salaboy
 */
@Entity
public class MyEntity implements Serializable {
    private static final long serialVersionUID = 510L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String test;

    public MyEntity() {
    }

    public MyEntity(String string) {
        MyEntity.this.test = string;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        MyEntity.this.id = id;
    }

    /**
     * @return the test
     */
    public String getTest() {
        return test;
    }

    /**
     * @param test the test to set
     */
    public void setTest(String test) {
        MyEntity.this.test = test;
    }

    public String toString() {
        return (("VARIABLE: " + (MyEntity.this.getId())) + " - ") + (MyEntity.this.getTest());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } 
        if ((getClass()) != (obj.getClass())) {
            return false;
        } 
        final MyEntity other = ((MyEntity) (obj));
        if (((MyEntity.this.id) != (other.id)) && (((MyEntity.this.id) == null) || (!(MyEntity.this.id.equals(other.id))))) {
            return false;
        } 
        if ((MyEntity.this.test) == null ? (other.test) != null : !(MyEntity.this.test.equals(other.test))) {
            return false;
        } 
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = (41 * hash) + ((MyEntity.this.id) != null ? MyEntity.this.id.hashCode() : 0);
        hash = (41 * hash) + ((MyEntity.this.test) != null ? MyEntity.this.test.hashCode() : 0);
        return hash;
    }
}

