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
package com.nerzid.autocomment.nlp;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.nerzid.autocomment.database.DataTypeTable;
import com.nerzid.autocomment.database.MethodTable;
import com.nerzid.autocomment.database.MongoDB;
import com.nerzid.autocomment.database.ParameterTable;
import com.nerzid.autocomment.sunit.SUnitStorage;
import com.nerzid.autocomment.sunit.VoidReturnSUnit;
import com.nerzid.autocomment.template.SUnitCommentTemplate;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.simple.Sentence;

import org.bson.BsonArray;
import org.bson.BsonString;
import org.bson.Document;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtTypeReference;

import java.io.StringReader;
import java.util.*;

/**
 * @author nerzid
 */
public class NLPToolkit {

    // Below are the MUST BE VERB words. Those are indeed verbs, but Stanford NLP toolkit
    // assumes that they'r not verbs. So we hard-coded them.
//    public static String[] mustVerbArr = {"sort", "fire", "copy", "swap", "check",
//        "process", "append", "dump", "print",
//        "println", "register", "resolve", "start",
//        "end", "visit", "fill", "search", "use", "clone"};
    public static String[] mustVerbArr = {};

    /**
     * Gets the {@link com.nerzid.autocomment.database.MethodTable} instance.
     *
     * @param signature
     * @param method_name
     * @param dtid
     * @return
     */
    public static MethodTable getMethodWithProperties(String signature, String method_name, int dtid) {
        Collection<String> identifiers = Tokenizer.split(method_name);
        String identifier_sentence = Tokenizer.getIdentifiersSentence(identifiers);
        identifier_sentence = "They " + identifier_sentence;
        String[] identifiers_list = identifier_sentence.split(" ");
        Sentence sent = new Sentence(identifier_sentence);
        List<String> lemmas_list = sent.lemmas();
        List<String> postags_list = changeJJstoNNs(sent.posTags());

        MethodTable m = new MethodTable();

        if (lemmas_list.size() > 2) {
            for (int i = 1; i < lemmas_list.size() - 1; i++) {
                m.addSplittedIdentifier(identifiers_list[i]);
                m.addLemma(lemmas_list.get(i));
                if (i == 1 && doesNeedToBeConverted(identifiers_list[i])) {
                    m.addPostag("VB");
                } else {
                    m.addPostag(postags_list.get(i));
                }
            }
        }


        m.addLastSplittedIdentifier(identifiers_list[lemmas_list.size() - 1]);
        m.addLastLemma(lemmas_list.get(lemmas_list.size() - 1));
        if (doesNeedToBeConverted(identifiers_list[identifiers_list.length - 1]))
            m.addLastPostag("VB");
        else
            m.addLastPostag(postags_list.get(lemmas_list.size() - 1));
        if (signature != null)
            m.setSignature(signature);
        m.setIdentifier(method_name);
        m.setFK_dtid(dtid);

        // This will change the this method, correct postags comes from this.
        m.setPostag(getPostagSentenceUsingLexicalParser(method_name));
        return m;
    }

    public static ParameterTable getParameterWithProperties(String param_name, int dtid) {
        Collection<String> identifiers = Tokenizer.split(param_name);
        String identifier_sentence = Tokenizer.getIdentifiersSentence(identifiers);
        identifier_sentence = "They " + identifier_sentence;
        String[] identifiers_list = identifier_sentence.split(" ");
        Sentence sent = new Sentence(identifier_sentence);
        List<String> lemmas_list = sent.lemmas();
        List<String> postags_list = changeJJstoNNs(sent.posTags());


        ParameterTable p = new ParameterTable();

        if (lemmas_list.size() > 2) {
            for (int i = 1; i < lemmas_list.size() - 1; i++) {
                p.addSplittedIdentifier(identifiers_list[i]);
                p.addLemma(lemmas_list.get(i));
                if (i == 1 && doesNeedToBeConverted(identifiers_list[i])) {
                    p.addPostag("VB");
                } else {
                    p.addPostag(postags_list.get(i));
                }
            }
        }

        p.addLastSplittedIdentifier(identifiers_list[lemmas_list.size() - 1]);
        p.addLastLemma(lemmas_list.get(lemmas_list.size() - 1));
        if (doesNeedToBeConverted(identifiers_list[identifiers_list.length - 1]))
            p.addLastPostag("VB");
        else
            p.addLastPostag(postags_list.get(lemmas_list.size() - 1));

        p.setIdentifier(param_name);
        p.setFK_dtid(dtid);

        return p;
    }

