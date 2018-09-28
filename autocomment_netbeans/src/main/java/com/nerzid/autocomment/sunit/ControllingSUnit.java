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

import com.nerzid.autocomment.model.Comment;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.visitor.filter.CompositeFilter;
import spoon.reflect.visitor.filter.FilteringOperator;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 *
 * @author nerzid
 */
public class ControllingSUnit extends SUnit{
    
    public ControllingSUnit(CtElement element) {
        super(element);
        boolean hasElement = false;
        for (ControllingSUnit controllingSUnit : controllingSUnits){
            if (controllingSUnit.getElement().equals(element)) {
                hasElement = true;
                break;
            }
        }
        if (!hasElement) {
            sunitType = SUnitType.CONTROLLING;
            controllingSUnits.add(this);
        }
    }

//    @Override
//    public String toString() {
//        return super.toString() + "ControllingSUnit{" + '}';
//    }

    public List<CtExpression> getConditions() {
        List<CtExpression> conditions = new ArrayList<>();

        if (element instanceof CtExpression) {
            CtExpression full_condition = (CtExpression) element;
            full_condition.getElements(new TypeFilter<>(CtExpression.class));
            System.out.println();
        }

        return conditions;
    }

}
