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

import java.util.Arrays;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.IOException;
import javax.persistence.Id;
import org.kie.internal.task.api.model.InternalContent;
import javax.persistence.Lob;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "Content")
@SequenceGenerator(allocationSize = 1, name = "contentIdSeq", sequenceName = "CONTENT_ID_SEQ")
public class ContentImpl implements InternalContent {
    @Id
    @GeneratedValue(generator = "contentIdSeq", strategy = GenerationType.AUTO)
    private Long id = 0L;

    @Lob
    @Column(length = 2147483647)
    private byte[] content;

    public ContentImpl() {
    }

    public ContentImpl(byte[] content) {
        ContentImpl.this.content = content;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(id);
        out.writeInt(content.length);
        out.write(content);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        id = in.readLong();
        content = new byte[in.readInt()];
        in.readFully(content);
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        ContentImpl.this.id = id;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        ContentImpl.this.content = content;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + (Arrays.hashCode(content));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ((ContentImpl.this) == obj)
            return true;
        
        if (obj == null)
            return false;
        
        if (!(obj instanceof ContentImpl))
            return false;
        
        ContentImpl other = ((ContentImpl) (obj));
        if (!(Arrays.equals(content, other.content)))
            return false;
        
        return true;
    }
}

