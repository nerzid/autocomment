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
    private String text;
    private String lemma;
    private String postag;
    private String data_type;
    private String word_group;

    public Parameter() {
    }
 
    public Parameter(String text, String lemma, String postag, String data_type, String word_group) {
        this.text = text;
        this.lemma = lemma;
        this.postag = postag;
        this.data_type = data_type;
        this.word_group = word_group;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public void setPostag(String postag) {
        this.postag = postag;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
    }

    public void setWord_group(String word_group) {
        this.word_group = word_group;
    }
}
