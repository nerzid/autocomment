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
package com.nerzid.autocomment.nlp;

import com.nerzid.autocomment.database.Word;
import com.nerzid.autocomment.database.WordGroup;
import edu.stanford.nlp.simple.Sentence;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author nerzid
 */
public class NLPToolkit {
    
    /**
     * 
     * @param identifiers_sentence
     * @param data_type
     * @return
     * @deprecated Use {@link #getWordGroup(java.lang.String, java.lang.String) getWordGroup} instead
     */
    @Deprecated
    public static Collection<Word> getWordsWithFeatures(String identifiers_sentence, String data_type){
        Collection<Word> words_list = new ArrayList<>();
        String[] identifiers_list = identifiers_sentence.split(" ");
        Sentence sent = new Sentence(identifiers_sentence);
        List<String> lemmas_list = sent.lemmas();
        List<String> posttags_list = sent.posTags();
        
        
        for(int i = 0; i < lemmas_list.size(); i++){
            words_list.add(new Word(identifiers_list[i],
                    lemmas_list.get(i),
                    posttags_list.get(i),
                    data_type
            ));
        }
        return words_list;
    }
    
    /**
     * Gets the {@link com.nerzid.autocomment.database.WordGroup} instance.
     * 
     * @param identifiers_sentence Use {@link com.nerzid.autocomment.nlp.Tokenizer#getIdentifiersSentence(java.util.Collection) getIdentifiersSentence}
     * @param data_type Use {@link com.nerzid.autocomment.nlp.Tokenizer#simplifyDataType(java.lang.String) simplifyDataType}
     * @return 
     */
    public static WordGroup getWordGroup(String identifiers_sentence, String data_type){
        Collection<Word> words_list = new ArrayList<>();
        String[] identifiers_list = identifiers_sentence.split(" ");
        Sentence sent = new Sentence(identifiers_sentence);
        List<String> lemmas_list = sent.lemmas();
        List<String> posttags_list = sent.posTags();
 
        for(int i = 0; i < lemmas_list.size(); i++){
            words_list.add(new Word(identifiers_list[i],
                    lemmas_list.get(i),
                    posttags_list.get(i),
                    data_type
            ));
        }
        WordGroup wg = new WordGroup(words_list);
        return wg;
    }
}
