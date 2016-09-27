/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.services.task.commands;

import org.kie.internal.command.Context;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "delete-attachment-command")
@XmlAccessorType(value = XmlAccessType.NONE)
public class DeleteAttachmentCommand extends TaskCommand<Void> {
    private static final long serialVersionUID = 5656199063761548979L;

    @XmlElement
    private Long attachmentId;

    public DeleteAttachmentCommand() {
    }

    public DeleteAttachmentCommand(long taskId, Long attachmentId) {
        this.taskId = taskId;
        DeleteAttachmentCommand.this.attachmentId = attachmentId;
    }

    public Void execute(Context cntxt) {
        TaskContext context = ((TaskContext) (cntxt));
        context.getTaskAttachmentService().deleteAttachment(taskId, attachmentId);
        return null;
    }

    public Long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Long contentId) {
        DeleteAttachmentCommand.this.attachmentId = contentId;
    }
}
