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

import java.util.List;

/**
 * @author nerzid
 */
public class BooleanMCT extends MethodCommentTemplate {

    // e.g. VBZ NN - isDirectory()
    public String booleanMethodWithOneVerb(PostaggedWord postaggedWord, boolean prefix) {
        String res = "";

        if (prefix)
            res += "This method ";
        res += "checks whether " + "this instance ";
        res += postaggedWord.getVerb1().getText() + " ";
        res += postaggedWord.getNounphrase1().getText() + " ";
        res += "or not.";

        return res;
    }

    // e.g. VBZ NN - isSeperator(char)
    public String booleanMethodWithOneVerbAndParameters(PostaggedWord postaggedWord, List<String> params, boolean prefix) {
        String res = "";

        if (prefix)
            res += "This method ";
        res += "checks whether " + "the given ";
        int ix = 0;
        for (String param : params) {
            if (ix + 1 == params.size()) {
                res += param + " ";
            } else {
                res += param + " and ";
            }
            ix++;
        }

        res += postaggedWord.getVerb1().getText() + " ";
        res += postaggedWord.getNounphrase1().getText() + " ";
        res += "or not.";

        return res;
    }

    // e.g. VBZ NN NN NN VBD - isGroupTargetEntityAllowed()
    public String booleanMethodWithTwoVerb(PostaggedWord postaggedWord, boolean prefix) {
        String res = "";

        if (prefix)
            res += "This method ";
        res += "checks whether the ";
        res += postaggedWord.getNounphrase1().getText() + " ";
        res += postaggedWord.getVerb1().getText() + " ";
        res += postaggedWord.getVerb2().getText() + " ";
        res += "or not.";

        return res;
    }

}
