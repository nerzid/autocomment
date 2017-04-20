/*
 * Copyright 2016 nerzid.
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
package com.nerzid.autocomment.template;

import spoon.reflect.code.CtExpression;

import java.util.Collection;
import java.util.List;

/**
 * @author nerzid
 */
public class VoidMCT extends MethodCommentTemplate {
    public VoidMCT() {

    }

    // e.g. VBZ NN - appendString()
    public String oneVerb(PostaggedWord postaggedWord, List<CtExpression> params, CtExpression target, boolean prefix){
        String res = "";

        if (prefix)
            res += "This method ";
        res += postaggedWord.getVerb1().getText() + " ";
        res += postaggedWord.getNounphrase1().getText() + " ";

        res += params.get(0).getType().getSimpleName() + " {" + params.get(0).toString() + "} ";
        res += "to ";
        res += target.getType().getSimpleName() + " {" + target.toString() + " }";
        return res;
    }
}
