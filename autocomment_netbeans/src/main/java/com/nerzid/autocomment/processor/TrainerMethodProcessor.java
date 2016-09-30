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
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
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

        System.out.println(method_name);
        System.out.println(isOrdinarySetMethod(e));
        
        // This part is to ignore get/set methods.
        // Checkout related issue #3 on github.com/nerzid/autocomment for further info.
        if (!isOrdinaryGetMethod(e) && !isOrdinarySetMethod(e)) {
            // Train database using method's name and return type
            //Trainer.train(method_name, e.getType().toString());
        }

    }

    @Override
    public boolean isToBeProcessed(CtMethod candidate) {
        return true;
    }

    /**
     * Checks whether the method is an ordinary get method or not. Ordinary get
     * method means that it only returns a class field (variable), and doesn't
     * do any operation on it.
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
        List<CtVariable> class_var_list = clazz.getFields();

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
                    String returned_var = returnExp.toString();

                    // returned variable_name must exist in class variables list
                    // if it is, then it is an ordinary get method
                    for (int i = 0; i < class_var_list.size(); i++) {
                        if (returned_var.equals(class_var_list.get(i).getSimpleName())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks whether the method is an ordinary set method or not. Ordinary set
     * method means that it only sets a class field (variable)'s with a new one, 
     * and doesn't do any more operation on it.
     * 
     * e.g. ordinary set method -> this.value = value; or value = newValue;
     * e.g. not ordinary set method -> this.value = value+1; or value = newValue/2;
     * 
     * @param e
     * @return True if set method is ordinary, false if not.
     */
    public boolean isOrdinarySetMethod(CtMethod e) {
        // Get method's simple name
        String method_name = e.getSimpleName();

        // Get method's parent which is class itself
        CtClass clazz = (CtClass) e.getParent();
        List<CtVariable> class_var_list = clazz.getFields();
        List<CtParameter> param_list = e.getParameters();

        if (Tokenizer.split(method_name).get(0).equals("set")) {
            if (e.getType().getSimpleName().equals("void")) {
                if (e.getParameters().size() == 1) {
                    List<CtStatement> stmt_list = e.getBody().getStatements();
                    if (stmt_list.size() == 1) {
                        CtStatement stmt = stmt_list.get(0);

                        // There is always equation after first token('=' after this.panel)
                        // e.g. this.panel = panel
                        // e.g. panel = newPanel
                        String[] tokens = stmt.toString().split(" = ");
                        String token_var = tokens[0];
                        
                        // Although when we put -> "this." before variable name
                        // It's actually like that -> "package_name.class_name.this.variable_name"
                        // But we need only the variable name so we get last string before dot.
                        token_var = Tokenizer.getLastStringBeforeDot(token_var);
                        String token_param = tokens[1];
                        
                        // First token must be class variable
                        boolean isFirstTokenClassVar = false;
                        
                        for (CtVariable var : class_var_list) {
                            if (var.getSimpleName().equals(token_var)) {
                                isFirstTokenClassVar = true;
                                break;
                            }
                        }

                        // Second token must be parameter
                        if (isFirstTokenClassVar) {
                            for (CtParameter param : param_list) {
                                if (param.getSimpleName().equals(token_param)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

}
