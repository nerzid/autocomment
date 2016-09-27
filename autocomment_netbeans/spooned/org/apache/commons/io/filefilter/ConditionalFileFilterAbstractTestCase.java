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

import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Before;
import java.io.File;
import java.util.List;
import org.junit.Test;

public abstract class ConditionalFileFilterAbstractTestCase extends IOFileFilterAbstractTestCase {
    private static final String TEST_FILE_NAME_PREFIX = "TestFile";

    private static final String TEST_FILE_TYPE = ".tst";

    protected TesterTrueFileFilter[] trueFilters;

    protected TesterFalseFileFilter[] falseFilters;

    private File file;

    private File workingPath;

    @Before
    public void setUp() throws Exception {
        ConditionalFileFilterAbstractTestCase.this.workingPath = determineWorkingDirectoryPath(ConditionalFileFilterAbstractTestCase.this.getWorkingPathNamePropertyKey(), ConditionalFileFilterAbstractTestCase.this.getDefaultWorkingPath());
        ConditionalFileFilterAbstractTestCase.this.file = new File(ConditionalFileFilterAbstractTestCase.this.workingPath, (((ConditionalFileFilterAbstractTestCase.TEST_FILE_NAME_PREFIX) + 1) + (ConditionalFileFilterAbstractTestCase.TEST_FILE_TYPE)));
        ConditionalFileFilterAbstractTestCase.this.trueFilters = new TesterTrueFileFilter[4];
        ConditionalFileFilterAbstractTestCase.this.falseFilters = new TesterFalseFileFilter[4];
        ConditionalFileFilterAbstractTestCase.this.trueFilters[1] = new TesterTrueFileFilter();
        ConditionalFileFilterAbstractTestCase.this.trueFilters[2] = new TesterTrueFileFilter();
        ConditionalFileFilterAbstractTestCase.this.trueFilters[3] = new TesterTrueFileFilter();
        ConditionalFileFilterAbstractTestCase.this.falseFilters[1] = new TesterFalseFileFilter();
        ConditionalFileFilterAbstractTestCase.this.falseFilters[2] = new TesterFalseFileFilter();
        ConditionalFileFilterAbstractTestCase.this.falseFilters[3] = new TesterFalseFileFilter();
    }

    @Test
    public void testAdd() {
        final List<TesterTrueFileFilter> filters = new ArrayList<TesterTrueFileFilter>();
        final ConditionalFileFilter fileFilter = ConditionalFileFilterAbstractTestCase.this.getConditionalFileFilter();
        filters.add(new TesterTrueFileFilter());
        filters.add(new TesterTrueFileFilter());
        filters.add(new TesterTrueFileFilter());
        filters.add(new TesterTrueFileFilter());
        for (int i = 0; i < (filters.size()); i++) {
            Assert.assertEquals("file filters count: ", i, fileFilter.getFileFilters().size());
            fileFilter.addFileFilter(filters.get(i));
            Assert.assertEquals("file filters count: ", (i + 1), fileFilter.getFileFilters().size());
        }
        for (final IOFileFilter filter : fileFilter.getFileFilters()) {
            Assert.assertTrue("found file filter", filters.contains(filter));
        }
        Assert.assertEquals("file filters count", filters.size(), fileFilter.getFileFilters().size());
    }

    @Test
    public void testRemove() {
        final List<TesterTrueFileFilter> filters = new ArrayList<TesterTrueFileFilter>();
        final ConditionalFileFilter fileFilter = ConditionalFileFilterAbstractTestCase.this.getConditionalFileFilter();
        filters.add(new TesterTrueFileFilter());
        filters.add(new TesterTrueFileFilter());
        filters.add(new TesterTrueFileFilter());
        filters.add(new TesterTrueFileFilter());
        for (TesterTrueFileFilter filter : filters) {
            fileFilter.removeFileFilter(filter);
            Assert.assertTrue("file filter removed", (!(fileFilter.getFileFilters().contains(filter))));
        }
        Assert.assertEquals("file filters count", 0, fileFilter.getFileFilters().size());
    }

    @Test
    public void testNoFilters() throws Exception {
        final ConditionalFileFilter fileFilter = ConditionalFileFilterAbstractTestCase.this.getConditionalFileFilter();
        final File file = new File(ConditionalFileFilterAbstractTestCase.this.workingPath, (((ConditionalFileFilterAbstractTestCase.TEST_FILE_NAME_PREFIX) + 1) + (ConditionalFileFilterAbstractTestCase.TEST_FILE_TYPE)));
        assertFileFiltering(1, ((IOFileFilter) (fileFilter)), file, false);
        assertFilenameFiltering(1, ((IOFileFilter) (fileFilter)), file, false);
    }

