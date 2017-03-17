/*
 * Copyright 2017 nerzid.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtCFlowBreak;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLoop;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.CompositeFilter;
import spoon.reflect.visitor.filter.FilteringOperator;
import spoon.reflect.visitor.filter.ReturnOrThrowFilter;
import spoon.reflect.visitor.filter.TypeFilter;

/**
 *
 * @author nerzid
 */
public class S_UnitMethodProcessor extends AbstractProcessor<CtMethod> {

    List<CtStatement> ending_units;
    List<CtInvocation> void_return_units;
    List<CtInvocation> same_action_units;

    List<CtAssignment> data_facilitating_units;
    List<CtVariableAccess> data_args;

    List<CtExpression> controlling_units;

    @Override
    public void process(CtMethod e) {

        ending_units = new ArrayList<>();
        void_return_units = new ArrayList<>();
        same_action_units = new ArrayList<>();

        data_facilitating_units = new ArrayList<>();
        data_args = new ArrayList<>();

        controlling_units = new ArrayList<>();

        endingSUnits(e);
        voidReturnSUnits(e);
        sameActionSUnits(e);
        dataFacilitatingSUnits(e);
        controllingSUnits(e);

        print(e.getSignature());
        System.out.println();
    }

    public void endingSUnits(CtMethod e) {
        List<CtCFlowBreak> flowBreakersList = e.getBody().getElements(new ReturnOrThrowFilter());
        if (flowBreakersList.size() >= 0) {
            for (CtCFlowBreak flowbreaker : flowBreakersList) {
                if (flowbreaker instanceof CtReturn) {
                    CtReturn returnStmt = (CtReturn) flowbreaker;
                    CtExpression returnExp = returnStmt.getReturnedExpression();
                    String returned_var = returnExp.toString();

//                    System.out.println("ending s_unit: " + returned_var);
                    ending_units.add(returnStmt);
                }
            }
        }
    }

    public void voidReturnSUnits(CtMethod e) {
        List<CtStatement> stmts = e.getBody().getStatements();
        for (CtStatement stmt : stmts) {
            if (stmt instanceof CtInvocation) {
//                System.out.println("void-return s_unit: " + stmt.toString());
                List<CtExpression> args = ((CtInvocation) stmt).getArguments();
                for (CtExpression arg : args) {
                    if (arg instanceof CtVariableAccess) {
//                        System.out.println("Arg: " + arg.toString() + " Type: " + arg.getType());
                        data_args.add((CtVariableAccess) arg);
                    } else if (arg instanceof CtAssignment) {
                        CtAssignment assignment = (CtAssignment) arg;
                        if (assignment.getAssigned() instanceof CtVariableAccess) {
                            data_args.add((CtVariableAccess) (assignment.getAssigned()));
                        }
                    }
                }
                void_return_units.add((CtInvocation) stmt);
            }
        }
    }

    public void sameActionSUnits(CtMethod e) {
        List<CtStatement> stmts = e.getBody().getStatements();
        HashMap<String, Integer> same_stmts = new HashMap<>();        

        for (CtStatement stmt : stmts) {
            if (stmt instanceof CtInvocation) {
                CtInvocation invoc = (CtInvocation) stmt;
                String invoc_str = invoc.toString();
//                System.out.println("Target Expression: " + invoc_str);
//                System.out.println("After regex: " + invoc_str.replaceFirst("\\(.*$", ""));
                invoc_str = invoc_str.replaceFirst("\\(.*$", "");
                if (same_stmts.containsKey(invoc_str)) {
                    same_stmts.put(invoc_str, same_stmts.get(invoc_str) + 1);
                    if (same_stmts.get(invoc_str) == 2) {
                        same_action_units.add(invoc);
                    }
                } else {
                    same_stmts.put(invoc_str, 1);
                }
            } else if (stmt instanceof CtAssignment) {

            }
        }
//        Iterator it = same_stmts.entrySet().iterator();
//
//        while (it.hasNext()) {
//            Map.Entry pair = (Map.Entry) it.next();
//            if (Integer.valueOf(pair.getValue().toString()) > 1) {
//                System.out.println("same-action s_unit: " + pair.getKey());
//                System.out.println("Count: " + pair.getValue());
//            }
//
//            it.remove();
//        }
    }

