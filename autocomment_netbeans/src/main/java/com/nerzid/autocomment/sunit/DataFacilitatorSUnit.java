/*
 * Copyright 2017 nerzid.
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
package com.nerzid.autocomment.sunit;

import java.util.Objects;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;

/**
 *
 * @author nerzid
 */
public class DataFacilitatorSUnit extends SUnit{

    // Data Variable to get Data Facilitators of it.
    private CtVariableAccess dataVar;

    public DataFacilitatorSUnit(CtElement element, CtVariableAccess dataVar) {
        super(element);
        this.dataVar = dataVar;
        dataFacilitatorSUnits.add(this);
    }

    public DataFacilitatorSUnit(CtVariableAccess dataVar) {
        this.dataVar = dataVar;
    }
    
    public CtVariableAccess getDataVar() {
        return dataVar;
    }

    public void setDataVar(CtVariableAccess dataVar) {
        this.dataVar = dataVar;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.dataVar);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DataFacilitatorSUnit other = (DataFacilitatorSUnit) obj;
        if (!Objects.equals(this.dataVar, other.dataVar)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return super.toString() + " => "+ "{" + "dataVar=> " + dataVar + '}' + "\n";
    }

}
