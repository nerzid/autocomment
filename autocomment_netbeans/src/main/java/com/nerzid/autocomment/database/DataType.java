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
import static com.nerzid.autocomment.database.DataTypeModel.COLUMN_TEXT;

/**
 *
 * @author nerzid
 */
public class DataType {
    private int dtid;
    private String text;
    private String lemma;
    private String postag;

    public DataType() {
    }

    public DataType(String text, String lemma, String postag) {
        this.text = text;
        this.lemma = lemma;
        this.postag = postag;
    }

    public static boolean insert(DataType dt) {
        if (DataTypeModel.findFirst(
                DataTypeModel.COLUMN_TEXT + " = ? AND "
                + DataTypeModel.COLUMN_LEMMA + " = ? AND "
                + DataTypeModel.COLUMN_POSTAG + " = ?",
                dt.getText(), dt.getLemma(), dt.getPostag()) == null) {
            boolean isSucces = new DataTypeModel().set(
                    COLUMN_TEXT, dt.getText(),
                    COLUMN_LEMMA, dt.getLemma(),
                    COLUMN_POSTAG, dt.getPostag())
                    .saveIt();
            return isSucces;
        } else {
            return false;
        }
    }

    public int getDtid() {
        return dtid;
    }

    public void setDtid(int dtid) {
        this.dtid = dtid;
    }
    
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
