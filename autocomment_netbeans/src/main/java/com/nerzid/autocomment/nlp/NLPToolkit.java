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

import com.nerzid.autocomment.database.DataType;
import com.nerzid.autocomment.database.Method;
import com.nerzid.autocomment.database.Parameter;
import edu.stanford.nlp.simple.Sentence;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author nerzid
 */
public class NLPToolkit {

    /**
     * Gets the {@link com.nerzid.autocomment.database.Method} instance.
     * 
     * @param signature
     * @param method_name
     * @param dtid
     * @return 
     */
    public static Method getMethodWithProperties(String signature, String method_name, int dtid){
        Collection<String> identifiers = Tokenizer.split(method_name);
        String identifier_sentence = Tokenizer.getIdentifiersSentence(identifiers);
        String[] identifiers_list = identifier_sentence.split(" ");
        Sentence sent = new Sentence(identifier_sentence);
        List<String> lemmas_list = sent.lemmas();
        List<String> postags_list = sent.posTags();
        
        Method m = new Method();
        for(int i = 0; i < lemmas_list.size(); i++){
            m.addSplittedIdentifier(identifiers_list[i]);
            m.addLemma(lemmas_list.get(i));
            m.addPostag(postags_list.get(i));
        }
        m.setSignature(signature);
        m.setIdentifier(method_name);
        m.setFK_dtid(dtid);
        
        return m;
    }
    
    public static Parameter getParameterWithProperties(String param_name, int dtid) {
        Collection<String> identifiers = Tokenizer.split(param_name);
        String identifier_sentence = Tokenizer.getIdentifiersSentence(identifiers);
        String[] identifiers_list = identifier_sentence.split(" ");
        Sentence sent = new Sentence(identifier_sentence);
        List<String> lemmas_list = sent.lemmas();
        List<String> postags_list = sent.posTags();
        
        Parameter p = new Parameter();
        for(int i = 0; i < lemmas_list.size(); i++){
            p.addSplittedIdentifier(identifiers_list[i]);
            p.addLemma(lemmas_list.get(i));
            p.addPostag(postags_list.get(i));
        }
        p.setFK_dtid(dtid);
        
        return p;
    }
    
    public static DataType getDataTypeWithProperties(String data_typeStr) {
        String original = data_typeStr;
        String text = Tokenizer.getCollectionOrMapString(original);
        String[] identifiers_list = text.split(" ");
        Sentence sent = new Sentence(text);
        List<String> lemmas_list = sent.lemmas();
        List<String> postags_list = sent.posTags();
        
        DataType data_type = new DataType();
        for (int i = 0; i < lemmas_list.size(); i++) {
            data_type.addSimplifiedIdentifier(identifiers_list[i]);
            data_type.addLemma(lemmas_list.get(i));
            data_type.addPostag(postags_list.get(i));
        }
        data_type.setIdentifier(original);
        
        return data_type;
    }
}
