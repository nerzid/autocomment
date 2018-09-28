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

import com.nerzid.autocomment.database.MethodTable;
import com.nerzid.autocomment.model.Comment;
import com.nerzid.autocomment.nlp.NLPToolkit;
import com.nerzid.autocomment.sunit.*;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.CompositeFilter;
import spoon.reflect.visitor.filter.FilteringOperator;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

/**
 * @author nerzid
 */
public abstract class MethodCommentTemplate {
    protected static HashMap<String, String> prepositionsForVerbs = new HashMap<>();
    protected static HashMap<String, String> presentVerbMap = new HashMap<>();
    protected static HashMap<String, String> verb2Map = new HashMap<>();
    protected static HashMap<String, String> verb3Map = new HashMap<>();

    static {
        prepositionsForVerbs.put("find", "from");
        prepositionsForVerbs.put("add", "to");
        prepositionsForVerbs.put("insert", "into");
        prepositionsForVerbs.put("get", "from");
        prepositionsForVerbs.put("create", "from");
        prepositionsForVerbs.put("clone", "from");
        prepositionsForVerbs.put("append", "to");
        prepositionsForVerbs.put("put", "into");

        verb2Map.put("get", "got");
        verb2Map.put("find", "found");
        verb2Map.put("put", "put");
        verb2Map.put("create", "created");

        verb3Map.put("get", "gotten");
        verb3Map.put("find", "found");
        verb3Map.put("put", "put");
        verb2Map.put("create", "created");

        presentVerbMap.put("get", "gets");
        presentVerbMap.put("find", "finds");
        presentVerbMap.put("insert", "inserts");
    }


    public static String getInvocationStatementComment(List<CtExecutableReference> invocationList, MethodCommentTemplate methodCommentTemplate, String target_string, SUnit sUnit, boolean isPassiveSentence) {
        List<PostaggedWord> postaggedWords = new ArrayList<>();
        boolean isStartingWithVerb = true;
        boolean isFirstTime = true;
        for (CtExecutableReference invocation : invocationList) {
            String method_name = invocation.getSimpleName();

            List<String> params = new ArrayList<>();
            List<CtTypeReference> params_types = invocation.getParameters();
            for (CtElement elem1_param : params_types) {
                params.add(sUnit.getUnknownTypedElementComment(elem1_param, false, methodCommentTemplate));
            }

            MethodTable mt = NLPToolkit.getMethodWithProperties("", method_name, 0);
            String postag = mt.getPostag();
            if (isFirstTime) {
                isStartingWithVerb = isStartingWithVerb(postag.split(" ")[0]);
                isFirstTime = false;
            }
            String splitted_identifier = mt.getSplittedIdentifier();
//            System.out.println("Generating InvocComment for " + method_name);
            PostaggedWord pw = Test.getPostaggedWord(postag, splitted_identifier.split(" "));
            pw.setParams(params);
            postaggedWords.add(pw);
        }
        if (!isStartingWithVerb) {
            return methodCommentTemplate.startingWithPRP(null, null, null, false, true, postaggedWords, isPassiveSentence, target_string).getCommentString(true);
        } else {
            return methodCommentTemplate.withOneVerb(null, null, null, false, true, postaggedWords, isPassiveSentence, target_string).getCommentString(true);
        }
//        return Test.getTemplateSentence("", postag, splitted_identifier.split(" "), params, "", false, null, false, isInvocComment);
    }

