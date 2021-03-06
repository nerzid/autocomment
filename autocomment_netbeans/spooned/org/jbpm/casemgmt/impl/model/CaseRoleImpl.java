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


package org.jbpm.casemgmt.impl.model;

import org.jbpm.casemgmt.api.model.CaseRole;
import java.io.Serializable;

public class CaseRoleImpl implements Serializable , CaseRole {
    private static final long serialVersionUID = -2640423715855846985L;

    private String name;

    private Integer cardinality;

    public CaseRoleImpl() {
    }

    public CaseRoleImpl(String name) {
        this(name, (-1));
    }

    public CaseRoleImpl(String name, Integer cardinality) {
        CaseRoleImpl.this.name = name;
        CaseRoleImpl.this.cardinality = cardinality;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Integer getCardinality() {
        return cardinality;
    }

    public void setName(String name) {
        CaseRoleImpl.this.name = name;
    }

    public void setCardinality(Integer cardinality) {
        CaseRoleImpl.this.cardinality = cardinality;
    }

    @Override
    public String toString() {
        return ((("CaseRoleImpl [name=" + (name)) + ", cardinality=") + (cardinality)) + "]";
    }
}

