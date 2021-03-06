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
/**
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package org.jbpm.services.task.impl.factories;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jbpm.services.task.utils.MVELUtils;
import java.util.Map;
import java.io.Reader;
import org.kie.api.task.model.Task;

/**
 */
public class TaskFactory {
    private static final Logger logger = LoggerFactory.getLogger(TaskFactory.class);

    public static Task evalTask(Reader reader, Map<String, Object> vars) {
        Task task = null;
        try {
            task = ((Task) (MVELUtils.eval(MVELUtils.toString(reader), vars)));
        } catch (IOException ex) {
            TaskFactory.logger.error("Error while evaluating task", ex);
        }
        return task;
    }

    public static Task evalTask(String taskString, Map<String, Object> vars) {
        Task task = ((Task) (MVELUtils.eval(taskString, vars)));
        return task;
    }

    public static Task evalTask(Reader reader) {
        return TaskFactory.evalTask(reader, null);
    }
}

