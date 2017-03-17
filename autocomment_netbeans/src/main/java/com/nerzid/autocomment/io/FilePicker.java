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
package com.nerzid.autocomment.io;

import com.nerzid.autocomment.exception.FileNotSelected;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author nerzid
 */
public class FilePicker {

    /**
     * Prepares and returns java files to be auto-commented in the future
     *
     * @return
     */
    public static ArrayList<File> chooseAndGetJavaFiles() throws FileNotSelected {
        setLookAndFeel();
        ArrayList<File> files_list = null;

        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JAVA Source Files (.java)", "java");
        fc.setFileFilter(filter);
        fc.setMultiSelectionEnabled(true);

        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File[] files_arr = fc.getSelectedFiles();
            files_list = new ArrayList<>(Arrays.asList(files_arr));
        } else {
            throw new FileNotSelected(FileNotSelected.MESSAGE_NO_FILE_SELECTED);
        }

        return files_list;
    }

    public static LinkedList<File> chooseDirAndGetJavaFiles() throws FileNotSelected {
        setLookAndFeel();
        LinkedList<File> files_list = null;

        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        String[] extn = {"java"};

        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            files_list = (LinkedList<File>) FileUtils.listFiles(fc.getSelectedFile(), extn, true);
        } else {
            throw new FileNotSelected(FileNotSelected.MESSAGE_NO_DIR_SELECTED);
        }
        
        for (File f : files_list) {
            System.out.println("Name: " + f.getName() + " ||| Path: " + f.getAbsolutePath());
        }

        return files_list;
    }
    
    public static File chooseFile() throws FileNotSelected{
        setLookAndFeel();
        
        File f = null;
        
        JFileChooser fc = new  JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JAVA Source Files (.java)", "java");
        fc.setFileFilter(filter);
        fc.setMultiSelectionEnabled(false);
        
        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            f = fc.getSelectedFile();
        } else {
            throw new FileNotSelected(FileNotSelected.MESSAGE_NO_FILE_SELECTED);
        }
        
        return f;
    }
    
    public static File chooseDBFile() throws FileNotSelected{
        setLookAndFeel();
        
        JOptionPane.showMessageDialog(null, "Choose a database file with an extension of .db");
        File f = null;
        
        JFileChooser fc = new  JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Database File (.db)", "db");
        fc.setFileFilter(filter);
        fc.setMultiSelectionEnabled(false);
        
        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            f = fc.getSelectedFile();
        } else {
            throw new FileNotSelected(FileNotSelected.MESSAGE_NO_FILE_SELECTED);
        }
        
        return f;
    }
    
    public static File chooseDir() throws FileNotSelected{
        setLookAndFeel();
        
        File f = null;
        
        JFileChooser fc = new  JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setMultiSelectionEnabled(false);
        
        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            f = fc.getSelectedFile();
        } else {
            throw new FileNotSelected(FileNotSelected.MESSAGE_NO_FILE_SELECTED);
        }
        
        return f;
    }
    
    public static String getFilePath(File f){
        return f == null ? null : f.getPath(); 
    }

    private static void setLookAndFeel() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } else if (System.getProperty("os.name").contains("Linux")) {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(FilePicker.class.getName()).log(Level.SEVERE, null, ex);

        }
    }
}
