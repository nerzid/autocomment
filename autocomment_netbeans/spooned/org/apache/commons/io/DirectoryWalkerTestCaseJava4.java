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

import java.io.FileFilter;
import java.util.ArrayList;
import org.junit.Assert;
import java.util.Collection;
import java.io.File;
import org.apache.commons.io.filefilter.FileFilterUtils;
import java.io.IOException;
import org.apache.commons.io.filefilter.IOFileFilter;
import java.util.List;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.junit.Test;

/**
 * This is used to test DirectoryWalker for correctness when using Java4 (i.e. no generics).
 *
 * @version $Id: DirectoryWalkerTestCaseJava4.java 1718944 2015-12-09 19:50:30Z krosenvold $
 * @see DirectoryWalker
 */
// Java4
@SuppressWarnings(value = { "unchecked" , "rawtypes" })
public class DirectoryWalkerTestCaseJava4 {
    // Directories
    private static final File current = new File(".");

    private static final File javaDir = new File("src/main/java");

    private static final File orgDir = new File(DirectoryWalkerTestCaseJava4.javaDir, "org");

    private static final File apacheDir = new File(DirectoryWalkerTestCaseJava4.orgDir, "apache");

    private static final File commonsDir = new File(DirectoryWalkerTestCaseJava4.apacheDir, "commons");

    private static final File ioDir = new File(DirectoryWalkerTestCaseJava4.commonsDir, "io");

    private static final File outputDir = new File(DirectoryWalkerTestCaseJava4.ioDir, "output");

    private static final File[] dirs = new File[]{ DirectoryWalkerTestCaseJava4.orgDir , DirectoryWalkerTestCaseJava4.apacheDir , DirectoryWalkerTestCaseJava4.commonsDir , DirectoryWalkerTestCaseJava4.ioDir , DirectoryWalkerTestCaseJava4.outputDir };

    // Files
    private static final File filenameUtils = new File(DirectoryWalkerTestCaseJava4.ioDir, "FilenameUtils.java");

    private static final File ioUtils = new File(DirectoryWalkerTestCaseJava4.ioDir, "IOUtils.java");

    private static final File proxyWriter = new File(DirectoryWalkerTestCaseJava4.outputDir, "ProxyWriter.java");

    private static final File nullStream = new File(DirectoryWalkerTestCaseJava4.outputDir, "NullOutputStream.java");

    private static final File[] ioFiles = new File[]{ DirectoryWalkerTestCaseJava4.filenameUtils , DirectoryWalkerTestCaseJava4.ioUtils };

    private static final File[] outputFiles = new File[]{ DirectoryWalkerTestCaseJava4.proxyWriter , DirectoryWalkerTestCaseJava4.nullStream };

    // Filters
    private static final IOFileFilter dirsFilter = DirectoryWalkerTestCaseJava4.createNameFilter(DirectoryWalkerTestCaseJava4.dirs);

    private static final IOFileFilter iofilesFilter = DirectoryWalkerTestCaseJava4.createNameFilter(DirectoryWalkerTestCaseJava4.ioFiles);

    private static final IOFileFilter outputFilesFilter = DirectoryWalkerTestCaseJava4.createNameFilter(DirectoryWalkerTestCaseJava4.outputFiles);

    private static final IOFileFilter ioDirAndFilesFilter = new OrFileFilter(DirectoryWalkerTestCaseJava4.dirsFilter, DirectoryWalkerTestCaseJava4.iofilesFilter);

    private static final IOFileFilter dirsAndFilesFilter = new OrFileFilter(DirectoryWalkerTestCaseJava4.ioDirAndFilesFilter, DirectoryWalkerTestCaseJava4.outputFilesFilter);

    // Filter to exclude SVN files
    private static final IOFileFilter NOT_SVN = FileFilterUtils.makeSVNAware(null);

