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

import org.kie.internal.task.api.model.Delegation;
import org.kie.internal.task.api.model.AllowedToDelegate;
import javax.persistence.ManyToMany;
import java.io.ObjectInput;
import org.jbpm.services.task.utils.CollectionUtils;
import java.util.Collections;
import java.io.ObjectOutput;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import org.kie.api.task.model.OrganizationalEntity;
import java.io.IOException;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import java.util.List;

@Embeddable
public class DelegationImpl implements Delegation {
    @Enumerated(value = EnumType.STRING)
    private AllowedToDelegate allowedToDelegate;

    @ManyToMany(targetEntity = OrganizationalEntityImpl.class)
    @JoinTable(inverseJoinColumns = @JoinColumn(name = "entity_id")
    , joinColumns = @JoinColumn(name = "task_id")
    , name = "Delegation_delegates")
    private List<OrganizationalEntity> delegates = Collections.emptyList();

    public void writeExternal(ObjectOutput out) throws IOException {
        if ((allowedToDelegate) != null) {
            out.writeBoolean(true);
            out.writeUTF(allowedToDelegate.toString());
        }else {
            out.writeBoolean(false);
        }
        // write organizational List{delegates} to void{CollectionUtils}
        CollectionUtils.writeOrganizationalEntityList(delegates, out);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        if (in.readBoolean()) {
            allowedToDelegate = AllowedToDelegate.valueOf(in.readUTF());
        }
        delegates = CollectionUtils.readOrganizationalEntityList(in);
    }

    public AllowedToDelegate getAllowed() {
        return allowedToDelegate;
    }

    public void setAllowed(AllowedToDelegate allowedToDelegate) {
        this.allowedToDelegate = allowedToDelegate;
    }

    public List<OrganizationalEntity> getDelegates() {
        return delegates;
    }

    public void setDelegates(List<OrganizationalEntity> delegates) {
        this.delegates = delegates;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((allowedToDelegate) == null ? 0 : allowedToDelegate.hashCode());
        result = (prime * result) + (CollectionUtils.hashCode(delegates));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ((this) == obj)
            return true;
        
        if (obj == null)
            return false;
        
        if (!(obj instanceof DelegationImpl))
            return false;
        
        DelegationImpl other = ((DelegationImpl) (obj));
        if ((allowedToDelegate) == null) {
            if ((other.allowedToDelegate) != null)
                return false;
            
        }else
            if (!(allowedToDelegate.equals(other.allowedToDelegate)))
                return false;
            
        
        return CollectionUtils.equals(delegates, other.delegates);
    }
}

