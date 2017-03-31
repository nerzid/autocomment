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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.TaggedIOException;
import org.junit.Test;
import java.util.UUID;

/**
 * JUnit Test Case for {@link TaggedInputStream}.
 */
public class TaggedInputStreamTest {
    @Test
    public void testEmptyStream() {
        try {
            final InputStream stream = new TaggedInputStream(new ClosedInputStream());
            Assert.assertEquals(0, stream.available());
            Assert.assertEquals((-1), stream.read());
            Assert.assertEquals((-1), stream.read(new byte[1]));
            Assert.assertEquals((-1), stream.read(new byte[1], 0, 1));
            stream.close();
        } catch (final IOException e) {
            Assert.fail("Unexpected exception thrown");
        }
    }

    @Test
    public void testNormalStream() {
        try {
            final InputStream stream = new TaggedInputStream(new ByteArrayInputStream(new byte[]{ 'a' , 'b' , 'c' }));
            Assert.assertEquals(3, stream.available());
            Assert.assertEquals('a', stream.read());
            final byte[] buffer = new byte[1];
            Assert.assertEquals(1, stream.read(buffer));
            Assert.assertEquals('b', buffer[0]);
            Assert.assertEquals(1, stream.read(buffer, 0, 1));
            Assert.assertEquals('c', buffer[0]);
            Assert.assertEquals((-1), stream.read());
            stream.close();
        } catch (final IOException e) {
            Assert.fail("Unexpected exception thrown");
        }
    }

    @Test
    public void testBrokenStream() {
        final IOException exception = new IOException("test exception");
        final TaggedInputStream stream = new TaggedInputStream(new BrokenInputStream(exception));
        // Test the available() method
        try {
            stream.available();
            Assert.fail("Expected exception not thrown.");
        } catch (final IOException e) {
            Assert.assertTrue(stream.isCauseOf(e));
            try {
                stream.throwIfCauseOf(e);
                Assert.fail("Expected exception not thrown.");
            } catch (final IOException e2) {
                Assert.assertEquals(exception, e2);
            }
        }
        // Test the read() method
        try {
            stream.read();
            Assert.fail("Expected exception not thrown.");
        } catch (final IOException e) {
            Assert.assertTrue(stream.isCauseOf(e));
            try {
                stream.throwIfCauseOf(e);
                Assert.fail("Expected exception not thrown.");
            } catch (final IOException e2) {
                Assert.assertEquals(exception, e2);
            }
        }
        // Test the close() method
        try {
            stream.close();
            Assert.fail("Expected exception not thrown.");
        } catch (final IOException e) {
            Assert.assertTrue(stream.isCauseOf(e));
            try {
                stream.throwIfCauseOf(e);
                Assert.fail("Expected exception not thrown.");
            } catch (final IOException e2) {
                Assert.assertEquals(exception, e2);
            }
        }
    }

    @Test
    public void testOtherException() throws Exception {
        final IOException exception = new IOException("test exception");
        final InputStream closed = new ClosedInputStream();
        final TaggedInputStream stream = new TaggedInputStream(closed);
        Assert.assertFalse(stream.isCauseOf(exception));
        Assert.assertFalse(stream.isCauseOf(new org.apache.commons.io.TaggedIOException(exception, UUID.randomUUID())));
        try {
            stream.throwIfCauseOf(exception);
        } catch (final IOException e) {
            Assert.fail("Unexpected exception thrown");
        }
        try {
            stream.throwIfCauseOf(new org.apache.commons.io.TaggedIOException(exception, UUID.randomUUID()));
        } catch (final IOException e) {
            Assert.fail("Unexpected exception thrown");
        }
        stream.close();
    }
}

