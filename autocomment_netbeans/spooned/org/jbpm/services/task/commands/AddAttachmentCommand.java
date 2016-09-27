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

import org.kie.api.task.model.Attachment;
import org.kie.api.task.model.Content;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.kie.internal.command.Context;
import org.kie.internal.task.api.model.InternalAttachment;
import org.kie.internal.task.api.model.InternalContent;
import org.jbpm.services.task.impl.model.xml.JaxbAttachment;
import org.jbpm.services.task.impl.model.xml.JaxbContent;
import org.kie.api.task.model.Task;
import org.jbpm.services.task.impl.TaskContentRegistry;
import org.kie.internal.task.api.TaskModelProvider;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "add-attachment-command")
@XmlAccessorType(value = XmlAccessType.NONE)
public class AddAttachmentCommand extends UserGroupCallbackTaskCommand<Long> {
    private static final long serialVersionUID = -1295175842745522756L;

    @XmlElement
    private JaxbAttachment jaxbAttachment;

    @XmlTransient
    private Attachment attachment;

    @XmlElement
    private JaxbContent jaxbContent;

    @XmlTransient
    private Content content;

    @XmlTransient
    private Object rawContent;

    public AddAttachmentCommand() {
    }

    public AddAttachmentCommand(Long taskId, Attachment attachment, Content content) {
        this.taskId = taskId;
        setAttachment(attachment);
        setContent(content);
    }

    public AddAttachmentCommand(Long taskId, Attachment attachment, Object rawContent) {
        this.taskId = taskId;
        setAttachment(attachment);
        setRawContent(rawContent);
    }

    public Long execute(Context cntxt) {
        TaskContext context = ((TaskContext) (cntxt));
        Attachment attachmentImpl = attachment;
        if (attachmentImpl == null) {
            attachmentImpl = jaxbAttachment;
        } 
        Content contentImpl = content;
        if (contentImpl == null) {
            contentImpl = jaxbContent;
        } 
        if (((rawContent) != null) && (contentImpl == null)) {
            Task task = context.getPersistenceContext().findTask(taskId);
            contentImpl = TaskModelProvider.getFactory().newContent();
            ContentMarshallerContext ctx = TaskContentRegistry.get().getMarshallerContext(task.getTaskData().getDeploymentId());
            ((InternalContent) (contentImpl)).setContent(ContentMarshallerHelper.marshallContent(task, rawContent, ctx.getEnvironment()));
            ((InternalAttachment) (attachmentImpl)).setSize(contentImpl.getContent().length);
        } 
        doCallbackOperationForAttachment(attachmentImpl, context);
        return context.getTaskAttachmentService().addAttachment(taskId, attachmentImpl, contentImpl);
    }

    public void setAttachment(Attachment attachment) {
        AddAttachmentCommand.this.attachment = attachment;
        if (attachment instanceof JaxbAttachment) {
            AddAttachmentCommand.this.jaxbAttachment = ((JaxbAttachment) (attachment));
        } else {
            AddAttachmentCommand.this.jaxbAttachment = new JaxbAttachment(attachment);
        }
    }

    public JaxbAttachment getJaxbAttachment() {
        return jaxbAttachment;
    }

    public void setJaxbAttachment(JaxbAttachment jaxbAttachment) {
        AddAttachmentCommand.this.jaxbAttachment = jaxbAttachment;
    }

    public JaxbContent getJaxbContent() {
        return jaxbContent;
    }

    public void setJaxbContent(JaxbContent jaxbContent) {
        AddAttachmentCommand.this.jaxbContent = jaxbContent;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        AddAttachmentCommand.this.content = content;
        if (content instanceof JaxbContent) {
            AddAttachmentCommand.this.jaxbContent = ((JaxbContent) (content));
        } else {
            AddAttachmentCommand.this.jaxbContent = new JaxbContent(content);
        }
    }

    public Attachment getAttachment() {
        return attachment;
    }

    public Object getRawContent() {
        return rawContent;
    }

    public void setRawContent(Object rawContent) {
        AddAttachmentCommand.this.rawContent = rawContent;
    }
}

