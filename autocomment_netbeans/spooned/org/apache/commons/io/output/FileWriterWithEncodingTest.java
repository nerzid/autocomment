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
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.io.File;
import org.apache.commons.io.testtools.FileBasedTestCase;
import java.io.FileOutputStream;
import org.apache.commons.io.FileUtils;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import java.io.OutputStreamWriter;
import org.junit.Test;
import org.apache.commons.io.testtools.TestUtils;
import java.io.Writer;

/**
 * Tests that the encoding is actually set and used.
 * 
 * @version $Id: FileWriterWithEncodingTest.java 1718944 2015-12-09 19:50:30Z krosenvold $
 */
public class FileWriterWithEncodingTest extends FileBasedTestCase {
    private String defaultEncoding;

    private File file1;

    private File file2;

    private String textContent;

    private char[] anotherTestContent = new char[]{ 'f' , 'z' , 'x' };

    @Before
    public void setUp() {
        final File encodingFinder = new File(getTestDirectory(), "finder.txt");
        OutputStreamWriter out = null;
        try {
            out = new OutputStreamWriter(new FileOutputStream(encodingFinder));
            defaultEncoding = out.getEncoding();
        } catch (final IOException ex) {
            throw new RuntimeException(ex.getMessage());
        } finally {
            IOUtils.closeQuietly(out);
        }
        file1 = new File(getTestDirectory(), "testfile1.txt");
        file2 = new File(getTestDirectory(), "testfile2.txt");
        final char[] arr = new char[1024];
        final char[] chars = "ABCDEFGHIJKLMNOPQabcdefgihklmnopq".toCharArray();
        for (int i = 0; i < (arr.length); i++) {
            arr[i] = chars[(i % (chars.length))];
        }
        textContent = new String(arr);
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(getTestDirectory());
        defaultEncoding = null;
        textContent = null;
    }

    // -----------------------------------------------------------------------
    @Test
    public void sameEncoding_string_constructor() throws Exception {
        succesfulRun(new FileWriterWithEncoding(file2, defaultEncoding));
    }

    @Test
    public void sameEncoding_string_string_constructor() throws Exception {
        succesfulRun(new FileWriterWithEncoding(file2.getPath(), defaultEncoding));
    }

    @Test
    public void sameEncoding_Charset_constructor() throws Exception {
        succesfulRun(new FileWriterWithEncoding(file2, Charset.defaultCharset()));
    }

    @Test
    public void sameEncoding_string_Charset_constructor() throws Exception {
        succesfulRun(new FileWriterWithEncoding(file2.getPath(), Charset.defaultCharset()));
    }

    @Test
    public void sameEncoding_CharsetEncoder_constructor() throws Exception {
        CharsetEncoder enc = Charset.defaultCharset().newEncoder();
        succesfulRun(new FileWriterWithEncoding(file2, enc));
    }

    @Test
    public void sameEncoding_string_CharsetEncoder_constructor() throws Exception {
        CharsetEncoder enc = Charset.defaultCharset().newEncoder();
        succesfulRun(new FileWriterWithEncoding(file2.getPath(), enc));
    }

    private void succesfulRun(FileWriterWithEncoding fw21) throws Exception {
        FileWriter fw1 = null;
        FileWriterWithEncoding fw2 = null;
        try {
            fw1 = new FileWriter(file1);// default encoding
            
            fw2 = fw21;
            writeTestPayload(fw1, fw2);
            TestUtils.checkFile(file1, file2);
        } finally {
            IOUtils.closeQuietly(fw1);
            IOUtils.closeQuietly(fw2);
        }
        Assert.assertTrue(file1.exists());
        Assert.assertTrue(file2.exists());
    }

