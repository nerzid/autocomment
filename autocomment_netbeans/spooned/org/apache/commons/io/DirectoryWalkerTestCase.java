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

import java.util.ArrayList;
import Assert.Assert;
import java.util.Collection;
import java.io.File;
import org.junit.Test;
import java.io.FileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import java.io.IOException;
import org.apache.commons.io.filefilter.IOFileFilter;
import java.util.List;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;

/**
 * This is used to test DirectoryWalker for correctness.
 *
 * @version $Id: DirectoryWalkerTestCase.java 1686260 2015-06-18 15:57:56Z krosenvold $
 * @see DirectoryWalker
 */
public class DirectoryWalkerTestCase {
    // Directories
    private static final File current = new File(".");

    private static final File javaDir = new File("src/main/java");

    private static final File orgDir = new File(DirectoryWalkerTestCase.javaDir, "org");

    private static final File apacheDir = new File(DirectoryWalkerTestCase.orgDir, "apache");

    private static final File commonsDir = new File(DirectoryWalkerTestCase.apacheDir, "commons");

    private static final File ioDir = new File(DirectoryWalkerTestCase.commonsDir, "io");

    private static final File outputDir = new File(DirectoryWalkerTestCase.ioDir, "output");

    private static final File[] dirs = new File[]{ DirectoryWalkerTestCase.orgDir , DirectoryWalkerTestCase.apacheDir , DirectoryWalkerTestCase.commonsDir , DirectoryWalkerTestCase.ioDir , DirectoryWalkerTestCase.outputDir };

    // Files
    private static final File filenameUtils = new File(DirectoryWalkerTestCase.ioDir, "FilenameUtils.java");

    private static final File ioUtils = new File(DirectoryWalkerTestCase.ioDir, "IOUtils.java");

    private static final File proxyWriter = new File(DirectoryWalkerTestCase.outputDir, "ProxyWriter.java");

    private static final File nullStream = new File(DirectoryWalkerTestCase.outputDir, "NullOutputStream.java");

    private static final File[] ioFiles = new File[]{ DirectoryWalkerTestCase.filenameUtils , DirectoryWalkerTestCase.ioUtils };

    private static final File[] outputFiles = new File[]{ DirectoryWalkerTestCase.proxyWriter , DirectoryWalkerTestCase.nullStream };

    // Filters
    private static final IOFileFilter dirsFilter = DirectoryWalkerTestCase.createNameFilter(DirectoryWalkerTestCase.dirs);

    private static final IOFileFilter iofilesFilter = DirectoryWalkerTestCase.createNameFilter(DirectoryWalkerTestCase.ioFiles);

    private static final IOFileFilter outputFilesFilter = DirectoryWalkerTestCase.createNameFilter(DirectoryWalkerTestCase.outputFiles);

    private static final IOFileFilter ioDirAndFilesFilter = new OrFileFilter(DirectoryWalkerTestCase.dirsFilter, DirectoryWalkerTestCase.iofilesFilter);

    private static final IOFileFilter dirsAndFilesFilter = new OrFileFilter(DirectoryWalkerTestCase.ioDirAndFilesFilter, DirectoryWalkerTestCase.outputFilesFilter);

    // Filter to exclude SVN files
    private static final IOFileFilter NOT_SVN = FileFilterUtils.makeSVNAware(null);

