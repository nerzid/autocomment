/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */


package org.jbpm.services.task.wih.util;

import org.jbpm.test.util.AbstractBaseTest;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Before;
import org.kie.api.task.model.Group;
import org.kie.internal.task.api.model.InternalPeopleAssignments;
import org.kie.internal.task.api.model.InternalTask;
import org.kie.internal.task.api.model.InternalTaskData;
import java.util.List;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.PeopleAssignments;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.TaskModelProvider;
import org.junit.Test;
import org.kie.api.task.model.User;
import org.drools.core.process.instance.WorkItem;

public class PeopleAssignmentHelperTest extends AbstractBaseTest {
    private PeopleAssignmentHelper peopleAssignmentHelper;

    @Before
    public void setup() {
        peopleAssignmentHelper = new PeopleAssignmentHelper();
    }

    @Test
    public void testProcessPeopleAssignments() {
        List<OrganizationalEntity> organizationalEntities = new ArrayList<OrganizationalEntity>();
        String ids = "espiegelberg,   drbug   ";
        Assert.assertTrue(organizationalEntities.isEmpty());
        peopleAssignmentHelper.processPeopleAssignments(ids, organizationalEntities, true);
        Assert.assertTrue(((organizationalEntities.size()) == 2));
        Assert.assertTrue(organizationalEntities.contains(createUser("drbug")));
        Assert.assertTrue(organizationalEntities.contains(createUser("espiegelberg")));
        ids = null;
        organizationalEntities = new ArrayList<OrganizationalEntity>();
        Assert.assertTrue(organizationalEntities.isEmpty());
        peopleAssignmentHelper.processPeopleAssignments(ids, organizationalEntities, true);
        Assert.assertTrue(organizationalEntities.isEmpty());
        ids = "     ";
        organizationalEntities = new ArrayList<OrganizationalEntity>();
        Assert.assertTrue(organizationalEntities.isEmpty());
        peopleAssignmentHelper.processPeopleAssignments(ids, organizationalEntities, true);
        Assert.assertTrue(organizationalEntities.isEmpty());
        ids = "Software Developer";
        organizationalEntities = new ArrayList<OrganizationalEntity>();
        Assert.assertTrue(organizationalEntities.isEmpty());
        peopleAssignmentHelper.processPeopleAssignments(ids, organizationalEntities, false);
        Assert.assertTrue(((organizationalEntities.size()) == 1));
        Assert.assertTrue(organizationalEntities.contains(createGroup("Software Developer")));
        // Test that a duplicate is not added; only 1 of the 2 passed in should be added
        ids = "Software Developer,Project Manager";
        peopleAssignmentHelper.processPeopleAssignments(ids, organizationalEntities, false);
        Assert.assertTrue(((organizationalEntities.size()) == 2));
        Assert.assertTrue(organizationalEntities.contains(createGroup("Software Developer")));
        Assert.assertTrue(organizationalEntities.contains(createGroup("Project Manager")));
    }

    @Test
    public void testAssignActors() {
        String actorId = "espiegelberg";
        Task task = TaskModelProvider.getFactory().newTask();
        InternalTaskData taskData = ((InternalTaskData) (TaskModelProvider.getFactory().newTaskData()));
        PeopleAssignments peopleAssignments = peopleAssignmentHelper.getNullSafePeopleAssignments(task);
        WorkItem workItem = new org.drools.core.process.instance.impl.WorkItemImpl();
        workItem.setParameter(PeopleAssignmentHelper.ACTOR_ID, actorId);
        peopleAssignmentHelper.assignActors(workItem, peopleAssignments, taskData);
        OrganizationalEntity organizationalEntity1 = peopleAssignments.getPotentialOwners().get(0);
        Assert.assertTrue((organizationalEntity1 instanceof User));
        Assert.assertEquals(actorId, organizationalEntity1.getId());
        Assert.assertEquals(actorId, taskData.getCreatedBy().getId());
        workItem = new org.drools.core.process.instance.impl.WorkItemImpl();
        peopleAssignments = peopleAssignmentHelper.getNullSafePeopleAssignments(task);
        workItem.setParameter(PeopleAssignmentHelper.ACTOR_ID, (actorId + ", drbug  "));
        peopleAssignmentHelper.assignActors(workItem, peopleAssignments, taskData);
        Assert.assertEquals(2, peopleAssignments.getPotentialOwners().size());
        organizationalEntity1 = peopleAssignments.getPotentialOwners().get(0);
        Assert.assertEquals(actorId, organizationalEntity1.getId());
        Assert.assertEquals(actorId, taskData.getCreatedBy().getId());
        OrganizationalEntity organizationalEntity2 = peopleAssignments.getPotentialOwners().get(1);
        Assert.assertEquals("drbug", organizationalEntity2.getId());
        workItem = new org.drools.core.process.instance.impl.WorkItemImpl();
        peopleAssignments = peopleAssignmentHelper.getNullSafePeopleAssignments(task);
        workItem.setParameter(PeopleAssignmentHelper.ACTOR_ID, "");
        peopleAssignmentHelper.assignActors(workItem, peopleAssignments, taskData);
        Assert.assertEquals(0, peopleAssignments.getPotentialOwners().size());
    }

