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

import java.util.ArrayList;
import java.util.Arrays;
import org.kie.internal.command.Context;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

/**
 * This command will be deleted as of jBPM 7.x.
 * @see {@link TaskService#taskQuery()}
 */
@XmlRootElement(name = "get-tasks-by-various-fields-command")
@XmlAccessorType(value = XmlAccessType.NONE)
@Deprecated
public class GetTasksByVariousFieldsCommand extends UserGroupCallbackTaskCommand<List<TaskSummary>> {
    private static final long serialVersionUID = -4894264083829084547L;

    @XmlElement
    private List<Long> workItemIds;

    @XmlElement
    private List<Long> taskIds;

    @XmlElement
    private List<Long> processInstanceIds;

    @XmlElement
    private List<String> businessAdmins;

    @XmlElement
    private List<String> potentialOwners;

    @XmlElement
    private List<String> taskOwners;

    @XmlElement
    private List<Status> statuses;

    @XmlElement
    @XmlSchemaType(name = "boolean")
    private Boolean union;

    @XmlElement
    private List<String> languages;

    @XmlElement
    private Integer maxResults;

    public GetTasksByVariousFieldsCommand() {
        // default for JAXB
    }

    public GetTasksByVariousFieldsCommand(List<Long> workItemIds, List<Long> taskIds, List<Long> procInstIds, List<String> busAdmins, List<String> potOwners, List<String> taskOwners, List<Status> statuses, boolean union) {
        this(workItemIds, taskIds, procInstIds, busAdmins, potOwners, taskOwners, statuses, null, union);
    }

    public GetTasksByVariousFieldsCommand(List<Long> workItemIds, List<Long> taskIds, List<Long> procInstIds, List<String> busAdmins, List<String> potOwners, List<String> taskOwners, List<Status> statuses, List<String> language, boolean union) {
        this(workItemIds, taskIds, procInstIds, busAdmins, potOwners, taskOwners, statuses, language, union, null);
    }

    public GetTasksByVariousFieldsCommand(List<Long> workItemIds, List<Long> taskIds, List<Long> procInstIds, List<String> busAdmins, List<String> potOwners, List<String> taskOwners, List<Status> statuses, List<String> language, boolean union, Integer maxResults) {
        GetTasksByVariousFieldsCommand.this.workItemIds = workItemIds;
        GetTasksByVariousFieldsCommand.this.taskIds = taskIds;
        GetTasksByVariousFieldsCommand.this.processInstanceIds = procInstIds;
        GetTasksByVariousFieldsCommand.this.businessAdmins = busAdmins;
        GetTasksByVariousFieldsCommand.this.potentialOwners = potOwners;
        GetTasksByVariousFieldsCommand.this.taskOwners = taskOwners;
        GetTasksByVariousFieldsCommand.this.statuses = statuses;
        GetTasksByVariousFieldsCommand.this.languages = language;
        GetTasksByVariousFieldsCommand.this.union = union;
        GetTasksByVariousFieldsCommand.this.maxResults = maxResults;
    }

    public GetTasksByVariousFieldsCommand(Map<String, List<?>> params, boolean union) {
        this(params, union, null);
    }

    @SuppressWarnings(value = "unchecked")
    public GetTasksByVariousFieldsCommand(Map<String, List<?>> params, boolean union, Integer maxResults) {
        GetTasksByVariousFieldsCommand.this.union = union;
        GetTasksByVariousFieldsCommand.this.maxResults = maxResults;
        if (params == null) {
            params = new HashMap<String, List<?>>();
        } else {
            GetTasksByVariousFieldsCommand.this.workItemIds = ((List<Long>) (params.get(WORK_ITEM_ID_LIST)));
            GetTasksByVariousFieldsCommand.this.taskIds = ((List<Long>) (params.get(TASK_ID_LIST)));
            GetTasksByVariousFieldsCommand.this.processInstanceIds = ((List<Long>) (params.get(PROCESS_INSTANCE_ID_LIST)));
            GetTasksByVariousFieldsCommand.this.businessAdmins = ((List<String>) (params.get(BUSINESS_ADMIN_ID_LIST)));
            GetTasksByVariousFieldsCommand.this.potentialOwners = ((List<String>) (params.get(POTENTIAL_OWNER_ID_LIST)));
            GetTasksByVariousFieldsCommand.this.taskOwners = ((List<String>) (params.get(ACTUAL_OWNER_ID_LIST)));
            GetTasksByVariousFieldsCommand.this.statuses = ((List<Status>) (params.get(TASK_STATUS_LIST)));
        }
    }

