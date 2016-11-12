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
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtComment;
import spoon.reflect.declaration.CtMethod;

/**
 *
 * @author nerzid
 */
public class CommentGeneratorMethodProcessor extends AbstractProcessor<CtMethod>{

    @Override
    public void process(CtMethod e) {
        String commentStr = "This javadoc is for " + e.getSimpleName();
        CtComment c = getFactory().Code().createComment(commentStr, CtComment.CommentType.JAVADOC);
        e.addComment(c);
        System.out.println("Method Name: " + e.getSimpleName());
        System.out.println("Comments: " + e.getComments());
        System.out.println("Body: " + e.toString());

        // Get method's simple name without any package extensions
        String method_name = e.getSimpleName();
        String signature = e.getSignature();
        
        MethodTable mt = NLPToolkit.getMethodWithProperties(signature, method_name, 0);
        
        String postag = mt.getPostag();
        
        System.out.println("postag: " + postag);
        
        if(Test.getCommentTitleType(postag)){
            System.out.println("Its valid");
            
            
        }
    }
    
}
