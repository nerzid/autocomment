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
package com.nerzid.autocomment.sunit;

import java.lang.reflect.Method;
import java.util.*;

import com.nerzid.autocomment.model.Comment;
import com.nerzid.autocomment.template.MethodCommentTemplate;
import jdk.internal.org.objectweb.asm.TypeReference;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.CompositeFilter;
import spoon.reflect.visitor.filter.FilteringOperator;
import spoon.reflect.visitor.filter.TypeFilter;

/**
 * @author nerzid
 */
public abstract class SUnit implements Comparable<SUnit> {

    protected CtElement element;
    protected SUnitType sunitType;
    protected String comment;

    protected static PriorityQueue<EndingSUnit> endingSUnits;
    protected static PriorityQueue<VoidReturnSUnit> voidReturnSUnits;
    protected static PriorityQueue<SameActionSequenceSUnit> sameActionSequenceSUnits;
    protected static PriorityQueue<ControllingSUnit> controllingSUnits;
    protected static PriorityQueue<DataFacilitatorSUnit> dataFacilitatorSUnits;

    static {
        clearAndInitSUnitLists();
    }

    public SUnit(CtElement element) {
        this.element = element;
    }

    public SUnit() {
    }

    public CtElement getElement() {
        return element;
    }

    public String getComment() {
        return comment;
    }

    public SUnitType getSUnitType() {
        return sunitType;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setElement(CtElement element) {
        this.element = element;
    }

    public static Collection<EndingSUnit> getEndingSUnits() {
        return endingSUnits;
    }

    public static Collection<VoidReturnSUnit> getVoidReturnSUnits() {
        return voidReturnSUnits;
    }

    public static Collection<SameActionSequenceSUnit> getSameActionSequenceSUnits() {
        return sameActionSequenceSUnits;
    }

    public static Collection<ControllingSUnit> getControllingSUnits() {
        return controllingSUnits;
    }

    public static Collection<DataFacilitatorSUnit> getDataFacilitatorSUnits() {
        return dataFacilitatorSUnits;
    }

    public static boolean isElementExists(CtElement e, SUnitType type) {
        Collection<? extends SUnit> elems;
        switch (type) {
            case CONTROLLING:
                elems = controllingSUnits;
                break;
            case ENDING:
                elems = endingSUnits;
                break;
            case VOID_RETURN:
                elems = voidReturnSUnits;
                break;
            case SAME_ACTION_SEQUENCE:
                elems = sameActionSequenceSUnits;
                break;
            default:
                return false;
        }
        if (type == SUnitType.SAME_ACTION_SEQUENCE) {
            for (SUnit elem : elems) {
                SameActionSequenceSUnit sassunit = (SameActionSequenceSUnit) elem;
                if (sassunit.getElement().toString().replaceFirst("\\(.*$", "").equals(e.toString().replaceFirst("\\(.*$", ""))) {
                    return true;
                }
            }
        } else {
            for (SUnit elem : elems) {
                if (elem.getElement().equals(e)) {
                    return true;
                }
            }
        }
        return false;
    }

//    @Override
//    public String toString() {
//        return getClass().getSimpleName() + "{" + "element=> " + element + '}';
//    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.element);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SUnit other = (SUnit) obj;
        return Objects.equals(this.element, other.element);
    }

    @Override
    public int compareTo(SUnit o) {
        if (this.element.getPosition().getLine() > o.element.getPosition().getLine()) {
            return 1;
        } else if (this.element.getPosition().getLine() < o.element.getPosition().getLine()) {
            return -1;
        } else {
            return 0;
        }
    }

    public static SUnitStorage getSUnitStorage() {
        return new SUnitStorage(endingSUnits, voidReturnSUnits, sameActionSequenceSUnits, controllingSUnits, dataFacilitatorSUnits);
    }

