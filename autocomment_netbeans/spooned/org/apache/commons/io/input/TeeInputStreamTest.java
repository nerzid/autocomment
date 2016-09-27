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
import org.junit.Before;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.junit.Test;

/**
 * JUnit Test Case for {@link TeeInputStream}.
 */
public class TeeInputStreamTest {
    private final String ASCII = "US-ASCII";

    private InputStream tee;

    private ByteArrayOutputStream output;

    @Before
    public void setUp() throws Exception {
        final InputStream input = new ByteArrayInputStream("abc".getBytes(ASCII));
        output = new ByteArrayOutputStream();
        tee = new TeeInputStream(input, output);
    }

    @Test
    public void testReadNothing() throws Exception {
        Assert.assertEquals("", new String(output.toString(ASCII)));
    }

    @Test
    public void testReadOneByte() throws Exception {
        Assert.assertEquals('a', tee.read());
        Assert.assertEquals("a", new String(output.toString(ASCII)));
    }

    @Test
    public void testReadEverything() throws Exception {
        Assert.assertEquals('a', tee.read());
        Assert.assertEquals('b', tee.read());
        Assert.assertEquals('c', tee.read());
        Assert.assertEquals((-1), tee.read());
        Assert.assertEquals("abc", new String(output.toString(ASCII)));
    }

    @Test
    public void testReadToArray() throws Exception {
        final byte[] buffer = new byte[8];
        Assert.assertEquals(3, tee.read(buffer));
        Assert.assertEquals('a', buffer[0]);
        Assert.assertEquals('b', buffer[1]);
        Assert.assertEquals('c', buffer[2]);
        Assert.assertEquals((-1), tee.read(buffer));
        Assert.assertEquals("abc", new String(output.toString(ASCII)));
    }

    @Test
    public void testReadToArrayWithOffset() throws Exception {
        final byte[] buffer = new byte[8];
        Assert.assertEquals(3, tee.read(buffer, 4, 4));
        Assert.assertEquals('a', buffer[4]);
        Assert.assertEquals('b', buffer[5]);
        Assert.assertEquals('c', buffer[6]);
        Assert.assertEquals((-1), tee.read(buffer, 4, 4));
        Assert.assertEquals("abc", new String(output.toString(ASCII)));
    }

    @Test
    public void testSkip() throws Exception {
        Assert.assertEquals('a', tee.read());
        Assert.assertEquals(1, tee.skip(1));
        Assert.assertEquals('c', tee.read());
        Assert.assertEquals((-1), tee.read());
        Assert.assertEquals("ac", new String(output.toString(ASCII)));
    }

    @Test
    public void testMarkReset() throws Exception {
        Assert.assertEquals('a', tee.read());
        tee.mark(1);
        Assert.assertEquals('b', tee.read());
        tee.reset();
        Assert.assertEquals('b', tee.read());
        Assert.assertEquals('c', tee.read());
        Assert.assertEquals((-1), tee.read());
        Assert.assertEquals("abbc", new String(output.toString(ASCII)));
    }
}

