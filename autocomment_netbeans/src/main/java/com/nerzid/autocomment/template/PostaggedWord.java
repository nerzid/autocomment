/*
 *
 *  * Copyright 2016 nerzid.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.nerzid.autocomment.template;

import java.util.List;

/**
 * Created by @author nerzid on 18.04.2017.
 */
public class PostaggedWord {
    private Word verb1;
    private Word nounphrase1;
    private Word verb2;
    private Word nounphrase2;
    private Word preposition;
    private List<String> params;

    private boolean startingWithPRP;

    public PostaggedWord() {
    }

    public Word getVerb1() {
        return verb1;
    }

    public void setVerb1(Word verb1) {
        this.verb1 = verb1;
    }

    public Word getNounphrase1() {
        return nounphrase1;
    }

    public void setNounphrase1(Word nounphrase1) {
        this.nounphrase1 = nounphrase1;
    }

    public Word getVerb2() {
        return verb2;
    }

    public void setVerb2(Word verb2) {
        this.verb2 = verb2;
    }

    public Word getNounphrase2() {
        return nounphrase2;
    }

    public void setNounphrase2(Word nounphrase2) {
        this.nounphrase2 = nounphrase2;
    }

    public Word getPreposition() {
        return preposition;
    }

    public void setPreposition(Word preposition) {
        this.preposition = preposition;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    public boolean isStartingWithPRP() {
        return startingWithPRP;
    }

    public void setStartingWithPRP(boolean startingWithPRP) {
        this.startingWithPRP = startingWithPRP;
    }

    public void setTextUsingPostagsLength(String[] words, String postags[]) {
//        if (verb1 == null) {
//            return;
//        }

        int i = 0;
        boolean isVerb1Finished = false;
        boolean isNounPhrase1Finished = false;
        while (i < words.length) {
            if (postags[i].contains("VB")) {
                if (nounphrase1 != null) {
                    if (!nounphrase1.getText().isEmpty()) {
                        isNounPhrase1Finished = true;
                    }
                }
                if (!isVerb1Finished) {
                    if (verb1.getText().isEmpty()) {
                        verb1.setText(words[i]);
                    } else {
                        verb1.appendTextAfterSpace(words[i]);
                    }
                } else {
                    if (verb2.getText().isEmpty()) {
                        verb2.setText(words[i]);
                    } else {
                        verb2.appendTextAfterSpace(words[i]);
                    }
                }
            } else if (postags[i].contains("NN") || postags[i].contains("JJ")) {
                if (verb1 != null) {
                    if (!verb1.getText().isEmpty()) {
                        isVerb1Finished = true;
                    }
                }
                if (!isNounPhrase1Finished) {
                    if (nounphrase1.getText().isEmpty()) {
                        nounphrase1.setText(words[i]);
                    } else {
                        nounphrase1.appendTextAfterSpace(words[i]);
                    }
                } else {
                    if (nounphrase2.getText().isEmpty()) {
                        nounphrase2.setText(words[i]);
                    } else {
                        nounphrase2.appendTextAfterSpace(words[i]);
                    }
                }
            } else if (postags[i].contains("IN")) {
                preposition.setText(words[i]);
            }
            i++;
        }


    }
}