    public static void clearAndInitSUnitLists() {
        endingSUnits = new PriorityQueue<>();
        voidReturnSUnits = new PriorityQueue<>();
        sameActionSequenceSUnits = new PriorityQueue<>();
        controllingSUnits = new PriorityQueue<>();
        dataFacilitatorSUnits = new PriorityQueue<>();
    }

//    @Override
//    public String toString() {
//        return getUnknownTypedElementComment(element, false);
//    }


    public String getUnknownTypedElementComment(CtElement element, boolean isPassiveSentence, MethodCommentTemplate methodCommentTemplate) {
        if (element instanceof CtReturn)
            return getReturnComment(element, false, methodCommentTemplate);
        else if (element instanceof CtThrow)
            return getThrowComment(element, isPassiveSentence,methodCommentTemplate);
        else if (element instanceof CtConstructorCall)
            return getContructorCallComment(element, isPassiveSentence,methodCommentTemplate);
        else if (element instanceof CtAssignment)
            return getAssignmentComment(element, isPassiveSentence,methodCommentTemplate);
        else if (element instanceof CtInvocation)
            return getInvocationComment(element, isPassiveSentence,methodCommentTemplate);
        else if (element instanceof CtVariableAccess)
            return getVariableAccessComment(element,methodCommentTemplate);
        else if (element instanceof CtLiteral)
            return getLiteralComment(element,methodCommentTemplate);
        else
            return element.toString();
    }

    private String getInvocationComment(CtElement element, boolean isPassiveSentence, MethodCommentTemplate methodCommentTemplate) {
        String result = "";
        CtInvocation invocation = (CtInvocation) element;

//        result += MethodCommentTemplate.getInvocationStatementComment(method_name, params, isPassiveSentence, true);
        CtInvocation invoc_tmp = invocation;
        List<CtExecutableReference> invocationList = new ArrayList<>();
//        invocationList.add(invoc_tmp);
        while (invoc_tmp != null) {
            if (invoc_tmp.getTarget() != null) {
                CtExpression target = invoc_tmp.getTarget();
                if (target instanceof CtInvocation) {
                    CtInvocation target_tmp = (CtInvocation) target;
                    if (((CtInvocation) target).getExecutable() != null) {
                        invocationList.add(invoc_tmp.getExecutable());
//                        result += " of the " + getInvocationComment(invocation.getTarget(), true);
                        invoc_tmp = target_tmp;
                    } else {
                        result += MethodCommentTemplate.getInvocationStatementComment(invocationList, methodCommentTemplate, null, this, isPassiveSentence);
                        return result;
                    }
                } else {
                    if ( invoc_tmp.getExecutable() != null)
                        invocationList.add(invoc_tmp.getExecutable());
                    String target_string = getUnknownTypedElementComment(invoc_tmp.getTarget(), false, methodCommentTemplate);
                    result += MethodCommentTemplate.getInvocationStatementComment(invocationList, methodCommentTemplate, target_string, this, isPassiveSentence);
                    return result;
                }
            } else {
                return result;
            }
        }
        return result;
    }

    private boolean isPrimitiveType(CtTypeReference type) {
        if (type.getSimpleName().equalsIgnoreCase("string")) {
            return true;
        }
        String boxed = type.box().getSimpleName();
        String unboxed = type.unbox().getSimpleName();
        return !((unboxed.equalsIgnoreCase(type.getSimpleName())) && (boxed.equalsIgnoreCase(type.getSimpleName())));
    }

    private String getLiteralComment(CtElement element, MethodCommentTemplate methodCommentTemplate) {
        CtLiteral literal = (CtLiteral) element;
        return literal.toString();
    }

    private String getVariableAccessComment(CtElement element, MethodCommentTemplate methodCommentTemplate) {
        CtVariableAccess variableAccess = (CtVariableAccess) element;
        if (variableAccess.getType() == null) {
            return variableAccess.toString();
        } else {
            if (isPrimitiveType(variableAccess.getType())) {
                return variableAccess.getVariable().getSimpleName();
            } else
//                return variableAccess.getType().getSimpleName() + " " + ;
                return variableAccess.getVariable().getSimpleName();
        }
    }

