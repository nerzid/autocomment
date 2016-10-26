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
public class Parameter {

    private int pid;
    private String identifier;
    private String splittedIdentifier;
    private String lemma;
    private String postag;
    private int FK_dtid;
    private int FK_mid;

    public Parameter() {
    }

    public Parameter(String identifier, String splittedIdentifier, String lemma, String postag) {
        this.identifier = identifier;
        this.splittedIdentifier = splittedIdentifier;
        this.lemma = lemma;
        this.postag = postag;
    }

    public static ParameterModel insert(Parameter p) {
        ParameterModel pm;
        pm = new ParameterModel().set(
                ParameterModel.COLUMN_IDENTIFIER + " = ? AND "
                + ParameterModel.COLUMN_SPLITTED_IDENTIFIER + " = ? AND "
                + ParameterModel.COLUMN_LEMMA + " = ? AND "
                + ParameterModel.COLUMN_POSTAG + " = ? AND "
                + ParameterModel.COLUMN_FK_DTID + " = ? AND "
                + ParameterModel.COLUMN_FK_MID + " = ?" ,
                p.getIdentifier(),
                p.getSplittedIdentifier(),
                p.getLemma(),
                p.getPostag(),
                p.getFK_dtid(),
                p.getFK_mid());
        pm.saveIt();
        return pm;
    }
    
    public String addSplittedIdentifier(String si) {
        return splittedIdentifier += si;
    }

    public String addLemma(String l) {
        return lemma += l;
    }

    public String addPostag(String p) {
        return postag += p;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
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

    public int getFK_mid() {
        return FK_mid;
    }

    public void setFK_mid(int FK_mid) {
        this.FK_mid = FK_mid;
    }

}
