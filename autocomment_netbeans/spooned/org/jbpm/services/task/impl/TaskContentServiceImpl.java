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


package org.jbpm.services.task.impl;

import java.util.ArrayList;
import org.kie.api.task.model.Content;
import org.kie.internal.task.api.model.ContentData;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import java.util.HashMap;
import org.kie.internal.task.api.model.InternalContent;
import org.kie.internal.task.api.model.InternalTaskData;
import java.util.List;
import java.util.Map;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.TaskContentService;
import org.kie.internal.task.api.TaskContext;
import org.jbpm.services.task.events.TaskEventSupport;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.TaskPersistenceContext;

/**
 */
public class TaskContentServiceImpl implements TaskContentService {
    private TaskPersistenceContext persistenceContext;

    private TaskEventSupport taskEventSupport;

    private TaskContext context;

    public TaskContentServiceImpl() {
    }

    public TaskContentServiceImpl(TaskContext context, TaskPersistenceContext persistenceContext, TaskEventSupport taskEventSupport) {
        TaskContentServiceImpl.this.context = context;
        TaskContentServiceImpl.this.persistenceContext = persistenceContext;
        TaskContentServiceImpl.this.taskEventSupport = taskEventSupport;
    }

    public void setPersistenceContext(TaskPersistenceContext persistenceContext) {
        TaskContentServiceImpl.this.persistenceContext = persistenceContext;
    }

    public void setTaskEventSupport(TaskEventSupport taskEventSupport) {
        TaskContentServiceImpl.this.taskEventSupport = taskEventSupport;
    }

    @SuppressWarnings(value = "unchecked")
    public long addOutputContent(long taskId, Map<String, Object> params) {
        Task task = persistenceContext.findTask(taskId);
        long outputContentId = task.getTaskData().getOutputContentId();
        Content outputContent = persistenceContext.findContent(outputContentId);
        long contentId = -1;
        if (outputContent == null) {
            ContentMarshallerContext context = getMarshallerContext(task);
            ContentData outputContentData = ContentMarshallerHelper.marshal(task, params, context.getEnvironment());
            Content content = TaskModelProvider.getFactory().newContent();
            ((InternalContent) (content)).setContent(outputContentData.getContent());
            persistenceContext.persistContent(content);
            ((InternalTaskData) (task.getTaskData())).setOutput(content.getId(), outputContentData);
            contentId = content.getId();
        } else {
            // I need to merge it if it already exist
            ContentMarshallerContext context = getMarshallerContext(task);
            Object unmarshalledObject = ContentMarshallerHelper.unmarshall(outputContent.getContent(), context.getEnvironment(), context.getClassloader());
            if ((unmarshalledObject != null) && (unmarshalledObject instanceof Map)) {
                ((Map<String, Object>) (unmarshalledObject)).putAll(params);
            } 
            ContentData outputContentData = ContentMarshallerHelper.marshal(task, unmarshalledObject, context.getEnvironment());
            ((InternalContent) (outputContent)).setContent(outputContentData.getContent());
            persistenceContext.persistContent(outputContent);
            contentId = outputContentId;
        }
        ((InternalTaskData) (task.getTaskData())).setTaskOutputVariables(params);
        taskEventSupport.fireAfterTaskOutputVariablesChanged(task, context, params);
        return contentId;
    }

    // TODO: if there's an existing document content entity, we lose all link to that through this!
    public long setDocumentContent(long taskId, Content content) {
        Task task = persistenceContext.findTask(taskId);
        persistenceContext.persistContent(content);
        ((InternalTaskData) (task.getTaskData())).setDocumentContentId(content.getId());
        return content.getId();
    }

    public void deleteDocumentContent(long taskId, long contentId) {
        Task task = persistenceContext.findTask(taskId);
        ((InternalTaskData) (task.getTaskData())).setDocumentContentId((-1));
        Content content = persistenceContext.findContent(contentId);
        persistenceContext.removeContent(content);
    }

    public List<Content> getAllContentByTaskId(long taskId) {
        Task task = persistenceContext.findTask(taskId);
        long inputContentId = task.getTaskData().getDocumentContentId();
        long outputContentId = task.getTaskData().getOutputContentId();
        long faultContentId = task.getTaskData().getFaultContentId();
        List<Content> allContent = new ArrayList<Content>();
        allContent.add(persistenceContext.findContent(inputContentId));
        allContent.add(persistenceContext.findContent(outputContentId));
        allContent.add(persistenceContext.findContent(faultContentId));
        return allContent;
    }

    public Content getContentById(long contentId) {
        return persistenceContext.findContent(contentId);
    }

    @Override
    public void addMarshallerContext(String ownerId, ContentMarshallerContext context) {
        TaskContentRegistry.get().addMarshallerContext(ownerId, context);
    }

    @Override
    public void removeMarshallerContext(String ownerId) {
        TaskContentRegistry.get().removeMarshallerContext(ownerId);
    }

    public ContentMarshallerContext getMarshallerContext(Task task) {
        return TaskContentRegistry.get().getMarshallerContext(task);
    }

    @Override
    public Task loadTaskVariables(Task task) {
        // load input
        if ((task.getTaskData().getTaskInputVariables()) == null) {
            Map<String, Object> input = loadContentData(task.getTaskData().getDocumentContentId(), task);
            ((InternalTaskData) (task.getTaskData())).setTaskInputVariables(input);
        } 
        // load output
        if ((task.getTaskData().getTaskOutputVariables()) == null) {
            Map<String, Object> output = loadContentData(task.getTaskData().getOutputContentId(), task);
            ((InternalTaskData) (task.getTaskData())).setTaskOutputVariables(output);
        } 
        return task;
    }

    @SuppressWarnings(value = "unchecked")
    protected Map<String, Object> loadContentData(Long contentId, Task task) {
        if (contentId != null) {
            Map<String, Object> data = null;
            Content contentById = getContentById(contentId);
            if (contentById != null) {
                ContentMarshallerContext mContext = getMarshallerContext(task);
                Object unmarshalledObject = ContentMarshallerHelper.unmarshall(contentById.getContent(), mContext.getEnvironment(), mContext.getClassloader());
                if (!(unmarshalledObject instanceof Map)) {
                    data = new HashMap<String, Object>();
                    data.put("Content", unmarshalledObject);
                } else {
                    data = ((Map<String, Object>) (unmarshalledObject));
                }
                return data;
            } 
        } 
        return null;
    }
}

