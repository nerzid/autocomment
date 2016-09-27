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


package org.jbpm.kie.services.impl.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.DataSetCore;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefFactory;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.dashbuilder.dataset.DataSetLookupBuilder;
import org.dashbuilder.dataset.DataSetLookupFactory;
import org.dashbuilder.dataset.DataSetManager;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataprovider.DataSetProviderRegistry;
import org.jbpm.services.api.model.DeployedAsset;
import org.jbpm.services.api.DeploymentEvent;
import org.jbpm.services.api.DeploymentEventListener;
import org.jbpm.kie.services.impl.security.DeploymentRolesManager;
import java.util.HashMap;
import org.kie.internal.identity.IdentityProvider;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.jbpm.kie.services.impl.model.ProcessAssetDesc;
import org.jbpm.services.api.query.QueryAlreadyRegisteredException;
import org.kie.api.runtime.query.QueryContext;
import org.jbpm.services.api.query.model.QueryDefinition;
import org.jbpm.kie.services.impl.query.persistence.QueryDefinitionEntity;
import org.jbpm.services.api.query.QueryNotFoundException;
import org.jbpm.services.api.query.model.QueryParam;
import org.jbpm.services.api.query.QueryService;
import org.dashbuilder.dataset.def.SQLDataSetDefBuilder;
import org.dashbuilder.dataprovider.sql.SQLDataSetProvider;
import org.jbpm.shared.services.impl.TransactionalCommandService;

public class QueryServiceImpl implements DeploymentEventListener , QueryService {
    private static final Logger logger = LoggerFactory.getLogger(QueryServiceImpl.class);

    private DataSetDefRegistry dataSetDefRegistry;

    private DataSetManager dataSetManager;

    private DataSetProviderRegistry providerRegistry;

    private IdentityProvider identityProvider;

    private TransactionalCommandService commandService;

    private DeploymentRolesManager deploymentRolesManager = new DeploymentRolesManager();

    public void setDeploymentRolesManager(DeploymentRolesManager deploymentRolesManager) {
        QueryServiceImpl.this.deploymentRolesManager = deploymentRolesManager;
    }

    public void setIdentityProvider(IdentityProvider identityProvider) {
        QueryServiceImpl.this.identityProvider = identityProvider;
    }

    public void setCommandService(TransactionalCommandService commandService) {
        QueryServiceImpl.this.commandService = commandService;
    }

    public void setDataSetDefRegistry(DataSetDefRegistry dataSetDefRegistry) {
        QueryServiceImpl.this.dataSetDefRegistry = dataSetDefRegistry;
    }

    public void setProviderRegistry(DataSetProviderRegistry providerRegistry) {
        QueryServiceImpl.this.providerRegistry = providerRegistry;
    }

    public void setDataSetManager(DataSetManager dataSetManager) {
        QueryServiceImpl.this.dataSetManager = dataSetManager;
    }

    public void init() {
        if ((((dataSetDefRegistry) == null) && ((dataSetManager) == null)) && ((providerRegistry) == null)) {
            dataSetDefRegistry = DataSetCore.get().getDataSetDefRegistry();
            dataSetManager = DataSetCore.get().getDataSetManager();
            providerRegistry = DataSetCore.get().getDataSetProviderRegistry();
            providerRegistry.registerDataProvider(SQLDataSetProvider.get());
            dataSetDefRegistry.addListener(new org.jbpm.kie.services.impl.query.persistence.PersistDataSetListener(commandService));
        } 
        // load previously registered query definitions
        if ((commandService) != null) {
            List<QueryDefinitionEntity> queries = commandService.execute(new org.jbpm.shared.services.impl.commands.QueryNameCommand<List<QueryDefinitionEntity>>("getQueryDefinitions"));
            for (QueryDefinitionEntity entity : queries) {
                QueryDefinition definition = entity.toQueryDefinition();
                try {
                    registerQuery(definition);
                } catch (QueryAlreadyRegisteredException e) {
                    QueryServiceImpl.logger.debug("Query {} already registered, skipping...", definition.getName());
                }
            }
        } 
    }

    @Override
    public void registerQuery(QueryDefinition queryDefinition) throws QueryAlreadyRegisteredException {
        if ((dataSetDefRegistry.getDataSetDef(queryDefinition.getName())) != null) {
            throw new QueryAlreadyRegisteredException((("Query" + (queryDefinition.getName())) + " is already registered"));
        } 
        replaceQuery(queryDefinition);
    }

