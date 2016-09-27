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


package org.apache.commons.io.monitor;

import org.junit.Assert;
import org.apache.commons.io.filefilter.CanReadFileFilter;
import java.io.File;
import java.io.FileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.FileUtils;
import java.util.Iterator;
import org.junit.Test;

/**
 * {@link FileAlterationObserver} Test Case.
 */
public class FileAlterationObserverTestCase extends AbstractMonitorTestCase {
    /**
     * Construct a new test case.
     */
    public FileAlterationObserverTestCase() {
        testDirName = "test-observer";
        listener = new CollectionFileListener(true);
    }

    /**
     * Test add/remove listeners.
     */
    @Test
    public void testAddRemoveListeners() {
        final FileAlterationObserver observer = new FileAlterationObserver("/foo");
        // Null Listener
        observer.addListener(null);
        Assert.assertFalse("Listeners[1]", observer.getListeners().iterator().hasNext());
        observer.removeListener(null);
        Assert.assertFalse("Listeners[2]", observer.getListeners().iterator().hasNext());
        // Add Listener
        final FileAlterationListenerAdaptor listener = new FileAlterationListenerAdaptor();
        observer.addListener(listener);
        final Iterator<FileAlterationListener> it = observer.getListeners().iterator();
        Assert.assertTrue("Listeners[3]", it.hasNext());
        Assert.assertEquals("Added", listener, it.next());
        Assert.assertFalse("Listeners[4]", it.hasNext());
        // Remove Listener
        observer.removeListener(listener);
        Assert.assertFalse("Listeners[5]", observer.getListeners().iterator().hasNext());
    }

    /**
     * Test toString().
     */
    @Test
    public void testToString() {
        final File file = new File("/foo");
        FileAlterationObserver observer = null;
        observer = new FileAlterationObserver(file);
        Assert.assertEquals((("FileAlterationObserver[file='" + (file.getPath())) + "', listeners=0]"), observer.toString());
        observer = new FileAlterationObserver(file, CanReadFileFilter.CAN_READ);
        Assert.assertEquals((("FileAlterationObserver[file='" + (file.getPath())) + "', CanReadFileFilter, listeners=0]"), observer.toString());
        Assert.assertEquals(file, observer.getDirectory());
    }

    /**
     * Test checkAndNotify() method
     */
    @Test
    public void testDirectory() {
        try {
            checkAndNotify();
            checkCollectionsEmpty("A");
            final File testDirA = new File(testDir, "test-dir-A");
            final File testDirB = new File(testDir, "test-dir-B");
            final File testDirC = new File(testDir, "test-dir-C");
            testDirA.mkdir();
            testDirB.mkdir();
            testDirC.mkdir();
            final File testDirAFile1 = touch(new File(testDirA, "A-file1.java"));
            final File testDirAFile2 = touch(new File(testDirA, "A-file2.txt"));// filter should ignore this
            
            final File testDirAFile3 = touch(new File(testDirA, "A-file3.java"));
            File testDirAFile4 = touch(new File(testDirA, "A-file4.java"));
            final File testDirBFile1 = touch(new File(testDirB, "B-file1.java"));
            checkAndNotify();
            checkCollectionSizes("B", 3, 0, 0, 4, 0, 0);
            Assert.assertTrue("B testDirA", listener.getCreatedDirectories().contains(testDirA));
            Assert.assertTrue("B testDirB", listener.getCreatedDirectories().contains(testDirB));
            Assert.assertTrue("B testDirC", listener.getCreatedDirectories().contains(testDirC));
            Assert.assertTrue("B testDirAFile1", listener.getCreatedFiles().contains(testDirAFile1));
            Assert.assertFalse("B testDirAFile2", listener.getCreatedFiles().contains(testDirAFile2));
            Assert.assertTrue("B testDirAFile3", listener.getCreatedFiles().contains(testDirAFile3));
            Assert.assertTrue("B testDirAFile4", listener.getCreatedFiles().contains(testDirAFile4));
            Assert.assertTrue("B testDirBFile1", listener.getCreatedFiles().contains(testDirBFile1));
            checkAndNotify();
            checkCollectionsEmpty("C");
            testDirAFile4 = touch(testDirAFile4);
            FileUtils.deleteDirectory(testDirB);
            checkAndNotify();
            checkCollectionSizes("D", 0, 0, 1, 0, 1, 1);
            Assert.assertTrue("D testDirB", listener.getDeletedDirectories().contains(testDirB));
            Assert.assertTrue("D testDirAFile4", listener.getChangedFiles().contains(testDirAFile4));
            Assert.assertTrue("D testDirBFile1", listener.getDeletedFiles().contains(testDirBFile1));
            FileUtils.deleteDirectory(testDir);
            checkAndNotify();
            checkCollectionSizes("E", 0, 0, 2, 0, 0, 3);
            Assert.assertTrue("E testDirA", listener.getDeletedDirectories().contains(testDirA));
            Assert.assertTrue("E testDirAFile1", listener.getDeletedFiles().contains(testDirAFile1));
            Assert.assertFalse("E testDirAFile2", listener.getDeletedFiles().contains(testDirAFile2));
            Assert.assertTrue("E testDirAFile3", listener.getDeletedFiles().contains(testDirAFile3));
            Assert.assertTrue("E testDirAFile4", listener.getDeletedFiles().contains(testDirAFile4));
            testDir.mkdir();
            checkAndNotify();
            checkCollectionsEmpty("F");
            checkAndNotify();
            checkCollectionsEmpty("G");
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(("Threw " + e));
        }
    }

