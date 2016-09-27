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

import org.kie.internal.command.Context;
import org.kie.api.task.model.Group;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "get-groups-command")
@XmlAccessorType(value = XmlAccessType.NONE)
public class GetGroupsCommand extends TaskCommand<List<Group>> {
    private static final long serialVersionUID = -836520791223188840L;

    public GetGroupsCommand() {
    }

    public List<Group> execute(Context cntxt) {
        TaskContext context = ((TaskContext) (cntxt));
        return context.getTaskIdentityService().getGroups();
    }
}

