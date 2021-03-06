/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.test.container.archive.ejbservices;

import java.util.HashMap;
import java.util.Map;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class ThreadInfoWorkItemHandler implements WorkItemHandler {
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        StringBuilder stackTraceBuilder = new StringBuilder();
        Thread curr = Thread.currentThread();
        StackTraceElement[] stackTrace = curr.getStackTrace();
        stackTraceBuilder.append(stackTrace[0]);
        for (int i = 1; i < (stackTrace.length); i++) {
            stackTraceBuilder.append("\n   at ");
            stackTraceBuilder.append(stackTrace[i]);
        }
        System.out.println(curr.getName());
        System.out.println(stackTraceBuilder);
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("ThreadName", curr.getName());
        results.put("StackTrace", stackTraceBuilder.toString());
        manager.completeWorkItem(workItem.getId(), results);
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        // Do nothing
    }
}

