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

import static com.nerzid.autocomment.database.MethodModel.COLUMN_FK_DTID;
import static com.nerzid.autocomment.database.MethodModel.COLUMN_LEMMA;
import static com.nerzid.autocomment.database.MethodModel.COLUMN_POSTAG;
import static com.nerzid.autocomment.database.MethodModel.COLUMN_TEXT;

/**
 *
 * @author nerzid
 */
public class Method {
    private int mid;
    private String text;
    private String lemma;
    private String postag;
    private int FK_dtid;

    public Method() {
        text = "";
        lemma = "";
        postag = "";
    }

    public Method(String text, String lemma, String postag) {
        this.text = text;
        this.lemma = lemma;
        this.postag = postag;
    }

    /**
     * Inserts Method w into Database
     * 
     * @param w
     * @return True if successfully inserted into database false if not.
     */
    public static boolean insert(Method w) {
        if (MethodModel.findFirst(
                MethodModel.COLUMN_TEXT + " = ? AND "
                + MethodModel.COLUMN_LEMMA + " = ? AND "
                + MethodModel.COLUMN_POSTAG + " = ? ",
                w.getText(), w.getLemma(), w.getPostag(), w.getFK_dtid()) == null) {
            boolean isSucces = new MethodModel().set(
                    COLUMN_TEXT, w.getText(),
                    COLUMN_LEMMA, w.getLemma(),
                    COLUMN_POSTAG, w.getLemma(),
                    COLUMN_FK_DTID, w.getFK_dtid())
                    .saveIt();
            return isSucces;
        } else {
            return false;
        }
    }
    
    public String addText(String t) {
        return text += t;
    }
    
    public String addLemma(String l) {
        return lemma += l;
    }
    
    public String addPostag(String p) {
        return postag += p;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
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

    public int getFK_dtid() {
        return FK_dtid;
    }

    public void setFK_dtid(int FK_dtid) {
        this.FK_dtid = FK_dtid;
    }
    
}
