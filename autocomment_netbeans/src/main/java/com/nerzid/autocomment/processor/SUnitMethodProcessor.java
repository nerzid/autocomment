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

import com.nerzid.autocomment.database.MethodTable;
import com.nerzid.autocomment.nlp.NLPToolkit;
import com.nerzid.autocomment.nlp.Tokenizer;
import com.nerzid.autocomment.sunit.*;

import java.util.*;

import com.nerzid.autocomment.template.SUnitCommentTemplate;
import com.nerzid.autocomment.template.Test;
import com.nerzid.autocomment.template.VoidReturnSUnitCT;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.visitor.filter.*;
import spoon.processing.AbstractProcessor;

/**
 * @author nerzid
 */
public class SUnitMethodProcessor extends AbstractProcessor<CtMethod> {

    public static int totalIndependentIfCount = 0;
    public static int totalDepthCount = 0;
    public static int totalIfCount = 0;
    public static List<CtExpression> actual_conditions = new ArrayList<>();

    @Override
    public void process(CtMethod e) {

        if (e.getBody() != null) {

            getIfCount(e);

            // Clear lists for every method.
            SUnit.clearAndInitSUnitLists();

            sameActionSUnits(e);
            endingSUnits(e);
            voidReturnSUnits(e);

            controllingSUnits(e);
            dataFacilitatingSUnits(e);

            print(e.getSignature());
            System.out.println();

            createComments();

        }
    }

    public void endingSUnits(CtMethod e) {
        if (!e.getType().toString().equals("void")) {
            List<CtCFlowBreak> flowBreakersList = e.getBody().getElements(new ReturnOrThrowFilter());
            if (flowBreakersList.size() >= 0) {
                for (CtCFlowBreak flowbreaker : flowBreakersList) {
                    if (flowbreaker instanceof CtReturn) {
                        CtReturn returnStmt = (CtReturn) flowbreaker;
//                        CtExpression returnExp = returnStmt.getReturnedExpression();
//                        String returned_var = returnExp.toString();

                        FunctionSUnit sunit = new EndingSUnit(returnStmt);

                        List<CtVariableAccess> vars = returnStmt.getElements(new TypeFilter(CtVariableAccess.class));
                        for (CtVariableAccess var : vars) {
                            sunit.addDataVar(var);
                        }
                    }
                }
            }
        } else {
            if (e.getBody().getStatements().size() > 0) {
                CtStatement lastStatement = e.getBody().getLastStatement();
                FunctionSUnit sunit = new EndingSUnit(lastStatement);

                List<CtVariableAccess> vars = lastStatement.getElements(new TypeFilter(CtVariableAccess.class));
                for (CtVariableAccess var : vars) {
                    sunit.addDataVar(var);
                }
            }
        }
    }

    public void voidReturnSUnits(CtMethod e) {
        List<CtStatement> stmts = e.getBody().getStatements();
        for (CtStatement stmt : stmts) {
            if (stmt instanceof CtInvocation) {
                if (!SUnit.isElementExists(stmt, SUnitType.SAME_ACTION_SEQUENCE)) {
                    FunctionSUnit sunit = new VoidReturnSUnit(stmt);
                    List<CtExpression> args = ((CtInvocation) stmt).getArguments();
                    for (CtExpression arg : args) {
                        if (arg instanceof CtVariableAccess) {
                            sunit.addDataVar((CtVariableAccess) arg);
//                        data_args.add((CtVariableAccess) arg);
                        } else if (arg instanceof CtAssignment) {
                            CtAssignment assignment = (CtAssignment) arg;
                            if (assignment.getAssigned() instanceof CtVariableAccess) {
                                sunit.addDataVar((CtVariableAccess) assignment.getAssigned());
//                            data_args.add((CtVariableAccess) (assignment.getAssigned()));
                            }
                        }
                    }
//                void_return_units.add((CtInvocation) stmt);
                }
            }
        }
    }

