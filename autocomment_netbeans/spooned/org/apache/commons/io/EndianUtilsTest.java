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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import org.junit.Test;

/**
 * @version $Id: EndianUtilsTest.java 1718944 2015-12-09 19:50:30Z krosenvold $
 */
public class EndianUtilsTest {
    @Test
    public void testCtor() {
        new EndianUtils();
        // Constructor does not blow up.
    }

    @Test
    public void testEOFException() throws IOException {
        final ByteArrayInputStream input = new ByteArrayInputStream(new byte[]{  });
        try {
            EndianUtils.readSwappedDouble(input);
            Assert.fail("Expected EOFException");
        } catch (final EOFException e) {
            // expected
        }
    }

    @Test
    public void testSwapShort() {
        // assert equals int{((short) (0))} to void{Assert}
        Assert.assertEquals(((short) (0)), EndianUtils.swapShort(((short) (0))));
        // assert equals int{((short) (513))} to void{Assert}
        Assert.assertEquals(((short) (513)), EndianUtils.swapShort(((short) (258))));
        // assert equals int{((short) (65535))} to void{Assert}
        Assert.assertEquals(((short) (65535)), EndianUtils.swapShort(((short) (65535))));
        // assert equals int{((short) (258))} to void{Assert}
        Assert.assertEquals(((short) (258)), EndianUtils.swapShort(((short) (513))));
    }

    @Test
    public void testSwapInteger() {
        // assert equals int{0} to void{Assert}
        Assert.assertEquals(0, EndianUtils.swapInteger(0));
        // assert equals int{67305985} to void{Assert}
        Assert.assertEquals(67305985, EndianUtils.swapInteger(16909060));
        // assert equals int{16777216} to void{Assert}
        Assert.assertEquals(16777216, EndianUtils.swapInteger(1));
        // assert equals int{1} to void{Assert}
        Assert.assertEquals(1, EndianUtils.swapInteger(16777216));
        // assert equals int{286331153} to void{Assert}
        Assert.assertEquals(286331153, EndianUtils.swapInteger(286331153));
        // assert equals int{(-1412567280)} to void{Assert}
        Assert.assertEquals((-1412567280), EndianUtils.swapInteger(284151211));
        // assert equals int{171} to void{Assert}
        Assert.assertEquals(171, EndianUtils.swapInteger((-1426063360)));
    }

    @Test
    public void testSwapLong() {
        // assert equals int{0} to void{Assert}
        Assert.assertEquals(0, EndianUtils.swapLong(0));
        // assert equals long{578437695752307201L} to void{Assert}
        Assert.assertEquals(578437695752307201L, EndianUtils.swapLong(72623859790382856L));
        // assert equals long{(-1L)} to void{Assert}
        Assert.assertEquals((-1L), EndianUtils.swapLong((-1L)));
        // assert equals int{171} to void{Assert}
        Assert.assertEquals(171, EndianUtils.swapLong((-6124895493223874560L)));
    }

    @Test
    public void testSwapFloat() {
        // assert equals float{0.0F} to void{Assert}
        Assert.assertEquals(0.0F, EndianUtils.swapFloat(0.0F), 0.0);
        final float f1 = Float.intBitsToFloat(16909060);
        final float f2 = Float.intBitsToFloat(67305985);
        // assert equals float{f2} to void{Assert}
        Assert.assertEquals(f2, EndianUtils.swapFloat(f1), 0.0);
    }

    @Test
    public void testSwapDouble() {
        // assert equals double{0.0} to void{Assert}
        Assert.assertEquals(0.0, EndianUtils.swapDouble(0.0), 0.0);
        final double d1 = Double.longBitsToDouble(72623859790382856L);
        final double d2 = Double.longBitsToDouble(578437695752307201L);
        // assert equals double{d2} to void{Assert}
        Assert.assertEquals(d2, EndianUtils.swapDouble(d1), 0.0);
    }

