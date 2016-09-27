/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nerzid.autocomment.database;

import static com.nerzid.autocomment.database.WordModel.COLUMN_DATA_TYPE;
import static com.nerzid.autocomment.database.WordModel.COLUMN_LEMMA;
import static com.nerzid.autocomment.database.WordModel.COLUMN_POSTAG;
import static com.nerzid.autocomment.database.WordModel.COLUMN_TEXT;
import java.util.Collection;

/**
 *
 * @author nerzid
 */
public class Word {

    private String text;
    private String lemma;
    private String postag;
    private String data_type;

    public Word(String text, String lemma, String postag, String data_type) {
        this.text = text;
        this.lemma = lemma;
        this.postag = postag;
        this.data_type = data_type;
    }

    /**
     * Insert all words from list into database.
     *
     * @param list
     * @return
     */
    public static boolean insertAll(Collection<Word> list) {
        for (Word w : list) {
            if (!insert(w)) {
                return false;
            }
        }
        return true;
    }

    /**
     * If Word w doesn't exist in database, insert it into database.
     *
     * @param w
     * @return
     */
    public static boolean insert(Word w) {
        if (WordModel.findFirst(
                WordModel.COLUMN_TEXT + " = ? AND "
                + WordModel.COLUMN_LEMMA + " = ? AND "
                + WordModel.COLUMN_POSTAG + " = ? AND "
                + WordModel.COLUMN_DATA_TYPE + " = ?",
                w.getText(), w.getLemma(), w.getPostag(), w.getData_type()) == null) {
            boolean isSucces = new WordModel().set(
                    COLUMN_TEXT, w.getText(),
                    COLUMN_LEMMA, w.getLemma(),
                    COLUMN_POSTAG, w.getPostag(),
                    COLUMN_DATA_TYPE, w.getData_type())
                    .saveIt();
            return isSucces;
        } else {
            return false;
        }
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

    public String getData_type() {
        return data_type;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
    }
}
