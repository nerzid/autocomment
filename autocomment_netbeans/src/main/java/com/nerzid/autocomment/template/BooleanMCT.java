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
 *
 * @author nerzid
 */
public class BooleanMCT extends MethodCommentTemplate {

    // e.g. VBZ NN - isDirectory()
    public String booleanMethodWithOneVerb(String verb, List<String> nouns) {
        String res = "";

        res += "This method checks whether " + "this instance ";
        res += verb + " ";
        for (String noun : nouns) {
            res += noun + " ";
        }
        res += "or not.";

        return res;
    }

    // e.g. VBZ NN - isSeperator(char)
    public String booleanMethodWithOneVerbAndParameters(String verb, List<String> nouns, List<String> params) {
        String res = "";

        res += "This method checks whether " + "the given ";
        int ix = 0;
        for (String param : params) {
            if (ix + 1 == params.size()) {
                res += param + " ";
            } else {
                res += param + " and ";
            }
            ix++;
        }

        res += verb + " ";
        for (String noun : nouns) {
            res += noun + " ";
        }
        res += "or not.";

        return res;
    }

    // e.g. VBZ NN NN NN VBD - isGroupTargetEntityAllowed()
    public String booleanMethodWithTwoVerb(String verb1, String verb2, List<String> nouns) {
        String res = "";

        res += "This method checks whether the ";
        for (String noun : nouns) {
            res += noun + " ";
        }
        res += verb1 + " ";
        res += verb2 + " ";
        res += "or not.";

        return res;
    }

}
