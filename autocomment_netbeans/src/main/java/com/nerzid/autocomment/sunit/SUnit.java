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

import java.util.Collection;
import java.util.Objects;
import java.util.PriorityQueue;
import spoon.reflect.declaration.CtElement;

/**
 *
 * @author nerzid
 */
public abstract class SUnit implements Comparable<SUnit> {

    protected CtElement element;
    protected String comment;

    protected static Collection<EndingSUnit> endingSUnits;
    protected static Collection<VoidReturnSUnit> voidReturnSUnits;
    protected static Collection<SameActionSequenceSUnit> sameActionSequenceSUnits;
    protected static Collection<ControllingSUnit> controllingSUnits;
    protected static Collection<DataFacilitatorSUnit> dataFacilitatorSUnits;

    static {
        clearAndInitSUnitLists();
    }

    public SUnit(CtElement element) {
        this.element = element;
    }

    public SUnit() {
    }

    public CtElement getElement() {
        return element;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setElement(CtElement element) {
        this.element = element;
    }

    public static Collection<EndingSUnit> getEndingSUnits() {
        return endingSUnits;
    }

    public static Collection<VoidReturnSUnit> getVoidReturnSUnits() {
        return voidReturnSUnits;
    }

    public static Collection<SameActionSequenceSUnit> getSameActionSequenceSUnits() {
        return sameActionSequenceSUnits;
    }

    public static Collection<ControllingSUnit> getControllingSUnits() {
        return controllingSUnits;
    }

    public static Collection<DataFacilitatorSUnit> getDataFacilitatorSUnits() {
        return dataFacilitatorSUnits;
    }

    public static boolean isElementExists(CtElement e, SUnitType type) {
        Collection<? extends SUnit> elems;
        switch (type) {
            case CONTROLLING:
                elems = controllingSUnits;
                break;
            case ENDING:
                elems = endingSUnits;
                break;
            case VOID_RETURN:
                elems = voidReturnSUnits;
                break;
            case SAME_ACTION_SEQUENCE:
                elems = sameActionSequenceSUnits;
                break;
            default:
                return false;
        }
        for (SUnit elem : elems) {
            if (elem.getElement().equals(e)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "element=> " + element + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.element);
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
        final SUnit other = (SUnit) obj;
        return Objects.equals(this.element, other.element);
    }

    @Override
    public int compareTo(SUnit o) {
        if (this.element.getPosition().getLine() > o.element.getPosition().getLine()) {
            return 1;
        } else if (this.element.getPosition().getLine() < o.element.getPosition().getLine()) {
            return -1;
        } else {
            return 0;
        }
    }

    public static void clearAndInitSUnitLists() {
        endingSUnits = new PriorityQueue<>();
        voidReturnSUnits = new PriorityQueue<>();
        sameActionSequenceSUnits = new PriorityQueue<>();
        controllingSUnits = new PriorityQueue<>();
        dataFacilitatorSUnits = new PriorityQueue<>();
    }
}
