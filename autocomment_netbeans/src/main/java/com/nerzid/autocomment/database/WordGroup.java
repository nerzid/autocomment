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

import static com.nerzid.autocomment.database.WordModel.COLUMN_DATA_TYPE;
import static com.nerzid.autocomment.database.WordModel.COLUMN_LEMMA;
import static com.nerzid.autocomment.database.WordModel.COLUMN_POSTAG;
import static com.nerzid.autocomment.database.WordModel.COLUMN_TEXT;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author nerzid
 */
public class WordGroup {
    private Collection<Word> words;

    public WordGroup() {
    }
    
    public WordGroup(Collection<Word> words) {
        this.words = words;
    }

    /**
     * Inserts WordGroup w into Database
     * 
     * @param w
     * @return True if successfully inserted into database false if not.
     */
    public static boolean insert(WordGroup w) {
        if (WordModel.findFirst(
                WordModel.COLUMN_TEXT + " = ? AND "
                + WordModel.COLUMN_LEMMA + " = ? AND "
                + WordModel.COLUMN_POSTAG + " = ? AND "
                + WordModel.COLUMN_DATA_TYPE + " = ?",
                w.getAllTexts(), w.getAllLemmas(), w.getAllPostags(), w.getDataType()) == null) {
            boolean isSucces = new WordModel().set(
                    COLUMN_TEXT, w.getAllTexts(),
                    COLUMN_LEMMA, w.getAllLemmas(),
                    COLUMN_POSTAG, w.getAllPostags(),
                    COLUMN_DATA_TYPE, w.getDataType())
                    .saveIt();
            return isSucces;
        } else {
            return false;
        }
    }
    
    /**
     * Gets all text of words
     * 
     * @return A String of texts of words separated by space
     */
    public String getAllTexts(){
        String s = "";
        Iterator it = words.iterator();
        for (int i = 0; i < words.size(); i++) {
            s += ((Word)it.next()).getText();
            // If its not the last word, then we can safely add space between words
            if(i != words.size() - 1) {
                s += " ";
            }
        }
        return s;
    }
    
    /**
     * Gets all lemmas
     * 
     * @return A String of lemmas of words separated by space
     */
    public String getAllLemmas(){
        String s = "";
        Iterator it = words.iterator();
        for (int i = 0; i < words.size(); i++) {
            s += ((Word)it.next()).getLemma();
            // If its not the last word, then we can safely add space between words
            if(i != words.size() - 1) {
                s += " ";
            }
        }
        return s;
    }
    
    /**
     * Gets all postags
     * 
     * @return A String of postags of words separated by space
     */
    public String getAllPostags(){
        String s = "";
        Iterator it = words.iterator();
        for (int i = 0; i < words.size(); i++) {
            s += ((Word)it.next()).getPostag();
            // If its not the last word, then we can safely add space between words
            if(i != words.size() - 1) {
                s += " ";
            }
        }
        return s;
    }
    
    /**
     * Gets data type of words
     * 
     * @return data_type as String
     */
    public String getDataType(){
        String s = "";
        Iterator it = words.iterator();
        return ((Word)it.next()).getData_type();
    }
    
    public Collection<Word> getWords() {
        return words;
    }

    public void setWords(Collection<Word> words) {
        this.words = words;
    }
}
