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
import org.jbpm.services.api.model.UserTaskInstanceDesc;

/**
 * Dedicated mapper that transform data set into List of UserTaskInstanceDesc
 */
public class UserTaskInstanceQueryMapper extends AbstractQueryMapper<UserTaskInstanceDesc> implements QueryResultMapper<List<UserTaskInstanceDesc>> {
    private static final long serialVersionUID = 5935133069234696712L;

    /**
     * Dedicated for ServiceLoader to create instance, use <code>get()</code> method instead
     */
    public UserTaskInstanceQueryMapper() {
        super();
    }

    public static UserTaskInstanceQueryMapper get() {
        return new UserTaskInstanceQueryMapper();
    }

    @Override
    public List<UserTaskInstanceDesc> map(Object result) {
        if (result instanceof DataSet) {
            DataSet dataSetResult = ((DataSet) (result));
            List<UserTaskInstanceDesc> mappedResult = new ArrayList<UserTaskInstanceDesc>();
            if (dataSetResult != null) {
                for (int i = 0; i < (dataSetResult.getRowCount()); i++) {
                    UserTaskInstanceDesc ut = buildInstance(dataSetResult, i);
                    mappedResult.add(ut);
                }
            } 
            return mappedResult;
        } 
        throw new IllegalArgumentException(("Unsupported result for mapping " + result));
    }

    @Override
    protected UserTaskInstanceDesc buildInstance(DataSet dataSetResult, int index) {
        UserTaskInstanceDesc userTask = // taskId,
        // status,
        // activationTime,
        // name,
        // description,
        // priority,
        // actualOwner,
        // createdBy,
        // deploymentId,
        // processId,
        // processInstanceId,
        // createdOn,
        // dueDate
        new org.jbpm.kie.services.impl.model.UserTaskInstanceDesc(getColumnLongValue(dataSetResult, COLUMN_TASKID, index), getColumnStringValue(dataSetResult, COLUMN_TASK_STATUS, index), getColumnDateValue(dataSetResult, COLUMN_ACTIVATIONTIME, index), getColumnStringValue(dataSetResult, COLUMN_NAME, index), getColumnStringValue(dataSetResult, COLUMN_DESCRIPTION, index), getColumnIntValue(dataSetResult, COLUMN_PRIORITY, index), getColumnStringValue(dataSetResult, COLUMN_ACTUALOWNER, index), getColumnStringValue(dataSetResult, COLUMN_CREATEDBY, index), getColumnStringValue(dataSetResult, COLUMN_DEPLOYMENTID, index), getColumnStringValue(dataSetResult, COLUMN_TASK_PROCESSID, index), getColumnLongValue(dataSetResult, COLUMN_TASK_PROCESSINSTANCEID, index), getColumnDateValue(dataSetResult, COLUMN_CREATEDON, index), getColumnDateValue(dataSetResult, COLUMN_DUEDATE, index));
        return userTask;
    }

    @Override
    public String getName() {
        return "UserTasks";
    }

    @Override
    public Class<?> getType() {
        return UserTaskInstanceDesc.class;
    }

    @Override
    public org.jbpm.kie.services.impl.query.mapper.QueryResultMapper<List<UserTaskInstanceDesc>> forColumnMapping(Map<String, String> columnMapping) {
        return new UserTaskInstanceQueryMapper();
    }
}

