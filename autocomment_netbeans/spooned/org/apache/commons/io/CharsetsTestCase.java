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
import java.nio.charset.Charset;
import Charsets.ISO_8859_1;
import java.util.SortedMap;
import org.junit.Test;
import Charsets.US_ASCII;
import Charsets.UTF_16;
import Charsets.UTF_16BE;
import Charsets.UTF_16LE;
import Charsets.UTF_8;

/**
 * Tests {@link Charsets}.
 *
 * @version $Id: CharEncodingTest.java 1298985 2012-03-09 19:12:49Z ggregory $
 */
public class CharsetsTestCase {
    @Test
    public void testRequiredCharsets() {
        final SortedMap<String, Charset> requiredCharsets = Charsets.requiredCharsets();
        // test for what we expect to be there as of Java 6
        // Make sure the object at the given key is the right one
        // assert equals String{requiredCharsets.get("US-ASCII").name()} to void{Assert}
        Assert.assertEquals(requiredCharsets.get("US-ASCII").name(), "US-ASCII");
        // assert equals String{requiredCharsets.get("ISO-8859-1").name()} to void{Assert}
        Assert.assertEquals(requiredCharsets.get("ISO-8859-1").name(), "ISO-8859-1");
        // assert equals String{requiredCharsets.get("UTF-8").name()} to void{Assert}
        Assert.assertEquals(requiredCharsets.get("UTF-8").name(), "UTF-8");
        // assert equals String{requiredCharsets.get("UTF-16").name()} to void{Assert}
        Assert.assertEquals(requiredCharsets.get("UTF-16").name(), "UTF-16");
        // assert equals String{requiredCharsets.get("UTF-16BE").name()} to void{Assert}
        Assert.assertEquals(requiredCharsets.get("UTF-16BE").name(), "UTF-16BE");
        // assert equals String{requiredCharsets.get("UTF-16LE").name()} to void{Assert}
        Assert.assertEquals(requiredCharsets.get("UTF-16LE").name(), "UTF-16LE");
    }

    // unavoidable until Java 7
    @Test
    @SuppressWarnings(value = "deprecation")
    public void testIso8859_1() {
        // assert equals String{"ISO-8859-1"} to void{Assert}
        Assert.assertEquals("ISO-8859-1", ISO_8859_1.name());
    }

    @Test
    public void testToCharset() {
        // assert equals Charset{Charset.defaultCharset()} to void{Assert}
        Assert.assertEquals(Charset.defaultCharset(), Charsets.toCharset(((String) (null))));
        // assert equals Charset{Charset.defaultCharset()} to void{Assert}
        Assert.assertEquals(Charset.defaultCharset(), Charsets.toCharset(((Charset) (null))));
        // assert equals Charset{Charset.defaultCharset()} to void{Assert}
        Assert.assertEquals(Charset.defaultCharset(), Charsets.toCharset(Charset.defaultCharset()));
        // assert equals Charset{Charset.forName("UTF-8")} to void{Assert}
        Assert.assertEquals(Charset.forName("UTF-8"), Charsets.toCharset(Charset.forName("UTF-8")));
    }

    // unavoidable until Java 7
    @Test
    @SuppressWarnings(value = "deprecation")
    public void testUsAscii() {
        // assert equals String{"US-ASCII"} to void{Assert}
        Assert.assertEquals("US-ASCII", US_ASCII.name());
    }

    // unavoidable until Java 7
    @Test
    @SuppressWarnings(value = "deprecation")
    public void testUtf16() {
        // assert equals String{"UTF-16"} to void{Assert}
        Assert.assertEquals("UTF-16", UTF_16.name());
    }

    // unavoidable until Java 7
    @Test
    @SuppressWarnings(value = "deprecation")
    public void testUtf16Be() {
        // assert equals String{"UTF-16BE"} to void{Assert}
        Assert.assertEquals("UTF-16BE", UTF_16BE.name());
    }

    // unavoidable until Java 7
    @Test
    @SuppressWarnings(value = "deprecation")
    public void testUtf16Le() {
        // assert equals String{"UTF-16LE"} to void{Assert}
        Assert.assertEquals("UTF-16LE", UTF_16LE.name());
    }

    // unavoidable until Java 7
    @Test
    @SuppressWarnings(value = "deprecation")
    public void testUtf8() {
        // assert equals String{"UTF-8"} to void{Assert}
        Assert.assertEquals("UTF-8", UTF_8.name());
    }
}

