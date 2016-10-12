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
