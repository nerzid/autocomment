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
import static com.nerzid.autocomment.database.MethodModel.COLUMN_SIGNATURE;
import static com.nerzid.autocomment.database.MethodModel.COLUMN_IDENTIFIER;
import static com.nerzid.autocomment.database.MethodModel.COLUMN_SPLITTED_IDENTIFIER;

/**
 *
 * @author nerzid
 */
public class Method {

    private int mid;
    private String signature;
    private String identifier;
    private String splittedIdentifier;
    private String lemma;
    private String postag;
    private int FK_dtid;

    public Method() {
        identifier = "";
        splittedIdentifier = "";
        lemma = "";
        postag = "";
    }

    public Method(String signature, String original, String text, String lemma, String postag) {
        this.signature = signature;
        this.identifier = original;
        this.splittedIdentifier = text;
        this.lemma = lemma;
        this.postag = postag;
    }

    /**
     * Inserts Method w into Database
     *
     * @param m
     * @return True if successfully inserted into database false if not.
     */
    public static MethodModel insertOrGet(Method m) {
        MethodModel mm = null;
        try {
            mm = MethodModel.findFirst(
                    MethodModel.COLUMN_SIGNATURE + " = ? AND "
                    + MethodModel.COLUMN_IDENTIFIER + " = ? AND "
                    + MethodModel.COLUMN_SPLITTED_IDENTIFIER + " = ? AND "
                    + MethodModel.COLUMN_LEMMA + " = ? AND "
                    + MethodModel.COLUMN_POSTAG + " = ? AND "
                    + MethodModel.COLUMN_FK_DTID + " = ?",
                    m.getSignature(),
                    m.getIdentifier(),
                    m.getSplittedIdentifier(),
                    m.getLemma(),
                    m.getPostag(),
                    m.getFK_dtid());
            if (mm == null) {
                mm = new MethodModel().set(
                        COLUMN_SIGNATURE, m.getSignature(),
                        COLUMN_IDENTIFIER, m.getIdentifier(),
                        COLUMN_SPLITTED_IDENTIFIER, m.getSplittedIdentifier(),
                        COLUMN_LEMMA, m.getLemma(),
                        COLUMN_POSTAG, m.getPostag(),
                        COLUMN_FK_DTID, m.getFK_dtid());
                if (mm.saveIt()) {
                    return mm;
                } else {
                    return null;
                }
            } else {
                return MethodModel.getMethodModelUsingSignature(m.getSignature());
            }
        } catch (NullPointerException e) {
            mm = new MethodModel().set(
                    COLUMN_SIGNATURE, m.getSignature(),
                    COLUMN_IDENTIFIER, m.getIdentifier(),
                    COLUMN_SPLITTED_IDENTIFIER, m.getSplittedIdentifier(),
                    COLUMN_LEMMA, m.getLemma(),
                    COLUMN_POSTAG, m.getPostag(),
                    COLUMN_FK_DTID, m.getFK_dtid());
            if (mm.saveIt()) {
                return mm;
            } else {
                return null;
            }
        }

    }

    public String addSplittedIdentifier(String si) {
        return splittedIdentifier += si + " ";
    }

    public String addLemma(String l) {
        return lemma += l + " ";
    }

    public String addPostag(String p) {
        return postag += p + " ";
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getSplittedIdentifier() {
        return splittedIdentifier;
    }

    public void setSplittedIdentifier(String splittedIdentifier) {
        this.splittedIdentifier = splittedIdentifier;
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