    protected Comment withOneVerb(PostaggedWord postaggedWord, List<String> params, SUnitStorage sUnitStorage, boolean isStatic, boolean isInvocComment, List<PostaggedWord> postaggedWords, boolean isFirstPostaggedWord, String target_string) {
        Comment comment = new Comment();
        if (postaggedWords != null) {
            if (postaggedWords.size() > 0) {
                postaggedWord = postaggedWords.get(0);
                List<PostaggedWord> otherPostaggedWords = new ArrayList<>();
                if (postaggedWords.size() > 1) {
                    for (int i = 1; i < postaggedWords.size(); i++) {
                        otherPostaggedWords.add(postaggedWords.get(i));
                    }
                }
                String verb1_postag = postaggedWord.getVerb1().getPostag();
                if (verb1_postag.equalsIgnoreCase("vbz")) {
                    comment.appendComment(withVBZVerb(postaggedWord, null, false, true, postaggedWords, isFirstPostaggedWord, target_string));
                } else {
                    comment.appendComment(withVBVerb(postaggedWord, null, false, true, postaggedWords, isFirstPostaggedWord, target_string));
                }
            } else {
                return comment;
            }
        } else {
            String verb1_postag = postaggedWord.getVerb1().getPostag();
            if (!isInvocComment)
                comment.appendToSummary("This method ");
            if (verb1_postag.equalsIgnoreCase("vbz")) {
                comment.appendComment(withVBZVerb(postaggedWord, params, isStatic, isInvocComment, postaggedWords, isFirstPostaggedWord, target_string));
            } else if (verb1_postag.equalsIgnoreCase("vb"))
                comment.appendComment(withVBVerb(postaggedWord, params, isStatic, isInvocComment, postaggedWords, isFirstPostaggedWord, target_string));
            else {
                comment.appendToSummary(prepareStringForParams(postaggedWord.getVerb1().getText(), params, isStatic));
                comment.appendToSummary(postaggedWord.getVerb1().getText() + " ");
                if (postaggedWord.getNounphrase1() != null)
                    comment.appendToSummary(postaggedWord.getNounphrase1().getText());
            }
            //        comment.appendComment(getReturnedInfo(sUnitStorage));

            if (!isInvocComment)
                comment.appendComment(getSUnitComments(sUnitStorage));
        }

        return comment;
    }

    protected Comment withVBVerb(PostaggedWord postaggedWord, List<String> params, boolean isStatic, boolean isInvocCmment, List<PostaggedWord> postaggedWords, boolean isFirstPostaggedWord, String target_string) {
        // Version findBestMatch(VersionSpecification versionSpec, Set<Version> versions)
        Comment comment = new Comment();
        if (postaggedWords != null) {
            if (postaggedWords.size() > 0) {
                postaggedWord = postaggedWords.get(0);
                List<PostaggedWord> otherPostaggedWords = new ArrayList<>();

                boolean finished = false;
                if (postaggedWords.size() > 1) {
                    for (int i = 1; i < postaggedWords.size(); i++) {
                        otherPostaggedWords.add(postaggedWords.get(i));
                    }
                } else {
                    finished = true;
                }
                if (isFirstPostaggedWord) {
                    comment.appendToSummary(toPresentVerb(postaggedWord.getVerb1().getText()) + " ");
                    if (postaggedWord.getNounphrase1() != null) {
                        comment.appendToSummary(postaggedWord.getNounphrase1().getText() + " ");
                    } else {
                        comment.appendToSummary("the ");
                    }
                    if (finished) {
                        if (postaggedWord.getNounphrase1() != null) {
                            comment.appendToSummary(getAppropriatePrepositionOfVerb(postaggedWord.getVerb1().getText()) + " ");
                            comment.appendToSummary("the ");
                        }
                        if (target_string == null)
                            if (isStatic)
                                comment.appendToSummary("this class");
                            else
                                comment.appendToSummary("this instance");
                        else
                            comment.appendToSummary(target_string);
                    } else {
                        PostaggedWord nextPostaggedWord = otherPostaggedWords.get(0);
                        if (nextPostaggedWord.isStartingWithPRP())
                            comment.appendComment(startingWithPRP(null, null, null, false, true, otherPostaggedWords, false, target_string));
                        else
                            comment.appendComment(withOneVerb(null, null, null, false, true, otherPostaggedWords, false, target_string));
                    }

                } else {
                    if (postaggedWord.getNounphrase1() != null) {
                        comment.appendToSummary(postaggedWord.getNounphrase1().getText() + " ");
                        comment.appendToSummary(toVerb3(postaggedWord.getVerb1().getText()) + " ");
                        comment.appendToSummary(getAppropriatePrepositionOfVerb(postaggedWord.getVerb1().getText()) + " ");
                    } else {
                        comment.appendToSummary(toVerb3(postaggedWord.getVerb1().getText()) + " instance ");
                        comment.appendToSummary(getAppropriatePrepositionOfVerb(postaggedWord.getVerb1().getText()) + " ");
                    }
                    if (finished) {
                        if (target_string == null)
                            comment.appendToSummary("this instance");
                        else
                            comment.appendToSummary(target_string);
                    } else {
                        if (postaggedWord.isStartingWithPRP())
                            comment.appendComment(startingWithPRP(null, null, null, false, true, otherPostaggedWords, false, target_string));
                        else
                            comment.appendComment(withOneVerb(null, null, null, false, true, otherPostaggedWords, false, target_string));
                    }
                }


            } else {
                return comment;
            }
        } else {
            if (!isInvocCmment) {
                comment.appendToSummary(toPresentVerb(postaggedWord.getVerb1().getText()) + " ");
//        if (params.isEmpty())
//            res += " this instance ";
                if (postaggedWord.getNounphrase1() != null)
                    comment.appendToSummary(postaggedWord.getNounphrase1().getText() + " ");
                comment.appendToSummary(prepareStringForParams(postaggedWord.getVerb1().getText(), params, isStatic));
            } else {
                if (postaggedWord.getNounphrase1() != null) {
                    comment.appendToSummary(postaggedWord.getNounphrase1().getText() + " ");
                } else {

                }
                comment.appendToSummary("that is ");
                comment.appendToSummary(toVerb3(postaggedWord.getVerb1().getText()));
                comment.appendToSummary(prepareStringForParams(postaggedWord.getVerb1().getText(), params, isStatic));
            }
        }
        return comment;
    }

