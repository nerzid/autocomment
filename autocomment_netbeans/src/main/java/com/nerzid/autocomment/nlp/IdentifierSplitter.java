/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nerzid.autocomment.nlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author nerzid
 */
public class IdentifierSplitter {

    private final static char[] PUNCTUATION = {'-', '_'};

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
            for (int j = 0; j < afterPuncsRemovedArr[i].length(); j++) {
                String word = afterPuncsRemovedArr[i];
                if (word.charAt(j) == ' ') {
                    continue;
                }
                if (Character.isUpperCase(word.charAt(j))) {
                    if (!newWord.isEmpty()) {
                        if (beforeWasUpperCase) {
                            waitingUpperCaseLetter = true;
                        }
                        if (!waitingUpperCaseLetter) {
                            splitted.add(newWord.toLowerCase());
                            newWord = "";
                        }
                    }
                    beforeWasUpperCase = true;
                } else {
                    if (waitingUpperCaseLetter) {
                        splitted.add(newWord.toLowerCase());
                        newWord = "";
                    }
                    beforeWasUpperCase = false;
                    waitingUpperCaseLetter = false;
                }
                newWord += word.charAt(j);

                // If it's the last character of the word
                // Then add it to the list
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