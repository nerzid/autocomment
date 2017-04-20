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

import javax.persistence.GenerationType;
import java.util.Arrays;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import java.io.IOException;
import javax.persistence.Id;
import org.kie.internal.task.api.model.InternalContent;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import java.io.ObjectInput;
import javax.persistence.Table;
import java.io.ObjectOutput;

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
        this.content = content;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        // write long Long{id} to ObjectOutput{out}
        out.writeLong(id);
        // write int int{content.length} to ObjectOutput{out}
        out.writeInt(content.length);
        // write byte[]{content} to ObjectOutput{out}
        out.write(content);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        id = in.readLong();
        content = new byte[in.readInt()];
        // read fully byte[]{content} to ObjectInput{in}
        in.readFully(content);
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
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
        if ((this) == obj)
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

