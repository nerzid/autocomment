/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.jbpm.kie.services.test;

import java.util.ArrayList;
import org.junit.Assert;
import java.util.Collection;
import org.jbpm.kie.services.api.DeploymentIdResolver;
import java.util.List;
import org.jbpm.runtime.manager.impl.filter.RegExRuntimeManagerIdFilter;
import org.kie.internal.runtime.manager.RuntimeManagerIdFilter;
import java.util.ServiceLoader;
import org.junit.Test;

public class RuntimeManagerIdentifierFilterTest {
    private static final ServiceLoader<RuntimeManagerIdFilter> runtimeManagerIdFilters = ServiceLoader.load(RuntimeManagerIdFilter.class);

    @Test
    public void testNumberOfFilterImplementationsFound() {
        // assert not ServiceLoader{RuntimeManagerIdentifierFilterTest.runtimeManagerIdFilters} to void{Assert}
        Assert.assertNotNull(RuntimeManagerIdentifierFilterTest.runtimeManagerIdFilters);
        List<String> collected = new ArrayList<String>();
        for (RuntimeManagerIdFilter filter : RuntimeManagerIdentifierFilterTest.runtimeManagerIdFilters) {
            collected.add(filter.getClass().getName());
        }
        // assert equals int{2} to void{Assert}
        Assert.assertEquals(2, collected.size());
        // assert true boolean{collected.contains(RegExRuntimeManagerIdFilter.class.getName())} to void{Assert}
        Assert.assertTrue(collected.contains(RegExRuntimeManagerIdFilter.class.getName()));
        // assert true boolean{collected.contains(DeploymentIdResolver.class.getName())} to void{Assert}
        Assert.assertTrue(collected.contains(DeploymentIdResolver.class.getName()));
    }

    @Test
    public void testGAVFilteringLatest() {
        // assert not ServiceLoader{RuntimeManagerIdentifierFilterTest.runtimeManagerIdFilters} to void{Assert}
        Assert.assertNotNull(RuntimeManagerIdentifierFilterTest.runtimeManagerIdFilters);
        Collection<String> input = new ArrayList<String>();
        // add String{"org.jbpm:test:2.0"} to Collection{input}
        input.add("org.jbpm:test:2.0");
        // add String{"org.jbpm:test:1.0"} to Collection{input}
        input.add("org.jbpm:test:1.0");
        // add String{"org.jbpm:another:1.0"} to Collection{input}
        input.add("org.jbpm:another:1.0");
        ArrayList<String> collected = new ArrayList<String>();
        String pattern = "org.jbpm:test:latest";
        for (RuntimeManagerIdFilter filter : RuntimeManagerIdentifierFilterTest.runtimeManagerIdFilters) {
            collected.addAll(filter.filter(pattern, input));
        }
        // assert equals int{1} to void{Assert}
        Assert.assertEquals(1, collected.size());
        // assert equals String{"org.jbpm:test:2.0"} to void{Assert}
        Assert.assertEquals("org.jbpm:test:2.0", collected.get(0));
    }

    @Test
    public void testRegExFilteringAll() {
        // assert not ServiceLoader{RuntimeManagerIdentifierFilterTest.runtimeManagerIdFilters} to void{Assert}
        Assert.assertNotNull(RuntimeManagerIdentifierFilterTest.runtimeManagerIdFilters);
        ArrayList<String> input = new ArrayList<String>();
        // add String{"org.jbpm:test:2.0"} to ArrayList{input}
        input.add("org.jbpm:test:2.0");
        // add String{"org.jbpm:test:1.0"} to ArrayList{input}
        input.add("org.jbpm:test:1.0");
        // add String{"org.jbpm:another:1.0"} to ArrayList{input}
        input.add("org.jbpm:another:1.0");
        ArrayList<String> collected = new ArrayList<String>();
        String pattern = ".*";
        for (RuntimeManagerIdFilter filter : RuntimeManagerIdentifierFilterTest.runtimeManagerIdFilters) {
            collected.addAll(filter.filter(pattern, input));
        }
        // assert equals int{3} to void{Assert}
        Assert.assertEquals(3, collected.size());
        // assert true boolean{collected.contains(input.get(0))} to void{Assert}
        Assert.assertTrue(collected.contains(input.get(0)));
        // assert true boolean{collected.contains(input.get(1))} to void{Assert}
        Assert.assertTrue(collected.contains(input.get(1)));
        // assert true boolean{collected.contains(input.get(2))} to void{Assert}
        Assert.assertTrue(collected.contains(input.get(2)));
    }

