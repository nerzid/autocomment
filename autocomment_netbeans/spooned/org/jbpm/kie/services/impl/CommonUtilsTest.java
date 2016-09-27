

package org.jbpm.kie.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Assert;
import org.reflections.util.ClasspathHelper;
import java.util.Collections;
import org.kie.api.command.Command;
import java.util.Comparator;
import java.lang.reflect.Field;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.kie.internal.command.ProcessInstanceIdCommand;
import org.reflections.Reflections;
import org.junit.Test;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class CommonUtilsTest {
    private static final Logger logger = LoggerFactory.getLogger(CommonUtilsTest.class);

    private static final Reflections reflections = new Reflections(ClasspathHelper.forPackage("org.drools"), ClasspathHelper.forPackage("org.jbpm"), new org.reflections.scanners.TypeAnnotationsScanner(), new org.reflections.scanners.FieldAnnotationsScanner(), new org.reflections.scanners.SubTypesScanner());

    @Test
    public void testProcessInstanceIdCommands() {
        List<Class<? extends Command>> cmdClasses = new ArrayList<Class<? extends Command>>(CommonUtilsTest.reflections.getSubTypesOf(Command.class));
        Assert.assertFalse("Empty set of command classes to test?!?", cmdClasses.isEmpty());
        // sort alphabetically in order to easily find problems and to make test reproducible
        Collections.sort(cmdClasses, new Comparator<Class>() {
            @Override
            public int compare(Class o1, Class o2) {
                if (o1 == null) {
                    return -1;
                } else if (o2 == null) {
                    return 1;
                } else {
                    return o1.getName().compareTo(o2.getName());
                }
            }
        });
        for (Class<? extends Command> cmdClass : cmdClasses) {
            System.out.println(cmdClass.getName());
            Field procInstIdField = CommonUtilsTest.findProcessInstanceIdField(cmdClass);
            if (procInstIdField != null) {
                List<Class<?>> cmdClassInterfaces = Arrays.asList(cmdClass.getInterfaces());
                Assert.assertTrue(((((cmdClass.getName()) + " does not implement the ") + (ProcessInstanceIdCommand.class.getSimpleName())) + " interface!"), cmdClassInterfaces.contains(ProcessInstanceIdCommand.class));
            } 
        }
    }

    private static Field findProcessInstanceIdField(Class<? extends Command> cmdClass) {
        // This code
        try {
            Field[] fields = cmdClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(XmlAttribute.class)) {
                    String attributeName = field.getAnnotation(XmlAttribute.class).name();
                    if ("process-instance-id".equalsIgnoreCase(attributeName)) {
                        return field;
                    } else if ("processInstanceId".equals(field.getName())) {
                        return field;
                    } 
                } else if (field.isAnnotationPresent(XmlElement.class)) {
                    String elementName = field.getAnnotation(XmlElement.class).name();
                    if ("process-instance-id".equalsIgnoreCase(elementName)) {
                        return field;
                    } else if ("processInstanceId".equals(field.getName())) {
                        return field;
                    } 
                } else if ("processInstanceId".equals(field.getName())) {
                    return field;
                } 
            }
        } catch (Exception e) {
            CommonUtilsTest.logger.debug("Unable to find process instance id field in {} due to {}", cmdClass.getName(), e.getMessage());
        }
        return null;
    }
}