    // -----------------------------------------------------------------------
    /**
     * Test Filtering
     */
    @Test
    public void testFilter() {
        final List<File> results = new DirectoryWalkerTestCaseJava4.TestFileFinder(DirectoryWalkerTestCaseJava4.dirsAndFilesFilter, (-1)).find(DirectoryWalkerTestCaseJava4.javaDir);
        // assert equals String{"Result Size"} to void{Assert}
        Assert.assertEquals("Result Size", (((1 + (DirectoryWalkerTestCaseJava4.dirs.length)) + (DirectoryWalkerTestCaseJava4.ioFiles.length)) + (DirectoryWalkerTestCaseJava4.outputFiles.length)), results.size());
        // assert true String{"Start Dir"} to void{Assert}
        Assert.assertTrue("Start Dir", results.contains(DirectoryWalkerTestCaseJava4.javaDir));
        // check contains String{"Dir"} to DirectoryWalkerTestCaseJava4{}
        checkContainsFiles("Dir", DirectoryWalkerTestCaseJava4.dirs, results);
        // check contains String{"IO File"} to DirectoryWalkerTestCaseJava4{}
        checkContainsFiles("IO File", DirectoryWalkerTestCaseJava4.ioFiles, results);
        // check contains String{"Output File"} to DirectoryWalkerTestCaseJava4{}
        checkContainsFiles("Output File", DirectoryWalkerTestCaseJava4.outputFiles, results);
    }

    /**
     * Test Filtering and limit to depth 0
     */
    @Test
    public void testFilterAndLimitA() {
        final List<File> results = new DirectoryWalkerTestCaseJava4.TestFileFinder(DirectoryWalkerTestCaseJava4.NOT_SVN, 0).find(DirectoryWalkerTestCaseJava4.javaDir);
        // assert equals String{"[A] Result Size"} to void{Assert}
        Assert.assertEquals("[A] Result Size", 1, results.size());
        // assert true String{"[A] Start Dir"} to void{Assert}
        Assert.assertTrue("[A] Start Dir", results.contains(DirectoryWalkerTestCaseJava4.javaDir));
    }

    /**
     * Test Filtering and limit to depth 1
     */
    @Test
    public void testFilterAndLimitB() {
        final List<File> results = new DirectoryWalkerTestCaseJava4.TestFileFinder(DirectoryWalkerTestCaseJava4.NOT_SVN, 1).find(DirectoryWalkerTestCaseJava4.javaDir);
        // assert equals String{"[B] Result Size"} to void{Assert}
        Assert.assertEquals("[B] Result Size", 2, results.size());
        // assert true String{"[B] Start Dir"} to void{Assert}
        Assert.assertTrue("[B] Start Dir", results.contains(DirectoryWalkerTestCaseJava4.javaDir));
        // assert true String{"[B] Org Dir"} to void{Assert}
        Assert.assertTrue("[B] Org Dir", results.contains(DirectoryWalkerTestCaseJava4.orgDir));
    }

    /**
     * Test Filtering and limit to depth 3
     */
    @Test
    public void testFilterAndLimitC() {
        final List<File> results = new DirectoryWalkerTestCaseJava4.TestFileFinder(DirectoryWalkerTestCaseJava4.NOT_SVN, 3).find(DirectoryWalkerTestCaseJava4.javaDir);
        // assert equals String{"[C] Result Size"} to void{Assert}
        Assert.assertEquals("[C] Result Size", 4, results.size());
        // assert true String{"[C] Start Dir"} to void{Assert}
        Assert.assertTrue("[C] Start Dir", results.contains(DirectoryWalkerTestCaseJava4.javaDir));
        // assert true String{"[C] Org Dir"} to void{Assert}
        Assert.assertTrue("[C] Org Dir", results.contains(DirectoryWalkerTestCaseJava4.orgDir));
        // assert true String{"[C] Apache Dir"} to void{Assert}
        Assert.assertTrue("[C] Apache Dir", results.contains(DirectoryWalkerTestCaseJava4.apacheDir));
        // assert true String{"[C] Commons Dir"} to void{Assert}
        Assert.assertTrue("[C] Commons Dir", results.contains(DirectoryWalkerTestCaseJava4.commonsDir));
    }

    /**
     * Test Filtering and limit to depth 5
     */
    @Test
    public void testFilterAndLimitD() {
        final List<File> results = new DirectoryWalkerTestCaseJava4.TestFileFinder(DirectoryWalkerTestCaseJava4.dirsAndFilesFilter, 5).find(DirectoryWalkerTestCaseJava4.javaDir);
        // assert equals String{"[D] Result Size"} to void{Assert}
        Assert.assertEquals("[D] Result Size", ((1 + (DirectoryWalkerTestCaseJava4.dirs.length)) + (DirectoryWalkerTestCaseJava4.ioFiles.length)), results.size());
        // assert true String{"[D] Start Dir"} to void{Assert}
        Assert.assertTrue("[D] Start Dir", results.contains(DirectoryWalkerTestCaseJava4.javaDir));
        // check contains String{"[D] Dir"} to DirectoryWalkerTestCaseJava4{}
        checkContainsFiles("[D] Dir", DirectoryWalkerTestCaseJava4.dirs, results);
        // check contains String{"[D] File"} to DirectoryWalkerTestCaseJava4{}
        checkContainsFiles("[D] File", DirectoryWalkerTestCaseJava4.ioFiles, results);
    }

