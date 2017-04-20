/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.jbpm.workflow.instance.node;

import org.jbpm.test.util.AbstractBaseTest;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Assert;
import java.util.List;
import org.slf4j.LoggerFactory;
import java.util.regex.Matcher;
import StateBasedNodeInstance.PARAMETER_MATCHER;
import org.junit.Test;

public class ParameterResolverTest extends AbstractBaseTest {
    public void addLogger() {
        logger = LoggerFactory.getLogger(this.getClass());
    }

    @Test
    public void testSingleVariable() {
        String[] expected = new String[]{ "var1" };
        String s = "#{var1}";
        List<String> foundVariables = new ArrayList<String>();
        Matcher matcher = PARAMETER_MATCHER.matcher(s);
        while (matcher.find()) {
            String paramName = matcher.group(1);
            foundVariables.add(paramName);
        } 
        // assert equals int{1} to void{Assert}
        Assert.assertEquals(1, foundVariables.size());
        // assert equals List{Arrays.asList(expected)} to void{Assert}
        Assert.assertEquals(Arrays.asList(expected), foundVariables);
    }

    @Test
    public void testSingleVariableEnclosedWithText() {
        String[] expected = new String[]{ "var1" };
        String s = "this is my #{var1} variable";
        List<String> foundVariables = new ArrayList<String>();
        Matcher matcher = PARAMETER_MATCHER.matcher(s);
        while (matcher.find()) {
            String paramName = matcher.group(1);
            foundVariables.add(paramName);
        } 
        // assert equals int{1} to void{Assert}
        Assert.assertEquals(1, foundVariables.size());
        // assert equals List{Arrays.asList(expected)} to void{Assert}
        Assert.assertEquals(Arrays.asList(expected), foundVariables);
    }

    @Test
    public void testMultiVariableWithoutWhitespace() {
        String[] expected = new String[]{ "var1" , "var2" };
        String s = "#{var1}=#{var2}";
        List<String> foundVariables = new ArrayList<String>();
        Matcher matcher = PARAMETER_MATCHER.matcher(s);
        while (matcher.find()) {
            String paramName = matcher.group(1);
            foundVariables.add(paramName);
        } 
        // assert equals int{2} to void{Assert}
        Assert.assertEquals(2, foundVariables.size());
        // assert equals List{Arrays.asList(expected)} to void{Assert}
        Assert.assertEquals(Arrays.asList(expected), foundVariables);
    }

    @Test
    public void testMultiVariableSeparatedWithComma() {
        String[] expected = new String[]{ "var1" , "var2" };
        String s = "#{var1},#{var2}";
        List<String> foundVariables = new ArrayList<String>();
        Matcher matcher = PARAMETER_MATCHER.matcher(s);
        while (matcher.find()) {
            String paramName = matcher.group(1);
            foundVariables.add(paramName);
        } 
        // assert equals int{2} to void{Assert}
        Assert.assertEquals(2, foundVariables.size());
        // assert equals List{Arrays.asList(expected)} to void{Assert}
        Assert.assertEquals(Arrays.asList(expected), foundVariables);
    }

    @Test
    public void testMultiVariableEnclosedWithText() {
        String[] expected = new String[]{ "var1" , "var2" };
        String s = "Here are my two #{var1},#{var2} variables";
        List<String> foundVariables = new ArrayList<String>();
        Matcher matcher = PARAMETER_MATCHER.matcher(s);
        while (matcher.find()) {
            String paramName = matcher.group(1);
            foundVariables.add(paramName);
        } 
        // assert equals int{2} to void{Assert}
        Assert.assertEquals(2, foundVariables.size());
        // assert equals List{Arrays.asList(expected)} to void{Assert}
        Assert.assertEquals(Arrays.asList(expected), foundVariables);
    }

    @Test
    public void testMultiVariableNextToEachOther() {
        String[] expected = new String[]{ "var1" , "var2" };
        String s = "#{var1}#{var2}";
        List<String> foundVariables = new ArrayList<String>();
        Matcher matcher = PARAMETER_MATCHER.matcher(s);
        while (matcher.find()) {
            String paramName = matcher.group(1);
            foundVariables.add(paramName);
        } 
        // assert equals int{2} to void{Assert}
        Assert.assertEquals(2, foundVariables.size());
        // assert equals List{Arrays.asList(expected)} to void{Assert}
        Assert.assertEquals(Arrays.asList(expected), foundVariables);
    }
}

