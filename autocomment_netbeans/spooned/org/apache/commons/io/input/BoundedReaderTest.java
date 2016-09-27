/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.apache.commons.io.input;

import org.junit.Assert;
import java.util.concurrent.atomic.AtomicBoolean;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.junit.Test;

public class BoundedReaderTest {
    private final Reader sr = new BufferedReader(new StringReader("01234567890"));

    private final Reader shortReader = new BufferedReader(new StringReader("01"));

    @Test
    public void readTillEnd() throws IOException {
        BoundedReader mr = new BoundedReader(sr, 3);
        mr.read();
        mr.read();
        mr.read();
        Assert.assertEquals((-1), mr.read());
        mr.close();
    }

    @Test
    public void shortReader() throws IOException {
        BoundedReader mr = new BoundedReader(shortReader, 3);
        mr.read();
        mr.read();
        Assert.assertEquals((-1), mr.read());
        mr.close();
    }

    @Test
    public void readMulti() throws IOException {
        BoundedReader mr = new BoundedReader(sr, 3);
        char[] cbuf = new char[4];
        for (int i = 0; i < (cbuf.length); i++) {
            cbuf[i] = 'X';
        }
        final int read = mr.read(cbuf, 0, 4);
        Assert.assertEquals(3, read);
        Assert.assertEquals('0', cbuf[0]);
        Assert.assertEquals('1', cbuf[1]);
        Assert.assertEquals('2', cbuf[2]);
        Assert.assertEquals('X', cbuf[3]);
        mr.close();
    }

    @Test
    public void readMultiWithOffset() throws IOException {
        BoundedReader mr = new BoundedReader(sr, 3);
        char[] cbuf = new char[4];
        for (int i = 0; i < (cbuf.length); i++) {
            cbuf[i] = 'X';
        }
        final int read = mr.read(cbuf, 1, 2);
        Assert.assertEquals(2, read);
        Assert.assertEquals('X', cbuf[0]);
        Assert.assertEquals('0', cbuf[1]);
        Assert.assertEquals('1', cbuf[2]);
        Assert.assertEquals('X', cbuf[3]);
        mr.close();
    }

    @Test
    public void markReset() throws IOException {
        BoundedReader mr = new BoundedReader(sr, 3);
        mr.mark(3);
        mr.read();
        mr.read();
        mr.read();
        mr.reset();
        mr.read();
        mr.read();
        mr.read();
        Assert.assertEquals((-1), mr.read());
        mr.close();
    }

    @Test
    public void markResetWithMarkOutsideBoundedReaderMax() throws IOException {
        BoundedReader mr = new BoundedReader(sr, 3);
        mr.mark(4);
        mr.read();
        mr.read();
        mr.read();
        Assert.assertEquals((-1), mr.read());
        mr.close();
    }

    @Test
    public void markResetWithMarkOutsideBoundedReaderMaxAndInitialOffset() throws IOException {
        BoundedReader mr = new BoundedReader(sr, 3);
        mr.read();
        mr.mark(3);
        mr.read();
        mr.read();
        Assert.assertEquals((-1), mr.read());
        mr.close();
    }

    @Test
    public void markResetFromOffset1() throws IOException {
        BoundedReader mr = new BoundedReader(sr, 3);
        mr.mark(3);
        mr.read();
        mr.read();
        mr.read();
        Assert.assertEquals((-1), mr.read());
        mr.reset();
        mr.mark(1);
        mr.read();
        Assert.assertEquals((-1), mr.read());
        mr.close();
    }

    @Test
    public void markResetMarkMore() throws IOException {
        BoundedReader mr = new BoundedReader(sr, 3);
        mr.mark(4);
        mr.read();
        mr.read();
        mr.read();
        mr.reset();
        mr.read();
        mr.read();
        mr.read();
        Assert.assertEquals((-1), mr.read());
        mr.close();
    }

    @Test
    public void skipTest() throws IOException {
        BoundedReader mr = new BoundedReader(sr, 3);
        mr.skip(2);
        mr.read();
        Assert.assertEquals((-1), mr.read());
        mr.close();
    }

    @Test
    public void closeTest() throws IOException {
        final AtomicBoolean closed = new AtomicBoolean(false);
        final Reader sr = new BufferedReader(new StringReader("01234567890")) {
            @Override
            public void close() throws IOException {
                closed.set(true);
                super.close();
            }
        };
        BoundedReader mr = new BoundedReader(sr, 3);
        mr.close();
        Assert.assertTrue(closed.get());
    }
}