    @Test
    public void testDifferentEncoding() throws Exception {
        if (Charset.isSupported("UTF-16BE")) {
            FileWriter fw1 = null;
            FileWriterWithEncoding fw2 = null;
            try {
                fw1 = new FileWriter(file1);// default encoding
                
                fw2 = new FileWriterWithEncoding(file2, defaultEncoding);
                writeTestPayload(fw1, fw2);
                try {
                    TestUtils.checkFile(file1, file2);
                    Assert.fail();
                } catch (final AssertionError ex) {
                    // success
                }
            } finally {
                IOUtils.closeQuietly(fw1);
                IOUtils.closeQuietly(fw2);
            }
            Assert.assertTrue(file1.exists());
            Assert.assertTrue(file2.exists());
        } 
        if (Charset.isSupported("UTF-16LE")) {
            FileWriter fw1 = null;
            FileWriterWithEncoding fw2 = null;
            try {
                fw1 = new FileWriter(file1);// default encoding
                
                fw2 = new FileWriterWithEncoding(file2, defaultEncoding);
                writeTestPayload(fw1, fw2);
                try {
                    TestUtils.checkFile(file1, file2);
                    Assert.fail();
                } catch (final AssertionError ex) {
                    // success
                }
            } finally {
                IOUtils.closeQuietly(fw1);
                IOUtils.closeQuietly(fw2);
            }
            Assert.assertTrue(file1.exists());
            Assert.assertTrue(file2.exists());
        } 
    }

    private void writeTestPayload(FileWriter fw1, FileWriterWithEncoding fw2) throws IOException {
        Assert.assertTrue(file1.exists());
        Assert.assertTrue(file2.exists());
        fw1.write(textContent);
        fw2.write(textContent);
        fw1.write(65);
        fw2.write(65);
        fw1.write(anotherTestContent);
        fw2.write(anotherTestContent);
        fw1.write(anotherTestContent, 1, 2);
        fw2.write(anotherTestContent, 1, 2);
        fw1.write("CAFE", 1, 2);
        fw2.write("CAFE", 1, 2);
        fw1.flush();
        fw2.flush();
    }

    // -----------------------------------------------------------------------
    @Test
    public void constructor_File_encoding_badEncoding() {
        Writer writer = null;
        try {
            writer = new FileWriterWithEncoding(file1, "BAD-ENCODE");
            Assert.fail();
        } catch (final IOException ex) {
            // expected
            Assert.assertFalse(file1.exists());
        } finally {
            IOUtils.closeQuietly(writer);
        }
        Assert.assertFalse(file1.exists());
    }

    // -----------------------------------------------------------------------
    @Test
    public void constructor_File_directory() {
        Writer writer = null;
        try {
            writer = new FileWriterWithEncoding(getTestDirectory(), defaultEncoding);
            Assert.fail();
        } catch (final IOException ex) {
            // expected
            Assert.assertFalse(file1.exists());
        } finally {
            IOUtils.closeQuietly(writer);
        }
        Assert.assertFalse(file1.exists());
    }

    // -----------------------------------------------------------------------
    @Test
    public void constructor_File_nullFile() throws IOException {
        Writer writer = null;
        try {
            writer = new FileWriterWithEncoding(((File) (null)), defaultEncoding);
            Assert.fail();
        } catch (final NullPointerException ex) {
            // expected
            Assert.assertFalse(file1.exists());
        } finally {
            IOUtils.closeQuietly(writer);
        }
        Assert.assertFalse(file1.exists());
    }

    // -----------------------------------------------------------------------
    @Test
    public void constructor_fileName_nullFile() throws IOException {
        Writer writer = null;
        try {
            writer = new FileWriterWithEncoding(((String) (null)), defaultEncoding);
            Assert.fail();
        } catch (final NullPointerException ex) {
            // expected
            Assert.assertFalse(file1.exists());
        } finally {
            IOUtils.closeQuietly(writer);
        }
        Assert.assertFalse(file1.exists());
    }

    @Test
    public void sameEncoding_null_Charset_constructor() throws Exception {
        try {
            succesfulRun(new FileWriterWithEncoding(file2, ((Charset) (null))));
            Assert.fail();
        } catch (NullPointerException ignore) {
        }
    }
}

