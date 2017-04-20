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
        // assert false boolean{FilenameUtils.wildcardMatch(null, "Foo")} to void{Assert}
        Assert.assertFalse(FilenameUtils.wildcardMatch(null, "Foo"));
        // assert false boolean{FilenameUtils.wildcardMatch("Foo", null)} to void{Assert}
        Assert.assertFalse(FilenameUtils.wildcardMatch("Foo", null));
        // assert true boolean{FilenameUtils.wildcardMatch(null, null)} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatch(null, null));
        // assert true boolean{FilenameUtils.wildcardMatch("Foo", "Foo")} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatch("Foo", "Foo"));
        // assert true boolean{FilenameUtils.wildcardMatch("", "")} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatch("", ""));
        // assert true boolean{FilenameUtils.wildcardMatch("", "*")} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatch("", "*"));
        // assert false boolean{FilenameUtils.wildcardMatch("", "?")} to void{Assert}
        Assert.assertFalse(FilenameUtils.wildcardMatch("", "?"));
        // assert true boolean{FilenameUtils.wildcardMatch("Foo", "Fo*")} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatch("Foo", "Fo*"));
        // assert true boolean{FilenameUtils.wildcardMatch("Foo", "Fo?")} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatch("Foo", "Fo?"));
        // assert true boolean{FilenameUtils.wildcardMatch("Foo Bar and Catflap", "Fo*")} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatch("Foo Bar and Catflap", "Fo*"));
        // assert true boolean{FilenameUtils.wildcardMatch("New Bookmarks", "N?w ?o?k??r?s")} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatch("New Bookmarks", "N?w ?o?k??r?s"));
        // assert false boolean{FilenameUtils.wildcardMatch("Foo", "Bar")} to void{Assert}
        Assert.assertFalse(FilenameUtils.wildcardMatch("Foo", "Bar"));
        // assert true boolean{FilenameUtils.wildcardMatch("Foo Bar Foo", "F*o Bar*")} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatch("Foo Bar Foo", "F*o Bar*"));
        // assert true boolean{FilenameUtils.wildcardMatch("Adobe Acrobat Installer", "Ad*er")} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatch("Adobe Acrobat Installer", "Ad*er"));
        // assert true boolean{FilenameUtils.wildcardMatch("Foo", "*Foo")} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatch("Foo", "*Foo"));
        // assert true boolean{FilenameUtils.wildcardMatch("BarFoo", "*Foo")} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatch("BarFoo", "*Foo"));
        // assert true boolean{FilenameUtils.wildcardMatch("Foo", "Foo*")} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatch("Foo", "Foo*"));
        // assert true boolean{FilenameUtils.wildcardMatch("FooBar", "Foo*")} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatch("FooBar", "Foo*"));
        // assert false boolean{FilenameUtils.wildcardMatch("FOO", "*Foo")} to void{Assert}
        Assert.assertFalse(FilenameUtils.wildcardMatch("FOO", "*Foo"));
        // assert false boolean{FilenameUtils.wildcardMatch("BARFOO", "*Foo")} to void{Assert}
        Assert.assertFalse(FilenameUtils.wildcardMatch("BARFOO", "*Foo"));
        // assert false boolean{FilenameUtils.wildcardMatch("FOO", "Foo*")} to void{Assert}
        Assert.assertFalse(FilenameUtils.wildcardMatch("FOO", "Foo*"));
        // assert false boolean{FilenameUtils.wildcardMatch("FOOBAR", "Foo*")} to void{Assert}
        Assert.assertFalse(FilenameUtils.wildcardMatch("FOOBAR", "Foo*"));
    }

    @Test
    public void testMatchOnSystem() {
        // assert false boolean{FilenameUtils.wildcardMatchOnSystem(null, "Foo")} to void{Assert}
        Assert.assertFalse(FilenameUtils.wildcardMatchOnSystem(null, "Foo"));
        // assert false boolean{FilenameUtils.wildcardMatchOnSystem("Foo", null)} to void{Assert}
        Assert.assertFalse(FilenameUtils.wildcardMatchOnSystem("Foo", null));
        // assert true boolean{FilenameUtils.wildcardMatchOnSystem(null, null)} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatchOnSystem(null, null));
        // assert true boolean{FilenameUtils.wildcardMatchOnSystem("Foo", "Foo")} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatchOnSystem("Foo", "Foo"));
        // assert true boolean{FilenameUtils.wildcardMatchOnSystem("", "")} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatchOnSystem("", ""));
        // assert true boolean{FilenameUtils.wildcardMatchOnSystem("Foo", "Fo*")} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatchOnSystem("Foo", "Fo*"));
        // assert true boolean{FilenameUtils.wildcardMatchOnSystem("Foo", "Fo?")} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatchOnSystem("Foo", "Fo?"));
        // assert true boolean{FilenameUtils.wildcardMatchOnSystem("Foo Bar and Catflap", "Fo*")} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatchOnSystem("Foo Bar and Catflap", "Fo*"));
        // assert true boolean{FilenameUtils.wildcardMatchOnSystem("New Bookmarks", "N?w ?o?k??r?s")} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatchOnSystem("New Bookmarks", "N?w ?o?k??r?s"));
        // assert false boolean{FilenameUtils.wildcardMatchOnSystem("Foo", "Bar")} to void{Assert}
        Assert.assertFalse(FilenameUtils.wildcardMatchOnSystem("Foo", "Bar"));
        // assert true boolean{FilenameUtils.wildcardMatchOnSystem("Foo Bar Foo", "F*o Bar*")} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatchOnSystem("Foo Bar Foo", "F*o Bar*"));
        // assert true boolean{FilenameUtils.wildcardMatchOnSystem("Adobe Acrobat Installer", "Ad*er")} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatchOnSystem("Adobe Acrobat Installer", "Ad*er"));
        // assert true boolean{FilenameUtils.wildcardMatchOnSystem("Foo", "*Foo")} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatchOnSystem("Foo", "*Foo"));
        // assert true boolean{FilenameUtils.wildcardMatchOnSystem("BarFoo", "*Foo")} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatchOnSystem("BarFoo", "*Foo"));
        // assert true boolean{FilenameUtils.wildcardMatchOnSystem("Foo", "Foo*")} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatchOnSystem("Foo", "Foo*"));
        // assert true boolean{FilenameUtils.wildcardMatchOnSystem("FooBar", "Foo*")} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatchOnSystem("FooBar", "Foo*"));
        // assert equals boolean{FilenameUtilsWildcardTestCase.WINDOWS} to void{Assert}
        Assert.assertEquals(FilenameUtilsWildcardTestCase.WINDOWS, FilenameUtils.wildcardMatchOnSystem("FOO", "*Foo"));
        // assert equals boolean{FilenameUtilsWildcardTestCase.WINDOWS} to void{Assert}
        Assert.assertEquals(FilenameUtilsWildcardTestCase.WINDOWS, FilenameUtils.wildcardMatchOnSystem("BARFOO", "*Foo"));
        // assert equals boolean{FilenameUtilsWildcardTestCase.WINDOWS} to void{Assert}
        Assert.assertEquals(FilenameUtilsWildcardTestCase.WINDOWS, FilenameUtils.wildcardMatchOnSystem("FOO", "Foo*"));
        // assert equals boolean{FilenameUtilsWildcardTestCase.WINDOWS} to void{Assert}
        Assert.assertEquals(FilenameUtilsWildcardTestCase.WINDOWS, FilenameUtils.wildcardMatchOnSystem("FOOBAR", "Foo*"));
    }

    @Test
    public void testMatchCaseSpecified() {
        // assert false boolean{FilenameUtils.wildcardMatch(null, "Foo", IOCase.SENSITIVE)} to void{Assert}
        Assert.assertFalse(FilenameUtils.wildcardMatch(null, "Foo", IOCase.SENSITIVE));
        // assert false boolean{FilenameUtils.wildcardMatch("Foo", null, IOCase.SENSITIVE)} to void{Assert}
        Assert.assertFalse(FilenameUtils.wildcardMatch("Foo", null, IOCase.SENSITIVE));
        // assert true boolean{FilenameUtils.wildcardMatch(null, null, IOCase.SENSITIVE)} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatch(null, null, IOCase.SENSITIVE));
        // assert true boolean{FilenameUtils.wildcardMatch("Foo", "Foo", IOCase.SENSITIVE)} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatch("Foo", "Foo", IOCase.SENSITIVE));
        // assert true boolean{FilenameUtils.wildcardMatch("", "", IOCase.SENSITIVE)} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatch("", "", IOCase.SENSITIVE));
        // assert true boolean{FilenameUtils.wildcardMatch("Foo", "Fo*", IOCase.SENSITIVE)} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatch("Foo", "Fo*", IOCase.SENSITIVE));
        // assert true boolean{FilenameUtils.wildcardMatch("Foo", "Fo?", IOCase.SENSITIVE)} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatch("Foo", "Fo?", IOCase.SENSITIVE));
        // assert true boolean{FilenameUtils.wildcardMatch("Foo Bar and Catflap", "Fo*", IOCase.SENSITIVE)} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatch("Foo Bar and Catflap", "Fo*", IOCase.SENSITIVE));
        // assert true boolean{FilenameUtils.wildcardMatch("New Bookmarks", "N?w ?o?k??r?s", IOCase.SENSITIVE)} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatch("New Bookmarks", "N?w ?o?k??r?s", IOCase.SENSITIVE));
        // assert false boolean{FilenameUtils.wildcardMatch("Foo", "Bar", IOCase.SENSITIVE)} to void{Assert}
        Assert.assertFalse(FilenameUtils.wildcardMatch("Foo", "Bar", IOCase.SENSITIVE));
        // assert true boolean{FilenameUtils.wildcardMatch("Foo Bar Foo", "F*o Bar*", IOCase.SENSITIVE)} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatch("Foo Bar Foo", "F*o Bar*", IOCase.SENSITIVE));
        // assert true boolean{FilenameUtils.wildcardMatch("Adobe Acrobat Installer", "Ad*er", IOCase.SENSITIVE)} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatch("Adobe Acrobat Installer", "Ad*er", IOCase.SENSITIVE));
        // assert true boolean{FilenameUtils.wildcardMatch("Foo", "*Foo", IOCase.SENSITIVE)} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatch("Foo", "*Foo", IOCase.SENSITIVE));
        // assert true boolean{FilenameUtils.wildcardMatch("Foo", "Foo*", IOCase.SENSITIVE)} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatch("Foo", "Foo*", IOCase.SENSITIVE));
        // assert true boolean{FilenameUtils.wildcardMatch("Foo", "*Foo", IOCase.SENSITIVE)} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatch("Foo", "*Foo", IOCase.SENSITIVE));
        // assert true boolean{FilenameUtils.wildcardMatch("BarFoo", "*Foo", IOCase.SENSITIVE)} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatch("BarFoo", "*Foo", IOCase.SENSITIVE));
        // assert true boolean{FilenameUtils.wildcardMatch("Foo", "Foo*", IOCase.SENSITIVE)} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatch("Foo", "Foo*", IOCase.SENSITIVE));
        // assert true boolean{FilenameUtils.wildcardMatch("FooBar", "Foo*", IOCase.SENSITIVE)} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatch("FooBar", "Foo*", IOCase.SENSITIVE));
        // assert false boolean{FilenameUtils.wildcardMatch("FOO", "*Foo", IOCase.SENSITIVE)} to void{Assert}
        Assert.assertFalse(FilenameUtils.wildcardMatch("FOO", "*Foo", IOCase.SENSITIVE));
        // assert false boolean{FilenameUtils.wildcardMatch("BARFOO", "*Foo", IOCase.SENSITIVE)} to void{Assert}
        Assert.assertFalse(FilenameUtils.wildcardMatch("BARFOO", "*Foo", IOCase.SENSITIVE));
        // assert false boolean{FilenameUtils.wildcardMatch("FOO", "Foo*", IOCase.SENSITIVE)} to void{Assert}
        Assert.assertFalse(FilenameUtils.wildcardMatch("FOO", "Foo*", IOCase.SENSITIVE));
        // assert false boolean{FilenameUtils.wildcardMatch("FOOBAR", "Foo*", IOCase.SENSITIVE)} to void{Assert}
        Assert.assertFalse(FilenameUtils.wildcardMatch("FOOBAR", "Foo*", IOCase.SENSITIVE));
        // assert true boolean{FilenameUtils.wildcardMatch("FOO", "*Foo", IOCase.INSENSITIVE)} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatch("FOO", "*Foo", IOCase.INSENSITIVE));
        // assert true boolean{FilenameUtils.wildcardMatch("BARFOO", "*Foo", IOCase.INSENSITIVE)} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatch("BARFOO", "*Foo", IOCase.INSENSITIVE));
        // assert true boolean{FilenameUtils.wildcardMatch("FOO", "Foo*", IOCase.INSENSITIVE)} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatch("FOO", "Foo*", IOCase.INSENSITIVE));
        // assert true boolean{FilenameUtils.wildcardMatch("FOOBAR", "Foo*", IOCase.INSENSITIVE)} to void{Assert}
        Assert.assertTrue(FilenameUtils.wildcardMatch("FOOBAR", "Foo*", IOCase.INSENSITIVE));
        // assert equals boolean{FilenameUtilsWildcardTestCase.WINDOWS} to void{Assert}
        Assert.assertEquals(FilenameUtilsWildcardTestCase.WINDOWS, FilenameUtils.wildcardMatch("FOO", "*Foo", IOCase.SYSTEM));
        // assert equals boolean{FilenameUtilsWildcardTestCase.WINDOWS} to void{Assert}
        Assert.assertEquals(FilenameUtilsWildcardTestCase.WINDOWS, FilenameUtils.wildcardMatch("BARFOO", "*Foo", IOCase.SYSTEM));
        // assert equals boolean{FilenameUtilsWildcardTestCase.WINDOWS} to void{Assert}
        Assert.assertEquals(FilenameUtilsWildcardTestCase.WINDOWS, FilenameUtils.wildcardMatch("FOO", "Foo*", IOCase.SYSTEM));
        // assert equals boolean{FilenameUtilsWildcardTestCase.WINDOWS} to void{Assert}
        Assert.assertEquals(FilenameUtilsWildcardTestCase.WINDOWS, FilenameUtils.wildcardMatch("FOOBAR", "Foo*", IOCase.SYSTEM));
    }

    @Test
    public void testSplitOnTokens() {
        // assert array String[]{new String[]{ "Ad" , "*" , "er" }} to void{Assert}
        Assert.assertArrayEquals(new String[]{ "Ad" , "*" , "er" }, FilenameUtils.splitOnTokens("Ad*er"));
        // assert array String[]{new String[]{ "Ad" , "?" , "er" }} to void{Assert}
        Assert.assertArrayEquals(new String[]{ "Ad" , "?" , "er" }, FilenameUtils.splitOnTokens("Ad?er"));
        // assert array String[]{new String[]{ "Test" , "*" , "?" , "One" }} to void{Assert}
        Assert.assertArrayEquals(new String[]{ "Test" , "*" , "?" , "One" }, FilenameUtils.splitOnTokens("Test*?One"));
        // assert array String[]{new String[]{ "Test" , "?" , "*" , "One" }} to void{Assert}
        Assert.assertArrayEquals(new String[]{ "Test" , "?" , "*" , "One" }, FilenameUtils.splitOnTokens("Test?*One"));
        // assert array String[]{new String[]{ "*" }} to void{Assert}
        Assert.assertArrayEquals(new String[]{ "*" }, FilenameUtils.splitOnTokens("****"));
        // assert array String[]{new String[]{ "*" , "?" , "?" , "*" }} to void{Assert}
        Assert.assertArrayEquals(new String[]{ "*" , "?" , "?" , "*" }, FilenameUtils.splitOnTokens("*??*"));
        // assert array String[]{new String[]{ "*" , "?" , "*" , "?" , "*" }} to void{Assert}
        Assert.assertArrayEquals(new String[]{ "*" , "?" , "*" , "?" , "*" }, FilenameUtils.splitOnTokens("*?**?*"));
        // assert array String[]{new String[]{ "*" , "?" , "*" , "?" , "*" }} to void{Assert}
        Assert.assertArrayEquals(new String[]{ "*" , "?" , "*" , "?" , "*" }, FilenameUtils.splitOnTokens("*?***?*"));
        // assert array String[]{new String[]{ "h" , "?" , "?" , "*" }} to void{Assert}
        Assert.assertArrayEquals(new String[]{ "h" , "?" , "?" , "*" }, FilenameUtils.splitOnTokens("h??*"));
        // assert array String[]{new String[]{ "" }} to void{Assert}
        Assert.assertArrayEquals(new String[]{ "" }, FilenameUtils.splitOnTokens(""));
    }

    private void assertMatch(final String text, final String wildcard, final boolean expected) {
        // assert equals String{((text + " ") + wildcard)} to void{Assert}
        Assert.assertEquals(((text + " ") + wildcard), expected, FilenameUtils.wildcardMatch(text, wildcard));
    }

    // A separate set of tests, added to this batch
    @Test
    public void testMatch2() {
        // assert match String{"log.txt"} to FilenameUtilsWildcardTestCase{}
        assertMatch("log.txt", "log.txt", true);
        // assert match String{"log.txt1"} to FilenameUtilsWildcardTestCase{}
        assertMatch("log.txt1", "log.txt", false);
        // assert match String{"log.txt"} to FilenameUtilsWildcardTestCase{}
        assertMatch("log.txt", "log.txt*", true);
        // assert match String{"log.txt"} to FilenameUtilsWildcardTestCase{}
        assertMatch("log.txt", "log.txt*1", false);
        // assert match String{"log.txt"} to FilenameUtilsWildcardTestCase{}
        assertMatch("log.txt", "*log.txt*", true);
        // assert match String{"log.txt"} to FilenameUtilsWildcardTestCase{}
        assertMatch("log.txt", "*.txt", true);
        // assert match String{"txt.log"} to FilenameUtilsWildcardTestCase{}
        assertMatch("txt.log", "*.txt", false);
        // assert match String{"config.ini"} to FilenameUtilsWildcardTestCase{}
        assertMatch("config.ini", "*.ini", true);
        // assert match String{"config.txt.bak"} to FilenameUtilsWildcardTestCase{}
        assertMatch("config.txt.bak", "con*.txt", false);
        // assert match String{"log.txt9"} to FilenameUtilsWildcardTestCase{}
        assertMatch("log.txt9", "*.txt?", true);
        // assert match String{"log.txt"} to FilenameUtilsWildcardTestCase{}
        assertMatch("log.txt", "*.txt?", false);
        // assert match String{"progtestcase.java~5~"} to FilenameUtilsWildcardTestCase{}
        assertMatch("progtestcase.java~5~", "*test*.java~*~", true);
        // assert match String{"progtestcase.java;5~"} to FilenameUtilsWildcardTestCase{}
        assertMatch("progtestcase.java;5~", "*test*.java~*~", false);
        // assert match String{"progtestcase.java~5"} to FilenameUtilsWildcardTestCase{}
        assertMatch("progtestcase.java~5", "*test*.java~*~", false);
        // assert match String{"log.txt"} to FilenameUtilsWildcardTestCase{}
        assertMatch("log.txt", "log.*", true);
        // assert match String{"log.txt"} to FilenameUtilsWildcardTestCase{}
        assertMatch("log.txt", "log?*", true);
        // assert match String{"log.txt12"} to FilenameUtilsWildcardTestCase{}
        assertMatch("log.txt12", "log.txt??", true);
        // assert match String{"log.log"} to FilenameUtilsWildcardTestCase{}
        assertMatch("log.log", "log**log", true);
        // assert match String{"log.log"} to FilenameUtilsWildcardTestCase{}
        assertMatch("log.log", "log**", true);
        // assert match String{"log.log"} to FilenameUtilsWildcardTestCase{}
        assertMatch("log.log", "log.**", true);
        // assert match String{"log.log"} to FilenameUtilsWildcardTestCase{}
        assertMatch("log.log", "**.log", true);
        // assert match String{"log.log"} to FilenameUtilsWildcardTestCase{}
        assertMatch("log.log", "**log", true);
        // assert match String{"log.log"} to FilenameUtilsWildcardTestCase{}
        assertMatch("log.log", "log*log", true);
        // assert match String{"log.log"} to FilenameUtilsWildcardTestCase{}
        assertMatch("log.log", "log*", true);
        // assert match String{"log.log"} to FilenameUtilsWildcardTestCase{}
        assertMatch("log.log", "log.*", true);
        // assert match String{"log.log"} to FilenameUtilsWildcardTestCase{}
        assertMatch("log.log", "*.log", true);
        // assert match String{"log.log"} to FilenameUtilsWildcardTestCase{}
        assertMatch("log.log", "*log", true);
        // assert match String{"log.log"} to FilenameUtilsWildcardTestCase{}
        assertMatch("log.log", "*log?", false);
        // assert match String{"log.log"} to FilenameUtilsWildcardTestCase{}
        assertMatch("log.log", "*log?*", true);
        // assert match String{"log.log.abc"} to FilenameUtilsWildcardTestCase{}
        assertMatch("log.log.abc", "*log?abc", true);
        // assert match String{"log.log.abc.log.abc"} to FilenameUtilsWildcardTestCase{}
        assertMatch("log.log.abc.log.abc", "*log?abc", true);
        // assert match String{"log.log.abc.log.abc.d"} to FilenameUtilsWildcardTestCase{}
        assertMatch("log.log.abc.log.abc.d", "*log?abc?d", true);
    }

    /**
     * See https://issues.apache.org/jira/browse/IO-246
     */
    @Test
    public void test_IO_246() {
        // Tests for "*?"
        // assert match String{"aaa"} to FilenameUtilsWildcardTestCase{}
        assertMatch("aaa", "aa*?", true);
        // these ought to work as well, but "*?" does not work properly at present
        // assertMatch("aaa", "a*?", true);
        // assertMatch("aaa", "*?", true);
        // Tests for "?*"
        // assert match String{""} to FilenameUtilsWildcardTestCase{}
        assertMatch("", "?*", false);
        // assert match String{"a"} to FilenameUtilsWildcardTestCase{}
        assertMatch("a", "a?*", false);
        // assert match String{"aa"} to FilenameUtilsWildcardTestCase{}
        assertMatch("aa", "aa?*", false);
        // assert match String{"a"} to FilenameUtilsWildcardTestCase{}
        assertMatch("a", "?*", true);
        // assert match String{"aa"} to FilenameUtilsWildcardTestCase{}
        assertMatch("aa", "?*", true);
        // assert match String{"aaa"} to FilenameUtilsWildcardTestCase{}
        assertMatch("aaa", "?*", true);
        // Test ending on "?"
        // assert match String{""} to FilenameUtilsWildcardTestCase{}
        assertMatch("", "?", false);
        // assert match String{"a"} to FilenameUtilsWildcardTestCase{}
        assertMatch("a", "a?", false);
        // assert match String{"aa"} to FilenameUtilsWildcardTestCase{}
        assertMatch("aa", "aa?", false);
        // assert match String{"aab"} to FilenameUtilsWildcardTestCase{}
        assertMatch("aab", "aa?", true);
        // assert match String{"aaa"} to FilenameUtilsWildcardTestCase{}
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

