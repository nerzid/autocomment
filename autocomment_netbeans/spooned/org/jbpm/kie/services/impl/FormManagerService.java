/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.kie.services.impl;

import java.util.Map;
import java.util.Set;

/**
 * @author salaboy
 */
public interface FormManagerService {
    void registerForm(String deploymentId, String key, String formContent);

    void unRegisterForms(String deploymentId);

    Map<String, String> getAllFormsByDeployment(String deploymentId);

    Set<String> getAllDeployments();

    Map<String, String> getAllForms();

    String getFormByKey(String deploymentId, String key);
}

