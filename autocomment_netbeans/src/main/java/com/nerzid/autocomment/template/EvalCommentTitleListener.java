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
    private PostaggedWord postaggedWord;

    @Override
    public void enterRule1(CommentTitleParser.Rule1Context ctx) {
        System.out.println("******");
        postaggedWord = new PostaggedWord();
        Word verb1 = new Word();
        verb1.setPostag(ctx.V().getText());
        postaggedWord.setVerb1(verb1);
        Word npr1 = new Word();
        npr1.setPostag(ctx.NPR().getText());
        postaggedWord.setNounphrase1(npr1);

        Word pp = new Word();

        if (ctx.PP() != null) {
            pp.setPostag(ctx.PP().getText());
            System.out.println("PP?: " + pp);
            postaggedWord.setPreposition(pp);
        }
        System.out.println("**********");
    }

    @Override
    public void enterOne_verb_rule(CommentTitleParser.One_verb_ruleContext ctx) {
        postaggedWord = new PostaggedWord();
        Word verb1 = new Word();
        verb1.setPostag(ctx.V().getText());
        postaggedWord.setVerb1(verb1);
        if (ctx.NPR() != null) {
            Word npr1 = new Word();
            npr1.setPostag(ctx.NPR().getText());
            postaggedWord.setNounphrase1(npr1);
        }
    }

    @Override
    public void enterTwo_verb_rule(CommentTitleParser.Two_verb_ruleContext ctx) {
        // TO DO
    }

    public String getPostagSent() {
        return postagSent;
    }

    public PostaggedWord getPostaggedWord() {
        return postaggedWord;
    }

}
