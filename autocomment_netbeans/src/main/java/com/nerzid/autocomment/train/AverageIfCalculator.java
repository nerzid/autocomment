package com.nerzid.autocomment.train;

import com.nerzid.autocomment.exception.FileNotSelected;
import com.nerzid.autocomment.io.FilePicker;
import com.nerzid.autocomment.processor.S_UnitMethodProcessor;
import static com.nerzid.autocomment.train.Trainer.files_list;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.support.JavaOutputProcessor;

/**
 *
 * @author nerzid
 */
public class AverageIfCalculator {

    public static List<File> files_list;

    public static void main(String[] args) {

        try {
            files_list = FilePicker.chooseDirAndGetJavaFiles();
        } catch (FileNotSelected ex) {
            Logger.getLogger(AverageIfCalculator.class.getName()).log(Level.SEVERE, null, ex);
        }

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
            // WARNING: Priority is important DO NOT CHANGE
            // JavaOutputProcessor must be at LOWERMOST to get all differences
            // and write them
            l.addProcessor(new S_UnitMethodProcessor());

            // Uncomment this if you want to insert comment into code
//                l.addProcessor(jop);
            // Debuglevel 
            // env.setLevel("0");
            // Run the Launcher
            try {
                l.run();
//                System.out.println("Average Depth:" + S_UnitMethodProcessor.calculateAverageIfDepth());
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

    }
}