    @Test
    public void testAssignActorsWithCustomSeparatorViaSysProp() {
        System.setProperty("org.jbpm.ht.user.separator", ";");
        peopleAssignmentHelper = new PeopleAssignmentHelper();
        String actorId = "user1;user2";
        Task task = TaskModelProvider.getFactory().newTask();
        InternalTaskData taskData = ((InternalTaskData) (TaskModelProvider.getFactory().newTaskData()));
        PeopleAssignments peopleAssignments = peopleAssignmentHelper.getNullSafePeopleAssignments(task);
        WorkItem workItem = new org.drools.core.process.instance.impl.WorkItemImpl();
        workItem.setParameter(PeopleAssignmentHelper.ACTOR_ID, actorId);
        peopleAssignmentHelper.assignActors(workItem, peopleAssignments, taskData);
        OrganizationalEntity organizationalEntity1 = peopleAssignments.getPotentialOwners().get(0);
        Assert.assertTrue((organizationalEntity1 instanceof User));
        Assert.assertEquals("user1", organizationalEntity1.getId());
        Assert.assertEquals("user1", taskData.getCreatedBy().getId());
        OrganizationalEntity organizationalEntity2 = peopleAssignments.getPotentialOwners().get(1);
        Assert.assertTrue((organizationalEntity2 instanceof User));
        Assert.assertEquals("user2", organizationalEntity2.getId());
        workItem = new org.drools.core.process.instance.impl.WorkItemImpl();
        peopleAssignments = peopleAssignmentHelper.getNullSafePeopleAssignments(task);
        workItem.setParameter(PeopleAssignmentHelper.ACTOR_ID, (actorId + "; drbug  "));
        peopleAssignmentHelper.assignActors(workItem, peopleAssignments, taskData);
        Assert.assertEquals(3, peopleAssignments.getPotentialOwners().size());
        organizationalEntity1 = peopleAssignments.getPotentialOwners().get(0);
        Assert.assertEquals("user1", organizationalEntity1.getId());
        Assert.assertEquals("user1", taskData.getCreatedBy().getId());
        organizationalEntity2 = peopleAssignments.getPotentialOwners().get(1);
        Assert.assertTrue((organizationalEntity2 instanceof User));
        Assert.assertEquals("user2", organizationalEntity2.getId());
        OrganizationalEntity organizationalEntity3 = peopleAssignments.getPotentialOwners().get(2);
        Assert.assertEquals("drbug", organizationalEntity3.getId());
        workItem = new org.drools.core.process.instance.impl.WorkItemImpl();
        peopleAssignments = peopleAssignmentHelper.getNullSafePeopleAssignments(task);
        workItem.setParameter(PeopleAssignmentHelper.ACTOR_ID, "");
        peopleAssignmentHelper.assignActors(workItem, peopleAssignments, taskData);
        Assert.assertEquals(0, peopleAssignments.getPotentialOwners().size());
        System.clearProperty("org.jbpm.ht.user.separator");
    }

