/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.services.api;

import java.util.Collection;
import org.kie.internal.process.CorrelationKey;
import java.util.List;
import java.util.Map;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;

public interface ProcessService {
    /**
     * Starts a process with no variables
     * 
     * @param deploymentId deployment information for the process's kjar
     * @param processId The process's identifier
     * @return process instance identifier
     * @throws RuntimeException in case of encountered errors
     * @throws DeploymentNotFoundException in case deployment with given deployment id does not exist or is not active
     */
    Long startProcess(String deploymentId, String processId);

    /**
     * Starts a process with no variables
     * 
     * @param deploymentId deployment information for the process's kjar
     * @param processId The process's identifier
     * @param params process variables
     * @return process instance identifier
     * @throws RuntimeException in case of encountered errors
     * @throws DeploymentNotFoundException in case deployment with given deployment id does not exist or is not active
     */
    Long startProcess(String deploymentId, String processId, Map<String, Object> params);

    /**
     * Starts a process with no variables
     * 
     * @param deploymentId deployment information for the process's kjar
     * @param processId The process's identifier
     * @param correlationKey correlation key to be assigned to process instance - must be unique
     * @return process instance identifier
     * @throws RuntimeException in case of encountered errors
     * @throws DeploymentNotFoundException in case deployment with given deployment id does not exist or is not active
     */
    Long startProcess(String deploymentId, String processId, CorrelationKey correlationKey);

    /**
     * Starts a process with no variables
     * 
     * @param deploymentId deployment information for the process's kjar
     * @param processId The process's identifier
     * @param correlationKey correlation key to be assigned to process instance - must be unique
     * @param params process variables
     * @return process instance identifier
     * @throws RuntimeException in case of encountered errors
     * @throws DeploymentNotFoundException in case deployment with given deployment id does not exist or is not active
     */
    Long startProcess(String deploymentId, String processId, CorrelationKey correlationKey, Map<String, Object> params);

    /**
     * Aborts the specified process
     * 
     * @param processInstanceId process instance's unique identifier
     * @throws DeploymentNotFoundException in case deployment unit was not found
     * @throws ProcessInstanceNotFoundException in case process instance with given id was not found
     */
    void abortProcessInstance(Long processInstanceId);

    /**
     * Aborts all specified processes
     * 
     * @param processInstanceIds list of process instance unique identifiers
     * @throws DeploymentNotFoundException in case deployment unit was not found
     * @throws ProcessInstanceNotFoundException in case process instance with given id was not found
     */
    void abortProcessInstances(List<Long> processInstanceIds);

    /**
     * Signal an event to a single process instance
     * 
     * @param processInstanceId the process instance's unique identifier
     * @param signalName the signal's id in the process
     * @param event the event object to be passed in with the event
     * @throws DeploymentNotFoundException in case deployment unit was not found
     * @throws ProcessInstanceNotFoundException in case process instance with given id was not found
     */
    void signalProcessInstance(Long processInstanceId, String signalName, Object event);

    /**
     * Signal an event to given list of process instances
     * 
     * @param processInstanceIds list of process instance unique identifiers
     * @param signalName the signal's id in the process
     * @param event the event object to be passed in with the event
     * @throws DeploymentNotFoundException in case deployment unit was not found
     * @throws ProcessInstanceNotFoundException in case process instance with given id was not found
     */
    void signalProcessInstances(List<Long> processInstanceIds, String signalName, Object event);

    /**
     * Signal an event to a any process instance that listens to give signal that belongs to given deployment
     * 
     * @param deployment information for the process's kjar
     * @param signalName the signal's id in the process
     * @param event the event object to be passed in with the event
     * @throws DeploymentNotFoundException in case deployment unit was not found
     */
    void signalEvent(String deployment, String signalName, Object event);

    /**
     * Returns process instance information. Will return null if no
     * active process with that id is found
     * 
     * @param processInstanceId The process instance's unique identifier
     * @return Process instance information
     * @throws DeploymentNotFoundException in case deployment unit was not found
     */
    ProcessInstance getProcessInstance(Long processInstanceId);

