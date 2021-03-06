/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.services.task.rule;

import org.kie.api.KieBase;
import java.util.Map;

public interface RuleContextProvider {
    KieBase getKieBase(String scope);

    void addKieBase(String scope, KieBase kbase);

    Map<String, Object> getGlobals(String scope);

    void addGlobals(String scope, Map<String, Object> globals);
}

