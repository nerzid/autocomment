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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author nerzid
 */
public class Tokenizer {

    private final static char[] PUNCTUATION = {'-', '_'};

    /**
     * Simplifies the data_type. e.g java.lang.String to String e.g
     * java.util.List<java.lang.String> to Collection of String
     *
     * @param data_type
     * @return simplified version of data_type as String
     */
    public static String simplifyDataType(String data_type) {
        int len = data_type.length();

        // If last character of data_type isn't ']' or '>', that means this is not a collection
        // e.g. java.io.File[] is collection, but java.io.File doesn't.
        if (data_type.charAt(len - 1) != '>' && data_type.charAt(len - 1) != ']') {
            return getLastStringBeforeDot(data_type);
        }

        return getCollectionOrMapString(data_type);
        //return "Collection of " + removePunctuations(getLastStringBeforeDot(data_type));
    }

    public static String getCollectionOrMapString(String s) {
        String lastString = getLastStringBeforeDot(s);

        if (s.contains(",")) {
            return getMapString(lastString);
        } else {
            return getCollectionString(lastString);
        }
    }

    public static String getCollectionString(String s) {
        // Array
        if (s.contains("[")) {
            // check for dimensionality of array
            // '[' count will give us the number of dimensionalities
            int count = 0;
            for (Character ch : s.toCharArray()) {
                if (ch == '[') {
                    count++;
                }
            }
            return count + "D " + "Array of " + removePunctuations(s);
        } else {
            // Other Collections, such as list etc.
            // check for dimensionality of collection
            // '>' count will give us the number of dimensionalities
            int count = 0;
            for (Character ch : s.toCharArray()) {
                if (ch == '>') {
                    count++;
                }
            }
            String[] ss = s.split("<");
            String lastString = ss[ss.length - 1];
            return count + "D " + "Collection of " + removePunctuations(lastString);
        }
    }

    public static String getMapString(String s) {
        // check for dimensionality of collection
        // '>' count will give us the number of dimensionalities
        int count = 0;
        for (Character ch : s.toCharArray()) {
            if (ch == '>') {
                count++;
            }
        }
        String[] ss = s.split(",");
        String lastString = ss[ss.length - 1];
        return count + "D " + "Map of " + removePunctuations(lastString);
    }

    /**
     * Gets last string before dot. e.g. for 'java.io.File', output will be
     * 'File'.
     *
     * @param s
     * @return
     */
    public static String getLastStringBeforeDot(String s) {
        String[] splitted = s.split("\\.");
        if (splitted.length != 0) {
            return splitted[splitted.length - 1];
        } else {
            return s;
        }
    }

    /**
     * Removes all
     *
     * @param s
     * @return
     */
    private static String removePunctuations(String s) {
        String res = "";
        for (Character c : s.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                res += c;
            }
        }
        return res;
    }

    /**
     * Splits the identifier as String based on camelcase notation and
     * punctuation
     *
     * @param identifier Identifier to be splitted
     * @return List of strings comes from splitting the identifier
     */
    public static List<String> split(String identifier) {
        ArrayList<String> splitted = new ArrayList<>();

        // Split by Punctuations
        String afterPuncIdentifier = "";
        for (Character ch : identifier.toCharArray()) {
            if (isItPunctuation(ch)) {
                afterPuncIdentifier += " ";
            } else {
                afterPuncIdentifier += ch;
            }
        }
        String[] afterPuncsRemovedArr = afterPuncIdentifier.split(" ");

        // Split by CamelCase Notation
        for (int i = 0; i < afterPuncsRemovedArr.length; i++) {
            String newWord = "";
            boolean waitingUpperCaseLetter = false;
            boolean beforeWasUpperCase = false;
            boolean beforeWasNumber = false;
            for (int j = 0; j < afterPuncsRemovedArr[i].length(); j++) {
                String word = afterPuncsRemovedArr[i];
                char ch = word.charAt(j);
                if (ch == ' ') {
                    continue;
                }
                if (Character.isDigit(ch)) {
                    if (!newWord.isEmpty()) {
                        beforeWasNumber = true;
                        beforeWasUpperCase = false;
                        waitingUpperCaseLetter = false;
                    }
                } else if (Character.isUpperCase(ch)) {
                    if (!newWord.isEmpty()) {
                        if (beforeWasUpperCase) {
                            waitingUpperCaseLetter = true;
                        }
                        if (!waitingUpperCaseLetter) {
                            splitted.add(newWord.toLowerCase());
                            newWord = "";
                        }
                    }
                    beforeWasNumber = false;
                    beforeWasUpperCase = true;
                } else {
                    if (waitingUpperCaseLetter || beforeWasNumber) {
                        splitted.add(newWord.toLowerCase());
                        newWord = "";
                    }
                    beforeWasNumber = false;
                    beforeWasUpperCase = false;
                    waitingUpperCaseLetter = false;
                }
                newWord += ch;

                // If it's the last character of the word
                // Then add word to the list
                if (j == word.length() - 1) {
                    splitted.add(newWord.toLowerCase());
                }
            }
        }
        return splitted;
    }

    public static String getIdentifiersSentence(Collection<String> list) {
        String s = "";
        for (String iden : list) {
            s += iden + " ";
        }
        return s;
    }

    /**
     * Checks whether the given character is an punctuation or not
     *
     * @param ch
     * @return True if the given character is punctuation
     */
    private static boolean isItPunctuation(Character ch) {
        for (Character punc : PUNCTUATION) {
            if (punc.equals(ch)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets punctuations to be used on splitting methods.
     *
     * @return PUNCTUATION Character Array
     */
    public static char[] getPunctuations() {
        return PUNCTUATION;
    }
}
