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


package org.apache.commons.io.output;

import java.util.Arrays;
import org.junit.Assert;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Test;

/**
 * @version $Id: XmlStreamWriterTest.java 1718944 2015-12-09 19:50:30Z krosenvold $
 */
public class XmlStreamWriterTest {
    /**
     * * french
     */
    private static final String TEXT_LATIN1 = "eacute: \u00e9";

    /**
     * * greek
     */
    private static final String TEXT_LATIN7 = "alpha: \u03b1";

    /**
     * * euro support
     */
    private static final String TEXT_LATIN15 = "euro: \u20ac";

    /**
     * * japanese
     */
    private static final String TEXT_EUC_JP = "hiragana A: \u3042";

    /**
     * * Unicode: support everything
     */
    private static final String TEXT_UNICODE = ((((((XmlStreamWriterTest.TEXT_LATIN1) + ", ") + (XmlStreamWriterTest.TEXT_LATIN7)) + ", ") + (XmlStreamWriterTest.TEXT_LATIN15)) + ", ") + (XmlStreamWriterTest.TEXT_EUC_JP);

    private static String createXmlContent(final String text, final String encoding) {
        String xmlDecl = "<?xml version=\"1.0\"?>";
        if (encoding != null) {
            xmlDecl = ("<?xml version=\"1.0\" encoding=\"" + encoding) + "\"?>";
        } 
        return ((xmlDecl + "\n<text>") + text) + "</text>";
    }

    private static void checkXmlContent(final String xml, final String encoding, final String defaultEncoding) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final XmlStreamWriter writer = new XmlStreamWriter(out, defaultEncoding);
        writer.write(xml);
        writer.close();
        final byte[] xmlContent = out.toByteArray();
        Assert.assertEquals(encoding, writer.getEncoding());
        Assert.assertTrue(Arrays.equals(xml.getBytes(encoding), xmlContent));
    }

    private static void checkXmlWriter(final String text, final String encoding) throws IOException {
        XmlStreamWriterTest.checkXmlWriter(text, encoding, null);
    }

    private static void checkXmlWriter(final String text, final String encoding, final String defaultEncoding) throws IOException {
        final String xml = XmlStreamWriterTest.createXmlContent(text, encoding);
        String effectiveEncoding = encoding;
        if (effectiveEncoding == null) {
            effectiveEncoding = (defaultEncoding == null) ? "UTF-8" : defaultEncoding;
        } 
        XmlStreamWriterTest.checkXmlContent(xml, effectiveEncoding, defaultEncoding);
    }

    @Test
    public void testNoXmlHeader() throws IOException {
        final String xml = "<text>text with no XML header</text>";
        XmlStreamWriterTest.checkXmlContent(xml, "UTF-8", null);
    }

    @Test
    public void testEmpty() throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final XmlStreamWriter writer = new XmlStreamWriter(out);
        writer.flush();
        writer.write("");
        writer.flush();
        writer.write(".");
        writer.flush();
        writer.close();
    }

    @Test
    public void testDefaultEncoding() throws IOException {
        XmlStreamWriterTest.checkXmlWriter(XmlStreamWriterTest.TEXT_UNICODE, null, null);
        XmlStreamWriterTest.checkXmlWriter(XmlStreamWriterTest.TEXT_UNICODE, null, "UTF-8");
        XmlStreamWriterTest.checkXmlWriter(XmlStreamWriterTest.TEXT_UNICODE, null, "UTF-16");
        XmlStreamWriterTest.checkXmlWriter(XmlStreamWriterTest.TEXT_UNICODE, null, "UTF-16BE");
        XmlStreamWriterTest.checkXmlWriter(XmlStreamWriterTest.TEXT_UNICODE, null, "ISO-8859-1");
    }

    @Test
    public void testUTF8Encoding() throws IOException {
        XmlStreamWriterTest.checkXmlWriter(XmlStreamWriterTest.TEXT_UNICODE, "UTF-8");
    }

    @Test
    public void testUTF16Encoding() throws IOException {
        XmlStreamWriterTest.checkXmlWriter(XmlStreamWriterTest.TEXT_UNICODE, "UTF-16");
    }

    @Test
    public void testUTF16BEEncoding() throws IOException {
        XmlStreamWriterTest.checkXmlWriter(XmlStreamWriterTest.TEXT_UNICODE, "UTF-16BE");
    }

    @Test
    public void testUTF16LEEncoding() throws IOException {
        XmlStreamWriterTest.checkXmlWriter(XmlStreamWriterTest.TEXT_UNICODE, "UTF-16LE");
    }

    @Test
    public void testLatin1Encoding() throws IOException {
        XmlStreamWriterTest.checkXmlWriter(XmlStreamWriterTest.TEXT_LATIN1, "ISO-8859-1");
    }

    @Test
    public void testLatin7Encoding() throws IOException {
        XmlStreamWriterTest.checkXmlWriter(XmlStreamWriterTest.TEXT_LATIN7, "ISO-8859-7");
    }

    @Test
    public void testLatin15Encoding() throws IOException {
        XmlStreamWriterTest.checkXmlWriter(XmlStreamWriterTest.TEXT_LATIN15, "ISO-8859-15");
    }

    @Test
    public void testEUC_JPEncoding() throws IOException {
        XmlStreamWriterTest.checkXmlWriter(XmlStreamWriterTest.TEXT_EUC_JP, "EUC-JP");
    }

    @Test
    public void testEBCDICEncoding() throws IOException {
        XmlStreamWriterTest.checkXmlWriter("simple text in EBCDIC", "CP1047");
    }
}
