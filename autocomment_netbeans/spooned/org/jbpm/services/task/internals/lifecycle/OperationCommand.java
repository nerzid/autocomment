/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.services.task.internals.lifecycle;

import java.util.List;
import org.kie.internal.task.api.model.Operation;
import org.kie.api.task.model.Status;

public class OperationCommand {
    private List<Status> status;

    private List<Status> previousStatus;

    private List<Allowed> allowed;

    private Status newStatus;

    private boolean setNewOwnerToUser;

    private boolean setNewOwnerToNull;

    private boolean setToPreviousStatus;

    private boolean userIsExplicitPotentialOwner;

    private boolean addTargetUserToPotentialOwners;

    private boolean removeUserFromPotentialOwners;

    private boolean groupTargetEntityAllowed = true;

    private boolean skipable;

    private Operation exec;

    public OperationCommand() {
        // Default constructor
    }

    public List<Status> getStatus() {
        return status;
    }

    public void setStatus(List<Status> status) {
        OperationCommand.this.status = status;
    }

    public List<Status> getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(List<Status> previousStatus) {
        OperationCommand.this.previousStatus = previousStatus;
    }

    public List<Allowed> getAllowed() {
        return allowed;
    }

    public void setAllowed(List<Allowed> allowed) {
        OperationCommand.this.allowed = allowed;
    }

    public Status getNewStatus() {
        return newStatus;
    }

    public boolean isSetNewOwnerToNull() {
        return setNewOwnerToNull;
    }

    public void setSetNewOwnerToNull(boolean setNewOwnerToNull) {
        OperationCommand.this.setNewOwnerToNull = setNewOwnerToNull;
    }

    public boolean isAddTargetUserToPotentialOwners() {
        return addTargetUserToPotentialOwners;
    }

    public void setNewStatus(Status newStatus) {
        OperationCommand.this.newStatus = newStatus;
    }

    public boolean isSetNewOwnerToUser() {
        return setNewOwnerToUser;
    }

    public void setSetNewOwnerToUser(boolean setNewOwnerToTargetUser) {
        OperationCommand.this.setNewOwnerToUser = setNewOwnerToTargetUser;
    }

    public boolean isSetToPreviousStatus() {
        return setToPreviousStatus;
    }

    public void setSetToPreviousStatus(boolean setToPreviousStatus) {
        OperationCommand.this.setToPreviousStatus = setToPreviousStatus;
    }

    public boolean isUserIsExplicitPotentialOwner() {
        return userIsExplicitPotentialOwner;
    }

    public void setUserIsExplicitPotentialOwner(boolean userIsExplicitPotentialOwner) {
        OperationCommand.this.userIsExplicitPotentialOwner = userIsExplicitPotentialOwner;
    }

    public boolean isAddTargetEntityToPotentialOwners() {
        return addTargetUserToPotentialOwners;
    }

    public void setAddTargetUserToPotentialOwners(boolean addTargetUserToPotentialOwners) {
        OperationCommand.this.addTargetUserToPotentialOwners = addTargetUserToPotentialOwners;
    }

    public boolean isRemoveUserFromPotentialOwners() {
        return removeUserFromPotentialOwners;
    }

    public void setRemoveUserFromPotentialOwners(boolean removeUserFromPotentialOwners) {
        OperationCommand.this.removeUserFromPotentialOwners = removeUserFromPotentialOwners;
    }

    public boolean isSkipable() {
        return skipable;
    }

    public void setSkipable(boolean skippable) {
        OperationCommand.this.skipable = skippable;
    }

    public Operation getExec() {
        return exec;
    }

    public void setExec(Operation exec) {
        OperationCommand.this.exec = exec;
    }

    public boolean isGroupTargetEntityAllowed() {
        return groupTargetEntityAllowed;
    }

    public void setGroupTargetEntityAllowed(boolean groupTargetEntityAllowed) {
        OperationCommand.this.groupTargetEntityAllowed = groupTargetEntityAllowed;
    }
}

