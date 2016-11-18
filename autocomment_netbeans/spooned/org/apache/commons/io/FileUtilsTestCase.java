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
import java.util.Arrays;
import org.junit.Assert;
import java.math.BigInteger;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.CRC32;
import java.nio.charset.Charset;
import java.util.zip.Checksum;
import java.util.Collection;
import java.util.Date;
import java.io.File;
import org.apache.commons.io.testtools.FileBasedTestCase;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.filefilter.NameFileFilter;
import java.io.OutputStream;
import org.apache.commons.io.testtools.TestUtils;
import java.net.URL;
import org.apache.commons.io.filefilter.WildcardFileFilter;

/**
 * This is used to test FileUtils for correctness.
 * 
 * @version $Id: FileUtilsTestCase.java 1718945 2015-12-09 19:51:14Z krosenvold $
 * @see FileUtils
 */
// unit tests include tests of many deprecated methods
@SuppressWarnings(value = { "deprecation" , "ResultOfMethodCallIgnored" })
public class FileUtilsTestCase extends FileBasedTestCase {
    // Test data
    /**
     * Size of test directory.
     */
    private static final int TEST_DIRECTORY_SIZE = 0;

    /**
     * Size of test directory.
     */
    private static final BigInteger TEST_DIRECTORY_SIZE_BI = BigInteger.ZERO;

    /**
     * Size (greater of zero) of test file.
     */
    private static final BigInteger TEST_DIRECTORY_SIZE_GT_ZERO_BI = BigInteger.valueOf(100);

    /**
     * List files recursively
     */
    private static final FileUtilsTestCase.ListDirectoryWalker LIST_WALKER = new FileUtilsTestCase.ListDirectoryWalker();

    /**
     * Delay in milliseconds to make sure test for "last modified date" are accurate
     */
    // private static final int LAST_MODIFIED_DELAY = 600;
    private final File testFile1;

    private final File testFile2;

    private final int testFile1Size;

    private final int testFile2Size;

    public FileUtilsTestCase() {
        testFile1 = new File(getTestDirectory(), "file1-test.txt");
        testFile2 = new File(getTestDirectory(), "file1a-test.txt");
        testFile1Size = ((int) (testFile1.length()));
        testFile2Size = ((int) (testFile2.length()));
    }

