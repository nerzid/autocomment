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

import java.util.Collections;
import java.io.IOException;
import org.kie.internal.task.api.model.InternalPeopleAssignments;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import java.util.List;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.PeopleAssignments;
import org.kie.api.task.model.User;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "people-assignments")
@XmlAccessorType(value = XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class JaxbPeopleAssignments implements InternalPeopleAssignments {
    @XmlElement(name = "task-initiator-id")
    @XmlSchemaType(name = "string")
    private String taskInitiatorId;

    @XmlElement(name = "potential-owners")
    private List<JaxbOrganizationalEntity> potentialOwners;

    @XmlElement(name = "business-administrators")
    private List<JaxbOrganizationalEntity> businessAdministrators;

    @XmlElement(name = "excluded-owners")
    private List<JaxbOrganizationalEntity> excludedOwners;

    @XmlElement(name = "task-stakeholders")
    private List<JaxbOrganizationalEntity> taskStakeholders;

    @XmlElement
    private List<JaxbOrganizationalEntity> recipients;

    public JaxbPeopleAssignments() {
        // Default constructor for JAXB
    }

    public JaxbPeopleAssignments(PeopleAssignments peopleAssignments) {
        User taskInitiatorUser = peopleAssignments.getTaskInitiator();
        if (taskInitiatorUser != null) {
            JaxbPeopleAssignments.this.taskInitiatorId = taskInitiatorUser.getId();
        } 
        JaxbPeopleAssignments.this.businessAdministrators = AbstractJaxbTaskObject.convertListFromInterfaceToJaxbImpl(((InternalPeopleAssignments) (peopleAssignments)).getBusinessAdministrators(), OrganizationalEntity.class, JaxbOrganizationalEntity.class);
        JaxbPeopleAssignments.this.excludedOwners = AbstractJaxbTaskObject.convertListFromInterfaceToJaxbImpl(((InternalPeopleAssignments) (peopleAssignments)).getExcludedOwners(), OrganizationalEntity.class, JaxbOrganizationalEntity.class);
        JaxbPeopleAssignments.this.potentialOwners = AbstractJaxbTaskObject.convertListFromInterfaceToJaxbImpl(((InternalPeopleAssignments) (peopleAssignments)).getPotentialOwners(), OrganizationalEntity.class, JaxbOrganizationalEntity.class);
        JaxbPeopleAssignments.this.recipients = AbstractJaxbTaskObject.convertListFromInterfaceToJaxbImpl(((InternalPeopleAssignments) (peopleAssignments)).getRecipients(), OrganizationalEntity.class, JaxbOrganizationalEntity.class);
        JaxbPeopleAssignments.this.taskStakeholders = AbstractJaxbTaskObject.convertListFromInterfaceToJaxbImpl(((InternalPeopleAssignments) (peopleAssignments)).getTaskStakeholders(), OrganizationalEntity.class, JaxbOrganizationalEntity.class);
    }

    @Override
    public User getTaskInitiator() {
        if ((JaxbPeopleAssignments.this.taskInitiatorId) != null) {
            return new org.jbpm.services.task.impl.model.xml.InternalJaxbWrapper.GetterUser(JaxbPeopleAssignments.this.taskInitiatorId);
        } 
        return null;
    }

    public void setTaskInitiator(User taskInitiatorUser) {
        if (taskInitiatorUser != null) {
            JaxbPeopleAssignments.this.taskInitiatorId = taskInitiatorUser.getId();
        } 
    }

    public String getTaskInitiatorId() {
        return taskInitiatorId;
    }

    public void setTaskInitiatorId(String taskInitiatorId) {
        JaxbPeopleAssignments.this.taskInitiatorId = taskInitiatorId;
    }

    @Override
    public List<OrganizationalEntity> getPotentialOwners() {
        if ((potentialOwners) == null) {
            return Collections.emptyList();
        } 
        return Collections.unmodifiableList(JaxbOrganizationalEntity.convertListFromJaxbImplToInterface(potentialOwners));
    }

    public void setPotentialOwners(List<OrganizationalEntity> potentialOwners) {
        JaxbPeopleAssignments.this.potentialOwners = AbstractJaxbTaskObject.convertListFromInterfaceToJaxbImpl(potentialOwners, OrganizationalEntity.class, JaxbOrganizationalEntity.class);
    }

    @Override
    public List<OrganizationalEntity> getBusinessAdministrators() {
        if ((businessAdministrators) == null) {
            return Collections.emptyList();
        } 
        return Collections.unmodifiableList(JaxbOrganizationalEntity.convertListFromJaxbImplToInterface(businessAdministrators));
    }

    public void setBusinessAdministrators(List<OrganizationalEntity> businessAdministrators) {
        JaxbPeopleAssignments.this.businessAdministrators = AbstractJaxbTaskObject.convertListFromInterfaceToJaxbImpl(businessAdministrators, OrganizationalEntity.class, JaxbOrganizationalEntity.class);
    }

    @Override
    public List<OrganizationalEntity> getExcludedOwners() {
        if ((excludedOwners) == null) {
            return Collections.emptyList();
        } 
        return Collections.unmodifiableList(JaxbOrganizationalEntity.convertListFromJaxbImplToInterface(excludedOwners));
    }

    @Override
    public void setExcludedOwners(List<OrganizationalEntity> excludedOwners) {
        JaxbPeopleAssignments.this.excludedOwners = AbstractJaxbTaskObject.convertListFromInterfaceToJaxbImpl(excludedOwners, OrganizationalEntity.class, JaxbOrganizationalEntity.class);
    }

    @Override
    public List<OrganizationalEntity> getTaskStakeholders() {
        if ((taskStakeholders) == null) {
            return Collections.emptyList();
        } 
        return Collections.unmodifiableList(JaxbOrganizationalEntity.convertListFromJaxbImplToInterface(taskStakeholders));
    }

    @Override
    public void setTaskStakeholders(List<OrganizationalEntity> taskStakeholders) {
        JaxbPeopleAssignments.this.taskStakeholders = AbstractJaxbTaskObject.convertListFromInterfaceToJaxbImpl(taskStakeholders, OrganizationalEntity.class, JaxbOrganizationalEntity.class);
    }

    @Override
    public List<OrganizationalEntity> getRecipients() {
        if ((recipients) == null) {
            return Collections.emptyList();
        } 
        return Collections.unmodifiableList(JaxbOrganizationalEntity.convertListFromJaxbImplToInterface(recipients));
    }

    @Override
    public void setRecipients(List<OrganizationalEntity> recipients) {
        JaxbPeopleAssignments.this.recipients = AbstractJaxbTaskObject.convertListFromInterfaceToJaxbImpl(recipients, OrganizationalEntity.class, JaxbOrganizationalEntity.class);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        AbstractJaxbTaskObject.unsupported(PeopleAssignments.class);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        AbstractJaxbTaskObject.unsupported(PeopleAssignments.class);
    }
}

