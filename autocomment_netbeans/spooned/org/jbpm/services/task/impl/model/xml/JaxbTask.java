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


package org.jbpm.services.task.impl.model.xml;

import java.util.ArrayList;
import org.kie.api.task.model.Attachment;
import java.util.Collections;
import org.kie.api.task.model.Comment;
import org.kie.internal.task.api.model.Deadlines;
import org.kie.internal.task.api.model.Delegation;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.I18NText;
import java.io.IOException;
import org.kie.internal.task.api.model.InternalAttachment;
import org.kie.internal.task.api.model.InternalComment;
import org.kie.internal.task.api.model.InternalI18NText;
import org.kie.internal.task.api.model.InternalPeopleAssignments;
import org.kie.internal.task.api.model.InternalTask;
import org.kie.internal.task.api.model.InternalTaskData;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import java.util.List;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.PeopleAssignments;
import org.kie.internal.task.api.model.SubTasksStrategy;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskData;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.api.task.model.User;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

@XmlRootElement(name = "task")
@XmlAccessorType(value = XmlAccessType.FIELD)
@JsonIgnoreProperties(value = { "archived" , "deadlines" })
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class JaxbTask implements InternalTask {
    @XmlElement
    @XmlSchemaType(name = "long")
    private Long id;

    @XmlElement
    @XmlSchemaType(name = "int")
    private Integer priority;

    @XmlElement
    @XmlSchemaType(name = "int")
    private Integer version;

    @XmlElement
    @XmlSchemaType(name = "boolean")
    private Boolean archived;

    @XmlElement(name = "task-type")
    @XmlSchemaType(name = "string")
    private String taskType;

    @XmlElement
    @XmlSchemaType(name = "string")
    private String name;

    @XmlElement
    @XmlSchemaType(name = "string")
    private String subject;

    @XmlElement
    @XmlSchemaType(name = "string")
    private String description;

    @XmlElement
    private List<JaxbI18NText> names;

    @XmlElement
    private List<JaxbI18NText> subjects;

    @XmlElement
    private List<JaxbI18NText> descriptions;

    @XmlElement(name = "people-assignments")
    private JaxbPeopleAssignments peopleAssignments;

    @XmlElement
    private SubTasksStrategy subTasksStrategy;

    @XmlElement
    private JaxbTaskData taskData;

    @XmlElement
    private JaxbDeadlines deadlines = new JaxbDeadlines();

    @XmlElement(name = "form-name")
    @XmlSchemaType(name = "string")
    private String formName;

    public JaxbTask() {
        // Default constructor
    }

    public JaxbTask(Task task) {
        initialize(task);
    }

    public void initialize(Task task) {
        if (task == null) {
            return ;
        } 
        JaxbTask.this.id = task.getId();
        JaxbTask.this.priority = task.getPriority();
        JaxbTask.this.subTasksStrategy = ((InternalTask) (task)).getSubTaskStrategy();
        JaxbTask.this.peopleAssignments = new JaxbPeopleAssignments(task.getPeopleAssignments());
        JaxbTask.this.names = AbstractJaxbTaskObject.convertListFromInterfaceToJaxbImpl(task.getNames(), I18NText.class, JaxbI18NText.class);
        JaxbTask.this.name = ((InternalTask) (task)).getName();
        JaxbTask.this.subjects = AbstractJaxbTaskObject.convertListFromInterfaceToJaxbImpl(task.getSubjects(), I18NText.class, JaxbI18NText.class);
        JaxbTask.this.subject = ((InternalTask) (task)).getSubject();
        JaxbTask.this.descriptions = AbstractJaxbTaskObject.convertListFromInterfaceToJaxbImpl(task.getDescriptions(), I18NText.class, JaxbI18NText.class);
        JaxbTask.this.description = ((InternalTask) (task)).getDescription();
        JaxbTask.this.taskType = task.getTaskType();
        JaxbTask.this.formName = ((InternalTask) (task)).getFormName();
        JaxbTask.this.taskData = new JaxbTaskData(task.getTaskData());
    }

    /**
     * This is a convienence method that retrieves a TaskImpl instance. It's used
     * internally in the {@link AddTaskCommand#execute(org.kie.internal.command.Context)} method
     * because that command requires a persistable task representation.
     * </p>
     * Users who are looking for information from the task should <i>not</i> use this method:
     * all of the task information is already available via the normal methods
     * defined by the {@link Task} or {@link InternalTask} interfaces, both of which this class
     * implements: for example: {@link JaxbTask#getId()}, {@link JaxbTask#getTaskData()}
     * or {@link JaxbTask#getPeopleAssignments()}.
     * @return a TaskImpl instance
     */
    public Task getTask() {
        InternalTask taskImpl = ((InternalTask) (TaskModelProvider.getFactory().newTask()));
        if ((JaxbTask.this.getId()) != null) {
            taskImpl.setId(JaxbTask.this.getId());
        } 
        if ((JaxbTask.this.priority) != null) {
            taskImpl.setPriority(JaxbTask.this.getPriority());
        } 
        JaxbPeopleAssignments jaxbPeopleAssignments = JaxbTask.this.peopleAssignments;
        InternalPeopleAssignments peopleAssignments = ((InternalPeopleAssignments) (TaskModelProvider.getFactory().newPeopleAssignments()));
        if ((jaxbPeopleAssignments.getTaskInitiator()) != null) {
            User user = createUser(JaxbTask.this.getPeopleAssignments().getTaskInitiator().getId());
            peopleAssignments.setTaskInitiator(user);
        } 
        List<OrganizationalEntity> potentialOwners = copyOrganizationalEntityList(jaxbPeopleAssignments.getPotentialOwners());
        peopleAssignments.setPotentialOwners(potentialOwners);
        List<OrganizationalEntity> businessAdmins = copyOrganizationalEntityList(jaxbPeopleAssignments.getBusinessAdministrators());
        peopleAssignments.setBusinessAdministrators(businessAdmins);
        List<OrganizationalEntity> exclOwners = copyOrganizationalEntityList(jaxbPeopleAssignments.getExcludedOwners());
        peopleAssignments.setExcludedOwners(exclOwners);
        List<OrganizationalEntity> taskStake = copyOrganizationalEntityList(jaxbPeopleAssignments.getTaskStakeholders());
        peopleAssignments.setTaskStakeholders(taskStake);
        List<OrganizationalEntity> recipients = copyOrganizationalEntityList(jaxbPeopleAssignments.getRecipients());
        peopleAssignments.setRecipients(recipients);
        taskImpl.setPeopleAssignments(peopleAssignments);
        taskImpl.setSubTaskStrategy(JaxbTask.this.getSubTaskStrategy());
        {
            List<I18NText> names = new ArrayList<I18NText>();
            for (I18NText n : JaxbTask.this.getNames()) {
                I18NText text = TaskModelProvider.getFactory().newI18NText();
                ((InternalI18NText) (text)).setId(n.getId());
                ((InternalI18NText) (text)).setLanguage(n.getLanguage());
                ((InternalI18NText) (text)).setText(n.getText());
                names.add(text);
            }
            taskImpl.setNames(names);
        }
        if ((JaxbTask.this.getName()) != null) {
            taskImpl.setName(JaxbTask.this.getName());
        } else if (!(JaxbTask.this.getNames().isEmpty())) {
            taskImpl.setName(JaxbTask.this.getNames().get(0).getText());
        } 
        {
            List<I18NText> subjects = new ArrayList<I18NText>();
            for (I18NText s : JaxbTask.this.getSubjects()) {
                I18NText text = TaskModelProvider.getFactory().newI18NText();
                ((InternalI18NText) (text)).setId(s.getId());
                ((InternalI18NText) (text)).setLanguage(s.getLanguage());
                ((InternalI18NText) (text)).setText(s.getText());
                subjects.add(text);
            }
            taskImpl.setSubjects(subjects);
        }
        if ((JaxbTask.this.getSubject()) != null) {
            taskImpl.setSubject(JaxbTask.this.getSubject());
        } else if (!(JaxbTask.this.getSubjects().isEmpty())) {
            taskImpl.setSubject(JaxbTask.this.getSubjects().get(0).getText());
        } 
        {
            List<I18NText> descriptions = new ArrayList<I18NText>();
            for (I18NText d : JaxbTask.this.getDescriptions()) {
                I18NText text = TaskModelProvider.getFactory().newI18NText();
                ((InternalI18NText) (text)).setId(d.getId());
                ((InternalI18NText) (text)).setLanguage(d.getLanguage());
                ((InternalI18NText) (text)).setText(d.getText());
                descriptions.add(text);
            }
            taskImpl.setDescriptions(descriptions);
        }
        if ((JaxbTask.this.getDescription()) != null) {
            taskImpl.setDescription(JaxbTask.this.getDescription());
        } else if (!(JaxbTask.this.getDescriptions().isEmpty())) {
            taskImpl.setDescription(JaxbTask.this.getDescriptions().get(0).getText());
        } 
        taskImpl.setTaskType(JaxbTask.this.getTaskType());
        taskImpl.setFormName(JaxbTask.this.getFormName());
        // task data
        InternalTaskData taskData = ((InternalTaskData) (TaskModelProvider.getFactory().newTaskData()));
        JaxbTaskData jaxbTaskData = ((JaxbTaskData) (JaxbTask.this.getTaskData()));
        taskData.setStatus(jaxbTaskData.getStatus());
        taskData.setPreviousStatus(jaxbTaskData.getPreviousStatus());
        taskData.setActualOwner(createUser(jaxbTaskData.getActualOwnerId()));
        taskData.setCreatedBy(createUser(jaxbTaskData.getCreatedById()));
        taskData.setCreatedOn(jaxbTaskData.getCreatedOn());
        taskData.setActivationTime(jaxbTaskData.getActivationTime());
        taskData.setExpirationTime(jaxbTaskData.getExpirationTime());
        taskData.setSkipable(jaxbTaskData.isSkipable());
        taskData.setWorkItemId(jaxbTaskData.getWorkItemId());
        taskData.setProcessInstanceId(jaxbTaskData.getProcessInstanceId());
        taskData.setDocumentContentId(jaxbTaskData.getDocumentContentId());
        taskData.setDocumentAccessType(jaxbTaskData.getDocumentAccessType());
        taskData.setDocumentType(jaxbTaskData.getDocumentType());
        taskData.setOutputAccessType(jaxbTaskData.getOutputAccessType());
        taskData.setOutputType(jaxbTaskData.getOutputType());
        taskData.setOutputContentId(jaxbTaskData.getOutputContentId());
        taskData.setFaultName(jaxbTaskData.getFaultName());
        taskData.setFaultAccessType(jaxbTaskData.getFaultAccessType());
        taskData.setFaultType(jaxbTaskData.getFaultType());
        taskData.setFaultContentId(jaxbTaskData.getFaultContentId());
        taskData.setParentId(jaxbTaskData.getParentId());
        taskData.setProcessId(jaxbTaskData.getProcessId());
        taskData.setProcessSessionId(jaxbTaskData.getProcessSessionId());
        List<Comment> jaxbComments = jaxbTaskData.getComments();
        if (jaxbComments != null) {
            List<Comment> comments = new ArrayList<Comment>(jaxbComments.size());
            for (Comment jaxbComment : jaxbComments) {
                InternalComment comment = ((InternalComment) (TaskModelProvider.getFactory().newComment()));
                if ((jaxbComment.getId()) != null) {
                    comment.setId(jaxbComment.getId());
                } 
                comment.setAddedAt(jaxbComment.getAddedAt());
                comment.setAddedBy(createUser(((JaxbComment) (jaxbComment)).getAddedById()));
                comment.setText(jaxbComment.getText());
                comments.add(comment);
            }
            taskData.setComments(comments);
        } 
        List<Attachment> jaxbAttachments = jaxbTaskData.getAttachments();
        if (jaxbAttachments != null) {
            List<Attachment> attachments = new ArrayList<Attachment>(jaxbAttachments.size());
            for (Attachment jaxbAttach : jaxbAttachments) {
                InternalAttachment attach = ((InternalAttachment) (TaskModelProvider.getFactory().newAttachment()));
                if ((jaxbAttach.getId()) != null) {
                    attach.setId(jaxbAttach.getId());
                } 
                attach.setName(jaxbAttach.getName());
                attach.setContentType(jaxbAttach.getContentType());
                attach.setAttachedAt(jaxbAttach.getAttachedAt());
                attach.setAttachedBy(createUser(((JaxbAttachment) (jaxbAttach)).getAttachedById()));
                attach.setSize(jaxbAttach.getSize());
                attach.setAttachmentContentId(jaxbAttach.getAttachmentContentId());
                attachments.add(attach);
            }
            taskData.setAttachments(attachments);
        } 
        taskData.setDeploymentId(jaxbTaskData.getDeploymentId());
        ((InternalTask) (taskImpl)).setTaskData(taskData);
        return taskImpl;
    }

    private User createUser(String userId) {
        if (userId == null) {
            return null;
        } 
        return TaskModelProvider.getFactory().newUser(userId);
    }

    private Group createGroup(String groupId) {
        if (groupId == null) {
            return null;
        } 
        return TaskModelProvider.getFactory().newGroup(groupId);
    }

    private List<OrganizationalEntity> copyOrganizationalEntityList(List<OrganizationalEntity> jaxbOrgEntList) {
        if (jaxbOrgEntList == null) {
            return null;
        } 
        List<OrganizationalEntity> orgEntList = new ArrayList<OrganizationalEntity>(jaxbOrgEntList.size());
        for (OrganizationalEntity jaxbOrgEnt : jaxbOrgEntList) {
            if (jaxbOrgEnt instanceof User) {
                User user = createUser(jaxbOrgEnt.getId());
                orgEntList.add(user);
            } else if (jaxbOrgEnt instanceof Group) {
                Group group = createGroup(jaxbOrgEnt.getId());
                orgEntList.add(group);
            } 
        }
        return orgEntList;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        JaxbTask.this.id = id;
    }

    @Override
    public int getPriority() {
        return whenNull(priority, 0);
    }

    @Override
    public void setPriority(int priority) {
        JaxbTask.this.priority = priority;
    }

    @Override
    public List<I18NText> getNames() {
        if ((names) == null) {
            return Collections.emptyList();
        } 
        return Collections.unmodifiableList(JaxbI18NText.convertListFromJaxbImplToInterface(names));
    }

    public void setNames(List<I18NText> names) {
        JaxbTask.this.names = AbstractJaxbTaskObject.convertListFromInterfaceToJaxbImpl(names, I18NText.class, JaxbI18NText.class);
    }

    public List<I18NText> getSubjects() {
        if ((subjects) == null) {
            return Collections.emptyList();
        } 
        return Collections.unmodifiableList(JaxbI18NText.convertListFromJaxbImplToInterface(subjects));
    }

    public void setSubjects(List<I18NText> subjects) {
        JaxbTask.this.subjects = AbstractJaxbTaskObject.convertListFromInterfaceToJaxbImpl(subjects, I18NText.class, JaxbI18NText.class);
    }

    @Override
    public List<I18NText> getDescriptions() {
        if ((descriptions) == null) {
            return Collections.emptyList();
        } 
        return Collections.unmodifiableList(JaxbI18NText.convertListFromJaxbImplToInterface(descriptions));
    }

    public void setDescriptions(List<I18NText> descriptions) {
        JaxbTask.this.descriptions = AbstractJaxbTaskObject.convertListFromInterfaceToJaxbImpl(descriptions, I18NText.class, JaxbI18NText.class);
    }

    @Override
    public PeopleAssignments getPeopleAssignments() {
        return peopleAssignments;
    }

    public void setPeopleAssignments(PeopleAssignments peopleAssignments) {
        if (peopleAssignments instanceof JaxbPeopleAssignments) {
            JaxbTask.this.peopleAssignments = ((JaxbPeopleAssignments) (peopleAssignments));
        } else {
            JaxbTask.this.peopleAssignments = new JaxbPeopleAssignments(peopleAssignments);
        }
    }

    @Override
    public TaskData getTaskData() {
        return taskData;
    }

    public void setTaskData(TaskData taskData) {
        if (taskData instanceof JaxbTaskData) {
            JaxbTask.this.taskData = ((JaxbTaskData) (taskData));
        } else {
            JaxbTask.this.taskData = new JaxbTaskData(taskData);
        }
    }

    @Override
    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        JaxbTask.this.taskType = taskType;
    }

    @Override
    public Deadlines getDeadlines() {
        return JaxbTask.this.deadlines;
    }

    @Override
    public void setDeadlines(Deadlines deadlines) {
        // no-op
    }

    @Override
    public void setFormName(String formName) {
        JaxbTask.this.formName = formName;
    }

    @Override
    public String getFormName() {
        return JaxbTask.this.formName;
    }

    @Override
    public Boolean isArchived() {
        return JaxbTask.this.archived;
    }

    @Override
    public void setArchived(Boolean archived) {
        JaxbTask.this.archived = archived;
    }

    public void setVersion(Integer version) {
        unsupported(Void.class);
    }

    @Override
    public int getVersion() {
        return unsupported(int.class);
    }

    @Override
    public Delegation getDelegation() {
        return unsupported(Delegation.class);
    }

    @Override
    public void setDelegation(Delegation delegation) {
        unsupported(Task.class);
    }

    @Override
    public SubTasksStrategy getSubTaskStrategy() {
        return JaxbTask.this.subTasksStrategy;
    }

    @Override
    public void setSubTaskStrategy(SubTasksStrategy subTaskStrategy) {
        JaxbTask.this.subTasksStrategy = subTaskStrategy;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSubject() {
        return subject;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        JaxbTask.this.name = name;
    }

    public void setSubject(String subject) {
        JaxbTask.this.subject = subject;
    }

    public void setDescription(String description) {
        JaxbTask.this.description = description;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        unsupported(Task.class);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        unsupported(Task.class);
    }
}

