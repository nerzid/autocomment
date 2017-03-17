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
package com.nerzid.autocomment.processor;

import com.nerzid.autocomment.database.MethodTable;
import com.nerzid.autocomment.nlp.NLPToolkit;
import com.nerzid.autocomment.template.Test;
import java.util.ArrayList;
import java.util.List;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtComment;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;

/**
 *
 * @author nerzid
 */
public class CommentGeneratorMethodProcessor extends AbstractProcessor<CtMethod> {

    @Override
    public void process(CtMethod e) {

//        System.out.println("Method Name: " + e.getSimpleName());
//        System.out.println("Comments: " + e.getComments());
//        System.out.println("Body: " + e.toString());
        // Get method's simple name without any package extensions
        String method_name = e.getSimpleName();
        String signature = e.getSignature();
        String data_type = e.getType().getSimpleName();

        MethodTable mt = NLPToolkit.getMethodWithProperties(signature, method_name, 0);

        String postag = mt.getPostag();
        String splitted_identifier = mt.getSplittedIdentifier();

        System.out.println("postag: " + postag);
//        String template = Test.getCommentTitleType(postag);
//        String commentStr = "This javadoc is for " + e.getSimpleName();
        String commentStr = "This method ";
        String[] postags = postag.split(" ");
        String[] identifiers = splitted_identifier.split(" ");
        
        List<CtParameter> ctParams = e.getParameters();
        List<String> params = new ArrayList<>();

        for (CtParameter ctp : ctParams) {
            params.add(ctp.getSimpleName());
        }
        
        commentStr = Test.getTemplateSentence(data_type, postag, splitted_identifier.split(" "), params);

//        int ix = 0;
//        if(!template.isEmpty()){
//            String[] parts = template.split("\\|");
//            
//            int firstPart = parts[0].split(" ").length;
//            
//            for(int i = ix; i < firstPart; i++){
//                commentStr += identifiers[i] + " ";
//                ix++;
//            }
//            
//            int secondPart = parts[1].split(" ").length;
//            
//            for(int i = ix; i < firstPart + secondPart; i++){
//                commentStr += identifiers[i] + " ";
//                ix++;
//            }
//            
//            int thirdPart = parts[2].split(" ").length;
//            
//            for(int i = ix; i < firstPart+ secondPart +thirdPart; i++){
//                commentStr += identifiers[i] + " ";
//                ix++;
//            }
//        }
        CtComment c = getFactory().Code().createComment(commentStr, CtComment.CommentType.JAVADOC);
        e.addComment(c);
    }

}
