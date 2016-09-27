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


package org.jbpm.services.task.jaxb;

import org.jbpm.services.task.commands.AddContentFromUserCommand;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Assert;
import java.io.ByteArrayInputStream;
import org.reflections.util.ClasspathHelper;
import java.util.Collection;
import org.jbpm.services.task.commands.CompositeCommand;
import java.lang.reflect.Constructor;
import java.util.Date;
import org.jbpm.services.task.impl.model.FaultDataImpl;
import java.lang.reflect.Field;
import org.jbpm.services.task.admin.listener.internal.GetCurrentTxTasksCommand;
import java.util.HashMap;
import java.util.HashSet;
import org.kie.api.task.model.I18NText;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import org.jbpm.services.task.impl.model.xml.JaxbContent;
import org.jbpm.services.task.impl.model.xml.JaxbTask;
import java.util.List;
import java.util.Map;
import javax.xml.bind.Marshaller;
import org.mockito.Matchers;
import org.mockito.Mockito;
import java.lang.reflect.ParameterizedType;
import org.reflections.Reflections;
import java.util.Set;
import org.jbpm.services.task.commands.SetTaskPropertyCommand;
import javax.xml.transform.stream.StreamSource;
import java.io.StringWriter;
import org.kie.internal.task.api.model.SubTasksStrategy;
import org.jbpm.services.task.commands.TaskCommand;
import org.jbpm.services.task.commands.TaskContext;
import org.kie.internal.task.api.TaskInstanceService;
import org.junit.Test;
import org.jbpm.services.task.jaxb.AbstractTaskSerializationTest.TestType;
import java.lang.reflect.Type;
import javax.xml.bind.Unmarshaller;
import org.kie.internal.task.api.UserGroupCallback;
import org.jbpm.services.task.commands.UserGroupCallbackTaskCommand;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

public class JaxbTaskSerializationTest extends AbstractTaskSerializationTest {
    private Class<?>[] jaxbClasses = new Class<?>[]{ JaxbTask.class , JaxbContent.class };

    public TestType getType() {
        return TestType.JAXB;
    }

    protected static Reflections reflections = new Reflections(ClasspathHelper.forPackage("org.jbpm.services.task"), ClasspathHelper.forPackage("org.jbpm.services.task.commands"), new org.reflections.scanners.TypeAnnotationsScanner(), new org.reflections.scanners.FieldAnnotationsScanner(), new org.reflections.scanners.MethodAnnotationsScanner(), new org.reflections.scanners.SubTypesScanner());

    @Override
    public <T> T testRoundTrip(T input) throws Exception {
        String xmlStr = convertJaxbObjectToString(input);
        logger.debug(xmlStr);
        if (input instanceof JAXBElement) {
            return ((T) (convertStringToJaxbElement(xmlStr, ((JAXBElement) (input)).getValue().getClass())));
        } 
        return ((T) (convertStringToJaxbObject(xmlStr)));
    }

    @Test
    public void taskCmdUniqueRootElementTest() throws Exception {
        Set<String> uniqueRootElemSet = new HashSet<String>();
        for (Class<?> jaxbClass : JaxbTaskSerializationTest.reflections.getTypesAnnotatedWith(XmlRootElement.class)) {
            XmlRootElement xmlRootElemAnno = jaxbClass.getAnnotation(XmlRootElement.class);
            Assert.assertTrue(((xmlRootElemAnno.name()) + " is not a unique @XmlRootElement value!"), uniqueRootElemSet.add(xmlRootElemAnno.name()));
        }
    }

    @Test
    public void taskCommandSubTypesCanBeSerialized() throws Exception {
        for (Class<?> jaxbClass : JaxbTaskSerializationTest.reflections.getSubTypesOf(TaskCommand.class)) {
            if ((jaxbClass.equals(UserGroupCallbackTaskCommand.class)) || (jaxbClass.equals(GetCurrentTxTasksCommand.class))) {
                continue;
            } 
            addClassesToSerializationContext(jaxbClass);
            Constructor<?> construct = jaxbClass.getConstructor(new Class[]{  });
            try {
                Object jaxbInst = construct.newInstance(new Object[]{  });
                testRoundTrip(jaxbInst);
            } catch (Exception e) {
                logger.warn(("Testing failed for" + (jaxbClass.getName())));
                throw e;
            }
        }
    }

