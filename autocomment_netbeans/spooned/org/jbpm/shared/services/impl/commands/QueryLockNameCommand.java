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


package org.jbpm.shared.services.impl.commands;

import org.kie.internal.command.Context;
import org.drools.core.command.impl.GenericCommand;
import org.jbpm.shared.services.impl.JpaPersistenceContext;
import java.util.Map;

public class QueryLockNameCommand<T> implements GenericCommand<T> {
    private static final long serialVersionUID = -4014807273522165028L;

    private Class<T> resultType;

    private String queryName;

    private Map<String, Object> params;

    private boolean singleResult;

    public QueryLockNameCommand(String queryName, Map<String, Object> params, boolean singleResult) {
        QueryLockNameCommand.this.resultType = ((Class<T>) (Object.class.getClass()));
        QueryLockNameCommand.this.queryName = queryName;
        QueryLockNameCommand.this.params = params;
    }

    @Override
    public T execute(Context context) {
        JpaPersistenceContext ctx = ((JpaPersistenceContext) (context));
        return ctx.queryAndLockWithParametersInTransaction(queryName, params, singleResult, resultType);
    }
}

