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


package org.jbpm.services.task.impl.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.kie.internal.task.api.model.Language;

@Embeddable
public class LanguageImpl implements Language {
    @Column(nullable = false)
    private String mapkey;

    public String getMapkey() {
        return mapkey;
    }

    public void setMapkey(String language) {
        LanguageImpl.this.mapkey = language;
    }

    public LanguageImpl() {
    }

    public LanguageImpl(String lang) {
        LanguageImpl.this.mapkey = lang;
    }

    @Override
    public int hashCode() {
        return (LanguageImpl.this.mapkey) == null ? 0 : LanguageImpl.this.mapkey.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            if ((LanguageImpl.this.mapkey) == null) {
                return true;
            } 
            return false;
        } else if (obj instanceof String) {
            return obj.equals(LanguageImpl.this.mapkey);
        } else if (obj instanceof LanguageImpl) {
            LanguageImpl other = ((LanguageImpl) (obj));
            if ((LanguageImpl.this.mapkey) == null) {
                return (other.mapkey) == null;
            } else {
                return LanguageImpl.this.mapkey.equals(other.mapkey);
            }
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return (mapkey) == null ? null : mapkey.toString();
    }
}

