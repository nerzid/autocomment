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

import java.io.IOException;
import org.kie.api.task.model.Content;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import java.util.Map;
import static org.codehaus.jackson.annotate.JsonAutoDetect.Visibility.ANY;
import static org.codehaus.jackson.annotate.JsonAutoDetect.Visibility.NONE;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.kie.internal.jaxb.StringKeyObjectValueMapXmlAdapter;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlElement;

@XmlRootElement(name = "content")
@XmlAccessorType(value = XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE)
public class JaxbContent implements Content {
    @XmlElement
    private Long id;

    @XmlElement
    @XmlSchemaType(name = "base64Binary")
    private byte[] content = null;

    @XmlJavaTypeAdapter(value = StringKeyObjectValueMapXmlAdapter.class)
    private Map<String, Object> contentMap = null;

    public JaxbContent() {
        // default
    }

    public JaxbContent(Content content) {
        initialize(content);
    }

    @SuppressWarnings(value = "unchecked")
    public void initialize(Content content) {
        if ((content == null) || ((content.getId()) == (-1))) {
            return ;
        }
        this.id = content.getId();
        this.content = content.getContent();
        if (content instanceof JaxbContent) {
            this.contentMap = ((JaxbContent) (content)).getContentMap();
        }else {
            try {
                Object unmarshalledContent = ContentMarshallerHelper.unmarshall(content.getContent(), null);
                if ((unmarshalledContent != null) && (unmarshalledContent instanceof Map)) {
                    contentMap = ((Map<String, Object>) (unmarshalledContent));
                }
            } catch (Exception e) {
                // don't fail in case of unmarshalling problem as it might be content not handled via jaxb
                // Ļe.g. custom classes, non map based etc
            }
        }
    }

    @Override
    public byte[] getContent() {
        return content;
    }

    public byte[] getSerializedContent() {
        return this.content;
    }

    public void setSerializedContent(byte[] content) {
        this.content = content;
    }

    public Map<String, Object> getContentMap() {
        return this.contentMap;
    }

    public void setContentMap(Map<String, Object> map) {
        this.contentMap = map;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        // unsupported Class{Content.class} to void{AbstractJaxbTaskObject}
        AbstractJaxbTaskObject.unsupported(Content.class);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        // unsupported Class{Content.class} to void{AbstractJaxbTaskObject}
        AbstractJaxbTaskObject.unsupported(Content.class);
    }
}

