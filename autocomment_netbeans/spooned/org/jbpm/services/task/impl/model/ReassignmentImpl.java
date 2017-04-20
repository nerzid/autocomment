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

import javax.persistence.Entity;
import javax.persistence.CascadeType;
import javax.persistence.Table;
import org.jbpm.services.task.utils.CollectionUtils;
import java.util.List;
import javax.persistence.OneToMany;
import java.util.Collections;
import javax.persistence.GeneratedValue;
import javax.persistence.ManyToMany;
import javax.persistence.GenerationType;
import java.io.ObjectInput;
import javax.persistence.SequenceGenerator;
import org.kie.api.task.model.I18NText;
import java.io.ObjectOutput;
import java.io.IOException;
import javax.persistence.Id;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.internal.task.api.model.Reassignment;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;

@Entity
@Table(name = "Reassignment")
@SequenceGenerator(allocationSize = 1, name = "reassignmentIdSeq", sequenceName = "REASSIGNMENT_ID_SEQ")
public class ReassignmentImpl implements Reassignment {
    @Id
    @GeneratedValue(generator = "reassignmentIdSeq", strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, targetEntity = I18NTextImpl.class)
    @JoinColumn(name = "Reassignment_Documentation_Id", nullable = true)
    private List<I18NText> documentation = Collections.emptyList();

    @ManyToMany(targetEntity = OrganizationalEntityImpl.class)
    @JoinTable(inverseJoinColumns = @JoinColumn(name = "entity_id")
    , joinColumns = @JoinColumn(name = "task_id")
    , name = "Reassignment_potentialOwners")
    private List<OrganizationalEntity> potentialOwners = Collections.emptyList();

    public void writeExternal(ObjectOutput out) throws IOException {
        // write long Long{id} to ObjectOutput{out}
        out.writeLong(id);
        // write i18 List{documentation} to void{CollectionUtils}
        CollectionUtils.writeI18NTextList(documentation, out);
        // write organizational List{potentialOwners} to void{CollectionUtils}
        CollectionUtils.writeOrganizationalEntityList(potentialOwners, out);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        id = in.readLong();
        documentation = CollectionUtils.readI18NTextList(in);
        potentialOwners = CollectionUtils.readOrganizationalEntityList(in);
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<I18NText> getDocumentation() {
        return documentation;
    }

    public void setDocumentation(List<I18NText> documentation) {
        this.documentation = documentation;
    }

    public List<OrganizationalEntity> getPotentialOwners() {
        return potentialOwners;
    }

    public void setPotentialOwners(List<OrganizationalEntity> potentialOwners) {
        this.potentialOwners = potentialOwners;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + (CollectionUtils.hashCode(documentation));
        result = (prime * result) + (CollectionUtils.hashCode(potentialOwners));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ((this) == obj)
            return true;
        
        if (obj == null)
            return false;
        
        if (!(obj instanceof ReassignmentImpl))
            return false;
        
        ReassignmentImpl other = ((ReassignmentImpl) (obj));
        return (CollectionUtils.equals(documentation, other.documentation)) && (CollectionUtils.equals(potentialOwners, other.potentialOwners));
    }
}

