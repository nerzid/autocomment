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

import spoon.reflect.code.*;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.visitor.filter.TypeFilter;

/**
 * @author nerzid
 */
public class EndingSUnit extends FunctionSUnit {

    public EndingSUnit(CtElement element) {
        super(element);
        sunitType = SUnitType.ENDING;
        endingSUnits.add(this);
    }

//    @Override
//    public String toString() {
//        return super.toString() + "\t\t" + "EndingSUnit{" + '}' + "\n";
//    }


//    @Override
//    public String toString() {
//        return getUnknownTypedElementComment(element, false);
//    }

}