    @Test
    public void compositeCommandXmlElementsAnnoTest() throws Exception {
        Field[] comCmdFields = CompositeCommand.class.getDeclaredFields();
        for (Field field : comCmdFields) {
            XmlElements xmlElemsAnno = field.getAnnotation(XmlElements.class);
            if (xmlElemsAnno != null) {
                Set<Class<? extends TaskCommand>> taskCmdSubTypes = JaxbTaskSerializationTest.reflections.getSubTypesOf(TaskCommand.class);
                Set<Class<? extends UserGroupCallbackTaskCommand>> userGrpTaskCmdSubTypes = JaxbTaskSerializationTest.reflections.getSubTypesOf(UserGroupCallbackTaskCommand.class);
                taskCmdSubTypes.addAll(userGrpTaskCmdSubTypes);
                Class[] exclTaskCmds = new Class[]{ UserGroupCallbackTaskCommand.class , CompositeCommand.class , GetCurrentTxTasksCommand.class };
                taskCmdSubTypes.removeAll(Arrays.asList(exclTaskCmds));
                for (XmlElement xmlElemAnno : xmlElemsAnno.value()) {
                    Class xmlElemAnnoType = xmlElemAnno.type();
                    Assert.assertTrue(((((xmlElemAnnoType.getName()) + " does not extend the ") + (TaskCommand.class.getSimpleName())) + " class!"), taskCmdSubTypes.contains(xmlElemAnnoType));
                }
                for (XmlElement xmlElemAnno : xmlElemsAnno.value()) {
                    Class xmlElemAnnoType = xmlElemAnno.type();
                    taskCmdSubTypes.remove(xmlElemAnnoType);
                }
                if (!(taskCmdSubTypes.isEmpty())) {
                    System.out.println(("##### " + (taskCmdSubTypes.iterator().next().getCanonicalName())));
                    Assert.fail((((((("(" + (taskCmdSubTypes.iterator().next().getSimpleName())) + ") Not all ") + (TaskCommand.class.getSimpleName())) + " sub types have been added to the @XmlElements in the CompositeCommand.") + (field.getName())) + " field."));
                } 
            } else {
                Assert.assertFalse("TaskCommand fields need to be annotated with @XmlElements annotations!", TaskCommand.class.equals(field.getType()));
                if (field.getType().isArray()) {
                    Class arrElemType = field.getType().getComponentType();
                    if (arrElemType != null) {
                        Assert.assertFalse((("TaskCommand fields (CompositeCommand." + (field.getName())) + ") need to be annotated with @XmlElements annotations!"), TaskCommand.class.equals(arrElemType));
                    } 
                } else if (Collection.class.isAssignableFrom(field.getType())) {
                    ParameterizedType fieldGenericType = ((ParameterizedType) (field.getGenericType()));
                    Type listType = fieldGenericType.getActualTypeArguments()[0];
                    if (listType != null) {
                        Assert.assertFalse((("TaskCommand fields (CompositeCommand." + (field.getName())) + ") need to be annotated with @XmlElements annotations!"), TaskCommand.class.equals(listType));
                    } 
                } 
            }
        }
    }

    public String convertJaxbObjectToString(Object object) throws JAXBException {
        Marshaller marshaller = JAXBContext.newInstance(jaxbClasses).createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(object, stringWriter);
        String output = stringWriter.toString();
        return output;
    }

    public Object convertStringToJaxbObject(String xmlStr) throws JAXBException {
        Unmarshaller unmarshaller = JAXBContext.newInstance(jaxbClasses).createUnmarshaller();
        ByteArrayInputStream xmlStrInputStream = new ByteArrayInputStream(xmlStr.getBytes());
        Object jaxbObj = unmarshaller.unmarshal(xmlStrInputStream);
        return jaxbObj;
    }

    public Object convertStringToJaxbElement(String xmlStr, Class actualClass) throws JAXBException {
        Unmarshaller unmarshaller = JAXBContext.newInstance(jaxbClasses).createUnmarshaller();
        ByteArrayInputStream xmlStrInputStream = new ByteArrayInputStream(xmlStr.getBytes());
        Object jaxbObj = unmarshaller.unmarshal(new StreamSource(xmlStrInputStream), actualClass);
        return jaxbObj;
    }

    @Override
    public void addClassesToSerializationContext(Class<?>... extraClass) {
        List<Class<?>> newJaxbClasses = new ArrayList<Class<?>>();
        newJaxbClasses.addAll(Arrays.asList(jaxbClasses));
        newJaxbClasses.addAll(Arrays.asList(extraClass));
        jaxbClasses = newJaxbClasses.toArray(new Class[newJaxbClasses.size()]);
    }

