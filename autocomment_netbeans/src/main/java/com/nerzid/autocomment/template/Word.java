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

/**
 * Created by @author nerzid on 18.04.2017.
 */
public class Word {
    private String postag;
    private String text;

    public Word() {
        postag = "";
        text = "";
    }

    public String getPostag() {
        return postag;
    }

    public void setPostag(String postag) {
        this.postag = postag;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void appendTextAfterSpace(String text) {

        this.text += " " + text;
    }

    public void appendPostagAfterSpace(String postag) {
        this.postag += " " + postag;
    }
}
