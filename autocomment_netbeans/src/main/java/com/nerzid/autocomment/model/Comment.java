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

package com.nerzid.autocomment.model;

import spoon.reflect.declaration.CtElement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by @author nerzid on 17.10.2017.
 */
public class Comment {
    private String summary;
    private List<String> importantStatements;

    private static HashSet<CtElement> commentedElements = new HashSet<>();

    public Comment() {
        summary = "";
        importantStatements = new ArrayList<>();
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getImportantStatements() {
        return importantStatements;
    }

    public void setImportantStatements(List<String> importantStatements) {
        this.importantStatements = importantStatements;
    }

    public void addImportantStatement(String stmt) {
        importantStatements.add(stmt);
    }

    public void appendToSummary(String text) {
        summary += text;
    }

    public void appendComment(Comment comment) {
        summary += " " + comment.getSummary();
        importantStatements.addAll(comment.getImportantStatements());
    }

    public static Comment empty() {
        return new Comment();
    }

    // TODO finish this.
    //e.g. this method find uniques this instance this instance  from .. > this method find uniques this instance from ..
    public String removeOccurences(String text) {
        String[] words = text.split(" ");
        int groupingCount = words.length / 2;
        for (int i = 0; i < text.length(); i++) {
            String groupingText = "";
            for (int j = 0; j < groupingCount; j++) {
                groupingText += words[j];
                if (j + 1 != groupingCount)
                    groupingText += " ";
            }
        }
        return "";
    }

    public void appendTextToAllImportantStatements(String text) {
        for (int i = 0; i < importantStatements.size(); i++) {
            importantStatements.set(i, importantStatements.get(i) + " " + text);
        }
    }

    public static void addToCommentedSet(CtElement elem) {
        commentedElements.add(elem);
    }

    public static boolean isCommented(CtElement elem) {
        return commentedElements.contains(elem);
    }

    public void removeDuplicateImportantStatements() {
        List<String> uniqueImportantStatements = new ArrayList<>();
        for (String importantStatement : importantStatements) {
            if (!uniqueImportantStatements.contains(importantStatement))
                uniqueImportantStatements.add(importantStatement);
        }
        setImportantStatements(uniqueImportantStatements);
    }

    @Override
    public String toString() {
        String javadoc;

        removeDuplicateImportantStatements();
        javadoc = "Summary: " + summary;
        if (!importantStatements.isEmpty()) {
            javadoc += "\n\n";
            javadoc += "Important Statements: ";
            javadoc += "\n";
            for (int i = 0; i < importantStatements.size(); i++) {
                javadoc += (i + 1) + "-) " + importantStatements.get(i).replaceAll("\\s{2,}", " ").trim();
                if (i + 1 < importantStatements.size())
                    javadoc += "\n";
            }
        }
        return javadoc.replaceAll("is equal to null", "is null").replaceAll("is not equal to null", "is not null");
    }

    public String getCommentString(boolean isInvoc){
        if (isInvoc)
            return summary.replaceAll("is equal to null", "is null").replaceAll("is not equal to null", "is not null");
        else
            return toString();
    }
}
