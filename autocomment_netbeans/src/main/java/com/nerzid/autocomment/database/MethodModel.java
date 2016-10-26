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

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

/**
 *
 * @author nerzid
 */
@Table(value = "WordGroup")
public class MethodModel extends Model {

    public static final String TABLE_NAME = "Method";
    
    public static final String COLUMN_MID = "mid";
    public static final String COLUMN_SIGNATURE = "signature";
    public static final String COLUMN_IDENTIFIER = "identifier";
    public static final String COLUMN_SPLITTED_IDENTIFIER = "splitted_identifier";
    public static final String COLUMN_LEMMA = "lemma";
    public static final String COLUMN_POSTAG = "postag";
    public static final String COLUMN_FK_DTID = "FK_dtid";
    
    public static final String COLUMN_MID_FIELD = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final String COLUMN_SIGNATURE_FIELD = "varchar(255)";
    public static final String COLUMN_IDENTIFIER_FIELD = "varchar(255)";
    public static final String COLUMN_SPLITTED_IDENTIFIER_FIELD = "varchar(255)";
    public static final String COLUMN_LEMMA_FIELD = "varchar(255)";
    public static final String COLUMN_POSTAG_FIELD = "varchar(255)";
    public static final String COLUMN_FK_DTID_FIELD = "INTEGER";
    public static final String COLUMN_FK_PGID_FIELD = "INTEGER";
    
    public static final String COLUMN_FK_DTID_FIELD_FOREIGNKEY = "FOREIGN KEY("
            + COLUMN_FK_DTID + ")" + " "
            + "REFERENCES " + DataTypeModel.TABLE_NAME
            + "(" + DataTypeModel.COLUMN_DTID + ")";

    public static final int COLUMN_MID_INT = 1;
    public static final int COLUMN_SIGNATURE_INT = 2;
    public static final int COLUMN_IDENTIFIER_INT = 3;
    public static final int COLUMN_SPLITTED_IDENTIFIER_INT = 4;
    public static final int COLUMN_LEMMA_INT = 5;
    public static final int COLUMN_POSTAG_INT = 6;
    public static final int COLUMN_FK_DTID_INT = 7;
    public static final int COLUMN_FK_PGID_INT = 8;
    
    public static MethodModel getMethodModelUsingSignature(String signature) {
        MethodModel mm;
        mm = (MethodModel) MethodModel.findFirst(
                MethodModel.COLUMN_SIGNATURE + " = ?",
                signature);
        return mm;
    }
    
    public static MethodModel getMethodModelUsingMid(int mid) {
        MethodModel mm;
        mm = (MethodModel) MethodModel.findFirst(
            MethodModel.COLUMN_MID + " = ?",
            mid);
        return mm;
    }

}
