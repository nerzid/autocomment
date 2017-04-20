/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.persistence.scripts.oldentities;

import javax.persistence.CascadeType;
import org.jbpm.services.task.utils.CollectionUtils;
import javax.persistence.Basic;
import javax.persistence.EnumType;
import org.jbpm.services.task.impl.model.TaskDataImpl;
import java.util.List;
import java.io.IOException;
import javax.persistence.Id;
import java.io.ObjectOutput;
import org.jbpm.services.task.impl.model.PeopleAssignmentsImpl;
import javax.persistence.GeneratedValue;
import org.kie.api.task.model.I18NText;
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;
import org.kie.internal.task.api.model.SubTasksStrategy;
import javax.persistence.OneToMany;
import org.kie.api.task.model.PeopleAssignments;
import javax.persistence.Table;
import javax.persistence.Version;
import org.jbpm.services.task.impl.model.I18NTextImpl;
import java.io.ObjectInput;
import java.util.Collections;
import org.kie.api.task.model.TaskData;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import org.kie.internal.task.api.model.Deadlines;
import javax.persistence.Enumerated;
import org.jbpm.services.task.impl.model.DeadlinesImpl;
import org.kie.internal.task.api.model.Delegation;
import org.jbpm.services.task.impl.model.DelegationImpl;
import javax.persistence.Embedded;
import javax.persistence.Entity;