    /**
     * Test separate dir and file filters
     */
    @Test
    public void testFilterDirAndFile1() {
        final List<File> results = new DirectoryWalkerTestCaseJava4.TestFileFinder(DirectoryWalkerTestCaseJava4.dirsFilter, DirectoryWalkerTestCaseJava4.iofilesFilter, (-1)).find(DirectoryWalkerTestCaseJava4.javaDir);
        // assert equals String{"[DirAndFile1] Result Size"} to void{Assert}
        Assert.assertEquals("[DirAndFile1] Result Size", ((1 + (DirectoryWalkerTestCaseJava4.dirs.length)) + (DirectoryWalkerTestCaseJava4.ioFiles.length)), results.size());
        // assert true String{"[DirAndFile1] Start Dir"} to void{Assert}
        Assert.assertTrue("[DirAndFile1] Start Dir", results.contains(DirectoryWalkerTestCaseJava4.javaDir));
        // check contains String{"[DirAndFile1] Dir"} to DirectoryWalkerTestCaseJava4{}
        checkContainsFiles("[DirAndFile1] Dir", DirectoryWalkerTestCaseJava4.dirs, results);
        // check contains String{"[DirAndFile1] File"} to DirectoryWalkerTestCaseJava4{}
        checkContainsFiles("[DirAndFile1] File", DirectoryWalkerTestCaseJava4.ioFiles, results);
    }

    /**
     * Test separate dir and file filters
     */
    @Test
    public void testFilterDirAndFile2() {
        final List<File> results = new DirectoryWalkerTestCaseJava4.TestFileFinder(null, null, (-1)).find(DirectoryWalkerTestCaseJava4.javaDir);
        // assert true String{"[DirAndFile2] Result Size"} to void{Assert}
        Assert.assertTrue("[DirAndFile2] Result Size", ((results.size()) > ((1 + (DirectoryWalkerTestCaseJava4.dirs.length)) + (DirectoryWalkerTestCaseJava4.ioFiles.length))));
        // assert true String{"[DirAndFile2] Start Dir"} to void{Assert}
        Assert.assertTrue("[DirAndFile2] Start Dir", results.contains(DirectoryWalkerTestCaseJava4.javaDir));
        // check contains String{"[DirAndFile2] Dir"} to DirectoryWalkerTestCaseJava4{}
        checkContainsFiles("[DirAndFile2] Dir", DirectoryWalkerTestCaseJava4.dirs, results);
        // check contains String{"[DirAndFile2] File"} to DirectoryWalkerTestCaseJava4{}
        checkContainsFiles("[DirAndFile2] File", DirectoryWalkerTestCaseJava4.ioFiles, results);
    }

    /**
     * Test separate dir and file filters
     */
    @Test
    public void testFilterDirAndFile3() {
        final List<File> results = new DirectoryWalkerTestCaseJava4.TestFileFinder(DirectoryWalkerTestCaseJava4.dirsFilter, null, (-1)).find(DirectoryWalkerTestCaseJava4.javaDir);
        final List resultDirs = directoriesOnly(results);
        // assert equals String{"[DirAndFile3] Result Size"} to void{Assert}
        Assert.assertEquals("[DirAndFile3] Result Size", (1 + (DirectoryWalkerTestCaseJava4.dirs.length)), resultDirs.size());
        // assert true String{"[DirAndFile3] Start Dir"} to void{Assert}
        Assert.assertTrue("[DirAndFile3] Start Dir", results.contains(DirectoryWalkerTestCaseJava4.javaDir));
        // check contains String{"[DirAndFile3] Dir"} to DirectoryWalkerTestCaseJava4{}
        checkContainsFiles("[DirAndFile3] Dir", DirectoryWalkerTestCaseJava4.dirs, resultDirs);
    }