    public List<Long> getWorkItemIds() {
        return workItemIds;
    }

    public void setWorkItemIds(List<Long> workItemIds) {
        GetTasksByVariousFieldsCommand.this.workItemIds = workItemIds;
    }

    public List<Long> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<Long> taskIds) {
        GetTasksByVariousFieldsCommand.this.taskIds = taskIds;
    }

    public List<Long> getProcInstIds() {
        return processInstanceIds;
    }

    public void setProcInstIds(List<Long> procInstIds) {
        GetTasksByVariousFieldsCommand.this.processInstanceIds = procInstIds;
    }

    public List<String> getBusAdmins() {
        return businessAdmins;
    }

    public void setBusAdmins(List<String> busAdmins) {
        GetTasksByVariousFieldsCommand.this.businessAdmins = busAdmins;
    }

    public List<String> getPotOwners() {
        return potentialOwners;
    }

    public void setPotOwners(List<String> potOwners) {
        GetTasksByVariousFieldsCommand.this.potentialOwners = potOwners;
    }

    public List<String> getTaskOwners() {
        return taskOwners;
    }

    public void setTaskOwners(List<String> taskOwners) {
        GetTasksByVariousFieldsCommand.this.taskOwners = taskOwners;
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<Status> statuses) {
        GetTasksByVariousFieldsCommand.this.statuses = statuses;
    }

    public List<String> getLanguage() {
        return languages;
    }

    public void setLanguage(List<String> language) {
        GetTasksByVariousFieldsCommand.this.languages = language;
    }

    public Boolean getUnion() {
        return union;
    }

    public void setUnion(Boolean union) {
        GetTasksByVariousFieldsCommand.this.union = union;
    }

    public Integer getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(Integer maxResults) {
        GetTasksByVariousFieldsCommand.this.maxResults = maxResults;
    }

    public List<TaskSummary> execute(Context cntxt) {
        TaskContext context = ((TaskContext) (cntxt));
        potentialOwners = populateOrganizationalEntityWithGroupInfo(potentialOwners, context);
        businessAdmins = populateOrganizationalEntityWithGroupInfo(businessAdmins, context);
        List<String> stakeHolders = new ArrayList<String>();
        stakeHolders = populateOrganizationalEntityWithGroupInfo(stakeHolders, context);
        Map<String, List<?>> params = new HashMap<String, List<?>>();
        params.put(WORK_ITEM_ID_LIST, workItemIds);
        params.put(TASK_ID_LIST, taskIds);
        params.put(PROCESS_INSTANCE_ID_LIST, processInstanceIds);
        params.put(BUSINESS_ADMIN_ID_LIST, businessAdmins);
        params.put(POTENTIAL_OWNER_ID_LIST, potentialOwners);
        params.put(STAKEHOLDER_ID_LIST, stakeHolders);
        params.put(ACTUAL_OWNER_ID_LIST, taskOwners);
        params.put(TASK_STATUS_LIST, statuses);
        if (((maxResults) != null) && ((maxResults.intValue()) > 0)) {
            Integer[] maxResultsArr = new Integer[]{ maxResults };
            params.put(MAX_RESULTS, Arrays.asList(maxResultsArr));
        } 
        if (((userId) == null) || (userId.isEmpty())) {
            throw new IllegalStateException(("A user id is required for this operation: " + (GetTasksByVariousFieldsCommand.class.getSimpleName())));
        } 
        return context.getTaskQueryService().getTasksByVariousFields(userId, params, union);
    }

    /**
     * Populates given list with group information taken from UserGroupCallback implementation
     * to allow proper query for tasks based on user assignments.
     * @param entities - "raw" list of organizational entities
     * @return if list is not null and not empty returns list of org entities populated with group info, otherwise same as argument
     */
    protected List<String> populateOrganizationalEntityWithGroupInfo(List<String> entities, TaskContext context) {
        if ((entities != null) && ((entities.size()) > 0)) {
            Set<String> groupIds = new HashSet<String>();
            for (String userId : entities) {
                List<String> tmp = doUserGroupCallbackOperation(userId, null, context);
                if (tmp != null) {
                    groupIds.addAll(tmp);
                } 
            }
            groupIds.addAll(entities);
            return new ArrayList<String>(groupIds);
        } 
        return entities;
    }
}

