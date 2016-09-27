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

import org.junit.After;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import java.io.BufferedOutputStream;
import java.util.Collection;
import java.io.File;
import org.apache.commons.io.testtools.FileBasedTestCase;
import java.io.FileOutputStream;
import java.io.IOException;
import org.junit.Test;
import org.apache.commons.io.testtools.TestUtils;

/**
 * This is used to test FilenameUtils for correctness.
 * 
 * @version $Id: FilenameUtilsTestCase.java 1718944 2015-12-09 19:50:30Z krosenvold $
 * @see FilenameUtils
 */
public class FilenameUtilsTestCase extends FileBasedTestCase {
    private static final String SEP = "" + (File.separatorChar);

    private static final boolean WINDOWS = (File.separatorChar) == '\\';

    private final File testFile1;

    private final File testFile2;

    private final int testFile1Size;

    private final int testFile2Size;

    public FilenameUtilsTestCase() {
        testFile1 = new File(getTestDirectory(), "file1-test.txt");
        testFile2 = new File(getTestDirectory(), "file1a-test.txt");
        testFile1Size = ((int) (testFile1.length()));
        testFile2Size = ((int) (testFile2.length()));
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        getTestDirectory();
        if (!(testFile1.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + (testFile1)) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output3 = new BufferedOutputStream(new FileOutputStream(testFile1));
        try {
            TestUtils.generateTestData(output3, ((long) (testFile1Size)));
        } finally {
            IOUtils.closeQuietly(output3);
        }
        if (!(testFile2.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + (testFile2)) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output2 = new BufferedOutputStream(new FileOutputStream(testFile2));
        try {
            TestUtils.generateTestData(output2, ((long) (testFile2Size)));
        } finally {
            IOUtils.closeQuietly(output2);
        }
        FileUtils.deleteDirectory(getTestDirectory());
        getTestDirectory();
        if (!(testFile1.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + (testFile1)) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output1 = new BufferedOutputStream(new FileOutputStream(testFile1));
        try {
            TestUtils.generateTestData(output1, ((long) (testFile1Size)));
        } finally {
            IOUtils.closeQuietly(output1);
        }
        if (!(testFile2.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + (testFile2)) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(testFile2));
        try {
            TestUtils.generateTestData(output, ((long) (testFile2Size)));
        } finally {
            IOUtils.closeQuietly(output);
        }
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(getTestDirectory());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testNormalize() throws Exception {
        Assert.assertEquals(null, FilenameUtils.normalize(null));
        Assert.assertEquals(null, FilenameUtils.normalize(":"));
        Assert.assertEquals(null, FilenameUtils.normalize("1:\\a\\b\\c.txt"));
        Assert.assertEquals(null, FilenameUtils.normalize("1:"));
        Assert.assertEquals(null, FilenameUtils.normalize("1:a"));
        Assert.assertEquals(null, FilenameUtils.normalize("\\\\\\a\\b\\c.txt"));
        Assert.assertEquals(null, FilenameUtils.normalize("\\\\a"));
        Assert.assertEquals((((("a" + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)) + "c.txt"), FilenameUtils.normalize("a\\b/c.txt"));
        Assert.assertEquals((((((("" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)) + "c.txt"), FilenameUtils.normalize("\\a\\b/c.txt"));
        Assert.assertEquals((((((("C:" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)) + "c.txt"), FilenameUtils.normalize("C:\\a\\b/c.txt"));
        Assert.assertEquals((((((((((("" + (FilenameUtilsTestCase.SEP)) + "") + (FilenameUtilsTestCase.SEP)) + "server") + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)) + "c.txt"), FilenameUtils.normalize("\\\\server\\a\\b/c.txt"));
        Assert.assertEquals((((((("~" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)) + "c.txt"), FilenameUtils.normalize("~\\a\\b/c.txt"));
        Assert.assertEquals((((((("~user" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)) + "c.txt"), FilenameUtils.normalize("~user\\a\\b/c.txt"));
        Assert.assertEquals((("a" + (FilenameUtilsTestCase.SEP)) + "c"), FilenameUtils.normalize("a/b/../c"));
        Assert.assertEquals("c", FilenameUtils.normalize("a/b/../../c"));
        Assert.assertEquals(("c" + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalize("a/b/../../c/"));
        Assert.assertEquals(null, FilenameUtils.normalize("a/b/../../../c"));
        Assert.assertEquals(("a" + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalize("a/b/.."));
        Assert.assertEquals(("a" + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalize("a/b/../"));
        Assert.assertEquals("", FilenameUtils.normalize("a/b/../.."));
        Assert.assertEquals("", FilenameUtils.normalize("a/b/../../"));
        Assert.assertEquals(null, FilenameUtils.normalize("a/b/../../.."));
        Assert.assertEquals((("a" + (FilenameUtilsTestCase.SEP)) + "d"), FilenameUtils.normalize("a/b/../c/../d"));
        Assert.assertEquals(((("a" + (FilenameUtilsTestCase.SEP)) + "d") + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalize("a/b/../c/../d/"));
        Assert.assertEquals((((("a" + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)) + "d"), FilenameUtils.normalize("a/b//d"));
        Assert.assertEquals(((("a" + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalize("a/b/././."));
        Assert.assertEquals(((("a" + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalize("a/b/./././"));
        Assert.assertEquals(("a" + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalize("./a/"));
        Assert.assertEquals("a", FilenameUtils.normalize("./a"));
        Assert.assertEquals("", FilenameUtils.normalize("./"));
        Assert.assertEquals("", FilenameUtils.normalize("."));
        Assert.assertEquals(null, FilenameUtils.normalize("../a"));
        Assert.assertEquals(null, FilenameUtils.normalize(".."));
        Assert.assertEquals("", FilenameUtils.normalize(""));
        Assert.assertEquals(((FilenameUtilsTestCase.SEP) + "a"), FilenameUtils.normalize("/a"));
        Assert.assertEquals((((FilenameUtilsTestCase.SEP) + "a") + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalize("/a/"));
        Assert.assertEquals(((((FilenameUtilsTestCase.SEP) + "a") + (FilenameUtilsTestCase.SEP)) + "c"), FilenameUtils.normalize("/a/b/../c"));
        Assert.assertEquals(((FilenameUtilsTestCase.SEP) + "c"), FilenameUtils.normalize("/a/b/../../c"));
        Assert.assertEquals(null, FilenameUtils.normalize("/a/b/../../../c"));
        Assert.assertEquals((((FilenameUtilsTestCase.SEP) + "a") + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalize("/a/b/.."));
        Assert.assertEquals(((FilenameUtilsTestCase.SEP) + ""), FilenameUtils.normalize("/a/b/../.."));
        Assert.assertEquals(null, FilenameUtils.normalize("/a/b/../../.."));
        Assert.assertEquals(((((FilenameUtilsTestCase.SEP) + "a") + (FilenameUtilsTestCase.SEP)) + "d"), FilenameUtils.normalize("/a/b/../c/../d"));
        Assert.assertEquals(((((((FilenameUtilsTestCase.SEP) + "a") + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)) + "d"), FilenameUtils.normalize("/a/b//d"));
        Assert.assertEquals((((((FilenameUtilsTestCase.SEP) + "a") + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalize("/a/b/././."));
        Assert.assertEquals(((FilenameUtilsTestCase.SEP) + "a"), FilenameUtils.normalize("/./a"));
        Assert.assertEquals(((FilenameUtilsTestCase.SEP) + ""), FilenameUtils.normalize("/./"));
        Assert.assertEquals(((FilenameUtilsTestCase.SEP) + ""), FilenameUtils.normalize("/."));
        Assert.assertEquals(null, FilenameUtils.normalize("/../a"));
        Assert.assertEquals(null, FilenameUtils.normalize("/.."));
        Assert.assertEquals(((FilenameUtilsTestCase.SEP) + ""), FilenameUtils.normalize("/"));
        Assert.assertEquals((("~" + (FilenameUtilsTestCase.SEP)) + "a"), FilenameUtils.normalize("~/a"));
        Assert.assertEquals(((("~" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalize("~/a/"));
        Assert.assertEquals((((("~" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "c"), FilenameUtils.normalize("~/a/b/../c"));
        Assert.assertEquals((("~" + (FilenameUtilsTestCase.SEP)) + "c"), FilenameUtils.normalize("~/a/b/../../c"));
        Assert.assertEquals(null, FilenameUtils.normalize("~/a/b/../../../c"));
        Assert.assertEquals(((("~" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalize("~/a/b/.."));
        Assert.assertEquals((("~" + (FilenameUtilsTestCase.SEP)) + ""), FilenameUtils.normalize("~/a/b/../.."));
        Assert.assertEquals(null, FilenameUtils.normalize("~/a/b/../../.."));
        Assert.assertEquals((((("~" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "d"), FilenameUtils.normalize("~/a/b/../c/../d"));
        Assert.assertEquals((((((("~" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)) + "d"), FilenameUtils.normalize("~/a/b//d"));
        Assert.assertEquals(((((("~" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalize("~/a/b/././."));
        Assert.assertEquals((("~" + (FilenameUtilsTestCase.SEP)) + "a"), FilenameUtils.normalize("~/./a"));
        Assert.assertEquals(("~" + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalize("~/./"));
        Assert.assertEquals(("~" + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalize("~/."));
        Assert.assertEquals(null, FilenameUtils.normalize("~/../a"));
        Assert.assertEquals(null, FilenameUtils.normalize("~/.."));
        Assert.assertEquals(("~" + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalize("~/"));
        Assert.assertEquals(("~" + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalize("~"));
        Assert.assertEquals((("~user" + (FilenameUtilsTestCase.SEP)) + "a"), FilenameUtils.normalize("~user/a"));
        Assert.assertEquals(((("~user" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalize("~user/a/"));
        Assert.assertEquals((((("~user" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "c"), FilenameUtils.normalize("~user/a/b/../c"));
        Assert.assertEquals((("~user" + (FilenameUtilsTestCase.SEP)) + "c"), FilenameUtils.normalize("~user/a/b/../../c"));
        Assert.assertEquals(null, FilenameUtils.normalize("~user/a/b/../../../c"));
        Assert.assertEquals(((("~user" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalize("~user/a/b/.."));
        Assert.assertEquals((("~user" + (FilenameUtilsTestCase.SEP)) + ""), FilenameUtils.normalize("~user/a/b/../.."));
        Assert.assertEquals(null, FilenameUtils.normalize("~user/a/b/../../.."));
        Assert.assertEquals((((("~user" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "d"), FilenameUtils.normalize("~user/a/b/../c/../d"));
        Assert.assertEquals((((((("~user" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)) + "d"), FilenameUtils.normalize("~user/a/b//d"));
        Assert.assertEquals(((((("~user" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalize("~user/a/b/././."));
        Assert.assertEquals((("~user" + (FilenameUtilsTestCase.SEP)) + "a"), FilenameUtils.normalize("~user/./a"));
        Assert.assertEquals((("~user" + (FilenameUtilsTestCase.SEP)) + ""), FilenameUtils.normalize("~user/./"));
        Assert.assertEquals((("~user" + (FilenameUtilsTestCase.SEP)) + ""), FilenameUtils.normalize("~user/."));
        Assert.assertEquals(null, FilenameUtils.normalize("~user/../a"));
        Assert.assertEquals(null, FilenameUtils.normalize("~user/.."));
        Assert.assertEquals(("~user" + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalize("~user/"));
        Assert.assertEquals(("~user" + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalize("~user"));
        Assert.assertEquals((("C:" + (FilenameUtilsTestCase.SEP)) + "a"), FilenameUtils.normalize("C:/a"));
        Assert.assertEquals(((("C:" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalize("C:/a/"));
        Assert.assertEquals((((("C:" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "c"), FilenameUtils.normalize("C:/a/b/../c"));
        Assert.assertEquals((("C:" + (FilenameUtilsTestCase.SEP)) + "c"), FilenameUtils.normalize("C:/a/b/../../c"));
        Assert.assertEquals(null, FilenameUtils.normalize("C:/a/b/../../../c"));
        Assert.assertEquals(((("C:" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalize("C:/a/b/.."));
        Assert.assertEquals((("C:" + (FilenameUtilsTestCase.SEP)) + ""), FilenameUtils.normalize("C:/a/b/../.."));
        Assert.assertEquals(null, FilenameUtils.normalize("C:/a/b/../../.."));
        Assert.assertEquals((((("C:" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "d"), FilenameUtils.normalize("C:/a/b/../c/../d"));
        Assert.assertEquals((((((("C:" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)) + "d"), FilenameUtils.normalize("C:/a/b//d"));
        Assert.assertEquals(((((("C:" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalize("C:/a/b/././."));
        Assert.assertEquals((("C:" + (FilenameUtilsTestCase.SEP)) + "a"), FilenameUtils.normalize("C:/./a"));
        Assert.assertEquals((("C:" + (FilenameUtilsTestCase.SEP)) + ""), FilenameUtils.normalize("C:/./"));
        Assert.assertEquals((("C:" + (FilenameUtilsTestCase.SEP)) + ""), FilenameUtils.normalize("C:/."));
        Assert.assertEquals(null, FilenameUtils.normalize("C:/../a"));
        Assert.assertEquals(null, FilenameUtils.normalize("C:/.."));
        Assert.assertEquals((("C:" + (FilenameUtilsTestCase.SEP)) + ""), FilenameUtils.normalize("C:/"));
        Assert.assertEquals(("C:" + "a"), FilenameUtils.normalize("C:a"));
        Assert.assertEquals((("C:" + "a") + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalize("C:a/"));
        Assert.assertEquals(((("C:" + "a") + (FilenameUtilsTestCase.SEP)) + "c"), FilenameUtils.normalize("C:a/b/../c"));
        Assert.assertEquals(("C:" + "c"), FilenameUtils.normalize("C:a/b/../../c"));
        Assert.assertEquals(null, FilenameUtils.normalize("C:a/b/../../../c"));
        Assert.assertEquals((("C:" + "a") + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalize("C:a/b/.."));
        Assert.assertEquals(("C:" + ""), FilenameUtils.normalize("C:a/b/../.."));
        Assert.assertEquals(null, FilenameUtils.normalize("C:a/b/../../.."));
        Assert.assertEquals(((("C:" + "a") + (FilenameUtilsTestCase.SEP)) + "d"), FilenameUtils.normalize("C:a/b/../c/../d"));
        Assert.assertEquals(((((("C:" + "a") + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)) + "d"), FilenameUtils.normalize("C:a/b//d"));
        Assert.assertEquals((((("C:" + "a") + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalize("C:a/b/././."));
        Assert.assertEquals(("C:" + "a"), FilenameUtils.normalize("C:./a"));
        Assert.assertEquals(("C:" + ""), FilenameUtils.normalize("C:./"));
        Assert.assertEquals(("C:" + ""), FilenameUtils.normalize("C:."));
        Assert.assertEquals(null, FilenameUtils.normalize("C:../a"));
        Assert.assertEquals(null, FilenameUtils.normalize("C:.."));
        Assert.assertEquals(("C:" + ""), FilenameUtils.normalize("C:"));
        Assert.assertEquals((((((FilenameUtilsTestCase.SEP) + (FilenameUtilsTestCase.SEP)) + "server") + (FilenameUtilsTestCase.SEP)) + "a"), FilenameUtils.normalize("//server/a"));
        Assert.assertEquals(((((((FilenameUtilsTestCase.SEP) + (FilenameUtilsTestCase.SEP)) + "server") + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalize("//server/a/"));
        Assert.assertEquals((((((((FilenameUtilsTestCase.SEP) + (FilenameUtilsTestCase.SEP)) + "server") + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "c"), FilenameUtils.normalize("//server/a/b/../c"));
        Assert.assertEquals((((((FilenameUtilsTestCase.SEP) + (FilenameUtilsTestCase.SEP)) + "server") + (FilenameUtilsTestCase.SEP)) + "c"), FilenameUtils.normalize("//server/a/b/../../c"));
        Assert.assertEquals(null, FilenameUtils.normalize("//server/a/b/../../../c"));
        Assert.assertEquals(((((((FilenameUtilsTestCase.SEP) + (FilenameUtilsTestCase.SEP)) + "server") + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalize("//server/a/b/.."));
        Assert.assertEquals((((((FilenameUtilsTestCase.SEP) + (FilenameUtilsTestCase.SEP)) + "server") + (FilenameUtilsTestCase.SEP)) + ""), FilenameUtils.normalize("//server/a/b/../.."));
        Assert.assertEquals(null, FilenameUtils.normalize("//server/a/b/../../.."));
        Assert.assertEquals((((((((FilenameUtilsTestCase.SEP) + (FilenameUtilsTestCase.SEP)) + "server") + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "d"), FilenameUtils.normalize("//server/a/b/../c/../d"));
        Assert.assertEquals((((((((((FilenameUtilsTestCase.SEP) + (FilenameUtilsTestCase.SEP)) + "server") + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)) + "d"), FilenameUtils.normalize("//server/a/b//d"));
        Assert.assertEquals(((((((((FilenameUtilsTestCase.SEP) + (FilenameUtilsTestCase.SEP)) + "server") + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalize("//server/a/b/././."));
        Assert.assertEquals((((((FilenameUtilsTestCase.SEP) + (FilenameUtilsTestCase.SEP)) + "server") + (FilenameUtilsTestCase.SEP)) + "a"), FilenameUtils.normalize("//server/./a"));
        Assert.assertEquals((((((FilenameUtilsTestCase.SEP) + (FilenameUtilsTestCase.SEP)) + "server") + (FilenameUtilsTestCase.SEP)) + ""), FilenameUtils.normalize("//server/./"));
        Assert.assertEquals((((((FilenameUtilsTestCase.SEP) + (FilenameUtilsTestCase.SEP)) + "server") + (FilenameUtilsTestCase.SEP)) + ""), FilenameUtils.normalize("//server/."));
        Assert.assertEquals(null, FilenameUtils.normalize("//server/../a"));
        Assert.assertEquals(null, FilenameUtils.normalize("//server/.."));
        Assert.assertEquals((((((FilenameUtilsTestCase.SEP) + (FilenameUtilsTestCase.SEP)) + "server") + (FilenameUtilsTestCase.SEP)) + ""), FilenameUtils.normalize("//server/"));
    }

    @Test
    public void testNormalize_with_nullbytes() throws Exception {
        try {
            Assert.assertEquals((((("a" + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)) + "c.txt"), FilenameUtils.normalize("a\\b/c .txt"));
        } catch (IllegalArgumentException ignore) {
        }
        try {
            Assert.assertEquals((((("a" + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)) + "c.txt"), FilenameUtils.normalize(" a\\b/c.txt"));
        } catch (IllegalArgumentException ignore) {
        }
    }

    @Test
    public void testNormalizeUnixWin() throws Exception {
        // Normalize (Unix Separator)
        Assert.assertEquals("/a/c/", FilenameUtils.normalize("/a/b/../c/", true));
        Assert.assertEquals("/a/c/", FilenameUtils.normalize("\\a\\b\\..\\c\\", true));
        // Normalize (Windows Separator)
        Assert.assertEquals("\\a\\c\\", FilenameUtils.normalize("/a/b/../c/", false));
        Assert.assertEquals("\\a\\c\\", FilenameUtils.normalize("\\a\\b\\..\\c\\", false));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testNormalizeNoEndSeparator() throws Exception {
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator(null));
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator(":"));
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator("1:\\a\\b\\c.txt"));
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator("1:"));
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator("1:a"));
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator("\\\\\\a\\b\\c.txt"));
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator("\\\\a"));
        Assert.assertEquals((((("a" + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)) + "c.txt"), FilenameUtils.normalizeNoEndSeparator("a\\b/c.txt"));
        Assert.assertEquals((((((("" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)) + "c.txt"), FilenameUtils.normalizeNoEndSeparator("\\a\\b/c.txt"));
        Assert.assertEquals((((((("C:" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)) + "c.txt"), FilenameUtils.normalizeNoEndSeparator("C:\\a\\b/c.txt"));
        Assert.assertEquals((((((((((("" + (FilenameUtilsTestCase.SEP)) + "") + (FilenameUtilsTestCase.SEP)) + "server") + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)) + "c.txt"), FilenameUtils.normalizeNoEndSeparator("\\\\server\\a\\b/c.txt"));
        Assert.assertEquals((((((("~" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)) + "c.txt"), FilenameUtils.normalizeNoEndSeparator("~\\a\\b/c.txt"));
        Assert.assertEquals((((((("~user" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)) + "c.txt"), FilenameUtils.normalizeNoEndSeparator("~user\\a\\b/c.txt"));
        Assert.assertEquals((("a" + (FilenameUtilsTestCase.SEP)) + "c"), FilenameUtils.normalizeNoEndSeparator("a/b/../c"));
        Assert.assertEquals("c", FilenameUtils.normalizeNoEndSeparator("a/b/../../c"));
        Assert.assertEquals("c", FilenameUtils.normalizeNoEndSeparator("a/b/../../c/"));
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator("a/b/../../../c"));
        Assert.assertEquals("a", FilenameUtils.normalizeNoEndSeparator("a/b/.."));
        Assert.assertEquals("a", FilenameUtils.normalizeNoEndSeparator("a/b/../"));
        Assert.assertEquals("", FilenameUtils.normalizeNoEndSeparator("a/b/../.."));
        Assert.assertEquals("", FilenameUtils.normalizeNoEndSeparator("a/b/../../"));
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator("a/b/../../.."));
        Assert.assertEquals((("a" + (FilenameUtilsTestCase.SEP)) + "d"), FilenameUtils.normalizeNoEndSeparator("a/b/../c/../d"));
        Assert.assertEquals((("a" + (FilenameUtilsTestCase.SEP)) + "d"), FilenameUtils.normalizeNoEndSeparator("a/b/../c/../d/"));
        Assert.assertEquals((((("a" + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)) + "d"), FilenameUtils.normalizeNoEndSeparator("a/b//d"));
        Assert.assertEquals((("a" + (FilenameUtilsTestCase.SEP)) + "b"), FilenameUtils.normalizeNoEndSeparator("a/b/././."));
        Assert.assertEquals((("a" + (FilenameUtilsTestCase.SEP)) + "b"), FilenameUtils.normalizeNoEndSeparator("a/b/./././"));
        Assert.assertEquals("a", FilenameUtils.normalizeNoEndSeparator("./a/"));
        Assert.assertEquals("a", FilenameUtils.normalizeNoEndSeparator("./a"));
        Assert.assertEquals("", FilenameUtils.normalizeNoEndSeparator("./"));
        Assert.assertEquals("", FilenameUtils.normalizeNoEndSeparator("."));
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator("../a"));
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator(".."));
        Assert.assertEquals("", FilenameUtils.normalizeNoEndSeparator(""));
        Assert.assertEquals(((FilenameUtilsTestCase.SEP) + "a"), FilenameUtils.normalizeNoEndSeparator("/a"));
        Assert.assertEquals(((FilenameUtilsTestCase.SEP) + "a"), FilenameUtils.normalizeNoEndSeparator("/a/"));
        Assert.assertEquals(((((FilenameUtilsTestCase.SEP) + "a") + (FilenameUtilsTestCase.SEP)) + "c"), FilenameUtils.normalizeNoEndSeparator("/a/b/../c"));
        Assert.assertEquals(((FilenameUtilsTestCase.SEP) + "c"), FilenameUtils.normalizeNoEndSeparator("/a/b/../../c"));
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator("/a/b/../../../c"));
        Assert.assertEquals(((FilenameUtilsTestCase.SEP) + "a"), FilenameUtils.normalizeNoEndSeparator("/a/b/.."));
        Assert.assertEquals(((FilenameUtilsTestCase.SEP) + ""), FilenameUtils.normalizeNoEndSeparator("/a/b/../.."));
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator("/a/b/../../.."));
        Assert.assertEquals(((((FilenameUtilsTestCase.SEP) + "a") + (FilenameUtilsTestCase.SEP)) + "d"), FilenameUtils.normalizeNoEndSeparator("/a/b/../c/../d"));
        Assert.assertEquals(((((((FilenameUtilsTestCase.SEP) + "a") + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)) + "d"), FilenameUtils.normalizeNoEndSeparator("/a/b//d"));
        Assert.assertEquals(((((FilenameUtilsTestCase.SEP) + "a") + (FilenameUtilsTestCase.SEP)) + "b"), FilenameUtils.normalizeNoEndSeparator("/a/b/././."));
        Assert.assertEquals(((FilenameUtilsTestCase.SEP) + "a"), FilenameUtils.normalizeNoEndSeparator("/./a"));
        Assert.assertEquals(((FilenameUtilsTestCase.SEP) + ""), FilenameUtils.normalizeNoEndSeparator("/./"));
        Assert.assertEquals(((FilenameUtilsTestCase.SEP) + ""), FilenameUtils.normalizeNoEndSeparator("/."));
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator("/../a"));
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator("/.."));
        Assert.assertEquals(((FilenameUtilsTestCase.SEP) + ""), FilenameUtils.normalizeNoEndSeparator("/"));
        Assert.assertEquals((("~" + (FilenameUtilsTestCase.SEP)) + "a"), FilenameUtils.normalizeNoEndSeparator("~/a"));
        Assert.assertEquals((("~" + (FilenameUtilsTestCase.SEP)) + "a"), FilenameUtils.normalizeNoEndSeparator("~/a/"));
        Assert.assertEquals((((("~" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "c"), FilenameUtils.normalizeNoEndSeparator("~/a/b/../c"));
        Assert.assertEquals((("~" + (FilenameUtilsTestCase.SEP)) + "c"), FilenameUtils.normalizeNoEndSeparator("~/a/b/../../c"));
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator("~/a/b/../../../c"));
        Assert.assertEquals((("~" + (FilenameUtilsTestCase.SEP)) + "a"), FilenameUtils.normalizeNoEndSeparator("~/a/b/.."));
        Assert.assertEquals((("~" + (FilenameUtilsTestCase.SEP)) + ""), FilenameUtils.normalizeNoEndSeparator("~/a/b/../.."));
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator("~/a/b/../../.."));
        Assert.assertEquals((((("~" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "d"), FilenameUtils.normalizeNoEndSeparator("~/a/b/../c/../d"));
        Assert.assertEquals((((((("~" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)) + "d"), FilenameUtils.normalizeNoEndSeparator("~/a/b//d"));
        Assert.assertEquals((((("~" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "b"), FilenameUtils.normalizeNoEndSeparator("~/a/b/././."));
        Assert.assertEquals((("~" + (FilenameUtilsTestCase.SEP)) + "a"), FilenameUtils.normalizeNoEndSeparator("~/./a"));
        Assert.assertEquals(("~" + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalizeNoEndSeparator("~/./"));
        Assert.assertEquals(("~" + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalizeNoEndSeparator("~/."));
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator("~/../a"));
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator("~/.."));
        Assert.assertEquals(("~" + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalizeNoEndSeparator("~/"));
        Assert.assertEquals(("~" + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalizeNoEndSeparator("~"));
        Assert.assertEquals((("~user" + (FilenameUtilsTestCase.SEP)) + "a"), FilenameUtils.normalizeNoEndSeparator("~user/a"));
        Assert.assertEquals((("~user" + (FilenameUtilsTestCase.SEP)) + "a"), FilenameUtils.normalizeNoEndSeparator("~user/a/"));
        Assert.assertEquals((((("~user" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "c"), FilenameUtils.normalizeNoEndSeparator("~user/a/b/../c"));
        Assert.assertEquals((("~user" + (FilenameUtilsTestCase.SEP)) + "c"), FilenameUtils.normalizeNoEndSeparator("~user/a/b/../../c"));
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator("~user/a/b/../../../c"));
        Assert.assertEquals((("~user" + (FilenameUtilsTestCase.SEP)) + "a"), FilenameUtils.normalizeNoEndSeparator("~user/a/b/.."));
        Assert.assertEquals((("~user" + (FilenameUtilsTestCase.SEP)) + ""), FilenameUtils.normalizeNoEndSeparator("~user/a/b/../.."));
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator("~user/a/b/../../.."));
        Assert.assertEquals((((("~user" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "d"), FilenameUtils.normalizeNoEndSeparator("~user/a/b/../c/../d"));
        Assert.assertEquals((((((("~user" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)) + "d"), FilenameUtils.normalizeNoEndSeparator("~user/a/b//d"));
        Assert.assertEquals((((("~user" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "b"), FilenameUtils.normalizeNoEndSeparator("~user/a/b/././."));
        Assert.assertEquals((("~user" + (FilenameUtilsTestCase.SEP)) + "a"), FilenameUtils.normalizeNoEndSeparator("~user/./a"));
        Assert.assertEquals((("~user" + (FilenameUtilsTestCase.SEP)) + ""), FilenameUtils.normalizeNoEndSeparator("~user/./"));
        Assert.assertEquals((("~user" + (FilenameUtilsTestCase.SEP)) + ""), FilenameUtils.normalizeNoEndSeparator("~user/."));
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator("~user/../a"));
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator("~user/.."));
        Assert.assertEquals(("~user" + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalizeNoEndSeparator("~user/"));
        Assert.assertEquals(("~user" + (FilenameUtilsTestCase.SEP)), FilenameUtils.normalizeNoEndSeparator("~user"));
        Assert.assertEquals((("C:" + (FilenameUtilsTestCase.SEP)) + "a"), FilenameUtils.normalizeNoEndSeparator("C:/a"));
        Assert.assertEquals((("C:" + (FilenameUtilsTestCase.SEP)) + "a"), FilenameUtils.normalizeNoEndSeparator("C:/a/"));
        Assert.assertEquals((((("C:" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "c"), FilenameUtils.normalizeNoEndSeparator("C:/a/b/../c"));
        Assert.assertEquals((("C:" + (FilenameUtilsTestCase.SEP)) + "c"), FilenameUtils.normalizeNoEndSeparator("C:/a/b/../../c"));
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator("C:/a/b/../../../c"));
        Assert.assertEquals((("C:" + (FilenameUtilsTestCase.SEP)) + "a"), FilenameUtils.normalizeNoEndSeparator("C:/a/b/.."));
        Assert.assertEquals((("C:" + (FilenameUtilsTestCase.SEP)) + ""), FilenameUtils.normalizeNoEndSeparator("C:/a/b/../.."));
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator("C:/a/b/../../.."));
        Assert.assertEquals((((("C:" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "d"), FilenameUtils.normalizeNoEndSeparator("C:/a/b/../c/../d"));
        Assert.assertEquals((((((("C:" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)) + "d"), FilenameUtils.normalizeNoEndSeparator("C:/a/b//d"));
        Assert.assertEquals((((("C:" + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "b"), FilenameUtils.normalizeNoEndSeparator("C:/a/b/././."));
        Assert.assertEquals((("C:" + (FilenameUtilsTestCase.SEP)) + "a"), FilenameUtils.normalizeNoEndSeparator("C:/./a"));
        Assert.assertEquals((("C:" + (FilenameUtilsTestCase.SEP)) + ""), FilenameUtils.normalizeNoEndSeparator("C:/./"));
        Assert.assertEquals((("C:" + (FilenameUtilsTestCase.SEP)) + ""), FilenameUtils.normalizeNoEndSeparator("C:/."));
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator("C:/../a"));
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator("C:/.."));
        Assert.assertEquals((("C:" + (FilenameUtilsTestCase.SEP)) + ""), FilenameUtils.normalizeNoEndSeparator("C:/"));
        Assert.assertEquals(("C:" + "a"), FilenameUtils.normalizeNoEndSeparator("C:a"));
        Assert.assertEquals(("C:" + "a"), FilenameUtils.normalizeNoEndSeparator("C:a/"));
        Assert.assertEquals(((("C:" + "a") + (FilenameUtilsTestCase.SEP)) + "c"), FilenameUtils.normalizeNoEndSeparator("C:a/b/../c"));
        Assert.assertEquals(("C:" + "c"), FilenameUtils.normalizeNoEndSeparator("C:a/b/../../c"));
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator("C:a/b/../../../c"));
        Assert.assertEquals(("C:" + "a"), FilenameUtils.normalizeNoEndSeparator("C:a/b/.."));
        Assert.assertEquals(("C:" + ""), FilenameUtils.normalizeNoEndSeparator("C:a/b/../.."));
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator("C:a/b/../../.."));
        Assert.assertEquals(((("C:" + "a") + (FilenameUtilsTestCase.SEP)) + "d"), FilenameUtils.normalizeNoEndSeparator("C:a/b/../c/../d"));
        Assert.assertEquals(((((("C:" + "a") + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)) + "d"), FilenameUtils.normalizeNoEndSeparator("C:a/b//d"));
        Assert.assertEquals(((("C:" + "a") + (FilenameUtilsTestCase.SEP)) + "b"), FilenameUtils.normalizeNoEndSeparator("C:a/b/././."));
        Assert.assertEquals(("C:" + "a"), FilenameUtils.normalizeNoEndSeparator("C:./a"));
        Assert.assertEquals(("C:" + ""), FilenameUtils.normalizeNoEndSeparator("C:./"));
        Assert.assertEquals(("C:" + ""), FilenameUtils.normalizeNoEndSeparator("C:."));
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator("C:../a"));
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator("C:.."));
        Assert.assertEquals(("C:" + ""), FilenameUtils.normalizeNoEndSeparator("C:"));
        Assert.assertEquals((((((FilenameUtilsTestCase.SEP) + (FilenameUtilsTestCase.SEP)) + "server") + (FilenameUtilsTestCase.SEP)) + "a"), FilenameUtils.normalizeNoEndSeparator("//server/a"));
        Assert.assertEquals((((((FilenameUtilsTestCase.SEP) + (FilenameUtilsTestCase.SEP)) + "server") + (FilenameUtilsTestCase.SEP)) + "a"), FilenameUtils.normalizeNoEndSeparator("//server/a/"));
        Assert.assertEquals((((((((FilenameUtilsTestCase.SEP) + (FilenameUtilsTestCase.SEP)) + "server") + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "c"), FilenameUtils.normalizeNoEndSeparator("//server/a/b/../c"));
        Assert.assertEquals((((((FilenameUtilsTestCase.SEP) + (FilenameUtilsTestCase.SEP)) + "server") + (FilenameUtilsTestCase.SEP)) + "c"), FilenameUtils.normalizeNoEndSeparator("//server/a/b/../../c"));
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator("//server/a/b/../../../c"));
        Assert.assertEquals((((((FilenameUtilsTestCase.SEP) + (FilenameUtilsTestCase.SEP)) + "server") + (FilenameUtilsTestCase.SEP)) + "a"), FilenameUtils.normalizeNoEndSeparator("//server/a/b/.."));
        Assert.assertEquals((((((FilenameUtilsTestCase.SEP) + (FilenameUtilsTestCase.SEP)) + "server") + (FilenameUtilsTestCase.SEP)) + ""), FilenameUtils.normalizeNoEndSeparator("//server/a/b/../.."));
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator("//server/a/b/../../.."));
        Assert.assertEquals((((((((FilenameUtilsTestCase.SEP) + (FilenameUtilsTestCase.SEP)) + "server") + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "d"), FilenameUtils.normalizeNoEndSeparator("//server/a/b/../c/../d"));
        Assert.assertEquals((((((((((FilenameUtilsTestCase.SEP) + (FilenameUtilsTestCase.SEP)) + "server") + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)) + "d"), FilenameUtils.normalizeNoEndSeparator("//server/a/b//d"));
        Assert.assertEquals((((((((FilenameUtilsTestCase.SEP) + (FilenameUtilsTestCase.SEP)) + "server") + (FilenameUtilsTestCase.SEP)) + "a") + (FilenameUtilsTestCase.SEP)) + "b"), FilenameUtils.normalizeNoEndSeparator("//server/a/b/././."));
        Assert.assertEquals((((((FilenameUtilsTestCase.SEP) + (FilenameUtilsTestCase.SEP)) + "server") + (FilenameUtilsTestCase.SEP)) + "a"), FilenameUtils.normalizeNoEndSeparator("//server/./a"));
        Assert.assertEquals((((((FilenameUtilsTestCase.SEP) + (FilenameUtilsTestCase.SEP)) + "server") + (FilenameUtilsTestCase.SEP)) + ""), FilenameUtils.normalizeNoEndSeparator("//server/./"));
        Assert.assertEquals((((((FilenameUtilsTestCase.SEP) + (FilenameUtilsTestCase.SEP)) + "server") + (FilenameUtilsTestCase.SEP)) + ""), FilenameUtils.normalizeNoEndSeparator("//server/."));
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator("//server/../a"));
        Assert.assertEquals(null, FilenameUtils.normalizeNoEndSeparator("//server/.."));
        Assert.assertEquals((((((FilenameUtilsTestCase.SEP) + (FilenameUtilsTestCase.SEP)) + "server") + (FilenameUtilsTestCase.SEP)) + ""), FilenameUtils.normalizeNoEndSeparator("//server/"));
    }

    @Test
    public void testNormalizeNoEndSeparatorUnixWin() throws Exception {
        // Normalize (Unix Separator)
        Assert.assertEquals("/a/c", FilenameUtils.normalizeNoEndSeparator("/a/b/../c/", true));
        Assert.assertEquals("/a/c", FilenameUtils.normalizeNoEndSeparator("\\a\\b\\..\\c\\", true));
        // Normalize (Windows Separator)
        Assert.assertEquals("\\a\\c", FilenameUtils.normalizeNoEndSeparator("/a/b/../c/", false));
        Assert.assertEquals("\\a\\c", FilenameUtils.normalizeNoEndSeparator("\\a\\b\\..\\c\\", false));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testConcat() {
        Assert.assertEquals(null, FilenameUtils.concat("", null));
        Assert.assertEquals(null, FilenameUtils.concat(null, null));
        Assert.assertEquals(null, FilenameUtils.concat(null, ""));
        Assert.assertEquals(null, FilenameUtils.concat(null, "a"));
        Assert.assertEquals(((FilenameUtilsTestCase.SEP) + "a"), FilenameUtils.concat(null, "/a"));
        Assert.assertEquals(null, FilenameUtils.concat("", ":"));// invalid prefix
        
        Assert.assertEquals(null, FilenameUtils.concat(":", ""));// invalid prefix
        
        Assert.assertEquals(("f" + (FilenameUtilsTestCase.SEP)), FilenameUtils.concat("", "f/"));
        Assert.assertEquals("f", FilenameUtils.concat("", "f"));
        Assert.assertEquals(((("a" + (FilenameUtilsTestCase.SEP)) + "f") + (FilenameUtilsTestCase.SEP)), FilenameUtils.concat("a/", "f/"));
        Assert.assertEquals((("a" + (FilenameUtilsTestCase.SEP)) + "f"), FilenameUtils.concat("a", "f"));
        Assert.assertEquals(((((("a" + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)) + "f") + (FilenameUtilsTestCase.SEP)), FilenameUtils.concat("a/b/", "f/"));
        Assert.assertEquals((((("a" + (FilenameUtilsTestCase.SEP)) + "b") + (FilenameUtilsTestCase.SEP)) + "f"), FilenameUtils.concat("a/b", "f"));
        Assert.assertEquals(((("a" + (FilenameUtilsTestCase.SEP)) + "f") + (FilenameUtilsTestCase.SEP)), FilenameUtils.concat("a/b/", "../f/"));
        Assert.assertEquals((("a" + (FilenameUtilsTestCase.SEP)) + "f"), FilenameUtils.concat("a/b", "../f"));
        Assert.assertEquals(((((("a" + (FilenameUtilsTestCase.SEP)) + "c") + (FilenameUtilsTestCase.SEP)) + "g") + (FilenameUtilsTestCase.SEP)), FilenameUtils.concat("a/b/../c/", "f/../g/"));
        Assert.assertEquals((((("a" + (FilenameUtilsTestCase.SEP)) + "c") + (FilenameUtilsTestCase.SEP)) + "g"), FilenameUtils.concat("a/b/../c", "f/../g"));
        Assert.assertEquals((((("a" + (FilenameUtilsTestCase.SEP)) + "c.txt") + (FilenameUtilsTestCase.SEP)) + "f"), FilenameUtils.concat("a/c.txt", "f"));
        Assert.assertEquals((((FilenameUtilsTestCase.SEP) + "f") + (FilenameUtilsTestCase.SEP)), FilenameUtils.concat("", "/f/"));
        Assert.assertEquals(((FilenameUtilsTestCase.SEP) + "f"), FilenameUtils.concat("", "/f"));
        Assert.assertEquals((((FilenameUtilsTestCase.SEP) + "f") + (FilenameUtilsTestCase.SEP)), FilenameUtils.concat("a/", "/f/"));
        Assert.assertEquals(((FilenameUtilsTestCase.SEP) + "f"), FilenameUtils.concat("a", "/f"));
        Assert.assertEquals(((((FilenameUtilsTestCase.SEP) + "c") + (FilenameUtilsTestCase.SEP)) + "d"), FilenameUtils.concat("a/b/", "/c/d"));
        Assert.assertEquals((("C:c" + (FilenameUtilsTestCase.SEP)) + "d"), FilenameUtils.concat("a/b/", "C:c/d"));
        Assert.assertEquals((((("C:" + (FilenameUtilsTestCase.SEP)) + "c") + (FilenameUtilsTestCase.SEP)) + "d"), FilenameUtils.concat("a/b/", "C:/c/d"));
        Assert.assertEquals((((("~" + (FilenameUtilsTestCase.SEP)) + "c") + (FilenameUtilsTestCase.SEP)) + "d"), FilenameUtils.concat("a/b/", "~/c/d"));
        Assert.assertEquals((((("~user" + (FilenameUtilsTestCase.SEP)) + "c") + (FilenameUtilsTestCase.SEP)) + "d"), FilenameUtils.concat("a/b/", "~user/c/d"));
        Assert.assertEquals(("~" + (FilenameUtilsTestCase.SEP)), FilenameUtils.concat("a/b/", "~"));
        Assert.assertEquals(("~user" + (FilenameUtilsTestCase.SEP)), FilenameUtils.concat("a/b/", "~user"));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testSeparatorsToUnix() {
        Assert.assertEquals(null, FilenameUtils.separatorsToUnix(null));
        Assert.assertEquals("/a/b/c", FilenameUtils.separatorsToUnix("/a/b/c"));
        Assert.assertEquals("/a/b/c.txt", FilenameUtils.separatorsToUnix("/a/b/c.txt"));
        Assert.assertEquals("/a/b/c", FilenameUtils.separatorsToUnix("/a/b\\c"));
        Assert.assertEquals("/a/b/c", FilenameUtils.separatorsToUnix("\\a\\b\\c"));
        Assert.assertEquals("D:/a/b/c", FilenameUtils.separatorsToUnix("D:\\a\\b\\c"));
    }

    @Test
    public void testSeparatorsToWindows() {
        Assert.assertEquals(null, FilenameUtils.separatorsToWindows(null));
        Assert.assertEquals("\\a\\b\\c", FilenameUtils.separatorsToWindows("\\a\\b\\c"));
        Assert.assertEquals("\\a\\b\\c.txt", FilenameUtils.separatorsToWindows("\\a\\b\\c.txt"));
        Assert.assertEquals("\\a\\b\\c", FilenameUtils.separatorsToWindows("\\a\\b/c"));
        Assert.assertEquals("\\a\\b\\c", FilenameUtils.separatorsToWindows("/a/b/c"));
        Assert.assertEquals("D:\\a\\b\\c", FilenameUtils.separatorsToWindows("D:/a/b/c"));
    }

    @Test
    public void testSeparatorsToSystem() {
        if (FilenameUtilsTestCase.WINDOWS) {
            Assert.assertEquals(null, FilenameUtils.separatorsToSystem(null));
            Assert.assertEquals("\\a\\b\\c", FilenameUtils.separatorsToSystem("\\a\\b\\c"));
            Assert.assertEquals("\\a\\b\\c.txt", FilenameUtils.separatorsToSystem("\\a\\b\\c.txt"));
            Assert.assertEquals("\\a\\b\\c", FilenameUtils.separatorsToSystem("\\a\\b/c"));
            Assert.assertEquals("\\a\\b\\c", FilenameUtils.separatorsToSystem("/a/b/c"));
            Assert.assertEquals("D:\\a\\b\\c", FilenameUtils.separatorsToSystem("D:/a/b/c"));
        } else {
            Assert.assertEquals(null, FilenameUtils.separatorsToSystem(null));
            Assert.assertEquals("/a/b/c", FilenameUtils.separatorsToSystem("/a/b/c"));
            Assert.assertEquals("/a/b/c.txt", FilenameUtils.separatorsToSystem("/a/b/c.txt"));
            Assert.assertEquals("/a/b/c", FilenameUtils.separatorsToSystem("/a/b\\c"));
            Assert.assertEquals("/a/b/c", FilenameUtils.separatorsToSystem("\\a\\b\\c"));
            Assert.assertEquals("D:/a/b/c", FilenameUtils.separatorsToSystem("D:\\a\\b\\c"));
        }
    }

    // -----------------------------------------------------------------------
    @Test
    public void testGetPrefixLength() {
        Assert.assertEquals((-1), FilenameUtils.getPrefixLength(null));
        Assert.assertEquals((-1), FilenameUtils.getPrefixLength(":"));
        Assert.assertEquals((-1), FilenameUtils.getPrefixLength("1:\\a\\b\\c.txt"));
        Assert.assertEquals((-1), FilenameUtils.getPrefixLength("1:"));
        Assert.assertEquals((-1), FilenameUtils.getPrefixLength("1:a"));
        Assert.assertEquals((-1), FilenameUtils.getPrefixLength("\\\\\\a\\b\\c.txt"));
        Assert.assertEquals((-1), FilenameUtils.getPrefixLength("\\\\a"));
        Assert.assertEquals(0, FilenameUtils.getPrefixLength(""));
        Assert.assertEquals(1, FilenameUtils.getPrefixLength("\\"));
        Assert.assertEquals(2, FilenameUtils.getPrefixLength("C:"));
        Assert.assertEquals(3, FilenameUtils.getPrefixLength("C:\\"));
        Assert.assertEquals(9, FilenameUtils.getPrefixLength("//server/"));
        Assert.assertEquals(2, FilenameUtils.getPrefixLength("~"));
        Assert.assertEquals(2, FilenameUtils.getPrefixLength("~/"));
        Assert.assertEquals(6, FilenameUtils.getPrefixLength("~user"));
        Assert.assertEquals(6, FilenameUtils.getPrefixLength("~user/"));
        Assert.assertEquals(0, FilenameUtils.getPrefixLength("a\\b\\c.txt"));
        Assert.assertEquals(1, FilenameUtils.getPrefixLength("\\a\\b\\c.txt"));
        Assert.assertEquals(2, FilenameUtils.getPrefixLength("C:a\\b\\c.txt"));
        Assert.assertEquals(3, FilenameUtils.getPrefixLength("C:\\a\\b\\c.txt"));
        Assert.assertEquals(9, FilenameUtils.getPrefixLength("\\\\server\\a\\b\\c.txt"));
        Assert.assertEquals(0, FilenameUtils.getPrefixLength("a/b/c.txt"));
        Assert.assertEquals(1, FilenameUtils.getPrefixLength("/a/b/c.txt"));
        Assert.assertEquals(3, FilenameUtils.getPrefixLength("C:/a/b/c.txt"));
        Assert.assertEquals(9, FilenameUtils.getPrefixLength("//server/a/b/c.txt"));
        Assert.assertEquals(2, FilenameUtils.getPrefixLength("~/a/b/c.txt"));
        Assert.assertEquals(6, FilenameUtils.getPrefixLength("~user/a/b/c.txt"));
        Assert.assertEquals(0, FilenameUtils.getPrefixLength("a\\b\\c.txt"));
        Assert.assertEquals(1, FilenameUtils.getPrefixLength("\\a\\b\\c.txt"));
        Assert.assertEquals(2, FilenameUtils.getPrefixLength("~\\a\\b\\c.txt"));
        Assert.assertEquals(6, FilenameUtils.getPrefixLength("~user\\a\\b\\c.txt"));
        Assert.assertEquals(9, FilenameUtils.getPrefixLength("//server/a/b/c.txt"));
        Assert.assertEquals((-1), FilenameUtils.getPrefixLength("\\\\\\a\\b\\c.txt"));
        Assert.assertEquals((-1), FilenameUtils.getPrefixLength("///a/b/c.txt"));
    }

    @Test
    public void testIndexOfLastSeparator() {
        Assert.assertEquals((-1), FilenameUtils.indexOfLastSeparator(null));
        Assert.assertEquals((-1), FilenameUtils.indexOfLastSeparator("noseperator.inthispath"));
        Assert.assertEquals(3, FilenameUtils.indexOfLastSeparator("a/b/c"));
        Assert.assertEquals(3, FilenameUtils.indexOfLastSeparator("a\\b\\c"));
    }

    @Test
    public void testIndexOfExtension() {
        Assert.assertEquals((-1), FilenameUtils.indexOfExtension(null));
        Assert.assertEquals((-1), FilenameUtils.indexOfExtension("file"));
        Assert.assertEquals(4, FilenameUtils.indexOfExtension("file.txt"));
        Assert.assertEquals(13, FilenameUtils.indexOfExtension("a.txt/b.txt/c.txt"));
        Assert.assertEquals((-1), FilenameUtils.indexOfExtension("a/b/c"));
        Assert.assertEquals((-1), FilenameUtils.indexOfExtension("a\\b\\c"));
        Assert.assertEquals((-1), FilenameUtils.indexOfExtension("a/b.notextension/c"));
        Assert.assertEquals((-1), FilenameUtils.indexOfExtension("a\\b.notextension\\c"));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testGetPrefix() {
        Assert.assertEquals(null, FilenameUtils.getPrefix(null));
        Assert.assertEquals(null, FilenameUtils.getPrefix(":"));
        Assert.assertEquals(null, FilenameUtils.getPrefix("1:\\a\\b\\c.txt"));
        Assert.assertEquals(null, FilenameUtils.getPrefix("1:"));
        Assert.assertEquals(null, FilenameUtils.getPrefix("1:a"));
        Assert.assertEquals(null, FilenameUtils.getPrefix("\\\\\\a\\b\\c.txt"));
        Assert.assertEquals(null, FilenameUtils.getPrefix("\\\\a"));
        Assert.assertEquals("", FilenameUtils.getPrefix(""));
        Assert.assertEquals("\\", FilenameUtils.getPrefix("\\"));
        Assert.assertEquals("C:", FilenameUtils.getPrefix("C:"));
        Assert.assertEquals("C:\\", FilenameUtils.getPrefix("C:\\"));
        Assert.assertEquals("//server/", FilenameUtils.getPrefix("//server/"));
        Assert.assertEquals("~/", FilenameUtils.getPrefix("~"));
        Assert.assertEquals("~/", FilenameUtils.getPrefix("~/"));
        Assert.assertEquals("~user/", FilenameUtils.getPrefix("~user"));
        Assert.assertEquals("~user/", FilenameUtils.getPrefix("~user/"));
        Assert.assertEquals("", FilenameUtils.getPrefix("a\\b\\c.txt"));
        Assert.assertEquals("\\", FilenameUtils.getPrefix("\\a\\b\\c.txt"));
        Assert.assertEquals("C:\\", FilenameUtils.getPrefix("C:\\a\\b\\c.txt"));
        Assert.assertEquals("\\\\server\\", FilenameUtils.getPrefix("\\\\server\\a\\b\\c.txt"));
        Assert.assertEquals("", FilenameUtils.getPrefix("a/b/c.txt"));
        Assert.assertEquals("/", FilenameUtils.getPrefix("/a/b/c.txt"));
        Assert.assertEquals("C:/", FilenameUtils.getPrefix("C:/a/b/c.txt"));
        Assert.assertEquals("//server/", FilenameUtils.getPrefix("//server/a/b/c.txt"));
        Assert.assertEquals("~/", FilenameUtils.getPrefix("~/a/b/c.txt"));
        Assert.assertEquals("~user/", FilenameUtils.getPrefix("~user/a/b/c.txt"));
        Assert.assertEquals("", FilenameUtils.getPrefix("a\\b\\c.txt"));
        Assert.assertEquals("\\", FilenameUtils.getPrefix("\\a\\b\\c.txt"));
        Assert.assertEquals("~\\", FilenameUtils.getPrefix("~\\a\\b\\c.txt"));
        Assert.assertEquals("~user\\", FilenameUtils.getPrefix("~user\\a\\b\\c.txt"));
    }

    @Test
    public void testGetPrefix_with_nullbyte() {
        try {
            Assert.assertEquals("~user\\", FilenameUtils.getPrefix("~u ser\\a\\b\\c.txt"));
        } catch (IllegalArgumentException ignore) {
        }
    }

    @Test
    public void testGetPath() {
        Assert.assertEquals(null, FilenameUtils.getPath(null));
        Assert.assertEquals("", FilenameUtils.getPath("noseperator.inthispath"));
        Assert.assertEquals("", FilenameUtils.getPath("/noseperator.inthispath"));
        Assert.assertEquals("", FilenameUtils.getPath("\\noseperator.inthispath"));
        Assert.assertEquals("a/b/", FilenameUtils.getPath("a/b/c.txt"));
        Assert.assertEquals("a/b/", FilenameUtils.getPath("a/b/c"));
        Assert.assertEquals("a/b/c/", FilenameUtils.getPath("a/b/c/"));
        Assert.assertEquals("a\\b\\", FilenameUtils.getPath("a\\b\\c"));
        Assert.assertEquals(null, FilenameUtils.getPath(":"));
        Assert.assertEquals(null, FilenameUtils.getPath("1:/a/b/c.txt"));
        Assert.assertEquals(null, FilenameUtils.getPath("1:"));
        Assert.assertEquals(null, FilenameUtils.getPath("1:a"));
        Assert.assertEquals(null, FilenameUtils.getPath("///a/b/c.txt"));
        Assert.assertEquals(null, FilenameUtils.getPath("//a"));
        Assert.assertEquals("", FilenameUtils.getPath(""));
        Assert.assertEquals("", FilenameUtils.getPath("C:"));
        Assert.assertEquals("", FilenameUtils.getPath("C:/"));
        Assert.assertEquals("", FilenameUtils.getPath("//server/"));
        Assert.assertEquals("", FilenameUtils.getPath("~"));
        Assert.assertEquals("", FilenameUtils.getPath("~/"));
        Assert.assertEquals("", FilenameUtils.getPath("~user"));
        Assert.assertEquals("", FilenameUtils.getPath("~user/"));
        Assert.assertEquals("a/b/", FilenameUtils.getPath("a/b/c.txt"));
        Assert.assertEquals("a/b/", FilenameUtils.getPath("/a/b/c.txt"));
        Assert.assertEquals("", FilenameUtils.getPath("C:a"));
        Assert.assertEquals("a/b/", FilenameUtils.getPath("C:a/b/c.txt"));
        Assert.assertEquals("a/b/", FilenameUtils.getPath("C:/a/b/c.txt"));
        Assert.assertEquals("a/b/", FilenameUtils.getPath("//server/a/b/c.txt"));
        Assert.assertEquals("a/b/", FilenameUtils.getPath("~/a/b/c.txt"));
        Assert.assertEquals("a/b/", FilenameUtils.getPath("~user/a/b/c.txt"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPath_with_nullbyte() {
        Assert.assertEquals("a/b/", FilenameUtils.getPath("~user/a/ b/c.txt"));
    }

    @Test
    public void testGetPathNoEndSeparator() {
        Assert.assertEquals(null, FilenameUtils.getPath(null));
        Assert.assertEquals("", FilenameUtils.getPath("noseperator.inthispath"));
        Assert.assertEquals("", FilenameUtils.getPathNoEndSeparator("/noseperator.inthispath"));
        Assert.assertEquals("", FilenameUtils.getPathNoEndSeparator("\\noseperator.inthispath"));
        Assert.assertEquals("a/b", FilenameUtils.getPathNoEndSeparator("a/b/c.txt"));
        Assert.assertEquals("a/b", FilenameUtils.getPathNoEndSeparator("a/b/c"));
        Assert.assertEquals("a/b/c", FilenameUtils.getPathNoEndSeparator("a/b/c/"));
        Assert.assertEquals("a\\b", FilenameUtils.getPathNoEndSeparator("a\\b\\c"));
        Assert.assertEquals(null, FilenameUtils.getPathNoEndSeparator(":"));
        Assert.assertEquals(null, FilenameUtils.getPathNoEndSeparator("1:/a/b/c.txt"));
        Assert.assertEquals(null, FilenameUtils.getPathNoEndSeparator("1:"));
        Assert.assertEquals(null, FilenameUtils.getPathNoEndSeparator("1:a"));
        Assert.assertEquals(null, FilenameUtils.getPathNoEndSeparator("///a/b/c.txt"));
        Assert.assertEquals(null, FilenameUtils.getPathNoEndSeparator("//a"));
        Assert.assertEquals("", FilenameUtils.getPathNoEndSeparator(""));
        Assert.assertEquals("", FilenameUtils.getPathNoEndSeparator("C:"));
        Assert.assertEquals("", FilenameUtils.getPathNoEndSeparator("C:/"));
        Assert.assertEquals("", FilenameUtils.getPathNoEndSeparator("//server/"));
        Assert.assertEquals("", FilenameUtils.getPathNoEndSeparator("~"));
        Assert.assertEquals("", FilenameUtils.getPathNoEndSeparator("~/"));
        Assert.assertEquals("", FilenameUtils.getPathNoEndSeparator("~user"));
        Assert.assertEquals("", FilenameUtils.getPathNoEndSeparator("~user/"));
        Assert.assertEquals("a/b", FilenameUtils.getPathNoEndSeparator("a/b/c.txt"));
        Assert.assertEquals("a/b", FilenameUtils.getPathNoEndSeparator("/a/b/c.txt"));
        Assert.assertEquals("", FilenameUtils.getPathNoEndSeparator("C:a"));
        Assert.assertEquals("a/b", FilenameUtils.getPathNoEndSeparator("C:a/b/c.txt"));
        Assert.assertEquals("a/b", FilenameUtils.getPathNoEndSeparator("C:/a/b/c.txt"));
        Assert.assertEquals("a/b", FilenameUtils.getPathNoEndSeparator("//server/a/b/c.txt"));
        Assert.assertEquals("a/b", FilenameUtils.getPathNoEndSeparator("~/a/b/c.txt"));
        Assert.assertEquals("a/b", FilenameUtils.getPathNoEndSeparator("~user/a/b/c.txt"));
    }

    @Test
    public void testGetPathNoEndSeparator_with_null_byte() {
        try {
            Assert.assertEquals("a/b", FilenameUtils.getPathNoEndSeparator("~user/a /b/c.txt"));
        } catch (IllegalArgumentException ignore) {
        }
    }

    @Test
    public void testGetFullPath() {
        Assert.assertEquals(null, FilenameUtils.getFullPath(null));
        Assert.assertEquals("", FilenameUtils.getFullPath("noseperator.inthispath"));
        Assert.assertEquals("a/b/", FilenameUtils.getFullPath("a/b/c.txt"));
        Assert.assertEquals("a/b/", FilenameUtils.getFullPath("a/b/c"));
        Assert.assertEquals("a/b/c/", FilenameUtils.getFullPath("a/b/c/"));
        Assert.assertEquals("a\\b\\", FilenameUtils.getFullPath("a\\b\\c"));
        Assert.assertEquals(null, FilenameUtils.getFullPath(":"));
        Assert.assertEquals(null, FilenameUtils.getFullPath("1:/a/b/c.txt"));
        Assert.assertEquals(null, FilenameUtils.getFullPath("1:"));
        Assert.assertEquals(null, FilenameUtils.getFullPath("1:a"));
        Assert.assertEquals(null, FilenameUtils.getFullPath("///a/b/c.txt"));
        Assert.assertEquals(null, FilenameUtils.getFullPath("//a"));
        Assert.assertEquals("", FilenameUtils.getFullPath(""));
        Assert.assertEquals("C:", FilenameUtils.getFullPath("C:"));
        Assert.assertEquals("C:/", FilenameUtils.getFullPath("C:/"));
        Assert.assertEquals("//server/", FilenameUtils.getFullPath("//server/"));
        Assert.assertEquals("~/", FilenameUtils.getFullPath("~"));
        Assert.assertEquals("~/", FilenameUtils.getFullPath("~/"));
        Assert.assertEquals("~user/", FilenameUtils.getFullPath("~user"));
        Assert.assertEquals("~user/", FilenameUtils.getFullPath("~user/"));
        Assert.assertEquals("a/b/", FilenameUtils.getFullPath("a/b/c.txt"));
        Assert.assertEquals("/a/b/", FilenameUtils.getFullPath("/a/b/c.txt"));
        Assert.assertEquals("C:", FilenameUtils.getFullPath("C:a"));
        Assert.assertEquals("C:a/b/", FilenameUtils.getFullPath("C:a/b/c.txt"));
        Assert.assertEquals("C:/a/b/", FilenameUtils.getFullPath("C:/a/b/c.txt"));
        Assert.assertEquals("//server/a/b/", FilenameUtils.getFullPath("//server/a/b/c.txt"));
        Assert.assertEquals("~/a/b/", FilenameUtils.getFullPath("~/a/b/c.txt"));
        Assert.assertEquals("~user/a/b/", FilenameUtils.getFullPath("~user/a/b/c.txt"));
    }

    @Test
    public void testGetFullPathNoEndSeparator() {
        Assert.assertEquals(null, FilenameUtils.getFullPathNoEndSeparator(null));
        Assert.assertEquals("", FilenameUtils.getFullPathNoEndSeparator("noseperator.inthispath"));
        Assert.assertEquals("a/b", FilenameUtils.getFullPathNoEndSeparator("a/b/c.txt"));
        Assert.assertEquals("a/b", FilenameUtils.getFullPathNoEndSeparator("a/b/c"));
        Assert.assertEquals("a/b/c", FilenameUtils.getFullPathNoEndSeparator("a/b/c/"));
        Assert.assertEquals("a\\b", FilenameUtils.getFullPathNoEndSeparator("a\\b\\c"));
        Assert.assertEquals(null, FilenameUtils.getFullPathNoEndSeparator(":"));
        Assert.assertEquals(null, FilenameUtils.getFullPathNoEndSeparator("1:/a/b/c.txt"));
        Assert.assertEquals(null, FilenameUtils.getFullPathNoEndSeparator("1:"));
        Assert.assertEquals(null, FilenameUtils.getFullPathNoEndSeparator("1:a"));
        Assert.assertEquals(null, FilenameUtils.getFullPathNoEndSeparator("///a/b/c.txt"));
        Assert.assertEquals(null, FilenameUtils.getFullPathNoEndSeparator("//a"));
        Assert.assertEquals("", FilenameUtils.getFullPathNoEndSeparator(""));
        Assert.assertEquals("C:", FilenameUtils.getFullPathNoEndSeparator("C:"));
        Assert.assertEquals("C:/", FilenameUtils.getFullPathNoEndSeparator("C:/"));
        Assert.assertEquals("//server/", FilenameUtils.getFullPathNoEndSeparator("//server/"));
        Assert.assertEquals("~", FilenameUtils.getFullPathNoEndSeparator("~"));
        Assert.assertEquals("~/", FilenameUtils.getFullPathNoEndSeparator("~/"));
        Assert.assertEquals("~user", FilenameUtils.getFullPathNoEndSeparator("~user"));
        Assert.assertEquals("~user/", FilenameUtils.getFullPathNoEndSeparator("~user/"));
        Assert.assertEquals("a/b", FilenameUtils.getFullPathNoEndSeparator("a/b/c.txt"));
        Assert.assertEquals("/a/b", FilenameUtils.getFullPathNoEndSeparator("/a/b/c.txt"));
        Assert.assertEquals("C:", FilenameUtils.getFullPathNoEndSeparator("C:a"));
        Assert.assertEquals("C:a/b", FilenameUtils.getFullPathNoEndSeparator("C:a/b/c.txt"));
        Assert.assertEquals("C:/a/b", FilenameUtils.getFullPathNoEndSeparator("C:/a/b/c.txt"));
        Assert.assertEquals("//server/a/b", FilenameUtils.getFullPathNoEndSeparator("//server/a/b/c.txt"));
        Assert.assertEquals("~/a/b", FilenameUtils.getFullPathNoEndSeparator("~/a/b/c.txt"));
        Assert.assertEquals("~user/a/b", FilenameUtils.getFullPathNoEndSeparator("~user/a/b/c.txt"));
    }

    /**
     * Test for https://issues.apache.org/jira/browse/IO-248
     */
    @Test
    public void testGetFullPathNoEndSeparator_IO_248() {
        // Test single separator
        Assert.assertEquals("/", FilenameUtils.getFullPathNoEndSeparator("/"));
        Assert.assertEquals("\\", FilenameUtils.getFullPathNoEndSeparator("\\"));
        // Test one level directory
        Assert.assertEquals("/", FilenameUtils.getFullPathNoEndSeparator("/abc"));
        Assert.assertEquals("\\", FilenameUtils.getFullPathNoEndSeparator("\\abc"));
        // Test one level directory
        Assert.assertEquals("/abc", FilenameUtils.getFullPathNoEndSeparator("/abc/xyz"));
        Assert.assertEquals("\\abc", FilenameUtils.getFullPathNoEndSeparator("\\abc\\xyz"));
    }

    @Test
    public void testGetName() {
        Assert.assertEquals(null, FilenameUtils.getName(null));
        Assert.assertEquals("noseperator.inthispath", FilenameUtils.getName("noseperator.inthispath"));
        Assert.assertEquals("c.txt", FilenameUtils.getName("a/b/c.txt"));
        Assert.assertEquals("c", FilenameUtils.getName("a/b/c"));
        Assert.assertEquals("", FilenameUtils.getName("a/b/c/"));
        Assert.assertEquals("c", FilenameUtils.getName("a\\b\\c"));
    }

    @Test
    public void testInjectionFailure() {
        try {
            Assert.assertEquals("c", FilenameUtils.getName("a\\b\\ c"));
        } catch (IllegalArgumentException ignore) {
        }
    }

    @Test
    public void testGetBaseName() {
        Assert.assertEquals(null, FilenameUtils.getBaseName(null));
        Assert.assertEquals("noseperator", FilenameUtils.getBaseName("noseperator.inthispath"));
        Assert.assertEquals("c", FilenameUtils.getBaseName("a/b/c.txt"));
        Assert.assertEquals("c", FilenameUtils.getBaseName("a/b/c"));
        Assert.assertEquals("", FilenameUtils.getBaseName("a/b/c/"));
        Assert.assertEquals("c", FilenameUtils.getBaseName("a\\b\\c"));
        Assert.assertEquals("file.txt", FilenameUtils.getBaseName("file.txt.bak"));
    }

    @Test
    public void testGetBaseName_with_nullByte() {
        try {
            Assert.assertEquals("file.txt", FilenameUtils.getBaseName("fil e.txt.bak"));
        } catch (IllegalArgumentException ignore) {
        }
    }

    @Test
    public void testGetExtension() {
        Assert.assertEquals(null, FilenameUtils.getExtension(null));
        Assert.assertEquals("ext", FilenameUtils.getExtension("file.ext"));
        Assert.assertEquals("", FilenameUtils.getExtension("README"));
        Assert.assertEquals("com", FilenameUtils.getExtension("domain.dot.com"));
        Assert.assertEquals("jpeg", FilenameUtils.getExtension("image.jpeg"));
        Assert.assertEquals("", FilenameUtils.getExtension("a.b/c"));
        Assert.assertEquals("txt", FilenameUtils.getExtension("a.b/c.txt"));
        Assert.assertEquals("", FilenameUtils.getExtension("a/b/c"));
        Assert.assertEquals("", FilenameUtils.getExtension("a.b\\c"));
        Assert.assertEquals("txt", FilenameUtils.getExtension("a.b\\c.txt"));
        Assert.assertEquals("", FilenameUtils.getExtension("a\\b\\c"));
        Assert.assertEquals("", FilenameUtils.getExtension("C:\\temp\\foo.bar\\README"));
        Assert.assertEquals("ext", FilenameUtils.getExtension("../filename.ext"));
    }

    @Test
    public void testRemoveExtension() {
        Assert.assertEquals(null, FilenameUtils.removeExtension(null));
        Assert.assertEquals("file", FilenameUtils.removeExtension("file.ext"));
        Assert.assertEquals("README", FilenameUtils.removeExtension("README"));
        Assert.assertEquals("domain.dot", FilenameUtils.removeExtension("domain.dot.com"));
        Assert.assertEquals("image", FilenameUtils.removeExtension("image.jpeg"));
        Assert.assertEquals("a.b/c", FilenameUtils.removeExtension("a.b/c"));
        Assert.assertEquals("a.b/c", FilenameUtils.removeExtension("a.b/c.txt"));
        Assert.assertEquals("a/b/c", FilenameUtils.removeExtension("a/b/c"));
        Assert.assertEquals("a.b\\c", FilenameUtils.removeExtension("a.b\\c"));
        Assert.assertEquals("a.b\\c", FilenameUtils.removeExtension("a.b\\c.txt"));
        Assert.assertEquals("a\\b\\c", FilenameUtils.removeExtension("a\\b\\c"));
        Assert.assertEquals("C:\\temp\\foo.bar\\README", FilenameUtils.removeExtension("C:\\temp\\foo.bar\\README"));
        Assert.assertEquals("../filename", FilenameUtils.removeExtension("../filename.ext"));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testEquals() {
        Assert.assertTrue(FilenameUtils.equals(null, null));
        Assert.assertFalse(FilenameUtils.equals(null, ""));
        Assert.assertFalse(FilenameUtils.equals("", null));
        Assert.assertTrue(FilenameUtils.equals("", ""));
        Assert.assertTrue(FilenameUtils.equals("file.txt", "file.txt"));
        Assert.assertFalse(FilenameUtils.equals("file.txt", "FILE.TXT"));
        Assert.assertFalse(FilenameUtils.equals("a\\b\\file.txt", "a/b/file.txt"));
    }

    @Test
    public void testEqualsOnSystem() {
        Assert.assertTrue(FilenameUtils.equalsOnSystem(null, null));
        Assert.assertFalse(FilenameUtils.equalsOnSystem(null, ""));
        Assert.assertFalse(FilenameUtils.equalsOnSystem("", null));
        Assert.assertTrue(FilenameUtils.equalsOnSystem("", ""));
        Assert.assertTrue(FilenameUtils.equalsOnSystem("file.txt", "file.txt"));
        Assert.assertEquals(FilenameUtilsTestCase.WINDOWS, FilenameUtils.equalsOnSystem("file.txt", "FILE.TXT"));
        Assert.assertFalse(FilenameUtils.equalsOnSystem("a\\b\\file.txt", "a/b/file.txt"));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testEqualsNormalized() {
        Assert.assertTrue(FilenameUtils.equalsNormalized(null, null));
        Assert.assertFalse(FilenameUtils.equalsNormalized(null, ""));
        Assert.assertFalse(FilenameUtils.equalsNormalized("", null));
        Assert.assertTrue(FilenameUtils.equalsNormalized("", ""));
        Assert.assertTrue(FilenameUtils.equalsNormalized("file.txt", "file.txt"));
        Assert.assertFalse(FilenameUtils.equalsNormalized("file.txt", "FILE.TXT"));
        Assert.assertTrue(FilenameUtils.equalsNormalized("a\\b\\file.txt", "a/b/file.txt"));
        Assert.assertFalse(FilenameUtils.equalsNormalized("a/b/", "a/b"));
    }

    @Test
    public void testEqualsNormalizedOnSystem() {
        Assert.assertTrue(FilenameUtils.equalsNormalizedOnSystem(null, null));
        Assert.assertFalse(FilenameUtils.equalsNormalizedOnSystem(null, ""));
        Assert.assertFalse(FilenameUtils.equalsNormalizedOnSystem("", null));
        Assert.assertTrue(FilenameUtils.equalsNormalizedOnSystem("", ""));
        Assert.assertTrue(FilenameUtils.equalsNormalizedOnSystem("file.txt", "file.txt"));
        Assert.assertEquals(FilenameUtilsTestCase.WINDOWS, FilenameUtils.equalsNormalizedOnSystem("file.txt", "FILE.TXT"));
        Assert.assertTrue(FilenameUtils.equalsNormalizedOnSystem("a\\b\\file.txt", "a/b/file.txt"));
        Assert.assertFalse(FilenameUtils.equalsNormalizedOnSystem("a/b/", "a/b"));
    }

    /**
     * Test for https://issues.apache.org/jira/browse/IO-128
     */
    @Test
    public void testEqualsNormalizedError_IO_128() {
        try {
            FilenameUtils.equalsNormalizedOnSystem("//file.txt", "file.txt");
            Assert.fail("Invalid normalized first file");
        } catch (final NullPointerException e) {
            // expected result
        }
        try {
            FilenameUtils.equalsNormalizedOnSystem("file.txt", "//file.txt");
            Assert.fail("Invalid normalized second file");
        } catch (final NullPointerException e) {
            // expected result
        }
        try {
            FilenameUtils.equalsNormalizedOnSystem("//file.txt", "//file.txt");
            Assert.fail("Invalid normalized both filse");
        } catch (final NullPointerException e) {
            // expected result
        }
    }

    @Test
    public void testEquals_fullControl() {
        Assert.assertFalse(FilenameUtils.equals("file.txt", "FILE.TXT", true, IOCase.SENSITIVE));
        Assert.assertTrue(FilenameUtils.equals("file.txt", "FILE.TXT", true, IOCase.INSENSITIVE));
        Assert.assertEquals(FilenameUtilsTestCase.WINDOWS, FilenameUtils.equals("file.txt", "FILE.TXT", true, IOCase.SYSTEM));
        Assert.assertFalse(FilenameUtils.equals("file.txt", "FILE.TXT", true, null));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testIsExtension() {
        Assert.assertFalse(FilenameUtils.isExtension(null, ((String) (null))));
        Assert.assertFalse(FilenameUtils.isExtension("file.txt", ((String) (null))));
        Assert.assertTrue(FilenameUtils.isExtension("file", ((String) (null))));
        Assert.assertFalse(FilenameUtils.isExtension("file.txt", ""));
        Assert.assertTrue(FilenameUtils.isExtension("file", ""));
        Assert.assertTrue(FilenameUtils.isExtension("file.txt", "txt"));
        Assert.assertFalse(FilenameUtils.isExtension("file.txt", "rtf"));
        Assert.assertFalse(FilenameUtils.isExtension("a/b/file.txt", ((String) (null))));
        Assert.assertFalse(FilenameUtils.isExtension("a/b/file.txt", ""));
        Assert.assertTrue(FilenameUtils.isExtension("a/b/file.txt", "txt"));
        Assert.assertFalse(FilenameUtils.isExtension("a/b/file.txt", "rtf"));
        Assert.assertFalse(FilenameUtils.isExtension("a.b/file.txt", ((String) (null))));
        Assert.assertFalse(FilenameUtils.isExtension("a.b/file.txt", ""));
        Assert.assertTrue(FilenameUtils.isExtension("a.b/file.txt", "txt"));
        Assert.assertFalse(FilenameUtils.isExtension("a.b/file.txt", "rtf"));
        Assert.assertFalse(FilenameUtils.isExtension("a\\b\\file.txt", ((String) (null))));
        Assert.assertFalse(FilenameUtils.isExtension("a\\b\\file.txt", ""));
        Assert.assertTrue(FilenameUtils.isExtension("a\\b\\file.txt", "txt"));
        Assert.assertFalse(FilenameUtils.isExtension("a\\b\\file.txt", "rtf"));
        Assert.assertFalse(FilenameUtils.isExtension("a.b\\file.txt", ((String) (null))));
        Assert.assertFalse(FilenameUtils.isExtension("a.b\\file.txt", ""));
        Assert.assertTrue(FilenameUtils.isExtension("a.b\\file.txt", "txt"));
        Assert.assertFalse(FilenameUtils.isExtension("a.b\\file.txt", "rtf"));
        Assert.assertFalse(FilenameUtils.isExtension("a.b\\file.txt", "TXT"));
    }

    @Test
    public void testIsExtension_injection() {
        try {
            FilenameUtils.isExtension("a.b\\fi le.txt", "TXT");
            Assert.fail("Should throw IAE");
        } catch (IllegalArgumentException ignore) {
        }
    }

    @Test
    public void testIsExtensionArray() {
        Assert.assertFalse(FilenameUtils.isExtension(null, ((String[]) (null))));
        Assert.assertFalse(FilenameUtils.isExtension("file.txt", ((String[]) (null))));
        Assert.assertTrue(FilenameUtils.isExtension("file", ((String[]) (null))));
        Assert.assertFalse(FilenameUtils.isExtension("file.txt", new String[0]));
        Assert.assertTrue(FilenameUtils.isExtension("file.txt", new String[]{ "txt" }));
        Assert.assertFalse(FilenameUtils.isExtension("file.txt", new String[]{ "rtf" }));
        Assert.assertTrue(FilenameUtils.isExtension("file", new String[]{ "rtf" , "" }));
        Assert.assertTrue(FilenameUtils.isExtension("file.txt", new String[]{ "rtf" , "txt" }));
        Assert.assertFalse(FilenameUtils.isExtension("a/b/file.txt", ((String[]) (null))));
        Assert.assertFalse(FilenameUtils.isExtension("a/b/file.txt", new String[0]));
        Assert.assertTrue(FilenameUtils.isExtension("a/b/file.txt", new String[]{ "txt" }));
        Assert.assertFalse(FilenameUtils.isExtension("a/b/file.txt", new String[]{ "rtf" }));
        Assert.assertTrue(FilenameUtils.isExtension("a/b/file.txt", new String[]{ "rtf" , "txt" }));
        Assert.assertFalse(FilenameUtils.isExtension("a.b/file.txt", ((String[]) (null))));
        Assert.assertFalse(FilenameUtils.isExtension("a.b/file.txt", new String[0]));
        Assert.assertTrue(FilenameUtils.isExtension("a.b/file.txt", new String[]{ "txt" }));
        Assert.assertFalse(FilenameUtils.isExtension("a.b/file.txt", new String[]{ "rtf" }));
        Assert.assertTrue(FilenameUtils.isExtension("a.b/file.txt", new String[]{ "rtf" , "txt" }));
        Assert.assertFalse(FilenameUtils.isExtension("a\\b\\file.txt", ((String[]) (null))));
        Assert.assertFalse(FilenameUtils.isExtension("a\\b\\file.txt", new String[0]));
        Assert.assertTrue(FilenameUtils.isExtension("a\\b\\file.txt", new String[]{ "txt" }));
        Assert.assertFalse(FilenameUtils.isExtension("a\\b\\file.txt", new String[]{ "rtf" }));
        Assert.assertTrue(FilenameUtils.isExtension("a\\b\\file.txt", new String[]{ "rtf" , "txt" }));
        Assert.assertFalse(FilenameUtils.isExtension("a.b\\file.txt", ((String[]) (null))));
        Assert.assertFalse(FilenameUtils.isExtension("a.b\\file.txt", new String[0]));
        Assert.assertTrue(FilenameUtils.isExtension("a.b\\file.txt", new String[]{ "txt" }));
        Assert.assertFalse(FilenameUtils.isExtension("a.b\\file.txt", new String[]{ "rtf" }));
        Assert.assertTrue(FilenameUtils.isExtension("a.b\\file.txt", new String[]{ "rtf" , "txt" }));
        Assert.assertFalse(FilenameUtils.isExtension("a.b\\file.txt", new String[]{ "TXT" }));
        Assert.assertFalse(FilenameUtils.isExtension("a.b\\file.txt", new String[]{ "TXT" , "RTF" }));
    }

    @Test
    public void testIsExtensionCollection() {
        Assert.assertFalse(FilenameUtils.isExtension(null, ((Collection<String>) (null))));
        Assert.assertFalse(FilenameUtils.isExtension("file.txt", ((Collection<String>) (null))));
        Assert.assertTrue(FilenameUtils.isExtension("file", ((Collection<String>) (null))));
        Assert.assertFalse(FilenameUtils.isExtension("file.txt", new ArrayList<String>()));
        Assert.assertTrue(FilenameUtils.isExtension("file.txt", new ArrayList<String>(Arrays.asList(new String[]{ "txt" }))));
        Assert.assertFalse(FilenameUtils.isExtension("file.txt", new ArrayList<String>(Arrays.asList(new String[]{ "rtf" }))));
        Assert.assertTrue(FilenameUtils.isExtension("file", new ArrayList<String>(Arrays.asList(new String[]{ "rtf" , "" }))));
        Assert.assertTrue(FilenameUtils.isExtension("file.txt", new ArrayList<String>(Arrays.asList(new String[]{ "rtf" , "txt" }))));
        Assert.assertFalse(FilenameUtils.isExtension("a/b/file.txt", ((Collection<String>) (null))));
        Assert.assertFalse(FilenameUtils.isExtension("a/b/file.txt", new ArrayList<String>()));
        Assert.assertTrue(FilenameUtils.isExtension("a/b/file.txt", new ArrayList<String>(Arrays.asList(new String[]{ "txt" }))));
        Assert.assertFalse(FilenameUtils.isExtension("a/b/file.txt", new ArrayList<String>(Arrays.asList(new String[]{ "rtf" }))));
        Assert.assertTrue(FilenameUtils.isExtension("a/b/file.txt", new ArrayList<String>(Arrays.asList(new String[]{ "rtf" , "txt" }))));
        Assert.assertFalse(FilenameUtils.isExtension("a.b/file.txt", ((Collection<String>) (null))));
        Assert.assertFalse(FilenameUtils.isExtension("a.b/file.txt", new ArrayList<String>()));
        Assert.assertTrue(FilenameUtils.isExtension("a.b/file.txt", new ArrayList<String>(Arrays.asList(new String[]{ "txt" }))));
        Assert.assertFalse(FilenameUtils.isExtension("a.b/file.txt", new ArrayList<String>(Arrays.asList(new String[]{ "rtf" }))));
        Assert.assertTrue(FilenameUtils.isExtension("a.b/file.txt", new ArrayList<String>(Arrays.asList(new String[]{ "rtf" , "txt" }))));
        Assert.assertFalse(FilenameUtils.isExtension("a\\b\\file.txt", ((Collection<String>) (null))));
        Assert.assertFalse(FilenameUtils.isExtension("a\\b\\file.txt", new ArrayList<String>()));
        Assert.assertTrue(FilenameUtils.isExtension("a\\b\\file.txt", new ArrayList<String>(Arrays.asList(new String[]{ "txt" }))));
        Assert.assertFalse(FilenameUtils.isExtension("a\\b\\file.txt", new ArrayList<String>(Arrays.asList(new String[]{ "rtf" }))));
        Assert.assertTrue(FilenameUtils.isExtension("a\\b\\file.txt", new ArrayList<String>(Arrays.asList(new String[]{ "rtf" , "txt" }))));
        Assert.assertFalse(FilenameUtils.isExtension("a.b\\file.txt", ((Collection<String>) (null))));
        Assert.assertFalse(FilenameUtils.isExtension("a.b\\file.txt", new ArrayList<String>()));
        Assert.assertTrue(FilenameUtils.isExtension("a.b\\file.txt", new ArrayList<String>(Arrays.asList(new String[]{ "txt" }))));
        Assert.assertFalse(FilenameUtils.isExtension("a.b\\file.txt", new ArrayList<String>(Arrays.asList(new String[]{ "rtf" }))));
        Assert.assertTrue(FilenameUtils.isExtension("a.b\\file.txt", new ArrayList<String>(Arrays.asList(new String[]{ "rtf" , "txt" }))));
        Assert.assertFalse(FilenameUtils.isExtension("a.b\\file.txt", new ArrayList<String>(Arrays.asList(new String[]{ "TXT" }))));
        Assert.assertFalse(FilenameUtils.isExtension("a.b\\file.txt", new ArrayList<String>(Arrays.asList(new String[]{ "TXT" , "RTF" }))));
    }
}

