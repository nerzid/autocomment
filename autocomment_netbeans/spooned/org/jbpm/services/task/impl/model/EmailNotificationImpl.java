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

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import org.kie.internal.task.api.model.EmailNotification;
import org.kie.internal.task.api.model.EmailNotificationHeader;
import javax.persistence.Entity;
import java.util.HashMap;
import java.io.IOException;
import org.kie.internal.task.api.model.Language;
import java.util.Map;
import javax.persistence.MapKeyColumn;
import org.kie.internal.task.api.model.NotificationType;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javax.persistence.OneToMany;

@Entity
@DiscriminatorValue(value = "EmailNotification")
public class EmailNotificationImpl extends NotificationImpl implements EmailNotification {
    @OneToMany(cascade = CascadeType.ALL)
    @MapKeyColumn(name = "mapkey")
    private Map<LanguageImpl, EmailNotificationHeaderImpl> emailHeaders;

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        if ((emailHeaders) != null) {
            out.writeInt(emailHeaders.size());
            for (EmailNotificationHeader header : emailHeaders.values()) {
                header.writeExternal(out);
            }
        } else {
            out.writeInt(0);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        int size = in.readInt();
        if (size > 0) {
            emailHeaders = new HashMap<LanguageImpl, EmailNotificationHeaderImpl>(size);
            for (int i = 0; i < size; i++) {
                EmailNotificationHeaderImpl header = new EmailNotificationHeaderImpl();
                header.readExternal(in);
                emailHeaders.put(new LanguageImpl(header.getLanguage()), header);
            }
        } 
    }

    public NotificationType getNotificationType() {
        return NotificationType.Email;
    }

    public Map<? extends Language, ? extends EmailNotificationHeader> getEmailHeaders() {
        return emailHeaders;
    }

    public void setEmailHeaders(Map<? extends Language, ? extends EmailNotificationHeader> emailHeaders) {
        EmailNotificationImpl.this.emailHeaders = ((Map<LanguageImpl, EmailNotificationHeaderImpl>) (emailHeaders));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = (prime * result) + ((emailHeaders) == null ? 0 : emailHeaders.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ((EmailNotificationImpl.this) == obj)
            return true;
        
        if (!(super.equals(obj)))
            return false;
        
        if (!(obj instanceof EmailNotificationImpl))
            return false;
        
        EmailNotificationImpl other = ((EmailNotificationImpl) (obj));
        if ((emailHeaders) == null) {
            if ((other.emailHeaders) != null)
                return false;
            
        } else if (!(emailHeaders.equals(other.emailHeaders)))
            return false;
        
        return true;
    }
}

