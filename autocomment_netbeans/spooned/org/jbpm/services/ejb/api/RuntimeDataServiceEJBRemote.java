/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.services.ejb.api;

import org.jbpm.services.api.DeploymentEventListener;
import javax.ejb.Remote;
import org.jbpm.services.api.RuntimeDataService;
import org.kie.internal.task.query.TaskSummaryQueryBuilder;

@Remote
public interface RuntimeDataServiceEJBRemote extends DeploymentEventListener , RuntimeDataService {
    /**
     * This method is not supported in EJB remote api as <code>TaskSummaryQueryBuilder<code> is not remote capable object
     */
    @Override
    TaskSummaryQueryBuilder taskSummaryQuery(String userId);
}
