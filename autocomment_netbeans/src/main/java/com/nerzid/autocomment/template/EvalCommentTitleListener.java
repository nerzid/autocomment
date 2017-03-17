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

import com.nerzid.autocomment.grammar.CommentTitleParser;
import com.nerzid.autocomment.grammar.CommentTitleBaseListener;

/**
 *
 * @author nerzid
 */
public class EvalCommentTitleListener extends CommentTitleBaseListener {

    private String postagSent = "";
    private String result = "";

    @Override
    public void enterRule1(CommentTitleParser.Rule1Context ctx) {
        System.out.println("******");

        String v = ctx.V().getText();
        String npr = ctx.NPR().getText();
        String pp = "";
        System.out.println("V: " + v);
        System.out.println("NPR: " + npr);

        if (ctx.PP() != null) {
            pp = ctx.PP().getText();
            System.out.println("PP?: " + pp);
        }
        postagSent = v + "|" + npr + "|" + pp;
        System.out.println("**********");
    }

    @Override
    public void enterBoolean_one_verb_rule(CommentTitleParser.Boolean_one_verb_ruleContext ctx) {
        String v = ctx.V().getText();
        String npr = ctx.NPR().getText();

        System.out.println("V: " + v);
        System.out.println("NPR: " + npr);
        postagSent = v + "|" + npr;
    }

    public String getPostagSent() {
        return postagSent;
    }

    public void setPostagSent(String postagSent) {
        this.postagSent = postagSent;
    }

}