    // -----------------------------------------------------------------------
    /**
     * Test Filtering
     */
    @Test
    public void testFilter() {
        final List<File> results = new DirectoryWalkerTestCase.TestFileFinder(DirectoryWalkerTestCase.dirsAndFilesFilter, (-1)).find(DirectoryWalkerTestCase.javaDir);
        // assert equals String{"Result Size"} to DirectoryWalkerTestCase{}
        assertEquals("Result Size", (((1 + (DirectoryWalkerTestCase.dirs.length)) + (DirectoryWalkerTestCase.ioFiles.length)) + (DirectoryWalkerTestCase.outputFiles.length)), results.size());
        // assert true String{"Start Dir"} to DirectoryWalkerTestCase{}
        assertTrue("Start Dir", results.contains(DirectoryWalkerTestCase.javaDir));
        // check contains String{"Dir"} to DirectoryWalkerTestCase{}
        checkContainsFiles("Dir", DirectoryWalkerTestCase.dirs, results);
        // check contains String{"IO File"} to DirectoryWalkerTestCase{}
        checkContainsFiles("IO File", DirectoryWalkerTestCase.ioFiles, results);
        // check contains String{"Output File"} to DirectoryWalkerTestCase{}
        checkContainsFiles("Output File", DirectoryWalkerTestCase.outputFiles, results);
    }

    /**
     * Test Filtering and limit to depth 0
     */
    @Test
    public void testFilterAndLimitA() {
        final List<File> results = new DirectoryWalkerTestCase.TestFileFinder(DirectoryWalkerTestCase.NOT_SVN, 0).find(DirectoryWalkerTestCase.javaDir);
        // assert equals String{"[A] Result Size"} to DirectoryWalkerTestCase{}
        assertEquals("[A] Result Size", 1, results.size());
        // assert true String{"[A] Start Dir"} to DirectoryWalkerTestCase{}
        assertTrue("[A] Start Dir", results.contains(DirectoryWalkerTestCase.javaDir));
    }

    /**
     * Test Filtering and limit to depth 1
     */
    @Test
    public void testFilterAndLimitB() {
        final List<File> results = new DirectoryWalkerTestCase.TestFileFinder(DirectoryWalkerTestCase.NOT_SVN, 1).find(DirectoryWalkerTestCase.javaDir);
        // assert equals String{"[B] Result Size"} to DirectoryWalkerTestCase{}
        assertEquals("[B] Result Size", 2, results.size());
        // assert true String{"[B] Start Dir"} to DirectoryWalkerTestCase{}
        assertTrue("[B] Start Dir", results.contains(DirectoryWalkerTestCase.javaDir));
        // assert true String{"[B] Org Dir"} to DirectoryWalkerTestCase{}
        assertTrue("[B] Org Dir", results.contains(DirectoryWalkerTestCase.orgDir));
    }

    /**
     * Test Filtering and limit to depth 3
     */
    @Test
    public void testFilterAndLimitC() {
        final List<File> results = new DirectoryWalkerTestCase.TestFileFinder(DirectoryWalkerTestCase.NOT_SVN, 3).find(DirectoryWalkerTestCase.javaDir);
        // assert equals String{"[C] Result Size"} to DirectoryWalkerTestCase{}
        assertEquals("[C] Result Size", 4, results.size());
        // assert true String{"[C] Start Dir"} to DirectoryWalkerTestCase{}
        assertTrue("[C] Start Dir", results.contains(DirectoryWalkerTestCase.javaDir));
        // assert true String{"[C] Org Dir"} to DirectoryWalkerTestCase{}
        assertTrue("[C] Org Dir", results.contains(DirectoryWalkerTestCase.orgDir));
        // assert true String{"[C] Apache Dir"} to DirectoryWalkerTestCase{}
        assertTrue("[C] Apache Dir", results.contains(DirectoryWalkerTestCase.apacheDir));
        // assert true String{"[C] Commons Dir"} to DirectoryWalkerTestCase{}
        assertTrue("[C] Commons Dir", results.contains(DirectoryWalkerTestCase.commonsDir));
    }

