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
package com.nerzid.autocomment.exception;

/**
 *
 * @author nerzid
 */
public class FileNotSelected extends Exception {

    public static final String MESSAGE_NO_DIR_SELECTED = "No folder selected.";
    public static final String MESSAGE_NO_FILE_SELECTED = "No file selected.";
    
    /**
     * Creates a new instance of <code>FileNotSelected</code> without detail
     * message.
     */
    public FileNotSelected() {
        
    }
    
    public FileNotSelected(String msg){
        super(msg);
    }

}