    @Test
    public void testRegExFilteringAllVersions() {
        // assert not ServiceLoader{RuntimeManagerIdentifierFilterTest.runtimeManagerIdFilters} to void{Assert}
        Assert.assertNotNull(RuntimeManagerIdentifierFilterTest.runtimeManagerIdFilters);
        ArrayList<String> input = new ArrayList<String>();
        // add String{"org.jbpm:test:2.0"} to ArrayList{input}
        input.add("org.jbpm:test:2.0");
        // add String{"org.jbpm:test:1.0"} to ArrayList{input}
        input.add("org.jbpm:test:1.0");
        // add String{"org.jbpm:another:1.0"} to ArrayList{input}
        input.add("org.jbpm:another:1.0");
        ArrayList<String> collected = new ArrayList<String>();
        String pattern = "org.jbpm:test:.*";
        for (RuntimeManagerIdFilter filter : RuntimeManagerIdentifierFilterTest.runtimeManagerIdFilters) {
            collected.addAll(filter.filter(pattern, input));
        }
        // assert equals int{2} to void{Assert}
        Assert.assertEquals(2, collected.size());
        // assert true boolean{collected.contains(input.get(0))} to void{Assert}
        Assert.assertTrue(collected.contains(input.get(0)));
        // assert true boolean{collected.contains(input.get(1))} to void{Assert}
        Assert.assertTrue(collected.contains(input.get(1)));
    }

    @Test
    public void testRegExFilteringAllArtifactsAndVersions() {
        // assert not ServiceLoader{RuntimeManagerIdentifierFilterTest.runtimeManagerIdFilters} to void{Assert}
        Assert.assertNotNull(RuntimeManagerIdentifierFilterTest.runtimeManagerIdFilters);
        ArrayList<String> input = new ArrayList<String>();
        // add String{"org.jbpm:test:2.0"} to ArrayList{input}
        input.add("org.jbpm:test:2.0");
        // add String{"org.jbpm:test:1.0"} to ArrayList{input}
        input.add("org.jbpm:test:1.0");
        // add String{"org.jbpm:another:1.0"} to ArrayList{input}
        input.add("org.jbpm:another:1.0");
        ArrayList<String> collected = new ArrayList<String>();
        String pattern = "org.jbpm:.*";
        for (RuntimeManagerIdFilter filter : RuntimeManagerIdentifierFilterTest.runtimeManagerIdFilters) {
            collected.addAll(filter.filter(pattern, input));
        }
        // assert equals int{3} to void{Assert}
        Assert.assertEquals(3, collected.size());
        // assert true boolean{collected.contains(input.get(0))} to void{Assert}
        Assert.assertTrue(collected.contains(input.get(0)));
        // assert true boolean{collected.contains(input.get(1))} to void{Assert}
        Assert.assertTrue(collected.contains(input.get(1)));
        // assert true boolean{collected.contains(input.get(2))} to void{Assert}
        Assert.assertTrue(collected.contains(input.get(2)));
    }

    @Test
    public void testRegExFilteringAllArtifactsWithGivenVersions() {
        // assert not ServiceLoader{RuntimeManagerIdentifierFilterTest.runtimeManagerIdFilters} to void{Assert}
        Assert.assertNotNull(RuntimeManagerIdentifierFilterTest.runtimeManagerIdFilters);
        ArrayList<String> input = new ArrayList<String>();
        // add String{"org.jbpm:test:2.0"} to ArrayList{input}
        input.add("org.jbpm:test:2.0");
        // add String{"org.jbpm:test:1.0"} to ArrayList{input}
        input.add("org.jbpm:test:1.0");
        // add String{"org.jbpm:another:1.0"} to ArrayList{input}
        input.add("org.jbpm:another:1.0");
        ArrayList<String> collected = new ArrayList<String>();
        String pattern = "org.jbpm:.*:1.0";
        for (RuntimeManagerIdFilter filter : RuntimeManagerIdentifierFilterTest.runtimeManagerIdFilters) {
            collected.addAll(filter.filter(pattern, input));
        }
        // assert equals int{2} to void{Assert}
        Assert.assertEquals(2, collected.size());
        // assert true boolean{collected.contains(input.get(1))} to void{Assert}
        Assert.assertTrue(collected.contains(input.get(1)));
        // assert true boolean{collected.contains(input.get(2))} to void{Assert}
        Assert.assertTrue(collected.contains(input.get(2)));
    }
}

