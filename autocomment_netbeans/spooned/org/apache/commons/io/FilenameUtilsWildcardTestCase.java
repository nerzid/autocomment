/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.apache.commons.io;

import org.junit.Assert;
import java.io.File;
import java.util.Locale;
import org.junit.Test;

public class FilenameUtilsWildcardTestCase {
    private static final boolean WINDOWS = (File.separatorChar) == '\\';

    // -----------------------------------------------------------------------
    // Testing:
    // FilenameUtils.wildcardMatch(String,String)
    @Test
    public void testMatch() {
        Assert.assertFalse(FilenameUtils.wildcardMatch(null, "Foo"));
        Assert.assertFalse(FilenameUtils.wildcardMatch("Foo", null));
        Assert.assertTrue(FilenameUtils.wildcardMatch(null, null));
        Assert.assertTrue(FilenameUtils.wildcardMatch("Foo", "Foo"));
        Assert.assertTrue(FilenameUtils.wildcardMatch("", ""));
        Assert.assertTrue(FilenameUtils.wildcardMatch("", "*"));
        Assert.assertFalse(FilenameUtils.wildcardMatch("", "?"));
        Assert.assertTrue(FilenameUtils.wildcardMatch("Foo", "Fo*"));
        Assert.assertTrue(FilenameUtils.wildcardMatch("Foo", "Fo?"));
        Assert.assertTrue(FilenameUtils.wildcardMatch("Foo Bar and Catflap", "Fo*"));
        Assert.assertTrue(FilenameUtils.wildcardMatch("New Bookmarks", "N?w ?o?k??r?s"));
        Assert.assertFalse(FilenameUtils.wildcardMatch("Foo", "Bar"));
        Assert.assertTrue(FilenameUtils.wildcardMatch("Foo Bar Foo", "F*o Bar*"));
        Assert.assertTrue(FilenameUtils.wildcardMatch("Adobe Acrobat Installer", "Ad*er"));
        Assert.assertTrue(FilenameUtils.wildcardMatch("Foo", "*Foo"));
        Assert.assertTrue(FilenameUtils.wildcardMatch("BarFoo", "*Foo"));
        Assert.assertTrue(FilenameUtils.wildcardMatch("Foo", "Foo*"));
        Assert.assertTrue(FilenameUtils.wildcardMatch("FooBar", "Foo*"));
        Assert.assertFalse(FilenameUtils.wildcardMatch("FOO", "*Foo"));
        Assert.assertFalse(FilenameUtils.wildcardMatch("BARFOO", "*Foo"));
        Assert.assertFalse(FilenameUtils.wildcardMatch("FOO", "Foo*"));
        Assert.assertFalse(FilenameUtils.wildcardMatch("FOOBAR", "Foo*"));
    }

    @Test
    public void testMatchOnSystem() {
        Assert.assertFalse(FilenameUtils.wildcardMatchOnSystem(null, "Foo"));
        Assert.assertFalse(FilenameUtils.wildcardMatchOnSystem("Foo", null));
        Assert.assertTrue(FilenameUtils.wildcardMatchOnSystem(null, null));
        Assert.assertTrue(FilenameUtils.wildcardMatchOnSystem("Foo", "Foo"));
        Assert.assertTrue(FilenameUtils.wildcardMatchOnSystem("", ""));
        Assert.assertTrue(FilenameUtils.wildcardMatchOnSystem("Foo", "Fo*"));
        Assert.assertTrue(FilenameUtils.wildcardMatchOnSystem("Foo", "Fo?"));
        Assert.assertTrue(FilenameUtils.wildcardMatchOnSystem("Foo Bar and Catflap", "Fo*"));
        Assert.assertTrue(FilenameUtils.wildcardMatchOnSystem("New Bookmarks", "N?w ?o?k??r?s"));
        Assert.assertFalse(FilenameUtils.wildcardMatchOnSystem("Foo", "Bar"));
        Assert.assertTrue(FilenameUtils.wildcardMatchOnSystem("Foo Bar Foo", "F*o Bar*"));
        Assert.assertTrue(FilenameUtils.wildcardMatchOnSystem("Adobe Acrobat Installer", "Ad*er"));
        Assert.assertTrue(FilenameUtils.wildcardMatchOnSystem("Foo", "*Foo"));
        Assert.assertTrue(FilenameUtils.wildcardMatchOnSystem("BarFoo", "*Foo"));
        Assert.assertTrue(FilenameUtils.wildcardMatchOnSystem("Foo", "Foo*"));
        Assert.assertTrue(FilenameUtils.wildcardMatchOnSystem("FooBar", "Foo*"));
        Assert.assertEquals(FilenameUtilsWildcardTestCase.WINDOWS, FilenameUtils.wildcardMatchOnSystem("FOO", "*Foo"));
        Assert.assertEquals(FilenameUtilsWildcardTestCase.WINDOWS, FilenameUtils.wildcardMatchOnSystem("BARFOO", "*Foo"));
        Assert.assertEquals(FilenameUtilsWildcardTestCase.WINDOWS, FilenameUtils.wildcardMatchOnSystem("FOO", "Foo*"));
        Assert.assertEquals(FilenameUtilsWildcardTestCase.WINDOWS, FilenameUtils.wildcardMatchOnSystem("FOOBAR", "Foo*"));
    }

