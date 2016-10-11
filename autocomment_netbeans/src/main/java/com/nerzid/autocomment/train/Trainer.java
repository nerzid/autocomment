/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nerzid.autocomment.train;

import com.nerzid.autocomment.database.Database;
import com.nerzid.autocomment.database.Word;
import com.nerzid.autocomment.database.WordGroup;
import com.nerzid.autocomment.log.ErrorLog;
import com.nerzid.autocomment.log.ErrorMessage;
import com.nerzid.autocomment.io.FilePicker;
import com.nerzid.autocomment.generator.CommentGenerator;
import com.nerzid.autocomment.processor.CtCommentProcessor;
import com.nerzid.autocomment.processor.TrainerMethodProcessor;
import com.nerzid.autocomment.nlp.Tokenizer;
import com.nerzid.autocomment.nlp.NLPToolkit;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.support.JavaOutputProcessor;

/**
 *
 * @author nerzid
 */
public class Trainer {

    public static List<File> files_list; // Java Source Files List
    public static ErrorLog errorlog;

    public static void main(String[] args) {
        errorlog = new ErrorLog("C:/users/nerzid/desktop/");
        Database.open();
        prepareTrainingProcess();
        Database.close();
    }

    /**
     * 
     * @param method_name
     * @param data_type 
     */
    public static void train(String method_name, String data_type) {
        Collection<String> identifiers = Tokenizer.split(method_name);
        String identifier_sentence = Tokenizer.getIdentifiersSentence(identifiers);
        //Collection<Word> words_list = NLPToolkit.getWordsWithFeatures(identifier_sentence, data_type);
        WordGroup wg = NLPToolkit.getWordGroup(identifier_sentence, data_type);
        //Word.insertAll(words_list);
        WordGroup.insert(wg);
    }

    /**
     * 
     */
    private static void prepareTrainingProcess() {
        // Choose Java Source Files via FilePicker
        //files_list = FilePicker.chooseAndGetJavaFiles();
        files_list = FilePicker.chooseDirAndGetJavaFiles();
        int count = 0;
        if (files_list == null || files_list.isEmpty()) {
            errorlog.add(new com.nerzid.autocomment.log.Error(ErrorMessage.FILE_HANDLER_NO_JAVA_FILE));
        } else {

            for (File f : files_list) {

                // Will be deleted in the future
                f.setWritable(true);

                // Launcher is to use Spoon.
                Launcher l = new Launcher();
                l.addInputResource(f.getPath());

                // Set Environment instance to configure Spoon
                // This is necessary, if you want to see "nice" result
                Environment env = l.getEnvironment();

                // To use uncompiliable java files, this needs to be enabled
                env.setNoClasspath(true);

                // This processor handles output file.
                // Without this line, file's contents won't change/updated.
                JavaOutputProcessor jop = l.createOutputWriter(f.getParentFile(), env);

                // To see comments on result 
                env.setCommentEnabled(true);

                // To see short-simple names instead of long ones 
                // eg. "System.out.println()" instead of "java.lang.System.out.println()"
                // Also sets imports and deletes all unused imports.
                env.setAutoImports(true);

                // Add Processors to Spoon Launcher
                // WARNING: Priority is import DO NOT CHANGE
                // JavaOutputProcessor must be at LOWERMOST to get all differences
                // and write them
                l.addProcessor(new CtCommentProcessor());
                l.addProcessor(new TrainerMethodProcessor());
                //l.addProcessor(jop);

                // Debuglevel 
                // env.setLevel("0");
                // Run the Launcher
                try {
                    l.run();
                } catch (Exception e) {
                    errorlog.add(new com.nerzid.autocomment.log.Error(e.getMessage(), null));
                }
                count++;
                System.out.println("Status: " + count + "/" + files_list.size());
            }
            finish();
        }
    }

    private static void finish() {
        int res = JOptionPane.showConfirmDialog(null, errorlog.getCount() + " many errors has occured.\n"
                + "Do you want to write them to errorlog.txt file on desktop ?");
        if (res == JOptionPane.OK_OPTION) {
            try {
                errorlog.createErrorLogFile();
            } catch (IOException ex) {
                Logger.getLogger(CommentGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * It is highly recommended to use our database(autocomment.db) instead of
     * creating a new one
     *
     * @deprecated
     */
    @Deprecated
    public static void createTrainFileIfNotExists() {
        //TO DO
    }
}