    /**
     * Test checkAndNotify() creating
     */
    @Test
    public void testFileCreate() {
        try {
            checkAndNotify();
            checkCollectionsEmpty("A");
            File testDirA = new File(testDir, "test-dir-A");
            testDirA.mkdir();
            testDir = touch(testDir);
            testDirA = touch(testDirA);
            File testDirAFile1 = new File(testDirA, "A-file1.java");
            final File testDirAFile2 = touch(new File(testDirA, "A-file2.java"));
            File testDirAFile3 = new File(testDirA, "A-file3.java");
            final File testDirAFile4 = touch(new File(testDirA, "A-file4.java"));
            File testDirAFile5 = new File(testDirA, "A-file5.java");
            checkAndNotify();
            checkCollectionSizes("B", 1, 0, 0, 2, 0, 0);
            Assert.assertFalse("B testDirAFile1", listener.getCreatedFiles().contains(testDirAFile1));
            Assert.assertTrue("B testDirAFile2", listener.getCreatedFiles().contains(testDirAFile2));
            Assert.assertFalse("B testDirAFile3", listener.getCreatedFiles().contains(testDirAFile3));
            Assert.assertTrue("B testDirAFile4", listener.getCreatedFiles().contains(testDirAFile4));
            Assert.assertFalse("B testDirAFile5", listener.getCreatedFiles().contains(testDirAFile5));
            Assert.assertFalse("B testDirAFile1 exists", testDirAFile1.exists());
            Assert.assertTrue("B testDirAFile2 exists", testDirAFile2.exists());
            Assert.assertFalse("B testDirAFile3 exists", testDirAFile3.exists());
            Assert.assertTrue("B testDirAFile4 exists", testDirAFile4.exists());
            Assert.assertFalse("B testDirAFile5 exists", testDirAFile5.exists());
            checkAndNotify();
            checkCollectionsEmpty("C");
            // Create file with name < first entry
            testDirAFile1 = touch(testDirAFile1);
            testDirA = touch(testDirA);
            checkAndNotify();
            checkCollectionSizes("D", 0, 1, 0, 1, 0, 0);
            Assert.assertTrue("D testDirAFile1 exists", testDirAFile1.exists());
            Assert.assertTrue("D testDirAFile1", listener.getCreatedFiles().contains(testDirAFile1));
            // Create file with name between 2 entries
            testDirAFile3 = touch(testDirAFile3);
            testDirA = touch(testDirA);
            checkAndNotify();
            checkCollectionSizes("E", 0, 1, 0, 1, 0, 0);
            Assert.assertTrue("E testDirAFile3 exists", testDirAFile3.exists());
            Assert.assertTrue("E testDirAFile3", listener.getCreatedFiles().contains(testDirAFile3));
            // Create file with name > last entry
            testDirAFile5 = touch(testDirAFile5);
            testDirA = touch(testDirA);
            checkAndNotify();
            checkCollectionSizes("F", 0, 1, 0, 1, 0, 0);
            Assert.assertTrue("F testDirAFile5 exists", testDirAFile5.exists());
            Assert.assertTrue("F testDirAFile5", listener.getCreatedFiles().contains(testDirAFile5));
        } catch (final Exception e) {
            Assert.fail(("Threw " + e));
        }
    }

