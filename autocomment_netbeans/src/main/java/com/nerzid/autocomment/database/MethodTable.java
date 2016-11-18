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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nerzid
 */
public class MethodTable {

    private int mid;
    private String signature;
    private String identifier;
    private String splittedIdentifier;
    private String lemma;
    private String postag;
    // list param
    private List<ParameterTable> params_list;
    private int FK_dtid;

    public MethodTable() {
        identifier = "";
        splittedIdentifier = "";
        lemma = "";
        postag = "";
    }

    public MethodTable(String signature, String original, String text, String lemma, String postag) {
        this.signature = signature;
        this.identifier = original;
        this.splittedIdentifier = text;
        this.lemma = lemma;
        this.postag = postag;
        params_list = new ArrayList<>();
    }

    /**
     * Inserts MethodTable w into Database
     *
     * @param m
     * @return True if successfully inserted into database false if not.
     */
    public static boolean insert(MethodTable m) {
        MethodModel mm = null;
        try {
            mm = findBySignature(m.getSignature());
            if (mm == null) {
                mm = new MethodModel().set(
                        COLUMN_SIGNATURE, m.getSignature(),
                        COLUMN_IDENTIFIER, m.getIdentifier(),
                        COLUMN_SPLITTED_IDENTIFIER, m.getSplittedIdentifier(),
                        COLUMN_LEMMA, m.getLemma(),
                        COLUMN_POSTAG, m.getPostag(),
                        COLUMN_FK_DTID, m.getFK_dtid());
                if (mm.saveIt()) {
                    return true;
                }
            }
        } catch (NullPointerException e) {
            System.out.println("Null : " + e.getMessage());
        }
        return false;
    }

    public static MethodModel findBySignature(String signature) {
        MethodModel mm = null;
        mm = MethodModel.findFirst(MethodModel.COLUMN_SIGNATURE + " = ?", signature);
        return mm;
    }

    /**
     *
     * @param identifier
     * @return
     * @throws NullPointerException
     * @deprecated Don't use this, it won't be unique.
     */
    @Deprecated
    public static String getSignatureFromDB(String identifier) throws NullPointerException {
        return MethodModel.findFirst(MethodModel.COLUMN_SIGNATURE + " = ?",
                identifier).get(COLUMN_SIGNATURE).toString();
    }

    public static String getSignatureFromDB(String identifier, List<String> data_types) throws NullPointerException {
        List<MethodModel> mm_list = MethodTable.getAll("find");

        for (MethodModel m : mm_list) {
            System.out.println(m.get(MethodModel.COLUMN_SIGNATURE));

            int mid = m.getInteger(MethodModel.COLUMN_MID);

            List<ParameterModel> params_models = ParameterTable.getAll(mid);
            if (params_models.size() != data_types.size()) {
                continue;
            }

            List<String> data_types_fromDB = DataTypeTable.getAll(params_models);
            if (data_types.size() != data_types_fromDB.size()) {
                continue;
            }

            boolean isSame = true;
            for (int i = 0; i < data_types.size(); i++) {
                if (!data_types.get(i).equals(data_types_fromDB.get(i))) {
                    isSame = false;
                    break;
                }
            }
            if (isSame) {
                return m.getString(MethodModel.COLUMN_SIGNATURE);

            }
        }

        return null;
    }

    public static List<MethodModel> getAll() {
        List<MethodModel> mm_list = null;

        mm_list = MethodModel.findAll();
        return mm_list;
    }

    public static List<MethodModel> getAll(String identifier) {
        List<MethodModel> mm_list = null;

        mm_list = MethodModel.find(MethodModel.COLUMN_IDENTIFIER + " = ?", identifier);
        return mm_list;
    }

    public String addSplittedIdentifier(String si) {
        return splittedIdentifier += si + " ";
    }

    public String addLastSplittedIdentifier(String si) {
        return splittedIdentifier += si;
    }

    public String addLemma(String l) {
        return lemma += l + " ";
    }

    public String addLastLemma(String l) {
        return lemma += l;
    }

    public String addPostag(String p) {
        return postag += p + " ";
    }

    public String addLastPostag(String p) {
        return postag += p;
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