    @Test
    public void testAssignActorsWithCustomSeparator() {
        peopleAssignmentHelper = new PeopleAssignmentHelper(":");
        String actorId = "user1:user2";
        Task task = TaskModelProvider.getFactory().newTask();
        InternalTaskData taskData = ((InternalTaskData) (TaskModelProvider.getFactory().newTaskData()));
        PeopleAssignments peopleAssignments = peopleAssignmentHelper.getNullSafePeopleAssignments(task);
        WorkItem workItem = new org.drools.core.process.instance.impl.WorkItemImpl();
        workItem.setParameter(PeopleAssignmentHelper.ACTOR_ID, actorId);
        peopleAssignmentHelper.assignActors(workItem, peopleAssignments, taskData);
        OrganizationalEntity organizationalEntity1 = peopleAssignments.getPotentialOwners().get(0);
        Assert.assertTrue((organizationalEntity1 instanceof User));
        Assert.assertEquals("user1", organizationalEntity1.getId());
        Assert.assertEquals("user1", taskData.getCreatedBy().getId());
        OrganizationalEntity organizationalEntity2 = peopleAssignments.getPotentialOwners().get(1);
        Assert.assertTrue((organizationalEntity2 instanceof User));
        Assert.assertEquals("user2", organizationalEntity2.getId());
        workItem = new org.drools.core.process.instance.impl.WorkItemImpl();
        peopleAssignments = peopleAssignmentHelper.getNullSafePeopleAssignments(task);
        workItem.setParameter(PeopleAssignmentHelper.ACTOR_ID, (actorId + ": drbug  "));
        peopleAssignmentHelper.assignActors(workItem, peopleAssignments, taskData);
        Assert.assertEquals(3, peopleAssignments.getPotentialOwners().size());
        organizationalEntity1 = peopleAssignments.getPotentialOwners().get(0);
        Assert.assertEquals("user1", organizationalEntity1.getId());
        Assert.assertEquals("user1", taskData.getCreatedBy().getId());
        organizationalEntity2 = peopleAssignments.getPotentialOwners().get(1);
        Assert.assertTrue((organizationalEntity2 instanceof User));
        Assert.assertEquals("user2", organizationalEntity2.getId());
        OrganizationalEntity organizationalEntity3 = peopleAssignments.getPotentialOwners().get(2);
        Assert.assertEquals("drbug", organizationalEntity3.getId());
        workItem = new org.drools.core.process.instance.impl.WorkItemImpl();
        peopleAssignments = peopleAssignmentHelper.getNullSafePeopleAssignments(task);
        workItem.setParameter(PeopleAssignmentHelper.ACTOR_ID, "");
        peopleAssignmentHelper.assignActors(workItem, peopleAssignments, taskData);
        Assert.assertEquals(0, peopleAssignments.getPotentialOwners().size());
    }

    @Test
    public void testAssignBusinessAdministrators() {
        String businessAdministratorId = "espiegelberg";
        Task task = TaskModelProvider.getFactory().newTask();
        PeopleAssignments peopleAssignments = peopleAssignmentHelper.getNullSafePeopleAssignments(task);
        WorkItem workItem = new org.drools.core.process.instance.impl.WorkItemImpl();
        workItem.setParameter(PeopleAssignmentHelper.BUSINESSADMINISTRATOR_ID, businessAdministratorId);
        peopleAssignmentHelper.assignBusinessAdministrators(workItem, peopleAssignments);
        Assert.assertEquals(3, peopleAssignments.getBusinessAdministrators().size());
        OrganizationalEntity organizationalEntity1 = peopleAssignments.getBusinessAdministrators().get(0);
        Assert.assertTrue((organizationalEntity1 instanceof User));
        Assert.assertEquals("Administrator", organizationalEntity1.getId());
        OrganizationalEntity organizationalEntity2 = peopleAssignments.getBusinessAdministrators().get(1);
        Assert.assertTrue((organizationalEntity2 instanceof Group));
        Assert.assertEquals("Administrators", organizationalEntity2.getId());
        OrganizationalEntity organizationalEntity3 = peopleAssignments.getBusinessAdministrators().get(2);
        Assert.assertTrue((organizationalEntity3 instanceof User));
        Assert.assertEquals(businessAdministratorId, organizationalEntity3.getId());
    }

    @Test
    public void testAssignBusinessAdministratorGroups() {
        String businessAdministratorGroupId = "Super users";
        Task task = TaskModelProvider.getFactory().newTask();
        PeopleAssignments peopleAssignments = peopleAssignmentHelper.getNullSafePeopleAssignments(task);
        WorkItem workItem = new org.drools.core.process.instance.impl.WorkItemImpl();
        workItem.setParameter(PeopleAssignmentHelper.BUSINESSADMINISTRATOR_GROUP_ID, businessAdministratorGroupId);
        peopleAssignmentHelper.assignBusinessAdministrators(workItem, peopleAssignments);
        Assert.assertEquals(3, peopleAssignments.getBusinessAdministrators().size());
        OrganizationalEntity organizationalEntity1 = peopleAssignments.getBusinessAdministrators().get(0);
        Assert.assertTrue((organizationalEntity1 instanceof User));
        Assert.assertEquals("Administrator", organizationalEntity1.getId());
        OrganizationalEntity organizationalEntity2 = peopleAssignments.getBusinessAdministrators().get(1);
        Assert.assertTrue((organizationalEntity2 instanceof Group));
        Assert.assertEquals("Administrators", organizationalEntity2.getId());
        OrganizationalEntity organizationalEntity3 = peopleAssignments.getBusinessAdministrators().get(2);
        Assert.assertTrue((organizationalEntity3 instanceof Group));
        Assert.assertEquals(businessAdministratorGroupId, organizationalEntity3.getId());
    }

