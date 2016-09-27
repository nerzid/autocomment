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
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.junit.Test;

/**
 * Tests the CountingInputStream.
 * 
 * @version $Id: ClassLoaderObjectInputStreamTest.java 1718944 2015-12-09 19:50:30Z krosenvold $
 */
public class ClassLoaderObjectInputStreamTest {
    /* Note: This test case tests the simplest functionality of
    ObjectInputStream.  IF we really wanted to test ClassLoaderObjectInputStream
    we would probably need to create a transient Class Loader. -TO
     */
    @Test
    public void testExpected() throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        final Object input = Boolean.FALSE;
        oos.writeObject(input);
        final InputStream bais = new ByteArrayInputStream(baos.toByteArray());
        final ClassLoaderObjectInputStream clois = new ClassLoaderObjectInputStream(getClass().getClassLoader(), bais);
        final Object result = clois.readObject();
        Assert.assertEquals(input, result);
        clois.close();
    }

    @Test
    public void testLong() throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        final Object input = ((long) (123));
        oos.writeObject(input);
        final InputStream bais = new ByteArrayInputStream(baos.toByteArray());
        final ClassLoaderObjectInputStream clois = new ClassLoaderObjectInputStream(getClass().getClassLoader(), bais);
        final Object result = clois.readObject();
        Assert.assertEquals(input, result);
        clois.close();
    }

    @Test
    public void testPrimitiveLong() throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        final long input = 12345L;
        oos.writeLong(input);
        oos.close();
        final InputStream bais = new ByteArrayInputStream(baos.toByteArray());
        final ClassLoaderObjectInputStream clois = new ClassLoaderObjectInputStream(getClass().getClassLoader(), bais);
        final long result = clois.readLong();
        Assert.assertEquals(input, result);
        clois.close();
    }

    private static enum E {
A, B, C;    }

    private static class Test implements Serializable {
        private static final long serialVersionUID = 1L;

        private int i;

        private Object o;

        private ClassLoaderObjectInputStreamTest.E e;

        Test(int i, Object o) {
            ClassLoaderObjectInputStreamTest.Test.this.i = i;
            ClassLoaderObjectInputStreamTest.Test.this.e = ClassLoaderObjectInputStreamTest.E.A;
            ClassLoaderObjectInputStreamTest.Test.this.o = o;
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof ClassLoaderObjectInputStreamTest.Test) {
                ClassLoaderObjectInputStreamTest.Test tother = ((ClassLoaderObjectInputStreamTest.Test) (other));
                return (((ClassLoaderObjectInputStreamTest.Test.this.i) == (tother.i)) & ((ClassLoaderObjectInputStreamTest.Test.this.e) == (tother.e))) & (equalObject(tother.o));
            } else {
                return false;
            }
        }

        private boolean equalObject(Object other) {
            if ((ClassLoaderObjectInputStreamTest.Test.this.o) == null) {
                return other == null;
            } 
            return o.equals(other);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }

    @Test
    public void testObject1() throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        final Object input = new ClassLoaderObjectInputStreamTest.Test(123, null);
        oos.writeObject(input);
        oos.close();
        final InputStream bais = new ByteArrayInputStream(baos.toByteArray());
        final ClassLoaderObjectInputStream clois = new ClassLoaderObjectInputStream(getClass().getClassLoader(), bais);
        final Object result = clois.readObject();
        Assert.assertEquals(input, result);
        clois.close();
    }

    @Test
    public void testObject2() throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        final Object input = new ClassLoaderObjectInputStreamTest.Test(123, 0);
        oos.writeObject(input);
        oos.close();
        final InputStream bais = new ByteArrayInputStream(baos.toByteArray());
        final ClassLoaderObjectInputStream clois = new ClassLoaderObjectInputStream(getClass().getClassLoader(), bais);
        final Object result = clois.readObject();
        Assert.assertEquals(input, result);
        clois.close();
    }

    @Test
    public void testResolveProxyClass() throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(Boolean.FALSE);
        final InputStream bais = new ByteArrayInputStream(baos.toByteArray());
        final ClassLoaderObjectInputStream clois = new ClassLoaderObjectInputStream(getClass().getClassLoader(), bais);
        final String[] interfaces = new String[]{ Comparable.class.getName() };
        final Class<?> result = clois.resolveProxyClass(interfaces);
        Assert.assertTrue("Assignable", Comparable.class.isAssignableFrom(result));
        clois.close();
    }
}

