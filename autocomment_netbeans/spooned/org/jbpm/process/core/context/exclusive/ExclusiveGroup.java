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


package org.jbpm.process.core.context.exclusive;

import org.jbpm.process.core.context.AbstractContext;
import org.jbpm.process.core.Context;

public class ExclusiveGroup extends AbstractContext {
    private static final long serialVersionUID = 510L;

    public static final String EXCLUSIVE_GROUP = "ExclusiveGroup";

    public String getType() {
        return ExclusiveGroup.EXCLUSIVE_GROUP;
    }

    public Context resolveContext(Object param) {
        return null;
    }
}

