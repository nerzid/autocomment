/*
 *
 *  * Copyright 2016 nerzid.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.nerzid.autocomment.template;

import com.nerzid.autocomment.nlp.Tokenizer;
import com.nerzid.autocomment.sunit.SUnit;
import com.nerzid.autocomment.sunit.SameActionSequenceSUnit;
import com.nerzid.autocomment.sunit.VoidReturnSUnit;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;

import java.util.List;

/**
 * Created by @author nerzid on 19.04.2017.
 */
public class SameActionSequenceSUnitCT extends SUnitCommentTemplate {
    public SameActionSequenceSUnitCT(SUnit sunit) {
        super(sunit);
    }

    // e.g. VBZ NN - appendString()
    public String getCommentWithOneVerb(SameActionSequenceSUnit sameActionSequenceSUnit) {
        String commentStr = "";
        CtElement element = sameActionSequenceSUnit.getElement();
        if (element instanceof CtInvocation) {
            CtInvocation invoc = (CtInvocation) element;
            String verb = invoc.getExecutable().getSimpleName();
            String target = invoc.getTarget().getType().getSimpleName();


            List<String> verbs = Tokenizer.split(verb);
            verb = verbs.get(0);
            if (verbs.size() > 1) {
                verb += " " + verbs.get(1);
            }

            commentStr = verb + " ";
            for (CtExpression dataVar : sameActionSequenceSUnit.getDataVars()) {
                commentStr += dataVar.getType().getSimpleName() + "{" +
                        dataVar.toString() + "}"
                        + " to " + target + "{" + invoc.getTarget().toString() + "}";
            }
        }

        return commentStr;
    }
}