    public static DataTypeTable getDataTypeWithProperties(String data_typeStr) {
        String original = data_typeStr;
        String text = Tokenizer.simplifyDataType(original);
        text = "They " + text;
        String[] identifiers_list = text.split(" ");
        Sentence sent = new Sentence(text);
        List<String> lemmas_list = sent.lemmas();
        List<String> postags_list = changeJJstoNNs(sent.posTags());

        DataTypeTable data_type = new DataTypeTable();

        if (lemmas_list.size() > 2) {
            for (int i = 1; i < lemmas_list.size() - 1; i++) {
                data_type.addSimplifiedIdentifier(identifiers_list[i]);
                data_type.addLemma(lemmas_list.get(i));
                if (i == 1 && doesNeedToBeConverted(identifiers_list[i])) {
                    data_type.addPostag("VB");
                } else {
                    data_type.addPostag(postags_list.get(i));
                }
            }
        }

        data_type.addLastSimplifiedIdentifier(identifiers_list[lemmas_list.size() - 1]);
        data_type.addLastLemma(lemmas_list.get(lemmas_list.size() - 1));
        data_type.addLastPostag(postags_list.get(lemmas_list.size() - 1));

        data_type.setIdentifier(original);

        return data_type;
    }

    public static boolean doesNeedToBeConverted(String identifier) {
        for (String s : mustVerbArr) {
            if (s.equals(identifier)) {
                return true;
            }
        }
        return false;
    }

    public static List<String> changeJJstoNNs(List<String> postagsList) {
        List<String> res = new ArrayList<>();
        for (String postag : postagsList) {
            if (postag.toLowerCase().contains("JJ"))
                res.add("JJ");
            else
                res.add(postag);
        }
        return res;
    }

    public static void insertMethodToMongo(String methodName, String javadoc, BsonArray params) {
        Collection<String> identifiers = Tokenizer.split(methodName);
        String identifier_sentence = Tokenizer.getIdentifiersSentence(identifiers);
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase db = mongoClient.getDatabase("local");
        MongoCollection collection = db.getCollection("methods_with_parse_trees");
        List<CoreLabel> tokens = tokenizerFactory.getTokenizer(
                new StringReader(identifier_sentence)).tokenize();
        LexicalizedParser p = LexicalizedParser.loadModel();

        Document doc = new Document();
        doc.append("method_name", new BsonString(methodName));
        doc.append("tokens", new BsonString(identifier_sentence));
        doc.append("parse_tree", new BsonString(p.parse(tokens).toString()));
        doc.append("params", new BsonArray(params));

        if (javadoc == null)
            return;
        doc.append("javadoc", new BsonString(Tokenizer.removePunctuationsExcludeSpaces(javadoc).toLowerCase()));

        try {
            collection.insertOne(doc);
        } catch (MongoWriteException e) {
            System.out.println("duplicate");
        }
    }

    public static void main(String[] args) {
        String methodName = "search query";
        System.out.println(getPostagSentenceUsingLexicalParser(methodName));
    }

    public static String getPostagSentenceUsingLexicalParser(String methodName) {
        Collection<String> identifiers = Tokenizer.split(methodName);
        String identifier_sentence = Tokenizer.getIdentifiersSentence(identifiers);
        List<CoreLabel> tokens = tokenizerFactory.getTokenizer(
                new StringReader(identifier_sentence)).tokenize();
        LexicalizedParser p = LexicalizedParser.loadModel();
        String postag_string = p.parse(tokens).flatten().toString().replaceAll("\\(", "");
        String[] postags_array = {"CC", "CD", "DT", "EX", "FW", "IN", "JJ", "JJR", "JJS", "LS", "MD", "NN", "NNS", "NNP", "NNPS", "PDT", "POS", "PRP", "PRP$", "RB", "RBR", "RBS", "RP", "SYM", "TO", "UH", "VB", "VBD", "VBG", "VBN", "VBP", "VBZ", "WDT", "WP", "WP$", "WRB"};
        ArrayList<String> postag_list = new ArrayList<>(Arrays.asList(postags_array));
        String my_postag = "";
        String[] splitted_postags = postag_string.split(" ");
        int i = 0;
        for (String postag : splitted_postags) {
            if (postag.equals("VBD")) {
                my_postag += "VB";
                i++;
                continue;
            }

            if (postag_list.contains(postag)) {
                if ((postag.equals("NN") || postag.equals("JJ"))&& i == 0)
                    my_postag += "VB";
                else
                    // This statement makes
                    // search query(VB RB) -> (VB NN)
                    // on create(RB VB) -> (RB VB) -> This will need another rule ANTLR grammar
                    if ((postag.contains("RB") || postag.contains("JJ")) && i != 0)
                        my_postag += " " + "NN";
                    else if(postag.contains("TO") ||postag.contains("RB")) {
                        if (i == 0)
                            my_postag += "IN";
                        else

                            my_postag += " " + "IN";
                    }
                    else if(postag.contains("CD")){
                        my_postag += " " + "NN";
                    }
                    else {
                        if (i == 0)
                            my_postag += postag;
                        else
                            my_postag += " " + postag;
                    }
                i++;
            }
        }
        return my_postag;
    }

