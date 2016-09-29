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

import com.nerzid.autocomment.nlp.Tokenizer;
import java.util.List;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtCFlowBreak;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.visitor.filter.ReturnOrThrowFilter;

/**
 *
 * @author nerzid
 *
 * Handles Methods. This processor can be used to handle method's child elements
 * (e.g CtComment).
 */
public class TrainerMethodProcessor extends AbstractProcessor<CtMethod> {

    @Override
    public void process(CtMethod e) {
//        String commentStr = "This javadoc is for " + e.getSimpleName();
//        CtComment c = getFactory().Code().createComment(commentStr, CtComment.CommentType.JAVADOC);
//        e.addComment(c);
//        System.out.println("Method Name: " + e.getSimpleName());
//        System.out.println("Comments: " + e.getComments());
//        System.out.println("Body: " + e.toString());

        // Get method's simple name without any package extensions
        String method_name = e.getSimpleName();

        if (isOrdinaryGetMethod(e)) {
            System.out.println("This is ordinary");
        } else {
            System.out.println("NOT ORDINARY!");
        }

        // This part is to ignore get/set methods.
        // Checkout related issue #3 on github.com/nerzid/autocomment for further info.
        // Train database using method's name and return type
        //Trainer.train(method_name, e.getType().toString());
    }

    @Override
    public boolean isToBeProcessed(CtMethod candidate) {
        return true;
    }

    /**
     * Checks whether the method is an ordinary get method or not.
     * Ordinary get method means that it only returns a class field (variable),
     * and doesn't do any operation on it.
     * 
     * e.g. ordinary get method -> return count;
     * e.g. not ordinary get method -> return count/2;
     * 
     * @param e 
     * @return True if get method is ordinary, false if not.
     */
    public boolean isOrdinaryGetMethod(CtMethod e) {
        // Get method's simple name
        String method_name = e.getSimpleName();

        // Get method's parent which is class itself
        CtClass clazz = (CtClass) e.getParent();

        // Get Flow Breakers' list. There are 2 Flow Breakers;
        // return statements and exceptions throws.
        List<CtCFlowBreak> flowBreakersList = e.getBody().getElements(new ReturnOrThrowFilter());

        // First word must be "get" in method_name
        if (Tokenizer.split(method_name).get(0).equals("get")) {
            // Get methods we want to ignore have only 1 return statement
            // and that return statement is to return one variable without any operation on it
            if (flowBreakersList.size() == 1) {
                if (flowBreakersList.get(0) instanceof CtReturn) {
                    CtReturn returnStmt = (CtReturn) flowBreakersList.get(0);
                    CtExpression returnExp = returnStmt.getReturnedExpression();

                    // in returnExp, returned variable has always brackets like
                    // e.g. (variable_name)
                    String returned_var = returnExp.toString(); 
                    List<CtVariable> var_list = clazz.getFields();

                    for (int i = 0; i < var_list.size(); i++) {
                        if (returned_var.equals(var_list.get(i).getSimpleName()))
                            return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     *
     * @param e
     * @return
     */
    public boolean isOrdinarySetMethod(CtMethod e) {
        // Get method's simple name
        String method_name = e.getSimpleName();

        // Get method's parent which is class itself
        CtClass clazz = (CtClass) e.getParent();
        
        return false;
    }

}
