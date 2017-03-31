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


package org.apache.commons.io.input;

import org.junit.After;
import java.util.Arrays;
import java.util.Collection;
import java.io.File;
import java.io.IOException;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runner.RunWith;
import org.junit.Test;
import java.net.URISyntaxException;
import java.io.UnsupportedEncodingException;

@RunWith(value = Parameterized.class)
public class ReversedLinesFileReaderTestParamBlockSize {
    private static final String UTF_8 = "UTF-8";

    private static final String ISO_8859_1 = "ISO-8859-1";

    // small and uneven block sizes are not used in reality but are good to show that the algorithm is solid
    @SuppressWarnings(value = "boxing")
    @Parameters(name = "BlockSize={0}")
    public static Collection<Integer[]> blockSizes() {
        return Arrays.asList(new Integer[][]{ new Integer[]{ 1 } , new Integer[]{ 3 } , new Integer[]{ 8 } , new Integer[]{ 256 } , new Integer[]{ 4096 } });
    }

    private ReversedLinesFileReader reversedLinesFileReader;

    private final int testParamBlockSize;

    public ReversedLinesFileReaderTestParamBlockSize(final Integer testWithBlockSize) {
        testParamBlockSize = testWithBlockSize;
    }

    // Strings are escaped in constants to avoid java source encoding issues (source file enc is UTF-8):
    // "A Test Line. Special chars: ÄäÜüÖöß ÃáéíïçñÂ ©µ¥£±²®"
    private static final String TEST_LINE = "A Test Line. Special chars: \u00c4\u00e4\u00dc\u00fc\u00d6\u00f6\u00df \u00c3\u00e1\u00e9\u00ed\u00ef\u00e7\u00f1\u00c2 \u00a9\u00b5\u00a5\u00a3\u00b1\u00b2\u00ae";

    // Hiragana letters: ???????????????
    private static final String TEST_LINE_SHIFT_JIS1 = "Hiragana letters: \u3041\u3042\u3043\u3044\u3045";

    // Kanji letters: ??????
    private static final String TEST_LINE_SHIFT_JIS2 = "Kanji letters: \u660e\u8f38\u5b50\u4eac";

    // windows-31j characters
    private static final String TEST_LINE_WINDOWS_31J_1 = "\u3041\u3042\u3043\u3044\u3045";

    private static final String TEST_LINE_WINDOWS_31J_2 = "\u660e\u8f38\u5b50\u4eac";

    // gbk characters (Simplified Chinese)
    private static final String TEST_LINE_GBK_1 = "\u660e\u8f38\u5b50\u4eac";

    private static final String TEST_LINE_GBK_2 = "\u7b80\u4f53\u4e2d\u6587";

    // x-windows-949 characters (Korean)
    private static final String TEST_LINE_X_WINDOWS_949_1 = "\ud55c\uad6d\uc5b4";

    private static final String TEST_LINE_X_WINDOWS_949_2 = "\ub300\ud55c\ubbfc\uad6d";

    // x-windows-950 characters (Traditional Chinese)
    private static final String TEST_LINE_X_WINDOWS_950_1 = "\u660e\u8f38\u5b50\u4eac";

    private static final String TEST_LINE_X_WINDOWS_950_2 = "\u7e41\u9ad4\u4e2d\u6587";

    @After
    public void closeReader() {
        try {
            reversedLinesFileReader.close();
        } catch (final Exception e) {
            // ignore
        }
    }

    @Test
    public void testIsoFileDefaults() throws IOException, URISyntaxException {
        final File testFileIso = new File(ReversedLinesFileReaderTestParamBlockSize.this.getClass().getResource("/test-file-iso8859-1.bin").toURI());
        reversedLinesFileReader = new ReversedLinesFileReader(testFileIso, testParamBlockSize, ReversedLinesFileReaderTestParamBlockSize.ISO_8859_1);
        assertFileWithShrinkingTestLines(reversedLinesFileReader);
    }