    protected Comment withVBZVerb(PostaggedWord postaggedWord, List<String> params, boolean isStatic, boolean isInvocCmment, List<PostaggedWord> postaggedWords, boolean isFirstPostaggedWord, String target_string) {
        Comment comment = new Comment();

        if (postaggedWords != null) {
            if (postaggedWords.size() > 0) {
                postaggedWord = postaggedWords.get(0);
                List<PostaggedWord> otherPostaggedWords = new ArrayList<>();

                boolean finished = false;
                if (postaggedWords.size() > 1) {
                    for (int i = 1; i < postaggedWords.size(); i++) {
                        otherPostaggedWords.add(postaggedWords.get(i));
                    }
                } else {
                    finished = true;
                }
                if (isFirstPostaggedWord) {
                    comment.appendToSummary(toPresentVerb(postaggedWord.getVerb1().getText() + " "));
                    if (postaggedWord.getNounphrase1() != null)
                        comment.appendToSummary(postaggedWord.getNounphrase1().getText() + " ");
                } else {
                    if (postaggedWord.getNounphrase1() != null) {
//                        comment.appendToSummary("of the ");
                        comment.appendToSummary(postaggedWord.getNounphrase1().getText() + " ");
                        comment.appendToSummary(toVerb3(postaggedWord.getVerb1().getText()) + " to ");
                    } else {
                        comment.appendToSummary(toVerb3(postaggedWord.getVerb1().getText()) + " ");
                        comment.appendToSummary(getAppropriatePrepositionOfVerb(postaggedWord.getVerb1().getText()) + " ");
                    }
                }
                if (finished) {
                    if (target_string == null)
                        comment.appendToSummary("this instance");
                    else
                        comment.appendToSummary(target_string);
                } else {
                    if (postaggedWord.isStartingWithPRP())
                        comment.appendComment(startingWithPRP(null, null, null, false, true, otherPostaggedWords, false, target_string));
                    else
                        comment.appendComment(withOneVerb(null, null, null, false, true, otherPostaggedWords, false, target_string));
                }

            } else {
                return comment;
            }
        } else {
            if (params.isEmpty())
                if (isStatic)
                    comment.appendToSummary("this class ");
                else
                    comment.appendToSummary("this instance ");
            comment.appendToSummary(postaggedWord.getVerb1().getText() + " ");
            if (postaggedWord.getNounphrase1() != null)
                comment.appendToSummary(postaggedWord.getNounphrase1().getText() + " ");
            comment.appendToSummary(prepareStringForParams(postaggedWord.getVerb1().getText(), params, isStatic));
        }
        return comment;
    }

