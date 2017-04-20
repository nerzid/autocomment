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
        // assert true boolean{subDir.mkdir()} to void{Assert}
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
        // assert true boolean{subDir.exists()} to void{Assert}
        Assert.assertTrue(subDir.exists());
        // assert true boolean{subFile.exists()} to void{Assert}
        Assert.assertTrue(subFile.exists());
        // delete dir
        try {
            FileDeleteStrategy.NORMAL.delete(subDir);
            Assert.fail();
        } catch (final IOException ex) {
            // expected
        }
        // assert true boolean{subDir.exists()} to void{Assert}
        Assert.assertTrue(subDir.exists());
        // assert true boolean{subFile.exists()} to void{Assert}
        Assert.assertTrue(subFile.exists());
        // delete file
        // delete File{subFile} to FileDeleteStrategy{FileDeleteStrategy.NORMAL}
        FileDeleteStrategy.NORMAL.delete(subFile);
        // assert true boolean{subDir.exists()} to void{Assert}
        Assert.assertTrue(subDir.exists());
        // assert false boolean{subFile.exists()} to void{Assert}
        Assert.assertFalse(subFile.exists());
        // delete dir
        // delete File{subDir} to FileDeleteStrategy{FileDeleteStrategy.NORMAL}
        FileDeleteStrategy.NORMAL.delete(subDir);
        // assert false boolean{subDir.exists()} to void{Assert}
        Assert.assertFalse(subDir.exists());
        // delete dir
        // delete File{subDir} to FileDeleteStrategy{FileDeleteStrategy.NORMAL}
        FileDeleteStrategy.NORMAL.delete(subDir);// no error
        
        // assert false boolean{subDir.exists()} to void{Assert}
        Assert.assertFalse(subDir.exists());
    }

    @Test
    public void testDeleteQuietlyNormal() throws Exception {
        final File baseDir = getTestDirectory();
        final File subDir = new File(baseDir, "test");
        // assert true boolean{subDir.mkdir()} to void{Assert}
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
        // assert true boolean{subDir.exists()} to void{Assert}
        Assert.assertTrue(subDir.exists());
        // assert true boolean{subFile.exists()} to void{Assert}
        Assert.assertTrue(subFile.exists());
        // delete dir
        // assert false boolean{FileDeleteStrategy.NORMAL.deleteQuietly(subDir)} to void{Assert}
        Assert.assertFalse(FileDeleteStrategy.NORMAL.deleteQuietly(subDir));
        // assert true boolean{subDir.exists()} to void{Assert}
        Assert.assertTrue(subDir.exists());
        // assert true boolean{subFile.exists()} to void{Assert}
        Assert.assertTrue(subFile.exists());
        // delete file
        // assert true boolean{FileDeleteStrategy.NORMAL.deleteQuietly(subFile)} to void{Assert}
        Assert.assertTrue(FileDeleteStrategy.NORMAL.deleteQuietly(subFile));
        // assert true boolean{subDir.exists()} to void{Assert}
        Assert.assertTrue(subDir.exists());
        // assert false boolean{subFile.exists()} to void{Assert}
        Assert.assertFalse(subFile.exists());
        // delete dir
        // assert true boolean{FileDeleteStrategy.NORMAL.deleteQuietly(subDir)} to void{Assert}
        Assert.assertTrue(FileDeleteStrategy.NORMAL.deleteQuietly(subDir));
        // assert false boolean{subDir.exists()} to void{Assert}
        Assert.assertFalse(subDir.exists());
        // delete dir
        // assert true boolean{FileDeleteStrategy.NORMAL.deleteQuietly(subDir)} to void{Assert}
        Assert.assertTrue(FileDeleteStrategy.NORMAL.deleteQuietly(subDir));// no error
        
        // assert false boolean{subDir.exists()} to void{Assert}
        Assert.assertFalse(subDir.exists());
    }

    @Test
    public void testDeleteForce() throws Exception {
        final File baseDir = getTestDirectory();
        final File subDir = new File(baseDir, "test");
        // assert true boolean{subDir.mkdir()} to void{Assert}
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
        // assert true boolean{subDir.exists()} to void{Assert}
        Assert.assertTrue(subDir.exists());
        // assert true boolean{subFile.exists()} to void{Assert}
        Assert.assertTrue(subFile.exists());
        // delete dir
        // delete File{subDir} to FileDeleteStrategy{FileDeleteStrategy.FORCE}
        FileDeleteStrategy.FORCE.delete(subDir);
        // assert false boolean{subDir.exists()} to void{Assert}
        Assert.assertFalse(subDir.exists());
        // assert false boolean{subFile.exists()} to void{Assert}
        Assert.assertFalse(subFile.exists());
        // delete dir
        // delete File{subDir} to FileDeleteStrategy{FileDeleteStrategy.FORCE}
        FileDeleteStrategy.FORCE.delete(subDir);// no error
        
        // assert false boolean{subDir.exists()} to void{Assert}
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
        // assert true boolean{FileDeleteStrategy.NORMAL.deleteQuietly(null)} to void{Assert}
        Assert.assertTrue(FileDeleteStrategy.NORMAL.deleteQuietly(null));
    }

    @Test
    public void testToString() {
        // assert equals String{"FileDeleteStrategy[Normal]"} to void{Assert}
        Assert.assertEquals("FileDeleteStrategy[Normal]", FileDeleteStrategy.NORMAL.toString());
        // assert equals String{"FileDeleteStrategy[Force]"} to void{Assert}
        Assert.assertEquals("FileDeleteStrategy[Force]", FileDeleteStrategy.FORCE.toString());
    }
}

