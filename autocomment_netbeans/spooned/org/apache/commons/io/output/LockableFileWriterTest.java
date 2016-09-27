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


package org.apache.commons.io.output;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.apache.commons.io.Charsets;
import java.io.File;
import org.apache.commons.io.testtools.FileBasedTestCase;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import java.nio.charset.UnsupportedCharsetException;
import java.io.Writer;

/**
 * Tests that files really lock, although no writing is done as
 * the locking is tested only on construction.
 * 
 * @version $Id: LockableFileWriterTest.java 1718944 2015-12-09 19:50:30Z krosenvold $
 */
public class LockableFileWriterTest extends FileBasedTestCase {
    private File file;

    private File lockDir;

    private File lockFile;

    private File altLockDir;

    private File altLockFile;

    @Before
    public void setUp() {
        file = new File(getTestDirectory(), "testlockfile");
        lockDir = new File(System.getProperty("java.io.tmpdir"));
        lockFile = new File(lockDir, ((file.getName()) + ".lck"));
        altLockDir = getTestDirectory();
        altLockFile = new File(altLockDir, ((file.getName()) + ".lck"));
    }

    @After
    public void tearDown() {
        file.delete();
        lockFile.delete();
        altLockFile.delete();
    }

    // -----------------------------------------------------------------------
    @Test
    public void testFileLocked() throws IOException {
        LockableFileWriter lfw1 = null;
        LockableFileWriter lfw2 = null;
        LockableFileWriter lfw3 = null;
        try {
            // open a valid locakable writer
            lfw1 = new LockableFileWriter(file);
            Assert.assertTrue(file.exists());
            Assert.assertTrue(lockFile.exists());
            // try to open a second writer
            try {
                lfw2 = new LockableFileWriter(file);
                Assert.fail("Somehow able to open a locked file. ");
            } catch (final IOException ioe) {
                final String msg = ioe.getMessage();
                Assert.assertTrue("Exception message does not start correctly. ", msg.startsWith("Can't write file, lock "));
                Assert.assertTrue(file.exists());
                Assert.assertTrue(lockFile.exists());
            }
            // try to open a third writer
            try {
                lfw3 = new LockableFileWriter(file);
                Assert.fail("Somehow able to open a locked file. ");
            } catch (final IOException ioe) {
                final String msg = ioe.getMessage();
                Assert.assertTrue("Exception message does not start correctly. ", msg.startsWith("Can't write file, lock "));
                Assert.assertTrue(file.exists());
                Assert.assertTrue(lockFile.exists());
            }
        } finally {
            IOUtils.closeQuietly(lfw1);
            IOUtils.closeQuietly(lfw2);
            IOUtils.closeQuietly(lfw3);
        }
        Assert.assertTrue(file.exists());
        Assert.assertFalse(lockFile.exists());
    }

    // -----------------------------------------------------------------------
    // unavoidable until Java 7
    @SuppressWarnings(value = "deprecation")
    @Test
    public void testAlternateLockDir() throws IOException {
        LockableFileWriter lfw1 = null;
        LockableFileWriter lfw2 = null;
        try {
            // open a valid locakable writer
            lfw1 = new LockableFileWriter(file, "UTF-8", true, altLockDir.getAbsolutePath());
            Assert.assertTrue(file.exists());
            Assert.assertTrue(altLockFile.exists());
            // try to open a second writer
            try {
                lfw2 = new LockableFileWriter(file, Charsets.UTF_8, true, altLockDir.getAbsolutePath());
                Assert.fail("Somehow able to open a locked file. ");
            } catch (final IOException ioe) {
                final String msg = ioe.getMessage();
                Assert.assertTrue("Exception message does not start correctly. ", msg.startsWith("Can't write file, lock "));
                Assert.assertTrue(file.exists());
                Assert.assertTrue(altLockFile.exists());
            }
        } finally {
            IOUtils.closeQuietly(lfw1);
            IOUtils.closeQuietly(lfw2);
        }
        Assert.assertTrue(file.exists());
        Assert.assertFalse(altLockFile.exists());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testFileNotLocked() throws IOException {
        // open a valid locakable writer
        LockableFileWriter lfw1 = null;
        try {
            lfw1 = new LockableFileWriter(file);
            Assert.assertTrue(file.exists());
            Assert.assertTrue(lockFile.exists());
        } finally {
            IOUtils.closeQuietly(lfw1);
        }
        Assert.assertTrue(file.exists());
        Assert.assertFalse(lockFile.exists());
        // open a second valid writer on the same file
        LockableFileWriter lfw2 = null;
        try {
            lfw2 = new LockableFileWriter(file);
            Assert.assertTrue(file.exists());
            Assert.assertTrue(lockFile.exists());
        } finally {
            IOUtils.closeQuietly(lfw2);
        }
        Assert.assertTrue(file.exists());
        Assert.assertFalse(lockFile.exists());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testConstructor_File_encoding_badEncoding() throws IOException {
        Writer writer = null;
        try {
            writer = new LockableFileWriter(file, "BAD-ENCODE");
            Assert.fail();
        } catch (final UnsupportedCharsetException ex) {
            // expected
            Assert.assertFalse(file.exists());
            Assert.assertFalse(lockFile.exists());
        } finally {
            IOUtils.closeQuietly(writer);
        }
        Assert.assertFalse(file.exists());
        Assert.assertFalse(lockFile.exists());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testConstructor_File_directory() {
        Writer writer = null;
        try {
            writer = new LockableFileWriter(getTestDirectory());
            Assert.fail();
        } catch (final IOException ex) {
            // expected
            Assert.assertFalse(file.exists());
            Assert.assertFalse(lockFile.exists());
        } finally {
            IOUtils.closeQuietly(writer);
        }
        Assert.assertFalse(file.exists());
        Assert.assertFalse(lockFile.exists());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testConstructor_File_nullFile() throws IOException {
        Writer writer = null;
        try {
            writer = new LockableFileWriter(((File) (null)));
            Assert.fail();
        } catch (final NullPointerException ex) {
            // expected
            Assert.assertFalse(file.exists());
            Assert.assertFalse(lockFile.exists());
        } finally {
            IOUtils.closeQuietly(writer);
        }
        Assert.assertFalse(file.exists());
        Assert.assertFalse(lockFile.exists());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testConstructor_fileName_nullFile() throws IOException {
        Writer writer = null;
        try {
            writer = new LockableFileWriter(((String) (null)));
            Assert.fail();
        } catch (final NullPointerException ex) {
            // expected
            Assert.assertFalse(file.exists());
            Assert.assertFalse(lockFile.exists());
        } finally {
            IOUtils.closeQuietly(writer);
        }
        Assert.assertFalse(file.exists());
        Assert.assertFalse(lockFile.exists());
    }
}