    @Override
    public void replaceQuery(QueryDefinition queryDefinition) {
        QueryServiceImpl.logger.debug("About to register {} query...", queryDefinition);
        if (queryDefinition instanceof SqlQueryDefinition) {
            SqlQueryDefinition sqlQueryDefinition = ((SqlQueryDefinition) (queryDefinition));
            SQLDataSetDefBuilder<?> builder = DataSetDefFactory.newSQLDataSetDef().uuid(sqlQueryDefinition.getName()).name((((sqlQueryDefinition.getName()) + "::") + (sqlQueryDefinition.getTarget().toString()))).dataSource(sqlQueryDefinition.getSource()).dbSQL(sqlQueryDefinition.getExpression(), true);
            DataSetDef sqlDef = builder.buildDef();
            dataSetDefRegistry.registerDataSetDef(sqlDef);
            if (queryDefinition.getTarget().equals(Target.BA_TASK)) {
                dataSetDefRegistry.registerPreprocessor(sqlDef.getUUID(), new org.jbpm.kie.services.impl.query.preprocessor.BusinessAdminTasksPreprocessor(identityProvider));
            } else if (queryDefinition.getTarget().equals(Target.PO_TASK)) {
                dataSetDefRegistry.registerPreprocessor(sqlDef.getUUID(), new org.jbpm.kie.services.impl.query.preprocessor.PotOwnerTasksPreprocessor(identityProvider));
            } else if (queryDefinition.getTarget().equals(Target.FILTERED_PROCESS)) {
                dataSetDefRegistry.registerPreprocessor(sqlDef.getUUID(), new org.jbpm.kie.services.impl.query.preprocessor.DeploymentIdsPreprocessor(deploymentRolesManager, identityProvider, org.jbpm.services.api.query.QueryResultMapper.COLUMN_EXTERNALID));
            } else if (queryDefinition.getTarget().equals(Target.FILTERED_BA_TASK)) {
                dataSetDefRegistry.registerPreprocessor(sqlDef.getUUID(), new org.jbpm.kie.services.impl.query.preprocessor.BusinessAdminTasksPreprocessor(identityProvider));
                dataSetDefRegistry.registerPreprocessor(sqlDef.getUUID(), new org.jbpm.kie.services.impl.query.preprocessor.DeploymentIdsPreprocessor(deploymentRolesManager, identityProvider, org.jbpm.services.api.query.QueryResultMapper.COLUMN_DEPLOYMENTID));
            } else if (queryDefinition.getTarget().equals(Target.FILTERED_PO_TASK)) {
                dataSetDefRegistry.registerPreprocessor(sqlDef.getUUID(), new org.jbpm.kie.services.impl.query.preprocessor.PotOwnerTasksPreprocessor(identityProvider));
                dataSetDefRegistry.registerPreprocessor(sqlDef.getUUID(), new org.jbpm.kie.services.impl.query.preprocessor.DeploymentIdsPreprocessor(deploymentRolesManager, identityProvider, org.jbpm.services.api.query.QueryResultMapper.COLUMN_DEPLOYMENTID));
            } 
            DataSetMetadata metadata = dataSetManager.getDataSetMetadata(sqlDef.getUUID());
            for (String columnId : metadata.getColumnIds()) {
                sqlDef.addColumn(columnId, metadata.getColumnType(columnId));
            }
            QueryServiceImpl.logger.info("Registered {} query successfully", queryDefinition.getName());
        } 
    }

    @Override
    public void unregisterQuery(final String uniqueQueryName) throws QueryNotFoundException {
        DataSetDef def = dataSetDefRegistry.removeDataSetDef(uniqueQueryName);
        if (def == null) {
            throw new QueryNotFoundException((("Query " + uniqueQueryName) + " not found"));
        } 
        QueryServiceImpl.logger.info("Unregistered {} query successfully", uniqueQueryName);
    }

    @Override
    public <T> T query(String queryName, QueryResultMapper<T> mapper, QueryContext queryContext, QueryParam... filterParams) throws QueryNotFoundException {
        return query(queryName, mapper, queryContext, new CoreFunctionQueryParamBuilder(filterParams));
    }

