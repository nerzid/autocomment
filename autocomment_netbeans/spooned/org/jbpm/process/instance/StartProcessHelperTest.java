/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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


package org.jbpm.process.instance;

import org.jbpm.test.util.AbstractBaseTest;
import java.util.ArrayList;
import org.junit.Assert;
import java.util.Collections;
import org.kie.internal.KnowledgeBase;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.junit.Test;

public class StartProcessHelperTest extends AbstractBaseTest {
    public void addLogger() {
        logger = LoggerFactory.getLogger(StartProcessHelperTest.this.getClass());
    }

    @Test
    public void testFindLatestProcessByNameNoInput() {
        String foundProcessId = StartProcessHelper.findLatestProcessByName(((KnowledgeBase) (null)), "Hello");
        Assert.assertNull(foundProcessId);
    }

    @Test
    public void testFindLatestProcessByNameNoExisting() {
        List<Process> processes = buildProcessCollection("Hello", 5);
        String foundProcessId = StartProcessHelper.findLatestProcessByName(processes, "NoSuchProcess");
        Assert.assertNull(foundProcessId);
    }

    @Test
    public void testFindLatestProcessByName() {
        List<Process> processes = buildProcessCollection("Hello", 5);
        String foundProcessId = StartProcessHelper.findLatestProcessByName(processes, "Hello");
        Assert.assertNotNull(foundProcessId);
        Assert.assertEquals("5", foundProcessId);
    }

    @Test
    public void testFindLatestProcessByNameMultipleProcesses() {
        List<Process> processes = buildProcessCollection("Hello", 5);
        processes.addAll(buildProcessCollection("DifferentProcess", 10));
        processes.addAll(buildProcessCollection("DifferentProcess1", 10));
        processes.addAll(buildProcessCollection("DifferentProcess2", 30));
        processes.addAll(buildProcessCollection("Process", 10));
        processes.addAll(buildProcessCollection("Diffeocess1", 10));
        processes.addAll(buildProcessCollection("Differs2", 30));
        processes.addAll(buildProcessCollection("zDifferentProcess", 10));
        processes.addAll(buildProcessCollection("xDifferentProcess1", 10));
        processes.addAll(buildProcessCollection("cDifferentProcess2", 30));
        processes.addAll(buildProcessCollection("vProcess", 10));
        processes.addAll(buildProcessCollection("bDiffeocess1", 10));
        processes.addAll(buildProcessCollection("nDiffers2", 30));
        Collections.shuffle(processes);
        String foundProcessId = StartProcessHelper.findLatestProcessByName(processes, "Hello");
        Assert.assertNotNull(foundProcessId);
        Assert.assertEquals("5", foundProcessId);
        foundProcessId = StartProcessHelper.findLatestProcessByName(processes, "DifferentProcess");
        Assert.assertNotNull(foundProcessId);
        Assert.assertEquals("10", foundProcessId);
    }

    @Test
    public void testFindLatestProcessByNameDoubleAsVersion() {
        List<Process> processes = new ArrayList<Process>();
        org.jbpm.process.core.impl.ProcessImpl process = new org.jbpm.process.core.impl.ProcessImpl();
        process.setName("Hello");
        process.setId("1");
        process.setVersion("0.1");
        processes.add(process);
        process = new org.jbpm.process.core.impl.ProcessImpl();
        process.setName("Hello");
        process.setId("2");
        process.setVersion("0.2");
        processes.add(process);
        String foundProcessId = StartProcessHelper.findLatestProcessByName(processes, "Hello");
        Assert.assertNotNull(foundProcessId);
        Assert.assertEquals("2", foundProcessId);
    }

    private List<Process> buildProcessCollection(String processName, int limit) {
        List<Process> processes = new ArrayList<Process>();
        org.jbpm.process.core.impl.ProcessImpl process = null;
        for (int i = 1; i <= limit; i++) {
            process = new org.jbpm.process.core.impl.ProcessImpl();
            process.setName(processName);
            process.setId((i + ""));
            process.setVersion((i + ""));
            processes.add(process);
        }
        return processes;
    }
}