@Entity
@Table(name = "Task")
@SequenceGenerator(allocationSize = 1, name = "taskIdSeq", sequenceName = "TASK_ID_SEQ")
public class TaskImpl {
    /**
     * WSHT uses a name for the unique identifier, for now we use a generated ID which is also the key, which can be
     * mapped to the name or a unique name field added later.
     */
    @Id
    @GeneratedValue(generator = "taskIdSeq", strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id = 0L;

    @Version
    @Column(name = "OPTLOCK")
    private int version;

    /**
     * While WSHT says this is an expression, it always resolves to an integer, so resolve before setting
     * default value is 0.
     */
    private int priority;

    @OneToMany(cascade = CascadeType.ALL, targetEntity = I18NTextImpl.class)
    @JoinColumn(name = "Task_Names_Id", nullable = true)
    private List<I18NText> names = Collections.emptyList();

    @OneToMany(cascade = CascadeType.ALL, targetEntity = I18NTextImpl.class)
    @JoinColumn(name = "Task_Subjects_Id", nullable = true)
    private List<I18NText> subjects = Collections.emptyList();

    @OneToMany(cascade = CascadeType.ALL, targetEntity = I18NTextImpl.class)
    @JoinColumn(name = "Task_Descriptions_Id", nullable = true)
    private List<I18NText> descriptions = Collections.emptyList();

    @Embedded
    private PeopleAssignmentsImpl peopleAssignments;

    @Embedded
    private DelegationImpl delegation;

    @Embedded
    private TaskDataImpl taskData;

    @Embedded
    private DeadlinesImpl deadlines;

    // Default Behaviour
    @Enumerated(value = EnumType.STRING)
    private SubTasksStrategy subTaskStrategy = SubTasksStrategy.NoAction;

    private String taskType;

    private String formName;

    @Basic
    private Short archived = 0;

    public TaskImpl() {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        // write long Long{id} to ObjectOutput{out}
        out.writeLong(id);
        // write int int{priority} to ObjectOutput{out}
        out.writeInt(priority);
        // write short Short{archived} to ObjectOutput{out}
        out.writeShort(archived);
        // write utf String{taskType} to ObjectOutput{out}
        out.writeUTF(taskType);
        // write utf String{formName} to ObjectOutput{out}
        out.writeUTF(formName);
        // write i18 List{names} to void{CollectionUtils}
        CollectionUtils.writeI18NTextList(names, out);
        // write i18 List{subjects} to void{CollectionUtils}
        CollectionUtils.writeI18NTextList(subjects, out);
        // write i18 List{descriptions} to void{CollectionUtils}
        CollectionUtils.writeI18NTextList(descriptions, out);
        if ((subTaskStrategy) != null) {
            out.writeBoolean(true);
            out.writeUTF(subTaskStrategy.toString());
        }else {
            out.writeBoolean(false);
        }
        if ((peopleAssignments) != null) {
            out.writeBoolean(true);
            peopleAssignments.writeExternal(out);
        }else {
            out.writeBoolean(false);
        }
        if ((delegation) != null) {
            out.writeBoolean(true);
            delegation.writeExternal(out);
        }else {
            out.writeBoolean(false);
        }
        if ((taskData) != null) {
            out.writeBoolean(true);
            taskData.writeExternal(out);
        }else {
            out.writeBoolean(false);
        }
        if ((deadlines) != null) {
            out.writeBoolean(true);
            deadlines.writeExternal(out);
        }else {
            out.writeBoolean(false);
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        id = in.readLong();
        priority = in.readInt();
        archived = in.readShort();
        taskType = in.readUTF();
        formName = in.readUTF();
        names = CollectionUtils.readI18NTextList(in);
        subjects = CollectionUtils.readI18NTextList(in);
        descriptions = CollectionUtils.readI18NTextList(in);
        if (in.readBoolean()) {
            subTaskStrategy = SubTasksStrategy.valueOf(in.readUTF());
        }
        if (in.readBoolean()) {
            peopleAssignments = new PeopleAssignmentsImpl();
            peopleAssignments.readExternal(in);
        }
        if (in.readBoolean()) {
            delegation = new DelegationImpl();
            delegation.readExternal(in);
        }
        if (in.readBoolean()) {
            taskData = new TaskDataImpl();
            taskData.readExternal(in);
        }
        if (in.readBoolean()) {
            deadlines = new DeadlinesImpl();
            deadlines.readExternal(in);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Boolean isArchived() {
        if ((archived) == null) {
            return null;
        }
        return (archived) == 1 ? Boolean.TRUE : Boolean.FALSE;
    }

    public void setArchived(Boolean archived) {
        if (archived == null) {
            this.archived = null;
        }else {
            this.archived = (archived == true) ? new Short("1") : new Short("0");
        }
    }

    public int getVersion() {
        return this.version;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public List<I18NText> getNames() {
        return names;
    }

    public void setNames(List<I18NText> names) {
        this.names = names;
    }

    public List<I18NText> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<I18NText> subjects) {
        this.subjects = subjects;
    }

    public List<I18NText> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<I18NText> descriptions) {
        this.descriptions = descriptions;
    }

    public PeopleAssignments getPeopleAssignments() {
        return peopleAssignments;
    }

    public void setPeopleAssignments(PeopleAssignments peopleAssignments) {
        this.peopleAssignments = ((PeopleAssignmentsImpl) (peopleAssignments));
    }

    public Delegation getDelegation() {
        return delegation;
    }

    public void setDelegation(Delegation delegation) {
        this.delegation = ((DelegationImpl) (delegation));
    }

    public TaskData getTaskData() {
        return taskData;
    }

    public void setTaskData(TaskData taskData) {
        this.taskData = ((TaskDataImpl) (taskData));
    }

    public Deadlines getDeadlines() {
        return deadlines;
    }

    public void setDeadlines(Deadlines deadlines) {
        this.deadlines = ((DeadlinesImpl) (deadlines));
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + (version);
        result = (prime * result) + (priority);
        result = (prime * result) + (archived.hashCode());
        result = (prime * result) + ((taskType) == null ? 0 : taskType.hashCode());
        result = (prime * result) + (CollectionUtils.hashCode(descriptions));
        result = (prime * result) + (CollectionUtils.hashCode(names));
        result = (prime * result) + (CollectionUtils.hashCode(subjects));
        result = (prime * result) + ((peopleAssignments) == null ? 0 : peopleAssignments.hashCode());
        result = (prime * result) + ((delegation) == null ? 0 : delegation.hashCode());
        result = (prime * result) + ((taskData) == null ? 0 : taskData.hashCode());
        result = (prime * result) + ((deadlines) == null ? 0 : deadlines.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ((this) == obj)
            return true;
        
        if (obj == null)
            return false;
        
        if (!(obj instanceof TaskImpl))
            return false;
        
        TaskImpl other = ((TaskImpl) (obj));
        if ((this.version) != (other.version)) {
            return false;
        }
        if ((this.archived) != (other.archived)) {
            return false;
        }
        if ((taskType) == null) {
            if ((other.taskType) != null)
                return false;
            
        }else
            if (!(taskType.equals(other.taskType)))
                return false;
            
        
        if ((deadlines) == null) {
            if ((other.deadlines) != null) {
            }
        }else
            if (!(deadlines.equals(other.deadlines)))
                return false;
            
        
        if ((delegation) == null) {
            if ((other.delegation) != null)
                return false;
            
        }else
            if (!(delegation.equals(other.delegation)))
                return false;
            
        
        if ((peopleAssignments) == null) {
            if ((other.peopleAssignments) != null)
                return false;
            
        }else
            if (!(peopleAssignments.equals(other.peopleAssignments)))
                return false;
            
        
        if ((priority) != (other.priority))
            return false;
        
        if ((taskData) == null) {
            if ((other.taskData) != null)
                return false;
            
        }else
            if (!(taskData.equals(other.taskData)))
                return false;
            
        
        return ((CollectionUtils.equals(descriptions, other.descriptions)) && (CollectionUtils.equals(names, other.names))) && (CollectionUtils.equals(subjects, other.subjects));
    }

    public SubTasksStrategy getSubTaskStrategy() {
        return subTaskStrategy;
    }

    public void setSubTaskStrategy(SubTasksStrategy subTaskStrategy) {
        this.subTaskStrategy = subTaskStrategy;
    }
}