    /**
     * Test checkAndNotify() creating
     */
    @Test
    public void testFileUpdate() {
        try {
            checkAndNotify();
            checkCollectionsEmpty("A");
            File testDirA = new File(testDir, "test-dir-A");
            testDirA.mkdir();
            testDir = touch(testDir);
            testDirA = touch(testDirA);
            File testDirAFile1 = touch(new File(testDirA, "A-file1.java"));
            final File testDirAFile2 = touch(new File(testDirA, "A-file2.java"));
            File testDirAFile3 = touch(new File(testDirA, "A-file3.java"));
            final File testDirAFile4 = touch(new File(testDirA, "A-file4.java"));
            File testDirAFile5 = touch(new File(testDirA, "A-file5.java"));
            checkAndNotify();
            checkCollectionSizes("B", 1, 0, 0, 5, 0, 0);
            Assert.assertTrue("B testDirAFile1", listener.getCreatedFiles().contains(testDirAFile1));
            Assert.assertTrue("B testDirAFile2", listener.getCreatedFiles().contains(testDirAFile2));
            Assert.assertTrue("B testDirAFile3", listener.getCreatedFiles().contains(testDirAFile3));
            Assert.assertTrue("B testDirAFile4", listener.getCreatedFiles().contains(testDirAFile4));
            Assert.assertTrue("B testDirAFile5", listener.getCreatedFiles().contains(testDirAFile5));
            Assert.assertTrue("B testDirAFile1 exists", testDirAFile1.exists());
            Assert.assertTrue("B testDirAFile2 exists", testDirAFile2.exists());
            Assert.assertTrue("B testDirAFile3 exists", testDirAFile3.exists());
            Assert.assertTrue("B testDirAFile4 exists", testDirAFile4.exists());
            Assert.assertTrue("B testDirAFile5 exists", testDirAFile5.exists());
            checkAndNotify();
            checkCollectionsEmpty("C");
            // Update first entry
            testDirAFile1 = touch(testDirAFile1);
            testDirA = touch(testDirA);
            checkAndNotify();
            checkCollectionSizes("D", 0, 1, 0, 0, 1, 0);
            Assert.assertTrue("D testDirAFile1", listener.getChangedFiles().contains(testDirAFile1));
            // Update file with name between 2 entries
            testDirAFile3 = touch(testDirAFile3);
            testDirA = touch(testDirA);
            checkAndNotify();
            checkCollectionSizes("E", 0, 1, 0, 0, 1, 0);
            Assert.assertTrue("E testDirAFile3", listener.getChangedFiles().contains(testDirAFile3));
            // Update last entry
            testDirAFile5 = touch(testDirAFile5);
            testDirA = touch(testDirA);
            checkAndNotify();
            checkCollectionSizes("F", 0, 1, 0, 0, 1, 0);
            Assert.assertTrue("F testDirAFile5", listener.getChangedFiles().contains(testDirAFile5));
        } catch (final Exception e) {
            Assert.fail(("Threw " + e));
        }
    }

    /**
     * Test checkAndNotify() deleting
     */
    @Test
    public void testFileDelete() {
        try {
            checkAndNotify();
            checkCollectionsEmpty("A");
            File testDirA = new File(testDir, "test-dir-A");
            testDirA.mkdir();
            testDir = touch(testDir);
            testDirA = touch(testDirA);
            final File testDirAFile1 = touch(new File(testDirA, "A-file1.java"));
            final File testDirAFile2 = touch(new File(testDirA, "A-file2.java"));
            final File testDirAFile3 = touch(new File(testDirA, "A-file3.java"));
            final File testDirAFile4 = touch(new File(testDirA, "A-file4.java"));
            final File testDirAFile5 = touch(new File(testDirA, "A-file5.java"));
            Assert.assertTrue("B testDirAFile1 exists", testDirAFile1.exists());
            Assert.assertTrue("B testDirAFile2 exists", testDirAFile2.exists());
            Assert.assertTrue("B testDirAFile3 exists", testDirAFile3.exists());
            Assert.assertTrue("B testDirAFile4 exists", testDirAFile4.exists());
            Assert.assertTrue("B testDirAFile5 exists", testDirAFile5.exists());
            checkAndNotify();
            checkCollectionSizes("B", 1, 0, 0, 5, 0, 0);
            Assert.assertTrue("B testDirAFile1", listener.getCreatedFiles().contains(testDirAFile1));
            Assert.assertTrue("B testDirAFile2", listener.getCreatedFiles().contains(testDirAFile2));
            Assert.assertTrue("B testDirAFile3", listener.getCreatedFiles().contains(testDirAFile3));
            Assert.assertTrue("B testDirAFile4", listener.getCreatedFiles().contains(testDirAFile4));
            Assert.assertTrue("B testDirAFile5", listener.getCreatedFiles().contains(testDirAFile5));
            checkAndNotify();
            checkCollectionsEmpty("C");
            // Delete first entry
            FileUtils.deleteQuietly(testDirAFile1);
            testDirA = touch(testDirA);
            checkAndNotify();
            checkCollectionSizes("D", 0, 1, 0, 0, 0, 1);
            Assert.assertFalse("D testDirAFile1 exists", testDirAFile1.exists());
            Assert.assertTrue("D testDirAFile1", listener.getDeletedFiles().contains(testDirAFile1));
            // Delete file with name between 2 entries
            FileUtils.deleteQuietly(testDirAFile3);
            testDirA = touch(testDirA);
            checkAndNotify();
            checkCollectionSizes("E", 0, 1, 0, 0, 0, 1);
            Assert.assertFalse("E testDirAFile3 exists", testDirAFile3.exists());
            Assert.assertTrue("E testDirAFile3", listener.getDeletedFiles().contains(testDirAFile3));
            // Delete last entry
            FileUtils.deleteQuietly(testDirAFile5);
            testDirA = touch(testDirA);
            checkAndNotify();
            checkCollectionSizes("F", 0, 1, 0, 0, 0, 1);
            Assert.assertFalse("F testDirAFile5 exists", testDirAFile5.exists());
            Assert.assertTrue("F testDirAFile5", listener.getDeletedFiles().contains(testDirAFile5));
        } catch (final Exception e) {
            Assert.fail(("Threw " + e));
        }
    }

