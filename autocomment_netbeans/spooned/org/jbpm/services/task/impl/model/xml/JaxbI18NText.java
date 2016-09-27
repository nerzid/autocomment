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
import org.kie.api.task.model.I18NText;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

@XmlRootElement(name = "i18n-text")
@XmlAccessorType(value = XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class JaxbI18NText extends AbstractJaxbTaskObject<I18NText> implements I18NText {
    @XmlElement
    @XmlSchemaType(name = "long")
    private Long id;

    @XmlElement
    @XmlSchemaType(name = "string")
    private String text;

    @XmlElement
    @XmlSchemaType(name = "string")
    private String language;

    public JaxbI18NText() {
        super(I18NText.class);
    }

    public JaxbI18NText(I18NText text) {
        super(text, I18NText.class);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        JaxbI18NText.this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        JaxbI18NText.this.text = text;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String lang) {
        JaxbI18NText.this.language = lang;
    }

    public static List<I18NText> convertListFromJaxbImplToInterface(List<JaxbI18NText> jaxbList) {
        List<I18NText> list;
        if (jaxbList != null) {
            list = new ArrayList<I18NText>(jaxbList.size());
            for (JaxbI18NText jaxb : jaxbList) {
                list.add(jaxb.createImplInstance());
            }
        } else {
            list = new ArrayList<I18NText>();
        }
        return list;
    }

    private I18NText createImplInstance() {
        return new org.jbpm.services.task.impl.model.xml.InternalJaxbWrapper.GetterI18NText(JaxbI18NText.this.id, JaxbI18NText.this.language, JaxbI18NText.this.text);
    }
}

