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
import com.nerzid.autocomment.sunit.SUnitStorage;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtReturn;

import java.util.List;
import java.util.PriorityQueue;

/**
 * @author nerzid
 */
public class NumericMCT extends MethodCommentTemplate {
//
//    @Override
//    protected Comment withOneVerb(PostaggedWord postaggedWord, List<String> params, SUnitStorage sUnitStorage, boolean isStatic, boolean isInvocComment, List<PostaggedWord> postaggedWords) {
//        Comment comment = new Comment();
//        if (isInvocComment)
//            comment.appendToSummary("This method ");
//        comment.appendToSummary(postaggedWord.getVerb1().getText() + " ");
//        if (postaggedWord.getNounphrase1() != null)
//            comment.appendToSummary(postaggedWord.getNounphrase1().getText() + " ");
//        comment.appendToSummary(prepareStringForParams(postaggedWord.getVerb1().getText(), params, isStatic));
////        comment.appendComment(getReturnedInfo(sUnitStorage));
//        comment.appendComment(getSUnitComments(sUnitStorage));
//        return comment;
//    }

}
