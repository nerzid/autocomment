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

import org.kie.api.task.model.User;
import java.util.Collections;
import static org.codehaus.jackson.annotate.JsonAutoDetect.Visibility.ANY;
import static org.codehaus.jackson.annotate.JsonAutoDetect.Visibility.NONE;
import java.io.IOException;
import javax.xml.bind.annotation.XmlType;
import org.kie.internal.task.api.model.InternalPeopleAssignments;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import java.util.List;
import java.io.ObjectInput;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import java.io.ObjectOutput;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.PeopleAssignments;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;

@XmlType(name = "people-assignments")
@XmlAccessorType(value = XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE)
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
            this.taskInitiatorId = taskInitiatorUser.getId();
        }
        this.businessAdministrators = AbstractJaxbTaskObject.convertListFromInterfaceToJaxbImpl(((InternalPeopleAssignments) (peopleAssignments)).getBusinessAdministrators(), OrganizationalEntity.class, JaxbOrganizationalEntity.class);
        this.excludedOwners = AbstractJaxbTaskObject.convertListFromInterfaceToJaxbImpl(((InternalPeopleAssignments) (peopleAssignments)).getExcludedOwners(), OrganizationalEntity.class, JaxbOrganizationalEntity.class);
        this.potentialOwners = AbstractJaxbTaskObject.convertListFromInterfaceToJaxbImpl(((InternalPeopleAssignments) (peopleAssignments)).getPotentialOwners(), OrganizationalEntity.class, JaxbOrganizationalEntity.class);
        this.recipients = AbstractJaxbTaskObject.convertListFromInterfaceToJaxbImpl(((InternalPeopleAssignments) (peopleAssignments)).getRecipients(), OrganizationalEntity.class, JaxbOrganizationalEntity.class);
        this.taskStakeholders = AbstractJaxbTaskObject.convertListFromInterfaceToJaxbImpl(((InternalPeopleAssignments) (peopleAssignments)).getTaskStakeholders(), OrganizationalEntity.class, JaxbOrganizationalEntity.class);
    }

    @Override
    public User getTaskInitiator() {
        if ((this.taskInitiatorId) != null) {
            return new InternalJaxbWrapper.GetterUser(this.taskInitiatorId);
        }
        return null;
    }

    public void setTaskInitiator(User taskInitiatorUser) {
        if (taskInitiatorUser != null) {
            this.taskInitiatorId = taskInitiatorUser.getId();
        }
    }

    public String getTaskInitiatorId() {
        return taskInitiatorId;
    }

    public void setTaskInitiatorId(String taskInitiatorId) {
        this.taskInitiatorId = taskInitiatorId;
    }

    @Override
    public List<OrganizationalEntity> getPotentialOwners() {
        if ((potentialOwners) == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(JaxbOrganizationalEntity.convertListFromJaxbImplToInterface(potentialOwners));
    }

    public void setPotentialOwners(List<OrganizationalEntity> potentialOwners) {
        this.potentialOwners = AbstractJaxbTaskObject.convertListFromInterfaceToJaxbImpl(potentialOwners, OrganizationalEntity.class, JaxbOrganizationalEntity.class);
    }

    @Override
    public List<OrganizationalEntity> getBusinessAdministrators() {
        if ((businessAdministrators) == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(JaxbOrganizationalEntity.convertListFromJaxbImplToInterface(businessAdministrators));
    }

    public void setBusinessAdministrators(List<OrganizationalEntity> businessAdministrators) {
        this.businessAdministrators = AbstractJaxbTaskObject.convertListFromInterfaceToJaxbImpl(businessAdministrators, OrganizationalEntity.class, JaxbOrganizationalEntity.class);
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
        this.excludedOwners = AbstractJaxbTaskObject.convertListFromInterfaceToJaxbImpl(excludedOwners, OrganizationalEntity.class, JaxbOrganizationalEntity.class);
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
        this.taskStakeholders = AbstractJaxbTaskObject.convertListFromInterfaceToJaxbImpl(taskStakeholders, OrganizationalEntity.class, JaxbOrganizationalEntity.class);
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
        this.recipients = AbstractJaxbTaskObject.convertListFromInterfaceToJaxbImpl(recipients, OrganizationalEntity.class, JaxbOrganizationalEntity.class);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        // unsupported Class{PeopleAssignments.class} to void{AbstractJaxbTaskObject}
        AbstractJaxbTaskObject.unsupported(PeopleAssignments.class);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        // unsupported Class{PeopleAssignments.class} to void{AbstractJaxbTaskObject}
        AbstractJaxbTaskObject.unsupported(PeopleAssignments.class);
    }
}

