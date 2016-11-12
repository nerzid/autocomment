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

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

/**
 *
 * @author nerzid
 */
public class Test {
    public static void main(String[] args) {
        getCommentTitleType("VBZ NNPS IN NN");
    }
    
    public static void getCommentTitleType(String postag_sentence){
        ANTLRInputStream input = new ANTLRInputStream(postag_sentence);
        CommentTitleLexer lexer = new CommentTitleLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CommentTitleParser parser = new CommentTitleParser(tokens);
        
        lexer.removeErrorListeners();
        parser.removeErrorListeners();
        
        PostagParsingErrorListener ppel = new PostagParsingErrorListener();
        lexer.addErrorListener(ppel);
        
        
        
        parser.rule1();
        
        
        
        if(!ppel.errorList.isEmpty())
            System.out.println("valid for rule1");
        else
            System.out.println("NOT valid for rule1");
    }
    
    public static void getCommentDescType(String postag_sentence){
        
    }
}
