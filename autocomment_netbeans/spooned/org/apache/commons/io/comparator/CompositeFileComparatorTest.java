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


package org.apache.commons.io.comparator;

import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Before;
import java.io.BufferedOutputStream;
import java.util.Comparator;
import java.io.File;
import java.io.FileOutputStream;
import org.apache.commons.io.testtools.TestUtils;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import java.util.List;
import org.junit.Test;

/**
 * Test case for {@link CompositeFileComparator}.
 */
public class CompositeFileComparatorTest extends ComparatorAbstractTestCase {
    @Before
    public void setUp() throws Exception {
        comparator = new CompositeFileComparator(new AbstractFileComparator[]{ ((AbstractFileComparator) (SizeFileComparator.SIZE_COMPARATOR)) , ((AbstractFileComparator) (ExtensionFileComparator.EXTENSION_COMPARATOR)) });
        reverse = new ReverseComparator(comparator);
        final File dir = getTestDirectory();
        lessFile = new File(dir, "xyz.txt");
        equalFile1 = new File(dir, "foo.txt");
        equalFile2 = new File(dir, "bar.txt");
        moreFile = new File(dir, "foo.xyz");
        if (!(lessFile.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + (lessFile)) + " as the parent directory does not exist"));
        }
        final BufferedOutputStream output3 = new BufferedOutputStream(new FileOutputStream(lessFile));
        try {
            TestUtils.generateTestData(output3, ((long) (32)));
        } finally {
            IOUtils.closeQuietly(output3);
        }
        if (!(equalFile1.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + (equalFile1)) + " as the parent directory does not exist"));
        }
        final BufferedOutputStream output2 = new BufferedOutputStream(new FileOutputStream(equalFile1));
        try {
            TestUtils.generateTestData(output2, ((long) (48)));
        } finally {
            IOUtils.closeQuietly(output2);
        }
        if (!(equalFile2.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + (equalFile2)) + " as the parent directory does not exist"));
        }
        final BufferedOutputStream output1 = new BufferedOutputStream(new FileOutputStream(equalFile2));
        try {
            TestUtils.generateTestData(output1, ((long) (48)));
        } finally {
            IOUtils.closeQuietly(output1);
        }
        if (!(moreFile.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + (moreFile)) + " as the parent directory does not exist"));
        }
        final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(moreFile));
        try {
            TestUtils.generateTestData(output, ((long) (48)));
        } finally {
            IOUtils.closeQuietly(output);
        }
    }

    /**
     * Test Constructor with null Iterable
     */
    @Test
    public void constructorIterable_order() {
        final List<Comparator<File>> list = new ArrayList<Comparator<File>>();
        // add Comparator{SizeFileComparator.SIZE_COMPARATOR} to List{list}
        list.add(SizeFileComparator.SIZE_COMPARATOR);
        // add Comparator{ExtensionFileComparator.EXTENSION_COMPARATOR} to List{list}
        list.add(ExtensionFileComparator.EXTENSION_COMPARATOR);
        final Comparator<File> c = new CompositeFileComparator(list);
        // assert equals String{"equal"} to void{Assert}
        Assert.assertEquals("equal", 0, c.compare(equalFile1, equalFile2));
        // assert true String{"less"} to void{Assert}
        Assert.assertTrue("less", ((c.compare(lessFile, moreFile)) < 0));
        // assert true String{"more"} to void{Assert}
        Assert.assertTrue("more", ((c.compare(moreFile, lessFile)) > 0));
    }

    /**
     * Test Constructor with null Iterable
     */
    @Test
    public void constructorIterable_Null() {
        final Comparator<File> c = new CompositeFileComparator(((Iterable<Comparator<File>>) (null)));
        // assert equals String{"less,more"} to void{Assert}
        Assert.assertEquals("less,more", 0, c.compare(lessFile, moreFile));
        // assert equals String{"more,less"} to void{Assert}
        Assert.assertEquals("more,less", 0, c.compare(moreFile, lessFile));
        // assert equals String{"toString"} to void{Assert}
        Assert.assertEquals("toString", "CompositeFileComparator{}", c.toString());
    }

    /**
     * Test Constructor with null array
     */
    @Test
    public void constructorArray_Null() {
        final Comparator<File> c = new CompositeFileComparator(((Comparator<File>[]) (null)));
        // assert equals String{"less,more"} to void{Assert}
        Assert.assertEquals("less,more", 0, c.compare(lessFile, moreFile));
        // assert equals String{"more,less"} to void{Assert}
        Assert.assertEquals("more,less", 0, c.compare(moreFile, lessFile));
        // assert equals String{"toString"} to void{Assert}
        Assert.assertEquals("toString", "CompositeFileComparator{}", c.toString());
    }
}

