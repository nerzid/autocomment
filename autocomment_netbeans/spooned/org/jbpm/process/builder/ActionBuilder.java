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


package org.jbpm.process.builder;

import org.drools.compiler.lang.descr.ActionDescr;
import org.jbpm.process.core.ContextResolver;
import org.jbpm.workflow.core.DroolsAction;
import org.drools.compiler.rule.builder.PackageBuildContext;

public interface ActionBuilder {
    public void build(final PackageBuildContext context, final DroolsAction action, final ActionDescr actionDescr, final ContextResolver contextResolver);
}

