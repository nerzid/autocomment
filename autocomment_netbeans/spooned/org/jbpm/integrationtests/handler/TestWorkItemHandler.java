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


package org.jbpm.integrationtests.handler;


public class TestWorkItemHandler implements WorkItemHandler {
    private WorkItem workItem;

    private boolean aborted = false;

    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        TestWorkItemHandler.this.workItem = workItem;
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        aborted = true;
    }

    public WorkItem getWorkItem() {
        return workItem;
    }

    public boolean isAborted() {
        return aborted;
    }

    public void reset() {
        workItem = null;
    }
}