    private String getAssignmentComment(CtElement element, boolean isPassiveSentence, MethodCommentTemplate methodCommentTemplate) {
        CtExpression assigned = ((CtAssignment) element).getAssigned();
        CtExpression assignment = ((CtAssignment) element).getAssignment();
        if (isPassiveSentence)
            return "which " + getUnknownTypedElementComment(assigned, true, methodCommentTemplate) + " is assigned by " + getUnknownTypedElementComment(assignment, true,  methodCommentTemplate);
        else
            return getUnknownTypedElementComment(assignment, true, methodCommentTemplate) + " is assigned to " + getUnknownTypedElementComment(assigned, true, methodCommentTemplate);
    }

    private String getReturnComment(CtElement element, boolean isPassiveSentence, MethodCommentTemplate methodCommentTemplate) {
        String res = "";
        CtReturn returnStmt = (CtReturn) element;
        CtExpression returnExp = returnStmt.getReturnedExpression();
        if (isPassiveSentence)
            res += "expression that is returned as " + getUnknownTypedElementComment(returnExp, true, methodCommentTemplate);
        else
            res += "returns the " + getUnknownTypedElementComment(returnExp, true, methodCommentTemplate);
        return res;
    }

    private String getThrowComment(CtElement element, boolean isPassiveSentence, MethodCommentTemplate methodCommentTemplate) {
        String res = "";
        CtThrow throwElem = (CtThrow) element;
        if (isPassiveSentence)
            res += "exception is thrown as " + throwElem.getThrownExpression().getType().getSimpleName();
        else
            res += "throws " + throwElem.getThrownExpression().getType().getSimpleName();
        return res;
    }

    private String getContructorCallComment(CtElement element, boolean isPassiveSentence, MethodCommentTemplate methodCommentTemplate) {
        String res = "";
        CtConstructorCall constructorCall = (CtConstructorCall) element;
        List<CtExpression> arguments = constructorCall.getArguments();
        if (isPassiveSentence)
            res += "new instance that is created as " + constructorCall.getType().getSimpleName() + " ";
        else
            res += "creates new " + constructorCall.getType().getSimpleName() + " instance ";
        if (arguments.size() > 0) {
            res += "using the ";
            for (CtExpression argument : arguments)
                res += getUnknownTypedElementComment(argument, true, methodCommentTemplate);
        }
        return res;
    }

    public Comment getSUnitComment(PriorityQueue<ControllingSUnit> controllingSUnits, MethodCommentTemplate methodCommentTemplate) {
        Comment comment = new Comment();
        CtElement controlling_parent = element.getParent(new CompositeFilter(
                FilteringOperator.UNION,
                new TypeFilter(CtLoop.class),
                new TypeFilter(CtSwitch.class),
                new TypeFilter(CtIf.class)));
        if (controlling_parent != null){
            if(controlling_parent instanceof CtIf){
                CtIf control_if = (CtIf) controlling_parent;
                CtElement else_elem = control_if.getElseStatement();
                boolean isElseStmt = false;
                if (element.hasParent(else_elem))
                    isElseStmt = true;
                comment.appendComment(getControlElementComment(control_if, isElseStmt, methodCommentTemplate));
                String sunitString = getUnknownTypedElementComment(element, false, methodCommentTemplate);
                if (isElseStmt)
                    comment.appendTextToAllImportantStatements(", " + sunitString);
                else
                    comment.appendTextToAllImportantStatements(", then " + sunitString);
            } else {
                comment.appendComment(getControlElementComment(controlling_parent, false, methodCommentTemplate));
                String sunitString = getUnknownTypedElementComment(element, false, methodCommentTemplate);
                comment.appendTextToAllImportantStatements(", then " + sunitString);
            }
        } else {
            comment.addImportantStatement(getUnknownTypedElementComment(element, false, methodCommentTemplate));
        }

        return comment;
    }

