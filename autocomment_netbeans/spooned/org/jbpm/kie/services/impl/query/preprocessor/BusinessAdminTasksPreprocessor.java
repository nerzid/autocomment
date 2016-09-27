/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.kie.services.impl.query.preprocessor;

import java.util.ArrayList;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.def.DataSetPreprocessor;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.kie.internal.identity.IdentityProvider;
import java.util.List;
import org.jbpm.services.api.query.QueryResultMapper;

public class BusinessAdminTasksPreprocessor implements DataSetPreprocessor {
    private IdentityProvider identityProvider;

    public BusinessAdminTasksPreprocessor(IdentityProvider identityProvider) {
        BusinessAdminTasksPreprocessor.this.identityProvider = identityProvider;
    }

    @SuppressWarnings(value = "rawtypes")
    @Override
    public void preprocess(DataSetLookup lookup) {
        if ((identityProvider) == null) {
            return ;
        } 
        List<Comparable> orgEntities = new ArrayList<Comparable>(identityProvider.getRoles());
        orgEntities.add(identityProvider.getName());
        if ((lookup.getFirstFilterOp()) != null) {
            lookup.getFirstFilterOp().addFilterColumn(FilterFactory.equalsTo(QueryResultMapper.COLUMN_ORGANIZATIONAL_ENTITY, orgEntities));
        } else {
            DataSetFilter filter = new DataSetFilter();
            filter.addFilterColumn(FilterFactory.equalsTo(QueryResultMapper.COLUMN_ORGANIZATIONAL_ENTITY, orgEntities));
            lookup.addOperation(filter);
        }
    }
}

