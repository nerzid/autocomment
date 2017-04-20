/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.jbpm.bpmn2;

import org.kie.api.builder.Message.Level.ERROR;
import org.jbpm.test.util.AbstractBaseTest;
import org.kie.api.KieServices;
import java.io.BufferedReader;
import KieServices.Factory;
import java.io.IOException;
import org.kie.internal.builder.IncrementalResults;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.kie.internal.builder.InternalKieBuilder;
import org.kie.api.builder.KieBuilder;
import org.junit.Test;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Results;

public class IncrementalCompilationTest extends AbstractBaseTest {
    @Test
    public void testIncrementalProcessCompilation() throws Exception {
        String invalidProcessDefinition = getResource("/BPMN2-Incremental-Build-Invalid.bpmn2");
        String validProcessDefinition = getResource("/BPMN2-Incremental-Build-Valid.bpmn2");
        KieServices ks = Factory.get();
        // This process file contains 4 errors
        KieFileSystem kfs = ks.newKieFileSystem().write("src/main/resources/p1.bpmn2", invalidProcessDefinition);
        KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
        Results results = kieBuilder.getResults();
        // assert equals int{4} to IncrementalCompilationTest{}
        assertEquals(4, results.getMessages(ERROR).size());
        // This process file has the errors fixed
        // write String{"src/main/resources/p1.bpmn2"} to KieFileSystem{kfs}
        kfs.write("src/main/resources/p1.bpmn2", validProcessDefinition);
        IncrementalResults addResults = ((InternalKieBuilder) (kieBuilder)).createFileSet("src/main/resources/p1.bpmn2").build();
        // I'd expect the 4 previous errors to be cleared
        // assert equals int{0} to IncrementalCompilationTest{}
        assertEquals(0, addResults.getAddedMessages().size());
        // assert equals int{4} to IncrementalCompilationTest{}
        assertEquals(4, addResults.getRemovedMessages().size());
    }

    private String getResource(String name) throws IOException {
        InputStream is = this.getClass().getResourceAsStream(name);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            } 
            return sb.toString();
        } finally {
            br.close();
        }
    }
}

