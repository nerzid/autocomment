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
import java.io.BufferedOutputStream;
import java.io.File;
import org.apache.commons.io.testtools.FileBasedTestCase;
import java.io.FileOutputStream;
import java.io.IOException;
import org.junit.Test;
import org.apache.commons.io.testtools.TestUtils;

/**
 * Test for FileDeleteStrategy.
 * 
 * @version $Id: FileDeleteStrategyTestCase.java 1718944 2015-12-09 19:50:30Z krosenvold $
 * @see FileDeleteStrategy
 */
public class FileDeleteStrategyTestCase extends FileBasedTestCase {
    // -----------------------------------------------------------------------
    @Test
    public void testDeleteNormal() throws Exception {
        final File baseDir = getTestDirectory();
        final File subDir = new File(baseDir, "test");
        Assert.assertTrue(subDir.mkdir());
        final File subFile = new File(subDir, "a.txt");
        if (!(subFile.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + subFile) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(subFile));
        try {
            TestUtils.generateTestData(output, ((long) (16)));
        } finally {
            IOUtils.closeQuietly(output);
        }
        Assert.assertTrue(subDir.exists());
        Assert.assertTrue(subFile.exists());
        // delete dir
        try {
            FileDeleteStrategy.NORMAL.delete(subDir);
            Assert.fail();
        } catch (final IOException ex) {
            // expected
        }
        Assert.assertTrue(subDir.exists());
        Assert.assertTrue(subFile.exists());
        // delete file
        FileDeleteStrategy.NORMAL.delete(subFile);
        Assert.assertTrue(subDir.exists());
        Assert.assertFalse(subFile.exists());
        // delete dir
        FileDeleteStrategy.NORMAL.delete(subDir);
        Assert.assertFalse(subDir.exists());
        // delete dir
        FileDeleteStrategy.NORMAL.delete(subDir);// no error
        
        Assert.assertFalse(subDir.exists());
    }

    @Test
    public void testDeleteQuietlyNormal() throws Exception {
        final File baseDir = getTestDirectory();
        final File subDir = new File(baseDir, "test");
        Assert.assertTrue(subDir.mkdir());
        final File subFile = new File(subDir, "a.txt");
        if (!(subFile.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + subFile) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(subFile));
        try {
            TestUtils.generateTestData(output, ((long) (16)));
        } finally {
            IOUtils.closeQuietly(output);
        }
        Assert.assertTrue(subDir.exists());
        Assert.assertTrue(subFile.exists());
        // delete dir
        Assert.assertFalse(FileDeleteStrategy.NORMAL.deleteQuietly(subDir));
        Assert.assertTrue(subDir.exists());
        Assert.assertTrue(subFile.exists());
        // delete file
        Assert.assertTrue(FileDeleteStrategy.NORMAL.deleteQuietly(subFile));
        Assert.assertTrue(subDir.exists());
        Assert.assertFalse(subFile.exists());
        // delete dir
        Assert.assertTrue(FileDeleteStrategy.NORMAL.deleteQuietly(subDir));
        Assert.assertFalse(subDir.exists());
        // delete dir
        Assert.assertTrue(FileDeleteStrategy.NORMAL.deleteQuietly(subDir));// no error
        
        Assert.assertFalse(subDir.exists());
    }

    @Test
    public void testDeleteForce() throws Exception {
        final File baseDir = getTestDirectory();
        final File subDir = new File(baseDir, "test");
        Assert.assertTrue(subDir.mkdir());
        final File subFile = new File(subDir, "a.txt");
        if (!(subFile.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + subFile) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(subFile));
        try {
            TestUtils.generateTestData(output, ((long) (16)));
        } finally {
            IOUtils.closeQuietly(output);
        }
        Assert.assertTrue(subDir.exists());
        Assert.assertTrue(subFile.exists());
        // delete dir
        FileDeleteStrategy.FORCE.delete(subDir);
        Assert.assertFalse(subDir.exists());
        Assert.assertFalse(subFile.exists());
        // delete dir
        FileDeleteStrategy.FORCE.delete(subDir);// no error
        
        Assert.assertFalse(subDir.exists());
    }

    @Test
    public void testDeleteNull() throws Exception {
        try {
            FileDeleteStrategy.NORMAL.delete(null);
            Assert.fail();
        } catch (final NullPointerException ex) {
            // expected
        }
        Assert.assertTrue(FileDeleteStrategy.NORMAL.deleteQuietly(null));
    }

    @Test
    public void testToString() {
        Assert.assertEquals("FileDeleteStrategy[Normal]", FileDeleteStrategy.NORMAL.toString());
        Assert.assertEquals("FileDeleteStrategy[Force]", FileDeleteStrategy.FORCE.toString());
    }
}

