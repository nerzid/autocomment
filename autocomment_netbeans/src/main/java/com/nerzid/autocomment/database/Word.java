/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nerzid.autocomment.database;

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