    public static void insertSUnitsToMongo(SUnitStorage sUnitStorage) {
        for (VoidReturnSUnit sUnit : sUnitStorage.getVoidReturnSUnits()) {
            CtElement element = sUnit.getElement();
            if (element instanceof CtInvocation) {
                CtInvocation invoc = (CtInvocation) element;
                String method_name = invoc.getExecutable().getSimpleName();
                CtExpression target = invoc.getTarget();
                List<CtExpression> params = invoc.getArguments();
                String params_string = "";
                HashMap<String, Integer> variable_count = new HashMap<>();
                variable_count.put("[string_var]", 0);
                variable_count.put("[numeric_var]", 0);
                variable_count.put("[bool_var]", 0);
                variable_count.put("[custom_var]", 0);
                for (CtExpression param : params) {
                    String param_type = "";
                    if (param.getType() != null) {
                        param_type = param.getType().getSimpleName();
                        if (param_type.equalsIgnoreCase("string") || param_type.equalsIgnoreCase("character")) {
                            param_type = "[string_var_" + variable_count.get("[string_var]") + "]";
                            variable_count.put("[string_var]", variable_count.get("[string_var]") + 1);
                        } else if (param_type.equalsIgnoreCase("double") ||
                                param_type.equalsIgnoreCase("integer") ||
                                param_type.equalsIgnoreCase("float") ||
                                param_type.equalsIgnoreCase("short")) {
                            param_type = "[numeric_var_" + variable_count.get("[numeric_var]") + "]";
                            variable_count.put("[numeric_var]", variable_count.get("[numeric_var]") + 1);
                        } else if (param_type.equalsIgnoreCase("boolean")) {
                            param_type = "[bool_var" + variable_count.get("[bool_var]") + "]";
                            variable_count.put("[bool_var]", variable_count.get("[bool_var]") + 1);
                        } else {
                            param_type = "[custom_var" + variable_count.get("[custom_var]") + "]";
                            variable_count.put("[custom_var]", variable_count.get("[custom_var]") + 1);
                        }
                    } else {
                        if (param instanceof CtBinaryOperator) {
                            CtBinaryOperator operator = (CtBinaryOperator) param;
                            CtTypeReference left_operand = operator.getLeftHandOperand().getType();
                            if (left_operand != null) {
                                param_type = left_operand.getSimpleName();
                                if (param_type.equalsIgnoreCase("string") || param_type.equalsIgnoreCase("character")) {
                                    param_type = "[string_var_" + variable_count.get("[string_var]") + "]";
                                    variable_count.put("[string_var]", variable_count.get("[string_var]") + 1);
                                } else if (param_type.equalsIgnoreCase("double") ||
                                        param_type.equalsIgnoreCase("integer") ||
                                        param_type.equalsIgnoreCase("float") ||
                                        param_type.equalsIgnoreCase("short")) {
                                    param_type = "[numeric_var_" + variable_count.get("[numeric_var]") + "]";
                                    variable_count.put("[numeric_var]", variable_count.get("[numeric_var]") + 1);
                                } else if (param_type.equalsIgnoreCase("boolean")) {
                                    param_type = "[bool_var" + variable_count.get("[bool_var]") + "]";
                                    variable_count.put("[bool_var]", variable_count.get("[bool_var]") + 1);
                                } else {
                                    param_type = "[custom_var" + variable_count.get("[custom_var]") + "]";
                                    variable_count.put("[custom_var]", variable_count.get("[custom_var]") + 1);
                                }
                            }
                        }
                    }
                    params_string += param_type + " ";
                }
                String target_str = "";
                if (target != null)
                    target_str = Tokenizer.getIdentifiersSentence(Tokenizer.split(target.toString()));

                String method_str = Tokenizer.getIdentifiersSentence(Tokenizer.split(method_name));

                String code = Tokenizer.removePunctuationsExcludeSpaces(target_str + " " + method_str);
                code += " " + params_string;
                String comment = Tokenizer.removePunctuationsExcludeSpaces(sUnit.getElement().getComments().toString());
                if (comment == null || comment.isEmpty())
                    return;
                Document doc = new Document();
                doc.append("code", new BsonString(code));
                doc.append("comment", new BsonString(comment));

                MongoDB.insertDocument(doc);

            }
        }
    }


    private static final TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "invertible=true");

}