    /**
     * Test Filtering and limit to depth 5
     */
    @Test
    public void testFilterAndLimitD() {
        final List<File> results = new DirectoryWalkerTestCase.TestFileFinder(DirectoryWalkerTestCase.dirsAndFilesFilter, 5).find(DirectoryWalkerTestCase.javaDir);
        // assert equals String{"[D] Result Size"} to DirectoryWalkerTestCase{}
        assertEquals("[D] Result Size", ((1 + (DirectoryWalkerTestCase.dirs.length)) + (DirectoryWalkerTestCase.ioFiles.length)), results.size());
        // assert true String{"[D] Start Dir"} to DirectoryWalkerTestCase{}
        assertTrue("[D] Start Dir", results.contains(DirectoryWalkerTestCase.javaDir));
        // check contains String{"[D] Dir"} to DirectoryWalkerTestCase{}
        checkContainsFiles("[D] Dir", DirectoryWalkerTestCase.dirs, results);
        // check contains String{"[D] File"} to DirectoryWalkerTestCase{}
        checkContainsFiles("[D] File", DirectoryWalkerTestCase.ioFiles, results);
    }

    /**
     * Test separate dir and file filters
     */
    @Test
    public void testFilterDirAndFile1() {
        final List<File> results = new DirectoryWalkerTestCase.TestFileFinder(DirectoryWalkerTestCase.dirsFilter, DirectoryWalkerTestCase.iofilesFilter, (-1)).find(DirectoryWalkerTestCase.javaDir);
        // assert equals String{"[DirAndFile1] Result Size"} to DirectoryWalkerTestCase{}
        assertEquals("[DirAndFile1] Result Size", ((1 + (DirectoryWalkerTestCase.dirs.length)) + (DirectoryWalkerTestCase.ioFiles.length)), results.size());
        // assert true String{"[DirAndFile1] Start Dir"} to DirectoryWalkerTestCase{}
        assertTrue("[DirAndFile1] Start Dir", results.contains(DirectoryWalkerTestCase.javaDir));
        // check contains String{"[DirAndFile1] Dir"} to DirectoryWalkerTestCase{}
        checkContainsFiles("[DirAndFile1] Dir", DirectoryWalkerTestCase.dirs, results);
        // check contains String{"[DirAndFile1] File"} to DirectoryWalkerTestCase{}
        checkContainsFiles("[DirAndFile1] File", DirectoryWalkerTestCase.ioFiles, results);
    }

    /**
     * Test separate dir and file filters
     */
    @Test
    public void testFilterDirAndFile2() {
        final List<File> results = new DirectoryWalkerTestCase.TestFileFinder(null, null, (-1)).find(DirectoryWalkerTestCase.javaDir);
        // assert true String{"[DirAndFile2] Result Size"} to DirectoryWalkerTestCase{}
        assertTrue("[DirAndFile2] Result Size", ((results.size()) > ((1 + (DirectoryWalkerTestCase.dirs.length)) + (DirectoryWalkerTestCase.ioFiles.length))));
        // assert true String{"[DirAndFile2] Start Dir"} to DirectoryWalkerTestCase{}
        assertTrue("[DirAndFile2] Start Dir", results.contains(DirectoryWalkerTestCase.javaDir));
        // check contains String{"[DirAndFile2] Dir"} to DirectoryWalkerTestCase{}
        checkContainsFiles("[DirAndFile2] Dir", DirectoryWalkerTestCase.dirs, results);
        // check contains String{"[DirAndFile2] File"} to DirectoryWalkerTestCase{}
        checkContainsFiles("[DirAndFile2] File", DirectoryWalkerTestCase.ioFiles, results);
    }

    /**
     * Test separate dir and file filters
     */
    @Test
    public void testFilterDirAndFile3() {
        final List<File> results = new DirectoryWalkerTestCase.TestFileFinder(DirectoryWalkerTestCase.dirsFilter, null, (-1)).find(DirectoryWalkerTestCase.javaDir);
        final List<File> resultDirs = directoriesOnly(results);
        // assert equals String{"[DirAndFile3] Result Size"} to DirectoryWalkerTestCase{}
        assertEquals("[DirAndFile3] Result Size", (1 + (DirectoryWalkerTestCase.dirs.length)), resultDirs.size());
        // assert true String{"[DirAndFile3] Start Dir"} to DirectoryWalkerTestCase{}
        assertTrue("[DirAndFile3] Start Dir", results.contains(DirectoryWalkerTestCase.javaDir));
        // check contains String{"[DirAndFile3] Dir"} to DirectoryWalkerTestCase{}
        checkContainsFiles("[DirAndFile3] Dir", DirectoryWalkerTestCase.dirs, resultDirs);
    }

