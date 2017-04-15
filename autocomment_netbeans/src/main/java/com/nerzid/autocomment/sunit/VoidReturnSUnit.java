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

import spoon.reflect.declaration.CtElement;

/**
 *
 * @author nerzid
 */
public class VoidReturnSUnit extends FunctionSUnit {
    
    public VoidReturnSUnit(CtElement element) {
        super(element);
        voidReturnSUnits.add(this);
    }

//    @Override
//    public String toString() {
//        return super.toString() + "\t\t" + "VoidReturnSUnit{" + '}' + "\n";
//    }

}
