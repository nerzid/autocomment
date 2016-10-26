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

import static com.nerzid.autocomment.database.DataTypeModel.COLUMN_LEMMA;
import static com.nerzid.autocomment.database.DataTypeModel.COLUMN_POSTAG;
import static com.nerzid.autocomment.database.DataTypeModel.COLUMN_SIMPLIFIED_IDENTIFIER;

/**
 *
 * @author nerzid
 */
public class DataType {
    private int dtid;
    private String identifier;
    private String simplifiedIdentifier;
    private String lemma;
    private String postag;

    public DataType() {
    }

    public DataType(String identifier, String simplifiedIdentifier, String lemma, String postag) {
        this.identifier = identifier;
        this.simplifiedIdentifier = simplifiedIdentifier;
        this.lemma = lemma;
        this.postag = postag;
    }

    public static DataTypeModel insertOrGet(DataType dt) {
        DataTypeModel data_type = null;
        data_type = DataTypeModel.findFirst(
                DataTypeModel.COLUMN_SIMPLIFIED_IDENTIFIER + " = ? AND "
                + DataTypeModel.COLUMN_LEMMA + " = ? AND "
                + DataTypeModel.COLUMN_POSTAG + " = ?",
                dt.getSimplifiedIdentifier(), 
                dt.getLemma(), 
                dt.getPostag());
        if (data_type == null) {
            data_type = new DataTypeModel().set(
                    COLUMN_SIMPLIFIED_IDENTIFIER, dt.getSimplifiedIdentifier(),
                    COLUMN_LEMMA, dt.getLemma(),
                    COLUMN_POSTAG, dt.getPostag());
            if (data_type.saveIt())
                return data_type;
            else 
                return null;
        } else {
            return DataTypeModel.getDataTypeModelUsingIdentifier(dt.getIdentifier());
        }
    }
    
    public String addSimplifiedIdentifier(String si) {
        return simplifiedIdentifier += si;
    }
    
    public String addLemma(String l) {
        return lemma += l;
    }
    
    public String addPostag(String p) {
        return postag += p;
    }
    
    public int getDtid() {
        return dtid;
    }

    public void setDtid(int dtid) {
        this.dtid = dtid;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    public String getSimplifiedIdentifier() {
        return simplifiedIdentifier;
    }

    public void setSimplifiedIdentifier(String simplifiedIdentifier) {
        this.simplifiedIdentifier = simplifiedIdentifier;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public String getPostag() {
        return postag;
    }

    public void setPostag(String postag) {
        this.postag = postag;
    }
}
