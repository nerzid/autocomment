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

import java.util.Arrays;
import org.junit.Assert;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.Test;

/**
 * Unit tests for the <code>DeferredFileOutputStream</code> class.
 * 
 * @version $Id: DeferredFileOutputStreamTest.java 1718944 2015-12-09 19:50:30Z krosenvold $
 */
public class DeferredFileOutputStreamTest {
    /**
     * The test data as a string (which is the simplest form).
     */
    private final String testString = "0123456789";

    /**
     * The test data as a byte array, derived from the string.
     */
    private final byte[] testBytes = testString.getBytes();

    /**
     * Tests the case where the amount of data falls below the threshold, and
     * is therefore confined to memory.
     */
    @Test
    public void testBelowThreshold() {
        final DeferredFileOutputStream dfos = new DeferredFileOutputStream(((testBytes.length) + 42), null);
        try {
            dfos.write(testBytes, 0, testBytes.length);
            dfos.close();
        } catch (final IOException e) {
            Assert.fail("Unexpected IOException");
        }
        Assert.assertTrue(dfos.isInMemory());
        final byte[] resultBytes = dfos.getData();
        Assert.assertEquals(testBytes.length, resultBytes.length);
        Assert.assertTrue(Arrays.equals(resultBytes, testBytes));
    }

    /**
     * Tests the case where the amount of data is exactly the same as the
     * threshold. The behavior should be the same as that for the amount of
     * data being below (i.e. not exceeding) the threshold.
     */
    @Test
    public void testAtThreshold() {
        final DeferredFileOutputStream dfos = new DeferredFileOutputStream(testBytes.length, null);
        try {
            dfos.write(testBytes, 0, testBytes.length);
            dfos.close();
        } catch (final IOException e) {
            Assert.fail("Unexpected IOException");
        }
        Assert.assertTrue(dfos.isInMemory());
        final byte[] resultBytes = dfos.getData();
        Assert.assertEquals(testBytes.length, resultBytes.length);
        Assert.assertTrue(Arrays.equals(resultBytes, testBytes));
    }

    /**
     * Tests the case where the amount of data exceeds the threshold, and is
     * therefore written to disk. The actual data written to disk is verified,
     * as is the file itself.
     */
    @Test
    public void testAboveThreshold() {
        final File testFile = new File("testAboveThreshold.dat");
        // Ensure that the test starts from a clean base.
        testFile.delete();
        final DeferredFileOutputStream dfos = new DeferredFileOutputStream(((testBytes.length) - 5), testFile);
        try {
            dfos.write(testBytes, 0, testBytes.length);
            dfos.close();
        } catch (final IOException e) {
            Assert.fail("Unexpected IOException");
        }
        Assert.assertFalse(dfos.isInMemory());
        Assert.assertNull(dfos.getData());
        verifyResultFile(testFile);
        // Ensure that the test starts from a clean base.
        testFile.delete();
    }

    /**
     * Tests the case where there are multiple writes beyond the threshold, to
     * ensure that the <code>thresholdReached()</code> method is only called
     * once, as the threshold is crossed for the first time.
     */
    @Test
    public void testThresholdReached() {
        final File testFile = new File("testThresholdReached.dat");
        // Ensure that the test starts from a clean base.
        testFile.delete();
        final DeferredFileOutputStream dfos = new DeferredFileOutputStream(((testBytes.length) / 2), testFile);
        final int chunkSize = (testBytes.length) / 3;
        try {
            dfos.write(testBytes, 0, chunkSize);
            dfos.write(testBytes, chunkSize, chunkSize);
            dfos.write(testBytes, (chunkSize * 2), ((testBytes.length) - (chunkSize * 2)));
            dfos.close();
        } catch (final IOException e) {
            Assert.fail("Unexpected IOException");
        }
        Assert.assertFalse(dfos.isInMemory());
        Assert.assertNull(dfos.getData());
        verifyResultFile(testFile);
        // Ensure that the test starts from a clean base.
        testFile.delete();
    }

    /**
     * Test wether writeTo() properly writes small content.
     */
    @Test
    public void testWriteToSmall() {
        final File testFile = new File("testWriteToMem.dat");
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Ensure that the test starts from a clean base.
        testFile.delete();
        final DeferredFileOutputStream dfos = new DeferredFileOutputStream(((testBytes.length) * 2), testFile);
        try {
            dfos.write(testBytes);
            Assert.assertFalse(testFile.exists());
            Assert.assertTrue(dfos.isInMemory());
            try {
                dfos.writeTo(baos);
                Assert.fail("Should not have been able to write before closing");
            } catch (final IOException ioe) {
                // ok, as expected
            }
            dfos.close();
            dfos.writeTo(baos);
        } catch (final IOException ioe) {
            Assert.fail("Unexpected IOException");
        }
        final byte[] copiedBytes = baos.toByteArray();
        Assert.assertTrue(Arrays.equals(testBytes, copiedBytes));
        testFile.delete();
    }

