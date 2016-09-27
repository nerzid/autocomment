/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.kie.services.impl.query.mapper;

import java.util.ArrayList;
import org.dashbuilder.dataset.DataSet;
import java.util.List;
import java.util.Map;
import org.jbpm.services.api.query.QueryResultMapper;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;

/**
 * Dedicated mapper that transform data set into List of TaskSummary
 */
public class TaskSummaryQueryMapper extends AbstractQueryMapper<TaskSummary> implements QueryResultMapper<List<TaskSummary>> {
    private static final long serialVersionUID = 5935133069234696712L;

    /**
     * Dedicated for ServiceLoader to create instance, use <code>get()</code> method instead
     */
    public TaskSummaryQueryMapper() {
        super();
    }

    public static TaskSummaryQueryMapper get() {
        return new TaskSummaryQueryMapper();
    }

    @Override
    public List<TaskSummary> map(Object result) {
        if (result instanceof DataSet) {
            DataSet dataSetResult = ((DataSet) (result));
            List<TaskSummary> mappedResult = new ArrayList<TaskSummary>();
            if (dataSetResult != null) {
                for (int i = 0; i < (dataSetResult.getRowCount()); i++) {
                    TaskSummary ut = buildInstance(dataSetResult, i);
                    mappedResult.add(ut);
                }
            } 
            return mappedResult;
        } 
        throw new IllegalArgumentException(("Unsupported result for mapping " + result));
    }

    @Override
    protected TaskSummary buildInstance(DataSet dataSetResult, int index) {
        TaskSummary userTask = // taskId,
        // name,
        // description,
        // status,
        // priority,
        // actualOwner,
        // createdBy,
        // createdOn,
        // activationTime,
        // dueDate
        // processId,
        // processInstanceId,
        // deploymentId,
        new org.jbpm.services.task.query.TaskSummaryImpl(getColumnLongValue(dataSetResult, COLUMN_TASKID, index), getColumnStringValue(dataSetResult, COLUMN_NAME, index), getColumnStringValue(dataSetResult, COLUMN_DESCRIPTION, index), Status.valueOf(getColumnStringValue(dataSetResult, COLUMN_TASK_STATUS, index)), getColumnIntValue(dataSetResult, COLUMN_PRIORITY, index), getColumnStringValue(dataSetResult, COLUMN_ACTUALOWNER, index), getColumnStringValue(dataSetResult, COLUMN_CREATEDBY, index), getColumnDateValue(dataSetResult, COLUMN_CREATEDON, index), getColumnDateValue(dataSetResult, COLUMN_ACTIVATIONTIME, index), getColumnDateValue(dataSetResult, COLUMN_DUEDATE, index), getColumnStringValue(dataSetResult, COLUMN_TASK_PROCESSID, index), getColumnLongValue(dataSetResult, COLUMN_TASK_PROCESSINSTANCEID, index), (-1L), getColumnStringValue(dataSetResult, COLUMN_DEPLOYMENTID, index), false);
        return userTask;
    }

    @Override
    public String getName() {
        return "TaskSummaries";
    }

    @Override
    public Class<?> getType() {
        return TaskSummary.class;
    }

    @Override
    public org.jbpm.kie.services.impl.query.mapper.QueryResultMapper<List<TaskSummary>> forColumnMapping(Map<String, String> columnMapping) {
        return new TaskSummaryQueryMapper();
    }
}

