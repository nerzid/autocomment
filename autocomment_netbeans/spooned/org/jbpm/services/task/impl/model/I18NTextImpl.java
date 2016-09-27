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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.IOException;
import javax.persistence.Id;
import org.kie.internal.task.api.model.InternalI18NText;
import java.util.List;
import javax.persistence.Lob;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "I18NText")
@SequenceGenerator(allocationSize = 1, name = "i18nTextIdSeq", sequenceName = "I18NTEXT_ID_SEQ")
public class I18NTextImpl implements InternalI18NText {
    @Id
    @GeneratedValue(generator = "i18nTextIdSeq", strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id = null;

    private String language;

    private String shortText;

    @Lob
    @Column(length = 65535)
    private String text;

    public I18NTextImpl() {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(id);
        if ((language) == null) {
            language = "";
        } 
        out.writeUTF(language);
        if ((shortText) == null) {
            shortText = "";
        } 
        out.writeUTF(shortText);
        if ((text) == null) {
            text = "";
        } 
        out.writeUTF(text);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        id = in.readLong();
        language = in.readUTF();
        shortText = in.readUTF();
        text = in.readUTF();
    }

    public I18NTextImpl(String language, String text) {
        I18NTextImpl.this.language = language;
        if ((text != null) && ((text.length()) > 256)) {
            I18NTextImpl.this.shortText = text.substring(0, 255);
        } else {
            I18NTextImpl.this.shortText = text;
        }
        I18NTextImpl.this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        I18NTextImpl.this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        I18NTextImpl.this.language = language;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if ((text != null) && ((text.length()) > 256)) {
            I18NTextImpl.this.shortText = text.substring(0, 255);
        } else {
            I18NTextImpl.this.shortText = text;
        }
        I18NTextImpl.this.text = text;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((language) == null ? 0 : language.hashCode());
        result = (prime * result) + ((shortText) == null ? 0 : shortText.hashCode());
        result = (prime * result) + ((text) == null ? 0 : text.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ((I18NTextImpl.this) == obj)
            return true;
        
        if (obj == null)
            return false;
        
        if (!(obj instanceof I18NTextImpl))
            return false;
        
        I18NTextImpl other = ((I18NTextImpl) (obj));
        if ((language) == null) {
            if ((other.language) != null)
                return false;
            
        } else if (!(language.equals(other.language)))
            return false;
        
        if ((shortText) == null) {
            if ((other.shortText) != null)
                return false;
            
        } else if (!(shortText.equals(other.shortText)))
            return false;
        
        if ((text) == null) {
            if ((other.text) != null)
                return false;
            
        } else if (!(text.equals(other.text)))
            return false;
        
        return true;
    }

    public static String getLocalText(List<I18NTextImpl> list, String prefferedLanguage, String defaultLanguage) {
        for (I18NTextImpl text : list) {
            if (text.getLanguage().equals(prefferedLanguage)) {
                return text.getText();
            } 
        }
        if (defaultLanguage == null) {
            for (I18NTextImpl text : list) {
                if (text.getLanguage().equals(defaultLanguage)) {
                    return text.getText();
                } 
            }
        } 
        return "";
    }
}