    /**
     * Tests all swapXxxx methods for symmetry when going from one endian
     * to another and back again.
     */
    @Test
    public void testSymmetry() {
        // assert equals int{((short) (258))} to void{Assert}
        Assert.assertEquals(((short) (258)), EndianUtils.swapShort(EndianUtils.swapShort(((short) (258)))));
        // assert equals int{16909060} to void{Assert}
        Assert.assertEquals(16909060, EndianUtils.swapInteger(EndianUtils.swapInteger(16909060)));
        // assert equals long{72623859790382856L} to void{Assert}
        Assert.assertEquals(72623859790382856L, EndianUtils.swapLong(EndianUtils.swapLong(72623859790382856L)));
        final float f1 = Float.intBitsToFloat(16909060);
        // assert equals float{f1} to void{Assert}
        Assert.assertEquals(f1, EndianUtils.swapFloat(EndianUtils.swapFloat(f1)), 0.0);
        final double d1 = Double.longBitsToDouble(72623859790382856L);
        // assert equals double{d1} to void{Assert}
        Assert.assertEquals(d1, EndianUtils.swapDouble(EndianUtils.swapDouble(d1)), 0.0);
    }

    @Test
    public void testReadSwappedShort() throws IOException {
        final byte[] bytes = new byte[]{ 2 , 1 };
        // assert equals int{258} to void{Assert}
        Assert.assertEquals(258, EndianUtils.readSwappedShort(bytes, 0));
        final ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        // assert equals int{258} to void{Assert}
        Assert.assertEquals(258, EndianUtils.readSwappedShort(input));
    }

    @Test
    public void testWriteSwappedShort() throws IOException {
        byte[] bytes = new byte[2];
        // write swapped byte[]{bytes} to void{EndianUtils}
        EndianUtils.writeSwappedShort(bytes, 0, ((short) (258)));
        // assert equals int{2} to void{Assert}
        Assert.assertEquals(2, bytes[0]);
        // assert equals int{1} to void{Assert}
        Assert.assertEquals(1, bytes[1]);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(2);
        // write swapped ByteArrayOutputStream{baos} to void{EndianUtils}
        EndianUtils.writeSwappedShort(baos, ((short) (258)));
        bytes = baos.toByteArray();
        // assert equals int{2} to void{Assert}
        Assert.assertEquals(2, bytes[0]);
        // assert equals int{1} to void{Assert}
        Assert.assertEquals(1, bytes[1]);
    }

    @Test
    public void testReadSwappedUnsignedShort() throws IOException {
        final byte[] bytes = new byte[]{ 2 , 1 };
        // assert equals int{258} to void{Assert}
        Assert.assertEquals(258, EndianUtils.readSwappedUnsignedShort(bytes, 0));
        final ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        // assert equals int{258} to void{Assert}
        Assert.assertEquals(258, EndianUtils.readSwappedUnsignedShort(input));
    }

    @Test
    public void testReadSwappedInteger() throws IOException {
        final byte[] bytes = new byte[]{ 4 , 3 , 2 , 1 };
        // assert equals int{16909060} to void{Assert}
        Assert.assertEquals(16909060, EndianUtils.readSwappedInteger(bytes, 0));
        final ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        // assert equals int{16909060} to void{Assert}
        Assert.assertEquals(16909060, EndianUtils.readSwappedInteger(input));
    }

    @Test
    public void testWriteSwappedInteger() throws IOException {
        byte[] bytes = new byte[4];
        // write swapped byte[]{bytes} to void{EndianUtils}
        EndianUtils.writeSwappedInteger(bytes, 0, 16909060);
        // assert equals int{4} to void{Assert}
        Assert.assertEquals(4, bytes[0]);
        // assert equals int{3} to void{Assert}
        Assert.assertEquals(3, bytes[1]);
        // assert equals int{2} to void{Assert}
        Assert.assertEquals(2, bytes[2]);
        // assert equals int{1} to void{Assert}
        Assert.assertEquals(1, bytes[3]);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(4);
        // write swapped ByteArrayOutputStream{baos} to void{EndianUtils}
        EndianUtils.writeSwappedInteger(baos, 16909060);
        bytes = baos.toByteArray();
        // assert equals int{4} to void{Assert}
        Assert.assertEquals(4, bytes[0]);
        // assert equals int{3} to void{Assert}
        Assert.assertEquals(3, bytes[1]);
        // assert equals int{2} to void{Assert}
        Assert.assertEquals(2, bytes[2]);
        // assert equals int{1} to void{Assert}
        Assert.assertEquals(1, bytes[3]);
    }

    @Test
    public void testReadSwappedUnsignedInteger() throws IOException {
        final byte[] bytes = new byte[]{ 4 , 3 , 2 , 1 };
        // assert equals long{16909060L} to void{Assert}
        Assert.assertEquals(16909060L, EndianUtils.readSwappedUnsignedInteger(bytes, 0));
        final ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        // assert equals long{16909060L} to void{Assert}
        Assert.assertEquals(16909060L, EndianUtils.readSwappedUnsignedInteger(input));
    }