    /**
     * Test separate dir and file filters
     */
    @Test
    public void testFilterDirAndFile4() {
        final List<File> results = new DirectoryWalkerTestCase.TestFileFinder(null, DirectoryWalkerTestCase.iofilesFilter, (-1)).find(DirectoryWalkerTestCase.javaDir);
        final List<File> resultFiles = filesOnly(results);
        // assert equals String{"[DirAndFile4] Result Size"} to DirectoryWalkerTestCase{}
        assertEquals("[DirAndFile4] Result Size", DirectoryWalkerTestCase.ioFiles.length, resultFiles.size());
        // assert true String{"[DirAndFile4] Start Dir"} to DirectoryWalkerTestCase{}
        assertTrue("[DirAndFile4] Start Dir", results.contains(DirectoryWalkerTestCase.javaDir));
        // check contains String{"[DirAndFile4] File"} to DirectoryWalkerTestCase{}
        checkContainsFiles("[DirAndFile4] File", DirectoryWalkerTestCase.ioFiles, resultFiles);
    }

    /**
     * Test Limiting to current directory
     */
    @Test
    public void testLimitToCurrent() {
        final List<File> results = new DirectoryWalkerTestCase.TestFileFinder(null, 0).find(DirectoryWalkerTestCase.current);
        // assert equals String{"Result Size"} to DirectoryWalkerTestCase{}
        assertEquals("Result Size", 1, results.size());
        // assert true String{"Current Dir"} to DirectoryWalkerTestCase{}
        assertTrue("Current Dir", results.contains(new File(".")));
    }

    /**
     * test an invalid start directory
     */
    @Test
    public void testMissingStartDirectory() {
        // TODO is this what we want with invalid directory?
        final File invalidDir = new File("invalid-dir");
        final List<File> results = new DirectoryWalkerTestCase.TestFileFinder(null, (-1)).find(invalidDir);
        // assert equals String{"Result Size"} to DirectoryWalkerTestCase{}
        assertEquals("Result Size", 1, results.size());
        // assert true String{"Current Dir"} to DirectoryWalkerTestCase{}
        assertTrue("Current Dir", results.contains(invalidDir));
        try {
            new DirectoryWalkerTestCase.TestFileFinder(null, (-1)).find(null);
            fail("Null start directory didn't throw Exception");
        } catch (final NullPointerException ignore) {
            // expected result
        }
    }

    /**
     * test an invalid start directory
     */
    @Test
    public void testHandleStartDirectoryFalse() {
        final List<File> results = new DirectoryWalkerTestCase.TestFalseFileFinder(null, (-1)).find(DirectoryWalkerTestCase.current);
        // assert equals String{"Result Size"} to DirectoryWalkerTestCase{}
        assertEquals("Result Size", 0, results.size());
    }

    // ------------ Convenience Test Methods ------------------------------------
    /**
     * Check the files in the array are in the results list.
     */
    private void checkContainsFiles(final String prefix, final File[] files, final Collection<File> results) {
        for (int i = 0; i < (files.length); i++) {
            assertTrue(((((prefix + "[") + i) + "] ") + (files[i])), results.contains(files[i]));
        }
    }

    private void checkContainsString(final String prefix, final File[] files, final Collection<String> results) {
        for (int i = 0; i < (files.length); i++) {
            assertTrue(((((prefix + "[") + i) + "] ") + (files[i])), results.contains(files[i].toString()));
        }
    }

    /**
     * Extract the directories.
     */
    private List<File> directoriesOnly(final Collection<File> results) {
        final List<File> list = new ArrayList<File>(results.size());
        for (final File file : results) {
            if (file.isDirectory()) {
                list.add(file);
            }
        }
        return list;
    }