    @Test
    public void testUTF8FileWindowsBreaks() throws IOException, URISyntaxException {
        final File testFileIso = new File(ReversedLinesFileReaderTestParamBlockSize.this.getClass().getResource("/test-file-utf8-win-linebr.bin").toURI());
        reversedLinesFileReader = new ReversedLinesFileReader(testFileIso, testParamBlockSize, ReversedLinesFileReaderTestParamBlockSize.UTF_8);
        assertFileWithShrinkingTestLines(reversedLinesFileReader);
    }

    @Test
    public void testUTF8FileCRBreaks() throws IOException, URISyntaxException {
        final File testFileIso = new File(ReversedLinesFileReaderTestParamBlockSize.this.getClass().getResource("/test-file-utf8-cr-only.bin").toURI());
        reversedLinesFileReader = new ReversedLinesFileReader(testFileIso, testParamBlockSize, ReversedLinesFileReaderTestParamBlockSize.UTF_8);
        assertFileWithShrinkingTestLines(reversedLinesFileReader);
    }

    @Test
    public void testUTF8File() throws IOException, URISyntaxException {
        final File testFileIso = new File(ReversedLinesFileReaderTestParamBlockSize.this.getClass().getResource("/test-file-utf8.bin").toURI());
        reversedLinesFileReader = new ReversedLinesFileReader(testFileIso, testParamBlockSize, ReversedLinesFileReaderTestParamBlockSize.UTF_8);
        assertFileWithShrinkingTestLines(reversedLinesFileReader);
    }

    @Test
    public void testEmptyFile() throws IOException, URISyntaxException {
        final File testFileEmpty = new File(ReversedLinesFileReaderTestParamBlockSize.this.getClass().getResource("/test-file-empty.bin").toURI());
        reversedLinesFileReader = new ReversedLinesFileReader(testFileEmpty, testParamBlockSize, ReversedLinesFileReaderTestParamBlockSize.UTF_8);
        assertNull(reversedLinesFileReader.readLine());
    }

    @Test
    public void testUTF16BEFile() throws IOException, URISyntaxException {
        final File testFileUTF16BE = new File(ReversedLinesFileReaderTestParamBlockSize.this.getClass().getResource("/test-file-utf16be.bin").toURI());
        reversedLinesFileReader = new ReversedLinesFileReader(testFileUTF16BE, testParamBlockSize, "UTF-16BE");
        assertFileWithShrinkingTestLines(reversedLinesFileReader);
    }

    @Test
    public void testUTF16LEFile() throws IOException, URISyntaxException {
        final File testFileUTF16LE = new File(ReversedLinesFileReaderTestParamBlockSize.this.getClass().getResource("/test-file-utf16le.bin").toURI());
        reversedLinesFileReader = new ReversedLinesFileReader(testFileUTF16LE, testParamBlockSize, "UTF-16LE");
        assertFileWithShrinkingTestLines(reversedLinesFileReader);
    }

    @Test
    public void testShiftJISFile() throws IOException, URISyntaxException {
        final File testFileShiftJIS = new File(ReversedLinesFileReaderTestParamBlockSize.this.getClass().getResource("/test-file-shiftjis.bin").toURI());
        reversedLinesFileReader = new ReversedLinesFileReader(testFileShiftJIS, testParamBlockSize, "Shift_JIS");
        ReversedLinesFileReaderTestParamBlockSize.assertEqualsAndNoLineBreaks(ReversedLinesFileReaderTestParamBlockSize.TEST_LINE_SHIFT_JIS2, reversedLinesFileReader.readLine());
        ReversedLinesFileReaderTestParamBlockSize.assertEqualsAndNoLineBreaks(ReversedLinesFileReaderTestParamBlockSize.TEST_LINE_SHIFT_JIS1, reversedLinesFileReader.readLine());
    }

