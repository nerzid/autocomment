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


package org.jbpm.integrationtests.test;

import java.io.Serializable;

public class Person implements Serializable {
    private static final long serialVersionUID = 510L;

    private String name;

    private int age;

    public Person() {
    }

    public Person(String name, int age) {
        super();
        Person.this.name = name;
        Person.this.age = age;
    }

    public Person(final String name) {
        this(name, 0);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        Person.this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        Person.this.age = age;
    }

    public String toString() {
        return ((("[Person name='" + (Person.this.name)) + " age='") + (Person.this.age)) + "']";
    }

    /**
     * @inheritDoc
     */
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = (PRIME * result) + (Person.this.age);
        result = (PRIME * result) + ((Person.this.name) == null ? 0 : Person.this.name.hashCode());
        return result;
    }

    /**
     * @inheritDoc
     */
    public boolean equals(final Object obj) {
        if ((Person.this) == obj) {
            return true;
        } 
        if (obj == null) {
            return false;
        } 
        if ((getClass()) != (obj.getClass())) {
            return false;
        } 
        final Person other = ((Person) (obj));
        if ((Person.this.age) != (other.age)) {
            return false;
        } 
        if ((Person.this.name) == null) {
            if ((other.name) != null) {
                return false;
            } 
        } else if (!(Person.this.name.equals(other.name))) {
            return false;
        } 
        return true;
    }
}

