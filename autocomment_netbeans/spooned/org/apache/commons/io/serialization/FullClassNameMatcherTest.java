/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.apache.commons.io.serialization;

import org.junit.Assert;
import org.junit.Test;

public class FullClassNameMatcherTest {
    private static final String[] NAMES_ARRAY = new String[]{ Integer.class.getName() , Long.class.getName() };

    @Test
    public void noNames() throws Exception {
        final FullClassNameMatcher m = new FullClassNameMatcher();
        Assert.assertFalse(m.matches(Integer.class.getName()));
    }

    @Test
    public void withNames() throws Exception {
        final FullClassNameMatcher m = new FullClassNameMatcher(FullClassNameMatcherTest.NAMES_ARRAY);
        Assert.assertTrue(m.matches(Integer.class.getName()));
        Assert.assertFalse(m.matches(String.class.getName()));
    }
}
