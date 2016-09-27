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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import org.apache.commons.io.testtools.FileBasedTestCase;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.junit.Test;

/**
 * This is used to test IOCase for correctness.
 * 
 * @version $Id: IOCaseTestCase.java 1718944 2015-12-09 19:50:30Z krosenvold $
 */
public class IOCaseTestCase extends FileBasedTestCase {
    private static final boolean WINDOWS = (File.separatorChar) == '\\';

    // -----------------------------------------------------------------------
    @Test
    public void test_forName() throws Exception {
        Assert.assertEquals(IOCase.SENSITIVE, IOCase.forName("Sensitive"));
        Assert.assertEquals(IOCase.INSENSITIVE, IOCase.forName("Insensitive"));
        Assert.assertEquals(IOCase.SYSTEM, IOCase.forName("System"));
        try {
            IOCase.forName("Blah");
            Assert.fail();
        } catch (final IllegalArgumentException ignore) {
        }
        try {
            IOCase.forName(null);
            Assert.fail();
        } catch (final IllegalArgumentException ignore) {
        }
    }

    @Test
    public void test_serialization() throws Exception {
        Assert.assertSame(IOCase.SENSITIVE, serialize(IOCase.SENSITIVE));
        Assert.assertSame(IOCase.INSENSITIVE, serialize(IOCase.INSENSITIVE));
        Assert.assertSame(IOCase.SYSTEM, serialize(IOCase.SYSTEM));
    }

    @Test
    public void test_getName() throws Exception {
        Assert.assertEquals("Sensitive", IOCase.SENSITIVE.getName());
        Assert.assertEquals("Insensitive", IOCase.INSENSITIVE.getName());
        Assert.assertEquals("System", IOCase.SYSTEM.getName());
    }

    @Test
    public void test_toString() throws Exception {
        Assert.assertEquals("Sensitive", IOCase.SENSITIVE.toString());
        Assert.assertEquals("Insensitive", IOCase.INSENSITIVE.toString());
        Assert.assertEquals("System", IOCase.SYSTEM.toString());
    }

