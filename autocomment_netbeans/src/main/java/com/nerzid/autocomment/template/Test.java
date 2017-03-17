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

import com.nerzid.autocomment.grammar.CommentTitleParser;
import com.nerzid.autocomment.grammar.CommentTitleLexer;
import com.nerzid.autocomment.database.Database;
import com.nerzid.autocomment.database.MethodModel;
import com.nerzid.autocomment.database.MethodTable;
import com.nerzid.autocomment.exception.FileNotSelected;
import com.nerzid.autocomment.io.FilePicker;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author nerzid
 */
public class Test {
    // IMPORTANT: If you update Comment title Lexer with a new one, dont forget to
    // put overrided recover methods in CommentTitleLexer.java
    // throw new RunTimeException() in them

    public static void main(String[] args) throws IOException, FileNotSelected {
        Database.openIfNot();
        work();
        Database.close();
    }

    public static String getCommentTitleType(String postag_sentence) {
        ANTLRInputStream input = new ANTLRInputStream(postag_sentence);
        CommentTitleLexer lexer = new CommentTitleLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CommentTitleParser parser = new CommentTitleParser(tokens);

        lexer.removeErrorListeners();
        parser.removeErrorListeners();

        PostagParsingErrorListener ppel = new PostagParsingErrorListener();
        lexer.addErrorListener(ppel);
        parser.addErrorListener(ppel);
        parser.setErrorHandler(new BailErrorStrategy());

        EvalCommentTitleListener evalListener = new EvalCommentTitleListener();

        try {
            ParseTree tree = parser.rule1();

            ParseTreeWalker ptw = new ParseTreeWalker();
            ptw.walk(evalListener, tree);
            System.out.println("PTW string. " + evalListener.getPostagSent());
            return evalListener.getPostagSent();
        } catch (Exception e) {
            ppel.errorList.add("err");
            System.out.println("Hata: " + e.getMessage());
            return "";
        }
    }

    public static void getCommentDescType(String postag_sentence) {

    }

    public static String getTemplateSentence(String return_type, String postag_sentence, String[] words, List<String> params) {
        ANTLRInputStream input = new ANTLRInputStream(postag_sentence);
        CommentTitleLexer lexer = new CommentTitleLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CommentTitleParser parser = new CommentTitleParser(tokens);

        lexer.removeErrorListeners();
        parser.removeErrorListeners();

        PostagParsingErrorListener ppel = new PostagParsingErrorListener();
        lexer.addErrorListener(ppel);
        parser.addErrorListener(ppel);
        parser.setErrorHandler(new BailErrorStrategy());

        EvalCommentTitleListener evalListener = new EvalCommentTitleListener();

        try {
            ParseTree tree = null;
            if (return_type.equals("boolean")) {
                int verbCount = postag_sentence.split("v").length;
                if (verbCount == 1) {
                    tree = parser.boolean_one_verb_rule();

                    ParseTreeWalker ptw = new ParseTreeWalker();
                    ptw.walk(evalListener, tree);
                    System.out.println("PTW string. " + evalListener.getPostagSent());
                    String postagsByAntlr = evalListener.getPostagSent();

                    String verb = "";
                    List<String> nouns = new ArrayList<>();
                    
                    verb = words[0];
                    for (int i = 1; i < words.length; i++) {
                        nouns.add(words[i]);
                    }
                    if (params.isEmpty())
                        return new BooleanMCT().booleanMethodWithOneVerb(verb, nouns);
                    else 
                        return new BooleanMCT().booleanMethodWithOneVerbAndParameters(verb, nouns, params);
                } else {
                    tree = parser.boolean_two_verb_rule();

                    ParseTreeWalker ptw = new ParseTreeWalker();
                    ptw.walk(evalListener, tree);
                    System.out.println("PTW string. " + evalListener.getPostagSent());
                    String postagsByAntlr = evalListener.getPostagSent();

                    String verb1 = "";
                    String verb2 = "";
                    List<String> nouns = new ArrayList<>();
                    
                    
                    
                    return new BooleanMCT().booleanMethodWithTwoVerb(verb1, verb2, nouns);
                }

            } else {
                System.out.println("NO ITS NOT BOOLEAN");
            }

            ParseTreeWalker ptw = new ParseTreeWalker();
            ptw.walk(evalListener, tree);
            System.out.println("PTW string. " + evalListener.getPostagSent());
            String postagsByAntlr = evalListener.getPostagSent();

            String verb1 = "";
            String verb2 = "";
            List<String> nouns = new ArrayList<>();

            return evalListener.getPostagSent();
        } catch (Exception e) {
            ppel.errorList.add("err");
            System.out.println("Hata: " + e.getMessage());
            return "";
        }
    }

    private static void work() throws IOException, FileNotSelected {
        List<MethodModel> m_list = MethodTable.getAll();
        JOptionPane.showMessageDialog(null, "Choose path for valid files.");
        String path = FilePicker.getFilePath(FilePicker.chooseDir());
        File f = new File(path + "/" + "valids");

        f.createNewFile();
        String x = "";
        while (!f.exists()) {
            System.out.println("Waitin for file to be created");
        }

        System.out.println("File created successfully.");

        int valid_count = 0;
        int nvalid_count = 0;

        for (MethodModel m : m_list) {
            String s = m.getString(MethodModel.COLUMN_POSTAG);
            if (!getCommentTitleType(s).isEmpty()) {
                valid_count++;
                x += s + "\n";

            } else {
                nvalid_count++;
            }
        }
        x += "Status: " + valid_count + "/" + m_list.size();
        FileUtils.writeStringToFile(f, x, "UTF-8");
    }
}
