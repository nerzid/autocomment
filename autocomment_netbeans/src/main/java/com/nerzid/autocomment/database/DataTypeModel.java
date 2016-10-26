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
@Table(value = "DataType")
public class DataTypeModel extends Model{
    public static final String TABLE_NAME = "DataType";
    
    public static final String COLUMN_DTID = "id";
    public static final String COLUMN_IDENTIFIER = "identifier";
    public static final String COLUMN_SIMPLIFIED_IDENTIFIER = "simplified_identifier";
    public static final String COLUMN_LEMMA = "lemma";
    public static final String COLUMN_POSTAG = "postag";
    
    public static final String COLUMN_DTID_FIELD = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final String COLUMN_IDENTIFIER_FIELD = "varchar(255)";
    public static final String COLUMN_SIMPLIFIED_IDENTIFIER_FIELD = "varchar(255)";
    public static final String COLUMN_LEMMA_FIELD = "varchar(255)";
    public static final String COLUMN_POSTAG_FIELD = "varchar(255)";

    public static final int COLUMN_DTID_INT = 1;
    public static final int COLUMN_IDENTIFIER_INT = 2;
    public static final int COLUMN_SIMPLIFIED_IDENTIFIER_INT = 3;
    public static final int COLUMN_LEMMA_INT = 4;
    public static final int COLUMN_POSTAG_INT = 5;
    
    public static DataTypeModel getDataTypeModelUsingIdentifier(String identifier) {
        DataTypeModel data_type;
        data_type = (DataTypeModel) DataTypeModel.findFirst(
                DataTypeModel.COLUMN_IDENTIFIER + " = ?",
                identifier);
        return data_type;
    }
    
    public static DataTypeModel getDataTypeModelUsingDtid(int dtid) {
        DataTypeModel data_type;
        data_type = (DataTypeModel) DataTypeModel.findFirst(
            DataTypeModel.COLUMN_DTID + " = ?",
            dtid);
        return data_type;
    }
}