    @Before
    public void setUp() throws Exception {
        getTestDirectory().mkdirs();
        if (!(testFile1.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + (testFile1)) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output3 = new BufferedOutputStream(new FileOutputStream(testFile1));
        try {
            TestUtils.generateTestData(output3, ((long) (testFile1Size)));
        } finally {
            IOUtils.closeQuietly(output3);
        }
        if (!(testFile2.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + (testFile2)) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output2 = new BufferedOutputStream(new FileOutputStream(testFile2));
        try {
            TestUtils.generateTestData(output2, ((long) (testFile2Size)));
        } finally {
            IOUtils.closeQuietly(output2);
        }
        FileUtils.deleteDirectory(getTestDirectory());
        getTestDirectory().mkdirs();
        if (!(testFile1.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + (testFile1)) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output1 = new BufferedOutputStream(new FileOutputStream(testFile1));
        try {
            TestUtils.generateTestData(output1, ((long) (testFile1Size)));
        } finally {
            IOUtils.closeQuietly(output1);
        }
        if (!(testFile2.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + (testFile2)) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(testFile2));
        try {
            TestUtils.generateTestData(output, ((long) (testFile2Size)));
        } finally {
            IOUtils.closeQuietly(output);
        }
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(getTestDirectory());
    }

    private String getName() {
        return FileUtilsTestCase.this.getClass().getSimpleName();
    }

    // -----------------------------------------------------------------------
    @Test
    public void testGetFile() {
        final File expected_A = new File("src");
        final File expected_B = new File(expected_A, "main");
        final File expected_C = new File(expected_B, "java");
        Assert.assertEquals("A", expected_A, FileUtils.getFile("src"));
        Assert.assertEquals("B", expected_B, FileUtils.getFile("src", "main"));
        Assert.assertEquals("C", expected_C, FileUtils.getFile("src", "main", "java"));
        try {
            FileUtils.getFile(((String[]) (null)));
            Assert.fail("Expected NullPointerException");
        } catch (final NullPointerException e) {
            // expected
        }
    }

    @Test
    public void testGetFile_Parent() {
        final File parent = new File("parent");
        final File expected_A = new File(parent, "src");
        final File expected_B = new File(expected_A, "main");
        final File expected_C = new File(expected_B, "java");
        Assert.assertEquals("A", expected_A, FileUtils.getFile(parent, "src"));
        Assert.assertEquals("B", expected_B, FileUtils.getFile(parent, "src", "main"));
        Assert.assertEquals("C", expected_C, FileUtils.getFile(parent, "src", "main", "java"));
        try {
            FileUtils.getFile(parent, ((String[]) (null)));
            Assert.fail("Expected NullPointerException");
        } catch (final NullPointerException e) {
            // expected
        }
        try {
            FileUtils.getFile(((File) (null)), "src");
            Assert.fail("Expected NullPointerException");
        } catch (final NullPointerException e) {
            // expected
        }
    }

    @Test
    public void testGetTempDirectoryPath() {
        Assert.assertEquals(System.getProperty("java.io.tmpdir"), FileUtils.getTempDirectoryPath());
    }

    @Test
    public void testGetTempDirectory() {
        final File tempDirectory = new File(System.getProperty("java.io.tmpdir"));
        Assert.assertEquals(tempDirectory, FileUtils.getTempDirectory());
    }

    @Test
    public void testGetUserDirectoryPath() {
        Assert.assertEquals(System.getProperty("user.home"), FileUtils.getUserDirectoryPath());
    }

    @Test
    public void testGetUserDirectory() {
        final File userDirectory = new File(System.getProperty("user.home"));
        Assert.assertEquals(userDirectory, FileUtils.getUserDirectory());
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_openInputStream_exists() throws Exception {
        final File file = new File(getTestDirectory(), "test.txt");
        TestUtils.createLineBasedFile(file, new String[]{ "Hello" });
        FileInputStream in = null;
        try {
            in = FileUtils.openInputStream(file);
            Assert.assertEquals('H', in.read());
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    @Test
    public void test_openInputStream_existsButIsDirectory() throws Exception {
        final File directory = new File(getTestDirectory(), "subdir");
        directory.mkdirs();
        FileInputStream in = null;
        try {
            in = FileUtils.openInputStream(directory);
            Assert.fail();
        } catch (final IOException ioe) {
            // expected
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    @Test
    public void test_openInputStream_notExists() throws Exception {
        final File directory = new File(getTestDirectory(), "test.txt");
        FileInputStream in = null;
        try {
            in = FileUtils.openInputStream(directory);
            Assert.fail();
        } catch (final IOException ioe) {
            // expected
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    // -----------------------------------------------------------------------
    void openOutputStream_noParent(final boolean createFile) throws Exception {
        final File file = new File("test.txt");
        Assert.assertNull(file.getParentFile());
        try {
            if (createFile) {
                TestUtils.createLineBasedFile(file, new String[]{ "Hello" });
            } 
            FileOutputStream out = null;
            try {
                out = FileUtils.openOutputStream(file);
                out.write(0);
            } finally {
                IOUtils.closeQuietly(out);
            }
            Assert.assertTrue(file.exists());
        } finally {
            if (!(file.delete())) {
                file.deleteOnExit();
            } 
        }
    }

    @Test
    public void test_openOutputStream_noParentCreateFile() throws Exception {
        openOutputStream_noParent(true);
    }

    @Test
    public void test_openOutputStream_noParentNoFile() throws Exception {
        openOutputStream_noParent(false);
    }

    @Test
    public void test_openOutputStream_exists() throws Exception {
        final File file = new File(getTestDirectory(), "test.txt");
        TestUtils.createLineBasedFile(file, new String[]{ "Hello" });
        FileOutputStream out = null;
        try {
            out = FileUtils.openOutputStream(file);
            out.write(0);
        } finally {
            IOUtils.closeQuietly(out);
        }
        Assert.assertTrue(file.exists());
    }

    @Test
    public void test_openOutputStream_existsButIsDirectory() throws Exception {
        final File directory = new File(getTestDirectory(), "subdir");
        directory.mkdirs();
        FileOutputStream out = null;
        try {
            out = FileUtils.openOutputStream(directory);
            Assert.fail();
        } catch (final IOException ioe) {
            // expected
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    @Test
    public void test_openOutputStream_notExists() throws Exception {
        final File file = new File(getTestDirectory(), "a/test.txt");
        FileOutputStream out = null;
        try {
            out = FileUtils.openOutputStream(file);
            out.write(0);
        } finally {
            IOUtils.closeQuietly(out);
        }
        Assert.assertTrue(file.exists());
    }

    @Test
    public void test_openOutputStream_notExistsCannotCreate() throws Exception {
        // according to Wikipedia, most filing systems have a 256 limit on filename
        final String longStr = "abcdevwxyzabcdevwxyzabcdevwxyzabcdevwxyzabcdevwxyz" + ("abcdevwxyzabcdevwxyzabcdevwxyzabcdevwxyzabcdevwxyz" + ("abcdevwxyzabcdevwxyzabcdevwxyzabcdevwxyzabcdevwxyz" + ("abcdevwxyzabcdevwxyzabcdevwxyzabcdevwxyzabcdevwxyz" + ("abcdevwxyzabcdevwxyzabcdevwxyzabcdevwxyzabcdevwxyz" + "abcdevwxyzabcdevwxyzabcdevwxyzabcdevwxyzabcdevwxyz"))));// 300 chars
        
        final File file = new File(getTestDirectory(), (("a/" + longStr) + "/test.txt"));
        FileOutputStream out = null;
        try {
            out = FileUtils.openOutputStream(file);
            Assert.fail();
        } catch (final IOException ioe) {
            // expected
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    // -----------------------------------------------------------------------
    // byteCountToDisplaySize
    @Test
    public void testByteCountToDisplaySizeBigInteger() {
        final BigInteger b1023 = BigInteger.valueOf(1023);
        final BigInteger b1025 = BigInteger.valueOf(1025);
        final BigInteger KB1 = BigInteger.valueOf(1024);
        final BigInteger MB1 = KB1.multiply(KB1);
        final BigInteger GB1 = MB1.multiply(KB1);
        final BigInteger GB2 = GB1.add(GB1);
        final BigInteger TB1 = GB1.multiply(KB1);
        final BigInteger PB1 = TB1.multiply(KB1);
        final BigInteger EB1 = PB1.multiply(KB1);
        Assert.assertEquals(FileUtils.byteCountToDisplaySize(BigInteger.ZERO), "0 bytes");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize(BigInteger.ONE), "1 bytes");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize(b1023), "1023 bytes");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize(KB1), "1 KB");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize(b1025), "1 KB");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize(MB1.subtract(BigInteger.ONE)), "1023 KB");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize(MB1), "1 MB");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize(MB1.add(BigInteger.ONE)), "1 MB");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize(GB1.subtract(BigInteger.ONE)), "1023 MB");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize(GB1), "1 GB");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize(GB1.add(BigInteger.ONE)), "1 GB");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize(GB2), "2 GB");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize(GB2.subtract(BigInteger.ONE)), "1 GB");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize(TB1), "1 TB");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize(PB1), "1 PB");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize(EB1), "1 EB");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize(Long.MAX_VALUE), "7 EB");
        // Other MAX_VALUEs
        Assert.assertEquals(FileUtils.byteCountToDisplaySize(BigInteger.valueOf(Character.MAX_VALUE)), "63 KB");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize(BigInteger.valueOf(Short.MAX_VALUE)), "31 KB");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize(BigInteger.valueOf(Integer.MAX_VALUE)), "1 GB");
    }

    @SuppressWarnings(value = "NumericOverflow")
    @Test
    public void testByteCountToDisplaySizeLong() {
        Assert.assertEquals(FileUtils.byteCountToDisplaySize(0), "0 bytes");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize(1), "1 bytes");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize(1023), "1023 bytes");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize(1024), "1 KB");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize(1025), "1 KB");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize((1024 * 1023)), "1023 KB");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize((1024 * 1024)), "1 MB");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize((1024 * 1025)), "1 MB");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize(((1024 * 1024) * 1023)), "1023 MB");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize(((1024 * 1024) * 1024)), "1 GB");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize(((1024 * 1024) * 1025)), "1 GB");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize((((1024L * 1024) * 1024) * 2)), "2 GB");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize(((((1024 * 1024) * 1024) * 2) - 1)), "1 GB");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize((((1024L * 1024) * 1024) * 1024)), "1 TB");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize(((((1024L * 1024) * 1024) * 1024) * 1024)), "1 PB");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize((((((1024L * 1024) * 1024) * 1024) * 1024) * 1024)), "1 EB");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize(Long.MAX_VALUE), "7 EB");
        // Other MAX_VALUEs
        Assert.assertEquals(FileUtils.byteCountToDisplaySize(Character.MAX_VALUE), "63 KB");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize(Short.MAX_VALUE), "31 KB");
        Assert.assertEquals(FileUtils.byteCountToDisplaySize(Integer.MAX_VALUE), "1 GB");
    }

    // -----------------------------------------------------------------------
    @Test
    public void testToFile1() throws Exception {
        final URL url = new URL("file", null, "a/b/c/file.txt");
        final File file = FileUtils.toFile(url);
        Assert.assertTrue(file.toString().contains("file.txt"));
    }

    @Test
    public void testToFile2() throws Exception {
        final URL url = new URL("file", null, "a/b/c/file%20n%61me%2520.tx%74");
        final File file = FileUtils.toFile(url);
        Assert.assertTrue(file.toString().contains("file name%20.txt"));
    }

    @Test
    public void testToFile3() throws Exception {
        Assert.assertEquals(null, FileUtils.toFile(null));
        Assert.assertEquals(null, FileUtils.toFile(new URL("http://jakarta.apache.org")));
    }

    @Test
    public void testToFile4() throws Exception {
        final URL url = new URL("file", null, "a/b/c/file%%20%me.txt%");
        final File file = FileUtils.toFile(url);
        Assert.assertTrue(file.toString().contains("file% %me.txt%"));
    }

    /* IO-252 */
    @Test
    public void testToFile5() throws Exception {
        final URL url = new URL("file", null, "both%20are%20100%20%25%20true");
        final File file = FileUtils.toFile(url);
        Assert.assertEquals("both are 100 % true", file.toString());
    }

    @Test
    public void testToFileUtf8() throws Exception {
        final URL url = new URL("file", null, "/home/%C3%A4%C3%B6%C3%BC%C3%9F");
        final File file = FileUtils.toFile(url);
        Assert.assertTrue(file.toString().contains("\u00e4\u00f6\u00fc\u00df"));
    }

    @Test
    public void testDecodeUrl() {
        Assert.assertEquals("", FileUtils.decodeUrl(""));
        Assert.assertEquals("foo", FileUtils.decodeUrl("foo"));
        Assert.assertEquals("+", FileUtils.decodeUrl("+"));
        Assert.assertEquals("% ", FileUtils.decodeUrl("%25%20"));
        Assert.assertEquals("%20", FileUtils.decodeUrl("%2520"));
        Assert.assertEquals("jar:file:/C:/dir/sub dir/1.0/foo-1.0.jar!/org/Bar.class", FileUtils.decodeUrl("jar:file:/C:/dir/sub%20dir/1.0/foo-1.0.jar!/org/Bar.class"));
    }

    @Test
    public void testDecodeUrlLenient() {
        Assert.assertEquals(" ", FileUtils.decodeUrl(" "));
        Assert.assertEquals("\u00e4\u00f6\u00fc\u00df", FileUtils.decodeUrl("\u00e4\u00f6\u00fc\u00df"));
        Assert.assertEquals("%", FileUtils.decodeUrl("%"));
        Assert.assertEquals("% ", FileUtils.decodeUrl("%%20"));
        Assert.assertEquals("%2", FileUtils.decodeUrl("%2"));
        Assert.assertEquals("%2G", FileUtils.decodeUrl("%2G"));
    }

    @Test
    public void testDecodeUrlNullSafe() {
        Assert.assertNull(FileUtils.decodeUrl(null));
    }

    @Test
    public void testDecodeUrlEncodingUtf8() {
        Assert.assertEquals("\u00e4\u00f6\u00fc\u00df", FileUtils.decodeUrl("%C3%A4%C3%B6%C3%BC%C3%9F"));
    }

    // toFiles
    @Test
    public void testToFiles1() throws Exception {
        final URL[] urls = new URL[]{ new URL("file", null, "file1.txt") , new URL("file", null, "file2.txt") };
        final File[] files = FileUtils.toFiles(urls);
        Assert.assertEquals(urls.length, files.length);
        Assert.assertEquals(("File: " + (files[0])), true, files[0].toString().contains("file1.txt"));
        Assert.assertEquals(("File: " + (files[1])), true, files[1].toString().contains("file2.txt"));
    }

    @Test
    public void testToFiles2() throws Exception {
        final URL[] urls = new URL[]{ new URL("file", null, "file1.txt") , null };
        final File[] files = FileUtils.toFiles(urls);
        Assert.assertEquals(urls.length, files.length);
        Assert.assertEquals(("File: " + (files[0])), true, files[0].toString().contains("file1.txt"));
        Assert.assertEquals(("File: " + (files[1])), null, files[1]);
    }

    @Test
    public void testToFiles3() throws Exception {
        final URL[] urls = null;
        final File[] files = FileUtils.toFiles(urls);
        Assert.assertEquals(0, files.length);
    }

    @Test
    public void testToFiles3a() throws Exception {
        final URL[] urls = new URL[0];// empty array
        
        final File[] files = FileUtils.toFiles(urls);
        Assert.assertEquals(0, files.length);
    }

    @Test
    public void testToFiles4() throws Exception {
        final URL[] urls = new URL[]{ new URL("file", null, "file1.txt") , new URL("http", "jakarta.apache.org", "file1.txt") };
        try {
            FileUtils.toFiles(urls);
            Assert.fail();
        } catch (final IllegalArgumentException ignore) {
        }
    }

    // toURLs
    @Test
    public void testToURLs1() throws Exception {
        final File[] files = new File[]{ new File(getTestDirectory(), "file1.txt") , new File(getTestDirectory(), "file2.txt") , new File(getTestDirectory(), "test file.txt") };
        final URL[] urls = FileUtils.toURLs(files);
        Assert.assertEquals(files.length, urls.length);
        Assert.assertTrue(urls[0].toExternalForm().startsWith("file:"));
        Assert.assertTrue(urls[0].toExternalForm().contains("file1.txt"));
        Assert.assertTrue(urls[1].toExternalForm().startsWith("file:"));
        Assert.assertTrue(urls[1].toExternalForm().contains("file2.txt"));
        // Test escaped char
        Assert.assertTrue(urls[2].toExternalForm().startsWith("file:"));
        Assert.assertTrue(urls[2].toExternalForm().contains("test%20file.txt"));
    }

    // @Test public void testToURLs2() throws Exception {
    // File[] files = new File[] {
    // new File(getTestDirectory(), "file1.txt"),
    // null,
    // };
    // URL[] urls = FileUtils.toURLs(files);
    // 
    // assertEquals(files.length, urls.length);
    // assertTrue(urls[0].toExternalForm().startsWith("file:"));
    // assertTrue(urls[0].toExternalForm().indexOf("file1.txt") > 0);
    // assertEquals(null, urls[1]);
    // }
    // 
    // @Test public void testToURLs3() throws Exception {
    // File[] files = null;
    // URL[] urls = FileUtils.toURLs(files);
    // 
    // assertEquals(0, urls.length);
    // }
    @Test
    public void testToURLs3a() throws Exception {
        final File[] files = new File[0];// empty array
        
        final URL[] urls = FileUtils.toURLs(files);
        Assert.assertEquals(0, urls.length);
    }

    // contentEquals
    @Test
    public void testContentEquals() throws Exception {
        // Non-existent files
        final File file = new File(getTestDirectory(), getName());
        final File file2 = new File(getTestDirectory(), ((getName()) + "2"));
        // both don't  exist
        Assert.assertTrue(FileUtils.contentEquals(file, file));
        Assert.assertTrue(FileUtils.contentEquals(file, file2));
        Assert.assertTrue(FileUtils.contentEquals(file2, file2));
        Assert.assertTrue(FileUtils.contentEquals(file2, file));
        // Directories
        try {
            FileUtils.contentEquals(getTestDirectory(), getTestDirectory());
            Assert.fail("Comparing directories should fail with an IOException");
        } catch (final IOException ioe) {
            // expected
        }
        // Different files
        final File objFile1 = new File(getTestDirectory(), ((getName()) + ".object"));
        objFile1.deleteOnExit();
        FileUtils.copyURLToFile(getClass().getResource("/java/lang/Object.class"), objFile1);
        final File objFile1b = new File(getTestDirectory(), ((getName()) + ".object2"));
        objFile1.deleteOnExit();
        FileUtils.copyURLToFile(getClass().getResource("/java/lang/Object.class"), objFile1b);
        final File objFile2 = new File(getTestDirectory(), ((getName()) + ".collection"));
        objFile2.deleteOnExit();
        FileUtils.copyURLToFile(getClass().getResource("/java/util/Collection.class"), objFile2);
        Assert.assertFalse(FileUtils.contentEquals(objFile1, objFile2));
        Assert.assertFalse(FileUtils.contentEquals(objFile1b, objFile2));
        Assert.assertTrue(FileUtils.contentEquals(objFile1, objFile1b));
        Assert.assertTrue(FileUtils.contentEquals(objFile1, objFile1));
        Assert.assertTrue(FileUtils.contentEquals(objFile1b, objFile1b));
        Assert.assertTrue(FileUtils.contentEquals(objFile2, objFile2));
        // Equal files
        file.createNewFile();
        file2.createNewFile();
        Assert.assertTrue(FileUtils.contentEquals(file, file));
        Assert.assertTrue(FileUtils.contentEquals(file, file2));
    }

    @Test
    public void testContentEqualsIgnoreEOL() throws Exception {
        // Non-existent files
        final File file1 = new File(getTestDirectory(), getName());
        final File file2 = new File(getTestDirectory(), ((getName()) + "2"));
        // both don't  exist
        Assert.assertTrue(FileUtils.contentEqualsIgnoreEOL(file1, file1, null));
        Assert.assertTrue(FileUtils.contentEqualsIgnoreEOL(file1, file2, null));
        Assert.assertTrue(FileUtils.contentEqualsIgnoreEOL(file2, file2, null));
        Assert.assertTrue(FileUtils.contentEqualsIgnoreEOL(file2, file1, null));
        // Directories
        try {
            FileUtils.contentEqualsIgnoreEOL(getTestDirectory(), getTestDirectory(), null);
            Assert.fail("Comparing directories should fail with an IOException");
        } catch (final IOException ioe) {
            // expected
        }
        // Different files
        final File tfile1 = new File(getTestDirectory(), ((getName()) + ".txt1"));
        tfile1.deleteOnExit();
        FileUtils.write(tfile1, "123\r");
        final File tfile2 = new File(getTestDirectory(), ((getName()) + ".txt2"));
        tfile1.deleteOnExit();
        FileUtils.write(tfile2, "123\n");
        final File tfile3 = new File(getTestDirectory(), ((getName()) + ".collection"));
        tfile3.deleteOnExit();
        FileUtils.write(tfile3, "123\r\n2");
        Assert.assertTrue(FileUtils.contentEqualsIgnoreEOL(tfile1, tfile1, null));
        Assert.assertTrue(FileUtils.contentEqualsIgnoreEOL(tfile2, tfile2, null));
        Assert.assertTrue(FileUtils.contentEqualsIgnoreEOL(tfile3, tfile3, null));
        Assert.assertTrue(FileUtils.contentEqualsIgnoreEOL(tfile1, tfile2, null));
        Assert.assertFalse(FileUtils.contentEqualsIgnoreEOL(tfile1, tfile3, null));
        Assert.assertFalse(FileUtils.contentEqualsIgnoreEOL(tfile2, tfile3, null));
        final URL urlCR = getClass().getResource("FileUtilsTestDataCR.dat");
        Assert.assertNotNull(urlCR);
        final File cr = new File(urlCR.getPath());
        Assert.assertTrue(cr.exists());
        final URL urlCRLF = getClass().getResource("FileUtilsTestDataCRLF.dat");
        Assert.assertNotNull(urlCRLF);
        final File crlf = new File(urlCRLF.getPath());
        Assert.assertTrue(crlf.exists());
        final URL urlLF = getClass().getResource("FileUtilsTestDataLF.dat");
        Assert.assertNotNull(urlLF);
        final File lf = new File(urlLF.getPath());
        Assert.assertTrue(lf.exists());
        Assert.assertTrue(FileUtils.contentEqualsIgnoreEOL(cr, cr, null));
        Assert.assertTrue(FileUtils.contentEqualsIgnoreEOL(crlf, crlf, null));
        Assert.assertTrue(FileUtils.contentEqualsIgnoreEOL(lf, lf, null));
        Assert.assertTrue(FileUtils.contentEqualsIgnoreEOL(cr, crlf, null));
        Assert.assertTrue(FileUtils.contentEqualsIgnoreEOL(cr, lf, null));
        Assert.assertTrue(FileUtils.contentEqualsIgnoreEOL(crlf, lf, null));
        // Check the files behave OK when EOL is not ignored
        Assert.assertTrue(FileUtils.contentEquals(cr, cr));
        Assert.assertTrue(FileUtils.contentEquals(crlf, crlf));
        Assert.assertTrue(FileUtils.contentEquals(lf, lf));
        Assert.assertFalse(FileUtils.contentEquals(cr, crlf));
        Assert.assertFalse(FileUtils.contentEquals(cr, lf));
        Assert.assertFalse(FileUtils.contentEquals(crlf, lf));
        // Equal files
        file1.createNewFile();
        file2.createNewFile();
        Assert.assertTrue(FileUtils.contentEqualsIgnoreEOL(file1, file1, null));
        Assert.assertTrue(FileUtils.contentEqualsIgnoreEOL(file1, file2, null));
    }

    // copyURLToFile
    @Test
    public void testCopyURLToFile() throws Exception {
        // Creates file
        final File file = new File(getTestDirectory(), getName());
        file.deleteOnExit();
        // Loads resource
        final String resourceName = "/java/lang/Object.class";
        FileUtils.copyURLToFile(getClass().getResource(resourceName), file);
        // Tests that resuorce was copied correctly
        final FileInputStream fis = new FileInputStream(file);
        try {
            Assert.assertTrue("Content is not equal.", IOUtils.contentEquals(getClass().getResourceAsStream(resourceName), fis));
        } finally {
            fis.close();
        }
        // TODO Maybe test copy to itself like for copyFile()
    }

    @Test
    public void testCopyURLToFileWithTimeout() throws Exception {
        // Creates file
        final File file = new File(getTestDirectory(), "testCopyURLToFileWithTimeout");
        file.deleteOnExit();
        // Loads resource
        final String resourceName = "/java/lang/Object.class";
        FileUtils.copyURLToFile(getClass().getResource(resourceName), file, 500, 500);
        // Tests that resuorce was copied correctly
        final FileInputStream fis = new FileInputStream(file);
        try {
            Assert.assertTrue("Content is not equal.", IOUtils.contentEquals(getClass().getResourceAsStream(resourceName), fis));
        } finally {
            fis.close();
        }
        // TODO Maybe test copy to itself like for copyFile()
    }

    // forceMkdir
    @Test
    public void testForceMkdir() throws Exception {
        // Tests with existing directory
        FileUtils.forceMkdir(getTestDirectory());
        // Creates test file
        final File testFile = new File(getTestDirectory(), getName());
        testFile.deleteOnExit();
        testFile.createNewFile();
        Assert.assertTrue("Test file does not exist.", testFile.exists());
        // Tests with existing file
        try {
            FileUtils.forceMkdir(testFile);
            Assert.fail("Exception expected.");
        } catch (final IOException ignore) {
        }
        testFile.delete();
        // Tests with non-existent directory
        FileUtils.forceMkdir(testFile);
        Assert.assertTrue("Directory was not created.", testFile.exists());
    }

    @Test
    public void testForceMkdirParent() throws Exception {
        // Tests with existing directory
        Assert.assertTrue(getTestDirectory().exists());
        final File testParentDir = new File(getTestDirectory(), "testForceMkdirParent");
        try {
            testParentDir.delete();
            Assert.assertFalse(testParentDir.exists());
            final File testFile = new File(testParentDir, "test.txt");
            Assert.assertFalse(testParentDir.exists());
            Assert.assertFalse(testFile.exists());
            // Create
            FileUtils.forceMkdirParent(testFile);
            Assert.assertTrue(testParentDir.exists());
            Assert.assertFalse(testFile.exists());
            // Again
            FileUtils.forceMkdirParent(testFile);
            Assert.assertTrue(testParentDir.exists());
            Assert.assertFalse(testFile.exists());
        } finally {
            testParentDir.delete();
        }
    }

    // sizeOfDirectory
    @Test
    public void testSizeOfDirectory() throws Exception {
        final File file = new File(getTestDirectory(), getName());
        // Non-existent file
        try {
            FileUtils.sizeOfDirectory(file);
            Assert.fail("Exception expected.");
        } catch (final IllegalArgumentException ignore) {
        }
        // Creates file
        file.createNewFile();
        file.deleteOnExit();
        // Existing file
        try {
            FileUtils.sizeOfDirectory(file);
            Assert.fail("Exception expected.");
        } catch (final IllegalArgumentException ignore) {
        }
        // Existing directory
        file.delete();
        file.mkdir();
        // Create a cyclic symlink
        FileUtilsTestCase.this.createCircularSymLink(file);
        Assert.assertEquals("Unexpected directory size", FileUtilsTestCase.TEST_DIRECTORY_SIZE, FileUtils.sizeOfDirectory(file));
    }

    private void createCircularSymLink(final File file) throws IOException {
        if (!(FilenameUtils.isSystemWindows())) {
            Runtime.getRuntime().exec((((("ln -s " + file) + "/.. ") + file) + "/cycle"));
        } else {
            try {
                Runtime.getRuntime().exec((((("mklink /D " + file) + "/cycle") + file) + "/.. "));
            } catch (final IOException ioe) {
                // So that tests run in FAT filesystems
                // don't fail
            }
        }
    }

    @Test
    public void testSizeOfDirectoryAsBigInteger() throws Exception {
        final File file = new File(getTestDirectory(), getName());
        // Non-existent file
        try {
            FileUtils.sizeOfDirectoryAsBigInteger(file);
            Assert.fail("Exception expected.");
        } catch (final IllegalArgumentException ignore) {
        }
        // Creates file
        file.createNewFile();
        file.deleteOnExit();
        // Existing file
        try {
            FileUtils.sizeOfDirectoryAsBigInteger(file);
            Assert.fail("Exception expected.");
        } catch (final IllegalArgumentException ignore) {
        }
        // Existing directory
        file.delete();
        file.mkdir();
        FileUtilsTestCase.this.createCircularSymLink(file);
        Assert.assertEquals("Unexpected directory size", FileUtilsTestCase.TEST_DIRECTORY_SIZE_BI, FileUtils.sizeOfDirectoryAsBigInteger(file));
        // Existing directory which size is greater than zero
        file.delete();
        file.mkdir();
        final File nonEmptyFile = new File(file, ("nonEmptyFile" + (System.nanoTime())));
        if (!(nonEmptyFile.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + nonEmptyFile) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(nonEmptyFile));
        try {
            TestUtils.generateTestData(output, FileUtilsTestCase.TEST_DIRECTORY_SIZE_GT_ZERO_BI.longValue());
        } finally {
            IOUtils.closeQuietly(output);
        }
        nonEmptyFile.deleteOnExit();
        Assert.assertEquals("Unexpected directory size", FileUtilsTestCase.TEST_DIRECTORY_SIZE_GT_ZERO_BI, FileUtils.sizeOfDirectoryAsBigInteger(file));
        nonEmptyFile.delete();
        file.delete();
    }

    // Compare sizes of a directory tree using long and BigInteger methods
    @Test
    public void testCompareSizeOf() {
        final File start = new File("src/test/java");
        final long sizeLong1 = FileUtils.sizeOf(start);
        final BigInteger sizeBig = FileUtils.sizeOfAsBigInteger(start);
        final long sizeLong2 = FileUtils.sizeOf(start);
        Assert.assertEquals("Size should not change", sizeLong1, sizeLong2);
        Assert.assertEquals("longSize should equal BigSize", sizeLong1, sizeBig.longValue());
    }

    @Test
    public void testSizeOf() throws Exception {
        final File file = new File(getTestDirectory(), getName());
        // Null argument
        try {
            FileUtils.sizeOf(null);
            Assert.fail("Exception expected.");
        } catch (final NullPointerException ignore) {
        }
        // Non-existent file
        try {
            FileUtils.sizeOf(file);
            Assert.fail("Exception expected.");
        } catch (final IllegalArgumentException ignore) {
        }
        // Creates file
        file.createNewFile();
        file.deleteOnExit();
        // New file
        Assert.assertEquals(0, FileUtils.sizeOf(file));
        file.delete();
        // Existing file
        Assert.assertEquals("Unexpected files size", testFile1Size, FileUtils.sizeOf(testFile1));
        // Existing directory
        Assert.assertEquals("Unexpected directory size", FileUtilsTestCase.TEST_DIRECTORY_SIZE, FileUtils.sizeOf(getTestDirectory()));
    }

    @Test
    public void testSizeOfAsBigInteger() throws Exception {
        final File file = new File(getTestDirectory(), getName());
        // Null argument
        try {
            FileUtils.sizeOfAsBigInteger(null);
            Assert.fail("Exception expected.");
        } catch (final NullPointerException ignore) {
        }
        // Non-existent file
        try {
            FileUtils.sizeOfAsBigInteger(file);
            Assert.fail("Exception expected.");
        } catch (final IllegalArgumentException ignore) {
        }
        // Creates file
        file.createNewFile();
        file.deleteOnExit();
        // New file
        Assert.assertEquals(BigInteger.ZERO, FileUtils.sizeOfAsBigInteger(file));
        file.delete();
        // Existing file
        Assert.assertEquals("Unexpected files size", BigInteger.valueOf(testFile1Size), FileUtils.sizeOfAsBigInteger(testFile1));
        // Existing directory
        Assert.assertEquals("Unexpected directory size", FileUtilsTestCase.TEST_DIRECTORY_SIZE_BI, FileUtils.sizeOfAsBigInteger(getTestDirectory()));
    }

    // isFileNewer / isFileOlder
    @Test
    public void testIsFileNewerOlder() throws Exception {
        final File reference = new File(getTestDirectory(), "FileUtils-reference.txt");
        final File oldFile = new File(getTestDirectory(), "FileUtils-old.txt");
        final File newFile = new File(getTestDirectory(), "FileUtils-new.txt");
        final File invalidFile = new File(getTestDirectory(), "FileUtils-invalid-file.txt");
        // Create Files
        if (!(oldFile.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + oldFile) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output1 = new BufferedOutputStream(new FileOutputStream(oldFile));
        try {
            TestUtils.generateTestData(output1, ((long) (0)));
        } finally {
            IOUtils.closeQuietly(output1);
        }
        do {
            try {
                TestUtils.sleep(1000);
            } catch (final InterruptedException ie) {
                // ignore
            }
            if (!(reference.getParentFile().exists())) {
                throw new IOException((("Cannot create file " + reference) + " as the parent directory does not exist"));
            } 
            final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(reference));
            try {
                TestUtils.generateTestData(output, ((long) (0)));
            } finally {
                IOUtils.closeQuietly(output);
            }
        } while ((oldFile.lastModified()) == (reference.lastModified()) );
        final Date date = new Date();
        final long now = date.getTime();
        do {
            try {
                TestUtils.sleep(1000);
            } catch (final InterruptedException ie) {
                // ignore
            }
            if (!(newFile.getParentFile().exists())) {
                throw new IOException((("Cannot create file " + newFile) + " as the parent directory does not exist"));
            } 
            final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(newFile));
            try {
                TestUtils.generateTestData(output, ((long) (0)));
            } finally {
                IOUtils.closeQuietly(output);
            }
        } while ((reference.lastModified()) == (newFile.lastModified()) );
        // Test isFileNewer()
        Assert.assertFalse("Old File - Newer - File", FileUtils.isFileNewer(oldFile, reference));
        Assert.assertFalse("Old File - Newer - Date", FileUtils.isFileNewer(oldFile, date));
        Assert.assertFalse("Old File - Newer - Mili", FileUtils.isFileNewer(oldFile, now));
        Assert.assertTrue("New File - Newer - File", FileUtils.isFileNewer(newFile, reference));
        Assert.assertTrue("New File - Newer - Date", FileUtils.isFileNewer(newFile, date));
        Assert.assertTrue("New File - Newer - Mili", FileUtils.isFileNewer(newFile, now));
        Assert.assertFalse("Invalid - Newer - File", FileUtils.isFileNewer(invalidFile, reference));
        final String invalidFileName = invalidFile.getName();
        try {
            FileUtils.isFileNewer(newFile, invalidFile);
            Assert.fail("Should have cause IllegalArgumentException");
        } catch (final IllegalArgumentException iae) {
            final String message = iae.getMessage();
            Assert.assertTrue(((("Message should contain: " + invalidFileName) + " but was: ") + message), message.contains(invalidFileName));
        }
        // Test isFileOlder()
        Assert.assertTrue("Old File - Older - File", FileUtils.isFileOlder(oldFile, reference));
        Assert.assertTrue("Old File - Older - Date", FileUtils.isFileOlder(oldFile, date));
        Assert.assertTrue("Old File - Older - Mili", FileUtils.isFileOlder(oldFile, now));
        Assert.assertFalse("New File - Older - File", FileUtils.isFileOlder(newFile, reference));
        Assert.assertFalse("New File - Older - Date", FileUtils.isFileOlder(newFile, date));
        Assert.assertFalse("New File - Older - Mili", FileUtils.isFileOlder(newFile, now));
        Assert.assertFalse("Invalid - Older - File", FileUtils.isFileOlder(invalidFile, reference));
        try {
            FileUtils.isFileOlder(newFile, invalidFile);
            Assert.fail("Should have cause IllegalArgumentException");
        } catch (final IllegalArgumentException iae) {
            final String message = iae.getMessage();
            Assert.assertTrue(((("Message should contain: " + invalidFileName) + " but was: ") + message), message.contains(invalidFileName));
        }
        // ----- Test isFileNewer() exceptions -----
        // Null File
        try {
            FileUtils.isFileNewer(null, now);
            Assert.fail("Newer Null, expected IllegalArgumentExcepion");
        } catch (final IllegalArgumentException expected) {
            // expected result
        }
        // Null reference File
        try {
            FileUtils.isFileNewer(oldFile, ((File) (null)));
            Assert.fail("Newer Null reference, expected IllegalArgumentExcepion");
        } catch (final IllegalArgumentException ignore) {
            // expected result
        }
        // Invalid reference File
        try {
            FileUtils.isFileNewer(oldFile, invalidFile);
            Assert.fail("Newer invalid reference, expected IllegalArgumentExcepion");
        } catch (final IllegalArgumentException ignore) {
            // expected result
        }
        // Null reference Date
        try {
            FileUtils.isFileNewer(oldFile, ((Date) (null)));
            Assert.fail("Newer Null date, expected IllegalArgumentExcepion");
        } catch (final IllegalArgumentException ignore) {
            // expected result
        }
        // ----- Test isFileOlder() exceptions -----
        // Null File
        try {
            FileUtils.isFileOlder(null, now);
            Assert.fail("Older Null, expected IllegalArgumentExcepion");
        } catch (final IllegalArgumentException ignore) {
            // expected result
        }
        // Null reference File
        try {
            FileUtils.isFileOlder(oldFile, ((File) (null)));
            Assert.fail("Older Null reference, expected IllegalArgumentExcepion");
        } catch (final IllegalArgumentException ignore) {
            // expected result
        }
        // Invalid reference File
        try {
            FileUtils.isFileOlder(oldFile, invalidFile);
            Assert.fail("Older invalid reference, expected IllegalArgumentExcepion");
        } catch (final IllegalArgumentException ignore) {
            // expected result
        }
        // Null reference Date
        try {
            FileUtils.isFileOlder(oldFile, ((Date) (null)));
            Assert.fail("Older Null date, expected IllegalArgumentExcepion");
        } catch (final IllegalArgumentException ignore) {
            // expected result
        }
    }

    // copyFile
    @Test
    public void testCopyFile1() throws Exception {
        final File destination = new File(getTestDirectory(), "copy1.txt");
        // Thread.sleep(LAST_MODIFIED_DELAY);
        // This is to slow things down so we can catch if
        // the lastModified date is not ok
        FileUtils.copyFile(testFile1, destination);
        Assert.assertTrue("Check Exist", destination.exists());
        Assert.assertEquals("Check Full copy", testFile1Size, destination.length());
        /* disabled: Thread.sleep doesn't work reliantly for this case
        assertTrue("Check last modified date preserved",
        testFile1.lastModified() == destination.lastModified());
         */
    }

    @Test
    public void testCopyFileToOutputStream() throws Exception {
        final ByteArrayOutputStream destination = new ByteArrayOutputStream();
        FileUtils.copyFile(testFile1, destination);
        Assert.assertEquals("Check Full copy size", testFile1Size, destination.size());
        final byte[] expected = FileUtils.readFileToByteArray(testFile1);
        Assert.assertArrayEquals("Check Full copy", expected, destination.toByteArray());
    }

    @Test
    @Ignore
    public void testCopyFileLarge() throws Exception {
        final File largeFile = new File(getTestDirectory(), "large.txt");
        final File destination = new File(getTestDirectory(), "copylarge.txt");
        System.out.println(("START:   " + (new Date())));
        if (!(largeFile.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + largeFile) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(largeFile));
        try {
            TestUtils.generateTestData(output, FileUtils.ONE_GB);
        } finally {
            IOUtils.closeQuietly(output);
        }
        System.out.println(("CREATED: " + (new Date())));
        FileUtils.copyFile(largeFile, destination);
        System.out.println(("COPIED:  " + (new Date())));
        Assert.assertTrue("Check Exist", destination.exists());
        Assert.assertEquals("Check Full copy", largeFile.length(), destination.length());
    }

    @Test
    public void testCopyFile2() throws Exception {
        final File destination = new File(getTestDirectory(), "copy2.txt");
        // Thread.sleep(LAST_MODIFIED_DELAY);
        // This is to slow things down so we can catch if
        // the lastModified date is not ok
        FileUtils.copyFile(testFile1, destination);
        Assert.assertTrue("Check Exist", destination.exists());
        Assert.assertEquals("Check Full copy", testFile2Size, destination.length());
        /* disabled: Thread.sleep doesn't work reliably for this case
        assertTrue("Check last modified date preserved",
        testFile1.lastModified() == destination.lastModified());
         */
    }

    @Test
    public void testCopyToSelf() throws Exception {
        final File destination = new File(getTestDirectory(), "copy3.txt");
        // Prepare a test file
        FileUtils.copyFile(testFile1, destination);
        try {
            FileUtils.copyFile(destination, destination);
            Assert.fail("file copy to self should not be possible");
        } catch (final IOException ioe) {
            // we want the exception, copy to self should be illegal
        }
    }

    @Test
    public void testCopyFile2WithoutFileDatePreservation() throws Exception {
        final File destination = new File(getTestDirectory(), "copy2.txt");
        // Thread.sleep(LAST_MODIFIED_DELAY);
        // This is to slow things down so we can catch if
        // the lastModified date is not ok
        FileUtils.copyFile(testFile1, destination, false);
        Assert.assertTrue("Check Exist", destination.exists());
        Assert.assertEquals("Check Full copy", testFile2Size, destination.length());
        /* disabled: Thread.sleep doesn't work reliantly for this case
        assertTrue("Check last modified date modified",
        testFile1.lastModified() != destination.lastModified());
         */
    }

    @Test
    public void testCopyDirectoryToDirectory_NonExistingDest() throws Exception {
        if (!(testFile1.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + (testFile1)) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output1 = new BufferedOutputStream(new FileOutputStream(testFile1));
        try {
            TestUtils.generateTestData(output1, ((long) (1234)));
        } finally {
            IOUtils.closeQuietly(output1);
        }
        if (!(testFile2.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + (testFile2)) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(testFile2));
        try {
            TestUtils.generateTestData(output, ((long) (4321)));
        } finally {
            IOUtils.closeQuietly(output);
        }
        final File srcDir = getTestDirectory();
        final File subDir = new File(srcDir, "sub");
        subDir.mkdir();
        final File subFile = new File(subDir, "A.txt");
        FileUtils.writeStringToFile(subFile, "HELLO WORLD", "UTF8");
        final File destDir = new File(System.getProperty("java.io.tmpdir"), "tmp-FileUtilsTestCase");
        FileUtils.deleteDirectory(destDir);
        final File actualDestDir = new File(destDir, srcDir.getName());
        FileUtils.copyDirectoryToDirectory(srcDir, destDir);
        Assert.assertTrue("Check exists", destDir.exists());
        Assert.assertTrue("Check exists", actualDestDir.exists());
        final long srcSize = FileUtils.sizeOfDirectory(srcDir);
        Assert.assertTrue("Size > 0", (srcSize > 0));
        Assert.assertEquals("Check size", srcSize, FileUtils.sizeOfDirectory(actualDestDir));
        Assert.assertTrue(new File(actualDestDir, "sub/A.txt").exists());
        FileUtils.deleteDirectory(destDir);
    }

    @Test
    public void testCopyDirectoryToNonExistingDest() throws Exception {
        if (!(testFile1.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + (testFile1)) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output1 = new BufferedOutputStream(new FileOutputStream(testFile1));
        try {
            TestUtils.generateTestData(output1, ((long) (1234)));
        } finally {
            IOUtils.closeQuietly(output1);
        }
        if (!(testFile2.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + (testFile2)) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(testFile2));
        try {
            TestUtils.generateTestData(output, ((long) (4321)));
        } finally {
            IOUtils.closeQuietly(output);
        }
        final File srcDir = getTestDirectory();
        final File subDir = new File(srcDir, "sub");
        subDir.mkdir();
        final File subFile = new File(subDir, "A.txt");
        FileUtils.writeStringToFile(subFile, "HELLO WORLD", "UTF8");
        final File destDir = new File(System.getProperty("java.io.tmpdir"), "tmp-FileUtilsTestCase");
        FileUtils.deleteDirectory(destDir);
        FileUtils.copyDirectory(srcDir, destDir);
        Assert.assertTrue("Check exists", destDir.exists());
        final long sizeOfSrcDirectory = FileUtils.sizeOfDirectory(srcDir);
        Assert.assertTrue("Size > 0", (sizeOfSrcDirectory > 0));
        Assert.assertEquals("Check size", sizeOfSrcDirectory, FileUtils.sizeOfDirectory(destDir));
        Assert.assertTrue(new File(destDir, "sub/A.txt").exists());
        FileUtils.deleteDirectory(destDir);
    }

    @Test
    public void testCopyDirectoryToExistingDest() throws Exception {
        if (!(testFile1.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + (testFile1)) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output1 = new BufferedOutputStream(new FileOutputStream(testFile1));
        try {
            TestUtils.generateTestData(output1, ((long) (1234)));
        } finally {
            IOUtils.closeQuietly(output1);
        }
        if (!(testFile2.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + (testFile2)) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(testFile2));
        try {
            TestUtils.generateTestData(output, ((long) (4321)));
        } finally {
            IOUtils.closeQuietly(output);
        }
        final File srcDir = getTestDirectory();
        final File subDir = new File(srcDir, "sub");
        subDir.mkdir();
        final File subFile = new File(subDir, "A.txt");
        FileUtils.writeStringToFile(subFile, "HELLO WORLD", "UTF8");
        final File destDir = new File(System.getProperty("java.io.tmpdir"), "tmp-FileUtilsTestCase");
        FileUtils.deleteDirectory(destDir);
        destDir.mkdirs();
        FileUtils.copyDirectory(srcDir, destDir);
        final long srcSize = FileUtils.sizeOfDirectory(srcDir);
        Assert.assertTrue("Size > 0", (srcSize > 0));
        Assert.assertEquals(srcSize, FileUtils.sizeOfDirectory(destDir));
        Assert.assertTrue(new File(destDir, "sub/A.txt").exists());
    }

    @Test
    public void testCopyDirectoryFiltered() throws Exception {
        final File grandParentDir = new File(getTestDirectory(), "grandparent");
        final File parentDir = new File(grandParentDir, "parent");
        final File childDir = new File(parentDir, "child");
        createFilesForTestCopyDirectory(grandParentDir, parentDir, childDir);
        final NameFileFilter filter = new NameFileFilter(new String[]{ "parent" , "child" , "file3.txt" });
        final File destDir = new File(getTestDirectory(), "copydest");
        FileUtils.copyDirectory(grandParentDir, destDir, filter);
        final List<File> files = FileUtilsTestCase.LIST_WALKER.list(destDir);
        Assert.assertEquals(3, files.size());
        Assert.assertEquals("parent", files.get(0).getName());
        Assert.assertEquals("child", files.get(1).getName());
        Assert.assertEquals("file3.txt", files.get(2).getName());
    }

    @Test
    public void testCopyDirectoryPreserveDates() throws Exception {
        final File source = new File(getTestDirectory(), "source");
        final File sourceDirectory = new File(source, "directory");
        final File sourceFile = new File(sourceDirectory, "hello.txt");
        // Prepare source data
        source.mkdirs();
        sourceDirectory.mkdir();
        FileUtils.writeStringToFile(sourceFile, "HELLO WORLD", "UTF8");
        // Set dates in reverse order to avoid overwriting previous values
        // Also, use full seconds (arguments are in ms) close to today
        // but still highly unlikely to occur in the real world
        sourceFile.setLastModified(1000000002000L);
        sourceDirectory.setLastModified(1000000001000L);
        source.setLastModified(1000000000000L);
        final File target = new File(getTestDirectory(), "target");
        final File targetDirectory = new File(target, "directory");
        final File targetFile = new File(targetDirectory, "hello.txt");
        // Test with preserveFileDate disabled
        FileUtils.copyDirectory(source, target, false);
        Assert.assertTrue((1000000000000L != (target.lastModified())));
        Assert.assertTrue((1000000001000L != (targetDirectory.lastModified())));
        Assert.assertTrue((1000000002000L != (targetFile.lastModified())));
        FileUtils.deleteDirectory(target);
        // Test with preserveFileDate enabled
        FileUtils.copyDirectory(source, target, true);
        Assert.assertEquals(1000000000000L, target.lastModified());
        Assert.assertEquals(1000000001000L, targetDirectory.lastModified());
        Assert.assertEquals(1000000002000L, targetFile.lastModified());
        FileUtils.deleteDirectory(target);
        // also if the target directory already exists (IO-190)
        target.mkdirs();
        FileUtils.copyDirectory(source, target, true);
        Assert.assertEquals(1000000000000L, target.lastModified());
        Assert.assertEquals(1000000001000L, targetDirectory.lastModified());
        Assert.assertEquals(1000000002000L, targetFile.lastModified());
        FileUtils.deleteDirectory(target);
        // also if the target subdirectory already exists (IO-190)
        targetDirectory.mkdirs();
        FileUtils.copyDirectory(source, target, true);
        Assert.assertEquals(1000000000000L, target.lastModified());
        Assert.assertEquals(1000000001000L, targetDirectory.lastModified());
        Assert.assertEquals(1000000002000L, targetFile.lastModified());
        FileUtils.deleteDirectory(target);
    }

    /* Test for IO-141 */
    @Test
    public void testCopyDirectoryToChild() throws Exception {
        final File grandParentDir = new File(getTestDirectory(), "grandparent");
        final File parentDir = new File(grandParentDir, "parent");
        final File childDir = new File(parentDir, "child");
        createFilesForTestCopyDirectory(grandParentDir, parentDir, childDir);
        final long expectedCount = (FileUtilsTestCase.LIST_WALKER.list(grandParentDir).size()) + (FileUtilsTestCase.LIST_WALKER.list(parentDir).size());
        final long expectedSize = (FileUtils.sizeOfDirectory(grandParentDir)) + (FileUtils.sizeOfDirectory(parentDir));
        FileUtils.copyDirectory(parentDir, childDir);
        Assert.assertEquals(expectedCount, FileUtilsTestCase.LIST_WALKER.list(grandParentDir).size());
        Assert.assertEquals(expectedSize, FileUtils.sizeOfDirectory(grandParentDir));
        Assert.assertTrue("Count > 0", (expectedCount > 0));
        Assert.assertTrue("Size > 0", (expectedSize > 0));
    }

    /* Test for IO-141 */
    @Test
    public void testCopyDirectoryToGrandChild() throws Exception {
        final File grandParentDir = new File(getTestDirectory(), "grandparent");
        final File parentDir = new File(grandParentDir, "parent");
        final File childDir = new File(parentDir, "child");
        createFilesForTestCopyDirectory(grandParentDir, parentDir, childDir);
        final long expectedCount = (FileUtilsTestCase.LIST_WALKER.list(grandParentDir).size()) * 2;
        final long expectedSize = (FileUtils.sizeOfDirectory(grandParentDir)) * 2;
        FileUtils.copyDirectory(grandParentDir, childDir);
        Assert.assertEquals(expectedCount, FileUtilsTestCase.LIST_WALKER.list(grandParentDir).size());
        Assert.assertEquals(expectedSize, FileUtils.sizeOfDirectory(grandParentDir));
        Assert.assertTrue("Size > 0", (expectedSize > 0));
    }

    /* Test for IO-217 FileUtils.copyDirectoryToDirectory makes infinite loops */
    @Test
    public void testCopyDirectoryToItself() throws Exception {
        final File dir = new File(getTestDirectory(), "itself");
        dir.mkdirs();
        FileUtils.copyDirectoryToDirectory(dir, dir);
        Assert.assertEquals(1, FileUtilsTestCase.LIST_WALKER.list(dir).size());
    }

    private void createFilesForTestCopyDirectory(final File grandParentDir, final File parentDir, final File childDir) throws Exception {
        final File childDir2 = new File(parentDir, "child2");
        final File grandChildDir = new File(childDir, "grandChild");
        final File grandChild2Dir = new File(childDir2, "grandChild2");
        final File file1 = new File(grandParentDir, "file1.txt");
        final File file2 = new File(parentDir, "file2.txt");
        final File file3 = new File(childDir, "file3.txt");
        final File file4 = new File(childDir2, "file4.txt");
        final File file5 = new File(grandChildDir, "file5.txt");
        final File file6 = new File(grandChild2Dir, "file6.txt");
        FileUtils.deleteDirectory(grandParentDir);
        grandChildDir.mkdirs();
        grandChild2Dir.mkdirs();
        FileUtils.writeStringToFile(file1, "File 1 in grandparent", "UTF8");
        FileUtils.writeStringToFile(file2, "File 2 in parent", "UTF8");
        FileUtils.writeStringToFile(file3, "File 3 in child", "UTF8");
        FileUtils.writeStringToFile(file4, "File 4 in child2", "UTF8");
        FileUtils.writeStringToFile(file5, "File 5 in grandChild", "UTF8");
        FileUtils.writeStringToFile(file6, "File 6 in grandChild2", "UTF8");
    }

    @Test
    public void testCopyDirectoryErrors() throws Exception {
        try {
            FileUtils.copyDirectory(null, null);
            Assert.fail();
        } catch (final NullPointerException ignore) {
        }
        try {
            FileUtils.copyDirectory(new File("a"), null);
            Assert.fail();
        } catch (final NullPointerException ignore) {
        }
        try {
            FileUtils.copyDirectory(null, new File("a"));
            Assert.fail();
        } catch (final NullPointerException ignore) {
        }
        try {
            FileUtils.copyDirectory(new File("doesnt-exist"), new File("a"));
            Assert.fail();
        } catch (final IOException ignore) {
        }
        try {
            FileUtils.copyDirectory(testFile1, new File("a"));
            Assert.fail();
        } catch (final IOException ignore) {
        }
        try {
            FileUtils.copyDirectory(getTestDirectory(), testFile1);
            Assert.fail();
        } catch (final IOException ignore) {
        }
        try {
            FileUtils.copyDirectory(getTestDirectory(), getTestDirectory());
            Assert.fail();
        } catch (final IOException ignore) {
        }
    }

    // forceDelete
    @Test
    public void testForceDeleteAFile1() throws Exception {
        final File destination = new File(getTestDirectory(), "copy1.txt");
        destination.createNewFile();
        Assert.assertTrue("Copy1.txt doesn't exist to delete", destination.exists());
        FileUtils.forceDelete(destination);
        Assert.assertTrue("Check No Exist", (!(destination.exists())));
    }

    @Test
    public void testForceDeleteAFile2() throws Exception {
        final File destination = new File(getTestDirectory(), "copy2.txt");
        destination.createNewFile();
        Assert.assertTrue("Copy2.txt doesn't exist to delete", destination.exists());
        FileUtils.forceDelete(destination);
        Assert.assertTrue("Check No Exist", (!(destination.exists())));
    }

    @Test
    public void testForceDeleteAFile3() throws Exception {
        final File destination = new File(getTestDirectory(), "no_such_file");
        Assert.assertTrue("Check No Exist", (!(destination.exists())));
        try {
            FileUtils.forceDelete(destination);
            Assert.fail("Should generate FileNotFoundException");
        } catch (final FileNotFoundException ignored) {
        }
    }

    // copyFileToDirectory
    @Test
    public void testCopyFile1ToDir() throws Exception {
        final File directory = new File(getTestDirectory(), "subdir");
        if (!(directory.exists())) {
            directory.mkdirs();
        } 
        final File destination = new File(directory, testFile1.getName());
        // Thread.sleep(LAST_MODIFIED_DELAY);
        // This is to slow things down so we can catch if
        // the lastModified date is not ok
        FileUtils.copyFileToDirectory(testFile1, directory);
        Assert.assertTrue("Check Exist", destination.exists());
        Assert.assertEquals("Check Full copy", testFile1Size, destination.length());
        /* disabled: Thread.sleep doesn't work reliantly for this case
        assertTrue("Check last modified date preserved",
        testFile1.lastModified() == destination.lastModified());
         */
        try {
            FileUtils.copyFileToDirectory(destination, directory);
            Assert.fail("Should not be able to copy a file into the same directory as itself");
        } catch (final IOException ioe) {
            // we want that, cannot copy to the same directory as the original file
        }
    }

    @Test
    public void testCopyFile2ToDir() throws Exception {
        final File directory = new File(getTestDirectory(), "subdir");
        if (!(directory.exists())) {
            directory.mkdirs();
        } 
        final File destination = new File(directory, testFile1.getName());
        // Thread.sleep(LAST_MODIFIED_DELAY);
        // This is to slow things down so we can catch if
        // the lastModified date is not ok
        FileUtils.copyFileToDirectory(testFile1, directory);
        Assert.assertTrue("Check Exist", destination.exists());
        Assert.assertEquals("Check Full copy", testFile2Size, destination.length());
        /* disabled: Thread.sleep doesn't work reliantly for this case
        assertTrue("Check last modified date preserved",
        testFile1.lastModified() == destination.lastModified());
         */
    }

    // forceDelete
    @Test
    public void testForceDeleteDir() throws Exception {
        final File testDirectory = getTestDirectory();
        FileUtils.forceDelete(testDirectory.getParentFile());
        Assert.assertTrue("Check No Exist", (!(testDirectory.getParentFile().exists())));
    }

    /* Test the FileUtils implementation. */
    @Test
    public void testFileUtils() throws Exception {
        // Loads file from classpath
        final File file1 = new File(getTestDirectory(), "test.txt");
        final String filename = file1.getAbsolutePath();
        // Create test file on-the-fly (used to be in CVS)
        final OutputStream out = new FileOutputStream(file1);
        try {
            out.write("This is a test".getBytes("UTF-8"));
        } finally {
            out.close();
        }
        final File file2 = new File(getTestDirectory(), "test2.txt");
        FileUtils.writeStringToFile(file2, filename, "UTF-8");
        Assert.assertTrue(file2.exists());
        Assert.assertTrue(((file2.length()) > 0));
        final String file2contents = FileUtils.readFileToString(file2, "UTF-8");
        Assert.assertTrue("Second file's contents correct", filename.equals(file2contents));
        Assert.assertTrue(file2.delete());
        final String contents = FileUtils.readFileToString(new File(filename), "UTF-8");
        Assert.assertEquals("FileUtils.fileRead()", "This is a test", contents);
    }

    @Test
    public void testTouch() throws IOException {
        final File file = new File(getTestDirectory(), "touch.txt");
        if (file.exists()) {
            file.delete();
        } 
        Assert.assertTrue("Bad test: test file still exists", (!(file.exists())));
        FileUtils.touch(file);
        Assert.assertTrue("FileUtils.touch() created file", file.exists());
        final FileOutputStream out = new FileOutputStream(file);
        Assert.assertEquals("Created empty file.", 0, file.length());
        out.write(0);
        out.close();
        Assert.assertEquals("Wrote one byte to file", 1, file.length());
        final long y2k = new GregorianCalendar(2000, 0, 1).getTime().getTime();
        final boolean res = file.setLastModified(y2k);// 0L fails on Win98
        
        Assert.assertEquals("Bad test: set lastModified failed", true, res);
        Assert.assertEquals("Bad test: set lastModified set incorrect value", y2k, file.lastModified());
        final long now = System.currentTimeMillis();
        FileUtils.touch(file);
        Assert.assertEquals("FileUtils.touch() didn't empty the file.", 1, file.length());
        Assert.assertEquals("FileUtils.touch() changed lastModified", false, (y2k == (file.lastModified())));
        Assert.assertEquals("FileUtils.touch() changed lastModified to more than now-3s", true, ((file.lastModified()) >= (now - 3000)));
        Assert.assertEquals("FileUtils.touch() changed lastModified to less than now+3s", true, ((file.lastModified()) <= (now + 3000)));
    }

    @Test
    public void testListFiles() throws Exception {
        final File srcDir = getTestDirectory();
        final File subDir = new File(srcDir, "list_test");
        subDir.mkdir();
        final File subDir2 = new File(subDir, "subdir");
        subDir2.mkdir();
        final String[] fileNames = new String[]{ "a.txt" , "b.txt" , "c.txt" , "d.txt" , "e.txt" , "f.txt" };
        final int[] fileSizes = new int[]{ 123 , 234 , 345 , 456 , 678 , 789 };
        for (int i = 0; i < (fileNames.length); ++i) {
            final File theFile = new File(subDir, fileNames[i]);
            if (!(theFile.getParentFile().exists())) {
                throw new IOException((("Cannot create file " + theFile) + " as the parent directory does not exist"));
            } 
            final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(theFile));
            try {
                TestUtils.generateTestData(output, ((long) (fileSizes[i])));
            } finally {
                IOUtils.closeQuietly(output);
            }
        }
        final Collection<File> files = FileUtils.listFiles(subDir, new WildcardFileFilter("*.*"), new WildcardFileFilter("*"));
        final int count = files.size();
        final Object[] fileObjs = files.toArray();
        Assert.assertEquals(fileNames.length, files.size());
        final Map<String, String> foundFileNames = new HashMap<String, String>();
        for (int i = 0; i < count; ++i) {
            boolean found = false;
            for (int j = 0; (!found) && (j < (fileNames.length)); ++j) {
                if (fileNames[j].equals(((File) (fileObjs[i])).getName())) {
                    foundFileNames.put(fileNames[j], fileNames[j]);
                    found = true;
                } 
            }
        }
        Assert.assertEquals(foundFileNames.size(), fileNames.length);
        subDir.delete();
    }

    @Test
    public void testListFilesWithDirs() throws IOException {
        final File srcDir = getTestDirectory();
        final File subDir1 = new File(srcDir, "subdir");
        subDir1.mkdir();
        final File subDir2 = new File(subDir1, "subdir2");
        subDir2.mkdir();
        final File someFile = new File(subDir2, "a.txt");
        if (!(someFile.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + someFile) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(someFile));
        try {
            TestUtils.generateTestData(output, ((long) (100)));
        } finally {
            IOUtils.closeQuietly(output);
        }
        final File subDir3 = new File(subDir2, "subdir3");
        subDir3.mkdir();
        final Collection<File> files = FileUtils.listFilesAndDirs(subDir1, new WildcardFileFilter("*.*"), new WildcardFileFilter("*"));
        Assert.assertEquals(4, files.size());
        Assert.assertTrue("Should contain the directory.", files.contains(subDir1));
        Assert.assertTrue("Should contain the directory.", files.contains(subDir2));
        Assert.assertTrue("Should contain the file.", files.contains(someFile));
        Assert.assertTrue("Should contain the directory.", files.contains(subDir3));
        subDir1.delete();
    }

    @Test
    public void testIterateFiles() throws Exception {
        final File srcDir = getTestDirectory();
        final File subDir = new File(srcDir, "list_test");
        subDir.mkdir();
        final String[] fileNames = new String[]{ "a.txt" , "b.txt" , "c.txt" , "d.txt" , "e.txt" , "f.txt" };
        final int[] fileSizes = new int[]{ 123 , 234 , 345 , 456 , 678 , 789 };
        for (int i = 0; i < (fileNames.length); ++i) {
            final File theFile = new File(subDir, fileNames[i]);
            if (!(theFile.getParentFile().exists())) {
                throw new IOException((("Cannot create file " + theFile) + " as the parent directory does not exist"));
            } 
            final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(theFile));
            try {
                TestUtils.generateTestData(output, ((long) (fileSizes[i])));
            } finally {
                IOUtils.closeQuietly(output);
            }
        }
        final Iterator<File> files = FileUtils.iterateFiles(subDir, new WildcardFileFilter("*.*"), new WildcardFileFilter("*"));
        final Map<String, String> foundFileNames = new HashMap<String, String>();
        while (files.hasNext()) {
            boolean found = false;
            final String fileName = files.next().getName();
            for (int j = 0; (!found) && (j < (fileNames.length)); ++j) {
                if (fileNames[j].equals(fileName)) {
                    foundFileNames.put(fileNames[j], fileNames[j]);
                    found = true;
                } 
            }
        }
        Assert.assertEquals(foundFileNames.size(), fileNames.length);
        subDir.delete();
    }

    @Test
    public void testIterateFilesAndDirs() throws IOException {
        final File srcDir = getTestDirectory();
        final File subDir1 = new File(srcDir, "subdir");
        subDir1.mkdir();
        final File subDir2 = new File(subDir1, "subdir2");
        subDir2.mkdir();
        final File someFile = new File(subDir2, "a.txt");
        if (!(someFile.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + someFile) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(someFile));
        try {
            TestUtils.generateTestData(output, ((long) (100)));
        } finally {
            IOUtils.closeQuietly(output);
        }
        final File subDir3 = new File(subDir2, "subdir3");
        subDir3.mkdir();
        final Collection<File> filesAndDirs = Arrays.asList(subDir1, subDir2, someFile, subDir3);
        int filesCount = 0;
        final Iterator<File> files = FileUtils.iterateFilesAndDirs(subDir1, new WildcardFileFilter("*.*"), new WildcardFileFilter("*"));
        while (files.hasNext()) {
            filesCount++;
            final File file = files.next();
            Assert.assertTrue("Should contain the directory/file", filesAndDirs.contains(file));
        }
        Assert.assertEquals(filesCount, filesAndDirs.size());
    }

    @Test
    public void testReadFileToStringWithDefaultEncoding() throws Exception {
        final File file = new File(getTestDirectory(), "read.obj");
        final FileOutputStream out = new FileOutputStream(file);
        final byte[] text = "Hello /u1234".getBytes();
        out.write(text);
        out.close();
        final String data = FileUtils.readFileToString(file);
        Assert.assertEquals("Hello /u1234", data);
    }

    @Test
    public void testReadFileToStringWithEncoding() throws Exception {
        final File file = new File(getTestDirectory(), "read.obj");
        final FileOutputStream out = new FileOutputStream(file);
        final byte[] text = "Hello /u1234".getBytes("UTF8");
        out.write(text);
        out.close();
        final String data = FileUtils.readFileToString(file, "UTF8");
        Assert.assertEquals("Hello /u1234", data);
    }

    @Test
    public void testReadFileToByteArray() throws Exception {
        final File file = new File(getTestDirectory(), "read.txt");
        final FileOutputStream out = new FileOutputStream(file);
        out.write(11);
        out.write(21);
        out.write(31);
        out.close();
        final byte[] data = FileUtils.readFileToByteArray(file);
        Assert.assertEquals(3, data.length);
        Assert.assertEquals(11, data[0]);
        Assert.assertEquals(21, data[1]);
        Assert.assertEquals(31, data[2]);
    }

    @Test
    public void testReadLines() throws Exception {
        final File file = TestUtils.newFile(getTestDirectory(), "lines.txt");
        try {
            final String[] data = new String[]{ "hello" , "/u1234" , "" , "this is" , "some text" };
            TestUtils.createLineBasedFile(file, data);
            final List<String> lines = FileUtils.readLines(file, "UTF-8");
            Assert.assertEquals(Arrays.asList(data), lines);
        } finally {
            TestUtils.deleteFile(file);
        }
    }

    @Test
    public void testWriteStringToFile1() throws Exception {
        final File file = new File(getTestDirectory(), "write.txt");
        FileUtils.writeStringToFile(file, "Hello /u1234", "UTF8");
        final byte[] text = "Hello /u1234".getBytes("UTF8");
        TestUtils.assertEqualContent(text, file);
    }

    @Test
    public void testWriteStringToFile2() throws Exception {
        final File file = new File(getTestDirectory(), "write.txt");
        FileUtils.writeStringToFile(file, "Hello /u1234", ((String) (null)));
        final byte[] text = "Hello /u1234".getBytes();
        TestUtils.assertEqualContent(text, file);
    }

    @Test
    public void testWriteStringToFile3() throws Exception {
        final File file = new File(getTestDirectory(), "write.txt");
        FileUtils.writeStringToFile(file, "Hello /u1234", ((Charset) (null)));
        final byte[] text = "Hello /u1234".getBytes();
        TestUtils.assertEqualContent(text, file);
    }

    @Test
    public void testWriteCharSequence1() throws Exception {
        final File file = new File(getTestDirectory(), "write.txt");
        FileUtils.write(file, "Hello /u1234", "UTF8");
        final byte[] text = "Hello /u1234".getBytes("UTF8");
        TestUtils.assertEqualContent(text, file);
    }

    @Test
    public void testWriteCharSequence2() throws Exception {
        final File file = new File(getTestDirectory(), "write.txt");
        FileUtils.write(file, "Hello /u1234", ((String) (null)));
        final byte[] text = "Hello /u1234".getBytes();
        TestUtils.assertEqualContent(text, file);
    }

    @Test
    public void testWriteByteArrayToFile() throws Exception {
        final File file = new File(getTestDirectory(), "write.obj");
        final byte[] data = new byte[]{ 11 , 21 , 31 };
        FileUtils.writeByteArrayToFile(file, data);
        TestUtils.assertEqualContent(data, file);
    }

    @Test
    public void testWriteByteArrayToFile_WithOffsetAndLength() throws Exception {
        final File file = new File(getTestDirectory(), "write.obj");
        final byte[] data = new byte[]{ 11 , 21 , 32 , 41 , 51 };
        final byte[] writtenData = new byte[3];
        System.arraycopy(data, 1, writtenData, 0, 3);
        FileUtils.writeByteArrayToFile(file, data, 1, 3);
        TestUtils.assertEqualContent(writtenData, file);
    }

    @Test
    public void testWriteLines_4arg() throws Exception {
        final Object[] data = new Object[]{ "hello" , new StringBuffer("world") , "" , "this is" , null , "some text" };
        final List<Object> list = Arrays.asList(data);
        final File file = TestUtils.newFile(getTestDirectory(), "lines.txt");
        FileUtils.writeLines(file, "US-ASCII", list, "*");
        final String expected = "hello*world**this is**some text*";
        final String actual = FileUtils.readFileToString(file, "US-ASCII");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testWriteLines_4arg_Writer_nullData() throws Exception {
        final File file = TestUtils.newFile(getTestDirectory(), "lines.txt");
        FileUtils.writeLines(file, "US-ASCII", null, "*");
        Assert.assertEquals("Sizes differ", 0, file.length());
    }

    @Test
    public void testWriteLines_4arg_nullSeparator() throws Exception {
        final Object[] data = new Object[]{ "hello" , new StringBuffer("world") , "" , "this is" , null , "some text" };
        final List<Object> list = Arrays.asList(data);
        final File file = TestUtils.newFile(getTestDirectory(), "lines.txt");
        FileUtils.writeLines(file, "US-ASCII", list, null);
        final String expected = (((((((("hello" + (IOUtils.LINE_SEPARATOR)) + "world") + (IOUtils.LINE_SEPARATOR)) + (IOUtils.LINE_SEPARATOR)) + "this is") + (IOUtils.LINE_SEPARATOR)) + (IOUtils.LINE_SEPARATOR)) + "some text") + (IOUtils.LINE_SEPARATOR);
        final String actual = FileUtils.readFileToString(file, "US-ASCII");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testWriteLines_3arg_nullSeparator() throws Exception {
        final Object[] data = new Object[]{ "hello" , new StringBuffer("world") , "" , "this is" , null , "some text" };
        final List<Object> list = Arrays.asList(data);
        final File file = TestUtils.newFile(getTestDirectory(), "lines.txt");
        FileUtils.writeLines(file, "US-ASCII", list);
        final String expected = (((((((("hello" + (IOUtils.LINE_SEPARATOR)) + "world") + (IOUtils.LINE_SEPARATOR)) + (IOUtils.LINE_SEPARATOR)) + "this is") + (IOUtils.LINE_SEPARATOR)) + (IOUtils.LINE_SEPARATOR)) + "some text") + (IOUtils.LINE_SEPARATOR);
        final String actual = FileUtils.readFileToString(file, "US-ASCII");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testWriteLines_5argsWithAppendOptionTrue_ShouldNotDeletePreviousFileLines() throws Exception {
        final File file = TestUtils.newFile(getTestDirectory(), "lines.txt");
        FileUtils.writeStringToFile(file, "This line was there before you...");
        final List<String> linesToAppend = Arrays.asList("my first line", "The second Line");
        FileUtils.writeLines(file, null, linesToAppend, null, true);
        final String expected = ((("This line was there before you..." + "my first line") + (IOUtils.LINE_SEPARATOR)) + "The second Line") + (IOUtils.LINE_SEPARATOR);
        final String actual = FileUtils.readFileToString(file);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testWriteLines_5argsWithAppendOptionFalse_ShouldDeletePreviousFileLines() throws Exception {
        final File file = TestUtils.newFile(getTestDirectory(), "lines.txt");
        FileUtils.writeStringToFile(file, "This line was there before you...");
        final List<String> linesToAppend = Arrays.asList("my first line", "The second Line");
        FileUtils.writeLines(file, null, linesToAppend, null, false);
        final String expected = (("my first line" + (IOUtils.LINE_SEPARATOR)) + "The second Line") + (IOUtils.LINE_SEPARATOR);
        final String actual = FileUtils.readFileToString(file);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testWriteLines_4argsWithAppendOptionTrue_ShouldNotDeletePreviousFileLines() throws Exception {
        final File file = TestUtils.newFile(getTestDirectory(), "lines.txt");
        FileUtils.writeStringToFile(file, "This line was there before you...");
        final List<String> linesToAppend = Arrays.asList("my first line", "The second Line");
        FileUtils.writeLines(file, linesToAppend, null, true);
        final String expected = ((("This line was there before you..." + "my first line") + (IOUtils.LINE_SEPARATOR)) + "The second Line") + (IOUtils.LINE_SEPARATOR);
        final String actual = FileUtils.readFileToString(file);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testWriteLines_4argsWithAppendOptionFalse_ShouldDeletePreviousFileLines() throws Exception {
        final File file = TestUtils.newFile(getTestDirectory(), "lines.txt");
        FileUtils.writeStringToFile(file, "This line was there before you...");
        final List<String> linesToAppend = Arrays.asList("my first line", "The second Line");
        FileUtils.writeLines(file, linesToAppend, null, false);
        final String expected = (("my first line" + (IOUtils.LINE_SEPARATOR)) + "The second Line") + (IOUtils.LINE_SEPARATOR);
        final String actual = FileUtils.readFileToString(file);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testWriteLinesEncoding_WithAppendOptionTrue_ShouldNotDeletePreviousFileLines() throws Exception {
        final File file = TestUtils.newFile(getTestDirectory(), "lines.txt");
        FileUtils.writeStringToFile(file, "This line was there before you...");
        final List<String> linesToAppend = Arrays.asList("my first line", "The second Line");
        FileUtils.writeLines(file, null, linesToAppend, true);
        final String expected = ((("This line was there before you..." + "my first line") + (IOUtils.LINE_SEPARATOR)) + "The second Line") + (IOUtils.LINE_SEPARATOR);
        final String actual = FileUtils.readFileToString(file);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testWriteLinesEncoding_WithAppendOptionFalse_ShouldDeletePreviousFileLines() throws Exception {
        final File file = TestUtils.newFile(getTestDirectory(), "lines.txt");
        FileUtils.writeStringToFile(file, "This line was there before you...");
        final List<String> linesToAppend = Arrays.asList("my first line", "The second Line");
        FileUtils.writeLines(file, null, linesToAppend, false);
        final String expected = (("my first line" + (IOUtils.LINE_SEPARATOR)) + "The second Line") + (IOUtils.LINE_SEPARATOR);
        final String actual = FileUtils.readFileToString(file);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testWriteLines_3argsWithAppendOptionTrue_ShouldNotDeletePreviousFileLines() throws Exception {
        final File file = TestUtils.newFile(getTestDirectory(), "lines.txt");
        FileUtils.writeStringToFile(file, "This line was there before you...");
        final List<String> linesToAppend = Arrays.asList("my first line", "The second Line");
        FileUtils.writeLines(file, linesToAppend, true);
        final String expected = ((("This line was there before you..." + "my first line") + (IOUtils.LINE_SEPARATOR)) + "The second Line") + (IOUtils.LINE_SEPARATOR);
        final String actual = FileUtils.readFileToString(file);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testWriteLines_3argsWithAppendOptionFalse_ShouldDeletePreviousFileLines() throws Exception {
        final File file = TestUtils.newFile(getTestDirectory(), "lines.txt");
        FileUtils.writeStringToFile(file, "This line was there before you...");
        final List<String> linesToAppend = Arrays.asList("my first line", "The second Line");
        FileUtils.writeLines(file, linesToAppend, false);
        final String expected = (("my first line" + (IOUtils.LINE_SEPARATOR)) + "The second Line") + (IOUtils.LINE_SEPARATOR);
        final String actual = FileUtils.readFileToString(file);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testWriteStringToFileWithEncoding_WithAppendOptionTrue_ShouldNotDeletePreviousFileLines() throws Exception {
        final File file = TestUtils.newFile(getTestDirectory(), "lines.txt");
        FileUtils.writeStringToFile(file, "This line was there before you...");
        FileUtils.writeStringToFile(file, "this is brand new data", ((String) (null)), true);
        final String expected = "This line was there before you..." + "this is brand new data";
        final String actual = FileUtils.readFileToString(file);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testWriteStringToFileWithEncoding_WithAppendOptionFalse_ShouldDeletePreviousFileLines() throws Exception {
        final File file = TestUtils.newFile(getTestDirectory(), "lines.txt");
        FileUtils.writeStringToFile(file, "This line was there before you...");
        FileUtils.writeStringToFile(file, "this is brand new data", ((String) (null)), false);
        final String expected = "this is brand new data";
        final String actual = FileUtils.readFileToString(file);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testWriteStringToFile_WithAppendOptionTrue_ShouldNotDeletePreviousFileLines() throws Exception {
        final File file = TestUtils.newFile(getTestDirectory(), "lines.txt");
        FileUtils.writeStringToFile(file, "This line was there before you...");
        FileUtils.writeStringToFile(file, "this is brand new data", true);
        final String expected = "This line was there before you..." + "this is brand new data";
        final String actual = FileUtils.readFileToString(file);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testWriteStringToFile_WithAppendOptionFalse_ShouldDeletePreviousFileLines() throws Exception {
        final File file = TestUtils.newFile(getTestDirectory(), "lines.txt");
        FileUtils.writeStringToFile(file, "This line was there before you...");
        FileUtils.writeStringToFile(file, "this is brand new data", false);
        final String expected = "this is brand new data";
        final String actual = FileUtils.readFileToString(file);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testWriteWithEncoding_WithAppendOptionTrue_ShouldNotDeletePreviousFileLines() throws Exception {
        final File file = TestUtils.newFile(getTestDirectory(), "lines.txt");
        FileUtils.writeStringToFile(file, "This line was there before you...");
        FileUtils.write(file, "this is brand new data", ((String) (null)), true);
        final String expected = "This line was there before you..." + "this is brand new data";
        final String actual = FileUtils.readFileToString(file);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testWriteWithEncoding_WithAppendOptionFalse_ShouldDeletePreviousFileLines() throws Exception {
        final File file = TestUtils.newFile(getTestDirectory(), "lines.txt");
        FileUtils.writeStringToFile(file, "This line was there before you...");
        FileUtils.write(file, "this is brand new data", ((String) (null)), false);
        final String expected = "this is brand new data";
        final String actual = FileUtils.readFileToString(file);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testWrite_WithAppendOptionTrue_ShouldNotDeletePreviousFileLines() throws Exception {
        final File file = TestUtils.newFile(getTestDirectory(), "lines.txt");
        FileUtils.writeStringToFile(file, "This line was there before you...");
        FileUtils.write(file, "this is brand new data", true);
        final String expected = "This line was there before you..." + "this is brand new data";
        final String actual = FileUtils.readFileToString(file);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testWrite_WithAppendOptionFalse_ShouldDeletePreviousFileLines() throws Exception {
        final File file = TestUtils.newFile(getTestDirectory(), "lines.txt");
        FileUtils.writeStringToFile(file, "This line was there before you...");
        FileUtils.write(file, "this is brand new data", false);
        final String expected = "this is brand new data";
        final String actual = FileUtils.readFileToString(file);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testWriteByteArrayToFile_WithAppendOptionTrue_ShouldNotDeletePreviousFileLines() throws Exception {
        final File file = TestUtils.newFile(getTestDirectory(), "lines.txt");
        FileUtils.writeStringToFile(file, "This line was there before you...");
        FileUtils.writeByteArrayToFile(file, "this is brand new data".getBytes(), true);
        final String expected = "This line was there before you..." + "this is brand new data";
        final String actual = FileUtils.readFileToString(file);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testWriteByteArrayToFile_WithAppendOptionFalse_ShouldDeletePreviousFileLines() throws Exception {
        final File file = TestUtils.newFile(getTestDirectory(), "lines.txt");
        FileUtils.writeStringToFile(file, "This line was there before you...");
        FileUtils.writeByteArrayToFile(file, "this is brand new data".getBytes(), false);
        final String expected = "this is brand new data";
        final String actual = FileUtils.readFileToString(file);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testWriteByteArrayToFile_WithOffsetAndLength_WithAppendOptionTrue_ShouldNotDeletePreviousFileLines() throws Exception {
        final File file = TestUtils.newFile(getTestDirectory(), "lines.txt");
        FileUtils.writeStringToFile(file, "This line was there before you...");
        final byte[] data = "SKIP_THIS_this is brand new data_AND_SKIP_THIS".getBytes(Charsets.UTF_8);
        FileUtils.writeByteArrayToFile(file, data, 10, 22, true);
        final String expected = "This line was there before you..." + "this is brand new data";
        final String actual = FileUtils.readFileToString(file, Charsets.UTF_8);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testWriteByteArrayToFile_WithOffsetAndLength_WithAppendOptionTrue_ShouldDeletePreviousFileLines() throws Exception {
        final File file = TestUtils.newFile(getTestDirectory(), "lines.txt");
        FileUtils.writeStringToFile(file, "This line was there before you...");
        final byte[] data = "SKIP_THIS_this is brand new data_AND_SKIP_THIS".getBytes(Charsets.UTF_8);
        FileUtils.writeByteArrayToFile(file, data, 10, 22, false);
        final String expected = "this is brand new data";
        final String actual = FileUtils.readFileToString(file, Charsets.UTF_8);
        Assert.assertEquals(expected, actual);
    }

    // -----------------------------------------------------------------------
    @Test
    public void testChecksumCRC32() throws Exception {
        // create a test file
        final String text = "Imagination is more important than knowledge - Einstein";
        final File file = new File(getTestDirectory(), "checksum-test.txt");
        FileUtils.writeStringToFile(file, text, "US-ASCII");
        // compute the expected checksum
        final Checksum expectedChecksum = new CRC32();
        expectedChecksum.update(text.getBytes("US-ASCII"), 0, text.length());
        final long expectedValue = expectedChecksum.getValue();
        // compute the checksum of the file
        final long resultValue = FileUtils.checksumCRC32(file);
        Assert.assertEquals(expectedValue, resultValue);
    }

    @Test
    public void testChecksum() throws Exception {
        // create a test file
        final String text = "Imagination is more important than knowledge - Einstein";
        final File file = new File(getTestDirectory(), "checksum-test.txt");
        FileUtils.writeStringToFile(file, text, "US-ASCII");
        // compute the expected checksum
        final Checksum expectedChecksum = new CRC32();
        expectedChecksum.update(text.getBytes("US-ASCII"), 0, text.length());
        final long expectedValue = expectedChecksum.getValue();
        // compute the checksum of the file
        final Checksum testChecksum = new CRC32();
        final Checksum resultChecksum = FileUtils.checksum(file, testChecksum);
        final long resultValue = resultChecksum.getValue();
        Assert.assertSame(testChecksum, resultChecksum);
        Assert.assertEquals(expectedValue, resultValue);
    }

    @Test
    public void testChecksumOnNullFile() throws Exception {
        try {
            FileUtils.checksum(null, new CRC32());
            Assert.fail();
        } catch (final NullPointerException ex) {
            // expected
        }
    }

    @Test
    public void testChecksumOnNullChecksum() throws Exception {
        // create a test file
        final String text = "Imagination is more important than knowledge - Einstein";
        final File file = new File(getTestDirectory(), "checksum-test.txt");
        FileUtils.writeStringToFile(file, text, "US-ASCII");
        try {
            FileUtils.checksum(file, null);
            Assert.fail();
        } catch (final NullPointerException ex) {
            // expected
        }
    }

    @Test
    public void testChecksumOnDirectory() throws Exception {
        try {
            FileUtils.checksum(new File("."), new CRC32());
            Assert.fail();
        } catch (final IllegalArgumentException ex) {
            // expected
        }
    }

    @Test
    public void testChecksumDouble() throws Exception {
        // create a test file
        final String text1 = "Imagination is more important than knowledge - Einstein";
        final File file1 = new File(getTestDirectory(), "checksum-test.txt");
        FileUtils.writeStringToFile(file1, text1, "US-ASCII");
        // create a second test file
        final String text2 = "To be or not to be - Shakespeare";
        final File file2 = new File(getTestDirectory(), "checksum-test2.txt");
        FileUtils.writeStringToFile(file2, text2, "US-ASCII");
        // compute the expected checksum
        final Checksum expectedChecksum = new CRC32();
        expectedChecksum.update(text1.getBytes("US-ASCII"), 0, text1.length());
        expectedChecksum.update(text2.getBytes("US-ASCII"), 0, text2.length());
        final long expectedValue = expectedChecksum.getValue();
        // compute the checksum of the file
        final Checksum testChecksum = new CRC32();
        FileUtils.checksum(file1, testChecksum);
        FileUtils.checksum(file2, testChecksum);
        final long resultValue = testChecksum.getValue();
        Assert.assertEquals(expectedValue, resultValue);
    }

    @Test
    public void testDeleteDirectoryWithNonDirectory() throws Exception {
        try {
            FileUtils.deleteDirectory(testFile1);
            Assert.fail();
        } catch (final IllegalArgumentException ex) {
            // expected
        }
    }

    @Test
    public void testDeleteQuietlyForNull() {
        try {
            FileUtils.deleteQuietly(null);
        } catch (final Exception ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void testDeleteQuietlyDir() throws IOException {
        final File testDirectory = new File(getTestDirectory(), "testDeleteQuietlyDir");
        final File testFile = new File(testDirectory, "testDeleteQuietlyFile");
        testDirectory.mkdirs();
        if (!(testFile.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + testFile) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(testFile));
        try {
            TestUtils.generateTestData(output, ((long) (0)));
        } finally {
            IOUtils.closeQuietly(output);
        }
        Assert.assertTrue(testDirectory.exists());
        Assert.assertTrue(testFile.exists());
        FileUtils.deleteQuietly(testDirectory);
        Assert.assertFalse("Check No Exist", testDirectory.exists());
        Assert.assertFalse("Check No Exist", testFile.exists());
    }

    @Test
    public void testDeleteQuietlyFile() throws IOException {
        final File testFile = new File(getTestDirectory(), "testDeleteQuietlyFile");
        if (!(testFile.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + testFile) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(testFile));
        try {
            TestUtils.generateTestData(output, ((long) (0)));
        } finally {
            IOUtils.closeQuietly(output);
        }
        Assert.assertTrue(testFile.exists());
        FileUtils.deleteQuietly(testFile);
        Assert.assertFalse("Check No Exist", testFile.exists());
    }

    @Test
    public void testDeleteQuietlyNonExistent() {
        final File testFile = new File("testDeleteQuietlyNonExistent");
        Assert.assertFalse(testFile.exists());
        try {
            FileUtils.deleteQuietly(testFile);
        } catch (final Exception ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void testMoveFile_Rename() throws Exception {
        final File destination = new File(getTestDirectory(), "move1.txt");
        FileUtils.moveFile(testFile1, destination);
        Assert.assertTrue("Check Exist", destination.exists());
        Assert.assertTrue("Original deleted", (!(testFile1.exists())));
    }

    @Test
    public void testMoveFile_CopyDelete() throws Exception {
        final File destination = new File(getTestDirectory(), "move2.txt");
        final File src = new File(testFile1.getAbsolutePath()) {
            private static final long serialVersionUID = 1L;

            // Force renameTo to fail, as if destination is on another
            // filesystem
            @Override
            public boolean renameTo(final File f) {
                return false;
            }
        };
        FileUtils.moveFile(src, destination);
        Assert.assertTrue("Check Exist", destination.exists());
        Assert.assertTrue("Original deleted", (!(src.exists())));
    }

    @Test
    public void testMoveFile_CopyDelete_Failed() throws Exception {
        final File destination = new File(getTestDirectory(), "move3.txt");
        final File src = new File(testFile1.getAbsolutePath()) {
            private static final long serialVersionUID = 1L;

            // Force renameTo to fail, as if destination is on another
            // filesystem
            @Override
            public boolean renameTo(final File f) {
                return false;
            }

            // Force delete failure
            @Override
            public boolean delete() {
                return false;
            }
        };
        try {
            FileUtils.moveFile(src, destination);
            Assert.fail("move should have failed as src has not been deleted");
        } catch (final IOException e) {
            // exepected
            Assert.assertTrue("Check Rollback", (!(destination.exists())));
            Assert.assertTrue("Original exists", src.exists());
        }
    }

    @Test
    public void testMoveFile_Errors() throws Exception {
        try {
            FileUtils.moveFile(null, new File("foo"));
            Assert.fail("Expected NullPointerException when source is null");
        } catch (final NullPointerException e) {
            // expected
        }
        try {
            FileUtils.moveFile(new File("foo"), null);
            Assert.fail("Expected NullPointerException when destination is null");
        } catch (final NullPointerException e) {
            // expected
        }
        try {
            FileUtils.moveFile(new File("nonexistant"), new File("foo"));
            Assert.fail("Expected FileNotFoundException for source");
        } catch (final FileNotFoundException e) {
            // expected
        }
        try {
            FileUtils.moveFile(getTestDirectory(), new File("foo"));
            Assert.fail("Expected IOException when source is a directory");
        } catch (final IOException e) {
            // expected
        }
        final File testSourceFile = new File(getTestDirectory(), "testMoveFileSource");
        final File testDestFile = new File(getTestDirectory(), "testMoveFileSource");
        if (!(testSourceFile.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + testSourceFile) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output1 = new BufferedOutputStream(new FileOutputStream(testSourceFile));
        try {
            TestUtils.generateTestData(output1, ((long) (0)));
        } finally {
            IOUtils.closeQuietly(output1);
        }
        if (!(testDestFile.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + testDestFile) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(testDestFile));
        try {
            TestUtils.generateTestData(output, ((long) (0)));
        } finally {
            IOUtils.closeQuietly(output);
        }
        try {
            FileUtils.moveFile(testSourceFile, testDestFile);
            Assert.fail("Expected FileExistsException when dest already exists");
        } catch (final FileExistsException e) {
            // expected
        }
    }

    @Test
    public void testMoveFileToDirectory() throws Exception {
        final File destDir = new File(getTestDirectory(), "moveFileDestDir");
        final File movedFile = new File(destDir, testFile1.getName());
        Assert.assertFalse("Check Exist before", destDir.exists());
        Assert.assertFalse("Check Exist before", movedFile.exists());
        FileUtils.moveFileToDirectory(testFile1, destDir, true);
        Assert.assertTrue("Check Exist after", movedFile.exists());
        Assert.assertTrue("Original deleted", (!(testFile1.exists())));
    }

    @Test
    public void testMoveFileToDirectory_Errors() throws Exception {
        try {
            FileUtils.moveFileToDirectory(null, new File("foo"), true);
            Assert.fail("Expected NullPointerException when source is null");
        } catch (final NullPointerException e) {
            // expected
        }
        try {
            FileUtils.moveFileToDirectory(new File("foo"), null, true);
            Assert.fail("Expected NullPointerException when destination is null");
        } catch (final NullPointerException e) {
            // expected
        }
        final File testFile1 = new File(getTestDirectory(), "testMoveFileFile1");
        final File testFile2 = new File(getTestDirectory(), "testMoveFileFile2");
        if (!(testFile1.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + testFile1) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output1 = new BufferedOutputStream(new FileOutputStream(testFile1));
        try {
            TestUtils.generateTestData(output1, ((long) (0)));
        } finally {
            IOUtils.closeQuietly(output1);
        }
        if (!(testFile2.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + testFile2) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(testFile2));
        try {
            TestUtils.generateTestData(output, ((long) (0)));
        } finally {
            IOUtils.closeQuietly(output);
        }
        try {
            FileUtils.moveFileToDirectory(testFile1, testFile2, true);
            Assert.fail("Expected IOException when dest not a directory");
        } catch (final IOException e) {
            // expected
        }
        final File nonexistant = new File(getTestDirectory(), "testMoveFileNonExistant");
        try {
            FileUtils.moveFileToDirectory(testFile1, nonexistant, false);
            Assert.fail("Expected IOException when dest does not exist and create=false");
        } catch (final IOException e) {
            // expected
        }
    }

    @Test
    public void testMoveDirectory_Rename() throws Exception {
        final File dir = getTestDirectory();
        final File src = new File(dir, "testMoveDirectory1Source");
        final File testDir = new File(src, "foo");
        final File testFile = new File(testDir, "bar");
        testDir.mkdirs();
        if (!(testFile.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + testFile) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(testFile));
        try {
            TestUtils.generateTestData(output, ((long) (0)));
        } finally {
            IOUtils.closeQuietly(output);
        }
        final File destination = new File(dir, "testMoveDirectory1Dest");
        FileUtils.deleteDirectory(destination);
        // Move the directory
        FileUtils.moveDirectory(src, destination);
        // Check results
        Assert.assertTrue("Check Exist", destination.exists());
        Assert.assertTrue("Original deleted", (!(src.exists())));
        final File movedDir = new File(destination, testDir.getName());
        final File movedFile = new File(movedDir, testFile.getName());
        Assert.assertTrue("Check dir moved", movedDir.exists());
        Assert.assertTrue("Check file moved", movedFile.exists());
    }

    @Test
    public void testMoveDirectory_CopyDelete() throws Exception {
        final File dir = getTestDirectory();
        final File src = new File(dir, "testMoveDirectory2Source") {
            private static final long serialVersionUID = 1L;

            // Force renameTo to fail
            @Override
            public boolean renameTo(final File dest) {
                return false;
            }
        };
        final File testDir = new File(src, "foo");
        final File testFile = new File(testDir, "bar");
        testDir.mkdirs();
        if (!(testFile.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + testFile) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(testFile));
        try {
            TestUtils.generateTestData(output, ((long) (0)));
        } finally {
            IOUtils.closeQuietly(output);
        }
        final File destination = new File(dir, "testMoveDirectory1Dest");
        FileUtils.deleteDirectory(destination);
        // Move the directory
        FileUtils.moveDirectory(src, destination);
        // Check results
        Assert.assertTrue("Check Exist", destination.exists());
        Assert.assertTrue("Original deleted", (!(src.exists())));
        final File movedDir = new File(destination, testDir.getName());
        final File movedFile = new File(movedDir, testFile.getName());
        Assert.assertTrue("Check dir moved", movedDir.exists());
        Assert.assertTrue("Check file moved", movedFile.exists());
    }

    @Test
    public void testMoveDirectory_Errors() throws Exception {
        try {
            FileUtils.moveDirectory(null, new File("foo"));
            Assert.fail("Expected NullPointerException when source is null");
        } catch (final NullPointerException e) {
            // expected
        }
        try {
            FileUtils.moveDirectory(new File("foo"), null);
            Assert.fail("Expected NullPointerException when destination is null");
        } catch (final NullPointerException e) {
            // expected
        }
        try {
            FileUtils.moveDirectory(new File("nonexistant"), new File("foo"));
            Assert.fail("Expected FileNotFoundException for source");
        } catch (final FileNotFoundException e) {
            // expected
        }
        final File testFile = new File(getTestDirectory(), "testMoveDirectoryFile");
        if (!(testFile.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + testFile) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(testFile));
        try {
            TestUtils.generateTestData(output, ((long) (0)));
        } finally {
            IOUtils.closeQuietly(output);
        }
        try {
            FileUtils.moveDirectory(testFile, new File("foo"));
            Assert.fail("Expected IOException when source is not a directory");
        } catch (final IOException e) {
            // expected
        }
        final File testSrcFile = new File(getTestDirectory(), "testMoveDirectorySource");
        final File testDestFile = new File(getTestDirectory(), "testMoveDirectoryDest");
        testSrcFile.mkdir();
        testDestFile.mkdir();
        try {
            FileUtils.moveDirectory(testSrcFile, testDestFile);
            Assert.fail("Expected FileExistsException when dest already exists");
        } catch (final FileExistsException e) {
            // expected
        }
    }

    @Test
    public void testMoveDirectoryToDirectory() throws Exception {
        final File dir = getTestDirectory();
        final File src = new File(dir, "testMoveDirectory1Source");
        final File testChildDir = new File(src, "foo");
        final File testFile = new File(testChildDir, "bar");
        testChildDir.mkdirs();
        if (!(testFile.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + testFile) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(testFile));
        try {
            TestUtils.generateTestData(output, ((long) (0)));
        } finally {
            IOUtils.closeQuietly(output);
        }
        final File destDir = new File(dir, "testMoveDirectory1Dest");
        FileUtils.deleteDirectory(destDir);
        Assert.assertFalse("Check Exist before", destDir.exists());
        // Move the directory
        FileUtils.moveDirectoryToDirectory(src, destDir, true);
        // Check results
        Assert.assertTrue("Check Exist after", destDir.exists());
        Assert.assertTrue("Original deleted", (!(src.exists())));
        final File movedDir = new File(destDir, src.getName());
        final File movedChildDir = new File(movedDir, testChildDir.getName());
        final File movedFile = new File(movedChildDir, testFile.getName());
        Assert.assertTrue("Check dir moved", movedDir.exists());
        Assert.assertTrue("Check child dir moved", movedChildDir.exists());
        Assert.assertTrue("Check file moved", movedFile.exists());
    }

    @Test
    public void testMoveDirectoryToDirectory_Errors() throws Exception {
        try {
            FileUtils.moveDirectoryToDirectory(null, new File("foo"), true);
            Assert.fail("Expected NullPointerException when source is null");
        } catch (final NullPointerException e) {
            // expected
        }
        try {
            FileUtils.moveDirectoryToDirectory(new File("foo"), null, true);
            Assert.fail("Expected NullPointerException when destination is null");
        } catch (final NullPointerException e) {
            // expected
        }
        final File testFile1 = new File(getTestDirectory(), "testMoveFileFile1");
        final File testFile2 = new File(getTestDirectory(), "testMoveFileFile2");
        if (!(testFile1.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + testFile1) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output1 = new BufferedOutputStream(new FileOutputStream(testFile1));
        try {
            TestUtils.generateTestData(output1, ((long) (0)));
        } finally {
            IOUtils.closeQuietly(output1);
        }
        if (!(testFile2.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + testFile2) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(testFile2));
        try {
            TestUtils.generateTestData(output, ((long) (0)));
        } finally {
            IOUtils.closeQuietly(output);
        }
        try {
            FileUtils.moveDirectoryToDirectory(testFile1, testFile2, true);
            Assert.fail("Expected IOException when dest not a directory");
        } catch (final IOException e) {
            // expected
        }
        final File nonexistant = new File(getTestDirectory(), "testMoveFileNonExistant");
        try {
            FileUtils.moveDirectoryToDirectory(testFile1, nonexistant, false);
            Assert.fail("Expected IOException when dest does not exist and create=false");
        } catch (final IOException e) {
            // expected
        }
    }

    @Test
    public void testMoveToDirectory() throws Exception {
        final File destDir = new File(getTestDirectory(), "testMoveToDirectoryDestDir");
        final File testDir = new File(getTestDirectory(), "testMoveToDirectoryTestDir");
        final File testFile = new File(getTestDirectory(), "testMoveToDirectoryTestFile");
        testDir.mkdirs();
        if (!(testFile.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + testFile) + " as the parent directory does not exist"));
        } 
        final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(testFile));
        try {
            TestUtils.generateTestData(output, ((long) (0)));
        } finally {
            IOUtils.closeQuietly(output);
        }
        final File movedFile = new File(destDir, testFile.getName());
        final File movedDir = new File(destDir, testFile.getName());
        Assert.assertFalse("Check File Doesnt exist", movedFile.exists());
        Assert.assertFalse("Check Dir Doesnt exist", movedDir.exists());
        // Test moving a file
        FileUtils.moveToDirectory(testFile, destDir, true);
        Assert.assertTrue("Check File exists", movedFile.exists());
        Assert.assertFalse("Check Original File doesn't exist", testFile.exists());
        // Test moving a directory
        FileUtils.moveToDirectory(testDir, destDir, true);
        Assert.assertTrue("Check Dir exists", movedDir.exists());
        Assert.assertFalse("Check Original Dir doesn't exist", testDir.exists());
    }

    @Test
    public void testMoveToDirectory_Errors() throws Exception {
        try {
            FileUtils.moveDirectoryToDirectory(null, new File("foo"), true);
            Assert.fail("Expected NullPointerException when source is null");
        } catch (final NullPointerException e) {
            // expected
        }
        try {
            FileUtils.moveDirectoryToDirectory(new File("foo"), null, true);
            Assert.fail("Expected NullPointerException when destination is null");
        } catch (final NullPointerException e) {
            // expected
        }
        final File nonexistant = new File(getTestDirectory(), "nonexistant");
        final File destDir = new File(getTestDirectory(), "MoveToDirectoryDestDir");
        try {
            FileUtils.moveToDirectory(nonexistant, destDir, true);
            Assert.fail("Expected IOException when source does not exist");
        } catch (final IOException e) {
            // expected
        }
    }

    @Test
    public void testIO300() throws Exception {
        final File testDirectory = getTestDirectory();
        final File src = new File(testDirectory, "dir1");
        final File dest = new File(src, "dir2");
        Assert.assertTrue(dest.mkdirs());
        Assert.assertTrue(src.exists());
        try {
            FileUtils.moveDirectoryToDirectory(src, dest, false);
            Assert.fail("expected IOException");
        } catch (final IOException ioe) {
            // expected
        }
        Assert.assertTrue(src.exists());
    }

    @Test
    public void testIO276() throws Exception {
        final File dir = new File("target", "IO276");
        Assert.assertTrue((dir + " should not be present"), dir.mkdirs());
        final File file = new File(dir, "IO276.txt");
        Assert.assertTrue((file + " should not be present"), file.createNewFile());
        FileUtils.forceDeleteOnExit(dir);
        // If this does not work, test will fail next time (assuming target is not cleaned)
    }

    // Test helper class to pretend a file is shorter than it is
    private static class ShorterFile extends File {
        private static final long serialVersionUID = 1L;

        public ShorterFile(String pathname) {
            super(pathname);
        }

        @Override
        public long length() {
            return (super.length()) - 1;
        }
    }

    // This test relies on FileUtils.copyFile using File.length to check the output size
    @Test
    public void testIncorrectOutputSize() throws Exception {
        File inFile = new File("pom.xml");
        File outFile = new FileUtilsTestCase.ShorterFile("target/pom.tmp");// it will report a shorter file
        
        try {
            FileUtils.copyFile(inFile, outFile);
            Assert.fail("Expected IOException");
        } catch (Exception e) {
            final String msg = e.toString();
            Assert.assertTrue(msg, msg.contains("Failed to copy full contents"));
        } finally {
            outFile.delete();// tidy up
            
        }
    }

    /**
     * DirectoryWalker implementation that recursively lists all files and directories.
     */
    static class ListDirectoryWalker extends DirectoryWalker<File> {
        ListDirectoryWalker() {
            super();
        }

        List<File> list(final File startDirectory) throws IOException {
            final ArrayList<File> files = new ArrayList<File>();
            walk(startDirectory, files);
            return files;
        }

        @Override
        protected void handleDirectoryStart(final File directory, final int depth, final Collection<File> results) throws IOException {
            // Add all directories except the starting directory
            if (depth > 0) {
                results.add(directory);
            } 
        }

        @Override
        protected void handleFile(final File file, final int depth, final Collection<File> results) throws IOException {
            results.add(file);
        }
    }
}

