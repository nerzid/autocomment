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

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Assume;
import org.kie.api.task.model.Attachment;
import org.jbpm.services.task.commands.CancelDeadlineCommand;
import org.kie.api.task.model.Comment;
import org.jbpm.services.task.impl.model.CommentImpl;
import org.kie.test.util.compare.ComparePair;
import org.jbpm.services.task.commands.CompositeCommand;
import org.jbpm.services.task.impl.model.ContentImpl;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import java.util.Date;
import org.jbpm.services.task.commands.GetTaskAssignedAsPotentialOwnerCommand;
import java.util.HashMap;
import java.util.HashSet;
import org.kie.api.task.model.I18NText;
import org.jbpm.services.task.impl.model.I18NTextImpl;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.kie.internal.task.api.model.InternalAttachment;
import org.kie.internal.task.api.model.InternalComment;
import org.kie.internal.task.api.model.InternalI18NText;
import org.kie.internal.task.api.model.InternalOrganizationalEntity;
import org.kie.internal.task.api.model.InternalTask;
import org.kie.internal.task.api.model.InternalTaskData;
import java.lang.reflect.InvocationTargetException;
import org.jbpm.services.task.impl.model.xml.JaxbComment;
import org.jbpm.services.task.impl.model.xml.JaxbContent;
import org.jbpm.services.task.impl.model.xml.JaxbI18NText;
import org.jbpm.services.task.impl.model.xml.JaxbTask;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.lang.reflect.Method;
import org.jbpm.services.task.MvelFilePath;
import org.jbpm.services.task.commands.ProcessSubTaskCommand;
import org.kie.internal.query.QueryFilter;
import java.util.Random;
import java.io.Reader;
import java.util.Set;
import org.jbpm.services.task.commands.SkipTaskCommand;
import org.jbpm.services.task.commands.StartTaskCommand;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskData;
import org.jbpm.services.task.impl.factories.TaskFactory;
import org.kie.internal.task.api.TaskModelProvider;
import org.junit.Test;
import org.kie.api.task.model.User;

public abstract class AbstractTaskSerializationTest {
    protected final Logger logger;

    public AbstractTaskSerializationTest() {
        logger = LoggerFactory.getLogger(AbstractTaskSerializationTest.this.getClass());
    }

    public abstract <T> T testRoundTrip(T input) throws Exception;

    public abstract AbstractTaskSerializationTest.TestType getType();

    public abstract void addClassesToSerializationContext(Class<?>... extraClass);

    public enum TestType {
JAXB, JSON, YAML;    }

    protected static Random random = new Random();

