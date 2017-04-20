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
import org.jbpm.process.core.impl.ProcessImpl;
import org.junit.Test;

public class StartProcessHelperTest extends AbstractBaseTest {
    public void addLogger() {
        logger = LoggerFactory.getLogger(this.getClass());
    }

    @Test
    public void testFindLatestProcessByNameNoInput() {
        String foundProcessId = StartProcessHelper.findLatestProcessByName(((KnowledgeBase) (null)), "Hello");
        // assert null String{foundProcessId} to void{Assert}
        Assert.assertNull(foundProcessId);
    }

    @Test
    public void testFindLatestProcessByNameNoExisting() {
        List<Process> processes = buildProcessCollection("Hello", 5);
        String foundProcessId = StartProcessHelper.findLatestProcessByName(processes, "NoSuchProcess");
        // assert null String{foundProcessId} to void{Assert}
        Assert.assertNull(foundProcessId);
    }

    @Test
    public void testFindLatestProcessByName() {
        List<Process> processes = buildProcessCollection("Hello", 5);
        String foundProcessId = StartProcessHelper.findLatestProcessByName(processes, "Hello");
        // assert not String{foundProcessId} to void{Assert}
        Assert.assertNotNull(foundProcessId);
        // assert equals String{"5"} to void{Assert}
        Assert.assertEquals("5", foundProcessId);
    }

    @Test
    public void testFindLatestProcessByNameMultipleProcesses() {
        List<Process> processes = buildProcessCollection("Hello", 5);
        // add all List{buildProcessCollection("DifferentProcess", 10)} to List{processes}
        processes.addAll(buildProcessCollection("DifferentProcess", 10));
        // add all List{buildProcessCollection("DifferentProcess1", 10)} to List{processes}
        processes.addAll(buildProcessCollection("DifferentProcess1", 10));
        // add all List{buildProcessCollection("DifferentProcess2", 30)} to List{processes}
        processes.addAll(buildProcessCollection("DifferentProcess2", 30));
        // add all List{buildProcessCollection("Process", 10)} to List{processes}
        processes.addAll(buildProcessCollection("Process", 10));
        // add all List{buildProcessCollection("Diffeocess1", 10)} to List{processes}
        processes.addAll(buildProcessCollection("Diffeocess1", 10));
        // add all List{buildProcessCollection("Differs2", 30)} to List{processes}
        processes.addAll(buildProcessCollection("Differs2", 30));
        // add all List{buildProcessCollection("zDifferentProcess", 10)} to List{processes}
        processes.addAll(buildProcessCollection("zDifferentProcess", 10));
        // add all List{buildProcessCollection("xDifferentProcess1", 10)} to List{processes}
        processes.addAll(buildProcessCollection("xDifferentProcess1", 10));
        // add all List{buildProcessCollection("cDifferentProcess2", 30)} to List{processes}
        processes.addAll(buildProcessCollection("cDifferentProcess2", 30));
        // add all List{buildProcessCollection("vProcess", 10)} to List{processes}
        processes.addAll(buildProcessCollection("vProcess", 10));
        // add all List{buildProcessCollection("bDiffeocess1", 10)} to List{processes}
        processes.addAll(buildProcessCollection("bDiffeocess1", 10));
        // add all List{buildProcessCollection("nDiffers2", 30)} to List{processes}
        processes.addAll(buildProcessCollection("nDiffers2", 30));
        // shuffle List{processes} to void{Collections}
        Collections.shuffle(processes);
        String foundProcessId = StartProcessHelper.findLatestProcessByName(processes, "Hello");
        // assert not String{foundProcessId} to void{Assert}
        Assert.assertNotNull(foundProcessId);
        // assert equals String{"5"} to void{Assert}
        Assert.assertEquals("5", foundProcessId);
        foundProcessId = StartProcessHelper.findLatestProcessByName(processes, "DifferentProcess");
        // assert not String{foundProcessId} to void{Assert}
        Assert.assertNotNull(foundProcessId);
        // assert equals String{"10"} to void{Assert}
        Assert.assertEquals("10", foundProcessId);
    }

    @Test
    public void testFindLatestProcessByNameDoubleAsVersion() {
        List<Process> processes = new ArrayList<Process>();
        ProcessImpl process = new ProcessImpl();
        // set name String{"Hello"} to ProcessImpl{process}
        process.setName("Hello");
        // set id String{"1"} to ProcessImpl{process}
        process.setId("1");
        // set version String{"0.1"} to ProcessImpl{process}
        process.setVersion("0.1");
        // add ProcessImpl{process} to List{processes}
        processes.add(process);
        process = new ProcessImpl();
        // set name String{"Hello"} to ProcessImpl{process}
        process.setName("Hello");
        // set id String{"2"} to ProcessImpl{process}
        process.setId("2");
        // set version String{"0.2"} to ProcessImpl{process}
        process.setVersion("0.2");
        // add ProcessImpl{process} to List{processes}
        processes.add(process);
        String foundProcessId = StartProcessHelper.findLatestProcessByName(processes, "Hello");
        // assert not String{foundProcessId} to void{Assert}
        Assert.assertNotNull(foundProcessId);
        // assert equals String{"2"} to void{Assert}
        Assert.assertEquals("2", foundProcessId);
    }

    private List<Process> buildProcessCollection(String processName, int limit) {
        List<Process> processes = new ArrayList<Process>();
        ProcessImpl process = null;
        for (int i = 1; i <= limit; i++) {
            process = new ProcessImpl();
            process.setName(processName);
            process.setId((i + ""));
            process.setVersion((i + ""));
            processes.add(process);
        }
        return processes;
    }
}

