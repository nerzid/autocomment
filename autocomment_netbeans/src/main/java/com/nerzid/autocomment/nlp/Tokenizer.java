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
        if (data_type.charAt(len - 1) != ']' && data_type.charAt(len - 1) != '>') {
            return getLastStringBeforeDot(data_type);
        }
        
        return "Collection of " + removePunctuations(getLastStringBeforeDot(data_type));
    }

    /**
     * Gets last string before dot. e.g. for 'java.io.File', output will be
     * 'File'.
     * 
     * @param s
     * @return
     */
    private static String getLastStringBeforeDot(String s) {
        String[] splitted = s.split("\\.");
        if (splitted.length != 0)
            return splitted[splitted.length - 1];
        else
            return s;
    }
    
    /**
     * Removes all
     * @param s
     * @return 
     */
    private static String removePunctuations(String s) {
        String res = "";
        for (Character c : s.toCharArray()) {
            if(Character.isLetterOrDigit(c))
                res += c;
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
