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

import java.util.Arrays;
import org.junit.Assert;
import java.nio.charset.Charset;
import org.junit.Test;

/**
 * Test for {@link ByteOrderMark}.
 * 
 * @version $Id: ByteOrderMarkTestCase.java 1718944 2015-12-09 19:50:30Z krosenvold $
 */
public class ByteOrderMarkTestCase {
    private static final ByteOrderMark TEST_BOM_1 = new ByteOrderMark("test1", 1);

    private static final ByteOrderMark TEST_BOM_2 = new ByteOrderMark("test2", 1, 2);

    private static final ByteOrderMark TEST_BOM_3 = new ByteOrderMark("test3", 1, 2, 3);

    /**
     * * Test {@link ByteOrderMark#getCharsetName()}
     */
    @Test
    public void charsetName() {
        Assert.assertEquals("test1 name", "test1", ByteOrderMarkTestCase.TEST_BOM_1.getCharsetName());
        Assert.assertEquals("test2 name", "test2", ByteOrderMarkTestCase.TEST_BOM_2.getCharsetName());
        Assert.assertEquals("test3 name", "test3", ByteOrderMarkTestCase.TEST_BOM_3.getCharsetName());
    }

    /**
     * * Tests that {@link ByteOrderMark#getCharsetName()} can be loaded as a {@link java.nio.charset.Charset} as advertised.
     */
    @Test
    public void constantCharsetNames() {
        Assert.assertNotNull(Charset.forName(ByteOrderMark.UTF_8.getCharsetName()));
        Assert.assertNotNull(Charset.forName(ByteOrderMark.UTF_16BE.getCharsetName()));
        Assert.assertNotNull(Charset.forName(ByteOrderMark.UTF_16LE.getCharsetName()));
        Assert.assertNotNull(Charset.forName(ByteOrderMark.UTF_32BE.getCharsetName()));
        Assert.assertNotNull(Charset.forName(ByteOrderMark.UTF_32LE.getCharsetName()));
    }

    /**
     * * Test {@link ByteOrderMark#length()}
     */
    @Test
    public void testLength() {
        Assert.assertEquals("test1 length", 1, ByteOrderMarkTestCase.TEST_BOM_1.length());
        Assert.assertEquals("test2 length", 2, ByteOrderMarkTestCase.TEST_BOM_2.length());
        Assert.assertEquals("test3 length", 3, ByteOrderMarkTestCase.TEST_BOM_3.length());
    }

    /**
     * * Test {@link ByteOrderMark#get(int)}
     */
    @Test
    public void get() {
        Assert.assertEquals("test1 get(0)", 1, ByteOrderMarkTestCase.TEST_BOM_1.get(0));
        Assert.assertEquals("test2 get(0)", 1, ByteOrderMarkTestCase.TEST_BOM_2.get(0));
        Assert.assertEquals("test2 get(1)", 2, ByteOrderMarkTestCase.TEST_BOM_2.get(1));
        Assert.assertEquals("test3 get(0)", 1, ByteOrderMarkTestCase.TEST_BOM_3.get(0));
        Assert.assertEquals("test3 get(1)", 2, ByteOrderMarkTestCase.TEST_BOM_3.get(1));
        Assert.assertEquals("test3 get(2)", 3, ByteOrderMarkTestCase.TEST_BOM_3.get(2));
    }

    /**
     * * Test {@link ByteOrderMark#getBytes()}
     */
    @Test
    public void getBytes() {
        Assert.assertTrue("test1 bytes", Arrays.equals(ByteOrderMarkTestCase.TEST_BOM_1.getBytes(), new byte[]{ ((byte) (1)) }));
        Assert.assertTrue("test1 bytes", Arrays.equals(ByteOrderMarkTestCase.TEST_BOM_2.getBytes(), new byte[]{ ((byte) (1)) , ((byte) (2)) }));
        Assert.assertTrue("test1 bytes", Arrays.equals(ByteOrderMarkTestCase.TEST_BOM_3.getBytes(), new byte[]{ ((byte) (1)) , ((byte) (2)) , ((byte) (3)) }));
    }

    /**
     * * Test {@link ByteOrderMark#equals(Object)}
     */
    @SuppressWarnings(value = "EqualsWithItself")
    @Test
    public void testEquals() {
        Assert.assertTrue("test1 equals", ByteOrderMarkTestCase.TEST_BOM_1.equals(ByteOrderMarkTestCase.TEST_BOM_1));
        Assert.assertTrue("test2 equals", ByteOrderMarkTestCase.TEST_BOM_2.equals(ByteOrderMarkTestCase.TEST_BOM_2));
        Assert.assertTrue("test3 equals", ByteOrderMarkTestCase.TEST_BOM_3.equals(ByteOrderMarkTestCase.TEST_BOM_3));
        Assert.assertFalse("Object not equal", ByteOrderMarkTestCase.TEST_BOM_1.equals(new Object()));
        Assert.assertFalse("test1-1 not equal", ByteOrderMarkTestCase.TEST_BOM_1.equals(new ByteOrderMark("1a", 2)));
        Assert.assertFalse("test1-2 not test2", ByteOrderMarkTestCase.TEST_BOM_1.equals(new ByteOrderMark("1b", 1, 2)));
        Assert.assertFalse("test2 not equal", ByteOrderMarkTestCase.TEST_BOM_2.equals(new ByteOrderMark("2", 1, 1)));
        Assert.assertFalse("test3 not equal", ByteOrderMarkTestCase.TEST_BOM_3.equals(new ByteOrderMark("3", 1, 2, 4)));
    }

    /**
     * * Test {@link ByteOrderMark#hashCode()}
     */
    @Test
    public void testHashCode() {
        final int bomClassHash = ByteOrderMark.class.hashCode();
        Assert.assertEquals("hash test1 ", (bomClassHash + 1), ByteOrderMarkTestCase.TEST_BOM_1.hashCode());
        Assert.assertEquals("hash test2 ", (bomClassHash + 3), ByteOrderMarkTestCase.TEST_BOM_2.hashCode());
        Assert.assertEquals("hash test3 ", (bomClassHash + 6), ByteOrderMarkTestCase.TEST_BOM_3.hashCode());
    }

    /**
     * * Test Erros
     */
    @Test
    public void errors() {
        try {
            new ByteOrderMark(null, 1, 2, 3);
            Assert.fail("null charset name, expected IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            // expected
        }
        try {
            new ByteOrderMark("", 1, 2, 3);
            Assert.fail("no charset name, expected IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            // expected
        }
        try {
            new ByteOrderMark("a", ((int[]) (null)));
            Assert.fail("null bytes, expected IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            // expected
        }
        try {
            new ByteOrderMark("b");
            Assert.fail("empty bytes, expected IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            // expected
        }
    }

    /**
     * * Test {@link ByteOrderMark#toString()}
     */
    @Test
    public void testToString() {
        Assert.assertEquals("test1 ", "ByteOrderMark[test1: 0x1]", ByteOrderMarkTestCase.TEST_BOM_1.toString());
        Assert.assertEquals("test2 ", "ByteOrderMark[test2: 0x1,0x2]", ByteOrderMarkTestCase.TEST_BOM_2.toString());
        Assert.assertEquals("test3 ", "ByteOrderMark[test3: 0x1,0x2,0x3]", ByteOrderMarkTestCase.TEST_BOM_3.toString());
    }
}