    @Override
    public <T> T query(String queryName, QueryResultMapper<T> mapper, QueryContext queryContext, QueryParamBuilder<?> paramBuilder) throws QueryNotFoundException {
        if ((dataSetDefRegistry.getDataSetDef(queryName)) == null) {
            throw new QueryNotFoundException((("Query " + queryName) + " not found"));
        } 
        QueryServiceImpl.logger.debug("About to query using {} definition with number of rows {} and starting at {} offset", queryName, queryContext.getCount(), queryContext.getOffset());
        DataSetLookupBuilder<?> builder = DataSetLookupFactory.newDataSetLookupBuilder().dataset(queryName).rowNumber(queryContext.getCount()).rowOffset(queryContext.getOffset());
        Object filter = paramBuilder.build();
        while (filter != null) {
            if (filter instanceof ColumnFilter) {
                // add filter
                builder.filter(((ColumnFilter) (filter)));
            } else if (filter instanceof AggregateColumnFilter) {
                // add aggregate function
                builder.column(((AggregateColumnFilter) (filter)).getColumnId(), ((AggregateColumnFilter) (filter)).getType(), ((AggregateColumnFilter) (filter)).getColumnId());
            } else if (filter instanceof GroupColumnFilter) {
                // add group function
                builder.group(((GroupColumnFilter) (filter)).getColumnId(), ((GroupColumnFilter) (filter)).getNewColumnId());
            } else if (filter instanceof ExtraColumnFilter) {
                // add extra column
                builder.column(((ExtraColumnFilter) (filter)).getColumnId(), ((ExtraColumnFilter) (filter)).getNewColumnId());
            } else {
                QueryServiceImpl.logger.warn("Unsupported filter '{}' generated by '{}'", filter, paramBuilder);
            }
            // call builder again in case more parameters are available
            filter = paramBuilder.build();
        }
        if ((queryContext.getOrderBy()) != null) {
            String[] oderByItems = queryContext.getOrderBy().split(",");
            for (String orderBy : oderByItems) {
                QueryServiceImpl.logger.debug("Applying order by {} and ascending {}", orderBy, queryContext.isAscending());
                builder.sort(orderBy.trim(), (queryContext.isAscending() ? "asc" : "desc"));
            }
        } 
        DataSet result = dataSetManager.lookupDataSet(builder.buildLookup());
        QueryServiceImpl.logger.debug("Query result is {}", result);
        T mappedResult = mapper.map(result);
        QueryServiceImpl.logger.debug("Mapped result is {}", mappedResult);
        return mappedResult;
    }

    @Override
    public QueryDefinition getQuery(String uniqueQueryName) throws QueryNotFoundException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", uniqueQueryName);
        List<QueryDefinitionEntity> queries = commandService.execute(new org.jbpm.shared.services.impl.commands.QueryNameCommand<List<QueryDefinitionEntity>>("getQueryDefinitionByName", params));
        if ((queries.size()) == 1) {
            return queries.get(0).toQueryDefinition();
        } 
        throw new QueryNotFoundException((("Query " + uniqueQueryName) + " not found"));
    }

    @Override
    public List<QueryDefinition> getQueries(QueryContext queryContext) {
        List<QueryDefinition> result = new ArrayList<QueryDefinition>();
        Map<String, Object> params = new HashMap<String, Object>();
        applyQueryContext(params, queryContext);
        List<QueryDefinitionEntity> queries = commandService.execute(new org.jbpm.shared.services.impl.commands.QueryNameCommand<List<QueryDefinitionEntity>>("getQueryDefinitions", params));
        for (QueryDefinitionEntity entity : queries) {
            QueryDefinition definition = entity.toQueryDefinition();
            result.add(definition);
        }
        return result;
    }

    protected void applyQueryContext(Map<String, Object> params, QueryContext queryContext) {
        if (queryContext != null) {
            params.put("firstResult", queryContext.getOffset());
            params.put("maxResults", queryContext.getCount());
            if (((queryContext.getOrderBy()) != null) && (!(queryContext.getOrderBy().isEmpty()))) {
                params.put(QueryManager.ORDER_BY_KEY, queryContext.getOrderBy());
                if (queryContext.isAscending()) {
                    params.put(QueryManager.ASCENDING_KEY, "true");
                } else {
                    params.put(QueryManager.DESCENDING_KEY, "true");
                }
            } 
        } 
    }

    public void onDeploy(DeploymentEvent event) {
        Collection<DeployedAsset> assets = event.getDeployedUnit().getDeployedAssets();
        List<String> roles = null;
        for (DeployedAsset asset : assets) {
            if (asset instanceof ProcessAssetDesc) {
                if (roles == null) {
                    roles = ((ProcessAssetDesc) (asset)).getRoles();
                } 
            } 
        }
        if (roles == null) {
            roles = Collections.emptyList();
        } 
        deploymentRolesManager.addRolesForDeployment(event.getDeploymentId(), roles);
    }

    public void onUnDeploy(DeploymentEvent event) {
        deploymentRolesManager.removeRolesForDeployment(event.getDeploymentId());
    }

    @Override
    public void onActivate(DeploymentEvent event) {
        // no op
    }

    @Override
    public void onDeactivate(DeploymentEvent event) {
        // no op
    }
}