    /**
     * Extract the files.
     */
    private List<File> filesOnly(final Collection<File> results) {
        final List<File> list = new ArrayList<File>(results.size());
        for (final File file : results) {
            if (file.isFile()) {
                list.add(file);
            }
        }
        return list;
    }

    /**
     * Create an name filter containg the names of the files
     * in the array.
     */
    private static IOFileFilter createNameFilter(final File[] files) {
        final String[] names = new String[files.length];
        for (int i = 0; i < (files.length); i++) {
            names[i] = files[i].getName();
        }
        return new NameFileFilter(names);
    }

    /**
     * Test Cancel
     */
    @Test
    public void testCancel() {
        String cancelName = null;
        // Cancel on a file
        try {
            cancelName = "DirectoryWalker.java";
            new DirectoryWalkerTestCase.TestCancelWalker(cancelName, false).find(DirectoryWalkerTestCase.javaDir);
            fail((("CancelException not thrown for '" + cancelName) + "'"));
        } catch (final DirectoryWalker.CancelException cancel) {
            assertEquals(("File:  " + cancelName), cancelName, cancel.getFile().getName());
            assertEquals(("Depth: " + cancelName), 5, cancel.getDepth());
        } catch (final IOException ex) {
            fail(((("IOException: " + cancelName) + " ") + ex));
        }
        // Cancel on a directory
        try {
            cancelName = "commons";
            new DirectoryWalkerTestCase.TestCancelWalker(cancelName, false).find(DirectoryWalkerTestCase.javaDir);
            fail((("CancelException not thrown for '" + cancelName) + "'"));
        } catch (final DirectoryWalker.CancelException cancel) {
            assertEquals(("File:  " + cancelName), cancelName, cancel.getFile().getName());
            assertEquals(("Depth: " + cancelName), 3, cancel.getDepth());
        } catch (final IOException ex) {
            fail(((("IOException: " + cancelName) + " ") + ex));
        }
        // Suppress CancelException (use same file name as preceding test)
        try {
            final List<File> results = new DirectoryWalkerTestCase.TestCancelWalker(cancelName, true).find(DirectoryWalkerTestCase.javaDir);
            final File lastFile = results.get(((results.size()) - 1));
            assertEquals(("Suppress:  " + cancelName), cancelName, lastFile.getName());
        } catch (final IOException ex) {
            fail(("Suppress threw " + ex));
        }
    }

    /**
     * Test Cancel
     */
    @Test
    public void testMultiThreadCancel() {
        String cancelName = "DirectoryWalker.java";
        DirectoryWalkerTestCase.TestMultiThreadCancelWalker walker = new DirectoryWalkerTestCase.TestMultiThreadCancelWalker(cancelName, false);
        // Cancel on a file
        try {
            walker.find(DirectoryWalkerTestCase.javaDir);
            fail((("CancelException not thrown for '" + cancelName) + "'"));
        } catch (final DirectoryWalker.CancelException cancel) {
            final File last = walker.results.get(((walker.results.size()) - 1));
            assertEquals(cancelName, last.getName());
            assertEquals(("Depth: " + cancelName), 5, cancel.getDepth());
        } catch (final IOException ex) {
            fail(((("IOException: " + cancelName) + " ") + ex));
        }
        // Cancel on a directory
        try {
            cancelName = "commons";
            walker = new DirectoryWalkerTestCase.TestMultiThreadCancelWalker(cancelName, false);
            walker.find(DirectoryWalkerTestCase.javaDir);
            fail((("CancelException not thrown for '" + cancelName) + "'"));
        } catch (final DirectoryWalker.CancelException cancel) {
            assertEquals(("File:  " + cancelName), cancelName, cancel.getFile().getName());
            assertEquals(("Depth: " + cancelName), 3, cancel.getDepth());
        } catch (final IOException ex) {
            fail(((("IOException: " + cancelName) + " ") + ex));
        }
        // Suppress CancelException (use same file name as preceding test)
        try {
            walker = new DirectoryWalkerTestCase.TestMultiThreadCancelWalker(cancelName, true);
            final List<File> results = walker.find(DirectoryWalkerTestCase.javaDir);
            final File lastFile = results.get(((results.size()) - 1));
            assertEquals(("Suppress:  " + cancelName), cancelName, lastFile.getName());
        } catch (final IOException ex) {
            fail(("Suppress threw " + ex));
        }
    }