    @Test
    public void test_isCaseSensitive() throws Exception {
        Assert.assertTrue(IOCase.SENSITIVE.isCaseSensitive());
        Assert.assertFalse(IOCase.INSENSITIVE.isCaseSensitive());
        Assert.assertEquals((!(IOCaseTestCase.WINDOWS)), IOCase.SYSTEM.isCaseSensitive());
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_checkCompare_functionality() throws Exception {
        Assert.assertTrue(((IOCase.SENSITIVE.checkCompareTo("ABC", "")) > 0));
        Assert.assertTrue(((IOCase.SENSITIVE.checkCompareTo("", "ABC")) < 0));
        Assert.assertTrue(((IOCase.SENSITIVE.checkCompareTo("ABC", "DEF")) < 0));
        Assert.assertTrue(((IOCase.SENSITIVE.checkCompareTo("DEF", "ABC")) > 0));
        Assert.assertEquals(0, IOCase.SENSITIVE.checkCompareTo("ABC", "ABC"));
        Assert.assertEquals(0, IOCase.SENSITIVE.checkCompareTo("", ""));
        try {
            IOCase.SENSITIVE.checkCompareTo("ABC", null);
            Assert.fail();
        } catch (final NullPointerException ignore) {
        }
        try {
            IOCase.SENSITIVE.checkCompareTo(null, "ABC");
            Assert.fail();
        } catch (final NullPointerException ignore) {
        }
        try {
            IOCase.SENSITIVE.checkCompareTo(null, null);
            Assert.fail();
        } catch (final NullPointerException ignore) {
        }
    }

    @Test
    public void test_checkCompare_case() throws Exception {
        Assert.assertEquals(0, IOCase.SENSITIVE.checkCompareTo("ABC", "ABC"));
        Assert.assertTrue(((IOCase.SENSITIVE.checkCompareTo("ABC", "abc")) < 0));
        Assert.assertTrue(((IOCase.SENSITIVE.checkCompareTo("abc", "ABC")) > 0));
        Assert.assertEquals(0, IOCase.INSENSITIVE.checkCompareTo("ABC", "ABC"));
        Assert.assertEquals(0, IOCase.INSENSITIVE.checkCompareTo("ABC", "abc"));
        Assert.assertEquals(0, IOCase.INSENSITIVE.checkCompareTo("abc", "ABC"));
        Assert.assertEquals(0, IOCase.SYSTEM.checkCompareTo("ABC", "ABC"));
        Assert.assertEquals(IOCaseTestCase.WINDOWS, ((IOCase.SYSTEM.checkCompareTo("ABC", "abc")) == 0));
        Assert.assertEquals(IOCaseTestCase.WINDOWS, ((IOCase.SYSTEM.checkCompareTo("abc", "ABC")) == 0));
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_checkEquals_functionality() throws Exception {
        Assert.assertFalse(IOCase.SENSITIVE.checkEquals("ABC", ""));
        Assert.assertFalse(IOCase.SENSITIVE.checkEquals("ABC", "A"));
        Assert.assertFalse(IOCase.SENSITIVE.checkEquals("ABC", "AB"));
        Assert.assertTrue(IOCase.SENSITIVE.checkEquals("ABC", "ABC"));
        Assert.assertFalse(IOCase.SENSITIVE.checkEquals("ABC", "BC"));
        Assert.assertFalse(IOCase.SENSITIVE.checkEquals("ABC", "C"));
        Assert.assertFalse(IOCase.SENSITIVE.checkEquals("ABC", "ABCD"));
        Assert.assertFalse(IOCase.SENSITIVE.checkEquals("", "ABC"));
        Assert.assertTrue(IOCase.SENSITIVE.checkEquals("", ""));
        try {
            IOCase.SENSITIVE.checkEquals("ABC", null);
            Assert.fail();
        } catch (final NullPointerException ignore) {
        }
        try {
            IOCase.SENSITIVE.checkEquals(null, "ABC");
            Assert.fail();
        } catch (final NullPointerException ignore) {
        }
        try {
            IOCase.SENSITIVE.checkEquals(null, null);
            Assert.fail();
        } catch (final NullPointerException ignore) {
        }
    }

    @Test
    public void test_checkEquals_case() throws Exception {
        Assert.assertTrue(IOCase.SENSITIVE.checkEquals("ABC", "ABC"));
        Assert.assertFalse(IOCase.SENSITIVE.checkEquals("ABC", "Abc"));
        Assert.assertTrue(IOCase.INSENSITIVE.checkEquals("ABC", "ABC"));
        Assert.assertTrue(IOCase.INSENSITIVE.checkEquals("ABC", "Abc"));
        Assert.assertTrue(IOCase.SYSTEM.checkEquals("ABC", "ABC"));
        Assert.assertEquals(IOCaseTestCase.WINDOWS, IOCase.SYSTEM.checkEquals("ABC", "Abc"));
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_checkStartsWith_functionality() throws Exception {
        Assert.assertTrue(IOCase.SENSITIVE.checkStartsWith("ABC", ""));
        Assert.assertTrue(IOCase.SENSITIVE.checkStartsWith("ABC", "A"));
        Assert.assertTrue(IOCase.SENSITIVE.checkStartsWith("ABC", "AB"));
        Assert.assertTrue(IOCase.SENSITIVE.checkStartsWith("ABC", "ABC"));
        Assert.assertFalse(IOCase.SENSITIVE.checkStartsWith("ABC", "BC"));
        Assert.assertFalse(IOCase.SENSITIVE.checkStartsWith("ABC", "C"));
        Assert.assertFalse(IOCase.SENSITIVE.checkStartsWith("ABC", "ABCD"));
        Assert.assertFalse(IOCase.SENSITIVE.checkStartsWith("", "ABC"));
        Assert.assertTrue(IOCase.SENSITIVE.checkStartsWith("", ""));
        try {
            IOCase.SENSITIVE.checkStartsWith("ABC", null);
            Assert.fail();
        } catch (final NullPointerException ignore) {
        }
        try {
            IOCase.SENSITIVE.checkStartsWith(null, "ABC");
            Assert.fail();
        } catch (final NullPointerException ignore) {
        }
        try {
            IOCase.SENSITIVE.checkStartsWith(null, null);
            Assert.fail();
        } catch (final NullPointerException ignore) {
        }
    }

    @Test
    public void test_checkStartsWith_case() throws Exception {
        Assert.assertTrue(IOCase.SENSITIVE.checkStartsWith("ABC", "AB"));
        Assert.assertFalse(IOCase.SENSITIVE.checkStartsWith("ABC", "Ab"));
        Assert.assertTrue(IOCase.INSENSITIVE.checkStartsWith("ABC", "AB"));
        Assert.assertTrue(IOCase.INSENSITIVE.checkStartsWith("ABC", "Ab"));
        Assert.assertTrue(IOCase.SYSTEM.checkStartsWith("ABC", "AB"));
        Assert.assertEquals(IOCaseTestCase.WINDOWS, IOCase.SYSTEM.checkStartsWith("ABC", "Ab"));
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_checkEndsWith_functionality() throws Exception {
        Assert.assertTrue(IOCase.SENSITIVE.checkEndsWith("ABC", ""));
        Assert.assertFalse(IOCase.SENSITIVE.checkEndsWith("ABC", "A"));
        Assert.assertFalse(IOCase.SENSITIVE.checkEndsWith("ABC", "AB"));
        Assert.assertTrue(IOCase.SENSITIVE.checkEndsWith("ABC", "ABC"));
        Assert.assertTrue(IOCase.SENSITIVE.checkEndsWith("ABC", "BC"));
        Assert.assertTrue(IOCase.SENSITIVE.checkEndsWith("ABC", "C"));
        Assert.assertFalse(IOCase.SENSITIVE.checkEndsWith("ABC", "ABCD"));
        Assert.assertFalse(IOCase.SENSITIVE.checkEndsWith("", "ABC"));
        Assert.assertTrue(IOCase.SENSITIVE.checkEndsWith("", ""));
        try {
            IOCase.SENSITIVE.checkEndsWith("ABC", null);
            Assert.fail();
        } catch (final NullPointerException ignore) {
        }
        try {
            IOCase.SENSITIVE.checkEndsWith(null, "ABC");
            Assert.fail();
        } catch (final NullPointerException ignore) {
        }
        try {
            IOCase.SENSITIVE.checkEndsWith(null, null);
            Assert.fail();
        } catch (final NullPointerException ignore) {
        }
    }

    @Test
    public void test_checkEndsWith_case() throws Exception {
        Assert.assertTrue(IOCase.SENSITIVE.checkEndsWith("ABC", "BC"));
        Assert.assertFalse(IOCase.SENSITIVE.checkEndsWith("ABC", "Bc"));
        Assert.assertTrue(IOCase.INSENSITIVE.checkEndsWith("ABC", "BC"));
        Assert.assertTrue(IOCase.INSENSITIVE.checkEndsWith("ABC", "Bc"));
        Assert.assertTrue(IOCase.SYSTEM.checkEndsWith("ABC", "BC"));
        Assert.assertEquals(IOCaseTestCase.WINDOWS, IOCase.SYSTEM.checkEndsWith("ABC", "Bc"));
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_checkIndexOf_functionality() throws Exception {
        // start
        Assert.assertEquals(0, IOCase.SENSITIVE.checkIndexOf("ABCDEFGHIJ", 0, "A"));
        Assert.assertEquals((-1), IOCase.SENSITIVE.checkIndexOf("ABCDEFGHIJ", 1, "A"));
        Assert.assertEquals(0, IOCase.SENSITIVE.checkIndexOf("ABCDEFGHIJ", 0, "AB"));
        Assert.assertEquals((-1), IOCase.SENSITIVE.checkIndexOf("ABCDEFGHIJ", 1, "AB"));
        Assert.assertEquals(0, IOCase.SENSITIVE.checkIndexOf("ABCDEFGHIJ", 0, "ABC"));
        Assert.assertEquals((-1), IOCase.SENSITIVE.checkIndexOf("ABCDEFGHIJ", 1, "ABC"));
        // middle
        Assert.assertEquals(3, IOCase.SENSITIVE.checkIndexOf("ABCDEFGHIJ", 0, "D"));
        Assert.assertEquals(3, IOCase.SENSITIVE.checkIndexOf("ABCDEFGHIJ", 3, "D"));
        Assert.assertEquals((-1), IOCase.SENSITIVE.checkIndexOf("ABCDEFGHIJ", 4, "D"));
        Assert.assertEquals(3, IOCase.SENSITIVE.checkIndexOf("ABCDEFGHIJ", 0, "DE"));
        Assert.assertEquals(3, IOCase.SENSITIVE.checkIndexOf("ABCDEFGHIJ", 3, "DE"));
        Assert.assertEquals((-1), IOCase.SENSITIVE.checkIndexOf("ABCDEFGHIJ", 4, "DE"));
        Assert.assertEquals(3, IOCase.SENSITIVE.checkIndexOf("ABCDEFGHIJ", 0, "DEF"));
        Assert.assertEquals(3, IOCase.SENSITIVE.checkIndexOf("ABCDEFGHIJ", 3, "DEF"));
        Assert.assertEquals((-1), IOCase.SENSITIVE.checkIndexOf("ABCDEFGHIJ", 4, "DEF"));
        // end
        Assert.assertEquals(9, IOCase.SENSITIVE.checkIndexOf("ABCDEFGHIJ", 0, "J"));
        Assert.assertEquals(9, IOCase.SENSITIVE.checkIndexOf("ABCDEFGHIJ", 8, "J"));
        Assert.assertEquals(9, IOCase.SENSITIVE.checkIndexOf("ABCDEFGHIJ", 9, "J"));
        Assert.assertEquals(8, IOCase.SENSITIVE.checkIndexOf("ABCDEFGHIJ", 0, "IJ"));
        Assert.assertEquals(8, IOCase.SENSITIVE.checkIndexOf("ABCDEFGHIJ", 8, "IJ"));
        Assert.assertEquals((-1), IOCase.SENSITIVE.checkIndexOf("ABCDEFGHIJ", 9, "IJ"));
        Assert.assertEquals(7, IOCase.SENSITIVE.checkIndexOf("ABCDEFGHIJ", 6, "HIJ"));
        Assert.assertEquals(7, IOCase.SENSITIVE.checkIndexOf("ABCDEFGHIJ", 7, "HIJ"));
        Assert.assertEquals((-1), IOCase.SENSITIVE.checkIndexOf("ABCDEFGHIJ", 8, "HIJ"));
        // not found
        Assert.assertEquals((-1), IOCase.SENSITIVE.checkIndexOf("ABCDEFGHIJ", 0, "DED"));
        // too long
        Assert.assertEquals((-1), IOCase.SENSITIVE.checkIndexOf("DEF", 0, "ABCDEFGHIJ"));
        try {
            IOCase.SENSITIVE.checkIndexOf("ABC", 0, null);
            Assert.fail();
        } catch (final NullPointerException ignore) {
        }
        try {
            IOCase.SENSITIVE.checkIndexOf(null, 0, "ABC");
            Assert.fail();
        } catch (final NullPointerException ignore) {
        }
        try {
            IOCase.SENSITIVE.checkIndexOf(null, 0, null);
            Assert.fail();
        } catch (final NullPointerException ignore) {
        }
    }

    @Test
    public void test_checkIndexOf_case() throws Exception {
        Assert.assertEquals(1, IOCase.SENSITIVE.checkIndexOf("ABC", 0, "BC"));
        Assert.assertEquals((-1), IOCase.SENSITIVE.checkIndexOf("ABC", 0, "Bc"));
        Assert.assertEquals(1, IOCase.INSENSITIVE.checkIndexOf("ABC", 0, "BC"));
        Assert.assertEquals(1, IOCase.INSENSITIVE.checkIndexOf("ABC", 0, "Bc"));
        Assert.assertEquals(1, IOCase.SYSTEM.checkIndexOf("ABC", 0, "BC"));
        Assert.assertEquals((IOCaseTestCase.WINDOWS ? 1 : -1), IOCase.SYSTEM.checkIndexOf("ABC", 0, "Bc"));
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_checkRegionMatches_functionality() throws Exception {
        Assert.assertTrue(IOCase.SENSITIVE.checkRegionMatches("ABC", 0, ""));
        Assert.assertTrue(IOCase.SENSITIVE.checkRegionMatches("ABC", 0, "A"));
        Assert.assertTrue(IOCase.SENSITIVE.checkRegionMatches("ABC", 0, "AB"));
        Assert.assertTrue(IOCase.SENSITIVE.checkRegionMatches("ABC", 0, "ABC"));
        Assert.assertFalse(IOCase.SENSITIVE.checkRegionMatches("ABC", 0, "BC"));
        Assert.assertFalse(IOCase.SENSITIVE.checkRegionMatches("ABC", 0, "C"));
        Assert.assertFalse(IOCase.SENSITIVE.checkRegionMatches("ABC", 0, "ABCD"));
        Assert.assertFalse(IOCase.SENSITIVE.checkRegionMatches("", 0, "ABC"));
        Assert.assertTrue(IOCase.SENSITIVE.checkRegionMatches("", 0, ""));
        Assert.assertTrue(IOCase.SENSITIVE.checkRegionMatches("ABC", 1, ""));
        Assert.assertFalse(IOCase.SENSITIVE.checkRegionMatches("ABC", 1, "A"));
        Assert.assertFalse(IOCase.SENSITIVE.checkRegionMatches("ABC", 1, "AB"));
        Assert.assertFalse(IOCase.SENSITIVE.checkRegionMatches("ABC", 1, "ABC"));
        Assert.assertTrue(IOCase.SENSITIVE.checkRegionMatches("ABC", 1, "BC"));
        Assert.assertFalse(IOCase.SENSITIVE.checkRegionMatches("ABC", 1, "C"));
        Assert.assertFalse(IOCase.SENSITIVE.checkRegionMatches("ABC", 1, "ABCD"));
        Assert.assertFalse(IOCase.SENSITIVE.checkRegionMatches("", 1, "ABC"));
        Assert.assertFalse(IOCase.SENSITIVE.checkRegionMatches("", 1, ""));
        try {
            IOCase.SENSITIVE.checkRegionMatches("ABC", 0, null);
            Assert.fail();
        } catch (final NullPointerException ignore) {
        }
        try {
            IOCase.SENSITIVE.checkRegionMatches(null, 0, "ABC");
            Assert.fail();
        } catch (final NullPointerException ignore) {
        }
        try {
            IOCase.SENSITIVE.checkRegionMatches(null, 0, null);
            Assert.fail();
        } catch (final NullPointerException ignore) {
        }
        try {
            IOCase.SENSITIVE.checkRegionMatches("ABC", 1, null);
            Assert.fail();
        } catch (final NullPointerException ignore) {
        }
        try {
            IOCase.SENSITIVE.checkRegionMatches(null, 1, "ABC");
            Assert.fail();
        } catch (final NullPointerException ignore) {
        }
        try {
            IOCase.SENSITIVE.checkRegionMatches(null, 1, null);
            Assert.fail();
        } catch (final NullPointerException ignore) {
        }
    }

    @Test
    public void test_checkRegionMatches_case() throws Exception {
        Assert.assertTrue(IOCase.SENSITIVE.checkRegionMatches("ABC", 0, "AB"));
        Assert.assertFalse(IOCase.SENSITIVE.checkRegionMatches("ABC", 0, "Ab"));
        Assert.assertTrue(IOCase.INSENSITIVE.checkRegionMatches("ABC", 0, "AB"));
        Assert.assertTrue(IOCase.INSENSITIVE.checkRegionMatches("ABC", 0, "Ab"));
        Assert.assertTrue(IOCase.SYSTEM.checkRegionMatches("ABC", 0, "AB"));
        Assert.assertEquals(IOCaseTestCase.WINDOWS, IOCase.SYSTEM.checkRegionMatches("ABC", 0, "Ab"));
    }

    // -----------------------------------------------------------------------
    private IOCase serialize(final IOCase value) throws Exception {
        final ByteArrayOutputStream buf = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(buf);
        out.writeObject(value);
        out.flush();
        out.close();
        final ByteArrayInputStream bufin = new ByteArrayInputStream(buf.toByteArray());
        final ObjectInputStream in = new ObjectInputStream(bufin);
        return ((IOCase) (in.readObject()));
    }
}

