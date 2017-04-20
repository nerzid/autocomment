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


package org.apache.commons.io.filefilter;

import org.junit.Assert;
import java.io.File;

public abstract class IOFileFilterAbstractTestCase {
    public static void assertFileFiltering(final int testNumber, final IOFileFilter filter, final File file, final boolean expected) throws Exception {
        // assert equals String{((((((("test " + testNumber) + " Filter(File) ") + (filter.getClass().getName())) + " not ") + expected) + " for ") + file)} to void{Assert}
        Assert.assertEquals(((((((("test " + testNumber) + " Filter(File) ") + (filter.getClass().getName())) + " not ") + expected) + " for ") + file), expected, filter.accept(file));
    }

    public static void assertFilenameFiltering(final int testNumber, final IOFileFilter filter, final File file, final boolean expected) throws Exception {
        // Assumes file has parent and is not passed as null
        // assert equals String{((((((("test " + testNumber) + " Filter(File, String) ") + (filter.getClass().getName())) + " not ") + expected) + " for ") + file)} to void{Assert}
        Assert.assertEquals(((((((("test " + testNumber) + " Filter(File, String) ") + (filter.getClass().getName())) + " not ") + expected) + " for ") + file), expected, filter.accept(file.getParentFile(), file.getName()));
    }

    public static void assertFiltering(final int testNumber, final IOFileFilter filter, final File file, final boolean expected) throws Exception {
        // Note. This only tests the (File, String) version if the parent of
        // the File passed in is not null
        // assert equals String{((((((("test " + testNumber) + " Filter(File) ") + (filter.getClass().getName())) + " not ") + expected) + " for ") + file)} to void{Assert}
        Assert.assertEquals(((((((("test " + testNumber) + " Filter(File) ") + (filter.getClass().getName())) + " not ") + expected) + " for ") + file), expected, filter.accept(file));
        if ((file != null) && ((file.getParentFile()) != null)) {
            Assert.assertEquals(((((((("test " + testNumber) + " Filter(File, String) ") + (filter.getClass().getName())) + " not ") + expected) + " for ") + file), expected, filter.accept(file.getParentFile(), file.getName()));
        }else
            if (file == null) {
                Assert.assertEquals((((((("test " + testNumber) + " Filter(File, String) ") + (filter.getClass().getName())) + " not ") + expected) + " for null"), expected, filter.accept(file));
            }
        
    }

    public static void assertTrueFiltersInvoked(final int testNumber, final IOFileFilterAbstractTestCase.TesterTrueFileFilter[] filters, final boolean[] invoked) {
        for (int i = 1; i < (filters.length); i++) {
            Assert.assertEquals((((("test " + testNumber) + " filter ") + i) + " invoked"), invoked[(i - 1)], filters[i].isInvoked());
        }
    }

    public static void assertFalseFiltersInvoked(final int testNumber, final IOFileFilterAbstractTestCase.TesterFalseFileFilter[] filters, final boolean[] invoked) {
        for (int i = 1; i < (filters.length); i++) {
            Assert.assertEquals((((("test " + testNumber) + " filter ") + i) + " invoked"), invoked[(i - 1)], filters[i].isInvoked());
        }
    }

    public static File determineWorkingDirectoryPath(final String key, final String defaultPath) {
        // Look for a system property to specify the working directory
        final String workingPathName = System.getProperty(key, defaultPath);
        return new File(workingPathName);
    }

    public static void resetFalseFilters(final IOFileFilterAbstractTestCase.TesterFalseFileFilter[] filters) {
        for (final IOFileFilterAbstractTestCase.TesterFalseFileFilter filter : filters) {
            if (filter != null) {
                filter.reset();
            }
        }
    }

    public static void resetTrueFilters(final IOFileFilterAbstractTestCase.TesterTrueFileFilter[] filters) {
        for (final IOFileFilterAbstractTestCase.TesterTrueFileFilter filter : filters) {
            if (filter != null) {
                filter.reset();
            }
        }
    }

    class TesterTrueFileFilter extends TrueFileFilter {
        private static final long serialVersionUID = 1828930358172422914L;

        private boolean invoked;

        @Override
        public boolean accept(final File file) {
            // set invoked boolean{true} to TesterTrueFileFilter{}
            setInvoked(true);
            return super.accept(file);
        }

        @Override
        public boolean accept(final File file, final String str) {
            // set invoked boolean{true} to TesterTrueFileFilter{}
            setInvoked(true);
            return super.accept(file, str);
        }

        public boolean isInvoked() {
            return this.invoked;
        }

        public void setInvoked(final boolean invoked) {
            this.invoked = invoked;
        }

        public void reset() {
            // set invoked boolean{false} to TesterTrueFileFilter{}
            setInvoked(false);
        }
    }

    class TesterFalseFileFilter extends FalseFileFilter {
        private static final long serialVersionUID = -3603047664010401872L;

        private boolean invoked;

        @Override
        public boolean accept(final File file) {
            // set invoked boolean{true} to TesterFalseFileFilter{}
            setInvoked(true);
            return super.accept(file);
        }

        @Override
        public boolean accept(final File file, final String str) {
            // set invoked boolean{true} to TesterFalseFileFilter{}
            setInvoked(true);
            return super.accept(file, str);
        }

        public boolean isInvoked() {
            return this.invoked;
        }

        public void setInvoked(final boolean invoked) {
            this.invoked = invoked;
        }

        public void reset() {
            // set invoked boolean{false} to TesterFalseFileFilter{}
            setInvoked(false);
        }
    }
}