    @Test
    public void testMatchCaseSpecified() {
        Assert.assertFalse(FilenameUtils.wildcardMatch(null, "Foo", IOCase.SENSITIVE));
        Assert.assertFalse(FilenameUtils.wildcardMatch("Foo", null, IOCase.SENSITIVE));
        Assert.assertTrue(FilenameUtils.wildcardMatch(null, null, IOCase.SENSITIVE));
        Assert.assertTrue(FilenameUtils.wildcardMatch("Foo", "Foo", IOCase.SENSITIVE));
        Assert.assertTrue(FilenameUtils.wildcardMatch("", "", IOCase.SENSITIVE));
        Assert.assertTrue(FilenameUtils.wildcardMatch("Foo", "Fo*", IOCase.SENSITIVE));
        Assert.assertTrue(FilenameUtils.wildcardMatch("Foo", "Fo?", IOCase.SENSITIVE));
        Assert.assertTrue(FilenameUtils.wildcardMatch("Foo Bar and Catflap", "Fo*", IOCase.SENSITIVE));
        Assert.assertTrue(FilenameUtils.wildcardMatch("New Bookmarks", "N?w ?o?k??r?s", IOCase.SENSITIVE));
        Assert.assertFalse(FilenameUtils.wildcardMatch("Foo", "Bar", IOCase.SENSITIVE));
        Assert.assertTrue(FilenameUtils.wildcardMatch("Foo Bar Foo", "F*o Bar*", IOCase.SENSITIVE));
        Assert.assertTrue(FilenameUtils.wildcardMatch("Adobe Acrobat Installer", "Ad*er", IOCase.SENSITIVE));
        Assert.assertTrue(FilenameUtils.wildcardMatch("Foo", "*Foo", IOCase.SENSITIVE));
        Assert.assertTrue(FilenameUtils.wildcardMatch("Foo", "Foo*", IOCase.SENSITIVE));
        Assert.assertTrue(FilenameUtils.wildcardMatch("Foo", "*Foo", IOCase.SENSITIVE));
        Assert.assertTrue(FilenameUtils.wildcardMatch("BarFoo", "*Foo", IOCase.SENSITIVE));
        Assert.assertTrue(FilenameUtils.wildcardMatch("Foo", "Foo*", IOCase.SENSITIVE));
        Assert.assertTrue(FilenameUtils.wildcardMatch("FooBar", "Foo*", IOCase.SENSITIVE));
        Assert.assertFalse(FilenameUtils.wildcardMatch("FOO", "*Foo", IOCase.SENSITIVE));
        Assert.assertFalse(FilenameUtils.wildcardMatch("BARFOO", "*Foo", IOCase.SENSITIVE));
        Assert.assertFalse(FilenameUtils.wildcardMatch("FOO", "Foo*", IOCase.SENSITIVE));
        Assert.assertFalse(FilenameUtils.wildcardMatch("FOOBAR", "Foo*", IOCase.SENSITIVE));
        Assert.assertTrue(FilenameUtils.wildcardMatch("FOO", "*Foo", IOCase.INSENSITIVE));
        Assert.assertTrue(FilenameUtils.wildcardMatch("BARFOO", "*Foo", IOCase.INSENSITIVE));
        Assert.assertTrue(FilenameUtils.wildcardMatch("FOO", "Foo*", IOCase.INSENSITIVE));
        Assert.assertTrue(FilenameUtils.wildcardMatch("FOOBAR", "Foo*", IOCase.INSENSITIVE));
        Assert.assertEquals(FilenameUtilsWildcardTestCase.WINDOWS, FilenameUtils.wildcardMatch("FOO", "*Foo", IOCase.SYSTEM));
        Assert.assertEquals(FilenameUtilsWildcardTestCase.WINDOWS, FilenameUtils.wildcardMatch("BARFOO", "*Foo", IOCase.SYSTEM));
        Assert.assertEquals(FilenameUtilsWildcardTestCase.WINDOWS, FilenameUtils.wildcardMatch("FOO", "Foo*", IOCase.SYSTEM));
        Assert.assertEquals(FilenameUtilsWildcardTestCase.WINDOWS, FilenameUtils.wildcardMatch("FOOBAR", "Foo*", IOCase.SYSTEM));
    }