    /**
     * Test separate dir and file filters
     */
    @Test
    public void testFilterDirAndFile4() {
        final List<File> results = new DirectoryWalkerTestCaseJava4.TestFileFinder(null, DirectoryWalkerTestCaseJava4.iofilesFilter, (-1)).find(DirectoryWalkerTestCaseJava4.javaDir);
        final List resultFiles = filesOnly(results);
        // assert equals String{"[DirAndFile4] Result Size"} to void{Assert}
        Assert.assertEquals("[DirAndFile4] Result Size", DirectoryWalkerTestCaseJava4.ioFiles.length, resultFiles.size());
        // assert true String{"[DirAndFile4] Start Dir"} to void{Assert}
        Assert.assertTrue("[DirAndFile4] Start Dir", results.contains(DirectoryWalkerTestCaseJava4.javaDir));
        // check contains String{"[DirAndFile4] File"} to DirectoryWalkerTestCaseJava4{}
        checkContainsFiles("[DirAndFile4] File", DirectoryWalkerTestCaseJava4.ioFiles, resultFiles);
    }

    /**
     * Test Limiting to current directory
     */
    @Test
    public void testLimitToCurrent() {
        final List<File> results = new DirectoryWalkerTestCaseJava4.TestFileFinder(null, 0).find(DirectoryWalkerTestCaseJava4.current);
        // assert equals String{"Result Size"} to void{Assert}
        Assert.assertEquals("Result Size", 1, results.size());
        // assert true String{"Current Dir"} to void{Assert}
        Assert.assertTrue("Current Dir", results.contains(new File(".")));
    }

    /**
     * test an invalid start directory
     */
    @Test
    public void testMissingStartDirectory() {
        // TODO is this what we want with invalid directory?
        final File invalidDir = new File("invalid-dir");
        final List<File> results = new DirectoryWalkerTestCaseJava4.TestFileFinder(null, (-1)).find(invalidDir);
        // assert equals String{"Result Size"} to void{Assert}
        Assert.assertEquals("Result Size", 1, results.size());
        // assert true String{"Current Dir"} to void{Assert}
        Assert.assertTrue("Current Dir", results.contains(invalidDir));
        try {
            new DirectoryWalkerTestCaseJava4.TestFileFinder(null, (-1)).find(null);
            Assert.fail("Null start directory didn't throw Exception");
        } catch (final NullPointerException ignore) {
            // expected result
        }
    }

    /**
     * test an invalid start directory
     */
    @Test
    public void testHandleStartDirectoryFalse() {
        final List<File> results = new DirectoryWalkerTestCaseJava4.TestFalseFileFinder(null, (-1)).find(DirectoryWalkerTestCaseJava4.current);
        // assert equals String{"Result Size"} to void{Assert}
        Assert.assertEquals("Result Size", 0, results.size());
    }

    // ------------ Convenience Test Methods ------------------------------------
    /**
     * Check the files in the array are in the results list.
     */
    private void checkContainsFiles(final String prefix, final File[] files, final Collection results) {
        for (int i = 0; i < (files.length); i++) {
            Assert.assertTrue(((((prefix + "[") + i) + "] ") + (files[i])), results.contains(files[i]));
        }
    }

    /**
     * Extract the directories.
     */
    private List directoriesOnly(final Collection<File> results) {
        final List list = new ArrayList(results.size());
        for (File file : results) {
            if (file.isDirectory()) {
                list.add(file);
            }
        }
        return list;
    }

