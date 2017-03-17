package com.nerzid.autocomment.generator;

import com.nerzid.autocomment.exception.FileNotSelected;
import com.nerzid.autocomment.io.FilePicker;
import com.nerzid.autocomment.processor.CommentGeneratorMethodProcessor;
import com.nerzid.autocomment.processor.CtCommentProcessor;
import com.nerzid.autocomment.processor.S_UnitMethodProcessor;
import com.nerzid.autocomment.processor.TrainerMethodProcessor;
import static com.nerzid.autocomment.train.Trainer.currentFileNo;
import static com.nerzid.autocomment.train.Trainer.errorlog;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.support.JavaOutputProcessor;

/**
 *
 * @author nerzid
 */
public class CommentGenerator {

    public static void main(String[] args) {
        File f = null;
        try {
            f = FilePicker.chooseFile();
        } catch (FileNotSelected ex) {
            Logger.getLogger(CommentGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

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
//                l.addProcessor(new CommentGeneratorMethodProcessor());
        l.addProcessor(new S_UnitMethodProcessor());

        // Uncomment this if you want to insert comment into code
//                l.addProcessor(jop);
        // Debuglevel 
        // env.setLevel("0");
        // Run the Launcher
        try {
            l.run();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

// Only avaliable for New spoon api
//    /**
//     * Prints model(AST) as String. Looks bad, needs some refactoring.
//     *
//     * @param l To get model and maybe some other components
//     */
//    public static void printModel(Launcher l) {
//        CtModel model = l.getModel();
//
//        Collection<CtType<?>> ls = model.getAllTypes();
//        
//        for (CtType<?> type : ls) {
//            System.out.println(type.getClass().getSimpleName() + " -> " + type.getShortRepresentation());
//            System.out.println("-");
//            for (CtField<?> field : type.getFields()) {
//                System.out.print("\t-");
//                System.out.println(field.getClass().getSimpleName() + " -> " + field.getShortRepresentation());
//            }
//
//            for (CtMethod<?> method : type.getMethods()) {
//                System.out.print("\t-");
//                System.out.println(method.getClass().getSimpleName() + " -> " + method.getShortRepresentation());
//                for (CtStatement stmt : method.getBody().getStatements()) {
//                    System.out.print("\t\t-");
//                    System.out.println(stmt.getClass().getSimpleName() + " -> " + stmt.getShortRepresentation());
//                    
//                }
//                
//            }
//        }
//    }
}
