

package org.jbpm.process.workitem.parser;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Person {
    private String name;

    private int age;

    public Person(String name, int age) {
        super();
        Person.this.name = name;
        Person.this.age = age;
    }

    public Person() {
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
}