    /**
     * Test Filtering
     */
    @Test
    public void testFilterString() {
        final List<String> results = new DirectoryWalkerTestCase.TestFileFinderString(DirectoryWalkerTestCase.dirsAndFilesFilter, (-1)).find(DirectoryWalkerTestCase.javaDir);
        // assert equals String{"Result Size"} to DirectoryWalkerTestCase{}
        assertEquals("Result Size", ((DirectoryWalkerTestCase.outputFiles.length) + (DirectoryWalkerTestCase.ioFiles.length)), results.size());
        // check contains String{"IO File"} to DirectoryWalkerTestCase{}
        checkContainsString("IO File", DirectoryWalkerTestCase.ioFiles, results);
        // check contains String{"Output File"} to DirectoryWalkerTestCase{}
        checkContainsString("Output File", DirectoryWalkerTestCase.outputFiles, results);
    }

    // ------------ Test DirectoryWalker implementation --------------------------
    /**
     * Test DirectoryWalker implementation that finds files in a directory hierarchy
     * applying a file filter.
     */
    private static class TestFileFinder extends DirectoryWalker<File> {
        protected TestFileFinder(final FileFilter filter, final int depthLimit) {
            super(filter, depthLimit);
        }

        protected TestFileFinder(final IOFileFilter dirFilter, final IOFileFilter fileFilter, final int depthLimit) {
            super(dirFilter, fileFilter, depthLimit);
        }

        /**
         * * find files.
         */
        protected List<File> find(final File startDirectory) {
            final List<File> results = new ArrayList<File>();
            try {
                walk(startDirectory, results);
            } catch (final IOException ex) {
                Assert.fail(ex.toString());
            }
            return results;
        }

        /**
         * * Handles a directory end by adding the File to the result set.
         */
        @Override
        protected void handleDirectoryEnd(final File directory, final int depth, final Collection<File> results) {
            // add File{directory} to Collection{results}
            results.add(directory);
        }

        /**
         * * Handles a file by adding the File to the result set.
         */
        @Override
        protected void handleFile(final File file, final int depth, final Collection<File> results) {
            // add File{file} to Collection{results}
            results.add(file);
        }
    }

    // ------------ Test DirectoryWalker implementation --------------------------
    /**
     * Test DirectoryWalker implementation that always returns false
     * from handleDirectoryStart()
     */
    private static class TestFalseFileFinder extends DirectoryWalkerTestCase.TestFileFinder {
        protected TestFalseFileFinder(final FileFilter filter, final int depthLimit) {
            super(filter, depthLimit);
        }

        /**
         * * Always returns false.
         */
        @Override
        protected boolean handleDirectory(final File directory, final int depth, final Collection<File> results) {
            return false;
        }
    }

    // ------------ Test DirectoryWalker implementation --------------------------
    /**
     * Test DirectoryWalker implementation that finds files in a directory hierarchy
     * applying a file filter.
     */
    static class TestCancelWalker extends DirectoryWalker<File> {
        private final String cancelFileName;

        private final boolean suppressCancel;

        TestCancelWalker(final String cancelFileName, final boolean suppressCancel) {
            super();
            this.cancelFileName = cancelFileName;
            this.suppressCancel = suppressCancel;
        }

        /**
         * * find files.
         */
        protected List<File> find(final File startDirectory) throws IOException {
            final List<File> results = new ArrayList<File>();
            // walk File{startDirectory} to TestCancelWalker{}
            walk(startDirectory, results);
            return results;
        }

