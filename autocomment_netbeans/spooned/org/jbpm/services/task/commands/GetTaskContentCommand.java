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

import org.kie.internal.command.Context;
import org.kie.api.task.model.Content;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.TaskContentService;
import javax.xml.bind.annotation.XmlAccessType;

@XmlRootElement(name = "get-task-content-command")
@XmlAccessorType(value = XmlAccessType.NONE)
public class GetTaskContentCommand extends TaskCommand<Map<String, Object>> {
    private static final long serialVersionUID = 5911387213149078240L;

    private static final Logger logger = LoggerFactory.getLogger(GetTaskContentCommand.class);

    public GetTaskContentCommand() {
    }

    public GetTaskContentCommand(Long taskId) {
        this.taskId = taskId;
    }

    @SuppressWarnings(value = "unchecked")
    public Map<String, Object> execute(Context cntxt) {
        TaskContext context = ((TaskContext) (cntxt));
        Task taskById = context.getTaskQueryService().getTaskInstanceById(taskId);
        if (taskById == null) {
            throw new IllegalStateException(("Unable to find task with id " + (taskId)));
        }
        TaskContentService contentService = context.getTaskContentService();
        Content contentById = contentService.getContentById(taskById.getTaskData().getDocumentContentId());
        ContentMarshallerContext mContext = contentService.getMarshallerContext(taskById);
        Object unmarshalledObject = ContentMarshallerHelper.unmarshall(contentById.getContent(), mContext.getEnvironment(), mContext.getClassloader());
        if (!(unmarshalledObject instanceof Map)) {
            GetTaskContentCommand.logger.debug(" The Task Content is not of type Map, it was: {} so packaging it into new map under Content key ", unmarshalledObject.getClass());
            Map<String, Object> content = new HashMap<String, Object>();
            content.put("Content", unmarshalledObject);
            return content;
        }
        Map<String, Object> content = ((Map<String, Object>) (unmarshalledObject));
        return content;
    }
}

