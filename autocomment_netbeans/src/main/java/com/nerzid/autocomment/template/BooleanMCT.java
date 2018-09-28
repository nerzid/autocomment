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

import com.nerzid.autocomment.model.Comment;
import com.nerzid.autocomment.sunit.EndingSUnit;
import com.nerzid.autocomment.sunit.FunctionSUnit;
import com.nerzid.autocomment.sunit.SUnitStorage;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtReturn;

import java.util.List;
import java.util.PriorityQueue;

/**
 * @author nerzid
 */
public class BooleanMCT extends MethodCommentTemplate {

    // e.g. VBZ NN - isDirectory()  isSeperator(char)
    public Comment withOneVerb(PostaggedWord postaggedWord, List<String> params, SUnitStorage sUnitStorage, boolean isStatic, boolean isInvocComment) {
        Comment comment = new Comment();
        if (postaggedWord.getNounphrase1() == null) {
            if (isInvocComment)
                comment.appendToSummary("This method ");
            comment.appendToSummary(postaggedWord.getVerb1().getText() + "s ");
            if (isStatic)
                comment.appendToSummary("this class ");
            else
                comment.appendToSummary("this instance ");
            comment.appendToSummary("and returns true if it is successfuly ");
            comment.appendToSummary(postaggedWord.getVerb1().getText() +"d");
            // TODO need to implement code to add sunits to comment here
        } else {
            if (isInvocComment)
                comment.appendToSummary("This method checks whether ");

            String verb1_postag = postaggedWord.getVerb1().getPostag();
            if (verb1_postag.equalsIgnoreCase("vbz")) {
                comment.appendComment(withVBZVerb(postaggedWord, params, isStatic));
            } else if (verb1_postag.equalsIgnoreCase("vb"))
                comment.appendComment(withVBVerb(postaggedWord, params, isStatic));
            else {
                comment.appendToSummary(
                        prepareStringForParams(postaggedWord.getVerb1().getText(), params, isStatic)
                                + postaggedWord.getVerb1().getText() + " "
                                + postaggedWord.getNounphrase1().getText() + " "
                                + "or not."
                );
            }
        }
        return comment;
    }

    // isDirectory
    protected Comment withVBZVerb(PostaggedWord postaggedWord, List<String> params, boolean isStatic) {
        // boolean delete(Page page)
        Comment comment = new Comment();
        if (params.isEmpty())
            if (isStatic)
                comment.appendToSummary("this class ");
            else
                comment.appendToSummary("this instance ");
        comment.appendToSummary(postaggedWord.getVerb1().getText() + " ");
        if (postaggedWord.getNounphrase1() != null)
            comment.appendToSummary(postaggedWord.getNounphrase1().getText() + " ");
        comment.appendToSummary(prepareStringForParams(postaggedWord.getVerb1().getText(), params, isStatic));
        comment.appendToSummary("or not.");
        return comment;
    }

    // delete(Page page), close()
    protected Comment withVBVerb(PostaggedWord postaggedWord, List<String> params, boolean isStatic) {
        // boolean delete(Page page)
        Comment comment = new Comment();
        comment.appendToSummary(postaggedWord.getVerb1().getText() + "ing ");
        if (params.isEmpty())
            if (isStatic)
                comment.appendToSummary("this class ");
            else
                comment.appendToSummary("this instance ");
        if (postaggedWord.getNounphrase1() != null)
            comment.appendToSummary(postaggedWord.getNounphrase1().getText() + " ");
        comment.appendToSummary(prepareStringForParams(postaggedWord.getVerb1().getText(), params, isStatic));
        comment.appendToSummary(" is successful or not.");
        return comment;
    }


    // e.g. VBZ NN NN NN VBD - isGroupTargetEntityAllowed()
    protected Comment withTwoVerbs(PostaggedWord postaggedWord, List<String> params, SUnitStorage sUnitStorage, boolean prefix) {
        Comment comment = new Comment();
        if (prefix)
            comment.appendToSummary("This method ");
        comment.appendToSummary("checks whether the ");
        comment.appendToSummary(postaggedWord.getNounphrase1().getText() + " ");
        if (postaggedWord.getVerb1().getText().equalsIgnoreCase("check")) {
            comment.appendToSummary("is correct ");
        } else {
            comment.appendToSummary(postaggedWord.getVerb1().getText() + " ");
            comment.appendToSummary(postaggedWord.getVerb2().getText() + " ");
        }
        comment.appendToSummary("or not.");

        return comment;
    }

}
