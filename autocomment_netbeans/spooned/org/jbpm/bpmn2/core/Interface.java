/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public class Interface implements Serializable {
    private static final long serialVersionUID = 510L;

    private String id;

    private String name;

    private String implementationRef;

    private Map<String, Interface.Operation> operations = new HashMap<String, Interface.Operation>();

    public Interface(String id, String name) {
        Interface.this.id = id;
        Interface.this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Interface.Operation addOperation(String id, String name) {
        Interface.Operation operation = new Interface.Operation(id, name);
        operations.put(id, operation);
        return operation;
    }

    public Interface.Operation getOperation(String name) {
        return operations.get(name);
    }

    public void setImplementationRef(String implementationRef) {
        Interface.this.implementationRef = implementationRef;
    }

    public String getImplementationRef() {
        return implementationRef;
    }

    public class Operation {
        private String id;

        private String name;

        private Message message;

        private String implementationRef;

        public Operation(String id, String name) {
            Interface.Operation.this.id = id;
            Interface.Operation.this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            Interface.Operation.this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            Interface.Operation.this.name = name;
        }

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            Interface.Operation.this.message = message;
        }

        public Interface getInterface() {
            return Interface.this;
        }

        public void setImplementationRef(String implementationRef) {
            Interface.Operation.this.implementationRef = implementationRef;
        }

        public String getImplementationRef() {
            return implementationRef;
        }
    }
}