    /**
     * Test checkAndNotify() method
     */
    @Test
    public void testObserveSingleFile() {
        try {
            final File testDirA = new File(testDir, "test-dir-A");
            File testDirAFile1 = new File(testDirA, "A-file1.java");
            testDirA.mkdir();
            final FileFilter nameFilter = FileFilterUtils.nameFileFilter(testDirAFile1.getName());
            createObserver(testDirA, nameFilter);
            checkAndNotify();
            checkCollectionsEmpty("A");
            Assert.assertFalse("A testDirAFile1 exists", testDirAFile1.exists());
            // Create
            testDirAFile1 = touch(testDirAFile1);
            File testDirAFile2 = touch(new File(testDirA, "A-file2.txt"));/* filter should ignore */
            
            File testDirAFile3 = touch(new File(testDirA, "A-file3.java"));/* filter should ignore */
            
            Assert.assertTrue("B testDirAFile1 exists", testDirAFile1.exists());
            Assert.assertTrue("B testDirAFile2 exists", testDirAFile2.exists());
            Assert.assertTrue("B testDirAFile3 exists", testDirAFile3.exists());
            checkAndNotify();
            checkCollectionSizes("C", 0, 0, 0, 1, 0, 0);
            Assert.assertTrue("C created", listener.getCreatedFiles().contains(testDirAFile1));
            Assert.assertFalse("C created", listener.getCreatedFiles().contains(testDirAFile2));
            Assert.assertFalse("C created", listener.getCreatedFiles().contains(testDirAFile3));
            // Modify
            testDirAFile1 = touch(testDirAFile1);
            testDirAFile2 = touch(testDirAFile2);
            testDirAFile3 = touch(testDirAFile3);
            checkAndNotify();
            checkCollectionSizes("D", 0, 0, 0, 0, 1, 0);
            Assert.assertTrue("D changed", listener.getChangedFiles().contains(testDirAFile1));
            Assert.assertFalse("D changed", listener.getChangedFiles().contains(testDirAFile2));
            Assert.assertFalse("D changed", listener.getChangedFiles().contains(testDirAFile3));
            // Delete
            FileUtils.deleteQuietly(testDirAFile1);
            FileUtils.deleteQuietly(testDirAFile2);
            FileUtils.deleteQuietly(testDirAFile3);
            Assert.assertFalse("E testDirAFile1 exists", testDirAFile1.exists());
            Assert.assertFalse("E testDirAFile2 exists", testDirAFile2.exists());
            Assert.assertFalse("E testDirAFile3 exists", testDirAFile3.exists());
            checkAndNotify();
            checkCollectionSizes("E", 0, 0, 0, 0, 0, 1);
            Assert.assertTrue("E deleted", listener.getDeletedFiles().contains(testDirAFile1));
            Assert.assertFalse("E deleted", listener.getDeletedFiles().contains(testDirAFile2));
            Assert.assertFalse("E deleted", listener.getDeletedFiles().contains(testDirAFile3));
        } catch (final Exception e) {
            Assert.fail(("Threw " + e));
        }
    }

    /**
     * Call {@link FileAlterationObserver#checkAndNotify()}.
     * 
     * @throws Exception if an error occurs
     */
    protected void checkAndNotify() throws Exception {
        observer.checkAndNotify();
    }
}

