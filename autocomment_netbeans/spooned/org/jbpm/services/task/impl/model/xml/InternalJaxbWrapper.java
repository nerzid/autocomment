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

import org.kie.api.task.model.Group;
import org.kie.api.task.model.I18NText;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.kie.api.task.model.User;

/**
 * This class is used to produce Task API objects.
 *
 * The class (and all subclasses) are deliberately package-scoped because there are interfaces for these objects.
 */
class InternalJaxbWrapper {
    static class GetterUser implements User {
        private final String id;

        public GetterUser(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return this.id;
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            // unsupported Class{User.class} to void{AbstractJaxbTaskObject}
            AbstractJaxbTaskObject.unsupported(User.class);
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            // unsupported Class{User.class} to void{AbstractJaxbTaskObject}
            AbstractJaxbTaskObject.unsupported(User.class);
        }
    }

    static class GetterGroup implements Group {
        private final String id;

        public GetterGroup(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return this.id;
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            // unsupported Class{Group.class} to void{AbstractJaxbTaskObject}
            AbstractJaxbTaskObject.unsupported(Group.class);
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            // unsupported Class{Group.class} to void{AbstractJaxbTaskObject}
            AbstractJaxbTaskObject.unsupported(Group.class);
        }
    }

    static class GetterI18NText implements I18NText {
        private final Long id;

        private final String lang;

        private final String text;

        public GetterI18NText(Long id, String lang, String text) {
            this.id = id;
            this.lang = lang;
            this.text = text;
        }

        @Override
        public Long getId() {
            return this.id;
        }

        @Override
        public String getLanguage() {
            return this.lang;
        }

        @Override
        public String getText() {
            return this.text;
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            // unsupported Class{I18NText.class} to void{AbstractJaxbTaskObject}
            AbstractJaxbTaskObject.unsupported(I18NText.class);
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            // unsupported Class{I18NText.class} to void{AbstractJaxbTaskObject}
            AbstractJaxbTaskObject.unsupported(I18NText.class);
        }
    }
}

