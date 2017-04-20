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

import java.util.ArrayList;
import java.io.IOException;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import static org.codehaus.jackson.annotate.JsonAutoDetect.Visibility.ANY;
import static org.codehaus.jackson.annotate.JsonAutoDetect.Visibility.NONE;
import java.util.List;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.User;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlElement;

@XmlType(name = "organizational-entity")
@XmlAccessorType(value = XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE)
public class JaxbOrganizationalEntity implements OrganizationalEntity {
    @XmlElement
    @XmlSchemaType(name = "string")
    private String id;

    @XmlElement
    private JaxbOrganizationalEntity.Type type;

    @XmlEnum
    public static enum Type {
USER, GROUP;    }

    public JaxbOrganizationalEntity() {
        // JAXB default
    }

    public JaxbOrganizationalEntity(OrganizationalEntity orgEntity) {
        this.id = orgEntity.getId();
        if (orgEntity instanceof User) {
            this.type = JaxbOrganizationalEntity.Type.USER;
        }else
            if (orgEntity instanceof org.kie.api.task.model.Group) {
                this.type = JaxbOrganizationalEntity.Type.GROUP;
            }else
                if (orgEntity instanceof JaxbOrganizationalEntity) {
                    this.type = ((JaxbOrganizationalEntity) (orgEntity)).type;
                }else {
                    throw new IllegalArgumentException(("Unknown type of organizational entity: " + (orgEntity.getClass().getSimpleName())));
                }
            
        
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JaxbOrganizationalEntity.Type getType() {
        return type;
    }

    public void setType(JaxbOrganizationalEntity.Type type) {
        this.type = type;
    }

    public static List<OrganizationalEntity> convertListFromJaxbImplToInterface(List<JaxbOrganizationalEntity> jaxbList) {
        List<OrganizationalEntity> orgEntList;
        if (jaxbList != null) {
            orgEntList = new ArrayList<OrganizationalEntity>(jaxbList.size());
            for (JaxbOrganizationalEntity jaxb : jaxbList) {
                orgEntList.add(jaxb.createImplInstance());
            }
        }else {
            // it would be nice to use Collections.EMPTY_LIST here, but there's a possibility the list is being modified after this call
            orgEntList = new ArrayList<OrganizationalEntity>();
        }
        return orgEntList;
    }

    private OrganizationalEntity createImplInstance() {
        switch (type) {
            case GROUP :
                return new InternalJaxbWrapper.GetterGroup(this.id);
            case USER :
                return new InternalJaxbWrapper.GetterUser(this.id);
            default :
                throw new IllegalStateException(("Unknown organizational type: " + (type)));
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        // unsupported Class{JaxbOrganizationalEntity.class} to void{AbstractJaxbTaskObject}
        AbstractJaxbTaskObject.unsupported(JaxbOrganizationalEntity.class);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        // unsupported Class{JaxbOrganizationalEntity.class} to void{AbstractJaxbTaskObject}
        AbstractJaxbTaskObject.unsupported(JaxbOrganizationalEntity.class);
    }
}

