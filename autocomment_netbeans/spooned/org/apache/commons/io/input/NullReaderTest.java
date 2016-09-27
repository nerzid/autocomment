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

import org.junit.Assert;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import org.junit.Test;

/**
 * JUnit Test Case for {@link NullReader}.
 * 
 * @version $Id: NullReaderTest.java 1718944 2015-12-09 19:50:30Z krosenvold $
 */
public class NullReaderTest {
    @Test
    public void testRead() throws Exception {
        final int size = 5;
        final NullReaderTest.TestNullReader reader = new NullReaderTest.TestNullReader(size);
        for (int i = 0; i < size; i++) {
            Assert.assertEquals((("Check Value [" + i) + "]"), i, reader.read());
        }
        // Check End of File
        Assert.assertEquals("End of File", (-1), reader.read());
        // Test reading after the end of file
        try {
            final int result = reader.read();
            Assert.fail((("Should have thrown an IOException, value=[" + result) + "]"));
        } catch (final IOException e) {
            Assert.assertEquals("Read after end of file", e.getMessage());
        }
        // Close - should reset
        reader.close();
        Assert.assertEquals("Available after close", 0, reader.getPosition());
    }

    @Test
    public void testReadCharArray() throws Exception {
        final char[] chars = new char[10];
        final Reader reader = new NullReaderTest.TestNullReader(15);
        // Read into array
        final int count1 = reader.read(chars);
        Assert.assertEquals("Read 1", chars.length, count1);
        for (int i = 0; i < count1; i++) {
            Assert.assertEquals("Check Chars 1", i, chars[i]);
        }
        // Read into array
        final int count2 = reader.read(chars);
        Assert.assertEquals("Read 2", 5, count2);
        for (int i = 0; i < count2; i++) {
            Assert.assertEquals("Check Chars 2", (count1 + i), chars[i]);
        }
        // End of File
        final int count3 = reader.read(chars);
        Assert.assertEquals("Read 3 (EOF)", (-1), count3);
        // Test reading after the end of file
        try {
            final int count4 = reader.read(chars);
            Assert.fail((("Should have thrown an IOException, value=[" + count4) + "]"));
        } catch (final IOException e) {
            Assert.assertEquals("Read after end of file", e.getMessage());
        }
        // reset by closing
        reader.close();
        // Read into array using offset & length
        final int offset = 2;
        final int lth = 4;
        final int count5 = reader.read(chars, offset, lth);
        Assert.assertEquals("Read 5", lth, count5);
        for (int i = offset; i < lth; i++) {
            Assert.assertEquals("Check Chars 3", i, chars[i]);
        }
    }

    @Test
    public void testEOFException() throws Exception {
        final Reader reader = new NullReaderTest.TestNullReader(2, false, true);
        Assert.assertEquals("Read 1", 0, reader.read());
        Assert.assertEquals("Read 2", 1, reader.read());
        try {
            final int result = reader.read();
            Assert.fail((("Should have thrown an EOFException, value=[" + result) + "]"));
        } catch (final EOFException e) {
            // expected
        }
        reader.close();
    }

    @Test
    public void testMarkAndReset() throws Exception {
        int position = 0;
        final int readlimit = 10;
        final Reader reader = new NullReaderTest.TestNullReader(100, true, false);
        Assert.assertTrue("Mark Should be Supported", reader.markSupported());
        // No Mark
        try {
            reader.reset();
            Assert.fail("Read limit exceeded, expected IOException ");
        } catch (final IOException e) {
            Assert.assertEquals("No Mark IOException message", "No position has been marked", e.getMessage());
        }
        for (; position < 3; position++) {
            Assert.assertEquals((("Read Before Mark [" + position) + "]"), position, reader.read());
        }
        // Mark
        reader.mark(readlimit);
        // Read further
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals((("Read After Mark [" + i) + "]"), (position + i), reader.read());
        }
        // Reset
        reader.reset();
        // Read From marked position
        for (int i = 0; i < (readlimit + 1); i++) {
            Assert.assertEquals((("Read After Reset [" + i) + "]"), (position + i), reader.read());
        }
        // Reset after read limit passed
        try {
            reader.reset();
            Assert.fail("Read limit exceeded, expected IOException ");
        } catch (final IOException e) {
            Assert.assertEquals("Read limit IOException message", (((("Marked position [" + position) + "] is no longer valid - passed the read limit [") + readlimit) + "]"), e.getMessage());
        }
        reader.close();
    }

    @Test
    public void testMarkNotSupported() throws Exception {
        final Reader reader = new NullReaderTest.TestNullReader(100, false, true);
        Assert.assertFalse("Mark Should NOT be Supported", reader.markSupported());
        try {
            reader.mark(5);
            Assert.fail("mark() should throw UnsupportedOperationException");
        } catch (final UnsupportedOperationException e) {
            Assert.assertEquals("mark() error message", "Mark not supported", e.getMessage());
        }
        try {
            reader.reset();
            Assert.fail("reset() should throw UnsupportedOperationException");
        } catch (final UnsupportedOperationException e) {
            Assert.assertEquals("reset() error message", "Mark not supported", e.getMessage());
        }
        reader.close();
    }

    @Test
    public void testSkip() throws Exception {
        final Reader reader = new NullReaderTest.TestNullReader(10, true, false);
        Assert.assertEquals("Read 1", 0, reader.read());
        Assert.assertEquals("Read 2", 1, reader.read());
        Assert.assertEquals("Skip 1", 5, reader.skip(5));
        Assert.assertEquals("Read 3", 7, reader.read());
        Assert.assertEquals("Skip 2", 2, reader.skip(5));// only 2 left to skip
        
        Assert.assertEquals("Skip 3 (EOF)", (-1), reader.skip(5));// End of file
        
        try {
            reader.skip(5);// 
            
            Assert.fail("Expected IOException for skipping after end of file");
        } catch (final IOException e) {
            Assert.assertEquals("Skip after EOF IOException message", "Skip after end of file", e.getMessage());
        }
        reader.close();
    }

    // ------------- Test NullReader implementation -------------
    private static final class TestNullReader extends NullReader {
        public TestNullReader(final int size) {
            super(size);
        }

        public TestNullReader(final int size, final boolean markSupported, final boolean throwEofException) {
            super(size, markSupported, throwEofException);
        }

        @Override
        protected int processChar() {
            return ((int) (getPosition())) - 1;
        }

        @Override
        protected void processChars(final char[] chars, final int offset, final int length) {
            final int startPos = ((int) (getPosition())) - length;
            for (int i = offset; i < length; i++) {
                chars[i] = ((char) (startPos + i));
            }
        }
    }
}