    @Test
    public void testSplitOnTokens() {
        Assert.assertArrayEquals(new String[]{ "Ad" , "*" , "er" }, FilenameUtils.splitOnTokens("Ad*er"));
        Assert.assertArrayEquals(new String[]{ "Ad" , "?" , "er" }, FilenameUtils.splitOnTokens("Ad?er"));
        Assert.assertArrayEquals(new String[]{ "Test" , "*" , "?" , "One" }, FilenameUtils.splitOnTokens("Test*?One"));
        Assert.assertArrayEquals(new String[]{ "Test" , "?" , "*" , "One" }, FilenameUtils.splitOnTokens("Test?*One"));
        Assert.assertArrayEquals(new String[]{ "*" }, FilenameUtils.splitOnTokens("****"));
        Assert.assertArrayEquals(new String[]{ "*" , "?" , "?" , "*" }, FilenameUtils.splitOnTokens("*??*"));
        Assert.assertArrayEquals(new String[]{ "*" , "?" , "*" , "?" , "*" }, FilenameUtils.splitOnTokens("*?**?*"));
        Assert.assertArrayEquals(new String[]{ "*" , "?" , "*" , "?" , "*" }, FilenameUtils.splitOnTokens("*?***?*"));
        Assert.assertArrayEquals(new String[]{ "h" , "?" , "?" , "*" }, FilenameUtils.splitOnTokens("h??*"));
        Assert.assertArrayEquals(new String[]{ "" }, FilenameUtils.splitOnTokens(""));
    }

    private void assertMatch(final String text, final String wildcard, final boolean expected) {
        Assert.assertEquals(((text + " ") + wildcard), expected, FilenameUtils.wildcardMatch(text, wildcard));
    }

    // A separate set of tests, added to this batch
    @Test
    public void testMatch2() {
        assertMatch("log.txt", "log.txt", true);
        assertMatch("log.txt1", "log.txt", false);
        assertMatch("log.txt", "log.txt*", true);
        assertMatch("log.txt", "log.txt*1", false);
        assertMatch("log.txt", "*log.txt*", true);
        assertMatch("log.txt", "*.txt", true);
        assertMatch("txt.log", "*.txt", false);
        assertMatch("config.ini", "*.ini", true);
        assertMatch("config.txt.bak", "con*.txt", false);
        assertMatch("log.txt9", "*.txt?", true);
        assertMatch("log.txt", "*.txt?", false);
        assertMatch("progtestcase.java~5~", "*test*.java~*~", true);
        assertMatch("progtestcase.java;5~", "*test*.java~*~", false);
        assertMatch("progtestcase.java~5", "*test*.java~*~", false);
        assertMatch("log.txt", "log.*", true);
        assertMatch("log.txt", "log?*", true);
        assertMatch("log.txt12", "log.txt??", true);
        assertMatch("log.log", "log**log", true);
        assertMatch("log.log", "log**", true);
        assertMatch("log.log", "log.**", true);
        assertMatch("log.log", "**.log", true);
        assertMatch("log.log", "**log", true);
        assertMatch("log.log", "log*log", true);
        assertMatch("log.log", "log*", true);
        assertMatch("log.log", "log.*", true);
        assertMatch("log.log", "*.log", true);
        assertMatch("log.log", "*log", true);
        assertMatch("log.log", "*log?", false);
        assertMatch("log.log", "*log?*", true);
        assertMatch("log.log.abc", "*log?abc", true);
        assertMatch("log.log.abc.log.abc", "*log?abc", true);
        assertMatch("log.log.abc.log.abc.d", "*log?abc?d", true);
    }

    /**
     * See https://issues.apache.org/jira/browse/IO-246
     */
    @Test
    public void test_IO_246() {
        // Tests for "*?"
        assertMatch("aaa", "aa*?", true);
        // these ought to work as well, but "*?" does not work properly at present
        // assertMatch("aaa", "a*?", true);
        // assertMatch("aaa", "*?", true);
        // Tests for "?*"
        assertMatch("", "?*", false);
        assertMatch("a", "a?*", false);
        assertMatch("aa", "aa?*", false);
        assertMatch("a", "?*", true);
        assertMatch("aa", "?*", true);
        assertMatch("aaa", "?*", true);
        // Test ending on "?"
        assertMatch("", "?", false);
        assertMatch("a", "a?", false);
        assertMatch("aa", "aa?", false);
        assertMatch("aab", "aa?", true);
        assertMatch("aaa", "*a", true);
    }

    @Test
    public void testLocaleIndependence() {
        final Locale orig = Locale.getDefault();
        final Locale[] locales = Locale.getAvailableLocales();
        final String[][] data = new String[][]{ new String[]{ "I" , "i" } , new String[]{ "i" , "I" } , new String[]{ "i" , "\u0130" } , new String[]{ "i" , "\u0131" } , new String[]{ "\u03a3" , "\u03c2" } , new String[]{ "\u03a3" , "\u03c3" } , new String[]{ "\u03c2" , "\u03c3" } };
        try {
            for (int i = 0; i < (data.length); i++) {
                for (final Locale locale : locales) {
                    Locale.setDefault(locale);
                    Assert.assertTrue(("Test data corrupt: " + i), data[i][0].equalsIgnoreCase(data[i][1]));
                    final boolean match = FilenameUtils.wildcardMatch(data[i][0], data[i][1], IOCase.INSENSITIVE);
                    Assert.assertTrue((((Locale.getDefault().toString()) + ": ") + i), match);
                }
            }
        } finally {
            Locale.setDefault(orig);
        }
    }
}

