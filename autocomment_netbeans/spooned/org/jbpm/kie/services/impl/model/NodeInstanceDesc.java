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


package org.jbpm.kie.services.impl.model;

import java.util.Date;
import java.io.Serializable;

public class NodeInstanceDesc implements Serializable , org.jbpm.services.api.model.NodeInstanceDesc {
    private static final long serialVersionUID = -5724814793988493958L;

    private long id;

    private String nodeId;

    private String name;

    private String deploymentId;

    private long processInstanceId;

    private String nodeType;

    private String connection;

    private int type;

    private Date dataTimeStamp;

    private Long workItemId;

    public NodeInstanceDesc() {
        // default constructor
    }

    public NodeInstanceDesc(String id, String nodeId, String name, String nodeType, String deploymentId, long processInstanceId, Date date, String connection, int type, Long workItemId) {
        NodeInstanceDesc.this.id = Long.parseLong(id);
        NodeInstanceDesc.this.name = name;
        NodeInstanceDesc.this.nodeId = nodeId;
        NodeInstanceDesc.this.nodeType = nodeType;
        NodeInstanceDesc.this.deploymentId = deploymentId;
        NodeInstanceDesc.this.processInstanceId = processInstanceId;
        NodeInstanceDesc.this.dataTimeStamp = date;
        NodeInstanceDesc.this.connection = connection;
        NodeInstanceDesc.this.type = type;
        NodeInstanceDesc.this.workItemId = workItemId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public Date getDataTimeStamp() {
        return dataTimeStamp;
    }

    public String getNodeType() {
        return nodeType;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeUniqueId) {
        NodeInstanceDesc.this.nodeId = nodeUniqueId;
    }

    public boolean isCompleted() {
        return (NodeInstanceDesc.this.type) == 1;
    }

    @Override
    public String toString() {
        return (((((((((((((((((("NodeInstanceDesc{" + "id=") + (id)) + ", nodeId=") + (nodeId)) + ", nodeUniqueId=") + (nodeId)) + ", name=") + (name)) + ", deploymentId=") + (deploymentId)) + ", processInstanceId=") + (processInstanceId)) + ", type=") + (nodeType)) + ", completed=") + (isCompleted())) + ", dataTimeStamp=") + (dataTimeStamp)) + '}';
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String incomingConnection) {
        NodeInstanceDesc.this.connection = incomingConnection;
    }

    public int getType() {
        return type;
    }

    public Long getWorkItemId() {
        return workItemId;
    }

    public void setWorkItemId(Long workItemId) {
        NodeInstanceDesc.this.workItemId = workItemId;
    }
}