        /**
         * * Handles a directory end by adding the File to the result set.
         */
        @Override
        protected void handleDirectoryEnd(final File directory, final int depth, final Collection<File> results) throws IOException {
            // add File{directory} to Collection{results}
            results.add(directory);
            if (cancelFileName.equals(directory.getName())) {
                throw new CancelException(directory, depth);
            }
        }

        /**
         * * Handles a file by adding the File to the result set.
         */
        @Override
        protected void handleFile(final File file, final int depth, final Collection<File> results) throws IOException {
            // add File{file} to Collection{results}
            results.add(file);
            if (cancelFileName.equals(file.getName())) {
                throw new CancelException(file, depth);
            }
        }

        /**
         * * Handles Cancel.
         */
        @Override
        protected void handleCancelled(final File startDirectory, final Collection<File> results, final CancelException cancel) throws IOException {
            if (!(suppressCancel)) {
                super.handleCancelled(startDirectory, results, cancel);
            }
        }
    }

    /**
     * Test DirectoryWalker implementation that finds files in a directory hierarchy
     * applying a file filter.
     */
    static class TestMultiThreadCancelWalker extends DirectoryWalker<File> {
        private final String cancelFileName;

        private final boolean suppressCancel;

        private boolean cancelled;

        public List<File> results;

        TestMultiThreadCancelWalker(final String cancelFileName, final boolean suppressCancel) {
            super();
            this.cancelFileName = cancelFileName;
            this.suppressCancel = suppressCancel;
        }

        /**
         * * find files.
         */
        protected List<File> find(final File startDirectory) throws IOException {
            results = new ArrayList<File>();
            // walk File{startDirectory} to TestMultiThreadCancelWalker{}
            walk(startDirectory, results);
            return results;
        }

        /**
         * * Handles a directory end by adding the File to the result set.
         */
        @Override
        protected void handleDirectoryEnd(final File directory, final int depth, final Collection<File> results) throws IOException {
            // add File{directory} to Collection{results}
            results.add(directory);
            // assert false boolean{cancelled} to TestMultiThreadCancelWalker{}
            assertFalse(cancelled);
            if (cancelFileName.equals(directory.getName())) {
                cancelled = true;
            }
        }

        /**
         * * Handles a file by adding the File to the result set.
         */
        @Override
        protected void handleFile(final File file, final int depth, final Collection<File> results) throws IOException {
            // add File{file} to Collection{results}
            results.add(file);
            // assert false boolean{cancelled} to TestMultiThreadCancelWalker{}
            assertFalse(cancelled);
            if (cancelFileName.equals(file.getName())) {
                cancelled = true;
            }
        }

        /**
         * * Handles Cancelled.
         */
        @Override
        protected boolean handleIsCancelled(final File file, final int depth, final Collection<File> results) throws IOException {
            return cancelled;
        }

        /**
         * * Handles Cancel.
         */
        @Override
        protected void handleCancelled(final File startDirectory, final Collection<File> results, final CancelException cancel) throws IOException {
            if (!(suppressCancel)) {
                super.handleCancelled(startDirectory, results, cancel);
            }
        }
    }

    /**
     * Test DirectoryWalker implementation that finds files in a directory hierarchy
     * applying a file filter.
     */
    private static class TestFileFinderString extends DirectoryWalker<String> {
        protected TestFileFinderString(final FileFilter filter, final int depthLimit) {
            super(filter, depthLimit);
        }

        /**
         * * find files.
         */
        protected List<String> find(final File startDirectory) {
            final List<String> results = new ArrayList<String>();
            try {
                walk(startDirectory, results);
            } catch (final IOException ex) {
                Assert.fail(ex.toString());
            }
            return results;
        }

        /**
         * * Handles a file by adding the File to the result set.
         */
        @Override
        protected void handleFile(final File file, final int depth, final Collection<String> results) {
            // add String{file.toString()} to Collection{results}
            results.add(file.toString());
        }
    }
}

