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
package com.nerzid.autocomment.train;

import com.nerzid.autocomment.database.DataTypeTable;
import com.nerzid.autocomment.database.DataTypeModel;
import com.nerzid.autocomment.database.Database;
import com.nerzid.autocomment.database.MethodTable;
import com.nerzid.autocomment.database.MethodModel;
import com.nerzid.autocomment.database.ParameterTable;
import com.nerzid.autocomment.database.ParameterModel;
import com.nerzid.autocomment.exception.FileNotSelected;
import com.nerzid.autocomment.log.ErrorLog;
import com.nerzid.autocomment.log.ErrorMessage;
import com.nerzid.autocomment.io.FilePicker;
import com.nerzid.autocomment.generator.CommentGenerator;
import com.nerzid.autocomment.gui.MainFrame;
import com.nerzid.autocomment.processor.CtCommentProcessor;
import com.nerzid.autocomment.processor.TrainerMethodProcessor;
import com.nerzid.autocomment.nlp.NLPToolkit;
import java.io.File;
import java.io.IOException;
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

    public static String[] excluded_files = {"test", "equals", "toString", "run", "init", "main"};

    public static List<File> files_list; // Java Source Files List
    public static ErrorLog errorlog;
    public static int currentFileNo = 0;
    public static boolean trainingProcessStarted = false;
    public static String current_training_file_name = "";

    public static void main(String[] args) throws FileNotSelected {

        JOptionPane.showMessageDialog(null, "Choose Error file path");
        String f = FilePicker.chooseDir().getPath();
        errorlog = new ErrorLog(f);
        Database.openIfNot();
        prepareTrainingProcess();
        //Database.close();
    }

    /**
     *
     * @param signature
     * @param method_name
     * @param data_typeStr
     * @param params
     * @param params_data_types
     */
    public static void train(String signature, String method_name, String data_typeStr, List<String> params, List<String> params_data_types) {

        //Collection<Word> words_list = NLPToolkit.getWordsWithFeatures(identifier_sentence, data_type);
        DataTypeTable data_type = NLPToolkit.getDataTypeWithProperties(data_typeStr);
        DataTypeModel dtm = DataTypeTable.insertOrGet(data_type);

        data_type.setDtid((int) dtm.getId());
        MethodTable m = NLPToolkit.getMethodWithProperties(signature, method_name, data_type.getDtid());
        MethodModel mm = null;

        if (MethodTable.insert(m)) {
            System.out.println("Insertion Success");
            mm = (MethodModel) MethodTable.findBySignature(signature);
        }

        if (mm != null) {
            m.setMid((int) mm.getId());
            for (int i = 0; i < params.size(); i++) {
                int dtid;

                data_type = NLPToolkit.getDataTypeWithProperties(params_data_types.get(i));
                dtm = DataTypeTable.insertOrGet(data_type);
                dtid = ((int) dtm.getId());
                data_type.setDtid(dtid);

                ParameterTable p = NLPToolkit.getParameterWithProperties(params.get(i), data_type.getDtid());
                p.setFK_mid(m.getMid());
                ParameterModel pm = (ParameterModel) ParameterTable.insert(p);
            }
        } else {
            throw new NullPointerException();
        }
    }

    /**
     *
     */
    private static void prepareTrainingProcess() throws FileNotSelected {

        JOptionPane.showMessageDialog(null, "Select projects to be used in training.");

        // Choose Java Source Files via FilePicker
        //files_list = FilePicker.chooseAndGetJavaFiles();
        files_list = FilePicker.chooseDirAndGetJavaFiles();
        if (files_list == null || files_list.isEmpty()) {
            errorlog.add(new com.nerzid.autocomment.log.Error(ErrorMessage.FILE_HANDLER_NO_JAVA_FILE));
        } else {
            trainingProcessStarted = true;
            for (File f : files_list) {
                MainFrame.latch.countDown();

                current_training_file_name = f.getName();

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
                // WARNING: Priority is important DO NOT CHANGE
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
                currentFileNo++;
                //System.out.println("Status: " + currentFileNo + "/" + files_list.size());
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