    @Test
    public void testReadSwappedLong() throws IOException {
        final byte[] bytes = new byte[]{ 8 , 7 , 6 , 5 , 4 , 3 , 2 , 1 };
        // assert equals long{72623859790382856L} to void{Assert}
        Assert.assertEquals(72623859790382856L, EndianUtils.readSwappedLong(bytes, 0));
        final ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        // assert equals long{72623859790382856L} to void{Assert}
        Assert.assertEquals(72623859790382856L, EndianUtils.readSwappedLong(input));
    }

    @Test
    public void testWriteSwappedLong() throws IOException {
        byte[] bytes = new byte[8];
        // write swapped byte[]{bytes} to void{EndianUtils}
        EndianUtils.writeSwappedLong(bytes, 0, 72623859790382856L);
        // assert equals int{8} to void{Assert}
        Assert.assertEquals(8, bytes[0]);
        // assert equals int{7} to void{Assert}
        Assert.assertEquals(7, bytes[1]);
        // assert equals int{6} to void{Assert}
        Assert.assertEquals(6, bytes[2]);
        // assert equals int{5} to void{Assert}
        Assert.assertEquals(5, bytes[3]);
        // assert equals int{4} to void{Assert}
        Assert.assertEquals(4, bytes[4]);
        // assert equals int{3} to void{Assert}
        Assert.assertEquals(3, bytes[5]);
        // assert equals int{2} to void{Assert}
        Assert.assertEquals(2, bytes[6]);
        // assert equals int{1} to void{Assert}
        Assert.assertEquals(1, bytes[7]);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(8);
        // write swapped ByteArrayOutputStream{baos} to void{EndianUtils}
        EndianUtils.writeSwappedLong(baos, 72623859790382856L);
        bytes = baos.toByteArray();
        // assert equals int{8} to void{Assert}
        Assert.assertEquals(8, bytes[0]);
        // assert equals int{7} to void{Assert}
        Assert.assertEquals(7, bytes[1]);
        // assert equals int{6} to void{Assert}
        Assert.assertEquals(6, bytes[2]);
        // assert equals int{5} to void{Assert}
        Assert.assertEquals(5, bytes[3]);
        // assert equals int{4} to void{Assert}
        Assert.assertEquals(4, bytes[4]);
        // assert equals int{3} to void{Assert}
        Assert.assertEquals(3, bytes[5]);
        // assert equals int{2} to void{Assert}
        Assert.assertEquals(2, bytes[6]);
        // assert equals int{1} to void{Assert}
        Assert.assertEquals(1, bytes[7]);
    }

    @Test
    public void testReadSwappedFloat() throws IOException {
        final byte[] bytes = new byte[]{ 4 , 3 , 2 , 1 };
        final float f1 = Float.intBitsToFloat(16909060);
        final float f2 = EndianUtils.readSwappedFloat(bytes, 0);
        // assert equals float{f1} to void{Assert}
        Assert.assertEquals(f1, f2, 0.0);
        final ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        // assert equals float{f1} to void{Assert}
        Assert.assertEquals(f1, EndianUtils.readSwappedFloat(input), 0.0);
    }

    @Test
    public void testWriteSwappedFloat() throws IOException {
        byte[] bytes = new byte[4];
        final float f1 = Float.intBitsToFloat(16909060);
        // write swapped byte[]{bytes} to void{EndianUtils}
        EndianUtils.writeSwappedFloat(bytes, 0, f1);
        // assert equals int{4} to void{Assert}
        Assert.assertEquals(4, bytes[0]);
        // assert equals int{3} to void{Assert}
        Assert.assertEquals(3, bytes[1]);
        // assert equals int{2} to void{Assert}
        Assert.assertEquals(2, bytes[2]);
        // assert equals int{1} to void{Assert}
        Assert.assertEquals(1, bytes[3]);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(4);
        // write swapped ByteArrayOutputStream{baos} to void{EndianUtils}
        EndianUtils.writeSwappedFloat(baos, f1);
        bytes = baos.toByteArray();
        // assert equals int{4} to void{Assert}
        Assert.assertEquals(4, bytes[0]);
        // assert equals int{3} to void{Assert}
        Assert.assertEquals(3, bytes[1]);
        // assert equals int{2} to void{Assert}
        Assert.assertEquals(2, bytes[2]);
        // assert equals int{1} to void{Assert}
        Assert.assertEquals(1, bytes[3]);
    }