    /**
     * Returns process instance information. Will return null if no
     * active process with that correlation key is found
     * 
     * @param correlationKey correlation key assigned to process instance
     * @return Process instance information
     * @throws DeploymentNotFoundException in case deployment unit was not found
     */
    ProcessInstance getProcessInstance(CorrelationKey correlationKey);

    /**
     * Sets a process variable.
     * @param processInstanceId The process instance's unique identifier.
     * @param variableName The variable name to set.
     * @param variable The variable value.
     * @throws DeploymentNotFoundException in case deployment unit was not found
     * @throws ProcessInstanceNotFoundException in case process instance with given id was not found
     */
    void setProcessVariable(Long processInstanceId, String variableId, Object value);

    /**
     * Sets process variables.
     * @param processInstanceId The process instance's unique identifier.
     * @param variables map of process variables (key - variable name, value - variable value)
     * @throws DeploymentNotFoundException in case deployment unit was not found
     * @throws ProcessInstanceNotFoundException in case process instance with given id was not found
     */
    void setProcessVariables(Long processInstanceId, Map<String, Object> variables);

    /**
     * Gets a process instance's variable.
     * @param processInstanceId the process instance's unique identifier.
     * @param variableName the variable name to get from the process.
     * @throws DeploymentNotFoundException in case deployment unit was not found
     * @throws ProcessInstanceNotFoundException in case process instance with given id was not found
     */
    Object getProcessInstanceVariable(Long processInstanceId, String variableName);

    /**
     * Gets a process instance's variable values.
     * @param processInstanceId The process instance's unique identifier.
     * @throws DeploymentNotFoundException in case deployment unit was not found
     * @throws ProcessInstanceNotFoundException in case process instance with given id was not found
     */
    Map<String, Object> getProcessInstanceVariables(Long processInstanceId);

    /**
     * Returns all signals available in current state of given process instance
     * @param processInstanceId process instance id
     * @return list of available signals or empty list if no signals are available
     */
    Collection<String> getAvailableSignals(Long processInstanceId);

    /**
     * Completes the specified WorkItem with the given results
     * 
     * @param id workItem id
     * @param results results of the workItem
     * @throws DeploymentNotFoundException in case deployment unit was not found
     * @throws WorkItemNotFoundException in case work item with given id was not found
     */
    void completeWorkItem(Long id, Map<String, Object> results);

    /**
     * Abort the specified workItem
     * 
     * @param id workItem id
     * @throws DeploymentNotFoundException in case deployment unit was not found
     * @throws WorkItemNotFoundException in case work item with given id was not found
     */
    void abortWorkItem(Long id);

    /**
     * Returns the specified workItem
     * 
     * @param id workItem id
     * @return The specified workItem
     * @throws DeploymentNotFoundException in case deployment unit was not found
     * @throws WorkItemNotFoundException in case work item with given id was not found
     */
    WorkItem getWorkItem(Long id);

    /**
     * Returns active work items by process instance id.
     * 
     * @param processInstanceId process instance id
     * @return The list of active workItems for the process instance
     * @throws DeploymentNotFoundException in case deployment unit was not found
     * @throws ProcessInstanceNotFoundException in case process instance with given id was not found
     */
    List<WorkItem> getWorkItemByProcessInstance(Long processInstanceId);

    /**
     * Executes provided command on the underlying command executor (usually KieSession)
     * @param deploymentId deployment information for the process's kjar
     * @param command actual command for execution
     * @return results of command execution
     * @throws DeploymentNotFoundException in case deployment with given deployment id does not exist
     * or is not active for restricted commands (e.g. start process)
     */
    public <T> T execute(String deploymentId, Command<T> command);

    /**
     * Executes provided command on the underlying command executor (usually KieSession)
     * @param deploymentId deployment information for the process's kjar
     * @param context context implementation to be used to get runtime engine
     * @param command actual command for execution
     * @return results of command execution
     * @throws DeploymentNotFoundException in case deployment with given deployment id does not exist
     * or is not active for restricted commands (e.g. start process)
     */
    public <T> T execute(String deploymentId, Context<?> context, Command<T> command);
}

