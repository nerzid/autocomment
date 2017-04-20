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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.PriorityQueue;

import spoon.reflect.code.*;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.CompositeFilter;
import spoon.reflect.visitor.filter.FilteringOperator;
import spoon.reflect.visitor.filter.TypeFilter;

/**
 *
 * @author nerzid
 */
public abstract class FunctionSUnit extends SUnit {

    protected Collection<DataFacilitatorSUnit> facilitators;
    protected Collection<CtVariableAccess> dataVars;

    protected static Collection<CtVariableAccess> allDataVars;

    {
        facilitators = new PriorityQueue<>();
        dataVars = new ArrayList<>();
    }

    static {
        allDataVars = new ArrayList<>();
    }

    public FunctionSUnit(CtElement element) {
        super(element);
    }



    private void addDataFacilitator(DataFacilitatorSUnit facilitator) {
        facilitators.add(facilitator);
    }

    public Collection<DataFacilitatorSUnit> getFacilitators() {
        return facilitators;
    }

    public void setFacilitators(Collection<DataFacilitatorSUnit> facilitators) {
        this.facilitators = facilitators;
    }

    public Collection<CtVariableAccess> getDataVars() {
        return dataVars;
    }

    public void setDataVars(Collection<CtVariableAccess> dataVars) {
        this.dataVars = dataVars;
    }

    public static Collection<CtVariableAccess> getAllDataVars() {
        return allDataVars;
    }

    public static void setAllDataVars(Collection<CtVariableAccess> allDataVars) {
        FunctionSUnit.allDataVars = allDataVars;
    }

    public static boolean isDataVarExists(CtVariableAccess var) {
        for (DataFacilitatorSUnit sunit : dataFacilitatorSUnits) {
            if (sunit.getDataVar().equals(var)) {
                return true;
            }
        }
        return false;
    }

    public void addDataVar(CtVariableAccess var) {
        dataVars.add(var);
        allDataVars.add(var);
    }

    /**
     * Add data vars using {@link CtElement#getElements(Filter)}
     */
    public void addDataVars() {
//        if (element instanceof CtInvocation) {
//            List<CtVariableAccess> vars = ((CtInvocation) element).getArguments();
//            for (CtVariableAccess var : vars) {
//                this.addDataVar(var);
//            }
//        }
        List<CtVariableAccess> vars = element.getElements(new TypeFilter(CtVariableAccess.class));
        for (CtVariableAccess var : vars) {
            this.addDataVar(var);
        }
    }

    /**
     * Add data vars using {@link CtElement#getElements(Filter)}
     * @param e
     */
    public void addDataVars(CtElement e){
        List<CtVariableAccess> vars = e.getElements(new TypeFilter(CtVariableAccess.class));
        for (CtVariableAccess var : vars) {
            this.addDataVar(var);
        }
    }

    /**
     *
     * @param e Get the method to search for facilitators using dataVars
     * @param fsunit
     */
    public void dataVarsToFacilitators(CtMethod e, FunctionSUnit fsunit) {
        CompositeFilter cf = new CompositeFilter(
                FilteringOperator.UNION,
                new TypeFilter(CtAssignment.class),
                new TypeFilter(CtLocalVariable.class));
        List<CtStatement> stmts = e.getElements(cf);

        CtLocalVariable clv;

        for (CtStatement stmt : stmts) {
            for (CtVariableAccess dataVar : fsunit.getDataVars()) {
                if ((stmt instanceof CtAssignment && dataVar.toString().equals(((CtAssignment) stmt).getAssigned().toString()))
                        || (stmt instanceof CtLocalVariable && dataVar.toString().equals(((CtLocalVariable) stmt).getSimpleName()))) {
                    if (!FunctionSUnit.isDataVarExists(dataVar)) {
                        addDataFacilitator(new DataFacilitatorSUnit(stmt, dataVar));
                    }

                }
            }
        }
    }

    @Override
    public String toString() {
        return super.toString() + "\n" + "\t"+ "FunctionSUnit{\n\t\t" + "facilitators=> \n" + prettyFacilitators() + '}' + "\n";
    }
    
    public String prettyFacilitators() {
        String result = "";
        for (DataFacilitatorSUnit facilitator : facilitators) {
            result += "\t\t\t->" + facilitator.toString() + "\n";
        }
        return result;
    }

}
