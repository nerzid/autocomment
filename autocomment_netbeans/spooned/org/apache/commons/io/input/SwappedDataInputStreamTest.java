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
import org.junit.Assert;
import org.junit.Before;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.Test;

/**
 * Test for the SwappedDataInputStream. This also
 * effectively tests the underlying EndianUtils Stream methods.
 * 
 * @version $Id: SwappedDataInputStreamTest.java 1718944 2015-12-09 19:50:30Z krosenvold $
 */
public class SwappedDataInputStreamTest {
    private SwappedDataInputStream sdis;

    private byte[] bytes;

    @Before
    public void setUp() {
        bytes = new byte[]{ 1 , 2 , 3 , 4 , 5 , 6 , 7 , 8 };
        final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        SwappedDataInputStreamTest.this.sdis = new SwappedDataInputStream(bais);
    }

    @After
    public void tearDown() {
        SwappedDataInputStreamTest.this.sdis = null;
    }

    @Test
    public void testReadBoolean() throws IOException {
        bytes = new byte[]{ 0 , 1 , 2 };
        final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        final SwappedDataInputStream sdis = new SwappedDataInputStream(bais);
        Assert.assertEquals(false, sdis.readBoolean());
        Assert.assertEquals(true, sdis.readBoolean());
        Assert.assertEquals(true, sdis.readBoolean());
        sdis.close();
    }

    @Test
    public void testReadByte() throws IOException {
        Assert.assertEquals(1, SwappedDataInputStreamTest.this.sdis.readByte());
    }

    @Test
    public void testReadChar() throws IOException {
        Assert.assertEquals(((char) (513)), SwappedDataInputStreamTest.this.sdis.readChar());
    }

    @Test
    public void testReadDouble() throws IOException {
        Assert.assertEquals(Double.longBitsToDouble(578437695752307201L), SwappedDataInputStreamTest.this.sdis.readDouble(), 0);
    }

    @Test
    public void testReadFloat() throws IOException {
        Assert.assertEquals(Float.intBitsToFloat(67305985), SwappedDataInputStreamTest.this.sdis.readFloat(), 0);
    }

    @Test
    public void testReadFully() throws IOException {
        final byte[] bytesIn = new byte[8];
        SwappedDataInputStreamTest.this.sdis.readFully(bytesIn);
        for (int i = 0; i < 8; i++) {
            Assert.assertEquals(bytes[i], bytesIn[i]);
        }
    }

    @Test
    public void testReadInt() throws IOException {
        Assert.assertEquals(67305985, SwappedDataInputStreamTest.this.sdis.readInt());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testReadLine() throws IOException {
        SwappedDataInputStreamTest.this.sdis.readLine();
        Assert.fail("readLine should be unsupported. ");
    }

    @Test
    public void testReadLong() throws IOException {
        Assert.assertEquals(578437695752307201L, SwappedDataInputStreamTest.this.sdis.readLong());
    }

    @Test
    public void testReadShort() throws IOException {
        Assert.assertEquals(((short) (513)), SwappedDataInputStreamTest.this.sdis.readShort());
    }

    @Test
    public void testReadUnsignedByte() throws IOException {
        Assert.assertEquals(1, SwappedDataInputStreamTest.this.sdis.readUnsignedByte());
    }

    @Test
    public void testReadUnsignedShort() throws IOException {
        Assert.assertEquals(((short) (513)), SwappedDataInputStreamTest.this.sdis.readUnsignedShort());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testReadUTF() throws IOException {
        SwappedDataInputStreamTest.this.sdis.readUTF();
        Assert.fail("readUTF should be unsupported. ");
    }

    @Test
    public void testSkipBytes() throws IOException {
        SwappedDataInputStreamTest.this.sdis.skipBytes(4);
        Assert.assertEquals(134678021, SwappedDataInputStreamTest.this.sdis.readInt());
    }
}

