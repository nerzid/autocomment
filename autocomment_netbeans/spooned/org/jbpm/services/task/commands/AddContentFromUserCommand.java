/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.jbpm.services.task.commands;

import org.drools.core.xml.jaxb.util.JaxbMapAdapter;
import org.kie.internal.command.Context;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

@XmlRootElement(name = "add-content-from-user-command")
@XmlAccessorType(value = XmlAccessType.NONE)
public class AddContentFromUserCommand extends UserGroupCallbackTaskCommand<Long> {
    private static final long serialVersionUID = -1295175858745522756L;

    @XmlElement(name = "document-content-bytes")
    @XmlSchemaType(name = "base64Binary")
    private byte[] documentContentBytes = null;

    @XmlJavaTypeAdapter(value = JaxbMapAdapter.class)
    @XmlElement(name = "output-content-map")
    private Map<String, Object> outputContentMap = null;

    public AddContentFromUserCommand() {
        // default JAXB constructor
    }

    public AddContentFromUserCommand(long taskId, String userId) {
        setTaskId(taskId);
        setUserId(userId);
    }

    public byte[] getDocumentContentBytes() {
        return documentContentBytes;
    }

    public void setDocumentContentBytes(byte[] documentContentBytes) {
        this.documentContentBytes = documentContentBytes;
    }

    public Map<String, Object> getOutputContentMap() {
        if ((this.outputContentMap) == null) {
            this.outputContentMap = new HashMap<String, Object>();
        }
        return outputContentMap;
    }

    public void setOutputContentMap(Map<String, Object> outputContentMap) {
        this.outputContentMap = outputContentMap;
    }

    public Long execute(Context cntxt) {
        TaskContext context = ((TaskContext) (cntxt));
        // do callback AddContentFromUserCommand{userId} to AddContentFromUserCommand{}
        doCallbackUserOperation(userId, context);
        groupIds = doUserGroupCallbackOperation(userId, null, context);
        // set String{"local:groups"} to TaskContext{context}
        context.set("local:groups", groupIds);
        // TODO!
        // return context.getTaskInstanceService().setDocumentContentFromUser(taskId, userId, documentContentBytes);
        if ((outputContentMap) != null) {
            return context.getTaskInstanceService().addOutputContentFromUser(taskId, userId, outputContentMap);
        }// TODO!
        // return context.getTaskInstanceService().setDocumentContentFromUser(taskId, userId, documentContentBytes);
        else
            if ((documentContentBytes) != null) {
            }
        
        return -1L;
    }
}

