/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nerzid.autocomment.swum;

import com.nerzid.autocomment.database.Word;
import edu.stanford.nlp.simple.Sentence;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author nerzid
 */
public class NLPToolkit {
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
}