    @Test
    public void testAssignTaskstakeholders() {
        String taskStakeholderId = "espiegelberg";
        Task task = TaskModelProvider.getFactory().newTask();
        InternalPeopleAssignments peopleAssignments = peopleAssignmentHelper.getNullSafePeopleAssignments(task);
        WorkItem workItem = new org.drools.core.process.instance.impl.WorkItemImpl();
        workItem.setParameter(PeopleAssignmentHelper.TASKSTAKEHOLDER_ID, taskStakeholderId);
        peopleAssignmentHelper.assignTaskStakeholders(workItem, peopleAssignments);
        Assert.assertEquals(1, peopleAssignments.getTaskStakeholders().size());
        OrganizationalEntity organizationalEntity1 = peopleAssignments.getTaskStakeholders().get(0);
        Assert.assertTrue((organizationalEntity1 instanceof User));
        Assert.assertEquals(taskStakeholderId, organizationalEntity1.getId());
    }

    @Test
    public void testAssignGroups() {
        String groupId = "Software Developers, Project Managers";
        Task task = TaskModelProvider.getFactory().newTask();
        PeopleAssignments peopleAssignments = peopleAssignmentHelper.getNullSafePeopleAssignments(task);
        WorkItem workItem = new org.drools.core.process.instance.impl.WorkItemImpl();
        workItem.setParameter(PeopleAssignmentHelper.GROUP_ID, groupId);
        peopleAssignmentHelper.assignGroups(workItem, peopleAssignments);
        OrganizationalEntity organizationalEntity1 = peopleAssignments.getPotentialOwners().get(0);
        Assert.assertTrue((organizationalEntity1 instanceof Group));
        Assert.assertEquals("Software Developers", organizationalEntity1.getId());
        OrganizationalEntity organizationalEntity2 = peopleAssignments.getPotentialOwners().get(1);
        Assert.assertTrue((organizationalEntity2 instanceof Group));
        Assert.assertEquals("Project Managers", organizationalEntity2.getId());
    }

    @Test
    public void testgetNullSafePeopleAssignments() {
        Task task = TaskModelProvider.getFactory().newTask();
        InternalPeopleAssignments peopleAssignment = peopleAssignmentHelper.getNullSafePeopleAssignments(task);
        Assert.assertNotNull(peopleAssignment);
        peopleAssignment = peopleAssignmentHelper.getNullSafePeopleAssignments(task);
        Assert.assertNotNull(peopleAssignment);
        ((InternalTask) (task)).setPeopleAssignments(null);
        peopleAssignment = peopleAssignmentHelper.getNullSafePeopleAssignments(task);
        Assert.assertNotNull(peopleAssignment);
        Assert.assertEquals(0, peopleAssignment.getPotentialOwners().size());
        Assert.assertEquals(0, peopleAssignment.getBusinessAdministrators().size());
        Assert.assertEquals(0, peopleAssignment.getExcludedOwners().size());
        Assert.assertEquals(0, peopleAssignment.getRecipients().size());
        Assert.assertEquals(0, peopleAssignment.getTaskStakeholders().size());
    }

