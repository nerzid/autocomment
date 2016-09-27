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


package org.jbpm.workflow.core.node;

import java.util.HashSet;
import org.drools.core.process.core.ParameterDefinition;
import java.util.Set;
import org.drools.core.process.core.Work;

public class HumanTaskNode extends WorkItemNode {
    private static final long serialVersionUID = 510L;

    private String swimlane;

    public HumanTaskNode() {
        Work work = new org.drools.core.process.core.impl.WorkImpl();
        work.setName("Human Task");
        Set<ParameterDefinition> parameterDefinitions = new HashSet<ParameterDefinition>();
        parameterDefinitions.add(new org.drools.core.process.core.impl.ParameterDefinitionImpl("TaskName", new org.drools.core.process.core.datatype.impl.type.StringDataType()));
        parameterDefinitions.add(new org.drools.core.process.core.impl.ParameterDefinitionImpl("ActorId", new org.drools.core.process.core.datatype.impl.type.StringDataType()));
        parameterDefinitions.add(new org.drools.core.process.core.impl.ParameterDefinitionImpl("Priority", new org.drools.core.process.core.datatype.impl.type.StringDataType()));
        parameterDefinitions.add(new org.drools.core.process.core.impl.ParameterDefinitionImpl("Comment", new org.drools.core.process.core.datatype.impl.type.StringDataType()));
        parameterDefinitions.add(new org.drools.core.process.core.impl.ParameterDefinitionImpl("Skippable", new org.drools.core.process.core.datatype.impl.type.StringDataType()));
        parameterDefinitions.add(new org.drools.core.process.core.impl.ParameterDefinitionImpl("Content", new org.drools.core.process.core.datatype.impl.type.StringDataType()));
        // TODO: initiator
        // TODO: attachments
        // TODO: deadlines
        // TODO: delegates
        // TODO: recipients
        // TODO: ...
        work.setParameterDefinitions(parameterDefinitions);
        setWork(work);
    }

    public String getSwimlane() {
        return swimlane;
    }

    public void setSwimlane(String swimlane) {
        HumanTaskNode.this.swimlane = swimlane;
    }
}

