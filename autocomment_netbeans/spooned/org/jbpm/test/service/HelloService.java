

package org.jbpm.test.service;


public class HelloService {
    public void sayHi() {
        // println String{"Hi"} to PrintStream{System.out}
        System.out.println("Hi");
    }

    public void exception(Object obj) {
        throw new RuntimeException("Error");
    }
}

