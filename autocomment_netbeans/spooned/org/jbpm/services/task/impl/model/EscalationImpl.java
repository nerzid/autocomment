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

import org.kie.internal.task.api.model.BooleanExpression;
import javax.persistence.CascadeType;
import org.jbpm.services.task.utils.CollectionUtils;
import java.util.Collections;
import javax.persistence.Entity;
import org.kie.internal.task.api.model.Escalation;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.IOException;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import java.util.List;
import org.kie.internal.task.api.model.Notification;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javax.persistence.OneToMany;
import org.kie.internal.task.api.model.Reassignment;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "Escalation")
@SequenceGenerator(allocationSize = 1, name = "escalationIdSeq", sequenceName = "ESCALATION_ID_SEQ")
public class EscalationImpl implements Escalation {
    @Id
    @GeneratedValue(generator = "escalationIdSeq", strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @OneToMany(cascade = CascadeType.ALL, targetEntity = BooleanExpressionImpl.class)
    @JoinColumn(name = "Escalation_Constraints_Id", nullable = true)
    private List<BooleanExpression> constraints = Collections.emptyList();

    @OneToMany(cascade = CascadeType.ALL, targetEntity = NotificationImpl.class)
    @JoinColumn(name = "Escalation_Notifications_Id", nullable = true)
    private List<Notification> notifications = Collections.emptyList();

    @OneToMany(cascade = CascadeType.ALL, targetEntity = ReassignmentImpl.class)
    @JoinColumn(name = "Escalation_Reassignments_Id", nullable = true)
    private List<Reassignment> reassignments = Collections.emptyList();

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(id);
        if ((name) != null) {
            out.writeBoolean(true);
            out.writeUTF(name);
        } else {
            out.writeBoolean(false);
        }
        CollectionUtils.writeBooleanExpressionList(constraints, out);
        CollectionUtils.writeNotificationList(notifications, out);
        CollectionUtils.writeReassignmentList(reassignments, out);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        id = in.readLong();
        if (in.readBoolean()) {
            name = in.readUTF();
        } 
        constraints = CollectionUtils.readBooleanExpressionList(in);
        notifications = CollectionUtils.readNotificationList(in);
        reassignments = CollectionUtils.readReassignmentList(in);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        EscalationImpl.this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        EscalationImpl.this.name = name;
    }

    public List<BooleanExpression> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<BooleanExpression> constraints) {
        EscalationImpl.this.constraints = constraints;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        EscalationImpl.this.notifications = notifications;
    }

    public List<Reassignment> getReassignments() {
        return reassignments;
    }

    public void setReassignments(List<Reassignment> reassignments) {
        EscalationImpl.this.reassignments = reassignments;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + (CollectionUtils.hashCode(constraints));
        result = (prime * result) + ((name) == null ? 0 : name.hashCode());
        result = (prime * result) + (CollectionUtils.hashCode(notifications));
        result = (prime * result) + (CollectionUtils.hashCode(reassignments));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ((EscalationImpl.this) == obj)
            return true;
        
        if (obj == null)
            return false;
        
        if (!(obj instanceof EscalationImpl))
            return false;
        
        EscalationImpl other = ((EscalationImpl) (obj));
        if ((name) == null) {
            if ((other.name) != null)
                return false;
            
        } else if (!(name.equals(other.name)))
            return false;
        
        return ((CollectionUtils.equals(constraints, other.constraints)) && (CollectionUtils.equals(notifications, other.notifications))) && (CollectionUtils.equals(reassignments, other.reassignments));
    }
}