    public void dataFacilitatingSUnits(CtMethod e) {
        List<CtStatement> stmts = e.getBody().getStatements();

        for (CtStatement stmt : stmts) {
            if (stmt instanceof CtAssignment) {
                CtAssignment assigment = (CtAssignment) stmt;

                for (CtVariableAccess data_arg : data_args) {
                    if (data_arg.toString().equals(assigment.getAssigned().toString())) {
                        data_facilitating_units.add(assigment);
//                        System.out.println("data-faciliting s_unit: " + stmt.toString());
                    }
//                    System.out.println("Data: " + data_arg.toString());
//                    System.out.println("Assign: " + assign.getAssigned().toString());
                }
            }
        }
    }

    public void controllingSUnits(CtMethod e) {
        List<CtStatement> stmts = e.getBody().getStatements();
        CompositeFilter cf = new CompositeFilter(
                FilteringOperator.UNION,
                new TypeFilter(CtLoop.class),
                new TypeFilter(CtSwitch.class),
                new TypeFilter(CtIf.class));
        List<CtElement> elems = e.getElements(cf);

// conditional expression needs to be added in the future as well.
        List<CtVariableAccess> vars;
        for (CtElement elem : elems) {
            if (elem instanceof CtLoop) {
                CtLoop e_loop = (CtLoop) elem;
                if (e_loop instanceof CtFor) {
                    CtFor e_for = (CtFor) e_loop;
                    vars = e_for.getExpression().getElements(new TypeFilter(CtVariableAccess.class));
                    for (CtVariableAccess data_arg : data_args) {
                        for (CtVariableAccess var : vars) {
                            if (data_arg.getVariable().equals(var.getVariable())) {
                                controlling_units.add(e_for.getExpression());
                                break;
                            }
                        }
                    }
                } else if (e_loop instanceof CtWhile) {
                    CtWhile e_while = (CtWhile) e_loop;
                    vars = e_while.getLoopingExpression().getElements(new TypeFilter(CtVariableAccess.class));
                    for (CtVariableAccess data_arg : data_args) {
                        for (CtVariableAccess var : vars) {
                            if (data_arg.getVariable().equals(var.getVariable())) {
                                controlling_units.add(e_while.getLoopingExpression());
                                break;
                            }
                        }
                    }
                } else if (e_loop instanceof CtDo) {
                    CtDo e_do = (CtDo) e_loop;
                    vars = e_do.getLoopingExpression().getElements(new TypeFilter(CtVariableAccess.class));
                    for (CtVariableAccess data_arg : data_args) {
                        for (CtVariableAccess var : vars) {
                            if (data_arg.getVariable().equals(var.getVariable())) {
                                controlling_units.add(e_do.getLoopingExpression());
                                break;
                            }
                        }
                    }
                }
            } else if (elem instanceof CtSwitch) {
                CtSwitch e_switch = (CtSwitch) elem;
                vars = e_switch.getSelector().getElements(new TypeFilter(CtVariableAccess.class));
                for (CtVariableAccess data_arg : data_args) {
                    for (CtVariableAccess var : vars) {
                        if (data_arg.getVariable().equals(var.getVariable())) {
                            controlling_units.add(e_switch.getSelector());
                            break;
                        }
                    }
                }
            } else if (elem instanceof CtIf) {
                CtIf e_if = (CtIf) elem;
                vars = e_if.getCondition().getElements(new TypeFilter(CtVariableAccess.class));
                for (CtVariableAccess data_arg : data_args) {
                    for (CtVariableAccess var : vars) {
                        if (data_arg.getVariable().equals(var.getVariable())) {
                            controlling_units.add(e_if.getCondition());
                            break;
                        }
                    }
                }

            }

        }
    }

    public void print(String method_signature) {
        System.out.println("METHOD: " + method_signature);
        printSUnits("ENDING S_UNITS", ending_units);
        printSUnits("VOID-RETURN S_UNITS", void_return_units);
        printSUnits("SAME-ACTION S_UNITS", same_action_units);
        printSUnits("DATA-FACILITATING S_UNITS", data_facilitating_units);
        printSUnits("CONTROLLING S_UNITS", controlling_units);
    }

    public void printSUnits(String s_unit_type_name, List<?> elems) {
        System.out.println(s_unit_type_name);
        Iterator it = elems.iterator();
        if (elems.isEmpty()) {
            System.out.println("none");
        } else {
            for (Iterator<? extends Object> iterator = elems.iterator(); iterator.hasNext();) {
                Object next = iterator.next();
                System.out.println("----> " + next);
            }
        }
        System.out.println();
    }
}
