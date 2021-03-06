/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.test.container.archive.registerrestservice;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "person")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class Person implements Serializable {
    /**
     * Default ID.
     */
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "name")
    private String name;

    @XmlElement(defaultValue = "", name = "middlename")
    private String middlename;

    @XmlElement(name = "surname")
    private String surname;

    public Person() {
    }

    /**
     * @param name
     * @param middlename
     * @param surname
     */
    public Person(String name, String middlename, String surname) {
        Person.this.name = name;
        Person.this.middlename = middlename;
        Person.this.surname = surname;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        Person.this.name = name;
    }

    /**
     * @return the middlename
     */
    public String getMiddlename() {
        return middlename;
    }

    /**
     * @param middlename
     *            the middlename to set
     */
    public void setMiddlename(String middlename) {
        Person.this.middlename = middlename;
    }

    /**
     * @return the surname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * @param surname
     *            the surname to set
     */
    public void setSurname(String surname) {
        Person.this.surname = surname;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder(name);
        b.append(" - ");
        if (((middlename) != null) && (!(middlename.isEmpty()))) {
            b.append(middlename);
            b.append(" - ");
        } 
        b.append(surname);
        return b.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((middlename) == null ? 1 : middlename.hashCode());
        result = (prime * result) + ((name) == null ? 1 : name.hashCode());
        result = (prime * result) + ((surname) == null ? 1 : surname.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } 
        if (obj instanceof Person) {
            Person p = ((Person) (obj));
            return p.toString().equals(toString());
        } 
        return false;
    }
}