    protected Comment startingWithPRP(PostaggedWord postaggedWord, List<String> params, SUnitStorage sUnitStorage, boolean isStatic, boolean isInvocCmment, List<PostaggedWord> postaggedWords, boolean isFirstPostaggedWord, String target_string) {
        Comment comment = new Comment();
        if (postaggedWords != null) {
            if (postaggedWords.size() > 0) {
                postaggedWord = postaggedWords.get(0);
                List<PostaggedWord> otherPostaggedWords = new ArrayList<>();

                boolean finished = false;
                if (postaggedWords.size() > 1) {
                    for (int i = 1; i < postaggedWords.size(); i++) {
                        otherPostaggedWords.add(postaggedWords.get(i));
                    }
                } else {
                    finished = true;
                }
                if (isFirstPostaggedWord && !finished) {
                    if (postaggedWord.getPreposition().getText().equalsIgnoreCase("to")) {
                        finished = true;
                        comment.appendToSummary("converts the ");
                        if (postaggedWord.getNounphrase1() != null) {
                            if (otherPostaggedWords.size() > 0) {
                                PostaggedWord nextPostaggedWord = otherPostaggedWords.get(0);
                                if (nextPostaggedWord.isStartingWithPRP())
                                    comment.appendComment(startingWithPRP(null, null, null, false, true, otherPostaggedWords, false, target_string));
                                else
                                    comment.appendComment(withOneVerb(null, null, null, false, true, otherPostaggedWords, false, target_string));
                            }
                            comment.appendToSummary(" to ");
                            comment.appendToSummary(postaggedWord.getNounphrase1().getText() + " ");
                        }
//                        comment.appendToSummary(prepareStringForParams(prepText, params, isStatic));
//            comment.appendComment(getReturnedInfo(sUnitStorage));
                    } else if (postaggedWord.getPreposition().getText().equalsIgnoreCase("on")) {
                        comment.appendToSummary("is called when this instance is ");

                        comment.appendToSummary(toVerb2(postaggedWord.getVerb1().getText()));
                    }
                } else {
                    if (postaggedWord.getNounphrase1() != null) {
//                        comment.appendToSummary("of the ");
                        comment.appendToSummary(postaggedWord.getNounphrase1().getText() + " ");
                        comment.appendToSummary("that is converted " + " from ");
                    } else {
                        if(postaggedWord.getPreposition().getText().equalsIgnoreCase("on")){
                            comment.appendToSummary("calls when ");
                        }else {
                            comment.appendToSummary("to ");
                        }
                    }
                }
                if (finished && !isFirstPostaggedWord) {
                    if (target_string == null)
                        if (isStatic)
                            comment.appendToSummary("this class ");
                        else
                            comment.appendToSummary("this instance ");
                    else
                        comment.appendToSummary(target_string + " ");
                    if (postaggedWord.getPreposition().getText().equalsIgnoreCase("on")){
                        comment.appendToSummary(" is" + toVerb2(postaggedWord.getVerb1().getText()));
                    }
                } else if (finished && isFirstPostaggedWord) {
                    if (target_string == null)
                        if (isStatic)
                            comment.appendToSummary("this class ");
                        else
                            comment.appendToSummary("this instance ");
                    else
                        comment.appendToSummary(target_string + " ");
                } else {
                    PostaggedWord nextPostaggedWord = otherPostaggedWords.get(0);
                    if (nextPostaggedWord.isStartingWithPRP())
                        comment.appendComment(startingWithPRP(null, null, null, false, true, otherPostaggedWords, false, target_string));
                    else
                        comment.appendComment(withOneVerb(null, null, null, false, true, otherPostaggedWords, false, target_string));
                }

            } else {
                return comment;
            }
        } else {
            String prepText = postaggedWord.getPreposition().getText();
            if (!isInvocCmment) {
                comment.appendToSummary("This method ");
//        String verb1_postag = postaggedWord.getVerb1().getPostag();
                if (prepText.equalsIgnoreCase("to")) {
                    comment.appendToSummary("converts this instance ");
                    if (postaggedWord.getNounphrase1() != null) {
                        comment.appendToSummary("to ");
                        comment.appendToSummary(postaggedWord.getNounphrase1().getText());
                        comment.appendToSummary(" ");
                    }
                    comment.appendToSummary(prepareStringForParams(prepText, params, isStatic));
//            comment.appendComment(getReturnedInfo(sUnitStorage));
                } else if (prepText.equalsIgnoreCase("on")) {
                    if (isStatic)
                        comment.appendToSummary("is called when this class is ");
                    else
                        comment.appendToSummary("is called when this instance is ");

                    comment.appendToSummary(toVerb2(postaggedWord.getVerb1().getText()));
                }
            } else {
                if (prepText.equalsIgnoreCase("to")) {
                    if (postaggedWord.getNounphrase1() != null) {
                        comment.appendToSummary("the ");
                        comment.appendToSummary(postaggedWord.getNounphrase1().getText());
                        comment.appendToSummary(" that is converted");
                    }
                }
            }
                comment.appendComment(getSUnitComments(sUnitStorage));
        }
        return comment;
    }

