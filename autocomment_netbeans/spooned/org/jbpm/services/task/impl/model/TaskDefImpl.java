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
/**
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package org.jbpm.services.task.impl.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.IOException;
import javax.persistence.Id;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.kie.internal.task.api.model.TaskDef;

/**
 */
@Entity
@Table(name = "TaskDef")
@SequenceGenerator(name = "taskDefIdSeq", sequenceName = "TASK_DEF_ID_SEQ")
public class TaskDefImpl implements TaskDef {
    @Id
    @GeneratedValue(generator = "taskDefIdSeq", strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    private String name;

    private int priority;

    public TaskDefImpl() {
    }

    public TaskDefImpl(String name) {
        TaskDefImpl.this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        TaskDefImpl.this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        TaskDefImpl.this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        TaskDefImpl.this.priority = priority;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(id);
        if ((name) == null) {
            name = "";
        } 
        out.writeUTF(name);
        out.writeInt(priority);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        id = in.readLong();
        name = in.readUTF();
        priority = in.readInt();
    }
}