    @Test
    public void uniqueRootElementTest() throws Exception {
        Set<String> idSet = new HashSet<String>();
        HashMap<String, Class> idClassMap = new HashMap<String, Class>();
        for (Class<?> jaxbClass : JaxbTaskSerializationTest.reflections.getTypesAnnotatedWith(XmlRootElement.class)) {
            XmlRootElement rootElemAnno = jaxbClass.getAnnotation(XmlRootElement.class);
            String id = rootElemAnno.name();
            if ("##default".equals(id)) {
                continue;
            } 
            String otherClass = (idClassMap.get(id)) == null ? "null" : idClassMap.get(id).getName();
            Assert.assertTrue(((((("ID '" + id) + "' used in both ") + (jaxbClass.getName())) + " and ") + otherClass), idSet.add(id));
            idClassMap.put(id, jaxbClass);
            String className = jaxbClass.getSimpleName();
            if (!(className.endsWith("Command"))) {
                continue;
            } 
            String idName = id.replace("-", "");
            Assert.assertEquals((("XML root element name should match class name in " + (jaxbClass.getName())) + "!"), className.toLowerCase(), idName.toLowerCase());
        }
    }

    @Test
    public void setTaskPropertyCommandTest() throws Exception {
        SetTaskPropertyCommand cmd;
        int taskId = 1;
        String userId = "user";
        FaultDataImpl faultData = new FaultDataImpl();
        faultData.setAccessType(AccessType.Inline);
        faultData.setContent("skinned shins".getBytes());
        faultData.setFaultName("Whoops!");
        faultData.setType("skates");
        List<I18NText> textList = new ArrayList<I18NText>();
        I18NText text = new org.jbpm.services.task.impl.model.I18NTextImpl("nl-NL", "Stroopwafel!");
        textList.add(text);
        Object[][] testData = new Object[][]{ new Object[]{ SetTaskPropertyCommand.FAULT_PROPERTY , faultData } , new Object[]{ SetTaskPropertyCommand.OUTPUT_PROPERTY , new Object() } , new Object[]{ SetTaskPropertyCommand.PRIORITY_PROPERTY , 23 } , new Object[]{ SetTaskPropertyCommand.TASK_NAMES_PROPERTY , textList } , new Object[]{ SetTaskPropertyCommand.EXPIRATION_DATE_PROPERTY , new Date() } , new Object[]{ SetTaskPropertyCommand.DESCRIPTION_PROPERTY , new ArrayList<I18NText>() } , new Object[]{ SetTaskPropertyCommand.SKIPPABLE_PROPERTY , false } , new Object[]{ SetTaskPropertyCommand.SUB_TASK_STRATEGY_PROPERTY , SubTasksStrategy.EndParentOnAllSubTasksEnd } };
        TaskContext mockContext = Mockito.mock(TaskContext.class);
        TaskInstanceService mockTaskService = Mockito.mock(TaskInstanceService.class);
        UserGroupCallback mockUserGroupCallback = Mockito.mock(UserGroupCallback.class);
        Mockito.when(mockContext.getTaskInstanceService()).thenReturn(mockTaskService);
        Mockito.when(mockContext.getUserGroupCallback()).thenReturn(mockUserGroupCallback);
        Mockito.when(mockUserGroupCallback.existsUser(Matchers.anyString())).thenReturn(false);
        for (Object[] data : testData) {
            int property = ((Integer) (data[0]));
            cmd = new SetTaskPropertyCommand(taskId, userId, property, data[1]);
            cmd.execute(mockContext);
        }
    }

    @Test
    public void contentCommandTest() throws Exception {
        addClassesToSerializationContext(AddContentFromUserCommand.class);
        AddContentFromUserCommand cmd = new AddContentFromUserCommand(23L, "user");
        cmd.getOutputContentMap().put("one", "two");
        cmd.getOutputContentMap().put("thr", new Integer(4));
        AddContentFromUserCommand copyCmd = testRoundTrip(cmd);
        Assert.assertEquals("task id", cmd.getTaskId(), copyCmd.getTaskId());
        Assert.assertEquals("user id", cmd.getUserId(), copyCmd.getUserId());
        for (Map.Entry<String, Object> entry : cmd.getOutputContentMap().entrySet()) {
            String key = entry.getKey();
            Object copyVal = copyCmd.getOutputContentMap().get(key);
            Assert.assertEquals(("entry: " + key), entry.getValue(), copyVal);
        }
    }
}

