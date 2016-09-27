/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nerzid.autocomment.log;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author nerzid
 */
public class ErrorLog {
    private String text;
    private String f_name = "errorlog";
    private int count;
    private File f;

    public ErrorLog(String path) {
        text = "";
        if(!path.endsWith("/"))
            f = new File(path + "/" + f_name + ".txt");
        else
            f = new File(path + f_name + ".txt");
        count = 0;
    }

    public ErrorLog() {
        text = "";
        f = new File("./" + f_name + ".txt");
        count = 0;
    }
    
    /**
     * Adds given error e to related errorlog file f
     * @param e 
     */
    public void add(Error e){
        text += "Type: " + e.getType() + "\n"
                + "Message: " + e.getMessage() + "\n";
        
        text += "\n";
        count++;
    }
    
    /**
     * Adds error count to errorlog file f
     */
    private void addCountToErrorLog(){
        text += "Error Count = " + count;
    }
    
    /**
     * Writes all error texts to errorlog file and creates it at the given path
     * @throws IOException 
     */
    public void createErrorLogFile() throws IOException{
        addCountToErrorLog();
        FileUtils.writeStringToFile(f, text);
    }

    public int getCount() {
        return count;
    }
}