    @Test
    public void testReadSwappedDouble() throws IOException {
        final byte[] bytes = new byte[]{ 8 , 7 , 6 , 5 , 4 , 3 , 2 , 1 };
        final double d1 = Double.longBitsToDouble(72623859790382856L);
        final double d2 = EndianUtils.readSwappedDouble(bytes, 0);
        // assert equals double{d1} to void{Assert}
        Assert.assertEquals(d1, d2, 0.0);
        final ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        // assert equals double{d1} to void{Assert}
        Assert.assertEquals(d1, EndianUtils.readSwappedDouble(input), 0.0);
    }

    @Test
    public void testWriteSwappedDouble() throws IOException {
        byte[] bytes = new byte[8];
        final double d1 = Double.longBitsToDouble(72623859790382856L);
        // write swapped byte[]{bytes} to void{EndianUtils}
        EndianUtils.writeSwappedDouble(bytes, 0, d1);
        // assert equals int{8} to void{Assert}
        Assert.assertEquals(8, bytes[0]);
        // assert equals int{7} to void{Assert}
        Assert.assertEquals(7, bytes[1]);
        // assert equals int{6} to void{Assert}
        Assert.assertEquals(6, bytes[2]);
        // assert equals int{5} to void{Assert}
        Assert.assertEquals(5, bytes[3]);
        // assert equals int{4} to void{Assert}
        Assert.assertEquals(4, bytes[4]);
        // assert equals int{3} to void{Assert}
        Assert.assertEquals(3, bytes[5]);
        // assert equals int{2} to void{Assert}
        Assert.assertEquals(2, bytes[6]);
        // assert equals int{1} to void{Assert}
        Assert.assertEquals(1, bytes[7]);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(8);
        // write swapped ByteArrayOutputStream{baos} to void{EndianUtils}
        EndianUtils.writeSwappedDouble(baos, d1);
        bytes = baos.toByteArray();
        // assert equals int{8} to void{Assert}
        Assert.assertEquals(8, bytes[0]);
        // assert equals int{7} to void{Assert}
        Assert.assertEquals(7, bytes[1]);
        // assert equals int{6} to void{Assert}
        Assert.assertEquals(6, bytes[2]);
        // assert equals int{5} to void{Assert}
        Assert.assertEquals(5, bytes[3]);
        // assert equals int{4} to void{Assert}
        Assert.assertEquals(4, bytes[4]);
        // assert equals int{3} to void{Assert}
        Assert.assertEquals(3, bytes[5]);
        // assert equals int{2} to void{Assert}
        Assert.assertEquals(2, bytes[6]);
        // assert equals int{1} to void{Assert}
        Assert.assertEquals(1, bytes[7]);
    }

    // tests #IO-101
    @Test
    public void testSymmetryOfLong() {
        final double[] tests = new double[]{ 34.345 , -345.5645 , 545.12 , 10.043 , 7.123456789123 };
        for (final double test : tests) {
            // testing the real problem
            byte[] buffer = new byte[8];
            final long ln1 = Double.doubleToLongBits(test);
            EndianUtils.writeSwappedLong(buffer, 0, ln1);
            final long ln2 = EndianUtils.readSwappedLong(buffer, 0);
            Assert.assertEquals(ln1, ln2);
            // testing the bug report
            buffer = new byte[8];
            EndianUtils.writeSwappedDouble(buffer, 0, test);
            final double val = EndianUtils.readSwappedDouble(buffer, 0);
            Assert.assertEquals(test, val, 0);
        }
    }

    // tests #IO-117
    @Test
    public void testUnsignedOverrun() throws Exception {
        final byte[] target = new byte[]{ 0 , 0 , 0 , ((byte) (128)) };
        final long expected = 2147483648L;
        long actual = EndianUtils.readSwappedUnsignedInteger(target, 0);
        // assert equals String{"readSwappedUnsignedInteger(byte[], int) was incorrect"} to void{Assert}
        Assert.assertEquals("readSwappedUnsignedInteger(byte[], int) was incorrect", expected, actual);
        final ByteArrayInputStream in = new ByteArrayInputStream(target);
        actual = EndianUtils.readSwappedUnsignedInteger(in);
        // assert equals String{"readSwappedUnsignedInteger(InputStream) was incorrect"} to void{Assert}
        Assert.assertEquals("readSwappedUnsignedInteger(InputStream) was incorrect", expected, actual);
    }
}

