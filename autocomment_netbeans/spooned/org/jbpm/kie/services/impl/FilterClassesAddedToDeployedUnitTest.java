

package org.jbpm.kie.services.impl;

import org.junit.Assert;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import javax.xml.bind.JAXBContext;
import java.lang.reflect.Method;
import org.jbpm.kie.services.impl.jaxb.Parent;
import java.io.StringWriter;
import org.junit.Test;

public class FilterClassesAddedToDeployedUnitTest {
    @Test
    public void jaxbContextAndTheClasspathTest() throws Exception {
        Class[] boundClasses = new Class[]{ Parent.class };
        JAXBContext ctx = JAXBContext.newInstance(boundClasses);
        // println String{ctx.toString()} to PrintStream{System.out}
        System.out.println(ctx.toString());
        Parent parent = new Parent();
        parent = new Child();
        parent = new GrandChild();
        parent = new GreatGrandChild();
        parent = "Carrie & Lowell";
        parent = "ignored";
        StringWriter writer = new StringWriter();
        // marshal Parent{parent} to Marshaller{ctx.createMarshaller()}
        ctx.createMarshaller().marshal(parent, writer);
        String xmlStr = writer.toString();
        ByteArrayInputStream xmlStrInputStream = new ByteArrayInputStream(xmlStr.getBytes(Charset.forName("UTF-8")));
        // println String{ctx.toString()} to PrintStream{System.out}
        System.out.println(ctx.toString());
        Parent copyParent = ((Parent) (ctx.createUnmarshaller().unmarshal(xmlStrInputStream)));
        // assert equals Parent{parent} to void{Assert}
        Assert.assertEquals(parent, copyParent);
        // assert not Parent{parent} to FilterClassesAddedToDeployedUnitTest{}
        assertNotEquals(parent, copyParent);
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Method method = getClass().getDeclaredMethods()[0];
        // assert equals String{methodName} to void{Assert}
        Assert.assertEquals(methodName, method.getName());
        Class returnType = method.getReturnType();
        // assert equals Class{void.class} to void{Assert}
        Assert.assertEquals(void.class, returnType);
    }
}