    // TO DO
    // ADD CODE TO GET DATA VARS FROM SAME ACTION SEQUENCE SUNITS
    public void sameActionSUnits(CtMethod e) {
        List<CtStatement> stmts = e.getBody().getStatements();
        HashMap<String, Integer> same_stmts = new HashMap<>();
        List<CtInvocation> invocs = new ArrayList<>();
        for (CtStatement stmt : stmts) {
            if (stmt instanceof CtInvocation) {
                CtInvocation invoc = (CtInvocation) stmt;
                String invoc_str = invoc.toString();

                // get only the invoked method
                invoc_str = invoc_str.replaceFirst("\\(.*$", "");
                invocs.add(invoc);
                if (same_stmts.containsKey(invoc_str)) {
                    same_stmts.put(invoc_str, same_stmts.get(invoc_str) + 1);
                    if (same_stmts.get(invoc_str) >= 2) {
                        for (CtInvocation invocation : invocs) {
                            String invocation_str = invocation.toString().replaceFirst("\\(.*$", "");
                            if (invocation_str.equals(invoc_str)) {
                                if (!SUnit.isElementExists(invocation, SUnitType.SAME_ACTION_SEQUENCE)) {
                                    SameActionSequenceSUnit sunit = new SameActionSequenceSUnit(invocation);
                                    sunit.addDataVars();
                                    sunit.addDataVars(invoc);
                                } else {
                                    for (SameActionSequenceSUnit sunit : SUnit.getSameActionSequenceSUnits()) {
                                        System.out.println();
                                        if (sunit.getElement().toString().replaceFirst("\\(.*$", "").equals(invoc_str)) {
                                            sunit.addDataVars(invoc);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    same_stmts.put(invoc_str, 1);
                }
            } else if (stmt instanceof CtAssignment) {

            }
        }
    }

    public void dataFacilitatingSUnits(CtMethod e) {
        Collection<FunctionSUnit> fsunits = new ArrayList<>();
        fsunits.addAll(SUnit.getEndingSUnits());
        fsunits.addAll(SUnit.getVoidReturnSUnits());
        fsunits.addAll(SUnit.getSameActionSequenceSUnits());

        for (FunctionSUnit fsunit : fsunits) {
            fsunit.dataVarsToFacilitators(e, fsunit);
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
                    for (DataFacilitatorSUnit data_sunit : FunctionSUnit.getDataFacilitatorSUnits()) {
                        for (CtVariableAccess var : vars) {
                            if (data_sunit.getDataVar().getVariable().equals(var.getVariable())) {
                                if (!SUnit.isElementExists(e_for.getExpression(), SUnitType.CONTROLLING)) {
                                    new ControllingSUnit(e_for.getExpression());
                                    break;
                                }
                            }
                        }
                    }
                } else if (e_loop instanceof CtWhile) {
                    CtWhile e_while = (CtWhile) e_loop;
                    vars = e_while.getLoopingExpression().getElements(new TypeFilter(CtVariableAccess.class));
                    for (DataFacilitatorSUnit data_sunit : FunctionSUnit.getDataFacilitatorSUnits()) {
                        for (CtVariableAccess var : vars) {
                            if (data_sunit.getDataVar().getVariable().equals(var.getVariable())) {
                                if (!SUnit.isElementExists(e_while.getLoopingExpression(), SUnitType.CONTROLLING)) {
                                    new ControllingSUnit(e_while.getLoopingExpression());
                                    break;
                                }
                            }
                        }
                    }
                } else if (e_loop instanceof CtDo) {
                    CtDo e_do = (CtDo) e_loop;
                    vars = e_do.getLoopingExpression().getElements(new TypeFilter(CtVariableAccess.class));
                    for (DataFacilitatorSUnit data_sunit : FunctionSUnit.getDataFacilitatorSUnits()) {
                        for (CtVariableAccess var : vars) {
                            if (data_sunit.getDataVar().getVariable().equals(var.getVariable())) {
                                if (!SUnit.isElementExists(e_do.getLoopingExpression(), SUnitType.CONTROLLING)) {
                                    new ControllingSUnit(e_do.getLoopingExpression());
                                    break;
                                }
                            }
                        }
                    }
                }
            } else if (elem instanceof CtSwitch) {
                CtSwitch e_switch = (CtSwitch) elem;
                vars = e_switch.getSelector().getElements(new TypeFilter(CtVariableAccess.class));
                for (DataFacilitatorSUnit data_sunit : FunctionSUnit.getDataFacilitatorSUnits()) {
                    for (CtVariableAccess var : vars) {
                        if (data_sunit.getDataVar().getVariable().equals(var.getVariable())) {
                            if (!SUnit.isElementExists(e_switch.getSelector(), SUnitType.CONTROLLING)) {
                                new ControllingSUnit(e_switch.getSelector());
                                break;
                            }
                        }
                    }
                }
            } else if (elem instanceof CtIf) {
                CtIf e_if = (CtIf) elem;
                vars = e_if.getCondition().getElements(new TypeFilter(CtVariableAccess.class));
                for (DataFacilitatorSUnit data_sunit : FunctionSUnit.getDataFacilitatorSUnits()) {
                    for (CtVariableAccess var : vars) {
                        if (data_sunit.getDataVar().getVariable().equals(var.getVariable())) {
                            if (!SUnit.isElementExists(e_if.getCondition(), SUnitType.CONTROLLING)) {
                                new ControllingSUnit(e_if.getCondition());
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public void print(String method_signature) {
        System.out.println("METHOD: " + method_signature + "\n");
        printSUnits("ENDING S_UNITS", SUnit.getEndingSUnits());
        printSUnits("VOID-RETURN S_UNITS", SUnit.getVoidReturnSUnits());
        printSUnits("SAME-ACTION-SEQUENCE S_UNITS", SUnit.getSameActionSequenceSUnits());
        printSUnits("DATA-FACILITATING S_UNITS", SUnit.getDataFacilitatorSUnits());
        printSUnits("CONTROLLING S_UNITS", SUnit.getControllingSUnits());
    }

    public void printSUnits(String s_unit_type_name, Collection<? extends SUnit> elems) {
        System.out.println(s_unit_type_name);
        if (elems.isEmpty()) {
            System.out.println("none");
        } else {
            for (SUnit next : elems) {
                System.out.println("---------------------------------------------------------------------------------------------------\n" + next);
            }
        }
        System.out.println();
        System.out.println();
    }

    private List<CtIf> getIfStmtsFromBlock(CtBodyHolder body_holder) {
        List<CtIf> ifs_from_body_list = new ArrayList<>();
        List<CtBodyHolder> trys_from_body_list = new ArrayList<>();
        if (body_holder != null) {

            List<CtStatement> stmts = ((CtBlock) body_holder.getBody()).getStatements();
            boolean foundIfStmt = false;
            for (CtStatement stmt : stmts) {
                if (stmt instanceof CtIf) {
                    foundIfStmt = true;
                    ifs_from_body_list.add((CtIf) stmt);
                } else if (stmt instanceof CtBodyHolder) {
                    trys_from_body_list.add((CtBodyHolder) stmt);
                }
            }
            if (!foundIfStmt) {
                for (CtBodyHolder stmt : trys_from_body_list) {
                    ifs_from_body_list.addAll(getIfStmtsFromBlock(stmt));
                }
            }
        }
        return ifs_from_body_list;
    }

    public List<CtIf> prepareIfStmtsList(List<CtBodyHolder> body_holders_list) {
        List<CtIf> if_stmts = new ArrayList<>();
        for (CtBodyHolder body_holder : body_holders_list) {
            List<CtStatement> stmts = ((CtBlock) body_holder.getBody()).getStatements();
            for (CtStatement stmt : stmts) {
                if (stmt instanceof CtBodyHolder) {
                    if_stmts.addAll(getIfStmtsFromBlock((CtBodyHolder) stmt));
                } else if (stmt instanceof CtIf) {
                    if_stmts.add((CtIf) stmt);
                }
            }
        }
        return if_stmts;
    }

    public void getIfCount(CtMethod e) {
        List<CtIf> if_stmts = new ArrayList<>();
        if_stmts.addAll(prepareIfStmtsList(Collections.singletonList(e)));
        for (CtIf if_stmt : if_stmts) {
            int depth;
            if (if_stmt instanceof CtIf) {
                depth = 1;
                totalIndependentIfCount++;
                totalIfCount++;
                depth = getIfCountRecursively(if_stmt, depth);
                totalDepthCount += depth;
            } else {
                System.out.println("EEEE ??");
            }
        }
    }

    private int getIfCountRecursively(CtStatement thenStatement, int depth) {
        int max_depth = 0;
        int if_depth;
        int else_depth = 0;
        if (thenStatement instanceof CtIf) {
            prepareConditionList((CtIf) thenStatement);
            if_depth = getIfCountRecursively(((CtIf) thenStatement).getThenStatement(), depth);
            if (((CtIf) thenStatement).getElseStatement() != null) {
                else_depth = getIfCountRecursively(((CtIf) thenStatement).getElseStatement(), depth);
            }
            if (if_depth > else_depth) {
                max_depth = if_depth;
            } else {
                max_depth = else_depth;
            }
            return max_depth;
        } else if (thenStatement instanceof CtBlock) {
            CtBlock stmt_block = (CtBlock) thenStatement;
            for (CtStatement stmt : stmt_block.getStatements()) {
                if (stmt instanceof CtIf) {
                    totalIfCount++;
                    prepareConditionList((CtIf) stmt);
                    if_depth = getIfCountRecursively(((CtIf) stmt).getThenStatement(), depth + 1);
                    if (((CtIf) stmt).getElseStatement() != null) {
                        else_depth = getIfCountRecursively(((CtIf) stmt).getElseStatement(), depth + 1);
                    }
                    if (if_depth > else_depth) {
                        max_depth = if_depth;
                    } else {
                        max_depth = else_depth;
                    }
                } else if (stmt instanceof CtBodyHolder) {
                    for (CtIf if_stmt : prepareIfStmtsList(Collections.singletonList((CtBodyHolder) stmt))) {
                        totalIfCount++;
                        prepareConditionList(if_stmt);
                        if_depth = getIfCountRecursively(if_stmt.getThenStatement(), depth + 1);
                        if (if_stmt.getElseStatement() != null) {
                            else_depth = getIfCountRecursively(if_stmt.getElseStatement(), depth + 1);
                        }
                        if (if_depth > else_depth) {
                            max_depth = if_depth;
                        } else {
                            max_depth = else_depth;
                        }
                    }
                }
            }
            if (max_depth == 0) {
                return depth;
            } else {
                return max_depth;
            }
        } else {
            return depth;
        }
    }

    public static double calculateAverageIfDepth() {
        System.out.println("Total Independent If Statement Count: " + totalIndependentIfCount);
        System.out.println("Total If Statement Depth: " + totalDepthCount);
        return totalDepthCount * 1.0 / totalIndependentIfCount;
    }

    public static double calculateAverageOfIfConditions() {
        System.out.println("Total If Statement Count: " + totalIfCount);
        System.out.println("Total Conditions Count: " + actual_conditions.size());
        return actual_conditions.size() * 1.0 / totalIfCount;
    }

    public static void prepareConditionList(CtIf if_stmt) {
        CtExpression<Boolean> exp_condition = if_stmt.getCondition();
        prepareConditionListRecursively(exp_condition);
    }

    private static void prepareConditionListRecursively(CtExpression exp_condition) {
        if (exp_condition instanceof CtBinaryOperator) {
            CtBinaryOperator binary_condition = (CtBinaryOperator) exp_condition;
            if (binary_condition.getKind() == BinaryOperatorKind.AND || binary_condition.getKind() == BinaryOperatorKind.OR) {
                prepareConditionListRecursively(binary_condition.getLeftHandOperand());
                prepareConditionListRecursively(binary_condition.getRightHandOperand());
            } else {
                actual_conditions.add(exp_condition);
            }

        } else if (exp_condition instanceof CtUnaryOperator) {
            CtUnaryOperator unary_condition = (CtUnaryOperator) exp_condition;
            if (unary_condition.getKind() == UnaryOperatorKind.NOT) {
                actual_conditions.add(exp_condition);
            }

        } else if (exp_condition instanceof CtVariableAccess) {
            CtVariableAccess variable_condition = (CtVariableAccess) exp_condition;
            if (variable_condition.getType() != null) {
                if (variable_condition.getType().toString().equalsIgnoreCase("boolean")) {
                    actual_conditions.add(exp_condition);
                }
            }
        } else if (exp_condition instanceof CtInvocation) {
            CtInvocation invocation_condition = (CtInvocation) exp_condition;
            if (invocation_condition.getType() != null) {
                if (invocation_condition.getType().toString().equalsIgnoreCase("boolean")) {
                    actual_conditions.add(exp_condition);
                }
            }
        }
    }

    public void createComments() {
        for (VoidReturnSUnit voidReturnSUnit : FunctionSUnit.getVoidReturnSUnits()) {
            CtElement element = voidReturnSUnit.getElement();
            if (element instanceof CtInvocation) {
                CtInvocation invoc = (CtInvocation) element;
                String method_name = invoc.getExecutable().getSimpleName();
                CtExpression target = invoc.getTarget();

                List<CtExpression> params = invoc.getArguments();
//
//                List<String> verbs = Tokenizer.split(verb);
//                verb = verbs.get(0);
//                if (verbs.size() > 1) {
//                    verb += " " + verbs.get(1);
//                }
//
//                String commentStr = verb + " " + params.get(0).getType().getSimpleName() + "{" +
//                        params.get(0).toString() + "}"
//                        + " to " + target + "{" + invoc.getTarget().toString() + "}";
                MethodTable mt = NLPToolkit.getMethodWithProperties("", method_name, 0);

                String postag = mt.getPostag();
                String splitted_identifier = mt.getSplittedIdentifier();
                SUnitCommentTemplate sunit_ct = new VoidReturnSUnitCT(voidReturnSUnit);
                String commentStr = sunit_ct.prepareThenGetComment(SUnitType.VOID_RETURN, target, postag, splitted_identifier.split(" "), params);

                CtComment c = getFactory().Code().createComment(commentStr, CtComment.CommentType.INLINE);
                invoc.addComment(c);
            }
        }

        for (SameActionSequenceSUnit sunit : FunctionSUnit.getSameActionSequenceSUnits()) {
            CtElement element = sunit.getElement();
            if (element instanceof CtInvocation) {
                CtInvocation invoc = (CtInvocation) element;
                String method_name = invoc.getExecutable().getSimpleName();
                CtExpression target = invoc.getTarget();

                List<CtExpression> params = invoc.getArguments();
//
//                List<String> verbs = Tokenizer.split(verb);
//                verb = verbs.get(0);
//                if (verbs.size() > 1) {
//                    verb += " " + verbs.get(1);
//                }
//
//                String commentStr = verb + " " + params.get(0).getType().getSimpleName() + "{" +
//                        params.get(0).toString() + "}"
//                        + " to " + target + "{" + invoc.getTarget().toString() + "}";
                MethodTable mt = NLPToolkit.getMethodWithProperties("", method_name, 0);

                String postag = mt.getPostag();
                String splitted_identifier = mt.getSplittedIdentifier();
                SUnitCommentTemplate sunit_ct = new VoidReturnSUnitCT(sunit);
                String commentStr = sunit_ct.prepareThenGetComment(SUnitType.VOID_RETURN, target, postag, splitted_identifier.split(" "), params);

                CtComment c = getFactory().Code().createComment(commentStr, CtComment.CommentType.INLINE);
                invoc.addComment(c);
            }
        }
    }
}