    @Test
    public void testWindows31jFile() throws IOException, URISyntaxException {
        final File testFileWindows31J = new File(ReversedLinesFileReaderTestParamBlockSize.this.getClass().getResource("/test-file-windows-31j.bin").toURI());
        reversedLinesFileReader = new ReversedLinesFileReader(testFileWindows31J, testParamBlockSize, "windows-31j");
        ReversedLinesFileReaderTestParamBlockSize.assertEqualsAndNoLineBreaks(ReversedLinesFileReaderTestParamBlockSize.TEST_LINE_WINDOWS_31J_2, reversedLinesFileReader.readLine());
        ReversedLinesFileReaderTestParamBlockSize.assertEqualsAndNoLineBreaks(ReversedLinesFileReaderTestParamBlockSize.TEST_LINE_WINDOWS_31J_1, reversedLinesFileReader.readLine());
    }

    @Test
    public void testGBK() throws IOException, URISyntaxException {
        final File testFileGBK = new File(ReversedLinesFileReaderTestParamBlockSize.this.getClass().getResource("/test-file-gbk.bin").toURI());
        reversedLinesFileReader = new ReversedLinesFileReader(testFileGBK, testParamBlockSize, "GBK");
        ReversedLinesFileReaderTestParamBlockSize.assertEqualsAndNoLineBreaks(ReversedLinesFileReaderTestParamBlockSize.TEST_LINE_GBK_2, reversedLinesFileReader.readLine());
        ReversedLinesFileReaderTestParamBlockSize.assertEqualsAndNoLineBreaks(ReversedLinesFileReaderTestParamBlockSize.TEST_LINE_GBK_1, reversedLinesFileReader.readLine());
    }

    @Test
    public void testxWindows949File() throws IOException, URISyntaxException {
        final File testFilexWindows949 = new File(ReversedLinesFileReaderTestParamBlockSize.this.getClass().getResource("/test-file-x-windows-949.bin").toURI());
        reversedLinesFileReader = new ReversedLinesFileReader(testFilexWindows949, testParamBlockSize, "x-windows-949");
        ReversedLinesFileReaderTestParamBlockSize.assertEqualsAndNoLineBreaks(ReversedLinesFileReaderTestParamBlockSize.TEST_LINE_X_WINDOWS_949_2, reversedLinesFileReader.readLine());
        ReversedLinesFileReaderTestParamBlockSize.assertEqualsAndNoLineBreaks(ReversedLinesFileReaderTestParamBlockSize.TEST_LINE_X_WINDOWS_949_1, reversedLinesFileReader.readLine());
    }

    @Test
    public void testxWindows950File() throws IOException, URISyntaxException {
        final File testFilexWindows950 = new File(ReversedLinesFileReaderTestParamBlockSize.this.getClass().getResource("/test-file-x-windows-950.bin").toURI());
        reversedLinesFileReader = new ReversedLinesFileReader(testFilexWindows950, testParamBlockSize, "x-windows-950");
        ReversedLinesFileReaderTestParamBlockSize.assertEqualsAndNoLineBreaks(ReversedLinesFileReaderTestParamBlockSize.TEST_LINE_X_WINDOWS_950_2, reversedLinesFileReader.readLine());
        ReversedLinesFileReaderTestParamBlockSize.assertEqualsAndNoLineBreaks(ReversedLinesFileReaderTestParamBlockSize.TEST_LINE_X_WINDOWS_950_1, reversedLinesFileReader.readLine());
    }

    // this test is run 3x for same block size as we want to test with 10
    @Test
    public void testFileSizeIsExactMultipleOfBlockSize() throws IOException, URISyntaxException {
        final int blockSize = 10;
        final File testFile20Bytes = new File(ReversedLinesFileReaderTestParamBlockSize.this.getClass().getResource("/test-file-20byteslength.bin").toURI());
        reversedLinesFileReader = new ReversedLinesFileReader(testFile20Bytes, blockSize, ReversedLinesFileReaderTestParamBlockSize.ISO_8859_1);
        final String testLine = "123456789";
        ReversedLinesFileReaderTestParamBlockSize.assertEqualsAndNoLineBreaks(testLine, reversedLinesFileReader.readLine());
        ReversedLinesFileReaderTestParamBlockSize.assertEqualsAndNoLineBreaks(testLine, reversedLinesFileReader.readLine());
    }

