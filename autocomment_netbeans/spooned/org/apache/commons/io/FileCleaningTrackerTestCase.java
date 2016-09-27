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
import org.junit.Assert;
import org.junit.Before;
import java.io.BufferedOutputStream;
import java.io.File;
import org.apache.commons.io.testtools.FileBasedTestCase;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.io.RandomAccessFile;
import java.lang.ref.ReferenceQueue;
import org.junit.Test;
import org.apache.commons.io.testtools.TestUtils;

/**
 * This is used to test {@link FileCleaningTracker} for correctness.
 * 
 * @version $Id: FileCleaningTrackerTestCase.java 1718945 2015-12-09 19:51:14Z krosenvold $
 * @see FileCleaningTracker
 */
public class FileCleaningTrackerTestCase extends FileBasedTestCase {
    protected FileCleaningTracker newInstance() {
        return new FileCleaningTracker();
    }

    private File testFile;

    private FileCleaningTracker theInstance;

    public FileCleaningTrackerTestCase() {
        testFile = new File(getTestDirectory(), "file-test.txt");
    }

    @Before
    public void setUp() throws Exception {
        theInstance = newInstance();
        getTestDirectory();
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(getTestDirectory());
        // reset file cleaner class, so as not to break other tests
        /**
         * The following block of code can possibly be removed when the
         * deprecated {@link FileCleaner} is gone. The question is, whether
         * we want to support reuse of {@link FileCleaningTracker} instances,
         * which we should, IMO, not.
         */
        {
            theInstance.q = new ReferenceQueue<Object>();
            theInstance.trackers.clear();
            theInstance.deleteFailures.clear();
            theInstance.exitWhenFinished = false;
            theInstance.reaper = null;
        }
        theInstance = null;
    }

    // -----------------------------------------------------------------------
    @Test
    public void testFileCleanerFile() throws Exception {
        final String path = testFile.getPath();
        Assert.assertFalse(testFile.exists());
        RandomAccessFile r = new RandomAccessFile(testFile, "rw");
        Assert.assertTrue(testFile.exists());
        Assert.assertEquals(0, theInstance.getTrackCount());
        theInstance.track(path, r);
        Assert.assertEquals(1, theInstance.getTrackCount());
        r.close();
        testFile = null;
        r = null;
        waitUntilTrackCount();
        pauseForDeleteToComplete(new File(path));
        Assert.assertEquals(0, theInstance.getTrackCount());
        Assert.assertEquals(showFailures(), false, new File(path).exists());
    }

    @Test
    public void testFileCleanerDirectory() throws Exception {
        TestUtils.createFile(testFile, 100);
        Assert.assertTrue(testFile.exists());
        Assert.assertTrue(getTestDirectory().exists());
        Object obj = new Object();
        Assert.assertEquals(0, theInstance.getTrackCount());
        theInstance.track(getTestDirectory(), obj);
        Assert.assertEquals(1, theInstance.getTrackCount());
        obj = null;
        waitUntilTrackCount();
        Assert.assertEquals(0, theInstance.getTrackCount());
        Assert.assertTrue(testFile.exists());// not deleted, as dir not empty
        
        Assert.assertTrue(testFile.getParentFile().exists());// not deleted, as dir not empty
        
    }

    @Test
    public void testFileCleanerDirectory_NullStrategy() throws Exception {
        TestUtils.createFile(testFile, 100);
        Assert.assertTrue(testFile.exists());
        Assert.assertTrue(getTestDirectory().exists());
        Object obj = new Object();
        Assert.assertEquals(0, theInstance.getTrackCount());
        theInstance.track(getTestDirectory(), obj, null);
        Assert.assertEquals(1, theInstance.getTrackCount());
        obj = null;
        waitUntilTrackCount();
        Assert.assertEquals(0, theInstance.getTrackCount());
        Assert.assertTrue(testFile.exists());// not deleted, as dir not empty
        
        Assert.assertTrue(testFile.getParentFile().exists());// not deleted, as dir not empty
        
    }

    @Test
    public void testFileCleanerDirectory_ForceStrategy() throws Exception {
        if (!(testFile.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + (testFile)) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(testFile));
        try {
            TestUtils.generateTestData(output, ((long) (100)));
        } finally {
            IOUtils.closeQuietly(output);
        }
        Assert.assertTrue(testFile.exists());
        Assert.assertTrue(getTestDirectory().exists());
        Object obj = new Object();
        Assert.assertEquals(0, theInstance.getTrackCount());
        theInstance.track(getTestDirectory(), obj, FileDeleteStrategy.FORCE);
        Assert.assertEquals(1, theInstance.getTrackCount());
        obj = null;
        waitUntilTrackCount();
        pauseForDeleteToComplete(testFile.getParentFile());
        Assert.assertEquals(0, theInstance.getTrackCount());
        Assert.assertEquals(showFailures(), false, new File(testFile.getPath()).exists());
        Assert.assertEquals(showFailures(), false, testFile.getParentFile().exists());
    }

    @Test
    public void testFileCleanerNull() throws Exception {
        try {
            theInstance.track(((File) (null)), new Object());
            Assert.fail();
        } catch (final NullPointerException ex) {
            // expected
        }
        try {
            theInstance.track(((File) (null)), new Object(), FileDeleteStrategy.NORMAL);
            Assert.fail();
        } catch (final NullPointerException ex) {
            // expected
        }
        try {
            theInstance.track(((String) (null)), new Object());
            Assert.fail();
        } catch (final NullPointerException ex) {
            // expected
        }
        try {
            theInstance.track(((String) (null)), new Object(), FileDeleteStrategy.NORMAL);
            Assert.fail();
        } catch (final NullPointerException ex) {
            // expected
        }
    }