    protected Comment withTwoVerbs(PostaggedWord postaggedWord, List<String> params, SUnitStorage sUnitStorage, boolean isStatic, boolean prefix) {
        return Comment.empty();
    }


    protected Comment getSUnitComments(SUnitStorage sUnitStorage) {
        Comment comment = new Comment();
        PriorityQueue<FunctionSUnit> functionSUnits = new PriorityQueue<>();
        functionSUnits.addAll(sUnitStorage.getVoidReturnSUnits());
        functionSUnits.addAll(sUnitStorage.getSameActionSequenceSUnits());
        functionSUnits.addAll(sUnitStorage.getEndingSUnits());

        PriorityQueue<ControllingSUnit> controllingSUnits = sUnitStorage.getControllingSUnits();
        for (FunctionSUnit functionSUnit : functionSUnits) {
            comment.appendComment(functionSUnit.getSUnitComment(controllingSUnits, this));
        }
        return comment;
    }

//    protected Comment getSUnitComment(FunctionSUnit functionSUnit, PriorityQueue<ControllingSUnit> controllingSUnits ){
//        Comment comment = new Comment();
//        CtElement elem = functionSUnit.getElement();
//        Comment controllingComments = getControllingSUnitComments(elem, controllingSUnits);
//        String sunitString = functionSUnit.toString();
//        if (controllingComments.getImportantStatements().size() == 0){
//
//            comment.addImportantStatement(sunitString);
//        } else {
//            comment.appendComment(controllingComments);
//            comment.appendTextToAllImportantStatements(", then " + sunitString);
//        }
//        return comment;
//    }

    protected String prepareStringForParams(String verb, List<String> params, boolean isStatic) {
        String res = getAppropriatePrepositionOfVerb(verb) + " ";
        if (params.isEmpty()) {
            if (isStatic)
                return res + "this class ";
            else
                return res + "this instance ";
        }


        res += "the given ";
        int ix = 0;
        for (String param : params) {
            if (ix + 1 == params.size()) {
                res += param;
            } else {
                res += param + " and ";
            }
            ix++;
        }
        return res;

    }

    // TODO: LOOK AT THE CHAT WITH ERAY YILDIZ
    protected String getAppropriatePrepositionOfVerb(String verb) {
        if (prepositionsForVerbs.containsKey(verb))
            return prepositionsForVerbs.get(verb);
        else
            return "using";
//        return "using";
    }

    public String toPresentVerb(String verb) {
        verb = verb.trim();
        if (presentVerbMap.containsKey(verb))
            return presentVerbMap.get(verb);
        else
            return verb.trim() + "s";
    }

    public String toVerb2(String verb) {
        verb = verb.trim();
        if (verb2Map.containsKey(verb))
            return verb2Map.get(verb);
        else
            return verb + "ed";
    }

    public String toVerb3(String verb) {
        verb = verb.trim();
        if (verb3Map.containsKey(verb))
            return verb3Map.get(verb);
        else
            return verb + "ed";
    }

    public static boolean isStartingWithVerb(String word) {
        return word.toLowerCase().contains("vb");
    }
}