    /**
     * Extract the files.
     */
    private List filesOnly(final Collection<File> results) {
        final List list = new ArrayList(results.size());
        for (File file : results) {
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
            new DirectoryWalkerTestCaseJava4.TestCancelWalker(cancelName, false).find(DirectoryWalkerTestCaseJava4.javaDir);
            Assert.fail((("CancelException not thrown for '" + cancelName) + "'"));
        } catch (final DirectoryWalker.CancelException cancel) {
            Assert.assertEquals(("File:  " + cancelName), cancelName, cancel.getFile().getName());
            Assert.assertEquals(("Depth: " + cancelName), 5, cancel.getDepth());
        } catch (final IOException ex) {
            Assert.fail(((("IOException: " + cancelName) + " ") + ex));
        }
        // Cancel on a directory
        try {
            cancelName = "commons";
            new DirectoryWalkerTestCaseJava4.TestCancelWalker(cancelName, false).find(DirectoryWalkerTestCaseJava4.javaDir);
            Assert.fail((("CancelException not thrown for '" + cancelName) + "'"));
        } catch (final DirectoryWalker.CancelException cancel) {
            Assert.assertEquals(("File:  " + cancelName), cancelName, cancel.getFile().getName());
            Assert.assertEquals(("Depth: " + cancelName), 3, cancel.getDepth());
        } catch (final IOException ex) {
            Assert.fail(((("IOException: " + cancelName) + " ") + ex));
        }
        // Suppress CancelException (use same file name as preceding test)
        try {
            final List results = new DirectoryWalkerTestCaseJava4.TestCancelWalker(cancelName, true).find(DirectoryWalkerTestCaseJava4.javaDir);
            final File lastFile = ((File) (results.get(((results.size()) - 1))));
            Assert.assertEquals(("Suppress:  " + cancelName), cancelName, lastFile.getName());
        } catch (final IOException ex) {
            Assert.fail(("Suppress threw " + ex));
        }
    }

    /**
     * Test Cancel
     */
    @Test
    public void testMultiThreadCancel() {
        String cancelName = "DirectoryWalker.java";
        DirectoryWalkerTestCaseJava4.TestMultiThreadCancelWalker walker = new DirectoryWalkerTestCaseJava4.TestMultiThreadCancelWalker(cancelName, false);
        // Cancel on a file
        try {
            walker.find(DirectoryWalkerTestCaseJava4.javaDir);
            Assert.fail((("CancelException not thrown for '" + cancelName) + "'"));
        } catch (final DirectoryWalker.CancelException cancel) {
            final File last = ((File) (walker.results.get(((walker.results.size()) - 1))));
            Assert.assertEquals(cancelName, last.getName());
            Assert.assertEquals(("Depth: " + cancelName), 5, cancel.getDepth());
        } catch (final IOException ex) {
            Assert.fail(((("IOException: " + cancelName) + " ") + ex));
        }
        // Cancel on a directory
        try {
            cancelName = "commons";
            walker = new DirectoryWalkerTestCaseJava4.TestMultiThreadCancelWalker(cancelName, false);
            walker.find(DirectoryWalkerTestCaseJava4.javaDir);
            Assert.fail((("CancelException not thrown for '" + cancelName) + "'"));
        } catch (final DirectoryWalker.CancelException cancel) {
            Assert.assertEquals(("File:  " + cancelName), cancelName, cancel.getFile().getName());
            Assert.assertEquals(("Depth: " + cancelName), 3, cancel.getDepth());
        } catch (final IOException ex) {
            Assert.fail(((("IOException: " + cancelName) + " ") + ex));
        }
        // Suppress CancelException (use same file name as preceding test)
        try {
            walker = new DirectoryWalkerTestCaseJava4.TestMultiThreadCancelWalker(cancelName, true);
            final List results = walker.find(DirectoryWalkerTestCaseJava4.javaDir);
            final File lastFile = ((File) (results.get(((results.size()) - 1))));
            Assert.assertEquals(("Suppress:  " + cancelName), cancelName, lastFile.getName());
        } catch (final IOException ex) {
            Assert.fail(("Suppress threw " + ex));
        }
    }

    // ------------ Test DirectoryWalker implementation --------------------------
    /**
     * Test DirectoryWalker implementation that finds files in a directory hierarchy
     * applying a file filter.
     */
    private static class TestFileFinder extends DirectoryWalker {
        protected TestFileFinder(final FileFilter filter, final int depthLimit) {
            super(filter, depthLimit);
        }

        protected TestFileFinder(final IOFileFilter dirFilter, final IOFileFilter fileFilter, final int depthLimit) {
            super(dirFilter, fileFilter, depthLimit);
        }

        /**
         * find files.
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
         * Handles a directory end by adding the File to the result set.
         */
        @Override
        protected void handleDirectoryEnd(final File directory, final int depth, final Collection results) {
            // add File{directory} to Collection{results}
            results.add(directory);
        }

        /**
         * Handles a file by adding the File to the result set.
         */
        @Override
        protected void handleFile(final File file, final int depth, final Collection results) {
            // add File{file} to Collection{results}
            results.add(file);
        }
    }

