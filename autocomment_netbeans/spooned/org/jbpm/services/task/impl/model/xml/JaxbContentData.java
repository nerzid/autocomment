

package org.jbpm.services.task.impl.model.xml;

import org.kie.internal.task.api.model.AccessType;
import org.kie.internal.task.api.model.ContentData;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

@XmlRootElement(name = "content-data")
@XmlAccessorType(value = XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class JaxbContentData extends AbstractJaxbTaskObject<ContentData> implements ContentData {
    @XmlElement
    private AccessType accessType;

    @XmlElement
    @XmlSchemaType(name = "string")
    private String type;

    @XmlElement
    @XmlSchemaType(name = "base64Binary")
    private byte[] content = null;

    public JaxbContentData() {
        super(ContentData.class);
    }

    public JaxbContentData(ContentData contentData) {
        super(contentData, ContentData.class);
    }

    @Override
    public AccessType getAccessType() {
        return accessType;
    }

    @Override
    public void setAccessType(AccessType accessType) {
        JaxbContentData.this.accessType = accessType;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        JaxbContentData.this.type = type;
    }

    @Override
    public byte[] getContent() {
        return content;
    }

    @Override
    public void setContent(byte[] content) {
        JaxbContentData.this.content = content;
    }
}

