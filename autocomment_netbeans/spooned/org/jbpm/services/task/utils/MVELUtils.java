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


package org.jbpm.services.task.utils;

import org.kie.internal.task.api.model.AccessType;
import org.jbpm.services.task.internals.lifecycle.Allowed;
import org.kie.internal.task.api.model.AllowedToDelegate;
import org.kie.api.command.Command;
import org.kie.internal.task.api.model.CommandName;
import java.util.HashMap;
import java.io.IOException;
import org.mvel2.MVEL;
import org.drools.core.util.MVELSafeHelper;
import java.util.Map;
import org.kie.internal.task.api.model.NotificationType;
import org.kie.internal.task.api.model.Operation;
import org.jbpm.services.task.internals.lifecycle.OperationCommand;
import org.kie.api.task.model.OrganizationalEntity;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import java.io.Reader;
import java.io.Serializable;
import org.kie.api.task.model.Status;
import org.kie.internal.task.api.model.SubTasksStrategy;
import org.kie.internal.task.api.TaskModelFactory;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.UserInfo;

/**
 */
public class MVELUtils {
    private static Map<String, Class<?>> inputs = new HashMap<String, Class<?>>();

    private static TaskModelFactory factory = TaskModelProvider.getFactory();

    public static Map<String, Class<?>> getInputs() {
        synchronized(MVELUtils.inputs) {
            if (MVELUtils.inputs.isEmpty()) {
                // org.jbpm.services.task
                MVELUtils.inputs.put("AccessType", AccessType.class);
                MVELUtils.inputs.put("AllowedToDelegate", AllowedToDelegate.class);
                MVELUtils.inputs.put("Attachment", MVELUtils.factory.newAttachment().getClass());
                MVELUtils.inputs.put("BooleanExpression", MVELUtils.factory.newBooleanExpression().getClass());
                MVELUtils.inputs.put("Comment", MVELUtils.factory.newComment().getClass());
                MVELUtils.inputs.put("Content", MVELUtils.factory.newContent().getClass());
                MVELUtils.inputs.put("Deadline", MVELUtils.factory.newDeadline().getClass());
                MVELUtils.inputs.put("Deadlines", MVELUtils.factory.newDeadlines().getClass());
                MVELUtils.inputs.put("Delegation", MVELUtils.factory.newDelegation().getClass());
                MVELUtils.inputs.put("EmailNotification", MVELUtils.factory.newEmialNotification().getClass());
                MVELUtils.inputs.put("EmailNotificationHeader", MVELUtils.factory.newEmailNotificationHeader().getClass());
                MVELUtils.inputs.put("Escalation", MVELUtils.factory.newEscalation().getClass());
                MVELUtils.inputs.put("Group", MVELUtils.factory.newGroup().getClass());
                MVELUtils.inputs.put("I18NText", MVELUtils.factory.newI18NText().getClass());
                MVELUtils.inputs.put("Notification", MVELUtils.factory.newNotification().getClass());
                MVELUtils.inputs.put("NotificationType", NotificationType.class);
                MVELUtils.inputs.put("OrganizationalEntity", OrganizationalEntity.class);
                MVELUtils.inputs.put("PeopleAssignments", MVELUtils.factory.newPeopleAssignments().getClass());
                MVELUtils.inputs.put("Reassignment", MVELUtils.factory.newReassignment().getClass());
                MVELUtils.inputs.put("Status", Status.class);
                MVELUtils.inputs.put("Task", MVELUtils.factory.newTask().getClass());
                MVELUtils.inputs.put("TaskData", MVELUtils.factory.newTaskData().getClass());
                MVELUtils.inputs.put("User", MVELUtils.factory.newUser().getClass());
                MVELUtils.inputs.put("UserInfo", UserInfo.class);
                MVELUtils.inputs.put("SubTasksStrategy", SubTasksStrategy.class);
                MVELUtils.inputs.put("Language", MVELUtils.factory.newLanguage().getClass());
                // org.jbpm.services.task.service
                MVELUtils.inputs.put("Allowed", Allowed.class);
                MVELUtils.inputs.put("Command", Command.class);
                MVELUtils.inputs.put("CommandName", CommandName.class);
                MVELUtils.inputs.put("ContentData", MVELUtils.factory.newContentData().getClass());
                MVELUtils.inputs.put("Operation", Operation.class);
                MVELUtils.inputs.put("Operation.Claim", Operation.class);
                MVELUtils.inputs.put("Operation.Delegate", Operation.class);
                MVELUtils.inputs.put("OperationCommand", OperationCommand.class);
                // org.drools.task.query
                MVELUtils.inputs.put("DeadlineSummary", MVELUtils.factory.newDeadline().getClass());
                MVELUtils.inputs.put("TaskSummary", MVELUtils.factory.newTaskSummary().getClass());
            } 
            return MVELUtils.inputs;
        }
    }

    public static Object eval(Reader reader, Map<String, Object> vars) {
        try {
            return MVELUtils.eval(MVELUtils.toString(reader), vars);
        } catch (IOException e) {
            throw new RuntimeException("Exception Thrown", e);
        }
    }

    public static Object eval(Reader reader) {
        try {
            return MVELUtils.eval(MVELUtils.toString(reader), null);
        } catch (IOException e) {
            throw new RuntimeException("Exception Thrown", e);
        }
    }

    public static Object eval(String str, Map<String, Object> vars) {
        ParserConfiguration pconf = new ParserConfiguration();
        pconf.addPackageImport("org.jbpm.services.task");
        // pconf.addPackageImport("org.jbpm.services.task.service");
        pconf.addPackageImport("org.jbpm.services.task.query");
        pconf.addPackageImport("java.util");
        for (String entry : MVELUtils.getInputs().keySet()) {
            pconf.addImport(entry, MVELUtils.getInputs().get(entry));
        }
        ParserContext context = new ParserContext(pconf);
        Serializable s = MVEL.compileExpression(str.trim(), context);
        if (vars != null) {
            return MVELSafeHelper.getEvaluator().executeExpression(s, vars);
        } else {
            return MVELSafeHelper.getEvaluator().executeExpression(s);
        }
    }

    public static String toString(Reader reader) throws IOException {
        int charValue;
        StringBuffer sb = new StringBuffer(1024);
        while ((charValue = reader.read()) != (-1)) {
            sb.append(((char) (charValue)));
        }
        return sb.toString();
    }
}

