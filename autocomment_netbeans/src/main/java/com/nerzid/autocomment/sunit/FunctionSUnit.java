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
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.CompositeFilter;
import spoon.reflect.visitor.filter.FilteringOperator;
import spoon.reflect.visitor.filter.TypeFilter;

/**
 * @author nerzid
 */
public abstract class FunctionSUnit extends SUnit {

    protected Collection<DataFacilitatorSUnit> facilitators;
    protected Collection<CtExpression> dataVars;

    protected static Collection<CtExpression> allDataVars;

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

    public Collection<CtExpression> getDataVars() {
        return dataVars;
    }

    public void setDataVars(Collection<CtExpression> dataVars) {
        this.dataVars = dataVars;
    }

    public static Collection<CtExpression> getAllDataVars() {
        return allDataVars;
    }

    public static void setAllDataVars(Collection<CtExpression> allDataVars) {
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

    public void addDataVar(CtExpression var) {
        dataVars.add(var);
        allDataVars.add(var);
    }

    /**
     * Add data vars using {@link CtElement#getElements(Filter)}
     */
    public void addDataVars() {

        // AbstractInvocation has ConstructorCall, Invocation and NewClass subinterfaces.
        if (element instanceof CtAbstractInvocation) {
            List<CtExpression> expressions = ((CtAbstractInvocation) element).getArguments();
            for (CtExpression expression : expressions) {
                this.addDataVar(expression);
            }
        }
    }

    /**
     * Add data vars using {@link CtElement#getElements(Filter)}
     *
     * @param e
     */
    public void addDataVars(CtElement e) {
        if (e instanceof CtAbstractInvocation) {
            List<CtExpression> expressions = ((CtAbstractInvocation) e).getArguments();
            for (CtExpression expression : expressions) {
                this.addDataVar(expression);
            }
        }
    }

    public String getPrettyDataVars() {
        String res = "";
        Collection<CtExpression> dataVars = getDataVars();
        List<String> addedTypesList = new ArrayList<>();
        if (dataVars.size() != 0) {
            for (CtExpression e : dataVars) {
                if (!addedTypesList.contains(e.getType().toString())) {
                    res += e.getType().getSimpleName() + "{" + e.toString();
                    for (CtExpression e2 : dataVars) {
                        if (!e.equals(e2)
                                && !addedTypesList.contains(e2.getType().toString())
                                && e.getType().equals(e2.getType())) {
                            res += ", " + e2.toString();
                        }
                    }
                    res += "}, ";
                    addedTypesList.add(e.getType().toString());
                }
            }
            res = res.substring(0, res.length() - 2) + " ";
        }
        return res;
    }

    /**
     * @param e      Get the method to search for facilitators using dataVars
     * @param fsunit
     */
    public void dataVarsToFacilitators(CtMethod e, FunctionSUnit fsunit) {
        CompositeFilter cf = new CompositeFilter(
                FilteringOperator.UNION,
                new TypeFilter(CtAssignment.class),
                new TypeFilter(CtLocalVariable.class));
        List<CtElement> elems = e.getElements(cf);
        elems.addAll(((CtClass)e.getParent()).getFields());

        for (CtElement elem : elems) {
            for (CtExpression dataVar : fsunit.getDataVars()) {
                if (dataVar instanceof CtVariableAccess) {
                    CtVariableAccess data = (CtVariableAccess) dataVar;
                    if ((elem instanceof CtAssignment && data.toString().equals(((CtAssignment) elem).getAssigned().toString()))
                            || (elem instanceof CtLocalVariable && data.toString().equals(((CtLocalVariable) elem).getSimpleName()))
                            || (elem instanceof CtField && data.toString().equals(((CtField) elem).getSimpleName()))) {
                        if (!FunctionSUnit.isDataVarExists(data)) {
                            addDataFacilitator(new DataFacilitatorSUnit(elem, data));
                        }

                    }
                }
            }
        }
    }

//    @Override
//    public String toString() {
//        return super.toString() + "\n" + "\t" + "FunctionSUnit{\n\t\t" + "facilitators=> \n" + prettyFacilitators() + '}' + "\n";
//    }

//    public String prettyFacilitators() {
//        String result = "";
//        for (DataFacilitatorSUnit facilitator : facilitators) {
//            result += "\t\t\t->" + facilitator.toString() + "\n";
//        }
//        return result;
//    }
//
//    @Override
//    public String toString() {
//        return super.toString();
//    }
}
