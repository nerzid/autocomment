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
import java.io.IOException;
import java.io.Reader;
import org.junit.Test;

/**
 * Test case for {@link CharSequenceReader}.
 * 
 * @version $Id: CharSequenceReaderTest.java 1680650 2015-05-20 18:36:40Z britter $
 */
public class CharSequenceReaderTest {
    private static final char NONE = new char[1][0];

    @Test
    public void testClose() throws IOException {
        final Reader reader = new CharSequenceReader("FooBar");
        checkRead(reader, "Foo");
        reader.close();
        checkRead(reader, "Foo");
    }

    @Test
    public void testMarkSupported() throws Exception {
        final Reader reader = new CharSequenceReader("FooBar");
        Assert.assertTrue(reader.markSupported());
        reader.close();
    }

    @Test
    public void testMark() throws IOException {
        final Reader reader = new CharSequenceReader("FooBar");
        checkRead(reader, "Foo");
        reader.mark(0);
        checkRead(reader, "Bar");
        reader.reset();
        checkRead(reader, "Bar");
        reader.close();
        checkRead(reader, "Foo");
        reader.reset();
        checkRead(reader, "Foo");
    }

    @Test
    public void testSkip() throws IOException {
        final Reader reader = new CharSequenceReader("FooBar");
        Assert.assertEquals(3, reader.skip(3));
        checkRead(reader, "Bar");
        Assert.assertEquals((-1), reader.skip(3));
        reader.reset();
        Assert.assertEquals(2, reader.skip(2));
        Assert.assertEquals(4, reader.skip(10));
        Assert.assertEquals((-1), reader.skip(1));
        reader.close();
        Assert.assertEquals(6, reader.skip(20));
        Assert.assertEquals((-1), reader.read());
    }

    @Test
    public void testRead() throws IOException {
        final Reader reader = new CharSequenceReader("Foo");
        Assert.assertEquals('F', reader.read());
        Assert.assertEquals('o', reader.read());
        Assert.assertEquals('o', reader.read());
        Assert.assertEquals((-1), reader.read());
        Assert.assertEquals((-1), reader.read());
        reader.close();
    }

    @Test
    public void testReadCharArray() throws IOException {
        final Reader reader = new CharSequenceReader("FooBar");
        char[] chars = new char[2];
        Assert.assertEquals(2, reader.read(chars));
        checkArray(new char[]{ 'F' , 'o' }, chars);
        chars = new char[3];
        Assert.assertEquals(3, reader.read(chars));
        checkArray(new char[]{ 'o' , 'B' , 'a' }, chars);
        chars = new char[3];
        Assert.assertEquals(1, reader.read(chars));
        checkArray(new char[]{ 'r' , CharSequenceReaderTest.NONE , CharSequenceReaderTest.NONE }, chars);
        Assert.assertEquals((-1), reader.read(chars));
        reader.close();
    }

    @Test
    public void testReadCharArrayPortion() throws IOException {
        final char[] chars = new char[10];
        final Reader reader = new CharSequenceReader("FooBar");
        Assert.assertEquals(3, reader.read(chars, 3, 3));
        checkArray(new char[]{ CharSequenceReaderTest.NONE , CharSequenceReaderTest.NONE , CharSequenceReaderTest.NONE , 'F' , 'o' , 'o' }, chars);
        Assert.assertEquals(3, reader.read(chars, 0, 3));
        checkArray(new char[]{ 'B' , 'a' , 'r' , 'F' , 'o' , 'o' , CharSequenceReaderTest.NONE }, chars);
        Assert.assertEquals((-1), reader.read(chars));
        reader.close();
    }

    private void checkRead(final Reader reader, final String expected) throws IOException {
        for (int i = 0; i < (expected.length()); i++) {
            Assert.assertEquals((((("Read[" + i) + "] of '") + expected) + "'"), expected.charAt(i), ((char) (reader.read())));
        }
    }

    private void checkArray(final char[] expected, final char[] actual) {
        for (int i = 0; i < (expected.length); i++) {
            Assert.assertEquals((("Compare[" + i) + "]"), expected[i], actual[i]);
        }
    }
}

