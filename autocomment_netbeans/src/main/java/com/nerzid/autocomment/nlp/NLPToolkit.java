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

import com.nerzid.autocomment.database.DataTypeTable;
import com.nerzid.autocomment.database.MethodTable;
import com.nerzid.autocomment.database.ParameterTable;
import edu.stanford.nlp.simple.Sentence;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author nerzid
 */
public class NLPToolkit {

    // Below are the MUST BE VERB words. Those are indeed verbs, but Stanford NLP toolkit
    // assumes that they'r not verbs. So we hard-coded them.
    public static String[] mustVerbArr = {"sort", "fire", "copy", "swap", "check",
        "process", "append", "dump", "print",
        "println", "register", "resolve", "start",
        "end", "visit", "fill", "search", "use", "clone"};

    /**
     * Gets the {@link com.nerzid.autocomment.database.MethodTable} instance.
     *
     * @param signature
     * @param method_name
     * @param dtid
     * @return
     */
    public static MethodTable getMethodWithProperties(String signature, String method_name, int dtid) {
        Collection<String> identifiers = Tokenizer.split(method_name);
        String identifier_sentence = Tokenizer.getIdentifiersSentence(identifiers);
        identifier_sentence = "Joe " + identifier_sentence;
        String[] identifiers_list = identifier_sentence.split(" ");
        Sentence sent = new Sentence(identifier_sentence);
        List<String> lemmas_list = sent.lemmas();
        List<String> postags_list = sent.posTags();

        MethodTable m = new MethodTable();

        if (lemmas_list.size() > 2) {
            for (int i = 1; i < lemmas_list.size() - 1; i++) {
                m.addSplittedIdentifier(identifiers_list[i]);
                m.addLemma(lemmas_list.get(i));
                if (i == 1 && doesNeedToBeConverted(identifiers_list[i])) {
                    m.addPostag("VB");
                } else {
                    m.addPostag(postags_list.get(i));
                }
            }
        }

        m.addLastSplittedIdentifier(identifiers_list[lemmas_list.size() - 1]);
        m.addLastLemma(lemmas_list.get(lemmas_list.size() - 1));
        if (doesNeedToBeConverted(identifiers_list[identifiers_list.length-1]))
            m.addLastPostag("VB");
        else
            m.addLastPostag(postags_list.get(lemmas_list.size() - 1));
        m.setSignature(signature);
        m.setIdentifier(method_name);
        m.setFK_dtid(dtid);

        return m;
    }

    public static ParameterTable getParameterWithProperties(String param_name, int dtid) {
        Collection<String> identifiers = Tokenizer.split(param_name);
        String identifier_sentence = Tokenizer.getIdentifiersSentence(identifiers);
        identifier_sentence = "Joe " + identifier_sentence;
        String[] identifiers_list = identifier_sentence.split(" ");
        Sentence sent = new Sentence(identifier_sentence);
        List<String> lemmas_list = sent.lemmas();
        List<String> postags_list = sent.posTags();

        ParameterTable p = new ParameterTable();

        if (lemmas_list.size() > 2) {
            for (int i = 1; i < lemmas_list.size() - 1; i++) {
                p.addSplittedIdentifier(identifiers_list[i]);
                p.addLemma(lemmas_list.get(i));
                if (i == 1 && doesNeedToBeConverted(identifiers_list[i])) {
                    p.addPostag("VB");
                } else {
                    p.addPostag(postags_list.get(i));
                }
            }
        }

        p.addLastSplittedIdentifier(identifiers_list[lemmas_list.size() - 1]);
        p.addLastLemma(lemmas_list.get(lemmas_list.size() - 1));
        if (doesNeedToBeConverted(identifiers_list[identifiers_list.length-1]))
            p.addLastPostag("VB");
        else
            p.addLastPostag(postags_list.get(lemmas_list.size() - 1));

        p.setIdentifier(param_name);
        p.setFK_dtid(dtid);

        return p;
    }

    public static DataTypeTable getDataTypeWithProperties(String data_typeStr) {
        String original = data_typeStr;
        String text = Tokenizer.simplifyDataType(original);
        text = "Joe " + text;
        String[] identifiers_list = text.split(" ");
        Sentence sent = new Sentence(text);
        List<String> lemmas_list = sent.lemmas();
        List<String> postags_list = sent.posTags();

        DataTypeTable data_type = new DataTypeTable();

        if (lemmas_list.size() > 2) {
            for (int i = 1; i < lemmas_list.size() - 1; i++) {
                data_type.addSimplifiedIdentifier(identifiers_list[i]);
                data_type.addLemma(lemmas_list.get(i));
                if (i == 1 && doesNeedToBeConverted(identifiers_list[i])) {
                    data_type.addPostag("VB");
                } else {
                    data_type.addPostag(postags_list.get(i));
                }
            }
        }

        data_type.addLastSimplifiedIdentifier(identifiers_list[lemmas_list.size() - 1]);
        data_type.addLastLemma(lemmas_list.get(lemmas_list.size() - 1));
        data_type.addLastPostag(postags_list.get(lemmas_list.size() - 1));

        data_type.setIdentifier(original);

        return data_type;
    }

    public static boolean doesNeedToBeConverted(String identifier) {
        for (String s : mustVerbArr) {
            if (s.equals(identifier)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        Sentence sent = new Sentence("get average of student grades");
        sent.parse().pennPrint();
    }
}
