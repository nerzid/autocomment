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
package com.nerzid.autocomment.database;

/**
 *
 * @author nerzid
 */
public class ParameterModel {
     public static final String TABLE_NAME = "Parameter";
    
    public static final String COLUMN_PID = "pid";
    public static final String COLUMN_ORIGINAL = "original";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_LEMMA = "lemma";
    public static final String COLUMN_POSTAG = "postag";
    public static final String COLUMN_FK_DTID = "FK_dtid";
    public static final String COLUMN_FK_MID = "FK_mid";
    
    public static final String COLUMN_PID_FIELD = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final String COLUMN_ORIGINAL_FIELD = "varchar(255)";
    public static final String COLUMN_TEXT_FIELD = "varchar(255)";
    public static final String COLUMN_LEMMA_FIELD = "varchar(255)";
    public static final String COLUMN_POSTAG_FIELD = "varchar(255)";
    public static final String COLUMN_FK_DTID_FIELD = "INTEGER";
    public static final String COLUMN_FK_MID_FIELD = "INTEGER";
    
    public static final String COLUMN_FK_DTID_FIELD_FOREIGNKEY = "FOREIGN KEY("
            + COLUMN_FK_DTID + ")" + " "
            + "REFERENCES " + DataTypeModel.TABLE_NAME
            + "(" + DataTypeModel.COLUMN_DTID + ")";
    public static final String COLUMN_FK_MID_FIELD_FOREIGNKEY = "FOREIGN KEY("
            + COLUMN_FK_MID + ")" + " "
            + "REFERENCES " + MethodModel.TABLE_NAME
            + "(" + MethodModel.COLUMN_MID + ")";
    
    public static final int COLUMN_PID_INT = 1;
    public static final int COLUMN_ORIGINAL_INT = 2;
    public static final int COLUMN_TEXT_INT = 3;
    public static final int COLUMN_LEMMA_INT = 4;
    public static final int COLUMN_POSTAG_INT = 5;
    public static final int COLUMN_FK_DTID_INT = 6;
    public static final int COLUMN_FK_MID_INT = 7;
}
