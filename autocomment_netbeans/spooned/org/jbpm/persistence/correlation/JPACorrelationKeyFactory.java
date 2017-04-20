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


package org.jbpm.persistence.correlation;

import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationKeyFactory;
import java.util.List;

public class JPACorrelationKeyFactory implements CorrelationKeyFactory {
    public CorrelationKey newCorrelationKey(String businessKey) {
        if (businessKey.isEmpty()) {
            throw new IllegalArgumentException("businessKey cannot be empty");
        }
        CorrelationKeyInfo correlationKey = new CorrelationKeyInfo();
        // add property CorrelationPropertyInfo{new CorrelationPropertyInfo(null, businessKey)} to CorrelationKeyInfo{correlationKey}
        correlationKey.addProperty(new CorrelationPropertyInfo(null, businessKey));
        return correlationKey;
    }

    public CorrelationKey newCorrelationKey(List<String> properties) {
        if (properties.isEmpty()) {
            throw new IllegalArgumentException("properties cannot be empty");
        }
        CorrelationKeyInfo correlationKey = new CorrelationKeyInfo();
        for (String businessKey : properties) {
            correlationKey.addProperty(new CorrelationPropertyInfo(null, businessKey));
        }
        return correlationKey;
    }
}

