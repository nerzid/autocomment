/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.services.task;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.kie.api.task.model.Comment;
import java.util.Date;
import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import org.kie.internal.task.api.model.InternalComment;
import org.kie.internal.task.api.model.InternalOrganizationalEntity;
import org.kie.internal.task.api.InternalTaskService;
import java.util.List;
import javax.persistence.Persistence;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import java.io.StringReader;
import org.kie.api.task.model.Task;
import org.jbpm.services.task.impl.factories.TaskFactory;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.api.task.model.TaskSummary;
import org.junit.Test;
import org.kie.api.task.model.User;

public class TaskCommentTest extends HumanTaskServicesBaseTest {
    private PoolingDataSource pds;

    private EntityManagerFactory emf;

    @Before
    public void setup() {
        pds = setupPoolingDataSource();
        emf = Persistence.createEntityManagerFactory("org.jbpm.services.task");
        TaskCommentTest.this.taskService = ((InternalTaskService) (HumanTaskServiceFactory.newTaskServiceConfigurator().entityManagerFactory(emf).getTaskService()));
    }

    @After
    public void clean() {
        super.tearDown();
        if ((emf) != null) {
            emf.close();
        } 
        if ((pds) != null) {
            pds.close();
        } 
    }

    @Test
    public void testTaskComment() throws Exception {
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { businessAdministrators = [new User('Bobba Fet')], }),";
        str += "name =  'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, java.lang.Object>());
        List<TaskSummary> tasks = taskService.getTasksAssignedAsBusinessAdministrator("Bobba Fet", "en-UK");
        String txt = "brainwashArmitageRecruitCaseGetPasswordFromLady3JaneAscentToStraylightIcebreakerUniteWithNeuromancer";
        Assert.assertEquals(1, tasks.size());
        TaskSummary taskSum = tasks.get(0);
        Comment comment = TaskModelProvider.getFactory().newComment();
        Date date = new Date();
        ((InternalComment) (comment)).setAddedAt(date);
        User user = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) (user)).setId("Troll");
        ((InternalComment) (comment)).setAddedBy(user);
        ((InternalComment) (comment)).setText(txt);
        Long commentId = taskService.addComment(taskSum.getId().longValue(), comment);
        Assert.assertNotNull(commentId);
        Assert.assertTrue(((commentId.longValue()) > 0L));
        Comment commentById = taskService.getCommentById(commentId.longValue());
        Assert.assertNotNull(commentById);
        Assert.assertEquals(commentId, commentById.getId());
        Assert.assertEquals(date, commentById.getAddedAt());
        Assert.assertEquals(user, commentById.getAddedBy());
        Assert.assertEquals(txt, commentById.getText());
        Comment comment2 = TaskModelProvider.getFactory().newComment();
        ((InternalComment) (comment2)).setAddedAt(new Date());
        User user2 = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) (user2)).setId("Master");
        ((InternalComment) (comment2)).setAddedBy(user2);
        ((InternalComment) (comment2)).setText((txt + "asdf"));
        Long commentId2 = taskService.addComment(taskSum.getId(), comment2);
        Assert.assertNotNull(commentId2);
        Assert.assertTrue(((commentId2.longValue()) > 0L));
        Assert.assertNotEquals(commentId, commentId2);
        Comment commentById2 = taskService.getCommentById(commentId2.longValue());
        Assert.assertNotNull(commentById2);
        Assert.assertNotEquals(commentById, commentById2);
        List<Comment> allCommentList = taskService.getAllCommentsByTaskId(taskSum.getId());
        Assert.assertEquals(2, allCommentList.size());
        // check id
        Assert.assertEquals(commentId, allCommentList.get(0).getId());
        Assert.assertEquals(commentId2, allCommentList.get(1).getId());
        taskService.deleteComment(taskSum.getId(), commentId2);
        Assert.assertFalse(taskService.getAllCommentsByTaskId(taskSum.getId()).isEmpty());
        // one item
        allCommentList = taskService.getAllCommentsByTaskId(taskSum.getId());
        Assert.assertEquals(1, allCommentList.size());
        taskService.deleteComment(taskSum.getId(), commentId);
        Assert.assertTrue(taskService.getAllCommentsByTaskId(taskSum.getId()).isEmpty());
    }

    @Test
    public void testTaskCommentsOrder() {
        int commentsCount = 50;
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { businessAdministrators = [new User('Bobba Fet')], }),";
        str += "name =  'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, java.lang.Object>());
        List<TaskSummary> tasks = taskService.getTasksAssignedAsBusinessAdministrator("Bobba Fet", "en-UK");
        TaskSummary taskSum = tasks.get(0);
        String[] messages = new String[commentsCount];
        Long[] commentId = new Long[commentsCount];
        for (int i = 0; i < commentsCount; i++) {
            Comment comment = TaskModelProvider.getFactory().newComment();
            messages[i] = ("Comment " + i) + ".";
            ((InternalComment) (comment)).setAddedAt(new Date());
            User user = TaskModelProvider.getFactory().newUser();
            ((InternalOrganizationalEntity) (user)).setId("Troll");
            ((InternalComment) (comment)).setAddedBy(user);
            ((InternalComment) (comment)).setText(messages[i]);
            commentId[i] = taskService.addComment(taskSum.getId(), comment);
            Assert.assertNotNull(commentId[i]);
        }
        List<Comment> allCommentList = taskService.getAllCommentsByTaskId(taskSum.getId());
        Assert.assertEquals(commentsCount, allCommentList.size());
        for (int i = 0; i < commentsCount; i++) {
            Comment comment = allCommentList.get(i);
            Assert.assertNotNull(comment);
            Assert.assertEquals(commentId[i], comment.getId());
            Assert.assertNotNull(comment.getAddedAt());
            Assert.assertEquals(messages[i], comment.getText());
            Assert.assertEquals("Troll", comment.getAddedBy().getId());
        }
    }
}

