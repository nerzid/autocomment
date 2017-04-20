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


package org.jbpm.services.task.impl.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.IOException;
import javax.persistence.Id;
import org.kie.internal.task.api.model.InternalComment;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import org.kie.api.task.model.User;
import java.io.ObjectOutput;
import javax.persistence.Table;
import javax.persistence.SequenceGenerator;
import java.io.ObjectInput;

@Entity
@Table(name = "task_comment")
@SequenceGenerator(allocationSize = 1, name = "commentIdSeq", sequenceName = "COMMENT_ID_SEQ")
public class CommentImpl implements InternalComment {
    @Id
    @GeneratedValue(generator = "commentIdSeq", strategy = GenerationType.AUTO)
    private Long id = 0L;

    @Lob
    @Column(length = 65535)
    private String text;

    @ManyToOne
    private UserImpl addedBy;

    private Date addedAt;

    public void writeExternal(ObjectOutput out) throws IOException {
        // write long Long{id} to ObjectOutput{out}
        out.writeLong(id);
        if ((text) == null) {
            text = "";
        }
        // write utf String{text} to ObjectOutput{out}
        out.writeUTF(text);
        // There are no guarantees that addedBy is not null = potential bug
        // write external ObjectOutput{out} to UserImpl{addedBy}
        addedBy.writeExternal(out);
        long addedAtTime = 0;
        if ((addedAt) != null) {
            addedAtTime = addedAt.getTime();
        }
        // write long long{addedAtTime} to ObjectOutput{out}
        out.writeLong(addedAtTime);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        id = in.readLong();
        text = in.readUTF();
        addedBy = new UserImpl();
        // read external ObjectInput{in} to UserImpl{addedBy}
        addedBy.readExternal(in);
        addedAt = new Date(in.readLong());
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Date addedDate) {
        this.addedAt = addedDate;
    }

    public User getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(User addedBy) {
        this.addedBy = convertToUserImpl(addedBy);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((addedBy) == null ? 0 : addedBy.hashCode());
        result = (prime * result) + ((addedAt) == null ? 0 : addedAt.hashCode());
        result = (prime * result) + ((text) == null ? 0 : text.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ((this) == obj)
            return true;
        
        if (obj == null)
            return false;
        
        if (!(obj instanceof CommentImpl))
            return false;
        
        CommentImpl other = ((CommentImpl) (obj));
        if ((addedBy) == null) {
            if ((other.addedBy) != null)
                return false;
            
        }else
            if (!(addedBy.equals(other.addedBy)))
                return false;
            
        
        if ((addedAt) == null) {
            if ((other.addedAt) != null)
                return false;
            
        }else
            if ((addedAt.getTime()) != (other.addedAt.getTime()))
                return false;
            
        
        if ((text) == null) {
            if ((other.text) != null)
                return false;
            
        }else
            if (!(text.equals(other.text)))
                return false;
            
        
        return true;
    }
}