    public Comment getControllingSUnitComments(CtElement elem, PriorityQueue<ControllingSUnit> controllingSUnits, MethodCommentTemplate methodCommentTemplate) {
        Comment comment = new Comment();
        CompositeFilter compositeFilterForControls = new CompositeFilter(
                FilteringOperator.UNION,
                new TypeFilter(CtLoop.class),
                new TypeFilter(CtSwitch.class),
                new TypeFilter(CtIf.class));
        CtElement tmpControlElem = elem;
        boolean hasParent = true;
        while (hasParent) {
            CtElement controlElem = null;
            try {
                controlElem = tmpControlElem.getParent(compositeFilterForControls);
                if (controlElem != null) {
                    boolean isContains = false;
                    for (ControllingSUnit sUnit : controllingSUnits) {
                        if (sUnit.getElement().toString().contains(controlElem.toString()))
                            isContains = true; break;
                    }
                    if (isContains) {
                        comment.appendComment(getControlElementComment(controlElem, false, methodCommentTemplate));
                    }
                } else {
                    return comment;
                }
            } catch (ParentNotInitializedException e) {
                hasParent = false;
            }
            tmpControlElem = controlElem;
        }
        return comment;
    }

    protected Comment getControlElementComment(CtElement controllingElement, boolean elseStatement, MethodCommentTemplate methodCommentTemplate) {
        Comment comment = new Comment();
        String importantStatement = "";
        if (controllingElement instanceof CtIf) {
            if (elseStatement)
                importantStatement += "unless ";
            else
                importantStatement += "if ";
            CtIf ifElem = (CtIf) controllingElement;
            CtExpression<Boolean> expElem = ifElem.getCondition();
            importantStatement += getConditionRecursively(expElem, methodCommentTemplate);
        } else if (controllingElement instanceof CtSwitch) {
            CtSwitch switchElem = (CtSwitch) controllingElement;
            CtExpression<Boolean> expElem = switchElem.getSelector();
            importantStatement += getConditionRecursively(expElem, methodCommentTemplate);
        } else if (controllingElement instanceof CtLoop) {
            importantStatement += "while ";
            CtLoop loopElem = (CtLoop) controllingElement;
            if (loopElem instanceof CtFor) {
                CtFor forElem = (CtFor) loopElem;
                importantStatement += getConditionRecursively(forElem.getExpression(), methodCommentTemplate);
            } else if (loopElem instanceof CtWhile || loopElem instanceof CtDo) {
                CtExpression<Boolean> expElem;
                if (loopElem instanceof CtWhile) {
                    expElem = ((CtWhile) loopElem).getLoopingExpression();
                } else {
                    expElem = ((CtDo) loopElem).getLoopingExpression();
                }
                importantStatement += getConditionRecursively(expElem, methodCommentTemplate);
            }
        }
        comment.addImportantStatement(importantStatement);
        return comment;
    }

