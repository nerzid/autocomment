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
import java.io.File;
import org.apache.commons.io.testtools.FileBasedTestCase;
import java.io.IOException;
import java.util.List;
import org.junit.Test;

/**
 * Test cases for FileUtils.cleanDirectory() method.
 * 
 * @version $Id: FileUtilsCleanDirectoryTestCase.java 1718944 2015-12-09 19:50:30Z krosenvold $
 */
public class FileUtilsCleanDirectoryTestCase extends FileBasedTestCase {
    final File top = getLocalTestDirectory();

    private File getLocalTestDirectory() {
        return new File(getTestDirectory(), "list-files");
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        top.mkdirs();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        chmod(top, 775, true);
        FileUtils.deleteDirectory(top);
    }

    // -----------------------------------------------------------------------
    @Test
    public void testCleanEmpty() throws Exception {
        Assert.assertEquals(0, top.list().length);
        FileUtils.cleanDirectory(top);
        Assert.assertEquals(0, top.list().length);
    }

    @Test
    public void testDeletesRegular() throws Exception {
        FileUtils.touch(new File(top, "regular"));
        FileUtils.touch(new File(top, ".hidden"));
        Assert.assertEquals(2, top.list().length);
        FileUtils.cleanDirectory(top);
        Assert.assertEquals(0, top.list().length);
    }

    @Test
    public void testDeletesNested() throws Exception {
        final File nested = new File(top, "nested");
        Assert.assertTrue(nested.mkdirs());
        FileUtils.touch(new File(nested, "file"));
        Assert.assertEquals(1, top.list().length);
        FileUtils.cleanDirectory(top);
        Assert.assertEquals(0, top.list().length);
    }

    @Test
    public void testThrowsOnNullList() throws Exception {
        if ((System.getProperty("os.name").startsWith("Win")) || (!(chmod(top, 0, false)))) {
            // test wont work if we can't restrict permissions on the
            // directory, so skip it.
            return ;
        } 
        try {
            FileUtils.cleanDirectory(top);
            Assert.fail("expected IOException");
        } catch (final IOException e) {
            Assert.assertEquals(("Failed to list contents of " + (top.getAbsolutePath())), e.getMessage());
        }
    }

    @Test
    public void testThrowsOnCannotDeleteFile() throws Exception {
        final File file = new File(top, "restricted");
        FileUtils.touch(file);
        if ((System.getProperty("os.name").startsWith("Win")) || (!(chmod(top, 500, false)))) {
            // test wont work if we can't restrict permissions on the
            // directory, so skip it.
            return ;
        } 
        try {
            FileUtils.cleanDirectory(top);
            Assert.fail("expected IOException");
        } catch (final IOException e) {
            Assert.assertEquals(("Unable to delete file: " + (file.getAbsolutePath())), e.getMessage());
        }
    }

    private boolean chmod(final File file, final int mode, final boolean recurse) throws InterruptedException {
        // TODO: Refactor this to FileSystemUtils
        final List<String> args = new ArrayList<String>();
        args.add("chmod");
        if (recurse) {
            args.add("-R");
        } 
        args.add(Integer.toString(mode));
        args.add(file.getAbsolutePath());
        Process proc;
        try {
            proc = Runtime.getRuntime().exec(args.toArray(new String[args.size()]));
        } catch (final IOException e) {
            return false;
        }
        final int result = proc.waitFor();
        return result == 0;
    }
}

