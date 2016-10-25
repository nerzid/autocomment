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

import com.nerzid.autocomment.database.Method;
import edu.stanford.nlp.simple.Sentence;
import java.util.List;

/**
 *
 * @author nerzid
 */
public class NLPToolkit {

    /**
     * Gets the {@link com.nerzid.autocomment.database.Method} instance.
     * 
     * @param identifiers_sentence Use {@link com.nerzid.autocomment.nlp.Tokenizer#getIdentifiersSentence(java.util.Collection) getIdentifiersSentence}
     * @param data_type Use {@link com.nerzid.autocomment.nlp.Tokenizer#simplifyDataType(java.lang.String) simplifyDataType}
     * @return 
     */
    public static Method getWordGroup(String identifiers_sentence, String data_type){
        String[] identifiers_list = identifiers_sentence.split(" ");
        Sentence sent = new Sentence(identifiers_sentence);
        List<String> lemmas_list = sent.lemmas();
        List<String> postags_list = sent.posTags();
        
        Method wg = new Method();
        for(int i = 0; i < lemmas_list.size(); i++){
            wg.addText(identifiers_list[i]);
            wg.addLemma(lemmas_list.get(i));
            wg.addPostag(postags_list.get(i));
        }
        return wg;
    }
}