    private String getConditionRecursively(CtExpression<Boolean> expElem, MethodCommentTemplate methodCommentTemplate) {
        String importantStatement = "";
        if (expElem instanceof CtUnaryOperator) {
            CtUnaryOperator unaryElem = (CtUnaryOperator) expElem;
            if (unaryElem.getKind() == UnaryOperatorKind.NOT) {
                CtExpression operandExp = unaryElem.getOperand();
                if (operandExp instanceof CtBinaryOperator) {
                    CtExpression leftOperand = ((CtBinaryOperator) operandExp).getLeftHandOperand();
                    CtExpression rightOperand = ((CtBinaryOperator) operandExp).getRightHandOperand();
                    // TODO Alttakinin "is not true" şeklinde değişmesi gerekebilir
                    importantStatement += getConditionRecursively(leftOperand, methodCommentTemplate) + " is not equal to " + getConditionRecursively(rightOperand, methodCommentTemplate);
                } else{
                    importantStatement += getUnknownTypedElementComment(operandExp,false,methodCommentTemplate);
                }
            }
        } else if (expElem instanceof CtBinaryOperator) {
            CtBinaryOperator binaryElem = (CtBinaryOperator) expElem;
            CtExpression leftOperand = binaryElem.getLeftHandOperand();
            CtExpression rightOperand = binaryElem.getRightHandOperand();
            if (binaryElem.getKind() == BinaryOperatorKind.NE) {
                importantStatement += getConditionRecursively(leftOperand, methodCommentTemplate) + " is not equal to " + getConditionRecursively(rightOperand, methodCommentTemplate);
            } else if (binaryElem.getKind() == BinaryOperatorKind.EQ) {
                importantStatement += getConditionRecursively(leftOperand, methodCommentTemplate) + " is equal to " + getConditionRecursively(rightOperand, methodCommentTemplate);
            } else if (binaryElem.getKind() == BinaryOperatorKind.AND) {
                importantStatement += getConditionRecursively(leftOperand, methodCommentTemplate) + " and " + getConditionRecursively(rightOperand, methodCommentTemplate);
            } else if (binaryElem.getKind() == BinaryOperatorKind.OR) {
                importantStatement += getConditionRecursively(leftOperand, methodCommentTemplate) + " or " + getConditionRecursively(rightOperand, methodCommentTemplate);
            } else if (binaryElem.getKind() == BinaryOperatorKind.GT) {
                importantStatement += getConditionRecursively(leftOperand, methodCommentTemplate) + " is greater than " + getConditionRecursively(rightOperand, methodCommentTemplate);
            } else if (binaryElem.getKind() == BinaryOperatorKind.GE) {
                importantStatement += getConditionRecursively(leftOperand, methodCommentTemplate) + " is greater than or equal to " + getConditionRecursively(rightOperand, methodCommentTemplate);
            } else if (binaryElem.getKind() == BinaryOperatorKind.LT) {
                importantStatement += getConditionRecursively(leftOperand, methodCommentTemplate) + " is less than " + getConditionRecursively(rightOperand, methodCommentTemplate);
            } else if (binaryElem.getKind() == BinaryOperatorKind.LE) {
                importantStatement += getConditionRecursively(leftOperand, methodCommentTemplate) + " is less than or equal to" + getConditionRecursively(rightOperand, methodCommentTemplate);
            } else if (binaryElem.getKind() == BinaryOperatorKind.INSTANCEOF) {
                importantStatement += getConditionRecursively(leftOperand, methodCommentTemplate) + " is instance of " + getConditionRecursively(rightOperand, methodCommentTemplate);
            } else if (binaryElem.getKind() == BinaryOperatorKind.PLUS) {
                importantStatement += getConditionRecursively(leftOperand, methodCommentTemplate) + " plus " + getConditionRecursively(rightOperand, methodCommentTemplate);
            } else if (binaryElem.getKind() == BinaryOperatorKind.MINUS) {
                importantStatement += getConditionRecursively(leftOperand, methodCommentTemplate) + " minus " + getConditionRecursively(rightOperand, methodCommentTemplate);
            } else if (binaryElem.getKind() == BinaryOperatorKind.MUL) {
                importantStatement += getConditionRecursively(leftOperand, methodCommentTemplate) + " multiplied by " + getConditionRecursively(rightOperand, methodCommentTemplate);
            } else if (binaryElem.getKind() == BinaryOperatorKind.DIV) {
                importantStatement += getConditionRecursively(leftOperand, methodCommentTemplate) + " divided by " + getConditionRecursively(rightOperand, methodCommentTemplate);
            } else if (binaryElem.getKind() == BinaryOperatorKind.MOD) {
                importantStatement += getConditionRecursively(leftOperand, methodCommentTemplate) + " mod " + getConditionRecursively(rightOperand, methodCommentTemplate);
            }

        } else {
            importantStatement += getUnknownTypedElementComment(expElem, false, methodCommentTemplate);
        }
        return importantStatement;
    }
}
