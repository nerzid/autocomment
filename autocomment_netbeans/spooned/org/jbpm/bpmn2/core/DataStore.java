/**
 * Copyright 2010 Intalio Inc
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


package org.jbpm.bpmn2.core;

import org.drools.core.process.core.datatype.DataType;
import java.io.Serializable;

/**
 * @author <a href="mailto:atoulme@intalio.com">Antoine Toulme</a>
 */
public class DataStore implements Serializable {
    private static final long serialVersionUID = 4L;

    private String id;

    private String name;

    private DataType type;

    private String itemSubjectRef;

    public void setId(String id) {
        DataStore.this.id = id;
    }

    public void setName(String name) {
        DataStore.this.name = name;
    }

    public void setType(DataType dataType) {
        DataStore.this.type = dataType;
    }

    public String getId() {
        return DataStore.this.id;
    }

    public String getName() {
        return DataStore.this.name;
    }

    public DataType getType() {
        return DataStore.this.type;
    }

    public void setItemSubjectRef(String itemSubjectRef) {
        DataStore.this.itemSubjectRef = itemSubjectRef;
    }

    public String getItemSubjectRef() {
        return DataStore.this.itemSubjectRef;
    }
}

