

package org.jbpm.services.task.impl.model.xml;

import org.kie.internal.task.api.model.AccessType;
import org.kie.internal.task.api.model.FaultData;
import java.io.IOException;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

@XmlRootElement(name = "fault-data")
@XmlAccessorType(value = XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class JaxbFaultData implements FaultData {
    @XmlElement
    private AccessType accessType;

    @XmlElement
    @XmlSchemaType(name = "string")
    private String type;

    @XmlElement
    @XmlSchemaType(name = "base64Binary")
    private byte[] content = null;

    @XmlElement(name = "fault-name")
    @XmlSchemaType(name = "string")
    private String faultName;

    public JaxbFaultData() {
        // JAXB constructor
    }

    public JaxbFaultData(FaultData faultData) {
        JaxbFaultData.this.accessType = faultData.getAccessType();
        JaxbFaultData.this.content = faultData.getContent();
        JaxbFaultData.this.faultName = faultData.getFaultName();
        JaxbFaultData.this.type = faultData.getType();
    }

    @Override
    public AccessType getAccessType() {
        return accessType;
    }

    @Override
    public void setAccessType(AccessType accessType) {
        JaxbFaultData.this.accessType = accessType;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        JaxbFaultData.this.type = type;
    }

    @Override
    public byte[] getContent() {
        return content;
    }

    @Override
    public void setContent(byte[] content) {
        JaxbFaultData.this.content = content;
    }

    @Override
    public String getFaultName() {
        return faultName;
    }

    @Override
    public void setFaultName(String faultName) {
        JaxbFaultData.this.faultName = faultName;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        AbstractJaxbTaskObject.unsupported(FaultData.class);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        AbstractJaxbTaskObject.unsupported(FaultData.class);
    }
}

