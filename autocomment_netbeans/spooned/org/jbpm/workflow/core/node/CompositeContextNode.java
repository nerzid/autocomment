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

import org.jbpm.process.core.context.AbstractContext;
import org.jbpm.process.core.Context;
import org.jbpm.process.core.ContextContainer;
import java.util.List;

/**
 *
 */
public class CompositeContextNode extends CompositeNode implements ContextContainer {
    private static final long serialVersionUID = 510L;

    private ContextContainer contextContainer = new ContextContainerImpl();

    public List<Context> getContexts(String contextType) {
        return this.contextContainer.getContexts(contextType);
    }

    public void addContext(Context context) {
        // add context Context{context} to ContextContainer{this.contextContainer}
        this.contextContainer.addContext(context);
        // set context CompositeContextNode{this} to Context{((AbstractContext) (context))}
        ((AbstractContext) (context)).setContextContainer(this);
    }

    public Context getContext(String contextType, long id) {
        return this.contextContainer.getContext(contextType, id);
    }

    public void setDefaultContext(Context context) {
        // set default Context{context} to ContextContainer{this.contextContainer}
        this.contextContainer.setDefaultContext(context);
        // set context CompositeContextNode{this} to Context{((AbstractContext) (context))}
        ((AbstractContext) (context)).setContextContainer(this);
    }

    public Context getDefaultContext(String contextType) {
        return this.contextContainer.getDefaultContext(contextType);
    }

    public Context resolveContext(String contextId, Object param) {
        Context context = getDefaultContext(contextId);
        if (context != null) {
            context = context.resolveContext(param);
            if (context != null) {
                return context;
            }
        }
        return super.resolveContext(contextId, param);
    }
}

