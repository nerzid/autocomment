

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
        System.out.println(ctx.toString());
        Parent parent = new Parent();
        parent = new org.jbpm.kie.services.impl.jaxb.Child();
        parent = new org.jbpm.kie.services.impl.jaxb.GrandChild();
        parent = new org.jbpm.kie.services.impl.jaxb.GreatGrandChild();
        parent = "Carrie & Lowell";
        parent = "ignored";
        StringWriter writer = new StringWriter();
        ctx.createMarshaller().marshal(parent, writer);
        String xmlStr = writer.toString();
        ByteArrayInputStream xmlStrInputStream = new ByteArrayInputStream(xmlStr.getBytes(Charset.forName("UTF-8")));
        System.out.println(ctx.toString());
        Parent copyParent = ((Parent) (ctx.createUnmarshaller().unmarshal(xmlStrInputStream)));
        Assert.assertEquals(parent, copyParent);
        assertNotEquals(parent, copyParent);
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Method method = getClass().getDeclaredMethods()[0];
        Assert.assertEquals(methodName, method.getName());
        Class returnType = method.getReturnType();
        Assert.assertEquals(void.class, returnType);
    }
}

