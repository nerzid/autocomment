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

import org.drools.core.util.ConfFileUtils;
import java.util.ArrayList;
import java.util.Arrays;
import org.drools.core.process.core.datatype.DataType;
import java.util.HashMap;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.drools.core.util.MVELSafeHelper;
import org.drools.core.process.core.ParameterDefinition;
import java.util.Set;
import java.util.Map;

public class WorkItemRepository {
    private static final Logger logger = LoggerFactory.getLogger(WorkItemRepository.class);

    public static Map<String, WorkDefinitionImpl> getWorkDefinitions(String path) {
        return WorkItemRepository.getWorkDefinitions(path, null);
    }

    public static Map<String, WorkDefinitionImpl> getWorkDefinitions(String path, String[] definitionNames) {
        Map<String, WorkDefinitionImpl> workDefinitions = new HashMap<String, WorkDefinitionImpl>();
        List<Map<String, Object>> workDefinitionsMaps = WorkItemRepository.getAllWorkDefinitionsMap(path);
        for (Map<String, Object> workDefinitionMap : workDefinitionsMaps) {
            if (workDefinitionMap != null) {
                WorkDefinitionImpl workDefinition = new WorkDefinitionImpl();
                workDefinition.setName(((String) (workDefinitionMap.get("name"))));
                workDefinition.setDisplayName(((String) (workDefinitionMap.get("displayName"))));
                workDefinition.setIcon(((String) (workDefinitionMap.get("icon"))));
                workDefinition.setCustomEditor(((String) (workDefinitionMap.get("customEditor"))));
                workDefinition.setCategory(((String) (workDefinitionMap.get("category"))));
                workDefinition.setPath(((String) (workDefinitionMap.get("path"))));
                workDefinition.setFile(((String) (workDefinitionMap.get("file"))));
                workDefinition.setDocumentation(((String) (workDefinitionMap.get("documentation"))));
                Set<ParameterDefinition> parameters = new HashSet<ParameterDefinition>();
                Map<String, DataType> parameterMap = ((Map<String, DataType>) (workDefinitionMap.get("parameters")));
                if (parameterMap != null) {
                    for (Map.Entry<String, DataType> entry : parameterMap.entrySet()) {
                        parameters.add(new ParameterDefinitionImpl(entry.getKey(), entry.getValue()));
                    }
                }
                workDefinition.setParameters(parameters);
                Set<ParameterDefinition> results = new HashSet<ParameterDefinition>();
                Map<String, DataType> resultMap = ((Map<String, DataType>) (workDefinitionMap.get("results")));
                if (resultMap != null) {
                    for (Map.Entry<String, DataType> entry : resultMap.entrySet()) {
                        results.add(new ParameterDefinitionImpl(entry.getKey(), entry.getValue()));
                    }
                }
                workDefinition.setResults(results);
                workDefinition.setDefaultHandler(((String) (workDefinitionMap.get("defaultHandler"))));
                if ((workDefinitionMap.get("dependencies")) != null) {
                    workDefinition.setDependencies(((List<String>) (workDefinitionMap.get("dependencies"))).toArray(new String[0]));
                }
                if ((workDefinitionMap.get("mavenDependencies")) != null) {
                    workDefinition.setMavenDependencies(((List<String>) (workDefinitionMap.get("mavenDependencies"))).toArray(new String[0]));
                }
                if ((workDefinitionMap.get("version")) != null) {
                    workDefinition.setVersion(((String) (workDefinitionMap.get("version"))));
                }
                if ((workDefinitionMap.get("description")) != null) {
                    workDefinition.setDescription(((String) (workDefinitionMap.get("description"))));
                }
                if ((workDefinitionMap.get("widType")) != null) {
                    workDefinition.setWidType(((String) (workDefinitionMap.get("widType"))));
                }
                workDefinitions.put(workDefinition.getName(), workDefinition);
            }
        }
        if (definitionNames != null) {
            if ((definitionNames.length) > 0) {
                workDefinitions.keySet().retainAll(new HashSet(Arrays.asList(definitionNames)));
            }else {
                return new HashMap<String, WorkDefinitionImpl>();
            }
        }
        return workDefinitions;
    }

    private static List<Map<String, Object>> getAllWorkDefinitionsMap(String directory) {
        List<Map<String, Object>> workDefinitions = new ArrayList<Map<String, Object>>();
        for (String s : WorkItemRepository.getDirectories(directory)) {
            try {
                workDefinitions.addAll(WorkItemRepository.getAllWorkDefinitionsMap(((directory + "/") + s)));
            } catch (Throwable t) {
                t.printStackTrace();
            }
            workDefinitions.addAll(WorkItemRepository.getWorkDefinitionsMap(directory, s));
        }
        return workDefinitions;
    }

    private static String[] getDirectories(String path) {
        String content = null;
        try {
            content = ConfFileUtils.URLContentsToString(new URL((path + "/index.conf")));
        } catch (Exception e) {
            // Do nothing
        }
        if (content == null) {
            return new String[0];
        }
        return content.split("\n");
    }

    private static List<Map<String, Object>> getWorkDefinitionsMap(String parentPath, String file) {
        String path = ((((parentPath + "/") + file) + "/") + file) + ".wid";
        String content = null;
        try {
            content = ConfFileUtils.URLContentsToString(new URL(path));
        } catch (Exception e) {
            // Do nothing
        }
        if (content == null) {
            return new ArrayList();
        }
        try {
            List<Map<String, Object>> result = ((List<Map<String, Object>>) (MVELSafeHelper.getEvaluator().eval(content, new HashMap())));
            for (Map<String, Object> wid : result) {
                wid.put("path", ((parentPath + "/") + file));
                wid.put("file", (file + ".wid"));
                wid.put("widType", "mvel");
            }
            return result;
        } catch (Throwable t) {
            WorkItemRepository.logger.warn("Could not parse work definition as mvel. Trying as json.");
            try {
                List<Map<String, Object>> result = JsonWorkItemParser.parse(content);
                for (Map<String, Object> wid : result) {
                    wid.put("path", ((parentPath + "/") + file));
                    wid.put("file", (file + ".wid"));
                    wid.put("widType", "json");
                }
                return result;
            } catch (Throwable tt) {
                WorkItemRepository.logger.error(("Error occured while loading work definitions " + path), tt);
                throw new RuntimeException(((("Could not parse work definitions " + path) + ": ") + (tt.getMessage())));
            }
        }
    }
}