    @Test
    public void testFilterBuiltUsingConstructor() throws Exception {
        final List<List<IOFileFilter>> testFilters = ConditionalFileFilterAbstractTestCase.this.getTestFilters();
        final List<boolean[]> testTrueResults = ConditionalFileFilterAbstractTestCase.this.getTrueResults();
        final List<boolean[]> testFalseResults = ConditionalFileFilterAbstractTestCase.this.getFalseResults();
        final List<Boolean> testFileResults = ConditionalFileFilterAbstractTestCase.this.getFileResults();
        final List<Boolean> testFilenameResults = ConditionalFileFilterAbstractTestCase.this.getFilenameResults();
        for (int i = 1; i < (testFilters.size()); i++) {
            final List<IOFileFilter> filters = testFilters.get(i);
            final boolean[] trueResults = testTrueResults.get(i);
            final boolean[] falseResults = testFalseResults.get(i);
            final boolean fileResults = testFileResults.get(i);
            final boolean filenameResults = testFilenameResults.get(i);
            // Test conditional AND filter created by passing filters to the constructor
            final IOFileFilter filter = ConditionalFileFilterAbstractTestCase.this.buildFilterUsingConstructor(filters);
            // Test as a file filter
            resetTrueFilters(ConditionalFileFilterAbstractTestCase.this.trueFilters);
            resetFalseFilters(ConditionalFileFilterAbstractTestCase.this.falseFilters);
            assertFileFiltering(i, filter, ConditionalFileFilterAbstractTestCase.this.file, fileResults);
            assertTrueFiltersInvoked(i, trueFilters, trueResults);
            assertFalseFiltersInvoked(i, falseFilters, falseResults);
            // Test as a filename filter
            resetTrueFilters(ConditionalFileFilterAbstractTestCase.this.trueFilters);
            resetFalseFilters(ConditionalFileFilterAbstractTestCase.this.falseFilters);
            assertFilenameFiltering(i, filter, ConditionalFileFilterAbstractTestCase.this.file, filenameResults);
            assertTrueFiltersInvoked(i, trueFilters, trueResults);
            assertFalseFiltersInvoked(i, falseFilters, falseResults);
        }
    }

    @Test
    public void testFilterBuiltUsingAdd() throws Exception {
        final List<List<IOFileFilter>> testFilters = ConditionalFileFilterAbstractTestCase.this.getTestFilters();
        final List<boolean[]> testTrueResults = ConditionalFileFilterAbstractTestCase.this.getTrueResults();
        final List<boolean[]> testFalseResults = ConditionalFileFilterAbstractTestCase.this.getFalseResults();
        final List<Boolean> testFileResults = ConditionalFileFilterAbstractTestCase.this.getFileResults();
        final List<Boolean> testFilenameResults = ConditionalFileFilterAbstractTestCase.this.getFilenameResults();
        for (int i = 1; i < (testFilters.size()); i++) {
            final List<IOFileFilter> filters = testFilters.get(i);
            final boolean[] trueResults = testTrueResults.get(i);
            final boolean[] falseResults = testFalseResults.get(i);
            final boolean fileResults = testFileResults.get(i);
            final boolean filenameResults = testFilenameResults.get(i);
            // Test conditional AND filter created by passing filters to the constructor
            final IOFileFilter filter = ConditionalFileFilterAbstractTestCase.this.buildFilterUsingAdd(filters);
            // Test as a file filter
            resetTrueFilters(ConditionalFileFilterAbstractTestCase.this.trueFilters);
            resetFalseFilters(ConditionalFileFilterAbstractTestCase.this.falseFilters);
            assertFileFiltering(i, filter, ConditionalFileFilterAbstractTestCase.this.file, fileResults);
            assertTrueFiltersInvoked(i, trueFilters, trueResults);
            assertFalseFiltersInvoked(i, falseFilters, falseResults);
            // Test as a filename filter
            resetTrueFilters(ConditionalFileFilterAbstractTestCase.this.trueFilters);
            resetFalseFilters(ConditionalFileFilterAbstractTestCase.this.falseFilters);
            assertFilenameFiltering(i, filter, ConditionalFileFilterAbstractTestCase.this.file, filenameResults);
            assertTrueFiltersInvoked(i, trueFilters, trueResults);
            assertFalseFiltersInvoked(i, falseFilters, falseResults);
        }
    }

    protected abstract ConditionalFileFilter getConditionalFileFilter();

    protected abstract IOFileFilter buildFilterUsingAdd(List<IOFileFilter> filters);

    protected abstract IOFileFilter buildFilterUsingConstructor(List<IOFileFilter> filters);

    protected abstract List<List<IOFileFilter>> getTestFilters();

    protected abstract List<boolean[]> getTrueResults();

    protected abstract List<boolean[]> getFalseResults();

    protected abstract List<Boolean> getFileResults();

    protected abstract List<Boolean> getFilenameResults();

    protected abstract String getWorkingPathNamePropertyKey();

    protected abstract String getDefaultWorkingPath();
}