    @Test
    public void testUTF8FileWindowsBreaksSmallBlockSize2VerifyBlockSpanningNewLines() throws IOException, URISyntaxException {
        final File testFileUtf8 = new File(ReversedLinesFileReaderTestParamBlockSize.this.getClass().getResource("/test-file-utf8-win-linebr.bin").toURI());
        reversedLinesFileReader = new ReversedLinesFileReader(testFileUtf8, testParamBlockSize, ReversedLinesFileReaderTestParamBlockSize.UTF_8);
        assertFileWithShrinkingTestLines(reversedLinesFileReader);
    }

    @Test
    public void testIsoFileManyWindowsBreaksSmallBlockSize2VerifyBlockSpanningNewLines() throws IOException, URISyntaxException {
        final File testFileIso = new File(ReversedLinesFileReaderTestParamBlockSize.this.getClass().getResource("/test-file-iso8859-1-shortlines-win-linebr.bin").toURI());
        reversedLinesFileReader = new ReversedLinesFileReader(testFileIso, testParamBlockSize, ReversedLinesFileReaderTestParamBlockSize.ISO_8859_1);
        for (int i = 3; i > 0; i--) {
            for (int j = 1; j <= 3; j++) {
                ReversedLinesFileReaderTestParamBlockSize.assertEqualsAndNoLineBreaks("", reversedLinesFileReader.readLine());
            }
            ReversedLinesFileReaderTestParamBlockSize.assertEqualsAndNoLineBreaks(("" + i), reversedLinesFileReader.readLine());
        }
    }

    @Test(expected = UnsupportedEncodingException.class)
    public void testUnsupportedEncodingUTF16() throws IOException, URISyntaxException {
        final File testFileEmpty = new File(ReversedLinesFileReaderTestParamBlockSize.this.getClass().getResource("/test-file-empty.bin").toURI());
        new ReversedLinesFileReader(testFileEmpty, testParamBlockSize, "UTF-16").close();
    }

    @Test(expected = UnsupportedEncodingException.class)
    public void testUnsupportedEncodingBig5() throws IOException, URISyntaxException {
        final File testFileEncodingBig5 = new File(ReversedLinesFileReaderTestParamBlockSize.this.getClass().getResource("/test-file-empty.bin").toURI());
        new ReversedLinesFileReader(testFileEncodingBig5, testParamBlockSize, "Big5").close();
    }

    private void assertFileWithShrinkingTestLines(final ReversedLinesFileReader reversedLinesFileReader) throws IOException {
        String line = null;
        int lineCount = 0;
        while ((line = reversedLinesFileReader.readLine()) != null) {
            lineCount++;
            ReversedLinesFileReaderTestParamBlockSize.assertEqualsAndNoLineBreaks((("Line " + lineCount) + " is not matching"), ReversedLinesFileReaderTestParamBlockSize.TEST_LINE.substring(0, lineCount), line);
        }
    }

    static void assertEqualsAndNoLineBreaks(final String msg, final String expected, final String actual) {
        if (actual != null) {
            assertFalse(("Line contains \\n: line=" + actual), actual.contains("\n"));
            assertFalse(("Line contains \\r: line=" + actual), actual.contains("\r"));
        } 
        assertEquals(msg, expected, actual);
    }

    static void assertEqualsAndNoLineBreaks(final String expected, final String actual) {
        ReversedLinesFileReaderTestParamBlockSize.assertEqualsAndNoLineBreaks(null, expected, actual);
    }
}

