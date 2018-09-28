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

import com.nerzid.autocomment.grammar.CommentTitleLexer;
import com.nerzid.autocomment.grammar.CommentTitleParser;
import com.nerzid.autocomment.sunit.ControllingSUnit;
import com.nerzid.autocomment.sunit.FunctionSUnit;
import com.nerzid.autocomment.sunit.SUnit;
import com.nerzid.autocomment.sunit.SUnitType;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtThisAccess;

import java.util.List;

/**
 * Created by @author nerzid on 16.04.2017.
 */
public class SUnitCommentTemplate {
    protected String comment;
    protected SUnit sunit;

    public SUnitCommentTemplate(SUnit sunit) {
        this.sunit = sunit;
    }

    public SUnitCommentTemplate(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String prepareThenGetComment(CtExpression target, String postag_sentence, String[] words, List<CtExpression> params) {
        ANTLRInputStream input = new ANTLRInputStream(postag_sentence);
        CommentTitleLexer lexer = new CommentTitleLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CommentTitleParser parser = new CommentTitleParser(tokens);

        lexer.removeErrorListeners();
        parser.removeErrorListeners();

        PostagParsingErrorListener ppel = new PostagParsingErrorListener();
        lexer.addErrorListener(ppel);
        parser.addErrorListener(ppel);
        parser.setErrorHandler(new BailErrorStrategy());

        EvalCommentTitleListener evalListener = new EvalCommentTitleListener();
        SUnitType sunitType = sunit.getSUnitType();
        try {
            ParseTree tree = null;
            ParseTreeWalker ptw = new ParseTreeWalker();
            int verbCount = postag_sentence.split("v").length;
            if (sunitType == SUnitType.ENDING
                    || sunitType == SUnitType.VOID_RETURN
                    || sunitType == SUnitType.SAME_ACTION_SEQUENCE) {
                if (verbCount == 1) {
                    tree = parser.one_verb_rule();
                    ptw.walk(evalListener, tree);

                    PostaggedWord postaggedWord = evalListener.getPostaggedWord();
                    postaggedWord.setTextUsingPostagsLength(words, postag_sentence.split(" "));

                    return useOneVerbRule(postaggedWord, target);
                } else {
                    tree = parser.two_verb_rule();
                    ptw.walk(evalListener, tree);

                    PostaggedWord postaggedWord = evalListener.getPostaggedWord();

                    postaggedWord.setTextUsingPostagsLength(words, postag_sentence.split(" "));
//                    return new BooleanMCT().withTwoVerbs(postaggedWord, true);
                }

            } else if (sunitType == SUnitType.CONTROLLING) {
                if (verbCount == 1) {
                    ((ControllingSUnit)sunit).getConditions();
                }
            }

            ptw.walk(evalListener, tree);

            return evalListener.getPostagSent();
        } catch (Exception e) {
            ppel.errorList.add("err");
            System.out.println("Hata: " + e.getMessage());
            return "";
        }
    }

    protected String useOneVerbRule(PostaggedWord postaggedWord, CtExpression target) {
        String res = "";

        res += postaggedWord.getVerb1().getText() + " ";
        if (postaggedWord.getNounphrase1() != null) {
            res += postaggedWord.getNounphrase1().getText() + " ";
        }
        if (sunit instanceof FunctionSUnit) {
            FunctionSUnit fsunit = (FunctionSUnit) sunit;
            res += fsunit.getPrettyDataVars();
        }
        res += getAppropriatePrepositionForVerb(postaggedWord.getVerb1().getText()) + " ";
        if (target instanceof CtThisAccess) {
            res += "this instance";
        } else {
            if (target.getType().getSimpleName().equalsIgnoreCase("void"))
                res += target.toString();
            else
                res += target.getType().getSimpleName() + "{" + target.toString() + "}";
        }
        return res;
    }

    protected String getAppropriatePrepositionForVerb(String verb) {
        switch (verb) {
            case "append":
                return "to";
            case "add":
                return "to";
            case "get":
                return "from";
            case "initialize":
                return "the";
            default:
                return "to";
        }
    }

    @Override
    public String toString() {
        return comment;
    }
}