    @Test
    public void testHandlePeopleAssignments() {
        InternalTask task = ((InternalTask) (TaskModelProvider.getFactory().newTask()));
        InternalTaskData taskData = ((InternalTaskData) (TaskModelProvider.getFactory().newTaskData()));
        InternalPeopleAssignments peopleAssignment = peopleAssignmentHelper.getNullSafePeopleAssignments(task);
        Assert.assertNotNull(peopleAssignment);
        Assert.assertEquals(0, peopleAssignment.getPotentialOwners().size());
        Assert.assertEquals(0, peopleAssignment.getBusinessAdministrators().size());
        Assert.assertEquals(0, peopleAssignment.getTaskStakeholders().size());
        String actorId = "espiegelberg";
        String taskStakeholderId = "drmary";
        String businessAdministratorId = "drbug";
        String businessAdministratorGroupId = "Super users";
        String excludedOwnerId = "john";
        String recipientId = "mary";
        WorkItem workItem = new org.drools.core.process.instance.impl.WorkItemImpl();
        workItem.setParameter(PeopleAssignmentHelper.ACTOR_ID, actorId);
        workItem.setParameter(PeopleAssignmentHelper.TASKSTAKEHOLDER_ID, taskStakeholderId);
        workItem.setParameter(PeopleAssignmentHelper.BUSINESSADMINISTRATOR_ID, businessAdministratorId);
        workItem.setParameter(PeopleAssignmentHelper.BUSINESSADMINISTRATOR_GROUP_ID, businessAdministratorGroupId);
        workItem.setParameter(PeopleAssignmentHelper.EXCLUDED_OWNER_ID, excludedOwnerId);
        workItem.setParameter(PeopleAssignmentHelper.RECIPIENT_ID, recipientId);
        peopleAssignmentHelper.handlePeopleAssignments(workItem, task, taskData);
        List<OrganizationalEntity> potentialOwners = task.getPeopleAssignments().getPotentialOwners();
        Assert.assertEquals(1, potentialOwners.size());
        Assert.assertEquals(actorId, potentialOwners.get(0).getId());
        List<OrganizationalEntity> businessAdministrators = task.getPeopleAssignments().getBusinessAdministrators();
        Assert.assertEquals(4, businessAdministrators.size());
        Assert.assertEquals("Administrator", businessAdministrators.get(0).getId());
        // Admin group
        Assert.assertEquals("Administrators", businessAdministrators.get(1).getId());
        Assert.assertEquals(businessAdministratorId, businessAdministrators.get(2).getId());
        Assert.assertEquals(businessAdministratorGroupId, businessAdministrators.get(3).getId());
        List<OrganizationalEntity> taskStakehoders = ((InternalPeopleAssignments) (task.getPeopleAssignments())).getTaskStakeholders();
        Assert.assertEquals(1, taskStakehoders.size());
        Assert.assertEquals(taskStakeholderId, taskStakehoders.get(0).getId());
        List<OrganizationalEntity> excludedOwners = ((InternalPeopleAssignments) (task.getPeopleAssignments())).getExcludedOwners();
        Assert.assertEquals(1, excludedOwners.size());
        Assert.assertEquals(excludedOwnerId, excludedOwners.get(0).getId());
        List<OrganizationalEntity> recipients = ((InternalPeopleAssignments) (task.getPeopleAssignments())).getRecipients();
        Assert.assertEquals(1, recipients.size());
        Assert.assertEquals(recipientId, recipients.get(0).getId());
    }

