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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jbpm.services.api.query.QueryResultMapper;
import org.jbpm.services.api.model.UserTaskInstanceWithVarsDesc;
import org.jbpm.services.api.query.org.jbpm.kie.services.impl.query.mapper.QueryResultMapper;

/**
 * Dedicated mapper that transforms data set into UserTaskInstanceWithVarsDesc based on given mapping of custom variables
 */
public class UserTaskInstanceWithCustomVarsQueryMapper extends AbstractQueryMapper<UserTaskInstanceWithVarsDesc> implements QueryResultMapper<List<UserTaskInstanceWithVarsDesc>> {
    private static final long serialVersionUID = 5935133069234696711L;

    private Map<String, String> variablesMap = new HashMap<String, String>();

    public UserTaskInstanceWithCustomVarsQueryMapper() {
    }

    public UserTaskInstanceWithCustomVarsQueryMapper(Map<String, String> variablesMap) {
        super();
        this.variablesMap = variablesMap;
    }

    public static UserTaskInstanceWithCustomVarsQueryMapper get(Map<String, String> variablesMap) {
        return new UserTaskInstanceWithCustomVarsQueryMapper(variablesMap);
    }

    @Override
    public List<UserTaskInstanceWithVarsDesc> map(Object result) {
        if (result instanceof DataSet) {
            DataSet dataSetResult = ((DataSet) (result));
            List<UserTaskInstanceWithVarsDesc> mappedResult = new ArrayList<UserTaskInstanceWithVarsDesc>();
            if (dataSetResult != null) {
                Map<Long, UserTaskInstanceWithVarsDesc> tmp = new HashMap<Long, UserTaskInstanceWithVarsDesc>();
                for (int i = 0; i < (dataSetResult.getRowCount()); i++) {
                    Long taskId = getColumnLongValue(dataSetResult, COLUMN_TASKID, i);
                    UserTaskInstanceWithVarsDesc ut = tmp.get(taskId);
                    if (ut == null) {
                        ut = buildInstance(dataSetResult, i);
                        mappedResult.add(ut);
                        tmp.put(taskId, ut);
                    }
                    Map<String, Object> variables = readVariables(variablesMap, dataSetResult, i);
                    ((UserTaskInstanceWithVarsDesc) (ut)).setVariables(variables);
                }
            }
            return mappedResult;
        }
        throw new IllegalArgumentException(("Unsupported result for mapping " + result));
    }

    @Override
    protected UserTaskInstanceWithVarsDesc buildInstance(DataSet dataSetResult, int index) {
        // taskId,
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
        UserTaskInstanceWithVarsDesc userTask = new UserTaskInstanceWithVarsDesc(getColumnLongValue(dataSetResult, COLUMN_TASKID, index), getColumnStringValue(dataSetResult, COLUMN_TASK_STATUS, index), getColumnDateValue(dataSetResult, COLUMN_ACTIVATIONTIME, index), getColumnStringValue(dataSetResult, COLUMN_NAME, index), getColumnStringValue(dataSetResult, COLUMN_DESCRIPTION, index), getColumnIntValue(dataSetResult, COLUMN_PRIORITY, index), getColumnStringValue(dataSetResult, COLUMN_ACTUALOWNER, index), getColumnStringValue(dataSetResult, COLUMN_CREATEDBY, index), getColumnStringValue(dataSetResult, COLUMN_DEPLOYMENTID, index), getColumnStringValue(dataSetResult, COLUMN_TASK_PROCESSID, index), getColumnLongValue(dataSetResult, COLUMN_TASK_PROCESSINSTANCEID, index), getColumnDateValue(dataSetResult, COLUMN_CREATEDON, index), getColumnDateValue(dataSetResult, COLUMN_DUEDATE, index));
        return userTask;
    }

    @Override
    public String getName() {
        return "UserTasksWithCustomVariables";
    }

    @Override
    public Class<?> getType() {
        return UserTaskInstanceWithVarsDesc.class;
    }

    @Override
    public org.jbpm.kie.services.impl.query.mapper.QueryResultMapper<List<UserTaskInstanceWithVarsDesc>> forColumnMapping(Map<String, String> columnMapping) {
        return new UserTaskInstanceWithCustomVarsQueryMapper(columnMapping);
    }
}