    /**
     * Test wether writeTo() properly writes large content.
     */
    @Test
    public void testWriteToLarge() {
        final File testFile = new File("testWriteToFile.dat");
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Ensure that the test starts from a clean base.
        testFile.delete();
        final DeferredFileOutputStream dfos = new DeferredFileOutputStream(((testBytes.length) / 2), testFile);
        try {
            dfos.write(testBytes);
            Assert.assertTrue(testFile.exists());
            Assert.assertFalse(dfos.isInMemory());
            try {
                dfos.writeTo(baos);
                Assert.fail("Should not have been able to write before closeing");
            } catch (final IOException ioe) {
                // ok, as expected
            }
            dfos.close();
            dfos.writeTo(baos);
        } catch (final IOException ioe) {
            Assert.fail("Unexpected IOException");
        }
        final byte[] copiedBytes = baos.toByteArray();
        Assert.assertTrue(Arrays.equals(testBytes, copiedBytes));
        verifyResultFile(testFile);
        testFile.delete();
    }

    /**
     * Test specifying a temporary file and the threshold not reached.
     */
    @Test
    public void testTempFileBelowThreshold() {
        final String prefix = "commons-io-test";
        final String suffix = ".out";
        final File tempDir = new File(".");
        final DeferredFileOutputStream dfos = new DeferredFileOutputStream(((testBytes.length) + 42), prefix, suffix, tempDir);
        Assert.assertNull("Check file is null-A", dfos.getFile());
        try {
            dfos.write(testBytes, 0, testBytes.length);
            dfos.close();
        } catch (final IOException e) {
            Assert.fail("Unexpected IOException");
        }
        Assert.assertTrue(dfos.isInMemory());
        Assert.assertNull("Check file is null-B", dfos.getFile());
    }

    /**
     * Test specifying a temporary file and the threshold is reached.
     */
    @Test
    public void testTempFileAboveThreshold() {
        final String prefix = "commons-io-test";
        final String suffix = ".out";
        final File tempDir = new File(".");
        final DeferredFileOutputStream dfos = new DeferredFileOutputStream(((testBytes.length) - 5), prefix, suffix, tempDir);
        Assert.assertNull("Check file is null-A", dfos.getFile());
        try {
            dfos.write(testBytes, 0, testBytes.length);
            dfos.close();
        } catch (final IOException e) {
            Assert.fail("Unexpected IOException");
        }
        Assert.assertFalse(dfos.isInMemory());
        Assert.assertNull(dfos.getData());
        Assert.assertNotNull("Check file not null", dfos.getFile());
        Assert.assertTrue("Check file exists", dfos.getFile().exists());
        Assert.assertTrue("Check prefix", dfos.getFile().getName().startsWith(prefix));
        Assert.assertTrue("Check suffix", dfos.getFile().getName().endsWith(suffix));
        Assert.assertEquals("Check dir", tempDir.getPath(), dfos.getFile().getParent());
        verifyResultFile(dfos.getFile());
        // Delete the temporary file.
        dfos.getFile().delete();
    }

    /**
     * Test specifying a temporary file and the threshold is reached.
     */
    @Test
    public void testTempFileAboveThresholdPrefixOnly() {
        final String prefix = "commons-io-test";
        final String suffix = null;
        final File tempDir = null;
        final DeferredFileOutputStream dfos = new DeferredFileOutputStream(((testBytes.length) - 5), prefix, suffix, tempDir);
        Assert.assertNull("Check file is null-A", dfos.getFile());
        try {
            dfos.write(testBytes, 0, testBytes.length);
            dfos.close();
        } catch (final IOException e) {
            Assert.fail("Unexpected IOException");
        }
        Assert.assertFalse(dfos.isInMemory());
        Assert.assertNull(dfos.getData());
        Assert.assertNotNull("Check file not null", dfos.getFile());
        Assert.assertTrue("Check file exists", dfos.getFile().exists());
        Assert.assertTrue("Check prefix", dfos.getFile().getName().startsWith(prefix));
        Assert.assertTrue("Check suffix", dfos.getFile().getName().endsWith(".tmp"));// ".tmp" is default
        
        verifyResultFile(dfos.getFile());
        // Delete the temporary file.
        dfos.getFile().delete();
    }

    /**
     * Test specifying a temporary file and the threshold is reached.
     * @throws Exception
     */
    @Test
    public void testTempFileError() throws Exception {
        final String prefix = null;
        final String suffix = ".out";
        final File tempDir = new File(".");
        try {
            new DeferredFileOutputStream(((testBytes.length) - 5), prefix, suffix, tempDir).close();
            Assert.fail("Expected IllegalArgumentException ");
        } catch (final IllegalArgumentException e) {
            // expected
        }
    }

    /**
     * Verifies that the specified file contains the same data as the original
     * test data.
     * 
     * @param testFile The file containing the test output.
     */
    private void verifyResultFile(final File testFile) {
        try {
            final FileInputStream fis = new FileInputStream(testFile);
            Assert.assertEquals(testBytes.length, fis.available());
            final byte[] resultBytes = new byte[testBytes.length];
            Assert.assertEquals(testBytes.length, fis.read(resultBytes));
            Assert.assertTrue(Arrays.equals(resultBytes, testBytes));
            Assert.assertEquals((-1), fis.read(resultBytes));
            try {
                fis.close();
            } catch (final IOException e) {
                // Ignore an exception on close
            }
        } catch (final FileNotFoundException e) {
            Assert.fail("Unexpected FileNotFoundException");
        } catch (final IOException e) {
            Assert.fail("Unexpected IOException");
        }
    }
}

