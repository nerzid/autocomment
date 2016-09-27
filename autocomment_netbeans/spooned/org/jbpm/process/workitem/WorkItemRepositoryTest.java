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


package org.jbpm.process.workitem;

import org.jbpm.test.util.AbstractBaseTest;
import java.util.Map;
import org.junit.Test;

public class WorkItemRepositoryTest extends AbstractBaseTest {
    @Test
    public void testGetWorkDefinitions() throws Exception {
        Map<String, WorkDefinitionImpl> repoResults = WorkItemRepository.getWorkDefinitions(getClass().getResource("repository").toURI().toString());
        assertNotNull(repoResults);
        assertFalse(repoResults.isEmpty());
        assertEquals(repoResults.size(), 6);
    }

    @Test
    public void testGetWorkDefinitionsForNames() throws Exception {
        Map<String, WorkDefinitionImpl> repoResults = WorkItemRepository.getWorkDefinitions(getClass().getResource("repository").toURI().toString(), new String[]{ "TestServiceOne" , "TestServiceTwo" });
        assertNotNull(repoResults);
        assertFalse(repoResults.isEmpty());
        assertEquals(repoResults.size(), 2);
    }

    @Test
    public void testGetWorkDefinitionsForInvalidNames() throws Exception {
        Map<String, WorkDefinitionImpl> repoResults = WorkItemRepository.getWorkDefinitions(getClass().getResource("repository").toURI().toString(), new String[]{ "TestServiceOne" , "INVALID_NAME" });
        assertNotNull(repoResults);
        assertFalse(repoResults.isEmpty());
        assertEquals(repoResults.size(), 1);
        Map<String, WorkDefinitionImpl> repoResults2 = WorkItemRepository.getWorkDefinitions(getClass().getResource("repository").toURI().toString(), new String[]{ "INVALID_NAME1" , "INVALID_NAME2" });
        assertNotNull(repoResults2);
        assertTrue(repoResults2.isEmpty());
        Map<String, WorkDefinitionImpl> repoResults3 = WorkItemRepository.getWorkDefinitions(getClass().getResource("repository").toURI().toString(), new String[]{  });
        assertNotNull(repoResults3);
        assertTrue(repoResults3.isEmpty());
    }
}