    // TESTS ----------------------------------------------------------------------------------------------------------------------
    @Test
    public void jaxbTaskTest() throws Exception {
        // Yaml serialization requires major changes in order to be supported.. :/
        Assume.assumeTrue((!(getType().equals(AbstractTaskSerializationTest.TestType.YAML))));
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());
        InputStream stream = getClass().getResourceAsStream(MvelFilePath.FullTask);
        Assert.assertNotNull(("Could not load file: " + (MvelFilePath.FullTask)), stream);
        Reader reader = new InputStreamReader(stream);
        InternalTask task = ((InternalTask) (TaskFactory.evalTask(reader, vars)));
        // fill task
        task.setFormName("Bruno's Form");
        task.setId(23);
        task.setSubTaskStrategy(SubTasksStrategy.EndParentOnAllSubTasksEnd);
        for (I18NText text : task.getNames()) {
            ((InternalI18NText) (text)).setId(((long) (AbstractTaskSerializationTest.random.nextInt(1000))));
        }
        for (I18NText text : task.getSubjects()) {
            ((InternalI18NText) (text)).setId(((long) (AbstractTaskSerializationTest.random.nextInt(1000))));
        }
        for (I18NText text : task.getDescriptions()) {
            ((InternalI18NText) (text)).setId(((long) (AbstractTaskSerializationTest.random.nextInt(1000))));
        }
        // fill task -> task data
        InternalTaskData taskData = ((InternalTaskData) (task.getTaskData()));
        taskData.setOutputAccessType(AccessType.Inline);
        taskData.setFaultAccessType(AccessType.Unknown);
        taskData.setTaskInputVariables(new HashMap<String, Object>());
        taskData.setTaskOutputVariables(new HashMap<String, Object>());
        // fill task -> task data -> comment
        String payload = "brainwashArmitageRecruitCaseGetPasswordFromLady3JaneAscentToStraylightIcebreakerUniteWithNeuromancer";
        InternalComment comment = ((InternalComment) (TaskModelProvider.getFactory().newComment()));
        comment.setId(42);
        comment.setText(payload);
        comment.setAddedAt(new Date());
        User user = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) (user)).setId("Case");
        comment.setAddedBy(user);
        taskData.addComment(comment);
        // fill task -> task data -> attachment
        InternalAttachment attach = ((InternalAttachment) (TaskModelProvider.getFactory().newAttachment()));
        attach.setId(10);
        attach.setName("virus");
        attach.setContentType("ROM");
        attach.setAttachedAt(new Date());
        user = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) (user)).setId("Wintermute");
        attach.setAttachedBy(user);
        attach.setSize(payload.getBytes().length);
        attach.setAttachmentContentId(comment.getId());
        taskData.addAttachment(attach);
        JaxbTask xmlTask = new JaxbTask(task);
        verifyThatAllFieldsAreFilledOrUnsupported(xmlTask, InternalTask.class);
        verifyThatAllFieldsAreFilledOrUnsupported(xmlTask.getTaskData(), TaskData.class);
        verifyThatAllFieldsAreFilledOrUnsupported(xmlTask.getTaskData().getAttachments().get(0), Attachment.class);
        verifyThatAllFieldsAreFilledOrUnsupported(xmlTask.getTaskData().getComments().get(0), Comment.class);
        Assert.assertNotNull(xmlTask.getNames());
        Assert.assertTrue(((xmlTask.getNames().size()) > 0));
        JaxbTask bornAgainTask = testRoundTrip(xmlTask);
        Assert.assertNotNull(bornAgainTask.getNames());
        Assert.assertTrue("Round-tripped task has empty 'names' list!", (!(bornAgainTask.getNames().isEmpty())));
        ComparePair compare = new ComparePair(task, bornAgainTask, Task.class);
        compare.recursiveCompare();
        Assert.assertNotNull(((InternalTask) (xmlTask)).getFormName());
        Assert.assertEquals(((InternalTask) (xmlTask)).getFormName(), ((InternalTask) (bornAgainTask)).getFormName());
        Task realTask = xmlTask.getTask();
        verifyThatXmlFieldsAreFilled(realTask, xmlTask, InternalTask.class, "deadlines");
        verifyThatXmlFieldsAreFilled(realTask.getTaskData(), xmlTask.getTaskData(), TaskData.class, "taskInputVariables", "taskOutputVariables");
    }

    private void verifyThatXmlFieldsAreFilled(Object realInst, Object xmlInst, Class interfaze, String... ignoreFields) {
        Set<String> ignoreFieldSet = new HashSet<String>(Arrays.asList(ignoreFields));
        Assert.assertNotNull(((interfaze.getSimpleName()) + " (XML) instance is null"), xmlInst);
        Assert.assertNotNull(((interfaze.getSimpleName()) + " (XML) instance is null"), realInst);
        String methodName = null;
        try {
            for (Method getMethod : interfaze.getMethods()) {
                methodName = getMethod.getName();
                if (!(methodName.startsWith("get"))) {
                    continue;
                } 
                try {
                    String fieldName = (methodName.substring(3, 4).toLowerCase()) + (methodName.substring(4));
                    if (ignoreFieldSet.contains(fieldName)) {
                        continue;
                    } 
                    // xml Inst
                    Object xmlFieldValue = getMethod.invoke(xmlInst);
                    // real Inst
                    Object fieldValue = getMethod.invoke(realInst);
                    if ((Enum.class.isAssignableFrom(xmlFieldValue.getClass())) || (xmlFieldValue.getClass().isEnum())) {
                        Assert.assertEquals(((((interfaze.getSimpleName()) + ".") + fieldName) + " value has not been copied"), xmlFieldValue, fieldValue);
                    } else if ((xmlFieldValue.getClass().getPackage().getName().contains("org.kie")) || (xmlFieldValue.getClass().getPackage().getName().contains("org.jbpm"))) {
                        Assert.assertNotNull(((((interfaze.getSimpleName()) + ".") + fieldName) + " value is empty"), fieldValue);
                    } else if (xmlFieldValue.getClass().isArray()) {
                        List xmlList = Arrays.asList(xmlFieldValue);
                        List realList = Arrays.asList(fieldValue);
                        Assert.assertEquals(((((interfaze.getSimpleName()) + ".") + fieldName) + " value has unequal list size"), xmlList.size(), realList.size());
                        for (int i = 0; i < (xmlList.size()); ++i) {
                            Object xmlElem = xmlList.get(i);
                            Object realElem = realList.get(i);
                            verifyThatXmlFieldsAreFilled(realElem, xmlElem, xmlElem.getClass().getInterfaces()[0]);
                        }
                    } else if (List.class.isAssignableFrom(xmlFieldValue.getClass())) {
                        List xmlList = ((List) (xmlFieldValue));
                        List realList = ((List) (fieldValue));
                        Assert.assertEquals(((((interfaze.getSimpleName()) + ".") + fieldName) + " value has unequal list size"), xmlList.size(), realList.size());
                        for (int i = 0; i < (xmlList.size()); ++i) {
                            Object xmlElem = xmlList.get(i);
                            Object realElem = realList.get(i);
                            verifyThatXmlFieldsAreFilled(realElem, xmlElem, xmlElem.getClass().getInterfaces()[0]);
                        }
                    } else {
                        Assert.assertEquals(((((interfaze.getSimpleName()) + ".") + fieldName) + " value has not been copied"), xmlFieldValue, fieldValue);
                    }
                } catch (InvocationTargetException ite) {
                    Throwable cause = ite.getCause();
                    if ((cause instanceof UnsupportedOperationException) && (cause.getMessage().contains("not supported on the JAXB"))) {
                        continue;
                    } 
                    throw ite;
                }
            }
        } catch (Exception e) {
            logger.error(("Test failed: " + (e.getMessage())), e);
            Assert.fail(((("Unable to verify field via method " + methodName) + ": ") + (e.getMessage())));
        }
    }

    private void verifyThatAllFieldsAreFilledOrUnsupported(Object instance, Class interfaze) {
        Assert.assertNotNull(((interfaze.getSimpleName()) + " instance is null"), instance);
        String methodName = null;
        try {
            for (Method getMethod : interfaze.getMethods()) {
                methodName = getMethod.getName();
                if (!(methodName.startsWith("get"))) {
                    continue;
                } 
                try {
                    Object fieldValue = getMethod.invoke(instance);
                    String fieldName = (methodName.substring(3, 4).toLowerCase()) + (methodName.substring(4));
                    Assert.assertNotNull(((((interfaze.getSimpleName()) + ".") + fieldName) + " is null"), fieldValue);
                    if (fieldValue instanceof Number) {
                        Assert.assertFalse(((((interfaze.getSimpleName()) + ".") + fieldName) + " is -1"), ((((Number) (fieldValue)).intValue()) < 0));
                    } 
                } catch (InvocationTargetException ite) {
                    Throwable cause = ite.getCause();
                    if ((cause instanceof UnsupportedOperationException) && (cause.getMessage().contains("not supported on the JAXB"))) {
                        continue;
                    } 
                    throw ite;
                }
            }
        } catch (Exception e) {
            logger.error(("Test failed: " + (e.getMessage())), e);
            Assert.fail(((("Unable to verify field via method " + methodName) + ": ") + (e.getMessage())));
        }
    }

    @Test
    public void taskCompositeCommandCanBeSerialized() throws Exception {
        Assume.assumeTrue(AbstractTaskSerializationTest.TestType.JAXB.equals(getType()));
        addClassesToSerializationContext(CompositeCommand.class);
        addClassesToSerializationContext(StartTaskCommand.class);
        addClassesToSerializationContext(CancelDeadlineCommand.class);
        CompositeCommand<Void> cmd = new CompositeCommand<Void>(new StartTaskCommand(1, "john"), new CancelDeadlineCommand(1, true, false));
        CompositeCommand<?> returned = testRoundTrip(cmd);
        Assert.assertNotNull(returned);
        Assert.assertNotNull(returned.getMainCommand());
        Assert.assertTrue(((returned.getMainCommand()) instanceof StartTaskCommand));
        Assert.assertEquals(Long.valueOf(1), returned.getTaskId());
        Assert.assertNotNull(returned.getCommands());
        Assert.assertEquals(1, returned.getCommands().size());
    }

    @Test
    public void taskCompositeCommandMultipleCanBeSerialized() throws Exception {
        Assume.assumeTrue(AbstractTaskSerializationTest.TestType.JAXB.equals(getType()));
        addClassesToSerializationContext(CompositeCommand.class);
        addClassesToSerializationContext(SkipTaskCommand.class);
        addClassesToSerializationContext(ProcessSubTaskCommand.class);
        addClassesToSerializationContext(CancelDeadlineCommand.class);
        CompositeCommand<Void> cmd = new CompositeCommand<Void>(new SkipTaskCommand(1, "john"), new ProcessSubTaskCommand(1, "john"), new CancelDeadlineCommand(1, true, true));
        CompositeCommand<?> returned = testRoundTrip(cmd);
        Assert.assertNotNull(returned);
        Assert.assertNotNull(returned.getMainCommand());
        Assert.assertTrue(((returned.getMainCommand()) instanceof SkipTaskCommand));
        Assert.assertEquals(Long.valueOf(1), returned.getTaskId());
        Assert.assertNotNull(returned.getCommands());
        Assert.assertEquals(2, returned.getCommands().size());
    }

    @Test
    public void statusInCommandSerialization() throws Exception {
        Assume.assumeTrue(getType().equals(AbstractTaskSerializationTest.TestType.JAXB));
        addClassesToSerializationContext(GetTaskAssignedAsPotentialOwnerCommand.class);
        List<Status> statuses = new ArrayList<Status>();
        statuses.add(Status.Completed);
        statuses.add(Status.Exited);
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("team");
        groupIds.add("region");
        QueryFilter filter = new QueryFilter(0, 0, "order", false);
        GetTaskAssignedAsPotentialOwnerCommand cmd = new GetTaskAssignedAsPotentialOwnerCommand("user", groupIds, statuses, filter);
        GetTaskAssignedAsPotentialOwnerCommand copyCmd = testRoundTrip(cmd);
        ComparePair.compareObjectsViaFields(cmd, copyCmd);
    }

    @Test
    public void jaxbContentTest() throws Exception {
        Assume.assumeFalse(getType().equals(AbstractTaskSerializationTest.TestType.YAML));
        ContentImpl content = new ContentImpl();
        content.setId(23);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("life", new Integer(23));
        map.put("sick", new Integer(45));
        byte[] bytes = ContentMarshallerHelper.marshallContent(null, map, null);
        content.setContent(bytes);
        JaxbContent jaxbContent = new JaxbContent(content);
        JaxbContent copyJaxbContent = testRoundTrip(jaxbContent);
        ComparePair.compareObjectsViaFields(jaxbContent, copyJaxbContent);
    }

    @Test
    public void jaxbCommentTest() throws Exception {
        Assume.assumeFalse(getType().equals(AbstractTaskSerializationTest.TestType.YAML));
        CommentImpl comment = new CommentImpl();
        comment.setAddedAt(new Date());
        comment.setAddedBy(new org.jbpm.services.task.impl.model.UserImpl("user"));
        comment.setId(23L);
        comment.setText("ILLUMINATI!");
        JaxbComment jaxbComment = new JaxbComment(comment);
        Assert.assertEquals("added at", comment.getAddedAt(), jaxbComment.getAddedAt());
        Assert.assertEquals("added by", comment.getAddedBy().getId(), jaxbComment.getAddedById());
        Assert.assertEquals("added by", comment.getAddedBy().getId(), jaxbComment.getAddedBy().getId());
        Assert.assertEquals("id", comment.getId(), jaxbComment.getId());
        Assert.assertEquals("text", comment.getText(), jaxbComment.getText());
        JaxbComment copyJaxbComment = testRoundTrip(jaxbComment);
        ComparePair.compareObjectsViaFields(jaxbComment, copyJaxbComment);
    }

    @Test
    public void jaxbI18NTextTest() throws Exception {
        Assume.assumeFalse(getType().equals(AbstractTaskSerializationTest.TestType.YAML));
        I18NTextImpl textImpl = new I18NTextImpl();
        textImpl.setId(1605L);
        textImpl.setLanguage("es-ES");
        textImpl.setText("Quixote");
        JaxbI18NText jaxbText = new JaxbI18NText(textImpl);
        Assert.assertEquals("id", textImpl.getId(), jaxbText.getId());
        Assert.assertEquals("language", textImpl.getLanguage(), jaxbText.getLanguage());
        Assert.assertEquals("text", textImpl.getText(), jaxbText.getText());
        JaxbI18NText copyJaxbText = testRoundTrip(jaxbText);
        ComparePair.compareObjectsViaFields(jaxbText, copyJaxbText);
        List<I18NText> intList = new ArrayList<I18NText>();
        intList.add(textImpl);
        List<JaxbI18NText> jaxbList = JaxbI18NText.convertListFromInterfaceToJaxbImpl(intList, I18NText.class, JaxbI18NText.class);
        jaxbText = jaxbList.get(0);
        Assert.assertEquals("id", textImpl.getId(), jaxbText.getId());
        Assert.assertEquals("language", textImpl.getLanguage(), jaxbText.getLanguage());
        Assert.assertEquals("text", textImpl.getText(), jaxbText.getText());
        intList = JaxbI18NText.convertListFromJaxbImplToInterface(jaxbList);
        I18NText text = intList.get(0);
        Assert.assertEquals("id", text.getId(), jaxbText.getId());
        Assert.assertEquals("language", text.getLanguage(), jaxbText.getLanguage());
        Assert.assertEquals("text", text.getText(), jaxbText.getText());
    }
}