    @Test
    public void testHandleMultiPeopleAssignments() {
        InternalTask task = ((InternalTask) (TaskModelProvider.getFactory().newTask()));
        InternalTaskData taskData = ((InternalTaskData) (TaskModelProvider.getFactory().newTaskData()));
        InternalPeopleAssignments peopleAssignment = peopleAssignmentHelper.getNullSafePeopleAssignments(task);
        Assert.assertNotNull(peopleAssignment);
        Assert.assertEquals(0, peopleAssignment.getPotentialOwners().size());
        Assert.assertEquals(0, peopleAssignment.getBusinessAdministrators().size());
        Assert.assertEquals(0, peopleAssignment.getTaskStakeholders().size());
        String actorId = "espiegelberg,john";
        String taskStakeholderId = "drmary,krisv";
        String businessAdministratorId = "drbug,peter";
        String businessAdministratorGroupId = "Super users,Flow administrators";
        String excludedOwnerId = "john,poul";
        String recipientId = "mary,steve";
        WorkItem workItem = new org.drools.core.process.instance.impl.WorkItemImpl();
        workItem.setParameter(PeopleAssignmentHelper.ACTOR_ID, actorId);
        workItem.setParameter(PeopleAssignmentHelper.TASKSTAKEHOLDER_ID, taskStakeholderId);
        workItem.setParameter(PeopleAssignmentHelper.BUSINESSADMINISTRATOR_ID, businessAdministratorId);
        workItem.setParameter(PeopleAssignmentHelper.BUSINESSADMINISTRATOR_GROUP_ID, businessAdministratorGroupId);
        workItem.setParameter(PeopleAssignmentHelper.EXCLUDED_OWNER_ID, excludedOwnerId);
        workItem.setParameter(PeopleAssignmentHelper.RECIPIENT_ID, recipientId);
        peopleAssignmentHelper.handlePeopleAssignments(workItem, task, taskData);
        List<OrganizationalEntity> potentialOwners = task.getPeopleAssignments().getPotentialOwners();
        Assert.assertEquals(2, potentialOwners.size());
        Assert.assertEquals("espiegelberg", potentialOwners.get(0).getId());
        Assert.assertEquals("john", potentialOwners.get(1).getId());
        List<OrganizationalEntity> businessAdministrators = task.getPeopleAssignments().getBusinessAdministrators();
        Assert.assertEquals(6, businessAdministrators.size());
        Assert.assertEquals("Administrator", businessAdministrators.get(0).getId());
        // Admin group
        Assert.assertEquals("Administrators", businessAdministrators.get(1).getId());
        Assert.assertEquals("drbug", businessAdministrators.get(2).getId());
        Assert.assertEquals("peter", businessAdministrators.get(3).getId());
        Assert.assertEquals("Super users", businessAdministrators.get(4).getId());
        Assert.assertEquals("Flow administrators", businessAdministrators.get(5).getId());
        List<OrganizationalEntity> taskStakehoders = ((InternalPeopleAssignments) (task.getPeopleAssignments())).getTaskStakeholders();
        Assert.assertEquals(2, taskStakehoders.size());
        Assert.assertEquals("drmary", taskStakehoders.get(0).getId());
        Assert.assertEquals("krisv", taskStakehoders.get(1).getId());
        List<OrganizationalEntity> excludedOwners = ((InternalPeopleAssignments) (task.getPeopleAssignments())).getExcludedOwners();
        Assert.assertEquals(2, excludedOwners.size());
        Assert.assertEquals("john", excludedOwners.get(0).getId());
        Assert.assertEquals("poul", excludedOwners.get(1).getId());
        List<OrganizationalEntity> recipients = ((InternalPeopleAssignments) (task.getPeopleAssignments())).getRecipients();
        Assert.assertEquals(2, recipients.size());
        Assert.assertEquals("mary", recipients.get(0).getId());
        Assert.assertEquals("steve", recipients.get(1).getId());
    }

    @Test
    public void testAssignExcludedOwners() {
        String excludedOwnerId = "espiegelberg";
        Task task = TaskModelProvider.getFactory().newTask();
        InternalPeopleAssignments peopleAssignments = peopleAssignmentHelper.getNullSafePeopleAssignments(task);
        WorkItem workItem = new org.drools.core.process.instance.impl.WorkItemImpl();
        workItem.setParameter(PeopleAssignmentHelper.EXCLUDED_OWNER_ID, excludedOwnerId);
        peopleAssignmentHelper.assignExcludedOwners(workItem, peopleAssignments);
        Assert.assertEquals(1, peopleAssignments.getExcludedOwners().size());
        OrganizationalEntity organizationalEntity1 = peopleAssignments.getExcludedOwners().get(0);
        Assert.assertTrue((organizationalEntity1 instanceof User));
        Assert.assertEquals(excludedOwnerId, organizationalEntity1.getId());
    }

    @Test
    public void testAssignRecipients() {
        String recipientId = "espiegelberg";
        Task task = TaskModelProvider.getFactory().newTask();
        InternalPeopleAssignments peopleAssignments = peopleAssignmentHelper.getNullSafePeopleAssignments(task);
        WorkItem workItem = new org.drools.core.process.instance.impl.WorkItemImpl();
        workItem.setParameter(PeopleAssignmentHelper.RECIPIENT_ID, recipientId);
        peopleAssignmentHelper.assignRecipients(workItem, peopleAssignments);
        Assert.assertEquals(1, peopleAssignments.getRecipients().size());
        OrganizationalEntity organizationalEntity1 = peopleAssignments.getRecipients().get(0);
        Assert.assertTrue((organizationalEntity1 instanceof User));
        Assert.assertEquals(recipientId, organizationalEntity1.getId());
    }

    private User createUser(String id) {
        return TaskModelProvider.getFactory().newUser(id);
    }

    private Group createGroup(String id) {
        return TaskModelProvider.getFactory().newGroup(id);
    }
}

