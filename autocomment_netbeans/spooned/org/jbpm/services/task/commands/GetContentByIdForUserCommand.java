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


package org.jbpm.services.task.commands;

import org.kie.api.task.model.Content;
import org.kie.internal.command.Context;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

@XmlRootElement(name = "get-content-by-id-for-user-command")
@XmlAccessorType(value = XmlAccessType.NONE)
public class GetContentByIdForUserCommand extends UserGroupCallbackTaskCommand<Content> {
    private static final long serialVersionUID = 5911387213149078240L;

    @XmlElement
    @XmlSchemaType(name = "long")
    private Long contentId;

    public GetContentByIdForUserCommand() {
    }

    public GetContentByIdForUserCommand(Long contentId) {
        this.contentId = contentId;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public Content execute(Context cntxt) {
        TaskContext context = ((TaskContext) (cntxt));
        // do callback GetContentByIdForUserCommand{userId} to GetContentByIdForUserCommand{}
        doCallbackUserOperation(userId, context);
        groupIds = doUserGroupCallbackOperation(userId, null, context);
        // set String{"local:groups"} to TaskContext{context}
        context.set("local:groups", groupIds);
        return context.getTaskInstanceService().getContentByIdForUser(contentId, userId);
    }
}