    @Test
    public void testFileCleanerExitWhenFinishedFirst() throws Exception {
        Assert.assertFalse(theInstance.exitWhenFinished);
        theInstance.exitWhenFinished();
        Assert.assertTrue(theInstance.exitWhenFinished);
        Assert.assertEquals(null, theInstance.reaper);
        waitUntilTrackCount();
        Assert.assertEquals(0, theInstance.getTrackCount());
        Assert.assertTrue(theInstance.exitWhenFinished);
        Assert.assertEquals(null, theInstance.reaper);
    }

    @Test
    public void testFileCleanerExitWhenFinished_NoTrackAfter() throws Exception {
        Assert.assertFalse(theInstance.exitWhenFinished);
        theInstance.exitWhenFinished();
        Assert.assertTrue(theInstance.exitWhenFinished);
        Assert.assertEquals(null, theInstance.reaper);
        final String path = testFile.getPath();
        final Object marker = new Object();
        try {
            theInstance.track(path, marker);
            Assert.fail();
        } catch (final IllegalStateException ex) {
            // expected
        }
        Assert.assertTrue(theInstance.exitWhenFinished);
        Assert.assertEquals(null, theInstance.reaper);
    }

    @Test
    public void testFileCleanerExitWhenFinished1() throws Exception {
        final String path = testFile.getPath();
        Assert.assertEquals("1-testFile exists", false, testFile.exists());
        RandomAccessFile r = new RandomAccessFile(testFile, "rw");
        Assert.assertEquals("2-testFile exists", true, testFile.exists());
        Assert.assertEquals("3-Track Count", 0, theInstance.getTrackCount());
        theInstance.track(path, r);
        Assert.assertEquals("4-Track Count", 1, theInstance.getTrackCount());
        Assert.assertEquals("5-exitWhenFinished", false, theInstance.exitWhenFinished);
        Assert.assertEquals("6-reaper.isAlive", true, theInstance.reaper.isAlive());
        Assert.assertEquals("7-exitWhenFinished", false, theInstance.exitWhenFinished);
        theInstance.exitWhenFinished();
        Assert.assertEquals("8-exitWhenFinished", true, theInstance.exitWhenFinished);
        Assert.assertEquals("9-reaper.isAlive", true, theInstance.reaper.isAlive());
        r.close();
        testFile = null;
        r = null;
        waitUntilTrackCount();
        pauseForDeleteToComplete(new File(path));
        Assert.assertEquals("10-Track Count", 0, theInstance.getTrackCount());
        Assert.assertEquals(("11-testFile exists " + (showFailures())), false, new File(path).exists());
        Assert.assertEquals("12-exitWhenFinished", true, theInstance.exitWhenFinished);
        Assert.assertEquals("13-reaper.isAlive", false, theInstance.reaper.isAlive());
    }

    @Test
    public void testFileCleanerExitWhenFinished2() throws Exception {
        final String path = testFile.getPath();
        Assert.assertFalse(testFile.exists());
        RandomAccessFile r = new RandomAccessFile(testFile, "rw");
        Assert.assertTrue(testFile.exists());
        Assert.assertEquals(0, theInstance.getTrackCount());
        theInstance.track(path, r);
        Assert.assertEquals(1, theInstance.getTrackCount());
        Assert.assertFalse(theInstance.exitWhenFinished);
        Assert.assertTrue(theInstance.reaper.isAlive());
        r.close();
        testFile = null;
        r = null;
        waitUntilTrackCount();
        pauseForDeleteToComplete(new File(path));
        Assert.assertEquals(0, theInstance.getTrackCount());
        Assert.assertEquals(showFailures(), false, new File(path).exists());
        Assert.assertFalse(theInstance.exitWhenFinished);
        Assert.assertTrue(theInstance.reaper.isAlive());
        Assert.assertFalse(theInstance.exitWhenFinished);
        theInstance.exitWhenFinished();
        for (int i = 0; (i < 20) && (theInstance.reaper.isAlive()); i++) {
            TestUtils.sleep(500L);// allow reaper thread to die
            
        }
        Assert.assertTrue(theInstance.exitWhenFinished);
        Assert.assertFalse(theInstance.reaper.isAlive());
    }

    // -----------------------------------------------------------------------
    private void pauseForDeleteToComplete(File file) {
        int count = 0;
        while ((file.exists()) && ((count++) < 40)) {
            try {
                TestUtils.sleep(500L);
            } catch (final InterruptedException ignore) {
            }
            file = new File(file.getPath());
        }
    }

    private String showFailures() throws Exception {
        if ((theInstance.deleteFailures.size()) == 1) {
            return ("[Delete Failed: " + (theInstance.deleteFailures.get(0))) + "]";
        } else {
            return ("[Delete Failures: " + (theInstance.deleteFailures.size())) + "]";
        }
    }

    private void waitUntilTrackCount() throws Exception {
        System.gc();
        TestUtils.sleep(500);
        int count = 0;
        while (((theInstance.getTrackCount()) != 0) && ((count++) < 5)) {
            List<String> list = new ArrayList<String>();
            try {
                long i = 0;
                while ((theInstance.getTrackCount()) != 0) {
                    list.add(("A Big String A Big String A Big String A Big String A Big String A Big String A Big String A Big String A Big String A Big String " + (i++)));
                }
            } catch (final Throwable ignored) {
            }
            list = null;
            System.gc();
            TestUtils.sleep(1000);
        }
        if ((theInstance.getTrackCount()) != 0) {
            throw new IllegalStateException("Your JVM is not releasing References, try running the testcase with less memory (-Xmx)");
        } 
    }
}