    // ------------ Test DirectoryWalker implementation --------------------------
    /**
     * Test DirectoryWalker implementation that always returns false
     * from handleDirectoryStart()
     */
    private static class TestFalseFileFinder extends DirectoryWalkerTestCaseJava4.TestFileFinder {
        protected TestFalseFileFinder(final FileFilter filter, final int depthLimit) {
            super(filter, depthLimit);
        }

        /**
         * Always returns false.
         */
        @Override
        protected boolean handleDirectory(final File directory, final int depth, final Collection results) {
            return false;
        }
    }

    // ------------ Test DirectoryWalker implementation --------------------------
    /**
     * Test DirectoryWalker implementation that finds files in a directory hierarchy
     * applying a file filter.
     */
    static class TestCancelWalker extends DirectoryWalker {
        private final String cancelFileName;

        private final boolean suppressCancel;

        TestCancelWalker(final String cancelFileName, final boolean suppressCancel) {
            super();
            this.cancelFileName = cancelFileName;
            this.suppressCancel = suppressCancel;
        }

        /**
         * find files.
         */
        protected List find(final File startDirectory) throws IOException {
            final List results = new ArrayList();
            // walk File{startDirectory} to TestCancelWalker{}
            walk(startDirectory, results);
            return results;
        }

        /**
         * Handles a directory end by adding the File to the result set.
         */
        @Override
        protected void handleDirectoryEnd(final File directory, final int depth, final Collection results) throws IOException {
            // add File{directory} to Collection{results}
            results.add(directory);
            if (cancelFileName.equals(directory.getName())) {
                throw new DirectoryWalker.CancelException(directory, depth);
            }
        }

        /**
         * Handles a file by adding the File to the result set.
         */
        @Override
        protected void handleFile(final File file, final int depth, final Collection results) throws IOException {
            // add File{file} to Collection{results}
            results.add(file);
            if (cancelFileName.equals(file.getName())) {
                throw new DirectoryWalker.CancelException(file, depth);
            }
        }

        /**
         * Handles Cancel.
         */
        @Override
        protected void handleCancelled(final File startDirectory, final Collection results, final DirectoryWalker.CancelException cancel) throws IOException {
            if (!(suppressCancel)) {
                super.handleCancelled(startDirectory, results, cancel);
            }
        }
    }

    /**
     * Test DirectoryWalker implementation that finds files in a directory hierarchy
     * applying a file filter.
     */
    static class TestMultiThreadCancelWalker extends DirectoryWalker {
        private final String cancelFileName;

        private final boolean suppressCancel;

        private boolean cancelled;

        public List results;

        TestMultiThreadCancelWalker(final String cancelFileName, final boolean suppressCancel) {
            super();
            this.cancelFileName = cancelFileName;
            this.suppressCancel = suppressCancel;
        }

        /**
         * find files.
         */
        protected List find(final File startDirectory) throws IOException {
            results = new ArrayList();
            // walk File{startDirectory} to TestMultiThreadCancelWalker{}
            walk(startDirectory, results);
            return results;
        }

        /**
         * Handles a directory end by adding the File to the result set.
         */
        @Override
        protected void handleDirectoryEnd(final File directory, final int depth, final Collection results) throws IOException {
            // add File{directory} to Collection{results}
            results.add(directory);
            // assert false boolean{cancelled} to void{Assert}
            Assert.assertFalse(cancelled);
            if (cancelFileName.equals(directory.getName())) {
                cancelled = true;
            }
        }

        /**
         * Handles a file by adding the File to the result set.
         */
        @Override
        protected void handleFile(final File file, final int depth, final Collection results) throws IOException {
            // add File{file} to Collection{results}
            results.add(file);
            // assert false boolean{cancelled} to void{Assert}
            Assert.assertFalse(cancelled);
            if (cancelFileName.equals(file.getName())) {
                cancelled = true;
            }
        }

        /**
         * Handles Cancelled.
         */
        @Override
        protected boolean handleIsCancelled(final File file, final int depth, final Collection results) throws IOException {
            return cancelled;
        }

        /**
         * Handles Cancel.
         */
        @Override
        protected void handleCancelled(final File startDirectory, final Collection results, final DirectoryWalker.CancelException cancel) throws IOException {
            if (!(suppressCancel)) {
                super.handleCancelled(startDirectory, results, cancel);
            }
        }
    }
}

